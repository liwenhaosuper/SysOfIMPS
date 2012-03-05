package com.imps.server.net.expand;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.SocketChannel;

import com.imps.server.net.IoSession;
import com.imps.server.net.expand.ClientIoSession;
import com.imps.server.net.expand.IoConnector;
import com.imps.server.net.impl.AbstractIoService;



/**
 * <p>
 * 实现IoService的连接器，此类本是IoServer框架的一个扩展，用于管理客户端连接所产生的IoSession,<br>
 * IoConnector本身是基于nio选择器的一个解决类，所有通过IoConnector所产生的IoSession都是会注<br>
 * 册到Selector中，和IoServerImpl一样，我们为每个CPU建立一个Selector选择器，并为每个选择器<br>
 * 提供一个可运行的处理线程，但这里由于IoConnector有时会用于客户端的创建，所以允需开发人员设<br>
 * 置可运行的读写处理线程个数（也就是选择器的个数） 	
 */
public class IoConnector extends AbstractIoService {

	/**
	 * <p>
	 * 在指定的代理接收器中生成新的代理会话，如果参数为空，返回null，如果连接器没有启动也返回null
	 * </p>
	 * <br>
	 * @param acceptor 代理接收器<br>
	 * @return PoxyIoSession 代理会话<br>
	 * @throws IOException
	 */
	public static IoSession newSession(IoConnector acceptor) throws IOException{
		
		if(acceptor == null) {
			return null;
		}
		
		if(!acceptor.isStart()) {
			return null;
		}

		long sessionId = acceptor.getNextSessionId();

		SocketChannel sc = SocketChannel.open();
		sc.configureBlocking(false);

		SocketAddress bindAddress = acceptor.getConfigure().getAddress();

		ClientIoSession session = new ClientIoSession(sessionId, sc, acceptor, bindAddress);

		return session;
	}

	
	public IoConnector() throws Exception {
		super();
		this.initIoReadWriteMachines(1);
	}
	
	public IoConnector(int readwriteThreadNum) throws Exception {
		super();
		this.initIoReadWriteMachines(readwriteThreadNum);
	}
	
	
	@Override
	public void start() throws Exception {
		super.start();
		this.startTimer();                //启动定时器
		this.startIoReadWriteMachines();
		this.isStart = true;
	}

	
	@Override
	public void stop(){
		synchronized (stopLock) {
			this.isStart = false;
			this.stopTimer();
			
			this.closeAllSession();
			
			this.stopIoReadWriteMachines();
		}
		
	}
}
