package com.android.samchat;

import java.util.ArrayList;
import java.util.List;

import com.android.samchat.easemobdemo.InviteMessgeDao;
import com.android.samservice.Constants;
import com.android.samservice.SMCallBack;
import com.android.samservice.SamLog;
import com.android.samservice.SamService;
import com.android.samservice.info.ContactUser;
import com.android.samservice.info.InviteMessageRecord;
import com.android.samservice.info.LoginUser;
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
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.content.IntentFilter;
import android.util.Log;


public class NewFriendActivity extends Activity {
	private final String TAG = "NewFriendActivity";

	public static final int MSG_QUERY_USR_INFO_CALLBACK = 1;
	
	private Context mContext;
	private ImageView mBack;
	private EditText mSearch;
	private ImageView mCancel;
	
	private ListView mNewFriendList;
	private NewFriendListAdapter mAdapter;

	private SamProcessDialog mDialog;

	private BroadcastReceiver broadcastReceiver;
	private LocalBroadcastManager broadcastManager;


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

		

		mDialog = new SamProcessDialog();

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
			public boolean onEditorAction(TextView v, int actionId,KeyEvent event) {    
				if (actionId==EditorInfo.IME_ACTION_SEARCH ||(event!=null&&event.getKeyCode()== KeyEvent.KEYCODE_ENTER)) { 
					closeInputMethod();

					String username = mSearch.getText().toString().trim();
					if(!username.equals("")){
						SamLog.e(TAG, "start search new friend");
						if(mDialog!=null){
    							mDialog.launchProcessDialog(NewFriendActivity.this,getString(R.string.process));
    						}

						/*query user info from server*/
						query_user_info_from_server(username);
							
					}
					
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

		mNewFriendList = (ListView) findViewById(R.id.new_friend_list);
		mAdapter = new NewFriendListAdapter(mContext);
		mNewFriendList.setAdapter(mAdapter);


		registerBroadcastReceiver();
		updateNewFriendList();

	}

	private void query_user_info_from_server(String username){
		SamService.getInstance().query_user_info_from_server(username,new SMCallBack(){
			@Override
			public void onSuccess(Object obj) {
				if(mDialog!=null){
	    				mDialog.dismissPrgoressDiglog();
	    			}
				/*launch user namecard activity*/
				SamLog.i(TAG,"query user info succeed ...");

				List<ContactUser> uiArray = (List<ContactUser>)obj;
				if(uiArray.size()>0){
					ContactUser userinfo = uiArray.get(0);
					launchNameCardActivity(userinfo);
				}else{
					launchDialogActivity(getString(R.string.query_user_info_failed_title),getString(R.string.query_user_info_no_user_statement));	
				}
				
			}

			@Override
			public void onFailed(int code) {
				SamLog.i(TAG,"query user info failed ...");
				/*send question failed due to server error*/
				if(mDialog!=null){
	    				mDialog.dismissPrgoressDiglog();
	    			}

				if(code == SamService.RET_QUERY_USERINFO_SERVER_NO_SUCH_USER){
					launchDialogActivity(getString(R.string.query_user_info_failed_title),getString(R.string.query_user_info_no_user_statement));	
				}else{
					launchDialogActivity(getString(R.string.query_user_info_failed_title),getString(R.string.query_user_info_failed_statement));				
				}
			}

			@Override
			public void onError(int code) {
				SamLog.i(TAG,"query user info errorr ...");
				if(mDialog!=null){
	    				mDialog.dismissPrgoressDiglog();
	    			}

				launchDialogActivity(getString(R.string.query_user_info_failed_title),getString(R.string.query_user_info_failed_statement));
			}

		});
	}

	private void registerBroadcastReceiver() {
		broadcastManager = LocalBroadcastManager.getInstance(this);
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(Constants.ACTION_CONTACT_CHANAGED);

		broadcastReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				updateNewFriendList();
			}
        };
		
        broadcastManager.registerReceiver(broadcastReceiver, intentFilter);
		
    }
	
	private void unregisterBroadcastReceiver(){
	    broadcastManager.unregisterReceiver(broadcastReceiver);
	}

	private void updateNewFriendList(){
		InviteMessgeDao dao = new InviteMessgeDao(mContext);
		List<InviteMessageRecord> recordList = dao.getMessagesList();

		SamLog.e(TAG,"updateNewFriendList size:"+recordList.size());
		
		mAdapter.setContactInviteRecordArray(recordList);
		mAdapter.setCount(recordList.size());
		mAdapter.notifyDataSetChanged();
		
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
		this.setResult(1);
		finish();
	}


	@Override
	public void onDestroy(){
		super.onDestroy();
		this.setResult(1);
		SamLog.e(TAG,"new friend activity destroy and send broadcast");
		unregisterBroadcastReceiver();

	}

	private void launchDialogActivity(String title,String msg){
		Intent newIntent = new Intent(this,DialogActivity.class);
		int intentFlags = Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP;
		newIntent.setFlags(intentFlags);
		newIntent.putExtra("title", title);
		newIntent.putExtra("message", msg);
		startActivity(newIntent);
	}

	private void launchNameCardActivity(ContactUser userinfo){
		Intent newIntent = new Intent(this,NameCardActivity.class);
		int intentFlags = Intent.FLAG_ACTIVITY_CLEAR_TOP;
		Bundle bundle = new Bundle();
		bundle.putSerializable("ContactUser",userinfo);
		newIntent.putExtras(bundle);
		newIntent.setFlags(intentFlags);
		startActivity(newIntent);
	}


	

}

