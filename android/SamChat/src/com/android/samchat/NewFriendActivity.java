package com.android.samchat;

import java.util.ArrayList;

import com.android.samservice.SamLog;
import com.android.samservice.SamService;
import com.android.samservice.info.ReceivedQuestion;

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
import android.widget.ListView;
import android.widget.TextView;
import android.view.MotionEvent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.content.BroadcastReceiver;
import android.text.TextUtils;
import android.content.IntentFilter;
import android.util.Log;


public class NewFriendActivity extends Activity {
	private final String TAG = "NewFriendActivity";
	
	private Context mContext;
	private ImageView mBack;
	private EditText mSearch;
	private ImageView mCancel;
	
	private ListView mNewFriendList;
	//private NewFriendListAdapter mAdapter;

	private void closeInputMethod() {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		boolean isOpen = imm.isActive();
		if (isOpen) {
			imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    		}
	}

	
	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);
	
		setContentView(R.layout.activity_new_friend);
		mContext = getBaseContext();

		mBack =  (ImageView) findViewById(R.id.back);
		mBack.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				NewFriendActivity.this.setResult(1);
				finish();
			}
		    	
		});

		mSearch = (EditText) findViewById(R.id.search_input);
		mSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {  
			public boolean onEditorAction(TextView v, int actionId,KeyEvent event)  {    
				if (actionId==EditorInfo.IME_ACTION_SEARCH ||(event!=null&&event.getKeyCode()== KeyEvent.KEYCODE_ENTER)) { 
					SamLog.e(TAG, "start search new friend");
					closeInputMethod();
					return true;  
				}    
				return false;    
			}    
		}); 

		
		mCancel = (ImageView) findViewById(R.id.search_cancel);

		mCancel.setOnClickListener(new OnClickListener(){
		    	@Override
		    	public void onClick(View arg0) {
		    		mSearch.setText("");
		    	}
		});
		
	       
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		SamLog.e(TAG,"QA activity onNewIntent");
		this.onResume();
	}

	@Override
	public void onResume(){
		super.onResume();

	 }

	@Override
	public void onBackPressed(){
		this.setResult(1);
		finish();
	}


	@Override
	public void onDestroy(){
		super.onDestroy();
		this.setResult(1);
		SamLog.e(TAG,"new friend activity destroy and send broadcast");
		
		//Intent intent = new Intent();
		//intent.setAction(SamChats_Fragment.HIDE_QA_BADGE);
		//sendBroadcast(intent);	
	}
}

