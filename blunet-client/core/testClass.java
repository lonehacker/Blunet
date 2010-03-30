/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package core;

import blunet.blunet;

/**
 *
 * @author Amit
 */
public class testClass {

    public static Worker[] w = new Worker[5];
    public static blunet midlet;

    public testClass()
    {
        for(int i = 0; i< 5; i++)
        {
            this.w[i] = new Worker(i);
        }
    }


}
