package core;
import util.Kits;
import java.io.IOException;
import comm.*;
import java.util.Vector;
import local.Labels;
import local.Local;
import view.FormChat;

public class WorkerAngel extends Thread {

    public DataStream dataStream;
   // public boolean isBusy;
    public int busyFlag = 0;
    public boolean stopFlag=false;
   
    public WorkerAngel(DataStream dataStream){
        this.dataStream=dataStream;
    }

    public void sendMessageToChat(String message)   {
        try {


        if(testClass.w[dataStream.w].chatObject!= null)
        {
            testClass.w[dataStream.w].chatObject.receiveMessage(message);
    
        }
        else
        {

            //System.out.println("Worker angel " + dataStream.w.no  + ": Found chat object to be null");
            dataStream.btDesc.name = dataStream.name;
            System.out.println("Worker angel " + dataStream.w  + ": buddy name"  +dataStream.name);
            FormChat tabChat = new FormChat(testClass.midlet, Local.get(Labels.CHAT), dataStream.btDesc, dataStream.name, testClass.w[dataStream.w].no);
            System.out.println("Worker angel " + dataStream.w  + ":  formchat called"  );
            testClass.midlet.taskList.addElement(tabChat);
            System.out.println("Worker angel " + dataStream.w  + ":  added to task");
            tabChat.setIndex(testClass.midlet.taskList.indexOf(tabChat));
            System.out.println("Worker angel " + dataStream.w  + ": indexSet of tabChat ");
            testClass.midlet.tabHelp.update();
            System.out.println("Worker angel " + dataStream.w  + ": tasklist updated ");

            testClass.w[dataStream.w].chatObject = tabChat.currentChat;
            testClass.w[dataStream.w].chatObject.receiveMessage(message);
        }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

    }


    public void sendResultToSearch(BTDesc source, String path)   {
        testClass.w[dataStream.w].searchObject.receiveResult(source, path);
    }

    public void searchLocally(String query)
    {
        Vector index = testClass.midlet.index.index;


        executeQueryAndReturnResult(query, index);
    }

    public void executeQueryAndReturnResult( String query,Vector index)   {

        int res;

        for(int i = 0; i< index.size(); i++)   {
            String item = index.elementAt(i).toString();
            res = item.indexOf(query);
            System.out.println("WA : " + "Result of the local search " + res + " query was" + query);

            if(res>=0)  {
                try {
                    //USE SHA! HASHING LATER TO UNIQUIFY SEARCH QUERIES - hahaha, good one. :p

                    System.out.println("Sending .." + item);
                    dataStream.dos.writeUTF("3searchresult" + query + "|" + item);
                    dataStream.dos.flush();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    public void run()  {
        while(!stopFlag)    {

            
            if(busyFlag <= 0)
                  dataStream.isBusy = false;

            busyFlag--;

            try {
                String s = dataStream.dis.readUTF();
                if(s!=null) {

                    testClass.w[dataStream.w].isBusy = true;
                    busyFlag = 10;
                    if(s.indexOf("1chat")==0)    {
                           sendMessageToChat(s.substring(5));
                        System.out.println("Worker angel " + testClass.w[dataStream.w].no  + ": Message recieved: " + s);
                    }
                    if(s.indexOf("2search")==0) {
                        System.out.println("Worker angel " + testClass.w[dataStream.w].no  + ": Search recieved: " + s);
                        searchLocally(s.substring(8));
                    }
                    if(s.indexOf("3searchresult")==0) {
                        System.out.println("Worker angel " + testClass.w[dataStream.w].no  + ": Result recieved: " + s);
                        sendResultToSearch(dataStream.btDesc ,s.substring(13));
                    }
                } else {
                    Kits.sleep(1000);
                }

            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

}
