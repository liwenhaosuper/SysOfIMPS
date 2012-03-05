package com.imps.events;

public interface ILoginEvent extends IEvent{
	void onLoginError(String errorStr,int errorCode);
	void onLoginSuccess();
}
