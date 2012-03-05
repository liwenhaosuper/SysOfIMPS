package com.imps.base;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;

import com.yz.net.NetMessage;


public class InputMessage extends AbstractMessage implements NetMessage {
	
	private String userName;
		
	private DataInputStream dis;
	
	public InputMessage(byte cmdtype,byte[] body)  //client
	{
		super(cmdtype,body);
	}

	public InputMessage(byte cmdtype,byte[] userName,byte[] body)//server
	{
		super(cmdtype,body);
		
		this.userName = new String(userName);
	}
	
	
	/**
	 * @param userId the userId to set
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}

	/**
	 * @return the userId
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * @param dis the dis to set
	 */
	public void setDis(DataInputStream dis) {
		this.dis = dis;
	}

	/**
	 * @return the dis
	 */
	public DataInputStream getDis() {
		return dis;
	}

	@Override
	public byte[] getContent() {
		throw new UnsupportedOperationException("���ܲ���...");
	}
	
	/**
	 * �������
	 */
	public DataInputStream getInputStream() {
		if(dis == null) {
			ByteArrayInputStream bis = new ByteArrayInputStream(body);
			dis = new DataInputStream(bis);
		}
		
		return dis;
	}
}

