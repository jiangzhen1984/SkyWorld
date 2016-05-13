package com.android.samchat.activity;


import java.io.IOException;
import java.util.List;

import com.android.samchat.dialog.SamProcessDialog;
import com.android.samservice.*;
import com.android.samservice.info.LoginUser;
import com.netease.nim.demo.DemoCache;
import com.netease.nim.demo.R;
import com.netease.nim.demo.config.preference.Preferences;
import com.netease.nim.demo.config.preference.UserPreferences;
import com.netease.nim.demo.main.activity.MainActivity;
import com.netease.nim.uikit.cache.DataCacheManager;
import com.netease.nim.uikit.common.util.string.MD5;
import com.netease.nimlib.sdk.AbortableFuture;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.auth.AuthService;
import com.netease.nimlib.sdk.auth.LoginInfo;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
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
	
	static final String TAG="SignInActivity";

	public static int SAM_SIGNIN_TIMEOUT=20000;


	public static final int MSG_SIGNIN_CALLBACK = 1;
	public static final int MSG_SIGNIN_TIMEOUT = 2;
		
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

	private AbortableFuture<LoginInfo> loginRequest;

	private String tokenFromPassword(String password) {
		String appKey = readAppKey(this);
		boolean isDemo = "45c6af3c98409b18a84451215d0bdd6e".equals(appKey) || "fe416640c8e8a72734219e1847ad2547".equals(appKey);

		return isDemo ? MD5.getStringMD5(password) : password;
	}

	private static String readAppKey(Context context) {
		try {
			ApplicationInfo appInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
			if (appInfo != null) {
				return appInfo.metaData.getString("com.netease.nim.appKey");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	private void onLoginDone() {
		loginRequest = null;
	}

	private void login(final String account, String token) {
		loginRequest = NIMClient.getService(AuthService.class).login(new LoginInfo(account, token));
		loginRequest.setCallback(new RequestCallback<LoginInfo>() {
			@Override
			public void onSuccess(LoginInfo param) {
				onLoginDone();
				DemoCache.setAccount(account);
				NIMClient.toggleNotification(UserPreferences.getNotificationToggle());
				DataCacheManager.buildDataCacheAsync();

				LoginUser user = SamService.getInstance().get_current_user();
				user.seteasemob_status(LoginUser.ACTIVE);
				SamService.getInstance().getDao().updateLoginUserEaseStatus(user.getusername(),LoginUser.ACTIVE);

				Preferences.saveUserAccount(user.getusername());
				Preferences.saveUserToken(tokenFromPassword(user.getpassword()));
				
				if(mDialog!=null){
					mDialog.dismissPrgoressDiglog();
				}
				isSigning = false;
				launchMainActivity();
			}

			@Override
			public void onFailed(int code) {
				onLoginDone();
				
				if(mDialog!=null){
					SamLog.i("test","dismiss dialog in sign in activity EMcb onError");
					mDialog.dismissPrgoressDiglog();
				}

				isSigning = false;
				setErrorPop(getString(R.string.sign_in_failed_reason_others));
			}

			@Override
			public void onException(Throwable exception) {
				onLoginDone();
				
				if(mDialog!=null){
					SamLog.i("test","dismiss dialog in sign in activity EMcb onError");
					mDialog.dismissPrgoressDiglog();
				}

				isSigning = false;
				setErrorPop(getString(R.string.sign_in_failed_reason_others));
			}
		});
    }

	private void invalideAllLoginRecord(){
		SamService.getInstance().getDao().clear_LoginUser_db();
	}

	private void login_nim(){

		SamService.getInstance().startWaitThread();

		final String userName = SamService.getInstance().get_current_user().geteasemob_username();
		final String password = SamService.getInstance().get_current_user().getpassword();
		
		DemoCache.getApp().NimInit();
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				login(userName,tokenFromPassword(password));
			}
		}).start();

	}
		
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

	private void clearPreferences(){
	     Preferences.saveUserToken("");
	     Preferences.saveUserAccount("");
	}

	private void SignIn(){
		if(mDialog!=null){
    			mDialog.launchProcessDialog(SignInActivity.this,getString(R.string.sign_in_now));
    		}

		isSigning = true;

		invalideAllLoginRecord();
		clearPreferences();

		SignService.getInstance().SignIn(username,password,new SMCallBack(){
			@Override
			public void onSuccess(final Object obj) {
				runOnUiThread(new Runnable() {
					public void run() {
						login_nim();
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
	
}
