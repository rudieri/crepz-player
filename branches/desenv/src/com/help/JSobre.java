/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * Aguarde.java
 *
 * Created on 17/06/2010, 20:44:20
 */
package com.help;

import com.main.gui.Aguarde;
import javax.swing.JFrame;

/**
 *
 * @author manchini
 */
public class JSobre extends javax.swing.JDialog {

    /**
     * Creates new form Aguarde
     */
    public JSobre(JFrame parent) {
        super(parent, false);
        initComponents();
        jPanel2.setOpaque(true);
        jPanel1.setOpaque(true);
        try {
            new Aguarde().intro();
        } catch (Exception ex) {
            ex.printStackTrace(System.err);
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

        jPanel3 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jPanel8 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Crepz Player Sobre");
        setBackground(new java.awt.Color(0, 0, 0));
        setMinimumSize(new java.awt.Dimension(302, 420));
        setResizable(false);
        getContentPane().setLayout(new javax.swing.BoxLayout(getContentPane(), javax.swing.BoxLayout.LINE_AXIS));

        jPanel3.setBackground(new java.awt.Color(0, 0, 0));
        jPanel3.setMinimumSize(new java.awt.Dimension(306, 306));
        jPanel3.setPreferredSize(new java.awt.Dimension(302, 420));
        jPanel3.setLayout(new java.awt.BorderLayout());

        jPanel2.setBackground(new java.awt.Color(0, 0, 0));
        jPanel2.setPreferredSize(new java.awt.Dimension(397, 110));
        jPanel2.setLayout(new javax.swing.BoxLayout(jPanel2, javax.swing.BoxLayout.Y_AXIS));

        jPanel4.setBackground(new java.awt.Color(0, 0, 0));
        jPanel4.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        jLabel2.setBackground(new java.awt.Color(0, 0, 0));
        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("Desenvolvimento:");
        jLabel2.setOpaque(true);
        jPanel4.add(jLabel2);

        jPanel2.add(jPanel4);

        jPanel5.setBackground(new java.awt.Color(0, 0, 0));
        jPanel5.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 1));

        jLabel3.setBackground(new java.awt.Color(0, 0, 0));
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("Fernando Manchini,");
        jLabel3.setOpaque(true);
        jPanel5.add(jLabel3);

        jPanel2.add(jPanel5);

        jPanel6.setBackground(new java.awt.Color(0, 0, 0));
        jPanel6.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 1));

        jLabel4.setBackground(new java.awt.Color(0, 0, 0));
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setText("Marcelo Arndt da Silva ");
        jLabel4.setOpaque(true);
        jPanel6.add(jLabel4);

        jPanel2.add(jPanel6);

        jPanel7.setBackground(new java.awt.Color(0, 0, 0));
        jPanel7.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 1));

        jLabel5.setBackground(new java.awt.Color(0, 0, 0));
        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setText("Rudieri Turchiello Colbek");
        jLabel5.setOpaque(true);
        jPanel7.add(jLabel5);

        jPanel2.add(jPanel7);

        jPanel8.setBackground(new java.awt.Color(0, 0, 0));
        jPanel8.setLayout(new java.awt.BorderLayout());
        jPanel2.add(jPanel8);

        jPanel3.add(jPanel2, java.awt.BorderLayout.SOUTH);

        jPanel1.setBackground(new java.awt.Color(0, 0, 0));
        jPanel1.setLayout(new java.awt.BorderLayout());

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/img/capa.jpg"))); // NOI18N
        jPanel1.add(jLabel1, java.awt.BorderLayout.CENTER);

        jPanel3.add(jPanel1, java.awt.BorderLayout.PAGE_START);

        getContentPane().add(jPanel3);

        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        setBounds((screenSize.width-306)/2, (screenSize.height-476)/2, 306, 476);
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    // End of variables declaration//GEN-END:variables
}
