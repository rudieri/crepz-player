/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.copiador;

import com.musica.Musica;
import java.awt.HeadlessException;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

/**
 *
 * @author rudieri
 */
public class JCopiador extends javax.swing.JDialog implements ItemListener, ActionListener, Runnable {

    /**
     * Creates new form JCopiador
     */
    private ArrayList<Musica> musicas;
    private JFileChooser jFileChooser;
    private Thread thread;

    public JCopiador(java.awt.Dialog parent, boolean modal) {
        super(parent, modal);
        init();
    }

    public JCopiador(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        init();

    }

    private void init() {
        initComponents();
        jComboBoxTipoEstruturaDestino.removeAllItems();
        TipoEstruturaDestino[] values = TipoEstruturaDestino.values();
        for (TipoEstruturaDestino tipoEstruturaDestino : values) {
            jComboBoxTipoEstruturaDestino.addItem(tipoEstruturaDestino);
        }
        jComboBoxTipoEstruturaDestino.setSelectedItem(TipoEstruturaDestino.ESTRUTURA_ORIGINAL);
        jFileChooser = new JFileChooser(jTextFieldPastaDestino.getText());
        jFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        jProgressBar1.setVisible(false);
        pack();
    }

    public void setMusicas(ArrayList<Musica> musicas) {
        if (musicas.isEmpty()) {
            JOptionPane.showMessageDialog(this, "A lista está vazia...");
            dispose();
        }
        this.musicas = musicas;
        jLabelQtdMusicas.setText(String.valueOf(musicas.size()));
        Musica primeiraMusica = musicas.get(0);
        String minimaPastaCommum = primeiraMusica.getCaminho().substring(0, primeiraMusica.getCaminho().lastIndexOf('/'));
        for (int i = 1; i < musicas.size(); i++) {
            Musica musica = musicas.get(i);
            while (!musica.getCaminho().startsWith(minimaPastaCommum)) {
                minimaPastaCommum = minimaPastaCommum.substring(0, minimaPastaCommum.lastIndexOf('/'));
            }
        }
        jTextFieldPastaOrigemBase.setText(minimaPastaCommum);
    }

    public void setNomePLayList(String nomePLayList) {
        jTextFieldPlayListOrigem.setText(nomePLayList);
    }

    private void selecionarPastaDestino() {
        jFileChooser.setCurrentDirectory(new File(jTextFieldPastaDestino.getText()));
        if (jFileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            jTextFieldPastaDestino.setText(jFileChooser.getSelectedFile().getAbsolutePath());
        }

    }

    private void selecionaPastaOrigemBase() {
        jFileChooser.setCurrentDirectory(new File(jTextFieldPastaOrigemBase.getText()));
        if (jFileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            jTextFieldPastaOrigemBase.setText(jFileChooser.getSelectedFile().getAbsolutePath());
        }
    }

