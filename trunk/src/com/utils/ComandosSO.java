/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.utils;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author rudieri
 */
public class ComandosSO {

    private static final byte LINUX = 0;
    private static final byte WINDOWS = 1;
    private static final byte OUTRO = 2;
    private static final byte mySO;

    static {
        String soName = System.getProperty("os.name");
        if (soName.toLowerCase().contains("nux")) {
            mySO = LINUX;
        } else if (soName.toLowerCase().contains("win")) {
            mySO = WINDOWS;
        } else {
            mySO = OUTRO;

        }
    }

    public static void abrirPasta(String path) {
        try {
            switch (mySO) {
                case LINUX:
                    Runtime.getRuntime().exec("nautilus " + path);
                    break;
                case WINDOWS:
                    Runtime.getRuntime().exec("explorer " + path);
                    break;
                default:
                    JOptionPane.showMessageDialog(null, "Nenhum comando definido para seu SO.", "Camando desconhecido.", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (IOException ex) {
            Logger.getLogger(ComandosSO.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
