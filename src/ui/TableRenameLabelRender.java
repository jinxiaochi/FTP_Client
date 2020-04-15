package ui;

import java.awt.Component;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/**
 * @author jinxiaochi
 * @date 2020��4��8��
 * @effect �ļ�(��)������labelͼ��;
 */
public class TableRenameLabelRender extends JLabel implements TableCellRenderer {
	private static final long serialVersionUID = 1L;
	public static final String writeFile = "rename.png";
	public static final String readOnlyFile = "unrename.png";
	
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column) {
//		JLabel renameLabel = new JLabel(new ImageIcon("lib/"+value), JLabel.CENTER);
		URL url = this.getClass().getResource("/resource/"+value);
		JLabel renameLabel = new JLabel(new ImageIcon(url), JLabel.CENTER);
		renameLabel.setOpaque(true);// ��������Ϊ��͸��������ʾ����ɫ
		if (isSelected) {
			renameLabel.setBackground(table.getSelectionBackground());
		} else {
			renameLabel.setBackground(table.getBackground());
		}
		return renameLabel;
	}


}
