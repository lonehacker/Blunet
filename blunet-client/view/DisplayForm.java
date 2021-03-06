package view;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;

import local.Labels;
import blunet.blunet;
import local.Local;

import core.*;

import javax.microedition.lcdui.Display;

public class DisplayForm extends BaseScreen {

	int width;
	int height;
	Form form;
        String name;
        int index;

        Displayable previous;
        Display display;

	Command exitCmd = new Command(Local.get(Labels.EXIT), Command.EXIT, 1);
	Command backCmd = new Command("Back", Command.EXIT, 2);

        boolean hasTab = true;
        
        public DisplayForm(blunet midlet, boolean hasTab, String name) {
		super(midlet);
		form = new Form("");
		if (hasTab)
			form.addCommand(exitCmd);
                else
                    form.addCommand(backCmd);
		form.setCommandListener(this);
		displayable = form;
		width = form.getWidth();
		height = form.getHeight();
                this.name = name;
                this.display = midlet.getDisplay();


	}

        public void displayMe()
        {
            previous = midlet.getDisplay().getCurrent();
            display.setCurrent(displayable);
        }

	public DisplayForm(blunet midlet, boolean hasTab) {
		super(midlet);
		form = new Form("");
		if (hasTab)
			form.addCommand(exitCmd);
		form.setCommandListener(this);
		displayable = form;
		width = form.getWidth();
		height = form.getHeight();
	}
	
	public DisplayForm(blunet midlet) {
		super(midlet);
		form = new Form("");
		form.addCommand(exitCmd);
		form.setCommandListener(this);
		displayable = form;
		width = form.getWidth();
		height = form.getHeight();
	}
	
	public void commandAction(Command cmd, Displayable disp) {
		if (cmd == exitCmd) {
			//midlet.getUIFsm().outEvent(UIEvent.EXIT);
		}
                if(cmd == backCmd)
                {
                    midlet.getDisplay().setCurrent(previous);

                }
	}

	//leave this form
	public void leave(String event) {
		//midlet.getUIFsm().outEvent(event);
	}
	
	public void setTitle(String title) {
		form.setTitle(title);
	}

        public String getName()
        {
            return name;
        }

        public void setIndex(int index)
        {
            this.index = index;
        }
}
