package com.imps.services.impl;

import java.util.LinkedList;

import org.jboss.netty.buffer.ChannelBuffers;

import android.util.Log;

import com.imps.IMPSDev;
import com.imps.basetypes.OutputMessage;

public class DoodleSenderService extends Thread{
	private static boolean DEBUG = IMPSDev.isDEBUG();
	private static String TAG = DoodleSenderService.class.getCanonicalName();
	private LinkedList<OutputMessage> mMsgQueue=new LinkedList<OutputMessage>();;
	private boolean stop = false;
	private boolean isStarted = false;
	@Override
	public void run(){
		setStarted(true);
		while(!stop){
			while (mMsgQueue.size() > 1){
				OutputMessage outMsg = mMsgQueue.removeFirst();
		    	if(ServiceManager.getmDoodleService().getChannel()!=null&&ServiceManager.getmDoodleService().getChannel().isConnected()){
		    		ServiceManager.getmDoodleService().getChannel().write(ChannelBuffers.wrappedBuffer(
		    				outMsg.build()));
		    		if(DEBUG)Log.d(TAG,"Doodle data sent...");
		    	}else{
		    		if(DEBUG)Log.d(TAG,"Doodle connection lost...");
		    	}
			}
		}
	}
	public void stopSending(){
		stop = true;
		setStarted(false);
	}
	public void addMsg(OutputMessage msg){
		mMsgQueue.add(msg);
	}
	public void setStarted(boolean isStarted) {
		this.isStarted = isStarted;
	}
	public boolean isStarted() {
		return isStarted;
	}
}
