package com.imps.server.handler;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.handler.codec.replay.ReplayingDecoder;
import org.jboss.netty.handler.codec.replay.VoidEnum;

public class PlainTextHandler extends ReplayingDecoder<VoidEnum>{
	//tag is the packet label to identify what the message is
	//AU:audio data
	//FL:file data
	//OK:plain text
	private static boolean DEBUG = true;
    private static final Logger logger = Logger.getLogger(
    		PlainTextHandler.class.getName());
    private byte[] tags = new byte[2];
	@Override
	protected Object decode(ChannelHandlerContext ctx, Channel channel,
			ChannelBuffer buffer, VoidEnum state) throws Exception {
		buffer.readBytes(tags); //'OK'
		if(tags[0]=='O'&&tags[1]=='K'){
			int len = buffer.readInt();
			if(DEBUG)System.out.println("Req len:"+len);
			return buffer.readBytes(len);
		}else{
			logger.log(Level.INFO,"Unknow tag recv:"+tags);
            buffer.skipBytes(buffer.readableBytes());
            ctx.getChannel().close();
            return null;
		}
	}
    @Override
    public void channelDisconnected(ChannelHandlerContext ctx, ChannelStateEvent e){
		logger.log(Level.INFO,"client disconnected from:"+e.getChannel().getRemoteAddress().toString());
    }
    
    @Override
    public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) {
    	logger.log(Level.INFO,"client connected from:"+e.getChannel().getRemoteAddress().toString());
    }
    @Override
    public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e){
    	logger.log(Level.INFO,"tcp closed to:"+e.getChannel().getRemoteAddress().toString());
    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) {
    	logger.log(Level.INFO,"tcp exception to:"+e.getChannel().getRemoteAddress().toString()+":"+e.toString());
    	ctx.getChannel().close();
    }
}
