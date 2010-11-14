/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.config;

import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author rudieri
 */
public class Centro {

    public Centro(){
        try {
            ConfiguracaoSC aaaaaa=new ConfiguracaoSC();
            aaaaaa.parteChave=null;
            all = ConfiguracaoBD.listar(aaaaaa);
        } catch (Exception ex) {
            Logger.getLogger(Centro.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public Object retorna(String chave){
        return all.get(chave);
    }

    HashMap<String, String> all;
}
