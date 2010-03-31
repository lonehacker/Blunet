package model;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */



import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Hashtable;
import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.LocalDevice;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;

import util.*;
/**
 *
 * @author dell
 */
public class BTTrackerClient extends Thread{

    public static String trackerUrl;
    public static String trackerAddress = "002186747AE2";
    StreamConnection conn;
    public static Hashtable deviceList;
    DataInputStream in;
    DataOutputStream out;

    public static boolean toClose = false;

    public BTTrackerClient(){
        BTURLExtractor urlext = new BTURLExtractor(trackerAddress);
        trackerUrl = urlext.getURL();
        deviceList = new Hashtable();
    }

    
    public void run(){
        System.out.println("client thread started");
        try {
            createConn();
            out.writeUTF("TR1");
            out.flush();
            String trList = in.readUTF();
            maintainList(trList);
            closeConn();
            Thread.sleep(BTTracker.T);

            while(!toClose){
                createConn();
                out.writeUTF("TR0");
                out.flush();
                trList = in.readUTF();
                maintainList(trList);
                closeConn();

                Thread.sleep(BTTracker.T);
            }

            createConn();
            out.writeUTF("TR2");
            out.flush();
            closeConn();
            
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void createConn(){
        try {
            conn = (StreamConnection) Connector.open(trackerUrl);
            in = conn.openDataInputStream();
            out = conn.openDataOutputStream();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void closeConn(){
        try {
            in.close();
            out.close();
            conn.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }


    public String getAddress(String str){
        StringTokenizer st = new StringTokenizer(str, "$");

        int count = 0;
        String name="";
        String address="";
        String url="";

        while(st.hasMoreTokens()){
            if(count == 0)
                name = st.nextToken();
            if(count == 1)
                url = st.nextToken();
            if(count == 2)
                address = st.nextToken();
            count++;
        }

        return address;
    }

    public BTDevice getDevice(String str){
        StringTokenizer st = new StringTokenizer(str, "$");

        int count = 0;
        String name="";
        String address="";
        String url="";

        while(st.hasMoreTokens()){
            if(count == 0)
                name = st.nextToken();
            if(count == 1)
                url = st.nextToken();
            if(count == 2)
                address = st.nextToken();
            count++;
        }
        BTDevice btDevice = new BTDevice(name, url, address);
        return btDevice;
    }

    public void editList(String changeList, boolean add){
        if(changeList.equals(""))
            return;

        StringTokenizer st = new StringTokenizer(changeList, "&");
        while(st.hasMoreTokens()){
            String strDevice = st.nextToken();
            String address = getAddress(strDevice);
            if(!deviceList.containsKey(address)){
                if(add){
                    LocalDevice ld;
                    try {
                        ld = LocalDevice.getLocalDevice();
                        if(!address.equals(ld.getBluetoothAddress())){
                           BTDevice btDevice = getDevice(strDevice);
                           System.out.println("Adding " + btDevice.name + "||" + btDevice.btAddress + "||" + btDevice.url);
                           deviceList.put(address, btDevice);
                    }
                    } catch (BluetoothStateException ex) {
                        ex.printStackTrace();
                    }
                }
                else{
                    System.out.println("Removing " + address);
                    deviceList.remove(address);
                }
            }
        }

    }

    public void maintainList(String trList){
       String addList = "";
       String removeList = "";
        if(trList.indexOf("|") >= 0 && trList.indexOf("|") < trList.length()){
            if(trList.charAt(trList.length()) != '|'){
                //StringTokenizer st = new StringTokenizer(trList, "|");
                int delimIndex = trList.indexOf("|");
                addList = trList.substring(0, delimIndex);
                removeList = trList.substring(delimIndex + 1, trList.length());
            }
            else
                addList = trList.substring(0, trList.length()-1);
        }
        else
            addList = trList;

       editList(addList, true);
       editList(removeList, false);

    }
}
