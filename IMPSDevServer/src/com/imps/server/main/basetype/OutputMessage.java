
/*
 * Author: liwenhaosuper
 * Date: 2011/5/18
 * Description:
 *     output message define
 */

package com.imps.server.main.basetype;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

public class OutputMessage extends AbstractMessage {
	
	private DataOutputStream dos;
	private ByteArrayOutputStream bos;
	
	private boolean isCanUseOutputStream = true;
	
	private byte[] content;

	public OutputMessage(byte cmdtype) {
		super("OK".getBytes(),cmdtype, null);
		bos = new ByteArrayOutputStream(512);
		dos = new DataOutputStream(bos);
	}
	public OutputMessage(String tag,byte cmdtype){
		super(tag.getBytes(),cmdtype, null);
		bos = new ByteArrayOutputStream(512);
		dos = new DataOutputStream(bos);
	}
	
	
	public DataOutputStream getOutputStream() {
		if(!isCanUseOutputStream) {
			throw new UnsupportedOperationException("不能操作");
		}
		return dos;
	}
	
	
	public byte[] getBody() {
		if(body == null) {
			byte[] bosdata = bos.toByteArray();
			body = new byte[1 + bosdata.length];
			body[0] = cmdtype;
			System.arraycopy(bosdata, 0, body, 1, bosdata.length);
		}
		
		isCanUseOutputStream = false;
		
		return body;
	}

	public byte[] build() {
		if(content == null) {
			byte[] _body = getBody();
			content = new byte[2 + 4+ _body.length];
			content[0] = tag[0];
			content[1] = tag[1];
			content[2] = (byte) ((_body.length) >>> 24 & 0xFF);
			content[3] = (byte) ((_body.length) >>> 16 & 0xFF);
			content[4] = (byte) ((_body.length) >>> 8 & 0xFF);
			content[5] = (byte) ((_body.length) >>> 0 & 0xFF);
			System.arraycopy(_body, 0, content, 6, _body.length);
		}	
		return content;
	}

}

