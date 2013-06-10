/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.musica;

/**
 *
 * @author manchini
 */
public class MusicaSC {

    private String nome;
    private String album;
    private String autor;
    private String genero;
    private String caminho;
    /**
     * @return the nome
     */
    public String getNome() {
        return nome;
    }

    /**
     * @param nome the nome to set
     */
    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setCaminho(String c){
        caminho=c;
    }
    public String getCaminho(){
        return caminho;
    }

    /**
     * @return the album
     */
    public String getAlbum() {
        return album;
    }

    /**
     * @param album the album to set
     */
    public void setAlbum(String album) {
        this.album = album;
    }

    /**
     * @return the autor
     */
    public String getAutor() {
        return autor;
    }

    /**
     * @param autor the autor to set
     */
    public void setAutor(String autor) {
        this.autor = autor;
    }
    /**
     @param genero da misica.
     */
    public void setGenero(String g){
        this.genero=g;
    }

    /**Retorna o genero*/
    public String getGenero(){
        return genero;
    }
}
