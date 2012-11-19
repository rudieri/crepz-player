/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.copiador;

/**
 *
 * @author rudieri
 */
public enum TipoEstruturaDestino {
    SEM_ESTRUTURA_DIRETORIO("Sem estrutura de diretório"),
    ESTRUTURA_DADOS_MUSICA("Baseado nos dados da música"),
    ESTRUTURA_ORIGINAL("Baseado na estrutura original"),
   ; 
    private final String nome;

    private TipoEstruturaDestino(String nome) {
        this.nome = nome;
    }

    @Override
    public String toString() {
        return nome;
    }
    
    
    
}
