package com.imps.test;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;


import com.imps.server.base.CommandId;
import com.imps.server.base.InputMessage;
import com.imps.server.base.MessageFactory;
import com.imps.server.base.OutputMessage;
import com.imps.server.net.Configure;
import com.imps.server.net.IoFuture;
import com.imps.server.net.IoHandler;
import com.imps.server.net.IoSession;
import com.imps.server.net.NetMessage;
import com.imps.server.net.ProtocolHandler;
import com.imps.server.net.expand.IoConnector;


public class Client {
	
	
	public static void main(String[] args) {
		try {
			Configure config = new Configure();
			config.setAddress(new InetSocketAddress("127.0.0.1",1200));
			config.setProtocolHandler(new Protocol());
			config.setIoHandler(new DataHandler());
			
			IoConnector connector = new IoConnector();
			
			config.start(connector);
			
			IoSession session = IoConnector.newSession(connector);
			IoFuture future = session.connect();
			
			future.await();
			
			//register test
	/*		OutputMessage out = new OutputMessage(CommandId.C_REGISTER);
				//TODO:�ڴ˴����������Ϣ
				//�˴��Ĳ��ԣ�o(����)o ��
			String str = "l111i";     //username
		    
			byte[] b = str.getBytes("GB2312");   //תΪ�ֽ���
			long len = b.length;      //�û�������
			//System.out.println("long:"+len); 
			out.getOutputStream().writeLong(len);   //д�볤��
			out.getOutputStream().write(b);        //д���ֽ���
			str = "l111i";  //����
			b = str.getBytes("GB2312");
			len = b.length;
			out.getOutputStream().writeLong(len);   //д�볤��
			out.getOutputStream().write(b);        //д���ֽ���
			int gender =0;  //�Ա�
			out.getOutputStream().writeInt(gender);
			str ="liwenhaosuper111@126.com";    //����
			b = str.getBytes("GB2312");
			len = b.length;
			out.getOutputStream().writeLong(len);
			out.getOutputStream().write(b);*/
			
			/**
			 * login test
			 */
/*			OutputMessage out = new OutputMessage(CommandId.C_LOGIN_REQ);
			//username
			String str = "li";
			long len = str.length();
			out.getOutputStream().writeLong(len);
			out.getOutputStream().write(str.getBytes("GB2312"));
			//password
			str="li";
			len = str.length();
			out.getOutputStream().writeLong(len);
			out.getOutputStream().write(str.getBytes("GB2312"));*/			
			
			/**
			 * heart beat test
			 */
			OutputMessage out = new OutputMessage(CommandId.C_HEARTBEAT_REQ);
			//username
			String str = "li";
			long len = str.length();
			out.getOutputStream().writeLong(len);
			out.getOutputStream().write(str.getBytes("GB2312"));
			//datasize
			len = 1;
			out.getOutputStream().writeLong(len);
			//information
			//time
			str = "2011-05-21 23:12:43";
			len = str.length();
			out.getOutputStream().writeLong(len);
			out.getOutputStream().write(str.getBytes("GB2312"));
			//x,y
			double x = 20.1;
			out.getOutputStream().writeDouble(x);
			double y = 100.2;
			out.getOutputStream().writeDouble(y);
			
		
			session.write(out);
				
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	
	public static class DataHandler implements IoHandler {

		@Override
		public void ioSessionClosed(IoFuture future) {
			//TODO:��һ���Ự�رպ󱻴����ķ���	
		}

		@Override
		public void messageReceived(IoSession session, NetMessage msg) {
			System.out.println("msg received!");
			InputMessage message = (InputMessage) msg;
			byte type = message.getCmdType();
			System.out.println("cmdtype:"+type);
		    if(type==CommandId.S_LOGIN_RSP)
		    {//login response
		    	DataInputStream in = message.getInputStream();
		    	try {
					long len = in.readLong();
					byte[] b = new byte[(int)len];
					in.read(b);
					String str = new String(b,"GB2312");
					System.out.println("username:"+str);
					int gender = in.readInt();
					System.out.println("gender:"+gender);
					len = in.readLong();
					byte[] bb = new byte[(int)len];
					in.read(bb);
					str = new String(bb,"GB2312");
					System.out.println("email:"+str);
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		    }
		    else if(type==CommandId.S_HEARTBEAT_RSP)
		    {//heartbeat response
		    	//TODO: do whatever you want!
		    }
		}

		
		@Override
		public void messageReceived(IoSession session, byte[] msgdata) {
			//TODO:��û��Э�������ʱ���밴������ĿҪ����ɴ˷�
			System.out.println("msg received!");
		}	
	}
	
	public static class Protocol implements ProtocolHandler {

		@Override
		public List<NetMessage> onData(ByteBuffer data, IoSession session) {
			//TODO:���︺��跿�����ݽ��н��������γ�һ���������Ϣ��
			
			ArrayList<NetMessage> list = new ArrayList<NetMessage>();
			while(data.remaining() >= 4) {
				System.out.println("ondata: msg received!");
				//TODO �ڴ˴���ӽ������ݸ�ʽ
				
				byte[] tag = new byte[2];
				tag[0] = data.get();
				tag[1]= data.get();
				
				System.out.println("tag: "+tag);
				
				Short size = data.getShort();
				
				System.out.println("size:"+size);
				
				byte type = data.get();
				
				byte[] content = new byte[size-1];
				data.get(content);
				
				InputMessage out = new InputMessage(type,content);
				list.add(out);
				
				break;
			}
			
			return list;
		}
		
	}
	
}
