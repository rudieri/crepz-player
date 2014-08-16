package com.serial;

import com.config.Configuracoes;
import com.musica.MusicaGerencia;
import com.musica.MusicaS;
import com.musica.album.AlbumS;
import com.musica.autor.AutorS;
import com.playlist.PlaylistI;
import com.utils.StringComparable;
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

    private static final ArrayList<AutorS> autores;// = new HashMap<String, AutorS>();
    private static final ArrayList<PlaylistI> playlists;// = new HashMap<String, PlaylistI>();
    // Musicas: 903,4 Kb
    // Musicas2: 821,7 Kb
    // Musicas2: 821,6 Kb
    // Musicas2: 772,1 Kb
    // Playlists: 659 Kb

    static {
        Object autoresAux = null;
        Object playListAux = null;
        try {
            autoresAux = abrir(new File(Configuracoes.FILE_BD_MUSICAS.getValor()));
            playListAux = abrir(new File(Configuracoes.FILE_BD_PLAYLISTS.getValor()));
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
        if (autoresAux == null) {
            autores = new ArrayList<AutorS>();
        } else if (autoresAux instanceof HashMap) {
            autores = new ArrayList<AutorS>();
            autores.addAll(((Map<? extends String, ? extends AutorS>) autoresAux).values());
        } else {
            autores = (ArrayList<AutorS>) autoresAux;
        }
        if (playListAux == null) {
            playlists = new ArrayList<PlaylistI>();
        } else if (playListAux instanceof HashMap) {
            playlists = new ArrayList<PlaylistI>();
            playlists.addAll(((Map<? extends String, ? extends PlaylistI>) playListAux).values());
        } else {
            playlists = (ArrayList<PlaylistI>) playListAux;
        }
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
        AutorS autor = Busca.buscar(autores, nome);
//        AutorS autor = autores.get(nome);
        if (criar && autor == null) {
            autor = new AutorS();
            autor.setNome(nome);
            autores.add(autor);
//            autores.put(nome, autor);
        }
        return autor;
    }

    public static boolean removerAutor(AutorS autor) {
        return autores.remove(autor);
    }

    public static ArrayList<MusicaS> getMusicas() {
        ArrayList<MusicaS> musicas = new ArrayList<MusicaS>();
        for (AutorS autor : autores) {
            for (AlbumS album : autor.getAlbuns()) {
                for (MusicaS musica : album.getMusicas()) {
                    musicas.add(musica);
                }
            }
        }
        return musicas;
    }

    @SuppressWarnings("ReturnOfCollectionOrArrayField")
    public static ArrayList<AutorS> getAutores() {
        return autores;
    }

    public static ArrayList<AutorS> listarAutores(String nomeAutor) {
        ArrayList<AutorS> lista = new ArrayList<AutorS>(autores.size());
        for (AutorS autorS : autores) {
            if (autorS.getNome().contains(nomeAutor)) {
                lista.add(autorS);
            }
        }
        return lista;
    }

    public static ArrayList<AlbumS> listarAlbuns(String nomeAlbum) {
        ArrayList<AlbumS> lista = new ArrayList<AlbumS>(autores.size() * 3);
        for (AutorS autorS : autores) {
            for (AlbumS albumS : autorS.getAlbuns()) {
                if (albumS.getNome().toLowerCase().contains(nomeAlbum.toLowerCase())) {
                    lista.add(albumS);
                }
            }
        }
        return lista;
    }

    public static ArrayList<MusicaS> listarMusicas(String nomeAutor, String nomeAlbum, String nomeMusica) {
        ArrayList<MusicaS> musicas = new ArrayList<MusicaS>();
        for (AutorS autor : autores) {
            if (autor.getNome().contains(nomeAutor)) {
                for (AlbumS album : autor.getAlbuns()) {
                    if (album.getNome().toLowerCase().contains(nomeAlbum.toLowerCase())) {
                        for (MusicaS musica : album.getMusicas()) {
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
        PlaylistI playlist = Busca.buscar(playlists, nome);
        if (criar && playlist == null) {
            try {
                playlist = tipoListaCriar.newInstance();
                playlist.setNome(nome);
                playlists.add(playlist);
            } catch (InstantiationException ex) {
                ex.printStackTrace(System.err);
            } catch (IllegalAccessException ex) {
                ex.printStackTrace(System.err);
            }
        }
        return playlist;
    }

    public static boolean  removerPlaylist(PlaylistI playlist) {
        return playlists.remove(playlist);
    }

    public static ArrayList<PlaylistI> listarPlayLists(String text) {
        ArrayList<PlaylistI> lista = new ArrayList<PlaylistI>();
        for (PlaylistI playlistI : lista) {
            String nome = playlistI.getNome();
            if (nome.contains(text)) {
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

    public static class Busca {

        public static <E extends StringComparable> E buscar(ArrayList<E> lista, String nome) {
            for (E e : lista) {
                if (e.compareTo(nome)==0) {
                    return e;
                }
            }
            return null;
        }
    }
}
