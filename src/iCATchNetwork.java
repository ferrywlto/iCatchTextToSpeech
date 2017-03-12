import icatch.LedColor;
import icatch.iCATch;


public class iCATchNetwork {
	protected int ledMatrixAddr;
	protected int lightSensorAddr;
	protected String port;
	protected String name;
	protected LedColor color;
	protected iCATch icatch;
	
	public int signalStrength = 0;
	public int brightness = 0;
	protected boolean isLightOn = false;
	protected boolean isConnected = false;
	
	public iCATchNetwork(String name, String port, int matrixAddr, int lightSensorAddr, String color){
		this.icatch = new iCATch();
		this.name = name;
		this.port = port;
		this.ledMatrixAddr = matrixAddr;
		this.lightSensorAddr = lightSensorAddr;
		if(color.equals("RED")){
			this.color = LedColor.RED;
		} else if(color.equals("GREEN")){
			this.color = LedColor.GREEN;
		} else if(color.equals("BLUE")){
			this.color = LedColor.BLUE;
		} else {
			this.color = LedColor.WHITE;
		}
	}
	public void connect(){
		if(!isConnected){
			icatch.connect(port);
			isConnected = true;
		}
	}
	public void disconnect(){
		if(isConnected){
			icatch.disconnect();
			isConnected = false;
		}
	}
	public void updateSignalStrength(){
		signalStrength = icatch.getSignalStrength();
	}
	public void updateLightSensorReading(){
		brightness = icatch.getLightSensorReading(lightSensorAddr);
	}
	public void turnOnLight(){
		if(!isLightOn){
			//int patternID = (int)Math.floor(Math.random()*50+103);
			//icatch.displayPatternInLedMatrix(ledMatrixAddr, patternID);
			//System.out.println("network:"+name+" pattern:"+patternID);
			icatch.turnOnAllLedsInLedMatrix(ledMatrixAddr, color);
			System.out.println("TOL:"+ledMatrixAddr);
			isLightOn = true;
		}
	}
	public void turnOffLight(){
		if(isLightOn){
			//icatch.turnOffLed(lightSensorAddr, LedColor.WHITE);
			//icatch.turnOffAllLedsInLedMatrix(ledMatrixAddr, LedColor.BLUE);
			//icatch.turnOffAllLedsInLedMatrix(ledMatrixAddr, LedColor.GREEN);
			//icatch.turnOffAllLedsInLedMatrix(ledMatrixAddr, LedColor.RED);
			icatch.turnOffAllLedsInLedMatrix(ledMatrixAddr, color);
			isLightOn = false;
		}
	}
}
