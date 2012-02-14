package com.imps.server.handler;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.handler.codec.replay.ReplayingDecoder;
import org.jboss.netty.handler.codec.replay.VoidEnum;

import com.imps.server.main.basetype.User;
import com.imps.server.main.basetype.userStatus;
import com.imps.server.manager.UserManager;

public class PortUnificationServerHandler extends ReplayingDecoder<VoidEnum>{
	//tag is the packet label to identify what the message is
	//AU:audio data
	//IM:image data
	//FL:file data
	//OK:plain text
	private byte[] tag = new byte[2];
    private static final Logger logger = Logger.getLogger(
            PortUnificationServerHandler.class.getName());
	@Override
	protected Object decode(ChannelHandlerContext ctx, Channel channel,
			ChannelBuffer buffer, VoidEnum state) throws Exception {
		buffer.readBytes(tag);
		int len = buffer.readInt();
		if(tag[0]=='A'&&tag[1]=='U'){
			throw new Error("Unknow tag recv1:"+tag);
			//return buffer.readBytes(len);
		}else if(tag[0]=='I'&&tag[1]=='M'){
			throw new Error("Unknow tag recv2:"+tag);
		}else if(tag[0]=='F'&&tag[1]=='L'){
			throw new Error("Unknow tag recv3:"+tag);
		}else if(tag[0]=='O'&&tag[1]=='K'){
			//ctx.getPipeline().addLast("Logic_Handler", new LogicHandler());
			//System.out.println("tag:OK:"+len);
			return buffer.readBytes(len);
		}else{//exception...
			throw new Error("Unknow tag recv:"+tag);
			//return tag;
		}
	}
    @Override
    public void channelDisconnected(ChannelHandlerContext ctx, ChannelStateEvent e){
		try {
			User user = UserManager.getInstance().getUserBySessionId(e.getChannel().getId());
	    	if(user!=null){
	    		user.setSessionId(new Integer(-1));
	    		user.setStatus(userStatus.OFFLINE);
	    	}
	    	UserManager.getInstance().updateUserStatus(user);
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} 
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
    }
}
