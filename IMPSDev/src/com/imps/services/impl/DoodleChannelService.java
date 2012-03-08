package com.imps.services.impl;

import java.io.UnsupportedEncodingException;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.imps.IMPSDev;
import com.imps.activities.CoupleDoodle;
import com.imps.basetypes.CommandId;
import com.imps.basetypes.Constant;
import com.imps.basetypes.DoodleAction;
import com.imps.net.handler.UserManager;
import com.imps.ui.DoodleView;

public class DoodleChannelService extends SimpleChannelUpstreamHandler{

	private static boolean DEBUG =IMPSDev.isDEBUG();
	private static String TAG = DoodleChannelService.class.getCanonicalName();
	private byte cmdType;
	private byte[] tag = new byte[2];
	private DoodleView mDoodleView;
	private static final int  DOODLEREQ = 1;
	private static final int DOODLERSP = 2;
	private static final int DOODLELIST = 3;
	private static final int DOODLESTATUS = 4;
	private Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg){
			switch(msg.what){
			case DOODLEREQ:
				String frireq = (String)msg.obj;
				Intent rintent = new Intent(Constant.DOODLEREQ);
				rintent.putExtra(Constant.USERNAME, frireq);
				IMPSDev.getContext().sendBroadcast(rintent);
				break;
			case DOODLERSP:
				String frirsp = (String)msg.obj;
				boolean res = msg.arg1==1?true:false;
				Intent intent = new Intent(Constant.DOODLERSP);
				intent.putExtra(Constant.USERNAME, frirsp);
				intent.putExtra(Constant.RESULT, res);
				IMPSDev.getContext().sendBroadcast(intent);
				break;
			case DOODLELIST:
				String[] frilist = (String[])msg.obj;
				//TODO: TO be continued...
				Intent listintent = new Intent(Constant.DOODLELIST);
				CoupleDoodle.onlineFriends.clear();
				for(int i=0;i<frilist.length;i++){
					CoupleDoodle.onlineFriends.add(frilist[i]);
				}
				IMPSDev.getContext().sendBroadcast(listintent);
				break;
			case DOODLESTATUS:
				String frinm = (String)msg.obj;
				boolean re = msg.arg1==1?true:false;
				//TODO: To be continued...
				Intent statusintent = new Intent(Constant.DOODLESTATUS);
				statusintent.putExtra(Constant.USERNAME, frinm);
				statusintent.putExtra(Constant.RESULT, re);
				IMPSDev.getContext().sendBroadcast(statusintent);
				break;
			}
		}
	};
	@Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e){
		ChannelBuffer buffer =(ChannelBuffer)e.getMessage();
    	cmdType = buffer.readByte();
    	switch(cmdType){
    	case CommandId.S_DOODLE_REQ:
    		if(DEBUG) Log.d(TAG,"S_DOODLE_REQ");
    		String fri = parseUserName(buffer);
    		Message reqmsg = new Message();
    		reqmsg.what = DOODLEREQ;
    		reqmsg.obj = fri;
    		handler.sendMessage(reqmsg);
    		break;
    	case CommandId.S_DOODLE_RSP:
    		if(DEBUG) Log.d(TAG,"S_DOODLE_RSP");
    		String frinm = parseUserName(buffer);
    		boolean res = buffer.readInt()==0?false:true;
    		Message rspmsg = new Message();
    		rspmsg.what = DOODLERSP;
    		rspmsg.obj = frinm;
    		rspmsg.arg1 = res==true?1:0;
    		handler.sendMessage(rspmsg);
    		break;
    	case CommandId.DOODLE_DATA:
    		if(DEBUG) Log.d(TAG,"S_DOODLE_DATA");
    		int len = buffer.readInt();
    		byte []nm = new byte[len];
    		buffer.readBytes(nm);
    		try {
    			String userName = new String(nm,"gb2312");
    			if(userName.equals(UserManager.getGlobaluser().getUsername())){
    				return;
    			}
    		} catch (UnsupportedEncodingException e10) {
    			e10.printStackTrace();
    			return;
    		}
    	    int action = buffer.readInt();
    	    float x = buffer.readFloat();
    	    float y = buffer.readFloat();
    	    if(mDoodleView==null){
    	    	if(DEBUG) Log.d(TAG,"Doodle View is null");
    	    	return;
    	    }
    	    if(action==DoodleAction.ACTION_DOWN){
    	    	mDoodleView.touch_start_fri(x, y);
    	    }else if(action==DoodleAction.ACTION_MOVE){
    	    	mDoodleView.touch_move_fri(x, y);
    	    }else if(action==DoodleAction.ACTION_UP){
    	    	mDoodleView.touch_up_fri();
    	    }
    		break;
    	case CommandId.DOODLE_ONLINEFRIENDLIST:
    		String []doodlelist = parseUserList(buffer);
    		Message msg = new Message();
    		msg.what = DOODLELIST;
    		msg.obj = doodlelist;
    		handler.sendMessage(msg);
    		break;
    	case CommandId.DOODLE_STATUSNOTIFY:
    		String name = parseUserName(buffer);
    		boolean online = buffer.readInt()==0?false:true;
    		Message notifymsg = new Message();
    		notifymsg.what = DOODLESTATUS;
    		notifymsg.obj = name;
    		notifymsg.arg1 = online?1:0;
    		handler.sendMessage(notifymsg);
    		break;
    	default:
    		if(DEBUG) Log.d(TAG,"Unknown msg type of doodle...");
    		break;
    	}
	}
	public void setmDoodleView(DoodleView mDoodleView) {
		this.mDoodleView = mDoodleView;
	}
	public DoodleView getmDoodleView() {
		return mDoodleView;
	}
	
	private String parseUserName(ChannelBuffer buffer){
		int len = buffer.readInt();
		byte []nm = new byte[len];
		buffer.readBytes(nm);
		String res = "";
		try {
			res = new String(nm,"gb2312");
		} catch (UnsupportedEncodingException e) {
			if(DEBUG)e.printStackTrace();
		}
		return res;
	}
	private String[] parseUserList(ChannelBuffer buffer){
		int len = buffer.readInt();
		String []res = new String[len];
		for(int i=0;i<len;i++){
			int sz = buffer.readInt();
			byte []nm = new byte[sz];
			buffer.readBytes(nm);
			try {
				res[i] = new String(nm,"gb2312");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		return res;
	}
	
}
