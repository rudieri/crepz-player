/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.playlist.listainteligente.condicao;

import com.musica.Musica;
import com.playlist.listainteligente.condicao.operadores.OperadorComparativo;
import com.playlist.listainteligente.condicao.operadores.OperadorLogico;
import com.utils.campo.Campo;
import com.utils.model.comboboxmodel.ComboBoxModelEditavel;
import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import javax.swing.JDialog;

/**
 *
 * @author rudieri
 */
public class JCondicao extends javax.swing.JPanel implements ItemListener {

    private static final String ESCOLHA_UM_OPERADOR = "Escolha um operador";
    private static final String ESCOLHA_UM_CAMPO = "Escolha um campo";
    private ComboBoxModelEditavel comboBoxModelValor1;
    private ComboBoxModelEditavel comboBoxModelOperadorComparativo;
    private ComboBoxModelEditavel comboBoxModelOperadorLogico;

    /**
     * Creates new form Jcondicao
     */
    public JCondicao() {
        init();
    }

    private void init() {
        initComponents();

        comboBoxModelValor1 = new ComboBoxModelEditavel();
        jComboBox_Valor1.setModel(comboBoxModelValor1);
        comboBoxModelValor1.setTextoNenhumItemSelecionado(ESCOLHA_UM_CAMPO);

        comboBoxModelOperadorComparativo = new ComboBoxModelEditavel();
        comboBoxModelOperadorComparativo.setTextoNenhumItemSelecionado(ESCOLHA_UM_OPERADOR);
        comboBoxModelOperadorComparativo.addAll(OperadorComparativo.values());
        jComboBox_OperadorComparativo.setModel(comboBoxModelOperadorComparativo);

        comboBoxModelOperadorLogico = new ComboBoxModelEditavel();
        comboBoxModelOperadorLogico.setTextoNenhumItemSelecionado(ESCOLHA_UM_OPERADOR);
        comboBoxModelOperadorLogico.addAll(OperadorLogico.values());
        jComboBox_OperadorLogico.setModel(comboBoxModelOperadorLogico);

        Field[] campos = Musica.class.getDeclaredFields();

        for (Field field : campos) {
            if (!Modifier.isStatic(field.getModifiers()) && Campo.contemAnotacaoNecessaria(field)) {
                comboBoxModelValor1.addItem(new Campo(field));
            }
        }
    }

    public void setCondicao(Condicao condicao) {
        switch (condicao.getTipoValorCondicao1()) {
            case CAMPO:
                jRadioButton_TipocondicaoComparativa.setSelected(true);
                jComboBox_Valor1.setSelectedItem(((ValorCondicao) condicao.getValor1()).getCampo());
                jComboBox_OperadorComparativo.setSelectedItem(condicao.getOperador());
                jTextField_Valor2.setText(condicao.getValor2().toString());
                break;
            case CONDICAO:
                jRadioButton_TipoCondicaoLogica.setSelected(true);
                jSelecionaCondicao1.setCondicao((Condicao) condicao.getValor1());
                jSelecionaCondicao2.setCondicao((Condicao) condicao.getValor2());
                jComboBox_OperadorLogico.setSelectedItem(condicao.getOperador());
                break;
        }
    }

