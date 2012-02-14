
/*
 * Author: liwenhaosuper
 * Date: 2011/5/19
 * Description:
 *     user manager class method
 */




package com.imps.server.manager;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.concurrent.ConcurrentHashMap;

import com.imps.server.db.ConnectDB;
import com.imps.server.main.basetype.NetAddress;
import com.imps.server.main.basetype.User;
import com.imps.server.main.basetype.location;
import com.imps.server.main.basetype.userStatus;



public class UserManager {

	private static UserManager instance;
	
	private Timer timer = new Timer(true);
	
	public ConcurrentHashMap<String, User> userMap = new ConcurrentHashMap<String, User>();

	//<username,message>
	//public ConcurrentHashMap<String, Vector<OutputMessage>> UserDatas = 
	//	new ConcurrentHashMap<String, Vector<OutputMessage>>();
	public ConcurrentHashMap<String,NetAddress> userAddress = new ConcurrentHashMap<String,NetAddress>();
	public ConnectDB db;
	
	public UserManager() throws SQLException
	{
		try{
		    db = new ConnectDB();
		}catch(Exception e)
		{
			System.out.println("ConnectDB 初始化失败");
			e.printStackTrace();
		}
		
	}
	
	public Timer getTimer() {
		return timer;
	}


	/**
	 * @param instance the instance to set
	 */
	public static void setInstance(UserManager instance) {
		UserManager.instance = instance;
	}


	/**
	 * @return the instance
	 * @throws SQLException 
	 */
	public static UserManager getInstance() throws SQLException {
		if(instance==null)
			instance = new UserManager();
		return instance;
	}


	/**
	 * @param userMap the userMap to set
	 */
	public void setUserMap(ConcurrentHashMap<String, User> userMap) {
		this.userMap = userMap;
	}
	public int addUserAddress(String name,NetAddress addr){
		if(name.equals("")){
			throw new NullPointerException("user name is null");
		}
		if(userMap.containsKey(name)){
			userAddress.put(name, addr);
			return 1;
		}
		return 0;
	}
	public void deleteUserAddress(String name){
		if(userAddress.containsKey(name)){
			userAddress.remove(name);
		}
	}
	public User getUserBySessionId(Integer integer){
		Iterator iter = userMap.entrySet().iterator();
		while(iter.hasNext()){
			Map.Entry entity = (Map.Entry)iter.next();
			if(((User)entity.getValue()).getSessionId().equals(integer)){
				return ((User)entity.getValue());
			}
		}
		return null;
	}

	/**
	 * @return the userMap
	 */
	public ConcurrentHashMap<String, User> getUserMap() {
		return userMap;
	}


/*	*//**
	 * @param userDatas the userDatas to set
	 *//*
	public void setUserDatas(ConcurrentHashMap<String, Vector<OutputMessage>> userDatas) {
		UserDatas = userDatas;
	}


	*//**
	 * @return the userDatas
	 *//*
	public ConcurrentHashMap<String, Vector<OutputMessage>> getUserDatas() {
		return UserDatas;
	}*/
	
	/**
	 * get user via username from the usermap
	 */
	public User getUser(String name)
	{
		return userMap.get(name);
	}
	
/*	*//**
	 * get user data via username
	 *//*
	public Vector<OutputMessage> getUserData(String username)
	{
		Vector<OutputMessage> vector = UserDatas.get(username);
		if(vector == null) {
			UserDatas.putIfAbsent(username, new Vector<OutputMessage>());
		}
		
		vector = UserDatas.get(username);
		
		return vector;
	}*/
	/**
	 * get a user's status
	 */
	public byte getUserStatus(String username)
	{
		if(userMap.containsKey(username))
			return userStatus.ONLINE;
		return userStatus.OFFLINE;
	}
	
	/**
	 * add a new user
	 * session id should be included
	 */
	public int addUser(User user) {
		if(user == null) {
			throw new NullPointerException("user is null");
		}
		if(user.getSessionId()==-1)
			return 0;
		
		User returnUser = userMap.putIfAbsent(user.getUsername(), user);
		
		//update status
		updateUserStatus(user);
		
		if(returnUser == null) {
			return 1;
		}
		else{
			return 0;
		}
	}
	/**
	 * delete a user from the userlist
	 */
	public void deleteUser(User user)
	{
		if(this.getUser(user.getUsername())==null)
			return;
		userMap.remove(user.getUsername());
		//UserDatas.remove(user.getUsername());
		System.out.println(user.getUsername()+" has been removed from the online user list!");
	}
	
