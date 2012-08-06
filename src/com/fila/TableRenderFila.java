/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fila;

import com.musica.Musica;
import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/**
 *
 * @author rudieri
 */
public class TableRenderFila implements  TableCellRenderer{
    private MusicaComponent mc = new MusicaComponent();
    private JLabel label = new JLabel();
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        if (value instanceof Musica) {
             mc.setMusica((Musica) value);
            return mc;
        }else{
            label.setText(value.toString());
            return label;
        }

    }

}
