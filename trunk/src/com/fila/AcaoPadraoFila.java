/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fila;

/**
 *
 * @author rudieri
 */
public enum AcaoPadraoFila {
    ADICIONAR_FILA("Adicionar na Fila"),
    REPRODUZIR("Reproduzir");

    private final  String nomeFake;

    private AcaoPadraoFila(String nomeFake) {
        this.nomeFake = nomeFake;
    }

    public static String[] getNomesFakes() {
        AcaoPadraoFila[] values = values();
        String[] nomes = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            AcaoPadraoFila acaoPadraoFila = values[i];
            nomes[i] = acaoPadraoFila.nomeFake;
        }
        return nomes;
    }

    public String getNomeFake() {
        return nomeFake;
    }

}
