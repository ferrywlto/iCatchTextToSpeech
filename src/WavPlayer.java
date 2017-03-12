import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.BooleanControl;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

public class WavPlayer implements Runnable {
	File file;
	Mixer mixer;
	AudioInputStream in;
	SourceDataLine line;
	int frameSize;
	byte[] buffer = new byte[32 * 1024];
	Thread playThread;
	boolean playing;
	boolean notYetEOF;
	BooleanControl muteControl;
	FloatControl gainControl;
	FloatControl panControl;

	// public static void main(String[] args) {
	// try {
	// File[] files = {
	// new File("/Users/edwardtoday/Dropbox/Public/01 OneDay(KeyMix).wav"),
	// new File("/Users/edwardtoday/Dropbox/Public/02 OneDay(Rhode).wav"),
	// new File("/Users/edwardtoday/Dropbox/Public/03 OneDay(Bassline).wav")
	// };
	// WavPlayer p0 = new WavPlayer(files[0]);
	// p0.start();
	// Thread.sleep(3000);
	// p0.setMute(true);
	// Thread.sleep(3000);
	// p0.setMute(false);
	// p0.setVolume(0.1f);
	// Thread.sleep(3000);
	// p0.setVolume(0.5f);
	// Thread.sleep(3000);
	// p0.setVolume(1.0f);
	// Thread.sleep(3000);
	// p0.setPan(-1);
	// Thread.sleep(3000);
	// p0.setPan(1);
	// Thread.sleep(3000);
	// p0.finalize();
	// return;
	// } catch (IOException e) {
	// e.printStackTrace();
	// } catch (UnsupportedAudioFileException e) {
	// e.printStackTrace();
	// } catch (LineUnavailableException e) {
	// e.printStackTrace();
	// } catch (InterruptedException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }

	public WavPlayer() {
	}

	public WavPlayer(File f) throws IOException, UnsupportedAudioFileException,
			LineUnavailableException {
		Mixer.Info[] mixerinfo = AudioSystem.getMixerInfo();
		mixer = AudioSystem.getMixer(mixerinfo[0]);
		file = f;
		in = AudioSystem.getAudioInputStream(file);
		AudioFormat format = in.getFormat();
		AudioFormat.Encoding formatEncoding = format.getEncoding();
		if (!(formatEncoding.equals(AudioFormat.Encoding.PCM_SIGNED) || formatEncoding
				.equals(AudioFormat.Encoding.PCM_UNSIGNED)))
			throw new UnsupportedAudioFileException(file.getName()
					+ " is not PCM audio");
		System.out.println("got PCM format:" + format.toString());
		frameSize = format.getFrameSize();
		DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
		System.out.println("got info:" + info.toString());
		System.out.println("max line of this info:" + mixer.getMaxLines(info));
		line = (SourceDataLine) mixer.getLine(info);
		line.open();
		System.out.println("opened line");
		gainControl = (FloatControl) line.getControls()[0];
		muteControl = (BooleanControl) line.getControls()[1];
		panControl = (FloatControl) line.getControls()[2];

		playThread = new Thread(this);
		playing = false;
		notYetEOF = true;
		playThread.start();
	}

	public void init(File f) throws IOException,
			UnsupportedAudioFileException, LineUnavailableException {
		Mixer.Info[] mixerinfo = AudioSystem.getMixerInfo();
		mixer = AudioSystem.getMixer(mixerinfo[0]);
		file = f;
		in = AudioSystem.getAudioInputStream(file);
		AudioFormat format = in.getFormat();
		AudioFormat.Encoding formatEncoding = format.getEncoding();
		if (!(formatEncoding.equals(AudioFormat.Encoding.PCM_SIGNED) || formatEncoding
				.equals(AudioFormat.Encoding.PCM_UNSIGNED)))
			throw new UnsupportedAudioFileException(file.getName()
					+ " is not PCM audio");
		System.out.println("got PCM format:" + format.toString());
		frameSize = format.getFrameSize();
		DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
		System.out.println("got info:" + info.toString());
		System.out.println("max line of this info:" + mixer.getMaxLines(info));
		line = (SourceDataLine) mixer.getLine(info);
		line.open();
		System.out.println("opened line");
		gainControl = (FloatControl) line.getControls()[0];
		muteControl = (BooleanControl) line.getControls()[1];
		panControl = (FloatControl) line.getControls()[2];

		playThread = new Thread(this);
		playing = false;
		notYetEOF = true;
		playThread.start();
	}

	public void finalize() {
		line.drain();
		line.stop();
	}

	public void run() {
		int readPoint = 0;
		int bytesRead = 0;

		try {
			while (notYetEOF) {
				if (playing) {
					bytesRead = in.read(buffer, readPoint, buffer.length
							- readPoint);
					if (bytesRead == -1) {
						notYetEOF = false;
						break;
					}
					// how many frames did we get,
					// and how many are left over?
//					int frames = bytesRead / frameSize;
					int leftover = bytesRead % frameSize;
					// send to line
					line.write(buffer, readPoint, bytesRead - leftover);
					// save the leftover bytes
					System.arraycopy(buffer, bytesRead, buffer, 0, leftover);
					readPoint = leftover;
				} else {
					// if not playing
					// Thread.yield();
					try {
						Thread.sleep(10);
					} catch (InterruptedException ie) {
					}
				}
			} // while notYetEOF
			System.out.println("reached eof");
			line.drain();
			line.stop();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} finally {
			// line.close();
		}
	} // run

	public void start() {
		playing = true;
		if (!playThread.isAlive())
			playThread.start();
		line.start();
	}

	public void stop() {
		playing = false;
		line.stop();
	}

	public float getGain() {
		return gainControl.getValue();
	}

	public void setGain(float gain) {
		float min = gainControl.getMinimum();
		float max = gainControl.getMaximum();
		gainControl.setValue(gain < min ? min : (gain > max ? max : gain));
	}

	public void setVolume(float vol) {
		setGain((float) (Math.log(vol) / Math.log(10.0) * 20.0));
	}

	public boolean getMute() {
		return muteControl.getValue();
	}

	public void setMute(boolean mute) {
		muteControl.setValue(mute);
	}

	public float getPan() {
		return panControl.getValue();
	}

	public void setPan(float pan) {
		float min = panControl.getMinimum();
		float max = panControl.getMaximum();
		panControl.setValue(pan < min ? min : (pan > max ? max : pan));
	}

	public SourceDataLine getLine() {
		return line;
	}

	public File getFile() {
		return file;
	}
}
