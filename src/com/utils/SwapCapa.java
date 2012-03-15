/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.utils;

import java.util.HashMap;
import javax.swing.ImageIcon;

/**
 *
 * @author manchini
 */
public class SwapCapa {

    public static HashMap<String,ImageIcon> swap = new HashMap<String, ImageIcon>(50);

    public static void reset(){
        swap.clear();
    }

}