    public Condicao getCondicao() {
        if (jRadioButton_TipocondicaoComparativa.isSelected()) {
            if (comboBoxModelOperadorComparativo.getSelectedItem() == ESCOLHA_UM_OPERADOR) {
                throw new IllegalStateException("Operador não escolhido.");
            }
            if (jComboBox_Valor1.getSelectedItem() == ESCOLHA_UM_CAMPO) {
                throw new IllegalStateException("Campo da música não escolhido.");
            }
            if (jTextField_Valor2.getText().isEmpty()) {
                throw new IllegalStateException("Valor do segundo campo da condição não informado.");
            }
            Condicao condicao = new Condicao();
            OperadorComparativo operador = (OperadorComparativo) comboBoxModelOperadorComparativo.getSelectedItem();
            ValorCondicao valor1 = new ValorCondicao((Campo) jComboBox_Valor1.getSelectedItem());
            Campo campo = (Campo) jComboBox_Valor1.getSelectedItem();
            ValorCondicao valor2;
            if (campo.isInteger()) {
                valor2 = new ValorCondicao(Integer.valueOf(jTextField_Valor2.getText()));
            } else if (campo.isByte()) {
                valor2 = new ValorCondicao(Byte.valueOf(jTextField_Valor2.getText()));
            } else if (campo.isShort()) {
                valor2 = new ValorCondicao(Short.valueOf(jTextField_Valor2.getText()));
            } else if (campo.isLong()) {
                valor2 = new ValorCondicao(Long.valueOf(jTextField_Valor2.getText()));
            } else if (campo.isBoolean()) {
                if (jTextField_Valor2.getText().matches("[0]|[Nn][AaÃa][Oo]|[Ff][Aa][Ll][Ss][Ee]")) {
                    valor2 = new ValorCondicao(false);
                } else {
                    valor2 = new ValorCondicao(true);
                }
            } else {
                valor2 = new ValorCondicao(jTextField_Valor2.getText());
            }
            condicao.setValoresCondicao(operador, valor1, valor2);
            return condicao;
        } else {
            Object selectedItem = comboBoxModelOperadorLogico.getSelectedItem();
            if (selectedItem.equals(ESCOLHA_UM_OPERADOR)) {
                throw new IllegalStateException("Operador não escolhido.");
            }
            Condicao condicao = new Condicao();
            OperadorLogico operador = (OperadorLogico) selectedItem;
            condicao.setValoresCondicao(operador, jSelecionaCondicao1.getCondicao(), jSelecionaCondicao2.getCondicao());
            return condicao;
        }
    }

    public static void main(String[] args) {
        JDialog jDialog = new JDialog((JDialog) null, true);
        jDialog.add(new JCondicao());
        jDialog.setPreferredSize(new Dimension(400, 70));
        jDialog.setSize(700, 90);
        jDialog.setLocation(500, 500);
        jDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        jDialog.setVisible(true);
        System.exit(0);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        jPanel8 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jRadioButton_TipocondicaoComparativa = new javax.swing.JRadioButton();
        jRadioButton_TipoCondicaoLogica = new javax.swing.JRadioButton();
        jPanel1 = new javax.swing.JPanel();
        jPanel_Comparativa = new javax.swing.JPanel();
        jComboBox_Valor1 = new javax.swing.JComboBox();
        jComboBox_OperadorComparativo = new javax.swing.JComboBox();
        jTextField_Valor2 = new javax.swing.JTextField();
        jPanel_Logica = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jSelecionaCondicao1 = new com.playlist.listainteligente.condicao.JSelecionaCondicao();
        jComboBox_OperadorLogico = new javax.swing.JComboBox();
        jPanel6 = new javax.swing.JPanel();
        jSelecionaCondicao2 = new com.playlist.listainteligente.condicao.JSelecionaCondicao();
        jPanel7 = new javax.swing.JPanel();

        setBorder(javax.swing.BorderFactory.createEtchedBorder());
        setLayout(new java.awt.BorderLayout());

        jPanel8.setLayout(new javax.swing.BoxLayout(jPanel8, javax.swing.BoxLayout.Y_AXIS));

        jPanel2.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        jLabel1.setText("Tipo de Condição:");
        jLabel1.setPreferredSize(new java.awt.Dimension(135, 15));
        jPanel2.add(jLabel1);

        buttonGroup1.add(jRadioButton_TipocondicaoComparativa);
        jRadioButton_TipocondicaoComparativa.setSelected(true);
        jRadioButton_TipocondicaoComparativa.setText("Comparativa");
        jRadioButton_TipocondicaoComparativa.addItemListener(this);
        jPanel2.add(jRadioButton_TipocondicaoComparativa);

        buttonGroup1.add(jRadioButton_TipoCondicaoLogica);
        jRadioButton_TipoCondicaoLogica.setText("Lógica");
        jRadioButton_TipoCondicaoLogica.addItemListener(this);
        jPanel2.add(jRadioButton_TipoCondicaoLogica);

        jPanel8.add(jPanel2);

        jPanel1.setLayout(new java.awt.CardLayout());

        jPanel_Comparativa.setLayout(new java.awt.GridLayout(1, 3));

        jComboBox_Valor1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jPanel_Comparativa.add(jComboBox_Valor1);

        jComboBox_OperadorComparativo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jPanel_Comparativa.add(jComboBox_OperadorComparativo);
        jPanel_Comparativa.add(jTextField_Valor2);

        jPanel1.add(jPanel_Comparativa, "card2");

        jPanel_Logica.setLayout(new java.awt.GridLayout(1, 3));

        jPanel5.setLayout(new java.awt.BorderLayout());
        jPanel5.add(jSelecionaCondicao1, java.awt.BorderLayout.CENTER);

        jPanel_Logica.add(jPanel5);

        jComboBox_OperadorLogico.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jPanel_Logica.add(jComboBox_OperadorLogico);

        jPanel6.setLayout(new java.awt.BorderLayout());
        jPanel6.add(jSelecionaCondicao2, java.awt.BorderLayout.CENTER);

        jPanel_Logica.add(jPanel6);

        jPanel1.add(jPanel_Logica, "card3");

        jPanel8.add(jPanel1);

        add(jPanel8, java.awt.BorderLayout.PAGE_START);

        jPanel7.setLayout(new java.awt.BorderLayout());
        add(jPanel7, java.awt.BorderLayout.CENTER);
    }

