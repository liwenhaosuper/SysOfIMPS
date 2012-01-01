package com.imps.server.net.impl;


import java.io.IOException;
import java.net.ServerSocket;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.imps.server.net.ProtocolHandler;
import com.imps.server.net.impl.AbstractIoService;
import com.imps.server.net.impl.IoSessionImpl;



/**
 * <p>
 * 用于构建服务器的具体实现类，实现类屏蔽了许多可以灵活设置的方法，比如设置IO处理线程的设置，而这个在扩展的IoConnector<br>
 * 中却又开放了，是因为我们认为服务器的IO处理线程是和实现的CPU个数挂勾的，我们认为要允份利用CPU资源每一个CPU可以利用一<br>
 * 线程，所以这里屏蔽是不想让使用者有更多的选择而感到不知所挫，
 * 
 * 
 * 关于IoConnector开发的原因:
 * 而IoConnector之所以开放，是因为IoConnector只是框架的一个扩展，是用于构建客户端应用的，最初的设想是让他构建代理服务器的<br>
 * ，不过后来经过大家的讨论，希望让框架能有客户端的支撑，所以在不修改原来的框架前提下开放IO处理线程的设置，这样可以方便的用<br>
 * 于构建代理服务器，与客户端应用。
 *
 */
public class IoServerImpl extends AbstractIoService implements Runnable{
	
	/**服务器监听Socket*/
	private ServerSocketChannel ssChannel;
	
	/**选择器*/
	private Selector selector;
	
	private boolean isRunning;
	
	private static final int CPU_NUM = Runtime.getRuntime().availableProcessors();
	
	public IoServerImpl() throws Exception{
		super();
		this.initIoReadWriteMachines(CPU_NUM);   //实始化读写机
	}

	
	@Override
	public void start() throws Exception {
		super.start();
		startTimer();              //启动定时器
		
		if(this.getConfigure().getAddress() == null) {
			throw new Exception("没有绑定地址");
		}
		
		
	
		selector = Selector.open();         //打开选择器
		
		ssChannel = ServerSocketChannel.open();
		ssChannel.configureBlocking(false);       //设定为非阻塞
		ServerSocket ss = ssChannel.socket();
		ss.bind(this.getConfigure().getAddress());
		
				
		ssChannel.register(selector, SelectionKey.OP_ACCEPT);
		
		this.startIoReadWriteMachines();
		
		Thread t = new Thread(this, "IoAcceptor");
		t.start();
		
		this.isStart = true;
	}
	
	
	
	
	public void stop() {
		synchronized (stopLock) {
			if(this.isRunning) {
				this.isStart = false;
				this.isRunning = false;
				this.selector.wakeup();
				getTimer().cancel();
				
				this.closeAllSession(); //一但调和就产生阻塞，只到所有会话关闭为止
				stopIoReadWriteMachines();
				
				try {
					this.selector.close();
					this.ssChannel.close();
				} catch (IOException e) {}
			}
		}
	}
	
	
	
	

	@Override
	public void run() {
		
		isRunning = true;
    	while(isRunning){
    		
    		try {
				selector.select();
			} catch (IOException e) {
				//TODO:记录下异常
				e.printStackTrace();
			}
    		
    		for(Iterator<SelectionKey> i = selector.selectedKeys().iterator(); i.hasNext();) {
    			SelectionKey sk = i.next();
    			i.remove();
    			
    			if(sk.isValid() == false) {
    				//TODO:考虑是否抛出异常
    			}
    			
    			if(sk.isAcceptable()) {
    				
    				SocketChannel sc = null;
    				try {
						sc = this.ssChannel.accept();
						sc.configureBlocking(false);
					} catch (IOException e) {
						//TODO:记录下此异常
						e.printStackTrace();
					}
					
					if(sc == null) {
						continue;
					}
					
					
					IoSessionImpl session = newIoSession(sc);
					scheduleToDispatcher(session);   //在一个发报机中排程注册
    			}
    		}
    	}	
	}

	
	/**
	 * 产生一个新的IoSession
	 * @param sc
	 * @return
	 */
	private IoSessionImpl newIoSession(SocketChannel sc) {
		long id = getNextSessionId();
		
		IoSessionImpl session = new IoSessionImpl(id, sc, this);
		return session;
	}

}
