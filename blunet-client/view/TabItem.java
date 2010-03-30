package view;

import java.util.*;

import javax.microedition.lcdui.*;

import core.*;
import blunet.blunet;


public class TabItem extends CustomItem implements ItemCommandListener {

	private final static int MAX_TAB_SIZE = 10;
	private static int tabSize = 0;
	private static String[] tabNames = new String[MAX_TAB_SIZE];
	private static Hashtable tabFormMap = new Hashtable();
	private static int[] tabXCoord = new int[MAX_TAB_SIZE];
	private static int selectedTab = -1;
	private int idleTicks = 0;
	private KeyRepeatTask repeater;
	private Timer theTimer;
	protected Vector rowItems = new Vector();

	static TabForm selectedTabForm;
	private static final int KEY_REPEAT_DELAY = 150;

        Graphics g;
        int w;
        int h;

	int myWidth; // width of the CustomItem
	int myHeight; // height of the CustomItem
	int tabHeight;
	
	long keyPressedTime;
	long keyReleasedTime;
	final static long KEY_HOLD_TIME = 500L; // .5 second 
	final static int NOKIA_KEY_PEN = -50;

	int focusIndex = -1; // index of the currently focused line
	int numItem;
	boolean hasFocus = false;
	int firstVisibleLine = 0;
	int lastVisibleLine = 0;
	int maxVisibleLines = 0;
	boolean paintTab = true;
	protected int rowHeight = blunet.plainFont.getHeight();
	protected String formName;
    public TabForm tf;
	
	public TabItem(int width, int height, String tabName, TabForm tabForm) {
		super("");
        this.tf = tabForm;
		init(width, height, tabName, tabForm);
	}

	// no tab
	public TabItem(int width, int height) {
		super("");
		paintTab = false;
		init(width, height, null, null);
	}

	private void init(int width, int height, String tabName, TabForm tabForm) {
		repeater = new KeyRepeatTask(this);
		
		tabHeight = blunet.boldFont.getHeight() + 6;
		if (tabName != null && tabForm != null) {
			if (tabSize < MAX_TAB_SIZE) {
				tabNames[tabSize++] = tabName;
				tabFormMap.put(tabName, tabForm);
				if (tabSize == 1) {
					setSelectedTab(0);
				}
			}
			else
				System.err.println("Too many tabs, max is 10!");
		}
		resize(width, height);
	}
	
	public void commandAction(Command cmd, Item item) {
	}

	protected void sizeChanged(int width, int height) {
		resize(width, height);
	}

	protected void resize(int width, int height) {
		myWidth = width;
		myHeight = height;
	}

	protected int getMinContentWidth() {
		return myWidth;
	}

	protected int getMinContentHeight() {
		return tabHeight;
	}

	protected int getPrefContentWidth(int w) {
		return myWidth;
	}

	protected int getPrefContentHeight(int h) {
		return myHeight;
	}

	public int getSelectedTab() {
		return selectedTab;
	}

	public void setRowItems(Vector rowItems) {
		this.rowItems = rowItems;
	}
	
	protected void pre_paint(Graphics g, int w, int h) {
	}
	
	protected void post_paint(Graphics g, int w, int h) {
	}
	
