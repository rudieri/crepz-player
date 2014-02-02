package com.utils.file;

import com.musica.MusicaGerencia;
import java.io.File;
import javax.swing.filechooser.FileFilter;

/**
 *
 * @author c90
 */
public class FiltroArquivoGenerico extends FileFilter implements java.io.FileFilter {

    public static final FiltroArquivoGenerico FILTRO_MUSICA = new FiltroArquivoGenerico(true, "Arquivos de Audio...", MusicaGerencia.extSuportadaMusica);
    public static final FiltroArquivoGenerico FILTRO_PLAYLIST = new FiltroArquivoGenerico(true, "Arquivos de PlayList *.m3u", ".m3u" );
    
    private final boolean aceitarDiretorio;
    private final String[] extensoes;
    private final String descricao;

    public FiltroArquivoGenerico(boolean aceitarDiretorio, String descricao, String... extensoes) {
        this.aceitarDiretorio = aceitarDiretorio;
        this.extensoes = extensoes;
        this.descricao = descricao;
    }
    
    @Override
    public boolean accept(File pathname) {
        if (aceitarDiretorio && pathname.isDirectory()) {
            return true;
        }
        for (String ext : extensoes) {
            if (pathname.getName().toLowerCase().endsWith(ext)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String getDescription() {
        return descricao;
    }

}
