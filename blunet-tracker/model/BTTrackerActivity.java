package model;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */



import java.io.*;
import java.util.*;

import javax.microedition.rms.*;

//import local.Labels;
//import local.Local;
//import blue.bExplore;
//import view.*;
/**
 *
 * @author alok
 */
public class BTTrackerActivity {
    public boolean isConnected; // true - joined, false - left
//    BTDesc btDesc;
    public String btName;
    public String btUrl;
    public String btAddress;
    public long tstamp;
    public long aliveTS;
    
    public BTTrackerActivity(boolean isConnected, String btName, String btUrl, String btAddress){
        this.isConnected = isConnected;
        //this.btDesc = btDesc;
        this.btName = btName;
        this.btUrl = btUrl;
        this.btAddress = btAddress;
        tstamp = System.currentTimeMillis();
        aliveTS = tstamp;
    }

}
