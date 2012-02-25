package com.imps.events;

public interface IVideoEvent extends IEvent{
	void onP2PVideoReq(String friName,String ip,int port);
	void onP2PVideoRsp(String friName,boolean result,String ip,int port);
	void onP2PVideoError(String msg,int errorCode);
	void onP2PVideoReqSuccess();
}
