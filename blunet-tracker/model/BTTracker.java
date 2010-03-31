package model;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import java.io.*;
import java.util.*;

import javax.microedition.rms.*;
import javax.bluetooth.LocalDevice;

//import local.Labels;
//import local.Local;
//import blue.bExplore;
import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.UUID;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;
import util.Kits;
//import view.*;

/**
 *
 * @author alok
 */
public class BTTracker {

    public static String name = "Tracker";
    public static String id = "0x001";
    public static Hashtable recentActivity; // bt address -> BTTrackerActivity
    public static int T=2000;

    LocalDevice localDevice;
    private StreamConnectionNotifier notifier;
    UUID localUuid;


    public BTTracker(){
        recentActivity = new Hashtable();
        BTCounterThread counter = new BTCounterThread(T);

    }

    public static long getLastTS(String btAddress){
        BTTrackerActivity activity = (BTTrackerActivity)recentActivity.get(btAddress);
        if(activity==null){
            return(0);
        }
        return activity.aliveTS;
    }

    public static void addMember(String btName, String btUrl, String btAddress, boolean join){
        BTTrackerActivity activity;
        if(recentActivity.containsKey(btAddress)){
            activity = (BTTrackerActivity)recentActivity.get(btAddress);
            recentActivity.remove(btAddress);
            if(!activity.isConnected){
                activity.isConnected = true;
                activity.tstamp = System.currentTimeMillis();
            }
            activity.aliveTS = System.currentTimeMillis();
            if(join)
              activity.tstamp = System.currentTimeMillis();
            
        }
        else{
            activity = new BTTrackerActivity(true, btName, btUrl, btAddress);
            System.out.println("adding " + btName + "||" + btAddress + "||" + btUrl);
        }
        recentActivity.put(btAddress, activity);
        // log to a file
    }

    public static void removeMember(String btAddress){
        if(recentActivity.containsKey(btAddress)){
            BTTrackerActivity activity = (BTTrackerActivity)recentActivity.get(btAddress);
            recentActivity.remove(btAddress);
            activity.isConnected = false;
            activity.tstamp = System.currentTimeMillis();
            System.out.println("removing " + btAddress);
            recentActivity.put(btAddress, activity);
            // log to file
        }
    }

    public static void sendList(DataOutputStream out,long ts, boolean full){
        
        String devicesLeft = "";
        String devicesJoined = "";
        String finalList = "";
        //synchronized(recentActivity){
            //Enumeration e = recentActivity.keys();
            Enumeration e = recentActivity.elements();
            if(full){
                while(e.hasMoreElements()){
                    BTTrackerActivity activity = (BTTrackerActivity)(e.nextElement());
                    if(activity.isConnected){
                        devicesJoined = devicesJoined + activity.btName + "$" + activity.btUrl + "$" + activity.btAddress + "&";
                    }
                }
                if(devicesJoined.length()>0){
                   finalList = devicesJoined.substring(0, devicesJoined.length()-1);
                }
                
            }
            else{
                while(e.hasMoreElements()){
                    BTTrackerActivity activity = (BTTrackerActivity)(e.nextElement());
                    if(activity.tstamp > ts){
                        if(activity.isConnected)
                            devicesJoined = devicesJoined + activity.btName + "$" + activity.btUrl + "$" + activity.btAddress + "&";
                        else
                            devicesLeft = devicesLeft + activity.btName + "$" + activity.btUrl + "$" + activity.btAddress  + "&";
                    }
                }
                if(devicesJoined.length()>0 && devicesLeft.length()>0){
                    finalList = devicesJoined.substring(0, devicesJoined.length()-1)+"|"+devicesLeft.substring(0, devicesLeft.length()-1);
                }
                else if(devicesJoined.length()>0){
                    finalList = devicesJoined.substring(0, devicesJoined.length()-1);
                }
                else if(devicesLeft.length() > 0){
                    finalList = "|"+devicesLeft.substring(0, devicesLeft.length()-1);
                }
                
          //  }
        }
        try {
            System.out.println("FinalList : " + finalList);
            
            out.writeUTF(finalList);
            out.flush();
            System.out.println("Sent list " + finalList);
            System.out.println("Done with SendList");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    
    public void start(){
        // Listen for connection
        System.out.println("tracker started");
        try
        {
            localDevice = LocalDevice.getLocalDevice();
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
        }
        String s="3c8b24e00eac144a5ad92800555c9a66";
        localUuid=new UUID(s,false);
        

        try
        {
            boolean isDisc = localDevice.setDiscoverable(DiscoveryAgent.GIAC);
            if(isDisc == false)
            {
                System.out.println("Not Discoverable");
            }


            System.out.println("main thread again strted");
            notifier = (StreamConnectionNotifier)Connector.open("btspp://localhost:" + localUuid);

            //Threaded
            try{
            while(true){
                System.out.println("Waiting for connections...");
                StreamConnection conn = notifier.acceptAndOpen();
                BTTrackerConnectionHandler handler = new BTTrackerConnectionHandler(conn);
                handler.start();
                handler.join();
                }
            }
            catch(Exception e){
                e.printStackTrace();
            }

        }
        catch (Exception e){
            System.out.println("Error at create connection gyugfjsgjsdjhjsagduy \n"+e.getMessage());
            e.printStackTrace();
	}
    }
}

class BTCounterThread extends Thread{
    int T;
    public BTCounterThread(int T){
        this.T = T;
        this.start();
    }

    public void run(){
        while(true){
            long sleepTime=T;
            synchronized(BTTracker.recentActivity){
                //Enumeration e = BTTracker.recentActivity.keys();
                Enumeration e = BTTracker.recentActivity.elements();
                long currTS = System.currentTimeMillis();
                long minTS = currTS;
                while(e.hasMoreElements()){
                    BTTrackerActivity activity = (BTTrackerActivity)(e.nextElement());
                    if(activity.isConnected){
                        if( (currTS - activity.aliveTS) > (10*T)){
                            System.out.println(currTS + ", " + activity.aliveTS +", " + (10*T));
                            BTTracker.removeMember(activity.btAddress);
                            System.out.println("removing " + activity.btAddress + "due to timeout");
                        }
                        else{
                            if(minTS > activity.aliveTS)
                                minTS = activity.aliveTS;
                        }
                    }
                }
                sleepTime = (minTS + 10*T) - currTS;
                
            }
            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }
}