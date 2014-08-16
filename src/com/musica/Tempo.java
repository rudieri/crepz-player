package com.musica;

import java.io.Serializable;

/**
 *
 * @author rudieri
 */
public class Tempo implements Serializable {

    private static final long serialVersionUID = 2L;
    
    public static final Tempo TEMPO_ZERO = new Tempo(0);
    private final short segundos;
    private transient String string;


    public Tempo(short segundos) {
        this.segundos = segundos;
        convertToString();
    }
    public Tempo(int milissegundos) {
        this((short)(milissegundos/1000));
    }

    public Tempo(long microssegundos) {
        this((int) (microssegundos / 1000));
    }

    public short getSegundos() {
        return segundos;
    }
    public int getMilissegundos() {
        return segundos*1000;
    }

    /**
     Retorna a representação do tempo no formato hh:mm:ss, as horas ou minutos
     * podem ser suprimidos caso seus valores forem (0) zero
     * @return 
     */
    @Override
    public String toString() {
        if (string == null) {
            convertToString();
        }
        return string;
    }

    private void convertToString() {
        int segsAux = segundos;
        int horas = segsAux / 3600;
        segsAux -= horas * 3600;
        int minutos = segsAux / 60;
        segsAux -= minutos * 60;
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
        if (segsAux > 9) {
            tempo.append(segsAux);
        } else {
            tempo.append('0').append(segsAux);
        }
        string = tempo.toString();
    }
}
