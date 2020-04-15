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
 * @date 2020��4��10��
 * @effect ��������ĳ�ʼ��; ���һ��
 */
public class InitCoponents {
	// ftp������
	private FtpClientCore_Imp client = new FtpClientCore_Imp();
	private static boolean hasLogin = false;

	public static boolean getHasLogin() {
		return hasLogin;
	}

	public static void setHasLogin(boolean hasLogin) {
		InitCoponents.hasLogin = hasLogin;
	}

	/**
	 * @effect ��ʼ�����
	 * @param frame
	 */
	private void init(JFrame frame) {
		Container container = frame.getContentPane();
		container.setLayout(null);
		Font font = new Font("Dialog", Font.BOLD, 18);

		// ip��ַ����������ʾ;
		JLabel ipLabel = new JLabel("ftp://");
		ipLabel.setBounds(10, 20, 40, 30);
		ipLabel.setFont(font);
		container.add(ipLabel);

		// ip��ַ������;
		JTextField ipAddress = new JTextField();
		ipAddress.setBounds(52, 20, 200, 30);
		ipAddress.addFocusListener(new JTextFieldInitText(ipAddress, "������ftp��ַ"));
		container.add(ipAddress);

		// �˿ں�ǰ�������;
		JLabel portLabel = new JLabel("�˿ں�: ");
		portLabel.setBounds(260, 20, 60, 30);
		portLabel.setFont(new Font("Dialog", Font.PLAIN, 15));
		container.add(portLabel);

		// �˿ں�
		JTextField port = new JTextField("21");
		port.setBounds(310, 20, 40, 30);
		container.add(port);

		// �Ƿ�������½
		JCheckBox checkBox = new JCheckBox();
		checkBox.setFont(new Font("Dialog", Font.ITALIC, 13));
		checkBox.setText("������¼");
		checkBox.setBounds(350, 20, 80, 30);
		container.add(checkBox);

		// ϵͳ������
		JTextArea serverType = new JTextArea("ftp���������:\n δ��¼");
		serverType.setBounds(690, 5, 130, 60);
		serverType.setForeground(Color.GRAY);
		serverType.setEditable(false);
		container.add(serverType);

		// ��־��
		DialogArea dialogArea = new DialogArea(container);

		// ָʾ��ǰ·��
		JLabel serverLocation = new JLabel();
		serverLocation.setBounds(20, 300, 800, 20);
		container.add(serverLocation);

		// ���
		FtpTable table = new FtpTable(frame, serverLocation, client, dialogArea);

		// �·���ť��;��ť���������
		JButton uploadBtn = new JButton("�ϴ�");
		uploadBtn.setBounds(20, 320, 60, 30);
		uploadBtn.addActionListener(new UploadListener(client, table, dialogArea, frame));
		JButton downloadBtn = new JButton("����");
		downloadBtn.setBounds(100, 320, 60, 30);
		downloadBtn.addActionListener(new DownloadListener(client, table, dialogArea, frame));
		JButton deleteBtn = new JButton("ɾ��");
		deleteBtn.setBounds(180, 320, 60, 30);
		deleteBtn.addActionListener(new DeleteListener(client, table, dialogArea, frame));

		JButton goParentBtn = new JButton("���ظ�Ŀ¼");
		goParentBtn.setBounds(260, 320, 100, 30);
		goParentBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (hasLogin) {
					String text = table.getLocation();
					if (text.equals("/")) {
						JOptionPane.showMessageDialog(frame, "�����ڸ�Ŀ¼,�޷������ϼ�Ŀ¼.", "�������", JOptionPane.ERROR_MESSAGE);
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

		JButton mkdirBtn = new JButton("�½��ļ���");
		mkdirBtn.setBounds(560, 320, 100, 30);
		mkdirBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (hasLogin) {
					String dirName = JOptionPane.showInputDialog(frame, "�������µ��ļ�������");
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
		JButton refreshBtn = new JButton("ˢ��");
		refreshBtn.setBounds(680, 320, 60, 30);
		refreshBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (hasLogin) {
					try {
						table.refresh();
						dialogArea.appendContent("ˢ�����");
					} catch (FtpException e1) {
						dialogArea.appendContent(e1.getErrorInfo());
					}
				} else {
					showUnloginErr(frame);
				}

			}
		});
		JButton selectAllBtn = new JButton("ȫѡ");
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

		// ��¼��ť
		JButton loginBtn = new JButton("��¼");
		loginBtn.setBounds(430, 20, 80, 30);
		loginBtn.addActionListener(
				new LoginListener(client, ipAddress, port, checkBox, serverType, table, dialogArea, loginBtn));

		container.add(loginBtn);

		// ע����ť
		JButton disconnectBtn = new JButton("�˳���¼");
		disconnectBtn.setBounds(570, 20, 100, 30);
		disconnectBtn.addActionListener(
				new LogOutListener(client, ipAddress, port, checkBox, serverType, table, dialogArea, loginBtn));
		container.add(disconnectBtn);
	}

	public InitCoponents(JFrame frame) {
		init(frame);
	}

	public static void showUnloginErr(JFrame frame) {
		JOptionPane.showMessageDialog(frame, "δ��¼����", "δ��¼", JOptionPane.ERROR_MESSAGE);
	}
}
