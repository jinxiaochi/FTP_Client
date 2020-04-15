package core.implement;

/**
 * @author jinxiaochi
 * @date 2020年3月31日
 * @effect 自定义异常类
 */
public class FtpException extends Exception {
	//错误号
	private int errorId;
	// errID对应的错误信息
	private final String[] errMessage = {
			"0-未知异常",
			"1-连接服务器异常", 
			"2-登录账号出现异常", 
			"3-注销账号出现异常",
			"4-未登录异常",
			"5-维持登录出现异常",
			"6-本地文件未找到",
			"7-文件传输异常",
			"8-文件夹创建异常",
			"9-不支持iso-8859-1编码方式",
			"10-文件删除出现异常"
	};
	//出现错误的位置
	private String location;
	private static final long serialVersionUID = 1L;

	/**
	 * @param errorID 错误ID,详见errMessage
	 */
	public FtpException(int errorID,String local) {
		this.errorId = errorID;
		this.location = local;
		System.out.println(this.errMessage[errorID]+": "+local);
	}
	
	public String getErrorInfo() {
		return super.getMessage()+";\n "+this.errMessage[errorId]+";\nLocation = "+location+";";
	}
}
