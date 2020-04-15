package action;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

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
 * @date 2020年4月14日
 * @effect TODO
 */
public class DeleteListener implements ActionListener {
	private FtpTable table;
	private FtpClientCore_Imp client;
	private DialogArea dialog;
	private JFrame frame;

	public DeleteListener(FtpClientCore_Imp client, FtpTable table, DialogArea dialog, JFrame frame) {
		this.table = table;
		this.client = client;
		this.dialog = dialog;
		this.frame = frame;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (InitCoponents.getHasLogin()) {
			if (!table.getSelectedFiles().isEmpty()) {
				deleteFiles();
			} else {
				JOptionPane.showMessageDialog(frame, "未选择任何要删除的文件", "未选择文件", JOptionPane.ERROR_MESSAGE);
			}

		} else {
			InitCoponents.showUnloginErr(frame);
		}

	}

	private void deleteFiles() {
		ArrayList<String> selectedFiles = table.getSelectedFiles();
		String[] files = new String[selectedFiles.size()];
		selectedFiles.toArray(files);
		//提示确定删除文件?
		int res = JOptionPane.showConfirmDialog(frame, "确定删除这些文件/夹", "确定删除", JOptionPane.YES_NO_OPTION);
		if(res == JOptionPane.YES_OPTION) {
			// 删除文件
			final ProgressorDialog progressor = new ProgressorDialog(frame, "删除中,请稍等...");
			new Thread() {
				public void run() {
					try {
						String path = client.getCurrentPath();
						client.deleteFiles(files);
						table.listFiles(null);//清空列表
						table.resizeSelectedFiles();
						table.listFiles(client.getFiles(path));
					} catch (FtpException e) {
						dialog.appendContent(e.getErrorInfo());
						return;
					} finally {
						progressor.dispose();
					}

				}
			}.start();
			
			progressor.setVisible(true);
		}
		
	}

}
