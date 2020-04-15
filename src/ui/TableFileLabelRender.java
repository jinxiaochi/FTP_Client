package ui;

import java.awt.Component;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/**
 * @author jinxiaochi
 * @date 2020年4月8日
 * @effect 文件和文件夹的图标显示;  
 */
public class TableFileLabelRender extends JLabel implements TableCellRenderer {
	private static final long serialVersionUID = 1L;
	public static final String Directory = "directory";
	public static final String File = "file";

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column) {
		JLabel labelImg = null;
		if(isSelected) {
//			labelImg = new JLabel(new ImageIcon("lib/" + value + "2.png"), JLabel.CENTER);
			URL url = this.getClass().getResource("/resource/"+value+"2.png");
			labelImg = new JLabel(new ImageIcon(url), JLabel.CENTER);
			labelImg.setOpaque(true);
			labelImg.setBackground(table.getSelectionBackground());
		}else {
//			labelImg = new JLabel(new ImageIcon("lib/" + value + ".png"), JLabel.CENTER);
			URL url = this.getClass().getResource("/resource/"+value+".png");
			labelImg = new JLabel(new ImageIcon(url), JLabel.CENTER);
		}
		
		return labelImg;
	}

}
