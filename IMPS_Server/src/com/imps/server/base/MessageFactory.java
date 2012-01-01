
/*
 * Author: liwenhaosuper
 * Date: 2011/5/19
 * Description:
 *     message factory, define all message type
 */


package com.imps.server.base;

import java.io.IOException;
import java.io.UnsupportedEncodingException;


public class MessageFactory {
	
	/**
	 * 创建服务器端登录响应
	 * @param type 协议类型
	 * @param status 状态
	 * @return
	 */
	public static OutputMessage createSLoginRsp() {
		OutputMessage outMsg = new OutputMessage(CommandId.S_LOGIN_RSP);
		
		//我什么也不做。。。
/*		try {
			//outMsg.getOutputStream().writeInt(validateCode);
		} catch (IOException e) {}
		*/
		return outMsg;
	}
	/**
	 * 创建服务端注册响应
	 * @return
	 * @throws IOException 
	 */
	public static OutputMessage createSRegisterRsp() throws IOException
	{
		OutputMessage outMsg = new OutputMessage(CommandId.S_REGISTER);
		outMsg.getOutputStream().writeInt(1);  //注册成功
		return outMsg;
	}
	
	/**
	 * 创建服务端错误响应
	 */
	public static OutputMessage createErrorMsg()
	{
		OutputMessage outMsg = new OutputMessage(CommandId.S_ERROR);
		return outMsg;
	}
	
	/**
	 * 创建服务端心跳响应
	 */
	public static OutputMessage createSHeartbeatRsp()
	{
		OutputMessage outMsg = new OutputMessage(CommandId.S_HEARTBEAT_RSP);
		return outMsg;
	}
	
