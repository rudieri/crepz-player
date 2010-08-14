package com;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.border.BevelBorder;


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
public class JMini extends javax.swing.JDialog {

    /** Creates new form treco */
    private JPrincipal principal;
    private JPlayList playList;
    private JBiBlioteca lib;
    private int initX;
    private int initY;
    private int thisX;
    private int thisY;
    private Timer th;
    Timer tarefa = new Timer();
    private int estado=0;

    public JMini(java.awt.Frame parent, boolean modal, JPrincipal jprincipal, JPlayList pl, JBiBlioteca bl) {
        super(parent, modal);
        th=new Timer(true);
        initComponents();
        this.principal = jprincipal;
        this.playList = pl;
        lib = new JBiBlioteca(this, principal);
        //  this.lib=bl;
        startEvents();



        //System.out.println(jLabel1.getX()+jLabel1.getWidth());
        //System.out.println(jLabel1.getX());


    }

    /**Coloca os icones nos Jlabels*/
    public void inicializaIcones() {
        jButton_Play.setText("");
        jButton_Stop.setText("");
        jButton_Next.setText("");
        jButton_Ant.setText("");
        jToggleButton1.setText("");
        jToggle_Repete.setText("");

        jButton_Stop.setIcon(principal.resizeIcons(principal.bf_stopIcon));
        jButton_Ant.setIcon(principal.resizeIcons(principal.bf_voltaIcon));
        jButton_Next.setIcon(principal.resizeIcons(principal.bf_frenteIcon));
        jLabel_Playlist.setIcon(principal.pl);
        jLabel_lib.setIcon(principal.lib);
        jLabel_popup.setIcon(principal.menu);
        jLabel1.setIcon(principal.xis);
        if (principal.getPause()) {
            jButton_Play.setIcon(principal.resizeIcons(principal.bf_playIcon));
        } else {
            jButton_Play.setIcon(principal.resizeIcons(principal.bf_pauseIcon));
        }
        if (principal.random) {
            jToggleButton1.setIcon(principal.resizeIcons(principal.bf_randomOnIcon));
        } else {
            jToggleButton1.setIcon(principal.resizeIcons(principal.bf_randomOffIcon));
        }
        if (principal.getRepetir()) {
            jToggle_Repete.setIcon(principal.resizeIcons(principal.bf_repeatOnIcon));
        } else {
            jToggle_Repete.setIcon(principal.resizeIcons(principal.bf_repeatOffIcon));
        }
        if (isAlwaysOnTop()) {
            jLabel_top.setIcon(principal.topOn);
        } else {
            jLabel_top.setIcon(principal.topOff);
        }
    }

