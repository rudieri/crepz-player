package com;

import com.Musiquera.PropriedadesMusica;
import com.graficos.Testes;
import com.main.Carregador;
import com.main.Notificavel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.Icon;
import javax.swing.JLabel;


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
public class JMini extends javax.swing.JDialog implements Notificavel {

    /** Creates new form treco */
    private int initX;
    private int initY;
    private int thisX;
    private int thisY;
    Timer tarefa = new Timer();
    private int estado = 0;
    Thread thAnim;
//    JViewport jv = new JViewport();
    private Musiquera musiquera;
    private final Carregador carregador;
    private boolean ajusteDeTempo;

    public JMini(Musiquera musiquera, Carregador carregador) {
        initComponents();
        this.musiquera = musiquera;
        this.carregador = carregador;
        inicializaIcones();
        startEvents();
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

    /**Coloca os icones nos Jlabels*/
    private void inicializaIcones() {
        jButton_Play.setText("");
        jButton_Stop.setText("");
        jButton_Next.setText("");
        jButton_Ant.setText("");
        jToggleButton1.setText("");
        jToggle_Repete.setText("");

        jButton_Stop.setIcon(carregador.icones.stopIcon16);
        jButton_Ant.setIcon(carregador.icones.voltaIcon16);
        jButton_Next.setIcon(carregador.icones.frenteIcon16);
        jLabel_Playlist.setIcon(carregador.icones.pl);
        jLabel_lib.setIcon(carregador.icones.lib);
        jLabel_popup.setIcon(carregador.icones.menu);
        jLabel1.setIcon(carregador.icones.xis);
        if (musiquera.isPaused()) {
            jButton_Play.setIcon(carregador.icones.playIcon16);
        } else {
            jButton_Play.setIcon(carregador.icones.pauseIcon16);
        }
        if (carregador.isRandom()) {
            jToggleButton1.setIcon(carregador.icones.randomOnIcon16);
        } else {
            jToggleButton1.setIcon(carregador.icones.randomOffIcon16);
        }
        if (carregador.isRepeat()) {
            jToggle_Repete.setIcon(carregador.icones.repeatOnIcon16);
        } else {
            jToggle_Repete.setIcon(carregador.icones.repeatOffIcon16);
        }
        if (isAlwaysOnTop()) {
            jLabel_top.setIcon(carregador.icones.topOn);
        } else {
            jLabel_top.setIcon(carregador.icones.topOff);
        }
    }
    MouseAdapter mouseAdapterPopUp = new MouseAdapter() {

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
    };
    MouseMotionAdapter mouseMotionPopUp = new MouseMotionAdapter() {

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
//
    }

    /**Atualiza o icones Play
    @param Icone a ser colocado.
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
//        jLabelNomeMusica.setVisible(true);
//        jLabelNomeMusicaMask.setVisible(false);
//        jLabelNomeMusicaSombra.setVisible(false);
        jLabelNomeMusica.setText(n);
//        jLabelNomeMusicaMask.setText(n + " 2 -- ");
//        jLabelNomeMusicaSombra.setText(n + " 3 -- ");
//        startTextAnim();
//        jv.add(jLabelNomeMusica);
//        jScrollPane1.setViewport(jv);
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
        pack();
    }

    @Override
    public void atualizaLabels(String nome, int bits, String tempo, int freq) {
        //TODO algo
    }

    class tarefaRollOut extends TimerTask {

        @Override
        public void run() {
            jPanel2.setVisible(false);
            pack();
        }
    }

    /**Faz o efeito de MouseEntered (circula o icones)
    @param Componente circular que você quer circular.
     */
    public void objetoRollOver(JLabel c) {
        Icon aux = c.getIcon();

        if (aux.getIconWidth() > 15) {

            c.getGraphics().drawOval(2, 3, aux.getIconWidth(), aux.getIconHeight());
            c.getGraphics().setColor(new Color(250, 250, 250, 30));
            c.getGraphics().fillOval(2, 3, aux.getIconWidth(), aux.getIconHeight());
        }
    }

    public void objetoRollOver2(JLabel c) {
        Icon aux = c.getIcon();
        c.getGraphics().setColor(new Color(250, 250, 250, 30));
        c.getGraphics().drawRect(2, 3, aux.getIconWidth(), aux.getIconHeight());
    }

    /**Deixa o JMini visivel ou não
    @param b boolean que indica se a janela é estará visivel.
    @param e MouseEvent é usado apenas quando o SO não for Windows
     */
    public void setVisible(boolean b, MouseEvent e) {
        if (!super.isVisible()) {

            if (carregador.isRandom()) {
                jToggleButton1.setIcon(carregador.icones.randomOnIcon16);
            } else {
                jToggleButton1.setIcon(carregador.icones.randomOffIcon16);
            }
            if (carregador.isRepeat()) {
                jToggle_Repete.setIcon(carregador.icones.repeatOnIcon16);
            } else {
                jToggle_Repete.setIcon(carregador.icones.repeatOffIcon16);
            }
        }

        jSlider_vol.setPreferredSize(new Dimension(jSlider_vol.getWidth(), jButton_Play.getHeight()));
        jSlider_vol.setValue(musiquera.getVolume());
        jPanel2.setVisible(false);
        pack();
        super.setVisible(b);

    }

    private void showMenu(MouseEvent e) {
        jPopupMenu1.show(e.getComponent(), e.getX(), e.getY());
    }

    public void atualizaTempo(int t) {
        jSlider_Tempo.setValue(t);
    }

    public void atualizaVolume(int t) {
        jSlider_vol.setValue(t);
        jSlider_vol.setToolTipText(jSlider_vol.getValue() + "%");
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;
        

        jPopupMenu1 = new javax.swing.JPopupMenu();
        jMenuVisualizacoes = new javax.swing.JMenu();
        jCheckBox_top = new javax.swing.JCheckBoxMenuItem();
        jCheckBox_lib = new javax.swing.JCheckBoxMenuItem();
        jCheckBox_list = new javax.swing.JCheckBoxMenuItem();
        jMenuBalanco = new javax.swing.JMenu();
        jMenu3 = new javax.swing.JMenu();
        jRadioButtonMenuItem1 = new javax.swing.JRadioButtonMenuItem();
        jRadioButtonMenuItem2 = new javax.swing.JRadioButtonMenuItem();
        jRadioButtonMenuItem3 = new javax.swing.JRadioButtonMenuItem();
        jRadioButtonMenuItem4 = new javax.swing.JRadioButtonMenuItem();
        jRadioButtonMenuItem5 = new javax.swing.JRadioButtonMenuItem();
        jMenuRestaura = new javax.swing.JMenuItem();
        jMenu_Ocult = new javax.swing.JMenuItem();
        jMenu_exit = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        jMenuCancelar = new javax.swing.JMenuItem();
        buttonGrou_Balanco = new javax.swing.ButtonGroup();
        jPanel4 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jSlider_Tempo = new javax.swing.JSlider();
        jPanel3 = new javax.swing.JPanel();
        jLabel_popup = new javax.swing.JLabel();
        jLabel_Playlist = new javax.swing.JLabel();
        jLabel_lib = new javax.swing.JLabel();
        jLabel_top = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jButton_Play = new javax.swing.JLabel();
        jButton_Stop = new javax.swing.JLabel();
        jButton_Ant = new javax.swing.JLabel();
        jButton_Next = new javax.swing.JLabel();
        jToggleButton1 = new javax.swing.JLabel();
        jToggle_Repete = new javax.swing.JLabel();
        jSlider_vol = new javax.swing.JSlider();
        jPanel5 = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        jLabelNomeMusica = new javax.swing.JLabel();

        jMenuVisualizacoes.setMnemonic('v');
        jMenuVisualizacoes.setText("Visualizações");

        jCheckBox_top.setText("Sempre Visível");
        jCheckBox_top.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jCheckBox_topStateChanged(evt);
            }
        });
        jMenuVisualizacoes.add(jCheckBox_top);

        jCheckBox_lib.setText("Mostrar Biblioteca");
        jCheckBox_lib.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox_libActionPerformed(evt);
            }
        });
        jMenuVisualizacoes.add(jCheckBox_lib);

        jCheckBox_list.setText("Mostrar PlayList");
        jCheckBox_list.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox_listActionPerformed(evt);
            }
        });
        jMenuVisualizacoes.add(jCheckBox_list);

        jPopupMenu1.add(jMenuVisualizacoes);

        jMenuBalanco.setMnemonic('o');
        jMenuBalanco.setText("Opções");

        jMenu3.setText("Balanço");

        buttonGrou_Balanco.add(jRadioButtonMenuItem1);
        jRadioButtonMenuItem1.setText("Dierito");
        jRadioButtonMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonMenuItem1ActionPerformed(evt);
            }
        });
        jMenu3.add(jRadioButtonMenuItem1);

        buttonGrou_Balanco.add(jRadioButtonMenuItem2);
        jRadioButtonMenuItem2.setText("75%");
        jRadioButtonMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonMenuItem2ActionPerformed(evt);
            }
        });
        jMenu3.add(jRadioButtonMenuItem2);

        buttonGrou_Balanco.add(jRadioButtonMenuItem3);
        jRadioButtonMenuItem3.setText("Centro");
        jRadioButtonMenuItem3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonMenuItem3ActionPerformed(evt);
            }
        });
        jMenu3.add(jRadioButtonMenuItem3);

        buttonGrou_Balanco.add(jRadioButtonMenuItem4);
        jRadioButtonMenuItem4.setText("25%");
        jRadioButtonMenuItem4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonMenuItem4ActionPerformed(evt);
            }
        });
        jMenu3.add(jRadioButtonMenuItem4);

        buttonGrou_Balanco.add(jRadioButtonMenuItem5);
        jRadioButtonMenuItem5.setText("Esquerdo");
        jRadioButtonMenuItem5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonMenuItem5ActionPerformed(evt);
            }
        });
        jMenu3.add(jRadioButtonMenuItem5);

        jMenuBalanco.add(jMenu3);

        jPopupMenu1.add(jMenuBalanco);

        jMenuRestaura.setMnemonic('r');
        jMenuRestaura.setText("Restaurar");
        jMenuRestaura.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuRestauraActionPerformed(evt);
            }
        });
        jPopupMenu1.add(jMenuRestaura);

        jMenu_Ocult.setMnemonic('m');
        jMenu_Ocult.setText("Ocultar-me");
        jMenu_Ocult.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenu_OcultActionPerformed(evt);
            }
        });
        jPopupMenu1.add(jMenu_Ocult);

        jMenu_exit.setMnemonic('f');
        jMenu_exit.setText("Fechar Player");
        jMenu_exit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenu_exitActionPerformed(evt);
            }
        });
        jPopupMenu1.add(jMenu_exit);
        jPopupMenu1.add(jSeparator1);

        jMenuCancelar.setMnemonic('c');
        jMenuCancelar.setText("Cancelar");
        jPopupMenu1.add(jMenuCancelar);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setBackground(new java.awt.Color(51, 51, 51));
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        setIconImage(null);
        setMinimumSize(new java.awt.Dimension(100, 40));
        setUndecorated(true);
        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseExited(java.awt.event.MouseEvent evt) {
                formMouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                formMousePressed(evt);
            }
        });
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });
        addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                formMouseDragged(evt);
            }
        });
        getContentPane().setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 1, 1));

        jPanel4.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED, new java.awt.Color(204, 204, 204), new java.awt.Color(204, 204, 204), new java.awt.Color(0, 0, 0), new java.awt.Color(51, 51, 51)), javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED, new java.awt.Color(153, 153, 153), new java.awt.Color(204, 204, 204), new java.awt.Color(102, 102, 102), new java.awt.Color(153, 153, 153))));
        jPanel4.setForeground(new java.awt.Color(102, 102, 102));
        jPanel4.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jPanel4.addMouseWheelListener(new java.awt.event.MouseWheelListener() {
            public void mouseWheelMoved(java.awt.event.MouseWheelEvent evt) {
                jPanel4MouseWheelMoved(evt);
            }
        });
        jPanel4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jPanel4MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jPanel4MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jPanel4MouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jPanel4MousePressed(evt);
            }
        });
        jPanel4.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                jPanel4MouseDragged(evt);
            }
        });
        jPanel4.setLayout(new java.awt.GridBagLayout());

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel2.setLayout(new java.awt.BorderLayout());

        jSlider_Tempo.setBackground(new java.awt.Color(255, 255, 255));
        jSlider_Tempo.setMaximum(1000);
        jSlider_Tempo.setToolTipText("");
        jSlider_Tempo.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jSlider_Tempo.setMinimumSize(new java.awt.Dimension(36, 14));
        jSlider_Tempo.setPreferredSize(new java.awt.Dimension(180, 14));

        

        jSlider_Tempo.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jSlider_TempoMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jSlider_TempoMouseReleased(evt);
            }
        });
        jPanel2.add(jSlider_Tempo, java.awt.BorderLayout.CENTER);

        jPanel3.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 1, 0));

        jLabel_popup.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/img/icons/tipo2/c_menu.png"))); // NOI18N
        jLabel_popup.setToolTipText("Menu de opções");
        jLabel_popup.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jLabel_popup.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel_popupMouseClicked(evt);
            }
        });
        jPanel3.add(jLabel_popup);

        jLabel_Playlist.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/img/playlist.gif"))); // NOI18N
        jLabel_Playlist.setToolTipText("Playlist");
        jLabel_Playlist.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jLabel_Playlist.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel_PlaylistMouseClicked(evt);
            }
        });
        jPanel3.add(jLabel_Playlist);

        jLabel_lib.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/img/biblioteca.png"))); // NOI18N
        jLabel_lib.setToolTipText("Biblioteca");
        jLabel_lib.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jLabel_lib.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel_libMouseClicked(evt);
            }
        });
        jPanel3.add(jLabel_lib);

        jLabel_top.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/img/icons/tipo2/c_top_off.png"))); // NOI18N
        jLabel_top.setToolTipText("Sempre Visível");
        jLabel_top.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jLabel_top.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel_topMouseClicked(evt);
            }
        });
        jPanel3.add(jLabel_top);

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 8));
        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/img/x.png"))); // NOI18N
        jLabel1.setToolTipText("Ocultar");
        jLabel1.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jLabel1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel1MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jLabel1MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jLabel1MouseExited(evt);
            }
        });
        jPanel3.add(jLabel1);

        jPanel2.add(jPanel3, java.awt.BorderLayout.LINE_START);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        jPanel4.add(jPanel2, gridBagConstraints);

        jPanel1.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jPanel1.setOpaque(false);
        jPanel1.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 5, 1));

        jButton_Play.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jButton_Play.setText("Tocar");
        jButton_Play.setToolTipText("Tocar");
        jButton_Play.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        jButton_Play.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jButton_Play.addMouseWheelListener(new java.awt.event.MouseWheelListener() {
            public void mouseWheelMoved(java.awt.event.MouseWheelEvent evt) {
                jButton_PlayMouseWheelMoved(evt);
            }
        });
        jButton_Play.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jButton_PlayMouseReleased(evt);
            }
        });
        jPanel1.add(jButton_Play);

        jButton_Stop.setText("Parar");
        jButton_Stop.setToolTipText("Parar");
        jButton_Stop.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jButton_Stop.addMouseWheelListener(new java.awt.event.MouseWheelListener() {
            public void mouseWheelMoved(java.awt.event.MouseWheelEvent evt) {
                jButton_StopMouseWheelMoved(evt);
            }
        });
        jButton_Stop.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jButton_StopMouseReleased(evt);
            }
        });
        jPanel1.add(jButton_Stop);

        jButton_Ant.setText("Voltar");
        jButton_Ant.setToolTipText("Voltar");
        jButton_Ant.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jButton_Ant.addMouseWheelListener(new java.awt.event.MouseWheelListener() {
            public void mouseWheelMoved(java.awt.event.MouseWheelEvent evt) {
                jButton_AntMouseWheelMoved(evt);
            }
        });
        jButton_Ant.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jButton_AntMouseReleased(evt);
            }
        });
        jPanel1.add(jButton_Ant);

        jButton_Next.setText("Avançar");
        jButton_Next.setToolTipText("Avançar");
        jButton_Next.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jButton_Next.addMouseWheelListener(new java.awt.event.MouseWheelListener() {
            public void mouseWheelMoved(java.awt.event.MouseWheelEvent evt) {
                jButton_NextMouseWheelMoved(evt);
            }
        });
        jButton_Next.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jButton_NextMouseReleased(evt);
            }
        });
        jPanel1.add(jButton_Next);

        jToggleButton1.setText("Random");
        jToggleButton1.setToolTipText("Aleatório");
        jToggleButton1.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jToggleButton1.addMouseWheelListener(new java.awt.event.MouseWheelListener() {
            public void mouseWheelMoved(java.awt.event.MouseWheelEvent evt) {
                jToggleButton1MouseWheelMoved(evt);
            }
        });
        jToggleButton1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jToggleButton1MouseReleased(evt);
            }
        });
        jPanel1.add(jToggleButton1);

        jToggle_Repete.setText("Repeat");
        jToggle_Repete.setToolTipText("Repetir PlayList");
        jToggle_Repete.addMouseWheelListener(new java.awt.event.MouseWheelListener() {
            public void mouseWheelMoved(java.awt.event.MouseWheelEvent evt) {
                jToggle_RepeteMouseWheelMoved(evt);
            }
        });
        jToggle_Repete.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jToggle_RepeteMouseReleased(evt);
            }
        });
        jPanel1.add(jToggle_Repete);

        jSlider_vol.setBackground(new java.awt.Color(255, 255, 255));
        jSlider_vol.setToolTipText("Volume");
        jSlider_vol.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jSlider_vol.setPreferredSize(new java.awt.Dimension(100, 23));

        

        jSlider_vol.addMouseWheelListener(new java.awt.event.MouseWheelListener() {
            public void mouseWheelMoved(java.awt.event.MouseWheelEvent evt) {
                jSlider_volMouseWheelMoved(evt);
            }
        });
        jSlider_vol.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jSlider_volMouseReleased(evt);
            }
        });
        jPanel1.add(jSlider_vol);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        jPanel4.add(jPanel1, gridBagConstraints);

        jPanel5.setFont(new java.awt.Font("DejaVu Sans", 0, 11));
        jPanel5.setPreferredSize(new java.awt.Dimension(300, 34));

        jLabelNomeMusica.setFont(new java.awt.Font("DejaVu Sans", 0, 11));
        jLabelNomeMusica.setText("jLabel2");

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addComponent(jLabelNomeMusica)
                .addContainerGap(223, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addComponent(jLabelNomeMusica))
        );

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanel4.add(jPanel5, gridBagConstraints);

        getContentPane().add(jPanel4);


        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jLabel1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel1MouseClicked
        if (evt.getButton() == 1) {
            setVisible(false);
        }
    }//GEN-LAST:event_jLabel1MouseClicked

    private void formMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMousePressed

        setOndeEstou(evt);
    }//GEN-LAST:event_formMousePressed

    private void formMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseDragged

        vouParaOnde(evt);
    }//GEN-LAST:event_formMouseDragged

    private void jButton_PlayMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton_PlayMouseReleased

        if (!(thisX != this.getX() || thisY != this.getY())) {
            if (evt.getButton() == MouseEvent.BUTTON1) {
                musiquera.tocarPausar();
            }
        }
    }//GEN-LAST:event_jButton_PlayMouseReleased

    private void jButton_StopMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton_StopMouseReleased

        if (!(thisX != this.getX() || thisY != this.getY())) {
            if (evt.getButton() == MouseEvent.BUTTON1) {
                musiquera.parar();
            }
        }
    }//GEN-LAST:event_jButton_StopMouseReleased

    private void jButton_AntMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton_AntMouseReleased

        if (!(thisX != this.getX() || thisY != this.getY())) {
            if (evt.getButton() == MouseEvent.BUTTON1) {
                tarefa.cancel();
                tarefa = new Timer();
                musiquera.abrir(musiquera.getPreviousMusica(), 0, false);
                // tarefa.schedule(principal.getExecutaAnterior(), 10);
            }
        }
    }//GEN-LAST:event_jButton_AntMouseReleased

    private void jButton_NextMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton_NextMouseReleased

        if (!(thisX != this.getX() || thisY != this.getY())) {
            if (evt.getButton() == MouseEvent.BUTTON1) {
                tarefa.cancel();
                tarefa = new Timer();
                musiquera.abrir(musiquera.getNextMusica(), 0, false);
                //  tarefa.schedule(principal.getExecutaProxima(), 10);
            }
        }
    }//GEN-LAST:event_jButton_NextMouseReleased

    private void jToggleButton1MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jToggleButton1MouseReleased

        if (!(thisX != this.getX() || thisY != this.getY())) {
            if (evt.getButton() == MouseEvent.BUTTON1) {

                carregador.setRandom(!carregador.isRandom());
                if (carregador.isRandom()) {
                    jToggleButton1.setIcon(carregador.icones.randomOnIcon16);
                } else {
                    jToggleButton1.setIcon(carregador.icones.randomOffIcon16);
                }
            }
        }
    }//GEN-LAST:event_jToggleButton1MouseReleased

    private void jSlider_volMouseWheelMoved(java.awt.event.MouseWheelEvent evt) {//GEN-FIRST:event_jSlider_volMouseWheelMoved
        int v = jSlider_vol.getValue() - evt.getWheelRotation();
        jSlider_vol.setValue(v);
        jSlider_volMouseReleased(evt);
}//GEN-LAST:event_jSlider_volMouseWheelMoved

    private void jSlider_TempoMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jSlider_TempoMouseReleased

        musiquera.skipTo(new Double(jSlider_Tempo.getValue()) / jSlider_Tempo.getMaximum());
        ajusteDeTempo = false;
}//GEN-LAST:event_jSlider_TempoMouseReleased

    private void jPanel4MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel4MouseEntered

        rollOver();
    }//GEN-LAST:event_jPanel4MouseEntered

    private void jPanel4MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel4MousePressed

        setOndeEstou(evt);
    }//GEN-LAST:event_jPanel4MousePressed

    private void jPanel4MouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel4MouseDragged

        vouParaOnde(evt);
    }//GEN-LAST:event_jPanel4MouseDragged

    private void jPanel4MouseWheelMoved(java.awt.event.MouseWheelEvent evt) {//GEN-FIRST:event_jPanel4MouseWheelMoved

        jSlider_vol.setValue(jSlider_vol.getValue() - evt.getWheelRotation());
    }//GEN-LAST:event_jPanel4MouseWheelMoved

    private void jButton_PlayMouseWheelMoved(java.awt.event.MouseWheelEvent evt) {//GEN-FIRST:event_jButton_PlayMouseWheelMoved
        jSlider_vol.setValue(jSlider_vol.getValue() - evt.getWheelRotation());
    }//GEN-LAST:event_jButton_PlayMouseWheelMoved

    private void jButton_StopMouseWheelMoved(java.awt.event.MouseWheelEvent evt) {//GEN-FIRST:event_jButton_StopMouseWheelMoved
        jSlider_vol.setValue(jSlider_vol.getValue() - evt.getWheelRotation());
    }//GEN-LAST:event_jButton_StopMouseWheelMoved

    private void jButton_AntMouseWheelMoved(java.awt.event.MouseWheelEvent evt) {//GEN-FIRST:event_jButton_AntMouseWheelMoved
        jSlider_vol.setValue(jSlider_vol.getValue() - evt.getWheelRotation());
    }//GEN-LAST:event_jButton_AntMouseWheelMoved

    private void jButton_NextMouseWheelMoved(java.awt.event.MouseWheelEvent evt) {//GEN-FIRST:event_jButton_NextMouseWheelMoved
        jSlider_vol.setValue(jSlider_vol.getValue() - evt.getWheelRotation());
    }//GEN-LAST:event_jButton_NextMouseWheelMoved

    private void jToggleButton1MouseWheelMoved(java.awt.event.MouseWheelEvent evt) {//GEN-FIRST:event_jToggleButton1MouseWheelMoved
        jSlider_vol.setValue(jSlider_vol.getValue() - evt.getWheelRotation());
    }//GEN-LAST:event_jToggleButton1MouseWheelMoved

    private void jLabel1MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel1MouseEntered
        // TODO add your handling code here:
        jLabel1.getGraphics().drawRect(0, 0, jLabel1.getWidth() - 1, jLabel1.getHeight() - 1);

    }//GEN-LAST:event_jLabel1MouseEntered

    private void jLabel1MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel1MouseExited
        evt.getComponent().repaint();
    }//GEN-LAST:event_jLabel1MouseExited

    private void jToggle_RepeteMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jToggle_RepeteMouseReleased
        if (!(thisX != this.getX() || thisY != this.getY())) {
            if (evt.getButton() == MouseEvent.BUTTON1) {

                carregador.setRepeat(!carregador.isRepeat());
                if (carregador.isRepeat()) {
                    jToggle_Repete.setIcon(carregador.icones.repeatOnIcon16);
                } else {
                    jToggle_Repete.setIcon(carregador.icones.repeatOffIcon16);
                }
            }
        }
    }//GEN-LAST:event_jToggle_RepeteMouseReleased

    private void jToggle_RepeteMouseWheelMoved(java.awt.event.MouseWheelEvent evt) {//GEN-FIRST:event_jToggle_RepeteMouseWheelMoved
        jSlider_vol.setValue(jSlider_vol.getValue() - evt.getWheelRotation());
    }//GEN-LAST:event_jToggle_RepeteMouseWheelMoved

    private void jPanel4MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel4MouseClicked
        // TODO add your handling code here:
        if (evt.getButton() == MouseEvent.BUTTON1) {
            if (evt.getClickCount() == 2) {
                carregador.setPrincipalComoBase();
            }
        }
        if (evt.getButton() == MouseEvent.BUTTON3) {
            jPopupMenu1.show(this, evt.getX(), evt.getY());
        }
    }//GEN-LAST:event_jPanel4MouseClicked

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        // TODO add your handling code here:
        // principal.setConf();
    }//GEN-LAST:event_formWindowClosing

    private void jMenu_exitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenu_exitActionPerformed
        // TODO add your handling code here:
        carregador.sair();
    }//GEN-LAST:event_jMenu_exitActionPerformed

    private void jRadioButtonMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonMenuItem1ActionPerformed
        // TODO add your handling code here:
        musiquera.setBalanco((byte) 100);
    }//GEN-LAST:event_jRadioButtonMenuItem1ActionPerformed

    private void jRadioButtonMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonMenuItem2ActionPerformed
        // TODO add your handling code here:
        musiquera.setBalanco((byte) 50);
    }//GEN-LAST:event_jRadioButtonMenuItem2ActionPerformed

    private void jRadioButtonMenuItem3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonMenuItem3ActionPerformed
        // TODO add your handling code here:
        musiquera.setBalanco((byte) 0);
    }//GEN-LAST:event_jRadioButtonMenuItem3ActionPerformed

    private void jRadioButtonMenuItem4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonMenuItem4ActionPerformed
        // TODO add your handling code here:
        musiquera.setBalanco((byte) -50);
    }//GEN-LAST:event_jRadioButtonMenuItem4ActionPerformed

    private void jRadioButtonMenuItem5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonMenuItem5ActionPerformed
        // TODO add your handling code here:
        musiquera.setBalanco((byte) -100);
    }//GEN-LAST:event_jRadioButtonMenuItem5ActionPerformed

    private void jCheckBox_libActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox_libActionPerformed
        // TODO add your handling code here:
        if (jCheckBox_lib.isSelected()) {
            carregador.mostrarBiblioteca();
        } else {
            carregador.ocultarBiblioteca();
        }
    }//GEN-LAST:event_jCheckBox_libActionPerformed

    private void jCheckBox_listActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox_listActionPerformed
        // TODO add your handling code here:
        if (jCheckBox_list.isSelected()) {
            carregador.mostrarPlayList();
        } else {
            carregador.ocultarPlayList();
        }

    }//GEN-LAST:event_jCheckBox_listActionPerformed

    private void jMenu_OcultActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenu_OcultActionPerformed
        // TODO add your handling code here:
        setVisible(false);
    }//GEN-LAST:event_jMenu_OcultActionPerformed

    private void jMenuRestauraActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuRestauraActionPerformed
        carregador.setPrincipalComoBase();
    }//GEN-LAST:event_jMenuRestauraActionPerformed

    private void jLabel_PlaylistMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel_PlaylistMouseClicked
        if (evt.getButton() == MouseEvent.BUTTON1) {
            boolean visible = carregador.isPlayListVisible();
            jCheckBox_list.setSelected(visible);
            if (visible) {
                carregador.mostrarPlayList();
            } else {
                carregador.ocultarPlayList();
            }
        }
}//GEN-LAST:event_jLabel_PlaylistMouseClicked

    private void jLabel_libMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel_libMouseClicked
        if (evt.getButton() == MouseEvent.BUTTON1) {
            boolean visible = carregador.isBibliotecaVisible();
            jCheckBox_list.setSelected(visible);
            if (visible) {
                carregador.mostrarBiblioteca();
            } else {
                carregador.ocultarBiblioteca();
            }
        }
}//GEN-LAST:event_jLabel_libMouseClicked

    private void jLabel_popupMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel_popupMouseClicked
        // TODO add your handling code here:
        jPopupMenu1.show(jLabel_popup, evt.getX(), evt.getY());
    }//GEN-LAST:event_jLabel_popupMouseClicked

    private void jLabel_topMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel_topMouseClicked
        // TODO add your handling code here:
        if (evt.getButton() == MouseEvent.BUTTON1) {
            jCheckBox_top.setSelected(!jCheckBox_top.isSelected());
        }
    }//GEN-LAST:event_jLabel_topMouseClicked

    private void jCheckBox_topStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jCheckBox_topStateChanged
        // TODO add your handling code here:
        setAlwaysOnTop(jCheckBox_top.isSelected());
        if (isAlwaysOnTop()) {
            jLabel_top.setIcon(carregador.icones.topOn);
        } else {
            jLabel_top.setIcon(carregador.icones.topOff);
        }
    }//GEN-LAST:event_jCheckBox_topStateChanged

    private void formMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseExited
        // TODO add your handling code here:
        System.out.println("Hit: " + Testes.hitTest(evt.getComponent(), new Point(evt.getXOnScreen(), evt.getYOnScreen())));
        if (!Testes.hitTest(this, new Point(evt.getXOnScreen(), evt.getYOnScreen()))) {
            tarefa.cancel();
            tarefa = new Timer();
            tarefa.schedule(new tarefaRollOut(), 500);
        }
    }//GEN-LAST:event_formMouseExited

    private void jPanel4MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel4MouseExited
        // TODO add your handling code here:
        System.out.println("Hit: " + Testes.hitTest(evt.getComponent(), new Point(evt.getXOnScreen(), evt.getYOnScreen())));
        if (!Testes.hitTest(this, new Point(evt.getXOnScreen(), evt.getYOnScreen()))) {
            tarefa.cancel();
            tarefa = new Timer();
            tarefa.schedule(new tarefaRollOut(), 500);
        }
    }//GEN-LAST:event_jPanel4MouseExited

    private void jSlider_volMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jSlider_volMouseReleased
        // TODO add your handling code here:
        musiquera.setVolume((byte) jSlider_vol.getValue());
        jSlider_vol.setToolTipText(jSlider_vol.getValue() + "%");
    }//GEN-LAST:event_jSlider_volMouseReleased

    private void jSlider_TempoMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jSlider_TempoMousePressed
        // TODO add your handling code here:
        ajusteDeTempo = true;
    }//GEN-LAST:event_jSlider_TempoMousePressed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        // System.setOut();
