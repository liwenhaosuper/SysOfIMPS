package com.imps.basetypes;

import java.util.LinkedList;
import java.util.List;

public class ListContentEntity {
    private String name;
    private String time;
    private String text;
    private String imagePath;
    public LinkedList<byte[]> audioData;
    private int layoutID;
    public byte status = MUTE;
	public static final byte MUTE = 0;
	public static final byte PLAY = 1;
	public static final int MESSAGE_FROM = 0;
	public static final int MESSAGE_TO = 1;
	public static final int MESSAGE_FROM_PICTURE = 2;
	public static final int MESSAGE_TO_PICTURE = 3;
	public static final int MESSAGE_FROM_AUDIO = 4;
	public static final int MESSAGE_TO_AUDIO = 5;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDate() {
		return time;
	}
	public void setDate(String date) {
		this.time = date;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public int getLayoutID() {
		return layoutID;
	}
	public void setLayoutID(int layoutID) {
		this.layoutID = layoutID;
	}
    
	public ListContentEntity() {
	}
	public ListContentEntity(String name, String date, String text, int layoutID) {
		super();
		this.name = name;
		this.time = date;
		this.text = text;
		this.layoutID = layoutID;
	}
	public ListContentEntity(String name,String date,String text,int layoutID,List<byte[]> data){
		super();
		this.name = name;
		this.time = date;
		this.text = text;
		this.layoutID = layoutID;
		this.audioData = (LinkedList)data;
	}
	public ListContentEntity(String name,String date,String text,int layoutID,String path){
		super();
		this.name = name;
		this.time = date;
		this.text = text;
		this.layoutID = layoutID;
		this.imagePath = path;
	}
	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}
	public String getImagePath() {
		return imagePath;
	}
	
	
    
}
