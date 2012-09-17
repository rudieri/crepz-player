package com.playlist;

import com.conexao.Transacao;
import com.config.Configuracaoes;
import com.fila.JFilaReproducao;
import com.main.Carregador;
import com.main.gui.JPrincipal;
import com.musica.*;
import com.playmusica.PlayMusica;
import com.playmusica.PlayMusicaBD;
import com.playmusica.PlayMusicaSC;
import com.utils.FileUtils;
import com.utils.model.ModelReadOnly;
import com.utils.model.objetcmodel.ObjectTransferable;
import com.utils.transferivel.TipoTransferenciaMusica;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.*;
import java.awt.event.KeyEvent;
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
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.DefaultTableModel;

/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
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

    private final SimpleDateFormat formatoPadraoData;
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
    private final Musiquera musiquera;
    private final Carregador carregador;
    private int contaErro = 0;
    private Playlist playlist;
    private ListSelectionListener listSelectionListener = new ListSelectionListener() {

        @Override
        public void valueChanged(ListSelectionEvent e) {
            if (jTable.getSelectedRow() == -1) {
                return;
            }
            try{
                jTable.scrollRectToVisible(jTable.getCellRect(jTable.getSelectedRow(), 0, false));
            }catch(Exception ex){
                ex.printStackTrace();
            }
        }
    };
    private DropTarget dropTargetPlayList;

    public JPlayList(Musiquera mus, Carregador carregador) {
        formatoPadraoData = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        initComponents();
        this.carregador = carregador;
        musiquera = mus;
        initTabelaLista();

        jPanelOpcoesLista.setVisible(false);
        setIconImage(carregador.icones.crepzIcon.getImage());

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
            tocar(playlist, false);
        } catch (Exception ex) {
            Logger.getLogger(JPlayList.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public Playlist getPlaylistAberta() {
        return playlist;
    }

    private void importarMusicas(File[] files) {
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
                //  s=s.substring(0,s.lastIndexOf("\\") );

                addMusica(MusicaGerencia.addFiles(s, t));
            }
        } catch (Exception ex) {
            Logger.getLogger(getClass().getName()).log(Level.ALL, "Erro ao adicionar música.", ex);
        }
    }

    private void tocarSelecionada() {
        try {
            Musica m = (Musica) jTable.getModel().getValueAt(jTable.getSelectedRow(), 0);
            //tocar(m);
            musiquera.abrir(m, 0, false);
            if (jahFoi.indexOf(m) != jahFoi.size() - 1) {
                jahFoi.add(m);
                faltamTocar.remove(m);
            }
        } catch (Exception ex) {
            Logger.getLogger(JPlayList.class.getName()).log(Level.SEVERE, null, ex);
        }
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
            ex.printStackTrace();
        }
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
        jTable.setDragEnabled(true);
        jTable.getSelectionModel().addListSelectionListener(listSelectionListener);
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
                        }else{
                            ((ModelReadOnly) jTable.getModel()).addRow(new Object[]{data});
                        }
                    } else if (data != null && data.getClass() == ArrayList.class) {
                        addMusicas((ArrayList) data, posicaoDestino);
                    } else {
                        //Windows
                        if (transferable.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                            ArrayList<File> arquivos = (ArrayList) transferable.getTransferData(java.awt.datatransfer.DataFlavor.javaFileListFlavor);
                            importarMusicas((File[])arquivos.toArray());
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
                                    importarMusicas((File[])arquivos.toArray());
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
                m.getMusica().setNumero(i);
                Object[] row = new Object[1];
                row[0] = m.getMusica();
                model.addRow(row);
                faltamTocar.add(m.getMusica());
                total.add(m.getMusica());

                pesquisa.add(m.getMusica());
            }
            jTable.requestFocus();
            if (jTable.getRowCount() > 0) {
                jTable.changeSelection(0, 0, false, false);
            }
            t.commit();
            setTitle(jTextField_NomePlayList.getText());

        } catch (Exception ex) {
            t.rollback();
            // JOptionPane.showMessageDialog(this, "Erro ao Filtrar \\õ/");
            ex.printStackTrace();
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
            ex.printStackTrace();
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

    public void setVisible(boolean b, boolean a) {
        super.setVisible(b);
        super.setAlwaysOnTop(a);
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
                jTable.setRowSelectionInterval(m.getNumero(), m.getNumero());
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
            Musica atual = musiquera.getMusica();
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
            Musica atual = musiquera.getMusica();
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
                if (jahFoi.indexOf(musiquera.getMusica()) > 0) {
                    return jahFoi.get(jahFoi.indexOf(musiquera.getMusica()) - 1);
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
                    Musica m = total.get(i);
                    String aux = m.getCaminho();
                    aux = aux.replace('/', File.separatorChar);
                    bfw.write(aux);
                    bfw.newLine();
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
            limpar();
        } catch (Exception ex) {
            t.rollback();
            ex.printStackTrace();
        }
    }

    public void limpar() {
        this.playlist = null;
        jTextField_NomePlayList.setText("");
//        initTabelaLista(false);
        ((ModelReadOnly) jTable.getModel()).setRowCount(0);
        faltamTocar = new ArrayList(200);
        total = new ArrayList(200);
    }

    public void tocar(Playlist playlist, boolean tocarMesmo) {
        this.playlist = playlist;
        try {
            jTextField_NomePlayList.setText(playlist.getNome());
            setTitle(playlist.getNome());
            atualizarTabelaLista();
            if (tocarMesmo) {
                musiquera.abrir(getProxima(false), 0, false);
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

            Musica m = MusicaGerencia.addFiles(in, t);
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

    private void openM3u() {
        JFileChooser jf = new JFileChooser();
        jf.setDialogType(JFileChooser.OPEN_DIALOG);
        jf.setFileSelectionMode(JFileChooser.FILES_ONLY);
        jf.setFileFilter(new FileFilter() {

            @Override
            public boolean accept(File f) {
                return f.isDirectory() || f.getName().toLowerCase().endsWith(".m3u");
            }

            @Override
            public String getDescription() {
                return "Arquivos de PlayList *.m3u";
            }
        });
        if (jf.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File f = jf.getSelectedFile();
            try {
                
                importarMusicas(FileUtils.lerM3u(f));
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

    public void filtraTexto(int code) {

        jTextEntrada.setVisible(true);
        repaint();
        // jPanel6.repaint();
        //jPanel3.repaint();
        jTextEntrada.requestFocus();
        jTextEntrada.setText("");
    }

    private void onTableKeyPressed(KeyEvent evt) {
        switch (evt.getKeyCode()) {
            case KeyEvent.VK_DELETE:
                jButtonRemoverActionPerformed(null);
                return;
            case KeyEvent.VK_PAGE_DOWN:
            case KeyEvent.VK_PAGE_UP:
            case KeyEvent.VK_END:
            case KeyEvent.VK_HOME:
            case KeyEvent.VK_SHIFT:
            case KeyEvent.VK_CONTROL:
                return;
            case KeyEvent.VK_ENTER:
                evt.setKeyCode(KeyEvent.VK_UNDEFINED);
                tocarSelecionada();
                break;
            case KeyEvent.VK_DOWN:
            case KeyEvent.VK_UP:
                if (!evt.isShiftDown()) {
                    filtraTexto(evt.getKeyCode());
                    evt.setKeyCode(KeyEvent.VK_UNDEFINED);
                }
                break;
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

        Conteto_Playlist = new javax.swing.JPopupMenu();
        cMenu_Tocar = new javax.swing.JMenuItem();
        cMenu_Editar_prop = new javax.swing.JMenuItem();
        cMenu_ExcluirLista = new javax.swing.JMenuItem();
        cMenu_ExcluirDisco = new javax.swing.JMenuItem();
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
        jTextEntrada = new javax.swing.JTextField();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItemAbrirPlayList = new javax.swing.JMenuItem();
        jMenuItemSalvar = new javax.swing.JMenuItem();
        jMenuItemLimparLista = new javax.swing.JMenuItem();
        jMenuItemConsultar = new javax.swing.JMenuItem();
        jMenuItemExcluirLista = new javax.swing.JMenuItem();
        jMenuItemFechar = new javax.swing.JMenuItem();

        cMenu_Tocar.setText("jMenuItem7");
        Conteto_Playlist.add(cMenu_Tocar);

        cMenu_Editar_prop.setText("jMenuItem7");
        Conteto_Playlist.add(cMenu_Editar_prop);

        cMenu_ExcluirLista.setText("jMenuItem7");
        Conteto_Playlist.add(cMenu_ExcluirLista);

        cMenu_ExcluirDisco.setText("jMenuItem7");
        Conteto_Playlist.add(cMenu_ExcluirDisco);

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
        jButtonSalvar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSalvarActionPerformed(evt);
            }
        });
        jPanel4.add(jButtonSalvar);

        jButtonExportar.setMnemonic('E');
        jButtonExportar.setText("Expotar M3u");
        jButtonExportar.setToolTipText("Exporta lista como um arquivo M3U");
        jButtonExportar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonExportarActionPerformed(evt);
            }
        });
        jPanel4.add(jButtonExportar);

        jPanel8.add(jPanel4);

        jPanel5.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 5, 4));

        jButtonAbrir.setText("Abrir M3U");
        jButtonAbrir.setToolTipText("Abre um arquivo m3u");
        jButtonAbrir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAbrirActionPerformed(evt);
            }
        });
        jPanel5.add(jButtonAbrir);

        jButtonConsultar.setText("Consultar Lista");
        jButtonConsultar.setToolTipText("Consulta na lista salva internamente.");
        jButtonConsultar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonConsultarActionPerformed(evt);
            }
        });
        jPanel5.add(jButtonConsultar);

        jButtonDeletar.setText("Deletar");
        jButtonDeletar.setToolTipText("Excluir lista salva internamente.");
        jButtonDeletar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDeletarActionPerformed(evt);
            }
        });
        jPanel5.add(jButtonDeletar);

        jButtonLimpar.setText("Limpar");
        jButtonLimpar.setToolTipText("Limpa a lista...");
        jButtonLimpar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonLimparActionPerformed(evt);
            }
        });
        jPanel5.add(jButtonLimpar);

        jPanel8.add(jPanel5);

        jPanelOpcoesLista.add(jPanel8, java.awt.BorderLayout.CENTER);

        getContentPane().add(jPanelOpcoesLista, java.awt.BorderLayout.PAGE_START);

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

        jButtonAdicionar.setText("+");
        jButtonAdicionar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAdicionarActionPerformed(evt);
            }
        });
        jPanel6.add(jButtonAdicionar);

        jButtonRemover.setText("-");
        jButtonRemover.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonRemoverActionPerformed(evt);
            }
        });
        jPanel6.add(jButtonRemover);

        jToggleButtonOpcoesLista.setMnemonic('o');
        jToggleButtonOpcoesLista.setText("Opções da lista");
        jToggleButtonOpcoesLista.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jToggleButtonOpcoesListaStateChanged(evt);
            }
        });
        jPanel6.add(jToggleButtonOpcoesLista);

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

        jMenu1.setMnemonic('F');
        jMenu1.setText("Funções");

        jMenuItemAbrirPlayList.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_A, java.awt.event.InputEvent.ALT_MASK));
        jMenuItemAbrirPlayList.setText("Abrir PlayList *.m3u");
        jMenuItemAbrirPlayList.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemAbrirPlayListActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItemAbrirPlayList);

        jMenuItemSalvar.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItemSalvar.setText("Salvar");
        jMenuItemSalvar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemSalvarActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItemSalvar);

        jMenuItemLimparLista.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_L, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItemLimparLista.setText("Limpar");
        jMenuItemLimparLista.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemLimparListaActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItemLimparLista);

        jMenuItemConsultar.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_P, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItemConsultar.setText("Consultar Lista");
        jMenuItemConsultar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemConsultarActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItemConsultar);

        jMenuItemExcluirLista.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_DELETE, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItemExcluirLista.setText("Excluir Lista");
        jMenuItemExcluirLista.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemExcluirListaActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItemExcluirLista);

        jMenuItemFechar.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ESCAPE, 0));
        jMenuItemFechar.setText("Fechar");
        jMenuItemFechar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemFecharActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItemFechar);

        jMenuBar1.add(jMenu1);

        setJMenuBar(jMenuBar1);

        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        setBounds((screenSize.width-494)/2, (screenSize.height-495)/2, 494, 495);
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonRemoverActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonRemoverActionPerformed
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


    }//GEN-LAST:event_jButtonRemoverActionPerformed

    private void jButtonAdicionarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAdicionarActionPerformed
        adicionarMusica();
    }//GEN-LAST:event_jButtonAdicionarActionPerformed

    private void jTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableMouseClicked
        if (evt.getClickCount() == 2) {
            tocarSelecionada();

        }
}//GEN-LAST:event_jTableMouseClicked

    private void jTableKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTableKeyPressed
        onTableKeyPressed(evt);
        System.out.println("Code: " + evt.getKeyCode());
}//GEN-LAST:event_jTableKeyPressed

    private void jScrollPaneFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jScrollPaneFocusGained
        jTable.requestFocus();
        jTable.changeSelection(0, 0, false, false);
}//GEN-LAST:event_jScrollPaneFocusGained

    private void jButtonDeletarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDeletarActionPerformed
        deletar();
    }//GEN-LAST:event_jButtonDeletarActionPerformed

    private void jButtonLimparActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonLimparActionPerformed
        limpar();
    }//GEN-LAST:event_jButtonLimparActionPerformed

    private void jButtonConsultarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonConsultarActionPerformed
        JSelectPlaylists jPlayLists = new JSelectPlaylists(principal, true, this);
        jPlayLists.setVisible(true);
    }//GEN-LAST:event_jButtonConsultarActionPerformed

    private void jMenuItemConsultarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemConsultarActionPerformed
        jButtonConsultarActionPerformed(evt);
}//GEN-LAST:event_jMenuItemConsultarActionPerformed

    private void jMenuItemLimparListaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemLimparListaActionPerformed
        limpar();
}//GEN-LAST:event_jMenuItemLimparListaActionPerformed

    private void jMenuItemExcluirListaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemExcluirListaActionPerformed
        deletar();
    }//GEN-LAST:event_jMenuItemExcluirListaActionPerformed

    private void jMenuItemSalvarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemSalvarActionPerformed
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


    }//GEN-LAST:event_jMenuItemSalvarActionPerformed

    private void jMenuItemFecharActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemFecharActionPerformed
        setVisible(false);
    }//GEN-LAST:event_jMenuItemFecharActionPerformed

    private void jButtonAbrirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAbrirActionPerformed
        // TODO add your handling code here:
        openM3u();
    }//GEN-LAST:event_jButtonAbrirActionPerformed

    private void jMenuItemAbrirPlayListActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemAbrirPlayListActionPerformed
        // TODO add your handling code here:
        openM3u();
    }//GEN-LAST:event_jMenuItemAbrirPlayListActionPerformed

    private void jToggleButtonOpcoesListaStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jToggleButtonOpcoesListaStateChanged
        // TODO add your handling code here:
        jPanelOpcoesLista.setVisible(jToggleButtonOpcoesLista.isSelected());
    }//GEN-LAST:event_jToggleButtonOpcoesListaStateChanged

    private void jTextEntradaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextEntradaKeyPressed
        // TODO add your handling code here:
        switch (evt.getKeyCode()) {
            case KeyEvent.VK_ESCAPE:
                jTextEntrada.setText("");
                break;
            case KeyEvent.VK_ENTER:
                Musica m = (Musica) jTable.getModel().getValueAt(jTable.getSelectedRow(), 0);
                try {
                    musiquera.abrir(m, 0, false);
                    //  tocarPausar(null);
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
            ArrayList<Musica> novaLista = new ArrayList<Musica>(200);
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

    private void jButtonSalvarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSalvarActionPerformed
        jMenuItemSalvarActionPerformed(evt);
    }//GEN-LAST:event_jButtonSalvarActionPerformed

    private void jButtonExportarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonExportarActionPerformed
        exportarPlayList();
    }//GEN-LAST:event_jButtonExportarActionPerformed

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
                }else{
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
    private javax.swing.JPopupMenu Conteto_Playlist;
    private javax.swing.JMenuItem cMenu_Editar_prop;
    private javax.swing.JMenuItem cMenu_ExcluirDisco;
    private javax.swing.JMenuItem cMenu_ExcluirLista;
    private javax.swing.JMenuItem cMenu_Tocar;
    private javax.swing.JButton jButtonAbrir;
    private javax.swing.JButton jButtonAdicionar;
    private javax.swing.JButton jButtonConsultar;
    private javax.swing.JButton jButtonDeletar;
    private javax.swing.JButton jButtonExportar;
    private javax.swing.JButton jButtonLimpar;
    private javax.swing.JButton jButtonRemover;
    private javax.swing.JButton jButtonSalvar;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItemAbrirPlayList;
    private javax.swing.JMenuItem jMenuItemConsultar;
    private javax.swing.JMenuItem jMenuItemExcluirLista;
    private javax.swing.JMenuItem jMenuItemFechar;
    private javax.swing.JMenuItem jMenuItemLimparLista;
    private javax.swing.JMenuItem jMenuItemSalvar;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanelOpcoesLista;
    private javax.swing.JScrollPane jScrollPane;
    private javax.swing.JTable jTable;
    private javax.swing.JTextField jTextEntrada;
    private javax.swing.JTextField jTextField_NomePlayList;
    private javax.swing.JToggleButton jToggleButtonOpcoesLista;
    // End of variables declaration//GEN-END:variables
}
