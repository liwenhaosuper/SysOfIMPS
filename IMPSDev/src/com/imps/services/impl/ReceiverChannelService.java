package com.imps.services.impl;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.database.SQLException;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.imps.IMPSDev;
import com.imps.R;
import com.imps.activities.AudioChat;
import com.imps.activities.ChatView;
import com.imps.activities.IMPSContainer;
import com.imps.activities.VideoContact;
import com.imps.basetypes.Constant;
import com.imps.basetypes.ListContentEntity;
import com.imps.basetypes.MediaType;
import com.imps.basetypes.SystemMsgType;
import com.imps.basetypes.User;
import com.imps.net.handler.UserManager;
import com.imps.util.CommonHelper;
import com.imps.util.LocalDBHelper;

public class ReceiverChannelService {
	private static boolean DEBUG = IMPSDev.isDEBUG();
	private static String TAG = ReceiverChannelService.class.getCanonicalName();
	public static final int LOGINSUCCESS = 1;
	public static final int LOGINFAILED = 2;
	
	public static final int REGISTERSUCCESS = 3;
	public static final int REGISTERFAIL = 4;
	
	public static final int FRIENDLISTREFRESH = 5;
	public static final int FRIENDSTATUSNOTIFY = 6;
	
	public static final int SMSRECV = 7;
	public static final int SMSRSP = 8;
	
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
	
