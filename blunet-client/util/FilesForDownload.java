package util;

public class FilesForDownload {
	
	protected boolean isChild;
	protected FolderObject remoteFolder;
	protected FolderObject localFolder;
	
	public FilesForDownload() {
	}

	public FolderObject getRemoteFolder() {
		return remoteFolder;
	}

	public void setRemoteFolder(FolderObject remoteFolder) {
		this.remoteFolder = remoteFolder;
	}

	public FolderObject getLocalFolder() {
		return localFolder;
	}

	public void setLocalFolder(FolderObject localFolder) {
		this.localFolder = localFolder;
	}

	public boolean isChild() {
		return isChild;
	}

	public void setChild(boolean isChild) {
		this.isChild = isChild;
	}
	
}
