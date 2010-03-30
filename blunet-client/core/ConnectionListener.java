package core;

import java.io.*;
import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.UUID;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;
import util.Kits;

class ConnectionListener extends Thread
{
    LocalDevice localDevice;
    private StreamConnectionNotifier notifier;
    UUID localUuid;
    DataInputStream in;
    DataOutputStream out;
    StreamConnection conn;
    int maxConn;

    Scheduler sched;

    public ConnectionListener(Scheduler scheduler)
    {
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

        this.sched=scheduler;
    }

    public void run()
    {
        System.out.println("starting the connection Listener");
        listenConn();

    }

    void listenConn()
    {
		try
        {
			boolean isDisc = localDevice.setDiscoverable(DiscoveryAgent.GIAC);
           // System.out.println(localDevice.getBluetoothAddress());
            if(isDisc == false)
            {
                System.out.println("Not Discoverable");
            }
             notifier = (StreamConnectionNotifier)Connector.open("btspp://localhost:" + localUuid);
             int no=0;
             while(true)
             {
               // System.out.println("main thread begins");
                conn = notifier.acceptAndOpen();
                in=conn.openDataInputStream();
                out = conn.openDataOutputStream();
                String s = in.readUTF();
                String type="connect";

                if(s.indexOf("chat")>0)
                    type="chat";
                if(s.indexOf("filetransfer")>0)
                    type="filetransfer";
                if(s.indexOf("connect")>0)
                    type="connect";

                while(sched.isBusy.busy==true);
                sched.isBusy.busy=true;

                int free = searchFreeThreadBadly(type);

                if(free==-1)    {
                  
                    out.writeUTF("no");
                    out.flush();
                    out.close();
                    in.close();
                    conn.close();
                }
                else    {
                    out.writeUTF("yes");
                    out.flush();
                    testClass.w[free].takeConnection(conn,in,out);
                    
                }

                sched.isBusy.busy=false;
                Kits.sleep(2000);
          }
		}
        catch (Exception e)
        {
			System.out.println("Error somewhere in listen connection: "+e.getMessage());
            e.printStackTrace();
		}
    }

    private int searchFreeThread(String type){
       for(int i=0;i<testClass.w.length;i++){
           if(testClass.w[i].dataStream.isConnected==false){
               return i;
           }
       }
       return -1;
    }


    private int searchFreeThreadBadly(String type){
       for(int i=0;i<testClass.w.length;i++){
           if(testClass.w[i].isConnected==false){
               return i;
           }
       }
       if(type.equals("connect")) return -1;

       for(int i=0;i<testClass.w.length;i++){
           if(testClass.w[i].isBusy==false){
               return i;
           }
       }
       return -1;
    }
}


