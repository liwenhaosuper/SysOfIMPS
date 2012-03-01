package com.imps.activities;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.imps.R;
import com.imps.basetypes.User;
import com.imps.net.handler.UserManager;

/**
 * MyCard contains personal card infomation.
 * @author Amesists
 *
 */
public class MyCard extends Activity {
	private static String TAG = MyCard.class.getCanonicalName();
	private User currentUser;

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.card_info);
		
		setUpMyCard();
	}
	
	private void setUpMyCard() {
		currentUser = UserManager.getGlobaluser();
		
		// set name
		TextView tv = (TextView) findViewById(R.id.portrait_name);
		tv.setText(currentUser.getUsername());
		
		// set gender
		tv = (TextView) findViewById(R.id.portrait_sex);
		if (currentUser.getGender() == 1)
			tv.setText(R.string.male);
		else
			tv.setText(R.string.female);
		
		// set email
		tv = (TextView) findViewById(R.id.email_user);
		tv.setText(currentUser.getEmail());
	}
}
