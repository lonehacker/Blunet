package core;

import java.io.IOException;
import util.Kits;
import view.*;
import util.Stack;

public class Chat  {
    BTDesc buddy;
    int w = -1;
    FormChat formChat;
    Stack messageObjects = new Stack(100);

    public Chat(BTDesc buddy, FormChat formChat)   {
        this.buddy = buddy;
        this.formChat = formChat;

        w = -1;

        RequestProfile rp = new RequestProfile();
        rp.object = this;
        rp.type = "chat";
        rp.targetName = buddy.name;

        testClass.midlet.sched.addRequest(rp);

        while(w==-1){
            System.out.println("looping waiting for worker");
            Kits.sleep(1000);
        }

        System.out.println("worker obtained");

        testClass.w[w].chatObject = this;
        testClass.w[w].createConnection(buddy.url, "chat");//CHECK IF buddy.CALL is right or wrong
        System.out.println("fatal error: chat: "+testClass.w[w].isConnected);

        testClass.w[w].isConnected = true;
                System.out.println("Worker number is " + w);

    }

    public Chat(BTDesc buddy, FormChat formChat, int w)   {
        this.buddy = buddy;
        this.formChat = formChat;

        this.w = w;
        System.out.println("worker obtained");

        testClass.w[w].chatObject = this;
        testClass.w[w].isConnected = true;
        System.out.println("Worker number is " + w);
        //w.createConnection(buddy.url, "chat");//CHECK IF buddy.CALL is right or wrong
    }


    public void setWorker(int w)  {
        this.w=w;
    }

    public void sendMessage(String message) throws IOException {

        testClass.w[w].dataStream.dos.writeUTF("1chat "+message);
        testClass.w[w].dataStream.dos.flush();

        System.out.println("message written " + message);

        messageObjects.push("Me: "+message);
        formChat.update(messageObjects);

    }

    public void receiveMessage(String message)  {
        messageObjects.push(buddy.name+": "+message);
        formChat.update(messageObjects);
    }

    public void endChat()   {
        testClass.w[w].closeConnection();
    }
}