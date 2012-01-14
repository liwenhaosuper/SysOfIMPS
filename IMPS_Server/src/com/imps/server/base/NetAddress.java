package com.imps.server.base;

public class NetAddress {
	private String pubIp = "";
	private int pubPort = -1;
	private String natIp = "";
	private int natPort = -1;
	
	public NetAddress(){
		
	}
	
	public void setNatPort(int natPort) {
		this.natPort = natPort;
	}
	public int getNatPort() {
		return natPort;
	}
	public void setNatIp(String natIp) {
		this.natIp = natIp;
	}
	public String getNatIp() {
		return natIp;
	}
	public void setPubPort(int pubPort) {
		this.pubPort = pubPort;
	}
	public int getPubPort() {
		return pubPort;
	}
	public void setPubIp(String pubIp) {
		this.pubIp = pubIp;
	}
	public String getPubIp() {
		return pubIp;
	}
	public void addPublicAddress(String ip,int port){
		this.setPubIp(ip);
		this.setPubPort(port);
	}
	public void addNATAddress(String ip,int port){
		this.setNatIp(ip);
		this.setNatPort(port);
	}
}
