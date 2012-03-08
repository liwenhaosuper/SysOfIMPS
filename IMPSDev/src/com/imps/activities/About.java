package com.imps.activities;

import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;

import com.imps.IMPSActivity;
import com.imps.R;

public class About extends IMPSActivity {

	private TextView aboutView;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.about);
		aboutView = (TextView)findViewById(R.id.about_content);
		aboutView.setText(
				getResources().getString(R.string.about_copyright)
				);		
		//aboutView.setTextColor(color.darker_gray);
	}
	@Override
	public void onStop(){
		super.onStop();
	}
}