//        java.awt.EventQueue.invokeLater(new Runnable() {
//            public void run() {
////                JMini dialog = new JMini(new javax.swing.JFrame(), true);
//                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
//                    public void windowClosing(java.awt.event.WindowEvent e) {
//                        System.exit(0);
//                    }
//                });
//                dialog.setVisible(true);
//            }
//        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGrou_Balanco;
    private javax.swing.JLabel jButton_Ant;
    private javax.swing.JLabel jButton_Next;
    private javax.swing.JLabel jButton_Play;
    private javax.swing.JLabel jButton_Stop;
    private javax.swing.JCheckBoxMenuItem jCheckBox_lib;
    private javax.swing.JCheckBoxMenuItem jCheckBox_list;
    private javax.swing.JCheckBoxMenuItem jCheckBox_top;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabelNomeMusica;
    private javax.swing.JLabel jLabel_Playlist;
    private javax.swing.JLabel jLabel_lib;
    private javax.swing.JLabel jLabel_popup;
    private javax.swing.JLabel jLabel_top;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenu jMenuBalanco;
    private javax.swing.JMenuItem jMenuCancelar;
    private javax.swing.JMenuItem jMenuRestaura;
    private javax.swing.JMenu jMenuVisualizacoes;
    private javax.swing.JMenuItem jMenu_Ocult;
    private javax.swing.JMenuItem jMenu_exit;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPopupMenu jPopupMenu1;
    private javax.swing.JRadioButtonMenuItem jRadioButtonMenuItem1;
    private javax.swing.JRadioButtonMenuItem jRadioButtonMenuItem2;
    private javax.swing.JRadioButtonMenuItem jRadioButtonMenuItem3;
    private javax.swing.JRadioButtonMenuItem jRadioButtonMenuItem4;
    private javax.swing.JRadioButtonMenuItem jRadioButtonMenuItem5;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JSlider jSlider_Tempo;
    public javax.swing.JSlider jSlider_vol;
    private javax.swing.JLabel jToggleButton1;
    private javax.swing.JLabel jToggle_Repete;
    // End of variables declaration//GEN-END:variables
}
