package com.imps.basetypes;

import java.text.SimpleDateFormat;

public class UserMessage {

	private String content;
	private String time;
	private String friend;
	private int    dir;

	public UserMessage(String content, String time, String friend, int dir) {
		this.content = content;
		this.time = time;
		this.friend = friend;
		this.dir = dir;
	}

	public UserMessage() {
		setTime((new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(new java.util.Date()));
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getFriend() {
		return friend;
	}

	public void setFriend(String friend) {
		this.friend = friend;
	}

	public int getDir() {
		return dir;
	}

	public void setDir(int dir) {
		this.dir = dir;
	}

}
