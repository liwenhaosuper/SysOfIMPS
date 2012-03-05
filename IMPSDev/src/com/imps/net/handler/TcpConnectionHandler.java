package com.imps.net.handler;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
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

import android.util.Log;

import com.imps.IMPSDev;
import com.imps.events.IConnEvent;
import com.imps.events.IConnEventDispacher;
import com.imps.services.impl.ConnectionService;
import com.imps.services.impl.ServiceManager;

public class TcpConnectionHandler extends ReplayingDecoder<VoidEnum> implements IConnEventDispacher {

	private static boolean DEBUG =IMPSDev.isDEBUG();
	private static String TAG = TcpConnectionHandler.class.getCanonicalName();
	private static int RECONNECT_DELAY = 2000;
	private final ClientBootstrap bootstrap;
	private List<IConnEvent> mConnList;
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
		mConnList = new ArrayList<IConnEvent>();
		this.addr = addr;
		timer = new Timer();
	}
	
	@Override
	protected Object decode(ChannelHandlerContext ctx, Channel channel,
			ChannelBuffer buffer, VoidEnum state) throws Exception {
		buffer.readBytes(tag);
		if(DEBUG)	Log.d(TAG,"TAG:"+tag[0]+tag[1]);
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
    	if(DEBUG) Log.d(TAG,"tcp disconnected to:"+e.getChannel().getRemoteAddress().toString());
       	for(int i=0;i<mConnList.size();i++){
    		mConnList.get(i).onDisconnected();
    	}
       	if(timer!=null)
       		timer.cancel();
       	if(!ServiceManager.isStarted){
       		return;
       	}
       	timer = new Timer();
        timer.schedule(new TimerTask() {
			@Override
			public void run() {
				if(!ServiceManager.getmNet().isAvailable()){
					return;
				}
				ConnectionService.setChannel(bootstrap.connect(addr).getChannel());
				if(DEBUG) Log.d(TAG,"Reconnecting...");
			}
        }, RECONNECT_DELAY,RECONNECT_DELAY);
    }
    @Override
    public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) {
    	if(DEBUG) Log.d(TAG,"tcp connected to:"+e.getChannel().getRemoteAddress().toString());
    	for(int i=0;i<mConnList.size();i++){
    		mConnList.get(i).onConnected();
    	}
    	if(timer!=null){
    		timer.cancel();
    	}
    }
    @Override
    public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e){
    	if(DEBUG) Log.d(TAG,"tcp closed");
        if(timer!=null){
        	timer.cancel();
        }
        if(!ServiceManager.isStarted)
        	return;
        timer = new Timer();
        timer.schedule(new TimerTask() {
			@Override
			public void run() {
				if(!ServiceManager.getmNet().isAvailable()){
					return;
				}
				ConnectionService.setChannel(bootstrap.connect(addr).getChannel());
				if(DEBUG) Log.d(TAG,"Reconnecting...");
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

	@Override
	public void addConnEventHandler(IConnEvent event) {
		mConnList.add(event);
	}

	@Override
	public void removeConnEventHandler(IConnEvent event) {
		mConnList.remove(event);
	}
    
}
