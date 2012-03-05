
/*
 * Author: liwenhaosuper
 * Date: 2011/5/19
 * Description:
 *     message factory, define all message type
 */


package com.imps.base;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.LinkedList;

import com.imps.util.CommonHelper;

import android.util.Log;


public class MessageFactory {
	
	/**
	 * �����������˵�¼��Ӧ
	 * @param type Э������
	 * @param status ״̬
	 * @return
	 */
	public static OutputMessage createSLoginRsp() {
		OutputMessage outMsg = new OutputMessage(CommandId.S_LOGIN_RSP);
		
		//��ʲôҲ����������
/*		try {
			//outMsg.getOutputStream().writeInt(validateCode);
		} catch (IOException e) {}
		*/
		return outMsg;
	}
	/**
	 * �����ͻ��˵�¼����
	 * 
	 */
	public static OutputMessage createCLoginReq()
	{
		OutputMessage outMsg = new OutputMessage(CommandId.C_LOGIN_REQ);
		return outMsg;
	}
	/**
	 * �����ͻ���ע������
	 */
	public static OutputMessage createCRegisterReq()
	{
		OutputMessage outMsg = new OutputMessage(CommandId.C_REGISTER);
		return outMsg;
	}
	/**
	 * �����ͻ��˷�����Ϣ����
	 */
	public static OutputMessage createCSendMsgReq()
	{
		OutputMessage outMsg = new OutputMessage(CommandId.C_SEND_MSG);
		return outMsg;
	}
	/**
	 * �����ͻ��˺����б�����
	 */
	public static OutputMessage createCFriendListReq(String userName)
	{
		OutputMessage outMsg = new OutputMessage(CommandId.C_FRIENDLIST_REFURBISH_REQ);
		
	    try {
	    	long len = userName.getBytes("gb2312").length;
			outMsg.getOutputStream().writeLong(len);
			outMsg.getOutputStream().write(userName.getBytes("GB2312"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	   
		return outMsg;
	}
	
	/**
	 * �����ͻ�����Ӻ�������
	 */
	public static OutputMessage createCAddFriReq(String userName,String friName)
	{
		OutputMessage outMsg = new OutputMessage(CommandId.C_ADDFRIEND_REQ);
		
		
		try {//�û���
			long len = userName.getBytes("gb2312").length;
			outMsg.getOutputStream().writeLong(len);
			outMsg.getOutputStream().write(userName.getBytes("GB2312"));
			//������
			outMsg.getOutputStream().writeLong((long)friName.getBytes("gb2312").length);
			outMsg.getOutputStream().write(friName.getBytes("GB2312"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return outMsg;
	}
	/**
	 * �����ͻ�����Ӻ�����Ӧ
	 */
	public static OutputMessage createCAddFriRsq(String username,String friName,boolean res)
	{
		OutputMessage outMsg = new OutputMessage(CommandId.C_ADDFRIEND_RSP);
		try {
			//�û���
			outMsg.getOutputStream().writeLong((long)username.getBytes("gb2312").length);
			outMsg.getOutputStream().write(username.getBytes("GB2312"));
			//������
			outMsg.getOutputStream().writeLong((long)friName.getBytes("gb2312").length);
			outMsg.getOutputStream().write(friName.getBytes("GB2312"));
			//���
			outMsg.getOutputStream().writeInt(res?1:0);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return outMsg;
	}
	/**
	 * �����ͻ���״̬ˢ������
	 */
	public static OutputMessage createCStatusNotify(String username,byte status)
	{
		OutputMessage outMsg = new OutputMessage(CommandId.C_STATUS_NOTIFY);
		try {
			outMsg.getOutputStream().writeLong(username.getBytes("gb2312").length);
			outMsg.getOutputStream().write(username.getBytes("GB2312"));
			outMsg.getOutputStream().writeChar(status);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return outMsg;
	}
	
	/**
	 * ���������ע����Ӧ
	 * @return
	 * @throws IOException 
	 */
	public static OutputMessage createSRegisterRsp() throws IOException
	{
		OutputMessage outMsg = new OutputMessage(CommandId.S_REGISTER);
		outMsg.getOutputStream().writeInt(1);  //ע��ɹ�
		return outMsg;
	}
	
	/**
	 * ��������˴�����Ӧ
	 */
	public static OutputMessage createErrorMsg()
	{
		OutputMessage outMsg = new OutputMessage(CommandId.S_ERROR);
		return outMsg;
	}
	
	/**
	 * ���������������Ӧ
	 */
	public static OutputMessage createSHeartbeatRsp()
	{
		OutputMessage outMsg = new OutputMessage(CommandId.S_HEARTBEAT_RSP);
		return outMsg;
	}
	
	public static OutputMessage createCHeartbeatReq()
	{
		OutputMessage outMsg = new OutputMessage(CommandId.C_HEARTBEAT_REQ);
		return outMsg;
	}
	
	/**
	 * ��������״̬֪ͨ
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
	 * ����¼������
	 */
	public static OutputMessage createCAudioReq(String username,String friName,byte[] data)
	{
		OutputMessage outMsg = new OutputMessage(CommandId.C_AUDIO_REQ);
		try {
			//username
			long len = username.getBytes("gb2312").length;
			byte[] nm = username.getBytes("GB2312");
			outMsg.getOutputStream().writeLong(len);
			outMsg.getOutputStream().write(nm);
			//friname
			len = friName.getBytes("gb2312").length;
			nm = friName.getBytes("GB2312");
			outMsg.getOutputStream().writeLong(len);
			outMsg.getOutputStream().write(nm);
			//data
			long size = data.length;
			Log.d("MessageFactory", "data size is "+size);
			if(size==0)
				return null;
			outMsg.getOutputStream().writeLong(size);
		    outMsg.getOutputStream().write(data);
		
		} catch (IOException e) {}		
		return outMsg;
	}
	
	/**
	 * ������Ƶ��������
	 */
	public static OutputMessage createCPTPAudioReq(String username,String friName,String ip,int port)
	{
		OutputMessage outMsg = new OutputMessage(CommandId.C_PTP_AUDIO_REQ);
		try {
			//username
			long len = username.getBytes("gb2312").length;
			byte[] nm = username.getBytes("GB2312");
			outMsg.getOutputStream().writeLong(len);
			outMsg.getOutputStream().write(nm);
			//friname
			len = friName.getBytes("gb2312").length;
			nm = friName.getBytes("GB2312");
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
	 * ������Ƶ��������
	 */
	public static OutputMessage createCPTPVideoReq(String username,String friName,String ip,int port)
	{
		OutputMessage outMsg = new OutputMessage(CommandId.C_PTP_VIDEO_REQ);
		try {
			//username
			long len = username.getBytes("gb2312").length;
			byte[] nm = username.getBytes("GB2312");
			outMsg.getOutputStream().writeLong(len);
			outMsg.getOutputStream().write(nm);
			//friname
			len = friName.getBytes("gb2312").length;
			nm = friName.getBytes("GB2312");
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
	 * ������Ƶ������Ӧ
	 */
	public static OutputMessage createCPTPAudioRsp(String username,String friName,String ip,int port,boolean res)
	{
		OutputMessage outMsg = new OutputMessage(CommandId.C_PTP_AUDIO_RSP);
		try {
			//�û���
			outMsg.getOutputStream().writeLong((long)username.getBytes("gb2312").length);
			outMsg.getOutputStream().write(username.getBytes("GB2312"));
			//������
			outMsg.getOutputStream().writeLong((long)friName.getBytes("gb2312").length);
			outMsg.getOutputStream().write(friName.getBytes("GB2312"));
			//���
			if(res==true)
			{
				Log.d("MessageFactory", " audio response is true and ip is "+ip+" and port is "+port);
				outMsg.getOutputStream().writeInt(1);
			}
			else{
				Log.d("MessageFactory", " audio response is false and ip is "+ip+" and port is "+port);
				outMsg.getOutputStream().writeInt(0);
			}
			
			//ip
			outMsg.getOutputStream().writeLong(ip.getBytes("gb2312").length);
			outMsg.getOutputStream().write(ip.getBytes("GB2312"));
			//port
			outMsg.getOutputStream().writeInt(port);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return outMsg;
	}
	/*
	 * ����ͼƬ��������,payload <=300
	 * Content:
	 */
	
	public static OutputMessage createCImageReq(String username,String friName,byte[] data){
		OutputMessage outMsg = new OutputMessage(CommandId.C_IMAGE_REQ);
		
		try {
			//�û���
			outMsg.getOutputStream().writeLong((long)username.getBytes("gb2312").length);
			outMsg.getOutputStream().write(username.getBytes("GB2312"));
			//������
			outMsg.getOutputStream().writeLong((long)friName.getBytes("gb2312").length);
			outMsg.getOutputStream().write(friName.getBytes("GB2312"));
			
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return outMsg;
	}
	/**
	 * ������Ƶ������Ӧ
	 */
	public static OutputMessage createCPTPVideoRsp(String username,String friName,String ip,int port,boolean res)
	{
		OutputMessage outMsg = new OutputMessage(CommandId.C_PTP_VIDEO_RSP);
		try {
			//�û���
			outMsg.getOutputStream().writeLong((long)username.getBytes("gb2312").length);
			outMsg.getOutputStream().write(username.getBytes("GB2312"));
			//������
			outMsg.getOutputStream().writeLong((long)friName.getBytes("gb2312").length);
			outMsg.getOutputStream().write(friName.getBytes("GB2312"));
			//���
			if(res==true)
			{
				Log.d("MessageFactory", " video response is true and ip is "+ip+" and port is "+port);
				outMsg.getOutputStream().writeInt(1);
			}
			else{
				Log.d("MessageFactory", " video response is false and ip is "+ip+" and port is "+port);
				outMsg.getOutputStream().writeInt(0);
			}			
			//ip
			outMsg.getOutputStream().writeLong(ip.getBytes("gb2312").length);
			outMsg.getOutputStream().write(ip.getBytes("GB2312"));
			//port
			outMsg.getOutputStream().writeInt(port);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    return outMsg;
	}
}
	
/*	
	
*//**
	 * <p>
	 * ������������ˢ����Ӧ
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
	 * ������֤����
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
	 * ����������Ӧ
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
	 * ������Ӻ�����Ӧ
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
			status = 1;      //���Ѳ�����
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
	 * ������Ϣ����
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
	 * ����CMWAP����Ϣ
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
