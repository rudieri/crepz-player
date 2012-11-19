/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.musica;

import com.utils.listaencadeada.ListaEncadeada;

/**
 *
 * @author rudieri
 */
public class LinhaDoTempo {

    private static final ListaEncadeada<Musica> listaEncadeada;
    private static int posicao;
    private static boolean ativa;

    static {
        listaEncadeada = new ListaEncadeada<Musica>();
        posicao = 0;
        ativa = true;
    }

    public static void adicionarNaPosicaoAtual(Musica musica) {
        if (!ativa) {
            return;
        }
        if (posicao == listaEncadeada.tamanho()) {
            adicionar(musica);
            return;
        }
        listaEncadeada.inserir(musica, posicao);
        posicao++;
    }

    public static void adicionar(Musica musica) {
        if (!ativa) {
            return;
        }
        listaEncadeada.adicionar(musica);
    }

    public static Musica getProxima() {
        if (!ativa) {
            return null;
        }
        if (++posicao >= 0 && posicao < listaEncadeada.tamanho()) {
            if (posicao - 1 == listaEncadeada.getPosicaoAtualIterador()) {
                return listaEncadeada.next();
            } else {
                return listaEncadeada.getValorA(posicao);
            }
        } else {
            posicao = Math.max(0, posicao);
            posicao = Math.min(listaEncadeada.tamanho() - 1, posicao);
            return null;
        }
    }

    public static Musica getEstaMesmo() {
        if (!ativa) {
            return null;
        }
        if (posicao >= 0 && posicao < listaEncadeada.tamanho()) {
            return listaEncadeada.getValorA(posicao);
        } else {
            return null;
        }
    }

    public static Musica getAnterior() {
        if (!ativa) {
            return null;
        }
        if (--posicao >= 0 && posicao < listaEncadeada.tamanho()) {
            if (posicao + 1 == listaEncadeada.getPosicaoAtualIterador()) {
                return listaEncadeada.prev();
            } else {
                return listaEncadeada.getValorA(posicao);
            }
        } else {
            posicao = Math.max(0, posicao);
            posicao = Math.min(listaEncadeada.tamanho() - 1, posicao);
            return null;
        }
    }

    public static boolean isAtiva() {
        return ativa;
    }

    public static void setAtiva(boolean ativa) {
        LinhaDoTempo.ativa = ativa;
    }

    private LinhaDoTempo() {
    }
}
