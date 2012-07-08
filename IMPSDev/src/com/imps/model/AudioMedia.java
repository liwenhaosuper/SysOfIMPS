package com.imps.model;

import com.imps.util.FileTool;

public class AudioMedia extends MediaType{
	
	private String url = "";
	
	public AudioMedia(boolean send) {
		super(IMPSType.AUDIO,send);
		url = String.valueOf(System.currentTimeMillis())+".audio";
	}
	@Override
	public void setContent(byte[] data) {
		super.setContent(data);
		writeToFile(data);
	}
	@Override
	public byte[] getContent() {
		FileTool localFileTool = new FileTool();
		String str = localFileTool.open(url,"imps/audio/",false, false);
		if(str.equals("")){
			return new byte[0];
		}else{
			byte[] buffer = new byte[1024*1024*2];//2M
			localFileTool.read(buffer, 0, 1024*1024*2);
			return buffer;
		}
	}
	public String getUrl(){
		return url;
	}
	public void setUrl(String url){
		this.url = url;
	}
	private void writeToFile(byte[] data){
		FileTool localFileTool = new FileTool();
		String str = localFileTool.open(url,"imps/audio/",true, false);
		if ((str != null) && (str.trim().length() > 0)){
			localFileTool.write(data);
			url = str;
		}
		localFileTool.close();
	}
}
