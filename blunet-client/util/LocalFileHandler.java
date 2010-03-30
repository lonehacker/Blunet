package util;

import java.io.*;
import java.util.*;

import javax.microedition.io.*;
import javax.microedition.io.file.*;

import local.Labels;
import local.Local;

import util.Kits;
import view.BWAlert;

public class LocalFileHandler {
	
	private static FolderObject currentFolder;
	private static int currentFocus;

	/**
	 * Create a folder.
	 * @param path
	 * @param name
	 * @return FileObject
	 */
		
	public static FileObject createFolder(String path, String name) {
		FileConnection fc = null;
		try
		{
			fc = (FileConnection) Connector.open("file:///" + path + name, Connector.READ_WRITE);
			if (!fc.exists()) {
				fc.mkdir();
				fc.setHidden(false);
				fc.setReadable(true);
				fc.setWritable(true);
			}
			fc.close();
			if (!name.endsWith(FileObject.FILE_SEPARATOR))
				name += FileObject.FILE_SEPARATOR;
			return new FileObject(name);
		}
		catch (SecurityException e)
		{
			BWAlert.errorAlert("Security Exception: " + e.getMessage());
		}
		catch (IOException e)
		{
			BWAlert.errorAlert("IO Exception: " + e.getMessage());
		}
		return null;
	}
	
	public static FileObject createFolder(String name) {
		if (currentFolder != null)
			return createFolder(currentFolder.getFullPath(), name);
		return null;
	}
	
	/**
	 * Load all file names (include folder names) from given path.
	 * @param path
	 * @return a FolderObject
	 */
	public static FolderObject loadFolder(String path) 
	{
		if (path == null || path.equals("/"))
			path = "";	// root
		
		FolderObject folder = new FolderObject(path);
		
		if (path.equals("")) {
			// root
			folder.setWritable(false);
			Enumeration roots = FileSystemRegistry.listRoots();
			while (roots.hasMoreElements())
			{
				String name = (String) roots.nextElement();
				folder.addFileObject(new FileObject(name, folder.getFullPath()));
			}
		}
		else {
			try
			{
				FileConnection fc = (FileConnection) Connector.open("file:///" + path, Connector.READ);
				if (!fc.exists()) {
					fc.close();
					BWAlert.errorAlert(Local.get(Labels.FOLDER) + " " + path + " " + Local.get(Labels.NOT_EXIST) + "!");
					// load root
					return loadFolder(null);
				}

				folder.setWritable(fc.canWrite());
				
				// add parent folder
				FileObject fo = new FileObject("..");
				fo.setFolder(true);
				folder.addFileObject(fo);

				Enumeration listOfFiles = fc.list();
				while (listOfFiles.hasMoreElements()) {
					String name = (String) listOfFiles.nextElement();
					folder.addFileObject(new FileObject(name, folder.getFullPath()));
				}
				fc.close();
			}
			catch (SecurityException e)
			{
				BWAlert.errorAlert("SecurityException: loadFolder() - " + path + ", " + e.getMessage());
			}
			catch (IOException e)
			{
				BWAlert.errorAlert("IOException: loadFolder() - folder not found: " + path + ", " + e.getMessage());
				// load root
				return loadFolder(null);
			}
		}
		// update current path in DB
		
		currentFolder = folder;
		return folder;
	}

	/**
	 * read content of a given file.
	 * @param path
	 * @param name
	 * @return file content in an array of bytes
	 */
	public static byte[] readFile(String path, String name) 
	{
		byte[] buf = null;
		try
		{
			FileConnection fc = (FileConnection) Connector.open(
						"file:///" + path + name, Connector.READ);
        	InputStream is  = fc.openInputStream();
        	buf = new byte[(int)fc.fileSize()];
        	is.read(buf);
        	is.close();
        	fc.close();
		}
		catch (SecurityException e)
		{
			BWAlert.errorAlert("readFile(): " + e.getMessage());
		}
		catch (IOException e)
		{
			BWAlert.errorAlert("readFile(): " + e.getMessage());
		}
		return buf;
	}

	/**
	 * remove a file from given folder
	 * @param path
	 * @param name
	 * @return true/false
	 */
	public static boolean removeFile(String path, String name) {
		try
		{
			FileConnection fc = (FileConnection) Connector.open(
						"file:///" + path + name);
			if (fc.exists()) {
				String childPath = path + name;
				if (fc.isDirectory()) {
					Enumeration listOfFiles = fc.list();
					while (listOfFiles.hasMoreElements()) {
						String child = (String) listOfFiles.nextElement();
						removeFile(childPath, child);
					}
				}
				fc.delete();
			}
			fc.close();
			return true;
		}
		catch (SecurityException e)
		{
			BWAlert.errorAlert("remove(): " + e.getMessage());
			Kits.sleep(3000);
		}
		catch (IOException e)
		{
			BWAlert.errorAlert("remove(): " + e.getMessage());
			Kits.sleep(3000);
		}
		return false;
	}

	public static boolean removeFile(String name) {
		String path = currentFolder.getFullPath();
		return removeFile(path, name);
	}

	public static FolderObject cd(String path) {
		if (path != null && path.equals("..")) {
			path = currentFolder.getFullPath();
			int r = path.lastIndexOf('/', path.length()-2);
			if (r > 0) { 
				path = path.substring(0, r+1); // to include '/'
			}
			else
				path = "";
		}
		return loadFolder(path);
	}

	/**
	 * help to remember the focus in last browse
	 * @return
	 */
	public static int getCurrentFocus() {
		return currentFocus;
	}

	public static void setCurrentFocus(int focus) {
		currentFocus = focus;
		
	}

	/**
	 * cache to the last folder loading
	 * @return
	 */
	public static FolderObject getCurrentFolder() {
		return currentFolder;
	}

	public static void setCurrentFolder(FolderObject currentFolder) {
		LocalFileHandler.currentFolder = currentFolder;
	}

}