/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.graficos;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

/**
 *
 * @author rudieri
 */
public class Icones {

    /**
    Carrega icones...
     * @param tipo nome da pasta em que estao os icones
     */
    public void loadIcons(String tipo) {
        try {
            /**


             */
            
            BufferedImage bf_playIcon32 = ImageIO.read(getClass().getResource("/com/img/icons/" + tipo + "/32/pl_play.png"));
            BufferedImage bf_stopIcon32 = ImageIO.read(getClass().getResource("/com/img/icons/" + tipo + "/32/pl_stop.png"));
            BufferedImage bf_voltaIcon32 = ImageIO.read(getClass().getResource("/com/img/icons/" + tipo + "/32/pl_tras.png"));
            BufferedImage bf_frenteIcon32 = ImageIO.read(getClass().getResource("/com/img/icons/" + tipo + "/32/pl_frente.png"));
            BufferedImage bf_pauseIcon32 = ImageIO.read(getClass().getResource("/com/img/icons/" + tipo + "/32/pl_pause.png"));
            BufferedImage bf_randomOnIcon32 = ImageIO.read(getClass().getResource("/com/img/icons/" + tipo + "/32/pl_randon_on.png"));
            BufferedImage bf_randomOffIcon32 = ImageIO.read(getClass().getResource("/com/img/icons/" + tipo + "/32/pl_randon_off.png"));
            BufferedImage bf_repeatOnIcon32 = ImageIO.read(getClass().getResource("/com/img/icons/" + tipo + "/32/pl_repeat_on.png"));
            BufferedImage bf_repeatOffIcon32 = ImageIO.read(getClass().getResource("/com/img/icons/" + tipo + "/32/pl_repeat_off.png"));
            BufferedImage bf_playIcon16 = ImageIO.read(getClass().getResource("/com/img/icons/" + tipo + "/16/pl_play.png"));
            BufferedImage bf_stopIcon16 = ImageIO.read(getClass().getResource("/com/img/icons/" + tipo + "/16/pl_stop.png"));
            BufferedImage bf_voltaIcon16 = ImageIO.read(getClass().getResource("/com/img/icons/" + tipo + "/16/pl_tras.png"));
            BufferedImage bf_frenteIcon16 = ImageIO.read(getClass().getResource("/com/img/icons/" + tipo + "/16/pl_frente.png"));
            BufferedImage bf_pauseIcon16 = ImageIO.read(getClass().getResource("/com/img/icons/" + tipo + "/16/pl_pause.png"));
            BufferedImage bf_randomOnIcon16 = ImageIO.read(getClass().getResource("/com/img/icons/" + tipo + "/16/pl_randon_on.png"));
            BufferedImage bf_randomOffIcon16 = ImageIO.read(getClass().getResource("/com/img/icons/" + tipo + "/16/pl_randon_off.png"));
            BufferedImage bf_repeatOnIcon16 = ImageIO.read(getClass().getResource("/com/img/icons/" + tipo + "/16/pl_repeat_on.png"));
            BufferedImage bf_repeatOffIcon16 = ImageIO.read(getClass().getResource("/com/img/icons/" + tipo + "/16/pl_repeat_off.png"));
            BufferedImage bf_topOn = ImageIO.read(getClass().getResource("/com/img/icons/" + tipo + "/mini/c_top_on.png"));
            BufferedImage bf_topOff = ImageIO.read(getClass().getResource("/com/img/icons/" + tipo + "/mini/c_top_off.png"));
            BufferedImage bf_menu = ImageIO.read(getClass().getResource("/com/img/icons/" + tipo + "/mini/c_menu.png"));
            BufferedImage bf_pl = ImageIO.read(getClass().getResource("/com/img/playlist.gif"));
            BufferedImage bf_lib = ImageIO.read(getClass().getResource("/com/img/biblioteca.png"));
            BufferedImage bf_xis = ImageIO.read(getClass().getResource("/com/img/x.png"));
            
            playIcon32 = new ImageIcon(bf_playIcon32);
            stopIcon32 = new ImageIcon(bf_stopIcon32);
            frenteIcon32 = new ImageIcon(bf_frenteIcon32);
            voltaIcon32 = new ImageIcon(bf_voltaIcon32);
            pauseIcon32 = new ImageIcon(bf_pauseIcon32);
            randomOnIcon32 = new ImageIcon(bf_randomOnIcon32);
            randomOffIcon32 = new ImageIcon(bf_randomOffIcon32);
            repeatOnIcon32 = new ImageIcon(bf_repeatOnIcon32);
            repeatOffIcon32 = new ImageIcon(bf_repeatOffIcon32);


            playIcon16 = new ImageIcon(bf_playIcon16);
            stopIcon16 = new ImageIcon(bf_stopIcon16);
            frenteIcon16 = new ImageIcon(bf_frenteIcon16);
            voltaIcon16 = new ImageIcon(bf_voltaIcon16);
            pauseIcon16 = new ImageIcon(bf_pauseIcon16);
            randomOnIcon16 = new ImageIcon(bf_randomOnIcon16);
            randomOffIcon16 = new ImageIcon(bf_randomOffIcon16);
            repeatOnIcon16 = new ImageIcon(bf_repeatOnIcon16);
            repeatOffIcon16 = new ImageIcon(bf_repeatOffIcon16);
//            save = new ImageIcon(bf_save);
//            saveAs = new ImageIcon(bf_saveAs);
            topOn = resizeIcons(bf_topOn, 13, 13);
            topOff = resizeIcons(bf_topOff, 13, 13);
            lib = resizeIcons(bf_lib, 13, 13);
            pl = resizeIcons(bf_pl, 13, 13);
            menu = resizeIcons(bf_menu, 13, 13);
            xis = resizeIcons(bf_xis, 13, 13);

            // inicializaIcones();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public ImageIcon resizeIcons(BufferedImage im, int l, int a) {
        return new ImageIcon(im.getScaledInstance(l, a, Image.SCALE_SMOOTH));
    }
    public ImageIcon playIcon32;
    public ImageIcon pauseIcon32;
    public ImageIcon stopIcon32;
    public ImageIcon randomOnIcon32;
    public ImageIcon randomOffIcon32;
    public ImageIcon frenteIcon32;
    public ImageIcon voltaIcon32;
    public ImageIcon repeatOnIcon32;
    public ImageIcon repeatOffIcon32;
    public ImageIcon playIcon16;
    public ImageIcon stopIcon16;
    public ImageIcon frenteIcon16;
    public ImageIcon voltaIcon16;
    public ImageIcon pauseIcon16;
    public ImageIcon randomOnIcon16;
    public ImageIcon randomOffIcon16;
    public ImageIcon repeatOnIcon16;
    public ImageIcon repeatOffIcon16;
//    public ImageIcon save;
//    public ImageIcon saveAs;
    public ImageIcon topOn;
    public ImageIcon topOff;
    public ImageIcon lib;
    public ImageIcon pl;
    public ImageIcon menu;
    public ImageIcon xis;
}
