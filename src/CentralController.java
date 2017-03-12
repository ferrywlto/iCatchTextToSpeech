import java.io.File;
import java.io.IOException;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class CentralController implements Runnable {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		CentralController controller = new CentralController(1);
		Thread control_thread = new Thread(controller);
		control_thread.run();
	}

	private static enum SYSTEM_STATE {
		IDLE, GREETING, INIT, PLAYING, BYE
	}

	SYSTEM_STATE current_state = SYSTEM_STATE.IDLE;

	iCATchManager manager;
	int randomAdd = 150;
	static int query_interval = 250; // interval (in milliseconds) between
										// queries of sensor value
	int criticalValue = 240;
	int intervalsBeforeStart = 2 * 500/query_interval;
	int intervalsBeforeOff = 2 * 500/query_interval;
	int intervalsBeforeEnding = 10 * 500/query_interval;
	final int num_of_users;
	final int num_of_tracks = 3;
	final File misc = new File ("./misc/");
	final File[] files = {
			new File(misc.getAbsolutePath() + "/1-1.wav"),
			new File(misc.getAbsolutePath() + "/1-2.wav"),
			new File(misc.getAbsolutePath() + "/1-3.wav"),
			new File(misc.getAbsolutePath() + "/2-1.wav"),
			new File(misc.getAbsolutePath() + "/2-2.wav"),
			new File(misc.getAbsolutePath() + "/2-3.wav") };

	WavPlayer[] players = { new WavPlayer(), new WavPlayer(), new WavPlayer() };

	int[] sensor_values;
	int[] sensor_off_count;
	int[] sensor_on_count;
	boolean[] sensor_is_on;
	String[] sensor_names = { "user1", "user1" };

	
	// TODO Check OS first, if it is not Lion, use FreeTTS instead
	MyTTSEngine english_speaker = new MyTTSEngine(
			MyTTSEngine.SPEECH_LANGUAGE.ENGLISH);
	MyTTSEngine mandarin_speaker = new MyTTSEngine(
			MyTTSEngine.SPEECH_LANGUAGE.MANDARIN);
	MyTTSEngine cantonese_speaker = new MyTTSEngine(
			MyTTSEngine.SPEECH_LANGUAGE.CANTONESE);

	public CentralController() {
		this(1);
	}

	public CentralController(int users) {
		num_of_users = users < num_of_tracks ? users : num_of_tracks;
		sensor_values = new int[num_of_users];
		sensor_off_count = new int[num_of_users];
		sensor_on_count = new int[num_of_users];
		sensor_is_on = new boolean[num_of_users];
		System.out.println("Controller created for " + this.num_of_users
				+ " users.");

		manager = new iCATchManager("bin/config.xml");
		manager.startService();
		try {
			Thread.sleep(3000); // wait 3 seconds first to let the sensors ready
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
	}

	void idle() {
		System.out.println("idle");
		updateSensorStatus();
		for(int user = 0; user < num_of_users; user++) {
			if(sensor_on_count[user] > intervalsBeforeStart) {
				current_state = SYSTEM_STATE.GREETING;
				break;
			}
		}
	}

	void greeting() {
		System.out.println("greeting");
		// read instruction
		english_speaker
				.speak("Hi! Welcome to the music lab. You are now sitting on the musician chairs.");
		english_speaker.speak("You may adjust your sitting position to turn on or mute the track. Or try another chair for different music track.");
		cantonese_speaker.speak("歡迎來到音樂實驗室試用音樂家之椅。您可以通過變換坐姿啟用或禁用音軌。或者試坐另一張椅子，嘗試另一個音軌。");
		mandarin_speaker.speak("欢迎来到音乐实验室试用音乐家之椅。 您可以通过变换坐姿启用或禁用音轨。或者试坐另一张椅子，尝试另一个音轨。");
		// goto next state
		current_state = SYSTEM_STATE.INIT;
	}

	void init() {
		System.out.println("init");
		int trackset = Math.random() < 0.5 ? 0 : 1;
		// create 3 players
		for (int track = 0; track < num_of_tracks; track++) {
			try {
				players[track].init(files[trackset * num_of_tracks + track]);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnsupportedAudioFileException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (LineUnavailableException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		// start playback
		for (int track = 0; track < num_of_tracks; track++) {
			players[track].start();
		}
		// goto next state
		current_state = SYSTEM_STATE.PLAYING;
	}

	void playing() {
		randomAdd = 120;
//		System.out.println("playing");
		// check sensor value
		updateSensorStatus();
		// change track setting
		for (int user = 0; user < num_of_users; user++) {
			if (sensor_is_on[user]) {
				players[user].setVolume(1);
			} else {
				players[user].setVolume(0);
			}
		}
		// decide whether to fade out
		boolean all_gone = false;
		int leave_time = Integer.MAX_VALUE;
		for (int user = 0; user < num_of_users; user++) {
			all_gone = all_gone || sensor_is_on[user];
			leave_time = leave_time < sensor_off_count[user] ? leave_time
					: sensor_off_count[user];
		}
		all_gone = !all_gone;
		if (all_gone && leave_time > 0) {
//			for (int track = 0; track < num_of_tracks; track++) {
//				players[track].setVolume(1/leave_time);
//			}
			if (leave_time > intervalsBeforeEnding) {
				current_state = SYSTEM_STATE.BYE;
			}
		}
//		if (!all_gone) {
//			for (int track = num_of_users - 1; track < num_of_tracks; track++) {
//				players[track].setVolume(1);
//			}
//		}
//		System.out.println(all_gone + "," + leave_time);
	}

	void bye() {
		randomAdd = 150;
		System.out.println("bye");
		// cleanup the players
		for (int track = 0; track < num_of_tracks; track++) {
			players[track].finalize();
		}
		// read bye message
		english_speaker.speak("Thank you for trying out our musician chairs. Have a nice day!");
		cantonese_speaker.speak("謝謝您試用音樂家之椅。祝您擁有好心情!");
		mandarin_speaker.speak("謝謝您試用音樂家之椅。祝您擁有好心情!");

		current_state = SYSTEM_STATE.IDLE;
	}

	void updateSensorStatus() {
		for (int user = 0; user < sensor_values.length; user++) {
			 sensor_values[user] =
			 manager.getLightSensorReading(sensor_names[user]);

//			sensor_values[user] = (int) (Math.random() * 100 + randomAdd);

			if (sensor_values[user] > criticalValue) {
				sensor_is_on[user] = true;
				manager.turnOnLight(sensor_names[user]);
				sensor_on_count[user]++;
				sensor_off_count[user] = 0;
			} else {
				sensor_off_count[user]++;
				sensor_on_count[user] = 0;
				if (sensor_off_count[user] > intervalsBeforeOff) {
					sensor_is_on[user] = false;
					manager.turnOffLight(sensor_names[user]);
				}
			}
		}

		System.out.println(sensor_values[0]);// + "," + sensor_values[1]);
//		System.out.println(sensor_off_count[0] + "," + sensor_off_count[1]);
//		System.out.println(sensor_on_count[0] + "," + sensor_on_count[1]);
//		System.out.println(sensor_is_on[0] + "," + sensor_is_on[1]);
	}
	
	public void finalize() {
		manager.stopService();
	}

	@Override
	public void run() {
		while (true) {
			switch (current_state) {
			case GREETING:
				greeting();
				break;
			case INIT:
				init();
				break;
			case PLAYING:
				playing();
				break;
			case BYE:
				bye();
				break;
			default: // IDLE
				idle();
				break;
			}
			try {
				Thread.sleep(query_interval);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
