package core;

import model.*;
import blunet.blunet;
import java.io.IOException;
import java.util.Vector;
import util.Kits;
import view.FormSearch;
import java.util.Enumeration;


/*
 * Chat 10
 * Search   5
 * FileTransfer 2
 */

public class Scheduler extends Thread {
    public int maxConn=5;
    public Busy isBusy = new Busy();
    public Busy addRequestIsBusy = new Busy();
    Vector queue = new Vector();
    int lastIndexOfQueue=0;
    ConnectionListener connListen;
    public blunet midlet;
    public testClass testClass;

    public Scheduler(blunet midlet)
    {

        this.midlet = midlet;
        //WORKER INIT CODE

        try {



            //w[i].searchObject = this.midlet.formSearch.search;

        }
        catch(Exception e)
        {
            e.printStackTrace();
            System.out.println(e.toString());
        }
        //USE TRACKER LIST TO CONNECT

        //INIT connListen and pass on Workers
        connListen = new ConnectionListener(this);
        connListen.start();

        System.out.println("scheduler: connection Listener thread started");
    }


    public void run()
    {
        boolean setFlag;
        while(true) {
           setFlag = false;
            if(!queue.isEmpty())    {
               //  System.out.println("Hello queue is empty: " + queue.isEmpty());

                RequestProfile rp = (RequestProfile)queue.firstElement();

                    for(int i = 0 ; i < testClass.w.length; i++)
                    {
                        if(testClass.w[i].isConnected)
                        {
                            if(testClass.w[i].dataStream.name.equals(rp.targetName))
                            {
                                setFlag = true;
                                if(rp.type.equals("chat"))
                                {
                                    ((Chat)rp.object).setWorker(i);
                                }
                                else if(rp.type.equals("filetransfer"))
                                {
                                    ((FormSearch)rp.object).setWorker(i);
                                }
                                queue.removeElementAt(0);
                            }
                        }
                    }

                    if(setFlag == false)
                    {
                        int free=searchFreeThreadBadly();

                        if(free!=-1)    {


                            allotRequestToWorker((RequestProfile)queue.firstElement(), free);
                            queue.removeElementAt(0);
                        }
                        else    {
                            Kits.sleep(500);
                        }
                    }
                }
                else    {
                    Kits.sleep(1000);
                }

             //search if there are unconnected threads and unconnected urls

            // TODO : Uncomment this and run properly
            /**************************************
            int free = searchFreeThread();
            String url = searchFreeURL();
            if(free!=-1 && url!=null){
                w[free].createConnection(url,"connect");
            }*/

        }
    }

    public void addRequest(RequestProfile request)
    {
        while(addRequestIsBusy.busy)
        {
            System.out.println("Waiting for Request queue to be free");
            Kits.sleep(500);
        }
        addRequestIsBusy.busy = true;

        int intType = 0 , i;
        String type;

        type = request.type;


        if(type.equals("chat"))
            intType=0;

        else if(type.equals("search"))
            intType=1;

        else if(type.equals("filetransfer"))
            intType=2;


        switch(intType)    {
            case 0:
                request.priority = 10;
                break;
            case 1:
                request.priority = 5;
                break;
            case 2:
                request.priority = 2;
                break;
        }

            System.out.println("Scheduler: Trying to add request in Scheduler");

            for(i=0; i<queue.size();i++)    {

                if(((RequestProfile) queue.elementAt(i)).priority<request.priority) {

                    break;
                }
            }
            queue.insertElementAt(request, i);
            System.out.println("scheduler: Request added at position " + i);
            addRequestIsBusy.busy = false;
    }


    private void allotRequestToWorker(RequestProfile request, int free)  {

        int intType = 0;
        String type;
        type = request.type;

        if(type.equals("chat"))
            intType=0;

        else if(type.equals("filetransfer"))
            intType=2;

        switch(intType)    {
            case 0:
                ((Chat)request.object).setWorker(free);
                System.out.println("scheduler: Worker is set for " + ((Chat)(request.object)).buddy.name + " " +  testClass.w[free].isConnected );
               // testClass.w[free].isConnected = true;

                break;
            case 2:
                ((FormSearch)request.object).setWorker(free);  // Do proper push popping
                //testClass.w[free].isConnected = true;

                break;
        }
    }


    public void searchAmongCurrentWorkers(Search searchObject) throws IOException  {

        System.out.println(testClass.w[0].dataStream.btDesc.name);

        for(int i=0;i<testClass.w.length;i++) {

            if(testClass.w[i].isConnected == true) {

                testClass.w[i].dataStream.dos.writeUTF("2search "+searchObject.query);
                testClass.w[i].dataStream.dos.flush();
            }
            //System.out.println("scheduler: Thread " + i + "Not Connected");
        }
    }


    //IMPLEMENT SEARCHING FOR THREADS WAITING TO BE BROKEN
    public int searchFreeThread()   {
        try {


       for(int i=0;i<testClass.w.length;i++){
           if(testClass.w[i].isConnected==false){
               return i;
           }
       }

        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
       return -1;
    }



 public int searchFreeThreadBadly()   {
       for(int i=0;i<testClass.w.length;i++){
           if(testClass.w[i].isConnected==false){
               return i;
           }
       }

       for(int i=0;i<testClass.w.length;i++){
           if(testClass.w[i].isBusy==false){
               return i;
           }
       }
       return -1;
    }

    public String searchFreeURL(){
        String url = null;
        boolean flag=false;
        for (Enumeration e = midlet.deviceList.elements() ; e.hasMoreElements() ;) {
            BTDesc neighbor = (BTDesc) e.nextElement();
            for(int i=0;i<testClass.w.length; i++){
                if(testClass.w[i].isConnected==true){
                    if(testClass.w[i].dataStream.name.equals(neighbor.name)){
                           flag=true;


                    }
                }
            }
            if(flag==false){
               url = neighbor.url;
                System.out.println("scheduler: returning url = "+ url);
                return url;
            }
        }
        return url;
    }
}
