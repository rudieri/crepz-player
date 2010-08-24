/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.musica;

import com.conexao.Transacao;
import com.configuracao.Configuracao;
import com.utils.BuscaGoogle;
import java.io.File;
import javax.swing.JProgressBar;
import org.farng.mp3.MP3File;

/**
 *
 * @author manchini
 */
public class Musica {

    public static String[] generos = new String("Blues,Classic Rock,Country,Dance,Disco,Funk,Grunge,Hip-Hop,Jazz,Metal,New Age,Oldies,Other,"
            + "Pop,R&B,Rap,Reggae,Rock,Techno,Industrial,Alternative,Ska,Death Metal,Pranks,Soundtrack,Euro-Techno,Ambient,Trip-Hop,"
            + "Vocal,Jazz+Funk,Fusion,Trance,Classical,Instrumental,Acid,House,Game,Sound Clip,Gospel,Noise,Altern Rock,Bass,Soul,Punk,"
            + "Space,Meditative,Instrumental Pop,Instrumental Rock,Ethnic,Gothic,Darkwave,Techno-Industrial,Electronic,Pop-Folk,Eurodance,"
            + "Dream,Southern Rock,Comedy,Cult,Gangsta,Top 40,Christian Rap,Pop/Funk,Jungle,Native American,Cabaret,New Wave,Psychadelic,Rave,"
            + "Showtunes,Trailer,Lo-Fi,Tribal,Acid Punk,Acid Jazz,Polka,Retro,Musical,Rock & Roll,Hard Rock,Folk,Folk/Rock,National Folk," + ""
            + "Swing,Bebob,Latin,Revival,Celtic,Bluegrass,Avantgarde,Gothic Rock,Progressive Rock,Psychedelic Rock,Symphonic Rock,Slow Rock,"
            + "Big Band,Chorus,Easy Listening,Acoustic,Humor,Speech,Chanson,Opera,Chamber Music,Sonata,Symphony,Booty Bass,Primus,Porn Groove,"
            + "Satire,Slow Jam,Club,Tango,Samba,Folclore").split(",");
    public static Integer count = 0;
    private Integer id;
    private String nome;
    private String autor;
    private String genero;
    private String Album;
    private String caminho;
    private String img;
    private int size;
    private int number;

    /**
     * @return the nome
     */
    public String getNome() {
        return nome;
    }

    /**
     * @param nome the nome to set
     */
    public boolean setNome(String nome) {
        if (nome == null || nome.equals("")) {
            System.out.println("\n------- NOME VAZIO -------\n");
            return false;
        } else {
            this.nome = nome;
            return true;
        }
    }

    /**
     * @return the autor
     */
    public String getAutor() {
        return autor;
    }

    /**
     * @param autor the autor to set
     */
    public void setAutor(String autor) {
        this.autor = autor;
    }

    /**
     * @return the genero
     */
    public String getGenero() {
        return genero;
    }

