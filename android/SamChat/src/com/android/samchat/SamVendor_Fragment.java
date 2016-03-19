package com.android.samchat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.android.samchat.R;
import com.android.samchat.easemobdemo.EaseMobHelper;

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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.samservice.*;
import com.android.samservice.info.AvatarRecord;
import com.android.samservice.info.LoginUser;
import com.easemob.EMCallBack;
import com.easemob.EMConnectionListener;
import com.easemob.EMError;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMContactManager;
import com.easemob.easeui.utils.EaseUserUtils;

public class SamVendor_Fragment extends Fragment{
	static final String TAG = "SamVendor_Fragment";

	public static final int REQUST_CODE_CONFIRM_INPUT_NEW = 21;
	public static final int REQUST_CODE_CONFIRM_INPUT_MODIFY = 22;

	private View rootView;
	private LinearLayout mInput_layout;
	private LinearLayout mNew_layout;
	private RelativeLayout mModify_layout;
	private TextView mModify;
		
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		SamLog.i(TAG, "onCreateView");
		
		if(rootView == null){
			rootView = inflater.inflate(R.layout.fragment_vendor, container,false);
			mInput_layout = (LinearLayout)rootView.findViewById(R.id.input_layout);
			mInput_layout.setOnClickListener(new OnClickListener(){
		    		@Override
		    		public void onClick(View arg0) {
		    			launchVendorInfoInputActivity();
		    		}
			});

			

			mNew_layout = (LinearLayout)rootView.findViewById(R.id.new_layout);
			mModify_layout = (RelativeLayout)rootView.findViewById(R.id.modify_layout);

			LoginUser cuser = SamService.getInstance().get_current_user();
			if(cuser.getUserType() == LoginUser.USER){
				mModify_layout.setVisibility(View.GONE);
				mNew_layout.setVisibility(View.VISIBLE);
			}else{
				mNew_layout.setVisibility(View.GONE);
				mModify_layout.setVisibility(View.VISIBLE);
			}

			mModify =  (TextView)rootView.findViewById(R.id.modify);
			mModify.setOnClickListener(new OnClickListener(){
		    		@Override
		    		public void onClick(View arg0) {
		    			launchVendorInfoInputActivity("a","b","c");
		    		}
			});
			
			
		}
		return rootView;
	}

	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		SamLog.i(TAG, "onCreated");
	}
	
	@Override
	public void onAttach(Activity activity){
		super.onAttach(activity);
		SamLog.i(TAG, "onAttach");
	}
	
	@Override
	public void onStart(){
		super.onStart();
		SamLog.i(TAG, "onStart");
	}
	
	@Override
	public void onResume(){
		super.onResume();
		SamLog.i(TAG, "onResume");
	}
	
	
	@Override
	public void onDetach(){
		super.onDetach();
		SamLog.i(TAG, "onDetach");
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState){
		super.onActivityCreated(savedInstanceState);
		
	}
	
	@Override
	public void onPause(){
		super.onPause();
		SamLog.i(TAG, "onPause");
	}
	
	@Override
	public void onStop(){
		super.onStop();
		SamLog.i(TAG, "onStop");
	}
	
	@Override
	public void onDestroyView(){
		super.onDestroyView();
		if(rootView != null){
			SamLog.i(TAG, "onDestroyView");
			((ViewGroup)(rootView.getParent())).removeView(rootView);
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
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

	private void launchVendorInfoInputActivity(String line,String location,String introducation){
		Intent newIntent = new Intent(getActivity(),VendorInfoInputActivity.class);
		int intentFlags = Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP;
		newIntent.setFlags(intentFlags);
		newIntent.putExtra("line", line);
		newIntent.putExtra("location", location);
		newIntent.putExtra("introduction", introducation);

		startActivityForResult(newIntent, REQUST_CODE_CONFIRM_INPUT_MODIFY);
	}

	private void launchVendorInfoInputActivity(){
		Intent newIntent = new Intent(getActivity(),VendorInfoInputActivity.class);
		int intentFlags = Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP;
		newIntent.setFlags(intentFlags);

		startActivityForResult(newIntent, REQUST_CODE_CONFIRM_INPUT_NEW);
	}
	
}


