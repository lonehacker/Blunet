package view;

import core.BTDesc;
import javax.microedition.lcdui.*;

import local.Labels;
import local.Local;
import model.*;

import blunet.blunet;

public class TabFormNeighbor extends TabForm {

	TabItemNeighbor tabItem;

    //Command downloadCmd = new Command(Local.get(Labels.BROWSE), Command.SCREEN, 1);
	//Command refreshCmd = new Command(Local.get(Labels.DISCOVER), Command.SCREEN, 2);
	//Command deleteCmd = new Command(Local.get(Labels.DELETE), Command.SCREEN, 4);
	Command chatCmd = new Command(Local.get(Labels.CHAT), Command.SCREEN, 5);
    Command searchCmd = new Command("Search", Command.SCREEN, 3);
    blunet midlet;


	public TabFormNeighbor(blunet midlet, String tabName) {
		super(midlet);
        this.midlet = midlet;
		tabItem = new TabItemNeighbor(width, height, tabName, this);
		form.setTitle(Local.get(Labels.BLUETOOTH));
		form.append(tabItem);
		form.addCommand(chatCmd);
        form.addCommand(searchCmd);
	}
	
	public void commandAction(Command cmd, Displayable disp) {
		super.commandAction(cmd, disp);
		if(cmd == chatCmd) {
                    BTDesc btDesc = tabItem.getSelectedItem();
                    if (btDesc == null) {
				BWAlert.infoAlert(Local.get(Labels.PLEASE_SELECT_A) + " " + Local.get(Labels.BLUETOOTH));
			}
                    else {

                        //String name = btDesc.getBtName();
                        //String name = "Ashish";

                        FormChat tabChat = new FormChat(midlet, Local.get(Labels.CHAT), btDesc, btDesc.name);
                        midlet.taskList.addElement(tabChat);
                        tabChat.setIndex(midlet.taskList.indexOf(tabChat));
                        
                        midlet.tabHelp.update();

                    }

                }
                else if(cmd == searchCmd)
                {
                    String name = "Search";
                    //FormSearch tabSearch = new FormSearch(midlet, "Search", name);
                    
                }
	}

	public void update() {
		tabItem.update(midlet.userList);
        repaint();

    }
	
	public void repaint() {
		tabItem.refresh();
	}
}
