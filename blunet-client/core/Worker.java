package core;

import javax.bluetooth.LocalDevice;
import javax.microedition.io.StreamConnection;
import comm.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import util.Kits;

public class Worker {

    public DataStream dataStream;
    public int no;
    public boolean isConnected = false;
    public boolean isBusy = false;
    public Chat chatObject = null;
    public Search searchObject = null;
    public FileTransfer fileTransferObject = null;
    public Scheduler sched;
    public OBEXSession session;

    public Worker(int number){
        this.no=number;
        //this.searchObject = sched.midlet.formSearch.search;
    }

    public void takeConnection(StreamConnection conn, DataInputStream in, DataOutputStream out){
        try{
            this.isBusy = true;
            dataStream = new DataStream(conn, this.no);

          
            dataStream.open(conn,in,out);
           dataStream.name = dataStream.dis.readUTF();
            LocalDevice localdevice = LocalDevice.getLocalDevice();
            dataStream.dos.writeUTF(localdevice.getFriendlyName());
            dataStream.dos.flush();

            dataStream.startAngel();
            this.isConnected = true;
            
        }catch(Exception e){
            System.out.println("worker "+no+": Error in taking the connection");
            dataStream.isConnected = false;
        }
    }

    public void takeConnection(StreamConnection conn, String path){
        OBEXSession obexSession = (OBEXSession)dataStream;
        try{
            //DO OBEXblah blah here
            //obexSession.connect(OBEXHeaderh abfsldkafl);
            obexSession.isConnected = true;
        }
        catch(Exception e){
            System.out.println("worker "+no+": Error in taking the connection");
            obexSession.isConnected = false;
        }
    }

     public boolean createObexConnection(String url)
    {
        session = new OBEXSession(url, no);
        return true;

    }

    public boolean createConnection(String url, String type){
        try
         {
            if(url!=null)
            {
                System.out.println("worker "+no+": Attempting to initiate chat with " + url);
                dataStream = new DataStream(url, this.no);
                System.out.println("worker "+no+": Datastream created");
                //dataStream.url=url;
                System.out.println("worker "+no+": Attempt to open datastream");
                dataStream.open();
                System.out.println("worker "+no+": Datastream opened yippeee. connecting:"+type);

                dataStream.dos.writeUTF("RequestToConnect "+type);
                dataStream.dos.flush();

                String s=dataStream.dis.readUTF();
                System.out.println("worker "+no+": remote machine replied "+s);
                if(s.equals("yes"))
                {
                    System.out.println("worker "+no+": remote machine is ready to connect");

                    dataStream.isConnected=true;
                    LocalDevice localdevice = LocalDevice.getLocalDevice();
                    dataStream.dos.writeUTF(localdevice.getFriendlyName());
                    dataStream.dos.flush();
                    System.out.println("worker "+no+": Going to receive name...");

                    dataStream.name = dataStream.dis.readUTF();

                    System.out.println("worker "+no+": name of remote machine " + dataStream.name);
                    testClass.w[no].isConnected = true;

                    dataStream.startAngel();

                    if(type.equals("filetransfer"))
                    {
                        //CHECK WHAT TO DO ?
                    }
                }
                else if(s.equals("no") && !type.equals("connect"))             // Handles reconnect after some time
                {
                    Kits.sleep(5000);
                    //return createConnection(url, type);
                }
            }
         }
         catch(Exception e){
             System.out.println("we got some error in createConnection method" +e.getMessage());
            e.printStackTrace();
         }


        return dataStream.isConnected;
    }

    /*
public boolean createConnection(String url, String path){
    OBEXSession obexSession = (OBEXSession)dataStream;
    try
         {
            if(url!=null)
            {
                obexSession.url=url;
                obexSession.connect();

                obexSession.isConnected=true;
            }
         }
         catch(Exception e){
             System.out.println("we got some error in createConnection method" +e.getMessage());
            e.printStackTrace();
         }
        return obexSession.isConnected;
    }
*/
    
    public void closeConnection(){
      try{
        dataStream.close();
      }catch(Exception e){
          System.out.println(no+": Error in closing the connection");
      }
    }

    public void setChatObject(Chat chatObject)  {
        this.chatObject = chatObject;
    }

    public void setSearchObject(Search searchObject)  {
        this.searchObject = searchObject;
    }

    public void setFileTransferObject(FileTransfer fileTransferObject)  {
        this.fileTransferObject = fileTransferObject;
    }

}
