package com.imps.net.handler;

import java.util.concurrent.ConcurrentLinkedQueue;

import org.jboss.netty.buffer.ChannelBuffers;

import android.util.Log;

import com.imps.IMPSDev;
import com.imps.model.IMPSType;
import com.imps.model.MediaType;
import com.imps.services.impl.ServiceManager;
import com.imps.util.LocalDBHelper;


public class MsgHandler {
	private static MsgHandler instance;	
	public boolean DEBUG = true;
	public String TAG ="MsgHandler";
	protected volatile boolean mSenderStop = false;
	private ConcurrentLinkedQueue<IMPSType> mDatas;
	private Object mSenderLock = new Object();
	private InnerSendLooper mSendLooper;
	private LocalDBHelper mLocalDb;
	protected MsgHandler(){
		mDatas = new ConcurrentLinkedQueue<IMPSType>();
		mSendLooper = new InnerSendLooper();
		mLocalDb = new LocalDBHelper(IMPSDev.getContext());
	}
	public static MsgHandler getInstance(){
		if(instance==null){
			instance = new MsgHandler();
		}
		return instance;
	}
	public void start(){
		if(mSendLooper.isAlive()){
			mSendLooper.stop();
		}
		mSendLooper.start();
	}
	public void sendRequest(MediaType media){
		if(media!=null){
			mDatas.add(media);
			mSenderLock.notify();
			//TODO: Add to db
			saveMessage(media);
		}
	}
	protected class InnerSendLooper extends Thread{
		@Override
		public void run(){
			while(!mSenderStop){
				if(mDatas.isEmpty()){
					try {
						mSenderLock.wait(1*60*1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				IMPSType item = mDatas.peek();
				if(ServiceManager.getmTcpConn().getChannel().isConnected()){
					ServiceManager.getmTcpConn().getChannel().write(ChannelBuffers.wrappedBuffer(item.MediaWrapper()));
					mDatas.poll();
				}else{
					try {
						mSenderLock.wait(1*60*1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
	public void recvRequest(MediaType media){
		//TODO
		if(DEBUG)Log.d(TAG,"recvRequest");
		saveMessage(media);
	}
	private void saveMessage(MediaType media){
		if(media!=null){
			try {
				mLocalDb.storeMsg(media);
			} catch (Exception e) {
				if(DEBUG) e.printStackTrace();
			} 
		}
	}
}
