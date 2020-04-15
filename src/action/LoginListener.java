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
 * @date 2020年4月12日
 * @effect TODO
 */
public class LoginListener implements ActionListener {
	private JTextField address;
	private JTextField portFiled;
	private JCheckBox isAnymous;
	private FtpClientCore_Imp client;
	private JTextArea serverType;
	private FtpTable table;
	private JFrame frame;// 客户端主体
	private DialogArea dialogArea;// 日志区
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
		if (add.equals("请输入ftp地址")) {
			JOptionPane.showMessageDialog(frame, "未输入ftp地址!!!", "ftp地址错误", JOptionPane.ERROR_MESSAGE);
			return;
		}

		int port = 21;
		try {
			port = Integer.parseInt(portFiled.getText());
		} catch (Exception e2) {
			dialogArea.appendContent("错误端口号: " + e2.getMessage());
			JOptionPane.showMessageDialog(frame, "异常端口号!!!", "端口号错误", JOptionPane.ERROR_MESSAGE);
			return;
		}

		if (Anymous) {
			// 匿名登录操作
			try {
				client.loginFtp(add, port);
			} catch (FtpException e1) {
				dialogArea.appendContent(e1.getErrorInfo());
				return;
			}
		} else {
			// 实名登录
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

		dialogArea.appendContent("登陆成功.....");
		InitCoponents.setHasLogin(true);

		// 获得系统信息
		try {
			serverType.setText("ftp服务端类型:\n" + client.getSystem());
		} catch (FtpException e1) {
			dialogArea.appendContent(e1.getErrorInfo());
		}

		// 让登录按钮和相关组件不可选中(已登录)
		disableComponents();
		// 获得当前目录 写入label
		try {
			table.setLocation(client.getCurrentPath());
		} catch (FtpException e1) {
			dialogArea.appendContent(e1.getErrorInfo());
		}

		// 获得当前目录文件 写入表格
		try {
			table.listFiles(client.getFiles(""));
		} catch (FtpException e1) {
			dialogArea.appendContent(e1.getErrorInfo());
		}
	}

	/**
	 * @effect 登录成功后让部分组件 不可选中/编辑
	 */
	private void disableComponents() {
		address.setEditable(false);
		portFiled.setEditable(false);
		isAnymous.setEnabled(false);
		loginBtn.setText("已登录");
		loginBtn.setEnabled(false);
	}

}
