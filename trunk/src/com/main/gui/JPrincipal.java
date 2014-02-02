package com.main.gui;

import com.config.Configuracoes;
import com.config.JConfiguracao;
import com.help.JHelp;
import com.help.JSobre;
import com.main.Carregador;
import com.main.Notificavel;
import com.melloware.jintellitype.HotkeyListener;
import com.melloware.jintellitype.IntellitypeListener;
import com.melloware.jintellitype.JIntellitype;
import com.musica.MusicaGerencia;
import com.musica.MusicaS;
import com.musica.Musiquera.PropriedadesMusica;
import com.utils.file.DiretorioUtils;
import com.utils.file.FiltroArquivoGenerico;
import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeListener;
import javazoom.jlgui.basicplayer.BasicPlayerEvent;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * NewJFrame.java
 *
 * Created on 29/05/2010, 08:38:25
 */
/**
 *
 * @author manchini
 */
public class JPrincipal extends javax.swing.JFrame implements HotkeyListener, IntellitypeListener, Notificavel, ActionListener, MouseWheelListener, WindowListener, ChangeListener, MouseListener, Runnable {

    public static final Color COLOR_BRANCO_ALPHA = new Color(1f, 1f, 1f, 0.5f);
    private int estado = 0;
    private JFileChooser jFileChooser;
    private int volAnt;
    private JConfiguracao configuracao;
    private final Carregador carregador;

