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
 * @date 2020年3月31日
 * @effect Ftp客户端核心功能实现类 注: 暂时省略读写权限问题; 默认账户 可读可写
 */
public class FtpClientCore_Imp implements FtpClient_core {
	private FTPClient client = null;
	private NoopLogin noopLogin = null;

	/**
	 * @effect 实名登录
	 * @param ftpHost  ftp主机地址或ip
	 * @param port     连接的端口号
	 * @param username 账户名称
	 * @param password 账户密码
	 * @return FTPClient对象
	 */
	@Override
	public FTPClient loginFtp(String ftpHost, int port, String username, String password) throws FtpException {
		this.client = new FTPClient();
		// 传输中6 分钟一次noop
		client.setControlKeepAliveTimeout(360);
		// 连接ftp服务器
		try {
			client.connect(ftpHost, port);
		} catch (Exception e1) {
			throw new FtpException(1, "实名FtpClientCore_Imp.logingFtp;=1; " + e1.getMessage());
		}

		// 登录ftp服务器
		try {
			client.login(username, password);
			// 确定密码正确
			if (!FTPReply.isPositiveCompletion(client.getReplyCode())) {
				client.disconnect();
				throw new FtpException(2, "返回状态码异常");
			}
			// 将客户端编码方式和服务端一致
			Charset charset = client.getCharset();
			client.setControlEncoding(charset.displayName());
			client.enterLocalPassiveMode();// 设置被动模式
			client.setFileType(FTP.BINARY_FILE_TYPE);// 设置传输的模式
			noopLogin = new NoopLogin(client);
			noopLogin.start();
			return client;
		} catch (IOException e) {
			throw new FtpException(2, "实名FtpClientCore_Imp.logingFtp;=2;" + e.getMessage());
		}
	}

	/**
	 * @effect 匿名登陆
	 * @param ftpHost ftp主机地址或ip
	 * @param port    连接的端口号
	 * @return FTPClient对象
	 */
	@Override
	public FTPClient loginFtp(String ftpHost, int port) throws FtpException {
		client = new FTPClient();
		client.setControlKeepAliveTimeout(360);
		try {
			client.connect(ftpHost, port);
		} catch (Exception e) {
			throw new FtpException(1, "匿名FtpClientCore_Imp.logingFtp;=1; " + e.getMessage());
		}

		try {
			client.login("anonymous", "898681619@qq.com");
			if (!FTPReply.isPositiveCompletion(client.getReplyCode())) {
				client.disconnect();
				throw new FtpException(2, "返回状态码异常");
			}
			// 将客户端编码方式和服务端一致
			Charset charset = client.getCharset();
			client.setControlEncoding(charset.displayName());
			client.enterLocalPassiveMode();// 设置被动模式
			client.setFileType(FTP.BINARY_FILE_TYPE);// 设置传输的模式

			noopLogin = new NoopLogin(client);
			noopLogin.start();
			return client;
		} catch (IOException e) {
			throw new FtpException(2, "匿名FtpClientCore_Imp.logingFtp;=2;" + e.getMessage());
		}

	}

