package core;

import model.*;
import comm.DataStream;
import java.io.IOException;
import java.util.Vector;
import view.*;
//import util.SHA1;



public class Search  {
    FormSearch formSearch;
    String query;
    Vector results = new Vector();
    DataStream dataStream;

    public Search(String query, FormSearch formSearch) throws IOException   {
        this.query = query;
        this.formSearch = formSearch;

        for(int i=0;i<testClass.w.length;i++) {

            if(testClass.w[i].isConnected == true) {
                testClass.w[i].dataStream.dos.writeUTF("2search "+query);
                testClass.w[i].dataStream.dos.flush();
            } else {
                System.out.println("scheduler: Thread " + i + "Not Connected");
            }
        }

    }


    //MOVE TO MULTITHREADED WORKER ANGELS
    public void receiveQuery( String query)   {

        int res;
        for(int i = 0; i< formSearch.midlet.index.index.size(); i++)   {
            String item = formSearch.midlet.index.index.elementAt(i).toString();
            res = item.indexOf(query);
            if(res>=0)  {
                try {
                    //USE SHA! HASHING LATER TO UNIQUIFY SEARCH QUERIES
                    dataStream.dos.writeUTF("3searchresult" + query + " ");
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    public void receiveResult(BTDesc source, String path)  {
        System.out.println("search: results are received. "+ path);
        ResultEntry resultEntry = new ResultEntry();
        resultEntry.path = path;
        resultEntry.source = source;
        results.addElement(resultEntry);
        formSearch.update(resultEntry);
    }


}