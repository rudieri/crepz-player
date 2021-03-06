/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.main;

import com.config.Configuracao;
import com.config.ConfiguracaoListener;
import com.config.Configuracoes;
import com.config.constantes.TelaPadrao;
import com.graficos.Icones;
import com.hotkey.linux.Comando;
import com.hotkey.linux.DisparaComando;
import com.hotkey.linux.Ouvinte;
import com.hotkey.linux.TipoComando;
import com.main.gui.Aguarde;
import com.main.gui.JMini;
import com.melloware.jintellitype.JIntellitype;
import com.musica.LinhaDoTempo;
import com.musica.MusicaGerencia;
import com.musica.MusicaS;
import com.musica.Musiquera;
import com.musica.Musiquera.PropriedadesMusica;
import com.serial.PortaCDs;
import com.utils.ComandosSO;
import com.utils.file.FileUtils;
import java.awt.SystemTray;
import java.awt.Window;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

/**
 *
 * @author rudieri
 */
public class Carregador extends Musiquera implements ConfiguracaoListener {

    private static Carregador me;
    private static File arquivoBloqueio;
    private Icones icones;
    private FonteReproducao fonteReproducao;
    private static TelaPadrao telaPadrao;
    private JCheckBoxMenuItem[] menusLnF;

    public Carregador() {
        super();
        Aguarde aguarde = new Aguarde();
        aguarde.mostrar();
        aguarde.intro();
        fonteReproducao = FonteReproducao.AVULSO;
        telaPadrao = Configuracoes.TELA_PADRAO.getValor();
        initLookAndFeel();
//        startBanco();
        createLog();
        aguarde.ocultar();
        // Deixa a tela padrão visível.

    }

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
    public MusicaS getNextMusica() {
        if (fonteReproducao == FonteReproducao.FILA_REPRODUCAO) {
            return GerenciadorTelas.getFilaReproducao().getProxima();
        } else {
            return GerenciadorTelas.getPlayList().getProxima();
        }
    }

    @Override
    public MusicaS getPreviousMusica() {
        if (fonteReproducao == FonteReproducao.FILA_REPRODUCAO) {
            return GerenciadorTelas.getFilaReproducao().getAnterior();
        } else {
            return GerenciadorTelas.getPlayList().getAnterior();
        }
    }

    @Override
    public void atualizaLabels(String nome, int bits, String tempo, int freq) {
        for (int i = 0; i < getTodasTelas().size(); i++) {
            getTodasTelas().get(i).atualizaLabels(nome, bits, tempo, freq);

        }
    }

    @Override
    public void setPropriedadesMusica(PropriedadesMusica propriedadesMusica) {
        MusicaS musica = getMusica();
        musica.setTempo(propriedadesMusica.getTempoTotal());
//        try {
//            MusicaBD.alterar(musica);
//            MusicaBD.carregar(musica);
//        } catch (Exception ex) {
//            Logger.getLogger(JFilaReproducao.class.getName()).log(Level.SEVERE, null, ex);
//        }
        getTelaPrincipal().propriedadesMusicaChanged(propriedadesMusica);
        if (fonteReproducao == FonteReproducao.FILA_REPRODUCAO) {
            GerenciadorTelas.getFilaReproducao().propriedadesMusicaChanged(propriedadesMusica);
        }
    }

    @Override
    public void configuracaoModificada(Configuracao configuracao) {
        if (configuracao == Configuracoes.MIXER) {
            setMixerName(Configuracoes.MIXER.getValor());
        }
    }

