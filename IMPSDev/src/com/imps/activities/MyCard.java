package com.imps.activities;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.jboss.netty.buffer.ChannelBuffers;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.imps.IMPSActivity;
import com.imps.IMPSDev;
import com.imps.R;
import com.imps.basetypes.Constant;
import com.imps.basetypes.OutputMessage;
import com.imps.basetypes.User;
import com.imps.net.handler.MessageFactory;
import com.imps.net.handler.UserManager;
import com.imps.receivers.IMPSBroadcastReceiver;
import com.imps.services.impl.ServiceManager;
import com.imps.util.CommonHelper;

/**
 * MyCard contains personal card infomation.
 * 
 * @author Amesists
 * 
 */
public class MyCard extends IMPSActivity {
	private static String TAG = MyCard.class.getCanonicalName();
	private static boolean DEBUG = IMPSDev.isDEBUG();

	private User currentUser;
	private View.OnClickListener clickListener;

	final private int SET_GENDER = 1;
	final private int SET_EMAIL = 2;

	private ProgressDialog pd;
	private ShowProgress executor;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.card_info);

		pd = new ProgressDialog(MyCard.this);
		pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		pd.setMessage(getResources().getString(R.string.login_loading));
		pd.setIndeterminate(false);
		pd.setCancelable(true);

		setUpMyCard();
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog dialog = null;
		switch (id) {
		case SET_GENDER:
			dialog = new AlertDialog.Builder(MyCard.this)
					.setTitle(R.string.chooseGender)
					.setItems(R.array.chooseGender,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {

									String[] result = getResources()
											.getStringArray(
													R.array.chooseGender);
									TextView tv = (TextView) findViewById(R.id.portrait_sex);
									tv.setText(result[which]);
									currentUser.setGender(which);

									if (executor != null
											&& !executor.isCancelled()) {
										executor.cancel(true);
									}
									executor = new ShowProgress();
									executor.execute();
									if (DEBUG)
										Log.d(TAG, "Begin update gender ...");
								}
							}).create();
			break;
		case SET_EMAIL:
			LayoutInflater li = LayoutInflater.from(MyCard.this);
			final View updateEmailView = li.inflate(R.layout.update_email, null);
			dialog = new AlertDialog.Builder(MyCard.this)
					.setTitle(R.string.updateEmailTitle)
					.setView(updateEmailView)
					.setPositiveButton(R.string.ok,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									EditText newEmail = (EditText) updateEmailView
											.findViewById(R.id.updateEmailEditText);
									String newEmailStr = newEmail.getText()
											.toString();
									TextView tv = (TextView) findViewById(R.id.email_user);
									tv.setText(newEmailStr);
									currentUser.setEmail(newEmailStr);
									
									if (executor != null
											&& !executor.isCancelled()) {
										executor.cancel(true);
									}
									executor = new ShowProgress();
									executor.execute();
									if (DEBUG)
										Log.d(TAG, "Begin update email ...");

								}
							})
					.setNegativeButton(R.string.cancel,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
								}
							}).create();
			break;
		default:
			break;
		}
		return dialog;
	}

	public void onResume() {
		super.onResume();
		registerReceiver(updateReceiver, updateReceiver.getFilter());
	}

	public void onStop() {
		super.onStop();
		if (executor != null && !executor.isCancelled()) {
			executor.cancel(true);
		}
		unregisterReceiver(updateReceiver);
	}

	private void setUpMyCard() {
		clickListener = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				switch (v.getId()) {
				case R.id.portrait:
					// Choose pictures from gallery
					Intent intent = new Intent();
			        intent.setType("image/*");
			        intent.setAction(Intent.ACTION_GET_CONTENT); 
			        startActivityForResult(intent, 1);
					break;
				case R.id.portrait_name:
					break;
				case R.id.portrait_sex:
					showDialog(SET_GENDER);
					break;
				case R.id.email_user:
					showDialog(SET_EMAIL);
					break;
				default:
					break;
				}
			}
		};

		currentUser = UserManager.getGlobaluser();

		// set portrait
		ImageView iv = (ImageView) findViewById(R.id.portrait);
		iv.setOnClickListener(clickListener);

		// set name
		TextView tv = (TextView) findViewById(R.id.portrait_name);
		tv.setText(currentUser.getUsername());
		tv.setOnClickListener(clickListener);

		// set gender
		tv = (TextView) findViewById(R.id.portrait_sex);
		if (currentUser.getGender() == 1)
			tv.setText(R.string.male);
		else
			tv.setText(R.string.female);
		tv.setOnClickListener(clickListener);

		// set email
		tv = (TextView) findViewById(R.id.email_user);
		tv.setText(currentUser.getEmail());
		tv.setOnClickListener(clickListener);
		
		// Download the portrait
		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			Uri uri = data.getData();
			ContentResolver cr = this.getContentResolver();
			try {
				Bitmap bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));
				ImageView imageView = (ImageView) findViewById(R.id.portrait);
				
				// scale the picture
				int width = bitmap.getWidth();
		        int height = bitmap.getHeight();
		        int newWidth = 128;
		        int newHeight = 128;
		        float scaleWidth = ((float) newWidth) / width;
		        float scaleHeight = ((float) newHeight) / height;
		        Matrix matrix = new Matrix();
		        matrix.postScale(scaleWidth, scaleHeight);
		        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
				
				// save the picture
		        String impsDataPath = Environment.getExternalStorageDirectory().getAbsolutePath()+ "/Android/data/com.imps/";
		        Log.d("Path", impsDataPath);
		        File fpath = new File(impsDataPath + "/portrait/");
		        if (!fpath.exists())
		        	fpath.mkdirs();
		        
		        File f = new File(impsDataPath + "/portrait/" + currentUser.getUsername() + ".png");
	        	f.createNewFile();
		        FileOutputStream fOut = null;
	        	fOut = new FileOutputStream(f);
		        resizedBitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
	        	fOut.flush();
	        	fOut.close();
	        	
	        	// set the portrait in the app
	        	imageView.setImageBitmap(resizedBitmap);
				
				// then send the image to the server
	        	byte[] container = CommonHelper.bitmapToBytes(resizedBitmap);
	        	int len = container.length;
	        	OutputMessage outMsg = MessageFactory.createCUploadPortraitReq(currentUser.getUsername());
	        	outMsg.getOutputStream().writeInt(len);
	        	outMsg.getOutputStream().write(container);
	        	
				if(ServiceManager.getmTcpConn().getChannel().isConnected()){
					ServiceManager.getmTcpConn().getChannel().write(ChannelBuffers.wrappedBuffer(
							outMsg.build()));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	public class ShowProgress extends AsyncTask<String, String, String> {

		@Override
		protected String doInBackground(String... params) {
			ServiceManager.getmAccount().updateUserInfo(currentUser);
			return null;
		}

		@Override
		protected void onPreExecute() {
			if (!pd.isShowing()) {
				pd.show();
			}
		}

		@Override
		protected void onPostExecute(String result) {
			if (pd.isShowing()) {
				pd.dismiss();
			}
		}

		@Override
		protected void onProgressUpdate(String... params) {

		}

		@Override
		protected void onCancelled() {
			if (pd.isShowing()) {
				pd.dismiss();
			}
		}
	}

	private UpdateUserInfoReceiver updateReceiver = new UpdateUserInfoReceiver();

	public class UpdateUserInfoReceiver extends IMPSBroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			super.onReceive(context, intent);
		}

		@Override
		public IntentFilter getFilter() {
			IntentFilter filter = new IntentFilter();
			return filter;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			if (executor != null && !executor.isCancelled()) {
				executor.cancel(true);
				//return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

}
