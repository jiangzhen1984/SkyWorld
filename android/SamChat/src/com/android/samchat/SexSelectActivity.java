package com.android.samchat;

import com.android.samchat.easemobdemo.EaseMobHelper;
import com.android.samservice.Constants;
import com.android.samservice.SMCallBack;
import com.android.samservice.SamLog;
import com.android.samservice.SamService;
import com.android.samservice.info.AvatarRecord;
import com.android.samservice.info.ContactUser;
import com.android.samservice.info.ReceivedAnswer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class SexSelectActivity extends Activity {
	public static final String TAG="SexSelectActivity";

	public static final String SELECT_RESULE="select_result";

	public static final int SEX_NONE=0;
	public static final int SEX_MALE=1;
	public static final int SEX_FEMALE=2;

	private Context mContext;
	
	private SamProcessDialog mDialog;

	private int sex;

	private LinearLayout mMale_layout;
	private LinearLayout mFemale_layout;
	private ImageView mMale_img;
	private ImageView mFemale_img;
	
	
	@Override
	protected void onCreate(Bundle icicle){
		super.onCreate(icicle);
		setContentView(R.layout.activity_sex_select);

		mContext = getBaseContext();
		mMale_layout = (LinearLayout) findViewById(R.id.male_layout); 
		mFemale_layout = (LinearLayout) findViewById(R.id.female_layout); 
		mMale_img = (ImageView) findViewById(R.id.male_img);
		mFemale_img = (ImageView) findViewById(R.id.female_img);
		
		initFromIntent(getIntent());

		if(sex == SEX_MALE || sex ==  SEX_NONE){
			mMale_img.setImageResource(R.drawable.select);
			mFemale_img.setImageResource(R.drawable.not_select);
		}else{
			mMale_img.setImageResource(R.drawable.not_select);
			mFemale_img.setImageResource(R.drawable.select);
		}

		mMale_layout.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				mMale_img.setImageResource(R.drawable.select);
				mFemale_img.setImageResource(R.drawable.not_select);
				Intent data = new Intent();
				data.putExtra(SELECT_RESULE, SEX_MALE);
				setResult(RESULT_OK, data);
				finish();
			}
		});

		mFemale_layout.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				mMale_img.setImageResource(R.drawable.not_select);
				mFemale_img.setImageResource(R.drawable.select);
				Intent data = new Intent();
				data.putExtra(SELECT_RESULE, SEX_FEMALE);
				setResult(RESULT_OK, data);
				finish();
			}
		});

		
		
	}


	@Override
	public boolean onTouchEvent(MotionEvent event) {
		//return super.onTouchEvent(event);
		setResult(RESULT_CANCELED);
		finish();
		return false;
	}
	 
	@Override
	public void onBackPressed(){
		setResult(RESULT_CANCELED);
		finish();
	}

	private void initFromIntent(Intent intent) {
		sex = intent.getIntExtra(Constants.SEX_SELECT, SEX_MALE);
		
	}

	
}
