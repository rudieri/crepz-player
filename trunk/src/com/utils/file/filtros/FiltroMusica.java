/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.utils.file.filtros;

import com.musica.MusicaGerencia;
import java.io.File;
import javax.swing.filechooser.FileFilter;

/**
 *
 * @author rudieri
 */
public class FiltroMusica extends FileFilter {
    private static final FiltroMusica FILTRO_MUSICA = new FiltroMusica();

    private FiltroMusica() {
    }
    
    @Override
    public boolean accept(File f) {
        return f.isDirectory() || MusicaGerencia.ehValido(f);
    }

    @Override
    public String getDescription() {
        return "Arquivos de músicas...";
    }
    public static  FiltroMusica getInstance(){
        return FILTRO_MUSICA;
    }
}
