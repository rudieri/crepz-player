/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.playmusica;

/**
 *
 * @author -moNGe_
 */
public class PlayMusica {
    private Integer id;
    private com.playlist.Playlist playlist;
    private com.musica.Musica musica;
    private Integer seq;

    /**
     * @return the id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * @return the playlist
     */
    public com.playlist.Playlist getPlaylist() {
        return playlist;
    }

    /**
     * @param playlist the playlist to set
     */
    public void setPlaylist(com.playlist.Playlist playlist) {
        this.playlist = playlist;
    }

    /**
     * @return the musica
     */
    public com.musica.Musica getMusica() {
        return musica;
    }

    /**
     * @param musica the musica to set
     */
    public void setMusica(com.musica.Musica musica) {
        this.musica = musica;
        this.id=musica.getId();
    }

    /**
     * @return the seq
     */
    public Integer getSeq() {
        return seq;
    }

    /**
     * @param seq the seq to set
     */
    public void setSeq(Integer seq) {
        this.seq = seq;
    }

    

}
