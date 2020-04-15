package ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JTextField;

/**
 * @author jinxiaochi
 * @date 2020年4月7日
 * @effect 显示ip地址栏默认信息
 */
public class JTextFieldInitText implements FocusListener {
	private String hideText;
	private JTextField textField;
	private Font font = new Font("Dialog",Font.PLAIN,15);

	public JTextFieldInitText(JTextField jTextField, String hideText) {
		this.textField = jTextField;
		this.hideText = hideText;
		jTextField.setText(hideText); // 默认直接显示
		jTextField.setFont(font);
		jTextField.setForeground(Color.GRAY);
	}

	/*
	 * 获得焦点的处理方法
	 */
	@Override
	public void focusGained(FocusEvent e) {
		// 获取焦点时，清空提示内容
		String temp = textField.getText();
		if (temp.equals(hideText)) {
			textField.setText("");
			textField.setFont(font);
			textField.setForeground(Color.BLACK);
		}

	}

	/*
	 * 失去焦点的处理方法
	 */
	@Override
	public void focusLost(FocusEvent e) {
		String temp = textField.getText();
		if(temp.equals("")) {
			textField.setForeground(Color.GRAY);
			textField.setFont(font);
			textField.setText(hideText);
		}

	}

}
