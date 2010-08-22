/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * JConfiguracao.java
 *
 * Created on 19/08/2010, 21:17:54
 */
package com.configuracao;

import java.io.File;
import java.util.HashMap;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.table.DefaultTableModel;

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
            HashMap<String, String> lista = ConfiguracaoBD.listar(new ConfiguracaoSC());
            jCheckBox_teste.setSelected(lista.get("teste") == null ? false : Boolean.parseBoolean(lista.get("teste").toString()));
            
            //Aba Organizador
            jCheckBox_Organizador.setSelected(lista.get("organizadorPastas") == null ? false : Boolean.parseBoolean(lista.get("organizadorPastas").toString()));
            jTextField_DestinoOrg.setText(lista.get("organizadorDestino") == null?"":lista.get("organizadorDestino").toString());
            jCheckBox_DownloadCapa.setSelected(lista.get("downloadCapas") == null ? false : Boolean.parseBoolean(lista.get("downloadCapas").toString()));

            setTabelaPastas(lista);
            Object ob = Integer.parseInt(lista.get("TempoAtualizar"));
            jSpinner1.setValue(ob);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void setTabelaPastas(HashMap<String, String> lista) {
        int c = 1;
        DefaultTableModel tm = (DefaultTableModel) jTable_pastas.getModel();
        tm.setRowCount(0);
        String pastaMonitorada = "pastaMonitorada";
        Set chaves = lista.keySet();
        for (int i = 0; i < chaves.toArray().length; i++) {
            if (lista.get(pastaMonitorada + c) != null) {
                Object[] linha = new Object[1];
                linha[0] = lista.get(pastaMonitorada + c);
                tm.addRow(linha);
            }
            c++;
        }
    }

    private File telaAbrirArquivo() throws Exception {

        // restringe a amostra a diretorios apenas
        jFileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        jFileChooser.setDialogTitle("Abrir Pasta");

        int res = jFileChooser.showOpenDialog(null);

        if (res == JFileChooser.APPROVE_OPTION) {
            jTextField_Jpasta.setText(jFileChooser.getSelectedFile().getAbsolutePath());
            return jFileChooser.getSelectedFile();
        }
        return null;
//        else {
//            throw new Exception("Voce nao selecionou nenhum diretorio.");
//        }
    }

    private void addTablePastas() {
        try {
            telaAbrirArquivo();
            if (jTextField_Jpasta.getText().equals("")) {
                return;
            }
            DefaultTableModel tm = (DefaultTableModel) jTable_pastas.getModel();
            tm.addRow(new Object[]{jTextField_Jpasta.getText()});
            jTextField_Jpasta.setText("");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void setDadosBancoPasta(HashMap<String, String> lista) {
        DefaultTableModel tm = (DefaultTableModel) jTable_pastas.getModel();
        String pastaMonitorada = "pastaMonitorada";
        try {
            ConfiguracaoBD.excluirChavesQueComecam(pastaMonitorada);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        int c = 1;
        for (int i = 0; i < tm.getRowCount(); i++) {
            String pasta = (String) tm.getValueAt(i, 0);
            if (pasta != null && !pasta.trim().equals("")) {
                lista.put(pastaMonitorada + (c), pasta);
                c++;
            }
        }
    }

    private void setDadosBanco() {
        HashMap<String, String> lista = new HashMap<String, String>();
        lista.put("teste", String.valueOf(jCheckBox_teste.isSelected()));
        lista.put("TempoAtualizar", jSpinner1.getValue().toString());

        lista.put("organizadorPastas", String.valueOf(jCheckBox_Organizador.isSelected()));
        lista.put("organizadorDestino", jTextField_DestinoOrg.getText());
        lista.put("downloadCapas", String.valueOf(jCheckBox_DownloadCapa.isSelected()));

        setDadosBancoPasta(lista);

        Set chaves = lista.keySet();
        for (int i = 0; i < chaves.toArray().length; i++) {
            try {
                Configuracao conf = new Configuracao();
                conf.setChave(chaves.toArray()[i].toString());
                Boolean existe = ConfiguracaoBD.carregar(conf);
                conf.setValor(lista.get(chaves.toArray()[i]));
                if (existe) {
                    ConfiguracaoBD.alterar(conf);
                } else {
                    ConfiguracaoBD.incluir(conf);
                }

            } catch (Exception ex) {
                System.out.println("Erro ao Salvar Configuracao");
                ex.printStackTrace();
            }

        }

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
        jCheckBox_teste = new javax.swing.JCheckBox();
        jPanel3 = new javax.swing.JPanel();
        jPanel_Avancada = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable_pastas = new javax.swing.JTable();
        jPanel5 = new javax.swing.JPanel();
        jPanel12 = new javax.swing.JPanel();
        jButton_ADD1 = new javax.swing.JButton();
        jButton_ADD = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jTextField_Jpasta = new javax.swing.JTextField();
        jPanel1 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jSpinner1 = new javax.swing.JSpinner();
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
        jButton1 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(441, 342));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jPanel4.setPreferredSize(new java.awt.Dimension(400, 25));

        jLabel2.setFont(new java.awt.Font("DejaVu Sans", 1, 13)); // NOI18N
        jLabel2.setText("Configurações");
        jPanel4.add(jLabel2);

        getContentPane().add(jPanel4, java.awt.BorderLayout.PAGE_START);

        jTabbedPane1.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        jTabbedPane1.setTabLayoutPolicy(javax.swing.JTabbedPane.SCROLL_TAB_LAYOUT);

        jPanel_Geral.setLayout(new javax.swing.BoxLayout(jPanel_Geral, javax.swing.BoxLayout.Y_AXIS));

        jPanel2.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        jLabel1.setText("Teste:");
        jLabel1.setPreferredSize(new java.awt.Dimension(80, 17));
        jPanel2.add(jLabel1);
        jPanel2.add(jCheckBox_teste);

        jPanel_Geral.add(jPanel2);

        jPanel3.setLayout(new java.awt.BorderLayout());
        jPanel_Geral.add(jPanel3);

        jTabbedPane1.addTab("Geral", jPanel_Geral);

        jPanel_Avancada.setLayout(new javax.swing.BoxLayout(jPanel_Avancada, javax.swing.BoxLayout.Y_AXIS));

        jPanel6.setPreferredSize(new java.awt.Dimension(452, 300));
        jPanel6.setLayout(new java.awt.BorderLayout());

        jScrollPane1.setPreferredSize(new java.awt.Dimension(452, 300));

        jTable_pastas.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null},
                {null},
                {null},
                {null}
            },
            new String [] {
                "Pasta"
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

        jButton_ADD1.setText("DEL");
        jButton_ADD1.setPreferredSize(new java.awt.Dimension(60, 30));
        jButton_ADD1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_ADD1ActionPerformed(evt);
            }
        });
        jPanel12.add(jButton_ADD1);

        jButton_ADD.setText("ADD");
        jButton_ADD.setPreferredSize(new java.awt.Dimension(60, 30));
        jButton_ADD.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_ADDActionPerformed(evt);
            }
        });
        jPanel12.add(jButton_ADD);

        jLabel3.setText("Pasta:");
        jPanel12.add(jLabel3);

        jTextField_Jpasta.setEditable(false);
        jTextField_Jpasta.setEnabled(false);
        jTextField_Jpasta.setPreferredSize(new java.awt.Dimension(250, 30));
        jPanel12.add(jTextField_Jpasta);

        jLabel6.setText("Tempo de Repetição");
        jPanel1.add(jLabel6);

        jSpinner1.setModel(new javax.swing.SpinnerNumberModel(30, 30, 120, 15));
        jPanel1.add(jSpinner1);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

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

        jTextField_DestinoOrg.setEditable(false);
        jTextField_DestinoOrg.setEnabled(false);
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
        jLabel7.setPreferredSize(new java.awt.Dimension(130, 17));
        jPanel10.add(jLabel7);
        jPanel10.add(jCheckBox_DownloadCapa);

        jPanel_Organizador.add(jPanel10);

        jPanel8.setLayout(new java.awt.BorderLayout());

        jTextArea1.setColumns(20);
        jTextArea1.setEditable(false);
        jTextArea1.setLineWrap(true);
        jTextArea1.setRows(5);
        jTextArea1.setText("O modo organizador ativado, o sitema ao ler uma musica ira organiza-la na pasta de destino, mais as pastas\n         Artista\n         Album\nCom download de capa ativo o sistema ira buscar uma capa, para o álbum caso o mesmo não tenha");
        jTextArea1.setAutoscrolls(true);
        jTextArea1.setEnabled(false);
        jTextArea1.setFocusable(false);
        jScrollPane2.setViewportView(jTextArea1);

        jPanel8.add(jScrollPane2, java.awt.BorderLayout.CENTER);

        jPanel_Organizador.add(jPanel8);

        jTabbedPane1.addTab("Organizador", jPanel_Organizador);

        getContentPane().add(jTabbedPane1, java.awt.BorderLayout.CENTER);

        jPanel11.setPreferredSize(new java.awt.Dimension(400, 35));

        jButton1.setText("Salvar");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jPanel11.add(jButton1);

        getContentPane().add(jPanel11, java.awt.BorderLayout.PAGE_END);

        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        setBounds((screenSize.width-497)/2, (screenSize.height-425)/2, 497, 425);
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        try {
            setDadosBanco();
            Configuracao.setConfiguracoes(ConfiguracaoBD.listar(new ConfiguracaoSC()));
        } catch (Exception ex) {
            Logger.getLogger(JConfiguracao.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_formWindowClosing

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
         try {
            setDadosBanco();
            Configuracao.setConfiguracoes(ConfiguracaoBD.listar(new ConfiguracaoSC()));
        } catch (Exception ex) {
            Logger.getLogger(JConfiguracao.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton_ADDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_ADDActionPerformed
        addTablePastas();
}//GEN-LAST:event_jButton_ADDActionPerformed

    private void jButton_ADD1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_ADD1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton_ADD1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
       // restringe a amostra a diretorios apenas
        jFileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        jFileChooser.setDialogTitle("Abrir Pasta");

        int res = jFileChooser.showOpenDialog(null);

        if (res == JFileChooser.APPROVE_OPTION) 
            jTextField_DestinoOrg.setText(jFileChooser.getSelectedFile().getAbsolutePath());
        
    }//GEN-LAST:event_jButton2ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                JConfiguracao dialog = new JConfiguracao(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {

                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton_ADD;
    private javax.swing.JButton jButton_ADD1;
    private javax.swing.JCheckBox jCheckBox_DownloadCapa;
    private javax.swing.JCheckBox jCheckBox_Organizador;
    private javax.swing.JCheckBox jCheckBox_teste;
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
    private javax.swing.JSpinner jSpinner1;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTable jTable_pastas;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextField jTextField_DestinoOrg;
    private javax.swing.JTextField jTextField_Jpasta;
    // End of variables declaration//GEN-END:variables
}
