package com;

import com.conexao.Transacao;
import java.awt.Dimension;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import com.musica.ModelReadOnly;
import com.musica.Musica;
import com.musica.MusicaBD;
import com.musica.MusicaSC;
import com.playlist.JLista;
import com.playlist.JPlaylists;
import com.playlist.PlayListRenderer;
import com.playlist.Playlist;
import com.playlist.PlaylistBD;
import com.playmusica.PlayMusica;
import com.playmusica.PlayMusicaBD;
import com.playmusica.PlayMusicaSC;
import com.utils.FileUtils;
import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import javax.swing.JFileChooser;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileFilter;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * JPlayList.java
 *
 * Created on 04/06/2010, 19:19:01
 */
/**
 *
 * @author manchini
 */
public class JPlayList extends javax.swing.JDialog {

    /** Creates new form JPlayList */
    private JPrincipal principal;
    boolean aleatorio;
    boolean recomecar = false;
    ArrayList faltamTocar = new ArrayList();
    ArrayList jahFoi = new ArrayList();
    ArrayList total = new ArrayList();
    ArrayList<Musica> pesquisa = new ArrayList<Musica>();
    int IdAberto = -1;

    public JPlayList(java.awt.Frame parent, boolean modal, JPrincipal principal) {
        super(principal, modal);
        initComponents();
        this.principal = principal;
        // setResizable(false);
        jPanel1.setVisible(jToggleButton1.isSelected());
        jLabel2.setText("");
        jLabel2.setToolTipText("Salvar internamente");
        jLabel3.setText("");
        jLabel3.setToolTipText("Salvar como Arquivo");
        jTextEntrada.setVisible(true);
        jTextEntrada.setText("");
//        pack();
        initTabelaLista(false);
        atualizarTabelaLista();


    }

    public void posicionar() {
        this.setLocation(principal.getX() - this.getWidth() - 5, principal.getY());
    }

    public void setAleatorio(boolean v) {
        aleatorio = v;
        jahFoi.clear();
        faltamTocar.clear();

        for (Object m : total) {
            faltamTocar.add(m);
        }
    }

    public void setRepetir(boolean v) {
        recomecar = v;
    }

    public int getId() {
        return IdAberto;
    }

    /** Método que inicializa a tela. */
    private void initTabelaLista(boolean noChange) {

        // Definindo as colunas...
        ModelReadOnly tm = new ModelReadOnly();
        tm.addColumn("");
        //   tm.addColumn("Autor");
        tm.addColumn("Obj");
        try {
            jTable.setModel(tm);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        jTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer());
        jTable.removeColumn(jTable.getColumn("Obj"));

        jTable.setAutoCreateRowSorter(true);
        jTable.setShowVerticalLines(true);
        jTable.setEditingColumn(-1);
        jTable.setEditingRow(-1);

        jTable.setDefaultRenderer(Object.class, new PlayListRenderer());
        jTable.setBackground(Color.DARK_GRAY);
        jTable.setIntercellSpacing(new Dimension(1, 2));
        jTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        jTable.setShowHorizontalLines(false);
        jTable.setShowVerticalLines(true);
        jTable.setRowSelectionAllowed(true);
        jTable.setColumnSelectionAllowed(false);
        jTable.setRowHeight(40);

        //Limpar a array list da lista "aleatorio"
        if (!noChange) {
            faltamTocar.clear();
            jahFoi.clear();
            total.clear();
        }

    }

