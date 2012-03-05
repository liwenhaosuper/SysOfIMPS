package com.imps.server.net.impl;


import java.nio.ByteBuffer;
import java.util.Comparator;
import java.util.Iterator;
import java.util.TreeSet;

import com.imps.server.net.impl.MemoryObjInface;
import com.imps.server.net.impl.MomoryBuffer;
import com.imps.server.net.impl.MomoryBufferCommpositor;
import com.imps.server.net.impl.MomoryManagerByte;

/**
 * <p>该内存管理对象主要是在当你需要长时间的new一快内存的时候使用，
 * <p><b>（主要作用是为了不让GC对这些内存不停的释放分配而消耗性能，而且每次获取的内存大小可以是自己指定的大小）</b>
 * <p>本内存管理对象主要通过预先分配一个大的ByteBuffer然后每次从这个ByteBuffer中获取一小块内存出来进行使用，
 * <p>具体获取内存的规则是在打的内存中获取一段连续的内存出来使用，如果中间有一小段内存(例如三个字节)未可以使用，
 * <p>但如果你获取的内存都币这三内存大的话，则永远获取不到该内存（这里就会产生一小块的内存碎片），
 * <p>当然只要该小段内存的前后被释放后将又可以获取使用
 * <p>主要是通过ByteBuffer的子序列来做到的，如果当预先分配的内存不足的时候，将重新分配另快大的内存
 * <p>(该该重新分配的内存也是有初始化的时候用使用者设置，重新分配的内存将由本内存管理对象中的子内存管理对象所拥有,
 * <p>主要算法是通过链表的形式来实现，理论上当总内存不可用的时候可以无限分配新的内存)
 * <br/>
 */
public class MomoryManagerByte implements MemoryObjInface{
	
	
	/*public static void main(String[] args) {
		try {
			
//			ByteBuffer buf = ByteBuffer.allocate(3);
//			System.out.println(buf.position());
//			System.out.println(buf.limit());
//			
//			buf.put((byte) 1);
//			buf.put((byte) 2);
//			buf.put((byte) 3);
//			buf.put((byte) 3);
			
			
			MomoryManagerByte newMomoryManager = new MomoryManagerByte(9,3,9,true);

			ByteBuffer buf_1 = newMomoryManager.allocat(3);
			ByteBuffer buf_2 = newMomoryManager.allocat(3);
			ByteBuffer buf_3 = newMomoryManager.allocat(3);
			
			ByteBuffer buf_4 = newMomoryManager.allocat(3);
			ByteBuffer buf_5 = newMomoryManager.allocat(3);


//			newMomoryManager.free(buf_2);
//			newMomoryManager.free(buf_4);
//
//			ByteBuffer buf_6 = newMomoryManager.allocat(22);

			buf_1.put((byte) 1);
			buf_1.put((byte) 2);
			buf_1.put((byte) 3);
			
			buf_2.put((byte) 4);
			buf_2.put((byte) 5);
			buf_2.put((byte) 6);
			
			buf_3.put((byte) 7);
			buf_3.put((byte) 8);
			buf_3.put((byte) 9);
			
			buf_4.put((byte) 10);
			buf_4.put((byte) 11);
			buf_4.put((byte) 12);
			
			buf_5.put((byte) 13);
			buf_5.put((byte) 14);
			buf_5.put((byte) 15);
			
			
			buf_1.flip();
			buf_2.flip();
			buf_3.flip();
			buf_4.flip();
			buf_5.flip();
			
			newMomoryManager.free(buf_1);
			buf_1 = newMomoryManager.allocat(3);
			buf_1.put((byte) 1);
			buf_1.put((byte) 2);
			buf_1.put((byte) 3);
			buf_1.flip();
			
			System.out.println( buf_1.get() + " - " + buf_1.get() + " - " + buf_1.get());
			System.out.println( buf_2.get() + " - " + buf_2.get() + " - " + buf_2.get());
			System.out.println( buf_3.get() + " - " + buf_3.get() + " - " + buf_3.get());
			System.out.println( buf_4.get() + " - " + buf_4.get() + " - " + buf_4.get());
			System.out.println( buf_5.get() + " - " + buf_5.get() + " - " + buf_5.get());
			
			

			System.out.println("");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}*/
	
	
	/** 默认获取的内存大小 */
	private static int defaultSize 		= 1024;				
	/** 默认创建的内存大小 */
	private static int byte_size 			= 1024 * 1024 * 1;
	/** 默认扩充的内存大小 */
	private static int dilatancy_size		= 1024 * 1024 * 1;
	/** 是否在虚拟机所所管理的范围内创建内存 */
	private static boolean isDirect		= false;
	
	
	
