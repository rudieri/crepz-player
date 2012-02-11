/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * JFilaReproducao.java
 *
 * Created on 29/01/2012, 00:24:46
 */
package com.fila;

import com.Musiquera;
import com.Musiquera.PropriedadesMusica;
import com.config.JConfiguracao;
import com.main.Carregador;
import com.main.Notificavel;
import com.musica.ModelReadOnly;
import com.musica.Musica;
import com.musica.MusicaBD;
import com.musica.MusicaGerencia;
import com.musica.MusicaSC;
import com.utils.ComandosSO;
import com.utils.DiretorioUtils;
import com.utils.model.ObjectTableModel;
import com.utils.model.ObjectTableModelListener;
import com.utils.model.ObjectTransferable;
import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComponent;
import javax.swing.TransferHandler;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableRowSorter;

/**
 *
 * @author rudieri
 */
public class JFilaReproducao extends javax.swing.JFrame implements Notificavel {

//    private ModelReadOnly modelMusicas;
    private ObjectTableModel<Musica> objModelMusicas;
    private final Musiquera musiquera;
    private ModelReadOnly modelFila;
    private final Carregador carregador;
    private TableRowSorter sorterMusicas;
    private DropTarget dropTargetFila;
    private boolean ajustandoTempo;

    /** Creates new form JFilaReproducao */
    public JFilaReproducao(Musiquera musiquera, Carregador carregador) {
        initComponents();
        jPanelProgress.setVisible(false);
        initTabelaFila();
        initTabelaMusica();
        atualizaTabelaMusica();
        this.musiquera = musiquera;

//        TransferHandler transferHandler = new TransferHandler(null);
//       jTableFila.setDropMode(DropMode.ON);
//        jTableFila.setTransferHandler(transferHandler);
        this.carregador = carregador;
        inicializaIcones();
    }

    private void tocarMusicaSelecionada() {
        Musica musica = objModelMusicas.getItem(jTableMusicas.convertRowIndexToModel(jTableMusicas.getSelectedRow()));
        musiquera.abrir(musica, 0, false);
//            alterarMusica(musica);
    }

    private void tocarMusicaSelecionadaFila() {
        Musica musica = (Musica) modelFila.getValueAt(jTableFila.getSelectedRow(), 0);
        modelFila.removeRow(jTableFila.getSelectedRow());
        musiquera.abrir(musica, 0, false);
//            alterarMusica(musica);
        atualizarBarraStausFila();
    }

    private void removerMusicasSelecionadasFila() {
        int[] linhas = jTableFila.getSelectedRows();
        for (int i = linhas.length - 1; i >= 0; i--) {
            int linha = linhas[i];
            modelFila.removeRow(linha);
        }
        atualizarBarraStausFila();
    }

    private void atualizarBarraStausFila() {
        jLabelQtdMusicasFila.setText(String.valueOf(modelFila.getRowCount()));
    }

    @SuppressWarnings("AssignmentToMethodParameter")
    private void alterarPosicaoMusicasFila(int[] indices, int novaPosicao) {
        int inicio = -1;
        int fim = -1;
        int ultimoLido = -1;
        HashMap<Integer, Integer> intervalos = new HashMap<Integer, Integer>(5);
        for (int i = 0; i < indices.length; i++) {
            int indice = indices[i];
            if (inicio == -1) {
                inicio = indice;
            }
            fim = indice;
            if (indice - 1 != ultimoLido) {
                inicio = indice;
            }
            intervalos.put(inicio, fim);
            ultimoLido = indice;
        }
        for (Iterator<Integer> it = intervalos.keySet().iterator(); it.hasNext();) {
            Integer start = it.next();
            int end = intervalos.get(start);
            modelFila.moveRow(start.intValue(), end, novaPosicao);
            novaPosicao += (end - start + 1);
        }
        jTableFila.clearSelection();
    }

    private void atualizarBarraStatusMusica() {
        jLabelQtdMusicas.setText(String.valueOf(objModelMusicas.getRowCount()));
    }

