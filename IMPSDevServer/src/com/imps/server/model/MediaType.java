package com.imps.server.model;




/**
 * Header:
 * Body:
 * 		body: description
 * @author liwenhaosuper
 *
 */
public class MediaType extends IMPSType{
	
	protected String receiver = new String();
	protected String sender = new String();
	protected String stime = new String();
	
	public MediaType(int type){
		super(type);
	}
	@Override
	public byte[] MediaWrapper(){
		this.getmHeader().put("From", sender);
		this.getmHeader().put("Receiver", receiver);
		this.getmHeader().put("Time", stime);
		return super.MediaWrapper();
	}
	@Override
	public void MediaParser(byte[] data){
		super.MediaParser(data);
		this.sender = this.getmHeader().get("UserName");
		this.receiver = this.getmHeader().get("Receiver");
		this.stime = this.getmHeader().get("Time");
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
}
