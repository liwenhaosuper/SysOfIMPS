package com.imps.server.net;
/**
 * <p>
 * Io处理适配�?
 */
public abstract class IoHandlerAdapter implements IoHandler {

	@Override
	public void ioSessionClosed(IoFuture future) {
		// TODO Auto-generated method stub

	}

	@Override
	public void messageReceived(IoSession session, NetMessage msg) {
		// TODO Auto-generated method stub

	}

	@Override
	public void messageReceived(IoSession session, byte[] msgdata) {
		// TODO Auto-generated method stub

	}

}
