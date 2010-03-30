package model;



import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.DeviceClass;
import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.DiscoveryListener;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.ServiceRecord;
import javax.bluetooth.UUID;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;

public class BTURLExtractor extends Thread implements DiscoveryListener
{
    UUID uuid;
    LocalDevice localDevice;
    private DiscoveryAgent discAgent;
    DataInputStream input;
    DataOutputStream output;
    boolean connected=false;
    private ServiceRecord service;
    private StreamConnection conn;
    public String url=null;
    RemoteDevice2 a;
    int t;

   BTURLExtractor(String btid)
    {

        a=new RemoteDevice2(btid);
        uuid=new UUID("3c8b24e00eac144a5ad92800555c9a67",false);
    }

    public String getURL()
    {
      outConn();
       while(url==null);
      return url;
    }

    public void outConn()
    {
        try
        {
            UUID uuid2=new UUID(0x0003);
            localDevice = LocalDevice.getLocalDevice();
            discAgent = localDevice.getDiscoveryAgent();
             localDevice.setDiscoverable(DiscoveryAgent.GIAC);
             int transationID = discAgent.searchServices(new int[] { 0x0100 }, new UUID[] { uuid2 }, a,this);
             System.out.println(t+": Transaction id = "+transationID);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }



    public void deviceDiscovered(RemoteDevice btDevice, DeviceClass cod)
    {
        System.out.println(t+": new device discovered");
    }
    public void inquiryCompleted(int discType)
    {
        System.out.println(t+": Discovery completed");
    }

    public void servicesDiscovered(int arg0, ServiceRecord[] servRecord)
    {
        service = servRecord[0];
        //System.out.println(t+": a sevice discovered");
        //System.out.println(servRecord.length);
        //System.out.println(service.getConnectionURL(ServiceRecord.NOAUTHENTICATE_NOENCRYPT, false));
    }

    public void serviceSearchCompleted(int transID, int respCode)
    {

        switch (respCode)
        {
            case SERVICE_SEARCH_TERMINATED:
            System.out.println(t+": The search for services was cancelled!");
            break;

            case SERVICE_SEARCH_ERROR:
            System.out.println(t+": The search for services gave an error");
            break;

            case SERVICE_SEARCH_NO_RECORDS:
            System.out.println(t+": No services found!");
            break;

            case SERVICE_SEARCH_DEVICE_NOT_REACHABLE:
            System.out.println(t+": The remote device isn't reachable!");
            break;

            case SERVICE_SEARCH_COMPLETED:
            try
            {
                createConnection();
                System.out.println(t+": Service search completed here  "+t);
            }
            catch (Exception e)
            {
                System.out.println(t+": error error");
            }
            break;
        }
    }



    private void createConnection()
    {
        System.out.println(t+": beginning og createconn");
        try
        {
            System.out.println(t+": before url = "+url);
            url = service.getConnectionURL(ServiceRecord.NOAUTHENTICATE_NOENCRYPT, false);
            System.out.println(t+": after url = "+url);

        }
        catch (Exception e)
        {
            System.out.println("we got some rror in createConnection method" +e.getMessage());
            e.printStackTrace();
         }

    }

}
