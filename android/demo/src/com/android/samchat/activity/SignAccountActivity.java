package com.android.samchat.activity;

import java.io.IOException;
import java.util.List;

import com.android.samchat.dialog.SamProcessDialog;
import com.android.samservice.*;
import com.android.samservice.info.*;
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
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.InputType;
import android.text.Selection;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class SignAccountActivity extends Activity {
	
	static final String TAG="SamChat_SignAccount";
	public static final int MSG_SIGNUP_CALLBACK = 1;
	public static final int MSG_SIGNUP_TIMEOUT= 2;
	public static final int MSG_EASEMOB_NAME_GOT_TIMEOUT = 3;
	public static final int MSG_CHECK_UP_UNIQUE=4;

	public static final int SAM_SIGNUP_TIMEOUT=60000;
	public static final int CHECK_UP_UNIQUE_TIMEOUT=2000;

	private boolean available_checkbox=true;
	private boolean available_username=false;
	private boolean available_password=false;
	private boolean unique_up=false;
	private String username=null;
	private String password=null;
	private String cellphone=null;
	private String country_code=null;

	private boolean isPasswordShow=false;
	private ImageView mShow_passwd;

	private ImageView mBack;
	private LinearLayout mLayout_signup;
	private EditText mUsername;
	private EditText mPassword;
	private Button mBtnSignup;
	private TextView mError_pop;

	private ImageView mCheckBox;
	private TextView mReadProtocol;
	
	SamProcessDialog mDialog;

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
					SamLog.i("test","dismiss dialog in sign in activity EMcb onSuccess");
					mDialog.dismissPrgoressDiglog();
				}
				isSigning = false;
				launchMainActivity(true);
			}

			@Override
			public void onFailed(int code) {
				onLoginDone();
				
				if(mDialog!=null){
					SamLog.i("test","dismiss dialog in sign in activity EMcb onError");
					mDialog.dismissPrgoressDiglog();
				}

				isSigning = false;
				setErrorPop(getString(R.string.sign_up_succeed_but_sign_in_failed));
				SamService.getInstance().stopSamService();
			}

			@Override
			public void onException(Throwable exception) {
				onLoginDone();
				
				if(mDialog!=null){
					SamLog.i("test","dismiss dialog in sign in activity EMcb onError");
					mDialog.dismissPrgoressDiglog();
				}

				isSigning = false;
				setErrorPop(getString(R.string.sign_up_succeed_but_sign_in_failed));
				SamService.getInstance().stopSamService();
			}
		});
    }

	private void invalideAllLoginRecord(){
		SamService.getInstance().getDao().clear_LoginUser_db();
	}

	private void clearPreferences(){
	     Preferences.saveUserToken("");
	     Preferences.saveUserAccount("");
	}

	private void login_Nim(){

		SamService.getInstance().startWaitThread();

		final String userName = SamService.getInstance().get_current_user().geteasemob_username();
		final String password = SamService.getInstance().get_current_user().getpassword();

		DemoCache.getApp().NimInit();

		new Thread(new Runnable() {
			@Override
			public void run() {
				login(userName, tokenFromPassword(password));
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
		    case MSG_CHECK_UP_UNIQUE:
				SamLog.e(TAG,"MSG_CHECK_UP_UNIQUE happened...");
				checkUN(username);
				break;
	        }
	    }
	 };

	void update_unique_up(boolean unique){
		unique_up = unique;
		if(unique){
			
		}else{
			setErrorPop(getString(R.string.sign_up_uname_existed));
		}

		updateBtnSignup();
	}

	private void checkUN(String username){
		String testname = username;
		if(testname == null || testname.equals("") ||testname.length()<SamService.MIN_USERNAME_LENGTH){
			return;
		}

		SamLog.e(TAG,"start query_user_info_from_server...");
		SamService.getInstance().query_user_existed_withOutToken_from_server(testname,new SMCallBack(){
			@Override
			public void onSuccess(final Object obj){
				runOnUiThread(new Runnable() {
					public void run() {
						List<ContactUser> list = (List<ContactUser>)obj;
						if(list!=null && list.size()>0){
							SamLog.e(TAG,"show error");
							update_unique_up(false);
						}else{
							SamLog.e(TAG,"no show error");
							update_unique_up(true);
						}

					}
				});
			} 

			@Override
			public void onFailed(int code) {
				runOnUiThread(new Runnable() {
					public void run() {
						update_unique_up(true);

					}
				});
			}

			@Override
			public void onError(int code) {
				runOnUiThread(new Runnable() {
					public void run() {
						update_unique_up(true);

					}
				});
			}

		});
	}


	private void cancelCheck() {
		SamLog.e(TAG,"cancelCheck...");
		mHandler.removeMessages(MSG_CHECK_UP_UNIQUE);
	}

	private void startCheck() {
		Message msg = mHandler.obtainMessage(MSG_CHECK_UP_UNIQUE);
		cancelCheck();
		mHandler.sendMessageDelayed(msg, CHECK_UP_UNIQUE_TIMEOUT);
		SamLog.e(TAG,"startCheck...");
	}

	private void clearErrorPop(){
		mError_pop.setText("");
		mError_pop.setTextColor(getResources().getColor(R.color.text_right_black));
		mError_pop.setVisibility(View.GONE);
	}
	
	private void setErrorPop(String errStr){
		SamLog.e(TAG,"setErrorPop...");
		mError_pop.setText(errStr);
		mError_pop.setTextColor(getResources().getColor(R.color.text_error_red));
		mError_pop.setVisibility(View.VISIBLE);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
	      super.onCreate(savedInstanceState);
	      
		IntentFilter destroy_filter = new IntentFilter();
		destroy_filter.addAction(SamService.FINISH_ALL_SIGN_ACTVITY);
		registerReceiver(DestoryReceiver, destroy_filter);
		
		setContentView(R.layout.sign_account);

	    mBack = (ImageView)findViewById(R.id.back); 
	    mLayout_signup = (LinearLayout)findViewById(R.id.layout_signup); 
	    mUsername = (EditText)findViewById(R.id.username);
	    mPassword = (EditText)findViewById(R.id.password);
	    mBtnSignup = (Button)findViewById(R.id.button_signup);
	    mCheckBox = (ImageView)findViewById(R.id.CheckBox);
	    mReadProtocol = (TextView)findViewById(R.id.read_protocol);

	    mShow_passwd = (ImageView)findViewById(R.id.show_passwd);

	    mError_pop = (TextView)findViewById(R.id.error_pop);
	    clearErrorPop();

	    //mCheckBox.setOnCheckedChangeListener(new CK_Listner());
	    mCheckBox.setOnClickListener(new OnClickListener(){
	    	@Override
	    	public void onClick(View arg0) {
			if(available_checkbox){
				available_checkbox  = false;
				mCheckBox.setImageResource(R.drawable.not_select);
				updateBtnSignup();
			}else{
				available_checkbox  = true;
				mCheckBox.setImageResource(R.drawable.select);
				updateBtnSignup();
			}
		}
	    	
	    });
	    
	    mUsername.addTextChangedListener(UN_TextWatcher);
	    mPassword.addTextChangedListener(PW_TextWatcher);
	    
	    mBtnSignup.setEnabled(false);
	    mBtnSignup.setClickable(false);
	    mLayout_signup.setEnabled(false);
	    mLayout_signup.setClickable(false);
	    
	    mDialog = new SamProcessDialog(this);

           cellphone = getIntent().getStringExtra(Constants.CELLPHONE_NUMBER);
           country_code = getIntent().getStringExtra(Constants.COUNTRY_CODE);	   
	    
	    mBack.setOnClickListener(new OnClickListener(){
	    	@Override
	    	public void onClick(View arg0) {
	    		SamLog.i(TAG,"cancel from sign account activity");
	    		SignAccountActivity.this.finish();
	    	}
	    	
	    });
	    
	    /*sign up*/
	    mLayout_signup.setOnClickListener(new OnClickListener(){
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
	    		SamLog.i(TAG,"do the sign up process "+"uname:"+username+"pwd:"+password+"cellphone:"+cellphone);
	  		SignUp();
	    	}
	    	
	    });

	    /*read the SAM authentication protocol*/
	    mReadProtocol.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG ); 
	    mReadProtocol.getPaint().setAntiAlias(true);
	    mReadProtocol.setOnClickListener(new OnClickListener(){
	    	@Override
	    	public void onClick(View arg0) {
	    		SamLog.i(TAG,"read the SAM authentication protocol");
	    		/*launch the protocol text */
	    		launchProtocolURL();
	    	}
	    	
	    });

	    mShow_passwd.setOnClickListener(new OnClickListener(){
	    	@Override
	    	public void onClick(View arg0) {
			if(!isPasswordShow){
				isPasswordShow = true;
				mPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
				Editable etable = mPassword.getText();
				Selection.setSelection(etable, etable.length());
			}else{
				isPasswordShow = false;
				mPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
				Editable etable = mPassword.getText();
				Selection.setSelection(etable, etable.length());
			}
		}
	    	
	    });
		
	}

	private void launchProtocolURL()
	{
		Intent intent = new Intent();
		intent.setAction("android.intent.action.VIEW");
		Uri content_url = Uri.parse("http://www.163.com");
		intent.setData(content_url);
		startActivity(intent);
	}


	private void SignUp(){
		isSigning = true;
		
		if(mDialog!=null){
    			mDialog.launchProcessDialog(SignAccountActivity.this,getString(R.string.sign_up_now));
    		}

		invalideAllLoginRecord();
		clearPreferences();

		SignService.getInstance().SignUp(username,password,cellphone,country_code,new SMCallBack(){
			@Override
			public void onSuccess(final Object obj) {
				runOnUiThread(new Runnable() {
					public void run() {
						login_Nim();
					}
				});
			}

			

			@Override
			public void onFailed(final int code) {
				runOnUiThread(new Runnable() {
					public void run() {
						if(mDialog!=null){
	    	    					mDialog.dismissPrgoressDiglog();
	    	    				}

						isSigning = false;
						
	            				if(code == SignService.RET_SU_FROM_SERVER_CELL_UN_EXISTED){
			            			setErrorPop(getString(R.string.sign_up_failed_reason_unph));
	            				}else{
							setErrorPop(getString(R.string.sign_up_failed_reason_others));
						}
					}
				});
			}

			@Override
			public void onError(final int code) {
				runOnUiThread(new Runnable() {
					public void run() {
						if(mDialog!=null){
    							mDialog.dismissPrgoressDiglog();
    						}

						isSigning = false;

						if(code ==SignService.R_SIGN_UP_TIMEOUT){
							setErrorPop(getString(R.string.sign_up_timeout_statement));
							Toast.makeText(getApplicationContext(), getString(R.string.sign_up_timeout_statement), Toast.LENGTH_LONG).show();
						}else{
							setErrorPop(getString(R.string.sign_up_failed_reason_others));
						}
					}
				});
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
		boolean clickable = available_username & available_password & unique_up & available_checkbox;

		if(clickable){
			//mSignin.setTextColor(Color.rgb(255, 255, 255));
			mBtnSignup.setTextColor(getResources().getColor(R.color.text_valid_white));
			
		}else{
			mBtnSignup.setTextColor(getResources().getColor(R.color.text_invalid_gray));
			//mSignin.setTextColor(Color.rgb(0x99, 0x99, 0x99));
		}
		
		mLayout_signup.setEnabled(clickable);
		mLayout_signup.setClickable(clickable);
		//clearErrorPop();
		
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
			unique_up = false;
			updateBtnSignup();
			username = mUsername.getText().toString();
			SamLog.e("TAG","username:"+ username);
			if(username!=null & !username.equals("") & username.length()>=SamService.MIN_USERNAME_LENGTH){
				available_username = true;
			}else{
				available_username = false;
			}

			if(available_username){
				cancelCheck();
				startCheck();
			}
		    	
			//updateBtnSignup();
			clearErrorPop();
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
			//clearErrorPop();
		}    
	};

	/*private class CK_Listner implements CompoundButton.OnCheckedChangeListener {
		@Override  
        	public void onCheckedChanged(CompoundButton button,boolean isChecked){
			available_checkbox = mCheckBox.isChecked();
			updateBtnSignup();
		}  
		
	};*/
	

	private void launchDialogActivity(String title,String msg){
		Intent newIntent = new Intent(this,DialogActivity.class);
		int intentFlags = Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP;
		newIntent.setFlags(intentFlags);
		newIntent.putExtra("title", title);
		newIntent.putExtra("message", msg);
		startActivity(newIntent);
	}
	
	
	private void launchMainActivity(boolean success){
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
