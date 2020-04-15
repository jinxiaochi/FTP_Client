package core.implement;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import core.interfaces.FtpClient_core;

/**
 * @author jinxiaochi
 * @date 2020��3��31��
 * @effect Ftp�ͻ��˺��Ĺ���ʵ���� ע: ��ʱʡ�Զ�дȨ������; Ĭ���˻� �ɶ���д
 */
public class FtpClientCore_Imp implements FtpClient_core {
	private FTPClient client = null;
	private NoopLogin noopLogin = null;

	/**
	 * @effect ʵ����¼
	 * @param ftpHost  ftp������ַ��ip
	 * @param port     ���ӵĶ˿ں�
	 * @param username �˻�����
	 * @param password �˻�����
	 * @return FTPClient����
	 */
	@Override
	public FTPClient loginFtp(String ftpHost, int port, String username, String password) throws FtpException {
		this.client = new FTPClient();
		// ������6 ����һ��noop
		client.setControlKeepAliveTimeout(360);
		// ����ftp������
		try {
			client.connect(ftpHost, port);
		} catch (Exception e1) {
			throw new FtpException(1, "ʵ��FtpClientCore_Imp.logingFtp;=1; " + e1.getMessage());
		}

		// ��¼ftp������
		try {
			client.login(username, password);
			// ȷ��������ȷ
			if (!FTPReply.isPositiveCompletion(client.getReplyCode())) {
				client.disconnect();
				throw new FtpException(2, "����״̬���쳣");
			}
			// ���ͻ��˱��뷽ʽ�ͷ����һ��
			Charset charset = client.getCharset();
			client.setControlEncoding(charset.displayName());
			client.enterLocalPassiveMode();// ���ñ���ģʽ
			client.setFileType(FTP.BINARY_FILE_TYPE);// ���ô����ģʽ
			noopLogin = new NoopLogin(client);
			noopLogin.start();
			return client;
		} catch (IOException e) {
			throw new FtpException(2, "ʵ��FtpClientCore_Imp.logingFtp;=2;" + e.getMessage());
		}
	}

	/**
	 * @effect ������½
	 * @param ftpHost ftp������ַ��ip
	 * @param port    ���ӵĶ˿ں�
	 * @return FTPClient����
	 */
	@Override
	public FTPClient loginFtp(String ftpHost, int port) throws FtpException {
		client = new FTPClient();
		client.setControlKeepAliveTimeout(360);
		try {
			client.connect(ftpHost, port);
		} catch (Exception e) {
			throw new FtpException(1, "����FtpClientCore_Imp.logingFtp;=1; " + e.getMessage());
		}

		try {
			client.login("anonymous", "898681619@qq.com");
			if (!FTPReply.isPositiveCompletion(client.getReplyCode())) {
				client.disconnect();
				throw new FtpException(2, "����״̬���쳣");
			}
			// ���ͻ��˱��뷽ʽ�ͷ����һ��
			Charset charset = client.getCharset();
			client.setControlEncoding(charset.displayName());
			client.enterLocalPassiveMode();// ���ñ���ģʽ
			client.setFileType(FTP.BINARY_FILE_TYPE);// ���ô����ģʽ

			noopLogin = new NoopLogin(client);
			noopLogin.start();
			return client;
		} catch (IOException e) {
			throw new FtpException(2, "����FtpClientCore_Imp.logingFtp;=2;" + e.getMessage());
		}

	}

	/**
	 * @effect ע����¼���Ͽ�����
	 */
	@Override
	public void logoutFtp() throws FtpException {
		if (client != null) {
			// ���Ѿ��Ͽ�����,������ע��
			if (!client.isConnected()) {
				return;
			}

			try {
				client.logout();
				client.disconnect();
			} catch (IOException e) {
				throw new FtpException(3, "FtpClientCore_Imp.logoutFtp;=1;" + e.getMessage());
			} finally {
				if (noopLogin != null) {
					noopLogin.stopNoop();
				}
			}

		}

	}

	/**
	 * @effect ���ַ���ת��ΪFtpЭ��Ҫ���iso-8859-1�����ʽ
	 * @param s Ҫת�����ַ���
	 * @return ת������ַ���
	 * @throws UnsupportedEncodingException
	 */
	public String toFTPString(String s) throws UnsupportedEncodingException {
		return new String(s.getBytes(), "iso-8859-1");// iso-8859-1
	}

