package com.imps.events;

public interface IConnEventDispacher {
	void addConnEventHandler(IConnEvent event);
	void removeConnEventHandler(IConnEvent event);
}
