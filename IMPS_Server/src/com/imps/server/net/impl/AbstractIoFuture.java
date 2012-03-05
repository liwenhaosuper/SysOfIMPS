package com.imps.server.net.impl;

import com.imps.server.net.IoFuture;
import com.imps.server.net.IoSession;

/**
 * <p>
 * 提供抽象的IoFuture服务
 */
public abstract class AbstractIoFuture implements IoFuture {
	
	private IoSession session;
	
	/**是否完成*/
	private boolean isComplete;
	
	/**是否被取消*/
	private boolean isCancel;
	
	/**异常类型*/
	private Throwable throwable;
	
	
	public AbstractIoFuture(IoSession session) {
		this.session = session;
	}
	
	@Override
	public void await()  {
		while(true) {
			if(isComplete()) {
				break;
			}
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {}
		}
	}

	@Override
	public void await(long timeout) {
		try {
			Thread.sleep(timeout);
		} catch (InterruptedException e) {}
	}

	
	@Override
	public void cancel() {
		synchronized (this) {
			isCancel = true;
		}
	}


	@Override
	public IoSession getSession() {
		return session;
	}

	@Override
	public Throwable getThrowable() {
		return this.throwable;
	}

	
	@Override
	public boolean isComplete() {
		synchronized (this) {
			return this.isComplete;
		}
	}

	@Override
	public boolean isError() {
		synchronized (this) {
			if(getThrowable() == null) {
				return false;
			}
			else {
				return true;
			}
		}
	}

	@Override
	public boolean isCannel() {
		synchronized (this) {
			if(this.isComplete) {
				return false;
			}
			
			return this.isCancel;
		}
	}

	
	/**设置完成*/
	public void setComplete(Throwable throwable) {
		synchronized (this) {
			if(isCancel) {  //如果任务还没有运行就取消了
				return;
			}
			
			this.isComplete = true;              //设置完成
			
			if(throwable != null) {
				this.throwable = throwable;
			}
			
			this.completeRun();
		}
	}	

	
	protected abstract void completeRun();
}
