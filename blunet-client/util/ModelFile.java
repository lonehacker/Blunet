package util;

import java.util.*;



import blunet.blunet;
import view.*;

/**
 * 
 * @author wjt
 *
 */
public class ModelFile {
	
	Vector fileObjects; // FileOjects
	TabFormFile tabFile;
	blunet midlet;
	
	public ModelFile(blunet midlet, TabFormFile tabFile) {
		this.midlet = midlet;
		this.tabFile = tabFile;
		String focus = "0";
		if (focus == null || focus.length() ==0)
			LocalFileHandler.setCurrentFocus(-1);
		else {
			int focus_index = Integer.parseInt(focus);
			LocalFileHandler.setCurrentFocus(focus_index);
		}
		String path = "/";
		loadFolder(path);
	}

	public void loadParentFolder() {
		fileObjects = LocalFileHandler.cd("..").getFileObjectList();
	}
	
	public void loadFolder(String path) {
		fileObjects = LocalFileHandler.cd(path).getFileObjectList();
	}
	
	public void clearChecked() {
		for (int i = 0; i < fileObjects.size(); i++) {
			FileObject fo = (FileObject)fileObjects.elementAt(i);
			fo.setChecked(false);
		}
	}
	
	public FileObject find(FileObject fn) {
		for (int i = 0; i < fileObjects.size(); i++) {
			FileObject fo = (FileObject)fileObjects.elementAt(i);
			if (fo.equals(fn))
				return fo;
		}
		return null;
	}
	
	public void refresh() {
		if (!tabFile.isCurrentDisplayable())
			return;
		loadFolder(LocalFileHandler.getCurrentFolder().getFullPath());
		fileObjects = LocalFileHandler.getCurrentFolder().getFileObjectList();
		onEnter();
	}
	
	public void fileObjectUpdate(Vector fileObjects) {
		this.fileObjects = fileObjects;
	}

	public void removeCheckedFile() {
		int i = 0;
		while (i < fileObjects.size()) {
			FileObject fo = (FileObject)fileObjects.elementAt(i);
			if (fo.isChecked()) {
				if (LocalFileHandler.removeFile(fo.getName()))
					fileObjects.removeElementAt(i);
				else
					i++;
			}
			else
				i++;
		}
	}
	
	public void onEnter() {
		tabFile.setTitle(LocalFileHandler.getCurrentFolder().getFullPath());
		tabFile.update(fileObjects);
		midlet.changeScreen(tabFile);
	}
	
	public void onUpdate() {
		tabFile.update(fileObjects);
	}

	public Vector getFileObjects() {
		return fileObjects;
	}
	
}
