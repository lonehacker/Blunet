package local;

import java.util.*;

public class LabelsEN extends Labels{
	
	final static String help_text ="With bExplore, you can browse files on a local mobile phone, " +
	"browse files on a nearby Bluetooth (BT) device, and exchange files " +
	"between a local mobile phone and a remote BT device.\n" +
	"bExplore's GUI has three tab screens: Neighbor, File, and Help. This is the Help screen.\n" +
	"Below is a guide for main operations:\n" +
	"Discover Nearby BTs - Use Discover command on Neighbor screen.\n" +
	"Browse Files on a remote BT - on Neighbor screen, select a BT device then run Browse command in Options menu.\n" +
	"Download Files from a remote BT - on Neighbor screen, select a BT device, run Browse command, " +
	"select remote files, select a local folder or create a new folder for saving remote files, then run Save command. " +
	"There are two ways to select multiple files: press and hold the fire key for more than 2 seconds " +
	"or press up/down navigation key while holding on star key or pen key.\n" +
	"Browse Files on a Local Phone - on File screen, select a file and press fire key " +
	"to open this file or select a folder to explore this subfolder.\n" +
	"Upload Files to a remote BT � on File screen, select files, run Push command, select a desired BT, " +
	"run Push command, select a folder on remote device, and run Save command";


	static Hashtable map = new Hashtable();
	static {
		map.put(ABOUT, "About");
		map.put(EXIT, "Exit");
		map.put(NEIGHBOR, "Neighbor");
		map.put(FILE, "File");
		map.put(HELP, "Help");
		map.put(SETTING, "Setting");
		map.put(HISTORY, "History");
		map.put(BACK, "Back");
		map.put(SAVE, "Save");
		map.put(CANCEL, "Cancel");
		map.put(DELETE, "Delete");
		map.put(BROWSE, "Browse");
		map.put(DISCOVER, "Discover");
		map.put(SAVE_TO_DB, "Save to DB");
		map.put(SELECT_BT_TO_DOWNLOAD_FROM, "Select Bluetooth for Download");
		map.put(OPEN, "Open");
		map.put(MOVE, "Move File");
		map.put(DOWNLOAD, "Download");
		map.put(UPLOAD, "Upload");
		map.put(SEND, "Send");
		map.put(CREATE, "Create ");
        map.put(CHAT, "Chat");
        map.put(SEND_MESSAGE, "Send Message");
		
		map.put(BT_INQUIRY_TIMEOUT, "Device Inquiry Timeout (sec.):");
		map.put(FILE_PATH, "File folder:");
		map.put(LANGUAGE, "Language");
		map.put(FOLDER, "Folder");
		
		map.put(SEARCH, "Find");
		map.put(ENGLISH, "English");
		map.put(CHINESE, "Chinese");
		map.put(UNKNOWN, "Unknown");
		map.put(PUSH, "Push");
		map.put(REFRESH, "Refresh");
		map.put(ALL, "All");
		map.put(NEARBY, "Nearby");
		map.put(VIEW, "View");
		map.put(EDIT, "Edit");
		map.put(BLUETOOTH, "Bluetooth");
		map.put(SELECT, "Select");
		map.put(YES, "Yes");
		map.put(NO, "No");

		map.put(SUCCEED, "Succeed");
		map.put(REMOTE, "Remote");
		map.put(FROM, "from");
		map.put(IN, "in");
		map.put(TO, "to");
		map.put(EXIST, "exist");
		map.put(NOT_EXIST, "not exist");
		map.put(FOUND, "found");
		map.put(NOT_FOUND, "not found");
		map.put(WAIT_FOR, "Wait for");
		map.put(AUTHORIZATION, "Authoization");
		map.put(CONNECTING, "Connecting");
		map.put(CONNECTION, "Connection");
		map.put(ACCEPTED, "Accepted");
		map.put(CANCELLED, "Cancelled");
		map.put(REJECTED, "Rejected");
		map.put(FAILED, "Failed");
		map.put(OUT_OF_RANGE, "Out of Range");
		map.put(PLEASE_SELECT_A, "Please Select A");
		map.put(DELETE_CONFIRM, "Do you want to delete");
		map.put(SEARCH_FOR_BT_DEVICE, "Searching for Bluetooth Devices ...");
		map.put(BROWSE_FOLDER, "Browsing folder ...");
		map.put(FILES, "File(s):");
		map.put(DOWNLOADING, "Downloading");
		map.put(PUSHING, "Pushing");
		map.put(SELECT_A_BT, "Select a Bluetooth Device");
		map.put(DEVICE_NOT_CONNECTED, "Remote device is not connected!");
		map.put(RETRIVING_FOLDER_FROM, "Retrieving folder from ");
		map.put(CREATE_FOLDER_FAILED, "Create folder failed, folder already exists!");
		map.put(CREATE_FILE_FAILED, "Create file failed, file already exists");
		map.put(OVERWRITE_LOCAL_FILE_CONFIRM, "Do you want to overwrite local file");
		
		map.put(HELP_TEXT, help_text);
	}
	
	public static String get(String key) {
		String value = (String) map.get(key);
		if (value == null)
			value = "Unknown";
		return value;
	}
}
