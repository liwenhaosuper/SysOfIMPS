package com.imps.events;

public interface IConnectionEvent extends IEvent{
	void onLoginError(String msg);
	void onLoginSuccess();
	
	void onRegError(String msg);
	void onRegSuccess();
}
