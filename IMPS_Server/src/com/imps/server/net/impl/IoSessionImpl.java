package com.imps.server.net.impl;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.List;
import java.util.Queue;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import com.imps.server.net.ClosedSessionException;
import com.imps.server.net.CloseingSessionException;
import com.imps.server.net.IoFuture;
import com.imps.server.net.IoSession;
import com.imps.server.net.NetMessage;

public class IoSessionImpl implements IoSession{
	
	/**sessionId*/
	private long id;
	
	/**属性映射表*/
	private HashMap<String, Object> attributeMap = new HashMap<String, Object>();
	
	/**是否正式被关闭*/
	private volatile boolean isClose;
	
	/**是否正在关闭中*/
	AtomicBoolean isCloseing = new AtomicBoolean(false);
	
	/**是否正在改变控制*/
	AtomicBoolean isChangeingControl = new AtomicBoolean(false);
	
	
	/**是否正在超时处理中超时处理中,false:还未开始超时处理,true:超时处理开始了*/
	AtomicBoolean isOverTimeHandleing = new AtomicBoolean(false);
	
	/**通道*/
	private SocketChannel channel;
	
	/**选择键*/
	private SelectionKey selectionKey;
	
	/**写消息时，是否为阻塞写*//*
	private boolean isblockWrite;
	
	*//**读消息时，是否阻塞读*//*
	private boolean isblockRead;
	
	*//**关闭会话时，是否阻塞关闭*//*
	private boolean isblockClose;*/
	
	/**最近读时间*/
	long lastReadTime = System.currentTimeMillis();
	
	/**最近写时间*/
	long lastWriteTime = System.currentTimeMillis();
	
	/**最近访问时间*/
	long lastAccessTime = System.currentTimeMillis();
	
	/**写队列*/
	private ConcurrentLinkedQueue<IoFuture> writeQueue = new ConcurrentLinkedQueue();
	
	/**IoSession在哪个地方注册的*/
	private IoReadWriteMachine ownerDispatcher;
	
	/**IoSession的归属接收器*/
	private AbstractIoService ownerAcceptor;
	
	/**输入buffer*/
	private ByteBuffer inBuffer;
	
	/**检查超时*/
	private OverTimeCheckTask checkOverTime;
	
	/**关闭会话时的Future*/
	private CloseFuture closeFuture = new CloseFuture(this);
	
	//TODO:考虑加入到目前为止，总共接收了多少数据，按字节计算
	
	IoSessionImpl() {
		checkOverTime = new OverTimeCheckTask();
	}
	
	protected IoSessionImpl(SocketChannel channel, IoReadWriteMachine dispatcher) {
		this.channel = channel;
		this.ownerDispatcher = dispatcher;
		checkOverTime = new OverTimeCheckTask();
		//this.inBuffer = this.ownerDispatcher.getMemoryManager().allocat(10240);        //分配10的输入cache 
	}
	
	
	protected IoSessionImpl(long id, SocketChannel channel, AbstractIoService acceptor) {
		this.id = id;
		this.channel = channel;
		this.ownerAcceptor = acceptor;
		checkOverTime = new OverTimeCheckTask();
	}
	
	CloseFuture getCloseFuture() {
		return closeFuture;
	}
	
	
	/**获得超时检查任务*/
	OverTimeCheckTask getCheckOverTime() {
		return checkOverTime;
	}
	
	/**获是通道*/
	public SocketChannel getChannel() {
		return channel;
	}
	
	SelectionKey getSelectionKey(){
		return selectionKey;
	}
	
	void setSelectionKey(SelectionKey key) {
		this.selectionKey = key;
	}
	
	/**
	 * 设置归属发报机
	 * @param dispatcher
	 */
	void setOwnerDispatcher(IoReadWriteMachine dispatcher) {
		this.ownerDispatcher = dispatcher;
	}
	
	/**
	 * 分配输入buffer
	 */
	void allocatInBuffer() {
		//TODO:
		//TODO:完善内存管理后，输入buffer从内存管理中获得
		//ByteBuffer buffer = this.ownerDispatcher.getMemoryManager().allocat();
		this.inBuffer = this.ownerDispatcher.getMemoryManager().allocat();
		
		//TODO:测试打印
		System.out.println("AllocatThreadName = " + this.ownerDispatcher.threadName);
//		System.out.println("AllocatNum = " + this.ownerDispatcher.getMemoryManager().getBufferNum());
		
		//this.inBuffer = ByteBuffer.allocateDirect(1024 * 5);
		
	}
	
	
	protected AbstractIoService getOwnerAcceptor() {
		return ownerAcceptor;
	}
	
	
	@Override
	public void addAttribute(String key, Object obj) {
		synchronized (attributeMap) {
			attributeMap.put(key, obj);
		}
	}

