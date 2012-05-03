package com.imps.server.main;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

import com.imps.server.handler.LogicHandler;
import com.imps.server.handler.PortUnificationServerHandler;

public class IMPSTcpServer {
	private static int port;
	private static ChannelGroup allGroups;
	public IMPSTcpServer(ChannelGroup allGroups,int port){
		this.port = port;
		this.setAllGroups(allGroups);
	}
    public void run() {
        // Configure the tcp server.
        ServerBootstrap bootstrap = new ServerBootstrap(
                new NioServerSocketChannelFactory(
                        Executors.newCachedThreadPool(),
                        Executors.newCachedThreadPool()));

        // Set up the pipeline factory.
        bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
        	@Override
            public ChannelPipeline getPipeline() throws Exception {
                return Channels.pipeline( 
                        new PortUnificationServerHandler(),
                        new LogicHandler());
            }
        });
        bootstrap.setOption("reuseAddress", true);
        bootstrap.setOption("child.reuseAddress", true);
        bootstrap.setOption("child.keepAlive", true);
        bootstrap.setOption("child.tcpNoDelay", true);
        // Bind and start to accept incoming connections.
        Channel serverChannel = bootstrap.bind(new InetSocketAddress(port));
        allGroups.add(serverChannel);
    }
	public static void setAllGroups(ChannelGroup allGroups) {
		IMPSTcpServer.allGroups = allGroups;
	}
	public static ChannelGroup getAllGroups() {
		return allGroups;
	}
}
