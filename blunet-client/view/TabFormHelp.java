package view;

import javax.microedition.lcdui.*;

import blunet.blunet;
import local.*;

public class TabFormHelp extends TabForm {

	TabItemTask tabItem;
	    Command startSearch = new Command("Search", Command.SCREEN, 1);
        Command switchCmd = new Command("Switch To", Command.SCREEN, 2);
        Command killTask = new Command("Close", Command.SCREEN, 3);
        TextField field;

	public TabFormHelp(blunet midlet, String tabName) {
		super(midlet);
		tabItem = new TabItemTask(width, height, tabName, this);
		form.setTitle(Local.get(Labels.HELP));
		form.append(tabItem);
	            form.addCommand(startSearch);
                form.addCommand(switchCmd);
                form.addCommand(killTask);
                
                //field = new TextField("Search", "", 50, TextField.ANY);
                //form.append(field);
                
        }
	
	public void commandAction(Command cmd, Displayable disp) {
		super.commandAction(cmd, disp);
	            if(cmd == switchCmd)
                {
                    DisplayForm df = tabItem.getSelectedTaskName();
                    df.displayMe();
                }
                else if(cmd == killTask)
                {
                    DisplayForm df = tabItem.getSelectedTaskName();
                    midlet.taskList.removeElement(df);
                    this.update();
                    df = null;
                }
	}

	public void itemStateChanged(Item item) {
		
	}
	
	public void update() {
		tabItem.update(midlet.taskList);
	}
}
