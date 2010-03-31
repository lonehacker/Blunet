package util;

public class Kits {

	public static void sleep(int s) {
		try {
			Thread.sleep(s);
		}
		catch (InterruptedException e) {}
	}
	
	public static String fileType(String fileName) {
		String f = fileName.toLowerCase();
		//text
		if (f.endsWith(".ics")) return "text/calendar";
		if (f.endsWith(".xml")) return "text/xml";
		
		if (f.endsWith(".asf")) return "video/x-ms-asf"; 
		if (f.endsWith(".asr")) return "video/x-ms-asf"; 
		if (f.endsWith(".asx")) return "video/x-ms-asf";
		if (f.endsWith(".avi")) return "video/x-msvideo"; 
		if (f.endsWith(".bas")) return "text/plain";
		if (f.endsWith(".bin")) return "application/octet-stream"; 
		if (f.endsWith(".bmp")) return "image/bmp";
		if (f.endsWith(".doc")) return "application/msword"; 
		if (f.endsWith(".dvi")) return "application/x-dvi"; 
		if (f.endsWith(".exe")) return "application/octet-stream"; 
		if (f.endsWith(".gif")) return "image/gif";
		if (f.endsWith(".htm")) return "text/html";
		if (f.endsWith(".html")) return "text/html";
		if (f.endsWith(".jpe")) return "image/jpeg";
		if (f.endsWith(".png")) return "image/png";
		if (f.endsWith(".jpeg")) return "image/jpeg";
		if (f.endsWith(".jpg")) return "image/jpeg";
		if (f.endsWith(".mov")) return "video/quicktime"; 
		if (f.endsWith(".mp2")) return "video/mpeg"; 
		if (f.endsWith(".mp3")) return "audio/mpeg"; 
		if (f.endsWith(".mpeg")) return "video/mpeg"; 
		if (f.endsWith(".mpg")) return "video/mpeg"; 
		if (f.endsWith(".mpp")) return "application/vnd.ms-project"; 
		if (f.endsWith(".mpv2")) return "video/mpeg"; 
		if (f.endsWith(".pdf")) return "application/pdf"; 
		if (f.endsWith(".pps")) return "application/vnd.ms-powerpoint"; 
		if (f.endsWith(".ppt")) return "application/vnd.ms-powerpoint"; 
		if (f.endsWith(".ps")) return "application/postscript"; 
		if (f.endsWith(".ra")) return "audio/x-pn-realaudio"; 
		if (f.endsWith(".ram")) return "audio/x-pn-realaudio"; 
		if (f.endsWith(".swf")) return "application/x-shockwave-flash"; 
		if (f.endsWith(".txt")) return "text/plain"; 
		if (f.endsWith(".vcf")) return "text/x-vcard"; 
		if (f.endsWith(".wav")) return "audio/x-wav"; 
		if (f.endsWith(".zip")) return "application/zip"; 
		
		return "application/octet-stream";
	}
		
}
