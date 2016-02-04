package com.android.samchat;

import com.android.samservice.*;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SignUpActivity extends Activity {

	static final String TAG="SamChat_SignUp";
	
	private boolean available_countrycode=true;
	private boolean available_phone=false;
	private boolean available_verifyCode=true;
	private boolean available_checkbox=true;
	
	/*country code*/
	private String mcc="86";
	/*phone number*/
	private String mpn;
	/*verified code*/
	private String mvc;
	
	LinearLayout mLayout_verify;
	RelativeLayout mWeixin_sign_layout;
	RelativeLayout mCancel_layout;
	TextView mPlus;
	EditText mCountryCode;
	EditText mPhoneNumber;
	TextView mSendVerifyCode;
	EditText mVerifyCode;
	Button mBtnVerify;
	CheckBox mCheckBox;
	TextView mReadProtocol;
	
	SamProcessDialog mDialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
           super.onCreate(savedInstanceState);
	      
           IntentFilter destroy_filter = new IntentFilter();
           destroy_filter.addAction(SamService.FINISH_ALL_SIGN_ACTVITY);
           registerReceiver(DestoryReceiver, destroy_filter);
	    
           setContentView(R.layout.sign_up);
	    
	    mLayout_verify = (LinearLayout)findViewById(R.id.layout_verify); 
	    mWeixin_sign_layout = (RelativeLayout)findViewById(R.id.weixin_sign_layout);  
	    mCancel_layout = (RelativeLayout)findViewById(R.id.cancel_layout);
	    mPlus = (TextView)findViewById(R.id.plus);
	    mCountryCode = (EditText)findViewById(R.id.countrycode);
	    mPhoneNumber = (EditText)findViewById(R.id.phonenumber);
	    mSendVerifyCode = (TextView)findViewById(R.id.send_verify_code);
	    mVerifyCode = (EditText)findViewById(R.id.verify_number);
	    mBtnVerify = (Button)findViewById(R.id.button_verify); 
	    mCheckBox = (CheckBox)findViewById(R.id.checkbox_protocol);
	    mReadProtocol = (TextView)findViewById(R.id.read_protocol);
	    
	    mPlus.setText("+");
	    mCountryCode.setText(mcc);
	    
	    mCountryCode.addTextChangedListener(CC_TextWatcher);
	    mPhoneNumber.addTextChangedListener(PN_TextWatcher);
	    mVerifyCode.addTextChangedListener(VC_TextWatcher);
	    mCheckBox.setOnCheckedChangeListener(new CK_Listner());
	    
	    mBtnVerify.setEnabled(false);
	    mBtnVerify.setClickable(false);
	    
	    mSendVerifyCode.setTextColor(Color.rgb(0x99, 0x99, 0x99));
	    mSendVerifyCode.setEnabled(false);
	    mSendVerifyCode.setClickable(false);
	    
	    /*sign in from weixin*/
	    mWeixin_sign_layout.setOnClickListener(new OnClickListener(){
	    	@Override
	    	public void onClick(View arg0) {
	    		SamLog.i(TAG,"sign in from weixin");
	    		//SignUpActivity.this.finish();
	    		/*launch the weixin sign activity*/
	    	}
	    	
	    });
	    
	    /*cancel from sign up activity*/
	    mCancel_layout.setOnClickListener(new OnClickListener(){
	    	@Override
	    	public void onClick(View arg0) {
	    		SamLog.i(TAG,"cancel from sign up activity");
	    		/*back to sign in*/
	    		SignUpActivity.this.finish();
	    	}
	    	
	    });
	    
	    /*request verify code*/
	    mSendVerifyCode.setOnClickListener(new OnClickListener(){
	    	@Override
	    	public void onClick(View arg0) {
	    		SamLog.i(TAG,"request verify code");
	    		/*call third party API to request server send verify code to the phone*/
	    	}
	    	
	    });
	    
	    /*verify the phone number*/
	    mBtnVerify.setOnClickListener(new OnClickListener(){
	    	@Override
	    	public void onClick(View arg0) {
	    		if(!NetworkMonitor.isNetworkAvailable()){
	    			launchDialogActivity(getString(R.string.nw_illegal_title),getString(R.string.network_status_no));
	    			return;
	    		}
	    		SamLog.i(TAG,"verify the phone number input by user");
	    		/*verify the code:compare to the one server received*/
	    		//do verify process
	    		if(!isSignParamIllegal()){
	    		/*launch the sign account activity*/
	    			launchSignAccountActivity();
	    		}
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
	    SamLog.i(TAG,"SignUpActivity onDestroy!");
	}
	
	private void launchSignAccountActivity()
	{
		Intent newIntent = new Intent(this,SignAccountActivity.class);
		int intentFlags = Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP;
		newIntent.setFlags(intentFlags);
		newIntent.putExtra("cellphone", mpn);
		startActivity(newIntent);
	}
	
	private void updateSendVerify(){
		boolean clickable = available_countrycode & available_phone;
		
		if(clickable){
			mSendVerifyCode.setTextColor(Color.rgb(0, 0, 0));
		}else{
			mSendVerifyCode.setTextColor(Color.rgb(0x99, 0x99, 0x99));
		}
		
		mSendVerifyCode.setEnabled(clickable);
		mSendVerifyCode.setClickable(clickable);
	}
	
	private void updateBtnVerify()
	{
		boolean clickable = available_countrycode & available_phone
				& available_verifyCode& available_checkbox;
		if(clickable){
			mBtnVerify.setTextColor(Color.rgb(255, 255, 255));
			mBtnVerify.setBackgroundColor(Color.rgb(0, 0x5F, 0xBF));
			mLayout_verify.setBackgroundColor(Color.rgb(0, 0x5F, 0xBF));
		}else{
			mBtnVerify.setTextColor(Color.rgb(0x99, 0x99, 0x99));
			mBtnVerify.setBackgroundColor(Color.rgb(0xBF, 0xBF, 0xBF));
			mLayout_verify.setBackgroundColor(Color.rgb(0xBF, 0xBF, 0xBF));
		}
		
		 mBtnVerify.setEnabled(clickable);
		 mBtnVerify.setClickable(clickable);
		
	}
	
	private TextWatcher VC_TextWatcher = new TextWatcher(){
		 @Override 
		    public void onTextChanged(CharSequence s, int start, int before, int count) { 
		        
		    }
		 
		    public void beforeTextChanged(CharSequence s, int start, int count,int after) { 
		       
		    } 

		    public void afterTextChanged(Editable s) { 
		    	mvc = mVerifyCode.getText().toString();
		    	SamLog.e("TAG","VerifyCode:"+mvc);
		    	if(mvc!=null & !mvc.equals("")){
		    		available_verifyCode = true;
		    	}else{
		    		available_verifyCode = true;
		    	}
		    	
		    	updateBtnVerify();
		    }     
	};
	
	private TextWatcher PN_TextWatcher = new TextWatcher(){
		 @Override 
		    public void onTextChanged(CharSequence s, int start, int before, int count) { 
		        
		    }
		 
		    public void beforeTextChanged(CharSequence s, int start, int count,int after) { 
		       
		    } 

		    public void afterTextChanged(Editable s) { 
		    	mpn = mPhoneNumber.getText().toString();
		    	SamLog.e("TAG","PhoneNumber:"+mpn);
		    	if(mpn!=null & !mpn.equals("") & mpn.length()>=SamService.MIN_MPHONE_NUMBER_LENGTH){
		    		SamLog.e("TAG","PhoneNumber is available");
		    		available_phone = true;
		    	}else{
		    		SamLog.e("TAG","PhoneNumber is still inavailable");
		    		available_phone = false;
		    	}
		    	
		    	updateBtnVerify();
		    	updateSendVerify();
		    }     
	};
	
	private TextWatcher CC_TextWatcher = new TextWatcher(){
		 @Override 
		    public void onTextChanged(CharSequence s, int start, int before, int count) { 
		        
		    }
		 
		    public void beforeTextChanged(CharSequence s, int start, int count,int after) { 
		       
		    } 

		    public void afterTextChanged(Editable s) { 
		    	mcc = mCountryCode.getText().toString();

		    	if(mcc!=null & !mcc.equals("")){
		    		available_countrycode = true;
		    	}else{
		    		available_countrycode = false;
		    	}
		    	
		    	updateBtnVerify();
		    	updateSendVerify();
		    }     
	};
	
	private class CK_Listner implements CompoundButton.OnCheckedChangeListener {
		@Override  
        public void onCheckedChanged(CompoundButton button,boolean isChecked){
			available_checkbox = mCheckBox.isChecked();
			updateBtnVerify();
		}  
		
	}; 
	
	private void launchProtocolURL()
	{
		Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        Uri content_url = Uri.parse("http://www.163.com");
        intent.setData(content_url);
        startActivity(intent);
	}
	
	private boolean isSignParamIllegal(){
		
		String errmsg=null;
		String errtitle=null;
		/*check country code illegal or not*/
		if(!mcc.equals("86")&!mcc.equals("086")&!mcc.equals("0086")
				& !mcc.equals("1")&!mcc.equals("01")&!mcc.equals("001")&!mcc.equals("0001")){
			errmsg = getString(R.string.cc_illegal_statement);
			errtitle = getString(R.string.cc_illegal_title);
			launchDialogActivity(errtitle,errmsg);
			return true;
		}
		/*check phone number illegal or not*/
		if(!SamService.isNumeric(mpn)){
			errmsg = getString(R.string.ph_illegal_statement);
			errtitle = getString(R.string.ph_illegal_title);
			launchDialogActivity(errtitle,errmsg);
			return true; 
		}
		/*check verify code illegal or not*/
		return false;
		
		
	}
	
	private void launchDialogActivity(String title,String msg){
		Intent newIntent = new Intent(this,DialogActivity.class);
		int intentFlags = Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP;
		newIntent.setFlags(intentFlags);
		newIntent.putExtra("title", title);
		newIntent.putExtra("message", msg);
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
