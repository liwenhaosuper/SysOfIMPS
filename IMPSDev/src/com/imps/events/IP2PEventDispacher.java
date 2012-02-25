package com.imps.events;

public interface IP2PEventDispacher {
	void addP2PEventHandler(IP2PEvent event);
	void removeP2PEventHandler(IP2PEvent event);
}
