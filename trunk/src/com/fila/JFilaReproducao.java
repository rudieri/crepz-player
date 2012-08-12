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

import com.config.Configuracaoes;
import com.config.JConfiguracao;
import com.config.constantes.AcaoPadraoFila;
import com.config.constantes.AcoesFilaVazia;
import com.config.constantes.AdicionarNaFilaVazia;
import com.main.Carregador;
import com.main.Notificavel;
import com.main.gui.JMP3Propriedades;
import com.musica.*;
import com.musica.Musiquera.PropriedadesMusica;
import com.utils.ComandosSO;
import com.utils.DiretorioUtils;
import com.utils.model.ModelReadOnly;
import com.utils.model.objetcmodel.ObjectTableModel;
import com.utils.model.objetcmodel.ObjectTableModelListener;
import com.utils.model.objetcmodel.ObjectTransferable;
import com.utils.pele.ColorUtils;
import com.utils.transferivel.TipoTransferenciaMusica;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.filechooser.FileFilter;
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

    /**
     * Creates new form JFilaReproducao
     */
    public JFilaReproducao(Musiquera musiquera, Carregador carregador) {
        initComponents();
        this.setIconImage(new ImageIcon(getClass().getResource("/com/img/icon.png")).getImage());
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

        jFileChooserImportar.setFileFilter(new FileFilter() {

            @Override
            public boolean accept(File f) {
                return f.isDirectory() || MusicaGerencia.ehValido(f);
            }

            @Override
            public String getDescription() {
                return "Arquivos de músicas ou pastas";
            }
        });
        jFileChooserImportar.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        jTextFieldPesquisa.requestFocus();
    }

    private void adicionarMusicasSelecionadas() {
        int[] linhas = jTableMusicas.getSelectedRows();
        for (int i = 0; i < linhas.length; i++) {
            modelFila.addRow(new Object[]{objModelMusicas.getItem(linhas[i])});
        }
        atualizarBarraStausFila();
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
        int fim;
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
                    if (!musiquera.isPlaying()
                            && Configuracaoes.getEnum(Configuracaoes.CONF_ADICIONAR_NA_FILA_VAZIA) == AdicionarNaFilaVazia.REPRODUZIR_MUSICA) {
                        musiquera.abrirETocar();
                    }
                }
            }
        });
        jTableFila.setDefaultRenderer(Object.class, new TableRenderFila());
        jTableFila.setRowHeight(50);
        jTableFila.getTableHeader().setVisible(false);
        // Código do Cão...

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
                return new ObjectTransferable(musicas, TipoTransferenciaMusica.JFILA_FILA);

            }
        });
        dropTargetFila = new DropTarget(jScrollPaneFila, new DropTargetAdapter() {

            @Override
            public void drop(DropTargetDropEvent dtde) {
                try {
                    Point location = dtde.getLocation();
                    int posicaoDestino = -1;
                    if (jTableFila.getSelectedRows().length > 0) {
                        Point visibleLocation = jTableFila.getVisibleRect().getLocation();
                        visibleLocation.x += location.x;
                        visibleLocation.y += location.y;
                        posicaoDestino = jTableFila.rowAtPoint(visibleLocation);

                        if ((dtde.getSourceActions() == TransferHandler.COPY_OR_MOVE
                                || location.y < jTableFila.getHeight())) {

                            if (TipoTransferenciaMusica.forDataFlavor(dtde.getCurrentDataFlavors()) == TipoTransferenciaMusica.JFILA_FILA) {
                                alterarPosicaoMusicasFila(jTableFila.getSelectedRows(), posicaoDestino);
                                return;
                            }
                        }
                    }
                    Transferable transferable = dtde.getTransferable();
                    DataFlavor[] flavors = transferable.getTransferDataFlavors();
                    Object data = null;
                    try {
                        data = transferable.getTransferData(transferable.getTransferDataFlavors()[0]);
                    } catch (Exception ex) {
                        // irá cair no drop do linux e la encontrará alguns arquivos :D
                        Logger.getLogger(JFilaReproducao.class.getName()).log(Level.SEVERE, "Crepz tratavel...", ex);
                    }
                    if (data != null && data instanceof Musica) {
                        if (posicaoDestino != -1) {
                            ((ModelReadOnly) jTableFila.getModel()).insertRow(posicaoDestino, new Object[]{data});
                        } else {
                            ((ModelReadOnly) jTableFila.getModel()).addRow(new Object[]{data});
                        }
                    } else if (data != null && data instanceof ArrayList) {
                        addMusicasToFila(data, posicaoDestino);
                    } else {
                        //Windows
                        if (transferable.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                            ArrayList<File> arquivos = (ArrayList) transferable.getTransferData(java.awt.datatransfer.DataFlavor.javaFileListFlavor);
                            importarMusicas(arquivos, true);
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
                                    importarMusicas(arquivos, true);
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

    private void importarMusicas(final ArrayList<File> arquivos, final boolean adicionarFila) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                importarMusicasRun(arquivos, adicionarFila);
            }
        }).start();
    }

    private void importarMusicasRun(ArrayList<File> arquivos, boolean adicionarFila) {
        try {
            int nroFiles = DiretorioUtils.calculaQuantidadeArquivos(arquivos);
            jPanelProgress.setVisible(true);
            ArrayList musicasImportadas = new ArrayList<Musica>(nroFiles);
            MusicaGerencia.mapearDiretorio(arquivos, musicasImportadas, jProgressBarImportando, nroFiles);
            if (adicionarFila) {
                addMusicasToFila(musicasImportadas);
            }
            jPanelProgress.setVisible(false);
            atualizaTabelaMusica();
        } catch (Exception ex) {
            Logger.getLogger(JFilaReproducao.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void addMusicasToFila(Object data, int posicao) {
        addMusicasToFila((ArrayList) data, posicao);
    }

    private void addMusicasToFila(ArrayList<Musica> data) {
        addMusicasToFila(data, -1);
    }

    private void addMusicasToFila(ArrayList<Musica> data, int posicao) {
        for (int i = 0; i < data.size(); i++) {
            Musica musica = data.get(i);
            if (posicao == -1) {
                ((ModelReadOnly) jTableFila.getModel()).addRow(new Object[]{musica});
            } else {
                ((ModelReadOnly) jTableFila.getModel()).insertRow(posicao, new Object[]{musica});
            }
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
        final Comparator comparator = new Comparator() {

            @Override
            public int compare(Object o1, Object o2) {
                if (o1 instanceof Comparable) {
                    return ((Comparable) o1).compareTo(o2);
                } else {
                    return o1.toString().compareTo(o2.toString());
                }
            }
        };
        for (int i = 0; i < jTableMusicas.getColumnCount(); i++) {
            sorterMusicas.setComparator(i, comparator);
        }
        ColorUtils.registrar(jTableMusicas);
        objModelMusicas.addTableModelListener(new TableModelListener() {

            @Override
            public void tableChanged(TableModelEvent e) {
                for (int i = 0; i < jTableMusicas.getColumnCount(); i++) {
                    sorterMusicas.setComparator(i, comparator);
                }
            }
        });
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
                return new ObjectTransferable(musicas, TipoTransferenciaMusica.JFILA_MUSICA);

            }
        });
        jTableMusicas.setDragEnabled(true);
        jTableMusicas.setRowHeight(20);
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

    public Musica getAnterior() {
        if (modelFila.getRowCount() == 0) {
            if (Configuracaoes.getEnum(Configuracaoes.CONF_ACOES_FILA_VAZIA) == AcoesFilaVazia.TOCAR_SEQ) {
                int selectedRow = jTableMusicas.getSelectedRow() - 1;
                if (selectedRow < 0) {
                    selectedRow = jTableMusicas.getRowCount() - 1;
                }
                jTableMusicas.setRowSelectionInterval(selectedRow, selectedRow);
                return objModelMusicas.getItem(selectedRow);
            }

        }
        return null;
    }

    public Musica getProxima() {
        if (modelFila.getRowCount() > 0) {
            Musica musica = (Musica) modelFila.getValueAt(0, 0);
            modelFila.removeRow(0);
            atualizarBarraStausFila();
//            alterarMusica(musica);
            return musica;
        } else if (Configuracaoes.getEnum(Configuracaoes.CONF_ACOES_FILA_VAZIA) != AcoesFilaVazia.NADA) {
            return getMusicaDaLista();
        }
        return null;
    }

    private Musica getMusicaDaLista() {
        if (objModelMusicas.getRowCount() == 0) {
            return null;
        }
        Rectangle rect = jTableMusicas.getVisibleRect();
        if (Configuracaoes.getEnum(Configuracaoes.CONF_ACOES_FILA_VAZIA) == AcoesFilaVazia.TOCAR_SEQ) {
            int selectedRow = jTableMusicas.getSelectedRow() + 1;
            if (selectedRow >= jTableMusicas.getRowCount()) {
                selectedRow = 0;
            }
            jTableMusicas.setRowSelectionInterval(selectedRow, selectedRow);
            rect.y = jTableMusicas.getCellRect(selectedRow, 0, true).y - rect.height / 2;
            jTableMusicas.scrollRectToVisible(rect);
            return objModelMusicas.getItem(selectedRow);
        } else {
            final int rand = (int) (Math.random() * objModelMusicas.getRowCount());
            jTableMusicas.setRowSelectionInterval(rand, rand);
            rect.y = jTableMusicas.getCellRect(rand, 0, true).y - rect.height / 2;
            jTableMusicas.scrollRectToVisible(rect);
            return objModelMusicas.getItem(rand);
        }
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
            System.out.println("Não aparece no filtro...");
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

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
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
        jMenu3 = new javax.swing.JMenu();
        jMenuItemAdicionarListaNova = new javax.swing.JMenuItem();
        jMenuItemAdicionarListaExistente = new javax.swing.JMenuItem();
        jMenuItemEditar = new javax.swing.JMenuItem();
        jFileChooserImportar = new javax.swing.JFileChooser();
        jSplitPane1 = new javax.swing.JSplitPane();
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
        jMenuItemAbrirBiblioteca = new javax.swing.JMenuItem();
        jMenuItemMostrarPlayList = new javax.swing.JMenuItem();
        jMenuItemImportarArquivos = new javax.swing.JMenuItem();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuItem3 = new javax.swing.JMenuItem();

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

        jMenuItemTocar.setMnemonic('R');
        jMenuItemTocar.setText("Reproduzir");
        jMenuItemTocar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemTocarActionPerformed(evt);
            }
        });
        jPopupMenuMusica.add(jMenuItemTocar);

        jMenuItemAdicionar.setMnemonic('F');
        jMenuItemAdicionar.setText("Adicionar na Fila");
        jMenuItemAdicionar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemAdicionarActionPerformed(evt);
            }
        });
        jPopupMenuMusica.add(jMenuItemAdicionar);

        jMenuItemPasta.setMnemonic('p');
        jMenuItemPasta.setText("Abrir Pasta da Música");
        jMenuItemPasta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemPastaActionPerformed(evt);
            }
        });
        jPopupMenuMusica.add(jMenuItemPasta);

        jMenu3.setMnemonic('L');
        jMenu3.setText("Lista de Reprodução");

        jMenuItemAdicionarListaNova.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_P, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        jMenuItemAdicionarListaNova.setMnemonic('n');
        jMenuItemAdicionarListaNova.setText("Adicionar a nova lista");
        jMenuItemAdicionarListaNova.setEnabled(false);
        jMenu3.add(jMenuItemAdicionarListaNova);

        jMenuItemAdicionarListaExistente.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_P, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItemAdicionarListaExistente.setMnemonic('E');
        jMenuItemAdicionarListaExistente.setText("Adicionar lista exestente");
        jMenuItemAdicionarListaExistente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemAdicionarListaExistenteActionPerformed(evt);
            }
        });
        jMenu3.add(jMenuItemAdicionarListaExistente);

        jPopupMenuMusica.add(jMenu3);

        jMenuItemEditar.setText("Propriedades");
        jMenuItemEditar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemEditarActionPerformed(evt);
            }
        });
        jPopupMenuMusica.add(jMenuItemEditar);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Fila de Reprodução");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jPanelCentro.setBorder(javax.swing.BorderFactory.createTitledBorder("Musicas"));
        jPanelCentro.setOpaque(false);
        jPanelCentro.setLayout(new java.awt.BorderLayout());

        jPanel2.setOpaque(false);
        jPanel2.setPreferredSize(new java.awt.Dimension(483, 32));
        jPanel2.setLayout(new javax.swing.BoxLayout(jPanel2, javax.swing.BoxLayout.LINE_AXIS));

        jPanel1.setOpaque(false);
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
        });
        jPanel3.add(jTextFieldPesquisa);

        jButtonLimparPesquisa.setMnemonic('L');
        jButtonLimparPesquisa.setText("Limpar");
        jButtonLimparPesquisa.setToolTipText("Limpar filtro. (Alt + L)");
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
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jTableMusicasMousePressed(evt);
            }
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

        jPanelStatus.setOpaque(false);
        jPanelStatus.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 2));

        jLabel2.setText("Numero de Músicas:");
        jPanelStatus.add(jLabel2);

        jLabelQtdMusicas.setText("0");
        jPanelStatus.add(jLabelQtdMusicas);

        jPanelCentro.add(jPanelStatus, java.awt.BorderLayout.PAGE_END);

        jSplitPane1.setRightComponent(jPanelCentro);

        jPanel_Esquerdo.setBorder(javax.swing.BorderFactory.createTitledBorder("Fila de Reprodução"));
        jPanel_Esquerdo.setOpaque(false);
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
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jTableFilaMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jTableFilaMouseReleased(evt);
            }
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTableFilaMouseClicked(evt);
            }
        });
        jTableFila.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTableFilaKeyPressed(evt);
            }
        });
        jScrollPaneFila.setViewportView(jTableFila);

        jPanel_Esquerdo.add(jScrollPaneFila, java.awt.BorderLayout.CENTER);

        jPanel6.setOpaque(false);
        jPanel6.setLayout(new javax.swing.BoxLayout(jPanel6, javax.swing.BoxLayout.Y_AXIS));

        jPanelProgress.setOpaque(false);
        jPanelProgress.setPreferredSize(new java.awt.Dimension(148, 20));
        jPanelProgress.setLayout(new java.awt.BorderLayout());
        jPanelProgress.add(jProgressBarImportando, java.awt.BorderLayout.CENTER);

        jPanel6.add(jPanelProgress);

        jPanel7.setOpaque(false);
        jPanel7.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 2));

        jLabel3.setText("Músicas na Fila: ");
        jPanel7.add(jLabel3);

        jLabelQtdMusicasFila.setText("0");
        jPanel7.add(jLabelQtdMusicasFila);

        jPanel6.add(jPanel7);

        jPanel_Esquerdo.add(jPanel6, java.awt.BorderLayout.SOUTH);

        jSplitPane1.setLeftComponent(jPanel_Esquerdo);

        getContentPane().add(jSplitPane1, java.awt.BorderLayout.CENTER);

        jPanel8.setOpaque(false);
        jPanel8.setLayout(new java.awt.BorderLayout());

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder("Tocando Agora..."));
        jPanel5.setOpaque(false);
        jPanel5.setLayout(new java.awt.BorderLayout());

        jLabelTocando.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N
        jLabelTocando.setText("Nada...");
        jLabelTocando.setPreferredSize(new java.awt.Dimension(0, 18));
        jPanel5.add(jLabelTocando, java.awt.BorderLayout.CENTER);

        jPanel8.add(jPanel5, java.awt.BorderLayout.PAGE_START);

        jPanel9.setOpaque(false);
        jPanel9.setLayout(new java.awt.BorderLayout());

        jPanelControles.setOpaque(false);
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

        jButton_Next.setText("Avançar");
        jButton_Next.setToolTipText("Avançar");
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

        jPanel11.setOpaque(false);
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

        jMenu1.setText("Atividades");

        jMenuItemAbrirBiblioteca.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_B, java.awt.event.InputEvent.ALT_MASK));
        jMenuItemAbrirBiblioteca.setText("Mostrar Biblioteca");
        jMenuItemAbrirBiblioteca.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemAbrirBibliotecaActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItemAbrirBiblioteca);

        jMenuItemMostrarPlayList.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_R, java.awt.event.InputEvent.ALT_MASK));
        jMenuItemMostrarPlayList.setText("Mostrar Lista de Reprodução");
        jMenuItemMostrarPlayList.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemMostrarPlayListActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItemMostrarPlayList);

        jMenuItemImportarArquivos.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_I, java.awt.event.InputEvent.ALT_MASK));
        jMenuItemImportarArquivos.setText("Importar Músicas...");
        jMenuItemImportarArquivos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemImportarArquivosActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItemImportarArquivos);

        jMenuItem2.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Q, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem2.setText("Sair");
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem2);

        jMenuBar1.add(jMenu1);

        jMenu2.setText("Editar");

        jMenuItem1.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_C, java.awt.event.InputEvent.ALT_MASK));
        jMenuItem1.setText("Configurações");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem1);

        jMenuItem3.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_T, java.awt.event.InputEvent.ALT_MASK));
        jMenuItem3.setText("Tema");
        jMenuItem3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem3ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem3);

        jMenuBar1.add(jMenu2);

        setJMenuBar(jMenuBar1);

        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        setBounds((screenSize.width-800)/2, (screenSize.height-600)/2, 800, 600);
    }// </editor-fold>//GEN-END:initComponents

    private void jTableMusicasMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableMusicasMouseClicked
        if (evt.getButton() == 1 && evt.getClickCount() > 1) {
            final AcaoPadraoFila acaoPadraoFila = (AcaoPadraoFila) Configuracaoes.getObject(Configuracaoes.CONF_ACAO_PADRAO_FILA);
            if (acaoPadraoFila == AcaoPadraoFila.ADICIONAR_FILA) {
                adicionarMusicasSelecionadas();
            } else if (acaoPadraoFila == AcaoPadraoFila.REPRODUZIR) {
                tocarMusicaSelecionada();
            } else {
                tocarMusicaSelecionada();
            }
        }
        if (evt.getButton() == 3) {
            jPopupMenuMusica.show(jTableMusicas, evt.getX(), evt.getY());
        }
    }//GEN-LAST:event_jTableMusicasMouseClicked

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
        if (evt.getModifiersEx() == KeyEvent.CTRL_DOWN_MASK) {
            switch (evt.getKeyCode()) {
                case KeyEvent.VK_A:
                case KeyEvent.VK_T:
                    jTableMusicas.selectAll();
                    evt.consume();
                    break;
                case KeyEvent.VK_ENTER:
                    final AcaoPadraoFila acaoPadraoFila = (AcaoPadraoFila) Configuracaoes.getObject(Configuracaoes.CONF_ACAO_PADRAO_FILA);
                    if (acaoPadraoFila != AcaoPadraoFila.ADICIONAR_FILA) {
                        adicionarMusicasSelecionadas();
                    } else if (acaoPadraoFila != AcaoPadraoFila.REPRODUZIR) {
                        tocarMusicaSelecionada();
                    } else {
                        tocarMusicaSelecionada();
                    }
                    evt.consume();
                    break;
            }
        } else if (evt.getModifiersEx() == KeyEvent.ALT_DOWN_MASK) {
            // nada por enquanto
        } else {
            final String textoPesquisa = jTextFieldPesquisa.getText();
            switch (evt.getKeyCode()) {
                case KeyEvent.VK_ENTER:
                    final AcaoPadraoFila acaoPadraoFila = (AcaoPadraoFila) Configuracaoes.getObject(Configuracaoes.CONF_ACAO_PADRAO_FILA);
                    if (acaoPadraoFila == AcaoPadraoFila.ADICIONAR_FILA) {
                        adicionarMusicasSelecionadas();
                    } else if (acaoPadraoFila == AcaoPadraoFila.REPRODUZIR) {
                        tocarMusicaSelecionada();
                    } else {
                        tocarMusicaSelecionada();
                    }
                    evt.consume();
                    break;
                case KeyEvent.VK_F5:
                    atualizaTabelaMusica();
                    jTextFieldPesquisa.requestFocus();
                    evt.consume();
                    break;
                case KeyEvent.VK_CONTEXT_MENU:
                    final Rectangle cellRect = jTableMusicas.getCellRect(jTableMusicas.getSelectedRow(), 0, true);
                    jPopupMenuMusica.show(jTableMusicas, jTableMusicas.getWidth() / 2, cellRect.y);
                    evt.consume();
                    break;
                case KeyEvent.VK_BACK_SPACE:
                    if (textoPesquisa.isEmpty()) {
                        jTextFieldPesquisa.requestFocus();
                    } else {
                        jTextFieldPesquisa.setText(textoPesquisa.substring(0, textoPesquisa.length() - 1));
                    }
                    evt.consume();
                    break;
            }

            if (evt.getKeyCode() > KeyEvent.VK_A && evt.getKeyCode() < KeyEvent.VK_Z) {
                jTextFieldPesquisa.setText(String.valueOf(evt.getKeyChar()));
                objModelMusicas.setFiltro(textoPesquisa);
                jTextFieldPesquisa.requestFocus();
            }
        }
    }//GEN-LAST:event_jTableMusicasKeyPressed

    private void jTableFilaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTableFilaKeyPressed
        switch (evt.getKeyCode()) {
            case KeyEvent.VK_ENTER:
                tocarMusicaSelecionadaFila();
                break;
            case KeyEvent.VK_DELETE:
                removerMusicasSelecionadasFila();
                break;
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
        jTextFieldPesquisa.requestFocus();
    }//GEN-LAST:event_jButtonLimparPesquisaActionPerformed

    private void jTextFieldPesquisaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldPesquisaKeyPressed

        switch (evt.getKeyCode()) {
            case KeyEvent.VK_ENTER:
                objModelMusicas.setFiltro(jTextFieldPesquisa.getText());
                jTableMusicas.requestFocus();
                break;
            case KeyEvent.VK_DOWN:
                jTableMusicas.requestFocus();
                jTableMusicas.setRowSelectionInterval(0, 0);
                break;
            case KeyEvent.VK_F5:
                atualizaTabelaMusica();
                break;
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
        adicionarMusicasSelecionadas();
    }//GEN-LAST:event_jMenuItemAdicionarActionPerformed

    private void jMenuItemTocarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemTocarActionPerformed
        tocarMusicaSelecionada();
    }//GEN-LAST:event_jMenuItemTocarActionPerformed

    private void jTableMusicasMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableMusicasMousePressed
        if (evt.getButton() != MouseEvent.BUTTON1) {
            int linha = jTableMusicas.rowAtPoint(evt.getPoint());
            int[] selectedRows = jTableMusicas.getSelectedRows();
            for (int i = 0; i < selectedRows.length; i++) {
                int linhaSelecionada = selectedRows[i];
                if (linha == linhaSelecionada) {
                    return;
                }
            }
            jTableMusicas.setRowSelectionInterval(linha, linha);
        }
    }//GEN-LAST:event_jTableMusicasMousePressed

    private void jMenuItemAbrirBibliotecaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemAbrirBibliotecaActionPerformed
        carregador.mostrarBiblioteca();
    }//GEN-LAST:event_jMenuItemAbrirBibliotecaActionPerformed

    private void jMenuItemImportarArquivosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemImportarArquivosActionPerformed
        int result = jFileChooserImportar.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            ArrayList<File> lista = new ArrayList<File>(2000);
            lista.addAll(Arrays.asList(jFileChooserImportar.getSelectedFiles()));
            if (lista.isEmpty()) {
                lista.add(jFileChooserImportar.getSelectedFile());
            }
            importarMusicas(lista, false);
        }
    }//GEN-LAST:event_jMenuItemImportarArquivosActionPerformed

    private void jMenuItemAdicionarListaExistenteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemAdicionarListaExistenteActionPerformed
        final int[] selectedRows = jTableMusicas.getSelectedRows();
        if (selectedRows.length == 0) {
            return;
        }
        ArrayList<Musica> lista = new ArrayList<Musica>(selectedRows.length);
        for (int i = 0; i < selectedRows.length; i++) {
            lista.add(objModelMusicas.getItem(selectedRows[i]));
        }
        carregador.addToPlayList(lista);
    }//GEN-LAST:event_jMenuItemAdicionarListaExistenteActionPerformed

    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
        carregador.sair();
    }//GEN-LAST:event_jMenuItem2ActionPerformed

    private void jMenuItemMostrarPlayListActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemMostrarPlayListActionPerformed
        carregador.mostrarPlayList();
        carregador.setFonteReproducao(Carregador.FonteReproducao.PLAY_LIST);
    }//GEN-LAST:event_jMenuItemMostrarPlayListActionPerformed

    private void jMenuItem3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem3ActionPerformed
        carregador.mostrarModificadorDeTema();
    }//GEN-LAST:event_jMenuItem3ActionPerformed

    private void jMenuItemEditarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemEditarActionPerformed
        try {
            Musica musica = objModelMusicas.getItem(jTableMusicas.getSelectedRow());
            new JMP3Propriedades(this, true, musica).setVisible(true);
            objModelMusicas.atualizarItem(musica, jTableMusicas.getSelectedRow());
        } catch (Exception ex) {
            Logger.getLogger(JFilaReproducao.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jMenuItemEditarActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {

        /*
         * Create and display the form
         */
        java.awt.EventQueue.invokeLater(new Runnable() {

            @Override
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
    private javax.swing.JFileChooser jFileChooserImportar;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabelQtdMusicas;
    private javax.swing.JLabel jLabelQtdMusicasFila;
    private javax.swing.JLabel jLabelTocando;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JMenuItem jMenuItemAbrirBiblioteca;
    private javax.swing.JMenuItem jMenuItemAdicionar;
    private javax.swing.JMenuItem jMenuItemAdicionarListaExistente;
    private javax.swing.JMenuItem jMenuItemAdicionarListaNova;
    private javax.swing.JMenuItem jMenuItemEditar;
    private javax.swing.JMenuItem jMenuItemEmbaralhar;
    private javax.swing.JMenuItem jMenuItemFilaMoveBaixo;
    private javax.swing.JMenuItem jMenuItemFilaMoveCima;
    private javax.swing.JMenuItem jMenuItemFilaRemover;
    private javax.swing.JMenuItem jMenuItemFilaTocar;
    private javax.swing.JMenuItem jMenuItemImportarArquivos;
    private javax.swing.JMenuItem jMenuItemLimpar;
    private javax.swing.JMenuItem jMenuItemMostrarPlayList;
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
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JTable jTableFila;
    private javax.swing.JTable jTableMusicas;
    private javax.swing.JTextField jTextFieldPesquisa;
    private javax.swing.JLabel jToggle_Repete;
    // End of variables declaration//GEN-END:variables
}
