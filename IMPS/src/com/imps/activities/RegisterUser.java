package com.imps.activities;

import java.io.IOException;
import java.net.InetSocketAddress;

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
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

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

public class RegisterUser extends Activity{
	
	private static final int USER_NAME = Menu.FIRST+1;
	private static final int VALID = Menu.FIRST+2;
	private static final int USEREXIST = Menu.FIRST+3;
	private static final int NET_ERROR = Menu.FIRST+4;
	
	private EditText usernameText;
	private EditText passwordText;
	private EditText eMailText;
	private RadioButton genderRadio;
	private EditText passwordAgainText;
	
	private static boolean flag = false;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.registeruser);
		setTitle("注册新用户");
		genderRadio = (RadioButton)findViewById(R.id.mail);
		usernameText = (EditText)findViewById(R.id.userName);
		passwordText = (EditText)findViewById(R.id.password);
		passwordAgainText = (EditText)findViewById(R.id.passwordAgain);
		eMailText = (EditText)findViewById(R.id.email);
		Button signUpButton = (Button) findViewById(R.id.signUp);
		Button cancelButton = (Button) findViewById(R.id.cancel_signUp);
		genderRadio.setOnCheckedChangeListener(new OnCheckedChangeListener(){

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
			}
			
		});
		signUpButton.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(CommonHelper.getLocalIpAddress()==null)
				{
					showDialog(NET_ERROR);
				}
				if(usernameText.length()>0&&passwordText.length()>0&&passwordAgainText.length()>0
						&&eMailText.length()>0)
				{
					if(passwordText.getText().toString().equals(passwordAgainText.getText().toString()))
					{
						 if(flag==false)
						 {
							 flag = true;
								try {
									initialConfig();
								} catch (Exception e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
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
						 User user = new User();
						 user.setUsername(usernameText.getText().toString());
						 user.setPassword(passwordText.getText().toString());
						 user.setEmail(eMailText.getText().toString());
						 user.setGender(genderRadio.isChecked()?1:0);//1为男，0为女
						 try {
							UserManager.getInstance().register(user);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					else{
						showDialog(VALID);
					}
				}
				else{
					showDialog(USER_NAME);
				}
			}			
		});
		cancelButton.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
	}
    @Override
    public void onResume()
    {
    	super.onResume();
    	IntentFilter ifilter = new IntentFilter();
    	ifilter.addAction("error_msg");
    	ifilter.addAction("register_success");
    	ifilter.addAction("exit");
    	registerReceiver(myRegisterReceiver,ifilter);
    }
    @Override
    public void onStop()
    {
    	super.onStop();
    	unregisterReceiver(myRegisterReceiver);
    }
    
    public class RegisterReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action = intent.getAction();
			if("error_msg".equals(action))
			{
				showDialog(USEREXIST);
			}
			else if("register_success".equals(action))
			{
				Toast.makeText(RegisterUser.this,"注册成功，请返回重新登录！",Toast.LENGTH_SHORT).show();
				finish();
			}
			else if("exit".equals(action))
			{
				finish();
			}
		}
    }
    private RegisterReceiver myRegisterReceiver = new RegisterReceiver();
    
    @Override
    protected Dialog onCreateDialog(int id){
    	Dialog dialog = null;
    	Builder b = new AlertDialog.Builder(this);
    	switch (id){
    	case USER_NAME:
    		b.setTitle("注册错误").setIcon(R.drawable.icon);
    		b.setMessage("用户名不能为空");
    		b.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub				
				}
			});
    		dialog = b.create();
    		break;
    	case VALID:
    		b.setTitle("注册错误").setIcon(R.drawable.icon);
    		b.setMessage("两次输入的密码不一致");
    		b.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub					
				}
			});
    		dialog = b.create();
    		break;
    	case USEREXIST:
    		b.setTitle("注册错误").setIcon(R.drawable.icon);
    		b.setMessage("用户名已经存在,请重新选择用户名");
    		b.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
				}
			});
    		dialog = b.create();
    		break;
    	case NET_ERROR:
    		b.setTitle("网络错误").setIcon(R.drawable.icon);
    		b.setMessage("您当前网络不佳，请检查网络并稍后重试");
    		b.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					
				}
			});
    		dialog = b.create();   	
    		break;
    	default:
    		break;
    	}
    	return dialog;
    }  
    
    public void initialConfig() throws Exception
    {
    	Configure config = new Configure();
        /**
         * 127.0.0.1换成响应的服务器IP
         * 1200为对应的端口号
         */
        config.setAddress(new InetSocketAddress("192.168.0.102",1200));
        config.setProtocolHandler(new NetProtocolHandler());
        config.setIoHandler(new LogicHandler());
        Client.connector = new IoConnector();
        config.start(Client.connector);
        Client.session = IoConnector.newSession(Client.connector);
        Client.future  = Client.session.connect();
        Client.future.await();
        Client.usermanager = UserManager.getInstance();
		//start service
		startService(new Intent(RegisterUser.this,IMService.class));
		System.out.println("net config has finished");
    }
    
    public void stopConfig()
    {
    	if(Client.session!=null&&!Client.session.isClose())
    	{
    		 Client.session.close();
    	}
    	stopService(new Intent(RegisterUser.this,IMService.class));	    
    }
    
    @Override
    public void onDestroy()
    {
    	super.onDestroy();
    	stopConfig();
    }
}
