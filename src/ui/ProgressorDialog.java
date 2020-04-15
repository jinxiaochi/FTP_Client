package ui;

import java.awt.Container;
import java.awt.Point;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JProgressBar;

/**
 * @author jinxiaochi
 * @date 2020年4月13日
 * @effect 进度条窗口
 */
public class ProgressorDialog extends JDialog {
	private static final long serialVersionUID = 1L;
	// 窗口的组件
	private JLabel message = new JLabel();
	private JProgressBar progressor = new JProgressBar();

	private void init(Container con) {
		this.setSize(190, 100);
		this.setLayout(null);
		this.progressor.setIndeterminate(true);
		this.progressor.setBounds(20, 50, 150, 20);
		con.add(this.progressor);
		this.message.setBounds(50, 10, 100, 30);
		con.add(message);
		this.setUndecorated(true);
		this.setModal(true);
	}
	
	/**
	 * @param title 窗口标题
	 * @param message 窗口提示信息
	 */
	public ProgressorDialog(JFrame frame,String message) {
			super(frame);
			Point location = frame.getLocation();
			this.setLocation(location.x+350, location.y+160);
			this.message.setText(message);
			init(this.getContentPane());
	}

	public void disposeDialog() {
		this.dispose();
	}
}
