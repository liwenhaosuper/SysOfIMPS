package com.imps.events;

public interface IRegisterEvent {
	void onRegError(String errorMsg,int errorCode);
	void onRegSuccess();
}
