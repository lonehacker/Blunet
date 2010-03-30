package view;
import java.util.TimerTask;

public class KeyRepeatTask extends TimerTask {
	private TabItem theItem;
	
	public KeyRepeatTask (TabItem item)
	{
		theItem = item;
	}

	public void run()
	{
		theItem.tick();
	}
}
