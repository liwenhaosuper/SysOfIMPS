package com.imps.server.net;

/**
 * <p>
 * IO超时处理者接口，IoSession超时后都会触发此接口听方法，这里定义了读超时(IoSession在规定的一段时间<br>
 * 内没有发生任何读的行为便视超时)，写超时(IoSession在规定的一段时间内没有发生写的行为便视为超时)，双<br>
 * 方超时(在规定时间没有发生读和写的行为便视为超时)，这里要注意的是，当发生双方超时时，不管读超时和写超<br>
 * 时是否发生，都不会被触发执行。<br>
 * 
 * 接口不仅定义了超时后执行方法，还定义了一些超时判断时间条件，时间条件都是以豪秒为单位计算，比如interval()<br>
 * 便定义了框架会间隔多少时间检查一次IoSession是否超时，readOverTime()便定义了读的超时单位，其它以此类推<br>
 */
public interface OverTimeHandler {
	/**
	 * <p>
	 * 一段时间内没有进行读操作时被触发
	 * </p>
	 * <br>
	 * @param session
	 */
	public void onReadOverTime(IoSession session);
	
	
	/**
	 * <p>
	 * 一段时间内没有进行写操作时被触发
	 * </p>
	 * <br>
	 * @param session
	 */
	public void onWriterOverTime(IoSession session);
	
	
	/**
	 * <p>
	 * 一段时间内没有进行读写操作时被触发
	 * </p>
	 * <br>
	 * @param session
	 */
	public void onBothOverTime(IoSession session);
	
	
	
}
