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
import com.main.FonteReproducao;
import com.main.Notificavel;
import com.main.gui.JMP3Propriedades;
import com.musica.*;
import com.musica.Musiquera.PropriedadesMusica;
import com.utils.ComandosSO;
import com.utils.file.DiretorioUtils;
import com.utils.file.filtros.FiltroMusica;
import com.utils.model.ModelReadOnly;
import com.utils.model.tablemodel.ObjectTableModel;
import com.utils.model.tablemodel.ObjectTableModelListener;
import com.utils.model.tablemodel.ObjectTransferable;
import com.utils.renderer.DefaultTableRenderer;
import com.utils.transferivel.TipoTransferenciaMusica;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowListener;
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
import javax.swing.table.TableRowSorter;

/**
 *
 * @author rudieri
 */
public class JFilaReproducao extends javax.swing.JFrame implements Notificavel, ActionListener, MouseListener, TableModelListener, ObjectTableModelListener, KeyListener, WindowListener {

    private ObjectTableModel<Musica> objModelMusicas;
    private ModelReadOnly modelFila;
    private final Carregador carregador;
    private TableRowSorter sorterMusicas;
    private DropTarget dropTargetFila;
    private boolean ajustandoTempo;

    /**
     * Creates new form JFilaReproducao
     * @param carregador 
     */
    public JFilaReproducao(Carregador carregador) {
        initComponents();
        this.setIconImage(new ImageIcon(getClass().getResource("/com/img/icon.png")).getImage());
        jPanelProgress.setVisible(false);
        initTabelaFila();
        initTabelaMusica();
        atualizaTabelaMusica();

        this.carregador = carregador;
        inicializaIcones();

        jFileChooserImportar.setFileFilter(FiltroMusica.getInstance());
        jFileChooserImportar.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        jTextFieldPesquisa.requestFocus();
        startEvents();
    }

    private void adicionarMusicasSelecionadas() {
        int[] linhas = jTableMusicas.getSelectedRows();
        for (int i = 0; i < linhas.length; i++) {
            modelFila.addRow(new Object[]{objModelMusicas.getItem(converterIndiceTabelaMusica(linhas[i]))});
        }
        atualizarBarraStausFila();
    }

    private int converterIndiceTabelaMusica(int linha) {
        return jTableMusicas.getRowSorter().convertRowIndexToModel(linha);
    }

    private void tocarMusicaSelecionada() {
        Musica musica = objModelMusicas.getItem(converterIndiceTabelaMusica(jTableMusicas.getSelectedRow()));
        carregador.abrir(musica, 0, false);
    }

