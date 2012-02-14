package com.imps.events;

public interface IP2PEvent extends IEvent{
	
	void onP2PAudioReq(String friName,String ip,int port);
	void onP2PAudioRsp(String friName,boolean result,String ip,int port);
	void onP2PAudioError(String msg);
	void onP2PAudioReqSuccess();
	
	void onP2PVideoReq(String friName,String ip,int port);
	void onP2PVideoRsp(String friName,boolean result,String ip,int port);
	void onP2PVideoError(String msg);
	void onP2PVideoReqSuccess();
	
	void onRecvSendPTPVideoRsp(String friName,boolean rel,String ip,int port);
	void onRecvSendPTPVideoReq(String friName,String ip,int port);
	
	void onRecvSendPTPAudioRsp(String friName,boolean rel,String ip,int port);
	void onRecvSendPTPAudioReq(String friName,String ip,int port);
	
	void SendPTPAudioReq(String friName,String ip,int port);
	void SendPTPAudioRsp(String friName,boolean rel,String ip,int port);
	void SendPTPVideoReq(String friName,String ip,int port);
	void SendPTPVideoRsp(String friName,boolean rel,String ip,int port);
	
}
