package view;

import javax.microedition.lcdui.CustomItem;
import javax.microedition.lcdui.Image;

public class RowItem {

	protected int count; // number of string items in a row
	protected Image icon;
	protected String[] items;
	protected int[] widthes;
	protected boolean isChecked;
	
	public RowItem(int count) {

		this.count = count;
		items = new String[count];
		widthes = new int[count];
	}

	public Image getIcon() {
		return icon;
	}

	public void setIcon(Image icon) {
		this.icon = icon;
	}

	public String[] getItems() {
		return items;
	}

	public int[] getWidthes() {
		return widthes;
	}

	public void setItems(int index, String text, int width) {
		if (index < 0 || index >= count)
			return;
		this.items[index] = text;
		this.widthes[index] = width;
	}

	public int getCount() {
		return count;
	}

	public boolean isChecked() {
		return isChecked;
	}

	public void setChecked(boolean isChecked) {
		this.isChecked = isChecked;
	}

}
