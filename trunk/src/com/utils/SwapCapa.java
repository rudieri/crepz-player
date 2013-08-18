/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.utils;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

/**
 *
 * @author manchini
 */
public class SwapCapa {

    public static HashMap<String, ImageIcon> swap = new HashMap<String, ImageIcon>(50);

    public static void reset() {
        swap.clear();
    }

    public static ImageIcon getCapa(String endereco) {
        ImageIcon imageIcon = swap.get(endereco);
        if (imageIcon == null) {
            try {
                BufferedImage bf;
                bf = ImageIO.read(new File(endereco));
                if (bf != null) {
                    imageIcon = new javax.swing.ImageIcon(bf.getScaledInstance(80, 120, Image.SCALE_SMOOTH));
                    swap.put(endereco, imageIcon);
                }
            } catch (IOException ex) {
                Logger.getLogger(SwapCapa.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return imageIcon;
    }
}