    /** Método que atualiza a consulta atual. */
    public void atualizarTabelaLista() {
        pesquisa.clear();
        if (jTextField_NomePlayList.getText().equals("")) {
            return;
        }

        Transacao t = new Transacao();
        try {
            t.begin();
            initTabelaLista(false);
            DefaultTableModel ts = (DefaultTableModel) jTable.getModel();
            // Filtro...
            PlayMusicaSC filtro = new PlayMusicaSC();
            Playlist p = new Playlist();
            p.setNome(jTextField_NomePlayList.getText());
            PlaylistBD.existe(p, t);
            filtro.setPlaylist(p);

            ArrayList lista = PlayMusicaBD.listar(filtro, t);
            for (int i = 0; i < lista.size(); i++) {
                PlayMusica m = (PlayMusica) lista.get(i);
                if (m.getMusica().getCaminho() == null) {
                    MusicaBD.carregar(m.getMusica(), t);
                }
                Object[] row = new Object[2];
                row[0] = new JLista(m.getMusica().getNome(), m.getMusica().getAutor());
                row[1] = m.getMusica();
                ts.addRow(row);
                faltamTocar.add(m.getMusica());
                total.add(m.getMusica());

                pesquisa.add(m.getMusica());
            }
            jTable.requestFocus();
            if (jTable.getRowCount() > 0) {
                jTable.changeSelection(0, 0, false, false);
            }
            t.commit();
            ListSelectionModel modeloDeSelecao = jTable.getSelectionModel();
            modeloDeSelecao.addListSelectionListener(new ListSelectionListener() {

                public void valueChanged(ListSelectionEvent e) {
                    jTable.scrollRectToVisible(jTable.getCellRect(jTable.getSelectedRow(), 0, false));
                }
            });
            setTitle(jTextField_NomePlayList.getText());

        } catch (Exception ex) {
            t.rollback();
            System.out.println("FUUUUUUUUUUUUUUUUUUUUUUUUUUUu");
            // JOptionPane.showMessageDialog(this, "Erro ao Filtrar \\õ/");
            ex.printStackTrace();
        }
    }

    public void atualizarTabelaLista(ArrayList novaLista) {
        try {

            initTabelaLista(true);
            DefaultTableModel ts = (DefaultTableModel) jTable.getModel();

            for (int i = 0; i < novaLista.size(); i++) {
                Musica m = (Musica) novaLista.get(i);

                Object[] row = new Object[2];
                row[0] = new JLista(m.getNome(), m.getAutor());
                row[1] = m;
                ts.addRow(row);
            }
            jTable.requestFocus();
            if (jTable.getRowCount() > 0) {
                jTable.changeSelection(0, 0, false, false);
            }

            ListSelectionModel modeloDeSelecao = jTable.getSelectionModel();
            modeloDeSelecao.addListSelectionListener(new ListSelectionListener() {

                public void valueChanged(ListSelectionEvent e) {
                    if (jTable.getRowCount() > 0) {
                        try {
                            jTable.scrollRectToVisible(jTable.getCellRect(jTable.getSelectedRow(), 0, false));
                        } catch (Exception ex) {
                            System.out.println("---------------Crepz do Rudieri----------------------");
                            ex.printStackTrace();
                        }
                    }
                }
            });
            setTitle(jTextField_NomePlayList.getText());

        } catch (Exception ex) {
            System.out.println("Probleminhas..... no filtrar.");
            //  JOptionPane.showMessageDialog(this, "Erro ao Filtrar! OO");
            ex.printStackTrace();
        }
    }

    public void setVisible(boolean b, boolean a) {
        super.setVisible(b);
        super.setAlwaysOnTop(a);
    }

//    private void tocar(Musica m) throws Exception {
//        System.out.println("------------" + m);
//        principal.getMusiquera().abrir(m, 0, false);
//        principal.getMusiquera().tocar();
//
//        faltamTocar.remove(m);
//    }

