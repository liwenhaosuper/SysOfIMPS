package com.imps.services.impl;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.replay.ReplayingDecoder;
import org.jboss.netty.handler.codec.replay.VoidEnum;

public class DoodleUnificationHandler extends ReplayingDecoder<VoidEnum>{

	/**
	 * DD+length+type+userName+data
	 */
	private byte cmdType;
	private byte[] tag = new byte[2];
	@Override
	protected Object decode(ChannelHandlerContext ctx, Channel channel,
			ChannelBuffer buffer, VoidEnum state) throws Exception {
		buffer.readBytes(tag);
		if(tag[0]=='D'&&tag[1]=='D'){
			return buffer.readBytes(buffer.readInt());
		}else{
			throw new Error("Unknow tag recv of Doodle:"+tag);
		}
	}
}
