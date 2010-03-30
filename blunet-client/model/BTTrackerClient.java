package model;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */



import blunet.blunet;
import core.BTDesc;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Enumeration;
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
    public static int T=2000;

    public blunet midlet;
    public static boolean toClose = false;

    public BTTrackerClient(blunet midlet){
        BTURLExtractor urlext = new BTURLExtractor(trackerAddress);
        trackerUrl = urlext.getURL();
        deviceList = new Hashtable();
        this.midlet = midlet;
    }


    public void run(){
        System.out.println("client thread started");
        String trList;
        try {
            createConn();
            out.writeUTF("TR1");
            out.flush();
            trList = in.readUTF();

            System.out.println("I am recieving from tracker " + trList);
            maintainList(trList);

            System.out.println("lists maintained");

            for(int i = 0 ; i< midlet.userList.size(); i++)
            {
                System.out.println(midlet.userList.elementAt(i).toString());
            }
            midlet.tabNeighbor.update();




            closeConn();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

            while(!toClose){
                try{


                    createConn();
                    out.writeUTF("TR0");
                    out.flush();
                    trList = in.readUTF();
                    maintainList(trList);
                    midlet.tabNeighbor.update();



                    closeConn();
                }
                catch(Exception ex)
                {
                    System.out.println(ex.getMessage());
                }

                Kits.sleep(this.T);
            }

            try{


            createConn();
            out.writeUTF("TR2");
            out.flush();
            closeConn();
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }


    }

    public void createConn() throws Exception
    {

            conn = (StreamConnection) Connector.open(trackerUrl);
            in = conn.openDataInputStream();
            out = conn.openDataOutputStream();


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

    public BTDesc getDevice(String str){
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
        BTDesc btDevice = new BTDesc(name, url, address);
        return btDevice;
    }

    public void editList(String changeList, boolean add){
        System.out.println(changeList+" add? "+add);
        Enumeration e = deviceList.keys();
        while(e.hasMoreElements()){
            System.out.println("btadd: "+e.nextElement());
        }

        if(changeList.equals(""))
            return;

        StringTokenizer st = new StringTokenizer(changeList, "&");
        while(st.hasMoreTokens()){
            String strDevice = st.nextToken();
            String address = getAddress(strDevice);
            if(!deviceList.containsKey(address)){
                if(add){
                    System.out.println("adding...");
                    LocalDevice ld;
                    try {
                        ld = LocalDevice.getLocalDevice();
                        if(!address.equals(ld.getBluetoothAddress())){
                           BTDesc btDevice = getDevice(strDevice);
                           System.out.println("Adding " + btDevice.name + "||" + btDevice.btAddress + "||" + btDevice.url);
                           deviceList.put(address, btDevice);
                           midlet.userList.addElement(btDevice);

                    }
                    } catch (BluetoothStateException ex) {
                        ex.printStackTrace();
                    }
                }
                //ALOK's Code
                /*
                else{
                    System.out.println("Removing " + address);

                    midlet.userList.removeElement(deviceList.get(address));
                    deviceList.remove(address);
                }
                 */
            }
            else{
                //BJP's code
                if(!add){
                    System.out.println("Removing " + address);

                    midlet.userList.removeElement(deviceList.get(address));
                    deviceList.remove(address);
                }
            }
        }

        e = deviceList.keys();
        while (e.hasMoreElements()) {
            System.out.println("btadd: " + e.nextElement());
        }

    }

    public void maintainList(String trList){
       System.out.println("Tracker List : "+trList);
       String addList = "";
       String removeList = "";
        if(trList.indexOf("|") >= 0 && trList.indexOf("|") < trList.length()){
            if(trList.charAt(trList.length()-1) != '|'){
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

       System.out.println("Add List : "+addList);
       System.out.println("Remove List : "+removeList);
       editList(addList, true);
       editList(removeList, false);

    }
}
