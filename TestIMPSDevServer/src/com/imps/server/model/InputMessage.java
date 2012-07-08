package com.imps.server.model;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;

public class InputMessage {
	private DataInputStream dis;
	private byte[] content;
	public InputMessage(byte[] content){
		this.content = content;
	}
	public void setContent(byte[] content){
		this.content = content;
	}
	public DataInputStream getInputStream() {
		if(dis == null) {
			ByteArrayInputStream bis = new ByteArrayInputStream(content);
			dis = new DataInputStream(bis);
		}
		return dis;
	}
}
