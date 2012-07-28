/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.utils.transferivel;

import java.awt.datatransfer.DataFlavor;
import java.io.Serializable;

/**
 *
 * @author rudieri
 */
public enum TipoTransferenciaMusica implements Serializable {

    JFILA_MUSICA("crepz.fila-musica/array-musica"),
    JFILA_FILA("crepz.fila-fina/array-musica"),
    JPLAY_LIST("crepz.playlist/array-musica");
    private String nome;

    private TipoTransferenciaMusica(String nome) {
        this.nome = nome;
    }

    public String getNome() {
        return nome;
    }

    public static TipoTransferenciaMusica forDataFlavor(DataFlavor[] dataFlavors) {
        for (DataFlavor dataFlavor : dataFlavors) {
            TipoTransferenciaMusica forDataFlavor = forDataFlavor(dataFlavor);
            if (forDataFlavor != null) {
                return forDataFlavor;
            }
        }
        return null;
    }

    public static TipoTransferenciaMusica forDataFlavor(DataFlavor dataFlavor) {
        return forName(dataFlavor.getHumanPresentableName());
    }

    public static TipoTransferenciaMusica forName(String nome) {
        for (int i = 0; i < values().length; i++) {
            TipoTransferenciaMusica valor = values()[i];
            if (valor.getNome().equals(nome)) {
                return valor;
            }
        }
        return null;
    }
}
