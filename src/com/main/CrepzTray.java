/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.main;

import com.config.constantes.TelaPadrao;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

/**
 *
 * @author rudieri
 */
public class CrepzTray implements ActionListener, MouseListener {

    private final SystemTray tray;
    private final Carregador carregador;
    private final Image image;
    private final TrayIcon trayIcon;
    private PopupMenu popup;
    private MouseAdapter trayMouseEvents;
    private boolean onTray = false;
    private MenuItem menuItemRestaurar;
    private MenuItem menuItemFechar;
    private int initX;
    private int initY;

    public CrepzTray(final Carregador carregador) throws Exception {
        if (!SystemTray.isSupported()) {
            throw new Exception("SystemTray não suportado.");
        }
        tray = SystemTray.getSystemTray();
        this.carregador = carregador;
        image = new ImageIcon(getClass().getResource("/com/img/icon.png")).getImage();
        createPopupMenu();
        trayIcon = new TrayIcon(image, "Crepz Player", popup);
        trayIcon.setImageAutoSize(true);

    }

    public void toTray() {
        try {
            trayIcon.addMouseListener(trayMouseEvents);
            tray.add(trayIcon);
            onTray = true;
            //carregador.setMiniComoBase();
        } catch (AWTException ex) {
            Logger.getLogger(CrepzTray.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void someTray() {
        tray.remove(trayIcon);
        onTray = false;
//        carregador.setPrincipalComoBase();
    }

    public void someTray(int x, int y) {
        carregador.setPrincipalComoBase(x, y);
        someTray();
    }

    public boolean isOnTray() {
        return onTray;
    }

    private void createPopupMenu() {
        popup = new PopupMenu();
        menuItemRestaurar = new MenuItem("Restaurar");
        menuItemFechar = new MenuItem("Sair");
        menuItemRestaurar.addActionListener(this);
        menuItemFechar.addActionListener(this);
        popup.add(menuItemRestaurar);
        popup.add(menuItemFechar);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == menuItemRestaurar) {
            carregador.setTelaBase(TelaPadrao.J_PRINCIPAL);
        } else if (e.getSource() == menuItemFechar) {
            carregador.sair();
        }
    }

    @Override
    public void mouseClicked(MouseEvent evt) {
        if (evt.getButton() == MouseEvent.BUTTON1) {
            if (evt.getClickCount() == 2) {
                carregador.tocarPausar();
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
            carregador.setTelaBase(TelaPadrao.J_PRINCIPAL);
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }
    
}
