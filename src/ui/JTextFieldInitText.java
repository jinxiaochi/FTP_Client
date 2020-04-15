package ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JTextField;

/**
 * @author jinxiaochi
 * @date 2020��4��7��
 * @effect ��ʾip��ַ��Ĭ����Ϣ
 */
public class JTextFieldInitText implements FocusListener {
	private String hideText;
	private JTextField textField;
	private Font font = new Font("Dialog",Font.PLAIN,15);

	public JTextFieldInitText(JTextField jTextField, String hideText) {
		this.textField = jTextField;
		this.hideText = hideText;
		jTextField.setText(hideText); // Ĭ��ֱ����ʾ
		jTextField.setFont(font);
		jTextField.setForeground(Color.GRAY);
	}

	/*
	 * ��ý���Ĵ�����
	 */
	@Override
	public void focusGained(FocusEvent e) {
		// ��ȡ����ʱ�������ʾ����
		String temp = textField.getText();
		if (temp.equals(hideText)) {
			textField.setText("");
			textField.setFont(font);
			textField.setForeground(Color.BLACK);
		}

	}

	/*
	 * ʧȥ����Ĵ�����
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
