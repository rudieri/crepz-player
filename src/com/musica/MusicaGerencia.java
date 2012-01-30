/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.musica;

import com.conexao.Transacao;
import com.utils.BuscaGoogle;
import java.io.File;
import java.io.IOException;
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
        m.setNome(file.getName());
        if (mp3.hasID3v2Tag()) {
            if (!m.setNome(mp3.getID3v2Tag().getSongTitle())) {
                m.setNome(file.getName());
            }
            m.setSize(mp3.getID3v2Tag().getSize());
            m.setAlbum(mp3.getID3v2Tag().getAlbumTitle());
            m.setAutor(mp3.getID3v2Tag().getLeadArtist());
            m.setGenero(mp3.getID3v2Tag().getSongGenre());
            //   mp3.getID3v1Tag().getSize();
        } else {
            if (mp3.hasID3v1Tag()) {
//        getID3v1
                if (!m.setNome(mp3.getID3v1Tag().getTitle())) {
                    m.setNome(file.getName());
                }
                m.setSize(mp3.getID3v1Tag().getSize());
                m.setAlbum(mp3.getID3v1Tag().getAlbum());
                m.setAutor(mp3.getID3v1Tag().getArtist());
                m.setGenero(Integer.valueOf(mp3.getID3v1Tag().getGenre()));
            }
        }
        m.setImg(getImagemDir(new File(mp3.getMp3file().getAbsolutePath().replace(mp3.getMp3file().getName(), ""))));

        return m;
    }

    public static Musica getMusica(Musica m, File file) throws Exception {
        m.setCaminho(file.getAbsolutePath());

        Map<String, String> pro = getPropriedades(file);
        if (pro != null) {
            if (!m.setNome(pro.get("title"))) {
                m.setNome(file.getName());
            }

            m.setAlbum(pro.get("album"));
            m.setAutor(pro.get("author"));
            m.setGenero(pro.get("ogg.comment.genre"));

        }
        m.setImg(getImagemDir(new File(file.getAbsolutePath().replace(file.getName(), ""))));

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

    public static Musica addFiles(File dir) {
        Transacao t = new Transacao();
        try {
            t.begin();
            Musica addFiles = addFiles(dir, t);
            t.commit();
            return addFiles;
        } catch (Exception ex) {
            Logger.getLogger(MusicaGerencia.class.getName()).log(Level.SEVERE, null, ex);
            t.rollback();
            return null;
        }
    }

    public static Musica addFiles(File dir, Transacao t) {

        if (ehValido(dir)) {
            Musica m = new Musica();
            if (getExtecao(dir).equalsIgnoreCase("ogg")) {
                return adicionaOGG(dir, t);
            }
            try {

                MP3File mp3 = new MP3File(dir.getAbsolutePath().replace("\\\\", "/").replace("\\", "/").trim());
                m.setCaminho(dir.getAbsolutePath());
                if (organizarPastas) {
                    getMusica(m, mp3, dir);
                    File destinoF = new File(destino);
                    destinoF.mkdirs();
                    destinoF = new File(destinoF.getAbsolutePath() + "/" + dir.getName());
                    if (!destinoF.getAbsolutePath().equals(dir.getAbsolutePath())) {
                        mp3 = null;
                        if (dir.renameTo(destinoF)) {
                            dir = destinoF;
                        }
                    }

                    mp3 = new MP3File(dir.getAbsolutePath().replace("\\\\", "/").replace("\\", "/").trim());
                    m.setCaminho(dir.getAbsolutePath().replace("\\\\", "/").replace("\\", "/").trim());

                }

                if (MusicaBD.existe(m, t)) {
                    MusicaBD.carregar(m, t);
                    getMusica(m, mp3, dir);
                    MusicaBD.alterar(m, t);
                } else {
                    getMusica(m, mp3, dir);
                    MusicaBD.incluir(m, t);
                }
                if (downLoadCapas && (m.getImg() == null || m.getImg().isEmpty())) {
                    m.setImg(BuscaGoogle.getAquivoBuscaImagens(m).getAbsolutePath());
                    MusicaBD.alterar(m, t);
                }


            } catch (Exception e) {
                System.out.println("Erro ao importar arquivos");
                e.printStackTrace();
            }
            return m;
        } else {
            System.out.println(dir.getName().toLowerCase() + " Não é um tipo válido.");
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
                    return true;
//                    return (new javax.swing.ImageIcon(pathname.getPath()).getIconHeight() > 0 && pathname.canRead()) && new File(pathname.getAbsolutePath()).exists();
                } catch (Exception ex) {
                    return false;
                }
            }
        });

        if (files != null && files.length > 0) {
            return files[0].getAbsolutePath();
        } else {
            return null;
        }

    }

    public static String removeCaracteresEsp(String st) {
        if (st==null) {
            return "";
        }
        String ret;
        ret = st.replace("/", "");
//        ret = ret.replace("|", "").replace("þ", "");
//        ret = ret.replace("|", "").replace("þ", "");
//        ret = ret.replace(" ", "_");
        ret = ret.replace("ÿ", "");
        ret = ret.replace("þ", "");

        return ret;
    }
}