	/**
	 * 创建在线状态通知
	 * @param username
	 * @param status
	 * @return
	 */
	public static OutputMessage createOnlineStatusNotify(String username, byte status) {
		OutputMessage outMsg = new OutputMessage(CommandId.S_STATUS_NOTIFY);
		try {
			long len = username.getBytes("gb2312").length;
			byte[] nm = username.getBytes("GB2312");
			outMsg.getOutputStream().writeLong(len);
			outMsg.getOutputStream().write(nm);
			outMsg.getOutputStream().writeByte(status);
		} catch (IOException e) {}
		
		return outMsg;
	}
	/**
	 * 创建服务器发送信息响应
	 * friendname:目的地用户
	 * 
	 */
	public static OutputMessage createSSendMsg(String friendName,String msg,String datetime)
	{
		OutputMessage outMsg = new OutputMessage(CommandId.S_SEND_MSG);
		try {
			//发送好友名
			long len = friendName.getBytes("gb2312").length;
			byte[] fri = new byte[(int)len];
			fri = friendName.getBytes("gb2312");
			outMsg.getOutputStream().writeLong(len);
			outMsg.getOutputStream().write(fri);
			//发送信息内容
			len = msg.getBytes("gb2312").length;
			byte[] msgcont = new byte[(int)len];
			msgcont = msg.getBytes("gb2312");
			outMsg.getOutputStream().writeLong(len);
			outMsg.getOutputStream().write(msgcont);
			//发送时间
			len = datetime.getBytes("gb2312").length;
			byte[] tm = new byte[(int)len];
			tm = datetime.getBytes("gb2312");
			outMsg.getOutputStream().writeLong(len);
			outMsg.getOutputStream().write(tm);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch(IOException e2)
		{
			e2.printStackTrace();
		}
		return outMsg;
	}
	/**
	 * 创建服务器发送音频响应
	 * @param friendName
	 * @return
	 */
	public static OutputMessage createSAudioRsp(String friendName,byte[] data,String datetime)
	{
		OutputMessage outMsg = new OutputMessage(CommandId.S_AUDIO_RSP);
		try {
			//发送好友名
			long len = friendName.getBytes("gb2312").length;
			byte[] fri = new byte[(int)len];
			fri = friendName.getBytes("gb2312");
			outMsg.getOutputStream().writeLong(len);
			outMsg.getOutputStream().write(fri);
			//发送信息内容
			len = data.length;
			byte[] listData = new byte[(int)len];
			outMsg.getOutputStream().writeLong(len);
			outMsg.getOutputStream().write(listData);
			//发送时间
			len = datetime.getBytes("gb2312").length;
			byte[] tm = new byte[(int)len];
			tm = datetime.getBytes("gb2312");
			outMsg.getOutputStream().writeLong(len);
			outMsg.getOutputStream().write(tm);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch(IOException e2)
		{
			e2.printStackTrace();
		}
		return outMsg;
	}
	
	/*
	 * 创建服务端添加好友请求
	 */
	public static OutputMessage createSAddFriReq(String friendName)
	{
		OutputMessage outMsg = new OutputMessage(CommandId.S_ADDFRIEND_REQ);

		try {
			long len = friendName.getBytes("gb2312").length;
			byte[] fri = new byte[(int)len];
			fri = friendName.getBytes("gb2312");
			outMsg.getOutputStream().writeLong(len);
			outMsg.getOutputStream().write(fri);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch(IOException e2)
		{
			e2.printStackTrace();
		}

		return outMsg;
	}
	/**
	 * 创建服务端添加好友响应
	 */
	public static OutputMessage createSAddFriRsp(String friendName,int res)
	{
		OutputMessage outMsg = new OutputMessage(CommandId.S_ADDFRIEND_RSP);

		try {
			long len = friendName.getBytes("gb2312").length;
			byte[] fri = new byte[(int)len];
			fri = friendName.getBytes("gb2312");
			outMsg.getOutputStream().writeLong(len);
			outMsg.getOutputStream().write(fri);
			outMsg.getOutputStream().writeInt(res);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch(IOException e2)
		{
			e2.printStackTrace();
		}

		return outMsg;
	}
	/**
	 * 创建服务器音频请求
	 */
	public static OutputMessage createSPTPAudioReq(String username,String ip,int port)
	{
		OutputMessage outMsg = new OutputMessage(CommandId.S_PTP_AUDIO_REQ);
		try {
			//username
			long len = username.getBytes("gb2312").length;
			byte[] nm = username.getBytes("GB2312");
			outMsg.getOutputStream().writeLong(len);
			outMsg.getOutputStream().write(nm);
			//ip
			len = ip.getBytes("gb2312").length;
			nm = ip.getBytes("GB2312");
			outMsg.getOutputStream().writeLong(len);
			outMsg.getOutputStream().write(nm);
			//port
			outMsg.getOutputStream().writeInt(port);
		} catch (IOException e) {}		
		return outMsg;
	}
	/**
	 * 创建服务器视频请求
	 */
	public static OutputMessage createSPTPVideoReq(String username,String ip,int port)
	{
		OutputMessage outMsg = new OutputMessage(CommandId.S_PTP_VIDEO_REQ);
		try {
			//username
			long len = username.getBytes("gb2312").length;
			byte[] nm = username.getBytes("GB2312");
			outMsg.getOutputStream().writeLong(len);
			outMsg.getOutputStream().write(nm);
			//ip
			len = ip.getBytes("gb2312").length;
			nm = ip.getBytes("GB2312");
			outMsg.getOutputStream().writeLong(len);
			outMsg.getOutputStream().write(nm);
			//port
			outMsg.getOutputStream().writeInt(port);
		} catch (IOException e) {}		
		return outMsg;
	}
	
	
	/**
	 * 创建服务器音频响应
	 */
	public static OutputMessage createSPTPAudioRsp(String username,String ip,int port,boolean res)
	{
		OutputMessage outMsg = new OutputMessage(CommandId.S_PTP_AUDIO_RSP);
		try {
			//username
			long len = username.getBytes("gb2312").length;
			byte[] nm = username.getBytes("GB2312");
			outMsg.getOutputStream().writeLong(len);
			outMsg.getOutputStream().write(nm);
			//res
			outMsg.getOutputStream().writeInt(res?1:0);
			//ip
			outMsg.getOutputStream().writeLong(ip.getBytes("gb2312").length);
			outMsg.getOutputStream().write(ip.getBytes("gb2312"));
			//port
			outMsg.getOutputStream().writeInt(port);
		} catch (IOException e) {}		
		return outMsg;
	}
	
	/**
	 * 创建服务器视频响应
	 */
	public static OutputMessage createSPTPVideoRsp(String username,String ip,int port,boolean res)
	{
		OutputMessage outMsg = new OutputMessage(CommandId.S_PTP_VIDEO_RSP);
		try {
			//username
			long len = username.getBytes("gb2312").length;
			byte[] nm = username.getBytes("GB2312");
			outMsg.getOutputStream().writeLong(len);
			outMsg.getOutputStream().write(nm);
			//res
			outMsg.getOutputStream().writeInt(res?1:0);
			//ip
			outMsg.getOutputStream().writeLong(ip.getBytes("gb2312").length);
			outMsg.getOutputStream().write(ip.getBytes("gb2312"));
			//port
			outMsg.getOutputStream().writeInt(port);
		} catch (IOException e) {}		
		return outMsg;
	}
	
	
}
	
/*	
	
*//**
	 * <p>
	 * 创建好友例表刷新响应
	 * </p>
	 * <br>
	 * @param type
	 * @param players
	 * @return
	 *//*
	public static OutputMessage createSFriendListRsp(User[] users) {
		
		OutputMessage outMsg = new OutputMessage(CommandId.S_FRIENDLIST_REFURBISH_RSP);
		try {
			outMsg.getOutputStream().writeShort(users.length);
			for(int i=0; i<users.length; i++) {
				outMsg.getOutputStream().writeLong(users[i].getUserid());
				outMsg.getOutputStream().writeUTF(users[i].getUsername());
				//outMsg.getOutputStream().writeInt(users[i].getStatus());
			}
		}
		catch(IOException e) {}
		
		return outMsg;
	}
	
	
	
	*//**
	 * <p>
	 * 创建验证错误
	 * </p>
	 * <br>
	 * @param type
	 * @return
	 *//*
	public static OutputMessage createValidateErr() {
		OutputMessage outMsg = new OutputMessage(ChatCommandId.S_VALIDATE_ERR);
		return outMsg;
	}
	
	
	*/
	/**
	 * 创建心跳响应
	 * @param type
	 * @return
	 *//*
	public static OutputMessage createHearTbeatRsp() {
		OutputMessage outMsg = new OutputMessage(ChatCommandId.S_HEARTBEAT_RSP);
		return outMsg;
	}
	
	
	public static OutputMessage createError(byte errorcode) {
		OutputMessage outMsg = new OutputMessage(ChatCommandId.S_HEARTBEAT_RSP);
		try {
			outMsg.getOutputStream().writeByte(errorcode);
		}
		catch(IOException e) {}
		
		return outMsg;
	}
	
	
	*//**
	 * <p>
	 * 创建添加好友响应
	 * </p>
	 * <br>
	 * @param type
	 * @param friend
	 * @return
	 *//*
	public static OutputMessage createAddFriendRsp(Player friend) {
		OutputMessage outMsg = new OutputMessage(ChatCommandId.S_ADDFRIEND_RSP);
		
		byte status = 0;
		if(friend == null) {
			status = 1;      //好友不存在
		}
		
		try {
			outMsg.getOutputStream().writeByte(status);
			if(status == 0) {
				outMsg.getOutputStream().writeUTF(friend.getNickName());
				outMsg.getOutputStream().writeByte(friend.isOnline()? 1 : 0);
			}
		}
		catch(IOException e) {}

		return outMsg;		
	}
	
	
	
	*//**
	 * <p>
	 * 创建消息发送
	 * </p>
	 * <br>
	 * @param type
	 * @param message
	 * @return
	 *//*
	public static OutputMessage createSendMessage(String message) {
		OutputMessage outMsg = new OutputMessage(ChatCommandId.S_SEND_MSG);
		
		try {
			outMsg.getOutputStream().writeUTF(message);
		}
		catch(IOException e) {}
		
		
		return outMsg;
	}

	
	*//**
	 * <p>
	 * 创建CMWAP绑定消息
	 * </p>
	 * <br>
	 * @param msgs
	 * @return
	 *//*
	public static CmWapBindMessage createCmWapBindMessage(OutputMessage[] msgs) {
		CmWapBindMessage msg = new CmWapBindMessage(ChatCommandId.S_CMWAPBIND, msgs);
		return msg;
	}
	
	*/
