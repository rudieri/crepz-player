package com.main.gui;

import com.config.JConfiguracao;
import com.graficos.Testes;
import com.main.Carregador;
import com.main.Notificavel;
import com.musica.Musiquera.PropriedadesMusica;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.event.ChangeListener;


/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * treco.java
 *
 * Created on 08/06/2010, 19:29:58
 */
/**
 *
 * @author manchini
 */
public class JMini extends javax.swing.JDialog implements Notificavel, ActionListener, MouseListener, MouseMotionListener, MouseWheelListener, ChangeListener {

    /**
     * Creates new form treco
     */
    private int initX;
    private int initY;
    private int thisX;
    private int thisY;
    private Timer tarefa = new Timer();
    private int estado = 0;
    private final Carregador carregador;
    private boolean ajusteDeTempo;

    public JMini(Carregador carregador) {
        super((Dialog) null);
        initComponents();
        this.setIconImage(carregador.getIcones().getCrepzIcon().getImage());
        this.carregador = carregador;
        inicializaIcones();
        startEvents();
        pack();
        if (carregador.isPlaying()) {
            jLabelNomeMusica.setText(carregador.getMusica().getNome());
        }
    }

    @Override
    public void tempoEhHMS(String hms) {
        //fazer algo
    }

    @Override
    public void eventoNaMusica(int tipo) {
        //atualizar  infs
    }

    @Override
    public void tempoEh(double v) {
        if (ajusteDeTempo) {
            return;
        }
        jSlider_Tempo.setValue((int) (jSlider_Tempo.getMaximum() * v));
    }

//    @Override
//    public void tempoTotalEhHMS(String hms) {
//        // do nothing
//    }
    @Override
    public void propriedadesMusicaChanged(PropriedadesMusica propriedadesMusica) {
    }

    /**
     * Coloca os icones nos Jlabels
     */
    private void inicializaIcones() {
        jButton_Play.setText("");
        jButton_Stop.setText("");
        jButton_Next.setText("");
        jButton_Ant.setText("");
        jToggleButton1.setText("");
        jToggle_Repete.setText("");

        jButton_Stop.setIcon(carregador.getIcones().getStopIcon16());
        jButton_Ant.setIcon(carregador.getIcones().getVoltaIcon16());
        jButton_Next.setIcon(carregador.getIcones().getFrenteIcon16());
        jLabel_Playlist.setIcon(carregador.getIcones().getPlayList());
        jLabel_lib.setIcon(carregador.getIcones().getBiblioteca());
        jLabel_popup.setIcon(carregador.getIcones().getMenu());
        jLabelFechar.setIcon(carregador.getIcones().getXis());
        if (carregador.isPaused()) {
            jButton_Play.setIcon(carregador.getIcones().getPlayIcon16());
        } else {
            jButton_Play.setIcon(carregador.getIcones().getPauseIcon16());
        }
        if (carregador.isRandom()) {
            jToggleButton1.setIcon(carregador.getIcones().getRandomOnIcon16());
        } else {
            jToggleButton1.setIcon(carregador.getIcones().getRandomOffIcon16());
        }
        if (carregador.isRepeat()) {
            jToggle_Repete.setIcon(carregador.getIcones().getRepeatOnIcon16());
        } else {
            jToggle_Repete.setIcon(carregador.getIcones().getRepeatOffIcon16());
        }
        if (isAlwaysOnTop()) {
            jLabel_top.setIcon(carregador.getIcones().getTopOn());
        } else {
            jLabel_top.setIcon(carregador.getIcones().getTopOff());
        }
    }
    private MouseAdapter mouseAdapterPopUp = new MouseAdapter() {
        @Override
        public void mouseReleased(MouseEvent e) {
            super.mouseReleased(e);
            if (e.getButton() == MouseEvent.BUTTON3) {
                showMenu(e);
            }
        }

        @Override
        public void mousePressed(MouseEvent e) {
            super.mousePressed(e);
            if (e.getButton() == MouseEvent.BUTTON1) {
                setOndeEstou(e);
            }
        }

        @Override
        public void mouseEntered(MouseEvent e) {
            super.mouseEntered(e);
            if (e.getComponent() instanceof JLabel) {

                objetoRollOver((JLabel) e.getComponent());
            }
        }

        @Override
        public void mouseExited(MouseEvent e) {
            super.mouseExited(e);
            e.getComponent().repaint();
        }

        @Override
        public void mouseWheelMoved(MouseWheelEvent e) {
            jSlider_vol.setValue(jSlider_vol.getValue() - e.getWheelRotation());
        }
    };
    private MouseMotionAdapter mouseMotionPopUp = new MouseMotionAdapter() {
        @Override
        public void mouseDragged(MouseEvent e) {
            super.mouseDragged(e);
            vouParaOnde(e);
        }
    };

