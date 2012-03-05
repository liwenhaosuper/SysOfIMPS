
/*
 * Author: liwenhaosuper
 * Date: 2011/5/18
 * Description:
 *     output message define
 */

package com.imps.server.base;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

import com.imps.server.net.NetMessage;

public class OutputMessage extends AbstractMessage implements NetMessage {
	
	private DataOutputStream dos;
	private ByteArrayOutputStream bos;
	
	private boolean isCanUseOutputStream = true;
	
	private byte[] content;

	public OutputMessage(byte cmdtype) {
		super(cmdtype, null);
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
			
			
			//System.out.println("bodysize:"+bosdata.length+" body:"+bosdata);
			
/*			System.out.println("bodysize:"+body.length+" body:"+body);
			byte[] ddd = new byte[4];ddd[0]=body[1];ddd[1]=body[2];ddd[2]=body[3];ddd[3]=body[4];
			String ttt = new String(ddd);
			System.out.println(ttt);
			
			byte[] ee = new byte[2];
			ee[0] = body[5];ee[1]=body[6];
			ttt = new String(ee);
			System.out.println(ttt);*/
			//String d = new String(bosdata);
			
			//System.out.println(d.length()+"  "+d);
		}
		
		isCanUseOutputStream = false;
		
		return body;
	}

	@Override
	public byte[] getContent() {
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
/*			content[_body.length+6] = 'K';
			content[_body.length+7] = 'O';*/
			//System.out.println("content[2]:"+content[2]+" content[3]:"+content[3]);
			//System.out.println(_body+" : "+_body.length);

		}
		
		return content;
	}

}

