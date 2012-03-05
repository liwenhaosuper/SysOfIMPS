package com.imps.base;

import java.io.IOException;
import java.sql.SQLException;


import com.imps.handler.UserManager;
import com.yz.net.IoSession;

public abstract class MessageProcessTask implements Runnable{
	
	protected IoSession session;
	
	protected InputMessage message;
	
	protected UserManager manager;
	
	
	public MessageProcessTask(IoSession session, InputMessage message) throws SQLException {
		this.session = session;
		this.message = message;
		manager = UserManager.getInstance();
	}
	

	/**
	 * �ַ�������
	 * @throws IOException
	 */
	public abstract void parse() throws IOException;
	

	/**
	 * ִ��
	 */
	public abstract void execute();
	 

	/**
	 * ��־�������ڲ��ԣ�����ʵ�֡�����������
	 * @return
	 */
	public StringBuilder toInputString(){
		return null;
	}
	public StringBuilder toOutputString() {
		return null;
	}
	 

	
	@Override
	public void run() {
		try {
			//�Ƚ���
			parse();
			
			StringBuilder inputStrBuffer = toInputString();
			if(inputStrBuffer != null) {
				printLog(inputStrBuffer.toString());
			}
			
			//��ִ��
			execute();
			
			StringBuilder outputStrBuffer = toOutputString();
			if(outputStrBuffer != null) {
				printLog(outputStrBuffer.toString());
			}
		}
		catch(Exception e) {
			//TODO:��һЩ�·�������ʱ�Ĵ���һ���Ƿ����˲��ɻָ��Ĵ���
			e.printStackTrace();
		}
	}
	
	
	protected final void printLog(String logmsg) {
		//TODO:������Է����ӡ������ʹ��Log4j����������һЩ��ӡ��־�ķ���,������Ȥô��
	}
}
