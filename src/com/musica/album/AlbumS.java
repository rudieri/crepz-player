package com.musica.album;

import com.musica.MusicaGerencia;
import com.musica.MusicaS;
import com.musica.autor.AutorS;
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
public class AlbumS implements Serializable, Comparable<AlbumS>, StringComparable {

    private static final long serialVersionUID = 2L;

    @ObjetoTabela()
    @NomeCampo(nome = "Album")
    private String nome;
    @ObjetoTabela()
    @NomeCampo(nome = "Genero")
    private String genero;
    private final ArrayList<MusicaS> musicas;
    private String img;
    @ObjetoTabela(temFilhos = true)
    private AutorS autor;

    public AlbumS() {
        musicas = new ArrayList<MusicaS>();
    }

    @SuppressWarnings("ReturnOfCollectionOrArrayField")
    public ArrayList<MusicaS> getMusicas() {
        return musicas;
    }

    public MusicaS getMusica(String nomeMusica) {
        return getMusica(nomeMusica, true);
    }

    public MusicaS getMusica(String nomeMusica, boolean criar) {
        nomeMusica = MusicaGerencia.removeCaracteresEsp(nomeMusica);
        if (nomeMusica == null) {
            if (criar) {
                return new MusicaS();
            } else {
                return null;
            }
        }
        MusicaS musica = PortaCDs.Busca.buscar(musicas, nomeMusica);
        if (criar && musica == null) {
            musica = new MusicaS();
            musica.setNome(nomeMusica);
            musica.setAlbum(this);
            musicas.add(musica);
        }
        return musica;
    }

    public void addMusica(MusicaS musicaS) {
        musicaS.setAlbum(this);
        musicas.add(musicaS);
    }

    public void removeMusica(MusicaS musicaS) {
        musicas.remove(musicaS);
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = MusicaGerencia.removeCaracteresEsp(nome);
    }

    public AutorS getAutor() {
        return autor;
    }

    public void setAutor(AutorS autor) {
        this.autor = autor;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        if (genero != null) {
            String normalizeGenero = MusicaGerencia.removeCaracteresEsp(genero);
            if (normalizeGenero.indexOf('(') == 0) {
                try {
                    String replace = normalizeGenero.replaceAll("[^0-9]", "");
                    setGenero(new Integer(replace));
                } catch (NumberFormatException ex) {
                    ex.printStackTrace(System.err);
                }
            } else {
                this.genero = normalizeGenero;
            }
        }
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

    /**
     * @return the img
     */
    public String getImg() {
        return img;
    }

    /**
     * Obs: Faça um replace('\\','/') em na hora de importar
     *
     * @param img O endereço da imagem
     */
    public void setImg(String img) {
        this.img = img;
    }

    @Override
    public String toString() {
        return "Album{" + "nome=" + nome + ", autor=" + autor + '}';
    }

    @Override
    public int compareTo(AlbumS o) {
        return o == null ? 1 : nome.compareTo(o.nome);
    }

    @Override
    public int compareTo(String o) {
        return o == null ? 1 : nome.compareTo(o);
    }
    
}
