package com.musica;

import com.musica.album.AlbumS;
import com.musica.autor.AutorS;
import com.serial.PortaCDs;
import com.utils.BuscaGoogle;
import com.utils.ComandosSO;
import com.utils.file.FileUtils;
import com.utils.file.FiltroArquivoGenerico;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JProgressBar;
import org.farng.mp3.MP3File;
import org.tritonus.share.sampled.TAudioFormat;

/**
 *
 * @author rudieri
 */
public class MusicaGerencia {

    public static final ArrayList<String> listaNegra;
    private static final FileFilter fileFilterImg;

    static {
        listaNegra = new ArrayList<String>();
        fileFilterImg = new java.io.FileFilter() {
            @Override
            public boolean accept(File pathname) {
                try {
                    boolean aceita = false;
                    String toLowerCase = pathname.getName().toLowerCase();
                    for (String ext : extSuportadaImagem) {
                        if (toLowerCase.endsWith(ext)) {
                            return true;
                        }
                    }
                    return false;
                } catch (Exception ex) {
                    return false;
                }
            }
        };

    }
    public static String[] generos = ("Blues,Classic Rock,Country,Dance,Disco,Funk,Grunge,Hip-Hop,Jazz,Metal,New Age,Oldies,Other,"
            + "Pop,R&B,Rap,Reggae,Rock,Techno,Industrial,Alternative,Ska,Death Metal,Pranks,Soundtrack,Euro-Techno,Ambient,Trip-Hop,"
            + "Vocal,Jazz+Funk,Fusion,Trance,Classical,Instrumental,Acid,House,Game,Sound Clip,Gospel,Noise,Altern Rock,Bass,Soul,Punk,"
            + "Space,Meditative,Instrumental Pop,Instrumental Rock,Ethnic,Gothic,Darkwave,Techno-Industrial,Electronic,Pop-Folk,Eurodance,"
            + "Dream,Southern Rock,Comedy,Cult,Gangsta,Top 40,Christian Rap,Pop/Funk,Jungle,Native American,Cabaret,New Wave,Psychadelic,Rave,"
            + "Showtunes,Trailer,Lo-Fi,Tribal,Acid Punk,Acid Jazz,Polka,Retro,Musical,Rock & Roll,Hard Rock,Folk,Folk/Rock,National Folk," + ""
            + "Swing,Bebob,Latin,Revival,Celtic,Bluegrass,Avantgarde,Gothic Rock,Progressive Rock,Psychedelic Rock,Symphonic Rock,Slow Rock,"
            + "Big Band,Chorus,Easy Listening,Acoustic,Humor,Speech,Chanson,Opera,Chamber Music,Sonata,Symphony,Booty Bass,Primus,Porn Groove,"
            + "Satire,Slow Jam,Club,Tango,Samba,Folclore").split(",");
    public static final String[] extSuportadaMusica = new String[]{"mp3", "ogg", "wav"};
    private static final String[] extSuportadaImagem = new String[]{"jpg", "jpeg", "png", "gif", "bmp"};
    public static int count = 0;
    public static boolean organizarPastas = false;
    public static boolean downLoadCapas = false;
    public static String destino = "";

    public static MusicaS getMusica(MP3File mp3, File file) throws Exception {
        getMaxDirComum(file);
        //getID3v2Tag
        MusicaS musica = null;
        String mp3Autor;
        String mp3Album;
        String mp3Genero;
        String mp3Musica;
        if (mp3.hasID3v2Tag()) {
            mp3Autor = mp3.getID3v2Tag().getLeadArtist();
            mp3Album = mp3.getID3v2Tag().getAlbumTitle();
            mp3Genero = mp3.getID3v2Tag().getSongGenre();
             mp3Musica = mp3.getID3v2Tag().getSongTitle();
        } else if (mp3.hasID3v1Tag()) {
            mp3Autor = mp3.getID3v1Tag().getLeadArtist();
            mp3Album = mp3.getID3v1Tag().getAlbumTitle();
            mp3Genero = mp3.getID3v1Tag().getSongGenre();
            mp3Musica = mp3.getID3v1Tag().getSongTitle();
        } else if (mp3.hasLyrics3Tag()){
            mp3Autor = mp3.getLyrics3Tag().getLeadArtist();
            mp3Album = mp3.getLyrics3Tag().getAlbumTitle();
            mp3Genero = mp3.getLyrics3Tag().getSongGenre();
            mp3Musica = mp3.getLyrics3Tag().getSongTitle();
        } else {
            mp3Autor = "";
            mp3Album = "";
            mp3Genero = "";
            mp3Musica = "";
        }
        AutorS autor = PortaCDs.getAutor(mp3Autor);
        AlbumS album = autor.getAlbum(mp3Album);
//            album.setNomeDiretorio(file.getParent());
        if (album.getImg() == null) {
            album.setImg(getImagemDir(file.getParentFile()));
        }
        album.setGenero(mp3Genero);
        musica = album.getMusica(mp3Musica);
        musica.setNomeArquivo(file.getAbsolutePath());

        if (musica.getNome() == null || musica.getNome().isEmpty()) {
            musica.setNome(file.getName());
        }

        return musica;
    }

