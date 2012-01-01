package com.imps.server.net.impl;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.imps.server.net.IoFuture;
import com.imps.server.net.IoSession;
import com.imps.server.net.expand.ClientIoSession;
import com.imps.server.net.expand.ConnectFuture;
import com.imps.server.net.impl.CloseFuture;
import com.imps.server.net.impl.DispatcherEventlListener;
import com.imps.server.net.impl.IoSessionImpl;
import com.imps.server.net.impl.MemoryObjInface;
import com.imps.server.net.impl.MomoryManagerByte;
import com.imps.server.net.impl.WriteFuture;

/**
 * <p>
 * 一个IO的读写，连接的处理机
 */
class IoReadWriteMachine implements Runnable {
	
	/**线程名字*/
	String threadName;
	
	/**最大的输出块*/
	private static final int MAX_OUT_SIZE = 1024;
	
	private boolean isRunning;
	
	/**监听者列表*/
	private HashSet<DispatcherEventlListener> listenerSet;
	
	/**内存管理器*/
	private MemoryObjInface memManager;
	
	/**等待注册队列*/
	private ConcurrentLinkedQueue<IoSessionImpl> waitRegQueue;
	
	/**等待删除队列*/
	//private ConcurrentLinkedQueue<CloseFuture> waitRemoveQueue;
	
	/**等待控制队列*/
	private ConcurrentLinkedQueue<IoSessionImpl> waitControlQueue;
	
	/**等待超时队列*/
	private ConcurrentLinkedQueue<IoSessionImpl> waitOverTimeQueue;
	
	/**等待连接队列*/
	private ConcurrentLinkedQueue<IoFuture> waitConnectQueue;
	
	private Selector selector;
	
	/**最大能承爱的IoSession数量*/
	private int maxIoSessionNum;
	
	/**并发IoSession的计数器*/
	private int concurrentIoSessionCount;
	
	
	void addListener(DispatcherEventlListener listener) {
		listenerSet.add(listener);
	}
	
	
	/**
	 * <p>
	 * 初始化IO处理器
	 * </p>
	 * <br>
	 * @param maxIoSessionNum
	 * @throws Exception
	 */
	public void init(int maxIoSessionNum) throws Exception {
		
		listenerSet = new HashSet<DispatcherEventlListener>();
		memManager = new MomoryManagerByte(IoSession.INBUFFER_SIZE * 100,IoSession.INBUFFER_SIZE ,IoSession.INBUFFER_SIZE * 100, true);
		
		waitRegQueue = new ConcurrentLinkedQueue<IoSessionImpl>();
		
		//waitRemoveQueue = new ConcurrentLinkedQueue<CloseFuture>();
		
		waitControlQueue = new ConcurrentLinkedQueue<IoSessionImpl>();
		waitOverTimeQueue = new ConcurrentLinkedQueue<IoSessionImpl>();
		
		waitConnectQueue = new ConcurrentLinkedQueue<IoFuture>();
		
		
		selector = Selector.open();
		
		this.maxIoSessionNum = maxIoSessionNum;
		
	}
	
	
	/**
	 * <p>
	 * 排程关闭并移除IO会话
	 * </p>
	 * <br>
	 * @param closeFuture
	 *//*
	public void scheduleRemove(CloseFuture closeFuture) {
		
		if(closeFuture == null) {
			return;
		}
		
		waitRemoveQueue.offer(closeFuture);
		this.wakeup();
	}*/
	
	
	public void scheduleOverTime(IoSessionImpl session) {
		if(session == null) {
			return;
		}
		waitOverTimeQueue.offer(session);
		this.wakeup();
	}
	
	
	public void wakeup() {
		selector.wakeup();
	}
	
	
	/**
	 * <p>
	 * 关闭IO处理器
	 * </p>
	 * <br>
	 * @throws IOException
	 */
	public void close() {
		this.isRunning = false;
		wakeup();
		
		try {
			this.selector.close();
		} catch (IOException e) {}
	}

	
	/**
	 * <p>
	 * 获取内存管理器
	 * </p>
	 * <br>
	 * @return
	 */
	public MemoryObjInface getMemoryManager() {
		return memManager;
	}

	
	/**
	 * <p>
	 * 判断是否打开
	 * </p>
	 * <br>
	 * @return
	 */
	public boolean isOpen() {
		// TODO Auto-generated method stub
		return false;
	}

	
	//TODO:考虑是否加入异常抛出
	private void registerIoSessionNow(IoSessionImpl session) throws IOException {
		SocketChannel channel = session.getChannel();
		if(channel == null) {
			throw new Error("通道不能为空");
		}

		/*if(channel.isRegistered()) {
			throw new RuntimeException("此通道已经主册过");
		}*/

		
		channel.configureBlocking(false);                //设置为非阻塞
		
		if(session.getClass() == IoSessionImpl.class) {
			//注册
			SelectionKey selectKey = channel.register(selector, SelectionKey.OP_READ, session);  //向选择器注册通道

			session.setSelectionKey(selectKey);   //设置会话的选择键

			//session.setOwnerDispatcher(this);    //设置归属IO处理器

			this.onRegisterSession(session);
		}
		
		else if(session.getClass() == ClientIoSession.class) {  //注册连接操作
			ClientIoSession clientIoSession = (ClientIoSession) session;
			SelectionKey selectKey = channel.register(selector, SelectionKey.OP_CONNECT, session);
			channel.connect(clientIoSession.getConnectAddress());
			session.setSelectionKey(selectKey);
		}
		
		//开启定时检查
		session.lastAccessTime = System.currentTimeMillis();
		session.lastReadTime = System.currentTimeMillis();
		session.lastWriteTime = System.currentTimeMillis();

		session.getOwnerAcceptor().getTimer().schedule(session.getCheckOverTime(), 
				session.getOwnerAcceptor().getConfigure().getOverTimeIntervalCheckTime(), 
				session.getOwnerAcceptor().getConfigure().getOverTimeIntervalCheckTime());

	}
	
	
	/**注册队列中的所有会话到选择器*/
	private void registerIoSessionsNow() {
		for(;;) {
			IoSessionImpl session = this.waitRegQueue.poll();
			if(session == null) {
				break;
			}
			
			try {
				registerIoSessionNow(session);
			} catch (IOException e) {
				//TODO:记录下异常
				e.printStackTrace();
				try {
					//注册失败时直接调用关闭，不用排程关闭，因为这时会话根本就没有在选择器中
					session.closeNow0();    
				} catch (IOException e1) {}
			}
		}
	}
	
	
//////////////////////////////////////完成所有需要移除的会话的正式关闭操作/////////////////////
	
