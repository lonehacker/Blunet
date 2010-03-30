package view;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Gauge;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.TextField;

import core.*;

import blunet.*;

public class ProgressScreen extends BaseScreen {

  private Command cancelCommand;

  private Gauge gauge;
  private Form progressBar;
  private TextField textField;
  final static int MAXLEN = 200;
  
  public ProgressScreen(blunet midlet, String message) {
    super(midlet);

    progressBar = new Form("Please wait");
    
    gauge = new Gauge(message, false, 100, 0);//Gauge.INDEFINITE, Gauge.CONTINUOUS_RUNNING);
    gauge.setLayout(Item.LAYOUT_CENTER);
    gauge.setLayout(Item.LAYOUT_VCENTER);
    progressBar.append(gauge);
    
    textField = new TextField("Status", "", MAXLEN, TextField.UNEDITABLE);
    progressBar.append(textField);
    
    cancelCommand = new Command("Cancel", Command.CANCEL, 1);
    progressBar.addCommand(cancelCommand);

    progressBar.setCommandListener(this);
   
    displayable = progressBar;
    
  }

  public void setLabel(String msg) {
	  gauge.setLabel(msg);
  }
  
  public void setMaxValue(int maxValue) {
	  gauge.setMaxValue(maxValue);
  }

  public void setValue(int value) {
	  gauge.setValue(value);
  }

  public void insertTextField(String text) {
	  int textLen = text.length();
	  if (textField.size() + textLen + 1 < MAXLEN) {
		  if (textField.size() > 0)
			  textField.insert("\n", textField.size());
		  textField.insert(text, textField.size());
	  }
	  else
		  textField.setString(text);
  }
  
  public void setText(String text) {
	  textField.setString(text);
  }
  
	public void progressUpdate(int part, int total, String text){
		if (total > 0) {
			setMaxValue(total);
			setValue(part);
		}
		if (text != null)
			setText(text);
	}
	
  public void commandAction(Command command, Displayable _displayable) {

    if (command == cancelCommand) {
    	// back to current state
    	
    }
  }

}
