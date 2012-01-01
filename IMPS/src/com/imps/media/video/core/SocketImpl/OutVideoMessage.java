package com.imps.media.video.core.SocketImpl;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

import com.yz.net.NetMessage;


public class OutVideoMessage implements NetMessage{
    
	private DataOutputStream dos;
	private ByteArrayOutputStream bos;
	private byte[] content;
	private byte type;
	public OutVideoMessage(byte type)
	{
		bos = new ByteArrayOutputStream(8080);
		dos = new DataOutputStream(bos);
		this.type = type;
	}
	@Override
	public byte[] getContent() {
		// TODO Auto-generated method stub
		if(content == null) {
			byte[] _body  = bos.toByteArray();	
			content = new byte[2 + 2 + _body.length];
			if(VideoMsgHeader.OK == type)
			{
				content[0] = 'O';
				content[1] = 'K';
			}
			else{
				content[0] = 'B';
				content[1] = 'B';
			}
				content[2] = (byte) ((_body.length) >>> 8 & 0xFF);
				content[3] = (byte) ((_body.length) >>> 0 & 0xFF);
				System.arraycopy(_body, 0, content, 4, _body.length);
			
		}		
		return content;
	}
	
	public DataOutputStream getOutputStream() {
		return dos;
	}

}
