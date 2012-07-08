package com.imps.net.tcp;

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

public class ConnectionService{
	private static String TAG = ConnectionService.class.getCanonicalName();
	private static boolean DEBUG = true;
	private static String server_ip;
	private static int server_port;
	private static Channel future;
	private static ClientBootstrap bootstrap;
	private static boolean isStarted = false;
	
	public ConnectionService(String ip,int port){
		server_ip = ip;
		server_port = port;
	}
	public boolean startTcp(final ConnectionListener listener){
		if(DEBUG) Log.d(TAG,"Start tcp...");
		isStarted = true;
        bootstrap = new ClientBootstrap(
                new NioClientSocketChannelFactory(
                        Executors.newCachedThreadPool(),
                        Executors.newCachedThreadPool()));
        
        bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
            public ChannelPipeline getPipeline() throws Exception {
                return Channels.pipeline(
                		new TcpConnectionHandler(listener),new NetMsgLogicHandler(listener));
            }
        });
        bootstrap.setOption("reuseAddress", true);
        bootstrap.setOption("child.reuseAddress", true);
        bootstrap.setOption("child.keepAlive", true);
        bootstrap.setOption("child.tcpNoDelay", true);
		ChannelFuture channelfuture = bootstrap.connect(new InetSocketAddress(server_ip,server_port));
		try {
			channelfuture.await(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
        future = channelfuture.getChannel();
        return true;
	}
	public static void fireConnect(){
		if(future.isConnected()){
			return;
		}
		if(!isStarted){
			return;
		}
		if(future.isOpen()){
			future.close();
		}
		ChannelFuture channelfuture = bootstrap.connect(new InetSocketAddress(server_ip,server_port));
		try {
			channelfuture.await();
		} catch (InterruptedException e) {
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
			if(future!=null){
				future.close().await();
			}
		} catch (InterruptedException e) {
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
}