    public JPrincipal(Carregador carregador) {
        initComponents();
        configuracao = new JConfiguracao(this, false);
        this.carregador = carregador;
        //--------------------------
        jButton_Play.setName("jButton_Play");
        jButton_Next.setName("jButton_Next");
        jButton_Ant.setName("jButton_Ant");
        jButton_Stop.setName("jButton_Stop");
        jToggle_Repeat.setName("jToggle_Repeat");
        jToggle_Random.setName("jToggle_Random");
        // _conf.getAllValores();
        //  configuracao = new JConfiguracao(this, true);
        inicializaIcones();
        //scan.setTempo(1);
        startEvents();
        try {
            jFileChooser = new JFileChooser();
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
        jFileChooserImportar.setFileFilter(FiltroArquivoGenerico.FILTRO_MUSICA);
        jFileChooserImportar.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        jProgressBar1.setVisible(false);
        JCheckBoxMenuItem[] menusLnF = carregador.getMenusLnF();
        for (JCheckBoxMenuItem jCheckBoxMenuItem : menusLnF) {
            jMenuLnF.add(jCheckBoxMenuItem);
        }
        SwingUtilities.updateComponentTreeUI(jMenuLnF);
    }

    @Override
    public void tempoEh(double v) {
        if (ajusteDeTempo) {
            return;
        }
        jSlider_Tempo.setValue((int) (jSlider_Tempo.getMaximum() * v));
    }

    @Override
    public void tempoEhHMS(String hms) {
        jLabel_tempo.setText(hms);
        jSlider_Tempo.setToolTipText(hms);
    }

    @Override
    public void propriedadesMusicaChanged(PropriedadesMusica propriedadesMusica) {
    }

//    @Override
//    public void tempoTotalEhHMS(String hms) {
//        jLabel_tempoTotal.setText(hms);
//    }
    @Override
    public void eventoNaMusica(int tipo) {
        switch (tipo) {
            case BasicPlayerEvent.PAUSED:

                break;
            case BasicPlayerEvent.PLAYING:
            case BasicPlayerEvent.RESUMED:

                break;
            case BasicPlayerEvent.STOPPED:

                break;
            default:
                System.err.print("Evento desconhecio. Id: " + tipo);
                break;
        }
    }

    /**
     * Atualiza labels da tela principal
     *
     * @param nome Nome da musica, autor e album
     * @param tempo Tempo em Minutos e Seguntos
     * @param bits KiloBits/s
     * @param freq Frequencia em ?Hz
     */
    @Override
    public void atualizaLabels(String nome, int bits, String tempo, int freq) {
        jLabel_Musica.setText(nome.replaceAll("  ", " ").trim());
        jLabel_tempoTotal.setText(tempo);
        jLabel_bit.setText(bits + " kbps");
        jLabel_freq.setText(freq + " KHz");
    }

    /**
     * Muda o icones do label que contenha o nome indicado.
     *
     * @param quem nome do label.
     * @param _icone
     */
    public void atualizaIcone(String quem, Icon _icone) {
        atualizaIcone(this.getContentPane(), quem, _icone);
    }

    public void atualizaIcone(String quem, String texto) {
        atualizaIcone(this.getContentPane(), quem, texto);
    }

    public void atualizaIcone(Container root, String quem, Icon _icone) {
        for (int i = 0; i < root.getComponentCount(); i++) {
            if (root.getComponent(i) instanceof JLabel) {
                if (root.getComponent(i).getName() != null && root.getComponent(i).getName().equals(quem)) {
                    ((JLabel) root.getComponent(i)).setIcon(_icone);
                    return;
                }
            } else {
                if (root.getComponent(i) instanceof JPanel) {
                    atualizaIcone((JPanel) root.getComponent(i), quem, _icone);
                }
            }
        }
    }

    /**
     * Muda o ToolTipText do label que contenha o nome indicado.
     *
     * @param root
     * @param quem nome do label.
     * @param texto texto a ser colocado como tooltip
     */
    public void atualizaIcone(Container root, String quem, String texto) {
        for (int i = 0; i < root.getComponentCount(); i++) {
            if (root.getComponent(i) instanceof JLabel) {
                if (root.getComponent(i).getName() != null && root.getComponent(i).getName().equals(quem)) {
                    ((JLabel) root.getComponent(i)).setToolTipText(texto);
                    return;
                }
            } else {
                if (root.getComponent(i) instanceof JPanel) {
                    atualizaIcone((JPanel) root.getComponent(i), quem, texto);
                }
            }
        }
    }

    public void setBalaco(int b) {
        jSlider_Balanco.setValue(b);
    }

    /*   public void setTocando(boolean b) {
     tocando = b;
     }
     */
    public String miliSegundosEmMinSeq(long mili) {
        SimpleDateFormat sdf = new SimpleDateFormat("ss");
        Date date = null;
        try {
            date = sdf.parse(String.valueOf(mili / 1000000));
        } catch (Exception ex) {
            ex.printStackTrace(System.err);
        }

        return new java.text.SimpleDateFormat("HH:mm:ss").format(date);
    }

    public File telaAbrirArquivo() throws Exception {

        // restringe a amostra a diretorios apenas
        jFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        jFileChooser.setDialogTitle("Abrir Arquivo");

        int res = jFileChooser.showOpenDialog(null);

        if (res == JFileChooser.APPROVE_OPTION) {
            return jFileChooser.getSelectedFile();
        }
        return null;
//        else {
//            throw new Exception("Voce nao selecionou nenhum diretorio.");
//        }
    }

    private void importarMusicas() {
        new Thread(this).start();
    }

    @Override
    public void run() {
        importarMusicasRun();
    }

    private void importarMusicasRun() {
        int result = jFileChooserImportar.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            jProgressBar1.setVisible(true);
            try {
                ArrayList<File> lista = new ArrayList<File>(2000);
                lista.addAll(Arrays.asList(jFileChooserImportar.getSelectedFiles()));
                if (lista.isEmpty()) {
                    lista.add(jFileChooserImportar.getSelectedFile());
                }
                int nroFiles = DiretorioUtils.calculaQuantidadeArquivos(lista);
                MusicaGerencia.mapearDiretorio(lista, new ArrayList<MusicaS>(nroFiles), jProgressBar1, nroFiles);
            } catch (Exception ex) {
                Logger.getLogger(JPrincipal.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        jProgressBar1.setVisible(false);
    }

    private void inicializaIcones() {
        //Estado inicial do botão (está Stop);
        jButton_Play.setIcon(carregador.getIcones().getPlayIcon32());
        //Se tiver tocando
        if (carregador.isPlaying()) {
            jButton_Play.setIcon(carregador.getIcones().getPauseIcon32());
        }
        //Se tiver pause
        if (carregador.isPlaying()) {
            jButton_Play.setIcon(carregador.getIcones().getPlayIcon32());
        }
        jButton_Stop.setIcon(carregador.getIcones().getStopIcon32());
        jButton_Next.setIcon(carregador.getIcones().getFrenteIcon32());
        jButton_Ant.setIcon(carregador.getIcones().getVoltaIcon32());

        if (carregador.isRandom()) {
            jToggle_Random.setIcon(carregador.getIcones().getRandomOnIcon32());
        } else {
            jToggle_Random.setIcon(carregador.getIcones().getRandomOffIcon32());
        }
        if (carregador.isRepeat()) {
            jToggle_Repeat.setIcon(carregador.getIcones().getRepeatOnIcon32());
        } else {
            jToggle_Repeat.setIcon(carregador.getIcones().getRepeatOffIcon32());
        }
        setIconImage(carregador.getIcones().getCrepzIcon().getImage());
    }

    public ImageIcon resizeIcons(BufferedImage im) {
        return new ImageIcon(im.getScaledInstance(17, 17, Image.SCALE_SMOOTH));

    }

    public ImageIcon resizeIcons(BufferedImage im, int l, int a) {
        return new ImageIcon(im.getScaledInstance(l, a, Image.SCALE_SMOOTH));
    }

    /*
     * (non-Javadoc)
     * @see com.melloware.jintellitype.HotkeyListener#onHotKey(int)
     */
    @Override
    public void onHotKey(int aIdentifier) {
//      output("WM_HOTKEY message received " + Integer.toString(aIdentifier));
    }

    /*
     * (non-Javadoc)
     * @see com.melloware.jintellitype.IntellitypeListener#onIntellitype(int)
     */
    @Override
    public void onIntellitype(int aCommand) {

        switch (aCommand) {
            case JIntellitype.APPCOMMAND_MEDIA_NEXTTRACK:
                carregador.tocarProxima();
                break;
            case JIntellitype.APPCOMMAND_MEDIA_PLAY_PAUSE:
                carregador.tocarPausar();
                break;
            case JIntellitype.APPCOMMAND_MEDIA_PREVIOUSTRACK:
                carregador.tocarAnterior();
                break;
            case JIntellitype.APPCOMMAND_MEDIA_STOP:
                carregador.parar();
                break;
            case JIntellitype.APPCOMMAND_VOLUME_DOWN:
                jSlider_vol.setValue(jSlider_vol.getValue() - 2);
                break;
            case JIntellitype.APPCOMMAND_VOLUME_UP:
                jSlider_vol.setValue(jSlider_vol.getValue() + 2);
                break;
            case JIntellitype.APPCOMMAND_VOLUME_MUTE:
                if (jSlider_vol.getValue() > 0) {
                    volAnt = jSlider_vol.getValue();
                    jSlider_vol.setValue(0);
                } else {
                    jSlider_vol.setValue(volAnt);
                }


                break;
//      default:
//         output("Undefined INTELLITYPE message caught " + Integer.toString(aCommand));
//         break;
        }
    }

    public void initJIntellitype() {
        try {

            JIntellitype.getInstance().addHotKeyListener(this);
            JIntellitype.getInstance().addIntellitypeListener(this);
        } catch (RuntimeException ex) {
            ex.printStackTrace(System.err);
        }
    }

    @Override
    public void setVisible(boolean b) {
        super.setVisible(b);
        if (carregador.isRandom()) {
            jToggle_Random.setIcon(carregador.getIcones().getRandomOnIcon32());
        } else {
            jToggle_Random.setIcon(carregador.getIcones().getRandomOffIcon32());
        }
        if (carregador.isRepeat()) {
            jToggle_Repeat.setIcon(carregador.getIcones().getRepeatOnIcon32());
        } else {
            jToggle_Repeat.setIcon(carregador.getIcones().getRepeatOffIcon32());
        }
        jSlider_vol.setValue(carregador.getVolume());
        jSlider_Balanco.setValue(carregador.getBalanco());
    }
    private MouseAdapter mouseAdapterArrastar = new MouseAdapter() {
        @Override
        public void mouseReleased(MouseEvent e) {
            super.mouseReleased(e);
            if (e.getButton() == MouseEvent.BUTTON3
                    && thisX == JPrincipal.this.getX() && thisY == JPrincipal.this.getY()) {
                jMenuDeContexto.show(e.getComponent(), e.getX(), e.getY());
            }
        }

        @Override
        public void mousePressed(MouseEvent e) {
            super.mousePressed(e);
            initX = e.getXOnScreen() - JPrincipal.this.getX();
            initY = e.getYOnScreen() - JPrincipal.this.getY();
            thisX = JPrincipal.this.getX();
            thisY = JPrincipal.this.getY();
        }

        @Override
        public void mouseEntered(MouseEvent e) {
            super.mouseEntered(e);
            if (e.getComponent() instanceof JLabel) {
                Graphics graphics = e.getComponent().getGraphics();
                graphics.setColor(COLOR_BRANCO_ALPHA);
                graphics.fillOval(0, 0, e.getComponent().getWidth(), e.getComponent().getHeight());
            }
        }

        @Override
        public void mouseExited(MouseEvent e) {
            super.mouseExited(e);
            if (e.getComponent() instanceof JLabel) {
                e.getComponent().repaint();
            }
        }
    };
    private MouseMotionAdapter mouseMotionArrastar = new MouseMotionAdapter() {
        @Override
        public void mouseDragged(MouseEvent e) {
            super.mouseDragged(e);
            estado++;
            if (estado == 5) {
                JPrincipal.this.setLocation(e.getXOnScreen() - initX, e.getYOnScreen() - initY);
                estado = 0;
            }
        }
    };

    private void startEvents() {
        // mouse motion
        jButton_Ant.addMouseMotionListener(mouseMotionArrastar);
        jButton_Next.addMouseMotionListener(mouseMotionArrastar);
        jButton_Play.addMouseMotionListener(mouseMotionArrastar);
        jButton_Stop.addMouseMotionListener(mouseMotionArrastar);
        jMenuBar1.addMouseMotionListener(mouseMotionArrastar);
        this.addMouseMotionListener(mouseMotionArrastar);

        // mouse adapter
        // Evento Geral
        jButton_Ant.addMouseListener(mouseAdapterArrastar);
        jButton_Next.addMouseListener(mouseAdapterArrastar);
        jButton_Play.addMouseListener(mouseAdapterArrastar);
        jButton_Stop.addMouseListener(mouseAdapterArrastar);
        jMenuBar1.addMouseListener(mouseAdapterArrastar);
        this.addMouseListener(mouseAdapterArrastar);

    }

    public int getSliderValue() {
        return jSlider_vol.getValue();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jMenuDeContexto = new javax.swing.JPopupMenu();
        jCMenuReproduz = new javax.swing.JMenu();
        jCIMenuPlay = new javax.swing.JMenuItem();
        jCIMenuStop = new javax.swing.JMenuItem();
        jCMenuVisual = new javax.swing.JMenu();
        jCCheckBarraTitulos = new javax.swing.JCheckBoxMenuItem();
        jCCheckBarraDeMenus = new javax.swing.JCheckBoxMenuItem();
        jCIMenuMinimizar = new javax.swing.JMenuItem();
        jCIMenuFechar = new javax.swing.JMenuItem();
        jFileChooserImportar = new javax.swing.JFileChooser();
        jPanel17 = new javax.swing.JPanel();
        jLabel_bib = new javax.swing.JLabel();
        jLabel_Playlist = new javax.swing.JLabel();
        jLabelFilaReproducao = new javax.swing.JLabel();
        jLabel_Edit = new javax.swing.JLabel();
        jLabel_Minimizar = new javax.swing.JLabel();
        jLabelHelp = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jLabel_Musica = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabel_bit = new javax.swing.JLabel();
        jLabel_tempoTotal = new javax.swing.JLabel();
        jLabel_freq = new javax.swing.JLabel();
        jLabel_tempo = new javax.swing.JLabel();
        jProgressBar1 = new javax.swing.JProgressBar();
        jSlider_Tempo = new javax.swing.JSlider();
        jPanel2 = new javax.swing.JPanel();
        jButton_Play = new javax.swing.JLabel();
        jButton_Stop = new javax.swing.JLabel();
        jButton_Ant = new javax.swing.JLabel();
        jButton_Next = new javax.swing.JLabel();
        jToggle_Random = new javax.swing.JLabel();
        jToggle_Repeat = new javax.swing.JLabel();
        jSlider_vol = new javax.swing.JSlider();
        jSlider_Balanco = new javax.swing.JSlider();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem_Arquivo = new javax.swing.JMenuItem();
        jMenuItemImportarArquivos = new javax.swing.JMenuItem();
        jMenuItem_Biblioteca = new javax.swing.JMenuItem();
        jMenuItem_Minimizar = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        jMenuItem_Propriedades = new javax.swing.JMenuItem();
        jMenuItem_PlayList = new javax.swing.JMenuItem();
        jMenuItem_Play = new javax.swing.JMenuItem();
        jMenuItem_Configuracoes = new javax.swing.JMenuItem();
        jMenuItem_Tema = new javax.swing.JMenuItem();
        jMenuLnF = new javax.swing.JMenu();
        jMenu3 = new javax.swing.JMenu();
        jMenuItem_Sobre = new javax.swing.JMenuItem();
        jMenuItem_Help = new javax.swing.JMenuItem();
        jMenuItem_Hadukem = new javax.swing.JMenuItem();

        jCMenuReproduz.setText("Reprodução");

        jCIMenuPlay.setText("Tocar");
        jCIMenuPlay.addActionListener(this);
        jCMenuReproduz.add(jCIMenuPlay);

        jCIMenuStop.setText("Parar");
        jCIMenuStop.addActionListener(this);
        jCMenuReproduz.add(jCIMenuStop);

        jMenuDeContexto.add(jCMenuReproduz);

        jCMenuVisual.setText("Visualização");

        jCCheckBarraTitulos.setSelected(true);
        jCCheckBarraTitulos.setText("Mostrar barra de titulos");
        jCCheckBarraTitulos.addActionListener(this);
        jCMenuVisual.add(jCCheckBarraTitulos);

        jCCheckBarraDeMenus.setSelected(true);
        jCCheckBarraDeMenus.setText("Mostrar barra de menus");
        jCCheckBarraDeMenus.addActionListener(this);
        jCMenuVisual.add(jCCheckBarraDeMenus);

        jMenuDeContexto.add(jCMenuVisual);

        jCIMenuMinimizar.setText("Minimizar");
        jCIMenuMinimizar.addActionListener(this);
        jMenuDeContexto.add(jCIMenuMinimizar);

        jCIMenuFechar.setText("Sair");
        jCIMenuFechar.addActionListener(this);
        jMenuDeContexto.add(jCIMenuFechar);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Crepz Player 1.0");
        setResizable(false);
        addWindowListener(this);

        jPanel17.setForeground(javax.swing.UIManager.getDefaults().getColor("Panel.foreground"));
        jPanel17.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 0));

        jLabel_bib.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/img/biblioteca.png"))); // NOI18N
        jLabel_bib.setToolTipText("Biblioteca");
        jLabel_bib.addMouseListener(this);
        jPanel17.add(jLabel_bib);

        jLabel_Playlist.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/img/playlist.gif"))); // NOI18N
        jLabel_Playlist.setToolTipText("Playlist");
        jLabel_Playlist.addMouseListener(this);
        jPanel17.add(jLabel_Playlist);

        jLabelFilaReproducao.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/img/fila.png"))); // NOI18N
        jLabelFilaReproducao.setToolTipText("Fila de Reproduçãod");
        jLabelFilaReproducao.addMouseListener(this);
        jPanel17.add(jLabelFilaReproducao);

        jLabel_Edit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/img/edit.png"))); // NOI18N
        jLabel_Edit.setToolTipText("Edit Propriedades MP3");
        jLabel_Edit.addMouseListener(this);
        jPanel17.add(jLabel_Edit);

        jLabel_Minimizar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/img/icon1616.png"))); // NOI18N
        jLabel_Minimizar.setToolTipText("Minimizar");
        jLabel_Minimizar.addMouseListener(this);
        jPanel17.add(jLabel_Minimizar);

        jLabelHelp.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/help/img/help.PNG"))); // NOI18N
        jLabelHelp.addMouseListener(this);
        jPanel17.add(jLabelHelp);

        getContentPane().add(jPanel17, java.awt.BorderLayout.PAGE_START);

        jPanel1.setForeground(javax.swing.UIManager.getDefaults().getColor("Panel.foreground"));
        jPanel1.setPreferredSize(new java.awt.Dimension(375, 130));
        jPanel1.setLayout(new javax.swing.BoxLayout(jPanel1, javax.swing.BoxLayout.Y_AXIS));

        jLabel_Musica.setText(":)");
        jPanel1.add(jLabel_Musica);

        jPanel3.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        jPanel3.setLayout(new java.awt.GridLayout(0, 2));

        jLabel_bit.setForeground(javax.swing.UIManager.getDefaults().getColor("Label.foreground"));
        jLabel_bit.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel_bit.setText("- Kbps");
        jPanel3.add(jLabel_bit);

        jLabel_tempoTotal.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel_tempoTotal.setText("0:00");
        jPanel3.add(jLabel_tempoTotal);

        jLabel_freq.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel_freq.setText("- KHz");
        jPanel3.add(jLabel_freq);

        jLabel_tempo.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel_tempo.setText("0:00");
        jPanel3.add(jLabel_tempo);

        jPanel1.add(jPanel3);
        jPanel1.add(jProgressBar1);

        jSlider_Tempo.setBackground(new java.awt.Color(255, 255, 255));
        jSlider_Tempo.setFont(new java.awt.Font("Cantarell", 0, 3)); // NOI18N
        jSlider_Tempo.setMaximum(1000);
        jSlider_Tempo.setToolTipText("0:00");
        jSlider_Tempo.setValue(0);
        jSlider_Tempo.setExtent(60);
        jSlider_Tempo.setMinimumSize(new java.awt.Dimension(34, 23));
        jSlider_Tempo.setPreferredSize(new java.awt.Dimension(202, 23));
        jSlider_Tempo.addMouseListener(this);
        jPanel1.add(jSlider_Tempo);

        getContentPane().add(jPanel1, java.awt.BorderLayout.CENTER);

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setForeground(javax.swing.UIManager.getDefaults().getColor("Panel.background"));
        jPanel2.setMinimumSize(new java.awt.Dimension(248, 35));
        jPanel2.setOpaque(false);
        jPanel2.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 2, 0));

