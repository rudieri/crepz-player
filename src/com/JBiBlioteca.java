package com;

import com.conexao.Transacao;
import com.main.Carregador;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import com.musica.JCapa;
import com.musica.BibliotecalRenderer;
import com.musica.ModelReadOnly;
import com.musica.Musica;
import com.musica.MusicaBD;
import com.musica.MusicaGerencia;
import com.musica.MusicaSC;
import com.utils.DiretorioUtils;
import com.utils.JTrocarImagem;
import java.awt.event.KeyEvent;
import java.util.Date;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * JBiBlioteca.java
 *
 * Created on 03/06/2010, 12:13:13
 */
/**
 *
 * @author manchini
 */
public class JBiBlioteca extends javax.swing.JDialog {

    /** Creates new form JBiBlioteca */
    private JPrincipal principal;
    private JFileChooser jFileChooser = new JFileChooser();
    private JMini mini;
    String genero = "";
    private Musiquera musiquera;
    private final Carregador carregador;
    

    public JBiBlioteca(Musiquera mus, Carregador carregador) {
        initComponents();
        musiquera= mus;
        this.carregador = carregador;
    }

    public void setVisible(boolean b, boolean a) {
        super.setVisible(b);
        super.setAlwaysOnTop(a);
    }

    /** Método que inicializa a tela. */
    private void initTabelaLista() {

        // Definindo as colunas...
        ModelReadOnly tm = new ModelReadOnly();
        tm.addColumn("Genero");
        tm.addColumn("Nome");
        tm.addColumn("Autor");
        tm.addColumn("Album");
        tm.addColumn("Obj");

        jTable.setModel(tm);

        // Definindo a largura das colunas...
        jTable.getColumn("Genero").setPreferredWidth(50);
        jTable.getColumn("Nome").setPreferredWidth(200);
        jTable.getColumn("Autor").setPreferredWidth(100);
        jTable.getColumn("Album").setPreferredWidth(100);

        // Removendo a coluna do objeto da view...
        jTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer());
        jTable.removeColumn(jTable.getColumn("Obj"));

        jTable.setAutoCreateRowSorter(true);
        jTable.setShowVerticalLines(true);
        jTable.setEditingColumn(-1);
        jTable.setEditingRow(-1);


