package com.imps.net.http;

import java.io.InputStream;

public class Response {
	public final static int CODE_SUCCESS = 200;
	public final static int CODE_ERROR = -999;
	private int mType;
	private String mId;
	private String mBody;
	private int mResponseCode;
	private InputStream mImageStream;
	private long mContentLength;
	
	public int getmResponseCode() {
		return mResponseCode;
	}
	public void setmResponseCode(int mResponseCode) {
		this.mResponseCode = mResponseCode;
	}
	public int getmType() {
		return mType;
	}
	public String getmId() {
		return mId;
	}
	public void setmId(String mId) {
		this.mId = mId;
	}
	public void setmType(int mType) {
		this.mType = mType;
	}
	public String getmBody() {
		return mBody;
	}
	public long getmContentLength() {
		return mContentLength;
	}
	public void setmContentLength(long mContentLength) {
		this.mContentLength = mContentLength;
	}
	public InputStream getmImageStream() {
		return mImageStream;
	}
	public void setmImageStream(InputStream mImageStream) {
		this.mImageStream = mImageStream;
	}
	public void setmBody(String mBody) {
		this.mBody = mBody;
	}
}
