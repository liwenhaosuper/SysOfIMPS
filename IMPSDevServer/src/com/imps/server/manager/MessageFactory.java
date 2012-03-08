package com.imps.server.manager;



/*
 * Author: liwenhaosuper
 * Date: 2011/5/19
 * Description:
 *     message factory, define all message type
 */
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import com.imps.server.main.basetype.CommandId;
import com.imps.server.main.basetype.OutputMessage;


public class MessageFactory {
	//AU:audio data
	//IM:image data
	//FL:file data
	//OK:plain text
	public static String AM = "AM";
	public static String IM = "IM";
	public static String FL = "FL";
	public static String OK = "OK";

	public static OutputMessage createSLoginRsp() {
		OutputMessage outMsg = new OutputMessage(CommandId.S_LOGIN_RSP);
		return outMsg;
	}

	public static OutputMessage createSRegisterRsp() throws IOException
	{
		OutputMessage outMsg = new OutputMessage(CommandId.S_REGISTER);
		outMsg.getOutputStream().writeInt(1); 
		return outMsg;
	}
	

	public static OutputMessage createErrorMsg()
	{
		OutputMessage outMsg = new OutputMessage(CommandId.S_ERROR);
		return outMsg;
	}
	public static OutputMessage createSSmsSuccess(String fName,int sid) throws IOException{
		OutputMessage outMsg = new OutputMessage(CommandId.S_SEND_MSG_RSP);
		outMsg.getOutputStream().writeInt(fName.length());
		outMsg.getOutputStream().write(fName.getBytes("gb2312"));
		outMsg.getOutputStream().writeInt(sid);
		return outMsg;
	}
	

	public static OutputMessage createSHeartbeatRsp()
	{
		OutputMessage outMsg = new OutputMessage(CommandId.S_HEARTBEAT_RSP);
		return outMsg;
	}
	

	public static OutputMessage createOnlineStatusNotify(String username, byte status) {
		OutputMessage outMsg = new OutputMessage(CommandId.S_STATUS_NOTIFY);
		try {
			int len = username.getBytes("gb2312").length;
			byte[] nm = username.getBytes("GB2312");
			outMsg.getOutputStream().writeInt(len);
			outMsg.getOutputStream().write(nm);
			outMsg.getOutputStream().writeByte(status);
		} catch (IOException e) {}
		
		return outMsg;
	}

