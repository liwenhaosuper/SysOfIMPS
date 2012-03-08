package com.imps.activities;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.imps.IMPSActivity;
import com.imps.IMPSDev;
import com.imps.R;
import com.imps.basetypes.Constant;
import com.imps.basetypes.User;
import com.imps.receivers.IMPSBroadcastReceiver;
import com.imps.services.impl.ServiceManager;

public class Register extends IMPSActivity{
	private static String TAG = Register.class.getCanonicalName();
	private static boolean DEBUG = IMPSDev.isDEBUG();
	
	private static final int USER_NAME = Menu.FIRST+1;
	private static final int VALID = Menu.FIRST+2;
	private static final int USEREXIST = Menu.FIRST+3;
	private static final int NET_ERROR = Menu.FIRST+4;
	private static final int SUCCESS = Menu.FIRST+5;

	private EditText usernameText;
	private EditText passwordText;
	private EditText eMailText;
	private RadioButton genderRadio;
	private RadioButton gender2Radio;
	private EditText passwordAgainText;
	private Button signUpButton;
	private Button cancelButton;
	private int gender = 1;
	
	private String errorMsg = "";
	
	private ProgressDialog pd;
	private User user;
	private ShowProgress executor;
	

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register);
		setTitle(R.string.sign_up);
		genderRadio = (RadioButton)findViewById(R.id.mail);
		gender2Radio = (RadioButton)findViewById(R.id.femail);
		usernameText = (EditText)findViewById(R.id.userName);
		passwordText = (EditText)findViewById(R.id.password);
		passwordAgainText = (EditText)findViewById(R.id.passwordAgain);
		eMailText = (EditText)findViewById(R.id.email);
		signUpButton = (Button) findViewById(R.id.signUp);
		cancelButton = (Button) findViewById(R.id.cancel_signUp);
		
        pd = new ProgressDialog(Register.this);
		pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);  
		pd.setMessage(getResources().getString(R.string.login_loading)); 
		pd.setIndeterminate(false); 
		pd.setCancelable(true);
	
		genderRadio.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if(isChecked){
					gender = 1;
				}
			}
		});
		gender2Radio.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if(isChecked){
					gender = 0;
				}
			}
		});		
		signUpButton.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				if(!ServiceManager.getmNet().isAvailable()){
					Toast.makeText(Register.this, getResources().getString(R.string.network_unavailable), Toast.LENGTH_LONG);
					return;
				}
				if(usernameText.length()>0&&passwordText.length()>0&&passwordAgainText.length()>0
						&&eMailText.length()>0)
				{
					if(passwordText.getText().toString().equals(passwordAgainText.getText().toString()))
					{
						user = new User();
						user.setUsername(usernameText.getText().toString());
						user.setEmail(eMailText.getText().toString());
						user.setPassword(passwordText.getText().toString());
						user.setGender(gender);
						//TODO:
						if(executor!=null&&!executor.isCancelled()){
							executor.cancel(true);
						}
						executor = new ShowProgress();
						executor.execute();
						//ServiceManager.getmConn().register(user);
						if(DEBUG) Log.d(TAG, "Begin register...");
						//Toast.makeText(Register.this, getResources().getString(R.string.register_sent), Toast.LENGTH_LONG);
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
	public void onResume(){
		super.onResume();
		registerReceiver(registerReceiver,registerReceiver.getFilter());
	}
	public void onStop(){
		super.onStop();
		if(executor!=null&&!executor.isCancelled()){
			executor.cancel(true);
		}
		unregisterReceiver(registerReceiver);
	}
	
    @Override
    protected Dialog onCreateDialog(int id){
    	Dialog dialog = null;
    	Builder b = new AlertDialog.Builder(this);
    	switch (id){
    	case USER_NAME:
    		b.setTitle(getResources().getString(R.string.register_warning)).setIcon(R.drawable.imps);
    		b.setMessage(getResources().getString(R.string.register_lack));
    		b.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub				
				}
			});
    		dialog = b.create();
    		break;
    	case VALID:
    		b.setTitle(getResources().getString(R.string.register_warning)).setIcon(R.drawable.imps);
    		b.setMessage(getResources().getString(R.string.register_2pwd_not_valid));
    		b.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub		
					passwordText.setText("");
					passwordAgainText.setText("");
				}
			});
    		dialog = b.create();
    		break;
    	case USEREXIST:
    		b.setTitle(getResources().getString(R.string.register_warning)).setIcon(R.drawable.imps);
    		b.setMessage(errorMsg);
    		b.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					usernameText.setText("");
				}
			});
    		dialog = b.create();
    		break;
    	case NET_ERROR:
    		b.setTitle(getResources().getString(R.string.register_warning)).setIcon(R.drawable.imps);
    		b.setMessage(getResources().getString(R.string.net_problem));
    		b.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					
				}
			});
    		dialog = b.create();   	
    		break;   	
    	}
    	return dialog;
    }
	public class ShowProgress extends AsyncTask<String,String,String>{

		@Override
		protected String doInBackground(String... params) {
			ServiceManager.getmAccount().register(user);
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
    
	private RegisterReceiver registerReceiver = new RegisterReceiver();
	   
    public class RegisterReceiver extends IMPSBroadcastReceiver{
    	
    	@Override
    	public void onReceive(Context context, Intent intent) {
    		// TODO Auto-generated method stub
    		super.onReceive(context, intent);
    		if(intent.getAction().equals(Constant.REGISTERSUCCESS)){
    			if(DEBUG) Log.d(TAG,"Reg success");
				if(executor!=null&&!executor.isCancelled())
				{
					executor.cancel(true);
				}
    			Toast.makeText(context, context.getResources().getString(R.string.register_success), Toast.LENGTH_LONG).show();
    			finish();
    		}else if(intent.getAction().equals(Constant.REGISTERERROR)){
    			errorMsg = intent.getStringExtra(Constant.REGISTERERRORMSG);
    			if(DEBUG) Log.d(TAG,"Reg error");
				if(executor!=null&&!executor.isCancelled())
				{
					executor.cancel(true);
				}
				showDialog(USEREXIST);
    		}
    	}

    	@Override
    	public IntentFilter getFilter() {
    		// TODO Auto-generated method stub
    		IntentFilter filter = new IntentFilter();
    		filter.addAction(Constant.REGISTERERROR);
    		filter.addAction(Constant.REGISTERSUCCESS);
    		return filter;
    	}
    }
	   @Override
	   public boolean onKeyDown(int keyCode,KeyEvent event){
		   switch (keyCode){
			case KeyEvent.KEYCODE_BACK:
				if(executor!=null&&!executor.isCancelled())
				{
					executor.cancel(true);
					return true;
				}
		   }
		   return super.onKeyDown(keyCode, event);
	   }

}
