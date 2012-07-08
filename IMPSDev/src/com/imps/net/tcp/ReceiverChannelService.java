package com.imps.net.tcp;

import java.text.ParseException;

import android.content.Intent;
import android.database.SQLException;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.imps.IMPSDev;
import com.imps.R;
import com.imps.basetypes.Constant;
import com.imps.basetypes.SystemMsgType;
import com.imps.basetypes.User;
import com.imps.model.CommandId;
import com.imps.model.CommandType;
import com.imps.model.IMPSType;
import com.imps.model.MediaType;
import com.imps.net.handler.UserManager;
import com.imps.services.impl.ServiceManager;
import com.imps.util.LocalDBHelper;

public class ReceiverChannelService {
	private static ReceiverChannelService instance;
	public static final int LOGINSUCCESS = 1;
	public static final int LOGINFAILED = 2;
	
	public static final int REGISTERSUCCESS = 3;
	public static final int REGISTERFAIL = 4;
	
	public static final int FRIENDLISTREFRESH = 5;
	public static final int FRIENDSTATUSNOTIFY = 6;
	
	public static final int SMSRECV = 7;
	public static final int SMSRSP = 8;
	
	public static final int MEDIARECV = 100;
	
	public static final int IMAGERECV = 9;
	public static final int IMAGERECVD = 10;  //done
	public static final int IMAGERSP = 11;
	
	public static final int AUDIORECV = 12;
	public static final int AUDIORECVD = 13;
	public static final int AUDIORSP = 14;
	
	public static final int ADDFRIENDREQ = 15;
	public static final int ADDFRIENDRSP  =16;
	
	public static final int STATUSNOTIFY = 17;
	
	public static final int P2PAUDIOREQ = 18;
	public static final int P2PAUDIORSP = 19;
	public static final int P2PVIDEOREQ = 20;
	public static final int P2PVIDEORSP = 21;
	
	public static final int SEARCHFRIENDRESULT = 22;
	
