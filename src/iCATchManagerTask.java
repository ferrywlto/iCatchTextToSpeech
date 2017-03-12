import java.util.TimerTask;

public class iCATchManagerTask extends TimerTask{
	iCATchNetwork network;
	
	public iCATchManagerTask(iCATchNetwork network) {
		this.network = network;
	}
	
	public void run(){
		
//		System.out.println(network.name+":Getting signal strength...");
		network.updateSignalStrength();

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
//		System.out.println(network.name+":Getting sensor reading...");
		network.updateLightSensorReading();
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
