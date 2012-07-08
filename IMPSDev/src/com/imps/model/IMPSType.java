package com.imps.model;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

public abstract class IMPSType {
	
	//media type
	public static int SMS = 1;
	public static int IMAGE = 2;
	public static int AUDIO = 3;
	public static int FILE = 4;
	public static int COMMAND = 5;
	
	
	private String CHARSET = "utf-8";
	protected int type = COMMAND;//media type
	protected byte[] body;
	protected HashMap<String,String> mHeader = new HashMap<String,String>();
	
	public IMPSType(int type){
		this.type = type;
	}
	public int getType(){
		return type;
	}
	public void setType(int type){
		this.type = type;
	}	
	public void setContent(byte[] data){
		body = data;
	}
	public byte[] getContent(){
		return body;
	}
	public byte[] MediaWrapper(){
		OutputMessage out = new OutputMessage();
		try {
			out.getOutputStream().writeByte(type);
			out.getOutputStream().write(getmHeader().size());
			if(getmHeader()!=null&&getmHeader().size()>0){
				Iterator iter = getmHeader().entrySet().iterator();
           	 	while(iter.hasNext()){
           	 		Entry entry = (Entry)iter.next();
           	 		String nm = (String)entry.getKey();
           	 		out.getOutputStream().write(nm.getBytes("").length);
           	 		out.getOutputStream().write(nm.getBytes(""));
           	 		String value = (String)entry.getValue();
           	 		out.getOutputStream().write(value.getBytes("").length);
           	 		out.getOutputStream().write(value.getBytes(""));
           	 	}
           	 	byte[] content = getContent();
           	 	out.getOutputStream().write(content.length);
           	 	if(content.length>0){
           	 		out.getOutputStream().write(content);
           	 	}
            }
			return out.build();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	public void MediaParser(byte[] data){
		InputMessage in = new InputMessage(data);
		try {
			type = in.getInputStream().readByte();
			//read header
			int len = in.getInputStream().readInt();
			byte[] key = new byte[0];
			byte[] value = new byte[0];
			for(int i=0;i<len;i++){
			    int sz = in.getInputStream().readInt();
			    key = new byte[sz];
			    in.getInputStream().read(key);
			    sz = in.getInputStream().readInt();
			    value = new byte[sz];
			    in.getInputStream().read(value);
			    getmHeader().put(new String(key,getCHARSET()), new String(value,getCHARSET()));
			}
			//read body
			len = in.getInputStream().readInt();
			if(len>0){
				key = new byte[len];
				in.getInputStream().read(key);
				setContent(key);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void setmHeader(HashMap<String,String> mHeader) {
		this.mHeader = mHeader;
	}
	public HashMap<String,String> getmHeader() {
		return mHeader;
	}
	public void setCHARSET(String cHARSET) {
		CHARSET = cHARSET;
	}
	public String getCHARSET() {
		return CHARSET;
	}
	public String toString(){
		String result ="";
		result+="type:";
		result+=type;
		result+=";\theader{\t";
		if(getmHeader()!=null&&getmHeader().size()>0){
			Iterator iter = getmHeader().entrySet().iterator();
       	 	while(iter.hasNext()){
       	 		Entry entry = (Entry)iter.next();
       	 		String nm = (String)entry.getKey();
       	 		result+=nm;
       	 		result+=":";
       	 		String value = (String)entry.getValue();
       	 		result+=value;
       	 		result+=";";     	 		
       	 	}
        }
		result+="};";
		result+="content:";
		if(body==null){
			result+="null";
		}else{
			result+=body.length;
		}
		result+="\n";
		return result;
	}
}