	private class P2PIden{
		public String ip;
		public int port;
		public String userName;
		public boolean res;
	}
	private class Identify{
		public int sid;
		public String friName;
	}
	//username, data   used to store the recv image and audio data
	public static HashMap<Identify,byte[] >  usersImageData = 
		      new HashMap<Identify,byte[] >();
    public static HashMap<Identify,LinkedList<byte[]> >  usersAudioData = 
			      new HashMap<Identify,LinkedList<byte[]> >();
	
	
	public ReceiverChannelService(){
		
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
			case SMSRECV:
				if(DEBUG) Log.d(TAG,"sms recv broadcast sent.");
				//add to the Current sessions
				MediaType media = (MediaType)msg.obj;
				if(UserManager.CurSessionFriList.containsKey(media.getFriend())){
					UserManager.CurSessionFriList.get(media.getFriend()).add(media);
				}else{
					List<MediaType> item = new ArrayList<MediaType>();
					item.add(media);
					UserManager.CurSessionFriList.put(media.getFriend(), item);
				}
				
				// store received message to database
//				try {
//					new LocalDBHelper(IMPSDev.getContext()).storeMsg(((MediaType)msg.obj).getMsgContant(),
//							((MediaType)msg.obj).getTime(),
//							((MediaType)msg.obj).getFriend(),
//							MediaType.from/* From friend */, MediaType.SMS);
//				} catch (SQLException e) {
//					e.printStackTrace();
//				} catch (ParseException e) {
//					e.printStackTrace();
//				}
				
				Intent sms = new Intent(Constant.SMS);
				sms.putExtra(Constant.SMSCONTENT, ((MediaType)msg.obj).getMsgContant());
				sms.putExtra(Constant.USERNAME, ((MediaType)msg.obj).getFriend());
				Log.d(TAG, ((MediaType)msg.obj).getFriend());
				sms.putExtra(Constant.TIME, ((MediaType)msg.obj).getTime());
				IMPSDev.getContext().sendBroadcast(sms);

				// if user is just chatting with the friend, then do nothing
				// else send a notification
				if(UserManager.activeFriend!=null&&UserManager.activeFriend.equals(media.getFriend())){
					return;
				}
				String snip = media.getMsgContant();
				if(snip.length()>10){
					snip = snip.substring(0,9)+"...";
				}
				ServiceManager.showNotification(R.drawable.new_msg_notification, R.drawable.new_msg_notification,
						media.getFriend()+":"+snip, ChatView.class,media.getFriend());
			
				break;
			case SMSRSP:
				if(DEBUG) Log.d(TAG,"sms rsp broadcast sent.");
				Intent smsrsp = new Intent(Constant.SMSRSP);
				smsrsp.putExtra(Constant.SMSID, ((Integer)msg.obj).intValue());
				IMPSDev.getContext().sendBroadcast(smsrsp);
				break;
			case IMAGERECV:
				if(DEBUG) Log.d(TAG,"Image recv");
				break;
			case IMAGERECVD:
				if(DEBUG) Log.d(TAG,"Image recv done");
				Intent imageintent = new Intent(Constant.IMAGE);
				MediaType imageType = ((MediaType)msg.obj);
				imageintent.putExtra(Constant.IMAGEID, imageType.getId());
				imageintent.putExtra(Constant.USERNAME, imageType.getFriend());
				IMPSDev.getContext().sendBroadcast(imageintent);
				break;
			case IMAGERSP:
				break;
			case AUDIORECV:
				if(DEBUG) Log.d(TAG,"Audio recv");
				break;
			case AUDIORECVD:
				if(DEBUG) Log.d(TAG,"Audio recv done");
				Intent audiointent = new Intent(Constant.AUDIO);
				MediaType audioType = ((MediaType)msg.obj);
				audiointent.putExtra(Constant.AUDIOID, audioType.getId());
				audiointent.putExtra(Constant.USERNAME, audioType.getFriend());
				IMPSDev.getContext().sendBroadcast(audiointent);
				break;
			case AUDIORSP:
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
				Intent audiop = new Intent(Constant.P2PAUDIOREQ);
				P2PIden iden = (P2PIden)msg.obj;
				audiop.putExtra(Constant.IP, iden.ip);
				audiop.putExtra(Constant.PORT, iden.port);
				audiop.putExtra(Constant.USERNAME, iden.userName);
				IMPSDev.getContext().sendBroadcast(audiop);
				ServiceManager.showNotification(R.drawable.new_audio_notification, R.drawable.new_audio_notification,
						iden.userName+":"+IMPSDev.getContext().getString(R.string.audio_chat_request_notify), AudioChat.class, 
						iden.userName,iden.ip,Integer.toString(iden.port));
				break;
			case P2PAUDIORSP:
				if(DEBUG) Log.d(TAG,"P2P audio rsp broadcast.");
				Intent audiopp = new Intent(Constant.P2PAUDIORSP);
				P2PIden idenp = (P2PIden)msg.obj;
				audiopp.putExtra(Constant.IP, idenp.ip);
				audiopp.putExtra(Constant.PORT, idenp.port);
				audiopp.putExtra(Constant.USERNAME, idenp.userName);
				audiopp.putExtra(Constant.RESULT, idenp.res);
				IMPSDev.getContext().sendBroadcast(audiopp);
				break;
			case P2PVIDEOREQ:
				if(DEBUG) Log.d(TAG,"P2P video req broadcast.");
				Intent videop = new Intent(Constant.P2PVIDEOREQ);
				P2PIden viden = (P2PIden)msg.obj;
				videop.putExtra(Constant.IP, viden.ip);
				videop.putExtra(Constant.PORT, viden.port);
				videop.putExtra(Constant.USERNAME, viden.userName);
				IMPSDev.getContext().sendBroadcast(videop);
				ServiceManager.showNotification(R.drawable.new_video_notification, R.drawable.new_video_notification,
						viden.userName+":"+IMPSDev.getContext().getString(R.string.video_chat_request_notify), VideoContact.class, 
						viden.userName,viden.ip,Integer.toString(viden.port));
				break;
			case P2PVIDEORSP:
				if(DEBUG) Log.d(TAG,"P2P video rsp broadcast.");
				Intent videopp = new Intent(Constant.P2PVIDEORSP);
				P2PIden videnp = (P2PIden)msg.obj;
				videopp.putExtra(Constant.IP, videnp.ip);
				videopp.putExtra(Constant.PORT, videnp.port);
				videopp.putExtra(Constant.USERNAME, videnp.userName);
				videopp.putExtra(Constant.RESULT, videnp.res);
				IMPSDev.getContext().sendBroadcast(videopp);
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
	/**
	 * login message
	 */
	
	//login success
	//@param:User the user msg from server
	public void LoginSuccess(User user){
		UserManager.globaluser.setEmail(user.getEmail());
		UserManager.globaluser.setDescription(user.getDescription());
		UserManager.globaluser.setGender(user.getGender());
		//send broadcast
		Message msg = new Message();
		msg.what = LOGINSUCCESS;
		dispatcher.sendMessage(msg);
	}
	public void LoginFailed(String errorMsg){
		//send broadcast
		Message msg = new Message();
		msg.what = LOGINFAILED;
		msg.obj = errorMsg;
		dispatcher.sendMessage(msg);
	}

	/**
	 * register message
	 */
	public void RegisterSuccess(){
		Message msg = new Message();
		msg.what = REGISTERSUCCESS;
		dispatcher.sendMessage(msg);
	}
	public void RegisterFailed(String errorMsg){
		//send broadcast
		Message msg = new Message();
		msg.what = REGISTERFAIL;
		msg.obj = errorMsg;
		dispatcher.sendMessage(msg);
	}
	
	/**
	 * friend list maintain
	 */
	public void recvFriendList(){
		Message msg = new Message();
		msg.what = FRIENDLISTREFRESH;
		dispatcher.sendMessage(msg);
	}
	public void FriendStatusNotify(User user){
		Message msg = new Message();
		msg.what = FRIENDSTATUSNOTIFY;
		msg.obj = user;
		dispatcher.sendMessage(msg);
	}
	
	/**
	 * sms message
	 */
	
	public void onSmsRecv(MediaType media){
		if(media.getType()!=MediaType.SMS)
			return;
		Message msg = new Message();
		msg.what = SMSRECV;
		msg.obj = media;
		dispatcher.sendMessage(msg);
	}
	//TODO: Need to be done
	public void onSmsSendSuccess(int smsId){
		Message msg =new Message();
		msg.what = SMSRSP;
		msg.obj = new Integer(smsId);
		dispatcher.sendMessage(msg);
	}
	/**
	 * audio message
	 */
	
	public void onAudioRecv(String friName,int sid,boolean isEOF,byte[] data){
		if(DEBUG) Log.d(TAG,"friName is:"+friName+" sid is:"+sid+" eof:"+isEOF);
		boolean flag = false;
		LinkedList<byte[]> packet = null;
		Iterator iter = usersAudioData.entrySet().iterator();
		Identify key = null;
		while (iter.hasNext()) { 
		    Map.Entry entry = (Map.Entry) iter.next(); 
		    key = (Identify)entry.getKey();
		    if(key.sid==sid&&key.friName.equals(friName)){
		    	((LinkedList<byte[]>)entry.getValue()).add(data);
		    	 flag = true;
		    	 if(isEOF){
		    		 packet = (LinkedList<byte[]>)entry.getValue();
		    	 }
		    	 break;
		    }
		}
		if(!flag){
			Identify newitem = new Identify();
			newitem.sid = sid;
			newitem.friName = friName;
			LinkedList<byte[]> nitem = new LinkedList<byte[]>();
			nitem.add(data);
			usersAudioData.put(newitem, nitem);
			//a new audio
			Message msg = new Message();
			msg.what = AUDIORECV;
			dispatcher.sendMessage(msg);
			if(isEOF){
				packet = nitem;
			}
		}
		if(isEOF){
			//TODO:Audio data changed...
			if(DEBUG) Log.d(TAG,"Start playing audio");
/*			Track track = new Track();
			track.data = packet;
			track.run();*/
			MediaType media = new MediaType(MediaType.AUDIO,packet,MediaType.from);
			media.setTime(CommonHelper.getTime());
			media.setFriend(friName);
			if(UserManager.CurSessionFriList.containsKey(friName))
			{
				 UserManager.CurSessionFriList.get(friName).add(media);
				 if(DEBUG) Log.d(TAG,"adding to the list");
			}
			else{
				List<MediaType> newmsgbox = new ArrayList<MediaType>();
				newmsgbox.add(media);
				UserManager.CurSessionFriList.put(friName, newmsgbox);
				if(DEBUG) Log.d(TAG,"adding to the list with new image");
			}
			usersAudioData.remove(key);
			if(UserManager.activeFriend!=null&&UserManager.activeFriend.equals(friName)){
				ListContentEntity entity = new ListContentEntity(friName,media.getTime(),"",ListContentEntity.MESSAGE_FROM_AUDIO,media.getContent());
				ChatView.list.add(entity);
				//TODO:Notify the user
			}else{
				ServiceManager.showNotification(R.drawable.new_msg_notification, R.drawable.new_audio_notification,
						IMPSDev.getContext().getResources().getString(R.string.new_audio_received_from_friend,media.getFriend()),
						ChatView.class,media.getFriend());
			}
			Message msg = new Message();
			msg.what = AUDIORECVD;
			msg.obj = media;
			dispatcher.sendMessage(msg);
			
		}
	}
	public void onAudioSendSuccess(String ret,int smsId){
		
	}
	
	/**
	 * image message
	 */
	
	public void onImageRecv(String userName,int sid,boolean isEOF,byte[] data){
		boolean flag = false;
		byte packet[] = null;
		if(DEBUG) Log.d(TAG,"friName is:"+userName+" sid is:"+sid);
		Iterator iter = usersImageData.entrySet().iterator();
		Identify key = null;
		while (iter.hasNext()) { 
		    Map.Entry entry = (Map.Entry) iter.next(); 
		    key = (Identify)entry.getKey();
		    if(key.sid==sid&&key.friName.equals(userName)){
				byte[] val = (byte[])entry.getValue();
				byte[] newval =new byte[data.length+val.length];
		    	System.arraycopy(val, 0, newval, 0, val.length);
		    	System.arraycopy(data, 0, newval, val.length, data.length);
		    	usersImageData.put(key, newval);
		    	if(isEOF) packet = newval;
		    	if(DEBUG) Log.d(TAG,"Put image snipper.");
		    	flag = true;
		    	break;
		    }
		    
		}
		if(!flag){//a new image
			key = new Identify();
			key.friName = userName;
			key.sid = sid;
			usersImageData.put(key, data);
			if(isEOF) packet = data;
			if(DEBUG) Log.d(TAG,"Add image item.");
			//send message to dispatcher
			Message msg = new Message();
			msg.what = IMAGERECV;
			msg.obj = userName;
			dispatcher.sendMessage(msg);
		}

		if(isEOF){
			Bitmap bMap = CommonHelper.bytesToBitmap(packet);
			if(bMap==null){
				if(DEBUG) Log.d(TAG,"Bitmap decode to null...byte buffer size is:"+packet.length);
			}
			if(DEBUG) Log.d(TAG,"Bitmap bytes size is :"+packet.length);
			MediaType media = new MediaType(MediaType.IMAGE,MediaType.from);
			media.setMsgContant(CommonHelper.saveImage(bMap));
			media.setFriend(userName);
			media.setTime(CommonHelper.getTime());
			if(UserManager.CurSessionFriList.containsKey(userName))
			{
				 UserManager.CurSessionFriList.get(userName).add(media);
				 if(DEBUG) Log.d(TAG,"adding to the list");
			}
			else{
				List<MediaType> newmsgbox = new ArrayList<MediaType>();
				newmsgbox.add(media);
				UserManager.CurSessionFriList.put(userName, newmsgbox);
				if(DEBUG) Log.d(TAG,"adding to the list with new image");
			}
			if(UserManager.activeFriend!=null&&UserManager.activeFriend.equals(userName)){
				ListContentEntity entity = new ListContentEntity(userName,media.getTime(),"",ListContentEntity.MESSAGE_FROM_PICTURE,media.getMsgContant());
				ChatView.list.add(entity);
				//TODO:Notify the user
			}else{
				ServiceManager.showNotification(R.drawable.new_msg_notification, R.drawable.new_msg_notification,
						IMPSDev.getContext().getResources().getString(R.string.new_image_received_from_friend,media.getFriend()),
						ChatView.class,media.getFriend());
			}
			usersImageData.remove(key);
			
			Message msg = new Message();
			msg.what = IMAGERECVD;
			msg.obj = media;
			dispatcher.sendMessage(msg);
		}
	}
	public void onImageSendSuccess(String ret,int smsId){
		
	}
	
	public void onRecvFriendReq(String friName){
		SystemMsgType item = new SystemMsgType();
		item.name = friName;
		item.status = SystemMsgType.NONE;
		item.text = IMPSDev.getContext().getResources().getString(R.string.add_friend_req_notify,friName);
		item.time = CommonHelper.getTime();
		item.type = SystemMsgType.FROM;
		UserManager.mSysMsgs.add(item);
		if(DEBUG)Log.d(TAG,"req ADD...");
		ServiceManager.showNotification(R.drawable.new_contact_notification, R.drawable.new_contact_notification,
				IMPSDev.getContext().getResources().getString(R.string.add_friend_req_notify,friName), IMPSContainer.class,2);
		Message msg = new Message();
		msg.what = ADDFRIENDREQ;
		msg.obj = item;
		dispatcher.sendMessage(msg);
	}
	public void onRecvFriendRsp(String friName,boolean rel){
		SystemMsgType item = new SystemMsgType();
		item.name = friName;
		if(rel){
			item.status = SystemMsgType.ACCEPTED;
			item.text = IMPSDev.getContext().getResources().getString(R.string.add_friend_rsp_accepted,friName);
			ServiceManager.getmContact().sendFriListReq();
		}else{
			item.status = SystemMsgType.DENIED;
			item.text = IMPSDev.getContext().getResources().getString(R.string.add_friend_rsp_denied,friName);
		}
		if(DEBUG) Log.d(TAG,"RSP...");
		item.time = CommonHelper.getTime();
		item.type = SystemMsgType.FROM;
		UserManager.mSysMsgs.add(item);
		if(DEBUG)Log.d(TAG,"rsp ADD...");
		ServiceManager.showNotification(R.drawable.new_contact_notification, R.drawable.new_contact_notification,
				IMPSDev.getContext().getResources().getString(rel==false?R.string.add_friend_rsp_denied:R.string.add_friend_rsp_accepted,friName), IMPSContainer.class,2);
		Message msg = new Message();
		msg.what = ADDFRIENDRSP;
		msg.obj = item;
		dispatcher.sendMessage(msg);
	}
	public void friendStatusNotify(User user){
		Message msg = new Message();
		msg.what = STATUSNOTIFY;
		msg.obj = user;
		dispatcher.sendMessage(msg);
	}
	/**
	 * p2p event
	 */

	public void onP2PAudioReq(String friName,String ip,int port){
		P2PIden iden = new P2PIden();
		iden.ip = ip;
		iden.port = port;
		iden.userName = friName;
		Message msg = new Message();
		msg.what = P2PAUDIOREQ;
		msg.obj = iden;
		dispatcher.sendMessage(msg);
	}
	public void onP2PAudioRsp(String friName,boolean result,String ip,int port){
		P2PIden iden = new P2PIden();
		iden.ip = ip;
		iden.port = port;
		iden.userName = friName;
		iden.res = result;
		Message msg = new Message();
		msg.what = P2PAUDIORSP;
		msg.obj = iden;
		dispatcher.sendMessage(msg);
	}
	public void onP2PVideoReq(String friName,String ip,int port){
		P2PIden iden = new P2PIden();
		iden.ip = ip;
		iden.port = port;
		iden.userName = friName;
		Message msg = new Message();
		msg.what = P2PVIDEOREQ;
		msg.obj = iden;
		dispatcher.sendMessage(msg);
	}
	public void onP2PVideoRsp(String friName,boolean result,String ip,int port){
		P2PIden iden = new P2PIden();
		iden.ip = ip;
		iden.port = port;
		iden.userName = friName;
		iden.res = result;
		Message msg = new Message();
		msg.what = P2PVIDEORSP;
		msg.obj = iden;
		dispatcher.sendMessage(msg);
	}
	public void recvSearchFriendRes(List<String> users){
		searchRes = users;
		Message msg = new Message();
		msg.what = SEARCHFRIENDRESULT;
		dispatcher.sendMessage(msg);
	}
	public List<String> searchRes = new ArrayList<String>();
}

