package com.imps.handler;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;


import com.imps.base.InputMessage;
import com.yz.net.IoSession;
import com.yz.net.NetMessage;
import com.yz.net.ProtocolHandler;


public class NetProtocolHandler implements ProtocolHandler {

	@Override
	public List<NetMessage> onData(ByteBuffer data, IoSession session) {
		// TODO Auto-generated method stub
		ArrayList<NetMessage> list = null;
		while(true)
		{
			if(data.remaining()<2)
				break;
			/** ��ȡЭ��ͷ*/    
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
			
			//o(����)o ����������ô��
			//��ȡ��Ϣ����
			int contentlen = data.getInt();
			/**Э��ͷ��ȡ���*/
			
			System.out.println("data.getInt():"+contentlen);
			System.out.println(data.position());
			System.out.println(data.limit());
			System.out.println("data.remaining():"+data.remaining());
			if(data.remaining() < contentlen) {
				break;
			}
			
			//��ȡЭ������			
			byte[] content = new byte[(int)contentlen];
			data.get(content);
			
			System.out.println("bodysize:"+content.length+" body:"+content);
			ByteBuffer contentBuffer = ByteBuffer.wrap(content);
			contentBuffer.clear();
			System.out.println("contentBuffer size:"+contentBuffer.limit());
			System.out.println("contentBuffer:"+contentBuffer);
			byte cmdtype = contentBuffer.get();//��ȡ��������
			System.out.println("type:"+cmdtype);
			byte[] msgbody = new byte[contentBuffer.remaining()];   //��Ϣ��
			
			contentBuffer.get(msgbody);
			
			InputMessage msg = new InputMessage(cmdtype,msgbody); 
			
			if(list == null) {
				list = new ArrayList<NetMessage>();
			}
	
			list.add(msg);
			
		}
		
		return list;
	}


}



