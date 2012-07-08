
/*
 * Author: liwenhaosuper
 * Description:
 *     output message define
 */

package com.imps.model;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

public class OutputMessage{
	
	private DataOutputStream dos;
	private ByteArrayOutputStream bos;
	private byte[] content;

	public OutputMessage() {
		bos = new ByteArrayOutputStream(512);
		dos = new DataOutputStream(bos);
	}
	public OutputMessage(String tag,byte cmdtype){
		bos = new ByteArrayOutputStream(512);
		dos = new DataOutputStream(bos);
	}	
	public DataOutputStream getOutputStream() {
		return dos;
	}
	public byte[] getBody() {
		return bos.toByteArray();
	}
	/**
	 * wrapper the content
	 */
	public byte[] build() {
		if(content == null) {
			byte[] _body = getBody();
			content = new byte[2 + 4+ _body.length];
			content[0] = 'O';
			content[1] = 'K';
			content[2] = (byte) ((_body.length) >>> 24 & 0xFF);
			content[3] = (byte) ((_body.length) >>> 16 & 0xFF);
			content[4] = (byte) ((_body.length) >>> 8 & 0xFF);
			content[5] = (byte) ((_body.length) >>> 0 & 0xFF);
			System.arraycopy(_body, 0, content, 6, _body.length);
		}	
		return content;
	}

}

