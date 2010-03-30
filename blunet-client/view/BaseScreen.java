package view;

import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;

import core.*;

import blunet.blunet;


public abstract class BaseScreen implements CommandListener {

	public blunet midlet;

	protected Displayable displayable;

	public BaseScreen(blunet midlet) {
		this.midlet = midlet;
	}

	/**
	 * Makes current screen active
	 * <p>
	 * Each class inherited from BaseScreen has a displayable associated.
	 * When this method is called the displayble is made visiable and
	 * active
	 * 
	 */
	public void makeActive() {
		try {
			Display d = Display.getDisplay(midlet);

			if (d.getCurrent() != displayable) { // this prevents from
				// bringing application to
				// foreground in case is
				// application is in
				// background and the
				// current displayable is
				// the same as the one
				// made active
				d.setCurrent(displayable);
			}

		} catch (NullPointerException e) {
			throw new RuntimeException(
					"Internal error #2: BaseScreen.midlet == null");
		}
	}

	public boolean isCurrentDisplayable() {
		Display d = Display.getDisplay(midlet);
		return (d.getCurrent() == displayable);

	}
}
