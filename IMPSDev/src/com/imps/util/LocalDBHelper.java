package com.imps.util;

import java.text.ParseException;
import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.imps.model.AudioMedia;
import com.imps.model.ImageMedia;
import com.imps.model.MediaType;
import com.imps.model.TextMedia;
import com.imps.net.handler.UserManager;
/**
 * Android local database helper</br>
 * Used to store and fetch text message locally</br>
 * 
 * Name of every user is by convention "localmsg_xxxx"</br>
 * 
 * @author Styx
 *
 */
public class LocalDBHelper extends SQLiteOpenHelper {

	private static final int DATABASE_VERSION = 1;
	private static final String DB_NAME = "localmsg_"
			+ UserManager.globaluser.getUsername();
	private static final String MSG_TABLE_NAME = "msg";
	private static final String RCT_TABLE_NAME = "recent";
	private static final String TABLE_CREAT_MAIN = "CREATE TABLE IF NOT EXISTS " +
			MSG_TABLE_NAME + 
			" (msg_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " + //message id
			"content TEXT NOT NULL, time LONG NOT NULL, " +   //message content
			"sender TEXT NOT NULL, " +  //sender name
			"receiver TEXT NOT NULL, " +  //receiver name
			"time TEXT NOT NULL, " +    //message timestamp
			"send INTEGER NOT NULL," +   //whether user send it or not
			"type INTEGER NOT NULL);";   //message type:command ,audio,sms,image,file
	private static final String TABLE_DROP_MAIN = "DROP TABLE IF EXISTS "+MSG_TABLE_NAME+";";
	private static final String TABLE_DROP_RECENT = "DROP TABLE IF EXISTS " + RCT_TABLE_NAME+";";
	private static final String TABLE_CREAT_RECENT = "CREATE TABLE IF NOT EXISTS " + 
			RCT_TABLE_NAME + 
			" (friend TEXT NOT NULL PRIMARY KEY, " +  //friend name
			"time INT NOT NULL);";                    //message timestamp

