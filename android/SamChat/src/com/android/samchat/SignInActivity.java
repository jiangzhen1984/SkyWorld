package com.android.samchat;


import java.io.IOException;
import java.util.List;

import com.android.samservice.*;
import com.android.samservice.info.LoginUser;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
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
import android.widget.Toast;

public class SignInActivity extends Activity {
	
	static final String TAG="SamChat_Signin";

	public static int SAM_SIGNIN_TIMEOUT=30000;


	public static final int MSG_SIGNIN_CALLBACK = 1;
	public static final int MSG_SIGNIN_TIMEOUT = 2;	
	public static final int  MSG_EASEMOB_NAME_GOT_TIMEOUT = 3;
	//private boolean timeout_happened = false;

		
	private boolean available_username=false;
	private boolean available_password=false;
	private String username=null;
	private String password=null;
	
	private EditText mUsername;
	private EditText mPassword;

	private TextView mError_pop;
	private LinearLayout mLayout_error_pop;

	private LinearLayout mLayout_signin;
	private TextView mSignin;

	private TextView mSignup;
	private TextView mForgetpwd;
	
	
	private SamProcessDialog mDialog;

	private boolean isSigning=false;

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
					if(mDialog!=null){
						SamLog.i("test","dismiss dialog in sign in activity EMcb onSuccess");
						mDialog.dismissPrgoressDiglog();
					}
					isSigning = false;
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
			runOnUiThread(new Runnable() {
				public void run() {
					LoginUser user = SamService.getInstance().get_current_user();
					user.seteasemob_status(LoginUser.INACTIVE);
					SamService.getInstance().getDao().updateLoginUserEaseStatus(user.getusername(),LoginUser.INACTIVE);
			
					if(mDialog!=null){
						SamLog.i("test","dismiss dialog in sign in activity EMcb onError");
						mDialog.dismissPrgoressDiglog();
					}

					isSigning = false;
					//launchMainActivity();
					setErrorPop(getString(R.string.sign_in_failed_reason_others));
					invalideAllLoginRecord();
				}
			});
			
		}
	};

	private void invalideAllLoginRecord(){
		LoginUser user=null;
		List<LoginUser> array = SamService.getInstance().getDao().query_AllLoginUser_db();
		for(int i=0;i<array.size();i++){
			user = array.get(i);
			SamService.getInstance().getDao().updateLoginUserAllStatus(user.getusername(),LoginUser.INACTIVE);
		}
	}

	private void cancelEaseMobNameGotTimeOut() {
		mHandler.removeMessages(MSG_EASEMOB_NAME_GOT_TIMEOUT);
	}

	private void startEaseMobNameGotTimeOut() {
		Message msg = mHandler.obtainMessage(MSG_EASEMOB_NAME_GOT_TIMEOUT);
		// Reset timeout.
		cancelEaseMobNameGotTimeOut();
		mHandler.sendMessageDelayed(msg, SAM_SIGNIN_TIMEOUT);
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
		
	Handler mHandler = new Handler() {
	    @Override
	    public void handleMessage(Message msg) {
	    	if(SignInActivity.this == null){
	    		SamLog.e(TAG,"SignInActivity is killed, drop msg...");
	    		return;
	    	}
	    	
	        switch(msg.what) {
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

	private void clearErrorPop(){
		mLayout_error_pop.setVisibility(View.GONE);
	}

	private void setErrorPop(String errStr){
		mError_pop.setText(errStr);
		mError_pop.setTextColor(getResources().getColor(R.color.text_error_red));
		mLayout_error_pop.setVisibility(View.VISIBLE);
	}

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	      super.onCreate(savedInstanceState);
	      
		IntentFilter destroy_filter = new IntentFilter();
		destroy_filter.addAction(SamService.FINISH_ALL_SIGN_ACTVITY);
		registerReceiver(DestoryReceiver, destroy_filter);
	
		setContentView(R.layout.sign_in);
		
		mUsername = (EditText) findViewById(R.id.username);
		mPassword = (EditText) findViewById(R.id.password);

		mLayout_error_pop = (LinearLayout) findViewById(R.id.layout_error_pop);
		mError_pop = (TextView) findViewById(R.id.error_pop);
		clearErrorPop();

		mLayout_signin = (LinearLayout) findViewById(R.id.layout_signin);
		mSignin = (TextView) findViewById(R.id.signin);
		mForgetpwd = (TextView) findViewById(R.id.forget_pwd);
		mSignup = (TextView)findViewById(R.id.signup);   
		

		mSignin.setEnabled(false);
		mSignin.setClickable(false);
		mLayout_signin.setEnabled(false);
		mLayout_signin.setClickable(false);

		mUsername.addTextChangedListener(UN_TextWatcher);
		mPassword.addTextChangedListener(PW_TextWatcher);
	    
		mDialog = new SamProcessDialog(this);
		    
		/*launch sign up activity*/
		mSignup.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				SamLog.i(TAG,"launch sign up activity");
				clearErrorPop();				
				launchSignUpActivity();
			}
		});

		/*Start sign in process*/
		mLayout_signin.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				if(isSigning){
					return;
				}
				
				clearErrorPop();
				if(!NetworkMonitor.isNetworkAvailable()){
					launchDialogActivity(getString(R.string.nw_illegal_title),getString(R.string.network_status_no));
					return;
				}
	    		
				SamLog.i(TAG,"Start sign in process");

				SignIn();

			}
	    });
	    
		/*Start get back passwd process*/
		mForgetpwd.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				SamLog.i(TAG,"Start get back passwd process");
			}
		});
	    
	}

	private void SignIn(){
		if(mDialog!=null){
    			mDialog.launchProcessDialog(SignInActivity.this,getString(R.string.sign_in_now));
    		}

		isSigning = true;

		SignService.getInstance().SignIn(username,password,new SMCallBack(){
			@Override
			public void onSuccess(final Object obj) {
				runOnUiThread(new Runnable() {
					public void run() {
						SignInfo sInfo = (SignInfo)obj;
						/*store new token in cache*/
						try{
							SamFile sfd = new SamFile();
							sfd.writeSamFile(SamService.sam_cache_path , SamService.TOKEN_FILE,sInfo.token);
						}catch(IOException e){
							e.printStackTrace();
						}finally{
							login_easemob();
						}
					}
				});
			}

			

			@Override
			public void onFailed(final int code) {
				runOnUiThread(new Runnable() {
					public void run() {
						if(mDialog!=null){
							SamLog.i("test","dismiss dialog in sign in activity onFailed");
	    	    					mDialog.dismissPrgoressDiglog();
	    	    				}

						isSigning = false;
						
	            				if(code == SignService.RET_SI_FROM_SERVER_UP_ERROR){
			            			setErrorPop(getString(R.string.sign_in_failed_reason_unpd));
	            				}else{
							setErrorPop(getString(R.string.sign_in_failed_reason_others));
						}
					}
				});
			}

			@Override
			public void onError(final int code) {
				runOnUiThread(new Runnable() {
					public void run() {
						if(mDialog!=null){
							SamLog.i("test","dismiss dialog in sign in activity onError");
    							mDialog.dismissPrgoressDiglog();
    						}

						isSigning = false;

						if(code ==SignService.R_SIGN_IN_TIMEOUT){
							setErrorPop(getString(R.string.sign_in_timeout_statement));
							Toast.makeText(getApplicationContext(), getString(R.string.sign_in_timeout_statement), Toast.LENGTH_LONG).show();
						}else{
							setErrorPop(getString(R.string.sign_in_failed_reason_others));
						}
					}
				});
			}

		});

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
		SignService.getInstance().stopSignService();
		SamLog.i(TAG,"SignInActivity onDestroy!");
	}
	
	private void launchSignUpActivity()
	{
		Intent newIntent = new Intent(this,SignUpActivity.class);
		int intentFlags = Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP;
		newIntent.setFlags(intentFlags);
		startActivity(newIntent);
	}
	
	private void updateBtnSignin()
	{		
		boolean clickable = available_username & available_password;
		if(clickable){
			//mSignin.setTextColor(Color.rgb(255, 255, 255));
			mSignin.setTextColor(getResources().getColor(R.color.text_valid_white));
			
		}else{
			mSignin.setTextColor(getResources().getColor(R.color.text_invalid_gray));
			//mSignin.setTextColor(Color.rgb(0x99, 0x99, 0x99));
		}
		
		mLayout_signin.setEnabled(clickable);
		mLayout_signin.setClickable(clickable);

		clearErrorPop();
	}
	
	private TextWatcher UN_TextWatcher = new TextWatcher(){
		 @Override 
		    public void onTextChanged(CharSequence s, int start, int before, int count) { 
		        
		    }
		 
		    public void beforeTextChanged(CharSequence s, int start, int count,int after) { 
		       
		    } 

		    public void afterTextChanged(Editable s) { 
		    	username = mUsername.getText().toString();
		    	SamLog.e("TAG","username:"+ username);
		    	if(username!=null & !username.equals("") & username.length()>=SamService.MIN_USERNAME_LENGTH){
		    		available_username = true;
		    	}else{
		    		available_username = false;
		    	}
		    	
		    	updateBtnSignin();
			clearErrorPop();
		    }     
	};
	
	private TextWatcher PW_TextWatcher = new TextWatcher(){
		 @Override 
		    public void onTextChanged(CharSequence s, int start, int before, int count) { 
		        
		    }
		 
		    public void beforeTextChanged(CharSequence s, int start, int count,int after) { 
		       
		    } 

		    public void afterTextChanged(Editable s) { 
		    	password = mPassword.getText().toString();
		    	SamLog.e("TAG","password:"+ password);
		    	if(password!=null & !password.equals("")&password.length()>=SamService.MIN_PASSWORD_LENGTH){
		    		available_password = true;
		    	}else{
		    		available_password = false;
		    	}
		    	
		    	updateBtnSignin();
			clearErrorPop();
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
						EMClient.getInstance().login(userName,password,EMcb);
					}
				}).start();
				
				
			}	
	    }
	};
	
	
}
