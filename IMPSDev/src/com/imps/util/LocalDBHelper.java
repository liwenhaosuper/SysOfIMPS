package com.imps.util;

import sun.security.pkcs11.Secmod.DbMode;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class LocalDBHelper extends SQLiteOpenHelper {

	private static final int DATABASE_VERSION = 2;
	private static final String TABLE_NAME = "localmsg";
	private static final String TABLE_CREAT = "CREATE TABLE "
			+ TABLE_NAME
			+ " (msg_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
			"time TEXT NOT NULL, content TEXT NOT NULL, " +
			"sender TEXT NOT NULL);";

	public LocalDBHelper(Context context) {
		super(context, TABLE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(TABLE_CREAT);
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {

	}

	public void storeMsg(String content, String time, String sender) {
		SQLiteDatabase msgdb = getReadableDatabase();
		msgdb.execSQL("INSERT INTO localmsg (content, time, sender) values ("
				+ content + ", " + time + ", " + sender + ")");
	}
}
