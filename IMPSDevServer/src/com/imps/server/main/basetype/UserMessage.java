package com.imps.server.main.basetype;

import java.text.SimpleDateFormat;

public class UserMessage {
	private int m_id;
	private int from;
	private int to;
	private String time;
	private String msg;

	public UserMessage() {
		setTime((new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(new java.util.Date()));
	}

	public UserMessage(int m_id, int to, int from, String time, String msg) {
		super();
		this.m_id = m_id;
		this.from = from;
		this.to = to;
		this.time = time;
		this.msg = msg;
	}

	public int getFrom() {
		return from;
	}

	public void setFrom(int from) {
		this.from = from;
	}

	public int getTo() {
		return to;
	}

	public void setTo(int to) {
		this.to = to;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public int getM_id() {
		return m_id;
	}

	public void setM_id(int m_id) {
		this.m_id = m_id;
	}
}
