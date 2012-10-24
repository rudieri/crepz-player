/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.utils.file.filtros;

import java.io.File;
import javax.swing.filechooser.FileFilter;

/**
 *
 * @author rudieri
 */
public class FiltroListaReproducao extends FileFilter {

    private static final FiltroListaReproducao FILTRO_LISTA_REPRODUCAO = new FiltroListaReproducao();

    @Override
    public boolean accept(File f) {
        return f.isDirectory() || f.getName().toLowerCase().endsWith(".m3u");
    }

    @Override
    public String getDescription() {
        return "Arquivos de PlayList *.m3u";
    }

    public static FiltroListaReproducao getInstance() {
        return FILTRO_LISTA_REPRODUCAO;
    }
}
