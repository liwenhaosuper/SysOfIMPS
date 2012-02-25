package com.imps.server.main;

import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.channel.group.DefaultChannelGroup;


public class ServerBoot {
	private static int tcpPort = 1200;
	private static int udpPort = 1300;
	private static ChannelGroup allGroups;
    public static void main(String argv[]) {
    	allGroups = new DefaultChannelGroup();
        new IMPSTcpServer(allGroups,tcpPort).run();
        new IMPSUdpServer(udpPort).run();
    }
}
