package com.imps.server.main;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.channel.group.DefaultChannelGroup;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

import com.imps.server.handler.DoodleLogicHandler;
import com.imps.server.handler.DoodleUnificationHandler;
import com.imps.server.main.basetype.User;

public class DoodleTcpServer {
	private static int port;
	//map is :room master:channel group
	//that means one user could not create one than one doodle room
	public static Map < String ,ChannelGroup> roomGroups = new HashMap < String ,ChannelGroup>();
	public static ChannelGroup allGroups = new DefaultChannelGroup();
	public static List<User> doodleUsers = new ArrayList<User>();
	public DoodleTcpServer(int port){
		DoodleTcpServer.port = port;
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
                return Channels.pipeline(new DoodleUnificationHandler(),
                        new DoodleLogicHandler());
            }
        });
        bootstrap.setOption("reuseAddress", true);
        bootstrap.setOption("child.reuseAddress", true);
        bootstrap.setOption("child.keepAlive", true);
        bootstrap.setOption("child.tcpNoDelay", true);
        // Bind and start to accept incoming connections.
        Channel serverChannel = bootstrap.bind(new InetSocketAddress(port));
       // allGroups.add(serverChannel);
    }
 
}
