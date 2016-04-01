package com.android.samchat;

import com.android.samchat.easemobdemo.EaseMobHelper;
import com.android.samservice.SMCallBack;
import com.android.samservice.SamLog;
import com.android.samservice.SamService;
import com.android.samservice.info.AvatarRecord;
import com.android.samservice.info.ContactUser;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class UserInfoActivity extends Activity {
	public static final String TAG="UserInfoActivity";

	private Context mContext;
	private ContactUser user;
	private ImageView mWall_photo;
	private ImageView mAvatar;
	private TextView mUsername;
	private TextView mLocation;
	private TextView mArea;
	private ListView mDescList;
	private ImageView mFriend_op_img;
	private TextView mFriend_op_txt;
	private RelativeLayout mFriend_op_layout;
	private ImageView mFollow_op_img;
	private TextView mFollow_op_txt;
	private RelativeLayout mFollow_op_layout;


	private boolean isFriend = false;
	private boolean isFollowed = false;

	private UserInfoDescListAdapter mAdapter;

	private SamProcessDialog mDialog;
	
	
	@Override
	protected void onCreate(Bundle icicle){
		super.onCreate(icicle);
		setContentView(R.layout.activity_user_info);

		mContext = getBaseContext();
		initFromIntent(getIntent());
		if(user == null){
			finish();
			return;
		}

		mDialog = new SamProcessDialog();

		mWall_photo = (ImageView)findViewById(R.id.wall_photo);
		mAvatar = (ImageView)findViewById(R.id.avatar); 
		mUsername = (TextView)findViewById(R.id.username); 
		mLocation = (TextView)findViewById(R.id.location); 
		mArea = (TextView)findViewById(R.id.area);  
		mDescList = (ListView)findViewById(R.id.descList); 
		mFriend_op_img = (ImageView)findViewById(R.id.friend_op_img); 
		mFriend_op_txt = (TextView)findViewById(R.id.friend_op_txt);
		mFollow_op_img = (ImageView)findViewById(R.id.follow_op_img); 
		mFollow_op_txt = (TextView)findViewById(R.id.follow_op_txt);
		mFriend_op_layout = (RelativeLayout)findViewById(R.id.friend_op_layout);
		mFollow_op_layout = (RelativeLayout)findViewById(R.id.follow_op_layout);


		boolean avatarExisted = false;
		AvatarRecord rd = SamService.getInstance().getDao().query_AvatarRecord_db_by_username(user.getusername());
		if(rd!=null && rd.getavatarname()!=null){
			Bitmap bp = EaseUserUtils.decodeFile(SamService.sam_cache_path+SamService.AVATAR_FOLDER+"/"+rd.getavatarname(), 
									   50,
									   50);
			if(bp!=null){
				mAvatar.setImageBitmap(bp);
				avatarExisted = true;
			}
		}
		
		if(!avatarExisted){
			mAvatar.setImageResource(R.drawable.em_default_avatar);
		}

		mUsername.setText(user.getusername());
		if(user.getlocation()!=null){
			mLocation.setText(user.getlocation());
		}

		if(user.getarea()!=null){
			mArea.setText(user.getarea());
		}

		mAdapter = new UserInfoDescListAdapter(mContext);
		mDescList.setAdapter(mAdapter);

		if(user.getdescription()!=null){
			mAdapter.setCount(1);
			mAdapter.setDesc(user.getdescription());
		}else{
			mAdapter.setCount(0);
		}

		if(SamService.getInstance().getDao().query_UserFriendRecord_db(user.geteasemob_username()) != null){
			isFriend = true;
			mFriend_op_txt.setText(getString(R.string.start_talk));
		}else{
			isFriend = false;
			mFriend_op_txt.setText(getString(R.string.add_friends));
		}

		if(SamService.getInstance().getDao().query_FollowerRecord_db(user.getunique_id(), SamService.getInstance().get_current_user().getunique_id())!=null){
			isFollowed = true;
			mFollow_op_txt.setText(getString(R.string.cancel_follow));
		}else{
			isFollowed = false;
			mFollow_op_txt.setText(getString(R.string.follow));
		}


		mFriend_op_layout.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				if(isFriend){
					//launch conversition activity
					EaseUser euser = new EaseUser(user.geteasemob_username());
					startActivity(new Intent(UserInfoActivity.this, ChatActivity.class).putExtra(EaseConstant.EXTRA_USER_ID, euser.getUsername()));
					finish();
				}else{
					//launch add friend activity
					launchSendVerifyMsgActivity(user.geteasemob_username());
					finish();
					
				}
			}
		});

		mFollow_op_layout.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				if(!isFollowed){
					follow_vendor();
				}else{
					unfollow_vendor();
				}
			}
		});

		
		
	}


	@Override
	public void onBackPressed(){
		finish();
	}

	private void initFromIntent(Intent intent) {
		user = (ContactUser)intent.getSerializableExtra("ContactUser");
		if(user == null){
			finish();
		}
	}

	private void launchSendVerifyMsgActivity(String easemob_name){
		Intent newIntent = new Intent(this,SendVerifyMsgActivity.class);
		int intentFlags = Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP;;
		newIntent.setFlags(intentFlags);
		newIntent.putExtra("easemob_name", easemob_name);
		startActivity(newIntent);
	}

	private void follow_vendor(){
		long user_id = user.getunique_id();
		String username = user.getusername();
		
		if(mDialog!=null){
			mDialog.launchProcessDialog(this,getString(R.string.process));
		}
		SamLog.i(TAG,"follow vendor id:"+user_id);
		SamService.getInstance().follow(user_id,username,new SMCallBack(){
			@Override
			public void onSuccess(Object obj) {
				if(UserInfoActivity.this == null ||UserInfoActivity.this.isFinishing() ){
					return;
				}
				
				runOnUiThread(new Runnable() {
					public void run() {
						if(mDialog!=null){
							mDialog.dismissPrgoressDiglog();
						}
						Toast.makeText(UserInfoActivity.this, R.string.follow_succeed, 0).show();
						EaseMobHelper.getInstance().sendFollowerChangeBroadcast();
						isFollowed = true;
						mFollow_op_txt.setText(getString(R.string.cancel_follow));
						UserInfoActivity.this.finish();
						
					}
				});
				
				
			}

			@Override
			public void onFailed(int code) {
				if(UserInfoActivity.this == null ||UserInfoActivity.this.isFinishing() ){
					return;
				}
				runOnUiThread(new Runnable() {
					public void run() {
						if(mDialog!=null){
							mDialog.dismissPrgoressDiglog();
						}
						Toast.makeText(UserInfoActivity.this, R.string.follow_failed, 0).show();
						//UserInfoActivity.this.finish();
					}
				});
			}

			@Override
			public void onError(int code) {
				if(UserInfoActivity.this == null ||UserInfoActivity.this.isFinishing() ){
					return;
				}
				runOnUiThread(new Runnable() {
					public void run() {
						if(mDialog!=null){
							mDialog.dismissPrgoressDiglog();
						}
						Toast.makeText(UserInfoActivity.this, R.string.follow_failed, 0).show();
						//UserInfoActivity.this.finish();
					}
				});
			}

		});
		
		
	}


	private void unfollow_vendor(){
		long user_id = user.getunique_id();
		String username = user.getusername();
		
		if(mDialog!=null){
			mDialog.launchProcessDialog(this,getString(R.string.process));
		}
		SamLog.i(TAG,"unfollow vendor id:"+user_id);
		SamService.getInstance().unfollow(user_id,username,new SMCallBack(){
			@Override
			public void onSuccess(Object obj) {
				if(UserInfoActivity.this == null ||UserInfoActivity.this.isFinishing() ){
					return;
				}
				
				runOnUiThread(new Runnable() {
					public void run() {
						if(mDialog!=null){
							mDialog.dismissPrgoressDiglog();
						}
						Toast.makeText(UserInfoActivity.this, R.string.cancel_follow_succeed, 0).show();
						EaseMobHelper.getInstance().sendFollowerChangeBroadcast();
						isFollowed = false;
						mFollow_op_txt.setText(getString(R.string.follow));
						UserInfoActivity.this.finish();
						
					}
				});
				
				
			}

			@Override
			public void onFailed(int code) {
				if(UserInfoActivity.this == null ||UserInfoActivity.this.isFinishing() ){
					return;
				}
				runOnUiThread(new Runnable() {
					public void run() {
						if(mDialog!=null){
							mDialog.dismissPrgoressDiglog();
						}
						Toast.makeText(UserInfoActivity.this, R.string.cancel_follow_failed, 0).show();
						//UserInfoActivity.this.finish();
					}
				});
			}

			@Override
			public void onError(int code) {
				if(UserInfoActivity.this == null ||UserInfoActivity.this.isFinishing() ){
					return;
				}
				runOnUiThread(new Runnable() {
					public void run() {
						if(mDialog!=null){
							mDialog.dismissPrgoressDiglog();
						}
						Toast.makeText(UserInfoActivity.this, R.string.cancel_follow_failed, 0).show();
						//UserInfoActivity.this.finish();
					}
				});
			}

		});
		
		
	}
}