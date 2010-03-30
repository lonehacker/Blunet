package view;

import java.util.Vector;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Displayable;

import local.Labels;
import local.Local;

import blunet.blunet;
import core.*;



public class TabFormFile extends TabForm {
	
	TabItemFile tabItem;

	
	public TabFormFile(blunet midlet, String tabName) {
		super(midlet);
		tabItem = new TabItemFile(width, height, tabName, this);
		form.setTitle(Local.get(Labels.FILE));
		form.append(tabItem);
	}
	
	public void commandAction(Command cmd, Displayable disp) {
		super.commandAction(cmd, disp);
	
	}

	public void update(Vector fileObjects) {
		tabItem.update(fileObjects);
	}
	
}
