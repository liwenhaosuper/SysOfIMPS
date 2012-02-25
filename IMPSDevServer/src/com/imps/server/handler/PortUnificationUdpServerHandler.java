package com.imps.server.handler;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.handler.codec.frame.FrameDecoder;

public class PortUnificationUdpServerHandler extends FrameDecoder{
    private static final Logger logger = Logger.getLogger(
    		PortUnificationUdpServerHandler.class.getName());
	private byte[] tag = new byte[2];
	private byte cmdType;
	@Override
	protected Object decode(ChannelHandlerContext ctx, Channel channel,
			ChannelBuffer buffer) throws Exception {
		buffer.readBytes(tag);
		logger.log(Level.FINE,"Udp msg recv...");
		if(tag[0]=='O'&&tag[1]=='K'){
			int len = buffer.readInt();
			return buffer.readBytes(len);
		}else{
			new Error("Unsupported tag in UDP server").printStackTrace();
			return tag;
		}
	}
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) {
    	try {
			super.exceptionCaught(ctx,e);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
    }

}
