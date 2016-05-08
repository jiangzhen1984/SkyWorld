package com.android.samchat;


import com.android.samservice.SamLog;

import android.app.Activity;
import android.content.Intent;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.view.MotionEvent;
import android.content.BroadcastReceiver;
import android.text.TextUtils;
import android.content.IntentFilter;
import android.util.Log;

public class DialogActivity extends Activity implements View.OnClickListener{
	private final String LOG_TAG = "DialogActivity";
	private TextView mMessageView;
	private TextView mTitleView;;
	private String mTextMsg;
	private String mTitle;
	
	public static final int OK_BUTTON = R.id.button_ok;
	public static final int CANCEL_BUTTON = R.id.button_cancel;
	
	@Override
	    protected void onCreate(Bundle icicle) {
	        super.onCreate(icicle);
	
	        requestWindowFeature(Window.FEATURE_LEFT_ICON);
	
	        setContentView(R.layout.dialog);
	
	        TextView okButton = (TextView) findViewById(R.id.button_ok);
	        TextView cancelButton = (TextView) findViewById(R.id.button_cancel);
	
	        okButton.setOnClickListener(this);
	        cancelButton.setOnClickListener(this);
	
	       
	    }
	
	 @Override
	 public boolean onTouchEvent(MotionEvent event) {
	      return false;
	 }
	 
	 @Override
	 public void onResume(){
		 super.onResume();
		 mMessageView = (TextView) findViewById(R.id.dialog_message);
		 mTitleView = (TextView) findViewById(R.id.dialog_title);
		 initFromIntent(getIntent());
		 
		 if(mTextMsg==null || mTitle==null){
			 finish();
		 }
		 
		 mMessageView.setText(mTextMsg);
		 mTitleView.setText(mTitle);
		 
	 }
	 
	   @Override
	       public boolean onKeyDown(int keyCode, KeyEvent event) {
	           switch (keyCode) {
	           case KeyEvent.KEYCODE_BACK:
			 this.setResult(-1);
	               finish();
	               break;
	           }
	           return false;
	       }
	   
	   public void onClick(View v) {
		           //String input = null;
		           SamLog.e("test","onClick not in two button");
		           switch (v.getId()) {
		               case OK_BUTTON:
		            	   this.setResult(1);
		                   break;
		               case CANCEL_BUTTON:
		            	   this.setResult(-1);
		                   break;
		           }
		           finish();
		       }
	 
	 private void initFromIntent(Intent intent) {
		 
		         if (intent != null) {
		             mTextMsg = intent.getStringExtra("message");
		             mTitle = intent.getStringExtra("title");
		         } else {
		             finish();
		         }
		     }

	 @Override
	 public void onDestroy(){
		super.onDestroy();
	 }

	 @Override
	 public void finish(){
	 	super.finish();
	 }
	
}
