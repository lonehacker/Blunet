package util;

public class FileObject {
	public final static String FILE_SEPARATOR =
		(System.getProperty("file.separator")!=null)? System.getProperty("file.separator"):"/";

	String name;
        String path;
	boolean isFolder;
	int size;
	boolean isChecked;
	boolean isOverwritable;
	
	public FileObject(){
	}

	public FileObject(String name){
		this.name = name;
		if (name.endsWith(FILE_SEPARATOR))
			setFolder(true);
	}

        public FileObject(String name, String path){
		this.name = name;
		if (name.endsWith(FILE_SEPARATOR))
			setFolder(true);

                this.path = path;
	}
	
	public boolean equals(FileObject fn) {
		if (fn == null)
			return false;
		if (fn.getName().equals(name))
			return true;
		return false;
	}
	public String getName() {
		return name;
	}

        public String getPath()
        {
            return path;
        }

	public void setName(String name) {
		this.name = name;
	}

	public boolean isFolder() {
		return isFolder;
	}

	public void setFolder(boolean isFolder) {
		this.isFolder = isFolder;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public boolean isChecked() {
		return isChecked;
	}

	public void setChecked(boolean isChecked) {
		this.isChecked = isChecked;
	}

	public boolean isOverwritable() {
		return isOverwritable;
	}

	public void setOverwritable(boolean isOverwritable) {
		this.isOverwritable = isOverwritable;
	}

}
