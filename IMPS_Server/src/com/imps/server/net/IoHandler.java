package com.imps.server.net;

import com.imps.server.net.IoFuture;
import com.imps.server.net.IoSession;
import com.imps.server.net.NetMessage;


/**
 * <p>
 * IO处理者，对于协议处理后的具体消息进行处理，可以看作是网络层连接逻缉层的一个地方，逻缉层对消息的处理<br>
 * 可以继承此接口进行消息的处理，IOServer的框架为了力求简捷，没有为IoHandler作线程池方面的处理，如果有<br>
 * 逻缉处理有必要用到线程池之类的处理，可以在自已实现的IoHandler中作这方面的实现.
 */
	public interface IoHandler {
	/**
	 * <p>
	 * 当有消息到达时被触发，此消息是经由协议处理者解析后的消息类型，注意，当没有设置消息处理器时此方法<br>
	 * 是不会被触发的
	 * </p>
	 * <br>
	 * @param session
	 */
	public void messageReceived(IoSession session, NetMessage msg);
	
	
	/**
	 * <p>
	 * 当有消息到达时被解发，此消息是没有经过协义处理者的ByteBuffer类型，对数据的解的需要实现者自已实现<br>
	 * 注意，当设置了协议处理器后，此方法是不会被触发的。
	 * </p>
	 * <br>
	 * @param session
	 * @param buffer
	 */
	public void messageReceived(IoSession session, byte[] msgdata);
	
	
	//public void onPutMessage(IoSession session);
	
	
	
	//public void onClosedIoSession(IoFuture future);
	
	
	/**
	 * <p>
	 * IoSession被正式关闭时触发执行(由于关闭是异常的，也就是不管IoSession是成功关闭<br>
	 * 还是在关闭时抛出了异常都会被触发
	 * </p>
	 * <br>
	 * @param session
	 */
	public void ioSessionClosed(IoFuture future);
	
	
	
	//public void onError(Exception e);
}
