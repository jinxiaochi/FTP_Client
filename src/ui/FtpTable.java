package ui;

import java.awt.Color;
import java.awt.Container;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import org.apache.commons.net.ftp.FTPFile;

import core.implement.FtpClientCore_Imp;
import core.implement.FtpException;

/**
 * @author jinxiaochi
 * @date 2020年4月10日
 * @effect Ftp服务端的列表; 未完成
 */
public class FtpTable {
	// 表格本身
	private JTable table = null;
	// 表格数据
	private DefaultTableModel data = null;
	// 选中的文件或文件夹
	private ArrayList<String> selectedFiles = new ArrayList<>();
	// ftp 客户端类
	private FtpClientCore_Imp client = null;
	private DialogArea dialog;
	//是否被全选
	private boolean isAllSelected  = false;
	// 当前所在ftp服务器位置
	@SuppressWarnings("unused")
	private String location = null;
	private JLabel currentPath;

	public String getLocation() {
		return this.location;
	}

	public void setLocation(String location) {
		if(location == null) {
			return;
		}
		try {
			if (!location.equals("[未登录]")) {
				this.location = new String(location.getBytes("iso-8859-1"));
			} else {
				this.location = location;
			}
			this.currentPath.setText("当前位置 :    \t" + this.location);
		} catch (UnsupportedEncodingException e) {
			dialog.appendContent(e.getMessage());
		}

	}

	private void initTable(JFrame frame) {
		Container container = frame.getContentPane();

		// 文件列表区初始化
		this.table = new JTable();
		this.data = new DefaultTableModel() { // 仅第一列可编辑
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}

		};
		data.setColumnIdentifiers(new Object[] { "选中", "类型", "文件名", "文件大小", "最后的修改时间", "修改名称", "是否可写入" });

		table.setModel(data);
		table.setRowHeight(25);

		// 设置部分列的内容为组件
//		table.getColumnModel().getColumn(0).setCellEditor(new DefaultCellEditor(new JCheckBox()));
		table.getColumnModel().getColumn(0).setCellRenderer(new TableCheckBoxRenderer());

		table.getColumnModel().getColumn(1).setCellRenderer(new TableFileLabelRender());
		table.getColumnModel().getColumn(5).setCellRenderer(new TableRenameLabelRender());

		TableCheckBoxRenderer renderer = new TableCheckBoxRenderer();
		renderer.setHorizontalAlignment(JLabel.CENTER);
		table.getColumnModel().getColumn(6).setCellRenderer(renderer);

		// 设置4 -5 列的对其方式;向右对齐
		DefaultTableCellRenderer alignToRight = new DefaultTableCellRenderer();
		alignToRight.setHorizontalAlignment(JLabel.RIGHT);
		table.getColumnModel().getColumn(3).setCellRenderer(alignToRight);
		table.getColumnModel().getColumn(4).setCellRenderer(alignToRight);

		// 设置表的各个列宽
		table.getColumnModel().getColumn(0).setPreferredWidth(30);
		table.getColumnModel().getColumn(1).setPreferredWidth(30);
		table.getColumnModel().getColumn(2).setPreferredWidth(200);
		table.getColumnModel().getColumn(3).setPreferredWidth(180);
		table.getColumnModel().getColumn(4).setPreferredWidth(180);
		table.getColumnModel().getColumn(5).setPreferredWidth(70);
		table.getColumnModel().getColumn(6).setPreferredWidth(70);

