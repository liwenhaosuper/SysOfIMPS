package com.imps.model;

import com.imps.util.FileTool;

public class ImageMedia extends MediaType{
	
	private String url = "";
	
	public ImageMedia(boolean send) {
		super(IMPSType.IMAGE,send);
		url = String.valueOf(System.currentTimeMillis())+".image";
	}
	@Override
	public void setContent(byte[] data) {
		writeToFile(data);
	}
	@Override
	public byte[] getContent() {
		FileTool localFileTool = new FileTool();
		String str = localFileTool.open(url,"imps/image/",false, false);
		if(str.equals("")){
			return new byte[0];
		}else{
			byte[] buffer = new byte[1024*1024];//1M
			localFileTool.read(buffer, 0, 1024*1024);
			return buffer;
		}
	}
	private void writeToFile(byte[] data){
		FileTool localFileTool = new FileTool();
		String str = localFileTool.open(url,"imps/image/",true, false);
		if ((str != null) && (str.trim().length() > 0)){
			localFileTool.write(data);
			url = str;
		}
		localFileTool.close();
	}
	public String getUrl(){
		return url;
	}
	public void setUrl(String url){
		this.url = url;
	}
}
