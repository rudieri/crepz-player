/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.utils.pele;

import com.main.Carregador;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.border.BevelBorder;

/**
 *
 * @author rudieri
 */
public class JPele extends javax.swing.JDialog implements CorSelecionadaListener, ActionListener, MouseListener {

    /**
     * Creates new form JPele
     */
    private JSelecionaCor jSelecionaCor;
    private JLabel componenteSelecionado;
    private DefaultComboBoxModel defaultComboBoxModel = new DefaultComboBoxModel();
    private final Carregador carregador;

    public JPele(Carregador carregador) {
        initComponents();
        jSelecionaCor = new JSelecionaCor(null, false);
        this.carregador = carregador;
        startEvents();
    }

    @Override
    public void corSelecionada(Color cor) {
        if (componenteSelecionado == null) {
            throw new IllegalStateException("O componente selecionado não pode ser nulo.");
        }
        componenteSelecionado.setBackground(cor);
    }

    @Override
    public void setVisible(boolean b) {
        super.setVisible(b);
        try {
            defaultComboBoxModel.removeAllElements();
            for (int i = 0; i < ColorUtils.getListaPelesConhecidas().size(); i++) {
                Pele pele = ColorUtils.getListaPelesConhecidas().get(i);
                defaultComboBoxModel.addElement(pele.getNome());
            }
            jComboBox1.setModel(defaultComboBoxModel);
            jComboBox1.setSelectedItem(ColorUtils.getPeleAtual().getNome());
            setPele(ColorUtils.getPeleAtual());
        } catch (Exception ex) {
            ex.printStackTrace(System.err);
        }
    }

    private void selecionarCor(Object source) {
        componenteSelecionado = (JLabel) source;
        jSelecionaCor.setVisible(true);
    }

    private void setPele(Pele pele) {
        jPanelEditarCores.setVisible(pele != Pele.PELE_PADRAO);
        jLabelCorFundoObjetos.setBackground(pele.getFundoTabelaNaoSelecionada());
        jLabelCorFundoSelecionado.setBackground(pele.getFundoTabelaSelecionada());
        jLabelCorTextoObjetos.setBackground(pele.getFrenteTabelaNaoSelecionada());
        jLabelCorTextoSelecionado.setBackground(pele.getFrenteTabelaSelecionada());
        jLabelCorFundoJanela.setBackground(pele.getFundoJanela());
        jLabelCorFundoJanela.setBorder(new BevelBorder(BevelBorder.LOWERED, ColorUtils.getFrenteJanela(), ColorUtils.getFrenteJanela()));
        jLabelCorTextoJanela.setBackground(pele.getFrenteJanela());
        jLabelCorTextoJanela.setBorder(new BevelBorder(BevelBorder.LOWERED, ColorUtils.getFundoJanela(), ColorUtils.getFundoJanela()));
    }