	/*
	 * @effect �ϴ������ļ���
	 * 
	 * @param fileTree
	 * 
	 * @param serverPath
	 * 
	 * @return
	 * 
	 * @throws FtpException
	 */
	private void uploadDir(FileLocalTree fileTree, String serverPath) throws FtpException {
		// 1 �ϴ�Ŀ¼
		ArrayList<File> diretories = fileTree.getDirectories();
		// ����·���� / ����; ���� �� /��""Ŀ¼
		if (!(serverPath.equals("") || serverPath.equals("/"))) {
			serverPath += "/";
		}
		// A. ��Ŀ¼��û���ļ���; �����еݹ�
		if (diretories.size() == 0) {
			try {
				// ftp�޷���������Ŀ¼
				String[] splitPath = serverPath.split("/");
				for (int j = 0; j < splitPath.length; j++) {
					String mkdir = splitPath[0];
					for (int i = 1; i <= j; i++) {
						mkdir += "/" + splitPath[i];
					}

					if (!(mkdir.equals("") || mkdir.equals("/"))) {
						client.makeDirectory(toFTPString(mkdir));
					}

				}
			} catch (IOException e) {
				throw new FtpException(8, "FtpClientCore_Imp.uploadDir;=1;" + e.getMessage());
			}
		}

		// B. ��Ŀ¼�����ļ���
		for (File dir : diretories) {
			// 1.1 ������ �ļ���·��
			String dirPath = null;
			dirPath = serverPath + dir.getName();

			try {
				uploadDir(new FileLocalTree(dir), dirPath);
			} catch (Exception e) {
				throw new FtpException(7, "FtpClientCore_Imp.uploadDir;=2;" + e.getMessage());
			}
			// }

		} // �ϴ��ļ��н���

		// 2 �ϴ��ļ�
		ArrayList<File> files = fileTree.getFiles();
		for (File f : files) {
			try {
				client.storeFile(toFTPString(serverPath + f.getName()), new FileInputStream(f));
			} catch (UnsupportedEncodingException e) {
				throw new FtpException(9, "FtpClientCore_Imp.uploadDir;=3;" + e.getMessage());
			} catch (IOException e1) {
				throw new FtpException(8, "FtpClientCore_Imp.uploadDir;=4;" + e1.getMessage());
			}
		}

		return;
	}

	/**
	 * @effect �ļ��ϴ�;
	 * @param files
	 * @param serverPath
	 * @throws FtpException
	 * @return true�ϴ��ɹ�; false�ϴ�ʧ��
	 * @throws UnsupportedEncodingException
	 */
	@Override
	public boolean uploadFiles(ArrayList<File> files, String serverPath) throws FtpException {
		if (examinClientStatus()) {
			// ����·���� / ����; ���� �� /��""Ŀ¼
			if (!(serverPath.equals("") || serverPath.equals("/"))) {
				serverPath += "/";
			}

			boolean isSuccess = true;
			for (File f : files) {
				// �����ļ���
				if (f.isDirectory()) {
					try {
						uploadDir(new FileLocalTree(f), serverPath + f.getName());
					} catch (Exception e) {
						isSuccess = false;
						throw new FtpException(7, "FtpClientCore_Imp.uploadFiles;=1;" + e.getMessage());
					}
				} else {

					// �����ļ�
					try {
						client.storeFile(toFTPString(serverPath + f.getName()), new FileInputStream(f));
					} catch (FileNotFoundException e) {
						isSuccess = false;
						throw new FtpException(9, "FtpClientCore_Imp.uploadFile;=2;" + e.getMessage());
					} catch (IOException e1) {
						isSuccess = false;
						throw new FtpException(8, "FtpClientCore_Imp.uploadFile;=3;" + e1.getMessage());
					}
				}
			}

			return isSuccess;

		}

		return false;
	}

