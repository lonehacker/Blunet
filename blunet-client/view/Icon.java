package view;
import java.io.*;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

public class Icon {

//  ###########################################################
// 	Icons
//	###########################################################
	
	
	//public static Image RIGHT_ARROW;	
	//public static Image HIGHLIGHT;
	public static Image CURSOR;
	public static Image CROSS;

	public static Image FILE;
	public static Image FOLDER;
	public static Image BLUETOOTH;
	public static Image GREY_BLUETOOTH;
	public static Image DB;
	public static Image UP;
	public static Image LOGO;
	
	public static int TRANSPARENET_RGB_PIXEL = 0x00000000;
	public static int SEMI_TRANSPARENET_RGB_PIXEL = 0x88FFFFFF;
	public static int HIGHLIGHT_RGB_PIXEL = 0x44FFFFFF;
	
	public static Image BLANKIMAGE;
	public static int ICON_WIDTH = 16;
	public static int ICON_HEIGHT = 16;
	
	static {
		int [] blankImageRGB = new int[ICON_WIDTH * ICON_HEIGHT];
		int bits = ICON_WIDTH * ICON_HEIGHT;
		for (int i = 0; i < bits; i++)
		{
//			blankImageRGB[i] = TRANSPARENET_RGB_PIXEL;
			blankImageRGB[i] = SEMI_TRANSPARENET_RGB_PIXEL;
		}
		BLANKIMAGE = Image.createRGBImage(blankImageRGB, ICON_WIDTH, ICON_HEIGHT, true);
		FILE = loadIcon("/file.png");
		FOLDER = loadIcon("/folder.png");
		BLUETOOTH = loadIcon("/bluetooth.png");
		GREY_BLUETOOTH = loadIcon("/grey_bluetooth.png");
		DB = loadIcon("/db.png");
		UP = loadIcon("/up.png");
		LOGO = halfSize(loadIcon("/b_folder.png"));
	}
	
	public static Image loadIcon(String imageFile){
		try {
			return Image.createImage(imageFile);
		}
		catch(IOException exception){
			return BLANKIMAGE;
		}
	}
	
	public static Image doubleSize(Image smallImage){
		int w = smallImage.getWidth();
		int h = smallImage.getHeight();
		return resizeImage(smallImage, w*2, h * 2);
	}
		
	public static Image halfSize(Image smallImage){
		int w = smallImage.getWidth();
		int h = smallImage.getHeight();
		return resizeImage(smallImage, w/2, h/2);
	}

	/**
	  * This methog resizes an image by resampling its pixels
	  * @param src The image to be resized
	  * @return The resized image
	  */

	public static Image resizeImage(Image src, int screenWidth, int screenHeight) {

		  int srcWidth = src.getWidth();
	      int srcHeight = src.getHeight();

	      if (screenHeight == -1)
	    	  screenHeight = screenWidth * srcHeight / srcWidth;
	      if (screenWidth == -1)
	    	  screenWidth = screenHeight * srcWidth / srcHeight;
	      
	      double whratio = (double) srcWidth / (double) srcHeight;
	      if (whratio > 1.0) { // reduce height
	    	  screenHeight = (int)((double)screenWidth / whratio);
	      }
	      else {
	    	  screenWidth = (int)((double)screenHeight *  whratio);
	      }

	      Image tmp = Image.createImage(screenWidth, srcHeight);
	      Graphics g = tmp.getGraphics();
	      int ratio = (srcWidth << 16) / screenWidth;
	      int pos = ratio/2;

	      //Horizontal Resize        

	      for (int x = 0; x < screenWidth; x++) {
	          g.setClip(x, 0, 1, srcHeight);
	          g.drawImage(src, x - (pos >> 16), 0, Graphics.LEFT | Graphics.TOP);
	          pos += ratio;
	      }

	      Image resizedImage = Image.createImage(screenWidth, screenHeight);
	      g = resizedImage.getGraphics();
	      ratio = (srcHeight << 16) / screenHeight;
	      pos = ratio/2;        

	      //Vertical resize

	      for (int y = 0; y < screenHeight; y++) {
	          g.setClip(0, y, screenWidth, 1);
	          g.drawImage(tmp, 0, y - (pos >> 16), Graphics.LEFT | Graphics.TOP);
	          pos += ratio;
	      }
	      return resizedImage;
	  }//resize image  
	
}



