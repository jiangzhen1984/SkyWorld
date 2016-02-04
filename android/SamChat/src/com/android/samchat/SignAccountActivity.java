package com.android.samchat;

import java.io.IOException;

import com.android.samservice.*;
import com.android.samservice.info.LoginUser;
import com.easemob.EMCallBack;
import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatManager;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SignAccountActivity extends Activity {
	
	static final String TAG="SamChat_SignAccount";
	public static final int MSG_SIGNUP_CALLBACK = 1;
	public static final int MSG_SIGNUP_TIMEOUT= 2;
	public static final int  MSG_EASEMOB_NAME_GOT_TIMEOUT = 3;

	public static final int SAM_SIGNUP_TIMEOUT=20000;
	
	private boolean available_username=false;
	private boolean available_password=false;
	private String username=null;
	private String password=null;
	private String cellphone=null;

	private boolean timeout_happened = false;
	
	LinearLayout mLayout_verify;
	RelativeLayout mWeixin_sign_layout;
	RelativeLayout mCancel_layout;
	EditText mUsername;
	TextView mUsed;
	EditText mPassword;
	TextView mSafe;
	Button mBtnSignup;
	
	SamProcessDialog mDialog;


	EMCallBack EMcb = new EMCallBack() {//»Øµ÷
		@Override
		public void onSuccess() {
			runOnUiThread(new Runnable() {
				public void run() {
					SamLog.i(TAG,"login easemob successfully");
					//EMChatManager.getInstance().updateCurrentUserNick(SamService.getInstance().get_current_user().getusername());
					EMChat.getInstance().setAutoLogin(true);
					LoginUser user = SamService.getInstance().get_current_user();
					user.seteasemob_status(LoginUser.ACTIVE);
					SamService.getInstance().getDao().updateLoginUserEaseStatus(user.getphonenumber(),LoginUser.ACTIVE);
					if(mDialog!=null){
						mDialog.dismissPrgoressDiglog();
					}
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
			SamService.getInstance().getDao().updateLoginUserEaseStatus(user.getphonenumber(),LoginUser.INACTIVE);

			if(mDialog!=null){
				mDialog.dismissPrgoressDiglog();
			}
			
			launchMainActivity();
		}
	};

	private void cancelEaseMobNameGotTimeOut() {
		mHandler.removeMessages(MSG_EASEMOB_NAME_GOT_TIMEOUT);
	}

	private void startEaseMobNameGotTimeOut() {
		Message msg = mHandler.obtainMessage(MSG_EASEMOB_NAME_GOT_TIMEOUT);
		// Reset timeout.
		cancelTimeOut();
		mHandler.sendMessageDelayed(msg, SAM_SIGNUP_TIMEOUT);
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
				EMChatManager.getInstance().login(userName,password,EMcb);
			}
		}).start();
	}
	
	Handler mHandler = new Handler() {
	    @Override
	    public void handleMessage(Message msg) {
	    	if(SignAccountActivity.this == null){
	    		SamLog.e(TAG,"SignAccountActivity is killed, drop msg...");
	    		return;
	    	}
	    	
	        switch(msg.what) {
	            case MSG_SIGNUP_CALLBACK:
			cancelTimeOut();
			if(timeout_happened){
				SamLog.i(TAG,"drop msg due to timeout happened");
				break;
			}
	            	if(msg.arg1==SignService.R_SIGN_UP_OK){
	            		SamLog.i(TAG,"sign up succeed!");
				SignInfo sInfo = (SignInfo)msg.obj;
				/*store new token and username/pwd in cache*/
				try{
					SamFile sfd = new SamFile();
					sfd.writeSamFile(SamService.sam_cache_path ,SamService.TOKEN_FILE,sInfo.token);
					sfd.writeSamFile(SamService.sam_cache_path,SamService.UP_FILE,sInfo.username+","+sInfo.password);
	            			
				}catch(IOException e){
					e.printStackTrace(); 
				}finally{
					login_easemob();
					//if(mDialog!=null){
					//	mDialog.dismissPrgoressDiglog();
					//}
					//launchMainActivity();
				}
	            	}else if(msg.arg1 == SignService.R_SIGN_UP_FAILED){
				if(mDialog!=null){
	    	    			mDialog.dismissPrgoressDiglog();
	    	    		}

				if(msg.arg2 == SignService.RET_SU_FROM_SERVER_CELL_UN_EXISTED){
					/*username or phone number has been registered*/
					launchDialogActivity(getString(R.string.sign_up_failed_title),getString(R.string.sign_up_failed_reason_unph));
				}else{
					launchDialogActivity(getString(R.string.sign_up_failed_title),getString(R.string.sign_up_failed_reason_others));
				}
				
			}else if(msg.arg1==SignService.R_SIGN_UP_ERROR){
	            		SamLog.w(TAG, "sign up error!");
	            		if(mDialog!=null){
	    	    			mDialog.dismissPrgoressDiglog();
	    	    		}
	            		launchDialogActivity(getString(R.string.sign_up_failed_title),getString(R.string.sign_up_failed_reason_others));
	            	}
			break;

		    case MSG_SIGNUP_TIMEOUT:
				timeout_happened = true;
				mHandler.removeCallbacksAndMessages(null);
				if(mDialog!=null){
	    	    			mDialog.dismissPrgoressDiglog();
	    	    		}
	            		launchDialogActivity(getString(R.string.sign_up_timeout_title),getString(R.string.sign_up_timeout_statement));
				break;
		    case MSG_EASEMOB_NAME_GOT_TIMEOUT:
				SamLog.ship(TAG,"MSG_EASEMOB_NAME_GOT_TIMEOUT happened...");
				cancelEaseMobNameGotTimeOut();
				unregisterReceiver(EaseMobNameGotReceiver);
				if(mDialog!=null){
	    	    			mDialog.dismissPrgoressDiglog();
	    	    		}
				launchMainActivity();
				break;
	        }
	    }
	 };

	private void cancelTimeOut() {
		mHandler.removeMessages(MSG_SIGNUP_TIMEOUT);
	}

	private void startTimeOut() {
		Message msg = mHandler.obtainMessage(MSG_SIGNUP_TIMEOUT);
		// Reset timeout.
		cancelTimeOut();
		mHandler.sendMessageDelayed(msg, SAM_SIGNUP_TIMEOUT);
	}
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	      super.onCreate(savedInstanceState);
	      
		IntentFilter destroy_filter = new IntentFilter();
		destroy_filter.addAction(SamService.FINISH_ALL_SIGN_ACTVITY);
		registerReceiver(DestoryReceiver, destroy_filter);
		
		setContentView(R.layout.sign_account);
	    
	    mLayout_verify = (LinearLayout)findViewById(R.id.layout_signup); 
	    mWeixin_sign_layout = (RelativeLayout)findViewById(R.id.weixin_sign_layout);  
	    mCancel_layout = (RelativeLayout)findViewById(R.id.cancel_layout);
	    mUsername = (EditText)findViewById(R.id.username);
	    mUsed = (TextView)findViewById(R.id.used);
	    mPassword = (EditText)findViewById(R.id.password);
	    mSafe = (TextView)findViewById(R.id.safe); 
	    mBtnSignup = (Button)findViewById(R.id.button_signup);
	    
	    mUsername.addTextChangedListener(UN_TextWatcher);
	    mPassword.addTextChangedListener(PW_TextWatcher);
	    
	    mBtnSignup.setEnabled(false);
	    mBtnSignup.setClickable(false);
	    
	    mDialog = new SamProcessDialog();

           cellphone = getIntent().getStringExtra("cellphone");
	    /*sign in from weixin*/
	    mWeixin_sign_layout.setOnClickListener(new OnClickListener(){
	    	@Override
	    	public void onClick(View arg0) {
	    		SamLog.i(TAG,"sign in from weixin");
	    		//SignAccountActivity.this.finish();
	    		/*launch the weixin sign activity*/
	    	}
	    	
	    });
	    
	    /*cancel from sign up activity*/
	    mCancel_layout.setOnClickListener(new OnClickListener(){
	    	@Override
	    	public void onClick(View arg0) {
	    		SamLog.i(TAG,"cancel from sign up activity");
	    		/*back to sign in*/
	    		SignAccountActivity.this.finish();
	    	}
	    	
	    });
	    
	    /*sign up*/
	    mBtnSignup.setOnClickListener(new OnClickListener(){
	    	@Override
	    	public void onClick(View arg0) {
	    		if(!NetworkMonitor.isNetworkAvailable()){
	    			launchDialogActivity(getString(R.string.nw_illegal_title),getString(R.string.network_status_no));
	    			return;
	    		}
	    		SamLog.i(TAG,"do the sign up process "+"uname:"+username+"pwd:"+password+"cellphone:"+cellphone);
	    		if(mDialog!=null){
	    			mDialog.launchProcessDialog(SignAccountActivity.this,getString(R.string.sign_up_now));
	    		}
	    		SignService.getInstance().SignUp(mHandler, MSG_SIGNUP_CALLBACK,username,password,cellphone);
			timeout_happened=false;
			startTimeOut();
	    	}
	    	
	    });
	}
	
	@Override
	protected void onPause() {
	    super.onPause();
	    //finish();
	}
	 
	@Override
	protected void onResume(){
	    super.onResume();
	}
	 
	@Override
	protected void onDestroy(){
	    super.onDestroy();
	    unregisterReceiver(DestoryReceiver);
	    SamLog.i(TAG,"SignAccountActivity onDestroy!");
	}
	
	
	private void updateBtnSignup()
	{
		boolean clickable = available_username & available_password;
		if(clickable){
			mBtnSignup.setTextColor(Color.rgb(255, 255, 255));
			mBtnSignup.setBackgroundColor(Color.rgb(0, 0x5F, 0xBF));
			mLayout_verify.setBackgroundColor(Color.rgb(0, 0x5F, 0xBF));
		}else{
			mBtnSignup.setTextColor(Color.rgb(0x99, 0x99, 0x99));
			mBtnSignup.setBackgroundColor(Color.rgb(0xBF, 0xBF, 0xBF));
			mLayout_verify.setBackgroundColor(Color.rgb(0xBF, 0xBF, 0xBF));
		}
		
		mBtnSignup.setEnabled(clickable);
		mBtnSignup.setClickable(clickable);
		
	}
	
	private TextWatcher UN_TextWatcher = new TextWatcher(){
		@Override 
		public void onTextChanged(CharSequence s, int start, int before, int count) { 
		        
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,int after) { 
		       
		} 

		@Override
		public void afterTextChanged(Editable s) { 
			username = mUsername.getText().toString();
			SamLog.e("TAG","username:"+ username);
			if(username!=null & !username.equals("") & username.length()>=SamService.MIN_USERNAME_LENGTH){
				available_username = true;
			}else{
				available_username = false;
			}
		    	
			updateBtnSignup();
		}     
	};
	
	private TextWatcher PW_TextWatcher = new TextWatcher(){
		@Override 
		public void onTextChanged(CharSequence s, int start, int before, int count) { 
		        
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,int after) { 
		       
		}
		
		@Override
		public void afterTextChanged(Editable s) { 
			password = mPassword.getText().toString();
			SamLog.e("TAG","password:"+ password);
			if(password!=null & !password.equals("") & password.length()>=SamService.MIN_PASSWORD_LENGTH){
				available_password = true;
			}else{
				available_password = false;
			}
			updateBtnSignup();
		}    
	};
	

	private void launchDialogActivity(String title,String msg){
		Intent newIntent = new Intent(this,DialogActivity.class);
		int intentFlags = Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP;
		newIntent.setFlags(intentFlags);
		newIntent.putExtra("title", title);
		newIntent.putExtra("message", msg);
		startActivity(newIntent);
	}
	
	
	private void launchMainActivity(){
		Intent newIntent = new Intent(this,MainActivity.class);
		int intentFlags = Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP;
		newIntent.setFlags(intentFlags);
		startActivity(newIntent);
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
						EMChatManager.getInstance().login(userName,password,EMcb);
					}
				}).start();
			}	
	    }
	};
}