	/** 内存byteBuffer */
	private ByteBuffer byteBuffer = null;
	
	/** 存放正在使用的内存区间 */
	private TreeSet<MomoryBuffer> bufferSet = new TreeSet<MomoryBuffer>(new MomoryBufferCommpositor());
	
	/** 当该管理器管理的内存不够用时将生成另一个同样的管理内存放到本身对象中，让其组成一个链表结构 */
	private MomoryManagerByte momoryManagerByte = null;
	
	
	public MomoryManagerByte(){
		this(byte_size, defaultSize, dilatancy_size, isDirect);
	}
	
	public MomoryManagerByte(boolean isDirect){
		this(byte_size, defaultSize, dilatancy_size, isDirect);
	}
	
	public MomoryManagerByte(int byteSize){
		this(byteSize, defaultSize, dilatancy_size, isDirect);
	}
	
	public MomoryManagerByte(int byteSize,boolean isDirect){
		this(byteSize, defaultSize, dilatancy_size, isDirect);
	}

	
	/**
	 * 构造
	 * @param byteSize			预先分配的总内存大小
	 * @param defaultSize		默认获取的内存大小
	 * @param dilatancySize		默认扩充的内存大小
	 * @param isDirect			是否在虚拟机所所管理的范围内创建内存
	 */
	public MomoryManagerByte(int byteSize,int defaultSize,int dilatancySize,boolean isDirect){
		this.byte_size = byteSize;
		this.defaultSize = defaultSize;
		this.dilatancy_size = dilatancySize;
		this.isDirect = isDirect;
		
		if(this.isDirect) {
			byteBuffer = ByteBuffer.allocateDirect(this.byte_size);
		}
		else {
			byteBuffer = ByteBuffer.allocate(this.byte_size);
		}
	}
	
	/**
	 * <p>获取默认的内存出来使用 在为调用free()方法时该内存区间将不可以再此被分配
	 * @return
	 */
	public ByteBuffer allocat() {
		return this.allocat(this.defaultSize);
	}
	
	/**
	 * <p>获取指定的内存出来使用  在为调用free()方法时该内存区间将不可以再此被分配
	 * <br/>
	 * @param size
	 * @return
	 */
	public ByteBuffer allocat(int size) {
		//先从总内存中获取
		ByteBuffer byteBuffer = gain(size);
		if(byteBuffer == null){
			//如果未获取到在到子内存管理对象里面获取
			MomoryManagerByte nextMomoryManagerByte = this.getMomoryManagerByte();
			if(nextMomoryManagerByte != null){
				//如果有子内存管理对象就直接获取
				byteBuffer = nextMomoryManagerByte.allocat(size);
				return byteBuffer;
			}
			//如果没有子内存管理对象就创建一个并获取
			nextMomoryManagerByte = new MomoryManagerByte(this.byte_size,this.defaultSize,this.dilatancy_size,this.isDirect);
			this.setMomoryManagerByte(nextMomoryManagerByte);
			byteBuffer = nextMomoryManagerByte.allocat(size);
		}
		
		return byteBuffer;
	}
	
