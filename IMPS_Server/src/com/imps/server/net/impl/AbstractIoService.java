package com.imps.server.net.impl;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Iterator;
import java.util.Map;
import java.util.Timer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import com.imps.server.net.Configure;
import com.imps.server.net.IoFuture;
import com.imps.server.net.IoHandler;
import com.imps.server.net.IoService;
import com.imps.server.net.IoSession;
import com.imps.server.net.OverTimeHandler;
import com.imps.server.net.ProtocolHandler;
import com.imps.server.net.impl.DispatcherEventlListener;
import com.imps.server.net.impl.IoReadWriteMachine;
import com.imps.server.net.impl.IoReadWriteMachineManager;
import com.imps.server.net.impl.IoSessionImpl;
import com.imps.server.net.impl.ProtocolGroup;

/**
 * <p>
 * 服务器抽象，提供基本的的服务功能
 *
 */
public abstract class AbstractIoService implements IoService, DispatcherEventlListener{
	/**是否启动*/
	protected volatile boolean isStart;

	
	/**IO处理线程管理器*/
	private IoReadWriteMachineManager dispatcherManager;
	
	
	/**定时器*/
	private Timer timer;
	
	//TODO:初始化时给一个能预测到的参数值，以便提高map的访问性能 //TODO:
	private ConcurrentHashMap<Long, IoSession> ioSessionMap = new ConcurrentHashMap<Long, IoSession>();
	
	/**提供只读Map*/
	private Map<Long, IoSession> readOnlyIoSessionMap = java.util.Collections.unmodifiableMap(ioSessionMap);
	
	/**ID提供器*/
	private AtomicLong nextId = new AtomicLong(0);
	
	private Configure configure;
	
	protected Object stopLock = new Object();
	
	
	/**
	 * 启动定时器
	 */
	protected void startTimer() {
		if(timer == null) {
			timer = new Timer(true);
		}
	}
	
	protected void stopTimer() {
		if(timer != null) {
			timer.cancel();
		}
	}
	
	/**
	 * 启动IO处理线程
	 */
	protected void startIoReadWriteMachines() {
		
	/*	//TODO:测试代码，加入用于测试IoSession关闭后是否从Map中移走
		this.timer.schedule(new TimerTask() {
			public void run() {
				
			}
		}, 5000, period)*/
		dispatcherManager.start();
	}
	
	
	
	/**
	 * 停止IO处理线程
	 * @throws IOException
	 */
	protected void stopIoReadWriteMachines(){
		dispatcherManager.stop();
	}
	
	
	/**
	 * 初始经读写机
	 * @param readwriteThreadNum
	 * @throws Exception
	 */
	protected void initIoReadWriteMachines(int readwriteThreadNum) throws Exception{
		this.dispatcherManager.init(readwriteThreadNum);
	}
	
	
	
	/**
	 * 关闭所有会话，方法为阻塞
	 */
	protected void closeAllSession()  {
		Iterator<Long> iter = ioSessionMap.keySet().iterator();
		while(iter.hasNext()) {
			long sessionId = iter.next();
			IoSession session = ioSessionMap.get(sessionId);
			if(session != null) {
				IoFuture future = session.close();
				future.await(2000);
			}
		}
	}
	
	
	/**是否启动*/
	public boolean isStart() {
		return isStart;
	}
	
	/**
	 * 获得定时器，如果对像没有启动，返回的则是null
	 * @return
	 */
	Timer getTimer() {
		return timer;
	}

	
	protected long getNextSessionId() {
		return nextId.incrementAndGet();
	}
	
	public AbstractIoService() {
		dispatcherManager = new IoReadWriteMachineManager(this);
		//dispatcherManager.init(readwriteThreadNum);
		
	}

	
	/*public AbstractIoService(int readwriteThreadNum, int port) throws Exception {		
		this(readwriteThreadNum);
		bind(port);
	}*/
	
	
	
	protected IoReadWriteMachineManager getIoDispatcherManager() {
		return this.dispatcherManager;
	}

	

	
	/**
	 * 排程IoSession到指定的Io处理器中
	 * @param session
	 */
	public void scheduleToDispatcher(IoSessionImpl session) {
		if(session == null) {
			return;
		}
		
		IoReadWriteMachine dispatcher = getIoDispatcherManager().getNextDispatcher();
		
		session.setOwnerDispatcher(dispatcher);   //设置会话的归属发报机
		
		session.allocatInBuffer();                //分配输入Buffer
		
		dispatcher.scheduleRegister(session);     //排程注册
	}

	
	/*@Override //构建协议组
	public ProtocolHandler buildProtocolGroup(int prefixByteNum,
			Map<String, ProtocolHandler> handlerMap) {
		if(prefixByteNum <= 0) {
			prefixByteNum = 1;
		}
		
		ProtocolGroup group = new ProtocolGroup(prefixByteNum);
		
		Iterator<String> keyIter = handlerMap.keySet().iterator();
		while(keyIter.hasNext()) {
			String flagstr = keyIter.next();
			ProtocolHandler handler = handlerMap.get(flagstr);
			
			if(flagstr == null || handler == null) {
				continue;
			}
			group.addHandler(flagstr, handler);
		}
		
		return group;
	}*/
	
	
	@Override
	public ProtocolHandler buildProtocolGroup(int prefixByteNum, ProtocolHandler... handlers) {
		if(handlers == null || handlers.length == 0) {
			return null;
		}
		
		if(prefixByteNum <= 0) {
			prefixByteNum = 1;
		}
		
		ProtocolGroup group = new ProtocolGroup(prefixByteNum);
		int len = handlers.length;
		for(int i=0; i<len; i++) {
			ProtocolHandler handler = handlers[i];
			if(handler == null) {
				continue;
			}
			
			group.addHandler(handler);
		}
		
		return group;
	}
	
	
	
	@Override
	public IoSession getIoSession(long ioSessionId) {
		return ioSessionMap.get(ioSessionId);
	}




	@Override  //IoSession在发报机中注册后触发
	public void onRegisterSession(IoSessionImpl session) {
		//TODO:测试打印
		System.out.println("Session-"+session.getId() + "-Register");
		if(session == null) {
			return;
		}
		
		this.ioSessionMap.putIfAbsent(session.getId(), session);
		
		//TODO:触发IoHandler事件
	}





	@Override  //IoSession真正在发报机中关闭并移除时触发
	public void onRemoveSession(IoSessionImpl session) {
		//TODO:测试打印
		System.out.println("Session-"+session.getId() + "-Remove");
		if(session == null) {
			return;
		}
		
		this.ioSessionMap.remove(session.getId());
		
		//TODO:触发相应事件
	}

	
	
	

	@Override
	public Configure getConfigure() {
		return this.configure;
	}

	@Override
	public void setConfigure(Configure config) {
		this.configure = config;
	}


	


	@Override
	public void start() throws Exception {
		if(this.configure == null) {
			throw new Exception("服务不存在配置，不能启动....");
		}
	}

}
