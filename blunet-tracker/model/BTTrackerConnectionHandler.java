package model;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */



import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import javax.bluetooth.RemoteDevice;
import javax.microedition.io.StreamConnection;

/**
 *
 * @author dell
 */
public class BTTrackerConnectionHandler extends Thread{

    private StreamConnection conn;
    private DataInputStream in;
    private DataOutputStream out;

    public BTTrackerConnectionHandler(StreamConnection conn) {
        try {
            this.conn = conn;
            in = conn.openDataInputStream();
            out = conn.openDataOutputStream();
            
            //this.start();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public long getLastTS(String btAddress){
        BTTrackerActivity activity = (BTTrackerActivity)BTTracker.recentActivity.get(btAddress);
        return activity.aliveTS;
    }

    public void addMember(String btName, String btUrl, String btAddress, boolean join){
        BTTrackerActivity activity;
        if(BTTracker.recentActivity.containsKey(btAddress)){
            activity = (BTTrackerActivity)BTTracker.recentActivity.get(btAddress);
            BTTracker.recentActivity.remove(btAddress);
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
        BTTracker.recentActivity.put(btAddress, activity);
        // log to a file
    }

    public void removeMember(String btAddress){
        if(BTTracker.recentActivity.containsKey(btAddress)){
            BTTrackerActivity activity = (BTTrackerActivity)BTTracker.recentActivity.get(btAddress);
            BTTracker.recentActivity.remove(btAddress);
            activity.isConnected = false;
            activity.tstamp = System.currentTimeMillis();
            System.out.println("removing " + btAddress);
            BTTracker.recentActivity.put(btAddress, activity);
            // log to file
        }
    }

    public void sendList(DataOutputStream out,long ts, boolean full){

        String devicesLeft = "";
        String devicesJoined = "";
        String finalList = "";
        synchronized(BTTracker.recentActivity){
            //Enumeration e = recentActivity.keys();
            Enumeration e = BTTracker.recentActivity.elements();
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

            }
        }
        try {
            System.out.println("FinalList : " + finalList);

            out.writeUTF(finalList);
            out.flush();
            System.out.println("Sent list " + finalList);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    


    public void run(){
        System.out.println("handler thread started");
        try {
            String strRcvd="";
            
            //strRcvd = in.readUTF();

            
            long start = System.currentTimeMillis();
            while(System.currentTimeMillis() < (start+50) || in.available()>0){
                if(in.available()>0) {
                    strRcvd = in.readUTF();

                    System.out.println(strRcvd);
                    String reqType = strRcvd.substring(2);
                    System.out.println(reqType);
                    RemoteDevice rd = RemoteDevice.getRemoteDevice(conn);
                    String btName;
                    String btAddress;
                    String btUrl;

                    if (reqType.equals("0")) {
                        long lastTS = BTTracker.getLastTS(rd.getBluetoothAddress());
                        btAddress = rd.getBluetoothAddress();
                        System.out.println("got I am Alive from " + btAddress);

                        btName = rd.getFriendlyName(false);
                        System.out.println(btAddress+" Friendly name " + btName);

                        BTURLExtractor extractor = new BTURLExtractor(btAddress);

                        System.out.println(btAddress+" Getting url");
                        btUrl = extractor.getURL();
                        System.out.println("Recieved url " + btUrl);

                        System.out.println(btAddress+ " to add mem");
                        this.addMember(btName, btUrl, btAddress, false);

                        this.sendList(out, lastTS, false);
                    }
                    else if (reqType.equals("1")) {
                        btAddress = rd.getBluetoothAddress();
                        System.out.println("Got joining from " + btAddress);

                        btName = rd.getFriendlyName(false);
                        System.out.println(btAddress+" Friendly name " + btName);

                        BTURLExtractor extractor = new BTURLExtractor(btAddress);

                        System.out.println(btAddress+" Getting url..");
                        btUrl = extractor.getURL();
                        System.out.println("Recieved url " + btUrl);

                        System.out.println(btAddress+" to add mem ..");
                        this.addMember(btName, btUrl, btAddress, true);

                        this.sendList(out, System.currentTimeMillis(), true);
                    } else if (reqType.equals("2")) {
                        // Leaving
                        System.out.println("Got leaving from " + rd.getBluetoothAddress());
                        this.removeMember(rd.getBluetoothAddress());
                    } else {
                        System.out.println("Invalid Tracker request");
                    }
                    break;
                }
            }
            in.close();
            System.out.println("in closed");
            out.close();
            System.out.println("out closed");
            conn.close();
            System.out.println("conn closed");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
