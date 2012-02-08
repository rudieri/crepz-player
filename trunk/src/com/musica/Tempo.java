/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.musica;

import java.io.Serializable;

/**
 *
 * @author rudieri
 */
public class Tempo implements Serializable {

    private final int milissegundos;
    private String string;


    public Tempo(int milissegundos) {
        this.milissegundos = milissegundos;
        convertToString();
    }

    public Tempo(long microssegundos) {
        this((int) (microssegundos / 1000));
    }

    public int getMilissegundos() {
        return milissegundos;
    }

    @Override
    public String toString() {
        return string;
    }

    private void convertToString() {
        int segundos = milissegundos / 1000;
        int horas = segundos / 3600;
        segundos -= horas * 3600;
        int minutos = segundos / 60;
        segundos -= minutos * 60;
        StringBuilder tempo = new StringBuilder(8);
        if (horas > 0) {
            if (horas > 9) {
                tempo.append(horas);
            } else {
                tempo.append('0').append(horas);
            }
            tempo.append(':');
        }
        if (minutos > 9) {
            tempo.append(minutos);
        } else {
            tempo.append('0').append(minutos);
        }
        tempo.append(':');
        if (segundos > 9) {
            tempo.append(segundos);
        } else {
            tempo.append('0').append(segundos);
        }
        string = tempo.toString();
    }
}
