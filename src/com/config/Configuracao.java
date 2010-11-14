/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.config;

import java.util.HashMap;

/**
 *
 * @author -moNGe_
 */
public class Configuracao {
    private String chave;
    private String valor;

    //Configuracoes
    private static HashMap<String, String> configuracoes;

    /**
     * @return the chave
     */
    public String getChave() {
        return chave;
    }

    /**
     * @param chave the chave to set
     */
    public void setChave(String chave) {
        this.chave = chave;
    }

    /**
     * @return the valor
     */
    public String getValor() {
        return valor;
    }

    /**
     * @param valor the valor to set
     */
    public void setValor(String valor) {
        this.valor = valor;
    }

        /**
     * @return the configuracoes
     */
    public static HashMap<String, String> getConfiguracoes() {
        return configuracoes;
    }

    /**
     * @param aConfiguracoes the configuracoes to set
     */
    public static void setConfiguracoes(HashMap<String, String> aConfiguracoes) {
        configuracoes = aConfiguracoes;
    }


}