	/**
	 * 从byteBuffer中获取一块内存出来使用
	 * @param size
	 * @return
	 */
	private ByteBuffer gain(int size) {
		boolean bor = false;
		//如果还没有获取过内存就直接从第一个位置开始获取
		if(bufferSet == null || bufferSet.size()<=0){
			this.byteBuffer.position(0);
			this.byteBuffer.limit(size);
			bor = true;
		}
		else{
			//如果之前获取过 
			synchronized (this.bufferSet) {
				//遍历之前获取的内存对象 拿到它的索引值 根据索引值来接着后面的位置获取
				Iterator<MomoryBuffer> iter =  bufferSet.iterator();
				int position = 0;
				while(iter.hasNext()){
					MomoryBuffer momoryBuffer = iter.next();
					if((momoryBuffer.getPosition() - position) >= size){
						this.byteBuffer.position(position);
						this.byteBuffer.limit(momoryBuffer.getPosition());
						bor = true;
						break;
					}
					position = momoryBuffer.getLimit();
				}
				if((this.byte_size - position) >= size){
					this.byteBuffer.position(position);
					this.byteBuffer.limit(position + size);
					bor = true;
				}
			}
		}
		ByteBuffer slicebuf = null;
		if(bor){
			slicebuf = this.byteBuffer.slice();
//			this.getBufferSet().add(new MomoryBuffer(slicebuf,slicebuf.arrayOffset(),slicebuf.arrayOffset() + slicebuf.limit()));
			this.getBufferSet().add(new MomoryBuffer(slicebuf,this.byteBuffer.position(),this.byteBuffer.limit()));
		}
		this.byteBuffer.clear();
		return slicebuf;
	}
	
	/**
	 * 将该内存区间释放掉
	 * @param buf
	 * @throws Exception
	 */
	public void free(ByteBuffer buf) throws Exception {
		boolean bor = false;
		synchronized (this.bufferSet) {
			Iterator<MomoryBuffer> iter =  bufferSet.iterator();
			while(iter.hasNext()){
				MomoryBuffer momoryBuffer = iter.next();
				if(momoryBuffer.getBuf() != buf){
					continue;
				}
				bor = true;
				this.bufferSet.remove(momoryBuffer);
				break;
			}
			if(!bor){
				MomoryManagerByte nextMomoryManagerByte = this.getMomoryManagerByte();
				if(nextMomoryManagerByte != null){
					nextMomoryManagerByte.free(buf);
				}
			}
		}
	}

	/**
	 * 返回总内存ByteBuffer
	 * @return
	 */
	private ByteBuffer getByteBuffer() {
		return byteBuffer;
	}

	/**
	 * 返回正在使用的ByteBuffer队列，用来标示有哪些区间已经在使用了
	 * @return
	 */
	private TreeSet<MomoryBuffer> getBufferSet() {
		return bufferSet;
	}


	/**
	 * 设置子内存管理对象
	 * @param momoryManagerByte
	 */
	private void setMomoryManagerByte(MomoryManagerByte momoryManagerByte) {
		this.momoryManagerByte = momoryManagerByte;
	}

	/**
	 * 返回子内存管理对象
	 * @return
	 */
	private MomoryManagerByte getMomoryManagerByte() {
		return momoryManagerByte;
	}
	
	
	
}

/**
 * 用来封装获取出来的内存，主要是为了标示该内存用到了总内存中的哪些区间
 * @author Administrator
 *
 */
class MomoryBuffer{
	private ByteBuffer buf = null;
	
	private int position = 0;
	private int limit = 0;
	
	public MomoryBuffer(ByteBuffer _buf,int _position,int _limit){
		this.buf = _buf;
		this.position = _position;
		this.limit = _limit;
	}

	public ByteBuffer getBuf() {
		return buf;
	}

	public int getPosition() {
		return position;
	}

	public int getLimit() {
		return limit;
	}
}

/**
 * 一个排序器。用来将MomoryBuffer进行排序
 * @author Administrator
 *
 */
class MomoryBufferCommpositor implements Comparator<MomoryBuffer>{

	@Override
	public int compare(MomoryBuffer o1, MomoryBuffer o2) {
		
		int position_1 = o1.getPosition();
		int position_2 = o2.getPosition();
		
		if(position_1 > position_2){
			return 1;
		}
		if(position_1 < position_2){
			return -1;
		}
		return 0;
	}
	
}



