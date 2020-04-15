package action;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import core.implement.FtpClientCore_Imp;
import core.implement.FtpException;
import ui.DialogArea;
import ui.FtpTable;
import ui.InitCoponents;

/**
 * @author jinxiaochi
 * @date 2020��4��12��
 * @effect �˳���¼�¼�ʵ����
 */
public class LogOutListener implements ActionListener {
	private JTextField address;
	private JTextField portFiled;
	private JCheckBox isAnymous;
	private FtpClientCore_Imp client;
	private JTextArea serverType;
	private FtpTable table;
	private JFrame frame;// �ͻ�������
	private DialogArea dialogArea;// ��־��
	private JButton loginBtn;

	/**
	 * 
	 */
	public LogOutListener(FtpClientCore_Imp client, JTextField address, JTextField portFiled, JCheckBox isAnymous,
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
		if (InitCoponents.getHasLogin()) {
			try {
				client.logoutFtp();
			} catch (FtpException e1) {
				dialogArea.appendContent(e1.getErrorInfo());
				return;
			}
		} else {
			InitCoponents.showUnloginErr(frame);
			return;
		}

		dialogArea.appendContent("�˳���¼�ɹ�...");
		InitCoponents.setHasLogin(false);

		// �õ�¼��ť����������ѡ��(δ��¼)
		enableComponents();

		// ϵͳ��������Ϊ δ��¼
		serverType.setText("ftp���������:\n δ��¼");

		// ��ǰĿ¼Ϊδ��¼ д��label
		table.setLocation("[δ��¼]");

		// ������
		table.listFiles(null);

	}

	private void enableComponents() {
		address.setEditable(true);
		portFiled.setEditable(true);
		isAnymous.setEnabled(true);
		loginBtn.setText("��¼");
		loginBtn.setEnabled(true);
	}

}
