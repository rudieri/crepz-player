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
import com.main.Carregador;
import com.main.Notificavel;
import com.musica.ModelReadOnly;
import com.musica.Musica;
import com.musica.MusicaBD;
import com.musica.MusicaGerencia;
import com.musica.MusicaSC;
import com.utils.model.ObjectTableModel;
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
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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
public class JFilaReproducao extends javax.swing.JFrame implements Notificavel{

//    private ModelReadOnly modelMusicas;
    private ObjectTableModel<Musica> objModelMusicas;
    private final Musiquera musiquera;
    private ModelReadOnly modelFila;
    private final Carregador carregador;
    private TableRowSorter sorterMusicas;

    /** Creates new form JFilaReproducao */
    public JFilaReproducao(Musiquera musiquera, Carregador carregador) {
        initComponents();
        initTabelaFila();
        initTabelaMusica();
        atualizaTabelaMusica();
        this.musiquera = musiquera;

//        TransferHandler transferHandler = new TransferHandler(null);
//       jTableFila.setDropMode(DropMode.ON);
//        jTableFila.setTransferHandler(transferHandler);
        this.carregador = carregador;
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
        jScrollPaneFila.setDropTarget(new DropTarget(jScrollPaneFila, new DropTargetAdapter() {

            @Override
            public void drop(DropTargetDropEvent dtde) {
                try {
                    Transferable transferable = dtde.getTransferable();
                    DataFlavor[] flavors = transferable.getTransferDataFlavors();
                    Object data = transferable.getTransferData(transferable.getTransferDataFlavors()[0]);
                    if (data instanceof Musica) {
                        System.out.println(data);
                        ((ModelReadOnly) jTableFila.getModel()).addRow(new Object[]{data});
                    } else if (data instanceof ArrayList) {
                        for (int i = 0; i < ((ArrayList) data).size(); i++) {
                            Musica musica = ((ArrayList<Musica>) data).get(i);
                            ((ModelReadOnly) jTableFila.getModel()).addRow(new Object[]{musica});
                        }
                    }
//                    transferable.getTransferData(dtde.getCurrentDataFlavors()[0]);
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

    private void initTabelaMusica() {
        objModelMusicas = new ObjectTableModel<Musica>(Musica.class);
        jTableMusicas.setModel(objModelMusicas);
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
            objModelMusicas.clear();
            ArrayList<Musica> listar = MusicaBD.listar(filtro);
            for (int i = 0; i < listar.size(); i++) {
                Musica musica = listar.get(i);
                objModelMusicas.addItem(musica);
//                Object[] row = new Object[modelMusicas.getColumnCount()];
//                row[0] = musica;
            }
            jTableMusicas.setModel(objModelMusicas);
            jTableMusicas.repaint();
        } catch (Exception ex) {
            Logger.getLogger(JFilaReproducao.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Musica getProxima() {
        if (modelFila.getRowCount() > 0) {
            Musica musica = (Musica) modelFila.getValueAt(0, 0);
            modelFila.removeRow(0);
            return musica;
        }
        return null;
    }

    @Override
    public void atualizaLabels(String nome, int bits, String tempo, int freq) {
        jLabelTocando.setText(nome);
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
        jPanel_Esquerdo = new javax.swing.JPanel();
        jScrollPaneFila = new javax.swing.JScrollPane();
        jTableFila = new javax.swing.JTable();
        jPanel5 = new javax.swing.JPanel();
        jLabelTocando = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenu2 = new javax.swing.JMenu();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

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
        jScrollPane1.setViewportView(jTableMusicas);

        jPanelCentro.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        getContentPane().add(jPanelCentro, java.awt.BorderLayout.CENTER);

        jPanel_Esquerdo.setBorder(javax.swing.BorderFactory.createTitledBorder("Fila de Reprodução"));
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
        jScrollPaneFila.setViewportView(jTableFila);

        jPanel_Esquerdo.add(jScrollPaneFila, java.awt.BorderLayout.CENTER);

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder("Tocando Agora..."));
        jPanel5.setLayout(new java.awt.BorderLayout());

        jLabelTocando.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N
        jLabelTocando.setText("Nada...");
        jLabelTocando.setPreferredSize(new java.awt.Dimension(0, 18));
        jPanel5.add(jLabelTocando, java.awt.BorderLayout.CENTER);

        jPanel_Esquerdo.add(jPanel5, java.awt.BorderLayout.PAGE_START);

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
            Musica musica = objModelMusicas.getItem(jTableMusicas.getSelectedRow());
            musiquera.abrir(musica, 0, false);
            try {
                int musicasBanco = MusicaBD.contarMusicas();
                if (musicasBanco != objModelMusicas.getRowCount()) {
                    atualizaTabelaMusica();
                } else {
                    MusicaBD.carregar(musica);
                    objModelMusicas.atualizarItem(musica, jTableMusicas.getSelectedRow());
                }
            } catch (Exception ex) {
                Logger.getLogger(JFilaReproducao.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_jTableMusicasMouseClicked

    private void jTextFieldPesquisaKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldPesquisaKeyTyped
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextFieldPesquisaKeyTyped

    private void jTableFilaMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableFilaMouseClicked
        if (evt.getClickCount()==2) {
            Musica musica = (Musica) modelFila.getValueAt(jTableFila.getSelectedRow(), 0);
            modelFila.removeRow(jTableFila.getSelectedRow());
            musiquera.abrir(musica, 0, false);
        }
    }//GEN-LAST:event_jTableFilaMouseClicked

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
    private javax.swing.JPanel jPanel_Esquerdo;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPaneFila;
    private javax.swing.JTable jTableFila;
    private javax.swing.JTable jTableMusicas;
    private javax.swing.JTextField jTextFieldPesquisa;
    // End of variables declaration//GEN-END:variables
}
