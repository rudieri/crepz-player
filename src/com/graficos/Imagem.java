/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.graficos;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

/**
 *
 * @author Rudieri T. Colbek
 */
public class Imagem {
    BufferedImage image;
    BufferedImage resised;
    private final String path="/com/img/cache/";
    public Imagem(BufferedImage bf){
        image=bf;
    }
    public void reSise(short width, short height){
        resised = (BufferedImage) (image.getScaledInstance(width, height, BufferedImage.SCALE_SMOOTH));

    }
    public ImageIcon getImage(){
        return new ImageIcon(image);
    }
    public ImageIcon getMiniatura(){
        return new ImageIcon(resised);
    }
    public boolean salvarImagem(String arquivo){
        try {
             File file = new File(path+arquivo);
            ImageIO.write(resised, "jpg", file);
            ImageIO.write(image, "jpg", file);
            return true;
        } catch (IOException ex) {
            Logger.getLogger(Imagem.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
     public boolean salvarMiniatura(String arquivo) throws Exception{
        try {
            if(resised==null){
                throw new Exception("A imagem não foi redimensionada!!! Tu esqueceru!!!");
            }
            File file = new File(path+arquivo);
            ImageIO.write(resised, "jpg", file);
            return true;
        } catch (IOException ex) {
            Logger.getLogger(Imagem.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
}
