 package com.android.samchat;

import com.android.samchat.easemobdemo.EaseMobModel;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;



public class NewMsgReminderActivity extends Activity {
	private final String TAG = "NewMsgReminderActivity";

	private ImageView mBack;
	private LinearLayout mEnable_new_msg_reminder_layout;
	private LinearLayout mNew_msg_reminder_voice_layout;
	private LinearLayout mNew_msg_reminder_vibrate_layout;

	private ImageView mEnable_new_msg_img;
	private ImageView mNew_msg_reminder_voice_img;
	private ImageView mNew_msg_reminder_vibrate_img;

	private EaseMobModel model;

	private void updateView(){
		if(model.isMsgNotificationEnable()){
			mEnable_new_msg_img.setImageResource(R.drawable.ease_open_icon);
			mNew_msg_reminder_voice_layout.setVisibility(View.VISIBLE);
			mNew_msg_reminder_vibrate_layout.setVisibility(View.VISIBLE);
			if(model.isMsgNotificationSoundEnable()){
				mNew_msg_reminder_voice_img.setImageResource(R.drawable.ease_open_icon);
			}else{
				mNew_msg_reminder_voice_img.setImageResource(R.drawable.ease_close_icon);
			}

			if(model.isMsgNotificationVibrateEnable()){
				mNew_msg_reminder_vibrate_img.setImageResource(R.drawable.ease_open_icon);
			}else{
				mNew_msg_reminder_vibrate_img.setImageResource(R.drawable.ease_close_icon);
			}
			
		}else{
			mEnable_new_msg_img.setImageResource(R.drawable.ease_close_icon);
			mNew_msg_reminder_voice_layout.setVisibility(View.GONE);
			mNew_msg_reminder_vibrate_layout.setVisibility(View.GONE);
		}
		

	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_newmsgsettings);

		model = new EaseMobModel(skyworld.appContext);

		mEnable_new_msg_img = (ImageView)findViewById(R.id.enable_new_msg_img);
		mNew_msg_reminder_voice_img = (ImageView)findViewById(R.id.new_msg_reminder_voice_img);
		mNew_msg_reminder_vibrate_img =  (ImageView)findViewById(R.id.new_msg_reminder_vibrate_img);

		
		
		
		mBack =  (ImageView) findViewById(R.id.back);
		mBack.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});

		mEnable_new_msg_reminder_layout = (LinearLayout) findViewById(R.id.enable_new_msg_reminder_layout);
		mEnable_new_msg_reminder_layout.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				if(model.isMsgNotificationEnable()){
					model.setMsgNotification(false);
				}else{
					model.setMsgNotification(true);
				}

				updateView();
			}
		});
		
		mNew_msg_reminder_voice_layout = (LinearLayout) findViewById(R.id.new_msg_reminder_voice_layout);
		mNew_msg_reminder_voice_layout.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				if(model.isMsgNotificationSoundEnable()){
					model.setMsgNotificationSound(false);
				}else{
					model.setMsgNotificationSound(true);
				}

				updateView();
			}
		});
		
		mNew_msg_reminder_vibrate_layout = (LinearLayout) findViewById(R.id.new_msg_reminder_vibrate_layout);
		mNew_msg_reminder_vibrate_layout.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				if(model.isMsgNotificationVibrateEnable()){
					model.setMsgNotificationVibrate(false);
				}else{
					model.setMsgNotificationVibrate(true);
				}

				updateView();
			}
		});
		
		updateView();
		
	}



	
	

}



