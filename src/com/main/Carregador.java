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
import com.main.gui.JMini;
import com.melloware.jintellitype.JIntellitype;
import com.musica.LinhaDoTempo;
import com.musica.Musica;
import com.musica.MusicaBD;
import com.musica.Musiquera;
import com.musica.Musiquera.PropriedadesMusica;
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

    private static Carregador me;
    private Musiquera musiquera;
    private Icones icones;
    private FonteReproducao fonteReproducao;
    private static TelaPadrao telaPadrao;

    public Carregador() {

        Aguarde aguarde = new Aguarde();
        aguarde.mostrar();
        aguarde.intro();
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
                System.out.println("Buscando música em: " + fonteReproducao);
                if (fonteReproducao == FonteReproducao.FILA_REPRODUCAO) {
                    return GerenciadorTelas.getFilaReproducao().getProxima();
                } else {
                    return GerenciadorTelas.getPlayList().getProxima();
                }
            }

            @Override
            public Musica getPreviousMusica() {
                if (fonteReproducao == FonteReproducao.FILA_REPRODUCAO) {
                    return GerenciadorTelas.getFilaReproducao().getProxima();
                } else {
                    return GerenciadorTelas.getPlayList().getProxima();
                }
            }

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
                    GerenciadorTelas.getFilaReproducao().propriedadesMusicaChanged(propriedadesMusica);
                }
            }
        };
        initLookAndFeel();
        startBanco();
        createLog();
        aguarde.dispose();
        // Deixa a tela padrão visível.


    }

    private void inicializarConfiguracoes() {
        icones = new Icones();
        icones.loadIcons("crepz");
        startMultimidiaKeys();
        setTelaBase(telaPadrao);
        if (!Configuracaoes.getList(Configuracaoes.CONF_PASTAS_SCANER).isEmpty()) {
            GerenciadorTelas.getScan();
        }

        Ouvinte ouvinte = new Ouvinte(musiquera);

    }

    public void mostrarModificadorDeTema() {
        GerenciadorTelas.getPele().setVisible(true);
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
        GerenciadorTelas.getPlayList().setLocation(getWindowPrincipal().getX() - GerenciadorTelas.getPlayList().getWidth() - 5, getWindowPrincipal().getY());
        GerenciadorTelas.getPlayList().setVisible(true);
        setFonteReproducao(FonteReproducao.PLAY_LIST);
    }

    public void ocultarPlayList() {
        GerenciadorTelas.getPlayList().setVisible(false);
    }

    public void mostrarBiblioteca() {
        GerenciadorTelas.getBiblioteca().setLocation(getWindowPrincipal().getX() - GerenciadorTelas.getBiblioteca().getWidth() - 5,
                getWindowPrincipal().getY());
        GerenciadorTelas.getBiblioteca().setVisible(true);
    }

    public void ocultarBiblioteca() {
        GerenciadorTelas.getPlayList().setVisible(false);
    }

    public Window getWindowPrincipal() {
        switch (telaPadrao) {
            case J_FILA:
                return GerenciadorTelas.getFilaReproducao();
            case J_PRINCIPAL:
                return GerenciadorTelas.getPrincipal();
            case J_MINI:
                return GerenciadorTelas.getMini();

        }
        return GerenciadorTelas.getPrincipal();
    }

    public Notificavel getTelaPrincipal() {
        switch (telaPadrao) {
            case J_FILA:
                return GerenciadorTelas.getFilaReproducao();
            case J_PRINCIPAL:
                return GerenciadorTelas.getPrincipal();
            case J_MINI:
                return GerenciadorTelas.getMini();

        }
        return GerenciadorTelas.getPrincipal();
    }

    public Notificavel[] getTodasTelas() {
        return new Notificavel[]{GerenciadorTelas.getFilaReproducao(), GerenciadorTelas.getPrincipal(), GerenciadorTelas.getMini()};
    }

    private void abrirUltimaConfiguracao() {
        if (Configuracaoes.getBoolean(Configuracaoes.CONF_VISIB_PRINCIPAL)) {
            GerenciadorTelas.getPrincipal().setVisible(true);
            
        }
        if (Configuracaoes.getBoolean(Configuracaoes.CONF_VISIB_MINI)) {
            GerenciadorTelas.getMini().setVisible(true);
            
        }
        if (Configuracaoes.getBoolean(Configuracaoes.CONF_VISIB_PLAYLIST)) {
            GerenciadorTelas.getPlayList().setVisible(true);
        }
        if (Configuracaoes.getBoolean(Configuracaoes.CONF_VISIB_BIBLIOTECA)) {
            GerenciadorTelas.getBiblioteca().setVisible(true);
        }
        if (Configuracaoes.getBoolean(Configuracaoes.CONF_VISIB_FILA)) {
            GerenciadorTelas.getFilaReproducao().setVisible(true);
            
        }
        GerenciadorTelas.getPlayList().setPlayListAberta(Configuracaoes.getInteger(Configuracaoes.CONF_LISTA_ABERTA));
        setFonteReproducao((FonteReproducao) Configuracaoes.getEnum(Configuracaoes.CONF_FONTE_REPRODUCAO));
        musiquera.tocarProxima();
    }

    public void setMiniComoBase() {
        GerenciadorTelas.getPrincipal().dispose();
        GerenciadorTelas.getFilaReproducao().dispose();
        GerenciadorTelas.getMini().setVisible(true);
        telaPadrao = TelaPadrao.J_MINI;
        if (GerenciadorTelas.getCrepzTray() != null) {
            mostrarIconeTray();
        }
    }

    public void setPrincipalComoBase(int x, int y) {
        GerenciadorTelas.getPrincipal().setLocation(x, y);
        setPrincipalComoBase();
    }

    public void setPrincipalComoBase() {
        if (GerenciadorTelas.getCrepzTray() != null && GerenciadorTelas.getCrepzTray().isOnTray()) {
            ocultarIconeTray();
        }
        telaPadrao = TelaPadrao.J_PRINCIPAL;
        GerenciadorTelas.getMini().dispose();
        GerenciadorTelas.getFilaReproducao().dispose();
        GerenciadorTelas.getPrincipal().setVisible(true);
    }

    public void setFilaComoBase() {
        if (GerenciadorTelas.getCrepzTray() != null && GerenciadorTelas.getCrepzTray().isOnTray()) {
            ocultarIconeTray();
        }
        setFonteReproducao(FonteReproducao.FILA_REPRODUCAO);
        telaPadrao = TelaPadrao.J_FILA;
        GerenciadorTelas.getMini().dispose();
        GerenciadorTelas.getPrincipal().dispose();
        GerenciadorTelas.getFilaReproducao().setVisible(true);
    }

    public boolean isPlayListVisible() {
        return GerenciadorTelas.getPlayList().isVisible();
    }

    public boolean isBibliotecaVisible() {
        return GerenciadorTelas.getBiblioteca().isVisible();
    }

    public void sair() {
        Configuracaoes.set(Configuracaoes.CONF_VISIB_PRINCIPAL, GerenciadorTelas.getPrincipal().isVisible());
        Configuracaoes.set(Configuracaoes.CONF_VISIB_MINI, GerenciadorTelas.getMini().isVisible());
        Configuracaoes.set(Configuracaoes.CONF_VISIB_BIBLIOTECA, GerenciadorTelas.getBiblioteca().isVisible());
        Configuracaoes.set(Configuracaoes.CONF_VISIB_FILA, GerenciadorTelas.getFilaReproducao().isVisible());
        Configuracaoes.set(Configuracaoes.CONF_VISIB_PLAYLIST, GerenciadorTelas.getPlayList().isVisible());
        Configuracaoes.set(Configuracaoes.CONF_LISTA_ABERTA, GerenciadorTelas.getPlayList().getPlaylistAberta() == null ? -1 : GerenciadorTelas.getPlayList().getPlaylistAberta().getId());
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
        GerenciadorTelas.getPlayList().addMusicas(musicas);
    }

    public void addToPlayList(Musica musica) {
        GerenciadorTelas.getPlayList().addMusica(musica);
    }

    private void mostrarIconeTray() {
        if (SystemTray.isSupported()) {
            if (!GerenciadorTelas.getCrepzTray().isOnTray()) {
                GerenciadorTelas.getCrepzTray().toTray();
            }
        }
    }

    private void ocultarIconeTray() {
        if (SystemTray.isSupported()) {
            GerenciadorTelas.getCrepzTray().someTray();
        }
    }

    private void startMultimidiaKeys() {
        try {
            if (System.getProperty("os.name").indexOf("Windows") > -1) {
                if (System.getProperty("sun.arch.data.model").equals("64")) {
                    JIntellitype.setLibraryLocation(GerenciadorTelas.getPrincipal().getClass().getResource("com/hotkey/windows/JIntellitype64.dll").getFile());
                } else {
                    JIntellitype.setLibraryLocation(GerenciadorTelas.getPrincipal().getClass().getResource("com/hotkey/windows/JIntellitype.dll").getFile());
                }

                if (JIntellitype.checkInstanceAlreadyRunning("JIntellitype Test Application")) {
                    System.exit(1);
                }
                if (!JIntellitype.isJIntellitypeSupported()) {
                    System.exit(1);
                }
                GerenciadorTelas.getPrincipal().initJIntellitype();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initLookAndFeel() {

        System.setProperty("Quaqua.tabLayoutPolicy", "wrap");
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");

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
        me = new Carregador();
        me.inicializarConfiguracoes();
    }

    public void mostrarFilaReproducao() {
        setFilaComoBase();
    }

    public void ocultarFilaReproducao() {
        setPrincipalComoBase();
//        GerenciadorTelas.getFilaReproducao().setVisible(false);
    }

    public boolean isFilaReproducaoVisivel() {
        return GerenciadorTelas.getFilaReproducao().isVisible();
    }

    public enum FonteReproducao {

        PLAY_LIST,
        FILA_REPRODUCAO,
        AVULSO
    }

    protected static Carregador getMe() {
        return me;
    }

    protected Musiquera getMusiquera() {
        return musiquera;
    }

    public Icones getIcones() {
        return icones;
    }
    
}
