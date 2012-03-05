package com.imps.server.net.impl;

import com.imps.server.net.IoSession;
import com.imps.server.net.impl.AbstractIoFuture;

class CloseFuture extends AbstractIoFuture {

	public CloseFuture(IoSession session) {
		super(session);
	}

	@Override
	public void completeRun() {

	}

}