	@Override
	public int available() {
		throw new java.lang.UnsupportedOperationException("还没有实现");
	}

	
	@Override //关闭会话，关闭会话是异步关闭，如果需要同步，调用IoFuture上的wait
	public IoFuture close() {
		//TODO:测度代码
		System.out.println("IoSession-" + id + "-Close");
		
		if(this.isCloseing.compareAndSet(false, true)) {
			if(this.closeFuture == null) {
				closeFuture = new CloseFuture(this);
			}
			
			this.writeQueue.offer(closeFuture);
			
			//已经进入关闭会话的流程，此时是不允许接收任何来自网络的数据了，所以要关闭读的监听
			this.setReadControl(false);
			
			//IO会话加入排程控制
			ownerDispatcher.scheduleControl(this);
		
		}
		return this.closeFuture; 
	}

	
	/**在发报机中调用的立即关闭，调用会检查关闭是否在进行中*/
	void closeNow() throws IOException {
		if(this.isCloseing.get()) {
			closeNow0();
			this.isClose = true;		
		}
	}
	
	
	@Override
	public Object getAttribute(String key) {
		synchronized (attributeMap) {
			return attributeMap.get(key);
		}
	}

	@Override
	public long getId() {
		return id;
	}

	@Override /**是否正式关闭*/
	public boolean isClose() {
		return isClose;
	}
	
	
	/**是否正在半闭中*/
	public boolean isCloseing() {
		return isCloseing.get();
	}
	

	/**马上从网络读取消息*/
	void readNow() throws IOException {
		int readlen = this.channel.read(this.inBuffer);
		
		if(readlen == -1) {  //到达流末
			throw new IOException("End_Stream");
		}
		
		if(readlen > 0) {
			
			//当没有设置协议处者时
			if(ownerAcceptor.getConfigure().getProtocolHandler() == null) {   
				inBuffer.flip();
				byte[] msgdata = new byte[inBuffer.remaining()];
				inBuffer.get(msgdata);
				inBuffer.clear();
				ownerAcceptor.getConfigure().getIoHandler().messageReceived(this, msgdata);
			}
			else {  //当设置了协议处理者时
				ByteBuffer readBuf = inBuffer.asReadOnlyBuffer();
				readBuf.flip();

				List<NetMessage> list = ownerAcceptor.getConfigure().getProtocolHandler().onData(readBuf, this);

				//TODO:如果得到错误消息，应该立即终止读取，并关闭Session
				
				if(list==null)
				{
					System.out.println("message is null");
					return;
				}
					
				int size = list.size();
				if(list != null && size > 0) {
					inBuffer.position(readBuf.position());
					inBuffer.limit(readBuf.limit());

					inBuffer.compact();                  //压缩，把剩余的字节放到inBuffer的最前面
					inBuffer.limit(inBuffer.capacity());

					//处理消息
					for(int i=0; i<size; i++) {
						NetMessage msg = list.get(i);
						if(msg.equals(NetMessage.ERROR_MSG)) {  //协议解析发生不可恢复错误
							this.close();     //发出关闭请求
							return;      //发生错误后，不能下文处理了.
						}
						else {
							this.ownerAcceptor.getConfigure().getIoHandler().messageReceived(this, msg);
						}
					}
				}
			}
		}
	}
	
	
	
	void closeNow0() throws IOException {
		//TODO:测试代码
		System.out.println("IoSession-" + id + "-CloseNow");
		
		if(this.selectionKey != null) {
			this.selectionKey.cancel();
		}
		
		if(channel != null) {
			channel.close();
			channel.socket().close();
		}
		
		try {
			if(this.inBuffer != null) {
				this.ownerDispatcher.getMemoryManager().free(this.inBuffer);
			}
		}
		catch(Exception e) {}
		
		//this.isClose = true;		
	}
	
	
	@Override
	public NetMessage read() {
		throw new java.lang.UnsupportedOperationException("还没有实现");
	}

	@Override
	public void read(NetMessage[] msgs) {
		throw new java.lang.UnsupportedOperationException("还没有实现");
		
	}

	@Override
	public Object removeAttribute(String key) {
		synchronized (attributeMap) {
			return attributeMap.remove(key);
		}
	}

