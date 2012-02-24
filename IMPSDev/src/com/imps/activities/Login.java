package com.imps.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.imps.IMPSDev;
import com.imps.R;
import com.imps.basetypes.Constant;
import com.imps.basetypes.User;
import com.imps.events.ILoginEvent;
import com.imps.net.handler.UserManager;
import com.imps.services.impl.ConfigurationService;
import com.imps.services.impl.ServiceManager;
/**
 * Copyright (C) 2010-2020 IMPS Development Team
 * @author liwenhaosuper
 *
 */
public class Login extends Activity implements ILoginEvent{
	
	private static String TAG = Login.class.getCanonicalName();
	private static boolean DEBUG = IMPSDev.isDEBUG();
	private static final int LOGIN_SUCCESS  = 1;
	private static final int LOGIN_NOTVALID  = 2;
	private static final int LOGIN_UNKNOWN = 3;
	private static final int LOGIN_START = 4;
	private EditText account;
	private EditText pwd;
	private Button mLogin;
	private Button mRegister;
	private CheckBox mRemPwd;
	private CheckBox mAutoLogin;
	private ProgressDialog pd;
	private String username;
	private String password;
	private Handler receiver = new Handler(){
		@Override
		public void handleMessage(Message msg){
			switch(msg.what){
			case LOGIN_SUCCESS:
				if(pd.isShowing()){
					pd.dismiss();
				}
				ServiceManager.getmContact().sendFriListReq();
				
				ServiceManager.getmContact().sendOfflineMsgReq();
				
				Toast.makeText(Login.this, getResources().getString(R.string.login_success), Toast.LENGTH_LONG);
				Intent start = new Intent(Login.this,IMPSContainer.class);
				startActivity(start);
				finish();
				break;
			case LOGIN_NOTVALID:
				if(pd.isShowing()){
					pd.dismiss();
				}
				Toast.makeText(Login.this, getResources().getString(R.string.login_username_password_error), Toast.LENGTH_LONG);
				break;
			case LOGIN_UNKNOWN:
				if(pd.isShowing()){
					pd.dismiss();
				}
				break;
			case LOGIN_START:
				//TODO:
				ServiceManager.getmAccount().login(username, password);
				break;
			}
		}
	};
	
	private static final int ALERT_DIALOG = 1;
	private static final int REGISTER = Menu.FIRST +1;
	private static final int EXIT_APP = Menu.FIRST+2;
	private static final int ERROR = Menu.FIRST+10;
	private static final int NET_ERROR = Menu.FIRST+4;
	
