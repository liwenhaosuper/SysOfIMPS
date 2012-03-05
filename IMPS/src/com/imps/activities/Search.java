package com.imps.activities;

import com.imps.R;
import com.imps.activities.MyTab.ExitReceiver;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class Search extends Activity {
	private EditText edit;
	private Spinner mySpinner;
	private Button button_search;
    private ArrayAdapter<String> adapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search);
		findView();

		adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,ConstData.city);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		
		mySpinner.setAdapter(adapter);	
		
		edit.setOnClickListener(new EditText.OnClickListener(){

			public void onClick(View v) {
				// TODO Auto-generated method stub
				edit.setText("");
			}});
		button_search.setOnClickListener(new Button.OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				String addres = edit.getText().toString();
				if ("".equals(edit.getText().toString()))
				 {
					Toast.makeText(Search.this,R.string.input_empty , Toast.LENGTH_LONG).show();
				 }else{
					Intent intent = new Intent();
					Bundle bundle = new Bundle();
					bundle.putString("addres", addres);
					intent.putExtras(bundle);
					Search.this.setResult(R.integer.RESULT_SEARCH, intent);
					Search.this.finish();
				}
			}
		});
		
		
		mySpinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){

			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				edit.setText(arg0.getSelectedItem().toString());
			}

			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}});

	}

	private void findView() {
		// TODO Auto-generated method stub
		edit = (EditText) findViewById(R.id.myEditText);
		button_search = (Button) findViewById(R.id.myButton_search);
		mySpinner = (Spinner) findViewById(R.id.mySpinner);
	}

	@Override
	public void onResume()
	{
		super.onResume();
		IntentFilter ifilter = new IntentFilter();
		ifilter.addAction("exit");
		registerReceiver(exitReceiver,ifilter);
	}
	@Override
	public void onStop()
	{
		super.onStop();
		unregisterReceiver(exitReceiver);
	}
	
	public class ExitReceiver extends BroadcastReceiver
	{

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if("exit".equals(intent.getAction()))
			{
				finish();
			}
		}		
	}
	private ExitReceiver exitReceiver = new ExitReceiver();
	
}
