/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.config;

import com.utils.FileUtils;
import java.io.File;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author rudieri
 */
public class ConfigFile {

    HashMap<String, String> mapa = new HashMap<String, String>();
    String total = "";
    String grava="";
    public static final String NOVA_LINHA = "<br>";
    public static final String SEPARADOR = "<gt>";
    private FileUtils fu;

    public void incluir(String chave, String valor) {
        grava += chave + SEPARADOR + valor + NOVA_LINHA;
    }

    public boolean gravar() {
        fu = new FileUtils();
        File mk = new File(".crepzConf");
        if (!mk.exists()) {
            mk.mkdir();
        }
        File f = new File(mk.getAbsolutePath() + "/config.crpz");
        System.out.println("Grav: " + f.getAbsoluteFile());
        try {
            return fu.gravaArquivoCodificacao(new StringBuffer(grava), f.getAbsolutePath(), "UTF-8", false);

        } catch (Exception ex) {
            return false;

        }
    }

//    private void trace(Object ob) {
//        System.out.println(ob.toString());
//    }
    public boolean read() {
        try {
            File mk = new File(".crepzConf");
            if (!mk.exists()) {
                mk.mkdir();
            }
            File f = new File(".crepzConf/config.crpz");
            if (!f.exists()) {
                f.createNewFile();
               
            } 
            System.out.println("local: " +f.getAbsoluteFile());
          total= new String( fu.leArquivoCodificacao(f, "UTF-8"));
            String[] lines = total.split(NOVA_LINHA);
            for (int i = 0; i < lines.length; i++) {
                String string[] = lines[i].split(SEPARADOR);
                if(string.length<2){
                    return true;
                }
                mapa.put(string[0], string[1]);
            }
            return true;
        } catch (Exception ex) {
            Logger.getLogger(ConfigFile.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    public String recuperar(String chave) {
        return mapa.get(chave);
    }
}
