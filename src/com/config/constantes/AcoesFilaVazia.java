/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.config.constantes;

/**
 *
 * @author rudieri
 */
public enum AcoesFilaVazia {
    NADA("Não faça nada"),
    TOCAR_RANDOM("Tocar da lista ao lado (Aleatório)"),
    TOCAR_SEQ("Tocar da lista ao lado (Sequencial)"),
    ;
    private String nomeFake;
    

    private AcoesFilaVazia(String nome) {
        this.nomeFake = nome;
    }

    public String getNomeFake() {
        return nomeFake;
    }
     public static String[] getNomesFakes() {
        AcoesFilaVazia[] values = values();
        String[] nomes = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            AcoesFilaVazia acoesFilaVazia = values[i];
            nomes[i] = acoesFilaVazia.nomeFake;
        }
        return nomes;
    }
}
