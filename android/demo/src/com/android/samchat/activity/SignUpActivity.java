package com.android.samchat.activity;

import com.android.samchat.dialog.SamProcessDialog;
import com.android.samservice.*;
import com.netease.nim.demo.R;


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
	private boolean available_verifycode=true;

	static final int CHINA=1;
	static final int USA=2;

	private Context mContext;
	
	/*country code*/
	private String mcc="86";
	/*phone number*/
	private String mpn;
	/*verified code*/
	private String mvc;

	private int country_selected=CHINA;

	private RelativeLayout mBack_layout;
	private LinearLayout mLayout_china;
	private ImageView mChina_select_button;
	private TextView mChina;

	private LinearLayout mLayout_usa;
	private ImageView mUsa_select_button;
	private TextView mUsa;

	private LinearLayout mLayout_verify;
	private TextView mPlus;
	private EditText mCountryCode;
	private EditText mPhoneNumber;
	private TextView mSend_verify_code;

	private EditText mInput_verify_code;

	private Button mBtnVerify;
	
	
	SamProcessDialog mDialog;

	private void countrySelect(int country){
		if(country == CHINA){
			country_selected = CHINA;
			mcc = "86";
			mCountryCode.setText(mcc);
			mCountryCode.setTextColor((mContext.getResources().getColor(R.color.common_bg_green)));
			mChina.setTextColor((mContext.getResources().getColor(R.color.common_bg_green)));
			mChina_select_button.setImageResource(R.drawable.select);
			mUsa.setTextColor(Color.rgb(0x00, 0x00, 0x00));
			mUsa_select_button.setImageResource(R.drawable.not_select);
						
			
		}else if(country == USA){
			country_selected = USA;
			mcc = "1";
			mCountryCode.setText(mcc);
			mCountryCode.setTextColor((mContext.getResources().getColor(R.color.common_bg_green)));
			mUsa.setTextColor((mContext.getResources().getColor(R.color.common_bg_green)));
			mUsa_select_button.setImageResource(R.drawable.select);
			mChina.setTextColor(Color.rgb(0x00, 0x00, 0x00));
			mChina_select_button.setImageResource(R.drawable.select);
			
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
           super.onCreate(savedInstanceState);
	      
           IntentFilter destroy_filter = new IntentFilter();
           destroy_filter.addAction(SamService.FINISH_ALL_SIGN_ACTVITY);
           registerReceiver(DestoryReceiver, destroy_filter);
	    
           setContentView(R.layout.sign_up);
	    mContext = getBaseContext();

	    mBack_layout = (RelativeLayout)findViewById(R.id.back_layout); 
	    mLayout_china = (LinearLayout)findViewById(R.id.layout_china); 
	    mChina_select_button=(ImageView)findViewById(R.id.china_select_button); 
	    mChina= (TextView)findViewById(R.id.china);

	    mLayout_usa = (LinearLayout)findViewById(R.id.layout_usa); 
	    mUsa_select_button=(ImageView)findViewById(R.id.usa_select_button); 
	    mUsa= (TextView)findViewById(R.id.usa);
		
	    
	    mPlus = (TextView)findViewById(R.id.plus);
	    mCountryCode = (EditText)findViewById(R.id.countrycode);
	    mPhoneNumber = (EditText)findViewById(R.id.phonenumber);
	    mSend_verify_code =  (TextView)findViewById(R.id.send_verify_code);

	    mInput_verify_code =(EditText)findViewById(R.id.input_verify_code); 
	    mBtnVerify = (Button)findViewById(R.id.button_verify); 
	    mLayout_verify = (LinearLayout)findViewById(R.id.layout_verify); 
	    
	    mPlus.setText("+");
	    mPlus.setTextColor((mContext.getResources().getColor(R.color.common_bg_green)));
	    
	    //mCountryCode.addTextChangedListener(CC_TextWatcher);
	    mPhoneNumber.addTextChangedListener(PN_TextWatcher);
	    
	    
	    mBtnVerify.setEnabled(false);
	    mBtnVerify.setClickable(false);
	    mLayout_verify.setEnabled(false);
	    mLayout_verify.setClickable(false);

	    countrySelect(country_selected);
	    
	    /*cancel from sign up activity*/
	    mBack_layout.setOnClickListener(new OnClickListener(){
	    	@Override
	    	public void onClick(View arg0) {
	    		SamLog.i(TAG,"cancel from sign up activity");
	    		/*back to sign in*/
	    		SignUpActivity.this.finish();
	    	}
	    	
	    });
	    
    
	    /*verify the phone number*/
	    mLayout_verify.setOnClickListener(new OnClickListener(){
	    	@Override
	    	public void onClick(View arg0) {
	    		if(!NetworkMonitor.isNetworkAvailable()){
	    			launchDialogActivity(getString(R.string.nw_illegal_title),getString(R.string.network_status_no));
	    			return;
	    		}
	    		SamLog.i(TAG,"verify the phone number input by user");
	    		/*verify the code:compare to the one server received*/
	    		//do verify process
	    		//if(!isSignParamIllegal()){
	    		/*launch the sign account activity*/
	    		launchSignAccountActivity();
	    		//}
	    	}
	    	
	    });


	    mLayout_china.setOnClickListener(new OnClickListener(){
	    	@Override
	    	public void onClick(View arg0) {
	    		if(country_selected !=CHINA){
	    			countrySelect(CHINA);
			}
	    	}
	    	
	    });

	    mLayout_usa.setOnClickListener(new OnClickListener(){
	    	@Override
	    	public void onClick(View arg0) {
	    		if(country_selected !=USA){
	    			countrySelect(USA);
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
		newIntent.putExtra(Constants.CELLPHONE_NUMBER, mpn);
		newIntent.putExtra(Constants.COUNTRY_CODE,mcc);
		startActivity(newIntent);
	}
	
	private void updateBtnVerify()
	{
		boolean clickable = available_countrycode & available_phone & available_verifycode ;
		if(clickable){
			mBtnVerify.setTextColor(getResources().getColor(R.color.text_valid_white));
		}else{
			mBtnVerify.setTextColor(getResources().getColor(R.color.text_invalid_gray));
		}
		
		 mLayout_verify.setEnabled(clickable);
		 mLayout_verify.setClickable(clickable);
		
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
	
	/*private TextWatcher CC_TextWatcher = new TextWatcher(){
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
				mcc = "1";
				mCountry.setText(getString(R.string.USA));
			}else{
				mCountry.setText("");
			}
		    	
		    	updateBtnVerify();
		    }     
	};*/
	
	 
	
	
	
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
