/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.playlist;

/**
 *
 * @author -moNGe_
 */
public class Playlist {
    private int id;
    private String nome;
    private TipoPlayList tipoPlayList;
    private int nrMusicas;

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }

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

    /**
     * @return the nrMusicas
     */
    public int getNrMusicas() {
        return nrMusicas;
    }

    /**
     * @param nrMusicas the nrMusicas to set
     */
    public void setNrMusicas(int nrMusicas) {
        this.nrMusicas = nrMusicas;
    }

    public TipoPlayList getTipoPlayList() {
        return tipoPlayList;
    }

    public void setTipoPlayList(TipoPlayList tipoPlayList) {
        this.tipoPlayList = tipoPlayList;
    }
    

}
