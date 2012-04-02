/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * JConfiguracao.java
 *
 * Created on 19/08/2010, 21:17:54
 */
package com.config;

import com.fila.AcaoPadraoFila;
import com.fila.AcoesFilaVazia;
import com.musica.ModelReadOnly;
import com.musica.MusicaGerencia;
import java.util.ArrayList;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JFileChooser;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

/**
 *
 * @author manchini
 */
public class JConfiguracao extends javax.swing.JDialog {

    private JFileChooser jFileChooser = new JFileChooser();

    /** Creates new form JConfiguracao */
    public JConfiguracao(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();

        setTela();
    }

    private void setTela() {
        try {
            // restringe a amostra a diretorios apenas
            jFileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
            jFileChooser.setDialogTitle("Abrir Pasta");

            jCheckBox_DownloadCapa.setSelected(MusicaGerencia.downLoadCapas);
            jCheckBox_Organizador.setSelected(MusicaGerencia.organizarPastas);
            jTextField_DestinoOrg.setText(MusicaGerencia.destino);
            ModelReadOnly tm = new ModelReadOnly();
            tm.addColumn("Pastas em que o Crepz procurar� por m�sicas");
            jTable_pastas.setModel(tm);
            tm.setRowCount(0);
            ArrayList<String> pastas = Configuracaoes.getList(Configuracaoes.PASTAS_SCANER);
            for (short i = 0; i < pastas.size(); i++) {
                if (pastas.get(i) != null && !pastas.get(i).replace(" ", "").isEmpty()) {
                    tm.addRow(new Object[]{pastas.get(i)});

                }
            }
            jComboBoxAcaoFila.setModel(new DefaultComboBoxModel(AcaoPadraoFila.getNomesFakes()));
            jComboBoxAcaoFila.setSelectedIndex(Configuracaoes.getEnum(Configuracaoes.ACAO_PADRAO_FILA).ordinal());
            jComboBoxAoEsvaziaFila.setModel(new DefaultComboBoxModel(AcoesFilaVazia.getNomesFakes()));
            jComboBoxAoEsvaziaFila.setSelectedIndex(Configuracaoes.getEnum(Configuracaoes.ACOES_FILA_VAZIA).ordinal());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

//    private File telaAbrirArquivo() throws Exception {
//
//
//        int res = jFileChooser.showOpenDialog(null);
//
//        if (res == JFileChooser.APPROVE_OPTION) {
//            jTextField_Pasta.setText(jFileChooser.getSelectedFile().getAbsolutePath());
//            return jFileChooser.getSelectedFile();
//        }
//        return null;
////        else {
////            throw new Exception("Voce nao selecionou nenhum diretorio.");
////        }
//    }
//    private void addTablePastas() {
//        try {
//            telaAbrirArquivo();
//            if (jTextField_Pasta.getText().isEmpty()) {
//                return;
//            }
//            DefaultTableModel tm = (DefaultTableModel) jTable_pastas.getModel();
//            tm.addRow(new Object[]{jTextField_Pasta.getText()});
//            jTextField_Pasta.setText("");
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//    }
    private void setDadosBanco() {

        MusicaGerencia.organizarPastas = jCheckBox_Organizador.isSelected();
        MusicaGerencia.destino = jTextField_DestinoOrg.getText() != null ? jTextField_DestinoOrg.getText() : "";
        MusicaGerencia.downLoadCapas = jCheckBox_DownloadCapa.isSelected();
//        Scan.setTempo((Integer) jSpinner1.getValue());
        TableModel tm = jTable_pastas.getModel();
        ArrayList<String> pastas = new ArrayList<String>(10);
        for (int i = 0; i < tm.getRowCount(); i++) {
            String tms = tm.getValueAt(i, 0) == null ? "" : tm.getValueAt(i, 0).toString();
            pastas.add(tms);
        }

    }

    private void remove() {
        DefaultTableModel tm = (DefaultTableModel) jTable_pastas.getModel();
        if (tm.getRowCount() > 1) {
            tm.removeRow(jTable_pastas.getSelectedRow());
        } else {
            tm.setRowCount(0);
        }
        setDadosBanco();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel4 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel_Geral = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jComboBoxAcaoFila = new javax.swing.JComboBox();
        jPanel1 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jComboBoxAoEsvaziaFila = new javax.swing.JComboBox();
        jPanel3 = new javax.swing.JPanel();
        jPanel_Avancada = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable_pastas = new javax.swing.JTable();
        jPanel5 = new javax.swing.JPanel();
        jPanel13 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jTextField_Pasta = new javax.swing.JTextField();
        jPanel12 = new javax.swing.JPanel();
        jButton_Add = new javax.swing.JButton();
        jButton_Remove = new javax.swing.JButton();
        jPanel_Organizador = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jCheckBox_Organizador = new javax.swing.JCheckBox();
        jPanel9 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jTextField_DestinoOrg = new javax.swing.JTextField();
        jButton2 = new javax.swing.JButton();
        jPanel10 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        jCheckBox_DownloadCapa = new javax.swing.JCheckBox();
        jPanel8 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jPanel11 = new javax.swing.JPanel();
        jButtonSalvar = new javax.swing.JButton();
        jButtonFechar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(441, 342));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jPanel4.setPreferredSize(new java.awt.Dimension(400, 25));

        jLabel2.setFont(new java.awt.Font("DejaVu Sans", 1, 13)); // NOI18N
        jLabel2.setText("Configura��es");
        jPanel4.add(jLabel2);

        getContentPane().add(jPanel4, java.awt.BorderLayout.PAGE_START);

        jTabbedPane1.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        jTabbedPane1.setTabLayoutPolicy(javax.swing.JTabbedPane.SCROLL_TAB_LAYOUT);

        jPanel_Geral.setLayout(new javax.swing.BoxLayout(jPanel_Geral, javax.swing.BoxLayout.Y_AXIS));

        jPanel2.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        jLabel1.setText("Ao clicar 2 vezes na m�sica:");
        jPanel2.add(jLabel1);

        jComboBoxAcaoFila.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Adicionar a Fila", "Reproduzir" }));
        jComboBoxAcaoFila.setToolTipText("O que acontece quando eu clido 2 vezes numa m�sica...");
        jPanel2.add(jComboBoxAcaoFila);

        jPanel_Geral.add(jPanel2);

        jPanel1.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        jLabel6.setText("Quando a Fila Estiver Vazia:");
        jPanel1.add(jLabel6);

        jComboBoxAoEsvaziaFila.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "N�o fa�a nada", "Tocar da lista ao lado (Aleat�rio)", "Tocar da lista ao lado (Sequencial)" }));
        jPanel1.add(jComboBoxAoEsvaziaFila);

        jPanel_Geral.add(jPanel1);

        jPanel3.setLayout(new java.awt.BorderLayout());
        jPanel_Geral.add(jPanel3);

        jTabbedPane1.addTab("Fila de Reprodu��o", jPanel_Geral);

        jPanel_Avancada.setLayout(new javax.swing.BoxLayout(jPanel_Avancada, javax.swing.BoxLayout.Y_AXIS));

        jPanel6.setPreferredSize(new java.awt.Dimension(452, 300));
        jPanel6.setLayout(new java.awt.BorderLayout());

        jScrollPane1.setPreferredSize(new java.awt.Dimension(452, 300));

        jTable_pastas.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Pastas em que o Crepz procurar� por m�sicas"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable_pastas.setRowHeight(22);
        jScrollPane1.setViewportView(jTable_pastas);

        jPanel6.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        jPanel_Avancada.add(jPanel6);

        jPanel5.setLayout(new javax.swing.BoxLayout(jPanel5, javax.swing.BoxLayout.Y_AXIS));

        jPanel13.setLayout(new javax.swing.BoxLayout(jPanel13, javax.swing.BoxLayout.LINE_AXIS));

        jLabel3.setText("Pasta:");
        jLabel3.setPreferredSize(new java.awt.Dimension(50, 18));
        jPanel13.add(jLabel3);

        jTextField_Pasta.setPreferredSize(new java.awt.Dimension(250, 30));
        jPanel13.add(jTextField_Pasta);

        jPanel5.add(jPanel13);

        jPanel12.setLayout(new java.awt.GridLayout(1, 0));

        jButton_Add.setMnemonic('A');
        jButton_Add.setText("Adicionar");
        jButton_Add.setToolTipText("Adicionar endere�o na lista. (Alt + A)");
        jButton_Add.setPreferredSize(new java.awt.Dimension(60, 30));
        jButton_Add.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_AddActionPerformed(evt);
            }
        });
        jPanel12.add(jButton_Add);

        jButton_Remove.setMnemonic('R');
        jButton_Remove.setText("Remover");
        jButton_Remove.setToolTipText("Remover da lista o item selecionado. (Alt + R)");
        jButton_Remove.setPreferredSize(new java.awt.Dimension(60, 30));
        jButton_Remove.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_RemoveActionPerformed(evt);
            }
        });
        jPanel12.add(jButton_Remove);

        jPanel5.add(jPanel12);

        jPanel_Avancada.add(jPanel5);

        jTabbedPane1.addTab("Monitorar Pastas", jPanel_Avancada);

        jPanel_Organizador.setLayout(new javax.swing.BoxLayout(jPanel_Organizador, javax.swing.BoxLayout.Y_AXIS));

        jPanel7.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        jLabel4.setText("Ativar Organizador:");
        jLabel4.setPreferredSize(new java.awt.Dimension(130, 17));
        jPanel7.add(jLabel4);
        jPanel7.add(jCheckBox_Organizador);

        jPanel_Organizador.add(jPanel7);

        jPanel9.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        jLabel5.setText("Destino:");
        jLabel5.setPreferredSize(new java.awt.Dimension(125, 17));
        jPanel9.add(jLabel5);

        jTextField_DestinoOrg.setPreferredSize(new java.awt.Dimension(250, 30));
        jPanel9.add(jTextField_DestinoOrg);

        jButton2.setText("...");
        jButton2.setOpaque(true);
        jButton2.setPreferredSize(new java.awt.Dimension(30, 30));
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        jPanel9.add(jButton2);

        jPanel_Organizador.add(jPanel9);

        jPanel10.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        jLabel7.setText("Donwload de Capas:");
        jPanel10.add(jLabel7);
        jPanel10.add(jCheckBox_DownloadCapa);

        jPanel_Organizador.add(jPanel10);

        jPanel8.setLayout(new java.awt.BorderLayout());

        jTextArea1.setColumns(20);
        jTextArea1.setEditable(false);
        jTextArea1.setLineWrap(true);
        jTextArea1.setRows(5);
        jTextArea1.setText("O modo organizador ativado, o sitema ao ler uma musica ira organiza-la na pasta de destino, mais as pastas\n         Artista\n         Album\nCom download de capa ativo o sistema ira buscar uma capa, para o �lbum caso o mesmo n�o tenha");
        jTextArea1.setEnabled(false);
        jTextArea1.setFocusable(false);
        jScrollPane2.setViewportView(jTextArea1);

        jPanel8.add(jScrollPane2, java.awt.BorderLayout.CENTER);

        jPanel_Organizador.add(jPanel8);

        jTabbedPane1.addTab("Organizador (Inativo)", jPanel_Organizador);

        getContentPane().add(jTabbedPane1, java.awt.BorderLayout.CENTER);

        jPanel11.setPreferredSize(new java.awt.Dimension(400, 35));

        jButtonSalvar.setMnemonic('S');
        jButtonSalvar.setText("Salvar");
        jButtonSalvar.setToolTipText("Salvar configura��es. (Alt + S )");
        jButtonSalvar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSalvarActionPerformed(evt);
            }
        });
        jPanel11.add(jButtonSalvar);

        jButtonFechar.setMnemonic('C');
        jButtonFechar.setText("Cancelar");
        jButtonFechar.setToolTipText("Cancelar. (Alt + C)");
        jButtonFechar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonFecharActionPerformed(evt);
            }
        });
        jPanel11.add(jButtonFechar);

        getContentPane().add(jPanel11, java.awt.BorderLayout.PAGE_END);

        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        setBounds((screenSize.width-497)/2, (screenSize.height-425)/2, 497, 425);
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
//        setDadosBanco();
    }//GEN-LAST:event_formWindowClosing

    private void jButtonSalvarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSalvarActionPerformed

        ArrayList<String> pastas = new ArrayList<String>(jTable_pastas.getRowCount());
        for (int i = 0; i < jTable_pastas.getRowCount(); i++) {
            String pasta = (String) jTable_pastas.getValueAt(i, 0);
            pastas.add(pasta);
        }
        Configuracaoes.set(Configuracaoes.PASTAS_SCANER, pastas);
        Configuracaoes.set(Configuracaoes.ACAO_PADRAO_FILA, AcaoPadraoFila.values()[jComboBoxAcaoFila.getSelectedIndex()]);
        Configuracaoes.set(Configuracaoes.ACOES_FILA_VAZIA, AcoesFilaVazia.values()[jComboBoxAoEsvaziaFila.getSelectedIndex()]);
        dispose();
