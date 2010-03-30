package view;




import java.io.IOException;
import java.util.Vector;
import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.TextField;
import core.*;
import blunet.blunet;
import comm.*;
import util.Kits;

public class FormSearch extends DisplayForm {




        Display display;
        Vector results = new Vector();
        int wD = 4;

        TextField searchBox = new TextField("Search", "", 40, TextField.ANY);

        ChoiceGroup resultBox = new ChoiceGroup("Results", ChoiceGroup.EXCLUSIVE);
        public Search search;
        //ModelSearch model;


	Command searchCmd = new Command("Search", Command.SCREEN, 2);
    Command downloadCmd = new Command("Download File", Command.SCREEN, 2);
    FileTransfer fileTransfer;

    int w = -1;

	public FormSearch(blunet midlet, String tabName, String name) {


                super(midlet, false, name);

              //  model =  new ModelSearch(midlet, this);
		this.setTitle(name);
		//form.append(tabItem);
		form.addCommand(searchCmd);
        form.addCommand(downloadCmd);



                form.append(searchBox);
                form.append(resultBox);
                displayMe();

    }



	public void commandAction(Command cmd, Displayable disp) {
		super.commandAction(cmd, disp);
		if (cmd == searchCmd) {

                            int res;
                            resultBox.deleteAll();


                            this.name = searchBox.getString();
            try {
                search = new Search(name, this);

                for(int i=0;i<midlet.sched.maxConn;i++)
                {
                    testClass.w[i].searchObject = search;
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }


                            this.setTitle(name);
                            midlet.tabHelp.update();
                            for(int i = 0; i< midlet.index.index.size(); i++)
                            {
                                String item = midlet.index.index.elementAt(i).toString();
                                res = item.indexOf(name);

                                if(res >= 0)
                                {
                                    results.addElement(item);
                                    resultBox.append(item, null);
                                }

                            }
                    }
        if(cmd == downloadCmd)
        {

            
            //System.out.println("Selected index = " + resultBox.getSelectedIndex());
            ///ResultEntry chosenOne = (ResultEntry)results.elementAt(resultBox.getSelectedIndex());

            
            


            //    testClass.midlet.sched.addRequest(rp);
             //   System.out.println("RequestAdded for download");

               

                //w.Object = this;



               // testClass.w[wD].createObexConnection(chosenOne.source.url);

               String fileName;
                //String fileName = chosenOne.path.substring(chosenOne.path.lastIndexOf('/'));
                //String remoteFolder = chosenOne.path.substring(2, chosenOne.path.lastIndexOf('/'));


                //System.out.println("Attempt to download " + fileName + "From folder " + remoteFolder + "of buddy " + testClass.w[w].dataStream.name + " worker num: " + w);

//                Vector fileNames = new Vector();
  //              fileNames.addElement(fileName);



                ProgressScreen progressScreen = new ProgressScreen(midlet, "Downloading ");
                progressScreen.progressUpdate(2, 10, "User Connected");
                testClass.midlet.changeScreen(progressScreen);

              //EDIT AFTER IMPLEMENTING FILE TRANFER

        }


		}

public void update(ResultEntry results)  {

    //resultBox.deleteAll();
        this.results.addElement(results);

        resultBox.append(results.path.substring(results.path.lastIndexOf('/')), null);

}


    public String getName()
    {
        return this.name;
    }

    public void setWorker(int w)
    {
        this.w = w;
    }



}
