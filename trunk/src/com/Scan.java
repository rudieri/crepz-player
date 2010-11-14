/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com;

import com.conexao.SQL;
import com.conexao.Transacao;
import com.musica.MusicaGerencia;
import java.io.File;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author rudieri
 */
public class Scan {

    private static Integer tempo = 100;
    Thread thMonitor;

    public Scan(Object t) {
        if (t == null) {
            t = 30;
        }
        if (tempo == null) {
            tempo = Integer.parseInt(t.toString());
        }
        this.start();
    }

    public static void setPastas(ArrayList<String> dirs) {
        pastas = dirs;
    }

    public static ArrayList<String> getPastas() {
        return pastas;
    }

    public static void setTempo(Object t) {
        if (t == null) {
            t = 30;
        }
        tempo = Integer.parseInt(t.toString());
    }

    public static Integer getTempo() {
        return tempo;
    }

    

    private void start() {
        t = new Transacao();
        try {
            t.begin();
        } catch (Exception ex) {
            Logger.getLogger(Scan.class.getName()).log(Level.SEVERE, null, ex);
        }
        thMonitor = new Thread(new Runnable() {

            public void run() {
                trace("Começando comparação!");
                while (true) {
                    try {
                        Thread.sleep(tempo * 60000);
                        for (int i = 0; i < pastas.size(); i++) {
                            atualiza();
                            verifica(new File(pastas.get(i)), t);
                        }
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Scan.class.getName()).log(Level.SEVERE, null, ex);
                    }

                }

            }
        });
        thMonitor.start();
    }

    private void trace(Object o) {
        System.out.println(o);
    }

    private void verifica(File end, Transacao t) {
        if (end.isDirectory()) {
            trace("Dir: " + end.getAbsolutePath());
            try {
                Thread.sleep(150);
            } catch (InterruptedException ex) {
                Logger.getLogger(Scan.class.getName()).log(Level.SEVERE, null, ex);
            }
            File[] files=end.listFiles(new java.io.FileFilter() {

                   public boolean accept(File pathname) {
                        return MusicaGerencia.ehValido(pathname) || pathname.isDirectory();
                    }

                });

            for (int i = 0; i < files.length; i++) {
                verifica(files[i], t);
            }

        } else {
            if (!MusicaGerencia.ehValido(end)) {
                return;
            }

            if (musicas.indexOf(end.getAbsolutePath()) == -1) {

                MusicaGerencia.addFiles(end, t);
            } else {
                trace("já tem!");
            }
        }


    }

    private void atualiza() {
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
    private Transacao t;
    Timer tScan;
    ArrayList musicas;
    private static ArrayList<String> pastas = new ArrayList();
    File teste = new File("/home/rudieri/Música");
}
