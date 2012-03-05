package com.imps.events;

import java.util.List;

public interface IContactEvent extends IEvent{
	void sendFriListReq();
	void sendUpdateMyStatusReq(byte status);
	
	void onRecvFriendReq(String friName);
	void onRecvFriendRsp(String friName,boolean rel);
	
	void onUpdateFriStatus();
	void onUpdateFriList();
	
	void sendSearchFriendReq(String keyword);
	void onRecvSearchFriendRsq(List< String> friends);
	
	void sendAddFriendReq(String friname);
	void onRecvAddFriendReq(String friname);
	void sendAddFriendRsp(String friname,boolean res);
	void onRecvAddFriendRsp(String friname,boolean res);
}
