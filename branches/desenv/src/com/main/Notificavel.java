/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.main;

import com.musica.Musiquera.PropriedadesMusica;

/**
 *
 * @author rudieri
 */
public interface  Notificavel {
    public void tempoEh(double  v);
    public void tempoEhHMS(String hms);
//    public void tempoTotalEhHMS(String hms);
    public void eventoNaMusica(int tipo);
    public void propriedadesMusicaChanged(PropriedadesMusica propriedadesMusica);
    public void atualizaLabels(String nome, int bits, String tempo, int freq) ;
}
