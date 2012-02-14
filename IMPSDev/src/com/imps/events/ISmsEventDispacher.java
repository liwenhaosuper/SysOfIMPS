package com.imps.events;

public interface ISmsEventDispacher {
	void addSmsEventHandler(ISmsEvent event);
	void removeSmsEventHandler(ISmsEvent event);
}
