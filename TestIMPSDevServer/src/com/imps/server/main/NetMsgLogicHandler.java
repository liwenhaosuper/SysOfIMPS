package com.imps.server.main;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;

public class NetMsgLogicHandler extends SimpleChannelUpstreamHandler{
	
	private static String TAG = NetMsgLogicHandler.class.getCanonicalName()+"<<<";
	private static boolean DEBUG = true;
	private byte cmdType;

	public NetMsgLogicHandler(){
	}
    @Override
    public void channelDisconnected(ChannelHandlerContext ctx, ChannelStateEvent e){
    	if(DEBUG) System.out.println(TAG+"tcp disconnected to:"+e.getChannel().getRemoteAddress().toString());
    }
    @Override
    public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) {
    	if(DEBUG) System.out.println(TAG+"tcp connected to:"+e.getChannel().getRemoteAddress().toString());
    }
	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) 
	{
		Channel session = e.getChannel();
		ChannelBuffer inMsg =(ChannelBuffer)e.getMessage();
		cmdType = inMsg.readByte();
		switch(cmdType) {
		case CommandId.S_ERROR:
			if(DEBUG){System.out.println(TAG+"error msg recv.");}
			
				int errorCode = inMsg.readInt();
				int detailCode = 0;
				switch(errorCode){
				case 1://login error
					detailCode = inMsg.readInt();
					switch(detailCode){
					case 1:
						//username or password not valid
						if(DEBUG) System.out.println(TAG+"Login error:username or password not valid.");
						////ServiceManager.getmReceiver().LoginFailed(IMPSDev.getContext().getResources().getString(R.string.login_username_password_error));
						break;
					case 2://username login at other place
						if(DEBUG) System.out.println(TAG+"Login error:account logins at other place.");
						////ServiceManager.getmReceiver().LoginFailed(IMPSDev.getContext().getResources().getString(R.string.login_account_at_other_place));
						break;
					case 3://other error
						if(DEBUG) System.out.println(TAG+"Unknown login error.");
						//ServiceManager.getmReceiver().LoginFailed(IMPSDev.getContext().getResources().getString(R.string.login_unknown_error));
						break;
					default:
						break;
					}
					break;
				case 2://register error
					detailCode = inMsg.readInt();
					switch(detailCode){
					case 1://username already exists
						if(DEBUG) System.out.println(TAG+"Reg error: username already exists");

						//ServiceManager.getmReceiver().RegisterFailed(IMPSDev.getContext().getResources().getString(R.string.register_user_exist));
						break;
					case 2://other error
						if(DEBUG) System.out.println(TAG+"Reg error: unknown");
						//ServiceManager.getmReceiver().RegisterFailed(IMPSDev.getContext().getResources().getString(R.string.register_fail));
						break;
					}
					break;
				case 3://send sms error
					detailCode = inMsg.readInt();
					int smsId = inMsg.readInt();
					switch(detailCode){
					case 1://send success
						if(DEBUG) System.out.println(TAG+"Send sms success:"+smsId);
						break;
					case 2://friend not exists or not online
						if(DEBUG) System.out.println(TAG+"Send sms fail(friend not exists or offline):"+smsId);
						break;
					case 3://friend rejects
						if(DEBUG) System.out.println(TAG+"Send sms fail(friend rejects):"+smsId);
						break;
					case 4://other error
						if(DEBUG) System.out.println(TAG+"Send sms fail(unknown error):"+smsId);
						break;
					}
					break;
				case 4://send image error
					detailCode = inMsg.readInt();
					int imageId = inMsg.readInt();
					switch(detailCode){
					case 1://send success
						if(DEBUG) System.out.println(TAG+"Send image success:"+imageId);
						break;
					case 2://friend not exists or not online
						if(DEBUG) System.out.println(TAG+"Send image fail(friend not exists or offline):"+imageId);
						break;
					case 3://friend rejects
						if(DEBUG) System.out.println(TAG+"Send image fail(friend rejects):"+imageId);
						break;
					case 4://other error
						if(DEBUG) System.out.println(TAG+"Send image fail(unknown error):"+imageId);
						break;
					}
					break;
				case 5://send audio error
					detailCode = inMsg.readInt();
					int audioId = inMsg.readInt();
					switch(detailCode){
					case 1://send success
						if(DEBUG) System.out.println(TAG+"Send audio success:"+audioId);
						break;
					case 2://friend not exists or not online
						if(DEBUG) System.out.println(TAG+"Send audio fail(friend not exists or offline):"+audioId);
						break;
					case 3://friend rejects
						if(DEBUG) System.out.println(TAG+"Send audio fail(friend rejects):"+audioId);
						break;
					case 4://other error
						if(DEBUG) System.out.println(TAG+"Send audio fail(unknown error):"+audioId);
						break;
					
					}
					break;
				case 6: //p2p audio request error
					detailCode = inMsg.readInt();
					int p2paudioId = inMsg.readInt();
					switch(detailCode){
					case 1://send success
						if(DEBUG) System.out.println(TAG+"p2p audio req sent success.");
						break;
					case 2://friend not exists or not online
						if(DEBUG) System.out.println(TAG+"p2p audio req sent faile:friend offline");
						break;
					case 3://friend reject
						if(DEBUG) System.out.println(TAG+"p2p audio req sent fail:friend reject");
						break;
					case 4://net error
						if(DEBUG) System.out.println(TAG+"p2p audio req sent faile:net error");
						break;
					case 5://other error
						if(DEBUG) System.out.println(TAG+"p2p audio req sent faile:unknown error");
						break;
					}
					break;
				case 7: //p2p video request error
					detailCode = inMsg.readInt();
					int p2pvideoId = inMsg.readInt();
					switch(detailCode){

					case 1://send success
						if(DEBUG) System.out.println(TAG+"p2p video req sent success.");
						break;
					case 2://friend not exists or not online
						if(DEBUG) System.out.println(TAG+"p2p video req sent faile:friend offline");
						break;
					case 3://friend reject
						if(DEBUG) System.out.println(TAG+"p2p video req sent fail:friend reject");
						break;
					case 4://net error
						if(DEBUG) System.out.println(TAG+"p2p video req sent faile:net error");
						break;
					case 5://other error
						if(DEBUG) System.out.println(TAG+"p2p video req sent faile:unknown error");
						break;
					
					}
					break;
				case 8://send path error
					detailCode = inMsg.readInt();
					int sendPathId = inMsg.readInt();
					switch(detailCode){
					case 1://send success
						break;
					case 2://send interrupt
						break;
					case 3://net error
						break;
					case 4://other error
						break;
					}
					break;
				case 9://request path error
					detailCode = inMsg.readInt();
					int requestPathId = inMsg.readInt();
					switch(detailCode){
					case 1://not exists
						break;
					case 2://send fail
						break;
					case 3://net error
						break;
					case 4://other error
						break;
					}
					break;
				case 10://refresh status error
					detailCode = inMsg.readInt();
					switch(detailCode){
					case 1://refresh success
						break;
					case 2://refresh fail
						break;
					}
					break;
				case 11://get friend list error
					detailCode = inMsg.readInt();
					int requestListId = inMsg.readInt();
					switch(detailCode){
					case 1://get friend fail
						if(DEBUG){System.out.println(TAG+"get friend fail.");}
						break;
					}
					break;
				case 12://other error
					if(DEBUG){System.out.println(TAG+"other error msg recv.");}
					break;
				default://not handle error message 
					if(DEBUG){System.out.println(TAG+"not handle error msg recv.");}
					break;
				}
			
			break;
		case CommandId.S_LOGIN_RSP:
			if(DEBUG){System.out.println(TAG+"login rsp msg recv");}
			//ServiceManager.getmReceiver().LoginSuccess(user);
			break;
		case CommandId.S_HEARTBEAT_RSP:
			if(DEBUG){System.out.println(TAG+"heart beat msg recv.");}
			break;
		case CommandId.S_FRIENDLIST_REFURBISH_RSP:
			if(DEBUG){System.out.println(TAG+"friendlist refresh msg recv.");}
			

			//ServiceManager.getmReceiver().recvFriendList();
			break;
		case CommandId.S_ADDFRIEND_REQ:
			if(DEBUG){System.out.println(TAG+"add fri req msg recv.");}
			//ServiceManager.getmReceiver().onRecvFriendReq(reqFriName);
			//ServiceManager.getmSound().playCommonTone();
			break;
		case CommandId.S_ADDFRIEND_RSP:
			if(DEBUG){System.out.println(TAG+"add fri rsp msg recv.");}
			//ServiceManager.getmReceiver().onRecvFriendRsp(rspFriName, rsprel);
			//ServiceManager.getmSound().playCommonTone();
			break;
		case CommandId.S_SEND_MSG:
			if(DEBUG){System.out.println(TAG+"send msg recv.");}
			//ServiceManager.getmSound().playNewSms();
		    //ServiceManager.getmReceiver().onSmsRecv(media);
			break;
		case CommandId.S_REGISTER:
			if(DEBUG){System.out.println(TAG+"register msg recv.");}
			//ServiceManager.getmReceiver().RegisterSuccess();
			break;
		case CommandId.S_STATUS_NOTIFY:
			if(DEBUG){System.out.println(TAG+"status notify msg recv.");}

			//ServiceManager.getmReceiver().FriendStatusNotify(tuser);
			break;
		case CommandId.S_PTP_AUDIO_REQ:
			if(DEBUG){System.out.println(TAG+"p2p audio req msg recv.");}
			//ServiceManager.getmReceiver().onP2PAudioReq(audiot.friName,audiot.ip,audiot.port);
			//ServiceManager.getmSound().playRingTone();
			break;
		case CommandId.S_PTP_AUDIO_RSP:
			if(DEBUG){System.out.println(TAG+"p2p audio rsp msg recv.");}
			//ServiceManager.getmReceiver().onP2PAudioRsp(audiop.friName,audiop.rel,audiop.ip,audiop.port);
			break;
		case CommandId.S_PTP_VIDEO_REQ:
			if(DEBUG){System.out.println(TAG+"p2p video req msg recv.");}
			//ServiceManager.getmReceiver().onP2PVideoReq(videor.friName,videor.ip,videor.port);
			//ServiceManager.getmSound().playRingTone();
			break;
		case CommandId.S_PTP_VIDEO_RSP:
			if(DEBUG){System.out.println(TAG+"p2p video rsp msg recv.");}
			//ServiceManager.getmReceiver().onP2PVideoRsp(videop.friName,videop.rel,videop.ip,videop.port);
			break;
		case CommandId.S_SEARCH_FRIEND_RSP:
			if(DEBUG){System.out.println(TAG+"search friends rsp msg recv.");}
			//ServiceManager.getmReceiver().recvSearchFriendRes(userNames);
			break;
		case CommandId.S_SEND_MSG_RSP:
			if(DEBUG) System.out.println(TAG+"send sms rsp recv.");
			//TODO:			break;
		case CommandId.S_IMAGE_REQ:
			if(DEBUG) System.out.println(TAG+"send image req recv.");

			//ServiceManager.getmReceiver().onImageRecv(friName, imageId, rel==1?true:false, frame);
			break;
		case CommandId.S_AUDIO_REQ:
			if(DEBUG) System.out.println(TAG+"send audio req recv.");
			
			break;
		default:
			if(DEBUG){System.out.println(TAG+"not handle msg recv.CommandId is:"+cmdType);}
			break;
		}
	}

	//Message Parser
	
}
