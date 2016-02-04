package com.android.samchat;

import com.android.samservice.SamLog;
import com.android.samservice.SamService;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;



public class SecuritySettingActivity extends Activity {
	private final String TAG = "SecuritySettingActivity";

	private ImageView mBack;
	private EditText mOld_passwd;
	private EditText mNew_passwd;
	private EditText mConfrim_passwd;

	private LinearLayout mButton_ok_layout;
	private LinearLayout mButton_cancel_layout;
	private TextView mButton_ok;
	private TextView mButton_cancel;

	private String old_passwd;
	private String new_passwd;
	private String confirm_passwd;

	private boolean available_old_passwd;
	private boolean available_new_passwd;
	private boolean available_confirm_passwd;

	private void updateBtn()
	{		
		boolean clickable = available_old_passwd & available_new_passwd & available_confirm_passwd;
		if(clickable){
			mButton_ok_layout.setBackgroundColor(Color.rgb(0, 0x5F, 0xBF));
			mButton_ok.setBackgroundColor(Color.rgb(0, 0x5F, 0xBF));
			mButton_ok.setTextColor(Color.rgb(0xFF, 0xFF, 0xFF));
			
		}else{
			mButton_ok_layout.setBackgroundColor(Color.rgb(0xBF, 0xBF, 0xBF));
			mButton_ok.setBackgroundColor(Color.rgb(0xBF, 0xBF, 0xBF));
			mButton_ok.setTextColor(Color.rgb(0x99, 0x99, 0x99));
		}
		
		mButton_ok_layout.setEnabled(clickable);
		mButton_ok_layout.setClickable(clickable);
	}


	private TextWatcher Old_TextWatcher = new TextWatcher(){
		 @Override 
		    public void onTextChanged(CharSequence s, int start, int before, int count) { 
		        
		    }
		 
		    public void beforeTextChanged(CharSequence s, int start, int count,int after) { 
		       
		    } 

		    public void afterTextChanged(Editable s) { 
		    	old_passwd = mOld_passwd.getText().toString();
		    	SamLog.e("TAG","old_passwd:"+ old_passwd);
		    	if(old_passwd!=null & !old_passwd.equals("") & old_passwd.length()>=SamService.MIN_PASSWORD_LENGTH){
		    		available_old_passwd = true;
		    	}else{
		    		available_old_passwd = false;
		    	}
		    	
		    	updateBtn();
		    }     
	};

	private TextWatcher New_TextWatcher = new TextWatcher(){
		 @Override 
		    public void onTextChanged(CharSequence s, int start, int before, int count) { 
		        
		    }
		 
		    public void beforeTextChanged(CharSequence s, int start, int count,int after) { 
		       
		    } 

		    public void afterTextChanged(Editable s) { 
		    	new_passwd = mNew_passwd.getText().toString();
		    	SamLog.e("TAG","new_passwd:"+ new_passwd);
		    	if(new_passwd!=null & !new_passwd.equals("") & new_passwd.length()>=SamService.MIN_PASSWORD_LENGTH){
		    		available_new_passwd = true;
		    	}else{
		    		available_new_passwd = false;
		    	}
		    	
		    	updateBtn();
		    }     
	};

	private TextWatcher Confirm_TextWatcher = new TextWatcher(){
		 @Override 
		    public void onTextChanged(CharSequence s, int start, int before, int count) { 
		        
		    }
		 
		    public void beforeTextChanged(CharSequence s, int start, int count,int after) { 
		       
		    } 

		    public void afterTextChanged(Editable s) { 
		    	confirm_passwd = mConfrim_passwd.getText().toString();
		    	SamLog.e("TAG","confirm_passwd:"+ confirm_passwd);
		    	if(confirm_passwd!=null & !confirm_passwd.equals("") & confirm_passwd.length()>=SamService.MIN_PASSWORD_LENGTH){
		    		available_confirm_passwd = true;
		    	}else{
		    		available_confirm_passwd = false;
		    	}
		    	
		    	updateBtn();
		    }     
	};
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_securitysettings);

		available_old_passwd=false;
		available_new_passwd=false;
		available_confirm_passwd=false;

		mBack =  (ImageView) findViewById(R.id.back);
		mBack.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});

		mOld_passwd = (EditText) findViewById(R.id.old_passwd);
		mOld_passwd.addTextChangedListener(Old_TextWatcher);
	

		mNew_passwd = (EditText) findViewById(R.id.new_passwd);
		mNew_passwd.addTextChangedListener(New_TextWatcher);
		

		mConfrim_passwd = (EditText) findViewById(R.id.confrim_passwd);
		mConfrim_passwd.addTextChangedListener(Confirm_TextWatcher);
		
		mButton_ok_layout = (LinearLayout) findViewById(R.id.button_ok_layout);
		mButton_cancel_layout = (LinearLayout) findViewById(R.id.button_cancel_layout);
		mButton_ok = (TextView) findViewById(R.id.button_ok);
		mButton_cancel = (TextView) findViewById(R.id.button_cancel);

		updateBtn();

		mButton_cancel_layout.setBackgroundColor(Color.rgb(0xFF, 0x66, 0x00));
		mButton_cancel.setBackgroundColor(Color.rgb(0xFF, 0x66, 0x00));
		mButton_cancel.setTextColor(Color.rgb(0xFF, 0xFF, 0xFF));

		mButton_cancel_layout.setEnabled(true);
		mButton_cancel_layout.setClickable(true);
		
	}
	

}