    private void tocarMusicaSelecionadaFila() {
        Musica musica = (Musica) modelFila.getValueAt(jTableFila.getSelectedRow(), 0);
        modelFila.removeRow(jTableFila.getSelectedRow());
        carregador.abrir(musica, 0, false);
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
        modelFila.addTableModelListener(this);
        jTableFila.setDefaultRenderer(Object.class, new TableRendererFila());
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
                    if (data != null && data.getClass() == Musica.class) {
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

        jButton_Stop.setIcon(carregador.getIcones().getStopIcon32());
//        jButton_Ant.setIcon(carregador.getIcones().voltaIcon32);
        jButton_Next.setIcon(carregador.getIcones().getFrenteIcon32());
        if (carregador.isPaused()) {
            jButton_Play.setIcon(carregador.getIcones().getPlayIcon32());
        } else {
            jButton_Play.setIcon(carregador.getIcones().getPauseIcon32());
        }
        if (carregador.isRepeat()) {
            jToggle_Repete.setIcon(carregador.getIcones().getRepeatOnIcon32());
        } else {
            jToggle_Repete.setIcon(carregador.getIcones().getRepeatOffIcon32());
        }

        // menus popup
        jMenuItemFilaTocar.setIcon(carregador.getIcones().getPlayIcon16());
        jMenuItemFilaRemover.setIcon(carregador.getIcones().getXis());
        jMenuItemEmbaralhar.setIcon(carregador.getIcones().getRandomOnIcon16());

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
        objModelMusicas.addObjectTableModelListener(this);
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
//        ColorUtils.registrar(jTableMusicas);
        objModelMusicas.addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                for (int i = 0; i < jTableMusicas.getColumnCount(); i++) {
                    sorterMusicas.setComparator(i, comparator);
                }
            }
        });
        jTableMusicas.setDefaultRenderer(Object.class, new DefaultTableRenderer());
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
                    musicas.add(objModelMusicas.getItem(converterIndiceTabelaMusica(rows[i])));
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
                return objModelMusicas.getItem(converterIndiceTabelaMusica(selectedRow));
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

        if (Configuracaoes.getEnum(Configuracaoes.CONF_ACOES_FILA_VAZIA) == AcoesFilaVazia.TOCAR_SEQ) {
            int selectedRow = jTableMusicas.getSelectedRow() + 1;
            if (selectedRow >= jTableMusicas.getRowCount()) {
                selectedRow = 0;
            }
            return getMusicaDaLista(selectedRow);
        } else {
            final int rand = (int) (Math.random() * jTableMusicas.getRowCount());
            return getMusicaDaLista(rand);
        }
    }

    private Musica getMusicaDaLista(int selectedRow) {
        Rectangle rect = jTableMusicas.getVisibleRect();
        jTableMusicas.setRowSelectionInterval(selectedRow, selectedRow);
        rect.y = jTableMusicas.getCellRect(selectedRow, 0, true).y - rect.height / 2;
        jTableMusicas.scrollRectToVisible(rect);
        return objModelMusicas.getItem(converterIndiceTabelaMusica(selectedRow));
    }
    
    public void selecionaMusica(Musica musica){
        int indexOf = objModelMusicas.indexOf(musica);
        if (indexOf!=-1) {
            Rectangle rect = jTableMusicas.getVisibleRect();
            jTableMusicas.setRowSelectionInterval(indexOf, indexOf);
            rect.y = jTableMusicas.getCellRect(indexOf, 0, true).y - rect.height / 2;
            jTableMusicas.scrollRectToVisible(rect);
        }
    }

    private void filtrarTabelaMusica(KeyEvent evt) {
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
    }

    @Override
    public void atualizaLabels(String nome, int bits, String tempoTotal, int freq) {
        // do nothing
    }

    @Override
    public void propriedadesMusicaChanged(PropriedadesMusica propriedadesMusica) {
        jLabelTocando.setText(propriedadesMusica.getNome() + " - " + propriedadesMusica.getArtista());
        int indexOf = objModelMusicas.indexOf(carregador.getMusica());
        carregador.getMusica().setTempo(propriedadesMusica.getTempoTotal());
        setTitle(propriedadesMusica.getNome() + " - " + propriedadesMusica.getArtista());
        try {

            int convertRow = jTableMusicas.convertRowIndexToView(indexOf);
            if (convertRow != -1) {
                objModelMusicas.atualizarItem(carregador.getMusica(), indexOf, convertRow);
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
        jLabelTempo.setText(hms);

    }

    @Override
    public void setVisible(boolean b) {
        super.setVisible(b);
        atualizaTabelaMusica();
    }

    private void startEvents() {
        jMenuItemFilaTocar.addActionListener(this);
        jMenuItemFilaRemover.addActionListener(this);
        jMenuItemEmbaralhar.addActionListener(this);
        jMenuItemFilaMoveBaixo.addActionListener(this);
        jMenuItemFilaMoveCima.addActionListener(this);
        jMenuItemLimpar.addActionListener(this);
        jMenuItemTocar.addActionListener(this);
        jMenuItemPasta.addActionListener(this);
        jMenuItemAdicionarListaExistente.addActionListener(this);
        jMenuItemAdicionar.addActionListener(this);
        jMenuItemEditar.addActionListener(this);
        jButtonLimparPesquisa.addActionListener(this);
        jMenuItemAbrirBiblioteca.addActionListener(this);
        jMenuItemMostrarPlayList.addActionListener(this);
        jMenuItemImportarArquivos.addActionListener(this);
        jMenuItem1.addActionListener(this);
        jMenuItem2.addActionListener(this);
        jMenuItem3.addActionListener(this);
        jMenuItemVoltarTelaPrincipal.addActionListener(this);

        // mouse
        jTableMusicas.addMouseListener(this);
        jTableFila.addMouseListener(this);
        jButton_Play.addMouseListener(this);
        jButton_Next.addMouseListener(this);
        jButton_Stop.addMouseListener(this);
        jToggle_Repete.addMouseListener(this);
        jSlider_Tempo.addMouseListener(this);
        
        // key listener
        jTextFieldPesquisa.addKeyListener(this);
        jTableFila.addKeyListener(this);
        jTableMusicas.addKeyListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == jMenuItemFilaTocar) {
            tocarMusicaSelecionada();
        } else if (e.getSource() == jMenuItemFilaRemover) {
            removerMusicasSelecionadasFila();
        } else if (e.getSource() == jMenuItemEmbaralhar) {
            embaralharFila();
        } else if (e.getSource() == jMenuItemFilaMoveBaixo) {
            int[] linhas = jTableFila.getSelectedRows();
            alterarPosicaoMusicasFila(linhas, linhas[0] + 1);
        } else if (e.getSource() == jMenuItemFilaMoveCima) {
            int[] linhas = jTableFila.getSelectedRows();
            alterarPosicaoMusicasFila(linhas, linhas[0] - 1);
        } else if (e.getSource() == jMenuItemLimpar) {
            limparFila();
        } else if (e.getSource() == jMenuItemTocar) {
            tocarMusicaSelecionada();
        } else if (e.getSource() == jMenuItemPasta) {
            ComandosSO.abrirPasta(
                    new File(objModelMusicas.getItem(
                    converterIndiceTabelaMusica(jTableMusicas.getSelectedRow())).getCaminho()).getParent());
        } else if (e.getSource() == jMenuItemAdicionarListaExistente) {
            final int[] selectedRows = jTableMusicas.getSelectedRows();
            if (selectedRows.length == 0) {
                return;
            }
            ArrayList<Musica> lista = new ArrayList<Musica>(selectedRows.length);
            for (int i = 0; i < selectedRows.length; i++) {
                lista.add(objModelMusicas.getItem(converterIndiceTabelaMusica(selectedRows[i])));
            }
            carregador.addToPlayList(lista);
        } else if (e.getSource() == jMenuItemAdicionar) {
            adicionarMusicasSelecionadas();
        } else if (e.getSource() == jMenuItemEditar) {
            try {
                Musica musica = objModelMusicas.getItem(converterIndiceTabelaMusica(jTableMusicas.getSelectedRow()));
                new JMP3Propriedades(this, true, musica).setVisible(true);
                objModelMusicas.atualizarItem(musica, jTableMusicas.getSelectedRow());
            } catch (Exception ex) {
                Logger.getLogger(JFilaReproducao.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else if (e.getSource() == jButtonLimparPesquisa) {
            jTextFieldPesquisa.setText("");
            objModelMusicas.setFiltro("");
            atualizaTabelaMusica();
            jTextFieldPesquisa.requestFocus();
        } else if (e.getSource() == jMenuItemAbrirBiblioteca) {
            carregador.mostrarBiblioteca();
        } else if (e.getSource() == jMenuItemMostrarPlayList) {
            carregador.mostrarPlayList();
            carregador.setFonteReproducao(FonteReproducao.PLAY_LIST);
        } else if (e.getSource() == jMenuItemImportarArquivos) {
            int result = jFileChooserImportar.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                ArrayList<File> lista = new ArrayList<File>(2000);
                lista.addAll(Arrays.asList(jFileChooserImportar.getSelectedFiles()));
                if (lista.isEmpty()) {
                    lista.add(jFileChooserImportar.getSelectedFile());
                }
                importarMusicas(lista, false);
            }
        } else if (e.getSource() == jMenuItem1) {
            new JConfiguracao(this, true).setVisible(true);
        } else if (e.getSource() == jMenuItem2) {
            carregador.sair();
        } else if (e.getSource() == jMenuItem3) {
            carregador.mostrarModificadorDeTema();
        }else if(e.getSource() == jMenuItemVoltarTelaPrincipal){
            carregador.setPrincipalComoBase();
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getSource() == jTableMusicas) {
            if (e.getButton() == 1 && e.getClickCount() > 1) {
                final AcaoPadraoFila acaoPadraoFila = (AcaoPadraoFila) Configuracaoes.getObject(Configuracaoes.CONF_ACAO_PADRAO_FILA);
                if (acaoPadraoFila == AcaoPadraoFila.ADICIONAR_FILA) {
                    adicionarMusicasSelecionadas();
                } else if (acaoPadraoFila == AcaoPadraoFila.REPRODUZIR) {
                    carregador.setFonteReproducao(FonteReproducao.FILA_REPRODUCAO);    
                    tocarMusicaSelecionada();
                } else {
                    tocarMusicaSelecionada();
                }
            }
            if (e.getButton() == 3) {
                jPopupMenuMusica.show(jTableMusicas, e.getX(), e.getY());
            }
        } else if (e.getSource() == jTableFila) {
            if (e.getClickCount() == 1 && e.getButton() == 2) {
                removerMusicasSelecionadasFila();
            } else if (e.getClickCount() == 2) {
                tocarMusicaSelecionadaFila();
//            alterarMusica(musica);
            }
        } else if (e.getSource() == jButton_Play) {
            carregador.tocarPausar();
        } else if (e.getSource() == jButton_Stop) {
            carregador.parar();
        } else if (e.getSource() == jButton_Next) {
            carregador.tocarProxima();
        } else if (e.getSource() == jToggle_Repete) {
            carregador.setRepeat(!carregador.isRepeat());
            if (carregador.isRepeat()) {
                jToggle_Repete.setIcon(carregador.getIcones().getRepeatOnIcon32());
            } else {
                jToggle_Repete.setIcon(carregador.getIcones().getRepeatOffIcon32());
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.getSource() == jTableMusicas) {
            if (e.getButton() != MouseEvent.BUTTON1) {
                int linha = jTableMusicas.rowAtPoint(e.getPoint());
                int[] selectedRows = jTableMusicas.getSelectedRows();
                for (int i = 0; i < selectedRows.length; i++) {
                    int linhaSelecionada = selectedRows[i];
                    if (linha == linhaSelecionada) {
                        return;
                    }
                }
                jTableMusicas.setRowSelectionInterval(linha, linha);
            }
        } else if (e.getSource() == jTableFila) {
            if (e.getButton() != MouseEvent.BUTTON1) {
                int linha = jTableFila.rowAtPoint(e.getPoint());
                int[] selectedRows = jTableFila.getSelectedRows();
                for (int i = 0; i < selectedRows.length; i++) {
                    int linhaSelecionada = selectedRows[i];
                    if (linha == linhaSelecionada) {
                        return;
                    }
                }
                jTableFila.setRowSelectionInterval(linha, linha);
            }
        } else if (e.getSource() == jSlider_Tempo) {
            ajustandoTempo = true;
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (e.getSource() == jTableFila) {
            if (e.getButton() == MouseEvent.BUTTON3) {
                jPopupMenuFila.show(jTableFila, e.getX(), e.getY());
            }
        } else if (e.getSource() == jSlider_Tempo) {
            carregador.skipTo((double) jSlider_Tempo.getValue() / (double) jSlider_Tempo.getMaximum());
            ajustandoTempo = false;
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getSource() == jTextFieldPesquisa) {
            switch (e.getKeyCode()) {
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
        } else if (e.getSource() == jTableMusicas) {
            filtrarTabelaMusica(e);
        } else if (e.getSource() == jTableFila) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_ENTER:
                    tocarMusicaSelecionadaFila();
                    break;
                case KeyEvent.VK_DELETE:
                    removerMusicasSelecionadasFila();
                    break;
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getSource() == jTextFieldPesquisa) {
            String text = jTextFieldPesquisa.getText();
            objModelMusicas.setFiltro(text);
            repaint();
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void tableChanged(TableModelEvent e) {
        if (e.getSource() == jTableFila.getModel()) {
            if (e.getType() == TableModelEvent.INSERT) {
                carregador.setFonteReproducao(FonteReproducao.FILA_REPRODUCAO);
                if (!carregador.isPlaying()
                        && Configuracaoes.getEnum(Configuracaoes.CONF_ADICIONAR_NA_FILA_VAZIA) == AdicionarNaFilaVazia.REPRODUZIR_MUSICA) {
                    carregador.abrirETocar();
                }
            }
        }
    }

    @Override
    public void sizeChanged(Object source, int newSize) {
        if (source == objModelMusicas) {
            atualizarBarraStatusMusica();
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
        jLabelTempo = new javax.swing.JLabel();
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
        jMenuItemVoltarTelaPrincipal = new javax.swing.JMenuItem();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuItem3 = new javax.swing.JMenuItem();

        jMenuItemFilaTocar.setMnemonic('R');
        jMenuItemFilaTocar.setText("Reproduzir");
        jPopupMenuFila.add(jMenuItemFilaTocar);

        jMenuItemFilaRemover.setMnemonic('e');
        jMenuItemFilaRemover.setText("Remover da Fila");
        jPopupMenuFila.add(jMenuItemFilaRemover);

        jMenuItemEmbaralhar.setText("Embaralhar Fila");
        jPopupMenuFila.add(jMenuItemEmbaralhar);

        jMenuItemFilaMoveCima.setMnemonic('C');
        jMenuItemFilaMoveCima.setText("Mover para Cima");
        jPopupMenuFila.add(jMenuItemFilaMoveCima);

        jMenuItemFilaMoveBaixo.setMnemonic('B');
        jMenuItemFilaMoveBaixo.setText("Mover para Baixo");
        jPopupMenuFila.add(jMenuItemFilaMoveBaixo);

        jMenuItemLimpar.setText("Limpar Fila");
        jPopupMenuFila.add(jMenuItemLimpar);

        jMenuItemTocar.setMnemonic('R');
        jMenuItemTocar.setText("Reproduzir");
        jPopupMenuMusica.add(jMenuItemTocar);

        jMenuItemAdicionar.setMnemonic('F');
        jMenuItemAdicionar.setText("Adicionar na Fila");
        jPopupMenuMusica.add(jMenuItemAdicionar);

        jMenuItemPasta.setMnemonic('p');
        jMenuItemPasta.setText("Abrir Pasta da Música");
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
        jMenu3.add(jMenuItemAdicionarListaExistente);

        jPopupMenuMusica.add(jMenu3);

        jMenuItemEditar.setText("Propriedades");
        jPopupMenuMusica.add(jMenuItemEditar);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Fila de Reprodução");
        addWindowListener(this);

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
        jPanel3.add(jTextFieldPesquisa);

        jButtonLimparPesquisa.setMnemonic('L');
        jButtonLimparPesquisa.setText("Limpar");
        jButtonLimparPesquisa.setToolTipText("Limpar filtro. (Alt + L)");
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

        jPanel5.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        jPanel5.setOpaque(false);
        jPanel5.setPreferredSize(new java.awt.Dimension(4, 32));
        jPanel5.setLayout(new java.awt.BorderLayout());

        jLabelTocando.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        jLabelTocando.setText("Nada...");
        jLabelTocando.setPreferredSize(new java.awt.Dimension(0, 18));
        jPanel5.add(jLabelTocando, java.awt.BorderLayout.CENTER);

        jLabelTempo.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        jLabelTempo.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabelTempo.setText("00:00:00");
        jLabelTempo.setPreferredSize(new java.awt.Dimension(80, 21));
        jPanel5.add(jLabelTempo, java.awt.BorderLayout.EAST);

        jPanel8.add(jPanel5, java.awt.BorderLayout.PAGE_START);

        jPanel9.setMinimumSize(new java.awt.Dimension(221, 36));
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
        jPanelControles.add(jButton_Play);

        jButton_Stop.setText("Parar");
        jButton_Stop.setToolTipText("Parar");
        jButton_Stop.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jPanelControles.add(jButton_Stop);

        jButton_Next.setText("Avançar");
        jButton_Next.setToolTipText("Avançar");
        jButton_Next.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jPanelControles.add(jButton_Next);

        jToggle_Repete.setText("Repeat");
        jToggle_Repete.setToolTipText("Repetir Musica");
        jPanelControles.add(jToggle_Repete);

        jPanel9.add(jPanelControles, java.awt.BorderLayout.WEST);

        jPanel11.setOpaque(false);
        jPanel11.setPreferredSize(new java.awt.Dimension(202, 18));
        jPanel11.setLayout(new javax.swing.BoxLayout(jPanel11, javax.swing.BoxLayout.LINE_AXIS));

        jSlider_Tempo.setFont(new java.awt.Font("Cantarell", 0, 1)); // NOI18N
        jSlider_Tempo.setForeground(javax.swing.UIManager.getDefaults().getColor("ProgressBar.background"));
        jSlider_Tempo.setMaximum(1000);
        jSlider_Tempo.setToolTipText("");
        jSlider_Tempo.setValue(0);
        jSlider_Tempo.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jSlider_Tempo.setMinimumSize(new java.awt.Dimension(36, 14));
        jSlider_Tempo.setPreferredSize(new java.awt.Dimension(202, 22));
        jPanel11.add(jSlider_Tempo);

        jPanel9.add(jPanel11, java.awt.BorderLayout.CENTER);

        jPanel8.add(jPanel9, java.awt.BorderLayout.CENTER);

        getContentPane().add(jPanel8, java.awt.BorderLayout.PAGE_START);

        jMenu1.setText("Atividades");

        jMenuItemAbrirBiblioteca.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_B, java.awt.event.InputEvent.ALT_MASK));
        jMenuItemAbrirBiblioteca.setText("Mostrar Biblioteca");
        jMenu1.add(jMenuItemAbrirBiblioteca);

        jMenuItemMostrarPlayList.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_R, java.awt.event.InputEvent.ALT_MASK));
        jMenuItemMostrarPlayList.setText("Mostrar Lista de Reprodução");
        jMenu1.add(jMenuItemMostrarPlayList);

        jMenuItemImportarArquivos.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_I, java.awt.event.InputEvent.ALT_MASK));
        jMenuItemImportarArquivos.setText("Importar Músicas...");
        jMenu1.add(jMenuItemImportarArquivos);

        jMenuItemVoltarTelaPrincipal.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_P, java.awt.event.InputEvent.ALT_MASK));
        jMenuItemVoltarTelaPrincipal.setText("Voltar para a tela Principal");
        jMenu1.add(jMenuItemVoltarTelaPrincipal);

        jMenuItem2.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Q, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem2.setText("Sair");
        jMenu1.add(jMenuItem2);

        jMenuBar1.add(jMenu1);

        jMenu2.setText("Editar");

        jMenuItem1.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_C, java.awt.event.InputEvent.ALT_MASK));
        jMenuItem1.setText("Configurações");
        jMenu2.add(jMenuItem1);

        jMenuItem3.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_T, java.awt.event.InputEvent.ALT_MASK));
        jMenuItem3.setText("Tema");
        jMenuItem3.setEnabled(false);
        jMenu2.add(jMenuItem3);

        jMenuBar1.add(jMenu2);

        setJMenuBar(jMenuBar1);

        setSize(new java.awt.Dimension(800, 600));
        setLocationRelativeTo(null);
    }

    // Code for dispatching events from components to event handlers.

    public void windowActivated(java.awt.event.WindowEvent evt) {
    }

    public void windowClosed(java.awt.event.WindowEvent evt) {
    }

    public void windowClosing(java.awt.event.WindowEvent evt) {
        if (evt.getSource() == JFilaReproducao.this) {
            JFilaReproducao.this.formWindowClosing(evt);
        }
    }

    public void windowDeactivated(java.awt.event.WindowEvent evt) {
    }

    public void windowDeiconified(java.awt.event.WindowEvent evt) {
    }

    public void windowIconified(java.awt.event.WindowEvent evt) {
    }

    public void windowOpened(java.awt.event.WindowEvent evt) {
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        carregador.sair();
    }//GEN-LAST:event_formWindowClosing

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {

        new JFilaReproducao(null).setVisible(true);
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
    private javax.swing.JLabel jLabelTempo;
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
    private javax.swing.JMenuItem jMenuItemVoltarTelaPrincipal;
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
