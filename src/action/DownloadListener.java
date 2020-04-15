package action;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import core.implement.FtpClientCore_Imp;
import core.implement.FtpException;
import ui.DialogArea;
import ui.FtpTable;
import ui.InitCoponents;
import ui.ProgressorDialog;

/**
 * @author jinxiaochi
 * @date  2020��4��14��
 * @effect ʵ�����ع���
 */
public class DownloadListener implements ActionListener {
	private FtpTable table;
	private FtpClientCore_Imp client;
	private DialogArea dialog;
	private JFrame frame;
	private JFileChooser downloadChooser = null;
	
	public DownloadListener(FtpClientCore_Imp client, FtpTable table, DialogArea dialog, JFrame frame) {
		this.table = table;
		this.client = client;
		this.dialog = dialog;
		this.frame = frame;
	}

	private void initFileChoooser() {
		this.downloadChooser = new JFileChooser();
		downloadChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

		int res = downloadChooser.showDialog(frame, "����");
		if (res == JFileChooser.APPROVE_OPTION) {
			final File path = downloadChooser.getSelectedFile();
			ArrayList<String> fileList = table.getSelectedFiles();
			String[] files = new String[fileList.size()];
			fileList.toArray(files);

			final ProgressorDialog progressor = new ProgressorDialog(frame, "������,���Ե�...");
			// �����ļ�
			new Thread() {
				public void run() {
					try {
						client.downloadFiles(files, path.getAbsolutePath());
						dialog.appendContent("�����ļ�/�� �ɹ�");
					} catch (FtpException e) {
						dialog.appendContent(e.getErrorInfo());
						return;
					}finally {
						progressor.disposeDialog();
					}
					
				}
			}.start();

			progressor.setVisible(true);
			
		}
		try {
			table.refresh();
		} catch (FtpException e) {
			dialog.appendContent(e.getErrorInfo());
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (InitCoponents.getHasLogin()) {
			 if(!table.getSelectedFiles().isEmpty()) {
				 initFileChoooser();
			 }else {
				 JOptionPane.showMessageDialog(frame, "δѡ���κ�Ҫ���ص��ļ�", "δѡ���ļ�", JOptionPane.ERROR_MESSAGE);
			 }
			
		} else {
			InitCoponents.showUnloginErr(frame);
		}

	}

}
