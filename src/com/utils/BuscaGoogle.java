/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.utils;

import com.musica.Musica;
import gsearch.Client;
import gsearch.Result;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import org.farng.mp3.MP3File;

/**
 *
 * @author manchini
 */
public class BuscaGoogle {

    /**
     * Busca Imagens Diretamente no Google
     * @param busca
     * @return
     */
    public static List<Result> buscaImagens(String busca) {
        Client c = new Client();
        return c.searchImages(busca);
    }
    public static File getAquivoBuscaImagens(Musica musica) throws Exception{
        File retorno = null;
        List<Result> lista = buscaImagens(musica.getAlbum()+" "+musica.getAutor());
        if(!lista.isEmpty()){
             URL link = new URL(lista.get(0).getUrl());
             File musicaF = new File(musica.getCaminho());
             File destino = new File(musicaF.getAbsolutePath().replace(musicaF.getName(), musica.getAlbum()+".jpg"));
             InputStream in = link.openStream();
                FileOutputStream out = new FileOutputStream(destino);
                byte[] buf = new byte[4 * 1024]; // 4K buffer
                int bytesRead;
                while ((bytesRead = in.read(buf)) != -1) {
                    out.write(buf, 0, bytesRead);
                }
                out.flush();
                out.close();
                retorno = destino;
        }

        return retorno;
    }
}
