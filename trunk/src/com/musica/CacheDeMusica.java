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
                if (rs == null) {
                    MusicaBD.carregar(musica);
                }else{
                    MusicaBD.carregarObjeto(musica, rs);
                }
            } catch (Exception ex) {
                Logger.getLogger(CacheDeMusica.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return musica;
    }

    public static void adicionar(Musica musica) {
        if (musica == null) {
            throw new IllegalStateException("Agrumento não pode ser nulo.");
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
