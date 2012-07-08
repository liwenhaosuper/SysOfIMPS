package com.imps.net.tcp;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;

import android.util.Log;

import com.imps.model.AudioMedia;
import com.imps.model.CommandType;
import com.imps.model.IMPSType;
import com.imps.model.ImageMedia;
import com.imps.model.MediaType;
import com.imps.model.TextMedia;

public class NetMsgLogicHandler extends SimpleChannelUpstreamHandler{
	
	private static String TAG = NetMsgLogicHandler.class.getCanonicalName();
	private static boolean DEBUG = true;
	private ConnectionListener listener;

	public NetMsgLogicHandler(ConnectionListener listener){
		this.listener = listener;
	}
    @Override
    public void channelDisconnected(ChannelHandlerContext ctx, ChannelStateEvent e){
    	if(DEBUG) Log.d(TAG,"tcp disconnected to:"+e.getChannel().getRemoteAddress().toString());
    	listener.onTCPDisconnect();
    }
    @Override
    public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) {
    	if(DEBUG) Log.d(TAG,"tcp connected to:"+e.getChannel().getRemoteAddress().toString());
    	listener.onTCPConnect();
    }
	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) 
	{
		Channel session = e.getChannel();
		ChannelBuffer inMsg =(ChannelBuffer)e.getMessage();
		if(inMsg==null){
			session.close();
			listener.onTCPDisconnect();
			return;
		}
		IMPSType media = null;
		byte type = inMsg.getByte(inMsg.readerIndex());
		if(type==MediaType.AUDIO){
			media = new AudioMedia(false);
		}else if(type==MediaType.IMAGE){
			media = new ImageMedia(false);
		}else if(type==MediaType.SMS){
			media = new TextMedia(false);
		}else if(type==MediaType.COMMAND){
			media = new CommandType();
		}else{
			if(DEBUG) Log.d(TAG,"Unknown media type......");
			inMsg.skipBytes(inMsg.readableBytes());
			return;
		}
		media.MediaParser(inMsg.array());
		ReceiverChannelService.getInstance().onMediaRecv(media);
	}
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) {
    	try {
    		if(DEBUG) Log.d(TAG,"tcp exception...");
    		ctx.getChannel().close();
    		listener.onTCPDisconnect();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
    }
}
