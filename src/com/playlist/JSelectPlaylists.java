/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * JSelectPlaylists.java
 *
 * Created on 12/06/2010, 17:22:14
 */
package com.playlist;

import com.utils.model.ModelReadOnly;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author manchini
 */
public class JSelectPlaylists extends javax.swing.JDialog implements ActionListener {

    /**
     * Creates new form JSelectPlaylists
     */
    JPlayList playlist;

    public JSelectPlaylists(java.awt.Frame parent, boolean modal, JPlayList playlist) {
        super(parent, modal);
        initComponents();
        this.playlist = playlist;
        atualizarTabelaLista();
        startEvents();
    }

    /**
     * Método que inicializa a tela.
     */
    private void initTabelaLista() {

        // Definindo as colunas...
        ModelReadOnly tm = new ModelReadOnly();
        tm.addColumn("Cod");
        tm.addColumn("Nome");
        tm.addColumn("Músicas");
        tm.addColumn("Obj");

        jTable.setModel(tm);

        // Definindo a largura das colunas...
        jTable.getColumn("Cod").setPreferredWidth(50);
        jTable.getColumn("Nome").setPreferredWidth(200);
        jTable.getColumn("Músicas").setPreferredWidth(100);

        // Removendo a coluna do objeto da view...
        jTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer());
        jTable.removeColumn(jTable.getColumn("Obj"));

        jTable.setAutoCreateRowSorter(true);
        jTable.setShowVerticalLines(true);
        jTable.setEditingColumn(-1);
        jTable.setEditingRow(-1);


        jTable.setIntercellSpacing(new Dimension(1, 2));
        jTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        jTable.setShowHorizontalLines(true);
        jTable.setShowVerticalLines(true);
        jTable.setRowSelectionAllowed(true);
        jTable.setColumnSelectionAllowed(false);
        jTable.setRowHeight(20);

    }

    public static Playlist getPlayList(int id) {
        Playlist playlist = new Playlist();
        System.out.println("id: " + id);
        playlist.setId(id);
        try {
            PlaylistBD.carregar(playlist);
        } catch (Exception ex) {
            Logger.getLogger(JSelectPlaylists.class.getName()).log(Level.SEVERE, null, ex);
        }
        return playlist;
    }

    /**
     * Método que atualiza a consulta atual.
     */
    private void atualizarTabelaLista() {

        try {
            // Filtro...
            initTabelaLista();
            DefaultTableModel ts = (DefaultTableModel) jTable.getModel();

            PlaylistSC filtro = new PlaylistSC();
            filtro.setNome(jTextField_Nome.getText());
            ArrayList lista = PlaylistBD.listar(filtro);
            for (int i = 0; i < lista.size(); i++) {
                Playlist m = (Playlist) lista.get(i);
                Object[] row = new Object[4];
                row[0] = m.getId();
                row[1] = m.getNome();
                row[2] = m.getNrMusicas();
                row[3] = m;

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

    public void rodarPlaylist(Playlist p) {
        playlist.limpar();
        playlist.tocar(p, true);
    }
    
    private void startEvents(){
       jButton_OK.addActionListener(this); 
       jMenuItem1.addActionListener(this); 
       jMenuItem2.addActionListener(this); 
       jMenuItem3.addActionListener(this); 
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == jButton_OK) {
            atualizarTabelaLista();
        } else if (e.getSource() == jMenuItem1) {
            atualizarTabelaLista();
        } else if (e.getSource() == jMenuItem2) {
            jTextField_Nome.setText("");
        } else if (e.getSource() == jMenuItem3) {
            setVisible(false);
            dispose();
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

        jPanel1 = new javax.swing.JPanel();
        jPanel_Nome4 = new javax.swing.JPanel();
        jLabel_Nome4 = new javax.swing.JLabel();
        jTextField_Nome = new javax.swing.JTextField();
        jButton_OK = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane = new javax.swing.JScrollPane();
        jTable = new javax.swing.JTable();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenuItem3 = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanel1.setPreferredSize(new java.awt.Dimension(400, 30));
        jPanel1.setLayout(new javax.swing.BoxLayout(jPanel1, javax.swing.BoxLayout.Y_AXIS));

        jPanel_Nome4.setMinimumSize(new java.awt.Dimension(0, 22));
        jPanel_Nome4.setPreferredSize(new java.awt.Dimension(0, 35));
        jPanel_Nome4.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 1));

        jLabel_Nome4.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N
        jLabel_Nome4.setText("Filtro:");
        jLabel_Nome4.setMaximumSize(new java.awt.Dimension(60, 16));
        jLabel_Nome4.setMinimumSize(new java.awt.Dimension(60, 16));
        jLabel_Nome4.setPreferredSize(new java.awt.Dimension(70, 16));
        jPanel_Nome4.add(jLabel_Nome4);

        jTextField_Nome.setMaximumSize(new java.awt.Dimension(300, 20));
        jTextField_Nome.setMinimumSize(new java.awt.Dimension(300, 20));
        jTextField_Nome.setPreferredSize(new java.awt.Dimension(200, 25));
        jPanel_Nome4.add(jTextField_Nome);

        jButton_OK.setText("...");
        jButton_OK.setToolTipText("Carrega a listagem");
        jButton_OK.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        jButton_OK.setMaximumSize(new java.awt.Dimension(22, 20));
        jButton_OK.setMinimumSize(new java.awt.Dimension(22, 20));
        jButton_OK.setPreferredSize(new java.awt.Dimension(30, 30));
        jPanel_Nome4.add(jButton_OK);

        jPanel1.add(jPanel_Nome4);

        getContentPane().add(jPanel1, java.awt.BorderLayout.PAGE_START);

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

        jMenu1.setText("Funções");

        jMenuItem1.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F5, 0));
        jMenuItem1.setText("Pesquisar");
        jMenu1.add(jMenuItem1);

        jMenuItem2.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F4, 0));
        jMenuItem2.setText("Limpar");
        jMenu1.add(jMenuItem2);

        jMenuItem3.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ESCAPE, 0));
        jMenuItem3.setText("Fechar");
        jMenu1.add(jMenuItem3);

        jMenuBar1.add(jMenu1);

        setJMenuBar(jMenuBar1);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableMouseClicked
        if (evt.getClickCount() == 2) {
            rodarPlaylist((Playlist) jTable.getModel().getValueAt(jTable.getSelectedRow(), jTable.getColumnCount()));
            setVisible(false);
            dispose();
        }
}//GEN-LAST:event_jTableMouseClicked

    private void jTableKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTableKeyPressed
        if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
            rodarPlaylist((Playlist) jTable.getModel().getValueAt(jTable.getSelectedRow(), jTable.getColumnCount()));
            setVisible(false);
            dispose();
        }
}//GEN-LAST:event_jTableKeyPressed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {

        JSelectPlaylists dialog = new JSelectPlaylists(new javax.swing.JFrame(), true, null);
        dialog.setVisible(true);
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton_OK;
    private javax.swing.JLabel jLabel_Nome4;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel_Nome4;
    private javax.swing.JScrollPane jScrollPane;
    private javax.swing.JTable jTable;
    private javax.swing.JTextField jTextField_Nome;
    // End of variables declaration//GEN-END:variables
}
