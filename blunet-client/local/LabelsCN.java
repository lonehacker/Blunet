package local;

import java.util.*;

public class LabelsCN extends Labels{
	
	static Hashtable map = new Hashtable();
	static {
		map.put(EXIT, "\u9000\u51FA");
		map.put(NEIGHBOR, "\u8FD1\u90BB");
		map.put(FILE, "\u6587\u4EF6");
		map.put(BACK, "\u8FD4\u56DE");
		map.put(HELP, "\u5E2E\u52A9");
		map.put(HISTORY, "\u5386\u53F2");
		map.put(SETTING, "\u8BBE\u7F6E");
		map.put(SAVE, "\u4FDD\u5B58");
		map.put(CANCEL, "\u53D6\u6D88");
		map.put(DELETE, "\u5220\u9664");
		map.put(DISCOVER, "\u53D1\u73B0");
		map.put(BROWSE, "\u6D4F\u89C8");
		map.put(SAVE_TO_DB, "\u5B58\u5165\u6570\u636E\u5E93");
		map.put(SELECT_BT_TO_DOWNLOAD_FROM, "\u9009\u62E9\u4E0B\u8F7D\u7684\u84DD\u7259");
		map.put(OPEN, "\u6253\u5F00");
		map.put(MOVE, "\u8F6C\u79FB\u6587\u4EF6");
		map.put(DOWNLOAD, "\u4E0B\u8F7D");
		map.put(UPLOAD, "\u4E0A\u4F20");
		map.put(SEND, "\u4F20\u9001");
		map.put(CREATE, "Create");
		
		map.put(BT_INQUIRY_TIMEOUT, "\u84DD\u7259\u641C\u5BFB\u8D85\u65F6\u65F6\u95F4(\u79D2)\uFF1A");
		map.put(FILE_PATH, "\u6587\u4EF6\u5B58\u653E\u8DEF\u5F84\uFF1A:");
		map.put(LANGUAGE, "\u8BED\u97F3\u9009\u62E9\uFF1A");
		map.put(FOLDER, "\u6587\u4EF6\u5939");
		
		map.put(SEARCH, "\u67E5\u627E");
		map.put(ENGLISH, "\u82F1\u6587");
		map.put(CHINESE, "\u4E2D\u6587");
		map.put(UNKNOWN, "\u65E0\u540D");
		map.put(PUSH, "\u4F20\u9001");
		map.put(REFRESH, "\u66F4\u65B0");
		map.put(ALL, "\u5168\u90E8");
		map.put(NEARBY, "\u8FD1\u90BB");
		map.put(VIEW, "\u67E5\u770B");
		map.put(EDIT, "\u4FEE\u6539");
		map.put(SELECT, "\u9009\u62E9");
		map.put(BLUETOOTH, "\u84DD\u7259");
		map.put(YES, "\u662F");
		map.put(NO, "\u4E0D\u662F");
	}
	
	public static String get(String key) {
		String value = (String) map.get(key);
		if (value == null)
			value = "Unknown";
		return value;
	}
}
