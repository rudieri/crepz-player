/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.utils.model.tablemodel;

import com.utils.campo.NomeCampo;
import java.lang.reflect.Field;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author rudieri
 */
public class ObjetoTabelaUtils {

    public static Object getValueAt(Object alvo, int coluna) {
        if (alvo != null) {

            Field[] campos = alvo.getClass().getDeclaredFields();
            int i = 0;
//            while (i < coluna) {
            for (int j = 0; j < campos.length; j++) {
                Field campo = campos[j];
                ObjetoTabela anotacao = campo.getAnnotation(ObjetoTabela.class);
                if (anotacao != null) {
                    if (anotacao.visivel()) {
                        if (i == coluna) {
                            try {
                                campo.setAccessible(true);
                                return campo.get(alvo);
                            } catch (IllegalArgumentException ex) {
                                Logger.getLogger(ObjetoTabelaUtils.class.getName()).log(Level.SEVERE, null, ex);
                            } catch (IllegalAccessException ex) {
                                Logger.getLogger(ObjetoTabelaUtils.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                        i++;
                    }
                }
            }
        }
        return "";
    }

    public static String getNomeColuna(Class alvo, int coluna) {
        if (alvo != null) {

            Field[] campos = alvo.getDeclaredFields();
            int i = 0;
//            while (i < coluna) {
            for (int j = 0; j < campos.length; j++) {
                Field campo = campos[j];
                ObjetoTabela anotacao = campo.getAnnotation(ObjetoTabela.class);
                if (anotacao != null) {
                    if (anotacao.visivel()) {
                        if (i == coluna) {
                            try {
                                NomeCampo nomeCampo = campo.getAnnotation(NomeCampo.class);
                                return nomeCampo == null ? campo.getName() : nomeCampo.nome();
                            } catch (IllegalArgumentException ex) {
                                Logger.getLogger(ObjetoTabelaUtils.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                        i++;
                    }
                }
            }
        }
        return "";
    }

    private ObjetoTabelaUtils() {
    }
}
