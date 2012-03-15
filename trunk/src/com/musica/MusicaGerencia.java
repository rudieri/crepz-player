/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.musica;

import com.conexao.Transacao;
import com.utils.BuscaGoogle;
import com.utils.ComandosSO;
import java.io.File;
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

    @SuppressWarnings("RedundantStringConstructorCall")
    public static String[] generos = new String("Blues,Classic Rock,Country,Dance,Disco,Funk,Grunge,Hip-Hop,Jazz,Metal,New Age,Oldies,Other,"
            + "Pop,R&B,Rap,Reggae,Rock,Techno,Industrial,Alternative,Ska,Death Metal,Pranks,Soundtrack,Euro-Techno,Ambient,Trip-Hop,"
            + "Vocal,Jazz+Funk,Fusion,Trance,Classical,Instrumental,Acid,House,Game,Sound Clip,Gospel,Noise,Altern Rock,Bass,Soul,Punk,"
            + "Space,Meditative,Instrumental Pop,Instrumental Rock,Ethnic,Gothic,Darkwave,Techno-Industrial,Electronic,Pop-Folk,Eurodance,"
            + "Dream,Southern Rock,Comedy,Cult,Gangsta,Top 40,Christian Rap,Pop/Funk,Jungle,Native American,Cabaret,New Wave,Psychadelic,Rave,"
            + "Showtunes,Trailer,Lo-Fi,Tribal,Acid Punk,Acid Jazz,Polka,Retro,Musical,Rock & Roll,Hard Rock,Folk,Folk/Rock,National Folk," + ""
            + "Swing,Bebob,Latin,Revival,Celtic,Bluegrass,Avantgarde,Gothic Rock,Progressive Rock,Psychedelic Rock,Symphonic Rock,Slow Rock,"
            + "Big Band,Chorus,Easy Listening,Acoustic,Humor,Speech,Chanson,Opera,Chamber Music,Sonata,Symphony,Booty Bass,Primus,Porn Groove,"
            + "Satire,Slow Jam,Club,Tango,Samba,Folclore").split(",");
    private static String[] extSuportadaMusica = new String[]{"mp3", "ogg", "wav"};
    private static String[] extSuportadaImagem = new String[]{"jpg", "jpeg", "png", "gif"};
    public static int count = 0;
    public static boolean organizarPastas = false;
    public static boolean downLoadCapas = false;
    public static String destino = "";

    public static Musica getMusica(Musica m, MP3File mp3, File file) throws Exception {
        //getID3v2Tag
        if (mp3.hasID3v2Tag()) {
            m.setNome(mp3.getID3v2Tag().getSongTitle());
            m.setSize(mp3.getID3v2Tag().getSize());
            m.setAlbum(mp3.getID3v2Tag().getAlbumTitle());
            m.setAutor(mp3.getID3v2Tag().getLeadArtist());
            m.setGenero(mp3.getID3v2Tag().getSongGenre());
            if (m.getAlbum() == null) {
                m.setAlbum(mp3.getID3v1Tag().getAlbumTitle());
            }
        }
//        getID3v1
        if (mp3.hasID3v1Tag()) {
            if (m.getNome() == null) {
                m.setNome(mp3.getID3v1Tag().getTitle());
            }
            m.setSize(mp3.getID3v1Tag().getSize());
            m.setAlbum(mp3.getID3v1Tag().getAlbumTitle());
            m.setAutor(mp3.getID3v1Tag().getArtist());
            m.setGenero(Integer.valueOf(mp3.getID3v1Tag().getGenre()));
        }
        if (m.getNome() == null || m.getNome().isEmpty()) {
            m.setNome(file.getName());
        }
        m.setImg(getImagemDir(file.getParentFile()));

        return m;
    }

    public static Musica getMusica(Musica m, File file) throws Exception {
        m.setCaminho(normalizarCaminhoArquivo(file));

        Map<String, String> pro = getPropriedades(file);
        if (pro != null) {
            m.setNome(pro.get("title"));
            if (m.getNome() == null) {
                m.setNome(file.getName());
            }

            m.setAlbum(pro.get("album"));
            m.setAutor(pro.get("author"));
            m.setGenero(pro.get("ogg.comment.genre"));

        }
        m.setImg(getImagemDir(file.getParentFile()));

        return m;
    }

    public static void mapearDiretorio(File dir, Transacao t, JProgressBar JProgressBar, Integer total) throws Exception {
        if (dir.isDirectory()) {
            File[] f = dir.listFiles();
            for (int i = 0; i < f.length; i++) {
                mapearDiretorio(f[i], t, JProgressBar, total);
            }

        } else {

            count++;
            if (JProgressBar != null) {
                JProgressBar.setValue(count * 100 / total);
                JProgressBar.setString(count + " de " + total);
            }
            addFiles(dir, t);
        }
    }

    public static void mapearDiretorio(File dir, ArrayList<Musica> container, Transacao t) throws Exception {
        if (dir.isDirectory()) {
            File[] f = dir.listFiles();
            for (int i = 0; i < f.length; i++) {
                mapearDiretorio(f[i], container, t);
            }

        } else {
            Musica musica = addFiles(dir, t);
            if (musica != null) {
                container.add(musica);
            }
        }
    }

    @SuppressWarnings("AssignmentToMethodParameter")
    private static void mapearDiretorio(File dir, ArrayList<Musica> container, JProgressBar jProgressBar, int total, Transacao t) throws Exception {
        if (dir.isDirectory()) {
            File[] f = dir.listFiles();
            for (File file : f) {
                mapearDiretorio(file, container, jProgressBar, total, t);
            }
        } else {
            Musica musica = addFiles(dir, t);
            if (musica != null) {
                container.add(musica);
                jProgressBar.setValue(container.size() * 100 / total);
                jProgressBar.setString(container.size() + " músicas de " + total + " arquivos.");
            } else {
            }
        }
    }

    public static void mapearDiretorio(ArrayList<File> dirs, ArrayList<Musica> container, JProgressBar jProgressBar, int total) throws Exception {
        Transacao t = new Transacao();
        t.begin();
        jProgressBar.setStringPainted(true);
        for (short i = 0; i < dirs.size(); i++) {
            File f = dirs.get(i);
//            File[] f = new File[dirs.size()];
            mapearDiretorio(f, container, jProgressBar, total, t);
        }

        t.commit();
    }

    public static boolean ehValido(File file) {
        //  System.out.println(new MimetypesFileTypeMap().getContentType(file));
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
            Logger.getLogger(Musica.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        } catch (IOException ex) {
            Logger.getLogger(Musica.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    @SuppressWarnings("AssignmentToMethodParameter")
    public static Musica addFiles(File file, Transacao t) {

        if (ehValido(file)) {
            Musica m = new Musica();
            if (getExtecao(file).toLowerCase().equals("ogg")) {
                return adicionaOGG(file, t);
            }
            try {

                String caminho = file.getAbsolutePath().trim().replace('\\', '/');
                MP3File mp3 = new MP3File(caminho);
                m.setCaminho(normalizarCaminhoArquivo(file));
                if (organizarPastas) {
                    getMusica(m, mp3, file);
                    File destinoF = new File(destino);
                    destinoF.mkdirs();
                    destinoF = new File(destinoF.getAbsolutePath() + "/" + file.getName());
                    if (!destinoF.getAbsolutePath().equals(file.getAbsolutePath())) {
                        if (file.renameTo(destinoF)) {
                            file = destinoF;
                        }
                        caminho = file.getAbsolutePath().trim().replace('\\', '/');
                        mp3 = new MP3File(caminho);
                        m.setCaminho(caminho);
                    }

                }

                if (MusicaBD.existe(m, t)) {
                    MusicaBD.carregar(m, t);
                    getMusica(m, mp3, file);
                    m.setDtModArquivo(file.lastModified());
                    MusicaBD.alterar(m, t);
                } else {
                    getMusica(m, mp3, file);
                    m.setDtModArquivo(file.lastModified());
                    MusicaBD.incluir(m, t);
                    MusicaBD.carregarPeloEndereco(m, t);

                }
                if (downLoadCapas && m.getImg() == null) {
                    m.setImg(BuscaGoogle.getAquivoBuscaImagens(m).getAbsolutePath());
                    MusicaBD.alterar(m, t);
                }


            } catch (Exception e) {
                System.out.println("Erro ao importar arquivos");
                e.printStackTrace();
            }
            return m;
        } else {
//            System.out.println(file.getName() + " Não é um tipo válido.");
            return null;
        }

    }

    public static Musica adicionaOGG(File dir, Transacao t) {
        Musica m = new Musica();
        try {
            getMusica(m, dir);


            if (MusicaBD.existe(m, t)) {
                MusicaBD.carregar(m, t);
                getMusica(m, dir);
                MusicaBD.alterar(m, t);
            } else {
                //  getMusica(m, mp3, dir);
                MusicaBD.incluir(m, t);
                MusicaBD.carregarPeloEndereco(m, t);
            }
        } catch (Exception ex) {
            Logger.getLogger(MusicaGerencia.class.getName()).log(Level.SEVERE, null, ex);
        }

        return m;
    }

    public static String getImagemDir(File dir) {
        if (!dir.isDirectory()) {
            return null;
        }
        File[] files = dir.listFiles(new java.io.FileFilter() {

            @Override
            public boolean accept(File pathname) {
                try {
                    boolean aceita = false;
                    for (int i = 0; !aceita && i < extSuportadaImagem.length; i++) {
                        aceita |= pathname.getName().toLowerCase().endsWith(extSuportadaImagem[i]);

                    }
                    return aceita;
//                    return (new javax.swing.ImageIcon(pathname.getPath()).getIconHeight() > 0 && pathname.canRead()) && new File(pathname.getAbsolutePath()).exists();
                } catch (Exception ex) {
                    return false;
                }
            }
        });

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
     */
    public static String normalizarCaminhoArquivo(File caminho) {
        return normalizarCaminhoArquivo(caminho.getAbsolutePath());
    }

    /**
     * Retorna o caminho absoluto do arquivo substituindo alguns caracteres por
     * outros
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

        String ret = st.replaceAll("[^0-9a-zA-Z/_.:;ç\\-+()*&@#$!%áâãéêíôõóú ]", "");
        return ret;
    }
}
