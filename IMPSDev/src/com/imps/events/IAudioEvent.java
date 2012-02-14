package com.imps.events;

public interface IAudioEvent extends IEvent{
	void onP2PAudioReq(String friName,String ip,int port);
	void onP2PAudioRsp(String friName,boolean result,String ip,int port);
	void onP2PAudioError(String msg,int errorCode);
	void onP2PAudioReqSuccess();
}
