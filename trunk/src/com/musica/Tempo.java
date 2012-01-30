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
public class Tempo implements Serializable{

    private int milissegundos;

    public Tempo(int milissegundos) {
        this.milissegundos = milissegundos;
    }

    public Tempo(long microssegundos) {
        this.milissegundos = (int) (microssegundos / 1000);
    }

    public void setMilissegundos(int milissegundos) {
        this.milissegundos = milissegundos;
    }

    public void setSegundos(int segundos) {
        this.milissegundos = segundos * 1000;
    }

    public void setMicrossegundos(long microssegundos) {
        this.milissegundos = (int) (microssegundos / 1000);
    }

    public int getMilissegundos() {
        return milissegundos;
    }

    @Override
    public String toString() {
        int segundos = milissegundos / 1000;
        int horas = segundos / 3600;
        segundos -= horas * 3600;
        int minutos = segundos / 60;
        segundos -= minutos * 60;
        String hora;
        String minuto;
        String segundo;
        if (horas > 0) {
            if (horas > 9) {
                hora = String.valueOf(horas);
            } else {
                hora = "0" + String.valueOf(horas);
            }
            hora += ":";
        } else {
            hora = "";
        }
        if (minutos > 9) {
            minuto = String.valueOf(minutos);
        } else {
            minuto = "0" + String.valueOf(minutos);
        }
        minuto += ":";
        if (segundos > 9) {
            segundo = String.valueOf(segundos);
        } else {
            segundo = "0" + String.valueOf(segundos);
        }
        return hora + minuto + segundo;
    }

    public static void main(String[] args) {
        System.out.println(new Tempo(35990));
    }
}
