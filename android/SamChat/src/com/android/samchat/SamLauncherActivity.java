package com.android.samchat;

import java.io.IOException;
import java.util.List;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import com.android.samservice.*;
import com.android.samservice.info.LoginUser;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;


/* Author KevinDong
 * This is the launcher activity.SamLuancherActivity will below 3 function:
 * 1. Launch the first advertisement UI
 * 2. help  to launch sign in activity 
 * 3. do automatic sign in function
 */

public class SamLauncherActivity extends Activity {
	static final String TAG="SamChat";
	public static int SAM_LAUNCHER_TIMEOUT=30000;


	public static final int MSG_AUTOLOGIN_CALLBACK = 1;
	public static final int MSG_LAUNCHER_TIMEOUT = 2;
	public static final int MSG_EASEMOB_NAME_GOT_TIMEOUT=3;

	private boolean timeout_happened = false;

	EMCallBack EMcb = new EMCallBack() {//»Øµ÷
		@Override
		public void onSuccess() {
			runOnUiThread(new Runnable() {
				public void run() {
					SamLog.i(TAG,"login easemob successfully");
					//EMChat.getInstance().setAutoLogin(true);
					//EMChatManager.getInstance().updateCurrentUserNick(SamService.getInstance().get_current_user().getusername());
					LoginUser user = SamService.getInstance().get_current_user();
					user.seteasemob_status(LoginUser.ACTIVE);
					SamService.getInstance().getDao().updateLoginUserEaseStatus(user.getusername(),LoginUser.ACTIVE);
					launchMainActivity();
				}
			});
		}

		@Override
		public void onProgress(int progress, String status) {
 
		}
 
		@Override
		public void onError(int code, String message) {
			SamLog.ship(TAG,"login easemob failed code:"+code+ " message:" + message);
			LoginUser user = SamService.getInstance().get_current_user();
			user.seteasemob_status(LoginUser.INACTIVE);
			SamService.getInstance().getDao().updateLoginUserEaseStatus(user.getusername(),LoginUser.INACTIVE);
			
			invalideAllLoginRecord();
	            	launchSignInActivity();
		}
	};

	private void cancelEaseMobNameGotTimeOut() {
		mHandler.removeMessages(MSG_EASEMOB_NAME_GOT_TIMEOUT);
	}

	private void startEaseMobNameGotTimeOut() {
		Message msg = mHandler.obtainMessage(MSG_EASEMOB_NAME_GOT_TIMEOUT);
		// Reset timeout.
		cancelTimeOut();
		mHandler.sendMessageDelayed(msg, SAM_LAUNCHER_TIMEOUT);
	}


