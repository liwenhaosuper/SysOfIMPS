package com.imps.model;

import com.imps.net.handler.UserManager;



/**
 * Header:
 * 	    Command:   xxx
 * 		UserName:  xxx
 *      *From: xxx
 * 		Time: xxx
 * Body:
 * 		body: description
 * @author liwenhaosuper
 *
 */
public class MediaType extends IMPSType{
	
	private boolean send = true;
	protected String receiver = new String();
	protected String sender = new String();
	protected String stime = new String();
	
	public MediaType(int type,boolean send){
		super(type);
		this.setSend(send);
		if(send){
			sender = UserManager.getGlobaluser().getUsername();
		}
	}
	@Override
	public byte[] MediaWrapper(){
		if(isSend()){
			this.getmHeader().put("UserName", sender);
			this.getmHeader().put("Receiver", receiver);
			this.getmHeader().put("Time", stime);
			return super.MediaWrapper();
		}else{
			throw new UnsupportedOperationException("receiver type could not call wrapper");
		}
	}
	@Override
	public void MediaParser(byte[] data){
		if(isSend()){
			throw new UnsupportedOperationException("send type could not call parser");
		}
		super.MediaParser(data);
		this.stime = this.getmHeader().get("Time");
		this.sender = this.getmHeader().get("From");
		this.receiver = UserManager.getGlobaluser().getUsername();
	}
	public void setReceiver(String friName){
		this.receiver = friName;
	}
	public String getReceiver(){
		return this.receiver;
	}
	public void setSender(String friName){
		this.sender = friName;
	}
	public String getSender(){
		return this.sender;
	}
	public String getTime(){
		return this.stime;
	}
	public void setTime(String stime){
		this.stime = stime;
	}
	public void setSend(boolean send) {
		this.send = send;
	}
	public boolean isSend() {
		return send;
	}

}
