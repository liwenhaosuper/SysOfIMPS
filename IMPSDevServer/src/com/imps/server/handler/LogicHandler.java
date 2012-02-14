package com.imps.server.handler;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;

import com.imps.server.handler.baseLogic.AddFriendReq;
import com.imps.server.handler.baseLogic.AddFriendRsp;
import com.imps.server.handler.baseLogic.FriendListRequest;
import com.imps.server.handler.baseLogic.Login;
import com.imps.server.handler.baseLogic.Register;
import com.imps.server.handler.baseLogic.SearchFriendReq;
import com.imps.server.handler.baseLogic.SendAudio;
import com.imps.server.handler.baseLogic.SendImageReq;
import com.imps.server.handler.baseLogic.SendMessage;
import com.imps.server.handler.baseLogic.SendPTPAudioReq;
import com.imps.server.handler.baseLogic.SendPTPAudioRsp;
import com.imps.server.main.basetype.CommandId;
import com.imps.server.main.basetype.User;
import com.imps.server.main.basetype.userStatus;
import com.imps.server.manager.UserManager;

public class LogicHandler extends SimpleChannelUpstreamHandler{
    private static final Logger logger = Logger.getLogger(
            LogicHandler.class.getName());
	private byte cmdType;
    @Override
    public void messageReceived(
            ChannelHandlerContext ctx, MessageEvent e){
    	ChannelBuffer buffer =(ChannelBuffer)e.getMessage();
    	cmdType = buffer.readByte();
    	switch(cmdType){
		case CommandId.C_LOGIN_REQ:
			/**客户端登录请求*/
			System.out.println("server:login request received!");
			new Login(e.getChannel(),buffer).run();
			break;
		case CommandId.C_HEARTBEAT_REQ:
			/** 心跳检测客户端请求,同时接收地理位置信息 */
			System.out.println("server: heartbeat received!");
			break;
		case CommandId.C_FRIENDLIST_REFURBISH_REQ:
			/**好友列表刷新请求*/
			System.out.println("server: friend list refresh");
			new FriendListRequest(e.getChannel(),buffer).run();
			break;
		case CommandId.C_ADDFRIEND_REQ:
		/**客户端添加好友请求*/
			System.out.println("server:add friend request receive");
			new AddFriendReq(e.getChannel(),buffer).run();
			break;
		case CommandId.C_ADDFRIEND_RSP:
			/** 客户端添加好友响应 */
			System.out.println("server:add friend response receive");
			new AddFriendRsp(e.getChannel(),buffer).run();
			break;
		case CommandId.C_SEND_MSG:
			/**客户端发送消息*/
			System.out.println("server: message from client to friend received");
			new SendMessage(e.getChannel(),buffer).run();
			break;
		case CommandId.C_REGISTER:
			/** 客户端用户注册*/
			System.out.println("server: register msg received");
			new Register(e.getChannel(),buffer).run();
			break;
		case CommandId.C_AUDIO_REQ:
			/**客户端音频文件请求**/
			System.out.println("server: audio msg received");
			new SendAudio(e.getChannel(),buffer).run();
			break;
		case CommandId.C_PTP_AUDIO_REQ:
			System.out.println("server: p2p audio req received");
			new SendPTPAudioReq(e.getChannel(),buffer,e.getRemoteAddress()).run();
			break;
		case CommandId.C_PTP_AUDIO_RSP:
			System.out.println("server: p2p audio rsp received");
			new SendPTPAudioRsp(e.getChannel(),buffer,e.getRemoteAddress()).run();
			break;
		case CommandId.C_PTP_VIDEO_REQ:

			break;
		case CommandId.C_PTP_VIDEO_RSP:

			break;
		case CommandId.C_SEARCH_FRIEND_REQ:
			new SearchFriendReq(e.getChannel(),buffer).run();
			break;
		case CommandId.C_IMAGE_REQ:
			new SendImageReq(e.getChannel(),buffer).run();
			break;
		default:
			System.out.println("server:unhandled msg received:"+cmdType);
			buffer.clear();
			break;
    	}
    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) {
    	
    	if(e.getCause() instanceof OutOfMemoryError){
    		logger.log(Level.WARNING,"OutOfMemnoryError");
       		try {
				super.exceptionCaught(ctx,e);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
    	}else{
    		try {
				super.exceptionCaught(ctx,e);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
    	}
    }
    @Override
    public void channelDisconnected(ChannelHandlerContext ctx, ChannelStateEvent e){
		try {
			User user = UserManager.getInstance().getUserBySessionId(e.getChannel().getId());
	    	if(user!=null){
	    		user.setSessionId(new Integer(-1));
	    		user.setStatus(userStatus.OFFLINE);
	    		UserManager.getInstance().updateUserStatus(user);
	    	}
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
}
