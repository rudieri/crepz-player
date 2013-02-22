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
    public static final String CAMINHO_BASE_ICONES = "/com/img/icons/";
    

    /**
    Carrega icones...
     * @param tipo nome da pasta em que estao os icones
     */
    public void loadIcons(String tipo) {
        try {
            /**


             */
            
            BufferedImage bf_playIcon32 = ImageIO.read(getClass().getResource(CAMINHO_BASE_ICONES + tipo + "/32/pl_play.png"));
            BufferedImage bf_stopIcon32 = ImageIO.read(getClass().getResource(CAMINHO_BASE_ICONES + tipo + "/32/pl_stop.png"));
            BufferedImage bf_voltaIcon32 = ImageIO.read(getClass().getResource(CAMINHO_BASE_ICONES + tipo + "/32/pl_tras.png"));
            BufferedImage bf_frenteIcon32 = ImageIO.read(getClass().getResource(CAMINHO_BASE_ICONES + tipo + "/32/pl_frente.png"));
            BufferedImage bf_pauseIcon32 = ImageIO.read(getClass().getResource(CAMINHO_BASE_ICONES + tipo + "/32/pl_pause.png"));
            BufferedImage bf_randomOnIcon32 = ImageIO.read(getClass().getResource(CAMINHO_BASE_ICONES + tipo + "/32/pl_randon_on.png"));
            BufferedImage bf_randomOffIcon32 = ImageIO.read(getClass().getResource(CAMINHO_BASE_ICONES + tipo + "/32/pl_randon_off.png"));
            BufferedImage bf_repeatOnIcon32 = ImageIO.read(getClass().getResource(CAMINHO_BASE_ICONES + tipo + "/32/pl_repeat_on.png"));
            BufferedImage bf_repeatOffIcon32 = ImageIO.read(getClass().getResource(CAMINHO_BASE_ICONES + tipo + "/32/pl_repeat_off.png"));
            BufferedImage bf_playIcon16 = ImageIO.read(getClass().getResource(CAMINHO_BASE_ICONES + tipo + "/16/pl_play.png"));
            BufferedImage bf_stopIcon16 = ImageIO.read(getClass().getResource(CAMINHO_BASE_ICONES + tipo + "/16/pl_stop.png"));
            BufferedImage bf_voltaIcon16 = ImageIO.read(getClass().getResource(CAMINHO_BASE_ICONES + tipo + "/16/pl_tras.png"));
            BufferedImage bf_frenteIcon16 = ImageIO.read(getClass().getResource(CAMINHO_BASE_ICONES + tipo + "/16/pl_frente.png"));
            BufferedImage bf_pauseIcon16 = ImageIO.read(getClass().getResource(CAMINHO_BASE_ICONES + tipo + "/16/pl_pause.png"));
            BufferedImage bf_randomOnIcon16 = ImageIO.read(getClass().getResource(CAMINHO_BASE_ICONES + tipo + "/16/pl_randon_on.png"));
            BufferedImage bf_randomOffIcon16 = ImageIO.read(getClass().getResource(CAMINHO_BASE_ICONES + tipo + "/16/pl_randon_off.png"));
            BufferedImage bf_repeatOnIcon16 = ImageIO.read(getClass().getResource(CAMINHO_BASE_ICONES + tipo + "/16/pl_repeat_on.png"));
            BufferedImage bf_repeatOffIcon16 = ImageIO.read(getClass().getResource(CAMINHO_BASE_ICONES + tipo + "/16/pl_repeat_off.png"));
            BufferedImage bf_topOn = ImageIO.read(getClass().getResource("/com/img/c_top_on.png"));
            BufferedImage bf_topOff = ImageIO.read(getClass().getResource("/com/img/c_top_off.png"));
            BufferedImage bf_menu = ImageIO.read(getClass().getResource("/com/img/c_menu.png"));
            BufferedImage bf_pl = ImageIO.read(getClass().getResource("/com/img/playlist.gif"));
            BufferedImage bf_edit = ImageIO.read(getClass().getResource("/com/img/edit.png"));
            BufferedImage bf_lib = ImageIO.read(getClass().getResource("/com/img/biblioteca.png"));
            BufferedImage bf_xis = ImageIO.read(getClass().getResource("/com/img/x.png"));
            
            crepzIcon = new ImageIcon(getClass().getResource("/com/img/icon.png"));
            
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
            biblioteca = resizeIcons(bf_lib, 13, 13);
            playList = resizeIcons(bf_pl, 13, 13);
            menu = resizeIcons(bf_menu, 13, 13);
            edit = resizeIcons(bf_edit, 13, 13);
            xis = resizeIcons(bf_xis, 13, 13);

            // inicializaIcones();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public ImageIcon resizeIcons(BufferedImage im, int largura, int altura) {
        return new ImageIcon(im.getScaledInstance(largura, altura, Image.SCALE_SMOOTH));
    }
    private ImageIcon crepzIcon;
    private ImageIcon playIcon32;
    private ImageIcon pauseIcon32;
    private ImageIcon stopIcon32;
    private ImageIcon randomOnIcon32;
    private ImageIcon randomOffIcon32;
    private ImageIcon frenteIcon32;
    private ImageIcon voltaIcon32;
    private ImageIcon repeatOnIcon32;
    private ImageIcon repeatOffIcon32;
    private ImageIcon playIcon16;
    private ImageIcon stopIcon16;
    private ImageIcon frenteIcon16;
    private ImageIcon voltaIcon16;
    private ImageIcon pauseIcon16;
    private ImageIcon randomOnIcon16;
    private ImageIcon randomOffIcon16;
    private ImageIcon repeatOnIcon16;
    private ImageIcon repeatOffIcon16;
//    public ImageIcon save;
//    public ImageIcon saveAs;
    private ImageIcon topOn;
    private ImageIcon topOff;
    private ImageIcon biblioteca;
    private ImageIcon playList;
    private ImageIcon menu;
    private ImageIcon edit;
    private ImageIcon xis;

    /**
     * @return the crepzIcon
     */
    public ImageIcon getCrepzIcon() {
        return crepzIcon;
    }

    /**
     * @return the playIcon32
     */
    public ImageIcon getPlayIcon32() {
        return playIcon32;
    }

    /**
     * @return the pauseIcon32
     */
    public ImageIcon getPauseIcon32() {
        return pauseIcon32;
    }

    /**
     * @return the stopIcon32
     */
    public ImageIcon getStopIcon32() {
        return stopIcon32;
    }

    /**
     * @return the randomOnIcon32
     */
    public ImageIcon getRandomOnIcon32() {
        return randomOnIcon32;
    }

    /**
     * @return the randomOffIcon32
     */
    public ImageIcon getRandomOffIcon32() {
        return randomOffIcon32;
    }

    /**
     * @return the frenteIcon32
     */
    public ImageIcon getFrenteIcon32() {
        return frenteIcon32;
    }

    /**
     * @return the voltaIcon32
     */
    public ImageIcon getVoltaIcon32() {
        return voltaIcon32;
    }

    /**
     * @return the repeatOnIcon32
     */
    public ImageIcon getRepeatOnIcon32() {
        return repeatOnIcon32;
    }

    /**
     * @return the repeatOffIcon32
     */
    public ImageIcon getRepeatOffIcon32() {
        return repeatOffIcon32;
    }

    /**
     * @return the playIcon16
     */
    public ImageIcon getPlayIcon16() {
        return playIcon16;
    }

    /**
     * @return the stopIcon16
     */
    public ImageIcon getStopIcon16() {
        return stopIcon16;
    }

    /**
     * @return the frenteIcon16
     */
    public ImageIcon getFrenteIcon16() {
        return frenteIcon16;
    }

    /**
     * @return the voltaIcon16
     */
    public ImageIcon getVoltaIcon16() {
        return voltaIcon16;
    }

    /**
     * @return the pauseIcon16
     */
    public ImageIcon getPauseIcon16() {
        return pauseIcon16;
    }

    /**
     * @return the randomOnIcon16
     */
    public ImageIcon getRandomOnIcon16() {
        return randomOnIcon16;
    }

    /**
     * @return the randomOffIcon16
     */
    public ImageIcon getRandomOffIcon16() {
        return randomOffIcon16;
    }

    /**
     * @return the repeatOnIcon16
     */
    public ImageIcon getRepeatOnIcon16() {
        return repeatOnIcon16;
    }

    /**
     * @return the repeatOffIcon16
     */
    public ImageIcon getRepeatOffIcon16() {
        return repeatOffIcon16;
    }

    /**
     * @return the topOn
     */
    public ImageIcon getTopOn() {
        return topOn;
    }

    /**
     * @return the topOff
     */
    public ImageIcon getTopOff() {
        return topOff;
    }

    /**
     * @return the biblioteca
     */
    public ImageIcon getBiblioteca() {
        return biblioteca;
    }

    /**
     * @return the playList
     */
    public ImageIcon getPlayList() {
        return playList;
    }

    /**
     * @return the menu
     */
    public ImageIcon getMenu() {
        return menu;
    }

    /**
     * @return the edit
     */
    public ImageIcon getEdit() {
        return edit;
    }

    /**
     * @return the xis
     */
    public ImageIcon getXis() {
        return xis;
    }
}
