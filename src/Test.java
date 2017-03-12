import icatch.*;
 
public class Test {
 
    public static void main(String[] args) {
 
        iCATch icatch = new iCATch();
 
        if (!icatch.connect("/dev/tty.usbserial-A50066A1")) {
            System.out.println("Cannot connect to iCATch");
            System.exit(1);
        }
 
        icatch.turnOnLed(12, LedColor.WHITE);
 
        System.out.println("Signal: " + icatch.getSignalStrength());
 
        icatch.sleep(2);
 
        icatch.turnOffLed(12, LedColor.WHITE);
 
        icatch.disconnect();
 
    }
 
}