    /**
     * @param genero the genero to set
     */
    public void setGenero(String genero) {
        if (!(genero == null)) {
            if (genero.indexOf("(") == 0) {
                try {
                    setGenero(new Integer(genero.replace("(", "").replace(")", "")));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } else {
                this.genero = genero;
            }
        }
    }

    /**
     * @return the Album
     */
    public String getAlbum() {
        return Album;
    }

    /**
     * @param Album the Album to set
     */
    public void setAlbum(String Album) {
        if (Album != null) {
            this.Album = Album;
        } else {
            Album = "";
        }
    }

    /**
     * @return the caminho
     */
    public String getCaminho() {
        if (caminho != null) {
            return caminho.replace("<&aspas>", "'");
        } else {
            return null;
        }
    }

    /**
     * @param caminho the caminho to set
     */
    public void setCaminho(String caminho) {

        this.caminho = caminho.replace("\\", "/").replace("//", "/").replace("'", "<&aspas>");
    }

    /**
     * @return the img
     */
    public String getImg() {
        if (img != null) {
            return img.replace("<&aspas>", "'");
        } else {
            return "";
        }
    }

    /**
     * @param img the img to set
     */
    public void setImg(String img) {
        if (!(img == null)) {
            this.img = img.replace("\\", "/").replace("//", "/").replace("'", "<&aspas>");
        }

    }

    /**
     * @return the id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Integer id) {
        this.id = id;
    }

    public static Musica getMusica(Musica m, MP3File mp3, File file) throws Exception {
        m.setCaminho(mp3.getMp3file().getAbsolutePath());

        //getID3v2Tag
        if (mp3.getID3v2Tag() != null) {
            if (!m.setNome(mp3.getID3v2Tag().getSongTitle())) {
                m.setNome(file.getName());
            }
            m.setSize(mp3.getID3v2Tag().getSize());
            m.setAlbum(mp3.getID3v2Tag().getAlbumTitle());
            m.setAutor(mp3.getID3v2Tag().getLeadArtist());
            m.setGenero(mp3.getID3v2Tag().getSongGenre());
            //   mp3.getID3v1Tag().getSize();
        } else if (mp3.getID3v1Tag() != null) {

//        getID3v1

            if (!m.setNome(mp3.getID3v1Tag().getTitle())) {
                m.setNome(file.getName());
            }
            m.setSize(mp3.getID3v1Tag().getSize());
            m.setAlbum(mp3.getID3v1Tag().getAlbum());
            m.setAutor(mp3.getID3v1Tag().getArtist());
            m.setGenero(Integer.valueOf(mp3.getID3v1Tag().getGenre()));

        } else {
            if (file.getName().indexOf(".mp3") != -1) {
                System.out.println("*******************************");

                m.setNome(file.getName());
            } else {

                throw new Exception("Erro arquivo não suportado.\n" + file.getName() + "\n" + file.getName().indexOf(".mp3"));

            }
        }



        m.setImg(getImagemDir(new File(mp3.getMp3file().getAbsolutePath().replace(mp3.getMp3file().getName(), ""))));

        return m;
    }

    public static void mapearDiretorio(File dir, Transacao t, JProgressBar JProgressBar, Integer total) throws Exception {
        if (dir.isDirectory()) {
            for (int i = 0; i < dir.listFiles().length; i++) {
                File f = dir.listFiles()[i];
                mapearDiretorio(f, t, JProgressBar, total);
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

    public static Musica addFiles(File dir, Transacao t) {
        Musica m = new Musica();
        if (dir.getName().toLowerCase().indexOf(".mp3") != -1) {
            try {
                dir = new File(dir.getAbsolutePath());
                MP3File mp3 = new MP3File(dir.getAbsolutePath().replace("\\\\", "/").replace("\\", "/").trim());
                m.setCaminho(dir.getAbsolutePath());
                if (Boolean.TRUE.toString().equals(Configuracao.getConfiguracoes().get("organizadorPastas").toString())) {
                    getMusica(m, mp3, dir);
                    String dest = Configuracao.getConfiguracoes().get("organizadorDestino") + "/" + removeCaracteresEsp(m.getAutor()) + "/" + removeCaracteresEsp(m.getAlbum()) + "/";
                    File destino = new File(dest);
                    destino.mkdirs();
                    destino = new File(destino.getAbsolutePath() + "/" + dir.getName());
                    if (!destino.getAbsolutePath().equals(dir.getAbsolutePath())) {
                        mp3 = null;
                        if (dir.renameTo(destino)) {
                            dir = destino;
                        }
                    }
                    mp3 = new MP3File(dir.getAbsolutePath().replace("\\\\", "/").replace("\\", "/").trim());
                    m.setCaminho(dir.getAbsolutePath());

                }

                System.out.println("---------------------\n" + dir.getName());
                if (MusicaBD.existe(m, t)) {
                    MusicaBD.carregar(m, t);
                    getMusica(m, mp3, dir);
                    MusicaBD.alterar(m, t);
                } else {
                    getMusica(m, mp3, dir);
                    MusicaBD.incluir(m, t);
                }

                if (Boolean.TRUE.toString().equals(Configuracao.getConfiguracoes().get("downloadCapas").toString()) && (m.getImg() == null || m.getImg().equals(""))) {
                    m.setImg(BuscaGoogle.getAquivoBuscaImagens(m).getAbsolutePath());
                    MusicaBD.alterar(m, t);
                }

            } catch (Exception e) {
                System.out.println("Erro ao importar arquivos");
                e.printStackTrace();
            }
        } else {
            System.out.println(dir.getName().toLowerCase() + " Não é MP3");
        }
        return m;
    }

    public static String getImagemDir(File dir) {
        if (!dir.isDirectory()) {
            return null;
        }
        File[] files = dir.listFiles(new java.io.FileFilter() {

            public boolean accept(File pathname) {
                try {
                    return (new javax.swing.ImageIcon(pathname.getPath()).getIconHeight() > 0 && pathname.canRead()) && new File(pathname.getAbsolutePath()).exists();
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

    private void setGenero(int genre) {
        if (genre >= generos.length || genre < 0) {
            genero = "";
            return;
        }
        try {
            genero = generos[genre];
        } catch (Exception b) {
            genero = "";
            b.printStackTrace();
        }
    }

    private void setSize(int s) {
        this.size = s;
    }

    public int getSize() {
        return this.size;
    }

    private void setNumero(int n) {
        this.number = n;
    }

    public int getNumero() {
        return number;
    }

    public static String removeCaracteresEsp(String st) {
        String ret = st;
        ret = ret.replace("/", "").replace("ÿ", "");
        ret = ret.replace("|", "").replace("þ", "");
        ret = ret.replace("|", "").replace("þ", "");
        ret = ret.replace(" ", "_");

        return ret;
    }
}
