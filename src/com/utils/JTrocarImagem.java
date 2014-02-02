/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * JTrocarImagem.java
 *
 * Created on 22/08/2010, 19:14:02
 */
package com.utils;

import com.musica.MusicaGerencia;
import com.musica.MusicaS;
import gsearch.Result;
import java.awt.Color;
import java.awt.Component;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

/**
 *
 * @author manchini
 */
public class JTrocarImagem extends javax.swing.JDialog implements Runnable, ActionListener {

    /**
     * Creates new form JTrocarImagem
     */
    private final MusicaS musica;
    private Thread th;

    public JTrocarImagem(java.awt.Frame parent, boolean modal, MusicaS musica) {
        super(parent, modal);
        initComponents();
        jLabel_Musica.setText("Musica: " + musica.getNome());
        this.musica = musica;
        atualizarTabela();
        startEvents();
    }

    private void atualizarTabela() {
        th = new Thread(this);
        th.start();
    }

    @Override
    public void run() {
        atualizarTabela_Run();
    }

    private void atualizarTabela_Run() {
        try {
            jProgressBar.setVisible(true);
            jButton_Salvar.setVisible(false);
            jProgressBar.setString("Conectando...");
            List<Result> lista = BuscaGoogle.buscaImagens(((musica.getAlbum() == null ? "" : musica.getAlbum().getNome())
                    + " " + (musica.getAlbum() == null ? "" : musica.getAlbum().getAutor() == null ? "" : musica.getAlbum().getAutor().getNome())
                    + "" + musica.getNome() == null ? "" : musica.getNome()).replaceAll("  ", " ").trim());
            jProgressBar.setString("Montando Tabela");
            DefaultTableModel tm = (DefaultTableModel) jTable1.getModel();
            TableCellRenderer tcr = new Imagem();
            TableColumn column = jTable1.getColumnModel().getColumn(0);
            column.setCellRenderer(tcr);
            column = jTable1.getColumnModel().getColumn(1);
            column.setCellRenderer(tcr);

            jTable1.removeColumn(jTable1.getColumn("txtImg1"));
            jTable1.removeColumn(jTable1.getColumn("txtImg2"));

            for (int i = 0; i < lista.size(); i += 2) {
                Object[] row = new Object[4];

                if (lista.get(i) != null) {
                    BufferedImage bf;
                    try {
                        jProgressBar.setString("Baixando: " + lista.get(i).getTitle());
                        bf = ImageIO.read(new URL(lista.get(i).getUrl()));
                        row[0] = new javax.swing.ImageIcon(bf.getScaledInstance(150, 200, Image.SCALE_SMOOTH));
                        row[2] = lista.get(i).getUrl();
                    } catch (Exception ex) {
                        Logger.getLogger(JTrocarImagem.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                if (lista.get(i) != null) {
                    BufferedImage bf;
                    try {
                        jProgressBar.setString("Baixando: " + lista.get(i).getTitle());
                        bf = ImageIO.read(new URL(lista.get(i + 1).getUrl()));
                        row[1] = new javax.swing.ImageIcon(bf.getScaledInstance(150, 200, Image.SCALE_SMOOTH));
                        row[3] = lista.get(i).getUrl();
                    } catch (Exception ex) {
                        Logger.getLogger(JTrocarImagem.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

                tm.addRow(row);
                jButton_Salvar.setVisible(true);
            }


        } catch (Exception ex) {
            jProgressBar.setVisible(false);
            jButton_Salvar.setVisible(true);
        }
    }

    private class Imagem extends JLabel implements TableCellRenderer {

        public Imagem() {
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table,
                Object value, boolean isSelected, boolean hasFocus, int row,
                int column) {

            if (value instanceof ImageIcon) {
                // certifique-se da existencia da imagem "icon.gif" antes de executar

                if (isSelected) {
                    setBackground(Color.BLACK);
                } else {
                    setBackground(Color.WHITE);
                }

                setIcon((Icon) value);
                setHorizontalAlignment(0);
            }

            return this;
        }
    }

    private void salvar() {
        th.interrupt();
//        Transacao t = new Transacao();
        try {
            String img = (String) jTable1.getModel().getValueAt(jTable1.getSelectedRow(), jTable1.getSelectedColumn() + 2);
            URL link = new URL(img);
            File musicaF = new File(musica.getCaminho());
            String dest = musica.getAlbum().getImg();
            if (dest == null) {
                dest = new File(musicaF.getAbsolutePath().replace(musicaF.getName(), musica.getAlbum().getNome() + "_" + musica.getAlbum().getAutor().getNome() + ".jpg")).getCanonicalPath();
            }
            File destino = new File(dest);
            InputStream in = link.openStream();
            FileOutputStream out = new FileOutputStream(destino);
            byte[] buf = new byte[4 * 1024]; // 4K buffer
            int bytesRead;
            while ((bytesRead = in.read(buf)) != -1) {
                out.write(buf, 0, bytesRead);
            }
            out.flush();
            out.close();

//            t.begin();
            MusicaGerencia.mapearDiretorio(new File(musicaF.getAbsolutePath().replace(musicaF.getName(), "")), new JProgressBar(), 10);
//            t.commit();
            setVisible(false);
            dispose();
        } catch (Exception ex) {
//            t.rollback();
            Logger.getLogger(JTrocarImagem.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void startEvents(){
        jButton_Salvar.addActionListener(this);
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        salvar();
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
        jLabel_Musica = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jPanel3 = new javax.swing.JPanel();
        jButton_Salvar = new javax.swing.JButton();
        jProgressBar = new javax.swing.JProgressBar();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanel1.setPreferredSize(new java.awt.Dimension(50, 30));
        jPanel1.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        jLabel_Musica.setText("Musica:");
        jPanel1.add(jLabel_Musica);

        getContentPane().add(jPanel1, java.awt.BorderLayout.PAGE_START);

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Img", "Img2", "txtImg1", "txtImg2"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Object.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable1.setCellSelectionEnabled(true);
        jTable1.setRowHeight(200);
        jTable1.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane1.setViewportView(jTable1);

        getContentPane().add(jScrollPane1, java.awt.BorderLayout.CENTER);

        jPanel3.setPreferredSize(new java.awt.Dimension(50, 35));
        jPanel3.setLayout(new javax.swing.BoxLayout(jPanel3, javax.swing.BoxLayout.LINE_AXIS));

        jButton_Salvar.setText("Salvar");
        jPanel3.add(jButton_Salvar);

        jProgressBar.setIndeterminate(true);
        jProgressBar.setString("Aguarde");
        jProgressBar.setStringPainted(true);
        jPanel3.add(jProgressBar);

        getContentPane().add(jPanel3, java.awt.BorderLayout.PAGE_END);

        setSize(new java.awt.Dimension(358, 361));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {

//        Musica m = Cache.getMusica(-1);// Nenhuma música, apenas para pegar uma nova instância
//        m.setAlbum("Appetit for destruction");
//        m.setAutor("Guns");
//        JTrocarImagem dialog = new JTrocarImagem(new javax.swing.JFrame(), true, m);
//        dialog.setVisible(true);
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton_Salvar;
    private javax.swing.JLabel jLabel_Musica;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JProgressBar jProgressBar;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    // End of variables declaration//GEN-END:variables
}
