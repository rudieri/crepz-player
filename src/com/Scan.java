/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com;

import com.conexao.SQL;
import com.conexao.Transacao;
import com.musica.Musica;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;

/**
 *
 * @author rudieri
 */
public class Scan {

    public static void main(String[] args) throws InterruptedException {
      //  Thread.sleep(500);
        Scan scan = new Scan();
        scan.start(5);
        scan.iconeTray();

    }

    class taskScan extends TimerTask {

        @Override
        public void run() {
            trace("oi");

            for(int i=0; i<pastas.size(); i++){
                atualiza();
                verifica((File)pastas.get(i), t);
            }

        }
    }

    public void start(int tempo) throws InterruptedException {
        t = new Transacao();
        addFolder(teste);
        addFolder(new File("/media/0A7CC6CC7CC6B1AD/Documents and Settings/Raoli S. Wagner/Desktop/Tudo/DVD"));
        try {
            t.begin();
        } catch (Exception ex) {
            Logger.getLogger(Scan.class.getName()).log(Level.SEVERE, null, ex);
        }
        tempo=tempo*60000;
        Timer tScan = new Timer();
        tScan.schedule(new taskScan(), 0, tempo);

    }
    public void addFolder(File f){
        pastas.add(f);
    }

    private void trace(Object o) {
        System.out.println(o);
    }

    private void verifica(File end, Transacao t) {
        if (end.isDirectory()) {
            trace("Dir: " + end.getAbsolutePath());
            for (int i = 0; i < end.listFiles().length; i++) {

                verifica(end.listFiles()[i], t);
            }
        } else {
            if(end.getAbsolutePath().indexOf(".mp3")==-1){
                return;
            }
           
            //  for(int i=0; i<end ; i++){
            if (musicas.indexOf(end.getAbsolutePath()) == -1) {

                Musica.addFiles(end, t);
            } else {
                trace("já tem!");
            }
            //    }
        }


    }
    private void  atualiza(){
         SQL sql = new SQL();
            sql.add("SELECT caminho FROM musica");
            musicas = new ArrayList();
            try {
                ResultSet rs = t.executeQuery(sql.getSql());
                while (rs.next()) {
                    musicas.add(rs.getString("caminho"));
                }
            } catch (Exception ex) {
                Logger.getLogger(Scan.class.getName()).log(Level.SEVERE, null, ex);
            }
    }
    private void iconeTray() {
        try {
            if (SystemTray.isSupported()) {
                tray = SystemTray.getSystemTray();
                //    setBandeija(true);
                Image image = new ImageIcon(getClass().getResource("/com/img/icon.png")).getImage();
                ActionListener listener1 = new ActionListener() {

                    public void actionPerformed(ActionEvent e) {
                        //   someTray();
                    }
                };
                ActionListener listener2 = new ActionListener() {

                    public void actionPerformed(ActionEvent e) {
                        //   sair();
                        System.exit(0);
                    }
                };

                PopupMenu popup = new PopupMenu();
                MenuItem item1 = new MenuItem("Restaurar");
                MenuItem item2 = new MenuItem("Sair");
                item1.addActionListener(listener1);
                item2.addActionListener(listener2);
                popup.add(item1);
                popup.add(item2);


                trayIcon = new TrayIcon(image, "Jar jar Player", popup);
                trayIcon.setImageAutoSize(true);

                try {
                    tray.add(trayIcon);
                } catch (Exception e) {
                    e.printStackTrace();
                }


                new Thread(new Runnable() {

                    public void run() {
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException ex) {
                            Logger.getLogger(JPrincipal.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }).start();

            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    private SystemTray tray;
    private TrayIcon trayIcon;
    private Transacao t;
    ArrayList musicas;
    ArrayList pastas = new ArrayList();
    File teste = new File("/home/rudieri/Música");
}
