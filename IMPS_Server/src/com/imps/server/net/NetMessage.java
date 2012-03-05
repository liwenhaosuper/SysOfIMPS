package com.imps.server.net;


/**
 * <p>
 * 网络消息
 */
public interface NetMessage {
	/**错误消息*/
	public static final NetMessage ERROR_MSG = new NetMessage() {
		public byte[] getContent() {
			return null;
		}
	};
	
	
	/**
	 * <p>
	 * 获得消息内容
	 * </p>
	 * <br>
	 * @return
	 */
	public byte[] getContent();
}