    private void copiarTudoPara() {
        jProgressBar1.setVisible(true);
        jProgressBar1.setMaximum(musicas.size());
        pack();
        File pastaDestino = new File(jTextFieldPastaDestino.getText());
        // Cipiar segundo a opção escolhida
        switch ((TipoEstruturaDestino) jComboBoxTipoEstruturaDestino.getSelectedItem()) {
            case ESTRUTURA_DADOS_MUSICA:
                for (int i = 0; i < musicas.size(); i++) {
                    jProgressBar1.setValue(i);
                    jProgressBar1.setString("Copiando... [" + i + " de " + musicas.size() + "] ");
                    Musica musica = musicas.get(i);
                    try {
                        File original = new File(musica.getCaminho());
                        String estruturaDir;
                        if (jRadioButtonEstArtistaAlbum.isSelected()) {
                            estruturaDir = musica.getAutor() + "/" + musica.getAlbum();
                        } else if (jRadioButtonEstArtista.isSelected()) {
                            estruturaDir = musica.getAutor();
                        } else if (jRadioButtonEstAlbumArtista.isSelected()) {
                            estruturaDir = musica.getAlbum() + "/" + musica.getAutor();
                        } else {
                            estruturaDir = musica.getAlbum();
                        }
                        File novo = new File(pastaDestino.getAbsolutePath() + "/" + estruturaDir + "/" + original.getName());
                        if (!novo.getParentFile().exists()) {
                            novo.getParentFile().mkdirs();
                        }
                        if (novo.exists() && jRadioButtonIgnorar.isSelected()) {
                            continue;
                        }
                        Files.copy(original.toPath(), novo.toPath(), StandardCopyOption.COPY_ATTRIBUTES,
                                StandardCopyOption.REPLACE_EXISTING);
                    } catch (IOException ex) {
                        if (mostrarMensagemErro(ex, i, musica)) {
                            return;
                        }
                    }
                }
                break;
            case ESTRUTURA_ORIGINAL:
                for (int i = 0; i < musicas.size(); i++) {
                    jProgressBar1.setValue(i);
                    jProgressBar1.setString("Copiando... [" + i + " de " + musicas.size() + "] ");
                    Musica musica = musicas.get(i);
                    try {
                        File original = new File(musica.getCaminho());
                        String estruturaDir = musica.getCaminho().replace(jTextFieldPastaOrigemBase.getText(), "/");
                        File novo = new File(pastaDestino.getAbsolutePath() + estruturaDir);
                        if (!novo.getParentFile().exists()) {
                            novo.getParentFile().mkdirs();
                        }
                        if (novo.exists() && jRadioButtonIgnorar.isSelected()) {
                            continue;
                        }
                        Files.copy(original.toPath(), novo.toPath(), StandardCopyOption.COPY_ATTRIBUTES,
                                StandardCopyOption.REPLACE_EXISTING);
                    } catch (IOException ex) {
                        if (mostrarMensagemErro(ex, i, musica)) {
                            return;
                        }
                    }

                }
                break;
            case SEM_ESTRUTURA_DIRETORIO:
                for (int i = 0; i < musicas.size(); i++) {
                    jProgressBar1.setValue(i);
                    jProgressBar1.setString("Copiando... [" + i + " de " + musicas.size() + "] ");
                    Musica musica = musicas.get(i);
                    try {
                        File original = new File(musica.getCaminho());

                        File novo = new File(pastaDestino.getAbsolutePath(), original.getName());
                        if (novo.exists() && jRadioButtonIgnorar.isSelected()) {
                            continue;
                        }
                        Files.copy(original.toPath(), novo.toPath(), StandardCopyOption.COPY_ATTRIBUTES,
                                StandardCopyOption.REPLACE_EXISTING);
                    } catch (IOException ex) {
                        if (mostrarMensagemErro(ex, i, musica)) {
                            return;
                        }
                    }

                }
                break;
        }
        jProgressBar1.setValue(0);
        jProgressBar1.setVisible(false);
        JOptionPane.showMessageDialog(this, "Musicas copiadas com sucesso.");
        dispose();
    }

