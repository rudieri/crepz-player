/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * Aguarde.java
 *
 * Created on 17/06/2010, 20:44:20
 */

package com;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javazoom.jlgui.basicplayer.BasicPlayer;
import javazoom.jlgui.basicplayer.BasicPlayerException;

/**
 *
 * @author manchini
 */
public class Aguarde extends javax.swing.JFrame{

    /** Creates new form Aguarde */
    public Aguarde() {
        initComponents();
        this.setIconImage(new ImageIcon(getClass().getResource("/com/img/icon.png")).getImage());
    }

    public void intro(){
        try {
            BasicPlayer player = new BasicPlayer();
            player.open(getClass().getResource("/com/img/intro.mp3"));
            player.play();
        } catch (BasicPlayerException ex) {
            ex.printStackTrace();
            System.out.println("Crepz no intro!");
        }
    }


    public void standBy(){
        new Thread(new Runnable() {

            public void run() {
                int count = 0;
                while(true){
                    try {
                        count++;
                        jProgressBar1.setValue(count*10);
                        if (count == 1) {
                            jProgressBar1.setString("Aguarde...");
                        }
                        if (count == 5) {
                            jProgressBar1.setString("Aguarde Mais Um Pouco");
                        }
                        if (count == 7) {
                            jProgressBar1.setString("Ta Um Pouco Demorado");
                        }
                        if (count == 8) {
                            jProgressBar1.setString("ZZZZZzzzzzZZZZZZzzz");
                        }
                        if (count == 10) {
                            jProgressBar1.setString("RRuuuuuu.... uiiiiiiii");
                        }
                        if (count == 15) {
                            jProgressBar1.setString("N�o Aperte Ctrl+Alt+Del");
                        }
                        if (count == 18) {
                            jProgressBar1.setString("Eu Consigo....");
                        }
                        if (count == 20) {
                            jProgressBar1.setString("Ta Quase...");
                        }
                        if (count == 25) {
                            jProgressBar1.setString("Ou N�o");
                        }
                        if (count == 35) {
                            jProgressBar1.setString("To Quase Desistindo");
                        }
                        if (count == 40) {
                            jProgressBar1.setString("Sou Brasileiro");
                        }
                        if (count == 43) {
                            jProgressBar1.setString("N�o Desisto Nunca");
                        }
                        if (count == 50) {
                            jProgressBar1.setString("T� Bom Pode Fecha.");
                        }
                        Thread.sleep(700);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Aguarde.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }).start();
    }


      public void fechar(){
        new Thread(new Runnable() {

            public void run() {
                int count = 0;
                while(true){
                    try {
                        count++;
                        jProgressBar1.setValue(100-count*10);
                        if (count == 1) {
                            jProgressBar1.setString("Aguarde Fechando...");
                        }
                        if (count == 5) {
                            jProgressBar1.setString("Salvando Configura��es");
                        }
                        if (count == 7) {
                            jProgressBar1.setString("Ta Um Pouco Demorado");
                        }
                        if (count == 8) {
                            jProgressBar1.setString("� culpa do Banco de Dados");
                        }
                        if (count == 10) {
                            jProgressBar1.setString("N�o Do Programa");
                        }
                        if (count == 15) {
                            jProgressBar1.setString("N�o Aperte Ctrl+Alt+Del");
                        }
                        if (count == 18) {
                            jProgressBar1.setString("Eu Consigo....");
                        }
                        if (count == 20) {
                            jProgressBar1.setString("Ta Quase...");
                        }
                        if (count == 20) {
                            jProgressBar1.setString("Ou N�o");
                        }
                        if (count == 30) {
                            jProgressBar1.setString("To Quase Desistindo");
                        }
                        if (count == 35) {
                            jProgressBar1.setString("Sou Brasileiro");
                        }
                        if (count == 33) {
                            jProgressBar1.setString("N�o Desisto Nunca");
                        }
                        if (count == 35) {
                            jProgressBar1.setString("T� Bom Pode Fecha.");
                        }
                        Thread.sleep(700);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Aguarde.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }).start();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel3 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jProgressBar1 = new javax.swing.JProgressBar();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Crepz Player");
        setMinimumSize(new java.awt.Dimension(310, 306));
        setResizable(false);
        setUndecorated(true);
        getContentPane().setLayout(new javax.swing.BoxLayout(getContentPane(), javax.swing.BoxLayout.LINE_AXIS));

        jPanel3.setBackground(new java.awt.Color(0, 0, 0));
        jPanel3.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jPanel3.setMinimumSize(new java.awt.Dimension(306, 306));
        jPanel3.setLayout(new java.awt.BorderLayout());

        jPanel2.setBackground(new java.awt.Color(0, 0, 0));
        jPanel2.setPreferredSize(new java.awt.Dimension(397, 50));

        jProgressBar1.setPreferredSize(new java.awt.Dimension(250, 30));
        jProgressBar1.setStringPainted(true);
        jPanel2.add(jProgressBar1);

        jPanel3.add(jPanel2, java.awt.BorderLayout.SOUTH);

        jPanel1.setBackground(new java.awt.Color(0, 0, 0));
        jPanel1.setLayout(new java.awt.BorderLayout());

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/img/homer x ray.jpg"))); // NOI18N
        jPanel1.add(jLabel1, java.awt.BorderLayout.CENTER);

        jPanel3.add(jPanel1, java.awt.BorderLayout.PAGE_START);

        getContentPane().add(jPanel3);

        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        setBounds((screenSize.width-302)/2, (screenSize.height-384)/2, 302, 384);
    }// </editor-fold>//GEN-END:initComponents

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                Aguarde g =new Aguarde();
                g.setVisible(true);
                g.standBy();
                g.intro();
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JProgressBar jProgressBar1;
    // End of variables declaration//GEN-END:variables



}
