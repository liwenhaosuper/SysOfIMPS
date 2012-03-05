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
import com.imps.ui.DoodleView;

public class DoodleConnectionService{
	private static String TAG = DoodleConnectionService.class.getCanonicalName();
	private static boolean DEBUG = IMPSDev.isDEBUG();
	private static String server_ip;
	private static int server_port;
	private static Channel future;
	private static DoodleChannelService handler;
	private static ClientBootstrap bootstrap;
	private static boolean isStarted = false;
	
	public DoodleConnectionService(String ip,int port){
		server_ip = ip;
		server_port = port;
	}
	/**
	 * connect and wait. That means it is synchronize
	 * @param view
	 * @return
	 */
	public boolean startDoodle(DoodleView view){
		if(DEBUG) Log.d(TAG,"Start Doodle...");
		isStarted = true;
        // Configure the client.
        bootstrap = new ClientBootstrap(
                new NioClientSocketChannelFactory(
                        Executors.newCachedThreadPool(),
                        Executors.newCachedThreadPool()));
        // Set up the pipeline factory.
        handler = new DoodleChannelService();
        bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
            public ChannelPipeline getPipeline() throws Exception {
                return Channels.pipeline(new DoodleUnificationHandler(),
                		handler);
            }
        });
        handler.setmDoodleView(view);
        bootstrap.setOption("reuseAddress", true);
        bootstrap.setOption("child.reuseAddress", true);
        bootstrap.setOption("child.keepAlive", true);
        bootstrap.setOption("child.tcpNoDelay", true);
        // Start the connection attempt.
        ChannelFuture channelFuture = bootstrap.connect(new InetSocketAddress(server_ip,server_port));
        try {
			channelFuture.await();
		} catch (InterruptedException e) {
			if(DEBUG) e.printStackTrace();
		}
        future = channelFuture.getChannel();
        
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
	public boolean stopDoodle(){
		if(DEBUG)Log.d(TAG,"stopDoodle...");
		isStarted = false;
		try {
			if(future!=null){
				future.close().await();
			}
		} catch (InterruptedException e) {
			if(DEBUG)e.printStackTrace();
		}
		return true;
	}
	public static Channel getChannel(){
		return future;
	}
	public static void setChannel(Channel futures){
		future = futures;
	}
	public DoodleChannelService getHandler(){
		return handler;
	}
}
