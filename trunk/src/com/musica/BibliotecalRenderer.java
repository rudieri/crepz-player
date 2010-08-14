

package com.musica;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;
			
/** Classe que define o renderer para JTable como sendo um JPanel. */
public class BibliotecalRenderer extends JPanel implements TableCellRenderer {
	
	// Atributos que definem as cores...
	private Color unselectedForeground = Color.WHITE;
	private Color unselectedBackground = Color.WHITE;
	 
	/** Método construtor. */
	public BibliotecalRenderer() {
		super();
		setOpaque(false);
		setLayout(new BorderLayout());
	}	

	/** Método sobreescrito de TableCellRenderer. */
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		if (isSelected) {
			super.setForeground(table.getSelectionForeground());
			super.setBackground(table.getSelectionBackground());
		} else {
			super.setForeground((unselectedForeground != null) ? unselectedForeground : table.getForeground());
			super.setBackground((unselectedBackground != null) ? unselectedBackground : table.getBackground());
		}

		if (hasFocus) {
			if (table.isCellEditable(row, column)) {
				super.setForeground( UIManager.getColor("Table.focusCellForeground") );
				super.setBackground( UIManager.getColor("Table.focusCellBackground") );
			}
		}

		super.setForeground( UIManager.getColor("Table.focusCellForeground") );
		super.setBackground( UIManager.getColor("Table.focusCellBackground") );
		
		removeAll();
		JCapa panel = (JCapa)value;
		if (panel != null) {
			panel.setBounds(table.getCellRect(0, 0, false));
                        panel.setSelecionado(isSelected);
			add(panel, BorderLayout.CENTER);			
		}
                
		return this;
	}
}
			