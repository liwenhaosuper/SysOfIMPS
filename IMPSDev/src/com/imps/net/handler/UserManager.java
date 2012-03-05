package com.imps.net.handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;

import android.content.Context;

import com.imps.IMPSDev;
import com.imps.basetypes.MediaType;
import com.imps.basetypes.SystemMsgType;
import com.imps.basetypes.User;
import com.imps.basetypes.UserStatus;
import com.imps.util.LocalDBHelper;

public class UserManager {
	private static String TAG = UserManager.class.getCanonicalName();
	private static boolean DEBUG = IMPSDev.isDEBUG();
	
	public static User globaluser = new User();
	private static UserManager instance;
	private static Timer timer = new Timer(true);
	public static List<User> AllFriList = new ArrayList<User>();
	public static HashMap<String,List<MediaType> > CurSessionFriList = new HashMap<String, List<MediaType>>();
	public static HashMap<String,List<MediaType>> UnReadMessages = new HashMap<String,List<MediaType>>();
	public static List<SystemMsgType> mSysMsgs = new ArrayList<SystemMsgType>();
	public static String activeFriend = new String();
	public static LocalDBHelper localDB; 
	
	public static UserManager getInstance(){
		if(instance==null){
			instance = new UserManager();
		}
		return instance;
	}
	
	public static void setInstance(UserManager instance) {
		UserManager.instance = instance;
	}
	public static void setGlobaluser(User globaluser) {
		UserManager.globaluser = globaluser;
	}

	public static User getGlobaluser() {
		return globaluser;
	}
	public User[] getOnlineFriendlist()
	{
		int frilen = 0;
		if(AllFriList==null){
			return null;
		}
        for(int i=0;i<AllFriList.size();i++)
        {
        	if(AllFriList.get(i).getStatus()==UserStatus.ONLINE)
        	{
        		frilen++;
        	}
        }
        User[] res = new User[frilen];
        frilen = 0;
        for(int i=0;i<AllFriList.size();i++){
        	if(AllFriList.get(i).getStatus()==UserStatus.ONLINE)
        	{
        		res[frilen] = AllFriList.get(i);
        		frilen++;
        	}
        }
        return res;
	}
	/**
	 * delete friend from the cursessionlist
	 */
	public void deleteFriFromSession(String friName)
	{
		Iterator iter = CurSessionFriList.entrySet().iterator();
		while(iter.hasNext())
		{
			Map.Entry entity = (Map.Entry)iter.next();
			User fri = (User)entity.getKey();
			if(fri.getUsername().equals(friName))
			{
				CurSessionFriList.remove(entity.getKey());
				break;
			}
		}
	}
	public void updateUserStatus(String friName,byte status)
	{
        for(int i=0;i<AllFriList.size();i++)
        {
        	if(AllFriList.get(i).getUsername().equals(friName))
        	{
        		User fri = AllFriList.get(i);
        		fri.setStatus(status);
        		break;
        	}
        }
	}
	
	public static void buildLocalDB(Context ctx) {
		localDB = new LocalDBHelper(ctx);
	}
}
