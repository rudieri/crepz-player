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
    private Integer id;
    private String nome;
    private Integer nrMusicas;

    /**
     * @return the id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Integer id) {
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
    public Integer getNrMusicas() {
        return nrMusicas;
    }

    /**
     * @param nrMusicas the nrMusicas to set
     */
    public void setNrMusicas(Integer nrMusicas) {
        this.nrMusicas = nrMusicas;
    }

}