    public static MusicaS getMusica(File file) {
        try {
            getMaxDirComum(file);
        } catch (Exception ex) {
            Logger.getLogger(MusicaGerencia.class.getName()).log(Level.SEVERE, null, ex);
        }
        Map<String, String> pro = getPropriedades(file);
        if (pro != null) {
            AutorS autor = PortaCDs.getAutor(pro.get("author"));
            AlbumS album = autor.getAlbum(pro.get("album"));
//            if (file.getAbsolutePath().equals(maxDirComum.getAbsolutePath())) {
//                autor.setCaminho(file.getParentFile().getAbsolutePath());
//                album.setNomeDiretorio(null);
//            } else {
//                autor.setCaminho(file.getParentFile().getParentFile().getAbsolutePath());
//                album.setNomeDiretorio(file.getParent());
//            }
            if (album.getImg() != null) {
                album.setImg(getImagemDir(file.getParentFile()));
            }
            album.setGenero(pro.get("ogg.comment.genre"));
            MusicaS musica = album.getMusica(pro.get("title"));
            if (musica.getNome() == null || musica.getNome().isEmpty()) {
                musica.setNome(file.getName());
            }
            musica.setNomeArquivo(file.getAbsolutePath());

            return musica;
        }

        return null;
    }

    private static File maxDirComum = null;

    public static void getMaxDirComum(File file) throws Exception {

        if (!file.isDirectory()) {
            if (maxDirComum == null) {
                maxDirComum = file.getAbsoluteFile();
            }
            String path = file.getAbsolutePath();
            while (!path.startsWith(maxDirComum.getAbsolutePath())) {
                maxDirComum = maxDirComum.getParentFile();
            }
        }
    }

    public static void mapearDiretorio(File file, JProgressBar JProgressBar, Integer total) throws Exception {
        getMaxDirComum(file);
        if (file.isDirectory()) {
            File[] f = file.listFiles(FiltroArquivoGenerico.FILTRO_MUSICA);
            for (File f1 : f) {
                mapearDiretorio(f1, JProgressBar, total);
            }

        } else {

            count++;
            if (JProgressBar != null) {
                JProgressBar.setValue(count * 100 / total);
                JProgressBar.setString(count + " de " + total);
            }
            addOneFile(file);
        }
    }

    public static void mapearDiretorio(File dir, ArrayList<MusicaS> container) throws Exception {
        getMaxDirComum(dir);
        if (dir.isDirectory()) {
            File[] f = dir.listFiles(FiltroArquivoGenerico.FILTRO_MUSICA);
            for (File f1 : f) {
                mapearDiretorio(f1, container);
            }

        } else {
            MusicaS musica = addOneFile(dir);
            if (musica != null) {
                container.add(musica);
            }
        }
    }

    private static void mapearDiretorio(File dir, ArrayList<MusicaS> container, JProgressBar jProgressBar, int total) throws Exception {
        if (dir.isDirectory()) {
            File[] f = dir.listFiles(FiltroArquivoGenerico.FILTRO_MUSICA);
            for (File file : f) {
                mapearDiretorio(file, container, jProgressBar, total);
            }
        } else {
            MusicaS musica = addOneFile(dir);
            if (musica != null) {
                container.add(musica);
                jProgressBar.setValue(total == 0 ? 1 : container.size() * 100 / total);
                jProgressBar.setString(container.size() + " músicas de " + total + " arquivos.");
            }
        }
    }

    public static void mapearDiretorio(ArrayList<File> dirs, ArrayList<MusicaS> container, JProgressBar jProgressBar, int total) throws Exception {
        jProgressBar.setStringPainted(true);
        for (short i = 0; i < dirs.size(); i++) {
            mapearDiretorio(dirs.get(i), container, jProgressBar, total);
        }

    }

    public static boolean ehValido(File file) {
        String ext = file.getName().toLowerCase();
        boolean valido = false;
        for (int i = 0; !valido && i < extSuportadaMusica.length; i++) {
            valido |= ext.endsWith(extSuportadaMusica[i]);
        }
        return valido;
    }

    public static String getExtecao(File f) {
        String ext = f.getName();
        return ext.substring(ext.lastIndexOf('.') + 1, ext.length());

    }

