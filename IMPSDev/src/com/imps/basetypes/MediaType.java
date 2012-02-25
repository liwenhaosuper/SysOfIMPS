package com.imps.basetypes;

import java.util.ArrayList;
import java.util.List;

public class MediaType {
	public static byte SMS = 1;
	public static byte IMAGE = 2;
	public static byte AUDIO = 3;
	public static int totalId = 1;
	public static byte from = 1;
	public static byte to = 2;
	protected int id;
	protected byte type;
	private byte direcet = from;
	protected List<byte[]> content = new ArrayList<byte[]>();
	protected String msg = new String();
	protected String friName = new String();
	protected String stime = new String();
	public MediaType(byte type){
		this.type = type;
		this.id = totalId++;
		this.direcet = from;
	}
	public MediaType(byte type,byte direct){
		this.type = type;
		this.id = totalId++;
		this.direcet = direct;
	}
	public MediaType(byte type,List<byte[]> data){
		this.type = type;
		this.content = data;
		this.id = totalId++;
		this.direcet = from;
	}
	public MediaType(byte type,List<byte[]> data,byte direct){
		this.type = type;
		this.content = data;
		this.id = totalId++;
		this.direcet = direct;
	}
	public MediaType(byte type,String msg){
		this.type = type;
		this.msg = msg;
		this.id = totalId++;
		this.direcet = from;
	}
	public MediaType(byte type,String msg,byte direct){
		this.type = type;
		this.msg = msg;
		this.id = totalId++;
		this.direcet = direct;
	}
	public byte getType(){
		return type;
	}
	public void setType(byte type){
		this.type = type;
	}
	public int getId(){
		return id;
	}
	public void setId(int id){
		this.id = id;
	}
	public List<byte[]> getContant(){
		return content;
	}
	public void setContant(List<byte[]> content){
		this.content = content;
	}
	public String getMsgContant(){
		return msg;
	}
	public void setMsgContant(String msg){
		this.msg = msg;
	}
	public void addFrame(byte[] data){
		this.content.add(data);
	}
	public void addFrame(int id,byte[] data){
		if(this.id==id){
			this.content.add(data);
		}
	}
	public void setFriend(String friName){
		this.friName = friName;
	}
	public String getFriend(){
		return this.friName;
	}
	public String getTime(){
		return this.stime;
	}
	public void setTime(String stime){
		this.stime = stime;
	}
	public void setDirecet(byte direcet) {
		this.direcet = direcet;
	}
	public byte getDirecet() {
		return direcet;
	}
}