    private void startEvents() {
        for (int i = 0; i < jPanel1.getComponentCount(); i++) {
            if (jPanel1.getComponent(i) instanceof JLabel) {
                jPanel1.getComponent(i).addMouseListener(mouseAdapterPopUp);
                jPanel1.getComponent(i).addMouseMotionListener(mouseMotionPopUp);
            }
        }
        for (int i = 0; i < jPanel3.getComponentCount(); i++) {
            jPanel3.getComponent(i).addMouseListener(mouseAdapterPopUp);
            jPanel3.getComponent(i).addMouseMotionListener(mouseMotionPopUp);
        }
        // Action Perform
        jMenuItemConfiguracoes.addActionListener(this);
        jCheckBox_lib.addActionListener(this);
        jCheckBox_list.addActionListener(this);
        jMenuRestaura.addActionListener(this);
        jMenu_Ocult.addActionListener(this);
        jMenu_exit.addActionListener(this);

        // Mouse Listener - Eventos específicos
        jPanel4.addMouseListener(this);
        jLabel_popup.addMouseListener(this);
        jLabel_Playlist.addMouseListener(this);
        jLabel_lib.addMouseListener(this);
        jLabel_top.addMouseListener(this);
        jLabelFechar.addMouseListener(this);
        jButton_Play.addMouseListener(this);
        jButton_Stop.addMouseListener(this);
        jButton_Next.addMouseListener(this);
        jButton_Ant.addMouseListener(this);
        jToggleButton1.addMouseListener(this);
        jToggle_Repete.addMouseListener(this);
        jSlider_vol.addMouseListener(this);
        jSlider_Tempo.addMouseListener(this);
        this.addMouseListener(this);
    }

    /**
     * Atualiza o icones Play
     *
     * @param ic
     */
    public void setPlayIcon(Icon ic) {
        jButton_Play.setIcon(ic);
    }

    public void setLocal(int x, int y) {
        setBounds(x, y, getWidth(), getHeight());
    }

    public Point getLocal() {
        return new Point(getX(), getY());
    }

    public boolean getTop() {
        return isAlwaysOnTop();
    }

    public void setNomeMusica(String n) {
        jLabelNomeMusica.setText(n);
    }

    public void setTop(boolean b) {
        setAlwaysOnTop(b);
        jCheckBox_top.setSelected(b);
    }

    private void vouParaOnde(MouseEvent e) {
        estado++;
        if (estado == 5) {
            this.setLocation(e.getXOnScreen() - initX, e.getYOnScreen() - initY);
            estado = 0;
        }
    }

    private void setOndeEstou(MouseEvent e) {
        initX = e.getXOnScreen() - this.getX();
        initY = e.getYOnScreen() - this.getY();
        thisX = this.getX();
        thisY = this.getY();
    }

    private void rollOver() {
        tarefa.cancel();
        jPanel2.setVisible(true);
        setPreferredSize(new Dimension(getPreferredSize().width, 75));
        pack();
    }

    @Override
    public void atualizaLabels(String nome, int bits, String tempo, int freq) {
        jLabelNomeMusica.setText(nome);
        jSlider_Tempo.setToolTipText(tempo);
    }

