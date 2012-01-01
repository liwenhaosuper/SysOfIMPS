package com.imps.media.video.core.SocketImpl;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;

import com.yz.net.NetMessage;

public class InVideoMessage implements NetMessage{

	private DataInputStream dis;
	private byte[] body;
	private byte type;
	public InVideoMessage(byte type,byte[] data)
	{
		body = data;
		this.setType(type);
	}
	@Override
	public byte[] getContent() {
		// TODO Auto-generated method stub
		return body;
	}
	
	/**
	 * ªÒµ√ ‰¡˜
	 */
	public DataInputStream getInputStream() {
		if(dis == null) {
			ByteArrayInputStream bis = new ByteArrayInputStream(body);
			dis = new DataInputStream(bis);
		}
		return dis;
	}
	public void setType(byte type) {
		this.type = type;
	}
	public byte getType() {
		return type;
	}

}