	private void login_easemob(){

		SamService.getInstance().startWaitThread();
		skyworld.EaseMobInit();

		final String userName = SamService.getInstance().get_current_user().geteasemob_username();
		final String password = SamService.getInstance().get_current_user().getpassword();

		if(userName == null){
			IntentFilter easemob_filter = new IntentFilter();
			easemob_filter.addAction(SamService.EASEMOBNAMEGOT);
			registerReceiver(EaseMobNameGotReceiver, easemob_filter);
			startEaseMobNameGotTimeOut();
			return;
		}
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				EMClient.getInstance().login(userName,password,EMcb);
			}
		}).start();
	}


	private void invalideAllLoginRecord(){
		LoginUser user=null;
		List<LoginUser> array = SamService.getInstance().getDao().query_AllLoginUser_db();
		for(int i=0;i<array.size();i++){
			user = array.get(i);
			SamService.getInstance().getDao().updateLoginUserAllStatus(user.getusername(),LoginUser.INACTIVE);
		}
	}
	
	Handler mHandler = new Handler() {
	    @Override
	    public void handleMessage(Message msg) {
	    	if(SamLauncherActivity.this == null){
	    		SamLog.e(TAG,"SamLauncherActivity is killed, drop msg...");
	    		return;
	    	}
	    	
	        switch(msg.what) {
	            case MSG_AUTOLOGIN_CALLBACK:
			//cancelTimeOut();
			if(timeout_happened){
				SamLog.i(TAG,"drop msg due to timeout happened");
				break;
			}
			
	            	if(msg.arg1==SignService.R_AUTO_SIGN_IN_OK){
	            		SamLog.i(TAG,"SamLauncherActivity auto login succeed!");
				login_easemob();
	            	}else if(msg.arg1==SignService.R_AUTO_SIGN_IN_NO_HISTORY){
	            		SamLog.w(TAG, "SamLauncherActivity auto login no history!");
				invalideAllLoginRecord();
	            		launchSignInActivity();
	            	}else if(msg.arg1==SignService.R_AUTO_SIGN_IN_TIMEOUT){
	            		SamLog.w(TAG, "SamLauncherActivity auto login timeout!");
	            		//launchMainActivity();
	            		invalideAllLoginRecord();
	            		launchSignInActivity();
	            	}else if(msg.arg1==SignService.R_AUTO_SIGN_IN_FAILED){
	            		SamLog.w(TAG, "SamLauncherActivity auto login failed!");
				invalideAllLoginRecord();
	            		launchSignInActivity();
	            	}
				break;
			case MSG_LAUNCHER_TIMEOUT:
				SamLog.i(TAG,"MSG_LAUNCHER_TIMEOUT happened");
				timeout_happened = true;
				mHandler.removeCallbacksAndMessages(null);
				invalideAllLoginRecord();
				launchSignInActivity();
				break;
			case MSG_EASEMOB_NAME_GOT_TIMEOUT:
				SamLog.ship(TAG,"MSG_EASEMOB_NAME_GOT_TIMEOUT happened...");
				cancelEaseMobNameGotTimeOut();
				unregisterReceiver(EaseMobNameGotReceiver);

				
				final String userName = Constants.USERNAME_EQUAL_EASEMOB_ID?
										SamService.getInstance().get_current_user().getusername():
										SamService.getInstance().get_current_user().getphonenumber();
				final String password = SamService.getInstance().get_current_user().getpassword();
				
				new Thread(new Runnable() {
					@Override
					public void run() {
						EMClient.getInstance().login(userName,password,EMcb);
					}
				}).start();
				break;
	        }
	    }
	 };

	private void cancelTimeOut() {
		mHandler.removeMessages(MSG_LAUNCHER_TIMEOUT);
	}

	private void startTimeOut() {
		Message msg = mHandler.obtainMessage(MSG_LAUNCHER_TIMEOUT);
		// Reset timeout.
		cancelTimeOut();
		mHandler.sendMessageDelayed(msg, SAM_LAUNCHER_TIMEOUT);
	}

	private void launchMainActivity()
	{
		Intent newIntent = new Intent(this,MainActivity.class);
		int intentFlags = Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP;
		newIntent.setFlags(intentFlags);
		startActivity(newIntent);
		
	}
	
	private void launchSignInActivity()
	{
		Intent newIntent = new Intent(this,SignInActivity.class);
		int intentFlags = Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP;
		newIntent.setFlags(intentFlags);
		startActivity(newIntent);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		IntentFilter destroy_filter = new IntentFilter();
		destroy_filter.addAction(SamService.FINISH_ALL_SIGN_ACTVITY);
		registerReceiver(DestoryReceiver, destroy_filter);

		setContentView(R.layout.activity_main);

		/*Check network is available or not*/
		if(!NetworkMonitor.isNetworkAvailable()){
			Toast.makeText(getApplicationContext(), 
				getString(R.string.network_status_no), Toast.LENGTH_LONG).show(); 
		}

		timeout_happened = false;
		SamService.getInstance(this).initSamService();
		SignService.getInstance().attemptAutoSignIn(mHandler, MSG_AUTOLOGIN_CALLBACK);
		//startTimeOut();
	      
	}
	 
	@Override
	protected void onPause() {
	    super.onPause();
	}
	 
	@Override
	protected void onResume(){
	    super.onResume();
	}
	
	@Override
	protected void onDestroy(){
		super.onDestroy();
		unregisterReceiver(DestoryReceiver);
		SamLog.i(TAG,"LauncherActivity onDestroy!");
	}
	 
 
	private BroadcastReceiver DestoryReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if(intent.getAction().equals(SamService.FINISH_ALL_SIGN_ACTVITY)){
				finish();
			}	
	    }
	};

	private BroadcastReceiver EaseMobNameGotReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if(intent.getAction().equals(SamService.EASEMOBNAMEGOT)){
				cancelEaseMobNameGotTimeOut();
				final String userName = SamService.getInstance().get_current_user().geteasemob_username();
				final String password = SamService.getInstance().get_current_user().getpassword();
				
				unregisterReceiver(EaseMobNameGotReceiver);
				
				new Thread(new Runnable() {
					@Override
					public void run() {
						EMClient.getInstance().login(userName,password,EMcb);
					}
				}).start();
			}	
	    }
	};
}
