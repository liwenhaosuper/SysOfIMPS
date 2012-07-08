package com.imps.net.http;




public class HttpModule {
	private static HttpModule netWorkModule = null;
	private HttpThread normalThread;
	private HttpThread imageThread;
	private HttpModule(){
	}
	public static HttpModule getInstance(){
		if(netWorkModule ==null){
			netWorkModule = new HttpModule();
		}
		return netWorkModule;
	}
	public void sendRequest(Request request){
		if(request.getmType() == Request.TYPE_IMAGE){
			sendRequestForImage(request);
		}else{
			sendReqeustForText(request);
		} 
	}
	public void clearRequset(){
		if(normalThread!=null){
			normalThread.clearRequest();
		}
		if(imageThread!=null){
			imageThread.clearRequest();
		}
	}
	public void sendReqeustForText(Request request){
		if(normalThread==null||!normalThread.isAlive()){
			normalThread = new HttpThread();
			normalThread.start();
		}
		normalThread.sendRequest(request);
	}
	public void sendRequestForImage(Request request){
		if(imageThread==null||!imageThread.isAlive()){
			imageThread = new HttpThread();
			imageThread.start();
		}
		imageThread.sendRequest(request);
	}
}