	public LocalDBHelper(Context context) {
		super(context, DB_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.d("LOCALDBHELPER", "CREATING DB");
		db.execSQL(TABLE_CREAT_MAIN);
		db.execSQL(TABLE_CREAT_RECENT);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
		Log.d("LOCALDBHELPER", "Upgrade DB");
	}

	/**
	 * Store the message to local database persistently</br>
	 * called by event listener in {@link com.imps.activities.ChatView ChatView}
	 * @throws ParseException 
	 * @throws SQLException 
	 */
	public void storeMsg(MediaType media) throws SQLException, ParseException {
		SQLiteDatabase msgdb = getWritableDatabase();
		if (msgdb == null) {
			Log.d("LOCALDB", "fail to get writable database");
			return;
		}
		if(media.getType()==MediaType.AUDIO||media.getType()==MediaType.IMAGE){
			msgdb.execSQL("INSERT INTO " + MSG_TABLE_NAME +"(content,sender,receiver,time,send,type) values (\""
					+media.getPath()+ "\", \""+media.getSender()+ "\", \"" +media.getReceiver() + "\", \"" + media.getTime()+ "\","+
					(media.isSend()?1:0)+","+(int)media.getType()+ ")");
		}
		else if(media.getType()==MediaType.SMS){
			msgdb.execSQL("INSERT INTO " + MSG_TABLE_NAME +"(content,sender,receiver,time,send,type) values (\""
					+new String(media.getContent())+ "\", \""+media.getSender()+ "\", \"" +media.getReceiver() + "\", \"" + media.getTime()+ "\","+
					(media.isSend()?1:0)+","+(int)media.getType()+ ")");
		}
		msgdb.close();
		updateRecentContact(media.isSend()?media.getReceiver():media.getSender());
	}

	/**
	 * Get all messages belonging to current user</br>
	 * without specifying friend</br>
	 * Maybe there are messages from many friends</br>
	 * @return list of messages of the current user
	 */
	public ArrayList<MediaType> fetchMsg() {
		SQLiteDatabase msgdb = getReadableDatabase();
		if (msgdb == null) {
			Log.d("LOCALDB", "fail to get readable database");
			return null;
		}
		ArrayList<MediaType> historyList = new ArrayList<MediaType>();
		Cursor result = msgdb.query(MSG_TABLE_NAME, null, null, null, null, null, null);
		if (result.getCount() < 1) {
			msgdb.close();
			result.close();
			return null;
		} else {
			do {
				int type = result.getInt(6);
				MediaType msg  = null;
				if(type==MediaType.AUDIO){
					msg = new AudioMedia(result.getInt(5)==1?true:false);
				}else if(type==MediaType.SMS){
					msg = new TextMedia(result.getInt(5)==1?true:false);
				}else if(type==MediaType.IMAGE){
					msg = new ImageMedia(result.getInt(5)==1?true:false);
				}else{
					continue;
				}
				if(type==MediaType.SMS)msg.setContent(result.getString(1).getBytes());
				else msg.setPath(result.getString(1));
				msg.setReceiver(result.getString(3));
				msg.setSender(result.getString(2));
				msg.setTime(result.getString(4));
				msg.setType((byte)result.getInt(6));
				historyList.add(msg);
				result.moveToNext();
			} while (!result.isAfterLast());
			result.close();
			msgdb.close();
			return historyList;
		}
	}
	
	/**
	 * Get all messages belonging to current user 
	 * from the specified by {@link #fetchMsg(String friend) friend}
	 * @return list of messages of the current user
	 */
	public ArrayList<MediaType> fetchMsg(String friend) {
		SQLiteDatabase msgdb = getReadableDatabase();
		if (msgdb == null) {
			Log.d("LOCALDB", "fail to get readable database");
			return null;
		}
		ArrayList<MediaType> historyList = new ArrayList<MediaType>();
		Cursor result = msgdb.query(MSG_TABLE_NAME, null, "sender=? or receiver=?",
				new String[] { friend,friend }, null, null, null);
		result.moveToFirst();
		if (result.getCount() < 1) {
			result.close();
			msgdb.close();
			return null;
		} else {
			do {
				int type = result.getInt(6);
				MediaType msg  = null;
				if(type==MediaType.AUDIO){
					msg = new AudioMedia(result.getInt(5)==1?true:false);
				}else if(type==MediaType.SMS){
					msg = new TextMedia(result.getInt(5)==1?true:false);
				}else if(type==MediaType.IMAGE){
					msg = new ImageMedia(result.getInt(5)==1?true:false);
				}else{
					continue;
				}
				if(type==MediaType.SMS)msg.setContent(result.getString(1).getBytes());
				else msg.setPath(result.getString(1));
				msg.setReceiver(result.getString(3));
				msg.setSender(result.getString(2));
				msg.setTime(result.getString(4));
				msg.setType((byte)result.getInt(6));
				historyList.add(msg);
				result.moveToNext();
			} while (!result.isAfterLast());
			result.close();
			msgdb.close();
			return historyList;
		}
	}
	
	/**
	 * Get the latest message belonging to current user
	 * from  {@link #fetchMsg(String friend) friend}
	 * @param friend
	 * @return
	 */
	public MediaType fetchLatest(String friend) {		
		SQLiteDatabase msgdb = getReadableDatabase();
		if (msgdb == null) {
			Log.d("LOCALDB", "fail to get readable database");
			return null;
		}
		Cursor result = msgdb.query(MSG_TABLE_NAME, null, "sender=? or receiver=?", new String[] { friend,friend }, null, null, "msg_id desc");
		if (result.getCount() < 1) {
			result.close();
			msgdb.close();
			return null;
		} else {
			int type = result.getInt(6);
			MediaType msg  = null;
			if(type==MediaType.AUDIO){
				msg = new AudioMedia(result.getInt(5)==1?true:false);
			}else if(type==MediaType.SMS){
				msg = new TextMedia(result.getInt(5)==1?true:false);
			}else if(type==MediaType.IMAGE){
				msg = new ImageMedia(result.getInt(5)==1?true:false);
			}else{
				return null;
			}
			if(type==MediaType.SMS)msg.setContent(result.getString(1).getBytes());
			else msg.setPath(result.getString(1));
			msg.setReceiver(result.getString(3));
			msg.setSender(result.getString(2));
			msg.setTime(result.getString(4));
			msg.setType((byte)result.getInt(6));
				result.moveToNext();
			result.close();
			msgdb.close();
			return msg;
		}
	}
	
	/**
	 * Retrieve records of recent contacts of current user</br>
	 * ordered by the time of last conversation between the two</br>
	 * @return list of contacts' names
	 */
	public ArrayList<String> fetchRecentContacts() {
		SQLiteDatabase rcdb = getReadableDatabase();
		if (rcdb == null) {
			Log.d("LOCALDB", "fetchRecentContacts: fail to get readable database");
			return null;
		}
		ArrayList<String> recentContacts = new ArrayList<String>();
		Cursor result = rcdb.query(RCT_TABLE_NAME, null, null, null, null, null, "time");
		result.moveToFirst();
		if (result.getCount() < 1) {
			result.close();
			rcdb.close();
			return null;
		} else {
			do {
				recentContacts.add(result.getString(0));
				result.moveToNext();
			} while (!result.isAfterLast());
			result.close();
			rcdb.close();
			return recentContacts;
		}
	}
	
	/**
	 * update recent contacts to database 
	 * if the contact already exists, update its last-chat-time</br>
	 * else insert a new recent contact to database
	 * @param friend the contact to be added
	 */
	public void updateRecentContact(String friend) {
		SQLiteDatabase rcdb = getWritableDatabase();
		if (rcdb == null) {
			Log.d("LOCALDB", "updateRecentContact: fail to get writable database");
			return;
		}
		Cursor existResult = rcdb.query(RCT_TABLE_NAME, null, "friend=?",
				new String[] { friend }, null, null, null);
		if (existResult.getCount() < 1) {
			// This friend does not exist in the recent contacts list
			// and should be add to the list
			existResult.close();
			rcdb.execSQL("INSERT INTO " + RCT_TABLE_NAME + " VALUES (\"" +
					friend + "\", " + new java.util.Date().getTime() + ")");
			rcdb.close();
		} else  {
			existResult.close();
			rcdb.execSQL("UPDATE " + RCT_TABLE_NAME + " SET time=" +
					new java.util.Date().getTime() + " WHERE friend=\"" + friend + "\"");
			rcdb.close();
		}
	}
}
