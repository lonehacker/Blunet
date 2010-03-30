package blunet;


import java.util.Vector;
import javax.bluetooth.BluetoothStateException;
import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;
import core.*;
import java.util.Enumeration;
import java.util.Hashtable;
import javax.bluetooth.LocalDevice;
import model.BTTrackerClient;
import model.BTURLExtractor;
import util.FileIndex;
import util.Kits;
import view.*;

public class blunet extends MIDlet {

        private static blunet myInstance;

        private static Display display;

        public String localFolder = "C:/Data/Sounds/";

        //OLD CODE
        //public static int maxConn = 5;
        public TabFormHelp tabHelp;
        public TabFormFile tabFile;
        public TabFormNeighbor tabNeighbor;
        public FormSearch formSearch;

        public FileIndex index;

        public Vector taskList = new Vector();
        public Vector userList = new Vector();

	public static Font plainFont = Font.getFont(
					Font.FACE_PROPORTIONAL, Font.STYLE_PLAIN, Font.SIZE_SMALL);
	public static Font boldFont = Font.getFont(
					Font.FACE_PROPORTIONAL, Font.STYLE_BOLD, Font.SIZE_SMALL);
	public static Font mediumFont = Font.getFont(
					Font.FACE_PROPORTIONAL, Font.STYLE_PLAIN, Font.SIZE_MEDIUM);
	
        public Scheduler sched;

        public BTTrackerClient trackerClient;
        public Hashtable deviceList;

        public testClass tc;

	public blunet() {
		super();
		display = Display.getDisplay(this);
		myInstance = this;


       // BTURLExtractor btue = new BTURLExtractor("00247E4AF9BE");
       // System.out.println(btue.getURL());

        
       //  System.out.println("blunet: tracker thread started");

    }

	/**
	 * @return a reference to this MIDLet
	 */
	static public blunet getInstance() {
		return myInstance;
	}

	/**
	 * @return reference to the Display
	 */
	static public Display getDisplay() {
		return display;
	}

	protected void startApp() throws MIDletStateChangeException {


                           LocalDevice localdevice;
            try {
                localdevice = LocalDevice.getLocalDevice();
                System.out.println(localdevice.getBluetoothAddress());

            } catch (BluetoothStateException ex) {
                ex.printStackTrace();
            }

    System.out.println("blunet: inside blunet constructor");

        trackerClient = new BTTrackerClient(this);
         trackerClient.start();

        //BTDesc amit = new BTDesc("amit", "btspp://00247E4AF9BE:8;authenticate=false;encrypt=false;master=false", "00247E4AF9BE");
        //BTTrackerClient.deviceList.put("00247E4AF9BE",amit);



        tc = new testClass();

        testClass.midlet = this;
        System.out.println("blunet: Now starting App");
                // screen and model
                //INSERT UI INIT CODE HERE

         sched = new Scheduler(this);
         sched.start();

         System.out.println("bluenet: scheduler thread started");
                //Yippeee!



        this.deviceList = trackerClient.deviceList;

        Enumeration e = deviceList.keys();

        while(e.hasMoreElements())
        {
            userList.addElement(deviceList.get(e.nextElement()));
            System.out.println("bluenet: Adding a neighbour");
        }

        

        tabNeighbor = new TabFormNeighbor(this, "Users");
        tabNeighbor.update();

        tabHelp = new TabFormHelp(this, "Tasks");

        formSearch = new FormSearch(this, "Search", "Search");
        taskList.addElement(formSearch);
        formSearch.setIndex(taskList.indexOf(formSearch));
        tabHelp.update();

        

        //display.setCurrent(form);

       // System.out.println("I am here");
        index = new FileIndex(this, localFolder);
        System.out.println("Iam here too" + index.index.toString());



        tabNeighbor.makeActive();
	}

	/**
	 * 
	 */
	public void destroyApp(boolean arg0) throws MIDletStateChangeException {
		trackerClient.toClose=true;
        Kits.sleep(4*trackerClient.T);

        // close everything
		notifyDestroyed();
	}


    public void setDeviceList(Hashtable deviceList)
    {
        this.deviceList = deviceList;

        Enumeration e = deviceList.keys();
        userList.removeAllElements();
        
        while(e.hasMoreElements())
        {
            userList.addElement(deviceList.get(e.nextElement()));
            System.out.println("bluenet: Adding a neighbour");
        }

        tabNeighbor.update();

    }
	/**
	 * 
	 */
	protected void pauseApp() {
		// not used on Symbian
	}

        public void changeScreen(BaseScreen bs)
        {
            bs.makeActive();
        }


}
