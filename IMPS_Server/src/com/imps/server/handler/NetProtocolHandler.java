package com.imps.server.handler;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.ArrayList;
import java.util.List;


import com.imps.server.base.InputMessage;
import com.imps.server.net.IoSession;
import com.imps.server.net.NetMessage;
import com.imps.server.net.ProtocolHandler;


public class NetProtocolHandler implements ProtocolHandler {

	@Override
	public List<NetMessage> onData(ByteBuffer data, IoSession session)  {
		// TODO Auto-generated method stub
		ArrayList<NetMessage> list = null;
		while(true)
		{
			if(data.remaining()<2)
				break;
			/** 读取协议头*/
			
/*			//test
            Charset charset  =   null ;
            CharsetDecoder decoder  =   null ;
            CharBuffer charBuffer  =   null ;
            try 
            {
                  charset  =  Charset.forName( "UTF-8" );
                  decoder  =  charset.newDecoder();
                  charBuffer  =  decoder.decode(data);
                  System.out.println( " charBuffer= "   +  charBuffer);
                  System.out.println(charBuffer.toString());
            } 
            catch(Exception ex)
            {
                  ex.printStackTrace();
                          
            } */    
			byte[] tag = new byte[2]; 
			tag[0]=data.get();
			tag[1]=data.get();
			if(tag[0]!='O'||tag[1]!='K')
			{
				System.out.println("message tag error!tag should be 'OK' "+tag[0]+tag[1]);
				break;
			}
			if(data.remaining()<2)
			{
				System.out.println("data remaining less than 2");
				break;
			}
			//get length
			
			//o(︶︿︶)o 唉，调了那么久
			int contentlen = data.getInt();
			System.out.println("data.getInt():"+contentlen+" and data.remaining() is "+data.remaining());
			/**协议头读取完毕*/
			if(data.remaining() < contentlen) {
				break;
			}
			
			//获取协议内容说明
			
			byte[] content = new byte[(int)contentlen];
			data.get(content);
			ByteBuffer contentBuffer = ByteBuffer.wrap(content);
			contentBuffer.clear();
			byte cmdtype = contentBuffer.get();//获取命令类型
			long namesize = contentBuffer.getLong(); //获取username长度
			byte[] username = new byte[(int) namesize]; 
			contentBuffer.get(username);  //获取username
			String nm = "";
			try {
				nm = new String(username,"gb2312");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			byte[] msgbody = new byte[contentBuffer.remaining()];   //消息体
			contentBuffer.get(msgbody);
			
			InputMessage msg = new InputMessage(cmdtype,nm,msgbody); 
			
			if(list == null) {
				list = new ArrayList<NetMessage>();
			}
	
			list.add(msg);
			
		}
		
		return list;
	}


}



