

package com.playlist;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;
			
/** Classe que define o renderer para JTable como sendo um JPanel. */
public class PlayListRenderer extends JPanel implements TableCellRenderer {
	
	// Atributos que definem as cores...
//	private Color unselectedForeground = new JLista("", "").getForeground();
//	private Color unselectedBackground = new JLista("", "").getBackground();
    private Color unselectedForeground = Color.WHITE;
	private Color unselectedBackground = Color.BLACK;
	 
	/** Método construtor. */
	public PlayListRenderer() {
		super();
		setOpaque(false);
		setLayout(new BorderLayout());
	}	

	/** Método sobreescrito de TableCellRenderer. */
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		removeAll();
                JLista panel=null;
                try{
		panel = (JLista)value;
                }
                catch(Exception e){
                    e.printStackTrace();
                }
		if (panel != null) {
			panel.setBounds(table.getCellRect(0, 0, false));
			add(panel, BorderLayout.CENTER);			
		}
                if (isSelected) {
                        panel.setOpaque(false);
			super.setForeground(table.getSelectionForeground());
			super.setBackground(table.getSelectionBackground());
		} else {
                        panel.setOpaque(true);
			super.setForeground((unselectedForeground != null) ? unselectedForeground : table.getForeground());
			super.setBackground((unselectedBackground != null) ? unselectedBackground : table.getBackground());
		}

		if (hasFocus) {
			if (table.isCellEditable(row, column)) {
				super.setForeground( UIManager.getColor("Table.focusCellForeground") );
				super.setBackground( UIManager.getColor("Table.focusCellBackground") );
			}
		}
		return this;
	}
}
			