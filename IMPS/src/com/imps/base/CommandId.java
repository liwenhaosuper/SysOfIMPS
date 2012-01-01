
/*
 * author: liwenhaosuper
 * Date:2011/5/18
 */

/*
 * this is a utility class of command id used for specifying the command type.
 */


package com.imps.base;

public class CommandId {
	
	/**客户端登录请求**/
	public static final byte C_LOGIN_REQ = 1;
	/**服务器登录响应**/
	public static final byte S_LOGIN_RSP = 2;
	
	
	/** 心跳检测客户端请求,同时发送地理位置信息 */
	public static final byte C_HEARTBEAT_REQ = 3;
	/**服务器心跳响应*/
	public static final byte S_HEARTBEAT_RSP = 4;
	
	
	/**好友列表刷新请求*/
	public static final byte C_FRIENDLIST_REFURBISH_REQ = 5;
	/**好友列表刷新响应*/
	public static final byte S_FRIENDLIST_REFURBISH_RSP = 6;
	
	
	/**客户端添加好友请求*/
	public static final byte C_ADDFRIEND_REQ = 7;
	/** 客户端添加好友响应 */
	public static final byte C_ADDFRIEND_RSP = 8;
	
	/**服务端添加好友请求*/
	public static final byte S_ADDFRIEND_REQ = 9;
	/**服务器添加好友响应*/
	public static final byte S_ADDFRIEND_RSP = 10;
	

	/**客户端发送消息*/
	public static final byte C_SEND_MSG = 11;
	/**服务器发送消息*/
	public static final byte S_SEND_MSG = 12;
	  
	/**客户端状态通知 */
	public static final byte C_STATUS_NOTIFY = 13;
	/**服务端状态响应 */
	public static final byte S_STATUS_NOTIFY = 14;
	
	
	/** 客户端用户注册*/
	public static final byte C_REGISTER = 15;
	/**服务端用户注册响应 */
	public static final byte S_REGISTER = 16;

	/**服务端错误响应*/
	public static final byte S_ERROR = 17;
	/**
	 * 录音传送
	 */
	public static final byte C_AUDIO_REQ = 18;
	public static final byte S_AUDIO_RSP = 19;
	/**
	 * 视频，语音对话
	 */
	public static final byte C_PTP_AUDIO_REQ  =20;
	public static final byte S_PTP_AUDIO_REQ = 21;
	public static final byte C_PTP_VIDEO_REQ = 22;
	public static final byte S_PTP_VIDEO_REQ = 23;
	
	public static final byte C_PTP_AUDIO_RSP = 24;
	public static final byte S_PTP_AUDIO_RSP = 25;
	public static final byte C_PTP_VIDEO_RSP = 26;
	public static final byte S_PTP_VIDEO_RSP = 27;
	
	/*
	 * 图片
	 */
	public static final byte C_IMAGE_REQ = 28;
	public static final byte S_IMAGE_RSP = 29;
	
}