    private void inicializarConfiguracoes(String[] args) {
        icones = new Icones();
        icones.loadIcons("crepz");
        startMultimidiaKeys();
        Configuracoes.MIXER.addConfiguracaoListener(this);
        setMixerName(Configuracoes.MIXER.getValor());
        ArrayList<File> abrir = new ArrayList<File>();
        for (String nomeArq : args) {
            File f = new File(nomeArq);
            if (MusicaGerencia.ehValido(f)) {
                abrir.add(f);
            }
        }
        if (abrir.size() > 0) {
            File[] files = new File[abrir.size()];
            GerenciadorTelas.getPlayList(abrir.toArray(files)).setVisible(true);
            Configuracoes.FONTE_REPRODUCAO.setValor(FonteReproducao.PLAY_LIST, true);
            if (telaPadrao == TelaPadrao.COMO_ESTAVA) {
                telaPadrao = TelaPadrao.J_PRINCIPAL;
            }
            tocarProxima();
        }
        setTelaBase(telaPadrao);
        if (!Configuracoes.PASTAS_SCANER.getValor().isEmpty()) {
            GerenciadorTelas.getScan();
        }

        Ouvinte ouvinte = new Ouvinte(this);

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

//    public static void startBanco() {
//        Server server = new org.hsqldb.server.Server();
//        server.setDatabaseName(0, "BD");
//        server.setDatabasePath(0,  ComandosSO.getLocalCrepzPath() + "BD");
//        server.start();
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
        GerenciadorTelas.getPlayList().setVisible(true);
        setFonteReproducao(FonteReproducao.PLAY_LIST);
    }

    public void ocultarPlayList() {
        if (GerenciadorTelas.isPlayListCarregado()) {
            GerenciadorTelas.getPlayList().setVisible(false);
        }
    }

    public void mostrarBiblioteca() {
        GerenciadorTelas.getBiblioteca().setVisible(true);
    }

    public void ocultarBiblioteca() {
        if (GerenciadorTelas.isBibliotecaCarregado()) {
            GerenciadorTelas.getBiblioteca().setVisible(false);
        }
    }

    public Window getWindowPrincipal() {
        switch (telaPadrao) {
            case J_FILA:
                return GerenciadorTelas.getFilaReproducao();
            case J_PRINCIPAL:
                return GerenciadorTelas.getPrincipal();
            case J_MINI:
                return GerenciadorTelas.getMini();
            case COMO_ESTAVA:
                if (GerenciadorTelas.isFilaReproducaoCarregada() && isFilaReproducaoVisivel()) {
                    return GerenciadorTelas.getFilaReproducao();
                }
                if (GerenciadorTelas.isPrincipalCarregado() && isPrincipalVisible()) {
                    return GerenciadorTelas.getPrincipal();
                }
                if (GerenciadorTelas.isMiniCarregado() && isMiniVisible()) {
                    return GerenciadorTelas.getMini();
                }
                break;

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
            case COMO_ESTAVA:
                if (GerenciadorTelas.isFilaReproducaoCarregada() && isFilaReproducaoVisivel()) {
                    return GerenciadorTelas.getFilaReproducao();
                }
                if (GerenciadorTelas.isPrincipalCarregado() && isPrincipalVisible()) {
                    return GerenciadorTelas.getPrincipal();
                }
                if (GerenciadorTelas.isMiniCarregado() && isMiniVisible()) {
                    return GerenciadorTelas.getMini();
                }
                break;

        }
        ArrayList<Notificavel> todasTelas = getTodasTelas();
        if (todasTelas.isEmpty()) {
            return GerenciadorTelas.getPrincipal();
        }
        return todasTelas.get(0);
    }

    public ArrayList<Notificavel> getTodasTelas() {
        ArrayList<Notificavel> list = new ArrayList<Notificavel>(3);
        if (GerenciadorTelas.isFilaReproducaoCarregada()) {
            list.add(GerenciadorTelas.getFilaReproducao());
        }
        if (GerenciadorTelas.isPrincipalCarregado()) {
            list.add(GerenciadorTelas.getPrincipal());
        }
        if (GerenciadorTelas.isMiniCarregado()) {
            list.add(GerenciadorTelas.getMini());
        }
        return list;
    }

