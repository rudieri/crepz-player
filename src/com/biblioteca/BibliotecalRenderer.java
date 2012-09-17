package com.biblioteca;

import com.musica.JCapa;
import com.utils.pele.ColorUtils;
import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

public class BibliotecalRenderer implements TableCellRenderer {

    // Atributos que definem as cores...
   
    private JCapa jCapa = new JCapa();

    /**
     * M�todo construtor.
     */
    public BibliotecalRenderer() {
        super();
        jCapa.setOpaque(true);
        jCapa.setVisible(true);
//        jCapa.setLayout(new BorderLayout());
    }

    /**
     * M�todo sobreescrito de TableCellRenderer.
     */
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        if (isSelected) {
            jCapa.setForeground(ColorUtils.getFrenteTabelaSelecionada());
            jCapa.setBackground(ColorUtils.getFundoTabelaSelecionada());
        } else {
            jCapa.setForeground(ColorUtils.getFrenteTabelaNaoSelecionada());
            jCapa.setBackground(ColorUtils.getFundoTabelaNaoSelecionada());
        }


        Capa capa = (Capa) value;
        if (capa != null) {
            jCapa.setBounds(table.getCellRect(0, 0, false));
            jCapa.setSelecionado(isSelected);
            jCapa.setCapa(capa);
        }

        return jCapa;
    }
}
