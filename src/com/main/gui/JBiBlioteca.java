package com.main.gui;

import com.biblioteca.BibliotecalRenderer;
import com.biblioteca.Capa;
import com.conexao.Transacao;
import com.main.Carregador;
import com.musica.*;
import com.utils.JTrocarImagem;
import com.utils.file.DiretorioUtils;
import com.utils.model.ModelReadOnly;
import com.utils.model.tablemodel.ObjectTransferable;
import com.utils.pele.ColorUtils;
import com.utils.transferivel.TipoTransferenciaMusica;
import java.awt.Dimension;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
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
public class JBiBlioteca extends javax.swing.JDialog implements ActionListener, Runnable {

    private final BibliotecalRenderer bibliotecalRenderer;
    /**
     * Creates new form JBiBlioteca
     */
    private JPrincipal principal;
    private JFileChooser jFileChooser = new JFileChooser();
    String genero = "";
    private final Carregador carregador;
    private DropTarget dropTargetBiblioteca;

    public JBiBlioteca(Carregador carregador) {
        bibliotecalRenderer = new BibliotecalRenderer();
        initComponents();
        this.carregador = carregador;
        inicializaDropTabela();
        startEvents();
    }

    public void setVisible(boolean b, boolean a) {
        super.setVisible(b);
        super.setAlwaysOnTop(a);
    }