//        setDadosBanco();

    }//GEN-LAST:event_jButtonSalvarActionPerformed

    private void jButton_AddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_AddActionPerformed
        String text = jTextField_Pasta.getText();
        if (text == null || text.isEmpty()) {
            int saida = jFileChooser.showOpenDialog(this);
            if (saida != JFileChooser.APPROVE_OPTION) {
                return;
            }
            text = jFileChooser.getSelectedFile().getAbsolutePath();
        }
        ((ModelReadOnly) jTable_pastas.getModel()).addRow(new Object[]{text});
}//GEN-LAST:event_jButton_AddActionPerformed

    private void jButton_RemoveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_RemoveActionPerformed
        remove();
    }//GEN-LAST:event_jButton_RemoveActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // restringe a amostra a diretorios apenas
        jFileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        jFileChooser.setDialogTitle("Abrir Pasta");

        int res = jFileChooser.showOpenDialog(null);

        if (res == JFileChooser.APPROVE_OPTION) {
            jTextField_DestinoOrg.setText(jFileChooser.getSelectedFile().getAbsolutePath());
        }

    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButtonFecharActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonFecharActionPerformed
        dispose();
    }//GEN-LAST:event_jButtonFecharActionPerformed
    /**
     * @param args the command line arguments
     */
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButtonFechar;
    private javax.swing.JButton jButtonSalvar;
    private javax.swing.JButton jButton_Add;
    private javax.swing.JButton jButton_Remove;
    private javax.swing.JCheckBox jCheckBox_DownloadCapa;
    private javax.swing.JCheckBox jCheckBox_Organizador;
    private javax.swing.JComboBox jComboBoxAcaoFila;
    private javax.swing.JComboBox jComboBoxAoEsvaziaFila;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JPanel jPanel_Avancada;
    private javax.swing.JPanel jPanel_Geral;
    private javax.swing.JPanel jPanel_Organizador;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTable jTable_pastas;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextField jTextField_DestinoOrg;
    private javax.swing.JTextField jTextField_Pasta;
    // End of variables declaration//GEN-END:variables
}
