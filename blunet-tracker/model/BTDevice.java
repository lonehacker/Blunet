package model;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */



/**
 *
 * @author dell
 */
public class BTDevice {
    String name;
    String btAddress;
    String url;

    public BTDevice(String name, String url, String btAddress) {
        this.name = name;
        this.btAddress = btAddress;
        this.url = url;
    }
}
