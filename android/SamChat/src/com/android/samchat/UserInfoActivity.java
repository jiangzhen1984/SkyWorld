package com.android.samchat;

import com.android.samservice.SamService;
import com.android.samservice.info.AvatarRecord;
import com.android.samservice.info.ContactUser;
import com.android.samservice.info.ReceivedAnswer;
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
	private ImageView mFollow_op_img;
	private TextView mFollow_op_txt;


	private boolean isFriend = false;
	private boolean isFollowed = false;

	private UserInfoDescListAdapter mAdapter;
	
	
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
			//mDesc.setText(user.getdescription());
			mAdapter.setCount(1);
			mAdapter.setDesc("testtesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttestesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttest");
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
			mFollow_op_txt.setText(getString(R.string.follow));
		}else{
			isFollowed = false;
			mFollow_op_txt.setText(getString(R.string.followed));
		}

		

		
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
}