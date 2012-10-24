package com.main.gui;

import com.conexao.BD;
import com.config.JConfiguracao;
import com.help.JHelp;
import com.help.JSobre;
import com.main.Carregador;
import com.main.Notificavel;
import com.melloware.jintellitype.HotkeyListener;
import com.melloware.jintellitype.IntellitypeListener;
import com.melloware.jintellitype.JIntellitype;
import com.musica.MusicaGerencia;
import com.musica.Musiquera.PropriedadesMusica;
import com.utils.Warning;
import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
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
public class JPrincipal extends javax.swing.JFrame implements HotkeyListener, IntellitypeListener, Notificavel, ActionListener {

    public static final Color COLOR_BRANCO_ALPHA = new Color(1f, 1f, 1f, 0.5f);
    private int estado = 0;
    private JFileChooser jFileChooser;
    private int volAnt;
    JConfiguracao configuracao;
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
                Warning.print("Evento desconhecio. Id: " + tipo);
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
     * @param icones nome icones.nomeDoIcone
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

    public void setVolume(int v) {
        jSlider_vol.setValue(v);
        jSlider_vol.setToolTipText(jSlider_vol.getValue() + "%");
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

    private void inicializaIcones() {
        //Estado inicial do botão (está Stop);
        jButton_Play.setIcon(carregador.getIcones().playIcon32);
        //Se tiver tocando
        if (carregador.isPlaying()) {
            jButton_Play.setIcon(carregador.getIcones().pauseIcon32);
        }
        //Se tiver pause
        if (carregador.isPlaying()) {
            jButton_Play.setIcon(carregador.getIcones().playIcon32);
        }
        jButton_Stop.setIcon(carregador.getIcones().stopIcon32);
        jButton_Next.setIcon(carregador.getIcones().frenteIcon32);
        jButton_Ant.setIcon(carregador.getIcones().voltaIcon32);

        if (carregador.isRandom()) {
            jToggle_Random.setIcon(carregador.getIcones().randomOnIcon32);
        } else {
            jToggle_Random.setIcon(carregador.getIcones().randomOffIcon32);
        }
        if (carregador.isRepeat()) {
            jToggle_Repeat.setIcon(carregador.getIcones().repeatOnIcon32);
        } else {
            jToggle_Repeat.setIcon(carregador.getIcones().repeatOffIcon32);
        }
        setIconImage(carregador.getIcones().crepzIcon.getImage());
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
            jToggle_Random.setIcon(carregador.getIcones().randomOnIcon32);
        } else {
            jToggle_Random.setIcon(carregador.getIcones().randomOffIcon32);
        }
        if (carregador.isRepeat()) {
            jToggle_Repeat.setIcon(carregador.getIcones().repeatOnIcon32);
        } else {
            jToggle_Repeat.setIcon(carregador.getIcones().repeatOffIcon32);
        }
        jSlider_vol.setValue(carregador.getVolume());
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

        // Evento Específico
        jLabel_bib.addMouseListener(mouseAdapterEventosEspecificos);
        jLabel_Playlist.addMouseListener(mouseAdapterEventosEspecificos);
        jLabelFilaReproducao.addMouseListener(mouseAdapterEventosEspecificos);
        jLabel_Edit.addMouseListener(mouseAdapterEventosEspecificos);
        jLabel_Minimizar.addMouseListener(mouseAdapterEventosEspecificos);
        jLabelHelp.addMouseListener(mouseAdapterEventosEspecificos);
        jButton_Play.addMouseListener(mouseAdapterEventosEspecificos);
        jButton_Stop.addMouseListener(mouseAdapterEventosEspecificos);
        jButton_Ant.addMouseListener(mouseAdapterEventosEspecificos);
        jButton_Next.addMouseListener(mouseAdapterEventosEspecificos);
        jToggle_Random.addMouseListener(mouseAdapterEventosEspecificos);
        jToggle_Repeat.addMouseListener(mouseAdapterEventosEspecificos);
        jSlider_Tempo.addMouseListener(mouseAdapterEventosEspecificos);

        // Actions
        jCIMenuPlay.addActionListener(this);
        jCIMenuStop.addActionListener(this);
        jCCheckBarraTitulos.addActionListener(this);
        jCCheckBarraDeMenus.addActionListener(this);
        jCIMenuMinimizar.addActionListener(this);
        jCIMenuFechar.addActionListener(this);
        jMenuItem_Arquivo.addActionListener(this);
        jMenuItem_Biblioteca.addActionListener(this);
        jMenuItem_Minimizar.addActionListener(this);
        jMenuItem_Propriedades.addActionListener(this);
        jMenuItem_PlayList.addActionListener(this);
        jMenuItem_Play.addActionListener(this);
        jMenuItem_Configuracoes.addActionListener(this);
        jMenuItem_Tema.addActionListener(this);
        jMenuItem_Sobre.addActionListener(this);
        jMenuItem_Help.addActionListener(this);
        jMenuItem_Hadukem.addActionListener(this);

    }

