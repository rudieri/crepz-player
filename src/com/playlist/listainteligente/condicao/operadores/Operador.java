/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.playlist.listainteligente.condicao.operadores;

import com.musica.Musica;

/**
 *
 * @param <E> Um Valor ou Uma condicao
 * @author rudieri
 */
public interface Operador<E> {
    public boolean resolverOperacao(E valor1, E valor2, Musica musica);
    public String getRepresentacao();
}
