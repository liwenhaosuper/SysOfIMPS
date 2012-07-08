package com.imps.net.http;

import java.util.HashMap;


public class Request {
	public final static int TYPE_STRING = 1;
	public final static int TYPE_IMAGE = 2;
	
	private int mType ;
	private String mId;

	private String mUrl;
	private String mBody;
	private HashMap<String,String> mHeader;
	private RequestListener mReqeustListener;
	
	public Request(RequestListener reqeustListener){
		mReqeustListener = reqeustListener;
	}


	public int getmType() {
		return mType;
	}


	public void setmType(int mType) {
		this.mType = mType;
	}


	public String getmUrl() {
		return mUrl;
	}


	public void setmUrl(String mUrl) {
		this.mUrl = mUrl;
	}
	public String getmBody() {
		return mBody;
	}

	public void setmBody(String mBody) {
		this.mBody = mBody;
	}
	public HashMap<String, String> getmHeader() {
		return mHeader;
	}


	public void setmHeader(HashMap<String, String> mHeader) {
		this.mHeader = mHeader;
	}
	public RequestListener getmReqeustListener() {
		return mReqeustListener;
	}
	public String getmId() {
		return mId;
	}
	public void setmId(String mId) {
		this.mId = mId;
	}
	
}