    private void startEvents() {
        // action
        jSelecionaCor.addCorSelecionadaListener(this);
        jButtonSalvar.addActionListener(this);
        jButtonCancelar.addActionListener(this);
        jButtonNovo.addActionListener(this);
        
        // mouse listener
        jLabelCorFundoJanela.addMouseListener(this);
        jLabelCorTextoJanela.addMouseListener(this);
        jLabelCorFundoSelecionado.addMouseListener(this);
        jLabelCorTextoSelecionado.addMouseListener(this);
        jLabelCorFundoObjetos.addMouseListener(this);
        jLabelCorTextoObjetos.addMouseListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == jButtonSalvar) {
            ColorUtils.setNomePele(jComboBox1.getSelectedItem().toString());
            ColorUtils.setFrenteTabelaSelecionada(jLabelCorTextoSelecionado.getBackground());
            ColorUtils.setFrenteTabelaNaoSelecionada(jLabelCorTextoObjetos.getBackground());
            ColorUtils.setFundoTabelaSelecionada(jLabelCorFundoSelecionado.getBackground());
            ColorUtils.setFundoTabelaNaoSelecionada(jLabelCorFundoObjetos.getBackground());
            ColorUtils.setFrenteJanela(jLabelCorTextoJanela.getBackground());
            ColorUtils.setFundoJanela(jLabelCorFundoJanela.getBackground());
            ColorUtils.aplicarTema();
            if (ColorUtils.getPeleAtual() == Pele.PELE_PADRAO) {
                int showConfirmDialog = JOptionPane.showConfirmDialog(this, "O Crepz deve ser reiniciado.\nDeseja fechá-lo agora?", "Fechar crepz...", JOptionPane.YES_NO_OPTION);
                if (showConfirmDialog == JOptionPane.YES_OPTION) {
                    carregador.sair();
                }
            }
            dispose();
        } else if (e.getSource() == jButtonCancelar) {
            dispose();
        } else if (e.getSource() == jButtonNovo) {
            Pele pele = new Pele("Novo...");
            jComboBox1.addItem(pele.getNome());
            jComboBox1.setSelectedItem(pele.getNome());
            setPele(pele);
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getSource() == jLabelCorFundoJanela 
                || e.getSource() == jLabelCorTextoJanela
                || e.getSource() == jLabelCorFundoSelecionado 
                || e.getSource() == jLabelCorTextoSelecionado
                || e.getSource() == jLabelCorFundoObjetos 
                || e.getSource() == jLabelCorTextoObjetos) {
            selecionarCor(e.getSource());
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
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
        jPanel5 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox();
        jButtonNovo = new javax.swing.JButton();
        jPanelEditarCores = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabelCorFundoJanela = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabelCorTextoJanela = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabelCorFundoSelecionado = new javax.swing.JLabel();
        jPanel8 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jLabelCorTextoSelecionado = new javax.swing.JLabel();
        jPanel9 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jLabelCorFundoObjetos = new javax.swing.JLabel();
        jPanel11 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jLabelCorTextoObjetos = new javax.swing.JLabel();
        jPanel10 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jButtonSalvar = new javax.swing.JButton();
        jButtonCancelar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanel1.setLayout(new java.awt.BorderLayout());

        jPanel5.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        jLabel7.setText("Tema: ");
        jLabel7.setPreferredSize(new java.awt.Dimension(120, 16));
        jPanel5.add(jLabel7);

        jComboBox1.setEditable(true);
        jComboBox1.setPreferredSize(new java.awt.Dimension(200, 26));
        jComboBox1.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBox1ItemStateChanged(evt);
            }
        });
        jComboBox1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jComboBox1KeyPressed(evt);
            }
        });
        jPanel5.add(jComboBox1);

        jButtonNovo.setText("Novo");
        jPanel5.add(jButtonNovo);

        jPanel1.add(jPanel5, java.awt.BorderLayout.NORTH);

        jPanelEditarCores.setLayout(new javax.swing.BoxLayout(jPanelEditarCores, javax.swing.BoxLayout.Y_AXIS));

        jPanel4.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        jLabel1.setText("Fundo Janelas:");
        jLabel1.setPreferredSize(new java.awt.Dimension(120, 16));
        jPanel4.add(jLabel1);

        jLabelCorFundoJanela.setBackground(new java.awt.Color(1, 1, 1));
        jLabelCorFundoJanela.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jLabelCorFundoJanela.setOpaque(true);
        jLabelCorFundoJanela.setPreferredSize(new java.awt.Dimension(22, 20));
        jPanel4.add(jLabelCorFundoJanela);

        jPanelEditarCores.add(jPanel4);

        jPanel6.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        jLabel2.setText("Texto Janelas:");
        jLabel2.setPreferredSize(new java.awt.Dimension(120, 16));
        jPanel6.add(jLabel2);

        jLabelCorTextoJanela.setBackground(new java.awt.Color(1, 1, 1));
        jLabelCorTextoJanela.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jLabelCorTextoJanela.setOpaque(true);
        jLabelCorTextoJanela.setPreferredSize(new java.awt.Dimension(22, 20));
        jPanel6.add(jLabelCorTextoJanela);

        jPanelEditarCores.add(jPanel6);

        jPanel7.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        jLabel3.setText("Fundo Selecionado:");
        jLabel3.setPreferredSize(new java.awt.Dimension(120, 16));
        jPanel7.add(jLabel3);

        jLabelCorFundoSelecionado.setBackground(new java.awt.Color(1, 1, 1));
        jLabelCorFundoSelecionado.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jLabelCorFundoSelecionado.setOpaque(true);
        jLabelCorFundoSelecionado.setPreferredSize(new java.awt.Dimension(22, 20));
        jPanel7.add(jLabelCorFundoSelecionado);

        jPanelEditarCores.add(jPanel7);

        jPanel8.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        jLabel4.setText("Texto Selecionado:");
        jLabel4.setPreferredSize(new java.awt.Dimension(120, 16));
        jPanel8.add(jLabel4);

        jLabelCorTextoSelecionado.setBackground(new java.awt.Color(1, 1, 1));
        jLabelCorTextoSelecionado.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jLabelCorTextoSelecionado.setOpaque(true);
        jLabelCorTextoSelecionado.setPreferredSize(new java.awt.Dimension(22, 20));
        jPanel8.add(jLabelCorTextoSelecionado);

        jPanelEditarCores.add(jPanel8);

        jPanel9.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        jLabel5.setText("Fundo Objetos:");
        jLabel5.setPreferredSize(new java.awt.Dimension(120, 16));
        jPanel9.add(jLabel5);

        jLabelCorFundoObjetos.setBackground(new java.awt.Color(1, 1, 1));
        jLabelCorFundoObjetos.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jLabelCorFundoObjetos.setOpaque(true);
        jLabelCorFundoObjetos.setPreferredSize(new java.awt.Dimension(22, 20));
        jPanel9.add(jLabelCorFundoObjetos);

        jPanelEditarCores.add(jPanel9);

        jPanel11.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        jLabel6.setText("Texto Objetos:");
        jLabel6.setPreferredSize(new java.awt.Dimension(120, 16));
        jPanel11.add(jLabel6);

        jLabelCorTextoObjetos.setBackground(new java.awt.Color(1, 1, 1));
        jLabelCorTextoObjetos.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jLabelCorTextoObjetos.setOpaque(true);
        jLabelCorTextoObjetos.setPreferredSize(new java.awt.Dimension(22, 20));
        jPanel11.add(jLabelCorTextoObjetos);

        jPanelEditarCores.add(jPanel11);

        jPanel10.setLayout(new java.awt.BorderLayout());
        jPanelEditarCores.add(jPanel10);

        jPanel1.add(jPanelEditarCores, java.awt.BorderLayout.CENTER);

        getContentPane().add(jPanel1, java.awt.BorderLayout.CENTER);
        getContentPane().add(jPanel2, java.awt.BorderLayout.PAGE_START);

        jButtonSalvar.setText("OK");
        jPanel3.add(jButtonSalvar);

        jButtonCancelar.setText("Cancelar");
        jPanel3.add(jButtonCancelar);

        getContentPane().add(jPanel3, java.awt.BorderLayout.PAGE_END);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jComboBox1ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBox1ItemStateChanged
        if (evt.getStateChange() == ItemEvent.SELECTED) {
            Pele pele = ColorUtils.getPelePorNome(evt.getItem().toString());
            if (pele == null) {
                pele = new Pele(jComboBox1.getSelectedItem().toString());
            }
            setPele(pele);
        }


    }//GEN-LAST:event_jComboBox1ItemStateChanged

    private void jComboBox1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jComboBox1KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            Pele pele = ColorUtils.getPelePorNome(jComboBox1.getSelectedItem().toString());
            if (pele == null) {
                pele = new Pele(jComboBox1.getSelectedItem().toString());
            }
            setPele(pele);
        }
    }//GEN-LAST:event_jComboBox1KeyPressed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonCancelar;
    private javax.swing.JButton jButtonNovo;
    private javax.swing.JButton jButtonSalvar;
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabelCorFundoJanela;
    private javax.swing.JLabel jLabelCorFundoObjetos;
    private javax.swing.JLabel jLabelCorFundoSelecionado;
    private javax.swing.JLabel jLabelCorTextoJanela;
    private javax.swing.JLabel jLabelCorTextoObjetos;
    private javax.swing.JLabel jLabelCorTextoSelecionado;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JPanel jPanelEditarCores;
    // End of variables declaration//GEN-END:variables
}