    private void initTabelaFila() {
        modelFila = new ModelReadOnly();
        modelFila.addColumn("");
        modelFila.setRowCount(0);
        modelFila.addTableModelListener(new TableModelListener() {

            @Override
            public void tableChanged(TableModelEvent e) {
                if (e.getType() == TableModelEvent.INSERT) {
                    carregador.setFonteReproducao(Carregador.FonteReproducao.FILA_REPRODUCAO);
                    if (!musiquera.isPlaying()) {
                        musiquera.abrirETocar();
                    }
                }
            }
        });
        jTableFila.setDefaultRenderer(Object.class, new TableRenderFila());
        jTableFila.setRowHeight(50);
        // C�digo do C�o...

        jTableFila.setTransferHandler(new TransferHandler(null) {

            @Override
            public int getSourceActions(JComponent c) {
                return MOVE;
            }

            @Override
            protected Transferable createTransferable(JComponent c) {
                int[] rows = jTableFila.getSelectedRows();
                ArrayList<Musica> musicas = new ArrayList<Musica>(rows.length);
                for (int i = 0; i < rows.length; i++) {
                    musicas.add((Musica) modelFila.getValueAt(rows[i], 0));
                }
                return new ObjectTransferable(musicas, "crepz/array-musica");

            }
        });
        dropTargetFila = new DropTarget(jScrollPaneFila, new DropTargetAdapter() {

            @Override
            public void drop(DropTargetDropEvent dtde) {
                try {
                    Point location = dtde.getLocation();
                    if ((dtde.getSourceActions() == TransferHandler.MOVE
                            || location.y < jTableFila.getHeight())
                            && jTableFila.getSelectedRows().length > 0) {
                        Point visibleLocation = jTableFila.getVisibleRect().getLocation();
                        visibleLocation.x += location.x;
                        visibleLocation.y += location.y;
                        int destino = jTableFila.rowAtPoint(visibleLocation);
                        alterarPosicaoMusicasFila(jTableFila.getSelectedRows(), destino);
                        return;
                    }
                    Transferable transferable = dtde.getTransferable();
                    DataFlavor[] flavors = transferable.getTransferDataFlavors();
                    Object data = null;
                    try {
                        data = transferable.getTransferData(transferable.getTransferDataFlavors()[0]);
                    } catch (Exception ex) {
                        // ir� cair no drop do linux e la encontrar� alguns arquivos :D
                        Logger.getLogger(JFilaReproducao.class.getName()).log(Level.SEVERE, "Crepz tratavel...", ex);
                    }
                    if (data != null && data instanceof Musica) {
                        ((ModelReadOnly) jTableFila.getModel()).addRow(new Object[]{data});
                    } else if (data != null && data instanceof ArrayList) {
                        addMusicasToFila(data);
                    } else {
                        //Windows
                        if (transferable.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                            ArrayList<File> arquivos = (ArrayList) transferable.getTransferData(java.awt.datatransfer.DataFlavor.javaFileListFlavor);
                            importarMusicas(arquivos);
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
                                                Logger.getLogger(JFilaReproducao.class.getName()).log(Level.SEVERE, null, ex);
                                            }
                                        }
                                    }
                                    importarMusicas(arquivos);
                                    break loop_flavor;
                                }
                            }
                        }
                    }
                } catch (UnsupportedFlavorException ex) {
                    Logger.getLogger(JFilaReproducao.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(JFilaReproducao.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            @Override
            public void dragEnter(DropTargetDragEvent evt) {
                evt.acceptDrag(DnDConstants.ACTION_COPY);
            }
        });
        jTableFila.setDropTarget(dropTargetFila);
        jScrollPaneFila.setDropTarget(dropTargetFila);
        jTableFila.setModel(modelFila);
    }

    private void inicializaIcones() {
        jButton_Play.setText("");
        jButton_Stop.setText("");
        jButton_Next.setText("");
//        jButton_Ant.setText("");
        jToggle_Repete.setText("");

        jButton_Stop.setIcon(carregador.icones.stopIcon32);
//        jButton_Ant.setIcon(carregador.icones.voltaIcon32);
        jButton_Next.setIcon(carregador.icones.frenteIcon32);
        if (musiquera.isPaused()) {
            jButton_Play.setIcon(carregador.icones.playIcon32);
        } else {
            jButton_Play.setIcon(carregador.icones.pauseIcon32);
        }
        if (carregador.isRepeat()) {
            jToggle_Repete.setIcon(carregador.icones.repeatOnIcon32);
        } else {
            jToggle_Repete.setIcon(carregador.icones.repeatOffIcon32);
        }

        // menus popup
        jMenuItemFilaTocar.setIcon(carregador.icones.playIcon16);
        jMenuItemFilaRemover.setIcon(carregador.icones.xis);
        jMenuItemEmbaralhar.setIcon(carregador.icones.randomOnIcon16);
//        jMenuItemFilaTocar.setIcon(carregador.icones.playIcon16);
//        jMenuItemFilaTocar.setIcon(carregador.icones.playIcon16);

    }

    private void importarMusicas(final ArrayList<File> arquivos) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                importarMusicasRun(arquivos);
            }
        }).start();
    }

    private void importarMusicasRun(ArrayList<File> arquivos) {
        try {
            int nroFiles = DiretorioUtils.calculaQuantidadeArquivos(arquivos);
            jPanelProgress.setVisible(true);
            ArrayList musicasImportadas = new ArrayList<Musica>(nroFiles);
            MusicaGerencia.mapearDiretorio(arquivos, musicasImportadas, jProgressBarImportando, nroFiles);
            addMusicasToFila(musicasImportadas);
            jPanelProgress.setVisible(false);
            atualizaTabelaMusica();
        } catch (Exception ex) {
            Logger.getLogger(JFilaReproducao.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void addMusicasToFila(Object data) {
        addMusicasToFila((ArrayList) data);
    }

    private void addMusicasToFila(ArrayList<Musica> data) {
        for (int i = 0; i < data.size(); i++) {
            Musica musica = data.get(i);
            ((ModelReadOnly) jTableFila.getModel()).addRow(new Object[]{musica});
        }
        atualizarBarraStausFila();
    }

    private void initTabelaMusica() {
        objModelMusicas = new ObjectTableModel<Musica>(Musica.class);
        jTableMusicas.setModel(objModelMusicas);
        objModelMusicas.addObjectTableModelListener(new ObjectTableModelListener() {

            @Override
            public void sizeChanged(int newSize) {
                atualizarBarraStatusMusica();
            }
        });
        sorterMusicas = new TableRowSorter(objModelMusicas);
        jTableMusicas.setRowSorter(sorterMusicas);
        jTableMusicas.setTransferHandler(new TransferHandler(null) {

            @Override
            public int getSourceActions(JComponent c) {
                return COPY;
            }

            @Override
            protected Transferable createTransferable(JComponent c) {
                int[] rows = jTableMusicas.getSelectedRows();
                ArrayList<Musica> musicas = new ArrayList<Musica>(rows.length);
                for (int i = 0; i < rows.length; i++) {
                    musicas.add(objModelMusicas.getItem(jTableMusicas.convertRowIndexToModel(rows[i])));
                }
                return new ObjectTransferable(musicas, "crepz/array-musica");

            }
        });
        jTableMusicas.setDragEnabled(true);
    }

    private void atualizaTabelaMusica() {
        try {
            MusicaSC filtro = new MusicaSC();
//            objModelMusicas.clear();
            ArrayList<Musica> listar = MusicaBD.listar(filtro);
            if (listar.isEmpty()) {
                objModelMusicas.clear();
            } else {
                objModelMusicas.setItens(listar);
            }
            jTableMusicas.repaint();
            atualizarBarraStatusMusica();
        } catch (Exception ex) {
            Logger.getLogger(JFilaReproducao.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void embaralharFila() {
        for (int i = 0; i < modelFila.getRowCount(); i++) {
            modelFila.moveRow(i, i, (int) (Math.random() * modelFila.getRowCount()));
        }
    }

    private void limparFila() {
        modelFila.setRowCount(0);
        atualizarBarraStausFila();
    }

    public Musica getProxima() {
        if (modelFila.getRowCount() > 0) {
            Musica musica = (Musica) modelFila.getValueAt(0, 0);
            modelFila.removeRow(0);
            atualizarBarraStausFila();
//            alterarMusica(musica);
            return musica;
        }
        return null;
    }

    @Override
    public void atualizaLabels(String nome, int bits, String tempoTotal, int freq) {
        // do nothing
    }

    @Override
    public void propriedadesMusicaChanged(PropriedadesMusica propriedadesMusica) {
        jLabelTocando.setText(propriedadesMusica.getNome() + " - " + propriedadesMusica.getArtista());
        int indexOf = objModelMusicas.indexOf(musiquera.getMusica());
        musiquera.getMusica().setTempo(propriedadesMusica.getTempoTotal());
        setTitle(propriedadesMusica.getNome() + " - " + propriedadesMusica.getArtista());
        try {

            int convertRow = jTableMusicas.convertRowIndexToView(indexOf);
            if (convertRow != -1) {
                objModelMusicas.atualizarItem(musiquera.getMusica(), indexOf, convertRow);
            }
            jTableMusicas.repaint();
        } catch (Exception ex) {
            System.out.println("N�o aparece no filtro...");
        }
    }

    @Override
    public void dispose() {
        jTextFieldPesquisa.setText("");
        super.dispose();
    }

    @Override
    public void eventoNaMusica(int tipo) {
    }

    @Override
    public void tempoEh(double v) {
        if (!ajustandoTempo) {
            jSlider_Tempo.setValue((int) (jSlider_Tempo.getMaximum() * v));
        }
    }

    @Override
    public void tempoEhHMS(String hms) {
        if (!ajustandoTempo) {
            jSlider_Tempo.setToolTipText(hms);
        }

    }

    @Override
    public void setVisible(boolean b) {
        super.setVisible(b);
        atualizaTabelaMusica();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPopupMenuFila = new javax.swing.JPopupMenu();
        jMenuItemFilaTocar = new javax.swing.JMenuItem();
        jMenuItemFilaRemover = new javax.swing.JMenuItem();
        jMenuItemEmbaralhar = new javax.swing.JMenuItem();
        jMenuItemFilaMoveCima = new javax.swing.JMenuItem();
        jMenuItemFilaMoveBaixo = new javax.swing.JMenuItem();
        jMenuItemLimpar = new javax.swing.JMenuItem();
        jPopupMenuMusica = new javax.swing.JPopupMenu();
        jMenuItemTocar = new javax.swing.JMenuItem();
        jMenuItemAdicionar = new javax.swing.JMenuItem();
        jMenuItemPasta = new javax.swing.JMenuItem();
        jPanelCentro = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jTextFieldPesquisa = new javax.swing.JTextField();
        jButtonLimparPesquisa = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTableMusicas = new javax.swing.JTable();
        jPanelStatus = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabelQtdMusicas = new javax.swing.JLabel();
        jPanel_Esquerdo = new javax.swing.JPanel();
        jScrollPaneFila = new javax.swing.JScrollPane();
        jTableFila = new javax.swing.JTable();
        jPanel6 = new javax.swing.JPanel();
        jPanelProgress = new javax.swing.JPanel();
        jProgressBarImportando = new javax.swing.JProgressBar();
        jPanel7 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabelQtdMusicasFila = new javax.swing.JLabel();
        jPanel8 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jLabelTocando = new javax.swing.JLabel();
        jPanel9 = new javax.swing.JPanel();
        jPanelControles = new javax.swing.JPanel();
        jButton_Play = new javax.swing.JLabel();
        jButton_Stop = new javax.swing.JLabel();
        jButton_Next = new javax.swing.JLabel();
        jToggle_Repete = new javax.swing.JLabel();
        jPanel11 = new javax.swing.JPanel();
        jSlider_Tempo = new javax.swing.JSlider();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();

        jMenuItemFilaTocar.setMnemonic('R');
        jMenuItemFilaTocar.setText("Reproduzir");
        jMenuItemFilaTocar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemFilaTocarActionPerformed(evt);
            }
        });
        jPopupMenuFila.add(jMenuItemFilaTocar);

        jMenuItemFilaRemover.setMnemonic('e');
        jMenuItemFilaRemover.setText("Remover da Fila");
        jMenuItemFilaRemover.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemFilaRemoverActionPerformed(evt);
            }
        });
        jPopupMenuFila.add(jMenuItemFilaRemover);

        jMenuItemEmbaralhar.setText("Embaralhar Fila");
        jMenuItemEmbaralhar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemEmbaralharActionPerformed(evt);
            }
        });
        jPopupMenuFila.add(jMenuItemEmbaralhar);

        jMenuItemFilaMoveCima.setMnemonic('C');
        jMenuItemFilaMoveCima.setText("Mover para Cima");
        jMenuItemFilaMoveCima.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemFilaMoveCimaActionPerformed(evt);
            }
        });
        jPopupMenuFila.add(jMenuItemFilaMoveCima);

        jMenuItemFilaMoveBaixo.setMnemonic('B');
        jMenuItemFilaMoveBaixo.setText("Mover para Baixo");
        jMenuItemFilaMoveBaixo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemFilaMoveBaixoActionPerformed(evt);
            }
        });
        jPopupMenuFila.add(jMenuItemFilaMoveBaixo);

        jMenuItemLimpar.setText("Limpar Fila");
        jMenuItemLimpar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemLimparActionPerformed(evt);
            }
        });
        jPopupMenuFila.add(jMenuItemLimpar);

        jMenuItemTocar.setText("Reproduzir");
        jMenuItemTocar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemTocarActionPerformed(evt);
            }
        });
        jPopupMenuMusica.add(jMenuItemTocar);

        jMenuItemAdicionar.setText("Adicionar na Fila");
        jMenuItemAdicionar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemAdicionarActionPerformed(evt);
            }
        });
        jPopupMenuMusica.add(jMenuItemAdicionar);

        jMenuItemPasta.setText("Abrir Pasta da M�sica");
        jMenuItemPasta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemPastaActionPerformed(evt);
            }
        });
        jPopupMenuMusica.add(jMenuItemPasta);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Fila de Reprodu��o");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jPanelCentro.setBorder(javax.swing.BorderFactory.createTitledBorder("Musicas"));
        jPanelCentro.setLayout(new java.awt.BorderLayout());

        jPanel2.setPreferredSize(new java.awt.Dimension(483, 32));
        jPanel2.setLayout(new javax.swing.BoxLayout(jPanel2, javax.swing.BoxLayout.LINE_AXIS));

        jPanel1.setLayout(new java.awt.BorderLayout());

        jPanel3.setPreferredSize(new java.awt.Dimension(483, 30));
        jPanel3.setLayout(new javax.swing.BoxLayout(jPanel3, javax.swing.BoxLayout.LINE_AXIS));

        jLabel1.setText("Pesquisar:");
        jLabel1.setPreferredSize(new java.awt.Dimension(100, 18));
        jPanel3.add(jLabel1);

        jTextFieldPesquisa.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextFieldPesquisaKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextFieldPesquisaKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextFieldPesquisaKeyTyped(evt);
            }
        });
        jPanel3.add(jTextFieldPesquisa);

        jButtonLimparPesquisa.setText("Limpar");
        jButtonLimparPesquisa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonLimparPesquisaActionPerformed(evt);
            }
        });
        jPanel3.add(jButtonLimparPesquisa);

        jPanel1.add(jPanel3, java.awt.BorderLayout.CENTER);

        jPanel2.add(jPanel1);

        jPanelCentro.add(jPanel2, java.awt.BorderLayout.PAGE_START);

        jTableMusicas.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        jTableMusicas.setDragEnabled(true);
        jTableMusicas.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTableMusicasMouseClicked(evt);
            }
        });
        jTableMusicas.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTableMusicasKeyPressed(evt);
            }
        });
        jScrollPane1.setViewportView(jTableMusicas);

        jPanelCentro.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        jPanelStatus.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 2));

        jLabel2.setText("Numero de M�sicas:");
        jPanelStatus.add(jLabel2);

        jLabelQtdMusicas.setText("0");
        jPanelStatus.add(jLabelQtdMusicas);

        jPanelCentro.add(jPanelStatus, java.awt.BorderLayout.PAGE_END);

        getContentPane().add(jPanelCentro, java.awt.BorderLayout.CENTER);

        jPanel_Esquerdo.setBorder(javax.swing.BorderFactory.createTitledBorder("Fila de Reprodu��o"));
        jPanel_Esquerdo.setPreferredSize(new java.awt.Dimension(250, 382));
        jPanel_Esquerdo.setLayout(new java.awt.BorderLayout());

        jTableFila.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        jTableFila.setDragEnabled(true);
        jTableFila.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTableFilaMouseClicked(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jTableFilaMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jTableFilaMouseReleased(evt);
            }
        });
        jTableFila.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTableFilaKeyPressed(evt);
            }
        });
        jScrollPaneFila.setViewportView(jTableFila);

        jPanel_Esquerdo.add(jScrollPaneFila, java.awt.BorderLayout.CENTER);

        jPanel6.setLayout(new javax.swing.BoxLayout(jPanel6, javax.swing.BoxLayout.Y_AXIS));

        jPanelProgress.setPreferredSize(new java.awt.Dimension(148, 20));
        jPanelProgress.setLayout(new java.awt.BorderLayout());
        jPanelProgress.add(jProgressBarImportando, java.awt.BorderLayout.CENTER);

        jPanel6.add(jPanelProgress);

        jPanel7.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 2));

        jLabel3.setText("M�sicas na Fila: ");
        jPanel7.add(jLabel3);

        jLabelQtdMusicasFila.setText("0");
        jPanel7.add(jLabelQtdMusicasFila);

        jPanel6.add(jPanel7);

        jPanel_Esquerdo.add(jPanel6, java.awt.BorderLayout.SOUTH);

        getContentPane().add(jPanel_Esquerdo, java.awt.BorderLayout.WEST);

        jPanel8.setLayout(new java.awt.BorderLayout());

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder("Tocando Agora..."));
        jPanel5.setLayout(new java.awt.BorderLayout());

        jLabelTocando.setFont(new java.awt.Font("Dialog", 0, 11));
        jLabelTocando.setText("Nada...");
        jLabelTocando.setPreferredSize(new java.awt.Dimension(0, 18));
        jPanel5.add(jLabelTocando, java.awt.BorderLayout.CENTER);

        jPanel8.add(jPanel5, java.awt.BorderLayout.PAGE_START);

        jPanel9.setLayout(new java.awt.BorderLayout());

        jPanelControles.setPreferredSize(new java.awt.Dimension(250, 36));
        jPanelControles.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 3, 2));

        jButton_Play.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jButton_Play.setText("Tocar");
        jButton_Play.setToolTipText("Tocar");
        jButton_Play.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        jButton_Play.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jButton_Play.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButton_PlayMouseClicked(evt);
            }
        });
        jPanelControles.add(jButton_Play);

        jButton_Stop.setText("Parar");
        jButton_Stop.setToolTipText("Parar");
        jButton_Stop.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jButton_Stop.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButton_StopMouseClicked(evt);
            }
        });
        jPanelControles.add(jButton_Stop);

        jButton_Next.setText("Avan�ar");
        jButton_Next.setToolTipText("Avan�ar");
        jButton_Next.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jButton_Next.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButton_NextMouseClicked(evt);
            }
        });
        jPanelControles.add(jButton_Next);

        jToggle_Repete.setText("Repeat");
        jToggle_Repete.setToolTipText("Repetir Musica");
        jToggle_Repete.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jToggle_RepeteMouseClicked(evt);
            }
        });
        jPanelControles.add(jToggle_Repete);

        jPanel9.add(jPanelControles, java.awt.BorderLayout.WEST);

        jPanel11.setLayout(new javax.swing.BoxLayout(jPanel11, javax.swing.BoxLayout.LINE_AXIS));

        jSlider_Tempo.setBackground(new java.awt.Color(255, 255, 255));
        jSlider_Tempo.setMaximum(1000);
        jSlider_Tempo.setToolTipText("");
        jSlider_Tempo.setValue(0);
        jSlider_Tempo.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jSlider_Tempo.setMinimumSize(new java.awt.Dimension(36, 14));
        jSlider_Tempo.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jSlider_TempoMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jSlider_TempoMouseReleased(evt);
            }
        });
        jPanel11.add(jSlider_Tempo);

        jPanel9.add(jPanel11, java.awt.BorderLayout.CENTER);

        jPanel8.add(jPanel9, java.awt.BorderLayout.CENTER);

        getContentPane().add(jPanel8, java.awt.BorderLayout.PAGE_START);

        jMenu1.setText("Importar");

        jMenuItem2.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_B, java.awt.event.InputEvent.ALT_MASK));
        jMenuItem2.setText("Mostrar Biblioteca");
        jMenu1.add(jMenuItem2);

        jMenuBar1.add(jMenu1);

        jMenu2.setText("Edtar");

        jMenuItem1.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_C, java.awt.event.InputEvent.ALT_MASK));
        jMenuItem1.setText("Configura��es");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem1);

        jMenuBar1.add(jMenu2);

        setJMenuBar(jMenuBar1);

        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        setBounds((screenSize.width-800)/2, (screenSize.height-600)/2, 800, 600);
    }// </editor-fold>//GEN-END:initComponents

    private void jTableMusicasMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableMusicasMouseClicked
        if (evt.getButton() == 1 && evt.getClickCount() > 1) {
            tocarMusicaSelecionada();
//            alterarMusica(musica);
        }
        if (evt.getButton() == 3) {
            jPopupMenuMusica.show(jTableMusicas, evt.getX(), evt.getY());
        }
    }//GEN-LAST:event_jTableMusicasMouseClicked

    private void jTextFieldPesquisaKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldPesquisaKeyTyped
