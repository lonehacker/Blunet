package view;

import java.util.Vector;

import javax.microedition.io.ConnectionNotFoundException;
import javax.microedition.lcdui.Graphics;

import core.*;
import util.*;
import blunet.blunet;

public class TabItemFile extends TabItem {
	Vector fileObjects;
	
	
	public TabItemFile(int width, int height, String tabName, TabForm tabForm) {
		super(width, height, tabName, tabForm);
	}

	protected void pre_paint(Graphics g, int w, int h) {
		if (focusIndex == -1) {
			focusIndex = LocalFileHandler.getCurrentFocus();
			int num = rowItems.size();
			if (focusIndex >= num)
				focusIndex = -1;
			if (focusIndex > 3)
				firstVisibleLine = focusIndex - 3;
		}
	}
	
	protected void post_paint(Graphics g, int w, int h) {
		LocalFileHandler.setCurrentFocus(focusIndex);
	}
	
	public void fire() {
		// lunch detail screen
		
	}

	public void check() {
		if (focusIndex < 0)
			return;
		FileObject fo = (FileObject) fileObjects.elementAt(focusIndex);
		if (!fo.isFolder()) {
			RowItem rowItem = (RowItem) rowItems.elementAt(focusIndex);
			if (fo.isChecked()) {
				fo.setChecked(false);
				rowItem.setChecked(false);
			}
			else {
				fo.setChecked(true);
				rowItem.setChecked(true);
			}
			repaint();
		}
	}
	
	public void update(Vector fileObjects) {
		this.fileObjects = fileObjects;
		rowItems.removeAllElements();
		
		for (int i = 0; i < fileObjects.size(); i++) {
			
			RowItem rowItem = new RowItem(1);
			
			FileObject fo = (FileObject) fileObjects.elementAt(i);
			String name = fo.getName();
			rowItem.setItems(0, name, blunet.plainFont.stringWidth(name));
			
			if (fo.isFolder()) {
				if (fo.getName().equals(".."))
					rowItem.setIcon(Icon.UP);
				else
					rowItem.setIcon(Icon.FOLDER);
			}
			else
				rowItem.setIcon(Icon.FILE);
			
			if (!fo.isFolder() && fo.isChecked()) {
				rowItem.setChecked(true);
			}
			
			rowItems.addElement(rowItem);
		}
	}

	public Vector getFileObjects() {
		return fileObjects;
	}

	public FileObject getSelectedFileName() {
		if (focusIndex < 0)
			return null;
		FileObject fo = (FileObject) fileObjects.elementAt(focusIndex);
		return fo;
	}
	
}