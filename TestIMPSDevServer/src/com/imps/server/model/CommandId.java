
/*
 * author: liwenhaosuper
 * Date:2011/5/18
 */

/*
 * this is a utility class of command id used for specifying the command type.
 */


package com.imps.server.model;

public class CommandId {
	
	/**登录**/
	public static final String C_LOGIN_REQ = "C_LOGIN_REQ";
	public static final String S_LOGIN_RSP = "S_LOGIN_RSP";
	public static final String S_LOGIN_ERROR_UNVALID = "S_LOGIN_ERROR_UNVALID";
	public static final String S_LOGIN_ERROR_OTHER_PLACE = "S_LOGIN_ERROR_OTHER_PLACE";
	public static final String S_LOGIN_UNKNOWN = "S_LOGIN_UNKNOWN";
	/**注册**/
	public static final String S_REG_ERROR_USER_EXIST = "S_REG_ERROR_USER_EXIST";
	public static final String S_REG_ERROR_UNKNOWN = "S_REG_ERROR_UNKNOWN";
	public static final String S_REGISTER = "S_REGISTER";
	public static final String C_REGISTER = "C_REGISTER";
	/** 心跳检测 */
	public static final String C_HEARTBEAT_REQ = "C_HEARTBEAT_REQ";
	public static final String S_HEARTBEAT_RSP = "S_HEARTBEAT_RSP";
	/**好友列表**/
	public static final String C_FRIENDLIST_REFURBISH_REQ = "C_FRIENDLIST_REFURBISH_REQ";
	public static final String S_FRIENDLIST_REFURBISH_RSP = "S_FRIENDLIST_REFURBISH_RSP";
	/**添加好友*/
	public static final String C_ADDFRIEND_REQ = "C_ADDFRIEND_REQ";
	public static final String C_ADDFRIEND_RSP = "C_ADDFRIEND_RSP";
	public static final String S_ADDFRIEND_REQ = "S_ADDFRIEND_REQ";
	public static final String S_ADDFRIEND_RSP = "S_ADDFRIEND_RSP";
	/**发送消息*/
	public static final String C_SEND_MSG = "C_SEND_MSG";
	public static final String S_SEND_MSG = "S_SEND_MSG";
	/**
	 * 录音传送
	 */
	public static final String C_AUDIO_REQ = "C_AUDIO_REQ";
	public static final String S_AUDIO_REQ = "S_AUDIO_REQ";
	/**图片**/
	public static final String C_IMAGE_REQ = "C_IMAGE_REQ";
	public static final String S_IMAGE_REQ = "S_IMAGE_REQ";
	/**状态通知 */
	public static final String C_STATUS_NOTIFY = "C_STATUS_NOTIFY";
	public static final String S_STATUS_NOTIFY = "S_STATUS_NOTIFY";
	
	/**服务端错误*/
	public static final String S_ERROR = "S_ERROR";

	/**
	 * 视频，语音对话
	 */
	public static final String C_PTP_AUDIO_REQ  ="C_PTP_AUDIO_REQ";
	public static final String S_PTP_AUDIO_REQ = "S_PTP_AUDIO_REQ";
	public static final String C_PTP_VIDEO_REQ = "C_PTP_VIDEO_REQ";
	public static final String S_PTP_VIDEO_REQ = "S_PTP_VIDEO_REQ";
	
	public static final String C_PTP_AUDIO_RSP = "C_PTP_AUDIO_RSP";
	public static final String S_PTP_AUDIO_RSP = "S_PTP_AUDIO_RSP";
	public static final String C_PTP_VIDEO_RSP = "C_PTP_VIDEO_RSP";
	public static final String S_PTP_VIDEO_RSP = "S_PTP_VIDEO_RSP";
	
	/**
	 * 查找好友
	 */
	public static final String C_SEARCH_FRIEND_REQ = "C_SEARCH_FRIEND_REQ";
	public static final String S_SEARCH_FRIEND_RSP = "S_SEARCH_FRIEND_RSP";
	
    /**
     * 离线消息
     */
	public static final String C_OFFLINE_MSG_REQ = "C_OFFLINE_MSG_REQ";
	public static final String S_OFFLINE_MSG_RSP = "S_OFFLINE_MSG_RSP";
	
	/** 客户端用户更改信息  **/
	public static final String C_UPDATE_USER_INFO_REQ = "C_UPDATE_USER_INFO_REQ";
	public static final String S_UPDATE_USER_INFO_RSP = "S_UPDATE_USER_INFO_RSP";
	public static final String C_UPLOAD_PORTRAIT_REQ = "C_UPLOAD_PORTRAIT_REQ";
	public static final String S_UPLOAD_PORTRAIT_RSP = "S_UPLOAD_PORTRAIT_RSP";

	/**
	 * App Plugin  
	 */
	//Doodle
	public static final String C_DOODLE_REQ = "C_DOODLE_REQ";
	public static final String C_DOODLE_RSP = "C_DOODLE_RSP";
	public static final String S_DOODLE_REQ = "S_DOODLE_REQ";
	public static final String S_DOODLE_RSP = "S_DOODLE_RSP";
	public static final String DOODLE_DATA = "DOODLE_DATA";
	public static final String DOODLE_LOGIN = "DOODLE_LOGIN";
	public static final String DOODLE_ONLINEFRIENDLIST ="DOODLE_ONLINEFRIENDLIST";
	public static final String DOODLE_STATUSNOTIFY = "DOODLE_STATUSNOTIFY";
}
