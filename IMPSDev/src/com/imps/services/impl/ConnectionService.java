package com.imps.services.impl;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;

import android.util.Log;

import com.imps.IMPSDev;
import com.imps.net.handler.TcpConnectionHandler;

public class ConnectionService{
	private static String TAG = ConnectionService.class.getCanonicalName();
	private static boolean DEBUG = IMPSDev.isDEBUG();
	private static String server_ip;
	private static int server_port;
	private static Channel future;
	private static TcpConnectionHandler handler;
	private static ClientBootstrap bootstrap;
	private static boolean isStarted = false;
	
	public ConnectionService(String ip,int port){
		server_ip = ip;
		server_port = port;
	}
	public boolean startTcp(){
		if(DEBUG) Log.d(TAG,"Start tcp...");
		isStarted = true;
        // Configure the client.
        bootstrap = new ClientBootstrap(
                new NioClientSocketChannelFactory(
                        Executors.newCachedThreadPool(),
                        Executors.newCachedThreadPool()));
        // Set up the pipeline factory.
        handler = new TcpConnectionHandler(bootstrap,new InetSocketAddress(server_ip,server_port));
        bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
            public ChannelPipeline getPipeline() throws Exception {
                return Channels.pipeline(
                		handler, ServiceManager.getmNetLogic());
            }
        });
        handler.addConnEventHandler(ServiceManager.getmAccount());
        bootstrap.setOption("reuseAddress", true);
        bootstrap.setOption("child.reuseAddress", true);
        bootstrap.setOption("child.keepAlive", true);
        bootstrap.setOption("child.tcpNoDelay", true);
        // Start the connection attempt.
        future = bootstrap.connect(new InetSocketAddress(server_ip,server_port)).getChannel();
		return true;
	}
	public static void fireConnect(){
		if(future.isConnected()){
			return;
		}
		if(!isStarted){
			return;
		}
		ChannelFuture channelfuture = bootstrap.connect(new InetSocketAddress(server_ip,server_port));
		try {
			channelfuture.await();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		future = channelfuture.getChannel();
		
	}
	public static void fireClose(){
		future.close();
	}
	public boolean stopTcp(){
		if(DEBUG)Log.d(TAG,"stopTcp...");
		isStarted = false;
		try {
			future.close().await();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}
	public static Channel getChannel(){
		return future;
	}
	public static void setChannel(Channel futures){
		future = futures;
	}
	public static void setHandler(TcpConnectionHandler handler) {
		ConnectionService.handler = handler;
	}
	public static TcpConnectionHandler getHandler() {
		return handler;
	}
	
}