//        for (int i = 0; i < objModelMusicas.getRowCount(); i++) {
//            Musica musica = objModelMusicas.getItem(i);
//            if (musica.getNome().contains(text)) {
//
//            }
//        }
    }//GEN-LAST:event_jTextFieldPesquisaKeyTyped

    private void jTableFilaMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableFilaMouseClicked
        if (evt.getClickCount() == 1 && evt.getButton() == 2) {
            removerMusicasSelecionadasFila();
        } else if (evt.getClickCount() == 2) {
            tocarMusicaSelecionadaFila();
//            alterarMusica(musica);
        }
    }//GEN-LAST:event_jTableFilaMouseClicked

    private void jTextFieldPesquisaKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldPesquisaKeyReleased
        String text = jTextFieldPesquisa.getText();
        objModelMusicas.setFiltro(text);
        repaint();
    }//GEN-LAST:event_jTextFieldPesquisaKeyReleased

    private void jTableMusicasKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTableMusicasKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            tocarMusicaSelecionada();
        } else if (evt.getKeyCode() == KeyEvent.VK_F5) {
            atualizaTabelaMusica();
        }
    }//GEN-LAST:event_jTableMusicasKeyPressed

    private void jTableFilaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTableFilaKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            tocarMusicaSelecionadaFila();
        }
    }//GEN-LAST:event_jTableFilaKeyPressed

    private void jMenuItemFilaTocarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemFilaTocarActionPerformed
        tocarMusicaSelecionada();
    }//GEN-LAST:event_jMenuItemFilaTocarActionPerformed

    private void jTableFilaMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableFilaMousePressed
        if (evt.getButton() != MouseEvent.BUTTON1) {
            int linha = jTableFila.rowAtPoint(evt.getPoint());
            int[] selectedRows = jTableFila.getSelectedRows();
            for (int i = 0; i < selectedRows.length; i++) {
                int linhaSelecionada = selectedRows[i];
                if (linha == linhaSelecionada) {
                    return;
                }
            }
            jTableFila.setRowSelectionInterval(linha, linha);
        }
    }//GEN-LAST:event_jTableFilaMousePressed

    private void jMenuItemFilaRemoverActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemFilaRemoverActionPerformed
        removerMusicasSelecionadasFila();
    }//GEN-LAST:event_jMenuItemFilaRemoverActionPerformed

    private void jTableFilaMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableFilaMouseReleased
        if (evt.getButton() == MouseEvent.BUTTON3) {
            jPopupMenuFila.show(jTableFila, evt.getX(), evt.getY());
        }
    }//GEN-LAST:event_jTableFilaMouseReleased

    private void jMenuItemFilaMoveCimaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemFilaMoveCimaActionPerformed
        int[] linhas = jTableFila.getSelectedRows();
        alterarPosicaoMusicasFila(linhas, linhas[0] - 1);
    }//GEN-LAST:event_jMenuItemFilaMoveCimaActionPerformed

    private void jMenuItemFilaMoveBaixoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemFilaMoveBaixoActionPerformed
        int[] linhas = jTableFila.getSelectedRows();
        alterarPosicaoMusicasFila(linhas, linhas[0] + 1);
    }//GEN-LAST:event_jMenuItemFilaMoveBaixoActionPerformed

    private void jButtonLimparPesquisaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonLimparPesquisaActionPerformed
        jTextFieldPesquisa.setText("");
        objModelMusicas.setFiltro("");
        atualizaTabelaMusica();
    }//GEN-LAST:event_jButtonLimparPesquisaActionPerformed

    private void jTextFieldPesquisaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldPesquisaKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            objModelMusicas.setFiltro(jTextFieldPesquisa.getText());
            jTableMusicas.requestFocus();
        }
    }//GEN-LAST:event_jTextFieldPesquisaKeyPressed

    private void jButton_PlayMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton_PlayMouseClicked
        musiquera.tocarPausar();
    }//GEN-LAST:event_jButton_PlayMouseClicked

    private void jButton_StopMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton_StopMouseClicked
        musiquera.parar();
    }//GEN-LAST:event_jButton_StopMouseClicked

    private void jButton_NextMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton_NextMouseClicked
        musiquera.tocarProxima();
    }//GEN-LAST:event_jButton_NextMouseClicked

    private void jToggle_RepeteMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jToggle_RepeteMouseClicked
        carregador.setRepeat(!carregador.isRepeat());
        if (carregador.isRepeat()) {
            jToggle_Repete.setIcon(carregador.icones.repeatOnIcon32);
        } else {
            jToggle_Repete.setIcon(carregador.icones.repeatOffIcon32);
        }
    }//GEN-LAST:event_jToggle_RepeteMouseClicked

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        carregador.setPrincipalComoBase();
    }//GEN-LAST:event_formWindowClosing

    private void jSlider_TempoMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jSlider_TempoMousePressed
        ajustandoTempo = true;
    }//GEN-LAST:event_jSlider_TempoMousePressed

    private void jSlider_TempoMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jSlider_TempoMouseReleased
        musiquera.skipTo((double) jSlider_Tempo.getValue() / (double) jSlider_Tempo.getMaximum());
        ajustandoTempo = false;
    }//GEN-LAST:event_jSlider_TempoMouseReleased

    private void jMenuItemEmbaralharActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemEmbaralharActionPerformed
        embaralharFila();
    }//GEN-LAST:event_jMenuItemEmbaralharActionPerformed

    private void jMenuItemLimparActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemLimparActionPerformed
        limparFila();
    }//GEN-LAST:event_jMenuItemLimparActionPerformed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        new JConfiguracao(this, true).setVisible(true);
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void jMenuItemPastaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemPastaActionPerformed
        ComandosSO.abrirPasta(new File(objModelMusicas.getItem(jTableMusicas.getSelectedRow()).getCaminho()).getParent());
        //        try {
