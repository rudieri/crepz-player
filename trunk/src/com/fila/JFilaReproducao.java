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
import com.main.Carregador;
import com.main.Notificavel;
import com.musica.ModelReadOnly;
import com.musica.Musica;
import com.musica.MusicaBD;
import com.musica.MusicaGerencia;
import com.musica.MusicaSC;
import com.utils.DiretorioUtils;
import com.utils.Warning;
import com.utils.model.ObjectTableModel;
import com.utils.model.ObjectTableModelListener;
import com.utils.model.ObjectTransferable;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.InvalidDnDOperationException;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DropMode;
import javax.swing.JComponent;
import javax.swing.TransferHandler;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
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
    }

    private void atualizarBarraStatus() {
        jLabelQtdMusicas.setText(String.valueOf(objModelMusicas.getRowCount()));
    }

//    private void alterarMusica(Musica musica) {
//        try {
////            int musicasBanco = MusicaBD.contarMusicas();
////            if (musicasBanco != objModelMusicas.getRowCount()) {
////                atualizaTabelaMusica();
////            } else {
//            MusicaBD.carregar(musica);
//            objModelMusicas.atualizarItem(musica, jTableMusicas.getSelectedRow());
////            }
//        } catch (Exception ex) {
//            Logger.getLogger(JFilaReproducao.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }
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
        // C�digo do C�o...
        jScrollPaneFila.setDropTarget(new DropTarget(jScrollPaneFila, new DropTargetAdapter() {

            @Override
            public void drop(DropTargetDropEvent dtde) {
                try {
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
                        System.out.println(data);
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
                                    // Say we'll take it.
                                    //evt.acceptDrop ( java.awt.dnd.DnDConstants.ACTION_COPY_OR_MOVE );
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
//                    System.out.println(transferable);
                } catch (UnsupportedFlavorException ex) {
                    Logger.getLogger(JFilaReproducao.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(JFilaReproducao.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            @Override
            public void dragEnter(DropTargetDragEvent evt) {
                System.out.println("Drag enter...");
                evt.acceptDrag(DnDConstants.ACTION_COPY);
            }
        }));
        jTableFila.setModel(modelFila);
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
    }

    private void initTabelaMusica() {
        objModelMusicas = new ObjectTableModel<Musica>(Musica.class);
        jTableMusicas.setModel(objModelMusicas);
        objModelMusicas.addObjectTableModelListener(new ObjectTableModelListener() {

            @Override
            public void sizeChanged(int newSize) {
                atualizarBarraStatus();
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
            atualizarBarraStatus();
        } catch (Exception ex) {
            Logger.getLogger(JFilaReproducao.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Musica getProxima() {
        if (modelFila.getRowCount() > 0) {
            Musica musica = (Musica) modelFila.getValueAt(0, 0);
            modelFila.removeRow(0);
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
        jLabelTocando.setText(propriedadesMusica.getNome());
        int indexOf = objModelMusicas.indexOf(musiquera.getMusica());
        musiquera.getMusica().setTempo(propriedadesMusica.getTempoTotal());
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
    }

    @Override
    public void tempoEhHMS(String hms) {
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

        jPanelCentro = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jTextFieldPesquisa = new javax.swing.JTextField();
        jButtonLimparPesquisa = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTableMusicas = new javax.swing.JTable();
        jPanelStatus = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabelQtdMusicas = new javax.swing.JLabel();
        jPanel_Esquerdo = new javax.swing.JPanel();
        jScrollPaneFila = new javax.swing.JScrollPane();
        jTableFila = new javax.swing.JTable();
        jPanel5 = new javax.swing.JPanel();
        jLabelTocando = new javax.swing.JLabel();
        jPanelProgress = new javax.swing.JPanel();
        jProgressBarImportando = new javax.swing.JProgressBar();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenu2 = new javax.swing.JMenu();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Fila de Reprodu��o");

        jPanelCentro.setBorder(javax.swing.BorderFactory.createTitledBorder("Musicas"));
        jPanelCentro.setLayout(new java.awt.BorderLayout());

        jPanel2.setLayout(new java.awt.BorderLayout());

        jPanel1.setLayout(new javax.swing.BoxLayout(jPanel1, javax.swing.BoxLayout.Y_AXIS));

        jPanel3.setPreferredSize(new java.awt.Dimension(483, 30));
        jPanel3.setLayout(new javax.swing.BoxLayout(jPanel3, javax.swing.BoxLayout.LINE_AXIS));

        jLabel1.setText("Pesquisar:");
        jLabel1.setPreferredSize(new java.awt.Dimension(100, 18));
        jPanel3.add(jLabel1);

        jTextFieldPesquisa.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextFieldPesquisaKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextFieldPesquisaKeyTyped(evt);
            }
        });
        jPanel3.add(jTextFieldPesquisa);

        jButtonLimparPesquisa.setText("Limpar");
        jPanel3.add(jButtonLimparPesquisa);

        jPanel1.add(jPanel3);

        jPanel4.setPreferredSize(new java.awt.Dimension(483, 100));
        jPanel4.setLayout(new java.awt.BorderLayout());
        jPanel1.add(jPanel4);

        jPanel2.add(jPanel1, java.awt.BorderLayout.CENTER);

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
        });
        jTableFila.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTableFilaKeyPressed(evt);
            }
        });
        jScrollPaneFila.setViewportView(jTableFila);

        jPanel_Esquerdo.add(jScrollPaneFila, java.awt.BorderLayout.CENTER);

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder("Tocando Agora..."));
        jPanel5.setLayout(new java.awt.BorderLayout());

        jLabelTocando.setFont(new java.awt.Font("Dialog", 0, 11));
        jLabelTocando.setText("Nada...");
        jLabelTocando.setPreferredSize(new java.awt.Dimension(0, 18));
        jPanel5.add(jLabelTocando, java.awt.BorderLayout.CENTER);

        jPanel_Esquerdo.add(jPanel5, java.awt.BorderLayout.PAGE_START);

        jPanelProgress.setPreferredSize(new java.awt.Dimension(148, 20));
        jPanelProgress.setLayout(new java.awt.BorderLayout());
        jPanelProgress.add(jProgressBarImportando, java.awt.BorderLayout.CENTER);

        jPanel_Esquerdo.add(jPanelProgress, java.awt.BorderLayout.PAGE_END);

        getContentPane().add(jPanel_Esquerdo, java.awt.BorderLayout.WEST);

        jMenu1.setText("File");
        jMenuBar1.add(jMenu1);

        jMenu2.setText("Edit");
        jMenuBar1.add(jMenu2);

        setJMenuBar(jMenuBar1);

        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        setBounds((screenSize.width-800)/2, (screenSize.height-600)/2, 800, 600);
    }// </editor-fold>//GEN-END:initComponents

    private void jTableMusicasMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableMusicasMouseClicked
        if (evt.getClickCount() > 1) {
            tocarMusicaSelecionada();
//            alterarMusica(musica);
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
        if (evt.getClickCount() == 2) {
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
        }
    }//GEN-LAST:event_jTableMusicasKeyPressed

    private void jTableFilaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTableFilaKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            tocarMusicaSelecionadaFila();
        }
    }//GEN-LAST:event_jTableFilaKeyPressed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(JFilaReproducao.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(JFilaReproducao.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(JFilaReproducao.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(JFilaReproducao.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                new JFilaReproducao(null, null).setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonLimparPesquisa;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabelQtdMusicas;
    private javax.swing.JLabel jLabelTocando;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanelCentro;
    private javax.swing.JPanel jPanelProgress;
    private javax.swing.JPanel jPanelStatus;
    private javax.swing.JPanel jPanel_Esquerdo;
    private javax.swing.JProgressBar jProgressBarImportando;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPaneFila;
    private javax.swing.JTable jTableFila;
    private javax.swing.JTable jTableMusicas;
    private javax.swing.JTextField jTextFieldPesquisa;
    // End of variables declaration//GEN-END:variables
}
