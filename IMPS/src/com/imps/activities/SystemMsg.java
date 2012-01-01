package com.imps.activities;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.imps.R;
import com.imps.handler.UserManager;

public class SystemMsg extends Activity{
	
	private ListView infoList;
	private static ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String,Object>>();
	private String friName;
	private String msg;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sysmsg);
		infoList = (ListView)findViewById(R.id.systemMsg);		
	    SimpleAdapter listItemAdapter = new SimpleAdapter(this, listItem, R.layout.messagelist, 
			new String[]{"date", "message"}, new int[]{R.id.dateText, R.id.messageText});
	    infoList.setAdapter(listItemAdapter);  
		Intent i = this.getIntent();
		if(i!=null)
		{
			String action = i.getAction();
			System.out.println(" action is "+action);
	          if("add_fri_rsq".equals(action))
	          {
	        	  friName = i.getStringExtra("fUsername");
	        	  int result =i.getIntExtra("result",0);
	        	  Date now = new Date(); 
	        	  SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	        	  if(result==1)
	        	  {
	        		  String stime = dt.format(now);
	        		  String msg = friName+ " has accepted your request to be your friend." ;
	        		  appendToList(msg,stime);
	        	  }
	        	  else if(result == 0)
	        	  {
	        		  String stime = dt.format(now);
	        		  String msg = friName+"  has denied your request to be your friend.";
	        		  appendToList(msg,stime);
	        	  }
	        	  
	          }
	          else if("add_fri_req".equals(action))
	          {
	        	  friName = i.getStringExtra("fUsername");
	        	  Date now = new Date();
	        	  SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	        	  String stime = dt.format(now);
	        	  String msg = friName + " wants to make friends with you.";
	        	  appendToList(msg,stime);
	        	  showDialog(1);
	          }
		}

	}
	@Override
	public void onPause()
	{
		super.onPause();
		unregisterReceiver(messagereceiver);
	}
	@Override
	public void onResume()
	{
		super.onResume();
		IntentFilter ifilter = new IntentFilter();
		ifilter.addAction("add_fri_req");
		ifilter.addAction("add_fri_rsp");
		ifilter.addAction("exit");
		registerReceiver(messagereceiver,ifilter);
	}
	/**
	 * append message to the list view and update it
	 * @param name
	 * @param msg
	 * @param stime
	 */
	public void appendToList(String msg,String stime)
	{
		String text = "System" +" : "+msg;
		HashMap<String, Object> map = new HashMap<String, Object>();  
		map.put("date", stime);
		map.put("message", text);
		listItem.add(map);
		SimpleAdapter listItemAdapter = (SimpleAdapter)infoList.getAdapter();
		listItemAdapter.notifyDataSetChanged();
	}
	
	public class MessageReceiver extends BroadcastReceiver{
		@Override
		public void onReceive(Context arg0, Intent arg1) {
			// TODO Auto-generated method stub
          String action = arg1.getAction();
          System.out.println("self broadcast has been received!action is"+ action);
          if("add_fri_rsp".equals(action))
          {
        	  friName = arg1.getStringExtra("fUsername");
        	  int result =arg1.getIntExtra("result",0);
        	  Date now = new Date(); 
        	  SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        	  if(result==1)
        	  {
        		  String stime = dt.format(now);
        		  String msg = friName+ " has accepted your request to be your friend." ;
        		  appendToList(msg,stime);
        	  }
        	  else if(result == 0)
        	  {
        		  String stime = dt.format(now);
        		  String msg = friName+"  has denied your request to be your friend.";
        		  appendToList(msg,stime);
        	  }
        	  
          }
          else if("add_fri_req".equals(action))
          {
        	  friName = arg1.getStringExtra("fUsername");
        	  Date now = new Date();
        	  SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        	  String stime = dt.format(now);
        	  String msg = friName + " wants to make friends with you.";
        	  appendToList(msg,stime);
        	  showDialog(1);
          }
          else if("exit".equals(action))
          {
        	  finish();
          }
	    }
	}
	private MessageReceiver messagereceiver = new MessageReceiver();
	
	 protected Dialog onCreateDialog(int id){
	    	Dialog dialog = null;
	    	Builder b = new AlertDialog.Builder(this);

	    	switch (id){
	    	//add friend request
	    	case 1:
	    		b.setTitle("Add new friend");
	    		b.setMessage(msg);
	    		b.setPositiveButton(R.string.accept, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
				    	Date now = new Date();
				      	  SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				      	  String stime = dt.format(now);
						UserManager.getInstance().AddFriRsp(friName, true);
						appendToList("You have accepted "+friName+" as your new friend",stime);
						//请求好友列表
						UserManager.getInstance().SendFriListReq();
					}
				}).setNegativeButton(R.string.deny, new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
				    	Date now = new Date();
				      	  SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				      	  String stime = dt.format(now);
						UserManager.getInstance().AddFriRsp(friName, false);
						appendToList("You have denied "+friName+" as your new friend",stime);
					}
				});
	    		dialog = b.create();
	    		break;
	    	}
	    	return dialog;
	 }
}
