package view;

import core.BTDesc;
import java.util.*;
import model.*;
import util.*;
import core.*;
import blunet.blunet;

public class TabItemNeighbor extends TabItem {
	
    Vector userList;
	
	final static int BTNameLimit = 18;
	final static int BTAddressLimit = 5;

	public TabItemNeighbor(int width, int height, String tabName, TabForm tabForm) {
		super(width, height, tabName, tabForm);
	}
	

	public BTDesc getSelectedItem() {
		if (userList != null &&
				focusIndex >= 0 && focusIndex < userList.size())
			return (BTDesc) userList.elementAt(focusIndex);
		return null;
	}
	
	public void check() {
		
	}
	
	public void update(Vector userList) {

        this.userList = userList;
		rowItems.removeAllElements();
		
      //  System.out.println("updating the neighbolist " + userList.size());


		for (int i = 0; i < userList.size(); i++) {

           // System.out.println("hello");
			RowItem rowItem = new RowItem(1);
			
			BTDesc btdesc = (BTDesc) userList.elementAt(i);
			String name = btdesc.name;
			rowItem.setItems(0, name, blunet.plainFont.stringWidth(name));
			
			
			rowItem.setIcon(Icon.FILE);
			
						
			rowItems.addElement(rowItem);
		}

	}

}