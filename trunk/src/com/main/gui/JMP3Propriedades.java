package com.main.gui;

import com.musica.Musica;
import com.musica.MusicaBD;
import com.musica.MusicaGerencia;
import com.utils.pele.ColorUtils;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import org.farng.mp3.MP3File;
import org.farng.mp3.TagConstant;
import org.farng.mp3.TagException;
import org.farng.mp3.TagOptionSingleton;
import org.farng.mp3.id3.ID3v1;

/*
 * JMP3Propriedades.java
 *
 * Created on 30/05/2010, 16:41:55
 */
/**
 *
 * @author manchini
 */
public class JMP3Propriedades extends javax.swing.JDialog {

    /**
     * Creates new form JMP3Propriedades
     */
    MP3File mp3File;
    private Musica musica;

    public JMP3Propriedades(java.awt.Frame parent, boolean modal, Musica musica) throws Exception {
        super(parent, modal);
        initComponents();
        this.musica = musica;
        montarGeneros();

        try {
            mp3File = new MP3File(musica.getCaminho());
            setDados();
        } catch (Exception ex) {
            throw new Exception("-Erro ao Carregar Propriedades do arquivo " + mp3File.getMp3file().getName() + " \n", ex);
        }
    }

    private void montarGeneros() {
        jComboBoxGenero.setModel(new DefaultComboBoxModel(com.musica.MusicaGerencia.generos));
    }

    private void setDados() {
        try {
            jTextField_Arquivo.setText(mp3File.getMp3file().getAbsolutePath());
            jTextField_Titulo.setText(mp3File.getID3v1Tag().getTitle());
            jTextField_Interp.setText(mp3File.getID3v1Tag().getArtist());
            jTextField_Album.setText(mp3File.getID3v1Tag().getAlbum());
            jComboBoxGenero.setSelectedIndex(mp3File.getID3v1Tag().getGenre());

            jTextField_ano.setText(mp3File.getID3v1Tag().getYear());
            jTextField_Comentario.setText(mp3File.getID3v1Tag().getComment());
        } catch (Exception ex) {
            setDadosv2();
        }

    }

