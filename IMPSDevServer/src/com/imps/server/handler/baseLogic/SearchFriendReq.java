package com.imps.server.handler.baseLogic;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;

import com.imps.server.main.basetype.MessageProcessTask;
import com.imps.server.model.CommandId;
import com.imps.server.model.CommandType;
import com.imps.server.model.IMPSType;

/**
 * Process Search Friend Request
 * request header should includes: UserName,Keyword
 * response header should be: Command,Result. Result set is divided by ::
 * @author liwenhaosuper
 *
 */ 
public class SearchFriendReq extends MessageProcessTask{

	public SearchFriendReq(Channel session, IMPSType message)
	{
		super(session, message);
	}
	@Override
	public void execute() {
		String userName = inMsg.getmHeader().get("UserName");
		String keyword = inMsg.getmHeader().get("Keyword");
		if(keyword==null||userName==null){
			if(DEBUG) System.out.println("SearchFriendReq:illegal request");
			return;
		}
		List <String> res =null;
		try {
			res =  manager.getSearchFriendResult(keyword);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		String lists = "";
		for(int i=0;res!=null&&i<res.size();i++){
			String item = res.get(i);
			if(item.equals(userName)){
				continue;
			}
			lists+=item;
			lists+="::";
		}
		IMPSType result = new CommandType();
		HashMap<String,String> header = new HashMap<String,String>();
		header.put("Command", CommandId.S_SEARCH_FRIEND_RSP);
		header.put("Result",lists);
		result.setmHeader(header);
		session.write(ChannelBuffers.wrappedBuffer(result.MediaWrapper()));
		updateList(userName,true);
		if(DEBUG) System.out.println("search result back to:"+userName);
	}

}
