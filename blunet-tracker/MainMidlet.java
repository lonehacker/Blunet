import java.io.IOException;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.List;
import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;
import javax.bluetooth.DiscoveryListener;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.DeviceClass;
import javax.bluetooth.ServiceRecord;
import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.DiscoveryAgent;

import model.*;

public class MainMidlet extends MIDlet{
    private Display display;

    /**
     * Constructor. Constructs the object and initializes displayables.
     */
    public MainMidlet() {
        InitializeComponents();
    }
    /**
     * Initializes a ListBox object and adds softkeys.
     */
    protected void InitializeComponents() {
        display = Display.getDisplay( this );
    }

    /**
     * From MIDlet.
     * Called when MIDlet is started.
     * @throws javax.microedition.midlet.MIDletStateChangeException
     */
    public void startApp() throws MIDletStateChangeException {
       System.out.println("Starting app.....");
                String device_address = "";
                try{
                    device_address = LocalDevice.getLocalDevice().getBluetoothAddress();
                    System.out.println(device_address);
                }
                catch(Exception e){
                    e.printStackTrace();
                }
                if(device_address.equals(BTTrackerClient.trackerAddress)){
                    // start to listen for connections
                    System.out.println("I am tracker !!!");
                    BTTracker tracker = new BTTracker();
                    tracker.start();
                }
                else{
                    // find tracker

                    // send its record

                    // Get the list and update own recordStore

                    System.out.println("Not tracker ... starting client thread");
                    BTTrackerClient trackerClient = new BTTrackerClient();
                    trackerClient.start();
                 }
    }
    /**
     * From MIDlet.
     * Called to signal the MIDlet to enter the Paused state.
     */
    public void pauseApp() {
        //No implementation required
    }
    /**
     * From MIDlet.
     * Called to signal the MIDlet to terminate.
     * @param unconditional whether the MIDlet has to be unconditionally
     * terminated
     * @throws javax.microedition.midlet.MIDletStateChangeException
     */
    public void destroyApp(boolean unconditional)
        throws MIDletStateChangeException {
        exitMIDlet();
    }
    /**
     * From CommandListener.
     * Called by the system to indicate that a command has been invoked on a
     * particular displayable.
     * @param command the command that was invoked
     * @param displayable the displayable where the command was invoked
     */
    
    protected void exitMIDlet() {
     //   stopDiscover();
     //   notifyDestroyed();
    }
}
    