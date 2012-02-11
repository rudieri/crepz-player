/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.main;

import com.Aguarde;
import com.JBiBlioteca;
import com.JMini;
import com.JPlayList;
import com.JPrincipal;
import com.Musiquera;
import com.Musiquera.PropriedadesMusica;
import com.Scan;
import com.fila.JFilaReproducao;
import com.graficos.Icones;
import com.melloware.jintellitype.JIntellitype;
import com.musica.Musica;
import com.musica.MusicaBD;
import com.utils.Warning;
import java.awt.SystemTray;
import java.awt.Window;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.UIManager;
import org.hsqldb.server.Server;

/**
 *
 * @author rudieri
 */
public final class Carregador {

    private static final byte TELA_NORMAL = 0;
    private static final byte TELA_MINI = 1;
    private static final byte TELA_FILA = 2;
    Musiquera musiquera;
    JPrincipal principal;
    JMini mini;
    Scan scan;
    private byte tipoTela;
    private JFilaReproducao filaReproducao;
    private final JPlayList playList;
    private final JBiBlioteca biblioteca;
    private CrepzTray crepzTray;
    private boolean random;
    private boolean repeat = false;
    public final Icones icones;
    private FonteReproducao fonteReproducao;

    public Carregador() {

        Aguarde aguarde = new Aguarde();
        aguarde.mostrar();
        initLookAndFeel();
        startBanco();
        icones = new Icones();
        icones.loadIcons("crepz");
        createLog();
        fonteReproducao = FonteReproducao.AVULSO;
        tipoTela = TELA_FILA;
        musiquera = new Musiquera(this) {

            @Override
            public void numberTempoChange(double s) {
                getTelaPrincipal().tempoEh(s);
            }

            @Override
            public void eventoOcorreuNaMusica(int evt) {
            }

            @Override
            public void stringTempoChange(String hms) {
                getTelaPrincipal().tempoEhHMS(hms);
            }

            @Override
            public Musica getNextMusica() {
                if (fonteReproducao == FonteReproducao.FILA_REPRODUCAO) {
                    return filaReproducao.getProxima();
                } else {
                    playList.setAleatorio(random);
                    return playList.getProxima();
                }
            }

            @Override
            public Musica getPreviousMusica() {
                if (fonteReproducao != FonteReproducao.FILA_REPRODUCAO) {
                    playList.setAleatorio(random);
                    return playList.getAnterior();
                }
                return null;
            }

//            @Override
//            public void stringTempoTotalChange(String hms) {
//                getTelaPrincipal().tempoTotalEhHMS(hms);
//            }
            @Override
            public void atualizaLabels(String nome, int bits, String tempo, int freq) {
                getTelaPrincipal().atualizaLabels(nome, bits, tempo, freq);
                if (fonteReproducao == FonteReproducao.FILA_REPRODUCAO) {
                    filaReproducao.atualizaLabels(nome, bits, tempo, freq);
                }
            }

            @Override
            public void setPropriedadesMusica(PropriedadesMusica propriedadesMusica) {
                Musica musica = musiquera.getMusica();
                musica.setTempo(propriedadesMusica.getTempoTotal());
                try {
                    MusicaBD.alterar(musica);
                    MusicaBD.carregar(musica);
                } catch (Exception ex) {
                    Logger.getLogger(JFilaReproducao.class.getName()).log(Level.SEVERE, null, ex);
                }
                getTelaPrincipal().propriedadesMusicaChanged(propriedadesMusica);
                if (fonteReproducao == FonteReproducao.FILA_REPRODUCAO) {
                    filaReproducao.propriedadesMusicaChanged(propriedadesMusica);
                }
            }
        };
        principal = new JPrincipal(musiquera, this);
        mini = new JMini(musiquera, this);
        playList = new JPlayList(musiquera, this);
        biblioteca = new JBiBlioteca(musiquera, this);
        filaReproducao = new JFilaReproducao(musiquera, this);

        try {
            crepzTray = new CrepzTray(musiquera, this);
        } catch (Exception ex) {
            Warning.print("System tray não supostado.");
            crepzTray = null;
        }


//        mini=new JMini(aguarde, true, principal, null, null)

        startMultimidiaKeys();
        aguarde.dispose();
        //TODO  ler das preferências
        setTelaBase(TELA_FILA);

        scan = new Scan();

    }

    public void setFonteReproducao(FonteReproducao fonteReproducao) {
        this.fonteReproducao = fonteReproducao;
    }

    public static void startBanco() {
        Server server = new org.hsqldb.server.Server();
        server.setDatabaseName(0, "BD");
        server.setDatabasePath(0, "BD");
        server.start();
    }

//    public boolean principalIsAtivo() {
//        return tipoTela == TELA_NORMAL;
//    }
//
//    public boolean miniIsAtivo() {
//        return tipoTela == TELA_MINI;
//    }
    public void setTelaBase(byte tipoTela) {
        switch (tipoTela) {
            case TELA_FILA:
                setFilaComoBase();
                break;
            case TELA_MINI:
                setMiniComoBase();
                break;
            case TELA_NORMAL:
                setPrincipalComoBase();
                break;
        }
    }

