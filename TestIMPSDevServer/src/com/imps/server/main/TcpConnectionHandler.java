package com.imps.server.main;

import java.net.InetSocketAddress;
import java.util.Timer;
import java.util.TimerTask;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.handler.codec.replay.ReplayingDecoder;
import org.jboss.netty.handler.codec.replay.VoidEnum;

public class TcpConnectionHandler extends ReplayingDecoder<VoidEnum>{

	private static boolean DEBUG =true;
	private static String TAG = TcpConnectionHandler.class.getCanonicalName()+"<<<";
	private static int RECONNECT_DELAY = 2000;
	private static int PERIOD = 5000;
	private final ClientBootstrap bootstrap;
	private InetSocketAddress addr;
	private static Timer timer;
	//tag is the packet label to identify what the message is
	//AU:audio data
	//IM:image data
	//FL:file data
	//OK:plain text
	private byte[] tag = new byte[2];
	public TcpConnectionHandler(ClientBootstrap bootstrap,InetSocketAddress addr){
		this.bootstrap = bootstrap;
		this.addr = addr;
		timer = new Timer();
	}
	
	@Override
	protected Object decode(ChannelHandlerContext ctx, Channel channel,
			ChannelBuffer buffer, VoidEnum state) throws Exception {
		buffer.readBytes(tag);
		if(DEBUG)	System.out.println(TAG+"TAG:"+tag[0]+tag[1]);
		if(tag[0]=='A'&&tag[1]=='U'){
			return buffer.readBytes(buffer.readInt());
		}else if(tag[0]=='I'&&tag[1]=='M'){
			return buffer.readBytes(buffer.readInt());
		}else if(tag[0]=='F'&&tag[1]=='L'){
			return buffer.readBytes(buffer.readInt());
		}else if(tag[0]=='O'&&tag[1]=='K'){
			int len = buffer.readInt();
			//ctx.getPipeline().remove(this);
			return buffer.readBytes(len);
		}else{//exception...
			//throw new Error("Unknow tag recv:"+tag);	
			return tag;
		}
	}
    @Override
    public void channelDisconnected(ChannelHandlerContext ctx, ChannelStateEvent e){
    	if(DEBUG) System.out.println(TAG+"tcp disconnected to:"+e.getChannel().getRemoteAddress().toString());
       	if(timer!=null)
       		timer.cancel();
       	timer = new Timer();
        timer.schedule(new TimerTask() {
			@Override
			public void run() {
				ConnectionService.setChannel(bootstrap.connect(addr).getChannel());
				if(DEBUG) System.out.println(TAG+"Reconnecting...");
			}
        }, RECONNECT_DELAY,PERIOD);
    }
    @Override
    public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) {
    	if(DEBUG) System.out.println(TAG+"tcp connected to:"+e.getChannel().getRemoteAddress().toString());
    	if(timer!=null){
    		timer.cancel();
    	}
    }
    @Override
    public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e){
    	if(DEBUG) System.out.println(TAG+"tcp closed");
        if(timer!=null){
        	timer.cancel();
        }
        timer = new Timer();
        timer.schedule(new TimerTask() {
			@Override
			public void run() {
				ConnectionService.setChannel(bootstrap.connect(addr).getChannel());
				if(DEBUG) System.out.println(TAG+"Reconnecting...");
			}
        }, RECONNECT_DELAY);
    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) {
    	try {
			super.exceptionCaught(ctx,e);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
    }
 
}
