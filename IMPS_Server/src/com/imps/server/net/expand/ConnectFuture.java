package com.imps.server.net.expand;

import com.imps.server.net.IoSession;
import com.imps.server.net.impl.AbstractIoFuture;

/**
 * <p>
 * 连接的异步计算结果
 */
public class ConnectFuture extends AbstractIoFuture {

	public ConnectFuture(IoSession session) {
		super(session);
	}

	@Override
	protected void completeRun() {
		// TODO Auto-generated method stub
		
	}

}
