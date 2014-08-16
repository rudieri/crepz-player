package com.musica.autor;

import com.musica.MusicaGerencia;
import com.musica.album.AlbumS;
import com.serial.PortaCDs;
import com.utils.StringComparable;
import com.utils.campo.NomeCampo;
import com.utils.model.tablemodel.ObjetoTabela;
import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author c90
 */
public class AutorS implements Serializable, Comparable<AutorS>, StringComparable {
    private static final long serialVersionUID = 2L;
    @ObjetoTabela()
    @NomeCampo(nome = "Autor")
    private String nome;
    private final ArrayList<AlbumS> albuns;

    public AutorS() {
        this.albuns = new ArrayList<AlbumS>();
    }
    
    
    
    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = MusicaGerencia.removeCaracteresEsp(nome);
    }

    public void addAlbum(AlbumS album){
        album.setAutor(this);
        albuns.add(album);
    }
    public void removeAlbum(AlbumS album){
        albuns.remove(album);
    }
    public AlbumS getAlbum(String nomeAlbum){
        return getAlbum(nomeAlbum, true);
    }
    public AlbumS getAlbum(String nomeAlbum, boolean criar){
        nomeAlbum = MusicaGerencia.removeCaracteresEsp(nomeAlbum);
        AlbumS album = PortaCDs.Busca.buscar(albuns, nomeAlbum);
        if (criar && album == null) {
            album = new AlbumS();
            album.setNome(nomeAlbum);
            album.setAutor(this);
            albuns.add(album);
        }
        return album;
    }
    @SuppressWarnings("ReturnOfCollectionOrArrayField")
    public ArrayList<AlbumS> getAlbuns(){
        return albuns;
    }
    

    @Override
    public String toString() {
        return "Autor{" + "nome=" + nome + '}';
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 79 * hash + (this.nome != null ? this.nome.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final AutorS other = (AutorS) obj;
        return !((this.nome == null) ? (other.nome != null) : !this.nome.equals(other.nome));
    }
    
    @Override
    public int compareTo(AutorS o) {
        return o == null ? 1 : nome.compareTo(o.nome);
    }
    @Override
    public int compareTo(String o) {
        return o == null ? 1 : nome.compareTo(o);
    }
    
}