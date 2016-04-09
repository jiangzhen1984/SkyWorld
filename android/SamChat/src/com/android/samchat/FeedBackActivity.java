package com.android.samchat;

import com.android.samservice.SMCallBack;
import com.android.samservice.SamLog;
import com.android.samservice.SamService;

import android.app.Activity;
import android.content.Intent;
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



public class FeedBackActivity extends Activity {
	private final String TAG = "FeedBackActivity";

	private ImageView mBack;
	private EditText mComments;
	private LinearLayout mSend_layout;
	private TextView mButton_send;

	private String comments;
	private boolean comments_avaliable;

	private SamProcessDialog mDialog;
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_feedback);

		mDialog = new SamProcessDialog(this);

		mBack =  (ImageView) findViewById(R.id.back);
		mBack.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});

		mButton_send = (TextView) findViewById(R.id.button_send);

		mComments =  (EditText) findViewById(R.id.comments);
		mSend_layout = (LinearLayout) findViewById(R.id.send_layout);
		mSend_layout.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				SamLog.e(TAG,"send comments ...");
				sendComments(comments);
			}
		});

		mComments.addTextChangedListener(Comments_TextWatcher);
		
		
	}

	private void updateBtnSend()
	{
		boolean clickable = comments_avaliable;
		if(clickable){
			mSend_layout.setBackgroundColor(Color.rgb(0, 0x5F, 0xBF));
			mButton_send.setBackgroundColor(Color.rgb(0, 0x5F, 0xBF));
			mButton_send.setTextColor(Color.rgb(0xFF, 0xFF, 0xFF));
			
		}else{
			mSend_layout.setBackgroundColor(Color.rgb(0xBF, 0xBF, 0xBF));
			mButton_send.setBackgroundColor(Color.rgb(0xBF, 0xBF, 0xBF));
			mButton_send.setTextColor(Color.rgb(0x99, 0x99, 0x99));
		}
		
		mSend_layout.setEnabled(clickable);
		mSend_layout.setClickable(clickable);
		
	}

	private TextWatcher Comments_TextWatcher = new TextWatcher(){
		@Override 
		public void onTextChanged(CharSequence s, int start, int before, int count) { 
		        
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,int after) { 
		       
		} 

		@Override
		public void afterTextChanged(Editable s) { 
			comments = mComments.getText().toString().trim();
			if(comments!=null & !comments.equals("")){
				comments_avaliable = true;
			}else{
				comments_avaliable = false;
			}
		    	
			updateBtnSend();
		}     
	};



	private void sendComments(String comments){
		if(mDialog!=null){
    			mDialog.launchProcessDialog(this,getString(R.string.process));
    		}
		
		SamService.getInstance().send_comments(comments, new SMCallBack(){
			@Override
			public void onSuccess(Object obj) {
				runOnUiThread(new Runnable() {
					public void run() {
						if(mDialog!=null){
							mDialog.dismissPrgoressDiglog();
						}
						mComments.setText("");
						launchDialogActivity(getString(R.string.send_feedback_succeed),getString(R.string.send_feedback_succeed_statement));
						
					}
				});
			}

			@Override
			public void onFailed(int code) {
				runOnUiThread(new Runnable() {
					public void run() {
						if(mDialog!=null){
							mDialog.dismissPrgoressDiglog();
						}
						launchDialogActivity(getString(R.string.send_feedback_failed),getString(R.string.send_feedback_failed_statement));
					}
				});
			}

			@Override
			public void onError(int code) {
				runOnUiThread(new Runnable() {
					public void run() {
						if(mDialog!=null){
							mDialog.dismissPrgoressDiglog();
						}
						launchDialogActivity(getString(R.string.send_feedback_failed),getString(R.string.send_feedback_failed_statement));
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



