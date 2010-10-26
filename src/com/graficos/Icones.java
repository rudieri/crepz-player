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
            BufferedImage bf_playIcon = ImageIO.read(getClass().getResource("/com/img/icons/" + tipo + "/play.png"));
            BufferedImage bf_stopIcon = ImageIO.read(getClass().getResource("/com/img/icons/" + tipo + "/stop.png"));
            BufferedImage bf_voltaIcon = ImageIO.read(getClass().getResource("/com/img/icons/" + tipo + "/tras.png"));
            BufferedImage bf_frenteIcon = ImageIO.read(getClass().getResource("/com/img/icons/" + tipo + "/frente.png"));
            BufferedImage bf_pauseIcon = ImageIO.read(getClass().getResource("/com/img/icons/" + tipo + "/pause.png"));
            BufferedImage bf_randomOnIcon = ImageIO.read(getClass().getResource("/com/img/icons/" + tipo + "/trueRandom.png"));
            BufferedImage bf_randomOffIcon = ImageIO.read(getClass().getResource("/com/img/icons/" + tipo + "/falseRandom.png"));
            BufferedImage bf_repeatOnIcon = ImageIO.read(getClass().getResource("/com/img/icons/" + tipo + "/repeatOn.png"));
            BufferedImage bf_repeatOffIcon = ImageIO.read(getClass().getResource("/com/img/icons/" + tipo + "/repeatOff.png"));
            BufferedImage bf_save = ImageIO.read(getClass().getResource("/com/img/icons/" + tipo + "/save.png"));
            BufferedImage bf_saveAs = ImageIO.read(getClass().getResource("/com/img/icons/" + tipo + "/saveAs.png"));
            BufferedImage bf_topOn = ImageIO.read(getClass().getResource("/com/img/icons/" + tipo + "/c_top_on.png"));
            BufferedImage bf_topOff = ImageIO.read(getClass().getResource("/com/img/icons/" + tipo + "/c_top_off.png"));
            BufferedImage bf_menu = ImageIO.read(getClass().getResource("/com/img/icons/" + tipo + "/c_menu.png"));
            BufferedImage bf_pl = ImageIO.read(getClass().getResource("/com/img/playlist.gif"));
            BufferedImage bf_lib = ImageIO.read(getClass().getResource("/com/img/biblioteca.png"));
            BufferedImage bf_xis = ImageIO.read(getClass().getResource("/com/img/x.png"));

            playIcon = new ImageIcon(bf_playIcon);
            stopIcon = new ImageIcon(bf_stopIcon);
            frenteIcon = new ImageIcon(bf_frenteIcon);
            voltaIcon = new ImageIcon(bf_voltaIcon);
            pauseIcon = new ImageIcon(bf_pauseIcon);
            randomOnIcon = new ImageIcon(bf_randomOnIcon);
            randomOffIcon = new ImageIcon(bf_randomOffIcon);
            repeatOnIcon = new ImageIcon(bf_repeatOnIcon);
            repeatOffIcon = new ImageIcon(bf_repeatOffIcon);
            save = new ImageIcon(bf_save);
            saveAs = new ImageIcon(bf_saveAs);
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
    public ImageIcon playIcon;
    public ImageIcon pauseIcon;
    public ImageIcon stopIcon;
    public ImageIcon randomOnIcon;
    public ImageIcon randomOffIcon;
    public ImageIcon frenteIcon;
    public ImageIcon voltaIcon;
    public ImageIcon repeatOnIcon;
    public ImageIcon repeatOffIcon;
    public ImageIcon save;
    public ImageIcon saveAs;
    public ImageIcon topOn;
    public ImageIcon topOff;
    public ImageIcon lib;
    public ImageIcon pl;
    public ImageIcon menu;
    public ImageIcon xis;
}