	private String errorMsg ="";
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
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.login);
		ServiceManager.getmNetLogic().addLoginEventHandler(this);
		ensureUI();
		processClick();
	}
	public void ensureUI(){
        pd = new ProgressDialog(Login.this);
		pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);  
		pd.setMessage(getResources().getString(R.string.login_loading)); 
		pd.setIndeterminate(false); 
		pd.setCancelable(false); 	       
        mLogin=(Button)findViewById(R.id.login_btn_login);
        mRegister = (Button)findViewById(R.id.register_btn_login);
		account = (EditText)findViewById(R.id.login_edit_account);
		pwd = (EditText)findViewById(R.id.login_edit_pwd);
		mRemPwd = (CheckBox) findViewById(R.id.login_cb_savepwd);
		mAutoLogin =(CheckBox)findViewById(R.id.login_auto);
		if(ServiceManager.getmConfig().getPreferences().getBoolean(ConfigurationService.REMEMBERPASSWORD, false)){
			mRemPwd.setChecked(true);
		}
		if(ServiceManager.getmConfig().getPreferences().getBoolean(ConfigurationService.AUTOLOGIN, false)){
			mAutoLogin.setChecked(true);
		}
	}
	public void processClick(){
		mLogin.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {	
				if(!ServiceManager.getmNet().isAvailable()){
					showDialog(NET_ERROR);
					return;
				}
				else if (account.getText().toString().equals("")){
        			showDialog(ALERT_DIALOG);
        			return;
        		}
        		else if (pwd.getText().toString().equals("")){
        			showDialog(ALERT_DIALOG + 1);
        			return;
        		}
        		startLogin();
			}});
		mRegister.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
		        Intent intent = new Intent(Login.this, Register.class);
		        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		        startActivity(intent);
			}});
		delayThread.start();
	}
	public void startLogin(){
		if(!ServiceManager.getmNet().isAvailable()){
			showDialog(NET_ERROR);
			return;
		}
		username = account.getText().toString();
		password = pwd.getText().toString();
		if(username==null||password==null||"".equals(username)||"".equals(password)){
			return;
		}
		Message msg = new Message();
		msg.what = LOGIN_START;
		receiver.sendMessage(msg);
		
		pd.show();
		startTime = System.currentTimeMillis();
		User user = new User();
		user.setUsername(username);
		user.setPassword(password);
		UserManager.setGlobaluser(user);
		Editor editor = ServiceManager.getmConfig().getPreferences().edit();
		if(mRemPwd.isChecked()){
			editor.putBoolean(ConfigurationService.REMEMBERPASSWORD, true);
			editor.putString(ConfigurationService.LoginUserName, account.getText().toString());
			editor.putString(ConfigurationService.LoginPassword, pwd.getText().toString());
			if(mAutoLogin.isChecked()){
				editor.putBoolean(ConfigurationService.AUTOLOGIN,true);
			}else{
				editor.putBoolean(ConfigurationService.AUTOLOGIN,false);
			}
		}else{
			editor.putBoolean(ConfigurationService.REMEMBERPASSWORD, true);
			editor.putString(ConfigurationService.LoginUserName, "");
			editor.putString(ConfigurationService.LoginPassword, "");
		}
		editor.commit();
	}
	@Override
	public void onResume(){
		super.onResume();
		if(!ServiceManager.getmConfig().getPreferences().getString(
				ConfigurationService.LoginPassword,"").equals("")&&
				!ServiceManager.getmConfig().getPreferences().getString(
						ConfigurationService.LoginUserName,"").equals(""))
		{
			account.setText(ServiceManager.getmConfig().getPreferences().getString(
						ConfigurationService.LoginUserName,""));
			pwd.setText(ServiceManager.getmConfig().getPreferences().getString(
					ConfigurationService.LoginPassword,""));
			if(ServiceManager.getmConfig().getPreferences().getBoolean(ConfigurationService.AUTOLOGIN, false)){
				Intent intent = this.getIntent();
				if(intent!=null){
					String action = intent.getAction();
					if(action==null||!action.equals(Constant.LOGOUT)){
						startLogin();
					}
				}
				
			}
		}
	}
	@Override
	public void onStop(){
		super.onStop();
		if(pd.isShowing()){
			pd.dismiss();
		}
	}
	   protected Dialog onCreateDialog(int id){
	    	Dialog dialog = null;
	    	Builder b = new AlertDialog.Builder(this);
	    	switch (id){
	    	case ALERT_DIALOG:
	    		b.setTitle(getResources().getString(R.string.login_error)).setIcon(R.drawable.imps);
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
	    		b.setTitle(getResources().getString(R.string.login_error)).setIcon(R.drawable.imps);
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
	    		b.setTitle(getResources().getString(R.string.login_error)).setIcon(R.drawable.imps);
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
	    		b.setTitle(getResources().getString(R.string.login_net_error)).setIcon(R.drawable.imps);
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
	    		b.setTitle(getResources().getString(R.string.login_error)).setIcon(R.drawable.imps);
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
	public void onLoginError(String errorStr, int errorCode) {
		if(DEBUG) Log.d(TAG,"FAIL");
		Message msg = new Message();
		msg.what = LOGIN_NOTVALID;
		receiver.sendMessage(msg);
		
	}
	@Override
	public void onLoginSuccess() {
		if(DEBUG) Log.d(TAG,"SUCCESS");
		Message msg = new Message();
		msg.what = LOGIN_SUCCESS;
		receiver.sendMessage(msg);
		
	}  
}
