/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com;

import com.conexao.SQL;
import com.conexao.Transacao;
import com.musica.MusicaGerencia;
import java.io.File;
import java.io.FileFilter;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Timer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author rudieri
 */
public class Scan {

    private static int tempo;
    Thread thMonitor;
    private final int ESCALA_TEMPO = 60000;

    public Scan() {
        tempo = 30;
        }

    public Scan(int t) {

        tempo = t;
        this.start();
    }

    public static void setPastas(ArrayList<String> dirs) {
        System.out.println("Novs locais definidos para o scan:");
        for (int i = 0; i < dirs.size(); i++) {
            System.out.println("L" + i + ": " + dirs.get(i));
        }
        pastas = dirs;
    }

    public static ArrayList<String> getPastas() {
        return pastas;
    }

    public static void setTempo(int t) {

        tempo = t;
        System.out.println("Novo tepo: " + tempo);
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

            @SuppressWarnings("SleepWhileInLoop")
            public void run() {
                trace("Começando comparação!");
                while (true) {
                    try {
                        System.out.println("Esperando: " + tempo + " min.");
                        Thread.sleep(tempo * ESCALA_TEMPO);
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
                Thread.sleep(170);
            } catch (InterruptedException ex) {
                Logger.getLogger(Scan.class.getName()).log(Level.SEVERE, null, ex);
            }
            File[] files = end.listFiles(new FileFilter() {

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
        musicas = new ArrayList(1000);
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
    private static ArrayList<String> pastas = new ArrayList(32);
    File teste = new File("/home/rudieri/Música");
}
