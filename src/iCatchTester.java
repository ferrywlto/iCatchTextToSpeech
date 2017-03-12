public class iCatchTester implements Runnable{
	
//	public static void testVibrate(){
//		icatch.turnOnVibrationMotor(88);
//		icatch.sleep(10);
//		icatch.turnOffVibrationMotor(88);
//	}
	
	public void run(){
		int count = 0;
		iCATchManager manager = new iCATchManager("bin/config.xml");
		
		manager.startService();
		
		try {
			Thread.sleep(3000); // wait 3 seconds first to let the sensors ready
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		while(count<30){
			try {
				int pianoReading = manager.getLightSensorReading("user1");
				int fluteReading = manager.getLightSensorReading("user2");
				System.out.println("round:"+count+" piano:"+pianoReading+" flute:"+fluteReading);
				
				if(pianoReading > 200){
					manager.turnOnLight("user1");
					//System.out.println("turnOnLight:piano");
				}
				else {
					manager.turnOffLight("user1");
					//System.out.println("turnOffLight:piano");
				}
				Thread.sleep(1000);
				/*
				if(fluteReading > 200) {
					manager.turnOnLight("user2");
					//System.out.println("turnOnLight:flute");
				}
				else {
					manager.turnOffLight("user2");
					//System.out.println("turnOffLight:flute");
				}
				Thread.sleep(1000);
				*/
				count++;
				
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		manager.stopService();
	}
	
	public static void main(String[] args) {
		iCatchTester tester = new iCatchTester();
		Thread th = new Thread(tester);
		th.run();
		//th.run();
	}
		//icatch = new iCATch();
		
		//String bluetoothID = "FTTR5HFW";
		//String bluetoothID = "A400BBZN";
		//String bluetoothID = "";
		//String bluetoothID = "A50066A1";
		/*
		if(!icatch.connect("/dev/tty.usbserial-"+bluetoothID)){
			System.out.println("Cannot connect to iCATch");
			System.exit(0);
		}
		 */
		//icatch.displayPatternInLedMatrix(124, 103);
		//icatch.turnOnLedInLedMatrix(124, LedColor.RED, 1, 1);
		//icatch.disconnect();
		/*
		int strength = 0;
		int strength2 = 0;
		while(strength2<30) {
			//testVibrate();
			System.out.println("Light: " + icatch.getLightSensorReading(46));
			strength = icatch.getSignalStrength();
			System.out.println("Singal: " + strength);
			icatch.sleep(1);
			//if(strength < 10) {
			//	icatch.disconnect();
			//}
			
			int mod = 4;
			int row = (int)Math.floor(Math.random()*10.0)%mod;
			int col = (int)Math.floor(Math.random()*10.0)%mod;
			//icatch.turnOnAllLedsInLedMatrix(124, LedColor.RED);
			//icatch.sleep(2);
			switch(strength2%4){
			case 0: icatch.turnOnLed(118, LedColor.RED); icatch.turnOnLed(88, LedColor.RED);  break;
			case 1: icatch.turnOnLed(118, LedColor.GREEN); icatch.turnOnLed(88, LedColor.GREEN);  break;
			case 2: icatch.turnOnLed(104, LedColor.WHITE); icatch.turnOnLed(88, LedColor.WHITE); break;
			case 3: icatch.turnOnLed(118, LedColor.BLUE); icatch.turnOnLed(88, LedColor.BLUE);  break;
			}
			icatch.sleep(1);
			switch(strength2%4){
			case 0: icatch.turnOffLed(118, LedColor.RED); icatch.turnOffLed(88, LedColor.RED);  break;
			case 1: icatch.turnOffLed(118, LedColor.GREEN); icatch.turnOffLed(88, LedColor.GREEN);  break;
			case 2: icatch.turnOffLed(104, LedColor.WHITE); icatch.turnOffLed(88, LedColor.WHITE); break;
			case 3: icatch.turnOffLed(118, LedColor.BLUE); icatch.turnOffLed(88, LedColor.BLUE); break;
			}
			
			
			//icatch.turnOnAllLedsInLedMatrix(124, LedColor.RED);
			//icatch.turnOnLedInLedMatrix(124, LedColor.RED, 1, 1);
			//icatch.turnOnLedInLedMatrix(124, LedColor.WHITE, (row+2)%mod, (col+2)%mod);
			//icatch.turnOnLedInLedMatrix(124, LedColor.BLUE, (row+3)%mod, (col+3)%mod);
			//System.out.println("Ultra: " + icatch.getUltraSonicSensorReading(138));
			//System.out.println("Light: "+icatch.getLightSensorReading(20));
			//icatch.playSound(80, Tone.C, Pitch.FLAT, 1, 1, 10);
			//icatch.playSoundEffect(80, SoundEffect.WORK_IN_PROGRESS_1, 2);
			//strength2++;
		}
		*/
		//icatch.disconnect();
		/*
		icatch.turnOnLed(104, LedColor.WHITE);
		icatch.turnOffLed(46, LedColor.WHITE);
		icatch.turnOffLed(48, LedColor.WHITE);
		//icatch.sleep(2);
		
		icatch.sleep(1);
		icatch.turnOffLed(48, LedColor.WHITE);
		icatch.sleep(1);
		icatch.turnOnVibrationMotor(7);
		icatch.sleep(1);
		icatch.turnOnVibrationMotor(90);
		
		int count = 0;
		
		while(count<30){
			//System.out.println("Ultra: " + icatch.get(138));
			System.out.println("Ultra: " + icatch.getUltraSonicSensorReading(138));
			System.out.println("Light: " + icatch.getLightSensorReading(6));
			count++;
			icatch.sleep(0.5);
		}
		icatch.sleep(2);
		icatch.turnOffVibrationMotor(7);
		
		icatch.turnOffVibrationMotor(90);
		//int i =0;
		*/
		/*
		for(int i=0; i<256; i++)
		{
			//icatch.turnOffLedInLedMatrix(i, LedColor.GREEN, 0, 0);
			//icatch.turnOnLedInLedMatrix(124, LedColor.RED, 0, 0);
			icatch.turnOnVibrationMotor(i);
			System.out.println(i);
			icatch.sleep(0.5);
		}
		
		icatch.playSoundEffect(54, SoundEffect.AMBULANCE, 1);
		icatch.sleep(1);
		icatch.playSound(54, Tone.A, Pitch.NATURAL, 1, 1, 1);
		
		icatch.sleep(1);
		icatch.turnOffLed(104, LedColor.WHITE);
		*/
}