    private void abrirUltimaConfiguracao() {
        boolean carregouTela = false;
        if (Configuracoes.VISIB_PRINCIPAL.getValor()) {
            GerenciadorTelas.getPrincipal().setVisible(true);
            carregouTela = true;
        }
        if (Configuracoes.VISIB_MINI.getValor()) {
            GerenciadorTelas.getMini().setVisible(true);
            GerenciadorTelas.getCrepzTray().toTray();
            carregouTela = true;
        }
        if (Configuracoes.VISIB_PLAYLIST.getValor()) {
            GerenciadorTelas.getPlayList().setVisible(true);
        }
        if (Configuracoes.VISIB_BIBLIOTECA.getValor()) {
            GerenciadorTelas.getBiblioteca().setVisible(true);
        }
        if (Configuracoes.VISIB_FILA.getValor()) {
            GerenciadorTelas.getFilaReproducao().setVisible(true);
            carregouTela = true;

        }
        if (!carregouTela) {
            GerenciadorTelas.getFilaReproducao().setVisible(true);
        }
        FonteReproducao fr = Configuracoes.FONTE_REPRODUCAO.getValor();
        if (fr == FonteReproducao.PLAY_LIST) {
            GerenciadorTelas.getPlayList().setPlayListAberta(Configuracoes.LISTA_ABERTA.getValor());
        }
        setFonteReproducao(fr);
        if (Configuracoes.MUSICA_CONTINUA_ONDE_PAROU.getValor()
                && Configuracoes.MUSICA_REPRODUZINDO.getValor() != null) {
            MusicaS musica = Configuracoes.MUSICA_REPRODUZINDO.getValor();
            if (musica == null) {
                tocarProxima();
            } else {
                if (Configuracoes.MUSICA_REPRODUZINDO_TEMPO.getValor() != -1) {
                    abrir(musica, Configuracoes.MUSICA_REPRODUZINDO_TEMPO.getValor(), false);

                } else {
                    abrir(musica, 0, false);
                }
                if (fr == FonteReproducao.PLAY_LIST) {
                    GerenciadorTelas.getPlayList().selecionarMusica(musica);
                }
                if (fr == FonteReproducao.FILA_REPRODUCAO) {
                    GerenciadorTelas.getFilaReproducao().selecionaMusica(musica);
                }
            }
        } else {
            tocarProxima();
        }
    }

    public void setMiniComoBase() {
        if (GerenciadorTelas.isPrincipalCarregado()) {
            GerenciadorTelas.getPrincipal().dispose();
        }
        if (GerenciadorTelas.isFilaReproducaoCarregada()) {
            GerenciadorTelas.getFilaReproducao().dispose();
        }
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
        if (GerenciadorTelas.isMiniCarregado()) {
            GerenciadorTelas.getMini().dispose();
        }
        if (GerenciadorTelas.isFilaReproducaoCarregada()) {
            GerenciadorTelas.getFilaReproducao().dispose();
        }
        GerenciadorTelas.getPrincipal().setVisible(true);
        telaPadrao = TelaPadrao.J_PRINCIPAL;
    }

    public void setFilaComoBase() {
        if (GerenciadorTelas.isCrepzTrayCarregado() && GerenciadorTelas.getCrepzTray().isOnTray()) {
            ocultarIconeTray();
        }
        setFonteReproducao(FonteReproducao.FILA_REPRODUCAO);
        telaPadrao = TelaPadrao.J_FILA;
        if (GerenciadorTelas.isMiniCarregado()) {
            GerenciadorTelas.getMini().dispose();
        }
        if (GerenciadorTelas.isPrincipalCarregado()) {
            GerenciadorTelas.getPrincipal().dispose();
        }
        GerenciadorTelas.getFilaReproducao().setVisible(true);

    }

    public boolean isPrincipalVisible() {
        return GerenciadorTelas.getPrincipal().isVisible();
    }

    public boolean isMiniVisible() {
        return GerenciadorTelas.getMini().isVisible();
    }

    public boolean isPlayListVisible() {
        return GerenciadorTelas.getPlayList().isVisible();
    }

    public boolean isBibliotecaVisible() {
        return GerenciadorTelas.getBiblioteca().isVisible();
    }

