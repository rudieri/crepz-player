/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.playlist.listainteligente.condicao;

import com.musica.MusicaS;
import com.playlist.listainteligente.condicao.operadores.Operador;
import com.playlist.listainteligente.condicao.operadores.OperadorComparativo;
import com.playlist.listainteligente.condicao.operadores.OperadorLogico;
import java.io.Serializable;

/**
 *
 * @author rudieri
 */
public class Condicao implements Serializable{
    private static final long serialVersionUID = 2L;

    private Operador operador;
    private Object valor1;
    private Object valor2;

    public Condicao() {
    }

   

    public void setValoresCondicao(OperadorLogico operadorLogico, Condicao condicao1, Condicao condicao2) {
        this.operador = operadorLogico;
        this.valor1 = condicao1;
        this.valor2 = condicao2;
    }

    public void setValoresCondicao(OperadorComparativo operadorComparativo, ValorCondicao valor1, ValorCondicao valor2) {
        this.operador = operadorComparativo;
        this.valor1 = valor1;
        this.valor2 = valor2;
    }

    protected Operador getOperador() {
        return operador;
    }

    protected TipoValorCondicao getTipoValorCondicao1() {
        if (valor1.getClass() == Condicao.class) {
            return TipoValorCondicao.CONDICAO;
        } else {
            return ((ValorCondicao) valor1).getTipoValorCondicao();
        }
    }

    protected Object getValor1() {
        return valor1;
    }
//
//    protected Object getValor1ToBD() {
//        if (valor1.getClass() == Condicao.class) {
//            return String.valueOf(((Condicao) valor1).getId());
//        } else {
//            return ((ValorCondicao) valor1).toBD();
//        }
//    }
//
//    protected Object getValor2ToBD() {
//        if (valor1.getClass() == Condicao.class) {
//            return String.valueOf(((Condicao) valor2).getId());
//        } else {
//            return valor2.toString();
//        }
//    }

    protected TipoValorCondicao getTipoValorCondicao2() {
        if (valor1.getClass() == Condicao.class) {
            return TipoValorCondicao.CONDICAO;
        } else {
            return ((ValorCondicao) valor2).getTipoValorCondicao();
        }
    }

    protected Object getValor2() {
        return valor2;
    }

//    protected void setPlaylist(Playlist playlist) {
//        this.playlist = playlist;
//    }
//
//    protected Playlist getPlaylist() {
//        return playlist;
//    }

    public boolean resolver(MusicaS musica) {
        return operador.resolverOperacao(valor1, valor2, musica);
    }

    @Override
    public String toString() {
        return "(" + valor1.toString() + " " + operador.getRepresentacao() + " " + valor2.toString() + ")";
    }
}
