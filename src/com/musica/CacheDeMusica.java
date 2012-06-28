/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.musica;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author rudieri
 */
public class CacheDeMusica {

    private static HashMap<Integer, Musica> cache;

    static {
        cache = new HashMap<Integer, Musica>(5000);
    }

    public static Musica get(Integer id) {
        return get(id, null);
    }
    public static Musica get(Integer id, ResultSet rs) {
        Musica musica = cache.get(id);
        if (musica == null) {
            try {
                musica = new Musica();
                musica.setId(id);
                if (rs==null) {
                    MusicaBD.carregar(musica);
                }else{
                    MusicaBD.carregarObjeto(musica, rs);
                }
                System.out.println("Musica não encontrada em cache, carregar " + musica + " com a chave: " + id);
            } catch (Exception ex) {
                Logger.getLogger(CacheDeMusica.class.getName()).log(Level.SEVERE, null, ex);
            }
        }else{
            System.out.println("Musica encontrada em cache " + musica + " com a chave: " + id);
        }
        return musica;
    }

    public static void adicionar(Musica musica) {
        if (musica == null) {
            throw new IllegalStateException("Agrumento não pode ser nulo.");
        }
        if (cache.containsKey(musica.getId())) {
            System.out.println("Música ja tinha na cache " + musica + " com a chave: " + musica.getId());
        } else {
            System.out.println("Musica adicionada na cache " + musica + " com a chave: " + musica.getId());

        }
        cache.put(musica.getId(), musica);
    }

    public static void remover(Musica musica) {
        cache.remove(musica.getId());
    }

    public static Musica remover(Integer id) {
        return cache.remove(id);
    }

    public static void limpar() {
        cache.clear();
    }
}