	public boolean DEBUG = true;
	public String TAG ="ReceiverChannelService";
	protected ReceiverChannelService(){
		
	}
	public static ReceiverChannelService getInstance(){
		if(instance==null){
			instance = new ReceiverChannelService();
		}
		return instance;
	}
	//broadcast and dispatches message
	private Handler dispatcher = new Handler(){
		@Override
		public void handleMessage(Message msg){
			switch(msg.what){
			case LOGINSUCCESS:
				Intent intent = new Intent(Constant.LOGINSUCCESS);
				if(DEBUG) Log.d(TAG,"Login success broadcast sent.");
				IMPSDev.getContext().sendBroadcast(intent);
				ServiceManager.getmAccount().onLoginSuccess();
				break;
			case LOGINFAILED:
				Intent loginfail = new Intent(Constant.LOGINERROR);
				loginfail.putExtra(Constant.LOGINERRORMSG, (String)msg.obj);
				if(DEBUG) Log.d(TAG,"Login failed broadcast sent.");
				IMPSDev.getContext().sendBroadcast(loginfail);
				ServiceManager.getmAccount().onLoginError();
				break;	
			case REGISTERSUCCESS:
				Intent regsucc = new Intent(Constant.REGISTERSUCCESS);
				if(DEBUG) Log.d(TAG,"Register success broadcast sent.");
				IMPSDev.getContext().sendBroadcast(regsucc);
				break;
			case REGISTERFAIL:
				Intent regfail = new Intent(Constant.REGISTERERROR);
				regfail.putExtra(Constant.REGISTERERRORMSG, (String)msg.obj);
				if(DEBUG) Log.d(TAG,"Register failed broadcast sent.");
				IMPSDev.getContext().sendBroadcast(regfail);
				break;	
			case FRIENDLISTREFRESH:
				if(DEBUG) Log.d(TAG,"Friend list broadcast sent.");
				Intent refreshlist = new Intent(Constant.FRIENDLISTREFRESH);
				IMPSDev.getContext().sendBroadcast(refreshlist);
				break;
			case FRIENDSTATUSNOTIFY:
				if(DEBUG) Log.d(TAG,"Friend status notify broadcast sent.");
				Intent statusnotify = new Intent(Constant.FRIENDLISTREFRESH);
				statusnotify.putExtra(Constant.USERNAME, ((User)msg.obj).getUsername());
				statusnotify.putExtra(Constant.STATUS, ((User)msg.obj).getStatus());
				IMPSDev.getContext().sendBroadcast(statusnotify);
				break;
			case MEDIARECV:
				if(DEBUG) Log.d(TAG,"media recv broadcast sent.");
				Intent data = new Intent(Constant.SMS);
				data.putExtra(Constant.USERNAME, ((MediaType)msg.obj).getSender());
				Log.d(TAG, ((MediaType)msg.obj).getSender());
				data.putExtra(Constant.TIME, ((MediaType)msg.obj).getTime());
				IMPSDev.getContext().sendBroadcast(data);
				break;
			case ADDFRIENDREQ:
				if(DEBUG) Log.d(TAG,"fri req sent...");
				Intent addreq = new Intent(Constant.ADDFRIENDREQ);
				SystemMsgType sysmsg = ((SystemMsgType)msg.obj);
				addreq.putExtra(Constant.USERNAME, sysmsg.name);
				IMPSDev.getContext().sendBroadcast(addreq);
				break;
			case ADDFRIENDRSP:
				if(DEBUG) Log.d(TAG,"fri rsp sent...");
				Intent addrsp = new Intent(Constant.ADDFRIENDRSP);
				SystemMsgType sysmsgrsp = ((SystemMsgType)msg.obj);
				addrsp.putExtra(Constant.USERNAME, sysmsgrsp.name);
				IMPSDev.getContext().sendBroadcast(addrsp);
				break;
			case STATUSNOTIFY:
				if(DEBUG) Log.d(TAG,"fri status notify recv...");
				Intent statusn = new Intent(Constant.STATUSNOTIFY);
				statusn.putExtra(Constant.USERNAME, ((User)msg.obj).getUsername());
				IMPSDev.getContext().sendBroadcast(statusn);
				break;
			case P2PAUDIOREQ:
				if(DEBUG) Log.d(TAG,"P2P audio req broadcast.");
				break;
			case P2PAUDIORSP:
				if(DEBUG) Log.d(TAG,"P2P audio rsp broadcast.");
				
				break;
			case P2PVIDEOREQ:
				if(DEBUG) Log.d(TAG,"P2P video req broadcast.");
				
				break;
			case P2PVIDEORSP:
				if(DEBUG) Log.d(TAG,"P2P video rsp broadcast.");
				
				break;
			case SEARCHFRIENDRESULT:
				if(DEBUG)Log.d(TAG,"Search fri broadcast");
				Intent searchrsp =new Intent(Constant.SEARCHFRIENDRSP);
				IMPSDev.getContext().sendBroadcast(searchrsp);
				break;
			default:
				break;
			}
		}
	};
	public void onMediaRecv(IMPSType media){
		if(media.getType()==IMPSType.COMMAND){
			String command = ((CommandType)media).getmHeader().get("Command");
			if(command.equals(CommandId.S_LOGIN_RSP)){
				UserManager.globaluser.setEmail(media.getmHeader().get("Email"));
				UserManager.globaluser.setDescription(media.getmHeader().get("Description"));
				UserManager.globaluser.setGender(media.getmHeader().get("Gender")=="M"?1:0);
				//send broadcast
				Message msg = new Message();
				msg.what = LOGINSUCCESS;
				dispatcher.sendMessage(msg);
			}else if(command.equals(CommandId.S_LOGIN_ERROR_UNVALID)){
				//send broadcast
				Message msg = new Message();
				msg.what = LOGINFAILED;
				msg.obj = IMPSDev.getContext().getResources().getString(R.string.login_username_password_error);
				dispatcher.sendMessage(msg);
			}else if(command.equals(CommandId.S_LOGIN_ERROR_OTHER_PLACE)){
				//send broadcast
				Message msg = new Message();
				msg.what = LOGINFAILED;
				msg.obj = IMPSDev.getContext().getResources().getString(R.string.login_account_at_other_place);
				dispatcher.sendMessage(msg);
			}else if(command.equals(CommandId.S_LOGIN_UNKNOWN)){
				//send broadcast
				Message msg = new Message();
				msg.what = LOGINFAILED;
				msg.obj = IMPSDev.getContext().getResources().getString(R.string.login_error);
				dispatcher.sendMessage(msg);
			}else if(command.equals(CommandId.S_REG_ERROR_USER_EXIST)){
				Message msg = new Message();
				msg.what = REGISTERFAIL;
				msg.obj = media.getmHeader().get("Description");
				dispatcher.sendMessage(msg);
				
			}else if(command.equals(CommandId.S_REG_ERROR_UNKNOWN)){
				Message msg = new Message();
				msg.what = REGISTERFAIL;
				msg.obj = media.getmHeader().get("Description");
				dispatcher.sendMessage(msg);
			}else if(command.equals(CommandId.S_REGISTER)){
				Message msg = new Message();
				msg.what = REGISTERSUCCESS;
				dispatcher.sendMessage(msg);
			}else if(command.equals(CommandId.S_HEARTBEAT_RSP)){
				
			}else if(command.equals(CommandId.S_FRIENDLIST_REFURBISH_RSP)){
				
			}else if(command.equals(CommandId.S_ADDFRIEND_REQ)){
				
			}else if(command.equals(CommandId.S_ADDFRIEND_RSP)){
				
			}else if(command.equals(CommandId.S_ERROR)){
				
			}
		}else if(media.getType()==IMPSType.IMAGE||media.getType()==IMPSType.SMS||media.getType()==IMPSType.AUDIO){
			try {
				new LocalDBHelper(IMPSDev.getContext()).storeMsg((com.imps.model.MediaType) media);
			} catch (SQLException e) {
				e.printStackTrace();
			} catch (ParseException e) {
				e.printStackTrace();
			}
			Message msg = new Message();
			msg.what = MEDIARECV;
			msg.obj = media;
			dispatcher.sendMessage(msg);
		}
	}
}
