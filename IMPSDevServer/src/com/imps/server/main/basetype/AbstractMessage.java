/*
 * Author: liwenhaosuper
 * Date: 2011/5/18
 * Description: 
 *      abstract message class define
 */

package com.imps.server.main.basetype;

public abstract class AbstractMessage {
	/** 包头**/
	protected byte[] tag;
	
	/**消息类型*/
	protected byte cmdtype;
	
	/**包体*/
	protected byte[] body;
	
	
	public AbstractMessage(byte[] tag,byte cmdtype, byte[] body) {
		this.tag = tag;
		this.cmdtype = cmdtype;
		this.body = body;
	}
	
	public byte getCmdType() {
		return cmdtype;
	}
	public byte[] getTag(){
		return tag;
	}
}
