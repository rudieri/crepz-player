package com.playlist;

import com.musica.Musica;
import java.awt.Color;
import java.awt.Component;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.TableCellRenderer;

/**
 * Classe que define o renderer para JTable como sendo um JPanel.
 */
public class PlayListRenderer implements TableCellRenderer {

    private Color unselectedForeground = Color.WHITE;
    private Color unselectedBackground = Color.BLACK;
    private JLista jLista;
    
    /**
     * Método construtor.
     */
    public PlayListRenderer() {
        jLista = new JLista(null, null);
    }

    /**
     * Método sobreescrito de TableCellRenderer.
     */
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        
        if (value instanceof Musica) {
            jLista.setMusica((Musica)value);
        }
        if (isSelected) {
            jLista.setOpaque(false);
            jLista.setForeground(table.getSelectionForeground());
            jLista.setBackground(table.getSelectionBackground());
        } else {
            jLista.setOpaque(true);
            jLista.setForeground((unselectedForeground != null) ? unselectedForeground : table.getForeground());
            jLista.setBackground((unselectedBackground != null) ? unselectedBackground : table.getBackground());
        }

        if (hasFocus) {
            if (table.isCellEditable(row, column)) {
                jLista.setForeground(UIManager.getColor("Table.focusCellForeground"));
                jLista.setBackground(UIManager.getColor("Table.focusCellBackground"));
            }
        }
        return jLista;
    }
}
