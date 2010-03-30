package view;

import javax.microedition.lcdui.*;

import core.*;
import blunet.blunet;


public class BWAlert {
	
	Displayable next;
	static Display display = Display.getDisplay(blunet.getInstance());
	
	static public void general(String title, AlertType alertType, String text, Displayable next, int timeout) {
		Alert alert = new Alert(title);
		alert.setString(text);
		alert.setType(alertType);
		alert.setTimeout(timeout);
		if (next != null)
			display.setCurrent(alert, next);
		else
			display.setCurrent(alert);
	}
	
	static public void errorAlert(String text) {
		general("Error", AlertType.ERROR, text, null, Alert.FOREVER);
	}
	
	static public void errorAlert(String text, Displayable next) {
		general("Error", AlertType.ERROR, text, next, Alert.FOREVER);
	}
	
	static public void infoAlert(String text) {
		general("Info", AlertType.INFO, text, null, Alert.FOREVER);
	}
	
	static public void infoAlert(String text, int timeout) {
		
		general("Info", AlertType.INFO, text, null, timeout);
	}
	
	static public void infoAlert(String text, Displayable next) {
		general("Info", AlertType.INFO, text, next, Alert.FOREVER);
	}
	
}
