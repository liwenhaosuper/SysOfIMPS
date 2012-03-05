package com.imps.server.net;




/**
 * <p>
 * 网络会话，表示一个通讯连接
 */
public interface IoSession {
	
	/**每个Session输Buffer的大小*/
	public static final int INBUFFER_SIZE = 1024 * 10;
	
	/**
	 * <p>
	 * 获取SessionId
	 * </p>
	 * <br>
	 * @return
	 */
	public long getId();
	
	
	/**
	 * <p>
	 * 添加属性
	 * </p>
	 * <br>
	 * @param key
	 * @param obj
	 */
	public void addAttribute(String key, Object obj);
	
	/**
	 * <p>
	 * 获取属性
	 * </p>
	 * <br>
	 * @param key
	 * @return
	 */
	public Object getAttribute(String key);
	
	/**
	 * <p>
	 * 移除属性
	 * </p>
	 * <br>
	 * @param key
	 * @return
	 */
	public Object removeAttribute(String key);
	
	
	/**
	 * <p>
	 * 写消息
	 * </p>
	 * <br>
	 * @param msg
	 * @return
	 */
	public IoFuture write(NetMessage msg);
	

	/**
	 * <p>
	 * 异步连接
	 * </p>
	 * <br>
	 * @return
	 */
	public IoFuture connect();
	
	
	/**
	 * <p>
	 * 获取现在能取到的消息数量
	 * </p>
	 * <br>
	 * @return
	 */
	public int available();
	
	
	/**
	 * <p>
	 * 读取消息，此方法不会产生阻塞，当没有消息可读时立即返回null
	 * </p>
	 * <br>
	 * @return
	 */
	public NetMessage read();
	
	
	/**
	 * <p>
	 * 读取消息，此方法不会产生阴塞
	 * </p>
	 * <br>
	 * @param msgs
	 */
	public void read(NetMessage[] msgs);
	
	
	/**
	 * <p>
	 * 是否关闭
	 * </p>
	 * <br>
	 * @return
	 */
	public boolean isClose();
	
	
	/**
	 * <p>
	 * 是否正在关闭
	 * </p>
	 * <br>
	 * @return
	 */
	public boolean isCloseing();
	
	
	/**
	 * <p>
	 * 关闭IO会话，方法为异步的，如果要想同步，可以调用返回结果的IoFuture.await()
	 * </p>
	 * <br>
	 * @return
	 */
	public IoFuture close();
}