    private void setDadosv2() {
        try {
            jTextField_Titulo.setText(mp3File.getID3v2Tag().getSongTitle());
            jTextField_Interp.setText(mp3File.getID3v2Tag().getLeadArtist());
            jTextField_Album.setText(mp3File.getID3v2Tag().getAlbumTitle());//(.getText());
            jComboBoxGenero.setSelectedItem(mp3File.getID3v2Tag().getSongGenre());//.setSongGenre((String) );
            jTextField_ano.setText(mp3File.getID3v2Tag().getYearReleased());//.getText());
            jTextField_Comentario.setText(mp3File.getID3v2Tag().getSongComment());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void setMp3File() {
        try {
            if (!mp3File.hasID3v1Tag()) {
                precisaCriar();
                return;
            }
            try {
//                TagOptionSingleton.getInstance().setDefaultSaveMode(TagConstant.MP3_FILE_SAVE_OVERWRITE);
                mp3File.getID3v1Tag().setTitle(jTextField_Titulo.getText());
                mp3File.getID3v1Tag().setArtist(jTextField_Interp.getText());
                mp3File.getID3v1Tag().setAlbum(jTextField_Album.getText());
                mp3File.getID3v1Tag().setGenre(Integer.valueOf(jComboBoxGenero.getSelectedIndex()).byteValue());
                mp3File.getID3v1Tag().setYear(jTextField_ano.getText());
                mp3File.getID3v1Tag().setComment(jTextField_Comentario.getText());
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            try {
                mp3File.getID3v2Tag().setSongTitle(jTextField_Titulo.getText());
                mp3File.getID3v2Tag().setLeadArtist(jTextField_Interp.getText());
                mp3File.getID3v2Tag().setAlbumTitle(jTextField_Album.getText());
                mp3File.getID3v2Tag().setSongGenre((String) jComboBoxGenero.getSelectedItem());
                mp3File.getID3v2Tag().setYearReleased(jTextField_ano.getText());
                mp3File.getID3v2Tag().setSongComment(jTextField_Comentario.getText());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            TagOptionSingleton.getInstance().setFilenameTagSave(true);
            mp3File.save(TagConstant.MP3_FILE_SAVE_OVERWRITE);
            alterarMusica();


        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao Salvar Propriedades.");
            ex.printStackTrace();
        }
    }

    private void precisaCriar() {
        //  if (mp3File.hasID3v1Tag()) {
        ID3v1 id = new ID3v1();
        id.setSongTitle(jTextField_Titulo.getText());
        id.setAlbumTitle(jTextField_Album.getText());
        id.setLeadArtist(jTextField_Interp.getText());
        // id.setSongGenre((String)(jComboBoxGenero.getSelectedItem()));
        id.setYearReleased(jTextField_ano.getText());
        id.setSongComment(jTextField_Comentario.getText());
        mp3File.setID3v1Tag(id);
        alterarMusica();
        try {
            mp3File.save(TagConstant.MP3_FILE_SAVE_APPEND);
            // }
        } catch (IOException ex) {
            Logger.getLogger(JMP3Propriedades.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TagException ex) {
            Logger.getLogger(JMP3Propriedades.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void alterarMusica() {
        try {
            MusicaGerencia.getMusica(musica, mp3File, new File(mp3File.getMp3file().getAbsolutePath().replace(mp3File.getMp3file().getName(), "")));
            MusicaBD.alterar(musica);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao Salvar Propriedades.");
            ex.printStackTrace();
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
        jLabel1 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jPanel13 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jTextField_Arquivo = new javax.swing.JTextField();
        jPanel7 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jTextField_Titulo = new javax.swing.JTextField();
        jPanel9 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jTextField_Interp = new javax.swing.JTextField();
        jPanel10 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jTextField_Album = new javax.swing.JTextField();
        jPanel11 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        jComboBoxGenero = new javax.swing.JComboBox();
        jPanel12 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        jTextField_ano = new javax.swing.JTextField();
        jPanel14 = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        jTextField_Comentario = new javax.swing.JTextField();
        jPanel5 = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanel3.setPreferredSize(new java.awt.Dimension(400, 30));

        jLabel1.setText("Editor de Propriedades");
        jPanel3.add(jLabel1);

        getContentPane().add(jPanel3, java.awt.BorderLayout.PAGE_START);

        jPanel1.setPreferredSize(new java.awt.Dimension(400, 250));
        jPanel1.setLayout(new javax.swing.BoxLayout(jPanel1, javax.swing.BoxLayout.Y_AXIS));

        jScrollPane1.setHorizontalScrollBar(null);
        jScrollPane1.setPreferredSize(new java.awt.Dimension(350, 212));

        jPanel13.setLayout(new javax.swing.BoxLayout(jPanel13, javax.swing.BoxLayout.Y_AXIS));

        jPanel4.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        jLabel2.setText("Arquivo:");
        jLabel2.setPreferredSize(new java.awt.Dimension(70, 14));
        jPanel4.add(jLabel2);

        jTextField_Arquivo.setEditable(false);
        jTextField_Arquivo.setPreferredSize(new java.awt.Dimension(300, 25));
        jPanel4.add(jTextField_Arquivo);

        jPanel13.add(jPanel4);

        jPanel7.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        jLabel3.setText("Titulo:");
        jLabel3.setPreferredSize(new java.awt.Dimension(70, 14));
        jPanel7.add(jLabel3);

        jTextField_Titulo.setPreferredSize(new java.awt.Dimension(300, 25));
        jPanel7.add(jTextField_Titulo);

        jPanel13.add(jPanel7);

        jPanel9.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        jLabel5.setText("Interprete:");
        jLabel5.setPreferredSize(new java.awt.Dimension(70, 14));
        jPanel9.add(jLabel5);

        jTextField_Interp.setPreferredSize(new java.awt.Dimension(300, 25));
        jPanel9.add(jTextField_Interp);

        jPanel13.add(jPanel9);

        jPanel10.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        jLabel6.setText("Album:");
        jLabel6.setPreferredSize(new java.awt.Dimension(70, 14));
        jPanel10.add(jLabel6);

        jTextField_Album.setPreferredSize(new java.awt.Dimension(300, 25));
        jPanel10.add(jTextField_Album);

        jPanel13.add(jPanel10);

        jPanel11.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        jLabel7.setText("Genero:");
        jLabel7.setPreferredSize(new java.awt.Dimension(70, 14));
        jPanel11.add(jLabel7);

        jComboBoxGenero.setPreferredSize(new java.awt.Dimension(150, 25));
        jPanel11.add(jComboBoxGenero);

        jPanel13.add(jPanel11);

        jPanel12.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        jLabel8.setText("Ano:");
        jLabel8.setPreferredSize(new java.awt.Dimension(70, 14));
        jPanel12.add(jLabel8);

        jTextField_ano.setPreferredSize(new java.awt.Dimension(300, 25));
        jPanel12.add(jTextField_ano);

        jPanel13.add(jPanel12);

        jPanel14.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        jLabel9.setText("Comentario:");
        jLabel9.setPreferredSize(new java.awt.Dimension(70, 14));
        jPanel14.add(jLabel9);

        jTextField_Comentario.setPreferredSize(new java.awt.Dimension(300, 25));
        jPanel14.add(jTextField_Comentario);

        jPanel13.add(jPanel14);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 452, Short.MAX_VALUE)
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        jPanel13.add(jPanel5);

        jPanel6.setLayout(new java.awt.BorderLayout());
        jPanel13.add(jPanel6);

        jScrollPane1.setViewportView(jPanel13);

        jPanel1.add(jScrollPane1);

        getContentPane().add(jPanel1, java.awt.BorderLayout.CENTER);

        jPanel2.setPreferredSize(new java.awt.Dimension(400, 50));

        jButton1.setText("Salvar");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jPanel2.add(jButton1);

        jButton2.setText("Fechar");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        jPanel2.add(jButton2);

        getContentPane().add(jPanel2, java.awt.BorderLayout.PAGE_END);

        pack();
        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        java.awt.Dimension dialogSize = getSize();
        setLocation((screenSize.width-dialogSize.width)/2,(screenSize.height-dialogSize.height)/2);
    }// </editor-fold>//GEN-END:initComponents

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        this.setVisible(false);
        this.dispose();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        setMp3File();
    }//GEN-LAST:event_jButton1ActionPerformed
  
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JComboBox jComboBoxGenero;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField jTextField_Album;
    private javax.swing.JTextField jTextField_Arquivo;
    private javax.swing.JTextField jTextField_Comentario;
    private javax.swing.JTextField jTextField_Interp;
    private javax.swing.JTextField jTextField_Titulo;
    private javax.swing.JTextField jTextField_ano;
    // End of variables declaration//GEN-END:variables
}
