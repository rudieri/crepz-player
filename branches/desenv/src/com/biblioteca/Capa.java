/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.biblioteca;

/**
 *
 * @author rudieri
 */
public class Capa {

    private String img;
    private String titulo;
    private int qtd;

    public Capa(String img, String titulo, Integer qtd) {
        this.img = img;
        this.titulo = titulo;
        this.qtd = qtd;

    }

    public String getImg() {
        return img;
    }

    public int getQtd() {
        return qtd;
    }

    public String getTitulo() {
        return titulo;
    }
    
    
}
