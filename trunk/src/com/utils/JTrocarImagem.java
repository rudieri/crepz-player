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

import com.conexao.Transacao;
import com.musica.Musica;
import gsearch.Result;
import java.awt.Color;
import java.awt.Component;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.MalformedURLException;
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
public class JTrocarImagem extends javax.swing.JDialog {

    /** Creates new form JTrocarImagem */
    Musica musica = null;
    Thread th;

    public JTrocarImagem(java.awt.Frame parent, boolean modal, Musica musica) {
        super(parent, modal);
        initComponents();
        jLabel1.setText("Musica: " + musica.getNome());
        this.musica = musica;
        atualizarTabela();
    }

    private void atualizarTabela() {
        th = new Thread(new Runnable() {

            public void run() {
                atualizarTabela_Run();
            }
        });
        th.start();
    }

    private void atualizarTabela_Run() {
        try {
            jProgressBar.setVisible(true);
            jButton1.setVisible(false);
            jProgressBar.setString("Conectando...");
            List<Result> lista = BuscaGoogle.buscaImagens(musica.getAlbum() + " " + musica.getAutor());
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
                    BufferedImage bf = null;
                    try {
                        jProgressBar.setString("Baixando: " + lista.get(i).getTitle());
                        bf = ImageIO.read(new URL(lista.get(i).getUrl()));
                    } catch (Exception ex) {
                        Logger.getLogger(JTrocarImagem.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    row[0] = new javax.swing.ImageIcon(bf.getScaledInstance(150, 200, Image.SCALE_SMOOTH));
                    row[2] = lista.get(i).getUrl();
                }
                if (lista.get(i) != null) {
                    BufferedImage bf = null;
                    try {
                        jProgressBar.setString("Baixando: " + lista.get(i).getTitle());
                        bf = ImageIO.read(new URL(lista.get(i + 1).getUrl()));
                    } catch (Exception ex) {
                        Logger.getLogger(JTrocarImagem.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    row[1] = new javax.swing.ImageIcon(bf.getScaledInstance(150, 200, Image.SCALE_SMOOTH));
                    row[3] = lista.get(i).getUrl();
                }

                tm.addRow(row);
                jButton1.setVisible(true);
            }


        } catch (Exception ex) {
            jProgressBar.setVisible(false);
            jButton1.setVisible(true);
        }
    }

    class Imagem extends JLabel implements TableCellRenderer {

        public Imagem() {
            setOpaque(true);
        }

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
        Transacao t = new Transacao();
        try {
            String img = (String) jTable1.getModel().getValueAt(jTable1.getSelectedRow(), jTable1.getSelectedColumn() + 2);
            URL link = new URL(img);
            File musicaF = new File(musica.getCaminho());
            String dest = musica.getImg();
            if (dest == null || dest.equals("")) {
                dest = new File(musicaF.getAbsolutePath().replace(musicaF.getName(), musica.getAlbum() + "_" + musica.getAutor() + ".jpg")).getCanonicalPath();
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

            t.begin();
            Musica.mapearDiretorio(new File(musicaF.getAbsolutePath().replace(musicaF.getName(), "")), t, new JProgressBar(), 10);
            t.commit();
            setVisible(false);
            dispose();
        } catch (Exception ex) {
            t.rollback();
            Logger.getLogger(JTrocarImagem.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jPanel3 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jProgressBar = new javax.swing.JProgressBar();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanel1.setPreferredSize(new java.awt.Dimension(50, 30));
        jPanel1.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        jLabel1.setText("Musica:");
        jPanel1.add(jLabel1);

        getContentPane().add(jPanel1, java.awt.BorderLayout.PAGE_START);

        jPanel2.setLayout(new java.awt.BorderLayout());

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

        jPanel2.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        getContentPane().add(jPanel2, java.awt.BorderLayout.CENTER);

        jPanel3.setPreferredSize(new java.awt.Dimension(50, 35));
        jPanel3.setLayout(new javax.swing.BoxLayout(jPanel3, javax.swing.BoxLayout.Y_AXIS));

        jButton1.setText("Salvar");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jPanel4.add(jButton1);

        jProgressBar.setIndeterminate(true);
        jProgressBar.setString("Aguarde");
        jProgressBar.setStringPainted(true);
        jPanel4.add(jProgressBar);

        jPanel3.add(jPanel4);

        getContentPane().add(jPanel3, java.awt.BorderLayout.PAGE_END);

        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        setBounds((screenSize.width-358)/2, (screenSize.height-361)/2, 358, 361);
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        salvar();
    }//GEN-LAST:event_jButton1ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                Musica m = new Musica();
                m.setAlbum("Appetit for destruction");
                m.setAutor("Guns");
                JTrocarImagem dialog = new JTrocarImagem(new javax.swing.JFrame(), true, m);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {

                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JProgressBar jProgressBar;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    // End of variables declaration//GEN-END:variables
}