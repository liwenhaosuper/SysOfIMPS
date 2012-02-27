package com.imps.util;

import java.util.ArrayList;

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
	private static final String TABLE_NAME = "localmsg_"
			+ UserManager.globaluser.getUsername();
	private static final String TABLE_CREAT = "CREATE TABLE "
			+ TABLE_NAME
			+ " (msg_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
			"content TEXT NOT NULL, time TEXT NOT NULL, " +
			"friend TEXT NOT NULL, " +
			"dir INTEGER NOT NULL);";

	public LocalDBHelper(Context context) {
		super(context, TABLE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.d("LOCALDBHELPER", "CREATING DB");
		db.execSQL(TABLE_CREAT);
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
		msgdb.execSQL("INSERT INTO " + TABLE_NAME + "(content, time, friend, dir) values (\""
				+ content + "\", \"" + time + "\", \"" + friend + "\", " + dir + ")");
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
		Cursor result = msgdb.query(TABLE_NAME, null, null, null, null, null, null);
		if (result.getCount() < 1) {
			return null;
		} else {
			do {
				UserMessage msg = new UserMessage(result.getString(1),
						result.getString(2), result.getString(3), result.getInt(4));
				historyList.add(msg);
			} while (result.isAfterLast());
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
		Cursor result = msgdb.query(TABLE_NAME, null, "friend=?",
				new String[] { friend }, null, null, null);
		result.moveToFirst();
		if (result.getCount() < 1) {
			return null;
		} else {
			do {
				UserMessage msg = new UserMessage(result.getString(1),
						result.getString(2), result.getString(3), result.getInt(4));
				historyList.add(msg);
			} while (result.isAfterLast());
			return historyList;
		}
	} 
}