	/**
	 * @effect �����ļ����ļ��еľ���ʵ�ֺ��Ĵ���
	 */
	private void downLoadDir(String[] files, String localPath) throws FtpException {
		// �������ļ����� ��Ŀ¼�������򴴽�
		File rootLocalPath = new File(localPath);
		if (!rootLocalPath.exists()) {
			rootLocalPath.mkdirs();
		}
		for (String downFile : files) {
			try {
				FTPFile[] file = client.listFiles(toFTPString(downFile));
				// ���ļ����ļ��е� ����
				if (file.length == 1) {
					if (file[0].isFile()) {
						// �ж�downFile��·���ǲ����ļ�
						String[] split = downFile.split("/");
						// a ��Ϊ�ļ� ��·���������ƺ��ļ�����ͬ
						if (file[0].getName().equals(split[split.length - 1])) {
							client.retrieveFile(toFTPString(downFile),
									new FileOutputStream(localPath + "/" + file[0].getName()));
						} else {
							// b ��Ϊ���ļ����µĵ����ļ�
							File newDir = new File(localPath + "/" + split[split.length - 1]);
							if (!newDir.exists()) {
								newDir.mkdirs();
							}
							client.retrieveFile(toFTPString(downFile + "/" + file[0].getName()),
									new FileOutputStream(localPath + "/" + newDir.getName() + "/" + file[0].getName()));
						}

					} else {
						// �ж����ļ����򱾵ش����ļ��� �õ�һ���ļ���
						String[] strPath = downFile.split("/");
						String localSubDir = localPath + "/" + strPath[strPath.length - 1];
						File dir_only = new File(localSubDir);
						if (!dir_only.exists()) {
							dir_only.mkdirs();
						}

						// ���ļ��е��� �ļ�(��) ��������
						FTPFile[] listF = client.listFiles(toFTPString(downFile + "/" + file[0].getName()));
						String[] arr = new String[listF.length];
						if (listF.length > 0) {
							for (int i = 0; i < listF.length; i++) {
								arr[i] = downFile + "/" + file[0].getName() + "/" + listF[i].getName();
							}
						}
						downLoadDir(arr, localSubDir + "/" + file[0].getName());

					}
				} else if (file.length > 1) {
					String[] names = new String[file.length];
					for (int i = 0; i < file.length; i++) {
						names[i] = downFile + "/" + file[i].getName();
					}
					// �޸� ���ص�·��
					String[] pathAppend = downFile.split("/");
					downLoadDir(names, localPath + "/" + pathAppend[pathAppend.length - 1]);
				} else {
					// ��Ϊ��Ŀ¼
					String[] str = downFile.split("/");
					new File(localPath + "/" + str[str.length - 1]).mkdirs();

				}
			} catch (IOException e) {
				new FtpException(7, "FtpClientCore_Imp.downLoadDir;=1;" + e.getMessage());
			}

		}
	}

	/**
	 * @effect �ļ�����
	 * @param files     �ļ�(��)·��
	 * @param localPath ��������Ŀ¼
	 * @return true���سɹ�; false����ʧ��
	 * @throws FtpException
	 */
	@Override
	public boolean downloadFiles(String[] files, String localPath) throws FtpException {
		boolean isSuccess = true;
		if (examinClientStatus()) {
			try {
				downLoadDir(files, localPath);
			} catch (Exception e) {
				isSuccess = false;
				new FtpException(7, "FtpClientCore_Imp.downloadFiles;=1;" + e.getMessage());
			}

		}
		return isSuccess;
	}

