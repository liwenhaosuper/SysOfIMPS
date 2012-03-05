package com.imps.server.net.impl;

import java.nio.ByteBuffer;


/**
 * 用来创建内存管理的工厂类 
 * 提供了两种内存管理的对象，
 *
 */
public interface MemoryObjInface {
	
	
	
	/**
	 * <p>获取默认的内存出来使用 在为调用free()方法时该内存区间将不可以再此被分配
	 * @return
	 */
	public ByteBuffer allocat();
	
	
	/**
	 * <p>获取指定的内存出来使用  在为调用free()方法时该内存区间将不可以再此被分配
	 * <br/>
	 * @param size
	 * @return
	 */
	public ByteBuffer allocat(int size);
	
	/**
	 * 将该内存区间释放掉
	 * @param buf
	 * @throws Exception
	 */
	public void free(ByteBuffer buf) throws Exception;
	

	
	
}
