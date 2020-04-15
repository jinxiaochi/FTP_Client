package core.interfaces;

import java.io.File;
import java.util.ArrayList;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import core.implement.FtpException;

/**
 * @author jinxiaochi
 * @date  2020��3��31��
 * @effect Ftp�ͻ��˺��Ĺ���
 */
/**
 * @author jinxiaochi
 * @date  2020��3��31��
 * @effect TODO
 */
public interface FtpClient_core {
	
	/** @effect ʵ����¼
	 * @param ftpHost ftp������ַ��ip
	 * @param port ���ӵĶ˿ں�
	 * @param username �˻�����
	 * @param password �˻�����
	 * @return FTPClient����
	 */
	public FTPClient loginFtp(String ftpHost,int port,String username,String password) throws FtpException ;
	
	
	/** @effect ������½
	 * @param ftpHost ftp������ַ��ip
	 * @param port ���ӵĶ˿ں�
	 * @return FTPClient����
	 */
	public FTPClient loginFtp(String ftpHost,int port) throws FtpException ;
	
	
	/** @effect ע����¼
	 */
	public void logoutFtp() throws FtpException ;
	
	/** @effect ���ͻ��˺ͷ����������Ƿ��쳣
	 * @param client
	 * @return true-��������;false�����쳣
	 * @throws FtpException 
	 */
	public boolean examinClientStatus() throws FtpException;
	
	/** @effect �ļ��ϴ�
	 * @param files
	 * @param serverPath
	 * @throws FtpException
	 * @return true�ϴ��ɹ�; false�ϴ�ʧ��
	 */
	public boolean uploadFiles (ArrayList<File> files,String serverPath)  throws FtpException ;
	
	/** @effect �ļ�����
	 * @param ftpfiles
	 * @param localPath
	 * @return true���سɹ�; false����ʧ��
	 * @throws FtpException
	 */
	public boolean downloadFiles(String[] filesPath, String localPath)  throws FtpException ;
	
	/** @effect ɾ���ļ�
	 * @param filesPath
	 * @return true ɾ���ɹ�;false ɾ��ʧ��
	 * @throws FtpException
	 */
	public boolean deleteFiles(String[] filesPath) throws FtpException;
	
	/** @effect �ļ�(��)����
	 * @param path
	 * @param oldName
	 * @param newName
	 * @return true�ɹ�;falseʧ��
	 * @throws FtpException
	 */
	public boolean renameFile(String path , String oldName,String newName) throws FtpException;
	
	/** @effect �½��ļ���
	 * @param dirName
	 * @return true�ɹ�;falseʧ��
	 * @throws FtpException
	 */
	public boolean mkdir(String dirName) throws FtpException;
	
	/** @effect ��� �ض�·���µ������ļ����ļ��� �б�
	 * @param path ��ȡ��·��;null��ʾ��ǰ·��
	 * @return ��������ļ����ļ���
	 * @throws FtpException
	 */
	public FTPFile[] getFiles(String path) throws FtpException;
		
	/** @effect ���ϵͳ����
	 * @return
	 */
	public String getSystem()  throws FtpException;
	
	/** @effect ��õ�ǰ·��
	 * @return
	 * @throws FtpException
	 */
	public String getCurrentPath() throws FtpException;
}