	protected void paint(Graphics g, int w, int h) {

                this.g = g;
                this.w= w;
                this.h = h;

		myWidth = w;
		g.setFont(blunet.plainFont);
		
		pre_paint(g, w, h);
		
		if (paintTab)
			drawTab(g, 0, 0, myWidth, tabHeight);
		else if(formName != null) {
			// draw title bar and background
			g.setGrayScale(255);
			g.fillRect(0, 0, myWidth, tabHeight);
			drawBackground(g, myWidth, myHeight, tabHeight);
			int y1 = 4 + (tabHeight - blunet.plainFont.getHeight()) / 2;
			g.drawString(formName, 4, y1, Graphics.TOP | Graphics.LEFT);
		}
		int xPos = 2;
		int yPos = tabHeight+4;
		maxVisibleLines = (myHeight - yPos) / rowHeight;
		
		if (rowItems.size() == 0)
			return;
		
		numItem = rowItems.size();
		if (focusIndex >= numItem)
			focusIndex = -1;
		
		if (numItem < maxVisibleLines) {
			lastVisibleLine = (firstVisibleLine + numItem) - 1;
		} else {
			lastVisibleLine = (firstVisibleLine + maxVisibleLines) - 1;
			if (lastVisibleLine >= numItem)
				lastVisibleLine = numItem - 1;
		}
		
		int halfIconWidth = 4;
		for (int i = 0; i < numItem; i++) {
			RowItem rowItem = (RowItem) rowItems.elementAt(i);
			if (rowItem.getIcon() != null && rowItem.getIcon().getWidth() > halfIconWidth)
				halfIconWidth = rowItem.getIcon().getWidth();
		}
		halfIconWidth /= 2;
		if (focusIndex == -1 && rowItems.size() > 0)
			focusIndex = 0;
		for (int i = firstVisibleLine; i <= lastVisibleLine; i++) {
			RowItem rowItem = (RowItem) rowItems.elementAt(i);
			
			// the row with 'focus'
			if (rowItem.isChecked()) {
				g.setColor(0xFAFAFAFA);
				g.fillRect(0, yPos, myWidth, rowHeight);
				g.setColor(0x000000);
			}
			else{
				g.setColor(0xFFFFFF);
			}
			
			xPos = 2 + halfIconWidth;
			int yshift = 4;

			if (rowItem.getIcon() != null) {
				g.drawImage(rowItem.getIcon(), xPos, yPos + rowHeight/2,
							Graphics.VCENTER | Graphics.HCENTER);
			}

			xPos += halfIconWidth + 2;
			String[] strItems = rowItem.getItems();
			int[] widthes = rowItem.getWidthes();
			for (int j = 0; j < strItems.length; j++) {
				String text = strItems[j];
				if (text != null)
					g.drawString(text, xPos, yPos+yshift, Graphics.TOP | Graphics.LEFT);
				xPos += widthes[j] + 2;
			}

			if (focusIndex == i) {
				g.drawRect(0, yPos, myWidth-1, rowHeight-1);
			}

			// now do the next row..
			yPos += rowHeight;
		}
		post_paint(g, w, h);
	}

	// ###########################################################
	// Movement
	// ###########################################################

	// this keeps track of which keys are pressed
	static final int KUP = 0;
	static final int KDOWN = 1;
	static final int KLEFT = 2;
	static final int KRIGHT = 3;
	static final int KFIRE = 4;
	static final int KSTAR = 5;

	int pressedKeys[] = { 0, 0, 0, 0, 0, 0 };
	int blk_move = 0; // -1 - move down, 1 - move up, 0 - initial

	/**
	 * 
	 */
	protected void keyPressed(int keyCode) {

		int keyAction = getGameAction(keyCode);
		keyPressedTime = System.currentTimeMillis();
		cleanKeyTrack();
	
//		BWAlert.infoAlert("code=" + keyCode + ", GameAction code=" + keyAction);
		if (keyAction == Canvas.UP) {
                        //System.out.println("Mouse Up pressed " + focusIndex);
			pressedKeys[KUP] = 1;
			if (pressedKeys[KSTAR] == 1 && blk_move != 1) {
				check();
				blk_move = 1;
			}
			moveUp();
		}

		if (keyAction == Canvas.DOWN) {
                        
			pressedKeys[KDOWN] = 1;
			if (pressedKeys[KSTAR] == 1 && blk_move != -1) {
				check();
				blk_move = -1;
			}
			moveDown();
		}

		if (keyAction == Canvas.RIGHT) {
			pressedKeys[KRIGHT] = 1;
			moveRight();
		}

		if (keyAction == Canvas.LEFT) {
			pressedKeys[KLEFT] = 1;
			moveLeft();
		}

		if (keyAction == Canvas.FIRE) {
			pressedKeys[KFIRE] = 1;
		}

		if (keyCode == Canvas.KEY_STAR || keyCode == NOKIA_KEY_PEN) {
			pressedKeys[KSTAR] = 1;
		}
	}

	protected void keyReleased(int keyCode) {
		int keyAction = getGameAction(keyCode);
		keyReleasedTime = System.currentTimeMillis();
		idleTicks = 0;

		if (keyAction == Canvas.UP) {
			pressedKeys[KUP] = 0;
		}

		if (keyAction == Canvas.DOWN) {
			pressedKeys[KDOWN] = 0;
		}

		if (keyAction == Canvas.RIGHT) {
			pressedKeys[KRIGHT] = 0;
		}

		if (keyAction == Canvas.LEFT) {
			pressedKeys[KLEFT] = 0;
		}

		if (keyCode == Canvas.KEY_STAR || keyCode == NOKIA_KEY_PEN) {
			blk_move = 0;
			pressedKeys[KSTAR] = 0;
		}

		if (keyAction == Canvas.FIRE) {
			pressedKeys[KFIRE] = 0;
			if (keyReleasedTime - keyPressedTime > KEY_HOLD_TIME)
				check();
			else  // launch application
				fire();
		}
	}