    public void mostrarPlayList() {
//        if (isFilaReproducaoVisivel()) {
//            ocultarFilaReproducao();
//        }
        playList.setLocation(getWindowPrincipal().getX() - playList.getWidth() - 5, getWindowPrincipal().getY());
        playList.setVisible(true);
        setFonteReproducao(FonteReproducao.PLAY_LIST);
    }

    public void ocultarPlayList() {
        playList.setVisible(false);
    }

    public void mostrarBiblioteca() {
        biblioteca.setLocation(getWindowPrincipal().getX() - biblioteca.getWidth() - 5, getWindowPrincipal().getY());
        biblioteca.setVisible(true);
    }

    public void ocultarBiblioteca() {
        playList.setVisible(false);
    }

    public Window getWindowPrincipal() {
        switch (tipoTela) {
            case TELA_FILA:
                return filaReproducao;
            case TELA_NORMAL:
                return principal;
            case TELA_MINI:
                return mini;

        }
        return principal;
    }

    public Notificavel getTelaPrincipal() {
        switch (tipoTela) {
            case TELA_FILA:
                return filaReproducao;
            case TELA_NORMAL:
                return principal;
            case TELA_MINI:
                return mini;

        }
        return principal;
    }

    public void setMiniComoBase() {
        principal.dispose();
        filaReproducao.dispose();
        mini.setVisible(true);
        tipoTela = TELA_MINI;
        if (crepzTray != null) {
            mostrarIconeTray();
        }
    }

    public void setPrincipalComoBase(int x, int y) {
        principal.setLocation(x, y);
        setPrincipalComoBase();
    }

    public void setPrincipalComoBase() {
        if (crepzTray != null) {
            ocultarIconeTray();
        }
        mini.dispose();
        filaReproducao.dispose();
        principal.setVisible(true);
    }

    public void setFilaComoBase() {
        if (crepzTray != null) {
            ocultarIconeTray();
        }
        setFonteReproducao(FonteReproducao.FILA_REPRODUCAO);
        mini.dispose();
        principal.dispose();
        filaReproducao.setVisible(true);
    }

    public boolean isPlayListVisible() {
        return playList.isVisible();
    }

    public boolean isBibliotecaVisible() {
        return biblioteca.isVisible();
    }

    public void sair() {
        System.exit(0);
    }

    private void createLog() {
        File mk = new File("nbproject");
        if (!mk.exists()) {
            mk = new File("log");
            if (!mk.exists()) {
                mk.mkdir();
            }
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");

            File f = new File(mk.getAbsolutePath() + "/" + format.format(new Date().getTime()) + ".txt");
            if (!f.exists()) {
                try {

                    f.createNewFile();
                    PrintStream saida = new PrintStream(f);
                    System.setOut(saida);
                    System.setErr(saida);
                } catch (IOException ex) {
                    Logger.getLogger(JMini.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

        }
    }

    public void addToPlayList(ArrayList<Musica> musicas) {
        playList.addMusicas(musicas);
    }

    public void addToPlayList(Musica musica) {
        playList.addMusica(musica);
    }

    private void mostrarIconeTray() {
        if (SystemTray.isSupported()) {
            crepzTray.toTray();
        }
    }

    private void ocultarIconeTray() {
        if (SystemTray.isSupported()) {
            crepzTray.someTray();
        }
    }

    private void startMultimidiaKeys() {
        try {
            if (System.getProperty("os.name").indexOf("Windows") > -1) {
                if (System.getProperty("sun.arch.data.model").equals("64")) {
                    JIntellitype.setLibraryLocation(principal.getClass().getResource("com/dll/JIntellitype64.dll").getFile());
                } else {
                    JIntellitype.setLibraryLocation(principal.getClass().getResource("com/dll/JIntellitype.dll").getFile());
                }

                if (JIntellitype.checkInstanceAlreadyRunning("JIntellitype Test Application")) {
                    System.exit(1);
                }
                if (!JIntellitype.isJIntellitypeSupported()) {
                    System.exit(1);
                }
                principal.initJIntellitype();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initLookAndFeel() {

        System.setProperty("Quaqua.tabLayoutPolicy", "wrap");
        try {
            UIManager.setLookAndFeel("ch.randelshofer.quaqua.QuaquaLookAndFeel");

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public boolean isRandom() {
        return random;
    }

    public void setRandom(boolean random) {
        this.random = random;
    }

    public boolean isRepeat() {
        return repeat;
    }

    public void setRepeat(boolean repeat) {
        this.repeat = repeat;
    }

    public static void main(String[] args) {
        Carregador carregador = new Carregador();
    }

    public void mostrarFilaReproducao() {
        setFilaComoBase();
    }

    public void ocultarFilaReproducao() {
        setPrincipalComoBase();
//        filaReproducao.setVisible(false);
    }

    public boolean isFilaReproducaoVisivel() {
        return filaReproducao.isVisible();
    }

    public enum FonteReproducao {

        PLAY_LIST,
        FILA_REPRODUCAO,
        AVULSO
    }
}