    public Musica getAleatorio(Musica atual) {
       try{
        if (jahFoi.indexOf(atual) == -1) {
            jahFoi.add(atual);
            faltamTocar.remove(atual);
            System.out.println("Atual ADD!");
        }
        if (jahFoi.indexOf(atual) < jahFoi.size() - 1 && jahFoi.indexOf(atual) != -1 && jahFoi.size() > 0) {
            jTable.setRowSelectionInterval(jahFoi.indexOf(atual), jahFoi.indexOf(atual));
            return (Musica) jahFoi.get(jahFoi.indexOf(atual) + 1);

        } else {
            if (faltamTocar.size() < 1) {

                if (recomecar) {
                    System.out.println("Recomeçando");
                    for (int i = 0; i < jTable.getRowCount(); i++) {
                        faltamTocar.add(total.get(i));
                        //faltamTocar.add(jTable.getModel().getValueAt(i, jTable.getColumnCount()));
                    }
                    return getProxima();

                }
                return null;
            } else {
                int random = (int) (Math.random() * faltamTocar.size());
                jahFoi.add(faltamTocar.get(random));

                for (int i = 0; i < jTable.getRowCount(); i++) {

                    if (((Musica) jTable.getModel().getValueAt(i, jTable.getColumnCount())).getCaminho().equals(principal.getMusiquera().getMusica().getCaminho())) {
                        jTable.setRowSelectionInterval(i, i);
                    }

                }
                return (Musica) faltamTocar.remove(random);
            }
        }
        }
       catch (Exception ex){
           System.out.println("Problema ou sortear uma musica");
           return null;
       }
    }