	void check() {
		
	}
	void fire() {
		
	}
	
	void moveUp() {
		if (numItem <= 0 || focusIndex == 0)
			return;
		
		if (focusIndex > numItem - 1)
			focusIndex = numItem;
		
		if (focusIndex < 0)
			focusIndex = 1;

		focusIndex--;
		if (focusIndex < firstVisibleLine){
			firstVisibleLine--;
			lastVisibleLine--;
		}
		
		if (pressedKeys[KSTAR] == 1) { 
			check();
		}
		
		repaint();
	}

	void moveDown() {

            System.out.println("Mouse down pressed " + focusIndex + " " + numItem);

		if (numItem <= 0 || focusIndex == numItem -1)
			return;
		
		if (focusIndex > numItem - 1)
			focusIndex = numItem - 2;
		
		if (focusIndex < 0)
			focusIndex = -1;

		focusIndex++;
		if (focusIndex > lastVisibleLine){
			firstVisibleLine++;
			lastVisibleLine++;
		}
		
		if (pressedKeys[KSTAR] == 1) { 
			check();
		}


                System.out.println("Mouse down pressed " + focusIndex);
		paint(g,w,h);
	}

	void moveRight() {
		if (!selectedTabForm.hasTab)
			return;
		// move on right only apply to tab
		int prevIndex = selectedTab;
		selectedTab++;
		if (selectedTab >= tabSize)
			selectedTab = 0;
		setSelectedTab(selectedTab);
		moveToNextState(prevIndex, selectedTab);
		repaint();
	}

	void moveLeft() {
		if (!selectedTabForm.hasTab)
			return;
		int prevIndex = selectedTab;
		// move on left only apply to tab
		selectedTab--;
		if (selectedTab < 0)
			selectedTab = tabSize - 1;
		setSelectedTab(selectedTab);
		moveToNextState(prevIndex, selectedTab);
		repaint();
	}

	/**
	 * tick() is called by TimerTask
	 *
	 */
	public void tick () {
//		System.out.println(idleTicks);
		
		if (pressedKeys[KUP] != 0)
		{
			if (pressedKeys[KUP] > 3)
				moveUp();
			else
				pressedKeys[KUP]++;
		}

		if (pressedKeys[KDOWN] != 0)
		{
			if (pressedKeys[KDOWN] > 3)
				moveDown();
			else
				pressedKeys[KDOWN]++;
		}
/*		
		if (pressedKeys[KLEFT] != 0)
		{
			if (pressedKeys[KLEFT] > 3)
				moveLeft();
			else
				pressedKeys[KLEFT]++;
		}

		if (pressedKeys[KRIGHT] != 0)
		{
			if (pressedKeys[KRIGHT] > 3)
				moveRight();
			else
				pressedKeys[KRIGHT]++;
		}
*/
		
		if ((pressedKeys[KUP] == 0) &&
			(pressedKeys[KDOWN] == 0) &&
			(pressedKeys[KLEFT] == 0) &&
			(pressedKeys[KRIGHT] == 0) &&
			(pressedKeys[KFIRE] == 0))
		{
			idleTicks++;
			
			if (idleTicks == 1000){
				idleTicks = 500;	
			}
		}
		
		
/*
		// hover off
		if (idleTicks > 15){
			drawCursor  = false; // turn off the cursor arrow
			
			if (hover == true){
				hover = false;
				//repaint();
			}
		}
		// hover on
		else if (idleTicks > 5)
		{
			if (hover == false){
				hover = true;
				//repaint();
			}
		}
*/		
		
	}
	
	
//  ###########################################################
// 	Visibility
//	###########################################################

	private boolean hover = false;
	private boolean hidden = false;
	
	protected void cleanKeyTrack() {
		pressedKeys[KUP] = 0;
		pressedKeys[KDOWN] = 0;
		pressedKeys[KLEFT] = 0;
		pressedKeys[KRIGHT] = 0;
		pressedKeys[KFIRE] = 0;
//		pressedKeys[KSTAR] = 0;
	}
	
