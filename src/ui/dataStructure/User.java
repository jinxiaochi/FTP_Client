package ui.dataStructure;

/**
 * @author jinxiaochi
 * @date  2020年4月10日
 * @effect 记录用户名和密码的类/对象
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