//            Runtime.getRuntime().exec("nautilus " + new File(objModelMusicas.getItem(jTableMusicas.getSelectedRow()).getCaminho()).getParent());
//        } catch (IOException ex) {
//            Logger.getLogger(JFilaReproducao.class.getName()).log(Level.SEVERE, null, ex);
//        }
    }//GEN-LAST:event_jMenuItemPastaActionPerformed

    private void jMenuItemAdicionarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemAdicionarActionPerformed
        int[] linhas = jTableMusicas.getSelectedRows();
        for (int i = 0; i < linhas.length; i++) {
            modelFila.addRow(new Object[]{objModelMusicas.getItem(linhas[i])});
        }
    }//GEN-LAST:event_jMenuItemAdicionarActionPerformed

    private void jMenuItemTocarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemTocarActionPerformed
        tocarMusicaSelecionada();
    }//GEN-LAST:event_jMenuItemTocarActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                new JFilaReproducao(null, null).setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonLimparPesquisa;
    private javax.swing.JLabel jButton_Next;
    private javax.swing.JLabel jButton_Play;
    private javax.swing.JLabel jButton_Stop;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabelQtdMusicas;
    private javax.swing.JLabel jLabelQtdMusicasFila;
    private javax.swing.JLabel jLabelTocando;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItemAdicionar;
    private javax.swing.JMenuItem jMenuItemEmbaralhar;
    private javax.swing.JMenuItem jMenuItemFilaMoveBaixo;
    private javax.swing.JMenuItem jMenuItemFilaMoveCima;
    private javax.swing.JMenuItem jMenuItemFilaRemover;
    private javax.swing.JMenuItem jMenuItemFilaTocar;
    private javax.swing.JMenuItem jMenuItemLimpar;
    private javax.swing.JMenuItem jMenuItemPasta;
    private javax.swing.JMenuItem jMenuItemTocar;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JPanel jPanelCentro;
    private javax.swing.JPanel jPanelControles;
    private javax.swing.JPanel jPanelProgress;
    private javax.swing.JPanel jPanelStatus;
    private javax.swing.JPanel jPanel_Esquerdo;
    private javax.swing.JPopupMenu jPopupMenuFila;
    private javax.swing.JPopupMenu jPopupMenuMusica;
    private javax.swing.JProgressBar jProgressBarImportando;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPaneFila;
    private javax.swing.JSlider jSlider_Tempo;
    private javax.swing.JTable jTableFila;
    private javax.swing.JTable jTableMusicas;
    private javax.swing.JTextField jTextFieldPesquisa;
    private javax.swing.JLabel jToggle_Repete;
    // End of variables declaration//GEN-END:variables
}
