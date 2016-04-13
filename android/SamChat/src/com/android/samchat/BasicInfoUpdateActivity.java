package com.android.samchat;

import java.util.ArrayList;

import com.android.samservice.Constants;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.view.MotionEvent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.content.BroadcastReceiver;
import android.text.TextUtils;
import android.content.IntentFilter;
import android.util.Log;


public class BasicInfoUpdateActivity extends Activity {
	private final String TAG = "BasicInfoUpdateActivity";

	public static final String NEW_INFO = "new_info";

	public static final int BASIC_INFO_ADDRESS = 0;
	public static final int BASIC_INFO_HOBBY=1;

	private Context mContext;
	private RelativeLayout mBack_layout;
	private TextView mBasic_info;
	private LinearLayout mStore_layout;
	private EditText mInfo_input;

	private SamProcessDialog mDialog;

	private int info_type;
	private String default_info;
	private String info;

	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		mDialog = new SamProcessDialog(this);
	
		setContentView(R.layout.activity_basic_info_update);
		mContext = getBaseContext();

		initFromIntent(getIntent());

		mBack_layout=(RelativeLayout) findViewById(R.id.back_layout);
		mBack_layout.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				setResult(RESULT_CANCELED);
				finish();
			}
		});
		
		mBasic_info = (TextView) findViewById(R.id.basic_info);
		if(info_type == BASIC_INFO_ADDRESS){
			mBasic_info.setText(getString(R.string.address));
		}else if(info_type == BASIC_INFO_HOBBY){
			mBasic_info.setText(getString(R.string.hobby));
		}

		mStore_layout = (LinearLayout) findViewById(R.id.store_layout);
		mInfo_input =  (EditText) findViewById(R.id.info_input);
		if(default_info!=null){
			mInfo_input.setText(default_info);
		}

		mStore_layout.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				String info = mInfo_input.getText().toString().trim();
				if(info!=null && !info.equals("")){
					storeBasicInfo(info_type,info);
				}
				return ;
			}
		});
	       
	}

	private void storeBasicInfo(int type, String info){
		closeInputMethod();
		//if(mDialog!=null){
    		//	mDialog.launchProcessDialog(BasicInfoUpdateActivity.this,getString(R.string.process));
    		//}

		Intent data = new Intent();
		data.putExtra(NEW_INFO, info);
		setResult(RESULT_OK, data);
		finish();

		

	}

	private void closeInputMethod() {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		boolean isOpen = imm.isActive();
		if (isOpen) {
			imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    		}
	}

	private void initFromIntent(Intent intent) {
		info_type = intent.getIntExtra(Constants.BASIC_INFO_TYPE, BASIC_INFO_ADDRESS);
		default_info = intent.getStringExtra(Constants.BASIC_INFO_DEFAULT_VALUE);
	}

	
	@Override
	public void onResume(){
		super.onResume();
		

	 }

	@Override
	public void onBackPressed(){
		setResult(RESULT_CANCELED);
		finish();
	}


	@Override
	public void onDestroy(){
		super.onDestroy();	
	}

	
}




