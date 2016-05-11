package com.android.samchat.activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.samservice.*;
import com.android.samservice.info.AvatarRecord;
import com.android.samservice.info.LoginUser;
import com.netease.nim.demo.R;

public class VendorSettingActivity extends Activity {
	
	static final String TAG="VendorSettingActivity";
	public static final int REQUST_CODE_CONFIRM_INPUT_NEW = 21;
	public static final int REQUST_CODE_CONFIRM_INPUT_MODIFY = 22;

	private RelativeLayout mBack_layout;
	private LinearLayout mInput_layout;
	private LinearLayout mNew_layout;
	private RelativeLayout mModify_layout;
	private TextView mModify;

	private TextView mMy_business;
	private TextView mIntroduction;
	private TextView mLocation;

	private TextView mMainpage;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_vendor_setting);

		mInput_layout = (LinearLayout)findViewById(R.id.input_layout);
			mInput_layout.setOnClickListener(new OnClickListener(){
		    		@Override
		    		public void onClick(View arg0) {
		    			launchVendorInfoInputActivity();
		    		}
			});

			mBack_layout =  (RelativeLayout)findViewById(R.id.back_layout);
			mBack_layout.setOnClickListener(new OnClickListener(){
		    		@Override
		    		public void onClick(View arg0) {
		    			finish();
		    		}
			});

			

			mNew_layout = (LinearLayout)findViewById(R.id.new_layout);
			mModify_layout = (RelativeLayout)findViewById(R.id.modify_layout);

			mMy_business = (TextView)findViewById(R.id.my_business);
			mIntroduction = (TextView)findViewById(R.id.introduction);
			mLocation = (TextView)findViewById(R.id.location);

			LoginUser cuser = SamService.getInstance().get_current_user();
			if(cuser.getUserType() == LoginUser.USER){
				mModify_layout.setVisibility(View.GONE);
				mNew_layout.setVisibility(View.VISIBLE);
			}else{
				mNew_layout.setVisibility(View.GONE);
				mModify_layout.setVisibility(View.VISIBLE);
				mMy_business.setText(cuser.area);
				mLocation.setText(cuser.location);
				mIntroduction.setText(cuser.description);
			}

			mModify =  (TextView)findViewById(R.id.modify);
			mModify.setOnClickListener(new OnClickListener(){
		    		@Override
		    		public void onClick(View arg0) {
		    			LoginUser cuser = SamService.getInstance().get_current_user();
		    			launchVendorInfoInputActivity(cuser.area,cuser.location,cuser.description);
		    		}
			});


			mMainpage =  (TextView)findViewById(R.id.mainpage);
			mMainpage.setOnClickListener(new OnClickListener(){
		    		@Override
		    		public void onClick(View arg0) {
		    			launchVendorWebView();
		    		}
			});
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {  
		if(requestCode == REQUST_CODE_CONFIRM_INPUT_NEW){
			if(resultCode == 1){ //OK
				LoginUser cuser = SamService.getInstance().get_current_user();
				if(cuser.getUserType() == LoginUser.USER){
					mModify_layout.setVisibility(View.GONE);
					mNew_layout.setVisibility(View.VISIBLE);
				}else{
					mNew_layout.setVisibility(View.GONE);
					mModify_layout.setVisibility(View.VISIBLE);
					
					mMy_business.setText(cuser.area);
					mLocation.setText(cuser.location);
					mIntroduction.setText(cuser.description);
					
				}
			}else{

			}
    	   
		}else if(requestCode == REQUST_CODE_CONFIRM_INPUT_MODIFY){
			if(resultCode == 1){ //OK
				LoginUser cuser = SamService.getInstance().get_current_user();
				if(cuser.getUserType() == LoginUser.USER){
					mModify_layout.setVisibility(View.GONE);
					mNew_layout.setVisibility(View.VISIBLE);
				}else{
					mNew_layout.setVisibility(View.GONE);
					mModify_layout.setVisibility(View.VISIBLE);
				}
			}else{

			}
		}
	} 

	@Override
	public void onBackPressed(){
		finish();
	}

	private void launchVendorInfoInputActivity(String line,String location,String introducation){
		Intent newIntent = new Intent(this,VendorInfoInputActivity.class);
		int intentFlags = Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP;
		newIntent.setFlags(intentFlags);
		newIntent.putExtra("line", line);
		newIntent.putExtra("location", location);
		newIntent.putExtra("introduction", introducation);

		startActivityForResult(newIntent, REQUST_CODE_CONFIRM_INPUT_MODIFY);
	}

	private void launchVendorInfoInputActivity(){
		Intent newIntent = new Intent(this,VendorInfoInputActivity.class);
		int intentFlags = Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP;
		newIntent.setFlags(intentFlags);

		startActivityForResult(newIntent, REQUST_CODE_CONFIRM_INPUT_NEW);
	}

	private void launchVendorWebView(){
		
	}
}

