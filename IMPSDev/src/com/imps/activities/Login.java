package com.imps.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
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
import com.imps.basetypes.UserStatus;
import com.imps.net.handler.UserManager;
import com.imps.receivers.IMPSBroadcastReceiver;
import com.imps.services.impl.ConfigurationService;
import com.imps.services.impl.ServiceManager;
/**
 * Copyright (C) 2010-2020 IMPS Development Team
 * @author liwenhaosuper
 *
 */
public class Login extends Activity{
	
	private static String TAG = Login.class.getCanonicalName();
	private static boolean DEBUG = IMPSDev.isDEBUG();
	private EditText account;
	private EditText pwd;
	private Button mLogin;
	private Button mRegister;
	private CheckBox mRemPwd;
	private CheckBox mAutoLogin;
	private ProgressDialog pd;
	private String username;
	private String password;
	
	private static final int ALERT_DIALOG = 1;
	private static final int ERROR = Menu.FIRST+10;
	private static final int NET_ERROR = Menu.FIRST+4;
	
	private String errorMsg ="";
	private ShowProgress executor;

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.login);
		ensureUI();
		processClick();
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
	public void ensureUI(){
        pd = new ProgressDialog(Login.this);
		pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);  
		pd.setMessage(getResources().getString(R.string.login_loading)); 
		pd.setIndeterminate(false); 
		pd.setCancelable(true);
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
	}
	public void startLogin(){
		//for test purpose
		if(!ServiceManager.getmNet().isAvailable()){
			showDialog(NET_ERROR);
			return;
		}
		username = account.getText().toString();
		password = pwd.getText().toString();
		if(username==null||password==null||"".equals(username)||"".equals(password)){
			return;
		}
		User user = new User();
		user.setUsername(username);
		user.setPassword(password);
		UserManager.setGlobaluser(user);
		if(executor!=null){
			executor.cancel(true);
		}
		executor = new ShowProgress();
		executor.execute();
		
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
		registerReceiver(loginReceiver,loginReceiver.getFilter());
	}
	public class ShowProgress extends AsyncTask<String,String,String>{

		@Override
		protected String doInBackground(String... params) {
			//For DEBUG purpose
			ServiceManager.getmAccount().login(username, password);
			/*for(int i=0;i<10;i++){
				User user = new User();
				user.setUsername("I am tester"+i);
				user.setDescription("test"+i+"号");
				user.setEmail("li"+i);
				user.setGender(i%2);
				user.setLoctime("2012-12-23");
				user.setLocX(100.5/(i+1));
				user.setLocY(80.0/(i+1));
				user.setStatus(i%2==0?UserStatus.OFFLINE:UserStatus.ONLINE);
				UserManager.AllFriList.add(user);
			}
			User user = new User();
			user.setUsername("li");
			user.setPassword("li");
			user.setEmail("li");
			user.setGender(1);
			user.setLoctime("2012-12-23");
			user.setStatus(UserStatus.OFFLINE);
			UserManager.AllFriList.add(user);
			User user1 = new User();
			user1.setUsername("lili");
			user1.setPassword("lili");
			user1.setEmail("lili");
			user1.setGender(1);
			user1.setLoctime("2012-12-23");
			user1.setStatus(UserStatus.OFFLINE);
			UserManager.AllFriList.add(user1);*/
			return null;
		}
		@Override
		protected void onPreExecute(){
			if(!pd.isShowing()){
				pd.show();	
			}
		}
		@Override
		protected void onPostExecute(String result){
			if(pd.isShowing()){
				pd.dismiss();	
			}
			//for DEBUG purpose
/*			ServiceManager.getmTcpConn().stopTcp();
			Intent start = new Intent(Login.this,IMPSContainer.class);
			start.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(start);
			finish();*/
		}
		@Override
		protected void onProgressUpdate(String... params){
			
		}
		@Override
		protected void onCancelled(){
			if(pd.isShowing()){
				pd.dismiss();	
			}
		}
		
	}
	@Override
	public void onStop(){
		super.onStop();
		if(executor!=null&&!executor.isCancelled()){
			executor.cancel(true);
		}
		unregisterReceiver(loginReceiver);
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
	   private LoginReceiver loginReceiver = new LoginReceiver();
	   
	   public class LoginReceiver extends IMPSBroadcastReceiver{

			@Override
			public void onReceive(Context context, Intent intent) {
				super.onReceive(context, intent);
				if(intent.getAction().equals(Constant.LOGINERROR)){
					errorMsg = intent.getStringExtra(Constant.LOGINERRORMSG);
					if(executor!=null&&!executor.isCancelled())
					{
						executor.cancel(true);
					}
					showDialog(ERROR);
					
				}else if(intent.getAction().equals(Constant.LOGINSUCCESS)){
					if(DEBUG){ Log.d(TAG, "Login success...");}
					Toast.makeText(context, context.getResources().getString(R.string.login_success), Toast.LENGTH_LONG).show();
					ServiceManager.getmContact().sendFriListReq();
					ServiceManager.getmContact().sendOfflineMsgReq();
					if(executor!=null&&!executor.isCancelled())
					{
						executor.cancel(true);
					}
					ServiceManager.getmAccount().onLoginSuccess();
					Intent start = new Intent(context,IMPSContainer.class);
					start.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					context.startActivity(start);
					finish();
				}
			}

			@Override
			public IntentFilter getFilter() {
				// TODO Auto-generated method stub
				IntentFilter filter = super.getFilter();
				filter.addAction(Constant.LOGINERROR);
				filter.addAction(Constant.LOGINSUCCESS);
				return filter;
			}
		}
	   
	   @Override
	   public boolean onKeyDown(int keyCode,KeyEvent event){
		   switch (keyCode){
			case KeyEvent.KEYCODE_BACK:
				if(executor!=null&&executor.getStatus()==AsyncTask.Status.RUNNING)
				{
					boolean res = executor.cancel(true);
					if(DEBUG) Log.d(TAG,"Cancel executor..."+res);
					return true;
				}
				ServiceManager.getmTcpConn().stopTcp();
		   }
		   return super.onKeyDown(keyCode, event);
	   }
}
