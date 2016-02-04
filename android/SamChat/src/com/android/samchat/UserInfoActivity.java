package com.android.samchat;

import com.android.samservice.SamService;
import com.android.samservice.info.ContactUser;
import com.android.samservice.info.ReceivedAnswer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class UserInfoActivity extends Activity {
	public static final String TAG="UserInfoActivity";

	private Context mContext;
	private ImageView mBack;
	
	@Override
	protected void onCreate(Bundle icicle){
		super.onCreate(icicle);
		setContentView(R.layout.activity_sam_answer_detail);

		mBack =  (ImageView) findViewById(R.id.back);
		mBack.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				finish();
			}
		    	
		});

	mContext = getBaseContext();

		
	}


	@Override
	public void onBackPressed(){
		finish();
	}
}