    public int getSliderValue() {
        return jSlider_vol.getValue();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == jCIMenuPlay) {
            carregador.tocarPausar();
        } else if (e.getSource() == jCIMenuStop) {
            carregador.parar();
        } else if (e.getSource() == jCCheckBarraTitulos) {
            this.setVisible(false);
            this.dispose();
            this.setUndecorated(!jCCheckBarraTitulos.getState());
            this.setVisible(true);
        } else if (e.getSource() == jCCheckBarraDeMenus) {
            this.dispose();
            jMenuBar1.setVisible(jCCheckBarraDeMenus.getState());
            this.setVisible(true);
        } else if (e.getSource() == jCIMenuMinimizar) {
            carregador.setMiniComoBase();
        } else if (e.getSource() == jCIMenuFechar) {
            carregador.sair();
        } else if (e.getSource() == jMenuItem_Arquivo) {
            try {

                carregador.abrir(MusicaGerencia.addFiles(telaAbrirArquivo(), null), 0, false);

            } catch (Exception ex) {
                Logger.getLogger(JPrincipal.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else if (e.getSource() == jMenuItem_Biblioteca) {
            carregador.mostrarBiblioteca();
        } else if (e.getSource() == jMenuItem_Minimizar) {
            carregador.setMiniComoBase();
        } else if (e.getSource() == jMenuItem_Propriedades) {
            try {
                new JMP3Propriedades(this, true, carregador.getMusica()).setVisible(true);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro ao Abrir Propriedades.\n" + ex);
                ex.printStackTrace(System.err);
            }
        } else if (e.getSource() == jMenuItem_PlayList) {
            carregador.mostrarPlayList();
        } else if (e.getSource() == jMenuItem_Play) {
            carregador.tocarPausar();
        } else if (e.getSource() == jMenuItem_Configuracoes) {
            configuracao.setVisible(true);
        } else if (e.getSource() == jMenuItem_Tema) {
            carregador.mostrarModificadorDeTema();
        } else if (e.getSource() == jMenuItem_Sobre) {
            new JSobre(this).setVisible(true);
        } else if (e.getSource() == jMenuItem_Help) {
            new JHelp(this).setVisible(true);
        } else if (e.getSource() == jMenuItem_Hadukem) {
            if (JOptionPane.showConfirmDialog(this, "Isso limpará a biblioteca e a playlist.\nO Crepz Player será fechado.\n Está certo disso ??") == JOptionPane.YES_OPTION) {
                try {
                    BD.hadukem();
//                ConfigFile.excluir();
                    super.setVisible(false);
                    System.exit(0);
                } catch (Exception ex) {
                    Logger.getLogger(JPrincipal.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
    private MouseAdapter mouseAdapterEventosEspecificos = new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent evt) {
            super.mouseClicked(evt);
            if (evt.getSource() == jLabel_bib) {
                carregador.mostrarBiblioteca();
            } else if (evt.getSource() == jLabel_Playlist) {
                carregador.mostrarPlayList();
            } else if (evt.getSource() == jLabelFilaReproducao) {
                if (carregador.isFilaReproducaoVisivel()) {
                    carregador.ocultarFilaReproducao();
                } else {
                    carregador.mostrarFilaReproducao();
                }
            } else if (evt.getSource() == jLabel_Edit) {
                try {
                    new JMP3Propriedades(JPrincipal.this, true, carregador.getMusica()).setVisible(true);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(JPrincipal.this, "Erro ao Abrir Propriedades.\n" + ex);
                    ex.printStackTrace(System.err);
                }
            } else if (evt.getSource() == jLabel_Minimizar) {
                carregador.setMiniComoBase();
            } else if (evt.getSource() == jLabelHelp) {
                if (evt.getButton() == MouseEvent.BUTTON1) {
                    new JHelp(JPrincipal.this).setVisible(true);
                }
            } else if (evt.getSource() == jButton_Play) {
                if (evt.getButton() == MouseEvent.BUTTON1) {
                    carregador.tocarPausar();
                }
            } else if (evt.getSource() == jButton_Stop) {
                if (evt.getButton() == MouseEvent.BUTTON1) {
                    carregador.parar();
                }
            } else if (evt.getSource() == jButton_Ant) {
                if (evt.getButton() == MouseEvent.BUTTON1) {
                    carregador.tocarAnterior();
                }
            } else if (evt.getSource() == jButton_Next) {
                if (evt.getButton() == MouseEvent.BUTTON1) {

                    carregador.tocarProxima();

                }
            } else if (evt.getSource() == jToggle_Random) {
                carregador.setRandom(!carregador.isRandom());
                if (carregador.isRandom()) {
                    jToggle_Random.setIcon(carregador.getIcones().randomOnIcon32);
                } else {
                    jToggle_Random.setIcon(carregador.getIcones().randomOffIcon32);
                }
            } else if (evt.getSource() == jToggle_Repeat) {
                carregador.setRepeat(!carregador.isRepeat());
                if (carregador.isRepeat()) {
                    jToggle_Repeat.setIcon(carregador.getIcones().repeatOnIcon32);
                } else {
                    jToggle_Repeat.setIcon(carregador.getIcones().repeatOffIcon32);
                }
            }

        }

        @Override
        public void mousePressed(MouseEvent e) {
            if (e.getSource() == jSlider_Tempo) {
                ajusteDeTempo = true;
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            if (e.getSource() == jSlider_Tempo) {
                carregador.skipTo((double) (jSlider_Tempo.getValue()) / jSlider_Tempo.getMaximum());
                ajusteDeTempo = false;
            }
        }
    };

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jMenuDeContexto = new javax.swing.JPopupMenu();
        jCMenuReproduz = new javax.swing.JMenu();
        jCIMenuPlay = new javax.swing.JMenuItem();
        jCIMenuStop = new javax.swing.JMenuItem();
        jCMenuVisual = new javax.swing.JMenu();
        jCCheckBarraTitulos = new javax.swing.JCheckBoxMenuItem();
        jCCheckBarraDeMenus = new javax.swing.JCheckBoxMenuItem();
        jCIMenuMinimizar = new javax.swing.JMenuItem();
        jCIMenuFechar = new javax.swing.JMenuItem();
        GrupoSpiner = new javax.swing.ButtonGroup();
        jPanel17 = new javax.swing.JPanel();
        jLabel_bib = new javax.swing.JLabel();
        jLabel_Playlist = new javax.swing.JLabel();
        jLabelFilaReproducao = new javax.swing.JLabel();
        jLabel_Edit = new javax.swing.JLabel();
        jLabel_Minimizar = new javax.swing.JLabel();
        jLabelHelp = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jPanel15 = new javax.swing.JPanel();
        jLabel_Musica = new javax.swing.JLabel();
        jPanel14 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jPanel11 = new javax.swing.JPanel();
        jLabel_bit = new javax.swing.JLabel();
        jPanel12 = new javax.swing.JPanel();
        jLabel_freq = new javax.swing.JLabel();
        jPanel13 = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        jPanel10 = new javax.swing.JPanel();
        jLabel_tempoTotal = new javax.swing.JLabel();
        jPanel8 = new javax.swing.JPanel();
        jLabel_tempo = new javax.swing.JLabel();
        jPanel9 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jSlider_Tempo = new javax.swing.JSlider();
        jPanel2 = new javax.swing.JPanel();
        jButton_Play = new javax.swing.JLabel();
        jButton_Stop = new javax.swing.JLabel();
        jButton_Ant = new javax.swing.JLabel();
        jButton_Next = new javax.swing.JLabel();
        jToggle_Random = new javax.swing.JLabel();
        jToggle_Repeat = new javax.swing.JLabel();
        jSlider_vol = new javax.swing.JSlider();
        jPanel16 = new javax.swing.JPanel();
        jSlider_Balanco = new javax.swing.JSlider();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem_Arquivo = new javax.swing.JMenuItem();
        jMenuItem_Biblioteca = new javax.swing.JMenuItem();
        jMenuItem_Minimizar = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        jMenuItem_Propriedades = new javax.swing.JMenuItem();
        jMenuItem_PlayList = new javax.swing.JMenuItem();
        jMenuItem_Play = new javax.swing.JMenuItem();
        jMenuItem_Configuracoes = new javax.swing.JMenuItem();
        jMenuItem_Tema = new javax.swing.JMenuItem();
        jMenu3 = new javax.swing.JMenu();
        jMenuItem_Sobre = new javax.swing.JMenuItem();
        jMenuItem_Help = new javax.swing.JMenuItem();
        jMenuItem_Hadukem = new javax.swing.JMenuItem();

        jCMenuReproduz.setText("Reprodução");

        jCIMenuPlay.setText("Tocar");
        jCMenuReproduz.add(jCIMenuPlay);

        jCIMenuStop.setText("Parar");
        jCMenuReproduz.add(jCIMenuStop);

        jMenuDeContexto.add(jCMenuReproduz);

        jCMenuVisual.setText("Visualização");

        jCCheckBarraTitulos.setSelected(true);
        jCCheckBarraTitulos.setText("Mostrar barra de titulos");
        jCMenuVisual.add(jCCheckBarraTitulos);

        jCCheckBarraDeMenus.setSelected(true);
        jCCheckBarraDeMenus.setText("Mostrar barra de menus");
        jCMenuVisual.add(jCCheckBarraDeMenus);

        jMenuDeContexto.add(jCMenuVisual);

        jCIMenuMinimizar.setText("Minimizar");
        jMenuDeContexto.add(jCIMenuMinimizar);

        jCIMenuFechar.setText("Sair");
        jMenuDeContexto.add(jCIMenuFechar);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Crepz Player 1.0");
        setBackground(new java.awt.Color(255, 255, 255));
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jPanel17.setBackground(new java.awt.Color(255, 255, 255));
        jPanel17.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 0));

        jLabel_bib.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/img/biblioteca.png"))); // NOI18N
        jLabel_bib.setToolTipText("Biblioteca");
        jPanel17.add(jLabel_bib);

        jLabel_Playlist.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/img/playlist.gif"))); // NOI18N
        jLabel_Playlist.setToolTipText("Playlist");
        jPanel17.add(jLabel_Playlist);

        jLabelFilaReproducao.setText("Fila");
        jPanel17.add(jLabelFilaReproducao);

        jLabel_Edit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/img/edit.png"))); // NOI18N
        jLabel_Edit.setToolTipText("Edit Propriedades MP3");
        jPanel17.add(jLabel_Edit);

        jLabel_Minimizar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/img/icon1616.png"))); // NOI18N
        jLabel_Minimizar.setToolTipText("Minimizar");
        jPanel17.add(jLabel_Minimizar);

        jLabelHelp.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/help/img/help.PNG"))); // NOI18N
        jPanel17.add(jLabelHelp);

        getContentPane().add(jPanel17, java.awt.BorderLayout.PAGE_START);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setPreferredSize(new java.awt.Dimension(375, 130));
        jPanel1.setLayout(new javax.swing.BoxLayout(jPanel1, javax.swing.BoxLayout.Y_AXIS));

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));
        jPanel3.setLayout(new javax.swing.BoxLayout(jPanel3, javax.swing.BoxLayout.Y_AXIS));

        jPanel15.setBackground(new java.awt.Color(255, 255, 255));
        jPanel15.setPreferredSize(new java.awt.Dimension(397, 30));
        jPanel15.add(jLabel_Musica);

        jPanel3.add(jPanel15);

        jPanel14.setBackground(new java.awt.Color(255, 255, 255));
        jPanel14.setLayout(new javax.swing.BoxLayout(jPanel14, javax.swing.BoxLayout.X_AXIS));

        jPanel5.setBackground(new java.awt.Color(255, 255, 255));
        jPanel5.setPreferredSize(new java.awt.Dimension(132, 100));
        jPanel5.setLayout(new javax.swing.BoxLayout(jPanel5, javax.swing.BoxLayout.Y_AXIS));

        jPanel11.setBackground(new java.awt.Color(255, 255, 255));
        jPanel11.add(jLabel_bit);

        jPanel5.add(jPanel11);

        jPanel12.setBackground(new java.awt.Color(255, 255, 255));
        jPanel12.add(jLabel_freq);

        jPanel5.add(jPanel12);

        jPanel13.setBackground(new java.awt.Color(255, 255, 255));
        jPanel13.setLayout(new java.awt.BorderLayout());
        jPanel5.add(jPanel13);

        jPanel14.add(jPanel5);

        jPanel6.setBackground(new java.awt.Color(255, 255, 255));
        jPanel6.setPreferredSize(new java.awt.Dimension(132, 120));
        jPanel6.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 0, 0));
        jPanel14.add(jPanel6);

