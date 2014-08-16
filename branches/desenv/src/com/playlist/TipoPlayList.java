/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.playlist;

/**
 *
 * @author rudieri
 */
public enum TipoPlayList {
    NORMAL("Seleção manual"),
    INTELIGENTE("Automática (por condições)");
    private final String nome;

    private TipoPlayList(String nome) {
        this.nome = nome;
    }

    @Override
    public String toString() {
        return nome;
    }

    public String getNome() {
        return nome;
    }
}
