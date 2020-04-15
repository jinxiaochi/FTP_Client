package core.interfaces;

import java.io.File;
import java.util.ArrayList;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import core.implement.FtpException;

/**
 * @author jinxiaochi
 * @date  2020年3月31日
 * @effect Ftp客户端核心功能
 */
/**
 * @author jinxiaochi
 * @date  2020年3月31日
 * @effect TODO
 */
public interface FtpClient_core {
	
	/** @effect 实名登录
	 * @param ftpHost ftp主机地址或ip
	 * @param port 连接的端口号
	 * @param username 账户名称
	 * @param password 账户密码
	 * @return FTPClient对象
	 */
	public FTPClient loginFtp(String ftpHost,int port,String username,String password) throws FtpException ;
	
	
	/** @effect 匿名登陆
	 * @param ftpHost ftp主机地址或ip
	 * @param port 连接的端口号
	 * @return FTPClient对象
	 */
	public FTPClient loginFtp(String ftpHost,int port) throws FtpException ;
	
	
	/** @effect 注销登录
	 */
	public void logoutFtp() throws FtpException ;
	
	/** @effect 检测客户端和服务器连接是否异常
	 * @param client
	 * @return true-连接正常;false连接异常
	 * @throws FtpException 
	 */
	public boolean examinClientStatus() throws FtpException;
	
	/** @effect 文件上传
	 * @param files
	 * @param serverPath
	 * @throws FtpException
	 * @return true上传成功; false上传失败
	 */
	public boolean uploadFiles (ArrayList<File> files,String serverPath)  throws FtpException ;
	
	/** @effect 文件下载
	 * @param ftpfiles
	 * @param localPath
	 * @return true下载成功; false下载失败
	 * @throws FtpException
	 */
	public boolean downloadFiles(String[] filesPath, String localPath)  throws FtpException ;
	
	/** @effect 删除文件
	 * @param filesPath
	 * @return true 删除成功;false 删除失败
	 * @throws FtpException
	 */
	public boolean deleteFiles(String[] filesPath) throws FtpException;
	
	/** @effect 文件(夹)改名
	 * @param path
	 * @param oldName
	 * @param newName
	 * @return true成功;false失败
	 * @throws FtpException
	 */
	public boolean renameFile(String path , String oldName,String newName) throws FtpException;
	
	/** @effect 新建文件夹
	 * @param dirName
	 * @return true成功;false失败
	 * @throws FtpException
	 */
	public boolean mkdir(String dirName) throws FtpException;
	
	/** @effect 获得 特定路径下的所有文件和文件夹 列表
	 * @param path 获取的路径;null表示当前路径
	 * @return 获得所有文件和文件夹
	 * @throws FtpException
	 */
	public FTPFile[] getFiles(String path) throws FtpException;
		
	/** @effect 获得系统类型
	 * @return
	 */
	public String getSystem()  throws FtpException;
	
	/** @effect 获得当前路径
	 * @return
	 * @throws FtpException
	 */
	public String getCurrentPath() throws FtpException;
}
