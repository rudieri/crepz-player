/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.config.constantes;

/**
 *
 * @author rudieri
 */
public enum TelaPadrao {
    J_PRINCIPAL("Tela Inicial"),
    J_MINI("Modo Miniatura"),
    J_FILA("Fila de Reprodução");

    private final  String nomeFake;

    private TelaPadrao(String nomeFake) {
        this.nomeFake = nomeFake;
    }

    public static String[] getNomesFakes() {
        TelaPadrao[] values = values();
        String[] nomes = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            TelaPadrao acaoPadraoFila = values[i];
            nomes[i] = acaoPadraoFila.nomeFake;
        }
        return nomes;
    }

    public String getNomeFake() {
        return nomeFake;
    }

}
