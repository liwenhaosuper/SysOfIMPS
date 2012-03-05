package com.imps.server.main.basetype;


import java.io.IOException;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;

public abstract class MessageProcessTask implements Runnable{
	protected Channel session;
	protected ChannelBuffer inMsg;
	public MessageProcessTask(Channel session,ChannelBuffer inMsg){
		this.session = session;
		this.inMsg = inMsg;
	}
	
	/**
	 * 字符串解析
	 * @throws IOException
	 */
	public abstract void parse();
	
	/**
	 * 执行
	 */
	public abstract void execute();
	
	@Override
	public void run() {
		try {
			//先解析
			parse();
			//再执行
			execute();
		}
		catch(Exception e) {
			//TODO:做一些事发生错误时的处理，一般是发生了不可恢复的错误
			e.printStackTrace();
		}
	}
}
