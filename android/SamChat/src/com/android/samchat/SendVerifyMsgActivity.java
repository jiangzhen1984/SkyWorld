package com.android.samchat;

import java.util.ArrayList;

import com.android.samservice.SamLog;
import com.android.samservice.SamService;
import com.android.samservice.info.ReceivedQuestion;
import com.easemob.chat.EMContactManager;
import com.easemob.exceptions.EaseMobException;

import android.app.Activity;
import android.content.Intent;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.view.MotionEvent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.content.BroadcastReceiver;
import android.text.TextUtils;
import android.content.IntentFilter;
import android.util.Log;


public class SendVerifyMsgActivity extends Activity {
	private final String TAG = "SendVerifyMsgActivity";
	public static int MSG_SEND_VERIFY_MSG_CALLBACK = 1;

	private Context mContext;
	private ImageView mBack;
	private LinearLayout mSend_msg_action_layout;
	private EditText mSend_msg_input;

	private SamProcessDialog mDialog;
	private String easemob_name;

	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		mDialog = new SamProcessDialog();
	
		setContentView(R.layout.activity_send_verify_msg);
		mContext = getBaseContext();

		initFromIntent(getIntent());

		mBack =  (ImageView) findViewById(R.id.back);
		mBack.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});

		mSend_msg_action_layout = (LinearLayout) findViewById(R.id.send_msg_action_layout);
		mSend_msg_input =  (EditText) findViewById(R.id.send_msg_input);

		mSend_msg_action_layout.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				String verify_msg = mSend_msg_input.getText().toString().trim();
				if(!verify_msg.equals("")){
					/*send verify_msg to server*/
					closeInputMethod();
					if(mDialog!=null){
    						mDialog.launchProcessDialog(SendVerifyMsgActivity.this,getString(R.string.process));
    					}
					
					if(!sendVerifyMsg(easemob_name,verify_msg)){
						if(mDialog!=null){
	    						mDialog.dismissPrgoressDiglog();
	    					}

						Toast.makeText(mContext, getString(R.string.send_msg_failed), Toast.LENGTH_SHORT).show(); 
						finish();
					}else{
						if(mDialog!=null){
	    						mDialog.dismissPrgoressDiglog();
	    					}
						
						Toast.makeText(mContext, getString(R.string.send_msg_succeed), Toast.LENGTH_SHORT).show(); 
						finish();

					}
				}
				return ;
			}
		});
	       
	}

	private boolean sendVerifyMsg(String easemob_name, String verify_msg){
	/*use easemob interface*/
		try{
			SamLog.e(TAG,"easemob_name:"+easemob_name+" reason:"+verify_msg);
			EMContactManager.getInstance().addContact(easemob_name, verify_msg);
		}catch(EaseMobException e){
			e.printStackTrace(); 
	    		return false;
		}

		return true;

	}

	private void closeInputMethod() {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		boolean isOpen = imm.isActive();
		if (isOpen) {
			imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    		}
	}

	private void initFromIntent(Intent intent) {
		if (intent != null) {
			easemob_name = intent.getStringExtra("easemob_name");
			if(easemob_name == null){
				SamLog.ship(TAG,"easemob_name is null, fatal error");
				finish();
			}
		}else{
			SamLog.ship(TAG,"initFromIntent is null, fatal error");
			finish();
		}
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		SamLog.e(TAG,"onNewIntent");
		this.onResume();
	}

	@Override
	public void onResume(){
		super.onResume();
		

	 }

	@Override
	public void onBackPressed(){
		finish();
	}


	@Override
	public void onDestroy(){
		super.onDestroy();	
	}

	
}



