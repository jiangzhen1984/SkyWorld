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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SignUpActivity extends Activity {

	static final String TAG="SamChat_SignUp";
	
	private boolean available_countrycode=true;
	private boolean available_phone=false;
	
	/*country code*/
	private String mcc="86";
	/*phone number*/
	private String mpn;
	/*verified code*/
	private String mvc;

	private ImageView mBack;
	private TextView mCountry;
	private ImageView mClear;
	private LinearLayout mLayout_verify;
	private TextView mPlus;
	private EditText mCountryCode;
	private EditText mPhoneNumber;

	private Button mBtnVerify;
	
	
	SamProcessDialog mDialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
           super.onCreate(savedInstanceState);
	      
           IntentFilter destroy_filter = new IntentFilter();
           destroy_filter.addAction(SamService.FINISH_ALL_SIGN_ACTVITY);
           registerReceiver(DestoryReceiver, destroy_filter);
	    
           setContentView(R.layout.sign_up);

	    mBack = (ImageView)findViewById(R.id.back); 
	    mCountry = (TextView)findViewById(R.id.country);
	    mClear = (ImageView)findViewById(R.id.clear); 
	    mLayout_verify = (LinearLayout)findViewById(R.id.layout_verify); 
	    mPlus = (TextView)findViewById(R.id.plus);
	    mCountryCode = (EditText)findViewById(R.id.countrycode);
	    mPhoneNumber = (EditText)findViewById(R.id.phonenumber);
	    mBtnVerify = (Button)findViewById(R.id.button_verify); 
	    
	    
	    mPlus.setText("+");
	    mCountryCode.setText(mcc);
	    mCountry.setText(getString(R.string.China));
	    
	    mCountryCode.addTextChangedListener(CC_TextWatcher);
	    mPhoneNumber.addTextChangedListener(PN_TextWatcher);
	    
	    
	    mBtnVerify.setEnabled(false);
	    mBtnVerify.setClickable(false);
	    
	    mClear.setOnClickListener(new OnClickListener(){
	    	@Override
	    	public void onClick(View arg0) {
	    		mPhoneNumber.setText("");
	    	}
	    	
	    });
	    
	    /*cancel from sign up activity*/
	    mBack.setOnClickListener(new OnClickListener(){
	    	@Override
	    	public void onClick(View arg0) {
	    		SamLog.i(TAG,"cancel from sign up activity");
	    		/*back to sign in*/
	    		SignUpActivity.this.finish();
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
	
	private void updateBtnVerify()
	{
		boolean clickable = available_countrycode & available_phone ;
		if(clickable){
			mBtnVerify.setTextColor(getResources().getColor(R.color.text_valid_white));
		}else{
			mBtnVerify.setTextColor(getResources().getColor(R.color.text_invalid_gray));
		}
		
		 mBtnVerify.setEnabled(clickable);
		 mBtnVerify.setClickable(clickable);
		
	}
	
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

			if(mcc.equals("86")){
				mCountry.setText(getString(R.string.China));
			}else if(mcc.equals("1")||mcc.equals("01")){
				mCountry.setText(getString(R.string.USA));
			}else{
				mCountry.setText("");
			}
		    	
		    	updateBtnVerify();
		    }     
	};
	
	 
	
	
	
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
