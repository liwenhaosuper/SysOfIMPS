package com.imps.media.video.core.SocketImpl;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import com.yz.net.IoSession;
import com.yz.net.NetMessage;
import com.yz.net.ProtocolHandler;

public class SocketProtocolHandler implements ProtocolHandler{

	@Override
	public List<NetMessage> onData(ByteBuffer data, IoSession session) {
		ArrayList<NetMessage> list = null;
		while(true)
		{
			if(data.remaining()<2)
				break;
			byte[] tag = new byte[2]; 
			tag[0]=data.get();
			tag[1]=data.get();
			byte type ;
			if(tag[0]=='O'||tag[1]=='K')
			{
				type = VideoMsgHeader.OK;
				if(data.remaining()<2)
				{
					System.out.println("data remaining less than 2");
					break;
				}
				long contentlen = data.getLong();
				/**协议头读取完毕*/
				if(data.remaining() < contentlen) {
					break;
				}
				byte[] content = new byte[(int)contentlen];
				data.get(content);
				InVideoMessage in = new InVideoMessage(type,content);
				if(list == null) {
					list = new ArrayList<NetMessage>();
				}
		
				list.add(in);
			}
			else if(tag[0]=='B'||tag[1]=='B')
			{
				///BYE BYE
				type = VideoMsgHeader.BYE;
				InVideoMessage in = new InVideoMessage(type,null);
				if(list == null) {
					list = new ArrayList<NetMessage>();
				}	
				list.add(in);
				break;
			}
			
		}
		return list;
	}

}
