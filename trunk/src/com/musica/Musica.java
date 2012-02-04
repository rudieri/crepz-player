/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.musica;

import com.utils.model.Filtravel;
import com.utils.model.ObjetoTabela;
import java.io.Serializable;

/**
 *
 * @author manchini
 */
public class Musica implements Serializable, Filtravel {
    private static final char ESPACO = ' ';
    private int id;
    @ObjetoTabela(nomeColuna = "Nome")
    private String nome;
    @ObjetoTabela(nomeColuna = "Autor")
    private String autor;
    @ObjetoTabela(nomeColuna = "Genero")
    private String genero;
    @ObjetoTabela(nomeColuna = "Album")
    private String album;
    @ObjetoTabela(nomeColuna = "Tempo")
    private Tempo tempo;
    private String caminho;
    private String img;
    private int size;
    private int number;

    /**
     * @return the nome
     */
    public String getNome() {
        return nome;
    }

    /**
     * @param nome the nome to set
     */
    public boolean setNome(String nome) {
        if (nome == null || nome.isEmpty()) {
            return false;
        } else {
            this.nome = MusicaGerencia.removeCaracteresEsp(nome);
            return true;
        }
    }

    /**
     * @return the autor
     */
    public String getAutor() {
        return autor;
    }

    /**
     * @param autor the autor to set
     */
    public void setAutor(String autor) {
        this.autor = MusicaGerencia.removeCaracteresEsp(autor);
    }

    /**
     * @return the genero
     */
    public String getGenero() {
        return genero;
    }

    /**
     * @param genero the genero to set
     */
    public void setGenero(String genero) {
        if (genero != null) {
            String normalizeGenero = MusicaGerencia.removeCaracteresEsp(genero);
            if (normalizeGenero.indexOf('(') == 0) {
                try {
                    String replace = normalizeGenero.replaceAll("[^0-9]", "");
                    setGenero(new Integer(replace));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } else {
                this.genero = normalizeGenero;
            }
        }
    }

    /**
     * @return the Album
     */
    public String getAlbum() {
        return album;
    }

    /**
     * @param album the Album to set
     */
    public void setAlbum(String album) {
        if (album != null) {
            this.album = MusicaGerencia.removeCaracteresEsp(album);
        } else {
            this.album = "";
        }
    }

    /**
     * @return the caminho
     */
    public String getCaminho() {
        if (caminho != null) {
            return caminho.replace("<&aspas>", "'");
        } else {
            return null;
        }
    }

    /**
     * @param caminho the caminho to set
     */
    public void setCaminho(String caminho) {

        this.caminho = caminho.replace("\\", "/").replace("//", "/").replace("'", "<&aspas>");
    }

    /**
     * @return the img
     */
    public String getImg() {
        if (img != null) {
            return img.replace("<&aspas>", "'");
        } else {
            return "";
        }
    }

    /**
     * @param img the img to set
     */
    public void setImg(String img) {
        if (img != null) {
            this.img = img.replace("\\", "/").replace("//", "/").replace("'", "<&aspas>");
        }

    }

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }

    public void setGenero(int genre) {
        if (genre >= MusicaGerencia.generos.length || genre < 0) {
            genero = "";
            return;
        }
        try {
            genero = MusicaGerencia.generos[genre];
        } catch (Exception b) {
            genero = "";
            b.printStackTrace();
        }
    }

    public void setSize(int s) {
        this.size = s;
    }

    public int getSize() {
        return this.size;
    }

    public void setNumero(int n) {
        this.number = n;
    }

    public int getNumero() {
        return number;
    }

    public Tempo getTempo() {
        return tempo;
    }

    public void setTempo(Tempo tempo) {
        this.tempo = tempo;
    }

    @Override
    public String toString() {
        return nome;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof Musica) {
            return ((Musica) obj).id == id;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + this.id;
        return hash;
    }

    @Override
    public String getTextoParaPesquisa() {
        StringBuilder sb = new StringBuilder(100);
        sb.append(nome).append(ESPACO).append(album).append(ESPACO).append(autor).append(ESPACO).append(genero).append(ESPACO).append(tempo.toString());
        return sb.toString().toLowerCase();
    }
}
