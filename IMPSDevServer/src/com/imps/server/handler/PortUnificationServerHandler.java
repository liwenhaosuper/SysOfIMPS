package com.imps.server.handler;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.handler.codec.frame.FrameDecoder;
import org.jboss.netty.handler.codec.http.HttpRequestDecoder;
import org.jboss.netty.handler.codec.http.HttpResponseEncoder;

public class PortUnificationServerHandler extends FrameDecoder{
	//tag is the packet label to identify what the message is
	//OK:plain text
	
	private byte[] tag = new byte[2];
    private static final Logger logger = Logger.getLogger(
            PortUnificationServerHandler.class.getName());
	@Override
	protected Object decode(ChannelHandlerContext ctx, Channel channel, ChannelBuffer buffer) throws Exception {
		tag[0] = buffer.getByte(buffer.readerIndex());
		tag[1] = buffer.getByte(buffer.readerIndex()+1);
		if(tag[0]=='O'&&tag[1]=='K'){//plain text
			logger.log(Level.INFO,"PortUnificationServerHandler:plain text req");
	        ChannelPipeline p = ctx.getPipeline();
	        if(p.get("PlainTextHandler")==null){
	        	p.addLast("PlainTextHandler", new PlainTextHandler());
	        }
	        if(p.get("LogicHandler")==null){
	        	p.addLast("LogicHandler",new LogicHandler());
	        }
	       // buffer.readBytes(tag);
		}else if(isHttp(tag[0],tag[1])){ //http request
			logger.log(Level.INFO,"http req:");
	        ChannelPipeline p = ctx.getPipeline();
	        if(p.get("PlainTextHandler")==null){
	        	p.addLast("HttpRequestDecoder", new HttpRequestDecoder());
	        }
	        if(p.get("HttpResponseEncoder")==null){
	        	p.addLast("HttpResponseEncoder", new HttpResponseEncoder());
	        }
	        if(p.get("HttpResponseEncoder")==null){
	        	p.addLast("HttpLogicHandler", new HttpLogicHandler());
	        }
	        buffer.readBytes(tag);
		}
		else{//exception...
			logger.log(Level.INFO,"Unknow tag recv:"+tag);
            buffer.skipBytes(buffer.readableBytes());
            ctx.getChannel().close();
            return null;
		}
        // Forward the current read buffer as is to the new handlers.
        return buffer.readBytes(buffer.readableBytes());
	}
	private boolean isHttp(byte magic1, byte magic2) {
        return
            magic1 == 'G' && magic2 == 'E' || // GET
            magic1 == 'P' && magic2 == 'O' || // POST
            magic1 == 'P' && magic2 == 'U' || // PUT
            magic1 == 'H' && magic2 == 'E' || // HEAD
            magic1 == 'O' && magic2 == 'P' || // OPTIONS
            magic1 == 'P' && magic2 == 'A' || // PATCH
            magic1 == 'D' && magic2 == 'E' || // DELETE
            magic1 == 'T' && magic2 == 'R' || // TRACE
            magic1 == 'C' && magic2 == 'O';   // CONNECT
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
    	//logger.log(Level.INFO,"tcp exception to:"+e.getChannel().getRemoteAddress().toString()+":"+e.toString());
    	ctx.getChannel().close();
    }
}
