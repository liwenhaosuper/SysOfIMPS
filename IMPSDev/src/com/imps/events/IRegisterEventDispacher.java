package com.imps.events;

public interface IRegisterEventDispacher {
	void addRegEventHandler(IRegisterEvent event);
	void removeRegEventHandler(IRegisterEvent event);
}
