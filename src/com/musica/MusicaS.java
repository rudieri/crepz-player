package com.musica;

import com.musica.album.AlbumS;
import com.utils.StringComparable;
import com.utils.campo.NomeCampo;
import com.utils.model.tablemodel.Filtravel;
import com.utils.model.tablemodel.ObjetoTabela;
import java.io.Serializable;

/**
 *
 * @author manchini
 */
public class MusicaS implements Serializable, Filtravel, Comparable<MusicaS>, StringComparable {

    private static final long serialVersionUID = 2L;
    private transient static final char ESPACO = ' ';
    @ObjetoTabela()
    @NomeCampo(nome = "Nome")
    private String nome;
    @ObjetoTabela(temFilhos = true)
    private AlbumS album;
    @ObjetoTabela()
    @NomeCampo(nome = "Tempo")
    private Tempo tempo;
    @ObjetoTabela()
    @NomeCampo(nome = "Nº Reproduções")
    private short numeroReproducoes = 0;
    @NomeCampo(nome = "Caminho do Arquivo")
    private String nomeArquivo;
    private long dtModArquivo;
    private byte number;
    /**
     * Caso o arquivo da música não seja encontrado.
     */
    @NomeCampo(nome = "Música Perdida")
    private boolean perdida = false;

    /**
     * Limitar o número de objetos, para usar melhor a cache em
     * {@link CacheDeMusica}
     */
    public MusicaS() {
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

    public long getDtModArquivo() {
        return dtModArquivo;
    }

    public void setDtModArquivo(long dtModArquivo) {
        this.dtModArquivo = dtModArquivo;
    }

    /**
     * @param album the Album to set
     */
    public void setAlbum(AlbumS album) {
        this.album = album;
    }

    public AlbumS getAlbum() {
        return album;
    }

    /**
     * @return the caminho
     */
    public String getCaminho() {
//        StringBuilder caminho = new StringBuilder();
//        caminho.append(album.getAutor().getCaminho()).append('/').append(album.getNomeDiretorio()).append('/').append(nomeArquivo);
//        caminho.append(album.getNomeDiretorio()).append('/').append(nomeArquivo);
        return nomeArquivo;//.toString();
    }

    public String getNomeArquivo() {
        return nomeArquivo;
    }

    public void setNomeArquivo(String nomeArquivo) {
        this.nomeArquivo = nomeArquivo;
    }

    public void setNumero(byte n) {
        this.number = n;
    }

    public byte getNumero() {
        return number;
    }

    public Tempo getTempo() {
        return tempo == null ? Tempo.TEMPO_ZERO : tempo;
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
        if (obj == this) {
            return true;
        } else if (obj != null && obj.getClass() == MusicaS.class) {
            return ((MusicaS) obj).getCaminho().equals(nomeArquivo);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + this.getNomeArquivo().hashCode();
        return hash;
    }

    @Override
    public String getTextoParaPesquisa() {
        StringBuilder sb = new StringBuilder(100);
        sb.append(nome).append(ESPACO).append(album.getNome()).append(ESPACO);
        sb.append(album.getAutor().getNome()).append(ESPACO).append(album.getGenero());
        return sb.toString().toLowerCase();
    }

    @Override
    public int compareTo(MusicaS o) {
        return o == null ? 1 : nome.compareTo(o.nome);
    }

    @Override
    public int compareTo(String o) {
        return o == null ? 1 : nome.compareTo(o);
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
