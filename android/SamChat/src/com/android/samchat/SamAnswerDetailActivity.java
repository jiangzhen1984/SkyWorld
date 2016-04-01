package com.android.samchat;

import com.android.samchat.easemobdemo.EaseMobHelper;
import com.android.samservice.SMCallBack;
import com.android.samservice.SamLog;
import com.android.samservice.SamService;
import com.android.samservice.info.AvatarRecord;
import com.android.samservice.info.ContactUser;
import com.android.samservice.info.FollowerRecord;
import com.android.samservice.info.LoginUser;
import com.android.samservice.info.ReceivedAnswer;
import com.easemob.easeui.EaseConstant;
import com.easemob.easeui.domain.EaseUser;
import com.easemob.easeui.utils.EaseUserUtils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class SamAnswerDetailActivity extends Activity {
	public static final String TAG="SamAnswerDetailActivity";

	private Context mContext;
	private ImageView mBack;
	private TextView mServicer_name;
	private ImageView mServicer_img;
	//private ListView mSam_answer_detail_list;
	private TextView mTemp_talk;
	private TextView mAdd_friend;
	private TextView mFollow;
	private TextView mQuestion;
	private TextView mAnswer;


	private SamProcessDialog mDialog;

	private String question;
	private ReceivedAnswer answer;
	//private SamAnswerDetailListAdapter mAdapter;

	private ContactUser syservicer; 

	private boolean isFollowed = false;
	private boolean isFriend = false;

	
	@Override
	protected void onCreate(Bundle icicle){
		super.onCreate(icicle);
		setContentView(R.layout.activity_sam_answer_detail);

		mDialog = new SamProcessDialog();

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
				launchUserInfoActivity(syservicer);
			}
		    	
		});
		//mSam_answer_detail_list = (ListView) findViewById(R.id.sam_answer_detail_list);

		mTemp_talk = (TextView) findViewById(R.id.temp_talk);
		mAdd_friend = (TextView) findViewById(R.id.add_friend);
		mFollow =  (TextView) findViewById(R.id.follow);
		mTemp_talk.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				EaseUser user = new EaseUser(syservicer.geteasemob_username());
				startActivity(new Intent(SamAnswerDetailActivity.this, ChatActivity.class).putExtra(EaseConstant.EXTRA_USER_ID, user.getUsername()));
			}
		    	
		});

		mAdd_friend.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				query_user_info_from_server(syservicer.getusername());
			}
		    	
		});

		mFollow.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				if(!isFollowed){
					follow_vendor();
				}else{
					unfollow_vendor();
				}
			}
		    	
		});




		mQuestion =  (TextView) findViewById(R.id.question);
		mAnswer =  (TextView) findViewById(R.id.answer);
		
		initFromIntent(getIntent());

		if(syservicer!=null){
			LoginUser currentUser = SamService.getInstance().get_current_user();
			FollowerRecord rd = SamService.getInstance().getDao().query_FollowerRecord_db(syservicer.getunique_id(),currentUser.getunique_id());
			if(rd!=null){
				mFollow.setText(getString(R.string.cancel_follow));
				isFollowed = true;
			}else{
				mFollow.setText(getString(R.string.follow));
				isFollowed = false;
			}
		}

		boolean avatarExisted=false;
		ContactUser cuser = syservicer;
		if(cuser!=null){
			AvatarRecord rd = SamService.getInstance().getDao().query_AvatarRecord_db_by_username(cuser.getusername());
			if(rd!=null && rd.getavatarname()!=null){
				Bitmap bp = EaseUserUtils.decodeFile(SamService.sam_cache_path+SamService.AVATAR_FOLDER+"/"+rd.getavatarname(), 
										   40,
										   40);
				if(bp!=null){
					mServicer_img.setImageBitmap(bp);
					avatarExisted = true;
				}
			}
		}

		if(!avatarExisted){
			mServicer_img.setImageResource(R.drawable.em_default_avatar);
		}

		mQuestion.setText(question);
		mAnswer.setText(answer.getanswer());

		mContext = getBaseContext();
		//mAdapter = new SamAnswerDetailListAdapter(mContext);
		//mSam_answer_detail_list.setAdapter(mAdapter);

		//mAdapter.setReceivedAnswer(answer);
		//mAdapter.setCount(1);
		//mAdapter.notifyDataSetChanged();
		
	}



	@Override
	public void onResume(){
		 super.onResume();
		 if(syservicer == null){
			return;
		 }
		 if(SamService.getInstance().getDao().query_UserFriendRecord_db(syservicer.geteasemob_username()) != null){
			isFriend = true;
			mAdd_friend.setEnabled(false);
			mAdd_friend.setClickable(false);
			mAdd_friend.setTextColor((getResources().getColor(R.color.text_invalid_gray)));
		}else{
			isFriend = false;
			mAdd_friend.setEnabled(true);
			mAdd_friend.setClickable(true);
		}

		if(SamService.getInstance().getDao().query_FollowerRecord_db(syservicer.getunique_id(), SamService.getInstance().get_current_user().getunique_id())!=null){
			isFollowed = true;
			mFollow.setText(getString(R.string.cancel_follow));
		}else{
			isFollowed = false;
			mFollow.setText(getString(R.string.follow));
		}
	}

	private void initFromIntent(Intent intent) {
		if (intent != null) {
			answer = (ReceivedAnswer)intent.getSerializableExtra("ReceivedAnswer");
			question = (String)intent.getSerializableExtra("CurrentQuestion");
			syservicer = SamService.getInstance().getDao().query_ContactUser_db(answer.getcontactuserid());
			if(syservicer!=null){
				mServicer_name.setText(syservicer.getusername());
			}
		}
	}

	@Override
	public void onBackPressed(){
		this.setResult(1);
		finish();
	}

	private void follow_vendor(){
		long user_id = syservicer.getunique_id();
		String username = syservicer.getusername();
		
		if(mDialog!=null){
			mDialog.launchProcessDialog(this,getString(R.string.process));
		}
		SamLog.i(TAG,"follow vendor id:"+user_id);
		SamService.getInstance().follow(user_id,username,new SMCallBack(){
			@Override
			public void onSuccess(Object obj) {
				if(SamAnswerDetailActivity.this == null ||SamAnswerDetailActivity.this.isFinishing() ){
					return;
				}
				
				runOnUiThread(new Runnable() {
					public void run() {
						if(mDialog!=null){
							mDialog.dismissPrgoressDiglog();
						}
						Toast.makeText(SamAnswerDetailActivity.this, R.string.follow_succeed, 0).show();
						isFollowed = true;
						mFollow.setText(getString(R.string.cancel_follow));
						EaseMobHelper.getInstance().sendFollowerChangeBroadcast();
					}
				});
				
				
			}

			@Override
			public void onFailed(int code) {
				if(SamAnswerDetailActivity.this == null ||SamAnswerDetailActivity.this.isFinishing() ){
					return;
				}
				runOnUiThread(new Runnable() {
					public void run() {
						if(mDialog!=null){
							mDialog.dismissPrgoressDiglog();
						}
						Toast.makeText(SamAnswerDetailActivity.this, R.string.follow_failed, 0).show();
					}
				});
			}

			@Override
			public void onError(int code) {
				if(SamAnswerDetailActivity.this == null ||SamAnswerDetailActivity.this.isFinishing() ){
					return;
				}
				runOnUiThread(new Runnable() {
					public void run() {
						if(mDialog!=null){
							mDialog.dismissPrgoressDiglog();
						}
						Toast.makeText(SamAnswerDetailActivity.this, R.string.follow_failed, 0).show();
					}
				});
			}

		});
		
		
	}


	private void unfollow_vendor(){
		long user_id = syservicer.getunique_id();
		String username = syservicer.getusername();
		
		if(mDialog!=null){
			mDialog.launchProcessDialog(this,getString(R.string.process));
		}
		SamLog.i(TAG,"unfollow vendor id:"+user_id);
		SamService.getInstance().unfollow(user_id,username,new SMCallBack(){
			@Override
			public void onSuccess(Object obj) {
				if(SamAnswerDetailActivity.this == null ||SamAnswerDetailActivity.this.isFinishing() ){
					return;
				}
				
				runOnUiThread(new Runnable() {
					public void run() {
						if(mDialog!=null){
							mDialog.dismissPrgoressDiglog();
						}
						Toast.makeText(SamAnswerDetailActivity.this, R.string.cancel_follow_succeed, 0).show();
						isFollowed = false;
						mFollow.setText(getString(R.string.follow));
						EaseMobHelper.getInstance().sendFollowerChangeBroadcast();
					}
				});
				
				
			}

			@Override
			public void onFailed(int code) {
				if(SamAnswerDetailActivity.this == null ||SamAnswerDetailActivity.this.isFinishing() ){
					return;
				}
				runOnUiThread(new Runnable() {
					public void run() {
						if(mDialog!=null){
							mDialog.dismissPrgoressDiglog();
						}
						Toast.makeText(SamAnswerDetailActivity.this, R.string.cancel_follow_failed, 0).show();
					}
				});
			}

			@Override
			public void onError(int code) {
				if(SamAnswerDetailActivity.this == null ||SamAnswerDetailActivity.this.isFinishing() ){
					return;
				}
				runOnUiThread(new Runnable() {
					public void run() {
						if(mDialog!=null){
							mDialog.dismissPrgoressDiglog();
						}
						Toast.makeText(SamAnswerDetailActivity.this, R.string.cancel_follow_failed, 0).show();
					}
				});
			}

		});
		
		
	}

	private void query_user_info_from_server(String username){
		SamService.getInstance().query_user_info_from_server(username,new SMCallBack(){
			@Override
			public void onSuccess(Object obj) {
				runOnUiThread(new Runnable() {
					public void run() {
						if(mDialog!=null){
	    						mDialog.dismissPrgoressDiglog();
	    					}

						launchNameCardActivity(syservicer);
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

						launchNameCardActivity(syservicer);
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

						launchNameCardActivity(syservicer);
					}
				});
			}

		});
	}

	private void launchNameCardActivity(ContactUser userinfo){
		Intent newIntent = new Intent(this,NameCardActivity.class);
		int intentFlags = Intent.FLAG_ACTIVITY_CLEAR_TOP;
		newIntent.setFlags(intentFlags);
		
		Bundle bundle = new Bundle();
		bundle.putSerializable("ContactUser",syservicer);
		newIntent.putExtras(bundle);
		
		startActivity(newIntent);
	}


	private void launchUserInfoActivity(ContactUser userinfo){
		Intent newIntent = new Intent(this,UserInfoActivity.class);
		int intentFlags = Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP;
		newIntent.setFlags(intentFlags);
		
		Bundle bundle = new Bundle();
		bundle.putSerializable("ContactUser",syservicer);
		newIntent.putExtras(bundle);
		
		startActivity(newIntent);
	}
}