        jButton_Play.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/img/icons/crepz/32/pl_play.png"))); // NOI18N
        jButton_Play.addMouseListener(this);
        jPanel2.add(jButton_Play);

        jButton_Stop.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/img/icons/crepz/32/pl_stop.png"))); // NOI18N
        jButton_Stop.addMouseListener(this);
        jPanel2.add(jButton_Stop);

        jButton_Ant.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/img/icons/crepz/32/pl_tras.png"))); // NOI18N
        jButton_Ant.addMouseListener(this);
        jPanel2.add(jButton_Ant);

        jButton_Next.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/img/icons/crepz/32/pl_frente.png"))); // NOI18N
        jButton_Next.addMouseListener(this);
        jPanel2.add(jButton_Next);

        jToggle_Random.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/img/icons/crepz/32/pl_randon_off.png"))); // NOI18N
        jToggle_Random.addMouseListener(this);
        jPanel2.add(jToggle_Random);

        jToggle_Repeat.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/img/icons/crepz/32/pl_repeat_off.png"))); // NOI18N
        jToggle_Repeat.addMouseListener(this);
        jPanel2.add(jToggle_Repeat);

        jSlider_vol.setBackground(new java.awt.Color(255, 255, 255));
        jSlider_vol.setFont(new java.awt.Font("Cantarell", 0, 3)); // NOI18N
        jSlider_vol.setPaintTicks(true);
        jSlider_vol.setToolTipText("Volume");
        jSlider_vol.setPreferredSize(new java.awt.Dimension(100, 23));
        jSlider_vol.addMouseWheelListener(this);
        jSlider_vol.addChangeListener(this);
        jPanel2.add(jSlider_vol);

