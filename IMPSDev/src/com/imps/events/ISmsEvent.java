package com.imps.events;

import com.imps.basetypes.MediaType;

public interface ISmsEvent extends IEvent{
	void onSmsRecv(MediaType media);
	void onSmsSendSuccess(String ret,int smsId);
	void onSmsSendFail(String errorCode,int smsId);
	void onSmsSendTimeOut(String errorCode,int smsId);
	
	void onAudioRecv(String friName,int sid,boolean isEOF,byte[] data);
	void onAudioSendSuccess(String ret,int smsId);
	void onAudioSendFail(String errorCode,int smsId);
	void onAudioSendTimeOut(String errorCode,int smsId);
	
	void onImageRecv(String friName,int sid,boolean isEOF,byte[] data);
	void onImageSendSuccess(String ret,int smsId);
	void onImageSendFail(String errorCode,int smsId);
	void onImageSendTimeOut(String errorCode,int smsId);
}
