package com.android.samchat;

import com.android.samservice.SamService;
import com.android.samservice.info.ContactUser;
import com.android.samservice.info.ReceivedAnswer;
import com.easemob.easeui.EaseConstant;
import com.easemob.easeui.domain.EaseUser;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class SamAnswerDetailActivity extends Activity {
	public static final String TAG="SamAnswerDetailActivity";

	private Context mContext;
	private ImageView mBack;
	private TextView mServicer_name;
	private ImageView mServicer_img;
	private ListView mSam_answer_detail_list;
	private TextView mTemp_talk;
	private TextView mAdd_friend;


	private ReceivedAnswer answer;
	private SamAnswerDetailListAdapter mAdapter;

	private ContactUser syservicer; 

	
	@Override
	protected void onCreate(Bundle icicle){
		super.onCreate(icicle);
		setContentView(R.layout.activity_sam_answer_detail);

		mBack =  (ImageView) findViewById(R.id.back);
		mBack.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				SamAnswerDetailActivity.this.setResult(1);
				finish();
			}
		    	
		});

		mServicer_name =  (TextView) findViewById(R.id.servicer_name);
		mServicer_img =  (ImageView) findViewById(R.id.servicer_img);
		mServicer_img.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {

			}
		    	
		});
		mSam_answer_detail_list = (ListView) findViewById(R.id.sam_answer_detail_list);

		mTemp_talk = (TextView) findViewById(R.id.temp_talk);
		mAdd_friend = (TextView) findViewById(R.id.add_friend);

		mTemp_talk.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				EaseUser user = new EaseUser(syservicer.get_easemob_username());
				startActivity(new Intent(SamAnswerDetailActivity.this, ChatActivity.class).putExtra(EaseConstant.EXTRA_USER_ID, user.getUsername()));
			}
		    	
		});

		mAdd_friend.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				
			}
		    	
		});

		
		initFromIntent(getIntent());

		mContext = getBaseContext();
		mAdapter = new SamAnswerDetailListAdapter(mContext);
		mSam_answer_detail_list.setAdapter(mAdapter);

		mAdapter.setReceivedAnswer(answer);
		mAdapter.setCount(1);
		mAdapter.notifyDataSetChanged();
		
	}

	private void initFromIntent(Intent intent) {
		if (intent != null) {
			answer = (ReceivedAnswer)intent.getSerializableExtra("ReceivedAnswer");
			syservicer = SamService.getInstance().query_ContactUser_db(answer.getcontactuserid());
			if(syservicer!=null){
				mServicer_name.setText(syservicer.get_username());
			}
		}
	}

	@Override
	public void onBackPressed(){
		this.setResult(1);
		finish();
	}
}