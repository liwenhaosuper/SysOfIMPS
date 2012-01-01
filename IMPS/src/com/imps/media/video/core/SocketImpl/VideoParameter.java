package com.imps.media.video.core.SocketImpl;

public class VideoParameter {

	private int time_out = 2000;
	private String name;
	private String ip;
	private int port;
	private String username;
	private int width;
	private int height;
	private int connectType;
	private boolean preserveAspectRatio;
	private boolean isServer;
	public void setTime_out(int time_out) {
		this.time_out = time_out;
	}
	public int getTime_out() {
		return time_out;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getName() {
		return name;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public int getPort() {
		return port;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getIp() {
		return ip;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getUsername() {
		return username;
	}
	public void setWidth(int width) {
		this.width = width;
	}
	public int getWidth() {
		return width;
	}
	public void setHeight(int height) {
		this.height = height;
	}
	public int getHeight() {
		return height;
	}
	public void setConnectType(int connectType) {
		this.connectType = connectType;
	}
	public int getConnectType() {
		return connectType;
	}
	public void setPreserveAspectRatio(boolean preserveAspectRatio) {
		this.preserveAspectRatio = preserveAspectRatio;
	}
	public boolean isPreserveAspectRatio() {
		return preserveAspectRatio;
	}
	public void setServer(boolean isServer) {
		this.isServer = isServer;
	}
	public boolean isServer() {
		return isServer;
	}
	
	
}