    public static Map getPropriedades(File file) {
        try {
            AudioFormat af = AudioSystem.getAudioFileFormat(file).getFormat();
            Map addproperties = ((TAudioFormat) af).properties();
            return addproperties;
        } catch (UnsupportedAudioFileException ex) {
            Logger.getLogger(MusicaS.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        } catch (IOException ex) {
            Logger.getLogger(MusicaS.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public static ArrayList<MusicaS> addFilesM3u(File m3uFile) {
        ArrayList<MusicaS> musicas = new ArrayList<MusicaS>();
        if (m3uFile.getName().endsWith("m3u")) {
            File[] arquivos = FileUtils.lerM3u(m3uFile);
            for (File arquivo : arquivos) {
                musicas.add(addOneFile(arquivo));
            }
        }
        return musicas;
    }

    public static MusicaS addOneFile(File file) {
        if (ehValido(file)) {
            if (getExtecao(file).toLowerCase().equals("ogg")) {
                return adicionaOGG(file);
            }
            try {

                String caminho = file.getAbsolutePath().trim().replace('\\', '/');
                if (listaNegra.contains(caminho)) {
                    System.out.println("Não importado, pois está na lista temporária de regeição: " + caminho);
                    return null;
                }
                MP3File mp3;
                try {
                    mp3 = new MP3File(caminho);
                    MusicaS musica = getMusica(mp3, file);
                    if (organizarPastas) {
                        File destinoF = new File(destino);
                        destinoF.mkdirs();
                        destinoF = new File(destinoF.getAbsolutePath() + "/" + file.getName());
                        if (!destinoF.getAbsolutePath().equals(file.getAbsolutePath())) {
                            if (file.renameTo(destinoF)) {
                                file = destinoF;
                            }
                            caminho = file.getAbsolutePath().replace('\\', '/');
                            mp3 = new MP3File(caminho);
                            musica = getMusica(mp3, file);
                        }

                    }
                    if (musica != null) {
                        musica.setDtModArquivo(file.lastModified());
                        musica.setPerdida(false);
                        if (downLoadCapas && musica.getAlbum().getImg() == null) {
                            musica.getAlbum().setImg(BuscaGoogle.getAquivoBuscaImagens(musica).getAbsolutePath());
                        }
                    }

                    return musica;
                } catch (Exception ex) {
                    System.out.println("Erro ao importar arquivo. Será adiconado na lista negra: " + file.getAbsolutePath());
                    ex.printStackTrace(System.err);
                    listaNegra.add(file.getAbsolutePath());
//                    Musica m = new Musica();
//                    m.setCaminho(normalizarCaminhoArquivo(file));
//                    m.setNome(file.getName());
//                    m.setAlbum(file.getParentFile().getName());
//                    m.setAutor(file.getParentFile().getParentFile().getName());
//                    if (MusicaBD.existe(m, t)) {
//                        MusicaBD.carregar(m, t);
//                        m.setDtModArquivo(file.lastModified());
//                        m.setPerdida(false);
//                        MusicaBD.alterar(m, t);
//                    } else {
//                        m.setDtModArquivo(file.lastModified());
//                        MusicaBD.incluir(m, t);
//                        MusicaBD.carregarPeloEndereco(m, t);
//                    }
                }
            } catch (Exception e) {
                System.out.println("Erro ao importar arquivo. Será adiconado na lista negra: " + file.getAbsolutePath());
                e.printStackTrace(System.err);
                listaNegra.add(file.getAbsolutePath());
            }
        }
        return null;

    }

    public static MusicaS adicionaOGG(File dir) {
        return getMusica(dir);
    }

    public static String getImagemDir(File dir) {
        if (!dir.isDirectory()) {
            return null;
        }

        File[] files = dir.listFiles(fileFilterImg);

        if (files != null && files.length > 0) {
            if (ComandosSO.getMySO() == ComandosSO.WINDOWS) {
                return files[0].getAbsolutePath().replace('\\', '/').replace("//", "/");
            } else {
                return files[0].getAbsolutePath();
            }
        } else {
            return null;
        }

    }

    /**
     * Retorna o caminho absoluto do arquivo substituindo alguns caracteres por
     * outros
     *
     * @param caminho
     * @return
     */
    public static String normalizarCaminhoArquivo(File caminho) {
        return normalizarCaminhoArquivo(caminho.getAbsolutePath());
    }

    /**
     * Retorna o caminho absoluto do arquivo substituindo alguns caracteres por
     * outros
     *
     * @param caminho
     * @return
     */
    public static String normalizarCaminhoArquivo(String caminho) {
        if (ComandosSO.getMySO() == ComandosSO.WINDOWS) {
            return caminho.replace('\\', '/').replace("//", "/");
        } else {
            return caminho;
        }
    }

    public static String removeCaracteresEsp(String st) {
        if (st == null) {
            return "";
        }
        st = st.replaceAll("^[^0-9a-zA-Z/_.:;ç\\-+()*&@#$!%àÀáÁâÂãÃéÉêÊíÍôÔõÕóÓúÚ \\[\\]\\{\\}]+", "");
        StringBuilder saida = new StringBuilder(st.length());
        for (int i = 0; i < st.length(); i++) {
            if (st.charAt(i) != 0) {
                saida.append(st.charAt(i));
            }
        }
//        return st;

        return saida.toString();
    }
}
