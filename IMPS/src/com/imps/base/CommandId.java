
/*
 * author: liwenhaosuper
 * Date:2011/5/18
 */

/*
 * this is a utility class of command id used for specifying the command type.
 */


package com.imps.base;

public class CommandId {
	
	/**�ͻ��˵�¼����**/
	public static final byte C_LOGIN_REQ = 1;
	/**��������¼��Ӧ**/
	public static final byte S_LOGIN_RSP = 2;
	
	
	/** �������ͻ�������,ͬʱ���͵���λ����Ϣ */
	public static final byte C_HEARTBEAT_REQ = 3;
	/**������������Ӧ*/
	public static final byte S_HEARTBEAT_RSP = 4;
	
	
	/**�����б�ˢ������*/
	public static final byte C_FRIENDLIST_REFURBISH_REQ = 5;
	/**�����б�ˢ����Ӧ*/
	public static final byte S_FRIENDLIST_REFURBISH_RSP = 6;
	
	
	/**�ͻ�����Ӻ�������*/
	public static final byte C_ADDFRIEND_REQ = 7;
	/** �ͻ�����Ӻ�����Ӧ */
	public static final byte C_ADDFRIEND_RSP = 8;
	
	/**�������Ӻ�������*/
	public static final byte S_ADDFRIEND_REQ = 9;
	/**��������Ӻ�����Ӧ*/
	public static final byte S_ADDFRIEND_RSP = 10;
	

	/**�ͻ��˷�����Ϣ*/
	public static final byte C_SEND_MSG = 11;
	/**������������Ϣ*/
	public static final byte S_SEND_MSG = 12;
	  
	/**�ͻ���״̬֪ͨ */
	public static final byte C_STATUS_NOTIFY = 13;
	/**�����״̬��Ӧ */
	public static final byte S_STATUS_NOTIFY = 14;
	
	
	/** �ͻ����û�ע��*/
	public static final byte C_REGISTER = 15;
	/**������û�ע����Ӧ */
	public static final byte S_REGISTER = 16;

	/**����˴�����Ӧ*/
	public static final byte S_ERROR = 17;
	/**
	 * ¼������
	 */
	public static final byte C_AUDIO_REQ = 18;
	public static final byte S_AUDIO_RSP = 19;
	/**
	 * ��Ƶ�������Ի�
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
	 * ͼƬ
	 */
	public static final byte C_IMAGE_REQ = 28;
	public static final byte S_IMAGE_RSP = 29;
	
}