		table.setBackground(Color.WHITE);
		table.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				int row = table.getSelectedRow();
				int column = table.getSelectedColumn();
				int clickCount = e.getClickCount();
				// 点击第1列
				if (column == 0) {
					Boolean isSelected = (Boolean) table.getValueAt(row, 0);
					table.setValueAt(new Boolean(!isSelected), row, 0);
					if (!isSelected) {
						selectedFiles.add((String) table.getValueAt(row, 2));
					} else {
						selectedFiles.remove((String) table.getValueAt(row, 2));
					}

				} else if (column == 5) {
					// 点击第5列
					Boolean checkBox = (Boolean) table.getValueAt(row, 6);
					if (checkBox.booleanValue()) {
						String oldName = (String) table.getValueAt(row, 2);
						String newName = JOptionPane.showInputDialog(frame, "请输入新的文件名: ", "更改文件名",
								JOptionPane.QUESTION_MESSAGE);
						try {
							client.renameFile(null, oldName, newName);
							// 刷新一次
							refresh();
						} catch (FtpException e1) {
							dialog.appendContent(e1.getErrorInfo());
							return;
						}
					} else {
						JOptionPane.showMessageDialog(frame, "无写入权限!", "无权限", JOptionPane.ERROR_MESSAGE);
					}

				} else if (clickCount >= 2) {
					// 双击应该是进入文件夹而非选中; 双击文件选中/不选中
					String type = (String) table.getValueAt(row, 3);
					if (type.equals("文件夹")) {
						// 进入此文件夹
						String dirName = (String) table.getValueAt(row, 2);
						try {
							listFiles(null);// 清空表格
							listFiles(client.getFiles(dirName));
							setLocation(client.getCurrentPath());
						} catch (FtpException e1) {
							dialog.appendContent(e1.getErrorInfo());
							return;
						}
					} else {
						// 改变文件选中状态
						Boolean check = (Boolean) table.getValueAt(row, 0);
						table.setValueAt(new Boolean(!check.booleanValue()), row, 0);
						if (!check.booleanValue()) {
							selectedFiles.add((String) table.getValueAt(row, 2));
						} else {
							selectedFiles.remove((String) table.getValueAt(row, 2));
						}
					}

				}

			}
		});

		JScrollPane scrollPane = new JScrollPane(table);
		scrollPane.setBounds(20, 80, 800, 220);
		container.add(scrollPane);

	}

	public FtpTable(JFrame frame, JLabel path, FtpClientCore_Imp client, DialogArea dialog) {
		initTable(frame);
		this.currentPath = path;
		setLocation("[未登录]");
		this.client = client;
		this.dialog = dialog;
	}

	/**
	 * @effect 列表追加一行
	 */
	public void appendRow(boolean isSelected, boolean isFile, String fileName, String size, String date,
			boolean canWrite) {
		// 添加部分内容
		String fileIcon = null;
		String renameIcon = null;
		if (isFile) {
			fileIcon = TableFileLabelRender.File;
		} else {
			fileIcon = TableFileLabelRender.Directory;
		}

		if (canWrite) {
			renameIcon = TableRenameLabelRender.writeFile;
		} else {
			renameIcon = TableRenameLabelRender.readOnlyFile;
		}

		if (size.equals("0")) {
			size = "文件夹";
		} else {
			size += "字节";
		}

		data.addRow(new Object[] { new Boolean(isSelected), fileIcon, fileName, size, date, renameIcon,
				new Boolean(canWrite) });
	}

	/**
	 * @effect 登陆后初始化表格
	 * @param files
	 */
	public void listFiles(FTPFile[] files) {
		resizeSelectedFiles();
		// 传入null;则清空表格
		if (files == null) {
			data.setRowCount(0);
			return;
		}
		for (FTPFile f : files) {
			SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			String date = s.format(f.getTimestamp().getTime());
			// f.hasPermission(FTPFile.USER_ACCESS, FTPFile.READ_PERMISSION) 不知为何失效
			appendRow(false, f.isFile(), f.getName(), Long.toString(f.getSize()), date, true);
		}
	}

	public ArrayList<String> getSelectedFiles() {
		return selectedFiles;
	}

	public void resizeSelectedFiles() {
		this.selectedFiles.clear();
	}

	/**
	 * @effect 刷新表格数据
	 * @throws FtpException
	 */
	public void refresh() throws FtpException {
		String path = client.getCurrentPath();
		listFiles(null);// 清空列表
		setLocation(path);// 刷新路径
		listFiles(client.getFiles(null));// 重新获得数据
	}

	public void selectAllRow() {
		int rowCount = table.getRowCount();
		resizeSelectedFiles();
		for (int i = 0; i < rowCount; i++) {
			table.setValueAt(new Boolean(!isAllSelected), i, 0);
			if(!isAllSelected) {
				selectedFiles.add((String) table.getValueAt(i, 2));
			}
		}
		
		isAllSelected = !isAllSelected;//改变全选状态
	}
	
	public void removeAllSelected() {
		int rowCount = table.getRowCount();
		for(int i =0 ;i<rowCount;i++) {
			if (selectedFiles.contains(table.getValueAt(i, 2))) {
				data.removeRow(i);
			}
		}
		
		resizeSelectedFiles();
	}

}