	/**
	 * @effect ɾ���ļ�(��)����ʵ��
	 * @param filesPath
	 * @throws FtpException
	 */
	private void deleteDir(String[] filePaths) throws FtpException {
		for (String path : filePaths) {
			try {
				FTPFile[] listFiles = client.listFiles(toFTPString(path));
				if (listFiles.length == 1) {
					if (listFiles[0].isFile()) {
						String[] split = path.split("/");
						String last_str = split[split.length - 1];
						// 1 ��·������ľ���һ���ļ� pathĩβ���ļ��� ; ɾ�����ļ�,��ɾ�����ļ�֮���ļ���Ϊ��,��ɾ���ļ���
						if (listFiles[0].getName().equals(last_str)) {
							client.deleteFile(toFTPString(path));

						} else {
							// 2 ·���ļ����� ֻ��һ���ļ�; ɾ�����ļ��͸��ļ���
							client.deleteFile(toFTPString(path + "/" + listFiles[0].getName()));
							client.removeDirectory(toFTPString(path));
						}

					} else {
						// ��·��Ϊһ���ļ���
						String oneDir = path + "/" + listFiles[0].getName();
						// ��Ψһ�ļ��е��������ļ�(��)
						FTPFile[] newList = client.listFiles(toFTPString(oneDir));
						if (newList.length == 0) {
							// �����ļ���Ϊ��
							client.removeDirectory(toFTPString(oneDir));
						} else {
							// �����ļ��в�Ϊ��
							String[] nameArr = new String[newList.length];
							for (int j = 0; j < newList.length; j++) {
								nameArr[j] = oneDir + "/" + newList[j].getName();
							}
							deleteDir(nameArr);
							client.removeDirectory(toFTPString(oneDir));
						}

					}

				} else if (listFiles.length > 1) {
					// 3 ��·���кܶ��ļ����ļ���;��ݹ���ô˷���
					String[] files = new String[listFiles.length];
					for (int i = 0; i < listFiles.length; i++) {
						files[i] = path + "/" + listFiles[i].getName();
					}

					deleteDir(files);

				} else {
					// 4��·�������һ�����ļ��� (length == 0)
					client.removeDirectory(toFTPString(path));
				}

				client.removeDirectory(toFTPString(path));
			} catch (IOException e) {
				throw new FtpException(10, "FtpClientCore_Imp.deleteDir;=1; " + e.getMessage());
			}
		}
	}

	/**
	 * @effect ɾ���ļ�
	 * @param filesPath
	 * @return true ɾ���ɹ�;false ɾ��ʧ��
	 * @throws FtpException
	 */
	@Override
	public boolean deleteFiles(String[] filesPath) throws FtpException {
		if (examinClientStatus()) {
			boolean isSuccess = true;
			try {
				deleteDir(filesPath);
			} catch (FtpException e) {
				isSuccess = false;
				throw e;
			}
			return isSuccess;

		}

		return false;
	}

	/**
	 * @effect �ļ�(��)����
	 * @param path    �ļ�����·��;���治�� /
	 * @param filePth �ɵ��ļ�(��)��
	 * @param newName �µ��ļ�(��)��
	 * @return true�ɹ�;falseʧ��
	 * @throws FtpException
	 */
	@Override
	public boolean renameFile(String path, String oldName, String newName) throws FtpException {
		if (examinClientStatus()) {
			boolean isSuccess = true;
			if ((path == null) || path.equals("")) {
				try {
					client.rename(toFTPString(oldName), toFTPString(newName));
				} catch (Exception e) {
					isSuccess = false;
					throw new FtpException(0, "FtpClientCore_Imp.renameFile;=1;" + e.getMessage());
				}
			} else {
				try {
					client.rename(toFTPString(path + "/" + oldName), toFTPString(path + "/" + newName));
				} catch (Exception e) {
					isSuccess = false;
					throw new FtpException(0, "FtpClientCore_Imp.renameFile;=2;" + e.getMessage());
				}
			}

			return isSuccess;
		}
		return false;
	}

	/**
	 * @effect ��ǰ����Ŀ¼�½�һ���ļ���
	 * @param dirName �ļ�����
	 * @throws FtpException
	 */
	public void mkdirCurrentPath(String dirName) throws FtpException {
		if (examinClientStatus()) {
			try {
				client.makeDirectory(dirName);
			} catch (IOException e) {
				throw new FtpException(0, "FtpClientCore_Imp.mkdirCurrentPath;=1;" + e.getMessage());
			}
		}
	}

	/**
	 * @effect ���� �½��ļ���
	 * @param dirPath �ļ�ȫ·��
	 * @return true�ɹ�;falseʧ��
	 * @throws FtpException
	 */
	@Override
	public boolean mkdir(String dirPath) throws FtpException {
		if (examinClientStatus()) {
			boolean isSuccess = true;
			try {
				String start_dot = "";
				if (dirPath.startsWith("/")) {
					start_dot = "/";
				}

				String[] splitPath = dirPath.split("/");
				for (int i = 0; i < splitPath.length; i++) {
					String head = "";
					for (int j = 0; j < i; j++) {
						head += splitPath[j] + "/";
					}
					client.makeDirectory(start_dot + head + splitPath[i]);

				}

			} catch (IOException e) {
				isSuccess = false;
				throw new FtpException(0, "FtpClientCore_Imp.mkdir;=1;" + e.getMessage());
			}

			return isSuccess;
		}
		return false;
	}

