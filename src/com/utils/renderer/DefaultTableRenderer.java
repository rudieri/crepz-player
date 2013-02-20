/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.utils.renderer;

import com.utils.pele.ColorUtils;
import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/**
 *
 * @author rudieri
 */
public class DefaultTableRenderer implements TableCellRenderer {

    private JLabel label;

    public DefaultTableRenderer() {
        label = new JLabel();
        label.setOpaque(true);
    }
    

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        if (value == null) {
            value = "";
        }
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
