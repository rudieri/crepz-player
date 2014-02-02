package com.musica.autor;

import com.musica.MusicaGerencia;
import com.musica.album.AlbumS;
import com.utils.campo.NomeCampo;
import com.utils.model.tablemodel.ObjetoTabela;
import java.io.Serializable;
import java.util.HashMap;

/**
 *
 * @author c90
 */
public class AutorS implements Serializable{
    private static final long serialVersionUID = 2L;
    @ObjetoTabela()
    @NomeCampo(nome = "Autor")
    private String nome;
    private final HashMap<String, AlbumS> albuns;

    public AutorS() {
        this.albuns = new HashMap<String, AlbumS>();
    }
    
    
    
    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = MusicaGerencia.removeCaracteresEsp(nome);
    }

    public void addAlbum(AlbumS album){
        album.setAutor(this);
        albuns.put(album.getNome(), album);
    }
    public void removeAlbum(AlbumS album){
        albuns.remove(album.getNome());
    }
    public AlbumS getAlbum(String nomeAlbum){
        return getAlbum(nomeAlbum, true);
    }
    public AlbumS getAlbum(String nomeAlbum, boolean criar){
        nomeAlbum = MusicaGerencia.removeCaracteresEsp(nomeAlbum);
        AlbumS album = albuns.get(nomeAlbum);
        if (criar && album == null) {
            album = new AlbumS();
            album.setNome(nomeAlbum);
            album.setAutor(this);
            albuns.put(nomeAlbum, album);
        }
        return album;
    }
    public HashMap<String, AlbumS> getAlbuns(){
        return albuns;
    }
    

    @Override
    public String toString() {
        return "Autor{" + "nome=" + nome + '}';
    }
    
    
}
