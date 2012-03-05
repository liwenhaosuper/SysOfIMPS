package com.imps.server.net;

import com.imps.server.net.IoSession;


/**
 * <p>
 * 表示IO的异步计算结果，提供检查计算是否完成的方法，和等待计算完成的方法。
 * 注意：
 * 一个IO操作的完成，只是说明操作不在有可能继续进行，操作是否成功并不由是否完成证明
 * 类提供了，检查异常的方法，如果操作在正确完成前发生异常，则通过检查异常可以获得操
 * 作发生的异常
 *
 */
public interface IoFuture {
	
	/**
	 * <p>
	 * 获取IO会话
	 * </p>
	 * <br>
	 * @return IoSession 表示一个IO会话<br>
	 */
	public IoSession getSession();
	
	
	/**
	 * <p>
	 * 检查发出的IO操作是否完成，返加true:完成  返回false:未完成
	 * </p>
	 * <br>
	 * @return boolean 是否完成<br>
	 */
	public boolean isComplete();
	
	
	/**
	 * <p>
	 * 检查IO操作是否发生错误了，返回true:发生错误了  返回false:未发生错误
	 * </p>
	 * <br>
	 * @return
	 */
	public boolean isError();
	
	
	/**
	 * <p>
	 * 检查IO操作是否被取消了，返回true:操作消息  返回false:操作未被取消
	 * </p>
	 * <br>
	 * @return boolean 是否取消<br>
	 */
	public boolean isCannel();
	
	
	
	/**
	 * <p>
	 * 获取IO操作发生异常后的异常对像，如果IO操作无错误发生，将返回null
	 * </p>
	 * <br>
	 * @return Throwable 异常<br>
	 */
	public Throwable getThrowable();

	
	/**
	 * <p>
	 * 等待完成，一直等待，直到IO操作完成为止，长时间等待是一个相对的说明，程序为在一些不可预<br>
	 * 测的情况下设定了一个最长等待，如果在超过最长等待还未得到IO操作完成的信号，则立即返回出<br>
	 * 来，以避免程序产生死循环。
	 * </p>
	 * <br>
	 */
	public void await();
	
	
	/**
	 * <p>
	 * 等待完成，等待指定的时间(豪秒)，直到IO操作完成为止，如果指定时间内IO操作未完成，则立即返回<br>
	 * </p>
	 * <br>
	 * @param timeout 等待时间<br>
	 */
	public void await(long timeout);

	/**
	 * <p>
	 * 取消当前IO操作
	 * </p>
	 * <br>
	 */
	public void cancel();
}
