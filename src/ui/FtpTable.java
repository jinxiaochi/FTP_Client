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
 * @date 2020��4��10��
 * @effect Ftp����˵��б�; δ���
 */
public class FtpTable {
	// �����
	private JTable table = null;
	// �������
	private DefaultTableModel data = null;
	// ѡ�е��ļ����ļ���
	private ArrayList<String> selectedFiles = new ArrayList<>();
	// ftp �ͻ�����
	private FtpClientCore_Imp client = null;
	private DialogArea dialog;
	//�Ƿ�ȫѡ
	private boolean isAllSelected  = false;
	// ��ǰ����ftp������λ��
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
			if (!location.equals("[δ��¼]")) {
				this.location = new String(location.getBytes("iso-8859-1"));
			} else {
				this.location = location;
			}
			this.currentPath.setText("��ǰλ�� :    \t" + this.location);
		} catch (UnsupportedEncodingException e) {
			dialog.appendContent(e.getMessage());
		}

	}

	private void initTable(JFrame frame) {
		Container container = frame.getContentPane();

		// �ļ��б�����ʼ��
		this.table = new JTable();
		this.data = new DefaultTableModel() { // ����һ�пɱ༭
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}

		};
		data.setColumnIdentifiers(new Object[] { "ѡ��", "����", "�ļ���", "�ļ���С", "�����޸�ʱ��", "�޸�����", "�Ƿ��д��" });

		table.setModel(data);
		table.setRowHeight(25);

		// ���ò����е�����Ϊ���
//		table.getColumnModel().getColumn(0).setCellEditor(new DefaultCellEditor(new JCheckBox()));
		table.getColumnModel().getColumn(0).setCellRenderer(new TableCheckBoxRenderer());

		table.getColumnModel().getColumn(1).setCellRenderer(new TableFileLabelRender());
		table.getColumnModel().getColumn(5).setCellRenderer(new TableRenameLabelRender());

		TableCheckBoxRenderer renderer = new TableCheckBoxRenderer();
		renderer.setHorizontalAlignment(JLabel.CENTER);
		table.getColumnModel().getColumn(6).setCellRenderer(renderer);

		// ����4 -5 �еĶ��䷽ʽ;���Ҷ���
		DefaultTableCellRenderer alignToRight = new DefaultTableCellRenderer();
		alignToRight.setHorizontalAlignment(JLabel.RIGHT);
		table.getColumnModel().getColumn(3).setCellRenderer(alignToRight);
		table.getColumnModel().getColumn(4).setCellRenderer(alignToRight);

		// ���ñ�ĸ����п�
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
				// �����1��
				if (column == 0) {
					Boolean isSelected = (Boolean) table.getValueAt(row, 0);
					table.setValueAt(new Boolean(!isSelected), row, 0);
					if (!isSelected) {
						selectedFiles.add((String) table.getValueAt(row, 2));
					} else {
						selectedFiles.remove((String) table.getValueAt(row, 2));
					}

				} else if (column == 5) {
					// �����5��
					Boolean checkBox = (Boolean) table.getValueAt(row, 6);
					if (checkBox.booleanValue()) {
						String oldName = (String) table.getValueAt(row, 2);
						String newName = JOptionPane.showInputDialog(frame, "�������µ��ļ���: ", "�����ļ���",
								JOptionPane.QUESTION_MESSAGE);
						try {
							client.renameFile(null, oldName, newName);
							// ˢ��һ��
							refresh();
						} catch (FtpException e1) {
							dialog.appendContent(e1.getErrorInfo());
							return;
						}
					} else {
						JOptionPane.showMessageDialog(frame, "��д��Ȩ��!", "��Ȩ��", JOptionPane.ERROR_MESSAGE);
					}

				} else if (clickCount >= 2) {
					// ˫��Ӧ���ǽ����ļ��ж���ѡ��; ˫���ļ�ѡ��/��ѡ��
					String type = (String) table.getValueAt(row, 3);
					if (type.equals("�ļ���")) {
						// ������ļ���
						String dirName = (String) table.getValueAt(row, 2);
						try {
							listFiles(null);// ��ձ��
							listFiles(client.getFiles(dirName));
							setLocation(client.getCurrentPath());
						} catch (FtpException e1) {
							dialog.appendContent(e1.getErrorInfo());
							return;
						}
					} else {
						// �ı��ļ�ѡ��״̬
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
		setLocation("[δ��¼]");
		this.client = client;
		this.dialog = dialog;
	}

	/**
	 * @effect �б�׷��һ��
	 */
	public void appendRow(boolean isSelected, boolean isFile, String fileName, String size, String date,
			boolean canWrite) {
		// ��Ӳ�������
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
			size = "�ļ���";
		} else {
			size += "�ֽ�";
		}

		data.addRow(new Object[] { new Boolean(isSelected), fileIcon, fileName, size, date, renameIcon,
				new Boolean(canWrite) });
	}

	/**
	 * @effect ��½���ʼ�����
	 * @param files
	 */
	public void listFiles(FTPFile[] files) {
		resizeSelectedFiles();
		// ����null;����ձ��
		if (files == null) {
			data.setRowCount(0);
			return;
		}
		for (FTPFile f : files) {
			SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			String date = s.format(f.getTimestamp().getTime());
			// f.hasPermission(FTPFile.USER_ACCESS, FTPFile.READ_PERMISSION) ��֪Ϊ��ʧЧ
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
	 * @effect ˢ�±������
	 * @throws FtpException
	 */
	public void refresh() throws FtpException {
		String path = client.getCurrentPath();
		listFiles(null);// ����б�
		setLocation(path);// ˢ��·��
		listFiles(client.getFiles(null));// ���»������
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
		
		isAllSelected = !isAllSelected;//�ı�ȫѡ״̬
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
