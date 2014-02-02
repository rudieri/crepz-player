package com.playlist;

import com.musica.MusicaS;
import java.io.Serializable;
import java.util.ArrayList;

public class PlaylistS implements Serializable, PlaylistI {
    private static final long serialVersionUID = 2L;
    
    private String nome;
    private final ArrayList<MusicaS> musicas;
    

    public PlaylistS() {
        this.musicas = new ArrayList<MusicaS>();
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


    @Override
    public TipoPlayList getTipoPlayList() {
        return TipoPlayList.NORMAL;
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
    

}