	/**
	 * @effect ���ͻ��˺ͷ����������Ƿ��쳣
	 * @param client
	 * @return true-��������;false�����쳣
	 * @throws FtpException
	 */
	@Override
	public boolean examinClientStatus() throws FtpException {
		boolean isSucess = true;
		if (client != null) {
			if (client.isConnected()) {
				if (!FTPReply.isPositiveCompletion(client.getReplyCode())) {
					try {
						client.disconnect();
						return false;
					} catch (IOException e) {
						isSucess = false;
						throw new FtpException(2, "FtpClientCore_Imp.examinClientStatus;=1;" + e.getMessage());
					}

				}
			}

		}
		return isSucess;
	}

	/**
	 * @effect ��� �ض�·���µ������ļ����ļ��� �б�
	 * @param path ��ȡ��·��;null��ʾ��ǰ·��
	 * @return ��������ļ����ļ���
	 * @throws FtpException
	 */
	@Override
	public FTPFile[] getFiles(String path) throws FtpException {
		if (path == null) {
			try {
				return client.listFiles();
			} catch (IOException e) {
				e.printStackTrace();
				throw new FtpException(0, "FtpClientCore_Imp.getFiles;=1;" + e.getMessage());
			}
		} else {
			try {
				client.changeWorkingDirectory(toFTPString(path));
				return client.listFiles();
			} catch (IOException e) {
				throw new FtpException(0, "FtpClientCore_Imp.getFiles;=2;" + e.getMessage());
			}
		}
	}

	@Override
	public String getSystem() throws FtpException {
		if (examinClientStatus()) {
			if (client != null) {
				try {
					return client.getSystemType();
				} catch (IOException e) {
					throw new FtpException(0, "FtpClientCore_Imp.getSystem;=1;" + e.getMessage());
				}
			}
		}
		return null;
	}

	// ���ܲ���
	public static void main(String[] args) throws IOException, FtpException {
		FtpClientCore_Imp ftpU = new FtpClientCore_Imp();
		try {
			FTPClient ftp = ftpU.loginFtp("192.168.6.131", 21, "jinxiaozhi", "jinxiaozhi");
//			System.out.println(name.length + " ");
			// + name[0].getName());

			// 1 �ϴ�
//			ArrayList<File> list = new ArrayList<>();
//			list.add(new File("G:/�����"));
//			ftpU.uploadFiles(list, "up");
			// 2 ����
//			String[] files = { "1" };
//			ftpU.downloadFiles(files, "G:/");
			
			ftp.changeWorkingDirectory("Mount&Blade Warband Savegames/16thbeibanqiu");
			// ɾ��
			String[] file = { "sg00.sav" };
			boolean deleteFiles = ftpU.deleteFiles(file);
			System.out.println(deleteFiles);

			// �½��ļ���
//			ftpU.mkdir("/123/jin/xiaochi/zz/ssad");

			// ����
//			ftpU.renameFile("", "1", "helloWorld");
			
			Thread.sleep(15000);
			// ����ļ����б�
			
			FTPFile[] files = ftpU.getFiles(null);
			for (FTPFile f : files) {
				System.out.println(f.getName());
			}
				// ϵͳ����
//			String system = ftpU.getSystem();
//			System.out.println(system);
			
		} catch (FtpException | InterruptedException e) {
			e.printStackTrace();
		} finally {
//			try {
//				System.out.println("Ҫ�˳���");
//				ftpU.logoutFtp();
//			} catch (FtpException e) {
//				e.printStackTrace();
//			}
		
		}

	}

	@Override
	public String getCurrentPath() throws FtpException {
		if (examinClientStatus()) {
			try {
				return client.printWorkingDirectory();

			} catch (IOException e) {
				throw new FtpException(0, "FtpClientCore_Imp.getCurrentPath;=1;" + e.getMessage());
			}
		}
		return null;
	}

	/**
	 * @throws FtpException
	 * @effect ���ظ�Ŀ¼
	 */
	public void changeToParentDir() throws FtpException {
//		if(examinClientStatus()) {
		try {
			client.changeToParentDirectory();
		} catch (IOException e) {
			e.printStackTrace();
			throw new FtpException(0, "FtpClientCore_Imp.changeToParentDir;=1;" + e.getMessage());
		}
//		}
	}
}
