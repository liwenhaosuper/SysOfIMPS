package com.imps.services.impl;

import java.io.IOException;
import java.net.DatagramPacket;

import android.util.Log;

import com.imps.IMPSDev;
import com.imps.R;
import com.imps.activities.AudioChat;
import com.imps.basetypes.OutputMessage;
import com.imps.events.IAudioEvent;
import com.imps.net.handler.MessageFactory;
import com.imps.net.handler.UserManager;

public class P2PAudioService implements IAudioEvent{

	private static boolean DEBUG =IMPSDev.isDEBUG();
	private static String TAG = P2PAudioService.class.getCanonicalName();
	public P2PAudioService(){
		ServiceManager.getmNetLogic().addAudioEventHandler(this);
	}
	@Override
	public void onP2PAudioReq(String friName, String ip, int port) {
		if(DEBUG) Log.d(TAG,"audio from "+friName+" received...");
		ServiceManager.showNotification(R.drawable.new_audio_notification, R.drawable.new_audio_notification,
				friName+":"+IMPSDev.getContext().getString(R.string.audio_chat_request_notify), AudioChat.class, friName,ip,Integer.toString(port));

	}

	@Override
	public void onP2PAudioRsp(String friName, boolean result, String ip,
			int port) {
	}

	@Override
	public void onP2PAudioError(String msg, int errorCode) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onP2PAudioReqSuccess() {
		// TODO Auto-generated method stub
		
	}
	public void SendPTPAudioReq(String friName) {
		// TODO Auto-generated method stub
		OutputMessage output = MessageFactory.createCPTPAudioReq(UserManager.getGlobaluser().getUsername(),
				friName);
		DatagramPacket udpPacket = ServiceManager.getmMedia().getPacket();
		udpPacket.setData(output.build());
		try {
			ServiceManager.getmMedia().getSocket().send(udpPacket);
			if(DEBUG) Log.d(TAG,"Audio req sent...");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void SendPTPAudioRsp(String friName,boolean res) {
		// TODO Auto-generated method stub
		OutputMessage output = MessageFactory.createCPTPAudioRsp(UserManager.getGlobaluser().getUsername(),
				friName,res);
		DatagramPacket udpPacket = ServiceManager.getmMedia().getPacket();
		udpPacket.setData(output.build());
		try {
			ServiceManager.getmMedia().getSocket().send(udpPacket);
			if(DEBUG) Log.d(TAG,"Audio rsp sent...");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