	@Override/**写消息*/
	public IoFuture write(NetMessage msg) {
		if(msg == null) {
			throw new IllegalArgumentException("msg is null");
		}
		
		WriteFuture future = new WriteFuture(this, ByteBuffer.wrap(msg.getContent()));
		
		//会话已经被关闭了
		if(isClose) {
			future.setComplete(new ClosedSessionException("会话已经被关闭了...."));
			return future;
		}
		
		if(!this.isCloseing.get()) {
			this.writeQueue.offer(future);
			
			/*//判断协议处理者的要求是否为写完后关闭
			if(this.ownerAcceptor.getProtocolHandler().isClose()) {
				this.close();
			}*/
		
			//IO会话加入排程控制
			ownerDispatcher.scheduleControl(this);
		
			//ownerDispatcher.wakeup();
		}
		else {
			future.setComplete(new CloseingSessionException("会话正处于正在关闭状态中...."));
		}
		
		return future;
	}

	
	
	
	/**获得写队列*/
	protected Queue<IoFuture> getWriteQueue() {
		return this.writeQueue;
	}
	
	/**
	 * <p>
	 * 设置写控制
	 * </p>
	 * <br>
	 * @param isopen 是否打开写
	 */
	void setWriteControl(boolean isopen) {
		if(!selectionKey.isValid()) {
			return;
		}
		
		int op = this.selectionKey.interestOps();
		if(isopen) {
			op = op | SelectionKey.OP_WRITE;
		}
		else {
			op = op ^ SelectionKey.OP_WRITE;
		}
		this.selectionKey.interestOps(op);
	}
	
	
	/**
	 * <p>
	 * 设置读控制
	 * </p>
	 * <br>
	 * @param isopen 是否打开读
	 */
	void setReadControl(boolean isopen) {
		if(!selectionKey.isValid()) {
			return;
		}
		
		int op = this.selectionKey.interestOps();
		if(isopen) {
			op = op | SelectionKey.OP_READ;
		}
		else {
			op = op ^ SelectionKey.OP_READ;
		}
		this.selectionKey.interestOps(op);
	}

	
	void setConnectControl(boolean isopen) {
		if(!selectionKey.isValid()) {
			return;
		}
		
		int op = this.selectionKey.interestOps();
		if(isopen) {
			op = op | SelectionKey.OP_CONNECT;
		}
		else {
			op = op ^ SelectionKey.OP_CONNECT;
		}
		this.selectionKey.interestOps(op);
	}
	
	
	/**通知超时了*/
	void notifyOverTime() {
		long currTime = System.currentTimeMillis();
		
		long bathOverTime = ownerAcceptor.getConfigure().getBothOverTime();
		
		if(bathOverTime > 0) {
			if((currTime - lastAccessTime) > bathOverTime) {
				if(isOverTimeHandleing.get()) {
					//都超时了
					this.ownerAcceptor.getConfigure().getOverTimeHandler().onBothOverTime(this);
				}
				isOverTimeHandleing.set(false);
				return;
			}
		}
		
		long readOverTime = ownerAcceptor.getConfigure().getReadOverTime();
		
		if(readOverTime > 0) {
			if((currTime - this.lastReadTime) > readOverTime) {
				if(isOverTimeHandleing.get()) {
				//都超时了
					this.ownerAcceptor.getConfigure().getOverTimeHandler().onReadOverTime(this);
				}
			}
		}
		
		long writeOverTime = ownerAcceptor.getConfigure().getWriteOverTime();
		
		if(writeOverTime > 0) {
			if((currTime - this.lastWriteTime) > writeOverTime) {
				if(isOverTimeHandleing.get()) {
					//都超时了
					this.ownerAcceptor.getConfigure().getOverTimeHandler().onWriterOverTime(this);

				}
			}
		}
		isOverTimeHandleing.set(false);
	}
	
	
	
	/**定时检查是否超时*/
	private class OverTimeCheckTask extends TimerTask {

		@Override
		public void run() {
			long currTime = System.currentTimeMillis();
			long bothOverTime = ownerAcceptor.getConfigure().getBothOverTime();
			long readOverTime = ownerAcceptor.getConfigure().getReadOverTime();
			long writeOverTime = ownerAcceptor.getConfigure().getWriteOverTime();
			if(bothOverTime <=0 || readOverTime <= 0 || writeOverTime <=0 ) {
				//TODO:不存在超时处理
				return;
			}
			//检查是否有超时产生，有的话就排序到IO处理线程中去
			if((currTime - lastAccessTime) > bothOverTime ||
					(currTime - lastReadTime) > readOverTime ||
					(currTime - lastWriteTime) > writeOverTime) {
				if(!IoSessionImpl.this.isCloseing() || !IoSessionImpl.this.isClose()) {
					if(isOverTimeHandleing.compareAndSet(false, true)) {
						ownerDispatcher.scheduleOverTime(IoSessionImpl.this); //排程	
					}
				}
			}
		}
		
	}



	@Override
	public IoFuture connect() {
		throw new java.lang.UnsupportedOperationException("不能进行操作....");
	}
}
