package com.imps.server.net;

import java.io.IOException;

/**
 * 会话已经被关闭了，再在会话上进行IO操作异常
 *
 */
public class ClosedSessionException extends IOException {
	public ClosedSessionException(String msg) {
		super(msg);
	}
}
