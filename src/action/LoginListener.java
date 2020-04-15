package action;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import core.implement.FtpClientCore_Imp;
import core.implement.FtpException;
import ui.DialogArea;
import ui.FtpTable;
import ui.InitCoponents;
import ui.LoginDialog;
import ui.dataStructure.User;

/**
 * @author jinxiaochi
 * @date 2020��4��12��
 * @effect TODO
 */
public class LoginListener implements ActionListener {
	private JTextField address;
	private JTextField portFiled;
	private JCheckBox isAnymous;
	private FtpClientCore_Imp client;
	private JTextArea serverType;
	private FtpTable table;
	private JFrame frame;// �ͻ�������
	private DialogArea dialogArea;// ��־��
	private JButton loginBtn;

	public LoginListener(FtpClientCore_Imp client, JTextField address, JTextField portFiled, JCheckBox isAnymous,
			JTextArea serverType, FtpTable table, DialogArea dialogArea, JButton login) {
		this.address = address;
		this.portFiled = portFiled;
		this.client = client;
		this.isAnymous = isAnymous;
		this.serverType = serverType;
		this.table = table;
		this.dialogArea = dialogArea;
		this.loginBtn = login;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		boolean Anymous = isAnymous.isSelected();
		String add = address.getText();
		if (add.equals("������ftp��ַ")) {
			JOptionPane.showMessageDialog(frame, "δ����ftp��ַ!!!", "ftp��ַ����", JOptionPane.ERROR_MESSAGE);
			return;
		}

		int port = 21;
		try {
			port = Integer.parseInt(portFiled.getText());
		} catch (Exception e2) {
			dialogArea.appendContent("����˿ں�: " + e2.getMessage());
			JOptionPane.showMessageDialog(frame, "�쳣�˿ں�!!!", "�˿ںŴ���", JOptionPane.ERROR_MESSAGE);
			return;
		}

		if (Anymous) {
			// ������¼����
			try {
				client.loginFtp(add, port);
			} catch (FtpException e1) {
				dialogArea.appendContent(e1.getErrorInfo());
				return;
			}
		} else {
			// ʵ����¼
			LoginDialog loginDialog = new LoginDialog(frame);
			User user = loginDialog.getLoginUser();
			if (user == null) {
				return;
			}
			try {
				client.loginFtp(add, port, user.getUsername(), user.getPasswd());
			} catch (FtpException e1) {
				dialogArea.appendContent(e1.getErrorInfo());
				return;
			}

		}

		dialogArea.appendContent("��½�ɹ�.....");
		InitCoponents.setHasLogin(true);

		// ���ϵͳ��Ϣ
		try {
			serverType.setText("ftp���������:\n" + client.getSystem());
		} catch (FtpException e1) {
			dialogArea.appendContent(e1.getErrorInfo());
		}

		// �õ�¼��ť������������ѡ��(�ѵ�¼)
		disableComponents();
		// ��õ�ǰĿ¼ д��label
		try {
			table.setLocation(client.getCurrentPath());
		} catch (FtpException e1) {
			dialogArea.appendContent(e1.getErrorInfo());
		}

		// ��õ�ǰĿ¼�ļ� д����
		try {
			table.listFiles(client.getFiles(""));
		} catch (FtpException e1) {
			dialogArea.appendContent(e1.getErrorInfo());
		}
	}

	/**
	 * @effect ��¼�ɹ����ò������ ����ѡ��/�༭
	 */
	private void disableComponents() {
		address.setEditable(false);
		portFiled.setEditable(false);
		isAnymous.setEnabled(false);
		loginBtn.setText("�ѵ�¼");
		loginBtn.setEnabled(false);
	}

}