	/**移除并关闭所有在移除队列中的会话*//*
	private void removeIoSessionsNow() {
		for(;;) {
			CloseFuture future = this.waitRemoveQueue.poll();
			if(future == null) {
				break;
			}
			
			removeIoSessionNow(future);
		}
	}*/
	
	
	/**立即关闭并从选择器中移除会话*/
	private void removeIoSessionNow(CloseFuture future) {
		IoSessionImpl session = (IoSessionImpl) future.getSession();
		
		
		//如果中途取消了删除，就重新安排控制
		if(future.isCannel()) {
			if(session.isClose()) {
				try {
					session.closeNow0();
				} catch (IOException e) {}
			}
			else {
			//中途取消关闭会话时，应该把会话复位
				if(session.isCloseing.compareAndSet(true, false)) {
					//由于是取消了关闭，会话要再次重新打开读的监听
					session.setReadControl(true);
					
					//重新加入一个控制
					this.scheduleControl(session);
				}
			}
		}
		else {

			try {
				session.closeNow();
				future.setComplete(null);

			} catch (IOException e) {
				e.printStackTrace();
				future.setComplete(e);
			}
			finally {
				this.onRemoveSession((IoSessionImpl) future.getSession());
				session.getOwnerAcceptor().getConfigure().getIoHandler().ioSessionClosed(future);
			}
		}
	}
	
	
	
	/**改变排程中的会话控制，让排程的会话得到写的事件*/
	private void changeControls() {
		while(true) {
			IoSessionImpl session = this.waitControlQueue.poll();
			if(session == null) {
				break;
			}
			
			if(!session.isClose()) {  //如果还没有正式关闭就执行
				session.isChangeingControl.set(true);
			
			
				session.setWriteControl(true);
			
				session.isChangeingControl.set(false);
			}
		}
	}
	
	

