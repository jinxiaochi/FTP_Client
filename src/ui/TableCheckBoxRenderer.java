package ui;

import java.awt.Component;

import javax.swing.JCheckBox;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/**
 * @author jinxiaochi
 * @date 2020年4月8日
 * @effect 渲染列表中的复选框
 */
public class TableCheckBoxRenderer extends JCheckBox implements TableCellRenderer {
	private static final long serialVersionUID = 1L;

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column) {
		if (value instanceof Boolean) {
			Boolean b = (Boolean) value;
			this.setSelected(b.booleanValue());
			this.setOpaque(true);
			if (isSelected) {
				this.setBackground(table.getSelectionBackground());
			} else {
				this.setBackground(table.getBackground());
			}
			
		}
		return this;
	}

}
