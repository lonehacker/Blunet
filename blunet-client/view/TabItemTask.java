package view;

import java.util.Vector;

import javax.microedition.lcdui.Graphics;

import core.*;
import util.*;
import blunet.blunet;

public class TabItemTask extends TabItem {
	Vector taskObjects;

	
	public TabItemTask(int width, int height, String tabName, TabForm tabForm) {
		super(width, height, tabName, tabForm);
	}

	protected void pre_paint(Graphics g, int w, int h) {

           // focusIndex = 0;
            /*if (focusIndex == -1) {
			focusIndex = model.LocalFileHandler.getCurrentFocus();
			int num = rowItems.size();
			if (focusIndex >= num)
				focusIndex = -1;
			if (focusIndex > 3)
				firstVisibleLine = focusIndex - 3;
		}*/
	}
	
	protected void post_paint(Graphics g, int w, int h) {

            //model.LocalFileHandler.setCurrentFocus(focusIndex);
	}
	
	public void fire() {

	}

	public void check() {

	}
	
	public void update(Vector taskObjects) {
		this.taskObjects = taskObjects;
		rowItems.removeAllElements();
		
		for (int i = 0; i < taskObjects.size(); i++) {
			
			RowItem rowItem = new RowItem(1);
			
			DisplayForm fo = (DisplayForm) taskObjects.elementAt(i);
			String name = fo.getName();
			rowItem.setItems(0, name, blunet.plainFont.stringWidth(name));
			
			
			rowItem.setIcon(Icon.FILE);
			
						
			rowItems.addElement(rowItem);
		}
	}

	public Vector getTaskObjects() {
		return taskObjects;
	}

	public DisplayForm getSelectedTaskName() {
		if (focusIndex < 0)
			return null;
		DisplayForm fo = (DisplayForm) taskObjects.elementAt(focusIndex);
		return fo;
	}
	
}