	/**
	 * @effect 注销登录并断开连接
	 */
	@Override
	public void logoutFtp() throws FtpException {
		if (client != null) {
			// 若已经断开连接,则无需注销
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
	 * @effect 将字符串转化为Ftp协议要求的iso-8859-1编码格式
	 * @param s 要转化的字符串
	 * @return 转化后的字符串
	 * @throws UnsupportedEncodingException
	 */
	public String toFTPString(String s) throws UnsupportedEncodingException {
		return new String(s.getBytes(), "iso-8859-1");// iso-8859-1
	}

	/*
	 * @effect 上传整个文件夹
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
		// 1 上传目录
		ArrayList<File> diretories = fileTree.getDirectories();
		// 处理路径的 / 问题; 处理 非 /和""目录
		if (!(serverPath.equals("") || serverPath.equals("/"))) {
			serverPath += "/";
		}
		// A. 此目录下没有文件夹; 不进行递归
		if (diretories.size() == 0) {
			try {
				// ftp无法级联创建目录
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

		// B. 此目录下有文件夹
		for (File dir : diretories) {
			// 1.1 创建的 文件夹路径
			String dirPath = null;
			dirPath = serverPath + dir.getName();

			try {
				uploadDir(new FileLocalTree(dir), dirPath);
			} catch (Exception e) {
				throw new FtpException(7, "FtpClientCore_Imp.uploadDir;=2;" + e.getMessage());
			}
			// }

		} // 上传文件夹结束

		// 2 上传文件
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
	 * @effect 文件上传;
	 * @param files
	 * @param serverPath
	 * @throws FtpException
	 * @return true上传成功; false上传失败
	 * @throws UnsupportedEncodingException
	 */
	@Override
	public boolean uploadFiles(ArrayList<File> files, String serverPath) throws FtpException {
		if (examinClientStatus()) {
			// 处理路径的 / 问题; 处理 非 /和""目录
			if (!(serverPath.equals("") || serverPath.equals("/"))) {
				serverPath += "/";
			}

			boolean isSuccess = true;
			for (File f : files) {
				// 处理文件夹
				if (f.isDirectory()) {
					try {
						uploadDir(new FileLocalTree(f), serverPath + f.getName());
					} catch (Exception e) {
						isSuccess = false;
						throw new FtpException(7, "FtpClientCore_Imp.uploadFiles;=1;" + e.getMessage());
					}
				} else {

					// 处理文件
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
	 * @effect 下载文件和文件夹的具体实现核心代码
	 */
	private void downLoadDir(String[] files, String localPath) throws FtpException {
		// 若本地文件下载 根目录不存在则创建
		File rootLocalPath = new File(localPath);
		if (!rootLocalPath.exists()) {
			rootLocalPath.mkdirs();
		}
		for (String downFile : files) {
			try {
				FTPFile[] file = client.listFiles(toFTPString(downFile));
				// 对文件和文件夹的 区分
				if (file.length == 1) {
					if (file[0].isFile()) {
						// 判断downFile的路径是不是文件
						String[] split = downFile.split("/");
						// a 此为文件 则路径最后的名称和文件名相同
						if (file[0].getName().equals(split[split.length - 1])) {
							client.retrieveFile(toFTPString(downFile),
									new FileOutputStream(localPath + "/" + file[0].getName()));
						} else {
							// b 此为本文件夹下的单独文件
							File newDir = new File(localPath + "/" + split[split.length - 1]);
							if (!newDir.exists()) {
								newDir.mkdirs();
							}
							client.retrieveFile(toFTPString(downFile + "/" + file[0].getName()),
									new FileOutputStream(localPath + "/" + newDir.getName() + "/" + file[0].getName()));
						}

					} else {
						// 判断是文件夹则本地创建文件夹 得到一个文件夹
						String[] strPath = downFile.split("/");
						String localSubDir = localPath + "/" + strPath[strPath.length - 1];
						File dir_only = new File(localSubDir);
						if (!dir_only.exists()) {
							dir_only.mkdirs();
						}

						// 对文件夹的子 文件(夹) 进行下载
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
					// 修改 下载的路径
					String[] pathAppend = downFile.split("/");
					downLoadDir(names, localPath + "/" + pathAppend[pathAppend.length - 1]);
				} else {
					// 此为空目录
					String[] str = downFile.split("/");
					new File(localPath + "/" + str[str.length - 1]).mkdirs();

				}
			} catch (IOException e) {
				new FtpException(7, "FtpClientCore_Imp.downLoadDir;=1;" + e.getMessage());
			}

		}
	}

	/**
	 * @effect 文件下载
	 * @param files     文件(夹)路径
	 * @param localPath 本地下载目录
	 * @return true下载成功; false下载失败
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
	 * @effect 删除文件(夹)具体实现
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
						// 1 此路径代表的就是一个文件 path末尾即文件名 ; 删除此文件,若删除此文件之后文件夹为空,则删除文件夹
						if (listFiles[0].getName().equals(last_str)) {
							client.deleteFile(toFTPString(path));

						} else {
							// 2 路径文件夹下 只有一个文件; 删除此文件和父文件夹
							client.deleteFile(toFTPString(path + "/" + listFiles[0].getName()));
							client.removeDirectory(toFTPString(path));
						}

					} else {
						// 此路径为一个文件夹
						String oneDir = path + "/" + listFiles[0].getName();
						// 此唯一文件夹的所有子文件(夹)
						FTPFile[] newList = client.listFiles(toFTPString(oneDir));
						if (newList.length == 0) {
							// 若此文件夹为空
							client.removeDirectory(toFTPString(oneDir));
						} else {
							// 若此文件夹不为空
							String[] nameArr = new String[newList.length];
							for (int j = 0; j < newList.length; j++) {
								nameArr[j] = oneDir + "/" + newList[j].getName();
							}
							deleteDir(nameArr);
							client.removeDirectory(toFTPString(oneDir));
						}

					}

				} else if (listFiles.length > 1) {
					// 3 此路径有很多文件和文件夹;则递归调用此方法
					String[] files = new String[listFiles.length];
					for (int i = 0; i < listFiles.length; i++) {
						files[i] = path + "/" + listFiles[i].getName();
					}

					deleteDir(files);

				} else {
					// 4此路径代表的一个空文件夹 (length == 0)
					client.removeDirectory(toFTPString(path));
				}

				client.removeDirectory(toFTPString(path));
			} catch (IOException e) {
				throw new FtpException(10, "FtpClientCore_Imp.deleteDir;=1; " + e.getMessage());
			}
		}
	}

	/**
	 * @effect 删除文件
	 * @param filesPath
	 * @return true 删除成功;false 删除失败
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
	 * @effect 文件(夹)改名
	 * @param path    文件所在路径;后面不接 /
	 * @param filePth 旧的文件(夹)名
	 * @param newName 新的文件(夹)名
	 * @return true成功;false失败
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
	 * @effect 当前工作目录新建一个文件夹
	 * @param dirName 文件夹名
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
	 * @effect 联级 新建文件夹
	 * @param dirPath 文件全路径
	 * @return true成功;false失败
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
	 * @effect 检测客户端和服务器连接是否异常
	 * @param client
	 * @return true-连接正常;false连接异常
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
	 * @effect 获得 特定路径下的所有文件和文件夹 列表
	 * @param path 获取的路径;null表示当前路径
	 * @return 获得所有文件和文件夹
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

	// 功能测试
	public static void main(String[] args) throws IOException, FtpException {
		FtpClientCore_Imp ftpU = new FtpClientCore_Imp();
		try {
			FTPClient ftp = ftpU.loginFtp("192.168.6.131", 21, "jinxiaozhi", "jinxiaozhi");
//			System.out.println(name.length + " ");
			// + name[0].getName());

			// 1 上传
//			ArrayList<File> list = new ArrayList<>();
//			list.add(new File("G:/表情包"));
//			ftpU.uploadFiles(list, "up");
			// 2 下载
//			String[] files = { "1" };
//			ftpU.downloadFiles(files, "G:/");
			
			ftp.changeWorkingDirectory("Mount&Blade Warband Savegames/16thbeibanqiu");
			// 删除
			String[] file = { "sg00.sav" };
			boolean deleteFiles = ftpU.deleteFiles(file);
			System.out.println(deleteFiles);

			// 新建文件夹
//			ftpU.mkdir("/123/jin/xiaochi/zz/ssad");

			// 改名
//			ftpU.renameFile("", "1", "helloWorld");
			
			Thread.sleep(15000);
			// 获得文件夹列表
			
			FTPFile[] files = ftpU.getFiles(null);
			for (FTPFile f : files) {
				System.out.println(f.getName());
			}
				// 系统类型
//			String system = ftpU.getSystem();
//			System.out.println(system);
			
		} catch (FtpException | InterruptedException e) {
			e.printStackTrace();
		} finally {
//			try {
//				System.out.println("要退出了");
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
	 * @effect 返回父目录
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
