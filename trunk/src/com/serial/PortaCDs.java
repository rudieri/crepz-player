package com.serial;

import com.config.Configuracoes;
import com.musica.MusicaGerencia;
import com.musica.MusicaS;
import com.musica.album.AlbumS;
import com.musica.autor.AutorS;
import com.playlist.PlaylistI;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JOptionPane;

/**
 *
 * @author c90
 */
public class PortaCDs {

    private static final HashMap<String, AutorS> autores;// = new HashMap<String, AutorS>();
    private static final HashMap<String, PlaylistI> playlists;// = new HashMap<String, PlaylistI>();

    static {
        HashMap<String, AutorS> autoresAux = null;
        HashMap<String, PlaylistI> playListAux = null;
        try {
            autoresAux = (HashMap<String, AutorS>) abrir(new File(Configuracoes.FILE_BD_MUSICAS.getValor()));
            playListAux = (HashMap<String, PlaylistI>) abrir(new File(Configuracoes.FILE_BD_PLAYLISTS.getValor()));
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
        autores = autoresAux == null ? new HashMap<String, AutorS>() : autoresAux;
        playlists = playListAux == null ? new HashMap<String, PlaylistI>() : playListAux;
    }

    public static void salvar() {
        salvarMusicas();
        salvarListas();
    }

    public static void salvarMusicas() {
        try {
            persistir(autores, new File(Configuracoes.FILE_BD_MUSICAS.getValor()));
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "Erro ao salvar listas de reprodução.");
            ex.printStackTrace(System.err);
        }
    }

    public static void salvarListas() {
        try {
            persistir(playlists, new File(Configuracoes.FILE_BD_PLAYLISTS.getValor()));
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "Erro ao salvar listas de reprodução.");
            ex.printStackTrace(System.err);
        }

    }

    private static void persistir(Serializable dados, File arquivo) throws FileNotFoundException, IOException {
        FileOutputStream fos = new FileOutputStream(arquivo);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(dados);
        oos.close();
        fos.close();
    }

    private static Object abrir(File arquivo) throws FileNotFoundException, IOException, ClassNotFoundException {
        FileInputStream fis = new FileInputStream(arquivo);
        ObjectInputStream ois = new ObjectInputStream(fis);
        return ois.readObject();
    }

    public static AutorS getAutor(String nome) {
        return getAutor(nome, true);
    }

    public static AutorS getAutor(String nome, boolean criar) {
        nome = MusicaGerencia.removeCaracteresEsp(nome);
        AutorS autor = autores.get(nome);
        if (criar && autor == null) {
            autor = new AutorS();
            autor.setNome(nome);
            autores.put(nome, autor);
        }
        return autor;
    }

    public static AutorS removerAutor(AutorS autor) {
        return autores.remove(autor.getNome());
    }

    public static ArrayList<MusicaS> getMusicas() {
        ArrayList<MusicaS> musicas = new ArrayList<MusicaS>();
        for (Map.Entry<String, AutorS> entry : autores.entrySet()) {
            AutorS autor = entry.getValue();
            for (Map.Entry<String, AlbumS> entryAlbum : autor.getAlbuns().entrySet()) {
                AlbumS album = entryAlbum.getValue();
                for (Map.Entry<String, MusicaS> entry1 : album.getMusicas().entrySet()) {
                    MusicaS musica = entry1.getValue();
                    musicas.add(musica);
                }
            }
        }
        return musicas;
    }
    
    public static ArrayList<AutorS> getAutores(){
        ArrayList<AutorS> lista = new ArrayList<AutorS>(autores.size());
        for (Map.Entry<String, AutorS> entry : autores.entrySet()) {
            AutorS autorS = entry.getValue();
            lista.add(autorS);
        }
        return lista;
    }

    public static ArrayList<MusicaS> listarMusicas(String nomeAutor, String nomeAlbum, String nomeMusica) {
        ArrayList<MusicaS> musicas = new ArrayList<MusicaS>();
        for (Map.Entry<String, AutorS> entry : autores.entrySet()) {
            AutorS autor = entry.getValue();
            if (autor.getNome().contains(nomeAutor)) {
                for (Map.Entry<String, AlbumS> entryAlbum : autor.getAlbuns().entrySet()) {
                    AlbumS album = entryAlbum.getValue();
                    if (album.getNome().contains(nomeAlbum)) {
                        for (Map.Entry<String, MusicaS> entry1 : album.getMusicas().entrySet()) {
                            MusicaS musica = entry1.getValue();
                            if (musica.getNome().contains(nomeMusica)) {
                                musicas.add(musica);
                            }
                        }
                    }
                }
            }

        }
        return musicas;

    }

    public static PlaylistI getPlaylist(String nome) {
        return getPlaylist(nome, false, null);
    }

    /**
     * Retorna a lista de reprodução contendo o nome informado.
     *
     * @param nome Nome da lista.
     * @param criar Caso a lista não exista, ela é criada. Porém é criada uma
     * lista normal e não uma automática.
     * @param tipoListaCriar
     * @return
     */
    public static PlaylistI getPlaylist(String nome, boolean criar, Class<? extends PlaylistI> tipoListaCriar) {
        nome = MusicaGerencia.removeCaracteresEsp(nome);
        PlaylistI playlist = playlists.get(nome);
        if (criar && playlist == null) {
            try {
                playlist = tipoListaCriar.newInstance();
                playlist.setNome(nome);
                playlists.put(nome, playlist);
            } catch (InstantiationException ex) {
                ex.printStackTrace(System.err);
            } catch (IllegalAccessException ex) {
                ex.printStackTrace(System.err);
            }
        }
        return playlist;
    }

    public static PlaylistI removerPlaylist(PlaylistI playlist) {
        return playlists.remove(playlist.getNome());
    }

    public static ArrayList<PlaylistI> listarPlayLists(String text) {
        ArrayList<PlaylistI> lista = new ArrayList<PlaylistI>();
        for (Map.Entry<String, PlaylistI> entry : playlists.entrySet()) {
            String nome = entry.getKey();
            if (nome.contains(text)) {
                PlaylistI playlistI = entry.getValue();
                lista.add(playlistI);
            }

        }
        return lista;
    }

    public static Long getMaxDtModArquivo(String nomeArq, boolean ehDiretorio) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private PortaCDs() {
    }
}
