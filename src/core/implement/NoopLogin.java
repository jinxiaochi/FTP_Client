package core.implement;

import org.apache.commons.net.ftp.FTPClient;

/**
 * @author jinxiaochi
 * @date 2020��4��2��
 * @effect ά�ֵ�¼״̬�����߳�;��Ϊftp�����޲���900��֮���Զ��Ͽ�;
 */
public class NoopLogin extends Thread {

	private FTPClient ftp;
	//�Ƿ����ά������;(���մ�ʩ)
	private boolean keepOn = true;
	public NoopLogin(FTPClient client) {
		this.ftp = client;
	}

	@Override
	public void run() {
		while (keepOn) {
			if (ftp != null) {
				if (ftp.isConnected()) {
					try {
						// �ȴ�850�����noop����ά������;��Ϊ10��
						Thread.sleep(10000);
						ftp.sendNoOp();
					} catch (InterruptedException e1) {
						return;
					} catch (Exception e) {
						e.printStackTrace();
						new FtpException(5,"���ӳ���NoopLogin.run;"+e.getMessage());
					}

				}
			}
		}

	}

	/**
	 * @effect ֹͣά�����ӵ��߳�
	 */
	public void stopNoop() {
		this.keepOn = false;
		interrupt();
	}
}
