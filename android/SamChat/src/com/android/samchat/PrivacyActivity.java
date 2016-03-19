package com.android.samchat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;



public class PrivacyActivity extends Activity {
	private final String TAG = "PrivacyActivity";

	private ImageView mBack;
	private LinearLayout mBlacklist_layout;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_privacy);

		mBack =  (ImageView) findViewById(R.id.back);
		mBack.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});

		mBlacklist_layout = (LinearLayout) findViewById(R.id.blacklist_layout);
		mBlacklist_layout.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				launchBlackListActivity();
			}
		});
		
		
	}

	private void launchBlackListActivity(){
		Intent newIntent = new Intent(this,BlackListActivity.class);
		int intentFlags = Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP;
		newIntent.setFlags(intentFlags);
		
		startActivityForResult(newIntent,1);
		
	}
	

}



