package com.imps.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Random;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.util.Log;

import com.imps.base.User;
import com.imps.base.userStatus;
import com.imps.handler.UserManager;
@Deprecated
public class ContactsManagerDbAdater {
	public static final String TAG="ContactsManagerDbAdater";
	public static final String DATABASE_NAME="imps_contactsmanager.db";
	public static final int DATABASE_VERSON=3;
	public static final String TABLE_OWNER = "owner";
	public static final String TABLE_CONTACTS="contacts";
	public static final String TABLE_GROUPS="groups";
	public static final String TABLE_MESSAGE = "message";
	public static final String TABLEOWNER = 
		"create table owner("+
		"_id INTEGER PRIMARY KEY,"+
		"ownername TEXT NOT NULL"+
		");"
		;
	public static final String TABLECONTACTS=
		"create table contacts("+
		"_id INTEGER PRIMARY KEY,"+//rowID    //0
		"username TEXT  NOT NULL,"+ //����      //1
		"contactIcon BLOB,"+ //��ϵ��ͼ��         //2
		"gender INTEGER,"+ //�Ա� 1Ϊ�� 0ΪŮ    //3
		"groupName TEXT,"+ //��������      //4
		"longitude FLOAT,"+ //����      //5
		"latitude FLOAT,"+ //γ��          //6
		"email TEXT NOT NULL,"+ //����         //7
		"description TEXT NOT NULL,"+ //ǩ��       //8
		"modifyTime TEXT,"+ //����ʱ��          //9
		"status INTEGER,"+  //״̬
		"ownername TEXT"+
		");";
	public static final String TABLEGROUPS=
		"create table groups("+
		"_id INTEGER PRIMARY KEY,"+ //rowId
		"groupName TEXT UNIQUE NOT NULL,"+ //����
		"createTime TEXT,"+ //����ʱ��
		"modifyTime TEXT,"+ //�޸�ʱ��
		"ownername TEXT"+
		");";
	public static final String TABLEMESSAGE=
		"create table message("+
		"_id INTEGER PRIMARY KEY,"+  //id
		"friendName TEXT NOT NULL,"+  //friend name
	    "message TEXT,"+   //message
		"time TEXT,"+    //send time
		"ownername TEXT"+
		");"
		;
	private Context context;
	private static DatabaseHelper dbHelper;
	private static SQLiteDatabase mSQLiteDatabase = null;
	
	
	public ContactsManagerDbAdater(Context context){
		this.context=context;
	}
	
	public void open(){
		dbHelper=new DatabaseHelper(context);
		if(mSQLiteDatabase!=null&&mSQLiteDatabase.isOpen())
			mSQLiteDatabase.close();
		mSQLiteDatabase=dbHelper.getWritableDatabase();
	}
	
