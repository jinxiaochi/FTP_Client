package action;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

import core.implement.FtpClientCore_Imp;
import core.implement.FtpException;
import ui.DialogArea;
import ui.FtpTable;
import ui.InitCoponents;
import ui.ProgressorDialog;

/**
 * @author jinxiaochi
 * @date 2020年4月13日
 * @effect 实现上传功能
 */
public class UploadListener implements ActionListener {
	private FtpTable table;
	private FtpClientCore_Imp client;
	private DialogArea dialog;
	private JFrame frame;
	private JFileChooser uploadChooser = null;

	public UploadListener(FtpClientCore_Imp client, FtpTable table, DialogArea dialog, JFrame frame) {
		this.table = table;
		this.client = client;
		this.dialog = dialog;
		this.frame = frame;
	}

	private void initFileChoooser() {
		this.uploadChooser = new JFileChooser();
		uploadChooser.setMultiSelectionEnabled(true);
		uploadChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		uploadChooser.addChoosableFileFilter(new FileFilter() {
			@Override
			public String getDescription() {
				return "文件";
			}

			@Override
			public boolean accept(File f) {
				if (f.isFile()) {
					return true;
				} else {
					return false;
				}

			}
		});

		int res = uploadChooser.showDialog(frame, "上传");
		if (res == JFileChooser.APPROVE_OPTION) {
			File[] selectedFiles = uploadChooser.getSelectedFiles();
			if(selectedFiles.length == 0) {
				JOptionPane.showMessageDialog(frame, "未选择任何要上传的文件", "未选择文件", JOptionPane.ERROR_MESSAGE);
				return;
			}
			
			ArrayList<File> uploadFiles = new ArrayList<>();
			for (File f : selectedFiles) {
				uploadFiles.add(f);
			}

			final ProgressorDialog progressor = new ProgressorDialog(frame, "上传中,请稍等...");
			// 上传文件
			new Thread() {
				public void run() {
					try {
						client.uploadFiles(uploadFiles, "");
						dialog.appendContent("上传文件/夹 成功");
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
			initFileChoooser();
		} else {
			InitCoponents.showUnloginErr(frame);
		}

	}

}
