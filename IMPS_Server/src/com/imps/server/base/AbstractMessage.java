/*
 * Author: liwenhaosuper
 * Date: 2011/5/18
 * Description: 
 *      abstract message class define
 */

package com.imps.server.base;

public abstract class AbstractMessage {
	/**消息类型*/
	protected byte cmdtype;
	
	/**包体*/
	protected byte[] body;
	
	
	public AbstractMessage(byte cmdtype, byte[] body) {
		this.cmdtype = cmdtype;
		this.body = body;
	}
	
	public byte getCmdType() {
		return cmdtype;
	}
}
