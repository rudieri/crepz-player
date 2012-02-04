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

    public static void print(String msg, Throwable t) {
        print(msg);
        StackTraceElement[] stackTrace = t.getStackTrace();
        for (int i = 0; i < stackTrace.length; i++) {
            StackTraceElement stackTraceElement = stackTrace[i];
            print(stackTraceElement.toString());

        }
    }

    public static void print(String msg) {
        write(msg + "\n");
    }

    private static void write(String msg) {
        try {
            System.err.write(msg.getBytes());
        } catch (IOException ex) {
            Logger.getLogger(Warning.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