        jPanel7.setLayout(new javax.swing.BoxLayout(jPanel7, javax.swing.BoxLayout.Y_AXIS));

        jPanel10.setBackground(new java.awt.Color(255, 255, 255));

        jLabel_tempoTotal.setText("0:00");
        jPanel10.add(jLabel_tempoTotal);

        jPanel7.add(jPanel10);

        jPanel8.setBackground(new java.awt.Color(255, 255, 255));

        jLabel_tempo.setText("0:00");
        jPanel8.add(jLabel_tempo);

        jPanel7.add(jPanel8);

        jPanel9.setBackground(new java.awt.Color(255, 255, 255));
        jPanel9.setLayout(new java.awt.BorderLayout());
        jPanel7.add(jPanel9);

        jPanel14.add(jPanel7);

        jPanel3.add(jPanel14);

        jPanel1.add(jPanel3);

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));
        jPanel4.setLayout(new java.awt.BorderLayout());

        jSlider_Tempo.setBackground(new java.awt.Color(255, 255, 255));
        jSlider_Tempo.setMaximum(1000);
        jSlider_Tempo.setToolTipText("0:00");
        jSlider_Tempo.setValue(0);
        jPanel4.add(jSlider_Tempo, java.awt.BorderLayout.CENTER);

        jPanel1.add(jPanel4);

        getContentPane().add(jPanel1, java.awt.BorderLayout.CENTER);

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setMinimumSize(new java.awt.Dimension(248, 35));
        jPanel2.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 2, 0));

        jButton_Play.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/img/icons/crepz/32/pl_play.png"))); // NOI18N
        jPanel2.add(jButton_Play);

        jButton_Stop.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/img/icons/crepz/32/pl_stop.png"))); // NOI18N
        jPanel2.add(jButton_Stop);

        jButton_Ant.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/img/icons/crepz/32/pl_tras.png"))); // NOI18N
        jPanel2.add(jButton_Ant);

        jButton_Next.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/img/icons/crepz/32/pl_frente.png"))); // NOI18N
        jPanel2.add(jButton_Next);

        jToggle_Random.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/img/icons/crepz/32/pl_randon_off.png"))); // NOI18N
        jPanel2.add(jToggle_Random);

        jToggle_Repeat.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/img/icons/crepz/32/pl_repeat_off.png"))); // NOI18N
        jPanel2.add(jToggle_Repeat);

        jSlider_vol.setBackground(new java.awt.Color(255, 255, 255));
        jSlider_vol.setToolTipText("Volume");
        jSlider_vol.setPreferredSize(new java.awt.Dimension(100, 23));
        jSlider_vol.addMouseWheelListener(new java.awt.event.MouseWheelListener() {
            public void mouseWheelMoved(java.awt.event.MouseWheelEvent evt) {
                jSlider_volMouseWheelMoved(evt);
            }
        });
        jSlider_vol.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSlider_volStateChanged(evt);
            }
        });
        jPanel2.add(jSlider_vol);

        jPanel16.setBackground(new java.awt.Color(255, 255, 255));
        jPanel16.setMaximumSize(new java.awt.Dimension(50, 2147483647));
        jPanel16.setMinimumSize(new java.awt.Dimension(20, 24));
        jPanel16.setPreferredSize(new java.awt.Dimension(50, 20));
        jPanel16.setLayout(new java.awt.BorderLayout());

        jSlider_Balanco.setBackground(new java.awt.Color(255, 255, 255));
        jSlider_Balanco.setMinimum(-100);
        jSlider_Balanco.setToolTipText("balanço");
        jSlider_Balanco.setValue(0);
        jSlider_Balanco.addMouseWheelListener(new java.awt.event.MouseWheelListener() {
            public void mouseWheelMoved(java.awt.event.MouseWheelEvent evt) {
                jSlider_BalancoMouseWheelMoved(evt);
            }
        });
        jSlider_Balanco.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSlider_BalancoStateChanged(evt);
            }
        });
        jPanel16.add(jSlider_Balanco, java.awt.BorderLayout.CENTER);

        jPanel2.add(jPanel16);

        getContentPane().add(jPanel2, java.awt.BorderLayout.PAGE_END);

        jMenu1.setText("Arquivo");

        jMenuItem_Arquivo.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_A, java.awt.event.InputEvent.ALT_MASK));
        jMenuItem_Arquivo.setText("Arquivo");
        jMenu1.add(jMenuItem_Arquivo);

        jMenuItem_Biblioteca.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_B, java.awt.event.InputEvent.ALT_MASK));
        jMenuItem_Biblioteca.setText("Biblioteca");
        jMenu1.add(jMenuItem_Biblioteca);

        jMenuItem_Minimizar.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_M, java.awt.event.InputEvent.ALT_MASK));
        jMenuItem_Minimizar.setText("Minimizar");
        jMenu1.add(jMenuItem_Minimizar);

        jMenuBar1.add(jMenu1);

        jMenu2.setText("Editar");

        jMenuItem_Propriedades.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_P, java.awt.event.InputEvent.ALT_MASK));
        jMenuItem_Propriedades.setText("Propriedades");
        jMenu2.add(jMenuItem_Propriedades);

        jMenuItem_PlayList.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_L, java.awt.event.InputEvent.ALT_MASK));
        jMenuItem_PlayList.setText("PlayList");
        jMenu2.add(jMenuItem_PlayList);

        jMenuItem_Play.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_SPACE, 0));
        jMenuItem_Play.setText("Play");
        jMenu2.add(jMenuItem_Play);

        jMenuItem_Configuracoes.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_C, java.awt.event.InputEvent.ALT_MASK));
        jMenuItem_Configuracoes.setText("Configurações");
        jMenu2.add(jMenuItem_Configuracoes);

        jMenuItem_Tema.setText("Tema");
        jMenu2.add(jMenuItem_Tema);

        jMenuBar1.add(jMenu2);

        jMenu3.setText("Sobre");

        jMenuItem_Sobre.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.ALT_MASK));
        jMenuItem_Sobre.setText("Sobre");
        jMenu3.add(jMenuItem_Sobre);

        jMenuItem_Help.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_H, java.awt.event.InputEvent.ALT_MASK));
        jMenuItem_Help.setText("Help");
        jMenu3.add(jMenuItem_Help);

        jMenuItem_Hadukem.setText("Restaurar Configuração Original");
        jMenu3.add(jMenuItem_Hadukem);

        jMenuBar1.add(jMenu3);

        setJMenuBar(jMenuBar1);

        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        setBounds((screenSize.width-398)/2, (screenSize.height-231)/2, 398, 231);
    }// </editor-fold>//GEN-END:initComponents

    private void jSlider_volMouseWheelMoved(java.awt.event.MouseWheelEvent evt) {//GEN-FIRST:event_jSlider_volMouseWheelMoved
        // TODO add your handling code here:
        jSlider_vol.setValue(jSlider_vol.getValue() - evt.getWheelRotation());
        carregador.setVolume((byte) jSlider_vol.getValue());
        jSlider_vol.setToolTipText(jSlider_vol.getValue() + "%");
    }//GEN-LAST:event_jSlider_volMouseWheelMoved

    private void jSlider_BalancoStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSlider_BalancoStateChanged

        carregador.setBalanco((byte) jSlider_Balanco.getValue());
        jSlider_Balanco.setToolTipText(String.valueOf(jSlider_Balanco.getValue() / 100));
    }//GEN-LAST:event_jSlider_BalancoStateChanged

    private void jSlider_BalancoMouseWheelMoved(java.awt.event.MouseWheelEvent evt) {//GEN-FIRST:event_jSlider_BalancoMouseWheelMoved
        jSlider_Balanco.setValue(jSlider_Balanco.getValue() - evt.getWheelRotation());
    }//GEN-LAST:event_jSlider_BalancoMouseWheelMoved

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        carregador.sair();
    }//GEN-LAST:event_formWindowClosing

    private void jSlider_volStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSlider_volStateChanged
        carregador.setVolume((byte) jSlider_vol.getValue());
        jSlider_vol.setToolTipText(jSlider_vol.getValue() + "%");
    }//GEN-LAST:event_jSlider_volStateChanged

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) throws ParseException {
//            }
//        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup GrupoSpiner;
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
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel16;
    private javax.swing.JPanel jPanel17;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JSlider jSlider_Balanco;
    private javax.swing.JSlider jSlider_Tempo;
    private javax.swing.JSlider jSlider_vol;
    private javax.swing.JLabel jToggle_Random;
    private javax.swing.JLabel jToggle_Repeat;
    // End of variables declaration//GEN-END:variables
    private boolean ajusteDeTempo = false;
    int initX;
    int initY;
    int thisX;
    int thisY;
}
