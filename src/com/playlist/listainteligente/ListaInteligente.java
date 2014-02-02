/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.playlist.listainteligente;

import com.musica.MusicaS;
import com.playlist.listainteligente.condicao.Condicao;
import java.util.ArrayList;

/**
 *
 * @author rudieri
 */
public class ListaInteligente {
    private ArrayList<Condicao> condicoes;
    private final String nome;

    public ListaInteligente(String nome) {
        this.nome = nome;
        condicoes = new ArrayList<Condicao>(10);
    }
    public void addCondicao(Condicao condicao){
        this.condicoes.add(condicao);
    }
    public ArrayList<MusicaS> filtrarLista(ArrayList<MusicaS> musicas){
        ArrayList<MusicaS> novaLista = new ArrayList<MusicaS>(500);
        for (int i = 0; i < musicas.size(); i++) {
            MusicaS musica = musicas.get(i);
            boolean add = true;
            for (int j = 0; add && j < condicoes.size(); j++) {
                Condicao condicao = condicoes.get(j);
                add &= condicao.resolver(musica);
            }
            if (add) {
                novaLista.add(musica);
            }
        }
        return novaLista;
    }

    @Override
    public String toString() {
        return nome;
    }
    
    
    
}
