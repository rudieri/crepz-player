/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * JHelp.java
 *
 * Created on 20/06/2010, 11:34:53
 */
package com.help;

import com.JPrincipal;
import java.io.File;
import javax.swing.JEditorPane;
import javax.swing.JFrame;

/**
 *
 * @author manchini
 */
public class JHelp extends javax.swing.JDialog {

    /** Creates new form JHelp */
    public JHelp(JFrame parent) {
        super(parent,false);
        initComponents();
        try{
        JPrincipal.aguarde.intro();
        }catch(Exception ex){
            ex.printStackTrace();
        }
//        jEditorPane1 = new JEditorPane(new File(getClass().getResource("/com/help/Help.html").toURI()).toURL());

    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        try{
            jEditorPane1 = new JEditorPane(new File(getClass().getResource("/com/help/Help.html").toURI()).toURL());
        }catch(Exception ex){
            ex.printStackTrace();
        }

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Crepz Player HELP!");

        jScrollPane1.setViewportView(jEditorPane1);

        getContentPane().add(jScrollPane1, java.awt.BorderLayout.CENTER);

        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        setBounds((screenSize.width-557)/2, (screenSize.height-598)/2, 557, 598);
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                new JHelp(new JFrame()).setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JEditorPane jEditorPane1;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables
}
