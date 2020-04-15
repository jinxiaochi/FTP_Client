package core.implement;

/**
 * @author jinxiaochi
 * @date 2020��3��31��
 * @effect �Զ����쳣��
 */
public class FtpException extends Exception {
	//�����
	private int errorId;
	// errID��Ӧ�Ĵ�����Ϣ
	private final String[] errMessage = {
			"0-δ֪�쳣",
			"1-���ӷ������쳣", 
			"2-��¼�˺ų����쳣", 
			"3-ע���˺ų����쳣",
			"4-δ��¼�쳣",
			"5-ά�ֵ�¼�����쳣",
			"6-�����ļ�δ�ҵ�",
			"7-�ļ������쳣",
			"8-�ļ��д����쳣",
			"9-��֧��iso-8859-1���뷽ʽ",
			"10-�ļ�ɾ�������쳣"
	};
	//���ִ����λ��
	private String location;
	private static final long serialVersionUID = 1L;

	/**
	 * @param errorID ����ID,���errMessage
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
