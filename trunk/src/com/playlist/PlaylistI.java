
package com.playlist;

import com.musica.MusicaS;
import com.utils.StringComparable;
import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author c90
 */
public interface PlaylistI extends Serializable, StringComparable{
    public String getNome();
    public void setNome(String nome);
    public ArrayList<MusicaS> getMusicas();
    public int getNroMusicas();
    public TipoPlayList getTipoPlayList();
}