    public Musica getProxima() {

        try {
            Musica atual = (principal.getMusiquera()).getMusica();
            int mAtual = -1;
            if (!aleatorio) {
                if(atual==null){

                }
                for (int i = 0; i < jTable.getRowCount(); i++) {
                    if (((Musica) jTable.getModel().getValueAt(i, jTable.getColumnCount())).getCaminho().equals(atual.getCaminho())) {
                        mAtual = i;
                    }
                }

                if (mAtual < 0) {
                    return null;
                }

                if (mAtual + 1 >= jTable.getRowCount()) {
                    jTable.setRowSelectionInterval(0, 0);
                    return (Musica) jTable.getModel().getValueAt(0, jTable.getColumnCount());

                } else {
                    jTable.setRowSelectionInterval(mAtual + 1, mAtual + 1);
                    return (Musica) jTable.getModel().getValueAt(mAtual + 1, jTable.getColumnCount());

                }


            } else {
                return getAleatorio(atual);
            }

        } catch (Exception ex) {
            Logger.getLogger(JPlayList.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }

    }

    public Musica getAnterior() {
        try {
            Musica atual = principal.getMusiquera().getMusica();
            int mAtual = -1;
            if (!aleatorio) {
                for (int i = 0; i < jTable.getRowCount(); i++) {
                    if (((Musica) jTable.getModel().getValueAt(i, jTable.getColumnCount())).getCaminho().equals(atual.getCaminho())) {
                        mAtual = i;
                    }
                }

                if (mAtual > -1) {
                    if (mAtual - 1 < 0) {
                        return (Musica) jTable.getModel().getValueAt(jTable.getRowCount() - 1, jTable.getColumnCount());
                    } else {
                        return (Musica) jTable.getModel().getValueAt(mAtual - 1, jTable.getColumnCount());
                    }
                }
                return null;
            } else {
                if (jahFoi.indexOf(principal.getMusiquera().getMusica()) > 0) {
                    return (Musica) jahFoi.get(jahFoi.indexOf(principal.getMusiquera().getMusica()) - 1);
                }
                return null;
            }
        } catch (Exception ex) {
            Logger.getLogger(JPlayList.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }

    }

    private void exportarPlayList() {
        JFileChooser jf = new JFileChooser();
        int foi = jf.showSaveDialog(this);
        if (foi == JFileChooser.APPROVE_OPTION) {
            File f;
            String fs = jf.getSelectedFile().toString();
            if (fs.toLowerCase().indexOf(".m3u") == -1) {
                f = new File(fs + ".m3u");
            } else {
                f = new File(fs);
            }
            if (f.exists()) {
                f.delete();
            }
            try {
                f.createNewFile();
                FileWriter fw = new FileWriter(f);
                BufferedWriter bfw = new BufferedWriter(fw);
                for (int i = 0; i < total.size(); i++) {
                    Musica m = (Musica) total.get(i);
                    String aux = m.getCaminho();
                    aux = aux.replace("/", File.separator);
                    bfw.write(aux);
                    bfw.newLine();
                    System.out.println(aux);
                }
                bfw.close();
                fw.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            System.out.println(f.getAbsolutePath());
        }

    }

    private void salvarPlaylist() {
        Transacao t = new Transacao();
        try {
            t.begin();
            Playlist playList = new Playlist();
            playList.setNome(jTextField_NomePlayList.getText());
            if (PlaylistBD.existe(playList, t)) {
                PlaylistBD.carregar(playList, t);
            } else {
                PlaylistBD.incluir(playList, t);
                PlaylistBD.existe(playList, t);
            }
            PlayMusicaBD.excluirMusica(playList, t);
            for (int i = 0; i < total.size(); i++) {
                Musica m = (Musica) total.get(i);
                System.out.println("--id" + m.getId());
                // if(!MusicaBD.existe(m))//----------------------aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa
                //  MusicaBD.incluir(m, t);
                PlayMusica plm = new PlayMusica();
                plm.setMusica(m);
                plm.setPlaylist(playList);
                plm.setSeq(i);
                //plm.setId(m.getId());
                PlayMusicaBD.incluir(plm, t);
            }
            playList.setNrMusicas(total.size());

            PlaylistBD.alterar(playList, t);
            IdAberto = playList.getId();
            setTitle(jTextField_NomePlayList.getText());

            t.commit();
        } catch (Exception ex) {
            t.rollback();
            ex.printStackTrace();
        }

    }

    private void deletar() {
        Transacao t = new Transacao();
        try {
            t.begin();

            Playlist playList = new Playlist();
            playList.setNome(jTextField_NomePlayList.getText());
            if (PlaylistBD.existe(playList, t)) {
                PlaylistBD.carregar(playList, t);
            }
            PlayMusicaBD.excluirMusica(playList, t);
            PlaylistBD.excluir(playList, t);


            t.commit();
        } catch (Exception ex) {
            t.rollback();
            ex.printStackTrace();
        }
    }

    public void limpar() {
        jTextField_NomePlayList.setText("");
        initTabelaLista(false);
        faltamTocar = new ArrayList();
        total = new ArrayList();
    }

    public void tocar(Playlist playlist, boolean tocarMesmo) {
        try {
            IdAberto = playlist.getId();
            jTextField_NomePlayList.setText(playlist.getNome());
            setTitle(playlist.getNome());
            atualizarTabelaLista();
            if (tocarMesmo) {
                principal.getMusiquera().abrir(getProxima(), 0, false, true);
//                if (!aleatorio) {
//                //    tocar((Musica) jTable.getModel().getValueAt(0, jTable.getColumnCount()));
//                } else {
//                    getProxima();
//                }
            }
        } catch (Exception ex) {
            Logger.getLogger(JPlayList.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void adicionarMusica() {
        Transacao t = new Transacao();
        try {
            File in = principal.telaAbrirArquivo();

            t.begin();

            Musica m = Musica.addFiles(in, t);
            t.commit();
            trace("ID: " + m.getId());
            trace("Nome: " + m.getNome());
            trace("Album: " + m.getAlbum());
            trace("Caminho: " + m.getCaminho());
            //trace("ID: " +m.getId());
            MusicaSC filtro = new MusicaSC();
            filtro.setCaminho(m.getCaminho().trim());


            m = (Musica) MusicaBD.listar(filtro).get(0);
//
            addPlaylist(m);


        } catch (Exception ex) {
            t.rollback();
            Logger.getLogger(JPlayList.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void trace(Object o) {
        System.out.println(o.toString());
    }

    public void atualizaIcons() {
        jLabel2.setIcon(principal.save);
        jLabel3.setIcon(principal.saveAs);
    }

    private void openM3u() {
        JFileChooser jf = new JFileChooser();
        jf.setDialogType(jf.OPEN_DIALOG);
        jf.setFileSelectionMode(JFileChooser.FILES_ONLY);
        jf.setFileFilter(new FileFilter() {

            @Override
            public boolean accept(File f) {
                if (f.getName().length() > 4) {
                    return f.getName().toLowerCase().lastIndexOf(".m3u") == f.getName().length() - 4;
                } else {
                    return false;
                }

            }

            @Override
            public String getDescription() {
                return "Arquivos de PlayList *.m3u";
            }
        });
        if (jf.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File f = jf.getSelectedFile();
            Transacao t = new Transacao();
            try {
                t.begin();
                String lido = new String(FileUtils.leArquivoCodificacao(f, "WINDOWS-1252"));
                String files[] = lido.split("\n");
                for (String s : files) {
                    if (s.indexOf(":\\") != -1) {
                        //  s=s.substring(0,s.lastIndexOf("\\") );
                        s = s.replace("\\\\", "/");
                        File mp3 = new File(s);
                        addPlaylist(Musica.addFiles(mp3, t));
                        System.out.println(mp3.getAbsolutePath());
                    }
                }
                t.commit();
            } catch (Exception ex) {
                t.rollback();
                Logger.getLogger(JPlayList.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    public void salvarPlaylistAtual() {
        if (jTextField_NomePlayList.getText() == null || jTextField_NomePlayList.getText().equals("")) {
            jTextField_NomePlayList.setText("Execução");
        }
        salvarPlaylist();

    }

    public void filtraTexto(int code) {

        jTextEntrada.setVisible(true);
        repaint();
        // jPanel6.repaint();
        //jPanel3.repaint();
        jTextEntrada.requestFocus();
        jTextEntrada.setText("");


    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jTextField_NomePlayList = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane = new javax.swing.JScrollPane();
        jTable = new javax.swing.JTable();
        jPanel3 = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        jButton4 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jToggleButton1 = new javax.swing.JToggleButton();
        jTextEntrada = new javax.swing.JTextField();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem6 = new javax.swing.JMenuItem();
        jMenuItem4 = new javax.swing.JMenuItem();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuItem3 = new javax.swing.JMenuItem();
        jMenuItem5 = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanel1.setPreferredSize(new java.awt.Dimension(323, 80));
        jPanel1.setLayout(new java.awt.BorderLayout());

        jPanel8.setLayout(new java.awt.GridLayout(2, 0));

        jPanel4.setPreferredSize(new java.awt.Dimension(323, 50));
        jPanel4.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 1, 0));

        jLabel1.setText("Nome:");
        jPanel4.add(jLabel1);

        jTextField_NomePlayList.setPreferredSize(new java.awt.Dimension(200, 25));
        jPanel4.add(jTextField_NomePlayList);

        jLabel2.setText("jLabel2");
        jLabel2.setNextFocusableComponent(jTextField_NomePlayList);
        jLabel2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel2MouseClicked(evt);
            }
        });
        jPanel4.add(jLabel2);

        jLabel3.setText("jLabel3");
        jLabel3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel3MouseClicked(evt);
            }
        });
        jPanel4.add(jLabel3);

        jPanel8.add(jPanel4);

        jPanel5.setPreferredSize(new java.awt.Dimension(323, 50));
        jPanel5.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 5, 0));

        jButton1.setText("Abrir");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jPanel5.add(jButton1);

        jButton2.setText("Consultar");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        jPanel5.add(jButton2);

        jButton3.setText("Deletar");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });
        jPanel5.add(jButton3);

        jButton6.setText("Limpar");
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });
        jPanel5.add(jButton6);

