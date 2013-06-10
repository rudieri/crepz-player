package com.playlist;

import com.conexao.Transacao;
import com.config.Configuracaoes;
import com.copiador.JCopiador;
import com.fila.JFilaReproducao;
import com.main.Carregador;
import com.main.gui.JMP3Propriedades;
import com.main.gui.JPrincipal;
import com.musica.*;
import com.playlist.listainteligente.condicao.Condicao;
import com.playlist.listainteligente.condicao.CondicaoBD;
import com.playlist.listainteligente.condicao.CondicaoSC;
import com.playlist.listainteligente.condicao.JListaInteligenteEditor;
import com.playmusica.PlayMusica;
import com.playmusica.PlayMusicaBD;
import com.playmusica.PlayMusicaSC;
import com.utils.file.FileUtils;
import com.utils.file.filtros.FiltroListaReproducao;
import com.utils.model.ModelReadOnly;
import com.utils.model.tablemodel.ObjectTransferable;
import com.utils.transferivel.TipoTransferenciaMusica;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;


/*
 * JPlayList.java
 *
 * Created on 04/06/2010, 19:19:01
 */
/**
 *
 * @author manchini
 */
public class JPlayList extends javax.swing.JDialog implements ActionListener, ListSelectionListener, KeyListener, MouseListener, ChangeListener {

    public static final SimpleDateFormat formatoPadraoData;
    /**
     * Creates new form JPlayList
     */
    private JPrincipal principal;
//    private boolean aleatorio = false;
//    private boolean recomecar = false;
    private ArrayList<Musica> faltamTocar = new ArrayList<Musica>(200);
    private ArrayList<Musica> jahFoi = new ArrayList<Musica>(200);
    private ArrayList<Musica> total = new ArrayList<Musica>(200);
    private ArrayList<Musica> pesquisa = new ArrayList<Musica>(200);
    private final Carregador carregador;
    private int contaErro = 0;
    private Playlist playlist;
    private DropTarget dropTargetPlayList;
    private JCopiador jCopiador;

    static {
        formatoPadraoData = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    }

    public JPlayList(Carregador carregador) {
        initComponents();
        this.carregador = carregador;
        initTabelaLista();

        jPanelOpcoesLista.setVisible(false);
        setIconImage(carregador.getIcones().getCrepzIcon().getImage());
        carregarIcones();
    }

