/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.playlist;

/**
 *
 * @author -moNGe_
 */
public class PlaylistSC {
    private String nome;
    private int ID=-1;

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
    public void setId(int id){
        ID=id;
    }
    public int getId(){
        return ID;
    }

}