    /**Atualiza o icone Play
    @param Icone a ser colocado.
     */
    private void startEvents() {
        jButton_Stop.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                showMenu(e);
            }
        });

        jButton_Next.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                showMenu(e);
            }
        });

        jButton_Ant.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                showMenu(e);
            }
        });

        jButton_Play.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                showMenu(e);
            }
        });
        jToggleButton1.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                showMenu(e);
            }
        });

        jToggle_Repete.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                showMenu(e);
            }
        });

        lib.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                jCheckBox_lib.setSelected(false);
                setTop(jCheckBox_top.isSelected());
            }
        });
        playList.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                jCheckBox_list.setSelected(false);
                setTop(jCheckBox_top.isSelected());
            }
        });
        MouseAdapter mal=new MouseAdapter() {

            @Override
            public void mouseEntered(MouseEvent e) {
                super.mouseEntered(e);
                   objetoRollOver2((JLabel)e.getComponent());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                super.mouseExited(e);
                e.getComponent().repaint();
            }
            
        
        };
        jLabel_Playlist.addMouseListener(mal);
        jLabel_lib.addMouseListener(mal);
        jLabel_popup.addMouseListener(mal);
        jLabel_top.addMouseListener(mal);
    }

    public void setPlayIcon(Icon ic) {
        jButton_Play.setIcon(ic);
    }

    public void setLocal(int x, int y) {
        reshape(x, y, getWidth(), getHeight());
    }

    public Point getLocal() {
        return new Point(getX(), getY());
    }

    public boolean getTop() {
        return isAlwaysOnTop();
    }

    public void setTop(boolean b) {
        setAlwaysOnTop(b);
        jCheckBox_top.setSelected(b);
    }

    private void vouParaOnde(MouseEvent e) {
        estado++;
        if(estado==5){
            this.setLocation(e.getXOnScreen() - initX, e.getYOnScreen() - initY);
            estado=0;
        }
    }

    private void ondeEstou(MouseEvent e) {
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

    class tarefaRollOut extends TimerTask {

        @Override
        public void run() {
            jPanel2.setVisible(false);
            pack();
        }
    }

    /**Faz o efeito de MouseEntered (circula o icone)
    @param Componente circular que voc� quer circular.
     */
    public void objetoRollOver(JLabel c) {
        Icon aux = c.getIcon();
        c.getGraphics().drawOval(2, 3, aux.getIconWidth(), aux.getIconHeight());
    }
    public void objetoRollOver2(JLabel c){
         Icon aux = c.getIcon();
         c.getGraphics().drawRect(2, 3, aux.getIconWidth(), aux.getIconHeight());
    }

    /**Deixa o JMini visivel ou n�o
    @param b boolean que indica se a janela � estar� visivel.
    @param e MouseEvent � usado apenas quando o SO n�o for Windows
     */
    public void setVisible(boolean b, MouseEvent e) {
        if (!super.isVisible()) {

            if (principal.random) {
                jToggleButton1.setIcon(principal.resizeIcons(principal.bf_randomOnIcon));
            } else {
                jToggleButton1.setIcon(principal.resizeIcons(principal.bf_randomOffIcon));
            }
            if (principal.repeat) {
                jToggle_Repete.setIcon(principal.resizeIcons(principal.bf_repeatOnIcon));
            } else {
                jToggle_Repete.setIcon(principal.resizeIcons(principal.bf_repeatOffIcon));
            }

//            if (!System.getProperty("sun.desktop").equalsIgnoreCase("windows")) {
//                if (e.getYOnScreen() < 300) {
//                    this.setLocation(e.getXOnScreen() - this.getWidth() / 2, e.getYOnScreen() + 13);
//                } else {
//                    this.setLocation(e.getXOnScreen() - this.getWidth() / 2, e.getYOnScreen() - this.getHeight() - 13);
//                }
//            }
        }

        jSlider_vol.setPreferredSize(new Dimension(jSlider_vol.getWidth(), jButton_Play.getHeight()));
        jSlider_vol.setValue(principal.getSliderValue());
        jPanel2.setVisible(false);
        pack();
        super.setVisible(b);

    }

    private void showMenu(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON3) {
            jPopupMenu1.show(e.getComponent(), e.getX(), e.getY());
        }
    }

    public void atualizaTempo(int t) {
        jSlider_Tempo.setValue(t);
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
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        jPopupMenu1 = new javax.swing.JPopupMenu();
        jMenu1 = new javax.swing.JMenu();
        jCheckBox_top = new javax.swing.JCheckBoxMenuItem();
        jCheckBox_lib = new javax.swing.JCheckBoxMenuItem();
        jCheckBox_list = new javax.swing.JCheckBoxMenuItem();
        jMenu2 = new javax.swing.JMenu();
        jMenu3 = new javax.swing.JMenu();
        jRadioButtonMenuItem1 = new javax.swing.JRadioButtonMenuItem();
        jRadioButtonMenuItem2 = new javax.swing.JRadioButtonMenuItem();
        jRadioButtonMenuItem3 = new javax.swing.JRadioButtonMenuItem();
        jRadioButtonMenuItem4 = new javax.swing.JRadioButtonMenuItem();
        jRadioButtonMenuItem5 = new javax.swing.JRadioButtonMenuItem();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenu_Ocult = new javax.swing.JMenuItem();
        jMenu_exit = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        jMenuItem1 = new javax.swing.JMenuItem();
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

        jMenu1.setMnemonic('v');
        jMenu1.setText("Visualizações");

        jCheckBox_top.setText("Sempre Visível");
        jCheckBox_top.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jCheckBox_topStateChanged(evt);
            }
        });
        jMenu1.add(jCheckBox_top);

        jCheckBox_lib.setText("Mostrar Biblioteca");
        jCheckBox_lib.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox_libActionPerformed(evt);
            }
        });
        jMenu1.add(jCheckBox_lib);

        jCheckBox_list.setText("Mostrar PlayList");
        jCheckBox_list.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox_listActionPerformed(evt);
            }
        });
        jMenu1.add(jCheckBox_list);

        jPopupMenu1.add(jMenu1);

        jMenu2.setMnemonic('o');
        jMenu2.setText("Opções");

        jMenu3.setText("Balanço");
        jMenu3.addMenuListener(new javax.swing.event.MenuListener() {
            public void menuCanceled(javax.swing.event.MenuEvent evt) {
            }
            public void menuDeselected(javax.swing.event.MenuEvent evt) {
            }
            public void menuSelected(javax.swing.event.MenuEvent evt) {
                jMenu3MenuSelected(evt);
            }
        });

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

        jMenu2.add(jMenu3);

        jPopupMenu1.add(jMenu2);

        jMenuItem2.setMnemonic('r');
        jMenuItem2.setText("Restaurar");
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        jPopupMenu1.add(jMenuItem2);

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

        jMenuItem1.setMnemonic('c');
        jMenuItem1.setText("Cancelar");
        jPopupMenu1.add(jMenuItem1);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setBackground(new java.awt.Color(51, 51, 51));
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        setIconImage(null);
        setMinimumSize(new java.awt.Dimension(100, 30));
        setUndecorated(true);
        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                formMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                formMouseEntered(evt);
            }
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
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                formMouseMoved(evt);
            }
        });
        getContentPane().setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 1, 3));

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

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jSlider_Tempo, org.jdesktop.beansbinding.ELProperty.create("${value}"), jSlider_Tempo, org.jdesktop.beansbinding.BeanProperty.create("value"));
        bindingGroup.addBinding(binding);

        jSlider_Tempo.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jSlider_TempoMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jSlider_TempoMouseExited(evt);
            }
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
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
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
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jButton_PlayMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jButton_PlayMouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jButton_PlayMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jButton_PlayMouseReleased(evt);
            }
        });
        jButton_Play.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                jButton_PlayMouseDragged(evt);
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
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jButton_StopMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jButton_StopMouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jButton_StopMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jButton_StopMouseReleased(evt);
            }
        });
        jButton_Stop.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                jButton_StopMouseDragged(evt);
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
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jButton_AntMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jButton_AntMouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jButton_AntMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jButton_AntMouseReleased(evt);
            }
        });
        jButton_Ant.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                jButton_AntMouseDragged(evt);
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
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jButton_NextMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jButton_NextMouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jButton_NextMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jButton_NextMouseReleased(evt);
            }
        });
        jButton_Next.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                jButton_NextMouseDragged(evt);
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
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jToggleButton1MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jToggleButton1MouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jToggleButton1MousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jToggleButton1MouseReleased(evt);
            }
        });
        jToggleButton1.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                jToggleButton1MouseDragged(evt);
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
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jToggle_RepeteMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jToggle_RepeteMouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jToggle_RepeteMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jToggle_RepeteMouseReleased(evt);
            }
        });
        jToggle_Repete.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                jToggle_RepeteMouseDragged(evt);
            }
        });
        jPanel1.add(jToggle_Repete);

        jSlider_vol.setBackground(new java.awt.Color(255, 255, 255));
        jSlider_vol.setToolTipText("Volume");
        jSlider_vol.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jSlider_vol.setPreferredSize(new java.awt.Dimension(100, 23));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jSlider_vol, org.jdesktop.beansbinding.ELProperty.create("${value}"), jSlider_vol, org.jdesktop.beansbinding.BeanProperty.create("value"));
        bindingGroup.addBinding(binding);

        jSlider_vol.addMouseWheelListener(new java.awt.event.MouseWheelListener() {
            public void mouseWheelMoved(java.awt.event.MouseWheelEvent evt) {
                jSlider_volMouseWheelMoved(evt);
            }
        });
        jSlider_vol.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jSlider_volMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jSlider_volMouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jSlider_volMousePressed(evt);
            }
        });
        jSlider_vol.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSlider_volStateChanged(evt);
            }
        });
        jPanel1.add(jSlider_vol);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.RELATIVE;
        jPanel4.add(jPanel1, gridBagConstraints);

        getContentPane().add(jPanel4);

        bindingGroup.bind();

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formMouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseMoved
//       setVisible(false);
    }//GEN-LAST:event_formMouseMoved

    private void formMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseExited
    }//GEN-LAST:event_formMouseExited

    private void jLabel1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel1MouseClicked
        if(evt.getButton()==1)
        setVisible(false);
    }//GEN-LAST:event_jLabel1MouseClicked

    private void formMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMousePressed

        ondeEstou(evt);
    }//GEN-LAST:event_formMousePressed

    private void formMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseDragged

        vouParaOnde(evt);
    }//GEN-LAST:event_formMouseDragged

    private void jButton_PlayMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton_PlayMousePressed

        ondeEstou(evt);
    }//GEN-LAST:event_jButton_PlayMousePressed

    private void jButton_PlayMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton_PlayMouseReleased

        if (!(thisX != this.getX() || thisY != this.getY())) {
            if (evt.getButton() == MouseEvent.BUTTON1) {
                principal.tocar();
            }
        }
    }//GEN-LAST:event_jButton_PlayMouseReleased

    private void jButton_PlayMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton_PlayMouseDragged


        vouParaOnde(evt);
    }//GEN-LAST:event_jButton_PlayMouseDragged

    private void jButton_StopMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton_StopMousePressed

        ondeEstou(evt);
    }//GEN-LAST:event_jButton_StopMousePressed

    private void jButton_StopMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton_StopMouseReleased

        if (!(thisX != this.getX() || thisY != this.getY())) {
            if (evt.getButton() == MouseEvent.BUTTON1) {
                principal.parar();
            }
        }
    }//GEN-LAST:event_jButton_StopMouseReleased

    private void jButton_StopMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton_StopMouseDragged

        vouParaOnde(evt);
    }//GEN-LAST:event_jButton_StopMouseDragged

    private void jButton_AntMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton_AntMouseReleased

        if (!(thisX != this.getX() || thisY != this.getY())) {
            if (evt.getButton() == MouseEvent.BUTTON1) {
                tarefa.cancel();
                tarefa = new Timer();
                tarefa.schedule(principal.getExecutaAnterior(), 10);
            }
        }
    }//GEN-LAST:event_jButton_AntMouseReleased

    private void jButton_NextMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton_NextMouseReleased

        if (!(thisX != this.getX() || thisY != this.getY())) {
            if (evt.getButton() == MouseEvent.BUTTON1) {
                tarefa.cancel();
                tarefa = new Timer();
                tarefa.schedule(principal.getExecutaProxima(), 10);
            }
        }
    }//GEN-LAST:event_jButton_NextMouseReleased

    private void jToggleButton1MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jToggleButton1MouseReleased

        if (!(thisX != this.getX() || thisY != this.getY())) {
            if (evt.getButton() == MouseEvent.BUTTON1) {
                principal.random = !principal.random;
                playList.setAleatorio(principal.random);
                if (principal.random) {
                    jToggleButton1.setIcon(principal.resizeIcons(principal.bf_randomOnIcon));
                } else {
                    jToggleButton1.setIcon(principal.resizeIcons(principal.bf_randomOffIcon));
                }
            }
        }
    }//GEN-LAST:event_jToggleButton1MouseReleased

    private void jButton_AntMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton_AntMousePressed

        ondeEstou(evt);
    }//GEN-LAST:event_jButton_AntMousePressed

    private void jButton_AntMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton_AntMouseDragged

        vouParaOnde(evt);
    }//GEN-LAST:event_jButton_AntMouseDragged

    private void jButton_NextMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton_NextMouseDragged

        vouParaOnde(evt);
    }//GEN-LAST:event_jButton_NextMouseDragged

    private void jButton_NextMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton_NextMousePressed

        ondeEstou(evt);
    }//GEN-LAST:event_jButton_NextMousePressed

    private void jToggleButton1MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jToggleButton1MousePressed

        ondeEstou(evt);
    }//GEN-LAST:event_jToggleButton1MousePressed

    private void jToggleButton1MouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jToggleButton1MouseDragged

        vouParaOnde(evt);
    }//GEN-LAST:event_jToggleButton1MouseDragged

    private void jSlider_volMouseWheelMoved(java.awt.event.MouseWheelEvent evt) {//GEN-FIRST:event_jSlider_volMouseWheelMoved
        jSlider_vol.setValue(jSlider_vol.getValue() - evt.getWheelRotation());
}//GEN-LAST:event_jSlider_volMouseWheelMoved

    private void jSlider_volMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jSlider_volMousePressed
}//GEN-LAST:event_jSlider_volMousePressed

    private void jSlider_volStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSlider_volStateChanged
        System.out.println(jSlider_vol.getValue());
        principal.mexeVolume(jSlider_vol.getValue());
