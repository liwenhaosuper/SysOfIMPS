package com.imps.server.net;

import java.io.IOException;

/**
 * 会话正在关闭中，在会话上进行IO操作
 *
 */
public class CloseingSessionException extends IOException {
	public CloseingSessionException(String msg) {
		super(msg);
	}
}
