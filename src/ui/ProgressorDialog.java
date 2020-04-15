package ui;

import java.awt.Container;
import java.awt.Point;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JProgressBar;

/**
 * @author jinxiaochi
 * @date 2020��4��13��
 * @effect ����������
 */
public class ProgressorDialog extends JDialog {
	private static final long serialVersionUID = 1L;
	// ���ڵ����
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
	 * @param title ���ڱ���
	 * @param message ������ʾ��Ϣ
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
