
/*
 * Author: liwenhaosuper
 * Date: 2011/5/18
 * Description:
 *     entity class of user
 */


package com.imps.basetypes;


public class User {
	private long userid;   //user id
	private String username;  //user name
	private String password;
	private int gender; //1为男 0为女
	private String email;
	private byte status;
	private String loctime; 
	private double locX;
	private double locY;
	public long lastAccessTime = System.currentTimeMillis(); 
	/**IoSessionId*/
	private long sessionId;
	private String description;
	private byte[] contactIcon;
	private String groupName;
	
	public User()
	{
		userid = -1;
		username="";
		password="";
		gender = -1;
		email = "";
		sessionId = -1;
		groupName="未分组";
		description = "";
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
	public void setSessionId(long sessionId) {
		this.sessionId = sessionId;
	}
	public long getSessionId() {
		return sessionId;
	}
	
	//constructor function
	public User(long id,String name,String password)
	{
		this.userid=id;
		this.username=name;
		this.password=password;
	}
	public User(long userid,String username,String password,int agender,String email)
	{
		this.userid = userid;
		this.username = username;
		this.password = password;
		this.gender = agender;
		this.email = email;
	}
	
	public void setLocX(double locX) {
		this.locX = locX;
	}

	public double getLocX() {
		return locX;
	}

	public void setLoctime(String loctime) {
		this.loctime = loctime;
	}

	public String getLoctime() {
		return loctime;
	}

	public void setLocY(double locY) {
		this.locY = locY;
	}

	public double getLocY() {
		return locY;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

	public void setContactIcon(byte[] contactIcon) {
		this.contactIcon = contactIcon;
	}

	public byte[] getContactIcon() {
		return contactIcon;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getGroupName() {
		return groupName;
	}
}