	@Override
	public void run() {

		this.isRunning = true;
		while(isRunning) {
			try {
				selector.select(1000);
			} catch (IOException e) {
				//TODO:记录异常
				e.printStackTrace();
			}

			for(Iterator<SelectionKey> i = selector.selectedKeys().iterator(); i.hasNext();) {
				SelectionKey sk = i.next();
				i.remove();

				
				IoSessionImpl session = (IoSessionImpl) sk.attachment();
				
				if(sk.isValid() == false) {
					try {
						session.closeNow0();
					} catch (IOException e) {}
					continue;
				}

				
				if(sk.isValid() && sk.isConnectable()) {
					//TODO:
					try {
						handleConnect(session);
					} catch (IOException e) {
						//TODO:记录下异常
						e.printStackTrace();
						ConnectFuture future =  (ConnectFuture) ((ClientIoSession) session).getConnectFuture();
						future.setComplete(e);          //设置完成
						session.close();
					}
				}
				
				if(sk.isValid() && sk.isReadable()) {
					handleRead(session);
				}

				if(sk.isValid() && sk.isWritable()) {
					handleWrite(session);
				}
				
				
			}
			
			
			this.handleOverTimeSessions();   //处理超时IoSession
			
			//this.removeIoSessionsNow();     //从移除队列中移除并关闭会话
			this.registerIoSessionsNow();   //从等待注册的队列中注册会话到选择器
			
			this.changeControls();          //改变会话控制，为需要写数据的会话加入OP_WRITE事件
		}
	}
	
	/**
	 * 处理超时IoSession
	 */
	public void handleOverTimeSessions() {
		for(;;) {
			IoSessionImpl session = this.waitOverTimeQueue.poll();
			if(session == null) {
				break;
			}
			
			session.notifyOverTime();
		}
	}
	
	
	/**处理会话连接*/
	public void handleConnect(IoSessionImpl session) throws IOException{
		if(session.getChannel().finishConnect()) {
			session.setReadControl(true);         //打开读
			session.setWriteControl(true);        //打开与
			session.setConnectControl(false);     //关闭
			this.onRegisterSession(session);      //session添加到管理器中

			ConnectFuture future =  (ConnectFuture) ((ClientIoSession) session).getConnectFuture();
			future.setComplete(null);                 //设置完成
		}
	}
	
	
	/**
	 * 处理会话读
	 * @param session
	 */
	public void handleRead(IoSessionImpl session) {
		try {
			session.readNow();
			
			session.lastReadTime = System.currentTimeMillis();
			session.lastAccessTime = session.lastReadTime;
			
			
		} catch (IOException e) {
			//e.printStackTrace();
			//TODO:打印异常
			session.close();
		}
	}

	
	
	/**
	 * <p>
	 * 写处理操作，//TODO:这里暂时考虑每次进行一条消息的写操作，如果消息的数据大于1k就放到下次
	 * 循环处理，//TODO:正在考虑是否按写的字节数来做为判断是在一次循环中写入小于多少的数据进入
	 * 下次循环，还是按消息条数来做判断
	 * </p>
	 * <br>
	 * @param session
	 */
	/*public void handleWrite(IoSessionImpl session) {
		Queue<IoFuture> writeQueue = session.getWriteQueue();
		
		IoFuture ioFuture =  writeQueue.peek();
		
		
		if(ioFuture == null) {
			session.setWriteControl(false);
			return;
		}
		
		if(ioFuture.getClass() == CloseFuture.class) {
			if(ioFuture.equals(session.getCloseFuture())) {
				//TODO:发生关闭事件
				this.removeIoSessionNow(session.getCloseFuture());
				writeQueue.poll();
				return;
			}
		}
			
		
		WriteFuture future = (WriteFuture) ioFuture;
		
		ByteBuffer outBuffer = future.getBuffer();
		
		int writelen = 0;            //写入字节的长度
		int count = 0;               //为了防止进入死循环的参数
		
		
		while(true) {
			count ++;
			try {
				writelen += session.getChannel().write(outBuffer);
			}
			catch(IOException e) {
				//TODO:记录异常
				e.printStackTrace();
				if(future.equals(writeQueue.peek())) {
					writeQueue.poll();
				}
				future.setComplete(e);
				break;
			}
			
			//检查是否还有残留的数据
			if(outBuffer.hasRemaining()) {
				if(writelen > MAX_OUT_SIZE) {
					//尽管还有残留数据，但是已经写入超过MAX_OUT_SIZE的数据，所为把残留的留到下次循环
					break;
				}
			}
			else {
				//已经没有残留的数据了
				if(future.equals(writeQueue.poll())) {
					future.setComplete(null);
					break;
				}
				else {
					//如果检查到被清理的队列头，与我们处理的队列头不符后，说明我们的程序在某些地方是不合理的
					//这时应该抛出错误，不在让程序继续执行下去了.
					throw new Error("发生严重错误");
					
					//TODO:考虑另外一种处理方法，而不是抛出错误
				}
			}
			
			if(count >= 10) {
				break;
			}
		}
		
		//session.setWriteControl(false);          //关闭写
		
		//如果会话中已经没有要写的数据了，就关闭会话中的写
		if(session.getWriteQueue().peek() == null) {
			session.setWriteControl(false);
			//this.scheduleControl(session);
		}
		
		session.lastWriteTime = System.currentTimeMillis();
		session.lastAccessTime = session.lastWriteTime;
	}*/
	
