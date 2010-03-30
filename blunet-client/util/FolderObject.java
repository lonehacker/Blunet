package util;

import java.util.*;

public class FolderObject {
	protected String fullPath;
	protected boolean isWritable;
	protected Vector fileObjectList;
	
	public FolderObject(String path, Vector files) {
		this.fullPath = path;
		fileObjectList = files;
		isWritable = true;
	}

	public FolderObject(String fullPath) {
		this.fullPath = fullPath;
		fileObjectList = new Vector();
		isWritable = true;
	}

	public void addFileObject(FileObject fo) {
		fileObjectList.addElement(fo);
	}
	
	public FileObject removeFileObject(int pos) {
		if (pos < fileObjectList.size()) {
			FileObject fo = (FileObject) fileObjectList.elementAt(pos);
			fileObjectList.removeElementAt(pos);
			return fo;
		}
		return null;
	}
	
	public FileObject removeFileObject(FileObject fo) {
		if (fo == null)
			return null;
		for (int i = 0; i < fileObjectList.size(); i++) {
			FileObject fo1 = (FileObject) fileObjectList.elementAt(i);
			if (fo.equals(fo1)) {
				fileObjectList.removeElementAt(i);
				return fo1;
			}
		}
		return null;
	}
	
	public String getFullPath() {
		return fullPath;
	}

	public void setFullPath(String fullPath) {
		this.fullPath = fullPath;
	}

	public boolean isWritable() {
		return isWritable;
	}

	public void setWritable(boolean isWritable) {
		this.isWritable = isWritable;
	}

	public Vector getFileObjectList() {
		return fileObjectList;
	}

	public void setFileObjectList(Vector fileObjectList) {
		this.fileObjectList = fileObjectList;
	}

	
}
