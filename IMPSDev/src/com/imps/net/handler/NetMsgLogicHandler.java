package com.imps.net.handler;

import java.util.ArrayList;
import java.util.List;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.imps.IMPSDev;
import com.imps.basetypes.Constant;
import com.imps.basetypes.User;
import com.imps.basetypes.UserStatus;
import com.imps.events.IConnEvent;
import com.imps.model.AudioMedia;
import com.imps.model.CommandId;
import com.imps.model.CommandType;
import com.imps.model.IMPSType;
import com.imps.model.ImageMedia;
import com.imps.model.TextMedia;
import com.imps.services.impl.ServiceManager;

public class NetMsgLogicHandler extends SimpleChannelUpstreamHandler{
	
	private static String TAG = NetMsgLogicHandler.class.getCanonicalName();
	private static boolean DEBUG = IMPSDev.isDEBUG();
	private int cmdType;

	public NetMsgLogicHandler(){
		thread = new LooperThread();
		thread.start();
	}
    @Override
    public void channelDisconnected(ChannelHandlerContext ctx, ChannelStateEvent e){
    	if(DEBUG) Log.d(TAG,"tcp disconnected to:"+e.getChannel().getRemoteAddress().toString());
    }
    @Override
    public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) {
    	if(DEBUG) Log.d(TAG,"tcp connected to:"+e.getChannel().getRemoteAddress().toString());
    }
    public Handler dispatcher;
    public LooperThread thread;
    public class LooperThread extends Thread{
    	@Override
    	public void run(){
    		Looper.prepare();
    		dispatcher = new Handler(){

    	    	@Override
    	    	public void handleMessage(Message msg){
    	    		IMPSType media = (IMPSType)msg.obj;
    	    		if(media==null){
    	    			return;
    	    		}
    	    		if(media.getType()==IMPSType.COMMAND){
    	    			String command = media.getmHeader().get("Command");
    	        		if(command==null){
    	        			return;
    	        		}
    	        		/** login success   */
    	        		if(command.equals(CommandId.S_LOGIN_RSP)){
    	        			if(DEBUG)Log.d(TAG,"login response received!");
    	        			String email = media.getmHeader().get("Email");
    	        			String userName = media.getmHeader().get("UserName");
    	        			String descrip = media.getmHeader().get("Message");
    	        			String gender = media.getmHeader().get("Gender");
    	        			User user = new User();
    	        			if(userName!=null){ user.setUsername(userName);}
    	        			if(email!=null){user.setEmail(email);}
    	        			if(descrip!=null){user.setDescription(descrip);}
    	        			if(gender!=null){user.setGender(gender.equals("M")?1:0);}
    	        			user.setPassword(UserManager.getGlobaluser().getPassword());
    	    				Intent intent = new Intent(Constant.LOGINSUCCESS);
    	    				if(DEBUG) Log.d(TAG,"Login success broadcast sent.");
    	    				IMPSDev.getContext().sendBroadcast(intent);
    	    				ServiceManager.getmAccount().onLoginSuccess();
    	        			
    	        		}
    	        		/** register response received. */
    	        		else if(command.equals(CommandId.S_REGISTER)){
    	        			Intent regsucc = new Intent(Constant.REGISTERSUCCESS);
    	    				if(DEBUG) Log.d(TAG,"Register success broadcast sent.");
    	    				IMPSDev.getContext().sendBroadcast(regsucc);
    	        		}
    	        		/** add friend request received. */
    	        		else if(command.equals(CommandId.S_ADDFRIEND_REQ)){
    	        			if(DEBUG)Log.d(TAG,"add friend request received!");
    	        			
    	        		}
    	        		/** add friend request fail received. */
    	        		else if(command.equals(CommandId.S_ADDFRIEND_REQ_FAIL)){
    	        			if(DEBUG) Log.d(TAG,"add fri req msg recv.");
    	        			String friendName = media.getmHeader().get("FriendName");
    	        			String message = media.getmHeader().get("Message");
    	        			//TODO: message should also be considered
    	        			ServiceManager.getmReceiver().onRecvFriendReq(friendName);
    	        			ServiceManager.getmSound().playCommonTone();
    	        			
    	        		}
    	        		/** Add friend response received.  */
    	        		else if(command.equals(CommandId.S_ADDFRIEND_RSP)){
    	        			String friendName = media.getmHeader().get("FriendName");
    	        			String message = media.getmHeader().get("Message");
    	        			String result = media.getmHeader().get("Result");
    	        			//TODO: message should also be considered
    	        			boolean rsprel = Integer.valueOf(result).intValue()==0?false:true;
    	        			ServiceManager.getmReceiver().onRecvFriendRsp(friendName, rsprel);
    	        			ServiceManager.getmSound().playCommonTone();
    	        		}else if(command.equals(CommandId.S_ADDFRIEND_RSP_FAIL)){
    	        			
    	        		}else if(command.equals(CommandId.S_ERROR)){
    	        			
    	        		}
    	        		/** friend list recv */
    	        		else if(command.equals(CommandId.S_FRIENDLIST_REFURBISH_RSP)){
    	        			if(DEBUG)Log.d(TAG,"friendlist refresh msg recv.");
    	        			String result = media.getmHeader().get("Result");
    	        			//TODO: need to be improved
    	        			String[] buffer = result.split("[{}]");
    	        			List<User> userList = new ArrayList<User>();
    	        			if(buffer!=null){
    	        				for(int i=0;i<buffer.length;i++){
    	        					if(buffer[i].equals("")||buffer[i].equals(",")){
    	        						continue;
    	        					}
    	        					User friend = new User();
    	        					String[] buf = buffer[i].split(",");
    	        					int inx = 0;
    	        					String key,value;
    	        					for(int j=0;j<buf.length;j++){
    	        						if(buf[j].equals("")){
    	        							continue;
    	        						}
    	        						inx = buf[j].indexOf(":",inx);
    	        						if(inx==-1)continue;
    	        						key = buf[j].substring(0, inx);
    	        						value = buf[j].substring(inx+1);
    	        						if(key.equals("UserName")){
    	        							friend.setUsername(value);
    	        						}else if(key.equals("Email")){
    	        							friend.setEmail(value);
    	        						}else if(key.equals("Status")){
    	        							friend.setStatus(value.equals("OFFLINE")?UserStatus.OFFLINE:UserStatus.ONLINE);
    	        						}else if(key.equals("Time")){
    	        							friend.setLoctime(value);
    	        						}else if(key.equals("Latitide")){
    	        							friend.setLocX(Double.valueOf(value));
    	        						}else if(key.equals("Longitude")){
    	        							friend.setLocY(Double.valueOf(value));
    	        						}else if(key.equals("Description")){
    	        							friend.setDescription(value);
    	        						}
    	        					}
    	        					userList.add(friend);
    	        				}
    	        			}
    	        			UserManager.AllFriList = userList;
    	        			ServiceManager.getmReceiver().recvFriendList();
    	        		}
    	        		/** heart beat  */
    	        		else if(command.equals(CommandId.S_HEARTBEAT_RSP)){
    	        			if(DEBUG)Log.d(TAG,"heart beat response received!");
    	        		}
    	        		/** login at other place */
    	        		else if(command.equals(CommandId.S_LOGIN_ERROR_OTHER_PLACE)){
    						if(DEBUG) Log.d(TAG,"Login error:account logins at other place.");
    	        		}
    	        		/** login fail: user name or password fail */
    	        		else if(command.equals(CommandId.S_LOGIN_ERROR_UNVALID)){
    						if(DEBUG) Log.d(TAG,"Login error:username or password not valid.");
    	        		}
    	        		/** login fail: unknown error */
    	        		else if(command.equals(CommandId.S_LOGIN_UNKNOWN)){
    	        			if(DEBUG) Log.d(TAG,"Unknown login error.");
    	        		}
    	        		/** reg error: other error */
    	        		else if(command.equals(CommandId.S_REG_ERROR_UNKNOWN)){
    	        			if(DEBUG) Log.d(TAG,"Reg error: unknown");
    	        		}
    	        		/**  reg error: user name exists */
    	        		else if(command.equals(CommandId.S_REG_ERROR_USER_EXIST)){
    	        			if(DEBUG) Log.d(TAG,"Reg error: username already exists");

    	        		}else if(command.equals(CommandId.S_SEARCH_FRIEND_RSP)){
    	        			
    	        		}else if(command.equals(CommandId.S_STATUS_NOTIFY)){
    	        			
    	        		}else if(command.equals(CommandId.S_UPDATE_USER_INFO_RSP)){
    	        			
    	        		}else if(command.equals(CommandId.S_UPLOAD_PORTRAIT_RSP)){
    	        			
    	        		}else{
    	        			//Unhandle command type
    	        		}
    	    		}else if(media.getType()==IMPSType.AUDIO||media.getType()==IMPSType.IMAGE||media.getType()==IMPSType.SMS){
    	    			String command = media.getmHeader().get("Command");
    	        		if(command==null){
    	        			return;
    	        		}
    	        		if(command.equals(CommandId.S_IMAGE_REQ)){
    	        			if(DEBUG){Log.d(TAG,"send image recv.");}
    	        			String friend = media.getmHeader().get("FriendName");
    	        			byte[] content = media.getContent(); 
    	        			ServiceManager.getmSound().playNewSms();
    	        		}else if(command.equals(CommandId.S_OFFLINE_MSG_RSP)){
    	        			
    	        		}
    	        		/** message received. */
    	        		else if(command.equals(CommandId.S_SEND_MSG)){
    	        			if(DEBUG){Log.d(TAG,"send msg recv.");}
    	        			String friend = media.getmHeader().get("FriendName");
    	        			String time = media.getmHeader().get("Time");
    	        			byte[] content = media.getContent(); 
    	        			ServiceManager.getmSound().playNewSms();
    	        		    //ServiceManager.getmReceiver().onSmsRecv(media);
    	        		}
    	        		/** audio message received */
    	        		else if(command.equals(CommandId.S_AUDIO_REQ)){
    	        			if(DEBUG){Log.d(TAG,"send audio recv.");}
    	        			String friend = media.getmHeader().get("FriendName");
    	        			byte[] content = media.getContent(); 
    	        			ServiceManager.getmSound().playNewSms();
    	        		}else {
    	        			//Unhandle command type
    	        			
    	        		}
    	    		}else{
    	    			if(DEBUG) Log.d(TAG,"unhandled data type:"+cmdType);
    	    			return;
    	    		}
    	    		
    	    	}
    	    
    		};
    		Looper.loop();
    	}
    }
	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) 
	{
		ChannelBuffer inMsg =(ChannelBuffer)e.getMessage();
		cmdType = inMsg.readInt();
		IMPSType media = null;
		switch(cmdType){
    	case com.imps.model.MediaType.SMS:
    		media = new TextMedia(false);
    		break;
    	case com.imps.model.MediaType.AUDIO:
    		media = new AudioMedia(false);
    		break;
    	case com.imps.model.MediaType.COMMAND:
    		media = new CommandType();
    		break;
    	case com.imps.model.MediaType.IMAGE:
    		media = new ImageMedia(false);
    		break;
		default:
			if(DEBUG) Log.d(TAG,"unhandled msg received:"+cmdType);
			inMsg.skipBytes(inMsg.readableBytes());
			e.getChannel().close();
			return;
    	}
		media.MediaParser(inMsg.array());
		Message messageSender = Message.obtain();
		messageSender.obj = media;
		dispatcher.sendMessage(messageSender);
	}
}