    public void sair() {
        if (Configuracoes.MUSICA_CONTINUA_ONDE_PAROU.getValor()) {
            Configuracoes.MUSICA_REPRODUZINDO.setValor((isPlaying() || isPaused()) ? getMusica() : null, false);
            Configuracoes.MUSICA_REPRODUZINDO_TEMPO.setValor(isPlaying() || isPaused() ? getTempoAtual() : -1l, false);
        }

        if (GerenciadorTelas.isPrincipalCarregado()) {
            Configuracoes.VISIB_PRINCIPAL.setValor(GerenciadorTelas.getPrincipal().isVisible(), false);
            Configuracoes.LOCAL_PRINCIPAL.setValor(GerenciadorTelas.getPrincipal().getBounds(), false);
        } else {
            Configuracoes.VISIB_PRINCIPAL.setValor(false, false);
        }
        if (GerenciadorTelas.isMiniCarregado()) {
            Configuracoes.VISIB_MINI.setValor(GerenciadorTelas.getMini().isVisible(), false);
            Configuracoes.LOCAL_MINI.setValor(GerenciadorTelas.getMini().getBounds(), false);
        } else {
            Configuracoes.VISIB_MINI.setValor(false, false);
        }
        if (GerenciadorTelas.isBibliotecaCarregado()) {
            Configuracoes.VISIB_BIBLIOTECA.setValor(GerenciadorTelas.getBiblioteca().isVisible(), false);
            Configuracoes.LOCAL_BIBLIOTECA.setValor(GerenciadorTelas.getBiblioteca().getBounds(), false);
        } else {
            Configuracoes.VISIB_BIBLIOTECA.setValor(false, false);
        }
        if (GerenciadorTelas.isFilaReproducaoCarregada()) {
            Configuracoes.VISIB_FILA.setValor(GerenciadorTelas.getFilaReproducao().isVisible(), false);
            Configuracoes.LOCAL_FILA.setValor(GerenciadorTelas.getFilaReproducao().getBounds(), false);
        } else {
            Configuracoes.VISIB_FILA.setValor(false, false);
        }
        if (GerenciadorTelas.isPlayListCarregado()) {
            Configuracoes.VISIB_PLAYLIST.setValor(GerenciadorTelas.getPlayList().isVisible(), false);
            Configuracoes.LISTA_ABERTA.setValor(GerenciadorTelas.getPlayList().getPlaylistAberta() == null
                    ? null : GerenciadorTelas.getPlayList().getPlaylistAberta().getNome(), false);
            Configuracoes.LOCAL_PLAYLIST.setValor(GerenciadorTelas.getPlayList().getBounds(), false);
        } else {
            Configuracoes.VISIB_PLAYLIST.setValor(false, false);
        }
        Configuracoes.FONTE_REPRODUCAO.setValor(fonteReproducao, false);
        Configuracoes.BALANCO.setValor((byte) getBalanco(), false);
        Configuracoes.VOLUME.setValor((byte) getVolume(), false);
        Configuracoes.LOOK_AND_FEEL.setValor(UIManager.getLookAndFeel().getID(), false);

        Configuracoes.gravar();
        PortaCDs.salvar();
        if (arquivoBloqueio != null) {
            arquivoBloqueio.deleteOnExit();
        }
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

    public void addToPlayList(ArrayList<MusicaS> musicas) {
        GerenciadorTelas.getPlayList().addMusicas(musicas);
    }

    public void addToPlayList(MusicaS musica) {
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
            e.printStackTrace(System.err);
        }
    }

