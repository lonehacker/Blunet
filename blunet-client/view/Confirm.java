package view;

import javax.microedition.lcdui.*;

import blunet.blunet;

public class Confirm extends BaseScreen {
	
	Alert alert;
	private Command okCommand;
	private Command cancelCommand;
		
	public Confirm(blunet midlet,
			String text, String okText, String cancelText) {
		
		super(midlet);
		
		alert = new Alert("Confirm", text, null, AlertType.CONFIRMATION);	
		okCommand = new Command(okText, Command.OK, 1);
		cancelCommand = new Command(cancelText, Command.CANCEL, 1);
		alert.addCommand(okCommand);
		alert.addCommand(cancelCommand);
		alert.setTimeout(Alert.FOREVER);
		alert.setCommandListener(this);
		
		displayable = alert;
	}
	
	public void commandAction(Command cmd, Displayable d) {
		if (cmd == okCommand) {
			okAction();
		}
		if (cmd == cancelCommand) {
			cancelAction();
		}
	}

	public void okAction() {
		
	}

	public void cancelAction() {
		
	}
}