	public void close(){
		dbHelper.close();
		System.out.println(">>>>>>>>>>>>>>>>>>>>>>contact DB CLOSE <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
	}
	private static class DatabaseHelper extends SQLiteOpenHelper{

		public DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSON);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			Log.i("TAG", "create table start...");
			
			db.execSQL("DROP TABLE IF EXISTS "+TABLE_CONTACTS);
			db.execSQL("DROP TABLE IF EXISTS "+TABLE_GROUPS);
			db.execSQL("DROP TABLE IF EXISTS "+TABLE_MESSAGE);
			db.execSQL(TABLECONTACTS);
			db.execSQL(TABLEGROUPS);
			db.execSQL(TABLEMESSAGE);
			//������ʱ����
			String tempGroups[]={"δ����","�ҵĺ���","ͬѧ","ͬ��","����"};
			for(int i=0;i<tempGroups.length;i++){
				String sql="insert into groups values(?,?,null,null,?)";
				Object[] bindArgs={i+1,tempGroups[i],UserManager.getGlobaluser().getUsername()};
				db.execSQL(sql,bindArgs);
			}
			
		/*	String tempName[]={
					"android",
					"google",
					"windows mobile",
					"microsoft",
					"symbian",
					"nokia",
					"bada",
					"sumsung",
					"IBM",
					"QQ"
			};
			
			Random random=new Random();
			int index=0;
			*//**
			 * 		"_id INTEGER PRIMARY KEY,"+//rowID    //0
		"username TEXT  NOT NULL,"+ //����      //1
		"contactIcon BLOB,"+ //��ϵ��ͼ��         //2
		"gender INTEGER,"+ //�Ա� 1Ϊ�� 0ΪŮ    //3
		"groupName TEXT,"+ //��������      //4
		"longitude FLOAT,"+ //����      //5
		"latitude FLOAT,"+ //γ��          //6
		"email TEXT NOT NULL,"+ //����         //7
		"description TEXT NOT NULL,"+ //ǩ��       //8
		"modifyTime TEXT"+ //����ʱ��          //9
			 *//*
			//������ʱ����ϵ��
			for(int i=0;i<10;i++){
				String sql="insert into contacts values(?,?,null,0,?,?,?,'liwenhaosuper@126.com',?,?,?)";
				index=random.nextInt(tempGroups.length);
				Object[] bindArgs={i+1,tempName[i],tempGroups[index],i*10+8,i*11+9,"��һ�ʲôҲ��˵...",getSysNowTime(),0};
				db.execSQL(sql, bindArgs);
			}
			
			Log.i("TAG", "create table over...");*/
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.i("TAG", "contactsmanager.db Upgrade...");
			onCreate(db);
			
		}
		
	}
	
	
	//��ͷ��ת����byte[]�Ա��ܽ�ͼƬ�浽��ݿ�
	public byte[] getBitmapByte(Bitmap bitmap){
		ByteArrayOutputStream out=new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
		try {
			out.flush();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
			Log.e(TAG, "transform byte exception");
		}
		return out.toByteArray();
	}
	
	/*
	 * 		"username TEXT  NOT NULL,"+ //����
"contactIcon BLOB,"+ //��ϵ��ͼ��
"gender INTEGER,"+ //�Ա� 1Ϊ�� 0ΪŮ
"groupName TEXT,"+ //��������
"longitude FLOAT,"+ //����
"latitude FLOAT,"+ //γ��
"email TEXT NOT NULL,"+ //����
"description TEXT NOT NULL,"+ //ǩ��
"modifyTime TEXT"+ //����ʱ��
	 */
	//table contacts
	public static final String contacts_id="_id";
	public static final String contacts_icon="contactIcon";
	public static final String contacts_name="username";
	public static final String contacts_description="description";
	public static final String contacts_email="email";
	public static final String contacts_modifyTime="modifyTime";
	public static final String contacts_groupName = "groupName";
	public static final String contacts_gender = "gender";
	public static final String contacts_longitude="longitude";
	public static final String contacts_latitude="latitude";
	public static final String contacts_status = "status";
	public static final String contacts_ownername = "ownername";
	String contactProjection[]={
				contacts_id,
				contacts_name,
				contacts_icon,
				contacts_gender,
				contacts_groupName,
				contacts_longitude,
				contacts_latitude,
				contacts_email,
				contacts_description,
				contacts_modifyTime,
				contacts_status,
				contacts_ownername
			}; 
	
	//table groups
	public static final String groups_id="_id";
	public static final String groups_groupName="groupName";
	public static final String groups_ownername = "ownername";
	String groupsProjection[]={
			groups_id,
			groups_groupName,
			groups_ownername
		};
	//message
	public static final String message_id = "_id";
	public static final String message_friendName="friendName";
	public static final String message_message = "message";
	public static final String message_time ="time";
	public static final String message_ownername = "ownername";
	private String messageProjection[]={
			message_id,
			message_friendName,
			message_message,
			message_time,
			message_ownername
	};
	
	//����������
	public Cursor getAllGroups(){
		return mSQLiteDatabase.query(
				TABLE_GROUPS, 
				groupsProjection, 
				null, null, null, null, null);
		
	}
	//�õ���������г�Ա
	public Cursor getContactsByGroupName(String groupName){
		if(UserManager.getGlobaluser()==null)
			return null;
		return mSQLiteDatabase.query(
				TABLE_CONTACTS, 
				contactProjection, 
				"groupName='"+groupName+"' AND ownername='"+UserManager.getGlobaluser().getUsername()+"'", 
				null, null, null, null);
	}
	//ͳ�Ƹ��������
	public int getCountContactByGroupName(String groupName){
		int count=0;
		String sql="select count(*) from contacts where groupName='"+groupName+"' AND ownername='"+UserManager.getGlobaluser().getUsername()+"'";
		Cursor cursor=mSQLiteDatabase.rawQuery(sql, null);
		if(cursor.moveToFirst()){
			count=cursor.getInt(0);
		}
		cursor.close();
		return count;
	}
	//ͳ�Ƹ�����������
	public int getOnlineCountContactByGroupName(String groupName){
		if(UserManager.getGlobaluser()==null)
			return 0;
		int count = 0;
		String sql = "select count(*) from contacts where groupName='"+groupName+"' AND status=1 AND ownername='"+UserManager.getGlobaluser().getUsername()+"'";
		Cursor cursor = mSQLiteDatabase.rawQuery(sql, null);
		if(cursor.moveToFirst())
		{
			count = cursor.getInt(0);
		}
		cursor.close();
		return count;
	}
	
	//ͬ������contacts��groupName�ֶ���Ϣ
	public void updateSyncData(String sql,Object[] Args){
		mSQLiteDatabase.execSQL(sql, Args);
	}
	
	//��ѯ��ϵ�����ĸ���
	public String checkContactGroup(String sql,String selectionArgs[]){
		String groupName="";
		Cursor cursor=mSQLiteDatabase.rawQuery(sql, selectionArgs);
		if(cursor.moveToFirst()){
			groupName=cursor.getString(0);
		}
		cursor.close();
		return groupName;
	}
	
	//��ѯ
	public Cursor getCursorBySql(String sql,String selectionArgs[]){
		return mSQLiteDatabase.rawQuery(sql, selectionArgs);
	}
	
	//���һ����
	public long inserDataToGroups(String groupName){
		
		String formatTime=getSysNowTime();
		ContentValues content=new ContentValues();
		content.put(groups_groupName, groupName);
		content.put("createTime", formatTime);
		content.put("modifyTime", formatTime);
		content.put("ownername", UserManager.getGlobaluser().getUsername());
		return mSQLiteDatabase.insert(TABLE_GROUPS, null, content);
		
	}
	
	//ɾ��һ����
	public int deleteDataFromGroups(String groupName){
		return mSQLiteDatabase.delete(TABLE_GROUPS, "groupName='"+groupName+"' AND ownername='"+UserManager.getGlobaluser().getUsername()+"'", null);
	}
	
	//����һ����
	public int updateDataToGroups(String newgroupName,String oldgroupName){
		String formatTime=getSysNowTime();
		ContentValues content=new ContentValues();
		content.put(groups_groupName, newgroupName);
		content.put("modifyTime", formatTime);
		return mSQLiteDatabase.update(TABLE_GROUPS, content, "groupName='"+oldgroupName+"' ownername='"+UserManager.getGlobaluser().getUsername()+"'", null);
	}
	//���һ����Ϣ
	public long insertDataToMessage(String friName,String message,String stime)
	{
		ContentValues content = new ContentValues();
		content.put(message_friendName,friName);
		content.put(message_message, message);
		content.put(message_time, stime);
		content.put("ownername", UserManager.getGlobaluser().getUsername());
		return mSQLiteDatabase.insert(TABLEMESSAGE, null, content);
	}
	//��ȡ������Ϣ
	public String[] getMessageByFriendName(String friName)
	{
		
		String sql = "select * from message where friendName='"+friName+"' AND ownername='"+UserManager.getGlobaluser().getUsername()+"'";
		Cursor cursor = mSQLiteDatabase.rawQuery(sql, messageProjection);
		int total = cursor.getColumnCount();
		String[] res = new String[total] ;
		cursor.moveToPrevious();
		int i=0;
		while(cursor.moveToNext())
		{
			res[i]=cursor.getString(2);
			i++;
		}
		return res;
	}
	
	
	//���һ����ϵ��
	public long insertDataToContacts(User contactInfo){
			ContentValues content=new ContentValues();
			content.put("username", contactInfo.getUsername());
			content.put("gender", contactInfo.getGender());
			content.put("latitude", contactInfo.getLocX());
			content.put("longitude", contactInfo.getLocY());
			content.put("email", contactInfo.getEmail());
			content.put("contactIcon", contactInfo.getContactIcon());
			content.put("description", contactInfo.getDescription());
			content.put("groupName", contactInfo.getGroupName());
			content.put("modifyTime", contactInfo.getLoctime());
			content.put("status",contactInfo.getStatus()==userStatus.ONLINE?1:0);
			content.put("ownername", UserManager.getGlobaluser().getUsername());
			if(isContactExist(contactInfo.getUsername()))
			{
				Log.d("ContactsManagerDbAdater", "update user data:"+contactInfo.getUsername());
				return mSQLiteDatabase.update(TABLE_CONTACTS, content, "username=? AND ownername=?", new String[]{contactInfo.getUsername(),UserManager.getGlobaluser().getUsername()});
			}
			else 
			{
				Log.d("ContactsManagerDbAdater", "insert user data:"+contactInfo.getUsername());
				return mSQLiteDatabase.insert(TABLE_CONTACTS, null, content);
			}
			
		}
	//�����û��Ƿ����
	public boolean isContactExist(String name){
		Cursor cursor = mSQLiteDatabase.rawQuery("select count(*) from contacts where username='"+name+"' AND ownername='"+UserManager.getGlobaluser().getUsername()+"'", null);
	    boolean res = false;
	    int cnt = 0;
	    if(cursor.moveToFirst())
	    {
	    	cnt = cursor.getInt(0);
	    	Log.d("ContactsManagerDbAdapter", "count is "+cnt);
	    	if(cnt>0)
	    	    res = true;
	    }
	    cursor.close();
	    return res;
	}
		
	//ɾ��һ����ϵ��
	public int deleteDataFromContacts(String name){
		return mSQLiteDatabase.delete(TABLE_CONTACTS, "username='"+name+"' AND ownername='"+UserManager.getGlobaluser().getUsername()+"'", null);
	}
	
	//������ϵ��
	/**
	 * 
	 * contactInfo:�û����±༭����ϵ����Ϣ
	 * name:�༭�����ĸ���ϵ��
	 */
	public int updateDataToContacts(User contactInfo,String name){
		ContentValues content=new ContentValues();
		content.put("username", contactInfo.getUsername());
		content.put("gender", contactInfo.getGender());
		content.put("latitude", contactInfo.getLocX());
		content.put("longitude", contactInfo.getLocY());
		content.put("email", contactInfo.getEmail());
		content.put("contactIcon", contactInfo.getContactIcon());
		content.put("description", contactInfo.getDescription());
		content.put("groupName", contactInfo.getGroupName());
		content.put("modifyTime", contactInfo.getLoctime());
		content.put("status",contactInfo.getStatus()==userStatus.ONLINE?1:0);
		System.out.println("update success");
		System.out.println(name);
		return mSQLiteDatabase.update(TABLE_CONTACTS, content, "username=? AND ownername=?", new String[]{name,UserManager.getGlobaluser().getUsername()});
	}
	
	//get sysTime
	static public String getSysNowTime(){
		Date now=new Date();
		java.text.DateFormat format = new java.text.SimpleDateFormat("yyyy-MM-dd HH:MM:ss");
		String formatTime=format.format(now);
		return formatTime;
	}
}
