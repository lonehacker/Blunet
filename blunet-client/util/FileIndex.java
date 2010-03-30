/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package util;

import java.lang.String;

import java.util.*;

import blunet.blunet;

/**
 *
 * @author Ashish
 */
public class FileIndex {

    public Vector index = new Vector();
    blunet midlet;
    Vector sharedPaths = new Vector();

    public FileIndex(blunet midlet, String path)
    {
        System.out.println("inside constructor");


        
        //for(int i = 0; i < paths.length ; i++)
        {
           
            System.out.println("path "  + " " + path);
            try{


            //sharedPaths.addElement(paths[i].toString());
            System.out.println("Path added");
            }
            catch(Exception e)
            {
                System.out.println("Exception occured" + e.getMessage());
            }
        }

        this.midlet = midlet;

        System.out.println("callin createindex");
        createIndex(path);
    }

    public void createIndex(String path)
    {
        int i;
        
        System.out.print("I am called");

            addToIndex(path);
        
    }

    private void addToIndex(String path)
    {
        try{


                Enumeration e =  LocalFileHandler.loadFolder(path).fileObjectList.elements();
                while(e.hasMoreElements())
                {
                    FileObject fo = (FileObject)e.nextElement();
                    index.addElement(path + fo.getName());
                 //   if(fo.isFolder)
                   //     addToIndex(path + fo.getPath());
                }
            }
            catch(Exception e)
            {
                System.err.println("Exception occured" + e.getMessage());
            }
    }

}
