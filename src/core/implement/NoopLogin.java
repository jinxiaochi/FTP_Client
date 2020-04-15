package core.implement;

import org.apache.commons.net.ftp.FTPClient;

/**
 * @author jinxiaochi
 * @date 2020年4月2日
 * @effect 维持登录状态的子线程;因为ftp连接无操作900秒之后自动断开;
 */
public class NoopLogin extends Thread {

	private FTPClient ftp;
	//是否继续维持连接;(保险措施)
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
						// 等待850秒后发送noop命令维持连接;改为10秒
						Thread.sleep(10000);
						ftp.sendNoOp();
					} catch (InterruptedException e1) {
						return;
					} catch (Exception e) {
						e.printStackTrace();
						new FtpException(5,"连接持续NoopLogin.run;"+e.getMessage());
					}

				}
			}
		}

	}

	/**
	 * @effect 停止维持连接的线程
	 */
	public void stopNoop() {
		this.keepOn = false;
		interrupt();
	}
}