    public void setPlayListAberta(Integer id) {
        if (id == null) {
            playlist = null;
            return;
        }
        try {
            playlist = new Playlist();
            playlist.setId(id);
            PlaylistBD.carregar(playlist);
            abrir(playlist);
        } catch (Exception ex) {
            Logger.getLogger(JPlayList.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public Playlist getPlaylistAberta() {
        return playlist;
    }

    public void importarMusicasParaPlayList(String[] nomes) {
        File[] files = new File[nomes.length];
        for (int i = 0; i < nomes.length; i++) {
            files[i] = new File(nomes[i]);
        }
        importarMusicasParaPlayList(files);
    }

    public void importarMusicasParaPlayList(File[] files) {
        Transacao t = new Transacao();
        try {
            t.begin();
            importarMusicas(files, t);
            t.commit();
        } catch (Exception ex) {
            t.rollback();
            Logger.getLogger(JPlayList.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void importarMusicas(File[] files, Transacao t) {
        try {
            for (File s : files) {
                Musica musica = MusicaGerencia.addOneFile(s, t);
                if (musica == null) {
                    continue;
                }
                addMusica(musica);
            }
        } catch (Exception ex) {
            Logger.getLogger(getClass().getName()).log(Level.ALL, "Erro ao adicionar música.", ex);
        }
    }

    private void carregarIcones() {
        jMenuItem_Editar.setIcon(carregador.getIcones().getEdit());
        jMenuItem_Tocar.setIcon(carregador.getIcones().getPlayIcon16());
        jMenuItem_ExcluirLista.setIcon(carregador.getIcones().getXis());
    }

    private void tocarSelecionada() {
        try {
            Musica m = (Musica) jTable.getModel().getValueAt(jTable.getSelectedRow(), 0);
            //tocar(m);
            carregador.abrir(m, 0, false);
            if (jahFoi.indexOf(m) != jahFoi.size() - 1) {
                jahFoi.add(m);
                faltamTocar.remove(m);
            }
        } catch (Exception ex) {
            Logger.getLogger(JPlayList.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void lookAndFeelChanged() {
        jTable.setDefaultRenderer(Object.class, new PlayListRenderer());
    }

    /**
     * Método que inicializa a tela.
     */
    private void initTabelaLista() {

        // Definindo as colunas...
        ModelReadOnly tm = new ModelReadOnly();
        tm.addColumn("");
        //   tm.addColumn("Autor");
        try {
            jTable.setModel(tm);
        } catch (Exception ex) {
            ex.printStackTrace(System.err);
        }
        jTable.setAutoCreateRowSorter(true);
        jTable.setShowVerticalLines(true);
        jTable.setEditingColumn(-1);
        jTable.setEditingRow(-1);

        jTable.setDefaultRenderer(Object.class, new PlayListRenderer());
        jTable.setIntercellSpacing(new Dimension(1, 2));
        jTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        jTable.setShowHorizontalLines(false);
        jTable.setShowVerticalLines(true);
        jTable.setRowSelectionAllowed(true);
        jTable.setColumnSelectionAllowed(false);
        jTable.setRowHeight(40);
        jTable.setDragEnabled(true);
        jTable.getSelectionModel().addListSelectionListener(this);
        jTable.setTransferHandler(new TransferHandler(null) {
            @Override
            public int getSourceActions(JComponent c) {
                return COPY_OR_MOVE;
            }

            @Override
            protected Transferable createTransferable(JComponent c) {
                int[] rows = jTable.getSelectedRows();
                ArrayList<Musica> musicas = new ArrayList<Musica>(rows.length);
                for (int i = 0; i < rows.length; i++) {
                    musicas.add((Musica) jTable.getModel().getValueAt(rows[i], 0));
                }
                return new ObjectTransferable(musicas, TipoTransferenciaMusica.JPLAY_LIST);

            }
        });

        dropTargetPlayList = new DropTarget(jScrollPane, new DropTargetAdapter() {
            @Override
            public void drop(DropTargetDropEvent dtde) {
                try {
                    Point location = dtde.getLocation();
                    int posicaoDestino = -1;
                    if (jTable.getSelectedRows().length > 0) {
                        Point visibleLocation = jTable.getVisibleRect().getLocation();
                        visibleLocation.x += location.x;
                        visibleLocation.y += location.y;
                        posicaoDestino = jTable.rowAtPoint(visibleLocation);

                        if ((dtde.getSourceActions() == TransferHandler.MOVE
                                || location.y < jTable.getHeight())) {

                            if (TipoTransferenciaMusica.forDataFlavor(dtde.getCurrentDataFlavors()) == TipoTransferenciaMusica.JPLAY_LIST) {
                                alterarPosicaoMusicasPlayList(jTable.getSelectedRows(), posicaoDestino);
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
                            ((ModelReadOnly) jTable.getModel()).insertRow(posicaoDestino, new Object[]{data});
                        } else {
                            ((ModelReadOnly) jTable.getModel()).addRow(new Object[]{data});
                        }
                    } else if (data != null && data.getClass() == ArrayList.class) {
                        addMusicas((ArrayList) data, posicaoDestino);
                    } else {
                        //Windows
                        if (transferable.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                            ArrayList<File> arquivos = (ArrayList) transferable.getTransferData(java.awt.datatransfer.DataFlavor.javaFileListFlavor);
                            importarMusicasParaPlayList((File[]) arquivos.toArray());
                        } else {
                            // Linux
                            loop_flavor:
                            for (int i = 0; i < flavors.length; i++) {
                                if (flavors[i].isRepresentationClassReader()) {
                                    dtde.acceptDrop(java.awt.dnd.DnDConstants.ACTION_COPY);

                                    Reader reader = flavors[i].getReaderForText(transferable);

                                    BufferedReader br = new BufferedReader(reader);
                                    ArrayList<File> arquivos = new ArrayList<File>(10);
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
                                    importarMusicasParaPlayList((File[]) arquivos.toArray());
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
        jTable.setDropTarget(dropTargetPlayList);
        jScrollPane.setDropTarget(dropTargetPlayList);
    }

    /**
     * Método que atualiza a consulta atual.
     */
    public void atualizarTabelaLista() {
        pesquisa.clear();
        if (jTextField_NomePlayList.getText().isEmpty()) {
            return;
        }

        Transacao t = new Transacao();
        try {
            t.begin();
//            initTabelaLista(false);

            ModelReadOnly model = (ModelReadOnly) jTable.getModel();
            model.setRowCount(0);
            if (playlist == null) {
                return;
            }
            // Filtro...
//            Playlist p = new Playlist();
//            p.setNome(jTextField_NomePlayList.getText());
//            PlaylistBD.existe(p, t);
            if (playlist.getTipoPlayList() != null && playlist.getTipoPlayList() == TipoPlayList.INTELIGENTE) {
                ArrayList<Musica> musicas = MusicaBD.listar(new MusicaSC(), t);
                CondicaoSC filtroCond = new CondicaoSC();
                filtroCond.setPlaylist(playlist);
                ArrayList<Condicao> condicaos = CondicaoBD.listar(filtroCond, t);
                for (int i = 0; i < musicas.size(); i++) {
                    Musica musica = musicas.get(i);
                    boolean todasAsCondicoes = true;
                    for (int j = 0; todasAsCondicoes && j < condicaos.size(); j++) {
                        Condicao condicao = condicaos.get(j);
                        todasAsCondicoes &= condicao.resolver(musica);
                    }
                    if (todasAsCondicoes) {
                        musica.setNumero(model.getRowCount());
                        Object[] row = new Object[1];
                        row[0] = musica;
                        model.addRow(row);
                        faltamTocar.add(musica);
                        total.add(musica);
                        pesquisa.add(musica);

                    }
                }

            } else {
                PlayMusicaSC filtro = new PlayMusicaSC();
                filtro.setPlaylist(playlist);
                ArrayList<PlayMusica> lista = PlayMusicaBD.listar(filtro, t);
                for (int i = 0; i < lista.size(); i++) {
                    PlayMusica m = (PlayMusica) lista.get(i);
                    if (m.getMusica().getCaminho() == null) {
                        MusicaBD.carregar(m.getMusica(), t);
                    }
                    m.getMusica().setNumero(i);
                    Object[] row = new Object[1];
                    row[0] = m.getMusica();
                    model.addRow(row);
                    faltamTocar.add(m.getMusica());
                    total.add(m.getMusica());
                    pesquisa.add(m.getMusica());
                }

            }
            jTable.requestFocus();
            if (jTable.getRowCount() > 0) {
                jTable.changeSelection(0, 0, false, false);
            }
            t.commit();
            setTitle(jTextField_NomePlayList.getText()
                    + (playlist.getTipoPlayList() == TipoPlayList.INTELIGENTE ? " (Lista Automática)" : ""));

        } catch (Exception ex) {
            t.rollback();
            // JOptionPane.showMessageDialog(this, "Erro ao Filtrar \\õ/");
            ex.printStackTrace(System.err);
        }
    }

    public void atualizarTabelaLista(ArrayList novaLista) {
        try {

//            initTabelaLista(true);
            ModelReadOnly ts = (ModelReadOnly) jTable.getModel();
            ts.setRowCount(0);
            for (int i = 0; i < novaLista.size(); i++) {
                Musica m = (Musica) novaLista.get(i);

                Object[] row = new Object[1];
                row[0] = m;
                ts.addRow(row);
            }
            jTable.requestFocus();
            if (jTable.getRowCount() > 0) {
                jTable.changeSelection(0, 0, false, false);
            }
            setTitle(jTextField_NomePlayList.getText());

        } catch (Exception ex) {
            //  JOptionPane.showMessageDialog(this, "Erro ao Filtrar! OO");
            ex.printStackTrace(System.err);
        }
    }

    @SuppressWarnings("AssignmentToMethodParameter")
    private void alterarPosicaoMusicasPlayList(int[] indices, int novaPosicao) {
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
            ((ModelReadOnly) jTable.getModel()).moveRow(start.intValue(), end, novaPosicao);
            novaPosicao += (end - start + 1);
        }
        jTable.clearSelection();
    }

    public void setVisible(boolean visivel, boolean sempreNoTopo) {
        super.setVisible(visivel);
        super.setAlwaysOnTop(sempreNoTopo);
    }

    public Musica getAleatorio(Musica atual) {
        if (faltamTocar.isEmpty()) {
            if (Configuracaoes.getBoolean(Configuracaoes.CONF_REPEAT_ATIVO)) {
                faltamTocar.addAll(jahFoi);
                jahFoi.clear();
                return getAleatorio(atual);
            } else {
                return null;
            }
        } else {
            if (jahFoi.indexOf(atual) == -1 && faltamTocar.indexOf(atual) != -1) {
                jahFoi.add(faltamTocar.remove(faltamTocar.indexOf(atual)));
                return getAleatorio(atual);
            } else {
                int random = (int) (Math.random() * faltamTocar.size());
                Musica m = faltamTocar.remove(random);
                jahFoi.add(m);
                if (jTable.getRowCount() > m.getNumero()) {
                    jTable.setRowSelectionInterval(m.getNumero(), m.getNumero());
                } else {
                    selecionarMusica(m);
                }
                return m;
            }
        }

    }

    public Musica getProxima() {
        return getProxima(false);
    }

    private Musica getProxima(boolean erro) {
        if (!erro) {
            contaErro = 0;
        } else {
            contaErro++;
        }
        if (total.size() < 1) {
            return null;
        }
        if (contaErro > total.size()) {
            JOptionPane.showMessageDialog(this, "Nenhum arquivo foi encontrado... Você montou sua unidades?");
            contaErro = 0;
            return null;
        }
        try {
            Musica atual = carregador.getMusica();
            int mAtual = -1;
            if (!Configuracaoes.getBoolean(Configuracaoes.CONF_RANDOM_ATIVO)) {
                if (atual != null) {
                    mAtual = atual.getNumero();
                }

                if (mAtual < 0) {
                    jTable.setRowSelectionInterval(0, 0);
                    return (Musica) jTable.getModel().getValueAt(0, 0);
                }

                if (mAtual + 1 >= jTable.getRowCount()) {
                    jTable.setRowSelectionInterval(0, 0);
                    return (Musica) jTable.getModel().getValueAt(0, 0);

                } else {
                    jTable.setRowSelectionInterval(mAtual + 1, mAtual + 1);
                    return (Musica) jTable.getModel().getValueAt(mAtual + 1, 0);

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
            Musica atual = carregador.getMusica();
            int mAtual;
            if (!Configuracaoes.getBoolean(Configuracaoes.CONF_RANDOM_ATIVO)) {
                mAtual = atual.getNumero();
                if (mAtual > -1) {
                    if (mAtual - 1 < 0) {
                        jTable.setRowSelectionInterval(mAtual - 1, mAtual - 1);
                        return (Musica) jTable.getModel().getValueAt(jTable.getRowCount() - 1, 0);
                    } else {
                        jTable.setRowSelectionInterval(mAtual - 1, mAtual - 1);
                        return (Musica) jTable.getModel().getValueAt(mAtual - 1, 0);
                    }
                }
                System.out.println("Não tem mais musicas, retornando null.");
                return null;
            } else {
                if (jahFoi.indexOf(carregador.getMusica()) > 0) {
                    Musica musica = jahFoi.get(jahFoi.indexOf(carregador.getMusica()) - 1);
                    selecionarMusica(musica);
                    return musica;
                }
                return null;
            }
        } catch (Exception ex) {
            Logger.getLogger(JPlayList.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }

    }

    public void selecionarMusica(Musica musica) {
        if (musica.getNumero() >= 0 && musica.getNumero() < jTable.getRowCount()
                && jTable.getValueAt(musica.getNumero(), 0).equals(musica)) {
            jTable.setRowSelectionInterval(musica.getNumero(), musica.getNumero());
        } else {
            for (int i = 0; i < jTable.getRowCount(); i++) {
                if (jTable.getValueAt(i, 0).equals(musica)) {
                    jTable.setRowSelectionInterval(i, i);
                }
            }
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
                    Musica m = total.get(i);
                    String aux = m.getCaminho();
                    aux = aux.replace('/', File.separatorChar);
                    bfw.write(aux);
                    bfw.newLine();
                }
                bfw.close();
                fw.close();
            } catch (IOException ex) {
                ex.printStackTrace(System.err);
            }

        }

    }

    private void salvarPlaylist() {
        Transacao t = new Transacao();
        try {
            t.begin();
            playlist = new Playlist();
            playlist.setNome(jTextField_NomePlayList.getText());
            if (PlaylistBD.existe(playlist, t)) {
                PlaylistBD.carregar(playlist, t);
            } else {
                PlaylistBD.incluir(playlist, t);
                PlaylistBD.existe(playlist, t);
            }
            PlayMusicaBD.excluirMusica(playlist, t);
            for (int i = 0; i < total.size(); i++) {
                Musica m = total.get(i);
                PlayMusica plm = new PlayMusica();
                plm.setMusica(m);
                plm.setPlaylist(playlist);
                plm.setSeq(i);
                //plm.setId(m.getId());
                PlayMusicaBD.incluir(plm, t);
            }
            playlist.setNrMusicas(total.size());

            PlaylistBD.alterar(playlist, t);
            setTitle(jTextField_NomePlayList.getText());

            t.commit();
        } catch (Exception ex) {
            t.rollback();
            ex.printStackTrace(System.err);
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
            limpar();
        } catch (Exception ex) {
            t.rollback();
            ex.printStackTrace(System.err);
        }
    }

    public void limpar() {
        this.playlist = null;
        jTextField_NomePlayList.setText("");
        setTitle("Lista de Reprodução - Vazia");
//        initTabelaLista(false);
        ((ModelReadOnly) jTable.getModel()).setRowCount(0);
        faltamTocar = new ArrayList(200);
        total = new ArrayList(200);
    }

    public void abrir(Playlist playlist) {
        this.playlist = playlist;
        boolean listaNormal = playlist.getTipoPlayList() == null || playlist.getTipoPlayList() == TipoPlayList.NORMAL;
        jMenuItem_ExcluirLista.setEnabled(listaNormal);
        jMenuItemSalvar.setEnabled(listaNormal);
        jButtonAdicionar.setEnabled(listaNormal);
        jButtonRemover.setEnabled(listaNormal);
        jButtonSalvar.setEnabled(listaNormal);


        jTextField_NomePlayList.setText(playlist.getNome());
        setTitle(playlist.getNome());
        atualizarTabelaLista();

    }

    public void tocar(Playlist playlist) {
        try {
            abrir(playlist);
            carregador.abrir(getProxima(false), 0, false);
        } catch (Exception ex) {
            Logger.getLogger(JPlayList.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void adicionarMusica() {
        Transacao t = new Transacao();
        try {
            File in = principal.telaAbrirArquivo();

            t.begin();

            Musica m = MusicaGerencia.addOneFile(in, t);
            t.commit();
            //trace("ID: " +m.getId());
            MusicaSC filtro = new MusicaSC();
            filtro.setCaminho(m.getCaminho().trim());


            m = MusicaBD.listar(filtro).get(0);
//
            addMusica(m);


        } catch (Exception ex) {
            t.rollback();
            Logger.getLogger(JPlayList.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void removerMusica() {
        if (playlist.getTipoPlayList() == TipoPlayList.INTELIGENTE) {
            JOptionPane.showMessageDialog(this, "Essa lista é automática!"
                    + "\nAltere as condições para remover ou adicionar músicas.");
            return;
        }
        DefaultTableModel tm = (DefaultTableModel) jTable.getModel();
        int selecteds[] = jTable.getSelectedRows();
        for (int i = selecteds.length - 1; i >= 0; i--) {

            Musica m = (Musica) tm.getValueAt(selecteds[i], 0);
            faltamTocar.remove(m);
            pesquisa.remove(m);
            total.remove(m);
            tm.removeRow(selecteds[i]);
            PlayMusica playMusica = new PlayMusica();
            playMusica.setMusica(m);
            playMusica.setPlaylist(playlist);
            try {
                PlayMusicaBD.excluirPelaBk(playMusica);
            } catch (Exception ex) {
                Logger.getLogger(JPlayList.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void openM3u() {
        JFileChooser jf = new JFileChooser();
        jf.setDialogType(JFileChooser.OPEN_DIALOG);
        jf.setFileSelectionMode(JFileChooser.FILES_ONLY);
        jf.setFileFilter(FiltroListaReproducao.getInstance());
        if (jf.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File f = jf.getSelectedFile();
            try {

                importarMusicasParaPlayList(FileUtils.lerM3u(f));
            } catch (Exception ex) {

                Logger.getLogger(JPlayList.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    private void salvarPlaylistAtual() {
        if (jTextField_NomePlayList.getText() == null || jTextField_NomePlayList.getText().isEmpty()) {
            jTextField_NomePlayList.setText("Execução " + formatoPadraoData.format(new Date()));
        }
        salvarPlaylist();

    }

    private void consultarPlayList() {
        JSelectPlaylists jPlayLists = new JSelectPlaylists(principal, true, this);
        jPlayLists.setVisible(true);
    }

    private void salvarPlayList() {
        jToggleButtonOpcoesLista.setSelected(true);
        if (jTextField_NomePlayList.getText() == null || jTextField_NomePlayList.getText().isEmpty() || jTextField_NomePlayList.getText().equals("Coloque um nome!")) {
            jTextField_NomePlayList.setText("Coloque um nome!");
            jTextField_NomePlayList.selectAll();

            transferFocus();
        } else {
            if (playlist != null) {
                playlist.setNome(jTextField_NomePlayList.getText());
                try {
                    PlaylistBD.alterar(playlist);
                } catch (Exception ex) {
                    Logger.getLogger(JPlayList.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            salvarPlaylist();
            jToggleButtonOpcoesLista.setSelected(false);

        }
    }

    private void onTableKeyPressed(KeyEvent evt) {
        switch (evt.getKeyCode()) {
            case KeyEvent.VK_DELETE:
                removerMusica();
                return;
            case KeyEvent.VK_PAGE_DOWN:
            case KeyEvent.VK_PAGE_UP:
            case KeyEvent.VK_END:
            case KeyEvent.VK_HOME:
            case KeyEvent.VK_SHIFT:
            case KeyEvent.VK_CONTROL:
                return;
            case KeyEvent.VK_ENTER:
                evt.consume();
                tocarSelecionada();
                break;
            case KeyEvent.VK_DOWN:
            case KeyEvent.VK_UP:
                break;
        }
        char keyChar = evt.getKeyChar();
        if ((keyChar >= 'a' && keyChar <= 'z')
                || (keyChar >= 'A' && keyChar <= 'Z')
                || (keyChar >= '0' && keyChar <= '9')) {
            jTextField_Pesquisa.setText(jTextField_Pesquisa.getText() + keyChar);
            jTextField_Pesquisa.requestFocus();
        }
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (jTable.getSelectedRow() == -1) {
            return;
        }
        try {
            jTable.scrollRectToVisible(jTable.getCellRect(jTable.getSelectedRow(), 0, false));
        } catch (Exception ex) {
            ex.printStackTrace(System.err);
        }
    }

    private void novaListaAutomatica() {
        limpar();
        JListaInteligenteEditor editor = new JListaInteligenteEditor(principal, true);
        editor.setPlaylist(null);
        editor.setVisible(true);
        if (editor.getPlaylist() != null) {
            abrir(editor.getPlaylist());
        }
    }

    private void editarListaAutomatica() {
        JListaInteligenteEditor editor = new JListaInteligenteEditor(principal, true);
        editor.setPlaylist(playlist);
        editor.setVisible(true);
        if (editor.getPlaylist() != null) {
            abrir(editor.getPlaylist());
        }
    }

    private void abrirPropriedades() {
        int linha = jTable.getSelectedRow();
        Musica musica = (Musica) jTable.getValueAt(linha, 0);
        try {
            new JMP3Propriedades(principal, true, musica).setVisible(true);
            jTable.setValueAt(musica, linha, 0);
        } catch (Exception ex) {
            Logger.getLogger(JPlayList.class.getName()).log(Level.SEVERE, null, ex);
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

        jPopupMenu_Playlist = new javax.swing.JPopupMenu();
        jMenuItem_Tocar = new javax.swing.JMenuItem();
        jMenuItem_Editar = new javax.swing.JMenuItem();
        jMenuItem_ExcluirLista = new javax.swing.JMenuItem();
        jPanelOpcoesLista = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jTextField_NomePlayList = new javax.swing.JTextField();
        jButtonSalvar = new javax.swing.JButton();
        jButtonExportar = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        jButtonAbrir = new javax.swing.JButton();
        jButtonConsultar = new javax.swing.JButton();
        jButtonDeletar = new javax.swing.JButton();
        jButtonLimpar = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane = new javax.swing.JScrollPane();
        jTable = new javax.swing.JTable();
        jPanel3 = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        jButtonAdicionar = new javax.swing.JButton();
        jButtonRemover = new javax.swing.JButton();
        jToggleButtonOpcoesLista = new javax.swing.JToggleButton();
        jTextField_Pesquisa = new javax.swing.JTextField();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenuFuncoes = new javax.swing.JMenu();
        jMenuItemAbrirPlayList = new javax.swing.JMenuItem();
        jMenuItemCopiarTudoPara = new javax.swing.JMenuItem();
        jMenuItemSalvar = new javax.swing.JMenuItem();
        jMenuItemLimparLista = new javax.swing.JMenuItem();
        jMenuItemConsultar = new javax.swing.JMenuItem();
        jMenuItemExcluirLista = new javax.swing.JMenuItem();
        jMenuItemFechar = new javax.swing.JMenuItem();
        jMenuListaAutomatica = new javax.swing.JMenu();
        jMenuItem_NovaListaAutomatica = new javax.swing.JMenuItem();
        jMenuItem_EditarListaAutomatica = new javax.swing.JMenuItem();

        jMenuItem_Tocar.setText("Reproduzir");
        jMenuItem_Tocar.addActionListener(this);
        jPopupMenu_Playlist.add(jMenuItem_Tocar);

        jMenuItem_Editar.setText("Propriedades");
        jMenuItem_Editar.addActionListener(this);
        jPopupMenu_Playlist.add(jMenuItem_Editar);

        jMenuItem_ExcluirLista.setText("Remover da Lista");
        jMenuItem_ExcluirLista.addActionListener(this);
        jPopupMenu_Playlist.add(jMenuItem_ExcluirLista);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanelOpcoesLista.setPreferredSize(new java.awt.Dimension(323, 70));
        jPanelOpcoesLista.setLayout(new java.awt.BorderLayout());

        jPanel8.setLayout(new java.awt.GridLayout(2, 0));

        jPanel4.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 1, 4));

        jLabel1.setText("Nome:");
        jPanel4.add(jLabel1);

        jTextField_NomePlayList.setPreferredSize(new java.awt.Dimension(200, 25));
        jPanel4.add(jTextField_NomePlayList);

        jButtonSalvar.setMnemonic('S');
        jButtonSalvar.setText("Salvar");
        jButtonSalvar.setToolTipText("Salva a lista internamente.");
        jButtonSalvar.addActionListener(this);
        jPanel4.add(jButtonSalvar);

        jButtonExportar.setMnemonic('E');
        jButtonExportar.setText("Expotar M3u");
        jButtonExportar.setToolTipText("Exporta lista como um arquivo M3U");
        jButtonExportar.addActionListener(this);
        jPanel4.add(jButtonExportar);

        jPanel8.add(jPanel4);

        jPanel5.setLayout(new java.awt.GridLayout(1, 0));

        jButtonAbrir.setText("Abrir M3U");
        jButtonAbrir.setToolTipText("Abre um arquivo m3u");
        jButtonAbrir.addActionListener(this);
        jPanel5.add(jButtonAbrir);

        jButtonConsultar.setText("Consultar Lista");
        jButtonConsultar.setToolTipText("Consulta na lista salva internamente.");
        jButtonConsultar.addActionListener(this);
        jPanel5.add(jButtonConsultar);

        jButtonDeletar.setText("Deletar");
        jButtonDeletar.setToolTipText("Excluir lista salva internamente.");
        jButtonDeletar.addActionListener(this);
        jPanel5.add(jButtonDeletar);

        jButtonLimpar.setText("Limpar");
        jButtonLimpar.setToolTipText("Limpa a lista...");
        jButtonLimpar.addActionListener(this);
        jPanel5.add(jButtonLimpar);

        jPanel8.add(jPanel5);

        jPanelOpcoesLista.add(jPanel8, java.awt.BorderLayout.CENTER);

        getContentPane().add(jPanelOpcoesLista, java.awt.BorderLayout.PAGE_START);

        jPanel2.setPreferredSize(new java.awt.Dimension(323, 150));
        jPanel2.setLayout(new java.awt.BorderLayout());

        jScrollPane.setAutoscrolls(true);
        jScrollPane.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jScrollPane.setRequestFocusEnabled(false);

        jTable.setAutoCreateRowSorter(true);
        jTable.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jTable.setFocusCycleRoot(true);
        jTable.setFocusTraversalPolicyProvider(true);
        jTable.setRowSelectionAllowed(false);
        jTable.setSurrendersFocusOnKeystroke(true);
        jTable.addMouseListener(this);
        jTable.addKeyListener(this);
        jScrollPane.setViewportView(jTable);

        jPanel2.add(jScrollPane, java.awt.BorderLayout.CENTER);

        getContentPane().add(jPanel2, java.awt.BorderLayout.CENTER);

        jPanel3.setPreferredSize(new java.awt.Dimension(323, 30));
        jPanel3.setLayout(new javax.swing.BoxLayout(jPanel3, javax.swing.BoxLayout.X_AXIS));

        jPanel6.setPreferredSize(new java.awt.Dimension(323, 25));
        jPanel6.setLayout(new javax.swing.BoxLayout(jPanel6, javax.swing.BoxLayout.LINE_AXIS));

        jButtonAdicionar.setText("+");
        jButtonAdicionar.setPreferredSize(new java.awt.Dimension(31, 31));
        jButtonAdicionar.addActionListener(this);
        jPanel6.add(jButtonAdicionar);

        jButtonRemover.setText("-");
        jButtonRemover.setPreferredSize(new java.awt.Dimension(31, 31));
        jButtonRemover.addActionListener(this);
        jPanel6.add(jButtonRemover);

        jToggleButtonOpcoesLista.setMnemonic('o');
        jToggleButtonOpcoesLista.setText("Opções da lista");
        jToggleButtonOpcoesLista.addChangeListener(this);
        jPanel6.add(jToggleButtonOpcoesLista);

        jTextField_Pesquisa.addKeyListener(this);
        jPanel6.add(jTextField_Pesquisa);

        jPanel3.add(jPanel6);

        getContentPane().add(jPanel3, java.awt.BorderLayout.PAGE_END);

        jMenuFuncoes.setMnemonic('F');
        jMenuFuncoes.setText("Funções");

        jMenuItemAbrirPlayList.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_A, java.awt.event.InputEvent.ALT_MASK));
        jMenuItemAbrirPlayList.setText("Abrir PlayList *.m3u");
        jMenuItemAbrirPlayList.addActionListener(this);
        jMenuFuncoes.add(jMenuItemAbrirPlayList);

        jMenuItemCopiarTudoPara.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_C, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        jMenuItemCopiarTudoPara.setText("Copiar Tudo Para...");
        jMenuItemCopiarTudoPara.addActionListener(this);
        jMenuFuncoes.add(jMenuItemCopiarTudoPara);

        jMenuItemSalvar.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItemSalvar.setText("Salvar");
        jMenuItemSalvar.addActionListener(this);
        jMenuFuncoes.add(jMenuItemSalvar);

        jMenuItemLimparLista.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_L, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItemLimparLista.setText("Limpar");
        jMenuItemLimparLista.addActionListener(this);
        jMenuFuncoes.add(jMenuItemLimparLista);

        jMenuItemConsultar.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_P, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItemConsultar.setText("Consultar Lista");
        jMenuItemConsultar.addActionListener(this);
        jMenuFuncoes.add(jMenuItemConsultar);

        jMenuItemExcluirLista.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_DELETE, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItemExcluirLista.setText("Excluir Lista");
        jMenuItemExcluirLista.addActionListener(this);
        jMenuFuncoes.add(jMenuItemExcluirLista);

        jMenuItemFechar.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ESCAPE, 0));
        jMenuItemFechar.setText("Fechar");
        jMenuItemFechar.addActionListener(this);
        jMenuFuncoes.add(jMenuItemFechar);

        jMenuBar1.add(jMenuFuncoes);

        jMenuListaAutomatica.setText("Lista Automática");

        jMenuItem_NovaListaAutomatica.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem_NovaListaAutomatica.setText("Nova Lista Automática");
        jMenuItem_NovaListaAutomatica.addActionListener(this);
        jMenuListaAutomatica.add(jMenuItem_NovaListaAutomatica);

        jMenuItem_EditarListaAutomatica.setText("Editar Lista Automática");
        jMenuItem_EditarListaAutomatica.addActionListener(this);
        jMenuListaAutomatica.add(jMenuItem_EditarListaAutomatica);

        jMenuBar1.add(jMenuListaAutomatica);

        setJMenuBar(jMenuBar1);

        setSize(new java.awt.Dimension(494, 495));
        setLocationRelativeTo(null);
    }

    // Code for dispatching events from components to event handlers.

    public void actionPerformed(java.awt.event.ActionEvent evt) {
        if (evt.getSource() == jMenuItem_Tocar) {
            JPlayList.this.jMenuItem_TocarActionPerformed(evt);
        }
        else if (evt.getSource() == jMenuItem_Editar) {
            JPlayList.this.jMenuItem_EditarActionPerformed(evt);
        }
        else if (evt.getSource() == jMenuItem_ExcluirLista) {
            JPlayList.this.jMenuItem_ExcluirListaActionPerformed(evt);
        }
        else if (evt.getSource() == jMenuItemCopiarTudoPara) {
            JPlayList.this.jMenuItemCopiarTudoParaActionPerformed(evt);
        }
        else if (evt.getSource() == jButtonAbrir) {
            JPlayList.this.jButtonAbrirActionPerformed(evt);
        }
        else if (evt.getSource() == jButtonConsultar) {
            JPlayList.this.jButtonConsultarActionPerformed(evt);
        }
        else if (evt.getSource() == jButtonDeletar) {
            JPlayList.this.jButtonDeletarActionPerformed(evt);
        }
        else if (evt.getSource() == jButtonLimpar) {
            JPlayList.this.jButtonLimparActionPerformed(evt);
        }
        else if (evt.getSource() == jButtonSalvar) {
            JPlayList.this.jButtonSalvarActionPerformed(evt);
        }
        else if (evt.getSource() == jButtonExportar) {
            JPlayList.this.jButtonExportarActionPerformed(evt);
        }
        else if (evt.getSource() == jButtonAdicionar) {
            JPlayList.this.jButtonAdicionarActionPerformed(evt);
        }
        else if (evt.getSource() == jButtonRemover) {
            JPlayList.this.jButtonRemoverActionPerformed(evt);
        }
        else if (evt.getSource() == jMenuItemAbrirPlayList) {
            JPlayList.this.jMenuItemAbrirPlayListActionPerformed(evt);
        }
        else if (evt.getSource() == jMenuItemSalvar) {
            JPlayList.this.jMenuItemSalvarActionPerformed(evt);
        }
        else if (evt.getSource() == jMenuItemLimparLista) {
            JPlayList.this.jMenuItemLimparListaActionPerformed(evt);
        }
        else if (evt.getSource() == jMenuItemConsultar) {
            JPlayList.this.jMenuItemConsultarActionPerformed(evt);
        }
        else if (evt.getSource() == jMenuItemExcluirLista) {
            JPlayList.this.jMenuItemExcluirListaActionPerformed(evt);
        }
        else if (evt.getSource() == jMenuItemFechar) {
            JPlayList.this.jMenuItemFecharActionPerformed(evt);
        }
        else if (evt.getSource() == jMenuItem_NovaListaAutomatica) {
            JPlayList.this.jMenuItem_NovaListaAutomaticaActionPerformed(evt);
        }
        else if (evt.getSource() == jMenuItem_EditarListaAutomatica) {
            JPlayList.this.jMenuItem_EditarListaAutomaticaActionPerformed(evt);
        }
    }

    public void keyPressed(java.awt.event.KeyEvent evt) {
        if (evt.getSource() == jTable) {
            JPlayList.this.jTableKeyPressed(evt);
        }
        else if (evt.getSource() == jTextField_Pesquisa) {
            JPlayList.this.jTextField_PesquisaKeyPressed(evt);
        }
    }

    public void keyReleased(java.awt.event.KeyEvent evt) {
    }

    public void keyTyped(java.awt.event.KeyEvent evt) {
        if (evt.getSource() == jTextField_Pesquisa) {
            JPlayList.this.jTextField_PesquisaKeyTyped(evt);
        }
    }

    public void mouseClicked(java.awt.event.MouseEvent evt) {
        if (evt.getSource() == jTable) {
            JPlayList.this.jTableMouseClicked(evt);
        }
    }

    public void mouseEntered(java.awt.event.MouseEvent evt) {
    }

    public void mouseExited(java.awt.event.MouseEvent evt) {
    }

    public void mousePressed(java.awt.event.MouseEvent evt) {
        if (evt.getSource() == jTable) {
            JPlayList.this.jTableMousePressed(evt);
        }
    }

    public void mouseReleased(java.awt.event.MouseEvent evt) {
    }

    public void stateChanged(javax.swing.event.ChangeEvent evt) {
        if (evt.getSource() == jToggleButtonOpcoesLista) {
            JPlayList.this.jToggleButtonOpcoesListaStateChanged(evt);
        }
    }// </editor-fold>//GEN-END:initComponents

    private void jTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableMouseClicked
        switch (evt.getButton()) {
            case MouseEvent.BUTTON1:
                if (evt.getClickCount() == 2) {
                    tocarSelecionada();
                }
                break;
            case MouseEvent.BUTTON2:
                abrirPropriedades();
                break;
            case MouseEvent.BUTTON3:
                jPopupMenu_Playlist.show((Component) evt.getSource(), evt.getX(), evt.getY());
                break;
        }
}//GEN-LAST:event_jTableMouseClicked

    private void jTableKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTableKeyPressed
        onTableKeyPressed(evt);
}//GEN-LAST:event_jTableKeyPressed

    private void jToggleButtonOpcoesListaStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jToggleButtonOpcoesListaStateChanged
        jPanelOpcoesLista.setVisible(jToggleButtonOpcoesLista.isSelected());
    }//GEN-LAST:event_jToggleButtonOpcoesListaStateChanged

    private void jTextField_PesquisaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField_PesquisaKeyPressed
        int rowSelct = jTable.getSelectedRow();
        switch (evt.getKeyCode()) {
            case KeyEvent.VK_ESCAPE:
                jTextField_Pesquisa.setText("");
                break;
            case KeyEvent.VK_ENTER:
                Musica m = (Musica) jTable.getModel().getValueAt(rowSelct, 0);
                try {
                    carregador.abrir(m, 0, false);
                    //  tocarPausar(null);
                } catch (Exception ex) {
                    Logger.getLogger(JPlayList.class.getName()).log(Level.SEVERE, null, ex);
                }
                break;
            case KeyEvent.VK_DOWN:
                if (jTable.getSelectedRow() == jTable.getRowCount() - 1) {
                    jTable.getSelectionModel().setSelectionInterval(0, 0);
                } else {
                    jTable.getSelectionModel().setSelectionInterval(rowSelct + 1, rowSelct + 1);
                }
                break;
            case KeyEvent.VK_UP:
                if (jTable.getSelectedRow() == 0) {
                    jTable.getSelectionModel().setSelectionInterval(jTable.getRowCount() - 1, jTable.getRowCount() - 1);
                } else {
                    jTable.getSelectionModel().setSelectionInterval(rowSelct - 1, rowSelct - 1);
                }

                break;
            case KeyEvent.VK_SHIFT:
                jTable.requestFocus();
                break;
        }

    }//GEN-LAST:event_jTextField_PesquisaKeyPressed
    private void jTextField_PesquisaKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField_PesquisaKeyTyped
        ArrayList<Musica> novaLista = new ArrayList<Musica>(200);
        char keyChar = Character.toLowerCase(evt.getKeyChar());
        String textoFiltro;
        if ((keyChar >= 'a' && keyChar <= 'z') || (keyChar >= '0' && keyChar <= '9')) {
            textoFiltro = jTextField_Pesquisa.getText().toLowerCase() + keyChar;
        } else {
            textoFiltro = jTextField_Pesquisa.getText().toLowerCase();
        }
        for (Iterator<Musica> it = pesquisa.iterator(); it.hasNext();) {
            Musica m = it.next();
            if ((m.getNome() + m.getAutor()).toLowerCase().indexOf(textoFiltro) != -1) {
                novaLista.add(m);
            }
        }
        atualizarTabelaLista(novaLista);
        if (jTable.getRowCount() > 0) {
            jTable.setRowSelectionInterval(0, 0);
            //Estou aqui
        }
        jTextField_Pesquisa.requestFocus();
    }//GEN-LAST:event_jTextField_PesquisaKeyTyped

    private void jMenuItemCopiarTudoParaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemCopiarTudoParaActionPerformed
        if (jCopiador == null) {
            jCopiador = new JCopiador(this, false);
        }
        jCopiador.setMusicas(total);
        jCopiador.setNomePLayList(playlist.getNome());
        jCopiador.setVisible(true);
    }//GEN-LAST:event_jMenuItemCopiarTudoParaActionPerformed

    private void jMenuItem_EditarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem_EditarActionPerformed
        abrirPropriedades();
    }//GEN-LAST:event_jMenuItem_EditarActionPerformed

    private void jMenuItem_TocarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem_TocarActionPerformed
        tocarSelecionada();
    }//GEN-LAST:event_jMenuItem_TocarActionPerformed

    private void jMenuItem_ExcluirListaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem_ExcluirListaActionPerformed
        removerMusica();
    }//GEN-LAST:event_jMenuItem_ExcluirListaActionPerformed

    private void jTableMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableMousePressed
        if (evt.getButton() == MouseEvent.BUTTON3) {
            int linha = jTable.rowAtPoint(evt.getPoint());
            if (linha != -1) {
                jTable.setRowSelectionInterval(linha, linha);
            }
        }
    }//GEN-LAST:event_jTableMousePressed

    private void jButtonAbrirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAbrirActionPerformed
        openM3u();
    }//GEN-LAST:event_jButtonAbrirActionPerformed

    private void jButtonConsultarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonConsultarActionPerformed
        consultarPlayList();
    }//GEN-LAST:event_jButtonConsultarActionPerformed

    private void jButtonDeletarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDeletarActionPerformed
        deletar();
    }//GEN-LAST:event_jButtonDeletarActionPerformed

    private void jButtonLimparActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonLimparActionPerformed
        limpar();
    }//GEN-LAST:event_jButtonLimparActionPerformed

    private void jButtonSalvarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSalvarActionPerformed
        salvarPlayList();
    }//GEN-LAST:event_jButtonSalvarActionPerformed

    private void jButtonExportarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonExportarActionPerformed
        exportarPlayList();
    }//GEN-LAST:event_jButtonExportarActionPerformed

    private void jButtonAdicionarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAdicionarActionPerformed
        adicionarMusica();
    }//GEN-LAST:event_jButtonAdicionarActionPerformed

    private void jButtonRemoverActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonRemoverActionPerformed
        removerMusica();
    }//GEN-LAST:event_jButtonRemoverActionPerformed

    private void jMenuItemAbrirPlayListActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemAbrirPlayListActionPerformed
        openM3u();
    }//GEN-LAST:event_jMenuItemAbrirPlayListActionPerformed

    private void jMenuItemSalvarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemSalvarActionPerformed
        salvarPlayList();
    }//GEN-LAST:event_jMenuItemSalvarActionPerformed

    private void jMenuItemLimparListaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemLimparListaActionPerformed
        limpar();
    }//GEN-LAST:event_jMenuItemLimparListaActionPerformed

    private void jMenuItemConsultarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemConsultarActionPerformed
        consultarPlayList();
    }//GEN-LAST:event_jMenuItemConsultarActionPerformed

    private void jMenuItemExcluirListaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemExcluirListaActionPerformed
        deletar();
    }//GEN-LAST:event_jMenuItemExcluirListaActionPerformed

    private void jMenuItemFecharActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemFecharActionPerformed
        setVisible(false);
    }//GEN-LAST:event_jMenuItemFecharActionPerformed

    private void jMenuItem_NovaListaAutomaticaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem_NovaListaAutomaticaActionPerformed
        novaListaAutomatica();
    }//GEN-LAST:event_jMenuItem_NovaListaAutomaticaActionPerformed

    private void jMenuItem_EditarListaAutomaticaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem_EditarListaAutomaticaActionPerformed
        editarListaAutomatica();
    }//GEN-LAST:event_jMenuItem_EditarListaAutomaticaActionPerformed

    public void addMusica(Musica m) {
        if (playlist == null) {
            salvarPlaylistAtual();
        }
        try {
            PlayMusica playMusica = new PlayMusica();
            playMusica.setMusica(m);
            playMusica.setPlaylist(playlist);
            playMusica.setSeq(jTable.getModel().getRowCount());
            PlayMusicaBD.incluir(playMusica);
        } catch (Exception ex) {
            Logger.getLogger(JPlayList.class.getName()).log(Level.SEVERE, null, ex);
        }

        m.setNumero(jTable.getModel().getRowCount());
        Object[] row = new Object[1];
        row[0] = m;
        ((ModelReadOnly) jTable.getModel()).addRow(row);
        faltamTocar.add(m);
        total.add(m);
        pesquisa.add(m);
    }

    public void addMusicas(ArrayList<Musica> musicas) {
        addMusicas(musicas, jTable.getRowCount());
    }

    public void addMusicas(ArrayList<Musica> musicas, int posicaoInicial) {
        if (playlist == null) {
            salvarPlaylistAtual();
        }
        int posicao = posicaoInicial;
        Transacao t = new Transacao();
        try {
            t.begin();
            for (int i = 0; i < musicas.size(); i++) {
                Musica musica = musicas.get(i);
                PlayMusica playMusica = new PlayMusica();
                playMusica.setMusica(musica);
                playMusica.setPlaylist(playlist);
                playMusica.setSeq(jTable.getModel().getRowCount());
                PlayMusicaBD.incluir(playMusica, t);


                Object[] row = new Object[1];
                row[0] = musica;
                if (posicaoInicial == -1 || posicaoInicial >= jTable.getRowCount()) {
                    ((DefaultTableModel) jTable.getModel()).addRow(row);
                    faltamTocar.add(musica);
                    total.add(musica);
                    pesquisa.add(musica);
                    musica.setNumero(getUltimaPosicao());
                } else {
                    ((DefaultTableModel) jTable.getModel()).insertRow(posicao, row);
                    faltamTocar.add(posicao, musica);
                    total.add(posicao, musica);
                    pesquisa.add(posicao, musica);
                    musica.setNumero(posicao);
                    posicao++;
                }
            }
            t.commit();
        } catch (Exception ex) {
            t.rollback();
            Logger.getLogger(JPlayList.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private int getUltimaPosicao() {
        return faltamTocar.size() + jahFoi.size();
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonAbrir;
    private javax.swing.JButton jButtonAdicionar;
    private javax.swing.JButton jButtonConsultar;
    private javax.swing.JButton jButtonDeletar;
    private javax.swing.JButton jButtonExportar;
    private javax.swing.JButton jButtonLimpar;
    private javax.swing.JButton jButtonRemover;
    private javax.swing.JButton jButtonSalvar;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenu jMenuFuncoes;
    private javax.swing.JMenuItem jMenuItemAbrirPlayList;
    private javax.swing.JMenuItem jMenuItemConsultar;
    private javax.swing.JMenuItem jMenuItemCopiarTudoPara;
    private javax.swing.JMenuItem jMenuItemExcluirLista;
    private javax.swing.JMenuItem jMenuItemFechar;
    private javax.swing.JMenuItem jMenuItemLimparLista;
    private javax.swing.JMenuItem jMenuItemSalvar;
    private javax.swing.JMenuItem jMenuItem_Editar;
    private javax.swing.JMenuItem jMenuItem_EditarListaAutomatica;
    private javax.swing.JMenuItem jMenuItem_ExcluirLista;
    private javax.swing.JMenuItem jMenuItem_NovaListaAutomatica;
    private javax.swing.JMenuItem jMenuItem_Tocar;
    private javax.swing.JMenu jMenuListaAutomatica;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanelOpcoesLista;
    private javax.swing.JPopupMenu jPopupMenu_Playlist;
    private javax.swing.JScrollPane jScrollPane;
    private javax.swing.JTable jTable;
    private javax.swing.JTextField jTextField_NomePlayList;
    private javax.swing.JTextField jTextField_Pesquisa;
    private javax.swing.JToggleButton jToggleButtonOpcoesLista;
    // End of variables declaration//GEN-END:variables
}
