package com.imps.server.net;

import java.nio.ByteBuffer;
import java.util.List;

/**
 * <p>
 * 协议处理器，使用者通过实现此接口来解析从网络收到字节数据，并把数据转换成内存中的<br>
 * 消息模形
 *
 */
public interface ProtocolHandler {
	/**
	 * <p>
	 * 当有数据通过时，通过此方法把数据转换成NetMessage <br>
	 * 注意：
	 * 进入 onData中的ByteBuffer参数是网络框架的子序列，对data操作后
	 * 所剩的字节数会引影网络
	 * </p>
	 * <br>
	 * @param data
	 * @param session
	 * @return
	 */
	List<NetMessage> onData(ByteBuffer data, IoSession session);
	
	/**
	 * <p>
	 * 每次经过时是否关闭通道，返回true关闭，否则保持
	 * </p>
	 * <br>
	 * @return
	 *//*
	boolean isClose();*/
}
