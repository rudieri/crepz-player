
package com.playlist;

import com.musica.MusicaS;
import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author c90
 */
public interface PlaylistI extends Serializable{
    public String getNome();
    public void setNome(String nome);
    public ArrayList<MusicaS> getMusicas();
    public int getNroMusicas();
    public TipoPlayList getTipoPlayList();
}
