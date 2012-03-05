/*
 * Author: liwenhaosuper
 * Date: 2011/5/19
 * Description:
 *     logic action handler
 */





package com.imps.handler;

import java.sql.SQLException;

import com.imps.base.CommandId;
import com.imps.base.InputMessage;
import com.imps.main.HeartBeat;
import com.yz.net.IoHandlerAdapter;
import com.yz.net.IoSession;
import com.yz.net.NetMessage;

public class LogicHandler extends IoHandlerAdapter {

	@Override
	/**
	 * 处理从服务器中获取到的信息
	 */
	public void messageReceived(IoSession session, NetMessage msg) {

		
		InputMessage inMsg = (InputMessage) msg;
		switch(inMsg.getCmdType()) {
		case CommandId.S_ERROR:
			/**得到错误信息**/
			System.out.println("client:error msg received");
			/**
			 * 在此处添加错误信息处理
			 */
			try {
				new IMService().new ErrorMsgHandler(session,inMsg).run();
			} catch (SQLException e3) {
				// TODO Auto-generated catch block
				e3.printStackTrace();
			}
			break;
			
		case CommandId.S_LOGIN_RSP:
			/**服务端登录响应信息*/
			System.out.println("client: server login response received!");
            try {				
            	new IMService().new MyLogin(session,inMsg).run();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			break;
		case CommandId.S_HEARTBEAT_RSP:
			/** 心跳检测服务端响应 */
			System.out.println("client: heartbeat response received!");
			try {
				new HeartBeat(session,inMsg).run();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			break;
		case CommandId.S_FRIENDLIST_REFURBISH_RSP:
			/**好友列表刷新响应*/
			System.out.println("client: friend list refresh from server");
			try {
				new IMService().new FriendListRequest(session,inMsg).run();
			} catch (SQLException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
			break;
		case CommandId.S_ADDFRIEND_REQ:
		/**服务端发送的添加好友请求*/
			System.out.println("client:add friend request from server");
            try {
            	new IMService().new AddFriReq(session,inMsg).run();
			} catch (SQLException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
			
			break;
		case CommandId.S_ADDFRIEND_RSP:
			/** 服务端发送的添加好友结果响应 */
			System.out.println("client:add friend response from server");
            try {
            	new IMService().new AddFriRsp(session,inMsg).run();
			} catch (SQLException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
			break;
		case CommandId.S_SEND_MSG:
			/**客户端获取到服务器发送的消息*/
			System.out.println("client: message from server received");
			try {
				new IMService().new SendMessage(session,inMsg).run();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			break;
		case CommandId.S_REGISTER:
			/** 服务端发送的用户注册响应*/
			System.out.println("client: register response from server received");
			try {
				new IMService().new Register(session,inMsg).run();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		case CommandId.S_STATUS_NOTIFY:
			/**
			 * 服务器发送的用户状态更新
			 */
			System.out.println("Client: friend status notify received");
			try {
				new IMService().new StatusNotify(session,inMsg).run();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		case CommandId.S_PTP_AUDIO_REQ:
			System.out.println("client:audio request from server");
			try {
				new IMService().new SendPTPAudioReq(session,inMsg).run();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		case CommandId.S_PTP_AUDIO_RSP:
			System.out.println("client:audio response from server");
			try {
				new IMService().new SendPTPAudioRsp(session,inMsg).run();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		case CommandId.S_PTP_VIDEO_REQ:
			System.out.println("client:video request from server");
			try {
				new IMService().new SendPTPVideoReq(session,inMsg).run();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		case CommandId.S_PTP_VIDEO_RSP:
			System.out.println("client:video response from server");
			try {
				new IMService().new SendPTPVideoRsp(session,inMsg).run();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		}
	}
}
