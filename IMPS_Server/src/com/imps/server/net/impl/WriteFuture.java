package com.imps.server.net.impl;

import java.nio.ByteBuffer;

import com.imps.server.net.IoSession;
import com.imps.server.net.impl.AbstractIoFuture;

class WriteFuture extends AbstractIoFuture {
	
	private ByteBuffer buffer;

	WriteFuture(IoSession session, ByteBuffer buffer) {
		super(session);
		this.buffer = buffer;
	}

	
	ByteBuffer getBuffer() {
		return buffer;
	}


	@Override
	public void completeRun() {
		
		
	}
}