    private class TarefaRollOut extends TimerTask {

        @Override
        public void run() {
            jPanel2.setVisible(false);
            setPreferredSize(new Dimension(getPreferredSize().width, 55));
            pack();
        }
    }

    /**
     * Faz o efeito de MouseEntered (circula o icones)
     *
     */
    private void objetoRollOver(JLabel c) {
        Icon aux = c.getIcon();

        if (aux.getIconWidth() > 15) {
            Graphics graphics = c.getGraphics();

            graphics.setColor(new Color(250, 250, 250, 70));
            graphics.fillOval(0, 0, aux.getIconWidth(), aux.getIconHeight());
        }
    }

    /**
     * Deixa o JMini visivel ou não
     *
     * @param b boolean que indica se a janela é estará visivel.
     */
    @Override
    public void setVisible(boolean b) {
        if (!super.isVisible()) {

            if (carregador.isRandom()) {
                jToggleButton1.setIcon(carregador.getIcones().getRandomOnIcon16());
            } else {
                jToggleButton1.setIcon(carregador.getIcones().getRandomOffIcon16());
            }
            if (carregador.isRepeat()) {
                jToggle_Repete.setIcon(carregador.getIcones().getRepeatOnIcon16());
            } else {
                jToggle_Repete.setIcon(carregador.getIcones().getRepeatOffIcon16());
            }
        }

        jSlider_vol.setPreferredSize(new Dimension(jSlider_vol.getWidth(), jButton_Play.getHeight()));
        jSlider_vol.setValue(carregador.getVolume());
        jPanel2.setVisible(false);
        pack();
        super.setVisible(b);
        repaint();

    }

