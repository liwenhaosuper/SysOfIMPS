package com.imps.server.net.expand;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.SocketChannel;
import java.util.concurrent.atomic.AtomicBoolean;

import com.imps.server.net.IoFuture;
import com.imps.server.net.expand.ConnectFuture;
import com.imps.server.net.impl.AbstractIoService;
import com.imps.server.net.impl.IoSessionImpl;

/**
 * <p>
 * 也表示一个IoSession，这是IoServer框架的一个扩展补充，支持客户端通讯会话，在一个客户端上利用<br>
 * 此框架生成的IoSession，因为客户端的IoSession需要连接功能，此类扩展出一个异步的连接方法，此扩<br>
 * 展很多情况下可用于构建高性能的代理服务器，在某些情况下，我们需要在不修改原服务器的情况下而加入<br>
 * 另外一种形式的通讯协议或方式，那么有一种策略是提供代理服务器来与原有的服务器进行通讯，每个从真<br>
 * 实客户端上来的连接，都可以生成一个IoSession并连接上层服务器，此连接尽管放心，决对不会产生任何<br>
 * 额外线程开销，因为ClientIoSession是从IoConnector(详见IoConnector的说明)生成出来，生成的同时<br>
 * 已注册到IoConnector中，并由IoConnector负责代理并管理IoSession，ClientIoSession的连接方法也<br>
 * 只是发出连接请求到IoConnector，由IoConnector负责真实连接工作。
 */
public class ClientIoSession extends IoSessionImpl {
	
	/**被接受器绑定的地址*/
	private SocketAddress connectAddress;
	
	private ConnectFuture connectFuture = new ConnectFuture(this);
	
	private AtomicBoolean isConnecting = new AtomicBoolean(false);        //是否正在连接
	
	
	ClientIoSession(long id, SocketChannel channel, AbstractIoService acceptor, SocketAddress address) {
		super(id, channel, acceptor);
		this.connectAddress = address;
	}
	
	/**
	 * <p>
	 * 连接上层服务器，返回异步运算结果。如果在连接中出错，IoFuture.isError()可检查出来
	 * </p>
	 * <br>
	 * @return IoFuture
	 */
	public IoFuture connect(){
		try {
			if(isConnecting.compareAndSet(false, true)) {
				getChannel().configureBlocking(false);
				getOwnerAcceptor().scheduleToDispatcher(this);
			}
		}
		catch(IOException e) {
			connectFuture.setComplete(e);     //设置完成
		}
		return connectFuture;
	}
	
	
	/**
	 * <p>
	 * 获得连接的异步运算结果，只有处于连接中时才能拿到，其它情况返回null
	 * </p>
	 * <br>
	 * @return
	 */
	public IoFuture getConnectFuture() {
		if(isConnecting.get()) {   //只有处在正在连接中才能拿到异步运算结果
			return connectFuture;
		}
		return null;
	}
	
	public SocketAddress getConnectAddress() {
		return connectAddress;
	}
}