	public static OutputMessage createSSendMsg(String friendName,String msg,String datetime)
	{
		OutputMessage outMsg = new OutputMessage(CommandId.S_SEND_MSG);
		try {

			int len = friendName.getBytes("gb2312").length;
			byte[] fri = new byte[(int)len];
			fri = friendName.getBytes("gb2312");
			outMsg.getOutputStream().writeInt(len);
			outMsg.getOutputStream().write(fri);

			len = msg.getBytes("gb2312").length;
			byte[] msgcont = new byte[(int)len];
			msgcont = msg.getBytes("gb2312");
			outMsg.getOutputStream().writeInt(len);
			outMsg.getOutputStream().write(msgcont);
			len = datetime.getBytes("gb2312").length;
			byte[] tm = new byte[(int)len];
			tm = datetime.getBytes("gb2312");
			outMsg.getOutputStream().writeInt(len);
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

	public static OutputMessage createSAudioReq(String friendName,byte[] data,int sid,boolean isEOF)
	{
		OutputMessage outMsg = new OutputMessage(CommandId.S_AUDIO_REQ);
		try {

			int len = friendName.getBytes("gb2312").length;
			byte[] fri = new byte[len];
			fri = friendName.getBytes("gb2312");
			outMsg.getOutputStream().writeInt(len);
			outMsg.getOutputStream().write(fri);
			
			outMsg.getOutputStream().writeInt(sid);
			outMsg.getOutputStream().writeInt(isEOF==true?1:0);
			len = data.length;
			outMsg.getOutputStream().writeInt(len);
			outMsg.getOutputStream().write(data);
			
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
	

	public static OutputMessage createSAddFriReq(String friendName)
	{
		OutputMessage outMsg = new OutputMessage(CommandId.S_ADDFRIEND_REQ);

		try {
			int len = friendName.getBytes("gb2312").length;
			byte[] fri = new byte[(int)len];
			fri = friendName.getBytes("gb2312");
			outMsg.getOutputStream().writeInt(len);
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

	public static OutputMessage createSAddFriRsp(String friendName,int res)
	{
		OutputMessage outMsg = new OutputMessage(CommandId.S_ADDFRIEND_RSP);

		try {
			int len = friendName.getBytes("gb2312").length;
			byte[] fri = new byte[(int)len];
			fri = friendName.getBytes("gb2312");
			outMsg.getOutputStream().writeInt(len);
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

	public static OutputMessage createSPTPAudioReq(String username,String ip,int port)
	{
		OutputMessage outMsg = new OutputMessage(CommandId.S_PTP_AUDIO_REQ);
		try {
			//username
			int len = username.getBytes("gb2312").length;
			byte[] nm = username.getBytes("GB2312");
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

	public static OutputMessage createSPTPVideoReq(String username,String ip,int port,String pubIp,int pubPort)
	{
		OutputMessage outMsg = new OutputMessage(CommandId.S_PTP_VIDEO_REQ);
		try {
			//username
			int len = username.getBytes("gb2312").length;
			byte[] nm = username.getBytes("GB2312");
			outMsg.getOutputStream().writeInt(len);
			outMsg.getOutputStream().write(nm);
			//ip
			len = ip.getBytes("gb2312").length;
			nm = ip.getBytes("GB2312");
			outMsg.getOutputStream().writeInt(len);
			outMsg.getOutputStream().write(nm);
			//port
			outMsg.getOutputStream().writeInt(port);
			//public ip
			len = pubIp.getBytes("gb2312").length;
			nm = pubIp.getBytes("GB2312");
			outMsg.getOutputStream().writeInt(len);
			outMsg.getOutputStream().write(nm);
			//public port
			outMsg.getOutputStream().writeInt(pubPort);
		} catch (IOException e) {}		
		return outMsg;
	}
	
	

	public static OutputMessage createSPTPAudioRsp(String username,String ip,int port,boolean res)
	{
		OutputMessage outMsg = new OutputMessage(CommandId.S_PTP_AUDIO_RSP);
		try {
			//username
			int len = username.getBytes("gb2312").length;
			byte[] nm = username.getBytes("GB2312");
			outMsg.getOutputStream().writeInt(len);
			outMsg.getOutputStream().write(nm);
			//res
			outMsg.getOutputStream().writeInt(res?1:0);
			//ip
			outMsg.getOutputStream().writeInt(ip.getBytes("gb2312").length);
			outMsg.getOutputStream().write(ip.getBytes("gb2312"));
			//port
			outMsg.getOutputStream().writeInt(port);
		} catch (IOException e) {}		
		return outMsg;
	}
	

	public static OutputMessage createSPTPVideoRsp(String username,String ip,int port,boolean res,String pubIp,int pubPort)
	{
		OutputMessage outMsg = new OutputMessage(CommandId.S_PTP_VIDEO_RSP);
		try {
			//username
			int len = username.getBytes("gb2312").length;
			byte[] nm = username.getBytes("GB2312");
			outMsg.getOutputStream().writeInt(len);
			outMsg.getOutputStream().write(nm);
			//res
			outMsg.getOutputStream().writeInt(res?1:0);
			//ip
			outMsg.getOutputStream().writeInt(ip.getBytes("gb2312").length);
			outMsg.getOutputStream().write(ip.getBytes("gb2312"));
			//port
			outMsg.getOutputStream().writeInt(port);
			//public ip
			outMsg.getOutputStream().writeInt(pubIp.getBytes("gb2312").length);
			outMsg.getOutputStream().write(pubIp.getBytes("gb2312"));
			//public port
			outMsg.getOutputStream().writeInt(pubPort);
		} catch (IOException e) {}		
		return outMsg;
	}
	public static OutputMessage createSSearchFriendRsp(List <String> res){
		OutputMessage outMsg = new OutputMessage(CommandId.S_SEARCH_FRIEND_RSP);
		try {
			if(res==null){
				outMsg.getOutputStream().writeInt(0);
				return outMsg;
			}
			outMsg.getOutputStream().writeInt(res.size());
			for(int i=0;i<res.size();i++){
				outMsg.getOutputStream().writeInt(res.get(i).length());
				outMsg.getOutputStream().write(res.get(i).getBytes());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}	
		return outMsg;
	}
	public static OutputMessage createSSendImageReq(String friName,int sid,boolean isEOF){
		OutputMessage outMsg = new OutputMessage(CommandId.S_IMAGE_REQ);
		try {
			//username
			int len = friName.getBytes("gb2312").length;
			byte[] nm = friName.getBytes("GB2312");
			outMsg.getOutputStream().writeInt(len);
			outMsg.getOutputStream().write(nm);
			//sid
			outMsg.getOutputStream().writeInt(sid);
			//flag
			outMsg.getOutputStream().writeInt(isEOF==true?1:0);
		} catch (IOException e) {e.printStackTrace();}
		return outMsg;
	}
	
	public static OutputMessage createSUpdateUserInfoRsp() {
		OutputMessage outMsg = new OutputMessage(CommandId.S_UPDATE_USER_INFO_RSP);
		return outMsg;
	}
	
	public static OutputMessage createSUploadPortraitRsp() {
		OutputMessage outMsg = new OutputMessage(CommandId.S_UPLOAD_PORTRAIT_RSP);
		return outMsg;
	}
	
	public static OutputMessage createSSendPortraitReq(String username){
		OutputMessage outMsg = new OutputMessage(CommandId.S_SEND_PORTRAIT_REQ);
		try {
			//username
			int len = username.getBytes("gb2312").length;
			byte[] nm = username.getBytes("GB2312");
			outMsg.getOutputStream().writeInt(len);
			outMsg.getOutputStream().write(nm);
		} catch (IOException e) {e.printStackTrace();}
		return outMsg;
	}
}
