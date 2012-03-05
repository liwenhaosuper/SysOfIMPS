/*
 * Author: liwenhaosuper
 * Date: 2011/5/18
 * Description: 
 *      abstract message class define
 */

package com.imps.base;

public abstract class AbstractMessage {
	/**��Ϣ����*/
	protected byte cmdtype;
	
	/**����*/
	protected byte[] body;
	
	
	public AbstractMessage(byte cmdtype, byte[] body) {
		this.cmdtype = cmdtype;
		this.body = body;
	}
	
	public byte getCmdType() {
		return cmdtype;
	}
}