    private void initLookAndFeel() {

//        System.setProperty("Quaqua.tabLayoutPolicy", "wrap");
        try {
            LookAndFeelInfo[] installedLookAndFeels = UIManager.getInstalledLookAndFeels();
            menusLnF = new JCheckBoxMenuItem[installedLookAndFeels.length];
            for (int i = 0; i < installedLookAndFeels.length; i++) {
                LookAndFeelInfo info = installedLookAndFeels[i];
                final JCheckBoxMenuItem jCheckBoxMenuItem = new JCheckBoxMenuItem(info.getName());
                jCheckBoxMenuItem.addItemListener(new ItemListener() {
                    @Override
                    public void itemStateChanged(ItemEvent e) {
                        if (e.getStateChange() == ItemEvent.SELECTED) {
                            try {
                                LookAndFeelInfo findLookAndFeel = findLookAndFeel(jCheckBoxMenuItem.getText());
                                if (UIManager.getLookAndFeel().getName().contains(jCheckBoxMenuItem.getText())
                                        || UIManager.getLookAndFeel().getID().contains(jCheckBoxMenuItem.getText())
                                        || UIManager.getLookAndFeel().getClass().getName().equals(findLookAndFeel.getClassName())) {
                                    return;
                                }
                                setLookAndFeel(findLookAndFeel);
                                for (JCheckBoxMenuItem menuItem : menusLnF) {
                                    if (menuItem != jCheckBoxMenuItem && menuItem.isSelected()) {
                                        menuItem.setSelected(false);
                                    }
                                }
                                updateTelasLookAndFeel();
                            } catch (Exception ex) {
                                Logger.getLogger(Carregador.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    }
                });
                menusLnF[i] = jCheckBoxMenuItem;
            }

            String lnfSalvo = Configuracoes.LOOK_AND_FEEL.getValor();
            //            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");

            boolean achou = false;
            if (lnfSalvo != null && !lnfSalvo.isEmpty()) {
                LookAndFeelInfo findLookAndFeel = findLookAndFeel(lnfSalvo);
                if (findLookAndFeel != null) {
                    achou = true;
                    setLookAndFeel(findLookAndFeel);

                } else {
                    achou = false;
                }
            }
            if (!achou) {

                String[] lnfPreferido = {"nimbus", "gtk+"};
                for (String nome : lnfPreferido) {
                    LookAndFeelInfo lookAndFeelInfo = findLookAndFeel(nome);
                    if (lookAndFeelInfo != null) {
                        setLookAndFeel(lookAndFeelInfo);
//                    UIDefaults defaults = UIManager.getLookAndFeel().getDefaults();
//                    
//                    for (Map.Entry<Object, Object> entry : defaults.entrySet()) {
//                        Object chave = entry.getKey();
//                        Object valor = entry.getValue();
//                        System.out.println("=> " + chave.toString() + " = " + (valor == null ? "null" : valor.toString() + "("+valor.getClass().getSimpleName()+")"));
//                    }

                        break;
                    }
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace(System.err);
        }
    }

    private LookAndFeelInfo findLookAndFeel(String nome) {

        LookAndFeelInfo[] installedLookAndFeels = UIManager.getInstalledLookAndFeels();
        for (LookAndFeelInfo lookAndFeelInfo : installedLookAndFeels) {
            if (lookAndFeelInfo.getName().toLowerCase().contains(nome.toLowerCase())) {
                return lookAndFeelInfo;
            }
        }
        return null;
    }

    public void updateTelasLookAndFeel() throws Exception {
        if (GerenciadorTelas.isPrincipalCarregado()) {
            SwingUtilities.updateComponentTreeUI(GerenciadorTelas.getPrincipal());
        }
        if (GerenciadorTelas.isPlayListCarregado()) {
            SwingUtilities.updateComponentTreeUI(GerenciadorTelas.getPlayList());
            GerenciadorTelas.getPlayList().lookAndFeelChanged();
        }
        if (GerenciadorTelas.isBibliotecaCarregado()) {
            SwingUtilities.updateComponentTreeUI(GerenciadorTelas.getBiblioteca());
        }
        if (GerenciadorTelas.isFilaReproducaoCarregada()) {
            SwingUtilities.updateComponentTreeUI(GerenciadorTelas.getFilaReproducao());
            GerenciadorTelas.getFilaReproducao().lookAndFeelChanged();
        }
        if (GerenciadorTelas.isMiniCarregado()) {
            SwingUtilities.updateComponentTreeUI(GerenciadorTelas.getMini());
        }
    }

    public void setLookAndFeel(LookAndFeelInfo lookAndFeelInfo) throws Exception {
        UIManager.setLookAndFeel(lookAndFeelInfo.getClassName());
        UIManager.getLookAndFeelDefaults().put("Slider.paintValue", false);
        PropertyChangeEvent changeEvent = new PropertyChangeEvent(UIManager.getLookAndFeelDefaults(), "Slider.paintValue", true, false);
        PropertyChangeListener[] propertyChangeListeners = UIManager.getLookAndFeel().getDefaults().getPropertyChangeListeners();
        for (PropertyChangeListener propertyChangeListener : propertyChangeListeners) {
            propertyChangeListener.propertyChange(changeEvent);
        }
        for (JCheckBoxMenuItem jCheckBoxMenuItem : menusLnF) {
            if (jCheckBoxMenuItem.getText().contains(lookAndFeelInfo.getName()) || lookAndFeelInfo.getName().contains(jCheckBoxMenuItem.getText())) {
                jCheckBoxMenuItem.setSelected(true);
            } else {
                jCheckBoxMenuItem.setSelected(false);

            }
        }
    }

    public JCheckBoxMenuItem[] getMenusLnF() {
        return menusLnF;
    }

    public boolean isRandom() {
        return Configuracoes.RANDOM_ATIVO.getValor();
    }

    public void setRandom(boolean random) {
        Configuracoes.RANDOM_ATIVO.setValor(random, true);
    }

    public void setRepeat(boolean repeat) {
        Configuracoes.REPEAT_ATIVO.setValor(repeat, true);
    }

    public void mostrarFilaReproducao() {
        setFilaComoBase();
    }

    public void ocultarFilaReproducao() {
        setPrincipalComoBase();
    }

    public boolean isFilaReproducaoVisivel() {
        return GerenciadorTelas.getFilaReproducao().isVisible();
    }

    public static Carregador getMe() {
        return me;
    }

    protected Musiquera getMusiquera() {
        return this;
    }

    public Icones getIcones() {
        return icones;
    }

    /**
     * Gera um arquivo de bloqueio, para que não se possa abrir mais de uma
     * instância do Crepz ao mesmo tempo.
     *
     * @return Se essa instância é válida ou não.
     */
    public static boolean gerarBloqueio() {
        try {
            long time = System.currentTimeMillis();

            File pasta = new File(ComandosSO.getLocalCrepz(), "etc");
            if (!pasta.exists()) {
                pasta.mkdirs();
            }
            arquivoBloqueio = new File(pasta, "lock");
            if (arquivoBloqueio.exists()) {
                String leArquivo = FileUtils.leArquivo(arquivoBloqueio).toString().replaceAll("[^0-9]", "");
                long timeArquivo;
                if (leArquivo.length() != 0) {
                    timeArquivo = Long.parseLong(leArquivo.toString());
                } else {
                    timeArquivo = 0;
                }

                if (timeArquivo == 0) {
                    FileUtils.gravaArquivo(Long.toString(time), arquivoBloqueio.getAbsolutePath());
                } else {
                    if (time - timeArquivo > 10000) {
                        boolean consegui = DisparaComando.disparar(new Comando(TipoComando.PING));
                        if (!consegui) {
                            arquivoBloqueio.delete();
                            return gerarBloqueio();
                        }
                    }
                    return false;
                }
            } else {
                arquivoBloqueio.createNewFile();
                FileUtils.gravaArquivo(Long.toString(time), arquivoBloqueio.getAbsolutePath());
            }
        } catch (Exception ex) {
            Logger.getLogger(Carregador.class.getName()).log(Level.SEVERE, null, ex);

        }
        return true;
    }

    public static void main(String[] args) {
        boolean possoContinuar = gerarBloqueio();
        if (possoContinuar) {
            me = new Carregador();
            me.inicializarConfiguracoes(args);
        } else {
            if (args.length > 0) {
                System.out.println("Tentando abrir: " + args[0]);
                DisparaComando.disparar(new Comando(TipoComando.ADICIONAR_LISTA, args[0]));
            }
            DisparaComando.disparar(new Comando(TipoComando.TO_FRONT));
        }
    }
}
