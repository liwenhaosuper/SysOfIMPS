package com.imps.server.net.impl;



import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * <p>该内存管理对象主要是在当你需要长时间的new一快内存的时候使用，
 * <p><b>（主要作用是为了不让GC对这些内存不停的释放分配而消耗性能）</b>
 * <p>与MomoryManagerByte的区别是在产生内存碎片的几率稍大一点，做法是当你获取一个指定的内存时 我们会到该大小对应的内存块队列中去找，
 * <P>如果没有在创建该内存块，并放进该内存快的队列当中,如果每次获取的内存大小不同则会每次都创建新的内存块队列。
 * <P> 注意该对象管理类 在初始化的时候不会预先分配，只有当需要使用的时候才会分配，并保留下来
 *
 *
 */
public class MemoryManagerLinked implements MemoryObjInface{
	
//	public static void main(String[] args) {
//		try{
//			MemoryManagerLinked memoryManagerLinked = new MemoryManagerLinked(1024,false);
//
//			ByteBuffer buf_1 = memoryManagerLinked.allocat(1);
//
//			ByteBuffer buf_2_1 = memoryManagerLinked.allocat(2);
//			ByteBuffer buf_2_2 = memoryManagerLinked.allocat(2);
//
//			ByteBuffer buf_3_1 = memoryManagerLinked.allocat(3);
//			ByteBuffer buf_3_2 = memoryManagerLinked.allocat(3);
//			ByteBuffer buf_3_3 = memoryManagerLinked.allocat(3);
//			ByteBuffer buf_3_4 = memoryManagerLinked.allocat(3);
//
//			memoryManagerLinked.free(buf_2_2);
//			memoryManagerLinked.free(buf_3_1);
//			memoryManagerLinked.free(buf_3_3);
//			
//			memoryManagerLinked.free(buf_3_3);
//
//		}catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
	
	/** 是否在虚拟机所所管理的范围内创建内存 */
	private static  boolean isDirect;
	/** 默认货物的内存大小 */
	private static int defaultSize 		= 1024;	
	
	
	/** 总内存容器 key:字节大小 value:ByteBuffer队列 */
	public HashMap<Integer,LinkedList<BufferObj>> bufferObjMap = null;
	

	
	
	
	public MemoryManagerLinked(){
		this(defaultSize, isDirect);
	}
	
	public MemoryManagerLinked(int defaultSize){
		this(defaultSize, isDirect);
	}
	
	/**
	 * 构造
	 * @param isDirect		是否在虚拟机所所管理的范围内创建内存
	 */
	public MemoryManagerLinked(int defaultSize,boolean isDirect){ 
		this.defaultSize = defaultSize;
		this.isDirect = isDirect;
		
		bufferObjMap = new HashMap<Integer, LinkedList<BufferObj>>();
		
		
	}
	

	/**
	 * <p>获取默认的内存出来使用 在为调用free()方法时该内存区间将不可以再此被分配
	 * @return
	 */
	public ByteBuffer allocat(){
		return allocat(this.defaultSize);
	}
	
	/**
	 * <p>获取指定的内存出来使用  在为调用free()方法时该内存区间将不可以再此被分配
	 * @param size
	 * @return
	 */
	public ByteBuffer allocat(int size) {
		LinkedList<BufferObj> _linkedList = bufferObjMap.get(size);
		if(_linkedList == null){
			_linkedList = new LinkedList<BufferObj>();
			BufferObj _bufferObj = newBufferObj(size);
			_linkedList.add(_bufferObj);
			bufferObjMap.put(size, _linkedList);
			return _bufferObj.buf;
		}
		else{
			for(int i=0;i<_linkedList.size();i++){
				BufferObj bufferObj = _linkedList.get(i);
				if(bufferObj == null){
					continue;
				}
				//检查该内存是否在使用 
				if(bufferObj.atomicBoolean.get()){
					continue;
				}
				//标示为使用 并返回出去
				bufferObj.atomicBoolean.compareAndSet(false, true);
				return bufferObj.buf;
			}
			BufferObj _bufferObj = newBufferObj(size);
			_linkedList.add(_bufferObj);
			return _bufferObj.buf;
		}
	}
	
	
	/**
	 * 产生一个新的BufferObj
	 * @return
	 */
	private BufferObj newBufferObj(int size){
		BufferObj bufferObj = new BufferObj(size);
		bufferObj.atomicBoolean.compareAndSet(false, true);
		return bufferObj;
	}
	

	/**
	 * 归还buffer 如果该buf已经归还则抛出异常
	 * @param buf
	 */
	public void free(ByteBuffer buf) throws Exception {
		int size = buf.limit();
		LinkedList<BufferObj> _linkedList = bufferObjMap.get(size);
		if(_linkedList != null){
			for(int i=0;i<_linkedList.size();i++){
				BufferObj bufferObj = _linkedList.get(i);
				if(bufferObj == null){
					continue;
				}
				if(bufferObj.buf != buf){
					continue;
				}
				bufferObj.buf.clear();
				boolean b = bufferObj.atomicBoolean.compareAndSet(true, false);
				if(!b){
					throw new Exception(" 该内存已经被释放掉了 ");
				}
			}
		}
	}
	
	/**
	 * 用来封装分配出去的内存Buffer，主要作用是有一个原子的变量来标示为是正在使用
	 * @author Administrator
	 *
	 */
	class BufferObj {
		
		/** 标示该buffer已经被获取使用了，不能再被其它地方获取使用 */
		public AtomicBoolean atomicBoolean = new AtomicBoolean(false);
		
		public ByteBuffer buf = null;
		
		public BufferObj(int byteSize){
			if(MemoryManagerLinked.this.isDirect) {
				buf = ByteBuffer.allocateDirect(byteSize);
			}
			else {
				buf = ByteBuffer.allocate(byteSize);
			}
		}
	}
}



