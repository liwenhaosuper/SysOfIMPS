package com.imps.activities;



import java.io.IOException;
import java.net.InetSocketAddress;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ant.liao.GifView;
import com.imps.R;
import com.imps.base.User;
import com.imps.handler.IMService;
import com.imps.handler.LogicHandler;
import com.imps.handler.NetProtocolHandler;
import com.imps.handler.UserManager;
import com.imps.main.Client;
import com.imps.util.CommonHelper;
import com.yz.net.Configure;
import com.yz.net.expand.IoConnector;

public class Login extends Activity {
    /** Called when the activity is first created. */
	
	private EditText account;
	private EditText pwd;
	private String errorMsg ="";
	private boolean flag = false;
	
	private static final int ALERT_DIALOG = 1;
	private static final int REGISTER = Menu.FIRST +1;
	private static final int EXIT_APP = Menu.FIRST+2;
	private static final int ERROR = Menu.FIRST+10;
	private static final int NET_ERROR = Menu.FIRST+4;
	
	private ProgressDialog pd;
	private GifView gv;
	
	@Override
	public void onPause(){
		super.onPause();
		unregisterReceiver(loginReceiver);
	}
	
	private boolean threadFlag = true;
	private long startTime = System.currentTimeMillis();
	private long endTime = System.currentTimeMillis();
	private Thread delayThread = new Thread(){
		
		public void run(){
			while (threadFlag){
				endTime = System.currentTimeMillis();
				if ((endTime - startTime > 10000) && pd.isShowing()){
					startTime = 0;
					endTime = 0;
					Message message = new Message();   
                    message.what = 1;   
					myHandler.sendMessage(message);
				}
			}
		}
	};
	private Handler myHandler = new Handler() {  
        public void handleMessage(Message msg) {   
             switch (msg.what) {   
             case 1:  
            	 pd.dismiss();
                 break;   
             }   
             super.handleMessage(msg);   
        }   
   };  
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.loginpage);
        
        gv = (GifView)findViewById(R.id.earth);
        gv.setGifImage(R.drawable.earth);
        
        pd = new ProgressDialog(Login.this);

		pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);  
		pd.setMessage(getResources().getString(R.string.login_loading)); 
		pd.setIndeterminate(false); 
		pd.setCancelable(false); 
        
        java.lang.System.setProperty("java.net.preferIPv6Addresses", "false");
        
        Button btn=(Button)findViewById(R.id.login_btn_login);
        delayThread.start();
        
        try {			
	        btn.setOnClickListener(new OnClickListener(){
	        	public void onClick(View v){			
	        		account = (EditText)findViewById(R.id.login_edit_account);
	        		pwd = (EditText)findViewById(R.id.login_edit_pwd);
	        		
	        		if (account.getText().toString().equals("")){
	        			showDialog(ALERT_DIALOG);
	        		}
	        		else if (pwd.getText().toString().equals("")){
	        			showDialog(ALERT_DIALOG + 1);
	        		}
	        		else if(CommonHelper.getLocalIpAddress()==null)
	        		{
	        			showDialog(NET_ERROR);
	        		}
	        		else {
	        			if(flag == false)
	        			{
	        				flag = true;
	        				try {
	        			    	initialConfig();
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
	        			}
	        			if(Client.session.isClose())
	        			{
	        				try {
								initialConfig();
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
	        			}
	        			
	        			pd.show(); 

	        			User user = new User();
	        			user.setUsername(account.getText().toString());
	        			user.setPassword(pwd.getText().toString());
	        			try {
	        				startTime = System.currentTimeMillis();
							boolean res = Client.usermanager.Login(user);
							if(!res)
							{
								
								return;
							}
						} catch (IOException e) {
							// TODO Auto-generated catch block
							Log.d("login error", e.getMessage());
						}
	        		}
	        	}
	        });
		}catch(Exception e)
		{
			Log.d("login error", e.getMessage());
		}
    }
    
    protected Dialog onCreateDialog(int id){
    	Dialog dialog = null;
    	Builder b = new AlertDialog.Builder(this);
    	switch (id){
    	case ALERT_DIALOG:
    		b.setTitle(getResources().getString(R.string.login_error)).setIcon(R.drawable.icon);
    		b.setMessage(getResources().getString(R.string.login_username_required));
    		b.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub				
				}
			});
    		dialog = b.create();
    		break;
    	case ALERT_DIALOG + 1:
    		b.setTitle(getResources().getString(R.string.login_error)).setIcon(R.drawable.icon);
    		b.setMessage(getResources().getString(R.string.login_password_required));
    		b.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub					
				}
			});
    		dialog = b.create();
    		break;
    	case ALERT_DIALOG + 2:
    		b.setTitle(getResources().getString(R.string.login_error)).setIcon(R.drawable.icon);
    		b.setMessage(getResources().getString(R.string.login_username_password_error));
    		b.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					
				}
			});
    		dialog = b.create();
    		break;
    	case NET_ERROR:
    		b.setTitle(getResources().getString(R.string.login_net_error)).setIcon(R.drawable.icon);
    		b.setMessage(getResources().getString(R.string.net_problem));
    		b.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					
				}
			});
    		dialog = b.create();   	
    		break;
    	case ERROR:
    		b.setTitle(getResources().getString(R.string.login_error)).setIcon(R.drawable.icon);
    		b.setMessage(errorMsg);
    		b.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					
				}
			});
    		dialog = b.create();   		
    	default:
    		break;
    	}
    	return dialog;
    }  
    @Override
    public void onResume()
    {
       super.onResume();
       IntentFilter ifilter = new IntentFilter();
       ifilter.addAction("error_msg");
       ifilter.addAction("login_success");
       registerReceiver(loginReceiver,ifilter);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
    	boolean result = super.onCreateOptionsMenu(menu);
    	menu.add(0,REGISTER,0,R.string.sign_up);
    	menu.add(0,EXIT_APP,0,R.string.exit);
		return result;
    	
    }
    @Override
    public boolean onMenuItemSelected(int id,MenuItem item)
    {
    	switch(item.getItemId()	)
    	{
    	case EXIT_APP:
    	{
    		Intent i = new Intent();
    		i.setAction("exit");
    		sendBroadcast(i);
    		if(Client.session!=null&&!Client.session.isClose())
    		     Client.session.close();
    		stopService(new Intent(Login.this,IMService.class));
    		finish();
    		return true;
    	}
    	case REGISTER:
    	{
    		Intent i = new Intent(Login.this,RegisterUser.class);
    		startActivity(i);
    	}
    	}
    	return super.onMenuItemSelected(id, item);
    }
    
    public class LoginReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action = intent.getAction();
			System.out.println("!!!login: get broadcast!!! action is "+action );
			pd.dismiss();
			if("error_msg".equals(action))
			{
				errorMsg = intent.getStringExtra("message");
				showDialog(ERROR);
				stopConfig();
				try {
					initialConfig();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else if("login_success".equals(action))
			{
				Toast.makeText(Login.this,getResources().getString(R.string.login_success), Toast.LENGTH_SHORT).show();
				System.out.println("!!!login_success has been received!!!");
				UserManager.getInstance().SendFriListReq();
				ComponentName cn=new ComponentName(Login.this,My_Map.class);
				Intent ti=new Intent();
				ti.setComponent(cn);
				Bundle bundle = new Bundle();
				bundle.putString("mUsername", account.getText().toString());
				ti.putExtra("username", bundle);
				startActivity(ti);
				
			}
			else if("exit".equals(action))
			{
	    		Client.session.close();
	    		stopService(new Intent(Login.this,IMService.class));
				finish();
			}
		}  	
    }
    public LoginReceiver loginReceiver = new LoginReceiver();
    

    public void initialConfig() throws Exception
    {
    	Configure config = new Configure();
        config.setAddress(new InetSocketAddress("169.254.95.183",1200));
        config.setProtocolHandler(new NetProtocolHandler());
        config.setIoHandler(new LogicHandler());
        Client.connector = new IoConnector();
        config.start(Client.connector);
        Client.session = IoConnector.newSession(Client.connector);
        Client.future  = Client.session.connect();
        Client.future.await();
        Client.usermanager = UserManager.getInstance();
		//start service
		startService(new Intent(Login.this,IMService.class));
		Log.d("Login","net config has finished");
    }
    
    public void stopConfig()
    {
    	if(!Client.session.isClose())
    	{
    		 Client.session.close();
    	}
    	stopService(new Intent(Login.this,IMService.class));	    
    }
    
    @Override
    public void onDestroy()
    {
    	threadFlag = false;
    	super.onDestroy();
    	System.exit(0);
    }
    
    
}