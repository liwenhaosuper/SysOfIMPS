package com.imps.services.impl;

import com.imps.events.IVideoEvent;

public class P2PVideoService implements IVideoEvent{

	@Override
	public void onP2PVideoReq(String friName, String ip, int port) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onP2PVideoRsp(String friName, boolean result, String ip,
			int port) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onP2PVideoError(String msg, int errorCode) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onP2PVideoReqSuccess() {
		// TODO Auto-generated method stub
		
	}
	
}
