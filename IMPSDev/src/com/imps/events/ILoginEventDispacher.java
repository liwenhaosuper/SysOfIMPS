package com.imps.events;

public interface ILoginEventDispacher {
	void addLoginEventHandler(ILoginEvent event);
	void removeLoginEventHandler(ILoginEvent event);
}