	/**
	 * update a user's status
	 * if a user is not in the user list, it will
	 * go the the database to find the user and add the user to the list 
	 */
	public void updateUserStatus(User user)
	{
		if(this.getUser(user.getUsername())==null)
		{
			try {
				User newuser = this.getUserFromDB(user.getUsername());
				if(newuser!=null)
				{
					newuser.setStatus(user.getStatus());
					this.addUser(newuser);
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else
		{
			if(user.getStatus()==userStatus.OFFLINE)
				userMap.remove(user.getUsername());
			else userMap.put(user.getUsername(), user);
		}
		User[] onlinefri = getOnlineFriendlist(user.getUsername());
		if(onlinefri==null)
			return;
		for(int  i= 0 ;i<onlinefri.length;i++)
		{
/*			IoSession session = ServerBoot.server.getIoSession(onlinefri[i].getSessionId());
			if(session == null || session.isCloseing()){
				continue;
			}
			else
			{
			   OutputMessage msg = MessageFactory.createOnlineStatusNotify(user.getUsername(), user.getStatus());
			   session.write(msg);
			}*/
		}
	}
	
	/**
	 * get the user's online friendlist 
	 * @throws SQLException 
	 */
	public User[] getOnlineFriendlist(String username) 
	{
		User[] friends = null;
		ArrayList<User> map = new ArrayList<User>();
		try {
			 friends= getFriendlistFromDB(username);
			 if(friends==null)
				 return null;
			 for(int j=0;j<friends.length;j++)
			 {
				if(userMap!=null&&userMap.containsKey(friends[j].getUsername()))
					map.add(userMap.get(friends[j].getUsername()));
			 }	
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		User[] users = new User[map.size()];
		for(int i = 0;i<map.size();i++)
		{
			users[i] = map.get(i);
		}
		return users;
	}
	
	public User[] getFriendlistInStatus(String username)
	{
		User[] friends = null;
		ArrayList<User> map = new ArrayList<User>();
		try {
			 friends= getFriendlistFromDB(username);
			 for(int j=0;j<friends.length;j++)
			 {
				if(userMap.containsKey(friends[j].getUsername()))
					map.add(userMap.get(friends[j].getUsername()));
				else {
					friends[j].setStatus(userStatus.OFFLINE);
					map.add(friends[j]);
				}
			 }	
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		User[] users = new User[map.size()];
		for(int i = 0;i<map.size();i++)
		{
			users[i] = map.get(i);
		}
		return users;	
	}
	
	public List <String> getSearchFriendResult(String keyword) throws SQLException{
		List <String> userNames = new ArrayList <String>();
		ConnectDB tdb = new ConnectDB();
		ResultSet res = tdb.executeSQL("select * from user where username like '%"+keyword+"%'");
		res.last();
		int row = res.getRow();
		if(row ==0)
			return null;
		res.beforeFirst();
		while(res.isClosed()==false&&res.next())
		{
			userNames.add(res.getString("username"));
		}
		return userNames;
	}
	
	/**
	 * get the user's location
	 * @param user
	 * @param friendname
	 */
	public location getFriendLocation(String friendname)
	{
		location loc = new location();
		try {
			loc = this.getLocation(friendname);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//TODO: send message
		return loc;
	}
	
	/**
	 * 
	 *       Database Handler 
	 * 
	 *       all methods that are relative to the database should be implemented in the following area
	 * @throws SQLException 
	 * 
	 * 
	 */
	
	public boolean checkUser(String username,String pwd) throws SQLException
	{
		User user = getUserFromDB(username);
		if(user==null)
			return false;
		if(!user.getPassword().equals(pwd))
			return false;
		return true;
	}
	
	
	
	/**
	 * add a user to the database
	 * @throws SQLException 
	 */
	public boolean registerUser(User user) throws SQLException
	{
		boolean flag = false;
		ConnectDB tdb = new ConnectDB();
		try{
			ResultSet res = null;
			res = tdb.executeSQL("select * from user where username = '"+user.getUsername()+"'");
			if(res.next())
				flag = false;
			else{
				String gender = "'M'";
				if(user.getGender()==0)
					gender = "'F'";
			    int re = tdb.executeUpdate("insert into user(username,password,email,gender) values("+"'"+user.getUsername()+"','"+
					    user.getPassword()+"','"+user.getEmail()+"',"+gender+")");
			    if(re!=0) flag=true;
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return flag;
	}
	
	/**
	 * get a user from the database via username
	 * @throws SQLException 
	 */
	public User getUserFromDB(String username) throws SQLException
	{
		User user = new User();
		
		ResultSet res =null;
		ConnectDB tdb = new ConnectDB();
		res = tdb.executeSQL("select * from user where username ='"+username+"'");
		if(res!=null&&res.next())
		{
			user.setUserid(res.getLong("userid"));
			user.setUsername(res.getString("username"));
			user.setPassword(res.getString("password"));
			user.setGender(res.getString("gender").equals("M")?1:0);
			user.setEmail(res.getString("email"));
			return user;
		}
		return null;
	}
	
	/**
	 * get a user from the database via userid
	 */
	public User getUserFromDB(long userid) throws SQLException
	{
		User user = new User();
		
		ResultSet res =null;
		ConnectDB tdb = new ConnectDB();
		res = tdb.executeSQL("select * from user where userid ="+userid);
		if(res.next())
		{
			user.setUserid(res.getLong("userid"));
			user.setUsername(res.getString("username"));
			user.setPassword(res.getString("password"));
			user.setGender(res.getString("gender").equals("M")?1:0);
			user.setEmail(res.getString("email"));
			return user;
		}
		
		return null;
	}
	
	/**
	 * get friendlist from database via username
	 * @throws SQLException 
	 */
	public User[] getFriendlistFromDB(String username) throws SQLException
	{
		User user = getUserFromDB(username);
		if(user==null)
			return null;
		ConnectDB tdb = new ConnectDB();
		ResultSet res = tdb.executeSQL("select distinct * from friend where userid="+user.getUserid()+" or use_userid="+user.getUserid());
		
		res.last();
		int row = res.getRow();
		if(row ==0)
			return null;
		User[] users = new User[row];
		
		res.beforeFirst();
		int index = 0;
		while(res.isClosed()==false&&res.next())
		{
			if(res.getLong("userid")==user.getUserid())
			{
				User demo = getUserFromDB(res.getLong("use_userid"));
				users[index++] = demo; 
			}
			else if(res.getLong("use_userid")==user.getUserid())
			{
				User demo = getUserFromDB(res.getLong("userid"));
				users[index++] = demo;
			}
		}
		return users;
	}
	
	
	/**
	 * add a friend to the user's friendlist
	 * @throws SQLException 
	 */
	public boolean addFriendToDB(String username,String friendname) throws SQLException
	{
		User u1 = getUserFromDB(username);
		User u2 = getUserFromDB(friendname);
		if(u1==null||u2==null)
		{
			return false;
		}
		User[] friends = getFriendlistFromDB(username);
		if(friends!=null)
		{
			for(int i=0;i<friends.length;i++)
			{
				if(friends[i].getUsername().equals(friendname))
					return false;
			}
		}
		ConnectDB tdb = new ConnectDB();
		tdb.executeUpdate("insert into friend(userid,use_userid) values("+u1.getUserid()+","+u2.getUserid()+")");
		
		return true;
	}
	
	public void updateLocation(String username,String p_time,double x,double y) throws SQLException
	{
		User user = getUserFromDB(username);
		if(user == null)
			return;
		ConnectDB tdb = new ConnectDB();
		tdb.executeUpdate("insert into position (userid,p_time,x_location,y_location)values("+user.getUserid()+",'"+p_time+"',"+x+","+y+")");
		
	}
	
	public location getLocation(String username) throws SQLException
	{
		String p_time="";
		double x=0;
		double y=0;
		User user = getUserFromDB(username);
		if(user ==null)
			return null;
		ConnectDB tdb = new ConnectDB();
		ResultSet res = tdb.executeSQL("select * from position where userid="+user.getUserid()+" order by p_time desc limit 1");
		if(res.next())
		{
			p_time = res.getString("p_time");
			x = res.getFloat("x_location");
			y = res.getFloat("y_location");
		}
		location loc=new location();
		loc.ptime = p_time;loc.x=x;loc.y=y;
		return loc;
	}
	
	public boolean addMessage(String username,String friendname,String m_time,String msg) throws SQLException
	{
		User u1 = getUserFromDB(username);
		User u2 = getUserFromDB(friendname);
		if(u1==null||u2==null)
		{
			return false;
		}
		ConnectDB tdb = new ConnectDB();
		tdb.executeUpdate("insert into message(userid,use_userid,m_time,message) values("+u1.getUserid()+","+u2.getUserid()
				+",'"+m_time+"','"+msg+"')");
		return true;
	}
	
	
	
	
}
