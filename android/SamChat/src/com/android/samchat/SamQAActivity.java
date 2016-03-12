package com.android.samchat;

import java.util.ArrayList;
import java.util.List;

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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.view.MotionEvent;
import android.content.BroadcastReceiver;
import android.text.TextUtils;
import android.content.IntentFilter;
import android.util.Log;


public class SamQAActivity extends Activity {
	private final String TAG = "SamQAActivity";
	static public final String ACTIVITY_NAME="com.android.samchat.SamQAActivity";
	
	private Context mContext;
	private ImageView mBack;
	private ListView mQuestionList;
	private QuestionListAdapter mAdpater;
	
	
	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);

		setContentView(R.layout.activity_qa);

		mBack =  (ImageView) findViewById(R.id.back);
		mBack.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				SamQAActivity.this.setResult(1);
				finish();
			}
		    	
		});

		mQuestionList =  (ListView) findViewById(R.id.question_list);
		mContext = getBaseContext();
		mAdpater = new QuestionListAdapter(mContext);
		mQuestionList.setAdapter(mAdpater);

		mQuestionList.setOnItemClickListener(new OnItemClickListener(){   
			@Override   
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {   
				/*arg2 is postion*/
				List<ReceivedQuestion> array = mAdpater.getReceivedQuestionArray();
				ReceivedQuestion question = array.get(arg2);
				/*update Received Question Status*/
				//question.setshown(ReceivedQuestion.SHOWN);
				//SamService.getInstance().getDao().add_update_ReceivedQuestion_db(question);
				
				Intent intent = new Intent();
				intent.setClass(SamQAActivity.this,SamQADetailActivity.class);
				Bundle bundle = new Bundle();
				bundle.putSerializable("ReceivedQuestion",question);
				intent.putExtras(bundle);
				startActivity(intent);

				
				
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

		/*query newest question*/
		List<ReceivedQuestion> array = SamService.getInstance().getDao().query_RecentReceivedQuestion_db();
		mAdpater.setReceivedQuestionArray(array);
		mAdpater.setCount(array.size());
		mAdpater.notifyDataSetChanged();
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
		SamLog.e(TAG,"QA activity destroy and send broadcast");
		
		//Intent intent = new Intent();
		//intent.setAction(SamChats_Fragment.HIDE_QA_BADGE);
		//sendBroadcast(intent);	
	}
}
