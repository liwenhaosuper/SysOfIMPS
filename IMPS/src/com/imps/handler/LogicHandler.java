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
	 * ����ӷ������л�ȡ������Ϣ
	 */
	public void messageReceived(IoSession session, NetMessage msg) {

		
		InputMessage inMsg = (InputMessage) msg;
		switch(inMsg.getCmdType()) {
		case CommandId.S_ERROR:
			/**�õ�������Ϣ**/
			System.out.println("client:error msg received");
			/**
			 * �ڴ˴���Ӵ�����Ϣ����
			 */
			try {
				new IMService().new ErrorMsgHandler(session,inMsg).run();
			} catch (SQLException e3) {
				// TODO Auto-generated catch block
				e3.printStackTrace();
			}
			break;
			
		case CommandId.S_LOGIN_RSP:
			/**����˵�¼��Ӧ��Ϣ*/
			System.out.println("client: server login response received!");
            try {				
            	new IMService().new MyLogin(session,inMsg).run();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			break;
		case CommandId.S_HEARTBEAT_RSP:
			/** �������������Ӧ */
			System.out.println("client: heartbeat response received!");
			try {
				new HeartBeat(session,inMsg).run();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			break;
		case CommandId.S_FRIENDLIST_REFURBISH_RSP:
			/**�����б�ˢ����Ӧ*/
			System.out.println("client: friend list refresh from server");
			try {
				new IMService().new FriendListRequest(session,inMsg).run();
			} catch (SQLException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
			break;
		case CommandId.S_ADDFRIEND_REQ:
		/**����˷��͵���Ӻ�������*/
			System.out.println("client:add friend request from server");
            try {
            	new IMService().new AddFriReq(session,inMsg).run();
			} catch (SQLException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
			
			break;
		case CommandId.S_ADDFRIEND_RSP:
			/** ����˷��͵���Ӻ��ѽ����Ӧ */
			System.out.println("client:add friend response from server");
            try {
            	new IMService().new AddFriRsp(session,inMsg).run();
			} catch (SQLException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
			break;
		case CommandId.S_SEND_MSG:
			/**�ͻ��˻�ȡ�����������͵���Ϣ*/
			System.out.println("client: message from server received");
			try {
				new IMService().new SendMessage(session,inMsg).run();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			break;
		case CommandId.S_REGISTER:
			/** ����˷��͵��û�ע����Ӧ*/
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
			 * ���������͵��û�״̬����
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
