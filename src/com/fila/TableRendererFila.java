/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fila;

import com.musica.Musica;
import com.utils.pele.ColorUtils;
import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/**
 *
 * @author rudieri
 */
public class TableRendererFila implements TableCellRenderer {

    private MusicaComponent mc = new MusicaComponent();
    private JLabel label = new JLabel();

    public TableRendererFila() {
        mc.setOpaque(true);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {


        if (value.getClass() == Musica.class) {
            mc.setMusica((Musica) value);
            mc.setSelecionado(isSelected);
            return mc;
        } else {
            label.setText(value.toString());
            if (isSelected) {
                label.setForeground(ColorUtils.getFrenteTabelaSelecionada());
                label.setBackground(ColorUtils.getFundoTabelaSelecionada());
            } else {
                label.setForeground(ColorUtils.getFrenteTabelaNaoSelecionada());
                label.setBackground(ColorUtils.getFundoTabelaNaoSelecionada());
            }
            return label;
        }

    }
}
