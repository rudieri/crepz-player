/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com;

import com.conexao.SQL;
import com.conexao.Transacao;
import com.musica.Musica;
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


    public Scan(Object tempo){
        if(tempo==null){
            tempo=30;
        }
        this.start(Integer.parseInt(tempo.toString()));
    }
    public void setPastas(ArrayList pastas){
        this.pastas = pastas;
    }

    class taskScan extends TimerTask {

        @Override
        public void run() {
            trace("Começando comparação!");

            for(int i=0; i<pastas.size(); i++){
                atualiza();
                verifica((File)pastas.get(i), t);
            }
            tScan=null;
        }
    }

    private void start(int tempo)  {
        t = new Transacao(); 
        try {
            t.begin();
        } catch (Exception ex) {
            Logger.getLogger(Scan.class.getName()).log(Level.SEVERE, null, ex);
        }
        tempo=tempo*60000;
        if(tScan==null){
            tScan = new Timer();
            tScan.schedule(new taskScan(), 0, tempo);
        }
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

            if (musicas.indexOf(end.getAbsolutePath()) == -1) {

                Musica.addFiles(end, t);
            } else {
                trace("já tem!");
            }
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
   
    
    private Transacao t;
      Timer tScan;
    ArrayList musicas;
    ArrayList pastas = new ArrayList();
    File teste = new File("/home/rudieri/Música");
}
