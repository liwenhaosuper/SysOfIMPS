package com.imps.net.http;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

public class HttpThread extends Thread{
	public final static int STATE_STOP = 0;
	public final static int STATE_PAUSE = 1;
	public final static int STATE_START = 2;

	private ArrayList<Request> mRequestPool;
	private int mTimeoutConnection = 5000;
	private int mTimeoutSocket = 10000;
	private int mRuningState = 2;
	
	public HttpThread() {
		mRequestPool = new ArrayList<Request>();
	}
	public void run() {
		Request request = null;
		while (true) {
			try {
				if (mRuningState == STATE_STOP) {
					break;
				} else if (mRuningState == STATE_PAUSE) {
					mRequestPool.wait();
				} else {
					synchronized (mRequestPool) {
						if (mRequestPool.size() == 0) {
							mRequestPool.wait();
						} else {
							request = mRequestPool.get(0);
							mRequestPool.remove(0);
							executeNotify(request, 2);
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	public void clearRequest() {
		synchronized (mRequestPool) {
			mRequestPool.clear();
			mRequestPool.notifyAll();
		}
	}
	public void sendRequest(Request request) {
		synchronized (mRequestPool) {
			mRequestPool.add(request);
			mRequestPool.notifyAll();
		}
	}
	public void setThreadState(int state) {
		mRuningState = state;
	}
	public void executeNotify(Request request, int leftTimes){
		Response response = new Response();
		RequestListener requestListener = request.getmReqeustListener();
		HttpResponse httpResponse = sendHttpRequest(request, leftTimes);
		if (httpResponse == null) {
			response.setmResponseCode(Response.CODE_ERROR);
		} else {
			int responseCode = httpResponse.getStatusLine().getStatusCode();
			HttpEntity entity = httpResponse.getEntity();
			response.setmResponseCode(responseCode);
			response.setmType(request.getmType());
			try {
				if (responseCode == Response.CODE_SUCCESS) {
					if (request.getmType() == Request.TYPE_IMAGE) {
						response.setmImageStream(entity.getContent());
						response.setmContentLength(entity.getContentLength());
					} else {
						String responseBody = EntityUtils.toString(entity);
						response.setmBody(responseBody);
						response.setmContentLength(responseBody.length());
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (requestListener != null) {
			requestListener.onRequestFinshed(response);
		}
	}
	public HttpResponse sendHttpRequest(Request request, int leftTimes) {
		HttpResponse httpResponse = null;
		DefaultHttpClient httpclient = new DefaultHttpClient(getDefaultHttpParams());
		try {
			HttpUriRequest httpPost = getHttpRequest(request.getmType(),request.getmUrl(), request.getmBody(), request.getmHeader());
			httpResponse = httpclient.execute(httpPost);
			return httpResponse;
		} catch (Exception e) {
			if (leftTimes > 0) {
				executeNotify(request, leftTimes - 1);
			} else {
				httpResponse = null;
			}
			e.printStackTrace();
		}
		return httpResponse;
	}
	public void setRequetHeader(HttpRequest httpRequest,
			HashMap<String, String> requestHeader) {
		Set<Entry<String, String>> entrySet = requestHeader.entrySet();
		for (Entry<String, String> entry : entrySet) {
			httpRequest.setHeader(entry.getKey(), entry.getValue());
		}
	}
	public HttpParams getDefaultHttpParams() {
		BasicHttpParams httpParameters = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParameters,
				mTimeoutConnection);
		HttpConnectionParams.setSoTimeout(httpParameters, mTimeoutSocket);
		return httpParameters;
	}
	public HttpUriRequest getHttpRequest(int requstType, String url,
			String body, HashMap<String, String> requestHeader)
			throws UnsupportedEncodingException {
		if (requstType == Request.TYPE_IMAGE) {
			HttpGet httpGet = new HttpGet(url);
			return httpGet;
		} else {
			HttpPost httpPost = new HttpPost(url);
			if (requestHeader != null) {
				setRequetHeader(httpPost, requestHeader);
			}
			if (body != null) {
				httpPost.setHeader("Content-Type", "text/plain; charset=utf-8");
				httpPost.setEntity(new StringEntity(body, "utf-8"));
			}
			return httpPost;
		}
	}
}
