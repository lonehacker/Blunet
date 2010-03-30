package view;


import javax.microedition.lcdui.*;
import javax.microedition.midlet.MIDletStateChangeException;
import core.*;
import util.*;
import blunet.blunet;


public class ExitAlert extends BaseScreen {
	
	private Command Cancel, Exit;
	private Alert alert;
	Displayable current;
	
	public ExitAlert(blunet midlet) {
		super(midlet);
		
		alert = new Alert("", "Exit?", null, AlertType.CONFIRMATION);
		Exit = new Command("Yes", Command.ITEM, 1);
		Cancel = new Command("No", Command.CANCEL, 1);
		
		alert.addCommand(Cancel);
		alert.addCommand(Exit);
		
		alert.setTimeout(Alert.FOREVER);
		alert.setCommandListener(this);
		displayable = alert;
		current = Display.getDisplay(midlet).getCurrent();
	}
	
	public void commandAction(Command cmd, Displayable d) {
		if (cmd == Exit) {
			try {
				// all clean up works go here
				alert.removeCommand(Exit);				
				alert.removeCommand(Cancel);
				alert.setString("Exiting ...");
				
				midlet.destroyApp(true);
                midlet.notifyDestroyed();
				
			} catch (Exception e) {
				System.out.println("Exit: " + e.toString());
				Display.getDisplay(midlet).setCurrent(current);
			}
			
		}
		if (cmd == Cancel) {
			
			Display.getDisplay(midlet).setCurrent(current);
		}
	}
}
