package com.android.samchat;

import com.android.samchat.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.samservice.*;
import com.android.samservice.info.LoginUser;

public class SamMe_Fragment extends Fragment{
	static final String TAG = "SamMe_Fragment";

	static final String EXIT_APP_CONFIRM = "com.android.samchat.exitapp";
	static final String LOGOUT_CONFIRM = "com.android.samchat.logout";
	static final String UPGRADE_CONFIRM = "com.android.samchat.upgrade";

	public static final int MSG_LOGOUT_CALLBACK = 1;
	public static final int MSG_UPGRADE_CALLBACK = 2;

	private View rootView;
	private LinearLayout mSettingLayout;
	private LinearLayout mLogoutLayout;
	private LinearLayout mExitappLayout;
	private LinearLayout mUpgradeLayout;

	private TextView mUpgrade_spec;

	private SamProcessDialog mDialog;


	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		SamLog.i(TAG, "onCreateView");
		
		if(rootView == null){
			mDialog = new SamProcessDialog();
			rootView = inflater.inflate(R.layout.fragment_me, container,false);
			mSettingLayout = (LinearLayout)rootView.findViewById(R.id.setting_layout);
			mUpgradeLayout = (LinearLayout)rootView.findViewById(R.id.upgrade_layout);
			mLogoutLayout = (LinearLayout)rootView.findViewById(R.id.logout_layout);
			mExitappLayout = (LinearLayout)rootView.findViewById(R.id.exitapp_layout);

			mUpgrade_spec = (TextView)rootView.findViewById(R.id.upgrade_spec);
			
			mSettingLayout.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View arg0) {
					
				}
			});
			
			mUpgradeLayout.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View arg0) {
					if(SamService.getInstance().get_current_user().usertype > LoginUser.USER){
						//launchDialogActivity(getString(R.string.reminder), getString(R.string.upgrade_already));
						return;
					}else{
						SamLog.e(TAG,"upgrade to servicer");
						launchDialogActivityNeedConfirmForUpgrade(getString(R.string.reminder),getString(R.string.upgrade_reminder));
					}
				}
			});
			
			mLogoutLayout.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View arg0) {
					launchDialogActivityNeedConfirmForLogout(getString(R.string.reminder),getString(R.string.logout_reminder));
				}
			});
			
			mExitappLayout.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View arg0) {
					launchDialogActivityNeedConfirmForExitApp(getString(R.string.reminder),getString(R.string.exitapp_reminder));
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

		if(SamService.getInstance().get_current_user().usertype> LoginUser.USER){
			mUpgrade_spec.setText(getString(R.string.sam_gbdy));
		}
	}
	
	
	@Override
	public void onDetach(){
		super.onDetach();
		SamLog.i(TAG, "onDetach");
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState){
		super.onActivityCreated(savedInstanceState);
		SamLog.i(TAG, "onActivityCreated");
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

	private void launchDialogActivity(String title,String msg){
		Intent newIntent = new Intent(getActivity(),DialogActivity.class);
		int intentFlags = Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP;
		newIntent.setFlags(intentFlags);
		newIntent.putExtra("title", title);
		newIntent.putExtra("message", msg);
		startActivity(newIntent);
	}	

	private void launchDialogActivityNeedConfirmForExitApp(String title,String msg){
		Intent newIntent = new Intent(EXIT_APP_CONFIRM);		
		//int intentFlags = Intent.FLAG_ACTIVITY_NEW_TASK;// | Intent.FLAG_ACTIVITY_CLEAR_TOP;
		//newIntent.setFlags(intentFlags);
		newIntent.putExtra("title", title);
		newIntent.putExtra("message", msg);
		this.startActivityForResult(newIntent, 1);
	}

	private void launchDialogActivityNeedConfirmForLogout(String title,String msg){
		Intent newIntent = new Intent(LOGOUT_CONFIRM);		
		//int intentFlags = Intent.FLAG_ACTIVITY_NEW_TASK;// | Intent.FLAG_ACTIVITY_CLEAR_TOP;
		//newIntent.setFlags(intentFlags);
		newIntent.putExtra("title", title);
		newIntent.putExtra("message", msg);
		this.startActivityForResult(newIntent, 2);
	}

	private void launchDialogActivityNeedConfirmForUpgrade(String title,String msg){
		Intent newIntent = new Intent(UPGRADE_CONFIRM);		
		//int intentFlags = Intent.FLAG_ACTIVITY_NEW_TASK;// | Intent.FLAG_ACTIVITY_CLEAR_TOP;
		//newIntent.setFlags(intentFlags);
		newIntent.putExtra("title", title);
		newIntent.putExtra("message", msg);
		this.startActivityForResult(newIntent, 3);
	}


	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {  
		if(requestCode == 1){
			if(resultCode == 1){ //OK
				SamLog.e(TAG,"exit app...");
				exitProgram();
			}else{
				SamLog.e(TAG,"cancel exit app...");
			}
		}else if(requestCode == 2){
			if(resultCode == 1){ //OK
				SamLog.e(TAG,"logout...");
				logoutAccount();
			}else{
				SamLog.e(TAG,"cancel logout...");
			}
		}else if(requestCode == 3){
			if(resultCode == 1){//OK
				SamLog.e(TAG,"upgrade...");
				upgradeToServicer();
			}else{
				SamLog.e(TAG,"cancel upgrade...");
			}
		}
	}

	private void logoutAccount(){
		if(mDialog!=null){
    			mDialog.launchProcessDialog(getActivity(),getString(R.string.question_publish_now));
    		}
		SignService.getInstance().SignOut( mHandler, MSG_LOGOUT_CALLBACK);
	}
	
	private void upgradeToServicer(){
		if(mDialog!=null){
    			mDialog.launchProcessDialog(getActivity(),getString(R.string.process));
    		}

		SamService.getInstance().upgrade( mHandler, MSG_UPGRADE_CALLBACK);

		
	}
	

	private void launchSignInActivity()
	{
		Intent newIntent = new Intent(getActivity(),SignInActivity.class);
		int intentFlags = Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP;
		newIntent.setFlags(intentFlags);
		startActivity(newIntent);
	}

	private void exitProgram(){
		((MainActivity)getActivity()).exitProgrames();
	}


	public Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if(getActivity() == null){
				SamLog.e(TAG,"MainActivity is killed, drop msg...");
				return;
			}

			switch(msg.what){
			case MSG_LOGOUT_CALLBACK:
				if(msg.arg1 == SignService.R_SIGN_OUT_OK){
					if(mDialog!=null){
    						mDialog.dismissPrgoressDiglog();
    					}
					((MainActivity)getActivity()).exitActivity();
					launchSignInActivity();
				}else if(msg.arg1 == SignService.R_SIGN_OUT_FAILED){
					if(mDialog!=null){
    						mDialog.dismissPrgoressDiglog();
    					}
					((MainActivity)getActivity()).exitActivity();
					launchSignInActivity();
				}
				break;
	
			case MSG_UPGRADE_CALLBACK:
				if(mDialog!=null){
    					mDialog.dismissPrgoressDiglog();
    				}
				
				if(msg.arg1 == SamService.R_UPGRADE_OK){
					launchDialogActivity(getString(R.string.upgrade_succeed_title),getString(R.string.upgrade_succeed_statement));
				}else if(msg.arg1 == SamService.R_UPGRADE_FAILED){
					launchDialogActivity(getString(R.string.upgrade_failed_title),getString(R.string.upgrade_failed_statement));
				}else if (msg.arg1 == SamService.R_UPGRADE_ERROR){
					launchDialogActivity(getString(R.string.upgrade_failed_title),getString(R.string.upgrade_failed_statement));
				}
				break;
			}
		}
	};
	
}