	/**
	 * 提供handleWrite调用的方法，主要是写数据到channel的操作
	 */
	private void handleWriteFuture(IoSessionImpl session, WriteFuture future) throws IOException {
		
		Queue<IoFuture> writeQueue = session.getWriteQueue();
		
		ByteBuffer outBuffer = future.getBuffer();
		
		int writelen = 0;
		for(int i=0; i< 10; i++) {
			writelen += session.getChannel().write(outBuffer);
			if(outBuffer.hasRemaining()) {
				if(writelen > MAX_OUT_SIZE) {
					break;
				}
			}
			else {
				if(future.equals(writeQueue.poll())) {
					future.setComplete(null);
					break;
				}
				else{
					throw new Error("发生严重错误");
				}
			}
		}
	}
	
	
	/**
	 * <p>
	 * 写处理操作，//TODO:这里暂时考虑每次进行一条消息的写操作，如果消息的数据大于1k就放到下次
	 * 循环处理，//TODO:正在考虑是否按写的字节数来做为判断是在一次循环中写入小于多少的数据进入
	 * 下次循环，还是按消息条数来做判断
	 * </p>
	 * <br>
	 * @param session
	 */
	public void handleWrite(IoSessionImpl session) {
		Queue<IoFuture> writeQueue = session.getWriteQueue();
		
		while(true) {
			IoFuture ioFuture =  writeQueue.peek();
			if(ioFuture == null) {
				session.setWriteControl(false);
				return;
			}
			
			if(ioFuture.isComplete()) {
				writeQueue.poll();
				return;
			}
			
			if(ioFuture.getClass() == CloseFuture.class) {
				if(ioFuture.equals(session.getCloseFuture())) {
					//TODO:发生关闭事件
					this.removeIoSessionNow(session.getCloseFuture());
					writeQueue.poll();
					return;
				}
			}
		
			//进行写数据的处理，如果写发生异常，就返回再次取队例中的写，直到不再发生异常
			try {
				handleWriteFuture(session, (WriteFuture) ioFuture);
			} catch (IOException e) {
				//TODO:记录异常信息
				e.printStackTrace();
				if(ioFuture.equals(writeQueue.peek())) {
					writeQueue.poll();
					((WriteFuture)ioFuture).setComplete(e);
				}
				else {
					throw new Error("发生严重错误");
				}
				continue;
			}
			
			
			
			//如果会话中已经没有要写的数据了，就关闭会话中的写
			if(session.getWriteQueue().peek() == null) {
				session.setWriteControl(false);
				//this.scheduleControl(session);
			}
			
			session.lastWriteTime = System.currentTimeMillis();
			session.lastAccessTime = session.lastWriteTime;
			return;
		}
	}
	
	
	
	
	/**排程控制,方便会话中加入写的事件*/
	public void scheduleControl(IoSessionImpl session) {
		if(!session.isChangeingControl.get()) {
			this.waitControlQueue.offer(session);
		}
		this.wakeup();
	}
	
	
	/**排程注册，方便会话注册*/
	public void scheduleRegister(IoSessionImpl session) {
		if(session == null) {
			return;
		}
		
		this.waitRegQueue.offer(session);
		this.wakeup();
	}

	
	
	////////////////////////////////////Listener//////////////////////////////
	void onRegisterSession(IoSessionImpl session) {
		Iterator<DispatcherEventlListener> iter = this.listenerSet.iterator();
		while(iter.hasNext()) {
			iter.next().onRegisterSession(session);
		}
	}
	
	void onRemoveSession(IoSessionImpl session) {
		Iterator<DispatcherEventlListener> iter = this.listenerSet.iterator();
		while(iter.hasNext()) {
			iter.next().onRemoveSession(session);
		}
	}
}
