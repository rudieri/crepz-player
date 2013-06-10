package com.musica;

import com.utils.campo.NomeCampo;
import com.utils.model.tablemodel.Filtravel;
import com.utils.model.tablemodel.ObjetoTabela;
import java.io.Serializable;

/**
 *
 * @author manchini
 */
public class Musica implements Serializable, Filtravel {

    private static final char ESPACO = ' ';
    private int id;
    @ObjetoTabela()
    @NomeCampo(nome = "Nome")
    private String nome;
    @ObjetoTabela()
    @NomeCampo(nome = "Autor")
    private String autor;
    @ObjetoTabela()
    @NomeCampo(nome = "Genero")
    private String genero;
    @ObjetoTabela()
    @NomeCampo(nome = "Album")
    private String album;
    @ObjetoTabela()
    @NomeCampo(nome = "Tempo")
    private Tempo tempo;
    @ObjetoTabela()
    @NomeCampo(nome = "Nº Reproduções")
    private short numeroReproducoes = 0;
    @NomeCampo(nome = "Caminho do Arquivo")
    private String caminho;
    private long dtModArquivo;
    private String img;
    private int number;
    /**Caso o arquivo da música não seja encontrado.*/
    @NomeCampo(nome = "Música Perdida")
    private boolean perdida = false;

    /**
     Limitar o número de objetos, para usar melhor a cache em {@link CacheDeMusica}
     */
    protected Musica() {
    }
    
    
    /**
     * @return the nome
     */
    public String getNome() {
        return nome;
    }

    /**
     * @param nome the nome to set
     */
    public void setNome(String nome) {
        if (nome == null) {
            this.nome = null;
        } else {
            this.nome = MusicaGerencia.removeCaracteresEsp(nome);
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

    public long getDtModArquivo() {
        return dtModArquivo;
    }

    public void setDtModArquivo(long dtModArquivo) {
        this.dtModArquivo = dtModArquivo;
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
                    ex.printStackTrace(System.err);
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
            this.album = null;
        }
    }

    /**
     * @return the caminho
     */
    public String getCaminho() {
        return caminho;
    }

    /**
     * @param caminho the caminho to set
     */
    public void setCaminho(String caminho) {

        this.caminho = caminho;
    }

    /**
     * @return the img
     */
    public String getImg() {
        return img;
    }

    /**
     * Obs: Faça um replace('\\','/') em nahorade importar
     *
     * @param img O endereço da imagem
     */
    public void setImg(String img) {
        this.img = img;
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
            b.printStackTrace(System.err);
        }
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
        if (obj != null && obj.getClass() == Musica.class) {
            return ((Musica) obj).getId() == id;
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
        sb.append(nome).append(ESPACO).append(album).append(ESPACO).append(autor).append(ESPACO).append(genero);
        return sb.toString().toLowerCase();
    }

    public short getNumeroReproducoes() {
        return numeroReproducoes;
    }

    public void setNumeroReproducoes(short numeroReproducoes) {
        this.numeroReproducoes = numeroReproducoes;
    }

    public boolean isPerdida() {
        return perdida;
    }

    public void setPerdida(boolean perdida) {
        this.perdida = perdida;
    }
    
}
