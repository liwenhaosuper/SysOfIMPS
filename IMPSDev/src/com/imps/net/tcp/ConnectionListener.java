package com.imps.net.tcp;

public interface ConnectionListener {
	public void onTCPConnect();
	public void onTCPDisconnect();
}
