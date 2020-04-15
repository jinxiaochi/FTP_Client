package ui;

import java.awt.Container;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import ui.dataStructure.User;

/**
 * @author jinxiaochi
 * @date 2020年4月10日
 * @effect 登录对会话框
 */
public class LoginDialog extends JDialog {
	private static final long serialVersionUID = 1L;
	// 登录对话框的各个组件
	private JTextField userNameText = null;
	private JPasswordField passwdText = null;
	private JLabel userLabel = null;
	private JLabel passwdLabel = null;
	private JButton loginBtn = null;
	private JButton cancelBtn = null;
	
	private User loginUser = null;
	public User getLoginUser() {
		return loginUser;
	}

	/**
	 * @effect 初始化各个组件
	 */
	private void initComponents(Container con) {
		con.setLayout(null);
		Font font = new Font("Dialog", Font.PLAIN, 16);
		
		userLabel = new JLabel("用户名:  ");
		userLabel.setFont(font);
		userLabel.setBounds(20, 20, 90, 30);
		userNameText = new JTextField();
		userNameText.setFont(font);
		userNameText.setBounds(80, 20, 200, 30);

		passwdLabel = new JLabel("密    码:");
		passwdLabel.setFont(font);
		passwdLabel.setBounds(20, 70, 90, 30);
		passwdText = new JPasswordField();
		passwdText.setFont(font);
		passwdText.setBounds(80, 70, 200, 30);

		loginBtn = new JButton("登录");
		loginBtn.setBounds(50, 120, 60, 30);
		loginBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//将账号和密码存入loginUser对象
				String name = userNameText.getText();
				String passwd = new String(passwdText.getPassword());
				if(name.equals("")||passwd.equals("")) {
					JOptionPane.showMessageDialog(LoginDialog.this, "你忘记输入用户名或密码", "用户名或密码不得为空", JOptionPane.ERROR_MESSAGE);
				}else {
					LoginDialog.this.loginUser = new User(name, passwd);
					LoginDialog.this.dispose();
				}
			}
		});
		cancelBtn = new JButton("取消");
		cancelBtn.setBounds(220, 120, 60, 30);
		cancelBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				LoginDialog.this.dispose();
			}
		});
		
		con.add(userLabel);
		con.add(userNameText);
		con.add(passwdLabel);
		con.add(passwdText);
		con.add(loginBtn);
		con.add(cancelBtn);
	}

	public LoginDialog(Frame parent) {
		super(parent, "登录");

		this.setSize(320, 200);
		this.setLocationRelativeTo(parent);
		this.setResizable(false);
		initComponents(this.getContentPane());
		this.setModal(true);
		this.setVisible(true);

	}
	
	
}
