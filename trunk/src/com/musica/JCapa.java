package com.musica;

import com.biblioteca.Capa;
import com.utils.SwapCapa;
import com.utils.pele.ColorUtils;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

public class JCapa extends javax.swing.JPanel implements Cloneable{

    public JCapa() {
        initComponents();
    }

    public void setCapa(Capa capa) {
        jLabel_Qts.setText("Musicas :" + capa.getQtd());
        jLabel_Titulo1.setText(capa.getTitulo());
        try {
            if (capa.getImg() != null) {
                ImageIcon imgIcon = null;
                if (SwapCapa.swap.get(capa.getImg()) != null) {
                    imgIcon = SwapCapa.swap.get(capa.getImg());
                } else {
                    BufferedImage bf;
                    bf = ImageIO.read(new File(capa.getImg()));
                    if (bf != null) {
                        imgIcon = new javax.swing.ImageIcon(bf.getScaledInstance(80, 120, Image.SCALE_SMOOTH));
                        SwapCapa.swap.put(capa.getImg(), imgIcon);
                    }
                }

                jLabel_Img.setIcon(imgIcon);
            }
        } catch (IOException ex) {
            Logger.getLogger(JCapa.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String getTXT(){
        return jLabel_Titulo1.getText();
    }

    public void setSelecionado(boolean sel) {
        if (sel) {
            jLabel_Titulo1.setForeground(ColorUtils.getFrenteTabelaSelecionada());
            jLabel_Qts.setForeground(ColorUtils.getFrenteTabelaSelecionada());
        } else {
            jLabel_Titulo1.setForeground(ColorUtils.getFrenteTabelaNaoSelecionada());
            jLabel_Qts.setForeground(ColorUtils.getFrenteTabelaNaoSelecionada());
        }
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jLabel_Img = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jLabel_Titulo1 = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        jLabel_Qts = new javax.swing.JLabel();

        setBackground(new java.awt.Color(255, 255, 255));
        setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        setMaximumSize(new java.awt.Dimension(112, 91));
        setMinimumSize(new java.awt.Dimension(112, 91));
        setPreferredSize(new java.awt.Dimension(112, 91));
        setLayout(new java.awt.BorderLayout());

        jPanel2.setOpaque(false);
        jPanel2.setLayout(new java.awt.BorderLayout());

        jPanel3.setMaximumSize(new java.awt.Dimension(80, 80));
        jPanel3.setMinimumSize(new java.awt.Dimension(80, 80));
        jPanel3.setOpaque(false);
        jPanel3.setPreferredSize(new java.awt.Dimension(80, 80));
        jPanel3.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 0, 0));

        jLabel_Img.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/img/icon.png"))); // NOI18N
        jLabel_Img.setMinimumSize(new java.awt.Dimension(100, 150));
        jLabel_Img.setPreferredSize(new java.awt.Dimension(80, 120));
        jPanel3.add(jLabel_Img);

        jPanel2.add(jPanel3, java.awt.BorderLayout.WEST);

        jPanel4.setOpaque(false);
        jPanel4.setLayout(new java.awt.GridLayout(3, 1, 5, 5));

        jPanel5.setOpaque(false);
        jPanel5.setLayout(new java.awt.BorderLayout());

        jLabel_Titulo1.setFont(new java.awt.Font("Comic Sans MS", 1, 11)); // NOI18N
        jLabel_Titulo1.setText("Titulo");
        jLabel_Titulo1.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        jPanel5.add(jLabel_Titulo1, java.awt.BorderLayout.CENTER);

        jPanel6.setOpaque(false);
        jPanel5.add(jPanel6, java.awt.BorderLayout.WEST);

        jPanel4.add(jPanel5);

        jPanel7.setOpaque(false);
        jPanel7.setLayout(new java.awt.BorderLayout());

        jPanel8.setOpaque(false);
        jPanel7.add(jPanel8, java.awt.BorderLayout.WEST);

        jLabel_Qts.setFont(new java.awt.Font("Comic Sans MS", 1, 11)); // NOI18N
        jLabel_Qts.setText("Titulo");
        jLabel_Qts.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        jPanel7.add(jLabel_Qts, java.awt.BorderLayout.CENTER);

        jPanel4.add(jPanel7);

        jPanel2.add(jPanel4, java.awt.BorderLayout.CENTER);

        add(jPanel2, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel_Img;
    private javax.swing.JLabel jLabel_Qts;
    private javax.swing.JLabel jLabel_Titulo1;
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
