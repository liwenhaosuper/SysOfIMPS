package com.imps.handler;

import java.sql.SQLException;
import java.util.TimerTask;

import com.imps.base.User;
import com.imps.main.Client;
import com.imps.main.HeartBeat;


public class RegularUserCheck extends TimerTask{
	public static final long OVERTIME = 1 * 60 * 1000;   //1∑÷÷”
	
	private User user;
	
	public RegularUserCheck(User user) {
		this.user = user;
	}

	@Override
	public void run() {
		if(user==null)
			return;
		synchronized (user) {
			try {
				new HeartBeat(Client.session,null).execute();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}


