package util;

import java.util.Vector;

import util.StringHelper;

public class RemoteData {

	private static RemoteData instance;
	
	private RemoteData() {
		
	}
	
	public static RemoteData getInstance() {
		if (instance == null)
			instance = new RemoteData();
		return instance;
	}
	
	public FileObject[] folderParser(String parent, byte[] result) {
		if (result == null || result.length == 0)
			return new FileObject[0];

		Vector v = getFileList(result);
		if (!parent.endsWith("/")) {
			FileObject fo = new FileObject();
			fo.setFolder(true);
			fo.setName("..");
			v.insertElementAt(fo, 0);
		}
		FileObject[] fos = new FileObject[v.size()];
		v.copyInto(fos);
		return fos;
	}

	public Vector getFileList(byte[] result) {
		Vector v = new Vector();
		if (result == null || result.length == 0)
			return v;
		
		String line = new String(result);
		String[] lines = StringHelper.split(line, '<');
		for (int i = 0; i < lines.length; i++) {
			if (lines[i].startsWith("folder ") || lines[i].startsWith("file ")) {
				FileObject fo = new FileObject();
				if (lines[i].startsWith("file "))
					fo.setFolder(false);
				else
					fo.setFolder(true);
				// extract name and size attributes
				String name = getAttribute(lines[i], "name=\"");
				if (name != null)
					fo.setName(name);
				String size = getAttribute(lines[i], "size=\"");
				if (size != null && size.length() > 0)
					fo.setSize(Integer.parseInt(size));
				v.addElement(fo);
			}
		}
		return v;
	}

	String getAttribute(String src, String attr) {
		int r = src.indexOf(attr);
		if (r > 0) {
			int t = r+attr.length();
			int s = src.indexOf("\"", t+1);
			if (s >= t)
				return src.substring(t, s);
		}
		return null;
	}
	
}
