/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.main;

import com.Musiquera;
import java.awt.AWTException;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;

/**
 *
 * @author rudieri
 */
public class CrepzTray {

    private final SystemTray tray;
    private final Musiquera musiquera;
    private final Carregador carregador;
    private final Image image;
    private ActionListener listenerRestaurar;
    private ActionListener listenerFechar;
    private final TrayIcon trayIcon;
    private PopupMenu popup;
    private MouseAdapter trayMouseEvents;

    public CrepzTray(Musiquera musiquera, final Carregador carregador) throws Exception {
        if (!SystemTray.isSupported()) {
            throw new Exception("SystemTray não suportado.");
        }
        tray = SystemTray.getSystemTray();
        this.musiquera = musiquera;
        this.carregador = carregador;
        image = new ImageIcon(getClass().getResource("/com/img/icon.png")).getImage();
        createPopupEvents();
        createMouseEvents();
        trayIcon = new TrayIcon(image, "Crepz Player", popup);
        trayIcon.setImageAutoSize(true);

    }

    public void toTray() {
        try {
            trayIcon.addMouseListener(trayMouseEvents);
            tray.add(trayIcon);
            //carregador.setMiniComoBase();
        } catch (AWTException ex) {
            Logger.getLogger(CrepzTray.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void someTray() {
        tray.remove(trayIcon);
//        carregador.setPrincipalComoBase();
    }

    public void someTray(int x, int y) {
        tray.remove(trayIcon);
//        carregador.setPrincipalComoBase(x, y);
    }

    private void createPopupEvents() {
        listenerRestaurar = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                someTray();
            }
        };
        listenerFechar = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                carregador.sair();

            }
        };

        popup = new PopupMenu();
        MenuItem itemRestaurar = new MenuItem("Restaurar");
        MenuItem itemFechar = new MenuItem("Sair");
        itemRestaurar.addActionListener(listenerRestaurar);
        itemFechar.addActionListener(listenerFechar);
        popup.add(itemRestaurar);
        popup.add(itemFechar);
    }

    private void createMouseEvents() {
        trayMouseEvents = new java.awt.event.MouseAdapter() {

            private int initX;
            private int initY;

            @Override
            public void mouseClicked(MouseEvent evt) {
                if (evt.getButton() == MouseEvent.BUTTON1) {
                    if (evt.getClickCount() == 2) {
                        musiquera.tocarPausar();
                    }
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
                initX = e.getXOnScreen();
                initY = e.getYOnScreen();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    if (Math.abs(initX - e.getXOnScreen()) > 1 || Math.abs(initY - e.getYOnScreen()) > 1) {
                        someTray(e.getXOnScreen(), e.getYOnScreen());
                    } else {
                        carregador.setMiniComoBase();
                    }
                }
                if (e.getButton() == MouseEvent.BUTTON2) {
                    someTray();
                }
            }
        };
    }
}