        jTable.setIntercellSpacing(new Dimension(1, 2));
        jTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        jTable.setShowHorizontalLines(true);
        jTable.setShowVerticalLines(true);
        jTable.setRowSelectionAllowed(true);
        jTable.setColumnSelectionAllowed(false);
        jTable.setRowHeight(20);

    }

    /** Método que atualiza a consulta atual. */
    public void atualizarTabelaLista() {

        try {
            jComboBox_Agrupar.setEnabled(false);
            // Filtro...
            MusicaSC filtro = new MusicaSC();
            // if (jPanelFiltrar.isVisible()) {
            filtro.setNome(jTextField_Album.getText());
            filtro.setAutor(jTextField_Album.getText());
            filtro.setAlbum(jTextField_Album.getText());
            filtro.setGenero(genero);
//            } else {
//                filtro.setNome("");
//                filtro.setAutor("");
//                filtro.setAlbum("");
//            }
            initTabelaLista();
            DefaultTableModel ts = (DefaultTableModel) jTable.getModel();
            ArrayList lista = MusicaBD.listar(filtro);
            for (int i = 0; i < lista.size(); i++) {
                Musica m = (Musica) lista.get(i);
                Object[] row = new Object[5];
                row[0] = m.getGenero();
                row[1] = m.getNome();
                row[2] = m.getAutor();
                row[3] = m.getAlbum();
                row[4] = m;
                ts.addRow(row);
            }
            jTable.requestFocus();
            if (jTable.getRowCount() > 0) {
                jTable.changeSelection(0, 0, false, false);
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao Filtrar");
            ex.printStackTrace();
        }
    }

    /** Método que inicializa a tela. */
    private void initTabelaCapa() {

        // Definindo as colunas...
        ModelReadOnly tm = new ModelReadOnly();
        tm.addColumn("");
        tm.addColumn("");


        jTable.setModel(tm);

        // Definindo a largura das colunas...
        jTable.getColumn("").setPreferredWidth(100);
        jTable.getColumn("").setPreferredWidth(100);



        jTable.setRowSelectionAllowed(true);
        jTable.setColumnSelectionAllowed(true);
        jTable.setShowHorizontalLines(false);
        jTable.setShowVerticalLines(false);
        jTable.setIntercellSpacing(new Dimension(2, 2));
        jTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        jTable.setDefaultRenderer(Object.class, new BibliotecalRenderer());
        jTable.setTableHeader(null);
        jTable.setRowHeight(100);
        jTable.changeSelection(0, 0, false, false);


    }

    /** Método que atualiza a consulta atual. */
    public void atualizarTabelaCapa() {

        try {
            jComboBox_Agrupar.setEnabled(true);
            // Filtro...
            MusicaSC filtro = new MusicaSC();
            if (jPanelFiltrar.isVisible()) {
                genero = "";
                filtro.setNome(jTextField_Album.getText());
                filtro.setAutor(jTextField_Album.getText());
                filtro.setAlbum(jTextField_Album.getText());
            } else {
                resetText();
            }
            initTabelaCapa();
            DefaultTableModel ts = (DefaultTableModel) jTable.getModel();
            ArrayList lista = MusicaBD.listarAgrupado(filtro, jComboBox_Agrupar.getSelectedItem().toString());
            for (int i = 0; i < lista.size(); i += 2) {
                Object[] row = new Object[3];
                row[0] = (JCapa) lista.get(i);
                if (i < lista.size() - 1) {
                    row[1] = (JCapa) lista.get(i + 1);
                }

                ts.addRow(row);
            }
            jTable.requestFocus();
            if (jTable.getRowCount() > 0) {
                jTable.changeSelection(0, 0, false, false);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao Filtrar");
        }
    }

    private File telaAbrirArquivo() throws Exception {

        // restringe a amostra a diretorios apenas
        jFileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        jFileChooser.setDialogTitle("Abrir Pasta");

        int res = jFileChooser.showOpenDialog(null);

        if (res == JFileChooser.APPROVE_OPTION) {
            return jFileChooser.getSelectedFile();
        }
        return null;
//        else {
//            throw new Exception("Voce nao selecionou nenhum diretorio.");
//        }
    }

    private void importarArquivos() {
        new Thread(new Runnable() {

            public void run() {
                Transacao t = new Transacao();
                jProgressBar.setVisible(true);
                try {
                    t.begin();
                    File pasta = telaAbrirArquivo();
                    if (pasta != null) {
                        Long date = new Date().getTime();
                        int total = DiretorioUtils.calculaQuantidadeArquivos(pasta);
                        MusicaGerencia.count = 0;
                        MusicaGerencia.mapearDiretorio(pasta, t, jProgressBar, total);
                        System.out.println("Tempo decorrido: " + (new Date().getTime() - date));


                    }
                    t.commit();
                    atualizarTabelaCapa();
                } catch (Exception ex) {
                    t.rollback();
                    ex.printStackTrace();
                } finally {
                    jProgressBar.setVisible(false);
                }
            }
        }).start();

    }

    public void resetText() {
        jTextField_Album.setText("");
        jTextField_Album.requestFocus();

        genero = "";
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanelFiltrar = new javax.swing.JPanel();
        jPanel_Nome4 = new javax.swing.JPanel();
        jLabel_Nome4 = new javax.swing.JLabel();
        jTextField_Album = new javax.swing.JTextField();
        jButton_PK4 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jComboBox_selecao = new javax.swing.JComboBox();
        jPanel6 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jComboBox_Agrupar = new javax.swing.JComboBox();
        jPanel10 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jCheckBox_capa = new javax.swing.JCheckBox();
        jPanel5 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane = new javax.swing.JScrollPane();
        jTable = new javax.swing.JTable();
        jPanel7 = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jPanel9 = new javax.swing.JPanel();
        jProgressBar = new javax.swing.JProgressBar();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenuItem3 = new javax.swing.JMenuItem();
        jMenuItem4 = new javax.swing.JMenuItem();
        jMenuItem5 = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Crepz Player");
        setMinimumSize(new java.awt.Dimension(500, 212));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jPanelFiltrar.setPreferredSize(new java.awt.Dimension(400, 40));
        jPanelFiltrar.setLayout(new javax.swing.BoxLayout(jPanelFiltrar, javax.swing.BoxLayout.Y_AXIS));

        jPanel_Nome4.setPreferredSize(new java.awt.Dimension(376, 35));
        jPanel_Nome4.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 1));

        jLabel_Nome4.setFont(new java.awt.Font("Dialog", 0, 11));
        jLabel_Nome4.setText("Filtro:");
        jLabel_Nome4.setMaximumSize(new java.awt.Dimension(60, 16));
        jLabel_Nome4.setMinimumSize(new java.awt.Dimension(60, 16));
        jLabel_Nome4.setPreferredSize(new java.awt.Dimension(70, 16));
        jPanel_Nome4.add(jLabel_Nome4);

        jTextField_Album.setMaximumSize(new java.awt.Dimension(300, 20));
        jTextField_Album.setMinimumSize(new java.awt.Dimension(300, 20));
        jTextField_Album.setPreferredSize(new java.awt.Dimension(276, 25));
        jTextField_Album.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTextField_AlbumFocusGained(evt);
            }
        });
        jTextField_Album.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextField_AlbumKeyPressed(evt);
            }
        });
        jPanel_Nome4.add(jTextField_Album);

        jButton_PK4.setText("OK");
        jButton_PK4.setToolTipText("Carrega a listagem");
        jButton_PK4.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        jButton_PK4.setMaximumSize(new java.awt.Dimension(22, 20));
        jButton_PK4.setMinimumSize(new java.awt.Dimension(22, 20));
        jButton_PK4.setPreferredSize(new java.awt.Dimension(30, 30));
        jButton_PK4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_PK4ActionPerformed(evt);
            }
        });
        jButton_PK4.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jButton_PK4FocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jButton_PK4FocusLost(evt);
            }
        });
        jPanel_Nome4.add(jButton_PK4);

        jButton2.setText("Cancelar");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        jPanel_Nome4.add(jButton2);

        jPanelFiltrar.add(jPanel_Nome4);

        getContentPane().add(jPanelFiltrar, java.awt.BorderLayout.PAGE_START);

        jPanel2.setPreferredSize(new java.awt.Dimension(80, 180));
        jPanel2.setLayout(new javax.swing.BoxLayout(jPanel2, javax.swing.BoxLayout.Y_AXIS));

        jPanel4.setMinimumSize(new java.awt.Dimension(80, 28));
        jPanel4.setPreferredSize(new java.awt.Dimension(0, 60));

        jLabel1.setText("Seleção");
        jPanel4.add(jLabel1);

        jComboBox_selecao.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Executar", "Playlist" }));
        jComboBox_selecao.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox_selecaoActionPerformed(evt);
            }
        });
        jPanel4.add(jComboBox_selecao);

        jPanel2.add(jPanel4);

        jPanel6.setMinimumSize(new java.awt.Dimension(80, 28));
        jPanel6.setPreferredSize(new java.awt.Dimension(120, 60));

        jLabel2.setText("Agrupar capas");
        jPanel6.add(jLabel2);

        jComboBox_Agrupar.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Autor", "Album", "Genero" }));
        jComboBox_Agrupar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox_AgruparActionPerformed(evt);
            }
        });
        jPanel6.add(jComboBox_Agrupar);

        jPanel2.add(jPanel6);

        jPanel10.setMinimumSize(new java.awt.Dimension(80, 28));
        jPanel10.setPreferredSize(new java.awt.Dimension(120, 60));

        jLabel4.setText("Modo Capa");
        jPanel10.add(jLabel4);

        jCheckBox_capa.setSelected(true);
        jCheckBox_capa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox_capaActionPerformed(evt);
            }
        });
        jPanel10.add(jCheckBox_capa);

        jPanel2.add(jPanel10);

        jPanel5.setLayout(new java.awt.BorderLayout());
        jPanel2.add(jPanel5);

        getContentPane().add(jPanel2, java.awt.BorderLayout.WEST);

        jPanel3.setPreferredSize(new java.awt.Dimension(450, 402));
        jPanel3.setLayout(new java.awt.BorderLayout());

        jScrollPane.setAutoscrolls(true);
        jScrollPane.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jScrollPane.setRequestFocusEnabled(false);
        jScrollPane.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jScrollPaneFocusGained(evt);
            }
        });

        jTable.setAutoCreateRowSorter(true);
        jTable.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jTable.setFocusCycleRoot(true);
        jTable.setFocusTraversalPolicyProvider(true);
        jTable.setRowSelectionAllowed(false);
        jTable.setSurrendersFocusOnKeystroke(true);
        jTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTableMouseClicked(evt);
            }
        });
        jTable.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTableKeyPressed(evt);
            }
        });
        jScrollPane.setViewportView(jTable);

        jPanel3.add(jScrollPane, java.awt.BorderLayout.CENTER);

        getContentPane().add(jPanel3, java.awt.BorderLayout.CENTER);

        jPanel7.setLayout(new javax.swing.BoxLayout(jPanel7, javax.swing.BoxLayout.Y_AXIS));

        jLabel3.setText("Importar Arquivos:");
        jPanel8.add(jLabel3);

        jTextField1.setEditable(false);
        jTextField1.setEnabled(false);
        jTextField1.setPreferredSize(new java.awt.Dimension(250, 30));
        jPanel8.add(jTextField1);

        jButton1.setText("ADD");
        jButton1.setPreferredSize(new java.awt.Dimension(60, 30));
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jPanel8.add(jButton1);

        jPanel7.add(jPanel8);

        jProgressBar.setPreferredSize(new java.awt.Dimension(300, 20));
        jProgressBar.setStringPainted(true);
        jPanel9.add(jProgressBar);

        jPanel7.add(jPanel9);

        getContentPane().add(jPanel7, java.awt.BorderLayout.PAGE_END);

        jMenu1.setText("Funções");

        jMenuItem1.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F5, 0));
        jMenuItem1.setText("Pesquisar");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem1);

        jMenuItem2.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F4, 0));
        jMenuItem2.setText("Limpar");
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem2);

        jMenuItem3.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_P, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem3.setText("Add Playlist");
        jMenuItem3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem3ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem3);

        jMenuItem4.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_T, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem4.setText("Add Todas Playlist");
        jMenuItem4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem4ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem4);

        jMenuItem5.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F2, 0));
        jMenuItem5.setText("Trocar Capa");
        jMenuItem5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem5ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem5);

        jMenuBar1.add(jMenu1);

        setJMenuBar(jMenuBar1);

        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        setBounds((screenSize.width-495)/2, (screenSize.height-419)/2, 495, 419);
    }// </editor-fold>//GEN-END:initComponents

    private void jScrollPaneFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jScrollPaneFocusGained
        jTable.requestFocus();
        jTable.changeSelection(0, 0, false, false);
}//GEN-LAST:event_jScrollPaneFocusGained

    private void jTableKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTableKeyPressed
        if (jTable.getSelectedRow() > -1) {
            if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
                evt.setKeyCode(java.awt.event.KeyEvent.VK_UNDEFINED);
                if (jCheckBox_capa.isSelected()) {
                    JCapa j = (JCapa) jTable.getModel().getValueAt(jTable.getSelectedRow(), jTable.getSelectedColumn());
                    if (jComboBox_Agrupar.getSelectedItem().equals("Autor")) {
                        jTextField_Album.setText(j.getTXT());
                    }
                    if (jComboBox_Agrupar.getSelectedItem().equals("Album")) {
                        jTextField_Album.setText(j.getTXT());
                    }
                    if (jComboBox_Agrupar.getSelectedItem().equals("Genero")) {
                        genero = j.getTXT();
                    }
                    jCheckBox_capa.setSelected(false);
                    atualizarTabelaLista();



                } else {
                    if (principal != null) {
                        if (jComboBox_selecao.getSelectedItem().equals("Executar")) {
                            try {
                                Musica m = (Musica) jTable.getModel().getValueAt(jTable.getSelectedRow(), 4);
                                principal.getMusiquera().abrir(m, 0, false);
                                principal.getMusiquera().tocarPausar();
                                jTable.changeSelection(jTable.getSelectedRow(), jTable.getSelectedColumn(), false, false);
                            } catch (Exception ex) {
                                Logger.getLogger(JBiBlioteca.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        } else {
                            Musica m = (Musica) jTable.getModel().getValueAt(jTable.getSelectedRow(), 4);
                            carregador.addToPlayList(m);
                        }
                    }
                }
            }

            if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_P) {
                Musica m = (Musica) jTable.getModel().getValueAt(jTable.getSelectedRow(), 4);
                carregador.addToPlayList(m);

            }
        }
    }//GEN-LAST:event_jTableKeyPressed

    private void jTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableMouseClicked
        if (evt.getButton() == MouseEvent.BUTTON1 && evt.getClickCount() == 2) {
            if (jCheckBox_capa.isSelected()) {
                JCapa j = (JCapa) jTable.getModel().getValueAt(jTable.getSelectedRow(), jTable.getSelectedColumn());
                if (jComboBox_Agrupar.getSelectedItem().equals("Autor")) {
                    jTextField_Album.setText(j.getTXT());
                }
                if (jComboBox_Agrupar.getSelectedItem().equals("Album")) {
                    jTextField_Album.setText(j.getTXT());
                }
                if (jComboBox_Agrupar.getSelectedItem().equals("Genero")) {
                    genero = j.getTXT();
                }

                jCheckBox_capa.setSelected(false);
                atualizarTabelaLista();
            } else {
                if (evt.getClickCount() == 2) {
                    if (principal != null) {
                        if (jComboBox_selecao.getSelectedItem().equals("Executar")) {
                            try {
                                Musica m = (Musica) jTable.getModel().getValueAt(jTable.getSelectedRow(), jTable.getColumnCount());
                                principal.getMusiquera().abrir(m, 0, false);
                                jTable.changeSelection(jTable.getSelectedRow(), jTable.getSelectedColumn(), false, false);
                            } catch (Exception ex) {
                                Logger.getLogger(JBiBlioteca.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        } else {
                            Musica m = (Musica) jTable.getModel().getValueAt(jTable.getSelectedRow(), jTable.getColumnCount());
                           carregador.addToPlayList(m);
                        }
                    }
                }
            }
        }
    }//GEN-LAST:event_jTableMouseClicked

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        importarArquivos();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
        resetText();
    }//GEN-LAST:event_jMenuItem2ActionPerformed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        // jButton_PK4ActionPerformed(evt);
        if (jPanelFiltrar.isVisible()) {
            jButton_PK4ActionPerformed(evt);
        } else {
            resetText();
            jPanelFiltrar.setVisible(true);
//            if (jCheckBox_capa.isSelected()) {
//                atualizarTabelaCapa();
//            } else {
//                atualizarTabelaLista();
//            }
        }
        jTextField_Album.requestFocus();
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void jMenuItem3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jMenuItem3ActionPerformed

    private void jMenuItem4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem4ActionPerformed
        for (int i = 0; i < jTable.getRowCount(); i++) {
            Musica m = (Musica) jTable.getModel().getValueAt(i, jTable.getColumnCount());
            carregador.addToPlayList(m);
        }
    }//GEN-LAST:event_jMenuItem4ActionPerformed

    private void jCheckBox_capaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox_capaActionPerformed
        // TODO add your handling code here:
        if (jCheckBox_capa.isSelected()) {
            atualizarTabelaCapa();
        } else {
            atualizarTabelaLista();
        }
    }//GEN-LAST:event_jCheckBox_capaActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
        jPanelFiltrar.setVisible(false);
        if (jCheckBox_capa.isSelected()) {
            atualizarTabelaCapa();
        } else {
            atualizarTabelaLista();
        }
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jComboBox_AgruparActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox_AgruparActionPerformed
        // TODO add your handling code here:
        if (jCheckBox_capa.isSelected()) {
            atualizarTabelaCapa();
        } else {
            atualizarTabelaLista();
        }
    }//GEN-LAST:event_jComboBox_AgruparActionPerformed

    private void jComboBox_selecaoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox_selecaoActionPerformed
        // TODO add your handling code here:
        if (jCheckBox_capa.isSelected()) {
            atualizarTabelaCapa();
        } else {
            atualizarTabelaLista();
        }
    }//GEN-LAST:event_jComboBox_selecaoActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        // TODO add your handling code here:
    }//GEN-LAST:event_formWindowClosing

    private void jTextField_AlbumKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField_AlbumKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            jButton_PK4ActionPerformed(null);
        }
    }//GEN-LAST:event_jTextField_AlbumKeyPressed

    private void jButton_PK4FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jButton_PK4FocusLost
        // TODO add your handling code here:
}//GEN-LAST:event_jButton_PK4FocusLost

    private void jButton_PK4FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jButton_PK4FocusGained
        // TODO add your handling code here:
}//GEN-LAST:event_jButton_PK4FocusGained

    private void jButton_PK4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_PK4ActionPerformed
        if (jCheckBox_capa.isSelected()) {
            atualizarTabelaCapa();
        } else {
            atualizarTabelaLista();
        }
}//GEN-LAST:event_jButton_PK4ActionPerformed

    private void jTextField_AlbumFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField_AlbumFocusGained
        jTextField_Album.selectAll();
    }//GEN-LAST:event_jTextField_AlbumFocusGained

    private void jMenuItem5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem5ActionPerformed
        if (jTable.getSelectedRow() > -1 && jTable.getSelectedColumn() > -1) {
            if (jCheckBox_capa.isSelected()) {
                JCapa capa = (JCapa) jTable.getValueAt(jTable.getSelectedRow(), jTable.getSelectedColumn());
                jTextField_Album.setText(capa.getTXT());
                atualizarTabelaLista();
            }
            Musica m = (Musica) jTable.getModel().getValueAt(jTable.getSelectedRow(), jTable.getColumnCount());
            new JTrocarImagem(principal, true, m).setVisible(true);
            jCheckBox_capa.setSelected(true);
            atualizarTabelaCapa();
        }
    }//GEN-LAST:event_jMenuItem5ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton_PK4;
    private javax.swing.JCheckBox jCheckBox_capa;
    private javax.swing.JComboBox jComboBox_Agrupar;
    private javax.swing.JComboBox jComboBox_selecao;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel_Nome4;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JMenuItem jMenuItem5;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JPanel jPanelFiltrar;
    private javax.swing.JPanel jPanel_Nome4;
    private javax.swing.JProgressBar jProgressBar;
    private javax.swing.JScrollPane jScrollPane;
    private javax.swing.JTable jTable;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField_Album;
    // End of variables declaration//GEN-END:variables
}
