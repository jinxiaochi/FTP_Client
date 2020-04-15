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
 * @date 2020年4月12日
 * @effect 退出登录事件实现类
 */
public class LogOutListener implements ActionListener {
	private JTextField address;
	private JTextField portFiled;
	private JCheckBox isAnymous;
	private FtpClientCore_Imp client;
	private JTextArea serverType;
	private FtpTable table;
	private JFrame frame;// 客户端主体
	private DialogArea dialogArea;// 日志区
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

		dialogArea.appendContent("退出登录成功...");
		InitCoponents.setHasLogin(false);

		// 让登录按钮和相关组件可选中(未登录)
		enableComponents();

		// 系统类型设置为 未登录
		serverType.setText("ftp服务端类型:\n 未登录");

		// 当前目录为未登录 写入label
		table.setLocation("[未登录]");

		// 表格清空
		table.listFiles(null);

	}

	private void enableComponents() {
		address.setEditable(true);
		portFiled.setEditable(true);
		isAnymous.setEnabled(true);
		loginBtn.setText("登录");
		loginBtn.setEnabled(true);
	}

}
