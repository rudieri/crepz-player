package com.musica;

import com.biblioteca.Capa;
import com.utils.pele.ColorUtils;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

public class JCapa extends javax.swing.JPanel implements Cloneable {

    private static HashMap<String, ImageIcon> swap = new HashMap<String, ImageIcon>(50);

    public static void reset() {
        swap.clear();
    }

    public static ImageIcon getCapa(String endereco) {
        ImageIcon imageIcon = swap.get(endereco);
        if (imageIcon == null) {
            try {
                BufferedImage bf;
                bf = ImageIO.read(new File(endereco));
                if (bf != null) {
                    imageIcon = new javax.swing.ImageIcon(bf.getScaledInstance(80, 120, Image.SCALE_SMOOTH));
                    swap.put(endereco, imageIcon);
                }
            } catch (IOException ex) {
                ex.printStackTrace(System.err);
            }
        }
        return imageIcon;
    }
    
    
    public JCapa() {
        initComponents();
    }

    public void setCapa(final Capa capa) {
        jLabel_Qts.setText("Musicas :" + capa.getQtd());
        jLabel_Titulo1.setText(capa.getTitulo());
        if (capa.getImg() != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    synchronized (JCapa.this) {
                        ImageIcon imgIcon = JCapa.getCapa(capa.getImg());
                        jLabel_Img.setIcon(imgIcon);
                        JCapa.this.notifyAll();
                    }
                }
            }).start();
        }
        synchronized (this) {
            try {
                this.wait();
            } catch (InterruptedException ex) {
                Logger.getLogger(JCapa.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public String getTXT() {
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

        jPanel3 = new javax.swing.JPanel();
        jLabel_Img = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jLabel_Titulo1 = new javax.swing.JLabel();
        jLabel_Qts = new javax.swing.JLabel();

        setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        setForeground(javax.swing.UIManager.getDefaults().getColor("Panel.foreground"));
        setMaximumSize(new java.awt.Dimension(112, 91));
        setMinimumSize(new java.awt.Dimension(112, 91));
        setPreferredSize(new java.awt.Dimension(112, 91));
        setLayout(new java.awt.BorderLayout());

        jPanel3.setMaximumSize(new java.awt.Dimension(80, 80));
        jPanel3.setMinimumSize(new java.awt.Dimension(80, 80));
        jPanel3.setOpaque(false);
        jPanel3.setPreferredSize(new java.awt.Dimension(80, 80));
        jPanel3.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 0, 0));

        jLabel_Img.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/img/icon.png"))); // NOI18N
        jLabel_Img.setMinimumSize(new java.awt.Dimension(100, 150));
        jLabel_Img.setPreferredSize(new java.awt.Dimension(80, 120));
        jPanel3.add(jLabel_Img);

        add(jPanel3, java.awt.BorderLayout.WEST);

        jPanel4.setOpaque(false);
        jPanel4.setLayout(new java.awt.GridLayout(2, 1, 5, 5));

        jLabel_Titulo1.setFont(new java.awt.Font("Comic Sans MS", 1, 11)); // NOI18N
        jLabel_Titulo1.setText("Titulo");
        jPanel4.add(jLabel_Titulo1);

        jLabel_Qts.setFont(new java.awt.Font("Comic Sans MS", 1, 11)); // NOI18N
        jLabel_Qts.setText("Titulo");
        jPanel4.add(jLabel_Qts);

        add(jPanel4, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel_Img;
    private javax.swing.JLabel jLabel_Qts;
    private javax.swing.JLabel jLabel_Titulo1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    // End of variables declaration//GEN-END:variables
}