        jPanel8.add(jPanel5);

        jPanel1.add(jPanel8, java.awt.BorderLayout.CENTER);

        getContentPane().add(jPanel1, java.awt.BorderLayout.PAGE_START);

        jPanel2.setPreferredSize(new java.awt.Dimension(323, 150));
        jPanel2.setLayout(new java.awt.BorderLayout());

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

        jPanel2.add(jScrollPane, java.awt.BorderLayout.CENTER);

        getContentPane().add(jPanel2, java.awt.BorderLayout.CENTER);

        jPanel3.setPreferredSize(new java.awt.Dimension(323, 30));
        jPanel3.setLayout(new javax.swing.BoxLayout(jPanel3, javax.swing.BoxLayout.X_AXIS));

        jPanel6.setPreferredSize(new java.awt.Dimension(323, 25));
        jPanel6.setLayout(new javax.swing.BoxLayout(jPanel6, javax.swing.BoxLayout.LINE_AXIS));

        jButton4.setText("+");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });
        jPanel6.add(jButton4);

        jButton5.setText("-");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });
        jPanel6.add(jButton5);

        jToggleButton1.setText("Opções da lista");
        jToggleButton1.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jToggleButton1StateChanged(evt);
            }
        });
        jPanel6.add(jToggleButton1);

        jTextEntrada.setText("jTextField1");
        jTextEntrada.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                jTextEntradaCaretUpdate(evt);
            }
        });
        jTextEntrada.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                jTextEntradaPropertyChange(evt);
            }
        });
        jTextEntrada.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextEntradaKeyPressed(evt);
            }
        });
        jPanel6.add(jTextEntrada);

        jPanel3.add(jPanel6);

        getContentPane().add(jPanel3, java.awt.BorderLayout.PAGE_END);

        jMenu1.setText("Funções");

        jMenuItem6.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_A, java.awt.event.InputEvent.ALT_MASK));
        jMenuItem6.setText("Abrir PlayList *.m3u");
        jMenuItem6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem6ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem6);

        jMenuItem4.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem4.setText("Salvar");
        jMenuItem4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem4ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem4);

        jMenuItem2.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F4, 0));
        jMenuItem2.setText("Limpar");
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem2);

        jMenuItem1.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F5, 0));
        jMenuItem1.setText("Pesquisar");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem1);

        jMenuItem3.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F6, 0));
        jMenuItem3.setText("Deletar");
        jMenuItem3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem3ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem3);

        jMenuItem5.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_SPACE, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem5.setText("Fechar");
        jMenuItem5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem5ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem5);

        jMenuBar1.add(jMenu1);

        setJMenuBar(jMenuBar1);

        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        setBounds((screenSize.width-363)/2, (screenSize.height-320)/2, 363, 320);
    }// </editor-fold>//GEN-END:initComponents

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        DefaultTableModel tm = (DefaultTableModel) jTable.getModel();
        int selecteds[] = jTable.getSelectedRows();
        for (int i = selecteds.length - 1; i >= 0; i--) {
            System.out.println(selecteds[i]);

            Musica m = (Musica) jTable.getModel().getValueAt(selecteds[i], jTable.getColumnCount());
            faltamTocar.remove(m);
            pesquisa.remove(m);
            total.remove(m);
            tm.removeRow(selecteds[i]);
        }
        System.out.println("Array: " + selecteds.toString());


    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        adicionarMusica();
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableMouseClicked
        if (evt.getClickCount() == 2) {
            try {
                Musica m = (Musica) jTable.getModel().getValueAt(jTable.getSelectedRow(), jTable.getColumnCount());
                //tocar(m);
                principal.getMusiquera().abrir(m, 0, false, true);
                if (jahFoi.indexOf(m) != jahFoi.size() - 1) {
                    jahFoi.add(m);
                    faltamTocar.remove(m);
                }
            } catch (Exception ex) {
                Logger.getLogger(JPlayList.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
}//GEN-LAST:event_jTableMouseClicked

    private void jTableKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTableKeyPressed
        if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
            evt.setKeyCode(KeyEvent.VK_UNDEFINED);
            try {
                Musica m = (Musica) jTable.getModel().getValueAt(jTable.getSelectedRows()[0], jTable.getColumnCount());
             //   tocar(m);
                principal.getMusiquera().abrir(m, 0, false, true);
                if (jahFoi.indexOf(m) != jahFoi.size() - 1) {
                    jahFoi.add(m);
                    faltamTocar.remove(m);
                }
            } catch (Exception ex) {
                Logger.getLogger(JPlayList.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        switch (evt.getKeyCode()) {
            case KeyEvent.VK_DELETE:
                jButton5ActionPerformed(null);
                return;
            case KeyEvent.VK_PAGE_DOWN:
            case KeyEvent.VK_PAGE_UP:
            case KeyEvent.VK_END:
            case KeyEvent.VK_HOME:
                return;

        }
        if (evt.getKeyCode() != KeyEvent.VK_SHIFT && evt.getKeyCode() != KeyEvent.VK_CONTROL) {
            if (!((evt.getKeyCode() == KeyEvent.VK_DOWN || evt.getKeyCode() == KeyEvent.VK_UP) && evt.isShiftDown())) {
                filtraTexto(evt.getKeyCode());
                evt.setKeyCode(KeyEvent.VK_UNDEFINED);
            }
        }

        System.out.println("Code: " + evt.getKeyCode());
}//GEN-LAST:event_jTableKeyPressed

    private void jScrollPaneFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jScrollPaneFocusGained
        jTable.requestFocus();
        jTable.changeSelection(0, 0, false, false);
}//GEN-LAST:event_jScrollPaneFocusGained

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        deletar();
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        jTextField_NomePlayList.setText("");
        initTabelaLista(false);
    }//GEN-LAST:event_jButton6ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        JPlaylists jPlayLists = new JPlaylists(principal, true, this);
        jPlayLists.setVisible(true);
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        jButton2ActionPerformed(evt);
}//GEN-LAST:event_jMenuItem1ActionPerformed

    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
        limpar();
}//GEN-LAST:event_jMenuItem2ActionPerformed

    private void jMenuItem3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem3ActionPerformed
        deletar();
    }//GEN-LAST:event_jMenuItem3ActionPerformed

    private void jMenuItem4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem4ActionPerformed
        jToggleButton1.setSelected(true);
        if (jTextField_NomePlayList.getText() == null || jTextField_NomePlayList.getText().equals("") || jTextField_NomePlayList.getText().equals("Coloque um nome!")) {
            jTextField_NomePlayList.setText("Coloque um nome!");
            jTextField_NomePlayList.selectAll();

            transferFocus();
        } else {
            salvarPlaylist();
            jToggleButton1.setSelected(false);

        }


    }//GEN-LAST:event_jMenuItem4ActionPerformed

    private void jMenuItem5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem5ActionPerformed
        setVisible(false);
    }//GEN-LAST:event_jMenuItem5ActionPerformed

    private void jLabel2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel2MouseClicked
        // TODO add your handling code here:
        if (evt.getButton() == MouseEvent.BUTTON1) {
            jMenuItem4ActionPerformed(null);
        }
    }//GEN-LAST:event_jLabel2MouseClicked

    private void jLabel3MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel3MouseClicked
        // TODO add your handling code here:
        if (evt.getButton() == MouseEvent.BUTTON1) {
            exportarPlayList();
        }
    }//GEN-LAST:event_jLabel3MouseClicked

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        openM3u();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jMenuItem6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem6ActionPerformed
        // TODO add your handling code here:
        openM3u();
    }//GEN-LAST:event_jMenuItem6ActionPerformed

    private void jToggleButton1StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jToggleButton1StateChanged
        // TODO add your handling code here:
        jPanel1.setVisible(jToggleButton1.isSelected());
    }//GEN-LAST:event_jToggleButton1StateChanged

    private void jTextEntradaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextEntradaKeyPressed
        // TODO add your handling code here:
        switch (evt.getKeyCode()) {
            case KeyEvent.VK_ESCAPE:
                jTextEntrada.setText("");
                break;
            case KeyEvent.VK_ENTER:
                Musica m = (Musica) jTable.getModel().getValueAt(jTable.getSelectedRow(), jTable.getColumnCount());
                try {
                    principal.getMusiquera().abrir(m, 0, false, true);
                    //  tocar(null);
                } catch (Exception ex) {
                    Logger.getLogger(JPlayList.class.getName()).log(Level.SEVERE, null, ex);
                }
                break;
            case KeyEvent.VK_DOWN:
                int RowSelct = jTable.getSelectedRow();
                if (jTable.getSelectedRow() == jTable.getRowCount() - 1) {
                    jTable.getSelectionModel().setSelectionInterval(0, 0);
                } else {
                    jTable.getSelectionModel().setSelectionInterval(RowSelct + 1, RowSelct + 1);
                }
                break;
            case KeyEvent.VK_UP:
                int RowSelct2 = jTable.getSelectedRow();
                if (jTable.getSelectedRow() == 0) {
                    jTable.getSelectionModel().setSelectionInterval(jTable.getRowCount() - 1, jTable.getRowCount() - 1);
                } else {

                    jTable.getSelectionModel().setSelectionInterval(RowSelct2 - 1, RowSelct2 - 1);
                }

                break;
            case KeyEvent.VK_SHIFT:
                jTable.requestFocus();
                break;
        }

