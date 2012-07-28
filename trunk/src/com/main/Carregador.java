/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.main;

import com.config.Configuracaoes;
import com.config.constantes.TelaPadrao;
import com.fila.JFilaReproducao;
import com.graficos.Icones;
import com.hotkey.linux.Ouvinte;
import com.main.gui.Aguarde;
import com.main.gui.JBiBlioteca;
import com.main.gui.JMini;
import com.main.gui.JPrincipal;
import com.melloware.jintellitype.JIntellitype;
import com.musica.LinhaDoTempo;
import com.musica.Musica;
import com.musica.MusicaBD;
import com.musica.Musiquera;
import com.musica.Musiquera.PropriedadesMusica;
import com.playlist.JPlayList;
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

    Musiquera musiquera;
    JPrincipal principal;
    JMini mini;
    Scan scan;
    private TelaPadrao telaPadrao;
    private JFilaReproducao filaReproducao;
    private final JPlayList playList;
    private final JBiBlioteca biblioteca;
    private CrepzTray crepzTray;
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
        telaPadrao = (TelaPadrao) Configuracaoes.getEnum(Configuracaoes.CONF_TELA_PADRAO);
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
                    return playList.getProxima();
                }
            }

            @Override
            public Musica getPreviousMusica() {
                if (fonteReproducao == FonteReproducao.FILA_REPRODUCAO) {
                    return filaReproducao.getAnterior();
                } else {
                    return playList.getAnterior();
                }
            }

//            @Override
//            public void stringTempoTotalChange(String hms) {
//                getTelaPrincipal().tempoTotalEhHMS(hms);
//            }
            @Override
            public void atualizaLabels(String nome, int bits, String tempo, int freq) {
                for (int i = 0; i < getTodasTelas().length; i++) {
                    getTodasTelas()[i].atualizaLabels(nome, bits, tempo, freq);

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
        Ouvinte ouvinte = new Ouvinte(musiquera);
        principal = new JPrincipal(musiquera, this);
        mini = new JMini(musiquera, this);
        playList = new JPlayList(musiquera, this);
        biblioteca = new JBiBlioteca(musiquera, this);
        filaReproducao = new JFilaReproducao(musiquera, this);

        try {
            crepzTray = new CrepzTray(musiquera, this);
        } catch (Exception ex) {
            Warning.print("System tray não supostado.");
            ex.printStackTrace(System.err);
            crepzTray = null;
        }

        startMultimidiaKeys();
        aguarde.dispose();
        // Deixa a tela padrão visível.
        setTelaBase(telaPadrao);

        scan = new Scan();

    }

    public void setFonteReproducao(FonteReproducao fonteReproducao) {
        this.fonteReproducao = fonteReproducao;
        LinhaDoTempo.setAtiva(fonteReproducao != FonteReproducao.PLAY_LIST);
        if (fonteReproducao == FonteReproducao.FILA_REPRODUCAO) {
            telaPadrao = TelaPadrao.J_FILA;
        }
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
    public void setTelaBase(TelaPadrao telaPadrao) {
        switch (telaPadrao) {
            case J_FILA:
                setFilaComoBase();
                break;
            case J_MINI:
                setMiniComoBase();
                break;
            case J_PRINCIPAL:
                setPrincipalComoBase();
                break;
            case COMO_ESTAVA:
                abrirUltimaConfiguracao();
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
        switch (telaPadrao) {
            case J_FILA:
                return filaReproducao;
            case J_PRINCIPAL:
                return principal;
            case J_MINI:
                return mini;

        }
        return principal;
    }

    public Notificavel getTelaPrincipal() {
        switch (telaPadrao) {
            case J_FILA:
                return filaReproducao;
            case J_PRINCIPAL:
                return principal;
            case J_MINI:
                return mini;

        }
        return principal;
    }

    public Notificavel[] getTodasTelas() {
        return new Notificavel[]{filaReproducao, principal, mini};
    }

    private void abrirUltimaConfiguracao() {
        principal.setVisible(Configuracaoes.getBoolean(Configuracaoes.CONF_VISIB_PRINCIPAL));
        mini.setVisible(Configuracaoes.getBoolean(Configuracaoes.CONF_VISIB_MINI));
        playList.setVisible(Configuracaoes.getBoolean(Configuracaoes.CONF_VISIB_PLAYLIST));
        biblioteca.setVisible(Configuracaoes.getBoolean(Configuracaoes.CONF_VISIB_BIBLIOTECA));
        filaReproducao.setVisible(Configuracaoes.getBoolean(Configuracaoes.CONF_VISIB_FILA));
        playList.setPlayListAberta(Configuracaoes.getInteger(Configuracaoes.CONF_LISTA_ABERTA));
        setFonteReproducao((FonteReproducao) Configuracaoes.getEnum(Configuracaoes.CONF_FONTE_REPRODUCAO));
        musiquera.tocarProxima();
    }

    public void setMiniComoBase() {
        principal.dispose();
        filaReproducao.dispose();
        mini.setVisible(true);
        telaPadrao = TelaPadrao.J_MINI;
        if (crepzTray != null) {
            mostrarIconeTray();
        }
    }

    public void setPrincipalComoBase(int x, int y) {
        principal.setLocation(x, y);
        setPrincipalComoBase();
    }

    public void setPrincipalComoBase() {
        if (crepzTray != null && crepzTray.isOnTray()) {
            ocultarIconeTray();
        }
        telaPadrao = TelaPadrao.J_PRINCIPAL;
        mini.dispose();
        filaReproducao.dispose();
        principal.setVisible(true);
    }

    public void setFilaComoBase() {
        if (crepzTray != null && crepzTray.isOnTray()) {
            ocultarIconeTray();
        }
        setFonteReproducao(FonteReproducao.FILA_REPRODUCAO);
        telaPadrao = TelaPadrao.J_FILA;
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
        Configuracaoes.set(Configuracaoes.CONF_VISIB_PRINCIPAL, principal.isVisible());
        Configuracaoes.set(Configuracaoes.CONF_VISIB_MINI, mini.isVisible());
        Configuracaoes.set(Configuracaoes.CONF_VISIB_BIBLIOTECA, biblioteca.isVisible());
        Configuracaoes.set(Configuracaoes.CONF_VISIB_FILA, filaReproducao.isVisible());
        Configuracaoes.set(Configuracaoes.CONF_VISIB_PLAYLIST, playList.isVisible());
        Configuracaoes.set(Configuracaoes.CONF_LISTA_ABERTA, playList.getPlaylistAberta() == null ? -1 : playList.getPlaylistAberta().getId());
        Configuracaoes.set(Configuracaoes.CONF_FONTE_REPRODUCAO, fonteReproducao);
        System.exit(0);
    }

    // Salvar preferencias        
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
                    JIntellitype.setLibraryLocation(principal.getClass().getResource("com/hotkey/windows/JIntellitype64.dll").getFile());
                } else {
                    JIntellitype.setLibraryLocation(principal.getClass().getResource("com/hotkey/windows/JIntellitype.dll").getFile());
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
        return Configuracaoes.getBoolean(Configuracaoes.CONF_RANDOM_ATIVO);
    }

    public void setRandom(boolean random) {
        Configuracaoes.set(Configuracaoes.CONF_RANDOM_ATIVO, random);
    }

    public boolean isRepeat() {
        return Configuracaoes.getBoolean(Configuracaoes.CONF_REPEAT_ATIVO);
    }

    public void setRepeat(boolean repeat) {
        Configuracaoes.set(Configuracaoes.CONF_REPEAT_ATIVO, repeat);
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
