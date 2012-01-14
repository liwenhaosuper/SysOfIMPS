package com.imps.server.handler;

import java.sql.SQLException;
import java.util.TimerTask;

import com.imps.server.base.User;
import com.imps.server.base.userStatus;
import com.imps.server.main.ServerBoot;


public class RegularUserCheck extends TimerTask{
	public static final long OVERTIME = 5 * 60 * 1000;   //3分钟
	
	private User user;
	
	public RegularUserCheck(User user) {
		this.user = user;
	}
	@Override
	public void run() {
		synchronized (user) {
			User tuser = null;
			try {
				tuser = UserManager.getInstance().getUser(user.getUsername());
				Thread.sleep(500);
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				if(tuser==null||System.currentTimeMillis() - tuser.lastAccessTime > OVERTIME||
						!UserManager.getInstance().userMap.containsKey(tuser.getUsername())||
						tuser.getSessionId()==-1||ServerBoot.server.getIoSession(tuser.getSessionId())==null) {
					tuser.setStatus(userStatus.OFFLINE);
					this.cancel();
					try {
					    UserManager manager = UserManager.getInstance();
					    User iusr = manager.getUser(tuser.getUsername());
					    manager.deleteUser(iusr);
						User[] friends = tuser.getOnlineFriendList();
						//通知所有朋友不在线了
						if(friends==null)
							return ;
						for(int i=0;i<friends.length;i++)
						{
						     manager.updateUserStatus(tuser);
						}
						System.out.println("check ends:"+tuser.getUsername());
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}