    // Code for dispatching events from components to event handlers.

    public void itemStateChanged(java.awt.event.ItemEvent evt) {
        if (evt.getSource() == jRadioButton_TipocondicaoComparativa) {
            JCondicao.this.jRadioButton_TipocondicaoComparativaItemStateChanged(evt);
        }
        else if (evt.getSource() == jRadioButton_TipoCondicaoLogica) {
            JCondicao.this.jRadioButton_TipoCondicaoLogicaItemStateChanged(evt);
        }
    }// </editor-fold>//GEN-END:initComponents

    private void jRadioButton_TipocondicaoComparativaItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jRadioButton_TipocondicaoComparativaItemStateChanged
        if (evt.getStateChange() == ItemEvent.SELECTED) {
            jPanel_Comparativa.setVisible(true);
            jPanel_Logica.setVisible(false);
        }
    }//GEN-LAST:event_jRadioButton_TipocondicaoComparativaItemStateChanged

    private void jRadioButton_TipoCondicaoLogicaItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jRadioButton_TipoCondicaoLogicaItemStateChanged
        if (evt.getStateChange() == ItemEvent.SELECTED) {
            jPanel_Logica.setVisible(true);
            jPanel_Comparativa.setVisible(false);
        }
    }//GEN-LAST:event_jRadioButton_TipoCondicaoLogicaItemStateChanged
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JComboBox jComboBox_OperadorComparativo;
    private javax.swing.JComboBox jComboBox_OperadorLogico;
    private javax.swing.JComboBox jComboBox_Valor1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel_Comparativa;
    private javax.swing.JPanel jPanel_Logica;
    private javax.swing.JRadioButton jRadioButton_TipoCondicaoLogica;
    private javax.swing.JRadioButton jRadioButton_TipocondicaoComparativa;
    private com.playlist.listainteligente.condicao.JSelecionaCondicao jSelecionaCondicao1;
    private com.playlist.listainteligente.condicao.JSelecionaCondicao jSelecionaCondicao2;
    private javax.swing.JTextField jTextField_Valor2;
    // End of variables declaration//GEN-END:variables
}
