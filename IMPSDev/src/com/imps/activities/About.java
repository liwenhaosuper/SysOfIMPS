package com.imps.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;

import com.imps.R;

public class About extends Activity {

	private TextView aboutView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
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

}