    @Override
    public void run() {
        jButtonCopiar.setEnabled(false);
        copiarTudoPara();
        jButtonCopiar.setEnabled(true);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroupSunstitIgnorar = new javax.swing.ButtonGroup();
        buttonGroupOpcoesEstruturaDadosMusica = new javax.swing.ButtonGroup();
        jPanel10 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jTextFieldPlayListOrigem = new javax.swing.JTextField();
        jPanel5 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jTextFieldPastaDestino = new javax.swing.JTextField();
        jButtonPastaDestino = new javax.swing.JButton();
        jPanel11 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jComboBoxTipoEstruturaDestino = new javax.swing.JComboBox();
        jPanel6 = new javax.swing.JPanel();
        jPanelOpcoesEstrutOriginal = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jTextFieldPastaOrigemBase = new javax.swing.JTextField();
        jButtonPastaOrigemBase = new javax.swing.JButton();
        jPanelOpcoesDadosMusica = new javax.swing.JPanel();
        jPanel13 = new javax.swing.JPanel();
        jRadioButtonEstArtistaAlbum = new javax.swing.JRadioButton();
        jRadioButtonEstArtista = new javax.swing.JRadioButton();
        jRadioButtonEstAlbumArtista = new javax.swing.JRadioButton();
        jRadioButtonEstAlbum = new javax.swing.JRadioButton();
        jPanelOpcoesSemEstrutura = new javax.swing.JPanel();
        jPanel12 = new javax.swing.JPanel();
        jRadioButtonSubstituir = new javax.swing.JRadioButton();
        jRadioButtonIgnorar = new javax.swing.JRadioButton();
        jPanel8 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jLabelQtdMusicas = new javax.swing.JLabel();
        jPanel9 = new javax.swing.JPanel();
        jProgressBar1 = new javax.swing.JProgressBar();
        jPanel3 = new javax.swing.JPanel();
        jButtonCopiar = new javax.swing.JButton();
        jButtonCancelar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanel10.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));
        jPanel10.add(jLabel6);

        getContentPane().add(jPanel10, java.awt.BorderLayout.PAGE_START);

        jPanel1.setLayout(new javax.swing.BoxLayout(jPanel1, javax.swing.BoxLayout.Y_AXIS));

        jPanel4.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 2));

        jLabel2.setText("PlayList Origem:");
        jLabel2.setPreferredSize(new java.awt.Dimension(180, 16));
        jPanel4.add(jLabel2);

        jTextFieldPlayListOrigem.setPreferredSize(new java.awt.Dimension(250, 26));
        jPanel4.add(jTextFieldPlayListOrigem);

        jPanel1.add(jPanel4);

        jPanel5.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 2));

        jLabel3.setText("Pasta Destino:");
        jLabel3.setPreferredSize(new java.awt.Dimension(180, 16));
        jPanel5.add(jLabel3);

        jTextFieldPastaDestino.setPreferredSize(new java.awt.Dimension(250, 26));
        jPanel5.add(jTextFieldPastaDestino);

        jButtonPastaDestino.setText("...");
        jButtonPastaDestino.setPreferredSize(new java.awt.Dimension(22, 22));
        jButtonPastaDestino.addActionListener(this);
        jPanel5.add(jButtonPastaDestino);

        jPanel1.add(jPanel5);

        jPanel11.setBorder(javax.swing.BorderFactory.createTitledBorder("Como Copiar"));
        jPanel11.setLayout(new javax.swing.BoxLayout(jPanel11, javax.swing.BoxLayout.Y_AXIS));

        jPanel2.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 2));

        jLabel1.setText("Estrutura de pastas Destino:");
        jLabel1.setPreferredSize(new java.awt.Dimension(180, 16));
        jPanel2.add(jLabel1);

        jComboBoxTipoEstruturaDestino.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jComboBoxTipoEstruturaDestino.addItemListener(this);
        jPanel2.add(jComboBoxTipoEstruturaDestino);

        jPanel11.add(jPanel2);

        jPanel6.setLayout(new java.awt.CardLayout());

        jPanelOpcoesEstrutOriginal.setBorder(javax.swing.BorderFactory.createTitledBorder("Opções - Baseado na Estrutura Original"));
        jPanelOpcoesEstrutOriginal.setLayout(new javax.swing.BoxLayout(jPanelOpcoesEstrutOriginal, javax.swing.BoxLayout.Y_AXIS));

        jPanel7.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        jLabel4.setText("Copiar Estrutura após Pasta:");
        jLabel4.setMinimumSize(new java.awt.Dimension(180, 16));
        jLabel4.setPreferredSize(new java.awt.Dimension(180, 16));
        jPanel7.add(jLabel4);

        jTextFieldPastaOrigemBase.setPreferredSize(new java.awt.Dimension(250, 26));
        jPanel7.add(jTextFieldPastaOrigemBase);

        jButtonPastaOrigemBase.setText("...");
        jButtonPastaOrigemBase.setPreferredSize(new java.awt.Dimension(22, 22));
        jButtonPastaOrigemBase.addActionListener(this);
        jPanel7.add(jButtonPastaOrigemBase);

        jPanelOpcoesEstrutOriginal.add(jPanel7);

        jPanel6.add(jPanelOpcoesEstrutOriginal, "card2");

        jPanelOpcoesDadosMusica.setBorder(javax.swing.BorderFactory.createTitledBorder("Opções - Baseado nos Dados das Músicas"));
        jPanelOpcoesDadosMusica.setLayout(new javax.swing.BoxLayout(jPanelOpcoesDadosMusica, javax.swing.BoxLayout.Y_AXIS));

        jPanel13.setBorder(javax.swing.BorderFactory.createTitledBorder("Estrutura de Pastas"));
        jPanel13.setLayout(new java.awt.GridLayout(0, 2));

        buttonGroupOpcoesEstruturaDadosMusica.add(jRadioButtonEstArtistaAlbum);
        jRadioButtonEstArtistaAlbum.setText("Artista/Album/[Músicas]");
        jPanel13.add(jRadioButtonEstArtistaAlbum);

        buttonGroupOpcoesEstruturaDadosMusica.add(jRadioButtonEstArtista);
        jRadioButtonEstArtista.setText("Artista/[Músicas]");
        jPanel13.add(jRadioButtonEstArtista);

        buttonGroupOpcoesEstruturaDadosMusica.add(jRadioButtonEstAlbumArtista);
        jRadioButtonEstAlbumArtista.setText("Album/Artista/[Músicas]");
        jPanel13.add(jRadioButtonEstAlbumArtista);

        buttonGroupOpcoesEstruturaDadosMusica.add(jRadioButtonEstAlbum);
        jRadioButtonEstAlbum.setText("Album/[Músicas]");
        jPanel13.add(jRadioButtonEstAlbum);

        jPanelOpcoesDadosMusica.add(jPanel13);

        jPanel6.add(jPanelOpcoesDadosMusica, "card3");

        javax.swing.GroupLayout jPanelOpcoesSemEstruturaLayout = new javax.swing.GroupLayout(jPanelOpcoesSemEstrutura);
        jPanelOpcoesSemEstrutura.setLayout(jPanelOpcoesSemEstruturaLayout);
        jPanelOpcoesSemEstruturaLayout.setHorizontalGroup(
            jPanelOpcoesSemEstruturaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 600, Short.MAX_VALUE)
        );
        jPanelOpcoesSemEstruturaLayout.setVerticalGroup(
            jPanelOpcoesSemEstruturaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 135, Short.MAX_VALUE)
        );

        jPanel6.add(jPanelOpcoesSemEstrutura, "card4");

        jPanel11.add(jPanel6);

        jPanel12.setBorder(javax.swing.BorderFactory.createTitledBorder("Arquivos Existentes"));
        jPanel12.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 2));

        buttonGroupSunstitIgnorar.add(jRadioButtonSubstituir);
        jRadioButtonSubstituir.setText("Substituir");
        jPanel12.add(jRadioButtonSubstituir);

        buttonGroupSunstitIgnorar.add(jRadioButtonIgnorar);
        jRadioButtonIgnorar.setSelected(true);
        jRadioButtonIgnorar.setText("Ignorar");
        jPanel12.add(jRadioButtonIgnorar);

        jPanel11.add(jPanel12);

        jPanel1.add(jPanel11);

        jPanel8.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        jLabel5.setText("Qtd. Músicas:");
        jPanel8.add(jLabel5);

        jLabelQtdMusicas.setPreferredSize(new java.awt.Dimension(250, 19));
        jPanel8.add(jLabelQtdMusicas);

        jPanel1.add(jPanel8);

        jPanel9.setLayout(new java.awt.GridLayout(1, 0));

        jProgressBar1.setString("Copiando músicas...");
        jProgressBar1.setStringPainted(true);
        jPanel9.add(jProgressBar1);

        jPanel1.add(jPanel9);

        getContentPane().add(jPanel1, java.awt.BorderLayout.CENTER);

        jButtonCopiar.setText("Copiar");
        jButtonCopiar.addActionListener(this);
        jPanel3.add(jButtonCopiar);

        jButtonCancelar.setText("Cancelar");
        jButtonCancelar.addActionListener(this);
        jPanel3.add(jButtonCancelar);

        getContentPane().add(jPanel3, java.awt.BorderLayout.PAGE_END);

        setSize(new java.awt.Dimension(624, 463));
        setLocationRelativeTo(null);
    }

    // Code for dispatching events from components to event handlers.

    public void actionPerformed(java.awt.event.ActionEvent evt) {
        if (evt.getSource() == jButtonPastaDestino) {
            JCopiador.this.jButtonPastaDestinoActionPerformed(evt);
        }
        else if (evt.getSource() == jButtonPastaOrigemBase) {
            JCopiador.this.jButtonPastaOrigemBaseActionPerformed(evt);
        }
        else if (evt.getSource() == jButtonCopiar) {
            JCopiador.this.jButtonCopiarActionPerformed(evt);
        }
        else if (evt.getSource() == jButtonCancelar) {
            JCopiador.this.jButtonCancelarActionPerformed(evt);
        }
    }

    public void itemStateChanged(java.awt.event.ItemEvent evt) {
        if (evt.getSource() == jComboBoxTipoEstruturaDestino) {
            JCopiador.this.jComboBoxTipoEstruturaDestinoItemStateChanged(evt);
        }
    }// </editor-fold>//GEN-END:initComponents

    private void jComboBoxTipoEstruturaDestinoItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBoxTipoEstruturaDestinoItemStateChanged
        if (evt.getStateChange() == ItemEvent.SELECTED) {
            switch ((TipoEstruturaDestino) jComboBoxTipoEstruturaDestino.getSelectedItem()) {
                case ESTRUTURA_ORIGINAL:
                    jPanelOpcoesEstrutOriginal.setVisible(true);
                    jPanelOpcoesDadosMusica.setVisible(false);
                    jPanelOpcoesSemEstrutura.setVisible(false);
                    break;
                case ESTRUTURA_DADOS_MUSICA:
                    jPanelOpcoesDadosMusica.setVisible(true);
                    jPanelOpcoesEstrutOriginal.setVisible(false);
                    jPanelOpcoesSemEstrutura.setVisible(false);
                    break;
                case SEM_ESTRUTURA_DIRETORIO:
                    jPanelOpcoesSemEstrutura.setVisible(true);
                    jPanelOpcoesDadosMusica.setVisible(false);
                    jPanelOpcoesEstrutOriginal.setVisible(false);

            }
        }
    }//GEN-LAST:event_jComboBoxTipoEstruturaDestinoItemStateChanged

    private void jButtonPastaDestinoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonPastaDestinoActionPerformed
        selecionarPastaDestino();
    }//GEN-LAST:event_jButtonPastaDestinoActionPerformed

    private void jButtonPastaOrigemBaseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonPastaOrigemBaseActionPerformed
        selecionaPastaOrigemBase();
    }//GEN-LAST:event_jButtonPastaOrigemBaseActionPerformed

    private void jButtonCopiarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCopiarActionPerformed
        thread = new Thread(this);
        thread.setPriority(Thread.MIN_PRIORITY);
        thread.start();
    }//GEN-LAST:event_jButtonCopiarActionPerformed

    private void jButtonCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCancelarActionPerformed
        if (thread != null) {
            if (!jButtonCopiar.isEnabled() && !thread.isInterrupted()) {
                thread.interrupt();
            }
        }
        dispose();
    }//GEN-LAST:event_jButtonCancelarActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroupOpcoesEstruturaDadosMusica;
    private javax.swing.ButtonGroup buttonGroupSunstitIgnorar;
    private javax.swing.JButton jButtonCancelar;
    private javax.swing.JButton jButtonCopiar;
    private javax.swing.JButton jButtonPastaDestino;
    private javax.swing.JButton jButtonPastaOrigemBase;
    private javax.swing.JComboBox jComboBoxTipoEstruturaDestino;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabelQtdMusicas;
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
    private javax.swing.JPanel jPanelOpcoesDadosMusica;
    private javax.swing.JPanel jPanelOpcoesEstrutOriginal;
    private javax.swing.JPanel jPanelOpcoesSemEstrutura;
    private javax.swing.JProgressBar jProgressBar1;
    private javax.swing.JRadioButton jRadioButtonEstAlbum;
    private javax.swing.JRadioButton jRadioButtonEstAlbumArtista;
    private javax.swing.JRadioButton jRadioButtonEstArtista;
    private javax.swing.JRadioButton jRadioButtonEstArtistaAlbum;
    private javax.swing.JRadioButton jRadioButtonIgnorar;
    private javax.swing.JRadioButton jRadioButtonSubstituir;
    private javax.swing.JTextField jTextFieldPastaDestino;
    private javax.swing.JTextField jTextFieldPastaOrigemBase;
    private javax.swing.JTextField jTextFieldPlayListOrigem;
    // End of variables declaration//GEN-END:variables

    private boolean mostrarMensagemErro(IOException ex, int i, Musica musica) throws HeadlessException {
        ex.printStackTrace(System.err);
        if (i == musicas.size() - 1) {
            JOptionPane.showMessageDialog(this, "Não foi possível copiar o arquivo");
        } else {
            if (JOptionPane.showConfirmDialog(this, "Não foi possível copiar o arquivo ["
                    + musica.getCaminho() + "]"
                    + "\nDeseja ignora-lo e continuar copiando os outros?") == JOptionPane.NO_OPTION) {
                return true;
            }
        }
        return false;
    }
}
