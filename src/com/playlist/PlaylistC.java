package com.playlist;

import com.musica.MusicaS;
import com.playlist.listainteligente.condicao.Condicao;
import com.serial.PortaCDs;
import java.io.Serializable;
import java.util.ArrayList;

public class PlaylistC implements Serializable, PlaylistI {
    private static final long serialVersionUID = 2L;

    private String nome;
    private final ArrayList<Condicao> condicoes;
    private transient ArrayList<MusicaS> musicas = new ArrayList<MusicaS>();

    public PlaylistC() {
        this.condicoes = new ArrayList<Condicao>();
    }

    public void updateMusicas() {
        if (musicas == null) {
            musicas = new ArrayList<MusicaS>();
        }else{
            musicas.clear();
        }
        ArrayList<MusicaS> todasMusicas = PortaCDs.getMusicas();
        for (int i = 0; i < todasMusicas.size(); i++) {
            MusicaS musica = todasMusicas.get(i);
            boolean todasAsCondicoes = true;
            for (int j = 0; todasAsCondicoes && j < condicoes.size(); j++) {
                Condicao condicao = condicoes.get(j);
                todasAsCondicoes &= condicao.resolver(musica);
            }
            if (todasAsCondicoes) {
                musicas.add(musica);
            }
        }
    }

    /**
     * @return the nome
     */
    @Override
    public String getNome() {
        return nome;
    }

    /**
     * @param nome the nome to set
     */
    @Override
    public void setNome(String nome) {
        this.nome = nome;
    }

    /**
     * @return the nrMusicas
     */
    @Override
    public int getNroMusicas() {
        return musicas.size();
    }
    
    public void addCondicao(Condicao condicao){
        this.condicoes.add(condicao);
    }
    public void removeCondicao(Condicao condicao){
        this.condicoes.remove(condicao);
    }

    public ArrayList<Condicao> getCondicoes() {
        return condicoes;
    }

    @Override
    public TipoPlayList getTipoPlayList() {
        return TipoPlayList.INTELIGENTE;
    }

    public void addMusica(MusicaS musica) {
        addMusica(musica, musicas.size());
    }

    public void addMusica(MusicaS musica, int posicao) {
        musicas.add(posicao, musica);
    }

    @Override
    public ArrayList<MusicaS> getMusicas() {
        return musicas;
    }

    public void removeMusica(int posicao) {
        musicas.remove(posicao);
    }

    /**
     * Remove todas as ocorrências do objeto informado.
     *
     * @param musica Objeto que será removido
     */
    public void removeMusica(MusicaS musica) {
        for (int i = musicas.size(); i >= 0; i--) {
            if (musica.equals(musicas.get(i))) {
                musicas.remove(i);
            }
        }
    }

    @Override
    public int compareTo(String o) {
        return o == null ? 1 : nome.compareTo(o);
    }
}
