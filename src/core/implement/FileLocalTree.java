package core.implement;

import java.io.File;
import java.util.ArrayList;

/**
 * @author jinxiaochi
 * @date  2020��4��2��
 * @effect ����ļ��е��ļ���
 */
public class FileLocalTree {
	//��ǰ�ʼۼ�����
	private String DirName = null;
	//��ǰ�ļ��е� �ļ��ڵ�� ���ļ��нڵ�
	private ArrayList<File> files = new ArrayList<>();
	private ArrayList<File> directories = new ArrayList<>();
	public ArrayList<File> getFiles() {
		return files;
	}

	public ArrayList<File> getDirectories() {
		return directories;
	}
	
	public String getDirName() {
		return DirName;
	}
	
	//�����ļ����ļ��нڵ�
	public FileLocalTree(File root) throws Exception {
		if(root.isDirectory()) {
			this.DirName  = root.getName();
			File[] list = root.listFiles();
			
			for(File f : list) {
				if(f.isDirectory()) {
					directories.add(f);
				}else {
					files.add(f);
				}
			}
		}else {
			throw new Exception("�޷����ļ������ļ���;�����ļ�������");
		}
	}
}
