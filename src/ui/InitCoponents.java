package ui;

import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import action.DeleteListener;
import action.DownloadListener;
import action.LogOutListener;
import action.LoginListener;
import action.UploadListener;
import core.implement.FtpClientCore_Imp;
import core.implement.FtpException;

/**
 * @author jinxiaochi
 * @date 2020年4月10日
 * @effect 各个组件的初始化; 完成一半
 */
public class InitCoponents {
	// ftp操作类
	private FtpClientCore_Imp client = new FtpClientCore_Imp();
	private static boolean hasLogin = false;

	public static boolean getHasLogin() {
		return hasLogin;
	}

	public static void setHasLogin(boolean hasLogin) {
		InitCoponents.hasLogin = hasLogin;
	}

	/**
	 * @effect 初始化组件
	 * @param frame
	 */
	private void init(JFrame frame) {
		Container container = frame.getContentPane();
		container.setLayout(null);
		Font font = new Font("Dialog", Font.BOLD, 18);

		// ip地址栏的文字提示;
		JLabel ipLabel = new JLabel("ftp://");
		ipLabel.setBounds(10, 20, 40, 30);
		ipLabel.setFont(font);
		container.add(ipLabel);

		// ip地址输入栏;
		JTextField ipAddress = new JTextField();
		ipAddress.setBounds(52, 20, 200, 30);
		ipAddress.addFocusListener(new JTextFieldInitText(ipAddress, "请输入ftp地址"));
		container.add(ipAddress);

		// 端口号前面的文字;
		JLabel portLabel = new JLabel("端口号: ");
		portLabel.setBounds(260, 20, 60, 30);
		portLabel.setFont(new Font("Dialog", Font.PLAIN, 15));
		container.add(portLabel);

		// 端口号
		JTextField port = new JTextField("21");
		port.setBounds(310, 20, 40, 30);
		container.add(port);

		// 是否匿名登陆
		JCheckBox checkBox = new JCheckBox();
		checkBox.setFont(new Font("Dialog", Font.ITALIC, 13));
		checkBox.setText("匿名登录");
		checkBox.setBounds(350, 20, 80, 30);
		container.add(checkBox);

		// 系统类型区
		JTextArea serverType = new JTextArea("ftp服务端类型:\n 未登录");
		serverType.setBounds(690, 5, 130, 60);
		serverType.setForeground(Color.GRAY);
		serverType.setEditable(false);
		container.add(serverType);

		// 日志区
		DialogArea dialogArea = new DialogArea(container);

		// 指示当前路径
		JLabel serverLocation = new JLabel();
		serverLocation.setBounds(20, 300, 800, 20);
		container.add(serverLocation);

		// 表格
		FtpTable table = new FtpTable(frame, serverLocation, client, dialogArea);

		// 下方按钮区;按钮字体待修正
		JButton uploadBtn = new JButton("上传");
		uploadBtn.setBounds(20, 320, 60, 30);
		uploadBtn.addActionListener(new UploadListener(client, table, dialogArea, frame));
		JButton downloadBtn = new JButton("下载");
		downloadBtn.setBounds(100, 320, 60, 30);
		downloadBtn.addActionListener(new DownloadListener(client, table, dialogArea, frame));
		JButton deleteBtn = new JButton("删除");
		deleteBtn.setBounds(180, 320, 60, 30);
		deleteBtn.addActionListener(new DeleteListener(client, table, dialogArea, frame));

		JButton goParentBtn = new JButton("返回父目录");
		goParentBtn.setBounds(260, 320, 100, 30);
		goParentBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (hasLogin) {
					String text = table.getLocation();
					if (text.equals("/")) {
						JOptionPane.showMessageDialog(frame, "你已在根目录,无法返回上级目录.", "错误操作", JOptionPane.ERROR_MESSAGE);
					} else {
						try {
							client.changeToParentDir();
							table.refresh();
						} catch (FtpException e1) {
							dialogArea.appendContent(e1.getErrorInfo());
						}
					}
				} else {
					showUnloginErr(frame);
				}
			}
		});

		JButton mkdirBtn = new JButton("新建文件夹");
		mkdirBtn.setBounds(560, 320, 100, 30);
		mkdirBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (hasLogin) {
					String dirName = JOptionPane.showInputDialog(frame, "请输入新的文件夹名称");
					if (dirName != null && !dirName.equals("")) {
						try {
							client.mkdirCurrentPath(dirName);
							table.appendRow(false, false, dirName, "0",DateFormat.getDateTimeInstance().format(new Date()), true);
						} catch (FtpException e1) {
							dialogArea.appendContent(e1.getErrorInfo());
						}
					}

				} else {
					showUnloginErr(frame);
				}

			}
		});
		JButton refreshBtn = new JButton("刷新");
		refreshBtn.setBounds(680, 320, 60, 30);
		refreshBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (hasLogin) {
					try {
						table.refresh();
						dialogArea.appendContent("刷新完成");
					} catch (FtpException e1) {
						dialogArea.appendContent(e1.getErrorInfo());
					}
				} else {
					showUnloginErr(frame);
				}

			}
		});
		JButton selectAllBtn = new JButton("全选");
		selectAllBtn.setBounds(760, 320, 60, 30);
		selectAllBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (hasLogin) {
					table.selectAllRow();
				} else {
					showUnloginErr(frame);
				}

			}
		});

		container.add(uploadBtn);
		container.add(downloadBtn);
		container.add(deleteBtn);
		container.add(goParentBtn);
		container.add(mkdirBtn);
		container.add(refreshBtn);
		container.add(selectAllBtn);

		// 登录按钮
		JButton loginBtn = new JButton("登录");
		loginBtn.setBounds(430, 20, 80, 30);
		loginBtn.addActionListener(
				new LoginListener(client, ipAddress, port, checkBox, serverType, table, dialogArea, loginBtn));

		container.add(loginBtn);

		// 注销按钮
		JButton disconnectBtn = new JButton("退出登录");
		disconnectBtn.setBounds(570, 20, 100, 30);
		disconnectBtn.addActionListener(
				new LogOutListener(client, ipAddress, port, checkBox, serverType, table, dialogArea, loginBtn));
		container.add(disconnectBtn);
	}

	public InitCoponents(JFrame frame) {
		init(frame);
	}

	public static void showUnloginErr(JFrame frame) {
		JOptionPane.showMessageDialog(frame, "未登录错误", "未登录", JOptionPane.ERROR_MESSAGE);
	}
}
