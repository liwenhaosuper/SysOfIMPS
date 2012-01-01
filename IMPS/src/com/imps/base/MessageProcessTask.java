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
	 * 字符串解析
	 * @throws IOException
	 */
	public abstract void parse() throws IOException;
	

	/**
	 * 执行
	 */
	public abstract void execute();
	 

	/**
	 * 日志处理，用于测试，懒得实现。。。。。。
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
			//先解析
			parse();
			
			StringBuilder inputStrBuffer = toInputString();
			if(inputStrBuffer != null) {
				printLog(inputStrBuffer.toString());
			}
			
			//再执行
			execute();
			
			StringBuilder outputStrBuffer = toOutputString();
			if(outputStrBuffer != null) {
				printLog(outputStrBuffer.toString());
			}
		}
		catch(Exception e) {
			//TODO:做一些事发生错误时的处理，一般是发生了不可恢复的错误
			e.printStackTrace();
		}
	}
	
	
	protected final void printLog(String logmsg) {
		//TODO:这里可以放入打印，可以使用Log4j，或其它的一些打印日志的方法,你有兴趣么？
	}
}
