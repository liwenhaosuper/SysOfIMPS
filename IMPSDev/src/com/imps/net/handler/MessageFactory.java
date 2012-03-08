/**
 * 
 */
package com.imps.net.handler;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;

import android.util.Log;

import com.imps.IMPSDev;
import com.imps.basetypes.CommandId;
import com.imps.basetypes.OutputMessage;

/**
 * @author liwenhaosuper
 *
 */
public class MessageFactory {
	private static String TAG = MessageFactory.class.getCanonicalName();
	private static boolean DEBUG = IMPSDev.isDEBUG();
	public static OutputMessage createCLoginReq(String username,String password){
		OutputMessage outMsg = new OutputMessage(CommandId.C_LOGIN_REQ);
		try {
			outMsg.getOutputStream().writeInt(username.length());
			outMsg.getOutputStream().write(username.getBytes("GB2312"));
			outMsg.getOutputStream().writeInt(password.length());
			outMsg.getOutputStream().write(password.getBytes("gb2312"));
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return outMsg;
	}
	public static OutputMessage createCRegisterReq(String username,String password,int gender,String email){
		OutputMessage outMsg = new OutputMessage(CommandId.C_REGISTER);
		try {
			outMsg.getOutputStream().writeInt(username.length());
			outMsg.getOutputStream().write(username.getBytes("GB2312"));
			outMsg.getOutputStream().writeInt(password.length());
			outMsg.getOutputStream().write(password.getBytes("gb2312"));
			outMsg.getOutputStream().writeInt(gender);
			outMsg.getOutputStream().writeInt(email.length());
			outMsg.getOutputStream().write(email.getBytes("gb2312"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return outMsg;
	}
	public static OutputMessage createCSendMsgReq(String username,String friName,String msg,int sid){
		OutputMessage outMsg = new OutputMessage(CommandId.C_SEND_MSG);
		try {
			outMsg.getOutputStream().writeInt(username.length());
			outMsg.getOutputStream().write(username.getBytes("GB2312"));
			outMsg.getOutputStream().writeInt(friName.length());
			outMsg.getOutputStream().write(friName.getBytes("gb2312"));
			outMsg.getOutputStream().writeInt(sid);
			outMsg.getOutputStream().writeInt(msg.getBytes("gb2312").length);
			outMsg.getOutputStream().write(msg.getBytes("gb2312"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return outMsg;
	}
	public static OutputMessage createCFriendListReq(String username){
		OutputMessage outMsg = new OutputMessage(CommandId.C_FRIENDLIST_REFURBISH_REQ);
		try {
			outMsg.getOutputStream().writeInt(username.length());
			outMsg.getOutputStream().write(username.getBytes("GB2312"));
		}catch(IOException e){
			e.printStackTrace();
		}
		return outMsg;
	}
	public static OutputMessage createCAddFriReq(String username,String friName)
	{
		OutputMessage outMsg = new OutputMessage(CommandId.C_ADDFRIEND_REQ);
		try {
			outMsg.getOutputStream().writeInt(username.length());
			outMsg.getOutputStream().write(username.getBytes("GB2312"));
			outMsg.getOutputStream().writeInt(friName.length());
			outMsg.getOutputStream().write(friName.getBytes("GB2312"));
		}catch(IOException e){
			e.printStackTrace();
		}
		return outMsg;
	}
	
	public static OutputMessage createCAddFriRsp(String username,String friName,boolean res)
	{
		OutputMessage outMsg = new OutputMessage(CommandId.C_ADDFRIEND_RSP);
		try {
			outMsg.getOutputStream().writeInt(username.length());
			outMsg.getOutputStream().write(username.getBytes("GB2312"));
			outMsg.getOutputStream().writeInt(friName.length());
			outMsg.getOutputStream().write(friName.getBytes("GB2312"));
			outMsg.getOutputStream().writeInt(res==true?1:0);
		}catch(IOException e){
			e.printStackTrace();
		}
		return outMsg;
	}
	
	public static OutputMessage createCStatusNotify(String username,byte status)
	{
		OutputMessage outMsg = new OutputMessage(CommandId.C_STATUS_NOTIFY);
		try {
			outMsg.getOutputStream().writeInt(username.length());
			outMsg.getOutputStream().write(username.getBytes("GB2312"));
			outMsg.getOutputStream().writeByte(status);
		}catch(IOException e){
			e.printStackTrace();
		}
		return outMsg;
	}
	public static OutputMessage createCHeartbeatReq(String username,int LatitudeE6,int LongitudeE6)
	{
		OutputMessage outMsg = new OutputMessage(CommandId.C_HEARTBEAT_REQ);
		try {
			outMsg.getOutputStream().writeInt(username.length());
			outMsg.getOutputStream().write(username.getBytes("GB2312"));
			SimpleDateFormat tempDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String datetime = tempDate.format(new java.util.Date());
			//发送时间
			outMsg.getOutputStream().writeInt(datetime.getBytes("gb2312").length);
			outMsg.getOutputStream().write(datetime.getBytes("gb2312"));
			outMsg.getOutputStream().writeInt(LatitudeE6);
			outMsg.getOutputStream().writeInt(LongitudeE6);
		}catch(IOException e){
			e.printStackTrace();
		}
		return outMsg;
	}
	public static OutputMessage createCAudioReq(String username,String friName,byte[] data,int sid,boolean isEOF)
	{
		OutputMessage outMsg = new OutputMessage(CommandId.C_AUDIO_REQ);
		try {
			//username
			outMsg.getOutputStream().writeInt(username.length());
			outMsg.getOutputStream().write(username.getBytes("gb2312"));
			//friname
			outMsg.getOutputStream().writeInt(friName.length());
			outMsg.getOutputStream().write(friName.getBytes("gb2312"));
			outMsg.getOutputStream().writeInt(sid);
			outMsg.getOutputStream().writeInt(isEOF==true?1:0);
			//data
			int size = data.length;
			outMsg.getOutputStream().writeInt(size);
		    outMsg.getOutputStream().write(data);
		
		} catch (IOException e) {}		
		return outMsg;
	}
	public static OutputMessage createCPTPAudioReq(String username,String friName)
	{
		OutputMessage outMsg = new OutputMessage(CommandId.C_PTP_AUDIO_REQ);
		try {
			//username
			int len = username.getBytes("gb2312").length;
			byte[] nm = username.getBytes("GB2312");
			outMsg.getOutputStream().writeInt(len);
			outMsg.getOutputStream().write(nm);
			//friname
			len = friName.getBytes("gb2312").length;
			nm = friName.getBytes("GB2312");
			outMsg.getOutputStream().writeInt(len);
			outMsg.getOutputStream().write(nm);
		} catch (IOException e) {}		
		return outMsg;
	}
	public static OutputMessage createCPTPVideoReq(String username,String friName,String ip,int port)
	{
		OutputMessage outMsg = new OutputMessage(CommandId.C_PTP_VIDEO_REQ);
		try {
			//username
			int len = username.getBytes("gb2312").length;
			byte[] nm = username.getBytes("GB2312");
			outMsg.getOutputStream().writeInt(len);
			outMsg.getOutputStream().write(nm);
			//friname
			len = friName.getBytes("gb2312").length;
			nm = friName.getBytes("GB2312");
			outMsg.getOutputStream().writeInt(len);
			outMsg.getOutputStream().write(nm);
			//ip
			len = ip.getBytes("gb2312").length;
			nm = ip.getBytes("GB2312");
			outMsg.getOutputStream().writeInt(len);
			outMsg.getOutputStream().write(nm);
			//port
			outMsg.getOutputStream().writeInt(port);
		} catch (IOException e) {}		
		return outMsg;
	}
	public static OutputMessage createCPTPAudioRsp(String username,String friName,boolean res)
	{
		OutputMessage outMsg = new OutputMessage(CommandId.C_PTP_AUDIO_RSP);
		try {
			//用户名
			outMsg.getOutputStream().writeInt((int)username.getBytes("gb2312").length);
			outMsg.getOutputStream().write(username.getBytes("GB2312"));
			//好友名
			outMsg.getOutputStream().writeInt((int)friName.getBytes("gb2312").length);
			outMsg.getOutputStream().write(friName.getBytes("GB2312"));
			//结果
			if(res==true)
			{
				outMsg.getOutputStream().writeInt(1);
			}
			else{
				outMsg.getOutputStream().writeInt(0);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return outMsg;
	}
	public static OutputMessage createCImageReq(String username,String friName,int sid,boolean isEOF){
		OutputMessage outMsg = new OutputMessage(CommandId.C_IMAGE_REQ);
		
		try {
			//用户名
			outMsg.getOutputStream().writeInt((int)username.getBytes("gb2312").length);
			outMsg.getOutputStream().write(username.getBytes("GB2312"));
			//好友名
			outMsg.getOutputStream().writeInt((int)friName.getBytes("gb2312").length);
			outMsg.getOutputStream().write(friName.getBytes("GB2312"));
			//sid
			outMsg.getOutputStream().writeInt(sid);
			//flag
			outMsg.getOutputStream().writeInt(isEOF==false?0:1);
			
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return outMsg;
	}
	public static OutputMessage createCPTPVideoRsp(String username,String friName,String ip,int port,boolean res)
	{
		OutputMessage outMsg = new OutputMessage(CommandId.C_PTP_VIDEO_RSP);
		try {
			//用户名
			outMsg.getOutputStream().writeInt((int)username.getBytes("gb2312").length);
			outMsg.getOutputStream().write(username.getBytes("GB2312"));
			//好友名
			outMsg.getOutputStream().writeInt((int)friName.getBytes("gb2312").length);
			outMsg.getOutputStream().write(friName.getBytes("GB2312"));
			//结果
			if(res==true)
			{
				if(DEBUG)Log.d(TAG, " video response is true and ip is "+ip+" and port is "+port);
				outMsg.getOutputStream().writeInt(1);
			}
			else{
				if(DEBUG)Log.d(TAG, " video response is false and ip is "+ip+" and port is "+port);
				outMsg.getOutputStream().writeInt(0);
			}			
			//ip
			outMsg.getOutputStream().writeInt(ip.getBytes("gb2312").length);
			outMsg.getOutputStream().write(ip.getBytes("GB2312"));
			//port
			outMsg.getOutputStream().writeInt(port);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    return outMsg;
	}
	public static OutputMessage createCSearchFriendReq(String username,String keyword){
		OutputMessage msg = new OutputMessage(CommandId.C_SEARCH_FRIEND_REQ);
		try {
			msg.getOutputStream().writeInt(username.length());
			msg.getOutputStream().write(username.getBytes("gb2312"));
			msg.getOutputStream().writeInt(keyword.length());
			msg.getOutputStream().write(keyword.getBytes("gb2312"));
		} catch (IOException e) {
			e.printStackTrace();
		}		
		return msg;
	}
	/**
	 * Create offline message request to the server 
	 * to get offline message
	 * @param username the receivers of the message fetched
	 * @return
	 */
	public static OutputMessage createCOfflineMsgReq(String username) {
		OutputMessage outMsg = new OutputMessage(CommandId.C_OFFLINE_MSG_REQ);
		try {
			outMsg.getOutputStream().writeInt(username.length());
			outMsg.getOutputStream().write(username.getBytes("GB2312"));
		}catch(IOException e){
			e.printStackTrace();
		}
		return outMsg;
	}

	public static OutputMessage createCUpdateUserInfoReq(String username, int gender, String email){
		OutputMessage outMsg = new OutputMessage(CommandId.C_UPDATE_USER_INFO_REQ);
		try {
			outMsg.getOutputStream().writeInt(username.length());
			outMsg.getOutputStream().write(username.getBytes("GB2312"));
			outMsg.getOutputStream().writeInt(gender);
			outMsg.getOutputStream().writeInt(email.length());
			outMsg.getOutputStream().write(email.getBytes("GB2312"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return outMsg;
	}
	
	public static OutputMessage createCUploadPortraitReq(String username) {
		OutputMessage outMsg = new OutputMessage(CommandId.C_UPLOAD_PORTRAIT_REQ);
		
		try {
			outMsg.getOutputStream().writeInt((int)username.getBytes("gb2312").length);
			outMsg.getOutputStream().write(username.getBytes("GB2312"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return outMsg;
	}
}
