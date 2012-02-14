package com.imps.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.imps.IMPSDev;
import com.imps.R;
import com.imps.basetypes.User;
import com.imps.events.IRegisterEvent;
import com.imps.net.handler.NetMsgLogicHandler;
import com.imps.services.impl.ServiceManager;

public class Register extends Activity implements IRegisterEvent {
	private static String TAG = Register.class.getCanonicalName();
	private static boolean DEBUG = IMPSDev.isDEBUG();
	
	private static final int USER_NAME = Menu.FIRST+1;
	private static final int VALID = Menu.FIRST+2;
	private static final int USEREXIST = Menu.FIRST+3;
	private static final int NET_ERROR = Menu.FIRST+4;
	private static final int SUCCESS = Menu.FIRST+5;
	private static final int FAIL = Menu.FIRST+6;
	private static final int START_REG =  Menu.FIRST+7;
	private EditText usernameText;
	private EditText passwordText;
	private EditText eMailText;
	private RadioButton genderRadio;
	private RadioButton gender2Radio;
	private EditText passwordAgainText;
	private Button signUpButton;
	private Button cancelButton;
	private int gender = 1;
	
	private Handler receiver = new Handler(){
		@Override
		public void handleMessage(Message msg){
			switch(msg.what){
			case START_REG:
				//TODO:Reg...
				Toast.makeText(Register.this, getResources().getString(R.string.register_sent), Toast.LENGTH_LONG);
				ServiceManager.getmAccount().register((User)msg.obj);
				break;
			case SUCCESS:
				showDialog(SUCCESS);
				break;
			case USEREXIST:
				showDialog(USEREXIST);
				break;
			case FAIL:
				showDialog(19);
				break;
			}
		}
	};

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
						User user = new User();
						user.setUsername(usernameText.getText().toString());
						user.setEmail(eMailText.getText().toString());
						user.setPassword(passwordText.getText().toString());
						user.setGender(gender);
						Message msg = new Message();
						msg.what = START_REG;
						msg.obj = user;
						receiver.sendMessage(msg);
						//ServiceManager.getmConn().register(user);
						if(DEBUG) Log.d(TAG, "Begin register...");
						Toast.makeText(Register.this, getResources().getString(R.string.register_sent), Toast.LENGTH_LONG);
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
		ServiceManager.getmNetLogic().addRegEventHandler(this);
	}
	public void onResume(){
		super.onResume();
	}
	public void onStop(){
		super.onStop();
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
    		b.setMessage(getResources().getString(R.string.register_user_exist));
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
    	case SUCCESS:
    		b.setTitle(getResources().getString(R.string.register_warning)).setIcon(R.drawable.imps);
    		b.setMessage(getResources().getString(R.string.register_success));
    		b.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					finish();
				}
			});
    		dialog = b.create();   	
    		break;
    	default:
    		b.setTitle(getResources().getString(R.string.register_warning)).setIcon(R.drawable.imps);
    		b.setMessage(getResources().getString(R.string.register_fail));
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
	@Override
	public void onRegError(String smsg,int errorCode) {
		//Toast.makeText(Register.this, msg, Toast.LENGTH_LONG);
		if(smsg.equals(getResources().getString(R.string.register_user_exist))){
			Message msg = new Message();
			msg.what = USEREXIST;
			receiver.sendMessage(msg);
		}else{
			Message msg = new Message();
			msg.what = 19;
			receiver.sendMessage(msg);
		}
	}
	@Override
	public void onRegSuccess() {
		//Toast.makeText(Register.this, getResources().getString(R.string.register_success), Toast.LENGTH_LONG);
		Message msg = new Message();
		msg.what = SUCCESS;
		receiver.sendMessage(msg);
	}
}
