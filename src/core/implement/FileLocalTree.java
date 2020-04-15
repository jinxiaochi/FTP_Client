package core.implement;

import java.io.File;
import java.util.ArrayList;

/**
 * @author jinxiaochi
 * @date  2020年4月2日
 * @effect 获得文件夹的文件树
 */
public class FileLocalTree {
	//当前问价夹名称
	private String DirName = null;
	//当前文件夹的 文件节点和 子文件夹节点
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
	
	//生成文件和文件夹节点
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
			throw new Exception("无法以文件生成文件树;请用文件夹生成");
		}
	}
}
