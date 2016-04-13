package com.android.samchat;

import java.util.ArrayList;
import java.util.List;

import com.android.samservice.SMCallBack;
import com.android.samservice.SamService;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class VendorInfoInputActivity extends Activity {
	private final String TAG = "VendorInfoInputActivity";

	private Context mContext;
	private LinearLayout mBack_layout;
	private EditText mBussiness_line_input;
	private EditText mBussiness_location_input;
	private EditText mBussiness_introduction_input;
	private TextView mOK;
	private ImageView mAgree;

	private String origin_line = null;
	private String origin_location = null;
	private String origin_introduction = null;

	private String new_line = null;
	private String new_location = null;
	private String new_introduction = null;

	private boolean available_line = false;
	private boolean available_location = false;
	private boolean available_introduction = false;
	private boolean available_agree=true;

	private SamProcessDialog mDialog;

	private void initFromIntent(Intent intent) {
		if (intent != null) {
			origin_line = intent.getStringExtra("line");
			origin_location = intent.getStringExtra("location"); 
			origin_introduction = intent.getStringExtra("introduction"); 

		}else{
			setResult(-1);
			finish();
		}
	}

	@Override
	public void onBackPressed(){
		setResult(-1);
		finish();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_vendor_info_input);
		mDialog = new SamProcessDialog(this);

		mContext = getBaseContext();

		mBack_layout =  (LinearLayout) findViewById(R.id.back_layout);
		mBack_layout.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				setResult(-1);
				finish();
			}
		    	
		});

		mAgree = (ImageView) findViewById(R.id.agree);
		mAgree.setImageResource(R.drawable.select);
		mAgree.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				if(!available_agree){
					available_agree = true;
					mAgree.setImageResource(R.drawable.select);
				}else{
					available_agree = false;
					mAgree.setImageResource(R.drawable.not_select);
				}
			}
		    	
		});

		mBussiness_line_input = (EditText)findViewById(R.id.bussiness_line_input);
		mBussiness_line_input.addTextChangedListener(Line_TextWatcher);

		mBussiness_location_input = (EditText)findViewById(R.id.bussiness_location_input);
		mBussiness_location_input.addTextChangedListener(Location_TextWatcher);

		mBussiness_introduction_input = (EditText)findViewById(R.id.bussiness_introduction_input);
		mBussiness_introduction_input.addTextChangedListener(Introduction_TextWatcher);

		mOK =  (TextView)findViewById(R.id.ok);
		mOK.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				upgradeToServicer();
			}
		    	
		});

		initFromIntent(getIntent());

		if(origin_line!=null){
			mBussiness_line_input.setText(origin_line);
		}

		if(origin_location!=null){
			mBussiness_location_input.setText(origin_location);
		}

		if(origin_introduction!=null){
			mBussiness_introduction_input.setText(origin_introduction);
		}

		mOK.setTextColor(getResources().getColor(R.color.text_invalid_gray));
		mOK.setEnabled(false);
		mOK.setClickable(false);
		
	}


	private TextWatcher Line_TextWatcher = new TextWatcher(){
		 @Override 
		    public void onTextChanged(CharSequence s, int start, int before, int count) { 
		        
		    }
		 
		    public void beforeTextChanged(CharSequence s, int start, int count,int after) { 
		       
		    } 

		    public void afterTextChanged(Editable s) {
				if(origin_line == null){
					new_line = mBussiness_line_input.getText().toString().trim();
		    			if(new_line!=null & !new_line.equals("")){
		    				available_line= true;
		    			}else{
		    				available_line = false;
		    			}
				}else{
					new_line = mBussiness_line_input.getText().toString().trim();
					new_location = mBussiness_location_input.getText().toString().trim();
					new_introduction = mBussiness_introduction_input.getText().toString().trim();
					
					if(new_line == null || new_location == null || new_introduction == null){
						available_line = false;
						available_location = false;
						available_introduction = false;
					}else if(new_line.equals("") || new_location.equals("") || new_introduction.equals("")){
						available_line = false;
						available_location = false;
						available_introduction = false;
					}else if(new_line.equals(origin_line) && new_location.equals(origin_location) && new_introduction.equals(origin_introduction)){
						available_line = false;
						available_location = false;
						available_introduction = false;
					}else{
						available_line = true;
						available_location = true;
						available_introduction = true;
					}

				}

				updateOK();
		    }     
	};

	private TextWatcher Location_TextWatcher = new TextWatcher(){
		 @Override 
		    public void onTextChanged(CharSequence s, int start, int before, int count) { 
		        
		    }
		 
		    public void beforeTextChanged(CharSequence s, int start, int count,int after) { 
		       
		    } 

		    public void afterTextChanged(Editable s) {
				if(origin_location == null){
					new_location = mBussiness_location_input.getText().toString().trim();
		    			if(new_location!=null & !new_location.equals("")){
		    				available_location= true;
		    			}else{
		    				available_location = false;
		    			}
				}else{
					new_line = mBussiness_line_input.getText().toString().trim();
					new_location = mBussiness_location_input.getText().toString().trim();
					new_introduction = mBussiness_introduction_input.getText().toString().trim();
					
					if(new_line == null || new_location == null || new_introduction == null){
						available_line = false;
						available_location = false;
						available_introduction = false;
					}else if(new_line.equals("") || new_location.equals("") || new_introduction.equals("")){
						available_line = false;
						available_location = false;
						available_introduction = false;
					}else if(new_line.equals(origin_line) && new_location.equals(origin_location) && new_introduction.equals(origin_introduction)){
						available_line = false;
						available_location = false;
						available_introduction = false;
					}else{
						available_line = true;
						available_location = true;
						available_introduction = true;
					}

				}

				updateOK();
		    }     
	};

	private TextWatcher Introduction_TextWatcher = new TextWatcher(){
		 @Override 
		    public void onTextChanged(CharSequence s, int start, int before, int count) { 
		        
		    }
		 
		    public void beforeTextChanged(CharSequence s, int start, int count,int after) { 
		       
		    } 

		    public void afterTextChanged(Editable s) {
				if(origin_introduction== null){
					new_introduction = mBussiness_introduction_input.getText().toString().trim();
		    			if(new_introduction!=null & !new_introduction.equals("")){
		    				available_introduction = true;
		    			}else{
		    				available_introduction = false;
		    			}
				}else{
					new_line = mBussiness_line_input.getText().toString().trim();
					new_location = mBussiness_location_input.getText().toString().trim();
					new_introduction = mBussiness_introduction_input.getText().toString().trim();
					
					if(new_line == null || new_location == null || new_introduction == null){
						available_line = false;
						available_location = false;
						available_introduction = false;
					}else if(new_line.equals("") || new_location.equals("") || new_introduction.equals("")){
						available_line = false;
						available_location = false;
						available_introduction = false;
					}else if(new_line.equals(origin_line) && new_location.equals(origin_location) && new_introduction.equals(origin_introduction)){
						available_line = false;
						available_location = false;
						available_introduction = false;
					}else{
						available_line = true;
						available_location = true;
						available_introduction = true;
					}

				}

				updateOK();
		    }     
	};


	private void updateOK()
	{		
		boolean clickable = available_line & available_location & available_introduction & available_agree;
		if(clickable){
			mOK.setTextColor(getResources().getColor(R.color.text_valid_white));
			
		}else{
			mOK.setTextColor(getResources().getColor(R.color.text_invalid_gray));
			//mSignin.setTextColor(Color.rgb(0x99, 0x99, 0x99));
		}
		
		mOK.setEnabled(clickable);
		mOK.setClickable(clickable);
	}

	private void upgradeToServicer(){
		if(mDialog!=null){
    			mDialog.launchProcessDialog(this,getString(R.string.process));
    		}

		SamVendorInfo vInfo = new SamVendorInfo(new_line,new_location,new_introduction);

		SamService.getInstance().upgrade(vInfo, new SMCallBack(){
			@Override
			public void onSuccess(final Object obj){
				if(VendorInfoInputActivity.this == null ||VendorInfoInputActivity.this.isFinishing() ){
					return;
				}

				runOnUiThread(new Runnable() {
					public void run() {
						if(mDialog!=null){
    							mDialog.dismissPrgoressDiglog();
    						}
						
						VendorInfoInputActivity.this.setResult(1);
						VendorInfoInputActivity.this.finish();

					}
				});
				
			} 

			@Override
			public void onFailed(int code) {
				if(VendorInfoInputActivity.this == null ||VendorInfoInputActivity.this.isFinishing() ){
					return;
				}
				
				runOnUiThread(new Runnable() {
					public void run() {
						if(mDialog!=null){
    							mDialog.dismissPrgoressDiglog();
    						}

						launchDialogActivity(getString(R.string.upgrade_failed_title),getString(R.string.upgrade_failed_statement));

					}
				});
			
				
				
			}

			

			@Override
			public void onError(int code) {
				if(VendorInfoInputActivity.this == null ||VendorInfoInputActivity.this.isFinishing() ){
					return;
				}
				
				runOnUiThread(new Runnable() {
					public void run() {
						if(mDialog!=null){
    							mDialog.dismissPrgoressDiglog();
    						}

						launchDialogActivity(getString(R.string.upgrade_failed_title),getString(R.string.upgrade_failed_statement));

					}
				});				
			}

		});


	

		
	}

	private void launchDialogActivity(String title,String msg){
		Intent newIntent = new Intent(this,DialogActivity.class);
		int intentFlags = Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP;
		newIntent.setFlags(intentFlags);
		newIntent.putExtra("title", title);
		newIntent.putExtra("message", msg);
		startActivity(newIntent);
	}
	

}
