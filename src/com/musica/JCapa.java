
package com.musica;

import com.utils.SwapCapa;
import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;


/**
 * Classe que representa o Bico no PDV.
 */
public class JCapa extends javax.swing.JPanel {


    /** Método construtor. */
      /** Creates new form JCapa */
    private String img;
    private  String titulo;
    private int qtd;
    public JCapa(String img, String titulo,Integer qtd) {
        try {
            initComponents();
            this.img = img;
            this.titulo = titulo;
            this.qtd = qtd;
            if(!(img == null)){
                ImageIcon imgIcon = null;
                if(SwapCapa.swap.get(img)!=null){
                    imgIcon = SwapCapa.swap.get(img);
                }else{
                    BufferedImage bf = ImageIO.read(new File(img));
                    if (bf!=null) {
                        imgIcon = new javax.swing.ImageIcon(bf.getScaledInstance(80, 120, Image.SCALE_SMOOTH));
                        SwapCapa.swap.put(img, imgIcon);
                    }
                }

                jLabel_Img.setIcon(imgIcon);
            }
            jLabel_Titulo1.setText(titulo);
            jLabel_Qts.setText("Musicas :" + qtd.toString());
            this.setFocusable(false);
            //        this.setVisible(true);
        }
        catch (Exception io){
            System.out.println("Erro em :"+img);
            io.printStackTrace();
        }

    }

    public String getImg(){
        return img;
    }
    public String getTXT(){
        return titulo;
    }


    public static void main(String[] args){
        new JCapa("","", WIDTH).setVisible(true);
    }

    public void setSelecionado(boolean sel){
        if(sel){
            jLabel_Titulo1.setForeground(Color.red);
        }else{
            jLabel_Titulo1.setForeground(Color.BLACK);
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
        jLabel_Qts = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();

        setBackground(new java.awt.Color(255, 255, 255));
        setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        setMaximumSize(new java.awt.Dimension(112, 91));
        setMinimumSize(new java.awt.Dimension(112, 91));
        setPreferredSize(new java.awt.Dimension(112, 91));
        setLayout(new java.awt.BorderLayout());

        jPanel2.setLayout(new javax.swing.BoxLayout(jPanel2, javax.swing.BoxLayout.LINE_AXIS));

        jPanel3.setMaximumSize(new java.awt.Dimension(80, 80));
        jPanel3.setMinimumSize(new java.awt.Dimension(80, 80));
        jPanel3.setPreferredSize(new java.awt.Dimension(80, 80));
        jPanel3.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 0, 0));

        jLabel_Img.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/img/icon.png"))); // NOI18N
        jLabel_Img.setMinimumSize(new java.awt.Dimension(100, 150));
        jLabel_Img.setPreferredSize(new java.awt.Dimension(80, 120));
        jPanel3.add(jLabel_Img);

        jPanel2.add(jPanel3);

        jPanel4.setLayout(new javax.swing.BoxLayout(jPanel4, javax.swing.BoxLayout.Y_AXIS));

        jPanel5.setMinimumSize(new java.awt.Dimension(130, 27));
        jPanel5.setPreferredSize(new java.awt.Dimension(100, 40));

        jLabel_Titulo1.setFont(new java.awt.Font("Comic Sans MS", 1, 11));
        jLabel_Titulo1.setText("Titulo");
        jLabel_Titulo1.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        jLabel_Titulo1.setMinimumSize(new java.awt.Dimension(120, 17));
        jLabel_Titulo1.setPreferredSize(new java.awt.Dimension(120, 20));
        jPanel5.add(jLabel_Titulo1);

        jPanel4.add(jPanel5);

        jLabel_Qts.setFont(new java.awt.Font("Comic Sans MS", 1, 11));
        jLabel_Qts.setText("Titulo");
        jLabel_Qts.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        jLabel_Qts.setPreferredSize(new java.awt.Dimension(120, 20));
        jPanel6.add(jLabel_Qts);

        jPanel4.add(jPanel6);

        jPanel7.setLayout(new java.awt.BorderLayout());
        jPanel4.add(jPanel7);

        jPanel2.add(jPanel4);

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
    // End of variables declaration//GEN-END:variables
}
