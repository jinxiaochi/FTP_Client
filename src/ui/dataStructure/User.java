package ui.dataStructure;

/**
 * @author jinxiaochi
 * @date  2020��4��10��
 * @effect ��¼�û������������/����
 */
public class User {
	private String username = null;
	private String passwd = null;
	public String getUsername() {
		return username;
	}
	public String getPasswd() {
		return passwd;
	}
	
	public User(String username,String password) {
		this.username = username;
		this.passwd = password;
	}
	@Override
	public String toString() {
		return "User [username=" + username + ", passwd=" + passwd + "]";
	}
	
	
}
