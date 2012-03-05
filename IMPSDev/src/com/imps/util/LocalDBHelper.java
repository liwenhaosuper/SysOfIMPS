package com.imps.util;

import java.rmi.server.LogStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import android.R.string;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.imps.basetypes.UserMessage;
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

	private static final int DATABASE_VERSION = 2;
	private static final String DB_NAME = "localmsg_"
			+ UserManager.globaluser.getUsername();
	private static final String MSG_TABLE_NAME = "msg";
	private static final String RCT_TABLE_NAME = "recent";
	private static final String TABLE_CREAT_MAIN = "CREATE TABLE IF NOT EXISTS " +
			MSG_TABLE_NAME + 
			" (msg_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
			"content TEXT NOT NULL, time TEXT NOT NULL, " +
			"friend TEXT NOT NULL, " +
			"dir INTEGER NOT NULL);";
	
	private static final String TABLE_CREAT_RECENT = "CREATE TABLE IF NOT EXISTS " + 
			RCT_TABLE_NAME + 
			" (friend TEXT NOT NULL PRIMARY KEY, " +
			"time INT NOT NULL);";

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
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {

	}

	/**
	 * Store the message to local database persistently</br>
	 * called by event listener in {@link com.imps.activities.ChatView ChatView}
	 * @param content message body content
	 * @param time time of the message
	 * @param sender sender of the message
	 * @param dir direction of the message, 1: from {@link friend}, 0: to {@link friend}
	 */
	public void storeMsg(String content, String time, String friend, int dir) {
		SQLiteDatabase msgdb = getWritableDatabase();
		if (msgdb == null) {
			Log.d("LOCALDB", "fail to get writable database");
			return;
		}
		msgdb.execSQL("INSERT INTO " + MSG_TABLE_NAME + "(content, time, friend, dir) values (\""
				+ content + "\", \"" + time + "\", \"" + friend + "\", " + dir + ")");
		msgdb.close();
	}

	/**
	 * Get all messages belonging to current user</br>
	 * without specifying friend</br>
	 * Maybe there are messages from many friends</br>
	 * @return list of messages of the current user
	 */
	public ArrayList<UserMessage> fetchMsg() {
		SQLiteDatabase msgdb = getReadableDatabase();
		if (msgdb == null) {
			Log.d("LOCALDB", "fail to get readable database");
			return null;
		}
		ArrayList<UserMessage> historyList = new ArrayList<UserMessage>();
		Cursor result = msgdb.query(MSG_TABLE_NAME, null, null, null, null, null, null);
		if (result.getCount() < 1) {
			msgdb.close();
			result.close();
			return null;
		} else {
			do {
				UserMessage msg = new UserMessage(result.getString(1),
						result.getString(2), result.getString(3), result.getInt(4));
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
	public ArrayList<UserMessage> fetchMsg(String friend) {
		SQLiteDatabase msgdb = getReadableDatabase();
		if (msgdb == null) {
			Log.d("LOCALDB", "fail to get readable database");
			return null;
		}
		ArrayList<UserMessage> historyList = new ArrayList<UserMessage>();
		Cursor result = msgdb.query(MSG_TABLE_NAME, null, "friend=?",
				new String[] { friend }, null, null, null);
		result.moveToFirst();
		if (result.getCount() < 1) {
			result.close();
			msgdb.close();
			return null;
		} else {
			do {
				UserMessage msg = new UserMessage(result.getString(1),
						result.getString(2), result.getString(3), result.getInt(4));
				historyList.add(msg);
				result.moveToNext();
			} while (!result.isAfterLast());
			result.close();
			msgdb.close();
			return historyList;
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
			rcdb.execSQL("UPDATE " + RCT_TABLE_NAME + " SET time=" +
					new java.util.Date().getTime() + " WHERE friend=\"" + friend + "\"");
		}
	}
}
