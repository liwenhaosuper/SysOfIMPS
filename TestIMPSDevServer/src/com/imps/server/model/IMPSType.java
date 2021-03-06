package com.imps.server.model;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

public abstract class IMPSType {
	
	//media type
	public final static int SMS = 1;
	public final static int IMAGE = 2;
	public final static int AUDIO = 3;
	public final static int FILE = 4;
	public final static int COMMAND = 5;
	
	
	private String CHARSET = "gb2312";
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
			out.getOutputStream().writeInt(type);
			out.getOutputStream().writeInt(getmHeader().size());
			if(getmHeader()!=null&&getmHeader().size()>0){
				Iterator iter = getmHeader().entrySet().iterator();
           	 	while(iter.hasNext()){
           	 		Entry entry = (Entry)iter.next();
           	 		String nm = (String)entry.getKey();
           	 		out.getOutputStream().writeInt(nm.getBytes(CHARSET).length);
           	 		out.getOutputStream().write(nm.getBytes(CHARSET));
           	 		String value = (String)entry.getValue();
           	 		out.getOutputStream().writeInt(value.getBytes(CHARSET).length);
           	 		out.getOutputStream().write(value.getBytes(CHARSET));
           	 	}
            }
			byte[] content = getContent();
       	 	if(content!=null){
           	 	out.getOutputStream().writeInt(content.length);
           	 	if(content.length>0){
           	 		out.getOutputStream().write(content);
           	 	}
       	 	}else{
       	 	out.getOutputStream().writeInt(0);
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
			type = in.getInputStream().readInt();
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
}
