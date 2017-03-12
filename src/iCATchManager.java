import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Timer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class iCATchManager {
	
	boolean isActive = false;
	boolean isConfigured = false;
	Hashtable<String, iCATchNetwork> networks;
	Hashtable<String, Timer> timers;
	
	public iCATchManager(String configFilePath) 
	{
		this.networks = getiCATchConfig(configFilePath);
		if(this.networks != null){
			timers = new Hashtable<String, Timer>();
			
			Enumeration<String> networkKeys = networks.keys();
			
			while(networkKeys.hasMoreElements()){
				String networkName = networkKeys.nextElement();
				iCATchNetwork network = networks.get(networkName);
				timers.put(network.name, new Timer());
			}
		}
	}

	public synchronized int getSignalReading(String networkName)
	{
		return isActive && networks.containsKey(networkName) ? networks.get(networkName).signalStrength : -1;
	}
	
	public synchronized int getLightSensorReading(String networkName)
	{
		return isActive && networks.containsKey(networkName) ? networks.get(networkName).brightness : -1;		
	}
	
	public Hashtable<String, iCATchNetwork> getiCATchConfig(String configFilePath){
		if(!isConfigured) {
			Hashtable<String, iCATchNetwork> networksTmp = null;
			try {
				DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
				Document doc = dBuilder.parse(configFilePath);
				doc.getDocumentElement().normalize();
				
				NodeList nList = doc.getElementsByTagName("network");
				
				if(nList!=null){
					networksTmp = new Hashtable<String, iCATchNetwork>();
				}
				
				for(int i=0; i<nList.getLength(); i++) {				
					Element node = (Element)nList.item(i);
					
					String name = node.getAttribute("name");
					String port = node.getAttribute("port");
					String color = node.getAttribute("LightColor");
					int lightSensorAddr = Integer.parseInt(node.getAttribute("LightSensor"));
					int ledMatrixAddr = Integer.parseInt(node.getAttribute("LEDMatrix"));
					iCATchNetwork network = new iCATchNetwork(name,port,ledMatrixAddr,lightSensorAddr,color);
					
					System.out.println("network:"+name+" port:"+port+" light sensor:"+lightSensorAddr+" LED matrix:"+ledMatrixAddr);
					networksTmp.put(network.name, network);
				}
				isConfigured = true;
				return networksTmp;
			} catch (Exception e){
				e.printStackTrace();
			} 
		}
		return null;
	}
	
	public void turnOnLight(String networkName)
	{
		if(isActive && networks.containsKey(networkName)) {
			iCATchNetwork network = networks.get(networkName);
			network.turnOnLight();
		}
	}
	
	public void turnOffLight(String networkName)
	{
		if(isActive && networks.containsKey(networkName)) {
			iCATchNetwork network = networks.get(networkName);
			network.turnOffLight();
		}
	}
	
	public void startService() 
	{
		if(!isActive) {	
			isActive = true;
			Enumeration<String> networkKeys = networks.keys();
			while(networkKeys.hasMoreElements()){
				String networkName = networkKeys.nextElement();
				iCATchNetwork network = networks.get(networkName);
				network.connect();
				network.turnOffLight();
				timers.get(networkName).scheduleAtFixedRate(new iCATchManagerTask(network), 0, 2000);
			}
		}
	}
	
	public void stopService()
	{
		if(isActive) {
			Enumeration<String> networkKeys = networks.keys();
			while(networkKeys.hasMoreElements()) {
				String networkName = networkKeys.nextElement();
				iCATchNetwork network = networks.get(networkName);
				timers.get(networkName).cancel();
				network.turnOffLight();
				network.disconnect();
			}
			isActive = false;
		}
	}
}
