package ui;

import java.awt.Color;
import java.awt.Container;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * @author jinxiaochi
 * @date  2020年4月10日
 * @effect 操作日志输出区; 未完成
 */
public class DialogArea {
	private JTextArea dialogArea = null;
	
	private void init(Container container) {
		dialogArea = new JTextArea();
		dialogArea.setEditable(false);
		dialogArea.setSelectedTextColor(Color.RED);
		dialogArea.setText("日志区:    \t\n");
		JScrollPane dialogPanel = new JScrollPane(dialogArea);
		dialogPanel.setBounds(20, 360, 800, 100);
		container.add(dialogPanel);
	}
	
	public DialogArea(Container container) {
		init(container);
	}
	
	/** @effect 日志操作区追加字符串
	 * @param str
	 */
	public void appendContent(String str) {
		dialogArea.append(str+"\n");
	}
	
	public void clearContent() {
		dialogArea.setText("日志区:    \t\n");
	}
}