//            principal.player.setGain(new Double(jSlider_vol.getValue()) / 100);
//            principal.player.setGain(new Double(jSlider_vol.getValue()) / 100);

        jSlider_vol.setToolTipText(jSlider_vol.getValue() + "%");

}//GEN-LAST:event_jSlider_volStateChanged

    private void jSlider_TempoMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jSlider_TempoMousePressed

        principal.ajust = true;
}//GEN-LAST:event_jSlider_TempoMousePressed

    private void jSlider_TempoMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jSlider_TempoMouseReleased
        principal.atualizaTempo(jSlider_Tempo.getValue());
        principal.skipTo();
        principal.ajust = false;
}//GEN-LAST:event_jSlider_TempoMouseReleased

    private void jSlider_TempoMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jSlider_TempoMouseEntered

//        System.out.println("RollOver");

        jSlider_Tempo.setBorder(new BevelBorder(1, Color.getHSBColor(1, 0.22f, 0.66f), Color.lightGray, Color.lightGray, Color.darkGray));
    }//GEN-LAST:event_jSlider_TempoMouseEntered

    private void jSlider_TempoMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jSlider_TempoMouseExited

        jSlider_Tempo.setBorder(null);
    }//GEN-LAST:event_jSlider_TempoMouseExited

    private void formMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseEntered
        tarefa.cancel();
        tarefa = new Timer();
        tarefa.schedule(new tarefaRollOut(), 500);
    }//GEN-LAST:event_formMouseEntered

    private void jPanel4MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel4MouseEntered

        rollOver();
    }//GEN-LAST:event_jPanel4MouseEntered

    private void jPanel4MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel4MousePressed

        ondeEstou(evt);
    }//GEN-LAST:event_jPanel4MousePressed

    private void jPanel4MouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel4MouseDragged

        vouParaOnde(evt);
    }//GEN-LAST:event_jPanel4MouseDragged

    private void jSlider_volMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jSlider_volMouseEntered

        jSlider_vol.setBorder(new BevelBorder(1, Color.getHSBColor(1, 0.22f, 0.66f), Color.lightGray, Color.lightGray, Color.darkGray));
    }//GEN-LAST:event_jSlider_volMouseEntered

    private void jSlider_volMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jSlider_volMouseExited

        jSlider_vol.setBorder(null);
    }//GEN-LAST:event_jSlider_volMouseExited

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

    private void jButton_PlayMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton_PlayMouseEntered
        // TODO add your handling code here:
        // jButton_Play.setIcon(principal.playIcon_Roll);
        objetoRollOver(jButton_Play);
    }//GEN-LAST:event_jButton_PlayMouseEntered

    private void jButton_PlayMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton_PlayMouseExited
        // TODO add your handling code here:
        evt.getComponent().repaint();
    }//GEN-LAST:event_jButton_PlayMouseExited

    private void jButton_StopMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton_StopMouseEntered
        // TODO add your handling code here:
        objetoRollOver(jButton_Stop);
    }//GEN-LAST:event_jButton_StopMouseEntered

    private void jButton_StopMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton_StopMouseExited
        // TODO add your handling code here:
        evt.getComponent().repaint();

    }//GEN-LAST:event_jButton_StopMouseExited

    private void jButton_AntMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton_AntMouseEntered
        // TODO add your handling code here:
        objetoRollOver(jButton_Ant);
    }//GEN-LAST:event_jButton_AntMouseEntered

    private void jButton_AntMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton_AntMouseExited
        // TODO add your handling code here:
        evt.getComponent().repaint();
    }//GEN-LAST:event_jButton_AntMouseExited

    private void jButton_NextMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton_NextMouseEntered
        // TODO add your handling code here:
        objetoRollOver(jButton_Next);
    }//GEN-LAST:event_jButton_NextMouseEntered

    private void jButton_NextMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton_NextMouseExited
        // TODO add your handling code here:
        evt.getComponent().repaint();
    }//GEN-LAST:event_jButton_NextMouseExited

    private void jToggleButton1MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jToggleButton1MouseEntered
        // TODO add your handling code here:
        objetoRollOver(jToggleButton1);
    }//GEN-LAST:event_jToggleButton1MouseEntered

    private void jToggleButton1MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jToggleButton1MouseExited
        // TODO add your handling code here:
        evt.getComponent().repaint();
    }//GEN-LAST:event_jToggleButton1MouseExited

    private void jLabel1MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel1MouseEntered
        // TODO add your handling code here:
        jLabel1.getGraphics().drawRect(0, 0, jLabel1.getWidth() - 1, jLabel1.getHeight() - 1);

    }//GEN-LAST:event_jLabel1MouseEntered

    private void jLabel1MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel1MouseExited
        evt.getComponent().repaint();
    }//GEN-LAST:event_jLabel1MouseExited

    private void jToggle_RepeteMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jToggle_RepeteMouseEntered
        objetoRollOver(jToggle_Repete);
    }//GEN-LAST:event_jToggle_RepeteMouseEntered

    private void jToggle_RepeteMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jToggle_RepeteMouseExited
        evt.getComponent().repaint();
    }//GEN-LAST:event_jToggle_RepeteMouseExited

    private void jToggle_RepeteMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jToggle_RepeteMousePressed
        ondeEstou(evt);
    }//GEN-LAST:event_jToggle_RepeteMousePressed

    private void jToggle_RepeteMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jToggle_RepeteMouseReleased
        if (!(thisX != this.getX() || thisY != this.getY())) {
            if (evt.getButton() == MouseEvent.BUTTON1) {
                principal.repeat = !principal.repeat;
                playList.setRepetir(principal.repeat);
                if (principal.repeat) {
                    jToggle_Repete.setIcon(principal.resizeIcons(principal.bf_repeatOnIcon));
                } else {
                    jToggle_Repete.setIcon(principal.resizeIcons(principal.bf_repeatOffIcon));
                }
            }
        }
    }//GEN-LAST:event_jToggle_RepeteMouseReleased

    private void jToggle_RepeteMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jToggle_RepeteMouseDragged
        // TODO add your handling code here:
        vouParaOnde(evt);
    }//GEN-LAST:event_jToggle_RepeteMouseDragged

    private void jToggle_RepeteMouseWheelMoved(java.awt.event.MouseWheelEvent evt) {//GEN-FIRST:event_jToggle_RepeteMouseWheelMoved
        jSlider_vol.setValue(jSlider_vol.getValue() - evt.getWheelRotation());
    }//GEN-LAST:event_jToggle_RepeteMouseWheelMoved

    private void jPanel4MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel4MouseClicked
        // TODO add your handling code here:
        if (evt.getButton() == MouseEvent.BUTTON1) {
            if (evt.getClickCount() == 2) {
                principal.someTray();
            }
        }
        if (evt.getButton() == MouseEvent.BUTTON3) {
            jPopupMenu1.show(this, evt.getX(), evt.getY());
        }
    }//GEN-LAST:event_jPanel4MouseClicked

    private void formMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_formMouseClicked

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        // TODO add your handling code here:
        principal.setConf();
    }//GEN-LAST:event_formWindowClosing

    private void jMenu_exitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenu_exitActionPerformed
        // TODO add your handling code here:
        principal.sair();
    }//GEN-LAST:event_jMenu_exitActionPerformed

    private void jRadioButtonMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonMenuItem1ActionPerformed
        // TODO add your handling code here:
        principal.setBalaco(100);
    }//GEN-LAST:event_jRadioButtonMenuItem1ActionPerformed

    private void jRadioButtonMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonMenuItem2ActionPerformed
        // TODO add your handling code here:
        principal.setBalaco(50);
    }//GEN-LAST:event_jRadioButtonMenuItem2ActionPerformed

    private void jRadioButtonMenuItem3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonMenuItem3ActionPerformed
        // TODO add your handling code here:
        principal.setBalaco(0);
    }//GEN-LAST:event_jRadioButtonMenuItem3ActionPerformed

    private void jRadioButtonMenuItem4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonMenuItem4ActionPerformed
        // TODO add your handling code here:
        principal.setBalaco(-50);
    }//GEN-LAST:event_jRadioButtonMenuItem4ActionPerformed

    private void jRadioButtonMenuItem5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonMenuItem5ActionPerformed
        // TODO add your handling code here:
        principal.setBalaco(-100);
    }//GEN-LAST:event_jRadioButtonMenuItem5ActionPerformed

    private void jCheckBox_libActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox_libActionPerformed
        // TODO add your handling code here:
        lib.setVisible(jCheckBox_lib.isSelected(), this.isAlwaysOnTop());
    }//GEN-LAST:event_jCheckBox_libActionPerformed

    private void jCheckBox_listActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox_listActionPerformed
        // TODO add your handling code here:
        playList.setVisible(jCheckBox_list.isSelected(), this.isAlwaysOnTop());
    }//GEN-LAST:event_jCheckBox_listActionPerformed

    private void jMenu_OcultActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenu_OcultActionPerformed
        // TODO add your handling code here:
        setVisible(false);
    }//GEN-LAST:event_jMenu_OcultActionPerformed

    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
        principal.someTray();
    }//GEN-LAST:event_jMenuItem2ActionPerformed

    private void jLabel_PlaylistMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel_PlaylistMouseClicked
              if (evt.getButton() == MouseEvent.BUTTON1) {
        playList.setVisible(true);
        jCheckBox_list.setSelected(true);
           }
}//GEN-LAST:event_jLabel_PlaylistMouseClicked

    private void jLabel_libMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel_libMouseClicked
               if (evt.getButton() == MouseEvent.BUTTON1) {

                lib.setVisible(true);
                jCheckBox_lib.setSelected(true);
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
                jLabel_top.setIcon(principal.topOn);
            } else {
                jLabel_top.setIcon(principal.topOff);
            }
    }//GEN-LAST:event_jCheckBox_topStateChanged

    private void jMenu3MenuSelected(javax.swing.event.MenuEvent evt) {//GEN-FIRST:event_jMenu3MenuSelected
        // TODO add your handling code here:
    }//GEN-LAST:event_jMenu3MenuSelected

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
    private javax.swing.JLabel jLabel_Playlist;
    private javax.swing.JLabel jLabel_lib;
    private javax.swing.JLabel jLabel_popup;
    private javax.swing.JLabel jLabel_top;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenu_Ocult;
    private javax.swing.JMenuItem jMenu_exit;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPopupMenu jPopupMenu1;
    private javax.swing.JRadioButtonMenuItem jRadioButtonMenuItem1;
    private javax.swing.JRadioButtonMenuItem jRadioButtonMenuItem2;
    private javax.swing.JRadioButtonMenuItem jRadioButtonMenuItem3;
    private javax.swing.JRadioButtonMenuItem jRadioButtonMenuItem4;
    private javax.swing.JRadioButtonMenuItem jRadioButtonMenuItem5;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JSlider jSlider_Tempo;
    private javax.swing.JSlider jSlider_vol;
    private javax.swing.JLabel jToggleButton1;
    private javax.swing.JLabel jToggle_Repete;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables
}