package com.playlist;

import com.musica.MusicaS;
import com.utils.pele.ColorUtils;
import java.awt.Color;
import java.awt.Component;
import javax.swing.JTable;
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
        jLista = new JLista();
        jLista.setOpaque(true);
    }

    /**
     * Método sobreescrito de TableCellRenderer.
     */
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

        if (value.getClass() == MusicaS.class) {
            jLista.setMusica((MusicaS) value);
        }
        //Aqui
//        if (ColorUtils.getPeleAtual() == Pele.PELE_PADRAO) {
//            if (isSelected) {
//                jLista.setBackground(Color.BLACK);
//                jLista.setForeground( new Color(255, 51, 0, 255));
//            } else {
//                jLista.setForeground(Color.BLACK);
//                jLista.setBackground( new Color(255, 106, 0, 255));
////                jLista.setBackground(Color.WHITE);
//            }
//        } else {
            if (isSelected) {
                jLista.setForeground(ColorUtils.getFrenteTabelaSelecionada());
                jLista.setBackground(ColorUtils.getFundoTabelaSelecionada());
            } else {
                jLista.setForeground(ColorUtils.getFrenteTabelaNaoSelecionada());
                jLista.setBackground(ColorUtils.getFundoTabelaNaoSelecionada());
            }
//        }
        return jLista;
    }
}
