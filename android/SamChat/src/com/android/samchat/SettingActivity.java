package com.android.samchat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;



public class SettingActivity extends Activity {
	private final String TAG = "SettingActivity";

	private ImageView mBack;
	private LinearLayout mSecurity_setting_layout;
	private LinearLayout mNew_msg_reminder_layout;
	private LinearLayout mPrivacy_layout;
	private LinearLayout mFeedback_layout;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);

		mBack =  (ImageView) findViewById(R.id.back);
		mBack.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});

		mSecurity_setting_layout = (LinearLayout) findViewById(R.id.security_setting_layout);
		mSecurity_setting_layout.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				launchSecuritySettingActivity();
			}
		});
		
		mNew_msg_reminder_layout = (LinearLayout) findViewById(R.id.new_msg_reminder_layout);
		mNew_msg_reminder_layout.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				
			}
		});
		
		mPrivacy_layout = (LinearLayout) findViewById(R.id.privacy_layout);
		mPrivacy_layout.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				
			}
		});
		
		mFeedback_layout = (LinearLayout) findViewById(R.id.feedback_layout);
		mFeedback_layout.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				
			}
		});
		
	}


	private void launchSecuritySettingActivity(){
		Intent newIntent = new Intent(this,SecuritySettingActivity.class);
		int intentFlags = Intent.FLAG_ACTIVITY_CLEAR_TOP;
		newIntent.setFlags(intentFlags);
		startActivity(newIntent);

	}
	
	

}