        jSlider_Balanco.setBackground(new java.awt.Color(255, 255, 255));
        jSlider_Balanco.setFont(new java.awt.Font("Cantarell", 0, 3)); // NOI18N
        jSlider_Balanco.setMinimum(-100);
        jSlider_Balanco.setPaintTicks(true);
        jSlider_Balanco.setToolTipText("balanço");
        jSlider_Balanco.setValue(0);
        jSlider_Balanco.setPreferredSize(new java.awt.Dimension(70, 23));
        jSlider_Balanco.addMouseWheelListener(this);
        jSlider_Balanco.addChangeListener(this);
        jPanel2.add(jSlider_Balanco);

        getContentPane().add(jPanel2, java.awt.BorderLayout.PAGE_END);

        jMenu1.setText("Arquivo");

        jMenuItem_Arquivo.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_A, java.awt.event.InputEvent.ALT_MASK));
        jMenuItem_Arquivo.setText("Abrir Arquivo");
        jMenuItem_Arquivo.addActionListener(this);
        jMenu1.add(jMenuItem_Arquivo);

        jMenuItemImportarArquivos.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_I, java.awt.event.InputEvent.ALT_MASK));
        jMenuItemImportarArquivos.setText("Importar Músicas...");
        jMenuItemImportarArquivos.addActionListener(this);
        jMenu1.add(jMenuItemImportarArquivos);

        jMenuItem_Biblioteca.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_B, java.awt.event.InputEvent.ALT_MASK));
        jMenuItem_Biblioteca.setText("Biblioteca");
        jMenuItem_Biblioteca.addActionListener(this);
        jMenu1.add(jMenuItem_Biblioteca);

        jMenuItem_Minimizar.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_M, java.awt.event.InputEvent.ALT_MASK));
        jMenuItem_Minimizar.setText("Minimizar");
        jMenuItem_Minimizar.addActionListener(this);
        jMenu1.add(jMenuItem_Minimizar);

        jMenuBar1.add(jMenu1);

        jMenu2.setText("Editar");

        jMenuItem_Propriedades.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_P, java.awt.event.InputEvent.ALT_MASK));
        jMenuItem_Propriedades.setText("Propriedades");
        jMenuItem_Propriedades.addActionListener(this);
        jMenu2.add(jMenuItem_Propriedades);

        jMenuItem_PlayList.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_L, java.awt.event.InputEvent.ALT_MASK));
        jMenuItem_PlayList.setText("PlayList");
        jMenuItem_PlayList.addActionListener(this);
        jMenu2.add(jMenuItem_PlayList);

        jMenuItem_Play.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_SPACE, 0));
        jMenuItem_Play.setText("Play");
        jMenuItem_Play.addActionListener(this);
        jMenu2.add(jMenuItem_Play);

        jMenuItem_Configuracoes.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_C, java.awt.event.InputEvent.ALT_MASK));
        jMenuItem_Configuracoes.setText("Configurações");
        jMenuItem_Configuracoes.addActionListener(this);
        jMenu2.add(jMenuItem_Configuracoes);

        jMenuItem_Tema.setText("Tema");
        jMenuItem_Tema.setEnabled(false);
        jMenuItem_Tema.addActionListener(this);
        jMenu2.add(jMenuItem_Tema);

        jMenuBar1.add(jMenu2);

        jMenuLnF.setText("L&F");
        jMenuBar1.add(jMenuLnF);

        jMenu3.setText("Sobre");

        jMenuItem_Sobre.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.ALT_MASK));
        jMenuItem_Sobre.setText("Sobre");
        jMenuItem_Sobre.addActionListener(this);
        jMenu3.add(jMenuItem_Sobre);

        jMenuItem_Help.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_H, java.awt.event.InputEvent.ALT_MASK));
        jMenuItem_Help.setText("Help");
        jMenuItem_Help.addActionListener(this);
        jMenu3.add(jMenuItem_Help);

        jMenuItem_Hadukem.setText("Restaurar Configuração Original");
        jMenuItem_Hadukem.addActionListener(this);
        jMenu3.add(jMenuItem_Hadukem);

        jMenuBar1.add(jMenu3);

        setJMenuBar(jMenuBar1);

        setSize(new java.awt.Dimension(398, 231));
        setLocationRelativeTo(null);
    }

    // Code for dispatching events from components to event handlers.

    public void actionPerformed(java.awt.event.ActionEvent evt) {
        if (evt.getSource() == jCIMenuPlay) {
            JPrincipal.this.jCIMenuPlayActionPerformed(evt);
        }
        else if (evt.getSource() == jCIMenuStop) {
            JPrincipal.this.jCIMenuStopActionPerformed(evt);
        }
        else if (evt.getSource() == jCCheckBarraTitulos) {
            JPrincipal.this.jCCheckBarraTitulosActionPerformed(evt);
        }
        else if (evt.getSource() == jCCheckBarraDeMenus) {
            JPrincipal.this.jCCheckBarraDeMenusActionPerformed(evt);
        }
        else if (evt.getSource() == jCIMenuMinimizar) {
            JPrincipal.this.jCIMenuMinimizarActionPerformed(evt);
        }
        else if (evt.getSource() == jCIMenuFechar) {
            JPrincipal.this.jCIMenuFecharActionPerformed(evt);
        }
        else if (evt.getSource() == jMenuItem_Arquivo) {
            JPrincipal.this.jMenuItem_ArquivoActionPerformed(evt);
        }
        else if (evt.getSource() == jMenuItemImportarArquivos) {
            JPrincipal.this.jMenuItemImportarArquivosActionPerformed(evt);
        }
        else if (evt.getSource() == jMenuItem_Biblioteca) {
            JPrincipal.this.jMenuItem_BibliotecaActionPerformed(evt);
        }
        else if (evt.getSource() == jMenuItem_Minimizar) {
            JPrincipal.this.jMenuItem_MinimizarActionPerformed(evt);
        }
        else if (evt.getSource() == jMenuItem_Propriedades) {
            JPrincipal.this.jMenuItem_PropriedadesActionPerformed(evt);
        }
        else if (evt.getSource() == jMenuItem_PlayList) {
            JPrincipal.this.jMenuItem_PlayListActionPerformed(evt);
        }
        else if (evt.getSource() == jMenuItem_Play) {
            JPrincipal.this.jMenuItem_PlayActionPerformed(evt);
        }
        else if (evt.getSource() == jMenuItem_Configuracoes) {
            JPrincipal.this.jMenuItem_ConfiguracoesActionPerformed(evt);
        }
        else if (evt.getSource() == jMenuItem_Tema) {
            JPrincipal.this.jMenuItem_TemaActionPerformed(evt);
        }
        else if (evt.getSource() == jMenuItem_Sobre) {
            JPrincipal.this.jMenuItem_SobreActionPerformed(evt);
        }
        else if (evt.getSource() == jMenuItem_Help) {
            JPrincipal.this.jMenuItem_HelpActionPerformed(evt);
        }
        else if (evt.getSource() == jMenuItem_Hadukem) {
            JPrincipal.this.jMenuItem_HadukemActionPerformed(evt);
        }
    }

    public void mouseClicked(java.awt.event.MouseEvent evt) {
        if (evt.getSource() == jLabel_bib) {
            JPrincipal.this.jLabel_bibMouseClicked(evt);
        }
        else if (evt.getSource() == jLabel_Playlist) {
            JPrincipal.this.jLabel_PlaylistMouseClicked(evt);
        }
        else if (evt.getSource() == jLabelFilaReproducao) {
            JPrincipal.this.jLabelFilaReproducaoMouseClicked(evt);
        }
        else if (evt.getSource() == jLabel_Edit) {
            JPrincipal.this.jLabel_EditMouseClicked(evt);
        }
        else if (evt.getSource() == jLabel_Minimizar) {
            JPrincipal.this.jLabel_MinimizarMouseClicked(evt);
        }
        else if (evt.getSource() == jLabelHelp) {
            JPrincipal.this.jLabelHelpMouseClicked(evt);
        }
        else if (evt.getSource() == jButton_Play) {
            JPrincipal.this.jButton_PlayMouseClicked(evt);
        }
        else if (evt.getSource() == jButton_Stop) {
            JPrincipal.this.jButton_StopMouseClicked(evt);
        }
        else if (evt.getSource() == jButton_Ant) {
            JPrincipal.this.jButton_AntMouseClicked(evt);
        }
        else if (evt.getSource() == jButton_Next) {
            JPrincipal.this.jButton_NextMouseClicked(evt);
        }
        else if (evt.getSource() == jToggle_Random) {
            JPrincipal.this.jToggle_RandomMouseClicked(evt);
        }
        else if (evt.getSource() == jToggle_Repeat) {
            JPrincipal.this.jToggle_RepeatMouseClicked(evt);
        }
    }

    public void mouseEntered(java.awt.event.MouseEvent evt) {
    }

    public void mouseExited(java.awt.event.MouseEvent evt) {
    }

    public void mousePressed(java.awt.event.MouseEvent evt) {
        if (evt.getSource() == jSlider_Tempo) {
            JPrincipal.this.jSlider_TempoMousePressed(evt);
        }
    }

    public void mouseReleased(java.awt.event.MouseEvent evt) {
        if (evt.getSource() == jSlider_Tempo) {
            JPrincipal.this.jSlider_TempoMouseReleased(evt);
        }
    }

    public void mouseWheelMoved(java.awt.event.MouseWheelEvent evt) {
        if (evt.getSource() == jSlider_vol) {
            JPrincipal.this.jSlider_volMouseWheelMoved(evt);
        }
        else if (evt.getSource() == jSlider_Balanco) {
            JPrincipal.this.jSlider_BalancoMouseWheelMoved(evt);
        }
    }

    public void windowActivated(java.awt.event.WindowEvent evt) {
    }

    public void windowClosed(java.awt.event.WindowEvent evt) {
    }

    public void windowClosing(java.awt.event.WindowEvent evt) {
        if (evt.getSource() == JPrincipal.this) {
            JPrincipal.this.formWindowClosing(evt);
        }
    }

    public void windowDeactivated(java.awt.event.WindowEvent evt) {
    }

    public void windowDeiconified(java.awt.event.WindowEvent evt) {
    }

    public void windowIconified(java.awt.event.WindowEvent evt) {
    }

    public void windowOpened(java.awt.event.WindowEvent evt) {
    }

    public void stateChanged(javax.swing.event.ChangeEvent evt) {
        if (evt.getSource() == jSlider_vol) {
            JPrincipal.this.jSlider_volStateChanged(evt);
        }
        else if (evt.getSource() == jSlider_Balanco) {
            JPrincipal.this.jSlider_BalancoStateChanged(evt);
        }
    }// </editor-fold>//GEN-END:initComponents

    private void jSlider_volMouseWheelMoved(java.awt.event.MouseWheelEvent evt) {//GEN-FIRST:event_jSlider_volMouseWheelMoved
        jSlider_vol.setValue(jSlider_vol.getValue() - evt.getWheelRotation());
    }//GEN-LAST:event_jSlider_volMouseWheelMoved

    private void jSlider_BalancoStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSlider_BalancoStateChanged

        carregador.setBalanco((byte) jSlider_Balanco.getValue());
        jSlider_Balanco.setToolTipText("Balanço: " + String.valueOf(jSlider_Balanco.getValue()) + "%");
    }//GEN-LAST:event_jSlider_BalancoStateChanged

    private void jSlider_BalancoMouseWheelMoved(java.awt.event.MouseWheelEvent evt) {//GEN-FIRST:event_jSlider_BalancoMouseWheelMoved
        jSlider_Balanco.setValue(jSlider_Balanco.getValue() - evt.getWheelRotation());
    }//GEN-LAST:event_jSlider_BalancoMouseWheelMoved

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        carregador.sair();
    }//GEN-LAST:event_formWindowClosing

    private void jSlider_volStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSlider_volStateChanged
        carregador.setVolume((byte) jSlider_vol.getValue());
        jSlider_vol.setToolTipText("Volume: " + jSlider_vol.getValue() + "%");
    }//GEN-LAST:event_jSlider_volStateChanged

    private void jMenuItemImportarArquivosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemImportarArquivosActionPerformed
        importarMusicas();
    }//GEN-LAST:event_jMenuItemImportarArquivosActionPerformed

    private void jCIMenuPlayActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCIMenuPlayActionPerformed
        carregador.tocarPausar();
    }//GEN-LAST:event_jCIMenuPlayActionPerformed

    private void jCIMenuStopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCIMenuStopActionPerformed
        carregador.parar();
    }//GEN-LAST:event_jCIMenuStopActionPerformed

    private void jCCheckBarraTitulosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCCheckBarraTitulosActionPerformed
        this.setVisible(false);
        this.dispose();
        this.setUndecorated(!jCCheckBarraTitulos.getState());
        this.setVisible(true);
    }//GEN-LAST:event_jCCheckBarraTitulosActionPerformed

    private void jCCheckBarraDeMenusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCCheckBarraDeMenusActionPerformed
        this.dispose();
        jMenuBar1.setVisible(jCCheckBarraDeMenus.getState());
        this.setVisible(true);
    }//GEN-LAST:event_jCCheckBarraDeMenusActionPerformed

    private void jCIMenuMinimizarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCIMenuMinimizarActionPerformed
        carregador.setMiniComoBase();
    }//GEN-LAST:event_jCIMenuMinimizarActionPerformed

    private void jCIMenuFecharActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCIMenuFecharActionPerformed
        carregador.sair();
    }//GEN-LAST:event_jCIMenuFecharActionPerformed

    private void jMenuItem_ArquivoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem_ArquivoActionPerformed
        try {
            carregador.abrir(MusicaGerencia.addOneFile(telaAbrirArquivo()), 0, false);
        } catch (Exception ex) {
            Logger.getLogger(JPrincipal.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jMenuItem_ArquivoActionPerformed

    private void jMenuItem_BibliotecaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem_BibliotecaActionPerformed
        carregador.mostrarBiblioteca();
    }//GEN-LAST:event_jMenuItem_BibliotecaActionPerformed

    private void jMenuItem_MinimizarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem_MinimizarActionPerformed
        carregador.setMiniComoBase();
    }//GEN-LAST:event_jMenuItem_MinimizarActionPerformed

    private void jMenuItem_PropriedadesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem_PropriedadesActionPerformed
        try {
            new JMP3Propriedades(this, true, carregador.getMusica()).setVisible(true);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao Abrir Propriedades.\n" + ex);
            ex.printStackTrace(System.err);
        }
    }//GEN-LAST:event_jMenuItem_PropriedadesActionPerformed

    private void jMenuItem_PlayListActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem_PlayListActionPerformed
        carregador.mostrarPlayList();
    }//GEN-LAST:event_jMenuItem_PlayListActionPerformed

    private void jMenuItem_PlayActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem_PlayActionPerformed
        carregador.tocarPausar();
    }//GEN-LAST:event_jMenuItem_PlayActionPerformed

    private void jMenuItem_ConfiguracoesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem_ConfiguracoesActionPerformed
        configuracao.setVisible(true);
    }//GEN-LAST:event_jMenuItem_ConfiguracoesActionPerformed

    private void jMenuItem_TemaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem_TemaActionPerformed
        carregador.mostrarModificadorDeTema();
    }//GEN-LAST:event_jMenuItem_TemaActionPerformed

    private void jMenuItem_SobreActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem_SobreActionPerformed
        new JSobre(this).setVisible(true);
    }//GEN-LAST:event_jMenuItem_SobreActionPerformed

    private void jMenuItem_HelpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem_HelpActionPerformed
        new JHelp(this).setVisible(true);
    }//GEN-LAST:event_jMenuItem_HelpActionPerformed

    private void jMenuItem_HadukemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem_HadukemActionPerformed
        if (JOptionPane.showConfirmDialog(this, "Isso limpará todos os dados.\nO Crepz Player será fechado.\n Está certo disso ??") == JOptionPane.YES_OPTION) {
            try {
                Configuracoes.limpar();
//                ConfigFile.excluir();
                super.setVisible(false);
                System.exit(0);
            } catch (Exception ex) {
                Logger.getLogger(JPrincipal.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_jMenuItem_HadukemActionPerformed

    private void jLabel_bibMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel_bibMouseClicked
        carregador.mostrarBiblioteca();
    }//GEN-LAST:event_jLabel_bibMouseClicked

    private void jLabel_PlaylistMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel_PlaylistMouseClicked
        carregador.mostrarPlayList();
    }//GEN-LAST:event_jLabel_PlaylistMouseClicked

    private void jLabelFilaReproducaoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabelFilaReproducaoMouseClicked
        if (carregador.isFilaReproducaoVisivel()) {
            carregador.ocultarFilaReproducao();
        } else {
            carregador.mostrarFilaReproducao();
        }
    }//GEN-LAST:event_jLabelFilaReproducaoMouseClicked

    private void jLabel_EditMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel_EditMouseClicked
        try {
            new JMP3Propriedades(JPrincipal.this, true, carregador.getMusica()).setVisible(true);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(JPrincipal.this, "Erro ao Abrir Propriedades.\n" + ex);
            ex.printStackTrace(System.err);
        }
    }//GEN-LAST:event_jLabel_EditMouseClicked

    private void jLabel_MinimizarMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel_MinimizarMouseClicked
        carregador.setMiniComoBase();
    }//GEN-LAST:event_jLabel_MinimizarMouseClicked

    private void jLabelHelpMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabelHelpMouseClicked
        if (evt.getButton() == MouseEvent.BUTTON1) {
            new JHelp(JPrincipal.this).setVisible(true);
        }
    }//GEN-LAST:event_jLabelHelpMouseClicked

    private void jButton_PlayMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton_PlayMouseClicked
        if (evt.getButton() == MouseEvent.BUTTON1) {
            carregador.tocarPausar();
        }
    }//GEN-LAST:event_jButton_PlayMouseClicked

    private void jButton_StopMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton_StopMouseClicked
        if (evt.getButton() == MouseEvent.BUTTON1) {
            carregador.parar();
        }
    }//GEN-LAST:event_jButton_StopMouseClicked

    private void jButton_AntMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton_AntMouseClicked
        if (evt.getButton() == MouseEvent.BUTTON1) {
            carregador.tocarAnterior();
        }
    }//GEN-LAST:event_jButton_AntMouseClicked

    private void jButton_NextMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton_NextMouseClicked
        if (evt.getButton() == MouseEvent.BUTTON1) {

            carregador.tocarProxima();

        }
    }//GEN-LAST:event_jButton_NextMouseClicked

    private void jToggle_RandomMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jToggle_RandomMouseClicked
        carregador.setRandom(!carregador.isRandom());
        if (carregador.isRandom()) {
            jToggle_Random.setIcon(carregador.getIcones().getRandomOnIcon32());
        } else {
            jToggle_Random.setIcon(carregador.getIcones().getRandomOffIcon32());
        }
    }//GEN-LAST:event_jToggle_RandomMouseClicked

    private void jToggle_RepeatMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jToggle_RepeatMouseClicked
        carregador.setRepeat(!carregador.isRepeat());
        if (carregador.isRepeat()) {
            jToggle_Repeat.setIcon(carregador.getIcones().getRepeatOnIcon32());
        } else {
            jToggle_Repeat.setIcon(carregador.getIcones().getRepeatOffIcon32());
        }
    }//GEN-LAST:event_jToggle_RepeatMouseClicked

    private void jSlider_TempoMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jSlider_TempoMousePressed
        ajusteDeTempo = true;
    }//GEN-LAST:event_jSlider_TempoMousePressed

    private void jSlider_TempoMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jSlider_TempoMouseReleased
        carregador.skipTo((double) (jSlider_Tempo.getValue()) / jSlider_Tempo.getMaximum());
        ajusteDeTempo = false;
    }//GEN-LAST:event_jSlider_TempoMouseReleased
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jButton_Ant;
    private javax.swing.JLabel jButton_Next;
    private javax.swing.JLabel jButton_Play;
    private javax.swing.JLabel jButton_Stop;
    private javax.swing.JCheckBoxMenuItem jCCheckBarraDeMenus;
    private javax.swing.JCheckBoxMenuItem jCCheckBarraTitulos;
    private javax.swing.JMenuItem jCIMenuFechar;
    private javax.swing.JMenuItem jCIMenuMinimizar;
    private javax.swing.JMenuItem jCIMenuPlay;
    private javax.swing.JMenuItem jCIMenuStop;
    private javax.swing.JMenu jCMenuReproduz;
    private javax.swing.JMenu jCMenuVisual;
    private javax.swing.JFileChooser jFileChooserImportar;
    private javax.swing.JLabel jLabelFilaReproducao;
    private javax.swing.JLabel jLabelHelp;
    private javax.swing.JLabel jLabel_Edit;
    private javax.swing.JLabel jLabel_Minimizar;
    private javax.swing.JLabel jLabel_Musica;
    private javax.swing.JLabel jLabel_Playlist;
    private javax.swing.JLabel jLabel_bib;
    private javax.swing.JLabel jLabel_bit;
    private javax.swing.JLabel jLabel_freq;
    private javax.swing.JLabel jLabel_tempo;
    private javax.swing.JLabel jLabel_tempoTotal;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPopupMenu jMenuDeContexto;
    private javax.swing.JMenuItem jMenuItemImportarArquivos;
    private javax.swing.JMenuItem jMenuItem_Arquivo;
    private javax.swing.JMenuItem jMenuItem_Biblioteca;
    private javax.swing.JMenuItem jMenuItem_Configuracoes;
    private javax.swing.JMenuItem jMenuItem_Hadukem;
    private javax.swing.JMenuItem jMenuItem_Help;
    private javax.swing.JMenuItem jMenuItem_Minimizar;
    private javax.swing.JMenuItem jMenuItem_Play;
    private javax.swing.JMenuItem jMenuItem_PlayList;
    private javax.swing.JMenuItem jMenuItem_Propriedades;
    private javax.swing.JMenuItem jMenuItem_Sobre;
    private javax.swing.JMenuItem jMenuItem_Tema;
    private javax.swing.JMenu jMenuLnF;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel17;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JProgressBar jProgressBar1;
    private javax.swing.JSlider jSlider_Balanco;
    private javax.swing.JSlider jSlider_Tempo;
    private javax.swing.JSlider jSlider_vol;
    private javax.swing.JLabel jToggle_Random;
    private javax.swing.JLabel jToggle_Repeat;
    // End of variables declaration//GEN-END:variables
    private boolean ajusteDeTempo = false;
    private int initX;
    private int initY;
    private int thisX;
    private int thisY;
}