//        if(aux>47 && aux<91 || aux>64 && aux<58 || aux>95 && aux<106 ){
//
//        }

    }//GEN-LAST:event_jTextEntradaKeyPressed

    private void jTextEntradaPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_jTextEntradaPropertyChange
        // TODO add your handling code here:
        System.out.println(evt.getPropertyName());
    }//GEN-LAST:event_jTextEntradaPropertyChange
    int dot = 0;
    private void jTextEntradaCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_jTextEntradaCaretUpdate
        if (evt.getDot() != dot) {
            dot = evt.getDot();
            ArrayList<Musica> novaLista = new ArrayList<Musica>();
            for (Musica m : pesquisa) {
                if ((m.getNome() + m.getAutor()).toLowerCase().indexOf(jTextEntrada.getText().toLowerCase()) != -1) {
                    novaLista.add(m);
                }
            }
            atualizarTabelaLista(novaLista);
            if (jTable.getRowCount() > 0) {
                jTable.setRowSelectionInterval(0, 0);
                //Estou aqui
            }
            jTextEntrada.requestFocus();
        }
    }//GEN-LAST:event_jTextEntradaCaretUpdate

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        System.setProperty("Quaqua.tabLayoutPolicy", "wrap");

        // set the Quaqua Look and Feel in the UIManager
        try {
            UIManager.setLookAndFeel("ch.randelshofer.quaqua.QuaquaLookAndFeel");

        } catch (Exception ex) {
            ex.printStackTrace();
        }


        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                JPlayList dialog = new JPlayList(new javax.swing.JFrame(), true, null);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {

                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    public void addPlaylist(Musica m) {
        System.out.println("Musica: " + m);
        Object[] row = new Object[2];
        row[0] = new JLista(m.getNome(), m.getAutor());
        // row[1] = m.getAutor();
        row[1] = m;
        ((ModelReadOnly) jTable.getModel()).addRow(row);
        faltamTocar.add(m);
        total.add(m);
        pesquisa.add(m);
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JMenuItem jMenuItem5;
    private javax.swing.JMenuItem jMenuItem6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JScrollPane jScrollPane;
    private javax.swing.JTable jTable;
    private javax.swing.JTextField jTextEntrada;
    private javax.swing.JTextField jTextField_NomePlayList;
    private javax.swing.JToggleButton jToggleButton1;
    // End of variables declaration//GEN-END:variables
}
