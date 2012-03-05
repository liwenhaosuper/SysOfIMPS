package com.imps.events;

public interface IConnectionEventDispacher {
	void addConnectionEventHandler(IConnectionEvent event);
	void removeConnectionEventHandler(IConnectionEvent event);
}