	protected void hideNotify(){
		theTimer.cancel();
		theTimer = null;
		hidden = true;
		cleanKeyTrack();
		// initialize data for next re-enter
//		firstVisibleLine = 0;
//		lastVisibleLine = 0;
//		maxVisibleLines = 0;
	}
	
	protected void showNotify(){
		
		theTimer = new Timer();
		theTimer.schedule(repeater, 0, KEY_REPEAT_DELAY);
				
		if (hover)
		{
			hover = false;
			//pressedKeys[KFIRE] = 0;
		}
		hidden = false;
		numItem = rowItems.size();
		if (numItem == 0 || firstVisibleLine > numItem)
			firstVisibleLine = 0;
//		lastVisibleLine = 0;
//		maxVisibleLines = 0;
	}
	
	// ###########################################################
	// Traversal
	// ###########################################################

    private final static int UPPER = 0;
    private final static int IN = 1;
    private final static int LOWER = 2;
    // status tells us where the cursor is on the form, UPPER, IN or LOWER
    private int status = UPPER;
    
	protected boolean traverse(int dir, int viewportWidth, int viewportHeight,
			int[] visRect_inout) {
/*		switch (dir) {
	        case Canvas.DOWN:
	        	if (status == UPPER) {
	        		status = IN;
	        	}
	        	else if (status == IN) {
	        		status = LOWER;
	        		return false;
	        	}
	        	repaint();
	          break;

	        case Canvas.UP:
	        	if (status == LOWER) {
	        		status = IN;
	        	}
	        	else if (status == IN) {
	        		status = UPPER;
	        		return false;
	        	}
	        	repaint();
	        break;
	    }*/
		return true;
	}

	public void refresh() {
		repaint();
	}
	
	private static void setSelectedTab(int index) {
		selectedTab = index;
		selectedTabForm = (TabForm) tabFormMap.get(tabNames[index]);
	}
	
	private static void moveToNextState(int prevIndex, int index) {
		TabForm tmpForm = (TabForm) tabFormMap.get(tabNames[index]);
		if (tmpForm != null) {
			// enter new form
			tmpForm.midlet.changeScreen(tmpForm);
		}
	}
	
	private void drawTab(Graphics g, 
			int sx, int sy, int myWidth, int tabHeight)
	{
		g.setGrayScale(255);
		g.fillRect(0, 0, myWidth, tabHeight);
		int x = 0;
		for (int i = 0; i < tabSize; i++) {
			tabXCoord[i] = x;
			int width = blunet.boldFont.stringWidth(tabNames[i]);
			if (i == selectedTab) {
				x += width + 6;
				continue;// selected tab
			}
			drawATab(g, 0xBBBBBB, x, width, tabHeight, tabNames[i]);
			x += width + 6;
		}
		if (selectedTab < tabSize) {
			// draw selected tab
			x = tabXCoord[selectedTab];
			int width = blunet.boldFont.stringWidth(tabNames[selectedTab]);
			drawATab(g, 0x606060, x, width, tabHeight, tabNames[selectedTab]);
		}
		drawBackground(g, myWidth, myHeight, tabHeight);
	}
	
	private void drawATab(Graphics g, int scale,
				int x, int width, int tabHeight, String name) {
		g.setColor(0xDDDDDD);
		g.fillRoundRect(x, 0, width+8, tabHeight+4, 8, 8);
		g.setColor(scale);
		g.fillRoundRect(x+2, 2, width+8, tabHeight+4, 8, 8);
		g.setColor(0xFFFFFF);
		g.setFont(blunet.plainFont);
		int y = 4 + (tabHeight - blunet.plainFont.getHeight()) / 2;
		g.drawString(name, x+4, y, Graphics.TOP |Graphics.LEFT);
	}

	void drawBackground(Graphics g, int myWidth, int myHeight, int tabHeight)
	{
		int xshift = 6;
		int yshift = 20;
		g.setColor(0x606060);
		g.fillRect(0, tabHeight, myWidth-xshift, myHeight-yshift);
	}

	public static TabForm getSelectedTabForm() {
		return selectedTabForm;
	}

	public String getFormName() {
		return formName;
	}

	public void setFormName(String formName) {
		this.formName = formName;
	}
	
}