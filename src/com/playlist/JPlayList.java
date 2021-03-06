package com.playlist;

import com.config.Configuracoes;
import com.copiador.JCopiador;
import com.fila.JFilaReproducao;
import com.main.Carregador;
import com.main.gui.JMP3Propriedades;
import com.main.gui.JPrincipal;
import com.musica.MusicaGerencia;
import com.musica.MusicaS;
import com.playlist.listainteligente.condicao.JListaInteligenteEditor;
import com.serial.PortaCDs;
import com.utils.file.FileUtils;
import com.utils.file.FiltroArquivoGenerico;
import com.utils.model.tablemodel.ObjectTableModel;
import com.utils.model.tablemodel.ObjectTransferable;
import com.utils.transferivel.TipoTransferenciaMusica;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.TransferHandler;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;


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
    private final Carregador carregador;
    private int contaErro = 0;
    private PlaylistI playlist;
    private DropTarget dropTargetPlayList;
    private JCopiador jCopiador;
    private ObjectTableModel<MusicaS> objectTableModel;

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
        jTextField_Pesquisa.getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent e) {
                objectTableModel.setFiltro(jTextField_Pesquisa.getText());
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                objectTableModel.setFiltro(jTextField_Pesquisa.getText());
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                objectTableModel.setFiltro(jTextField_Pesquisa.getText());
            }
        });
        
    }

    public void setPlayListAberta(String nome) {
        if (nome == null) {
            playlist = null;
            return;
        }
        try {
            playlist = PortaCDs.getPlaylist(nome, true, PlaylistS.class);
            abrir(playlist);
        } catch (Exception ex) {
            Logger.getLogger(JPlayList.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public PlaylistI getPlaylistAberta() {
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
        try {
            for (File s : files) {
                MusicaS musica = MusicaGerencia.addOneFile(s);
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
            MusicaS m = (MusicaS) jTable.getModel().getValueAt(jTable.getSelectedRow(), 0);
            carregador.abrir(m, 0, false);
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

        objectTableModel = new ObjectTableModel<MusicaS>(MusicaS.class, false);
        jTable.setModel(objectTableModel);
       
        jTable.setAutoCreateRowSorter(false);
        jTable.setShowVerticalLines(true);
        jTable.setEditingColumn(-1);
        jTable.setEditingRow(-1);

        jTable.setDefaultRenderer(MusicaS.class, new PlayListRenderer());
        jTable.setIntercellSpacing(new Dimension(1, 2));
        jTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        jTable.setShowHorizontalLines(false);
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
                ArrayList<MusicaS> musicas = new ArrayList<MusicaS>(rows.length);
                for (int i = 0; i < rows.length; i++) {
                    musicas.add((MusicaS) jTable.getModel().getValueAt(rows[i], 0));
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
                    if (data != null && data.getClass() == MusicaS.class) {
                        if (posicaoDestino != -1) {
                            ((ObjectTableModel<MusicaS>) jTable.getModel()).insertItem((MusicaS)data, posicaoDestino);
                        } else {
                            ((ObjectTableModel<MusicaS>)jTable.getModel()).addItem((MusicaS) data);
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
                            for (DataFlavor flavor : flavors) {
                                if (flavor.isRepresentationClassReader()) {
                                    dtde.acceptDrop(java.awt.dnd.DnDConstants.ACTION_COPY);
                                    Reader reader = flavor.getReaderForText(transferable);
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
                                    break;
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
        if (jTextField_NomePlayList.getText().isEmpty()) {
            return;
        }
        try {
            ObjectTableModel<MusicaS> model = (ObjectTableModel<MusicaS>) jTable.getModel();
            model.clear();
            if (playlist == null) {
                return;
            }
            // Filtro...
            if (playlist instanceof PlaylistC) {
                ((PlaylistC) playlist).updateMusicas();
            }
            objectTableModel.setItens(playlist.getMusicas());

            jTable.requestFocus();
            if (jTable.getRowCount() > 0) {
                jTable.changeSelection(0, 0, false, false);
            }
            setTitle(jTextField_NomePlayList.getText()
                    + (playlist instanceof PlaylistC ? " (Lista Automática)" : ""));

        } catch (Exception ex) {
            ex.printStackTrace(System.err);
        }
    }

    public void atualizarTabelaLista(ArrayList novaLista) {
        try {

         objectTableModel.setItens(playlist.getMusicas());
         
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
        for (Integer start : intervalos.keySet()) {
            int end = intervalos.get(start);
            ((ObjectTableModel) jTable.getModel()).moveRow(start.intValue(), end, novaPosicao);
            novaPosicao += (end - start + 1);
        }
        jTable.clearSelection();
    }

    public void setVisible(boolean visivel, boolean sempreNoTopo) {
        super.setVisible(visivel);
        super.setAlwaysOnTop(sempreNoTopo);
    }

    public MusicaS getAleatorio(MusicaS atual) {
//        if (Configuracoes.REPEAT_ATIVO.getValor()) {
//            return getAleatorio(atual);
//        } else {
//                return null;
//        }

        int random = (int) (Math.random() * objectTableModel.getRowCount());
        MusicaS m = objectTableModel.getItem(random);
        jTable.setRowSelectionInterval(random, random);
        return m;

    }

    public MusicaS getProxima() {
        return getProxima(false);
    }

    private MusicaS getProxima(boolean erro) {
        if (objectTableModel.getRowCount() == 0) {
            return null;
        }
        if (!erro) {
            contaErro = 0;
        } else {
            contaErro++;
        }
      
        if (contaErro > objectTableModel.getRowCount()) {
            JOptionPane.showMessageDialog(this, "Nenhum arquivo foi encontrado... Você montou sua unidades?");
            contaErro = 0;
            return null;
        }
        try {
            MusicaS atual = carregador.getMusica();
            int mAtual = -1;
            if (!Configuracoes.RANDOM_ATIVO.getValor()) {
                if (atual != null) {
                    mAtual = atual.getNumero();
                }

                if (mAtual < 0) {
                    jTable.setRowSelectionInterval(0, 0);
                    return (MusicaS) jTable.getModel().getValueAt(0, 0);
                }

                if (mAtual + 1 >= jTable.getRowCount()) {
                    jTable.setRowSelectionInterval(0, 0);
                    return (MusicaS) jTable.getModel().getValueAt(0, 0);

                } else {
                    jTable.setRowSelectionInterval(mAtual + 1, mAtual + 1);
                    return (MusicaS) jTable.getModel().getValueAt(mAtual + 1, 0);

                }
            } else {
                return getAleatorio(atual);
            }

        } catch (Exception ex) {
            Logger.getLogger(JPlayList.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }

    }

    public MusicaS getAnterior() {
        try {
            MusicaS atual = carregador.getMusica();
            int mAtual;
            if (!Configuracoes.RANDOM_ATIVO.getValor()) {
                mAtual = atual.getNumero();
                if (mAtual > -1) {
                    if (mAtual - 1 < 0) {
                        jTable.setRowSelectionInterval(mAtual - 1, mAtual - 1);
                        return (MusicaS) jTable.getModel().getValueAt(jTable.getRowCount() - 1, 0);
                    } else {
                        jTable.setRowSelectionInterval(mAtual - 1, mAtual - 1);
                        return (MusicaS) jTable.getModel().getValueAt(mAtual - 1, 0);
                    }
                }
                System.out.println("Não tem mais musicas, retornando null.");
                return null;
            } else {
                if (objectTableModel.indexOf(carregador.getMusica()) > 0) {
                    MusicaS musica = objectTableModel.getItem(objectTableModel.indexOf(carregador.getMusica()) - 1);
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

    public void selecionarMusica(MusicaS musica) {
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
                for (int i = 0; i < objectTableModel.getRowCount(); i++) {
                    MusicaS m = objectTableModel.getItem(i);
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
        try {
            playlist = PortaCDs.getPlaylist(jTextField_NomePlayList.getText(), true, PlaylistS.class);
            playlist.getMusicas().clear();
            playlist.getMusicas().addAll(objectTableModel.getItens());

            setTitle(jTextField_NomePlayList.getText());
        } catch (Exception ex) {
            ex.printStackTrace(System.err);
        }

    }

    private void deletar() {
        try {
            PortaCDs.removerPlaylist(playlist);
            limpar();
        } catch (Exception ex) {
            ex.printStackTrace(System.err);
        }
    }

    public void limpar() {
        this.playlist = null;
        jTextField_NomePlayList.setText("");
        setTitle("Lista de Reprodução - Vazia");
//        initTabelaLista(false);
        ((ObjectTableModel) jTable.getModel()).clear();
    }

    public void abrir(PlaylistI playlist) {
        this.playlist = playlist;
        boolean listaNormal = playlist instanceof PlaylistS;
        jMenuItem_ExcluirLista.setEnabled(listaNormal);
        jMenuItemSalvar.setEnabled(listaNormal);
        jButtonAdicionar.setEnabled(listaNormal);
        jButtonRemover.setEnabled(listaNormal);
        jButtonSalvar.setEnabled(listaNormal);


        jTextField_NomePlayList.setText(playlist.getNome());
        setTitle(playlist.getNome());
        atualizarTabelaLista();

    }

    public void tocar(PlaylistI playlist) {
        try {
            abrir(playlist);
            carregador.abrir(getProxima(false), 0, false);
        } catch (Exception ex) {
            Logger.getLogger(JPlayList.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void adicionarMusica() {
        try {
            File in = principal.telaAbrirArquivo();


            MusicaS m = MusicaGerencia.addOneFile(in);
            addMusica(m);


        } catch (Exception ex) {
            Logger.getLogger(JPlayList.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void removerMusica() {
        if (playlist instanceof PlaylistC) {
            JOptionPane.showMessageDialog(this, "Essa lista é automática!"
                    + "\nAltere as condições para remover ou adicionar músicas.");
            return;
        }
        
        int selecteds[] = jTable.getSelectedRows();
        objectTableModel.removeItem(selecteds);
    }

    private void openM3u() {
        JFileChooser jf = new JFileChooser();
        jf.setDialogType(JFileChooser.OPEN_DIALOG);
        jf.setFileSelectionMode(JFileChooser.FILES_ONLY);
        jf.setFileFilter(FiltroArquivoGenerico.FILTRO_PLAYLIST);
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
            PlaylistI novaPlaylist = PortaCDs.getPlaylist(jTextField_NomePlayList.getText(), false, PlaylistS.class);
            if (novaPlaylist != null && novaPlaylist != playlist) {
                JOptionPane.showMessageDialog(this, "Já existe uma lista de reprodução com esse nome.\nColoque um nome diferente.");
                return ;
            }
            if (playlist != null) {
                playlist.setNome(jTextField_NomePlayList.getText());
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
        editor.setPlaylist((PlaylistC) playlist);
        editor.setVisible(true);
        if (editor.getPlaylist() != null) {
            abrir(editor.getPlaylist());
        }
    }

    private void abrirPropriedades() {
        int linha = jTable.getSelectedRow();
        MusicaS musica = (MusicaS) jTable.getValueAt(linha, 0);
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
        if (evt.getSource() == jButtonSalvar) {
            JPlayList.this.jButtonSalvarActionPerformed(evt);
        }
        else if (evt.getSource() == jButtonExportar) {
            JPlayList.this.jButtonExportarActionPerformed(evt);
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
        else if (evt.getSource() == jButtonAdicionar) {
            JPlayList.this.jButtonAdicionarActionPerformed(evt);
        }
        else if (evt.getSource() == jButtonRemover) {
            JPlayList.this.jButtonRemoverActionPerformed(evt);
        }
        else if (evt.getSource() == jMenuItem_Tocar) {
            JPlayList.this.jMenuItem_TocarActionPerformed(evt);
        }
        else if (evt.getSource() == jMenuItem_Editar) {
            JPlayList.this.jMenuItem_EditarActionPerformed(evt);
        }
        else if (evt.getSource() == jMenuItem_ExcluirLista) {
            JPlayList.this.jMenuItem_ExcluirListaActionPerformed(evt);
        }
        else if (evt.getSource() == jMenuItemAbrirPlayList) {
            JPlayList.this.jMenuItemAbrirPlayListActionPerformed(evt);
        }
        else if (evt.getSource() == jMenuItemCopiarTudoPara) {
            JPlayList.this.jMenuItemCopiarTudoParaActionPerformed(evt);
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
                MusicaS m = ((ObjectTableModel<MusicaS>)jTable.getModel()).getItem(rowSelct);
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

    private void jMenuItemCopiarTudoParaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemCopiarTudoParaActionPerformed
        if (jCopiador == null) {
            jCopiador = new JCopiador(this, false);
        }
        jCopiador.setMusicas(objectTableModel.getItens());
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

    public void addMusica(MusicaS m) {
       objectTableModel.addItem(m);
        if (playlist == null) {
            salvarPlaylistAtual();
        }else{
            playlist.getMusicas().add(m);
        }
       
    }

    public void addMusicas(ArrayList<MusicaS> musicas) {
        addMusicas(musicas, jTable.getRowCount());
    }

    public void addMusicas(ArrayList<MusicaS> musicas, int posicaoInicial) {
        if (playlist == null) {
            salvarPlaylistAtual();
        }
        
        try {
            if (posicaoInicial == -1) {
                playlist.getMusicas().addAll(musicas);
            }else{
                playlist.getMusicas().addAll(posicaoInicial, musicas);
            }
            objectTableModel.setItens(playlist.getMusicas());
        } catch (Exception ex) {
            Logger.getLogger(JPlayList.class.getName()).log(Level.SEVERE, null, ex);
        }

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