    private void showMenu(MouseEvent e) {
        jPopupMenu1.show(e.getComponent(), e.getX(), e.getY());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == jMenuItemConfiguracoes) {
            new JConfiguracao(this, true).setVisible(true);
        } else if (e.getSource() == jCheckBox_lib) {
            if (jCheckBox_lib.isSelected()) {
                carregador.mostrarBiblioteca();
            } else {
                carregador.ocultarBiblioteca();
            }
        } else if (e.getSource() == jCheckBox_list) {
            if (jCheckBox_list.isSelected()) {
                carregador.mostrarPlayList();
            } else {
                carregador.ocultarPlayList();
            }
        } else if (e.getSource() == jMenuRestaura) {
            carregador.setPrincipalComoBase();
        } else if (e.getSource() == jMenu_Ocult) {
            setVisible(false);
        } else if (e.getSource() == jMenu_exit) {
            carregador.sair();
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getSource() == jPanel4) {
            if (e.getButton() == MouseEvent.BUTTON1) {
                if (e.getClickCount() == 2) {
                    carregador.setPrincipalComoBase();
                }
            }
            if (e.getButton() == MouseEvent.BUTTON3) {
                jPopupMenu1.show(this, e.getX(), e.getY());
            }
        } else if (e.getSource() == jLabel_popup) {
            jPopupMenu1.show(jLabel_popup, e.getX(), e.getY());
        } else if (e.getSource() == jLabel_Playlist) {
            if (e.getButton() == MouseEvent.BUTTON1) {
                boolean visible = carregador.isPlayListVisible();
                jCheckBox_list.setSelected(visible);
                if (visible) {
                    carregador.mostrarPlayList();
                } else {
                    carregador.ocultarPlayList();
                }
            }
        } else if (e.getSource() == jLabel_lib) {
            if (e.getButton() == MouseEvent.BUTTON1) {
                boolean visible = carregador.isBibliotecaVisible();
                jCheckBox_list.setSelected(visible);
                if (visible) {
                    carregador.mostrarBiblioteca();
                } else {
                    carregador.ocultarBiblioteca();
                }
            }
        } else if (e.getSource() == jLabel_top) {
            if (e.getButton() == MouseEvent.BUTTON1) {
                jCheckBox_top.setSelected(!jCheckBox_top.isSelected());
            }
        } else if (e.getSource() == jLabelFechar) {
            if (e.getButton() == 1) {
                setVisible(false);
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.getSource() == jPanel4) {
            setOndeEstou(e);
        } else if (e.getSource() == jSlider_Tempo) {
            ajusteDeTempo = true;
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (e.getSource() == jButton_Play) {
            if (!(thisX != this.getX() || thisY != this.getY())) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    carregador.tocarPausar();
                }
            }
        } else if (e.getSource() == jButton_Stop) {
            if (!(thisX != this.getX() || thisY != this.getY())) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    carregador.parar();
                }
            }
        } else if (e.getSource() == jButton_Next) {
            if (!(thisX != this.getX() || thisY != this.getY())) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    carregador.abrir(carregador.getNextMusica(), 0, false);
                    //  tarefa.schedule(principal.getExecutaProxima(), 10);
                }
            }
        } else if (e.getSource() == jButton_Ant) {
            if (!(thisX != this.getX() || thisY != this.getY())) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    carregador.abrir(carregador.getPreviousMusica(), 0, false);
                    // tarefa.schedule(principal.getExecutaAnterior(), 10);
                }
            }
        } else if (e.getSource() == jToggleButton1) {
            if (!(thisX != this.getX() || thisY != this.getY())) {
                if (e.getButton() == MouseEvent.BUTTON1) {

                    carregador.setRandom(!carregador.isRandom());
                    if (carregador.isRandom()) {
                        jToggleButton1.setIcon(carregador.getIcones().getRandomOnIcon16());
                    } else {
                        jToggleButton1.setIcon(carregador.getIcones().getRandomOffIcon16());
                    }
                }
            }
        } else if (e.getSource() == jToggle_Repete) {
            if (!(thisX != this.getX() || thisY != this.getY())) {
                if (e.getButton() == MouseEvent.BUTTON1) {

                    carregador.setRepeat(!carregador.isRepeat());
                    if (carregador.isRepeat()) {
                        jToggle_Repete.setIcon(carregador.getIcones().getRepeatOnIcon16());
                    } else {
                        jToggle_Repete.setIcon(carregador.getIcones().getRepeatOffIcon16());
                    }
                }
            }
        } else if (e.getSource() == jSlider_Tempo) {
            carregador.skipTo(new Double(jSlider_Tempo.getValue()) / jSlider_Tempo.getMaximum());
            ajusteDeTempo = false;
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        if (e.getSource() == jPanel4) {
            rollOver();
        }
    }

    @Override
    public void mouseExited(MouseEvent e) {
        if (e.getSource() == this || e.getSource() == jPanel4) {
            if (!Testes.hitTest(this, new Point(e.getXOnScreen(), e.getYOnScreen()))) {
                tarefa.cancel();
                tarefa = new Timer();
                tarefa.schedule(new TarefaRollOut(), 500);
            }
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPopupMenu1 = new javax.swing.JPopupMenu();
        jMenuItemConfiguracoes = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        jMenuVisualizacoes = new javax.swing.JMenu();
        jCheckBox_top = new javax.swing.JCheckBoxMenuItem();
        jCheckBox_lib = new javax.swing.JCheckBoxMenuItem();
        jCheckBox_list = new javax.swing.JCheckBoxMenuItem();
        jMenuRestaura = new javax.swing.JMenuItem();
        jMenu_Ocult = new javax.swing.JMenuItem();
        jMenu_exit = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        jMenuCancelar = new javax.swing.JMenuItem();
        buttonGrou_Balanco = new javax.swing.ButtonGroup();
        jPanel4 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jButton_Play = new javax.swing.JLabel();
        jButton_Stop = new javax.swing.JLabel();
        jButton_Ant = new javax.swing.JLabel();
        jButton_Next = new javax.swing.JLabel();
        jToggleButton1 = new javax.swing.JLabel();
        jToggle_Repete = new javax.swing.JLabel();
        jSlider_vol = new javax.swing.JSlider();
        jPanel2 = new javax.swing.JPanel();
        jSlider_Tempo = new javax.swing.JSlider();
        jPanel3 = new javax.swing.JPanel();
        jLabel_popup = new javax.swing.JLabel();
        jLabel_Playlist = new javax.swing.JLabel();
        jLabel_lib = new javax.swing.JLabel();
        jLabel_top = new javax.swing.JLabel();
        jLabelFechar = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jLabelNomeMusica = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();

        jMenuItemConfiguracoes.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_C, java.awt.event.InputEvent.ALT_MASK));
        jMenuItemConfiguracoes.setMnemonic('C');
        jMenuItemConfiguracoes.setText("Configurações");
        jPopupMenu1.add(jMenuItemConfiguracoes);
        jPopupMenu1.add(jSeparator2);

        jMenuVisualizacoes.setMnemonic('v');
        jMenuVisualizacoes.setText("Visualizações");

        jCheckBox_top.setText("Sempre Visível");
        jCheckBox_top.addChangeListener(this);
        jMenuVisualizacoes.add(jCheckBox_top);

        jCheckBox_lib.setText("Mostrar Biblioteca");
        jMenuVisualizacoes.add(jCheckBox_lib);

        jCheckBox_list.setText("Mostrar PlayList");
        jMenuVisualizacoes.add(jCheckBox_list);

        jPopupMenu1.add(jMenuVisualizacoes);

        jMenuRestaura.setMnemonic('r');
        jMenuRestaura.setText("Restaurar");
        jPopupMenu1.add(jMenuRestaura);

        jMenu_Ocult.setMnemonic('m');
        jMenu_Ocult.setText("Ocultar-me");
        jPopupMenu1.add(jMenu_Ocult);

        jMenu_exit.setMnemonic('f');
        jMenu_exit.setText("Fechar Player");
        jPopupMenu1.add(jMenu_exit);
        jPopupMenu1.add(jSeparator1);

        jMenuCancelar.setMnemonic('c');
        jMenuCancelar.setText("Cancelar");
        jPopupMenu1.add(jMenuCancelar);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Crepz Player");
        setBackground(new java.awt.Color(51, 51, 51));
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        setIconImage(null);
        setMinimumSize(new java.awt.Dimension(100, 40));
        setUndecorated(true);
        setPreferredSize(new java.awt.Dimension(300, 75));

        jPanel4.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED, new java.awt.Color(204, 204, 204), new java.awt.Color(204, 204, 204), new java.awt.Color(0, 0, 0), new java.awt.Color(51, 51, 51)), javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED, new java.awt.Color(153, 153, 153), new java.awt.Color(204, 204, 204), new java.awt.Color(102, 102, 102), new java.awt.Color(153, 153, 153))));
        jPanel4.setForeground(new java.awt.Color(102, 102, 102));
        jPanel4.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jPanel4.addMouseWheelListener(this);
        jPanel4.addMouseMotionListener(this);
        jPanel4.setLayout(new javax.swing.BoxLayout(jPanel4, javax.swing.BoxLayout.Y_AXIS));

        jPanel1.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jPanel1.setOpaque(false);
        jPanel1.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 0));

        jButton_Play.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jButton_Play.setText("Tocar");
        jButton_Play.setToolTipText("Tocar");
        jButton_Play.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        jButton_Play.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jPanel1.add(jButton_Play);

        jButton_Stop.setText("Parar");
        jButton_Stop.setToolTipText("Parar");
        jButton_Stop.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jPanel1.add(jButton_Stop);

        jButton_Ant.setText("Vol");
        jButton_Ant.setToolTipText("Voltar");
        jButton_Ant.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jPanel1.add(jButton_Ant);

        jButton_Next.setText("Av");
        jButton_Next.setToolTipText("Avançar");
        jButton_Next.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jPanel1.add(jButton_Next);

        jToggleButton1.setText("Ran");
        jToggleButton1.setToolTipText("Aleatório");
        jToggleButton1.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jPanel1.add(jToggleButton1);

        jToggle_Repete.setText("Rep");
        jToggle_Repete.setToolTipText("Repetir PlayList");
        jPanel1.add(jToggle_Repete);

        jSlider_vol.setBackground(new java.awt.Color(255, 255, 255));
        jSlider_vol.setFont(new java.awt.Font("Cantarell", 0, 3)); // NOI18N
        jSlider_vol.setToolTipText("Volume");
        jSlider_vol.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jSlider_vol.setPreferredSize(new java.awt.Dimension(100, 23));
        jSlider_vol.addMouseWheelListener(this);
        jSlider_vol.addChangeListener(this);
        jPanel1.add(jSlider_vol);

        jPanel4.add(jPanel1);

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel2.setLayout(new java.awt.BorderLayout());

        jSlider_Tempo.setBackground(new java.awt.Color(255, 255, 255));
        jSlider_Tempo.setFont(new java.awt.Font("Cantarell", 0, 3)); // NOI18N
        jSlider_Tempo.setMaximum(1000);
        jSlider_Tempo.setToolTipText("");
        jSlider_Tempo.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jSlider_Tempo.setMinimumSize(new java.awt.Dimension(36, 13));
        jSlider_Tempo.setPreferredSize(new java.awt.Dimension(80, 13));
        jPanel2.add(jSlider_Tempo, java.awt.BorderLayout.CENTER);

        jPanel3.setPreferredSize(new java.awt.Dimension(84, 14));
        jPanel3.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 1, 0));

        jLabel_popup.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/img/c_menu.png"))); // NOI18N
        jLabel_popup.setToolTipText("Menu de opções");
        jLabel_popup.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jPanel3.add(jLabel_popup);

        jLabel_Playlist.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/img/playlist.gif"))); // NOI18N
        jLabel_Playlist.setToolTipText("Playlist");
        jLabel_Playlist.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jPanel3.add(jLabel_Playlist);

        jLabel_lib.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/img/biblioteca.png"))); // NOI18N
        jLabel_lib.setToolTipText("Biblioteca");
        jLabel_lib.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jPanel3.add(jLabel_lib);

        jLabel_top.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/img/c_top_off.png"))); // NOI18N
        jLabel_top.setToolTipText("Sempre Visível");
        jLabel_top.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jPanel3.add(jLabel_top);

        jLabelFechar.setFont(new java.awt.Font("Tahoma", 0, 8)); // NOI18N
        jLabelFechar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/img/x.png"))); // NOI18N
        jLabelFechar.setToolTipText("Ocultar");
        jLabelFechar.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jPanel3.add(jLabelFechar);

        jPanel2.add(jPanel3, java.awt.BorderLayout.LINE_START);

        jPanel4.add(jPanel2);

        jPanel5.setFont(new java.awt.Font("DejaVu Sans", 0, 11)); // NOI18N
        jPanel5.setPreferredSize(new java.awt.Dimension(300, 26));
        jPanel5.setLayout(new java.awt.BorderLayout());

        jLabelNomeMusica.setFont(new java.awt.Font("DejaVu Sans", 0, 11)); // NOI18N
        jLabelNomeMusica.setText("Nenhuma música sendo reproduzida...");
        jPanel5.add(jLabelNomeMusica, java.awt.BorderLayout.PAGE_START);

        jPanel7.setCursor(new java.awt.Cursor(java.awt.Cursor.MOVE_CURSOR));

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 348, Short.MAX_VALUE)
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 21, Short.MAX_VALUE)
        );

        jPanel5.add(jPanel7, java.awt.BorderLayout.CENTER);

        jPanel4.add(jPanel5);

        getContentPane().add(jPanel4, java.awt.BorderLayout.CENTER);

        pack();
    }

    // Code for dispatching events from components to event handlers.

    public void mouseDragged(java.awt.event.MouseEvent evt) {
        if (evt.getSource() == jPanel4) {
            JMini.this.jPanel4MouseDragged(evt);
        }
    }

    public void mouseMoved(java.awt.event.MouseEvent evt) {
    }

    public void mouseWheelMoved(java.awt.event.MouseWheelEvent evt) {
        if (evt.getSource() == jPanel4) {
            JMini.this.jPanel4MouseWheelMoved(evt);
        }
        else if (evt.getSource() == jSlider_vol) {
            JMini.this.jSlider_volMouseWheelMoved(evt);
        }
    }

    public void stateChanged(javax.swing.event.ChangeEvent evt) {
        if (evt.getSource() == jCheckBox_top) {
            JMini.this.jCheckBox_topStateChanged(evt);
        }
        else if (evt.getSource() == jSlider_vol) {
            JMini.this.jSlider_volStateChanged(evt);
        }
    }// </editor-fold>//GEN-END:initComponents

    private void jSlider_volMouseWheelMoved(java.awt.event.MouseWheelEvent evt) {//GEN-FIRST:event_jSlider_volMouseWheelMoved
        int v = jSlider_vol.getValue() - evt.getWheelRotation();
        jSlider_vol.setValue(v);
}//GEN-LAST:event_jSlider_volMouseWheelMoved

    private void jPanel4MouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel4MouseDragged

        vouParaOnde(evt);
    }//GEN-LAST:event_jPanel4MouseDragged

    private void jPanel4MouseWheelMoved(java.awt.event.MouseWheelEvent evt) {//GEN-FIRST:event_jPanel4MouseWheelMoved

        jSlider_vol.setValue(jSlider_vol.getValue() - evt.getWheelRotation());
    }//GEN-LAST:event_jPanel4MouseWheelMoved

    private void jCheckBox_topStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jCheckBox_topStateChanged
        setAlwaysOnTop(jCheckBox_top.isSelected());
        if (isAlwaysOnTop()) {
            jLabel_top.setIcon(carregador.getIcones().getTopOn());
        } else {
            jLabel_top.setIcon(carregador.getIcones().getTopOff());
        }
    }//GEN-LAST:event_jCheckBox_topStateChanged

    private void jSlider_volStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSlider_volStateChanged
        carregador.setVolume((byte) jSlider_vol.getValue());
        jSlider_vol.setToolTipText("Volume: " + jSlider_vol.getValue() + "%");
    }//GEN-LAST:event_jSlider_volStateChanged
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGrou_Balanco;
    private javax.swing.JLabel jButton_Ant;
    private javax.swing.JLabel jButton_Next;
    private javax.swing.JLabel jButton_Play;
    private javax.swing.JLabel jButton_Stop;
    private javax.swing.JCheckBoxMenuItem jCheckBox_lib;
    private javax.swing.JCheckBoxMenuItem jCheckBox_list;
    private javax.swing.JCheckBoxMenuItem jCheckBox_top;
    private javax.swing.JLabel jLabelFechar;
    private javax.swing.JLabel jLabelNomeMusica;
    private javax.swing.JLabel jLabel_Playlist;
    private javax.swing.JLabel jLabel_lib;
    private javax.swing.JLabel jLabel_popup;
    private javax.swing.JLabel jLabel_top;
    private javax.swing.JMenuItem jMenuCancelar;
    private javax.swing.JMenuItem jMenuItemConfiguracoes;
    private javax.swing.JMenuItem jMenuRestaura;
    private javax.swing.JMenu jMenuVisualizacoes;
    private javax.swing.JMenuItem jMenu_Ocult;
    private javax.swing.JMenuItem jMenu_exit;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPopupMenu jPopupMenu1;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JSlider jSlider_Tempo;
    public javax.swing.JSlider jSlider_vol;
    private javax.swing.JLabel jToggleButton1;
    private javax.swing.JLabel jToggle_Repete;
    // End of variables declaration//GEN-END:variables
}
