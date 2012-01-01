package com.imps.server.net;

import java.io.IOException;
import java.net.SocketAddress;
import java.util.Map;

/**
 * <p>
 * IO接收器，通过接收器，可以绑定本地IP地址和端口，来对网络进行监听，同时还可以设置一些网络处理相关的<br>
 * 处理者，比如ProtocolHandler(协议处理者)，比如OverTimeHandler(超时处理者)，IoHandler(消息处理者)<br>
 * 此类也是整个框架的启动者
 * 
 * 注意：<br>
 * 1.IoAcceptor目前版本只能监听一个端口，之后的版本考虑加入可以同时监听不同的地址<br>
 * 2.ProtocolHandler与IoHandler一定要进行设置，否则不起启动框架 <br>
 * 3.如果OverTimeHandler未被设置，框架会默认提供一个，默认的处理者以5分钟作为超时的判断条件，默认处理<br>
 *   者只对读写均无操作发生的超时作出了处理<br>
 * 4.默认的OverTimeHandler只是当发生超时时关闭IoSession<br>
 * 
 * 
 */
public interface IoService {
	
	/**
	 * <p>
	 * 获取配置
	 * </p>
	 * <br>
	 * @return
	 */
	public Configure getConfigure();
	
	
	/**
	 * </p>
	 * 设置配置
	 * </p>
	 * <br>
	 * @param config
	 */
	public void setConfigure(Configure config);
	
	
	/**
	 * <p>
	 * 构建协议组,协议组是包装不同协议处理的一种协议结构
	 * </p>
	 * <br>
	 * @param handlers
	 * @return
	 */
	public ProtocolHandler buildProtocolGroup(int prefixByteNum, ProtocolHandler... handlers);
	
	
	public IoSession getIoSession(long sessionId);
	
	/**
	 * <p>
	 * 启动框架
	 * </p>
	 * <br>
	 * @throws Exception
	 */
	public void start() throws Exception;
	
	
	/**
	 * <p>
	 * 停止框架
	 * </p>
	 * <br>
	 * @throws Exception
	 */
	public void stop() throws Exception;
}
