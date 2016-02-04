package com.android.samchat;

import java.util.ArrayList;
import java.util.List;

import com.android.samservice.SamLog;
import com.android.samservice.SamService;
import com.android.samservice.info.ContactUser;
import com.android.samservice.info.ReceivedQuestion;
import com.android.samservice.info.SendAnswer;

import android.app.Activity;
import android.content.Intent;
import android.content.Context;
import android.graphics.Color;
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
import android.view.MotionEvent;
import android.view.inputmethod.InputMethodManager;
import android.content.BroadcastReceiver;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.content.IntentFilter;
import android.util.Log;


public class SamQADetailActivity extends Activity {
	private final String TAG = "SamQADetailActivity";

	public static final String SEND_ANSWER_STATUS_BROADCAST = "com.android.sam.sndasts";
	
	private Context mContext;
	private ImageView mBack;
	private TextView mUsername;
	private ListView mQADetailList;
	private QuestionAnswerDetailListAdapter mAdpater;
	private ReceivedQuestion question;
	private ContactUser user;
	private EditText mAnswer_input;
	private TextView mQuestion_action;
	private LinearLayout mQuestion_action_layout;

	private String str_answer;
	private boolean question_action_enable;

//	private SamProcessDialog mDialog;

	
	
	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);

		setContentView(R.layout.activity_qadetails);

		IntentFilter sndastatus_filter = new IntentFilter();
		sndastatus_filter.addAction(SEND_ANSWER_STATUS_BROADCAST);
		registerReceiver(AnswerSendStatusReceiver, sndastatus_filter);

		

		//mDialog = new SamProcessDialog();

		mBack =  (ImageView) findViewById(R.id.back);
		mBack.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				finish();
			}
		    	
		});

		mUsername = (TextView) findViewById(R.id.username);

		mQADetailList =  (ListView) findViewById(R.id.qa_detail_list);
		mContext = getBaseContext();
		mAdpater = new QuestionAnswerDetailListAdapter(mContext);
		mQADetailList.setAdapter(mAdpater);

		mQuestion_action_layout = (LinearLayout)findViewById(R.id.question_action_layout);
		mAnswer_input = (EditText)findViewById(R.id.answer_input);
		
		mAnswer_input.addTextChangedListener(AI_TextWatcher);
		
		mQuestion_action = (TextView)findViewById(R.id.question_action);

		mQuestion_action.setTextColor(Color.rgb(0xFF, 0x66, 0x00));
		mQuestion_action_layout.setBackgroundColor(Color.rgb(0xBF, 0xBF, 0xBF));
		
		mQuestion_action.setEnabled(false);
		mQuestion_action.setClickable(false);
		mQuestion_action_layout.setEnabled(false);
		mQuestion_action_layout.setClickable(false);

		mQuestion_action_layout.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				SamLog.i(TAG,"answer input is clicked");
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE); 
				imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS); 

				//if(mDialog!=null){
				//	mDialog.launchProcessDialog(SamQADetailActivity.this,getString(R.string.process));
				//}
				
				answerQuestion();
				
				mAnswer_input.setText("");
				question_action_enable = false;
				updateBtnQuestionAction();

				onResume();
	   	 	}

		});

		initFromIntent(getIntent());

	}

	private void answerQuestion(){
		String answer = str_answer;

		SamService.getInstance().answer_question(question.question_id, answer);
		
	}

	private void updateBtnQuestionAction(){
		boolean clickable = question_action_enable;
		if(clickable){
			mQuestion_action.setTextColor(Color.rgb(255, 255, 255));
			mQuestion_action_layout.setBackgroundColor(Color.rgb(0xFF, 0x66, 0x00));
		}else{
			mQuestion_action.setTextColor(Color.rgb(0xFF, 0x66, 0x00));
			mQuestion_action_layout.setBackgroundColor(Color.rgb(0xBF, 0xBF, 0xBF));
		}
		
		mQuestion_action_layout.setEnabled(clickable);
		mQuestion_action_layout.setClickable(clickable);
		
		
	}

	private TextWatcher AI_TextWatcher = new TextWatcher(){
		@Override 
		public void onTextChanged(CharSequence s, int start, int before, int count) { 
		        
		}
		
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,int after) { 
		       
		} 
		
		@Override
		public void afterTextChanged(Editable s) { 
		  	str_answer = mAnswer_input.getText().toString();
		   	if(str_answer!=null & !str_answer.equals("") ){
		   		question_action_enable = true;
		   	}else{
		   		question_action_enable = false;
		   	}
		    	
		   	updateBtnQuestionAction();
		}     
	};

	private void initFromIntent(Intent intent) {
		if (intent != null) {
			question = (ReceivedQuestion)intent.getSerializableExtra("ReceivedQuestion");
			long contactuserid = question.getcontactuserid();
			user = SamService.getInstance().getDao().query_ContactUser_db(contactuserid);
			mUsername.setText(user.getusername());
		}
	}

	@Override
	public void onResume(){
		super.onResume();
		
		mAdpater.setReceivedQuestion(question);
		mAdpater.setContactUser(user);
		/*query newest answer*/
		List<SendAnswer> answerArray = SamService.getInstance().getDao().query_SendAnswer_db(question.getquestion_id());
		mAdpater.setSendAnswerArray(answerArray);
		mAdpater.setCount(1+answerArray.size());
		mAdpater.notifyDataSetChanged();
	 }

	@Override
	public void onDestroy(){
		super.onDestroy();
		unregisterReceiver(AnswerSendStatusReceiver);
	}


	private BroadcastReceiver AnswerSendStatusReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			SamLog.e(TAG,"AnswerSendStatusReceiver received"+intent.getAction());
			if(intent.getAction().equals(SEND_ANSWER_STATUS_BROADCAST)){
				//if(mDialog!=null){
				//	mDialog.dismissPrgoressDiglog();
				//}

				boolean NoSuchQuestion = intent.getBooleanExtra("NoSuchQuestion",false);
				if(!NoSuchQuestion){
					onResume();
				}else{
					SamLog.e(TAG,"Question has been closed by submitter");
					onResume();
				}
			}	
	    }
	};
}

