package comm;

import core.BTDesc;
import java.io.*;

import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import core.*;
import model.*;

public class DataStream {
	public StreamConnection streamConn;
	public DataInputStream dis;
	public DataOutputStream dos;
	public String url;
	public boolean isConnected = false;
    public boolean isBusy = false;
    public WorkerAngel workerAngel;
    public int w;
    public BTDesc btDesc;
    public String name;


	// Folder Browsing UUID: F9EC7BC4-953C-11D2-984E-525400DC9E09
	public static byte[] FBUUID = 
			{(byte)0xF9,(byte)0xEC,(byte)0x7B,(byte)0xC4,(byte)0x95,(byte)0x3C,(byte)0x11,(byte)0xD2,
             (byte)0x98,(byte)0x4E,(byte)0x52,(byte)0x54,(byte)0x00,(byte)0xDC,(byte)0x9E,(byte)0x09};

	public DataStream(String url, int w) {
		//btgoep://0013FD94B84F:10;authenticate=false;encrypt=false;master=true
		//btgoep://0013FD94B84F:10;authenticate=false;encrypt=false;master=true;name=this server
		btDesc = new BTDesc();

        this.url = url;
        this.w = w;
        this.btDesc.url=url;
	}

    public DataStream(StreamConnection streamConn, int w) {
		//btgoep://0013FD94B84F:10;authenticate=false;encrypt=false;master=true
		//btgoep://0013FD94B84F:10;authenticate=false;encrypt=false;master=true;name=this server
		btDesc = new BTDesc();
        this.streamConn = streamConn;
        this.w = w;
	}

	public void open() throws IOException {
		streamConn = (StreamConnection) Connector.open(url);
        if(streamConn==null) System.out.println("datastream "+this.w+": error in open() connection");

        dis = streamConn.openDataInputStream();
        System.out.println("datastream "+this.w+": input stream set");

		dos = streamConn.openDataOutputStream();
        
		System.out.println("datastream "+this.w+": I/O stream recieved");

        isConnected = true;

         testClass.w[w].isConnected = true;

       
	}

    public void startAngel(){
         workerAngel = new WorkerAngel(this);
        workerAngel.start();
    }

    public void open(StreamConnection conn, DataInputStream in, DataOutputStream out) throws IOException {
               streamConn = conn;
               dis = in;
               dos = out;
               isConnected = true;
               testClass.w[w].isConnected = true;

        
	}

	public void close() throws IOException {
        workerAngel.stopFlag=true;
      
        if (dos != null)
			dos.close();
		if (dis != null)
			dis.close();
		if (streamConn != null)
			streamConn.close();
		isConnected = false;
        testClass.w[w].isConnected = false;
	}
	
	public boolean isConnected() {
		return isConnected;
	}


    public boolean isBusy() {
		return isBusy;
	}

}

