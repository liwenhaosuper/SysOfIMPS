
/*
 * Author: liwenhaosuper
 * Date: 2011/5/18
 * Description:
 *     entity class of user
 */


package com.imps.server.main.basetype;

import java.sql.SQLException;

import com.imps.server.manager.UserManager;




public class User {
	

	private long userid;   //user id
	private String username;  //user name
	private String password;
	private int gender;
	private String email;
	private byte status = userStatus.OFFLINE;
	public long lastAccessTime = System.currentTimeMillis(); //最近访问时间
	
	/**IoSessionId*/
	private Integer sessionId;
	
	public User()
	{
		userid = -1;
		username="";
		password="";
		gender = -1;
		email = "";
		sessionId = new Integer(-1);
	}
	
	//getter and setter
	public void setUserid(long userid) {
		this.userid = userid;
	}
	public long getUserid() {
		return userid;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getUsername() {
		return username;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getPassword() {
		return password;
	}
	public void setGender(int gender) {
		this.gender = gender;
	}
	public int getGender() {
		return gender;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getEmail() {
		return email;
	}
	
	public void setStatus(byte status) {
		this.status = status;
	}
	public  byte getStatus() {
		return status;
	}
	public void setSessionId(Integer sessionId) {
		this.sessionId = sessionId;
	}
	public Integer getSessionId() {
		return sessionId;
	}
	
	//constructor function
	public User(Integer id,String name,String password)
	{
		this.userid=id;
		this.username=name;
		this.password=password;
	}
	public User(Integer userid,String username,String password,int agender,String email)
	{
		this.userid = userid;
		this.username = username;
		this.password = password;
		this.gender = agender;
		this.email = email;
	}
	
	//get friend list
	public User[] getFriendList() throws SQLException
	{
		UserManager manager = UserManager.getInstance();
		User[] users = manager.getFriendlistInStatus(username);
		for(int i=0;i<users.length;i++)
		{
		//	if(manager.users[i].getUsername())
		}
		return users;
	}
	
	
	/**
	 * get online friend list
	 */
	public User[] getOnlineFriendList() throws SQLException
	{
		UserManager manager = UserManager.getInstance();
		User[] users = manager.getOnlineFriendlist(username);
		if(users==null)
			return null;
		for(int i=0;i<users.length;i++)
		{
		//	if(manager.users[i].getUsername())
		}
		return users;
	}
}
