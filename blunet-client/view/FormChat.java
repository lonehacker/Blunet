package view;

import core.BTDesc;
import java.io.IOException;

import model.*;
import local.Labels;
import local.Local;

import javax.microedition.lcdui.*;
import core.*;
import blunet.*;
import util.Stack;

public class FormChat extends DisplayForm {
	

        Display display;

        StringItem history =new StringItem("history","");
        TextField mesg = new TextField("message", "", 40, TextField.ANY);

        public Chat currentChat;
        public blunet midlet;

	Command sendMessage = new Command(Local.get(Labels.SEND_MESSAGE), Command.SCREEN, 2);
        
	public FormChat(blunet midlet, String tabName, BTDesc btDesc, String name) {
                
                super(midlet, false, name);
                this.midlet=midlet;
                //System.out.println("initiate chat object");
                currentChat =  new Chat(btDesc, this);
                //System.out.println("chat object created");
		this.setTitle(Local.get(Labels.CHAT));
		//form.append(tabItem);
		form.addCommand(sendMessage);
                
                
                form.append( history);
                form.append(mesg );
                displayMe();

    }

    public FormChat(blunet midlet, String tabName, BTDesc btDesc, String name, int w) {

                super(midlet, false, name);
                this.midlet=midlet;
                System.out.println("initiate chat object");
                currentChat =  new Chat(btDesc, this, w);
                
                System.out.println("chat object created");
		this.setTitle(Local.get(Labels.CHAT));
		//form.append(tabItem);
		form.addCommand(sendMessage);


                form.append( history);
                form.append(mesg );
                displayMe();

    }

        
	
	public void commandAction(Command cmd, Displayable disp) {
		super.commandAction(cmd, disp);
		if (cmd == sendMessage) {
            try {
                currentChat.sendMessage(mesg.getString());
            } catch (Exception ex) {
                System.err.println(ex.getMessage());
                ex.printStackTrace();
            }
                        mesg.setString("");
			}

               
		}

	public void update(Stack messageObjects) {
	
            for(int i = 0; i<messageObjects.size();i++ )
                history.setText(history.getText() + "\n" + (String)messageObjects.pop());
	}
	
}
