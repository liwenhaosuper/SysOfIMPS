package com.imps.net.tcp;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.handler.codec.replay.ReplayingDecoder;
import org.jboss.netty.handler.codec.replay.VoidEnum;

import android.util.Log;

public class TcpConnectionHandler extends ReplayingDecoder<VoidEnum>{

	private static boolean DEBUG =true;
	private static String TAG = TcpConnectionHandler.class.getCanonicalName();
	private byte[] tag = new byte[2];
	private ConnectionListener listener;
	public TcpConnectionHandler(ConnectionListener listener){
		this.listener = listener;
	}
	@Override
	protected Object decode(ChannelHandlerContext ctx, Channel channel,
			ChannelBuffer buffer, VoidEnum state) throws Exception {
		buffer.readBytes(tag);
		if(DEBUG)	Log.d(TAG,"TAG:"+tag[0]+tag[1]);
		if(tag[0]=='O'&&tag[1]=='K'){
			int len = buffer.readInt();
			return buffer.readBytes(len);
		}else{//exception...
			buffer.skipBytes(buffer.readableBytes());
			ctx.getChannel().close();
			return null;
		}
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
    public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e){
    	if(DEBUG) Log.d(TAG,"tcp closed");
    	listener.onTCPDisconnect();
    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) {
    	try {
    		if(DEBUG) Log.d(TAG,"tcp exception..."+e.toString());
    		ctx.getChannel().close();
    		listener.onTCPDisconnect();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
    }

}
