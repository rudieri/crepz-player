/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fila;

import com.musica.Musica;
import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

/**
 *
 * @author rudieri
 */
public class TableRenderFila implements  TableCellRenderer{
    private MusicaComponent mc;
    private JLabel label = new JLabel();
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        if (value instanceof Musica) {
             mc  = new MusicaComponent((Musica)value);
            return mc;
        }else{
            label.setText(value.toString());
            return label;
        }

    }

}
