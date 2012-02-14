package com.imps.server.main;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ConnectionlessBootstrap;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.socket.nio.NioDatagramChannelFactory;

import com.imps.server.handler.LogicHandler;
import com.imps.server.handler.PortUnificationUdpServerHandler;

public class IMPSUdpServer {
	private static int port;
	public IMPSUdpServer(int port){
		this.port = port;
	}
	public void run(){
		ConnectionlessBootstrap boostrap = new ConnectionlessBootstrap(
				new NioDatagramChannelFactory(Executors.newCachedThreadPool()));
		
		boostrap.setPipelineFactory(new ChannelPipelineFactory(){
			@Override
			public ChannelPipeline getPipeline() throws Exception {
				return Channels.pipeline(
						new PortUnificationUdpServerHandler(),
						new LogicHandler());
			}
		});
		boostrap.bind(new InetSocketAddress(port));
	}
}