    private void inicializaDropTabela() {
        jTable.setDragEnabled(true);
        jTable.setTransferHandler(new TransferHandler(null) {
            @Override
            public int getSourceActions(JComponent c) {
                return COPY_OR_MOVE;
            }

            @Override
            protected Transferable createTransferable(JComponent c) {
                int[] rows = jTable.getSelectedRows();
                int[] cols = jTable.getSelectedColumns();
                ArrayList<Musica> musicas = new ArrayList<Musica>(rows.length);
                for (int i = 0; i < rows.length; i++) {
                    if (jCheckBox_capa.isSelected()) {
                        for (int j = 0; j < cols.length; j++) {
                            int k = cols[j];

                            Capa capa = (Capa) jTable.getModel().getValueAt(rows[i], k);
                            MusicaSC filtro = new MusicaSC();
                            // if (jPanelFiltrar.isVisible()) {
                            filtro.setNome(capa.getTitulo());
                            filtro.setAutor(capa.getTitulo());
                            filtro.setAlbum(capa.getTitulo());
                            filtro.setGenero(capa.getTitulo());
                            initTabelaLista();
                            try {
                                musicas.addAll(MusicaBD.listar(filtro));
                            } catch (Exception ex) {
                                Logger.getLogger(JBiBlioteca.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    } else {
                        musicas.add((Musica) jTable.getModel().getValueAt(rows[i], jTable.getModel().getColumnCount() - 1));
                    }
                }
                return new ObjectTransferable(musicas, TipoTransferenciaMusica.JBIBLIOTECA);

            }
        });

        dropTargetBiblioteca = new DropTarget(jScrollPane, new DropTargetAdapter() {
            @Override
            public void drop(DropTargetDropEvent dtde) {
                try {
                    Transferable transferable = dtde.getTransferable();
                    DataFlavor[] flavors = transferable.getTransferDataFlavors();

                    //Windows
                    if (transferable.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                        ArrayList<File> arquivos = (ArrayList) transferable.getTransferData(java.awt.datatransfer.DataFlavor.javaFileListFlavor);
                        importarArquivos(arquivos);
                    } else {
                        // Linux
                        loop_flavor:
                        for (int i = 0; i < flavors.length; i++) {
                            if (flavors[i].isRepresentationClassReader()) {
                                dtde.acceptDrop(java.awt.dnd.DnDConstants.ACTION_COPY);

                                Reader reader = flavors[i].getReaderForText(transferable);

                                BufferedReader br = new BufferedReader(reader);
                                ArrayList<File> arquivos = new ArrayList<File>(1);
                                String linhaLida;
                                while ((linhaLida = br.readLine()) != null) {
                                    if (!linhaLida.isEmpty()) {
                                        try {
                                            arquivos.add(new File(new URI(linhaLida)));
                                        } catch (URISyntaxException ex) {
                                            Logger.getLogger(JBiBlioteca.class.getName()).log(Level.SEVERE, null, ex);
                                        }
                                    }
                                }
                                importarArquivos(arquivos);
                                break loop_flavor;
                            }
                        }

                    }
                } catch (UnsupportedFlavorException ex) {
                    Logger.getLogger(JBiBlioteca.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(JBiBlioteca.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            @Override
            public void dragEnter(DropTargetDragEvent evt) {
                if (TipoTransferenciaMusica.forDataFlavor(evt.getCurrentDataFlavors()) == TipoTransferenciaMusica.JBIBLIOTECA) {
                    evt.rejectDrag();
                } else {
                    evt.acceptDrag(DnDConstants.ACTION_COPY);
                }
            }
        });
        jTable.setDropTarget(dropTargetBiblioteca);
        jScrollPane.setDropTarget(dropTargetBiblioteca);
    }

    /**
     * Método que inicializa a tela.
     */
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
        jTable.setBackground(ColorUtils.getFundoTabelaNaoSelecionada());
        jTable.setForeground(ColorUtils.getFrenteTabelaNaoSelecionada());
        jTable.setSelectionBackground(ColorUtils.getFundoTabelaSelecionada());
        jTable.setSelectionForeground(ColorUtils.getFrenteTabelaSelecionada());


        jTable.setIntercellSpacing(new Dimension(1, 2));
        jTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        jTable.setShowHorizontalLines(true);
        jTable.setShowVerticalLines(true);
        jTable.setRowSelectionAllowed(true);
        jTable.setColumnSelectionAllowed(false);
        jTable.setRowHeight(20);
        jTable.getTableHeader().setVisible(true);

    }

    /**
     * Método que atualiza a consulta atual.
     */
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
            ex.printStackTrace(System.err);
        }
    }

    /**
     * Método que inicializa a tela.
     */
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
        jTable.setDefaultRenderer(Object.class, bibliotecalRenderer);
        jTable.getTableHeader().setVisible(false);
        jTable.setRowHeight(100);
        if (jTable.getRowCount() > 0) {
            jTable.changeSelection(0, 0, false, false);
        }


    }

    /**
     * Método que atualiza a consulta atual.
     */
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
                row[0] = (Capa) lista.get(i);
                if (i < lista.size() - 1) {
                    row[1] = (Capa) lista.get(i + 1);
                }

                ts.addRow(row);
            }
            jTable.requestFocus();
            if (jTable.getRowCount() > 0) {
                jTable.changeSelection(0, 0, false, false);
            }

        } catch (Exception ex) {
            ex.printStackTrace(System.err);
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
        new Thread(this).start();

    }

    @Override
    public void run() {
        Transacao t = new Transacao();
        jProgressBar.setVisible(true);
        try {
            t.begin();
            File pasta = telaAbrirArquivo();
            if (pasta != null) {
                importarArquivos(pasta, t);


            }
            t.commit();
            atualizarTabelaCapa();
        } catch (Exception ex) {
            t.rollback();
            ex.printStackTrace(System.err);
        } finally {
            jProgressBar.setVisible(false);
        }
    }

    private void importarArquivos(ArrayList<File> pasta) {
        Transacao t = new Transacao();
        try {
            t.begin();
            for (int i = 0; i < pasta.size(); i++) {
                importarArquivos(pasta.get(i), t);
            }
            t.commit();
        } catch (Exception ex) {
            t.rollback();
            Logger.getLogger(JBiBlioteca.class.getName()).log(Level.WARNING, "Erro ao importar arquivos arrastados: ", ex);
        }
    }

    private void importarArquivos(File pasta, Transacao t) throws Exception {
        long date = new Date().getTime();
        int total = DiretorioUtils.calculaQuantidadeArquivos(pasta);
        MusicaGerencia.count = 0;
        MusicaGerencia.mapearDiretorio(pasta, t, jProgressBar, total);
        System.out.println("Tempo decorrido: " + (new Date().getTime() - date));
    }

    public void resetText() {
        jTextField_Album.setText("");
        jTextField_Album.requestFocus();

        genero = "";
    }

    private void startEvents() {
        jButton_PK4.addActionListener(this);
        jButton2.addActionListener(this);
        jComboBox_selecao.addActionListener(this);
        jComboBox_Agrupar.addActionListener(this);
        jCheckBox_capa.addActionListener(this);
        jButton1.addActionListener(this);
        jMenuItem1.addActionListener(this);
        jMenuItem2.addActionListener(this);
        jMenuItem3.addActionListener(this);
        jMenuItem4.addActionListener(this);
        jMenuItem5.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == jButton_PK4) {
            atualizarTabelas();
        } else if (e.getSource() == jButton2) {
            jPanelFiltrar.setVisible(false);
            atualizarTabelas();
        } else if (e.getSource() == jComboBox_selecao) {
            atualizarTabelas();
        } else if (e.getSource() == jComboBox_Agrupar) {
            atualizarTabelas();
        } else if (e.getSource() == jCheckBox_capa) {
            atualizarTabelas();
        } else if (e.getSource() == jButton1) {
            importarArquivos();
        } else if (e.getSource() == jMenuItem1) {
            if (jPanelFiltrar.isVisible()) {
                atualizarTabelas();
            } else {
                resetText();
                jPanelFiltrar.setVisible(true);
            }
            jTextField_Album.requestFocus();
        } else if (e.getSource() == jMenuItem2) {
            resetText();
        } else if (e.getSource() == jMenuItem3) {
            if (jTable.getSelectedRow() != -1) {
                carregador.addToPlayList(
                        (Musica) jTable.getModel().getValueAt(jTable.getSelectedRow(), jTable.getColumnCount()));
            }
        } else if (e.getSource() == jMenuItem4) {
            for (int i = 0; i < jTable.getRowCount(); i++) {
                Musica m = (Musica) jTable.getModel().getValueAt(i, jTable.getColumnCount());
                carregador.addToPlayList(m);
            }
        } else if (e.getSource() == jMenuItem5) {
            if (jTable.getSelectedRow() > -1 && jTable.getSelectedColumn() > -1) {
                if (jCheckBox_capa.isSelected()) {
                    Capa capa = (Capa) jTable.getValueAt(jTable.getSelectedRow(), jTable.getSelectedColumn());
                    jTextField_Album.setText(capa.getTitulo());
                    atualizarTabelaLista();
                }
                Musica m = (Musica) jTable.getModel().getValueAt(jTable.getSelectedRow(), jTable.getColumnCount());
                new JTrocarImagem(principal, true, m).setVisible(true);
                jCheckBox_capa.setSelected(true);
                atualizarTabelaCapa();
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

        jPanelFiltrar.setPreferredSize(new java.awt.Dimension(400, 40));
        jPanelFiltrar.setLayout(new javax.swing.BoxLayout(jPanelFiltrar, javax.swing.BoxLayout.Y_AXIS));

        jPanel_Nome4.setPreferredSize(new java.awt.Dimension(376, 35));
        jPanel_Nome4.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 1));

        jLabel_Nome4.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N
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
        jPanel_Nome4.add(jButton_PK4);

        jButton2.setText("Cancelar");
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
        jPanel4.add(jComboBox_selecao);

        jPanel2.add(jPanel4);

        jPanel6.setMinimumSize(new java.awt.Dimension(80, 28));
        jPanel6.setPreferredSize(new java.awt.Dimension(120, 60));

        jLabel2.setText("Agrupar capas");
        jPanel6.add(jLabel2);

        jComboBox_Agrupar.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Autor", "Album", "Genero" }));
        jPanel6.add(jComboBox_Agrupar);

        jPanel2.add(jPanel6);

        jPanel10.setMinimumSize(new java.awt.Dimension(80, 28));
        jPanel10.setPreferredSize(new java.awt.Dimension(120, 60));

        jLabel4.setText("Modo Capa");
        jPanel10.add(jLabel4);

        jCheckBox_capa.setSelected(true);
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
        jMenu1.add(jMenuItem1);

        jMenuItem2.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F4, 0));
        jMenuItem2.setText("Limpar");
        jMenu1.add(jMenuItem2);

        jMenuItem3.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_P, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem3.setText("Add Playlist");
        jMenu1.add(jMenuItem3);

        jMenuItem4.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_T, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem4.setText("Add Todas Playlist");
        jMenu1.add(jMenuItem4);

        jMenuItem5.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F2, 0));
        jMenuItem5.setText("Trocar Capa");
        jMenu1.add(jMenuItem5);

        jMenuBar1.add(jMenu1);

        setJMenuBar(jMenuBar1);

        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        setBounds((screenSize.width-495)/2, (screenSize.height-419)/2, 495, 419);
    }// </editor-fold>//GEN-END:initComponents

    private void jTableKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTableKeyPressed
        if (jTable.getSelectedRow() > -1) {
            if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
                evt.consume();
                if (jCheckBox_capa.isSelected()) {
                    Capa capa = (Capa) jTable.getModel().getValueAt(jTable.getSelectedRow(), jTable.getSelectedColumn());
                    if (jComboBox_Agrupar.getSelectedItem().equals("Autor")) {
                        jTextField_Album.setText(capa.getTitulo());
                    }
                    if (jComboBox_Agrupar.getSelectedItem().equals("Album")) {
                        jTextField_Album.setText(capa.getTitulo());
                    }
                    if (jComboBox_Agrupar.getSelectedItem().equals("Genero")) {
                        genero = capa.getTitulo();
                    }
                    jCheckBox_capa.setSelected(false);
                    atualizarTabelaLista();
                } else {
                    if (jComboBox_selecao.getSelectedItem().equals("Executar")) {
                        try {
                            Musica m = (Musica) jTable.getModel().getValueAt(jTable.getSelectedRow(), 4);
                            carregador.abrir(m, 0, false);
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

            if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_P) {
                Musica m = (Musica) jTable.getModel().getValueAt(jTable.getSelectedRow(), 4);
                carregador.addToPlayList(m);

            }
        }
    }//GEN-LAST:event_jTableKeyPressed

    private void jTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableMouseClicked
        if (evt.getButton() == MouseEvent.BUTTON1 && evt.getClickCount() == 2) {
            if (jCheckBox_capa.isSelected()) {
                Capa capa = (Capa) jTable.getModel().getValueAt(jTable.getSelectedRow(), jTable.getSelectedColumn());
                if (jComboBox_Agrupar.getSelectedItem().equals("Autor")) {
                    jTextField_Album.setText(capa.getTitulo());
                }
                if (jComboBox_Agrupar.getSelectedItem().equals("Album")) {
                    jTextField_Album.setText(capa.getTitulo());
                }
                if (jComboBox_Agrupar.getSelectedItem().equals("Genero")) {
                    genero = capa.getTitulo();
                }

                jCheckBox_capa.setSelected(false);
                atualizarTabelaLista();
            } else {
                if (jComboBox_selecao.getSelectedItem().equals("Executar")) {
                    try {
                        Musica m = (Musica) jTable.getModel().getValueAt(jTable.getSelectedRow(), jTable.getColumnCount());
                        carregador.abrir(m, 0, false);
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
    }//GEN-LAST:event_jTableMouseClicked

    private void jTextField_AlbumKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField_AlbumKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            atualizarTabelas();
        }
    }//GEN-LAST:event_jTextField_AlbumKeyPressed

    private void jTextField_AlbumFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField_AlbumFocusGained
        jTextField_Album.selectAll();
    }//GEN-LAST:event_jTextField_AlbumFocusGained
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

    private void atualizarTabelas() {
        if (jCheckBox_capa.isSelected()) {
            atualizarTabelaCapa();
        } else {
            atualizarTabelaLista();
        }
    }
}
