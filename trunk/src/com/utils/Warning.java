/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.utils;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author rudieri
 */
public class Warning {
    public static void write(String msg){
        try {
            msg+="\n";
            System.err.write(msg.getBytes());
        } catch (IOException ex) {
            Logger.getLogger(Warning.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
