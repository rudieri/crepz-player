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
import java.net.URL;
import java.util.List;

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
        return c.searchImages(busca +"");
    }
    public static File getAquivoBuscaImagens(Musica musica) throws Exception{
        File retorno = null;
        List<Result> lista = buscaImagens(((musica.getAlbum()==null?"": musica.getAlbum())+
                                    " "+musica.getAutor()==null?"":musica.getAutor()+
                                    ""+musica.getNome()==null?"":musica.getNome()).replaceAll("  ", " ").trim());
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
