package com.android.samchat;

import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.android.samchat.easemobdemo.EaseMobHelper;
import com.android.samchat.easemobdemo.InviteMessgeDao;
import com.android.samchat.easemobdemo.UserFriendDao;
import com.android.samservice.Constants;
import com.android.samservice.SamLog;
import com.android.samservice.SamService;
import com.android.samservice.info.AvatarRecord;
import com.android.samservice.info.ContactUser;
import com.android.samservice.info.ReceivedQuestion;
import com.easemob.chat.EMContactManager;
import com.easemob.easeui.EaseConstant;
import com.easemob.easeui.domain.EaseUser;
import com.easemob.exceptions.EaseMobException;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
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
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.content.IntentFilter;
import android.util.Log;


public class NameCardActivity extends Activity {
	private final String TAG = "NameCardActivity";

	static final String DELETE_USERFRIEND_CONFIRM = "com.android.samchat.deleteuserfriend";

	static final int MENU_ID_MOVE_TO_BLACKLIST = android.view.Menu.FIRST;
	static final int MENU_ID_MOVE_FROM_BLACKLIST = android.view.Menu.FIRST + 1;
	static final int MENU_ID_DELETE = android.view.Menu.FIRST + 2;

	private Context mContext;
	private ImageView mBack;
	private ImageView mUserimg;
	private TextView mUsername;
	private TextView mResidence;
	private TextView mVisitence;
	private TextView mCareer_info;
	private TextView mSocial_info;
	private LinearLayout mAdd_friend_layout;
	private LinearLayout mSend_msg_layout;

	private ContactUser userinfo;

	private ActionBar actionBar;

	public enum UserStatus{
		MYSELF,
		NOT_FRIEND,
		FRIEND_NOT_IN_BLACKLIST,
		FRIEND_IN_BLACKLIST
	};

	int userStatus;
	private BroadcastReceiver broadcastReceiver;
	private LocalBroadcastManager broadcastManager;
	private Menu menu;

	private boolean isUserInBlackList(String easemob_name){
		List<String> usernames = EMContactManager.getInstance().getBlackListUsernames();
		return usernames.contains(easemob_name);
	}

	private void setIconEnable(Menu menu, boolean enable)  
	{  
		if (menu.getClass().getSimpleName().equals("MenuBuilder")) {
			try {
				Method m = menu.getClass().getDeclaredMethod(
							"setOptionalIconsVisible", Boolean.TYPE);
				m.setAccessible(true);
				m.invoke(menu, enable);
			} catch (Exception e) {
				 e.printStackTrace();  
			}
		}
       }  

	private Bitmap decodeFile(String path, String filename,int req_Height,int req_Width){
		File filePath = null; 
		File file = null;
		FileInputStream fos = null;
		
		try {

			filePath = new File(path);
			if(!filePath.exists()){
				return null;
			}

			file = new File(path  +"/"+ filename);

			if(!file.exists()){
				return null;
			}
			
			//decode image size
			BitmapFactory.Options o1 = new BitmapFactory.Options();
			o1.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(new FileInputStream(file),null,o1);


			//Find the correct scale value. It should be the power of 2.
			int width_tmp = o1.outWidth;
			int height_tmp = o1.outHeight;
			int scale = 1;

			if(width_tmp > req_Width || height_tmp > req_Height)
			{
				int heightRatio = Math.round((float) height_tmp / (float) req_Height);
				int widthRatio = Math.round((float) width_tmp / (float) req_Width);
				scale = heightRatio < widthRatio ? heightRatio : widthRatio;
			}

			BitmapFactory.Options o2 = new BitmapFactory.Options();
			o2.inSampleSize = scale;
			o2.inScaled = false;
			return BitmapFactory.decodeFile(path+"/"+filename,o2);
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	
}

	private Bitmap decodeImageFileAsBitmap(String path, String filename) {
		File filePath = null; 
		File file = null;
		FileInputStream fos = null;

		try{
			filePath = new File(path);
			if(!filePath.exists()){
				return null;
			}

			file = new File(path  +"/"+ filename);

			if(!file.exists()){
				return null;
			}

			fos = new FileInputStream(file);    

			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(fos, null, o);
 
			// Find the correct scale value. It should be the power of 2.
			final int REQUIRED_SIZE = 50;
			int width_tmp = o.outWidth, height_tmp = o.outHeight;
			int scale = 1;
			while (true) {
				if (width_tmp / 2 < REQUIRED_SIZE || height_tmp / 2 < REQUIRED_SIZE)
					break;
				width_tmp /= 2;
				height_tmp /= 2;
				scale *= 2;
			}
 
			// decode with inSampleSize
			BitmapFactory.Options o2 = new BitmapFactory.Options();
			o2.inSampleSize = scale;
			
			return BitmapFactory.decodeStream(fos, null, o2);

		}catch(Exception e){
			return null;
			
		}finally{
			try{
				if(fos!=null) fos.close();
			}catch(Exception e){

			}

		}


	}

	private void refreshView(){
		Bitmap bp = null;
		AvatarRecord rd = SamService.getInstance().getDao().query_AvatarRecord_db_by_username(userinfo.getusername());
		
		if(rd!=null && rd.getavatarname()!=null){
			SamLog.e(TAG,"show image:"+rd.getavatarname());
			//bp = decodeImageFileAsBitmap(SamService.sam_cache_path+SamService.AVATAR_FOLDER,rd.getavatarname());
			bp = decodeFile(SamService.sam_cache_path+SamService.AVATAR_FOLDER,rd.getavatarname(),50,50);
		}

		if(bp!=null){
			SamLog.e(TAG,"set into imageview:"+rd.getavatarname());
			mUserimg.setImageBitmap(bp);
		}

		mUsername.setText(userinfo.getusername());
	}

	private void update(){
		if(userinfo.getusername().equals(SamService.getInstance().get_current_user().getusername())){
			userStatus = UserStatus.MYSELF.ordinal();
			mAdd_friend_layout.setVisibility(View.GONE);
			mSend_msg_layout.setVisibility(View.VISIBLE);
			refreshView();
			return;
		}

		String easemob_name = userinfo.geteasemob_username();

		UserFriendDao dao = new UserFriendDao(skyworld.appContext);
		if(dao.getContact(easemob_name)!=null){
			if(isUserInBlackList(easemob_name)){
				userStatus = UserStatus.FRIEND_IN_BLACKLIST.ordinal();
				if(menu!=null){
					menu.findItem(MENU_ID_MOVE_TO_BLACKLIST).setVisible(false);
					menu.findItem(MENU_ID_MOVE_FROM_BLACKLIST).setVisible(true);
					menu.findItem(MENU_ID_DELETE).setVisible(true);
				}
			}else{
				userStatus = UserStatus.FRIEND_NOT_IN_BLACKLIST.ordinal();
				if(menu!=null){
					menu.findItem(MENU_ID_MOVE_TO_BLACKLIST).setVisible(true);
					menu.findItem(MENU_ID_MOVE_FROM_BLACKLIST).setVisible(false);
					menu.findItem(MENU_ID_DELETE).setVisible(true);
				}
			}

			mAdd_friend_layout.setVisibility(View.GONE);
			mSend_msg_layout.setVisibility(View.VISIBLE);
			
		}else{
			userStatus = UserStatus.NOT_FRIEND.ordinal();
			mAdd_friend_layout.setVisibility(View.VISIBLE);
			mSend_msg_layout.setVisibility(View.GONE);

			if(menu!=null){
				menu.findItem(MENU_ID_MOVE_TO_BLACKLIST).setVisible(false);
				menu.findItem(MENU_ID_MOVE_FROM_BLACKLIST).setVisible(false);
				menu.findItem(MENU_ID_DELETE).setVisible(false);
			}
			
		}

		refreshView();

		
	}


	private void registerBroadcastReceiver() {
		broadcastManager = LocalBroadcastManager.getInstance(this);
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(Constants.ACTION_CONTACT_CHANAGED);

		broadcastReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				boolean isInvite = intent.getBooleanExtra("isInvite",false);
				if(!isInvite){
					update();
				}

			}
		};
		
		broadcastManager.registerReceiver(broadcastReceiver, intentFilter);
	}
		

	
	private void unregisterBroadcastReceiver(){
	    broadcastManager.unregisterReceiver(broadcastReceiver);
	}
	
	@Override
	public boolean onOptionsItemSelected (MenuItem item){
		switch(item.getItemId()){
			case android.R.id.home:
				finish();
				return true;
			case MENU_ID_MOVE_TO_BLACKLIST:
				launchDialogActivityNeedConfirmForMoveIntoBlackList(getString(R.string.move_to_blacklist),getString(R.string.move_to_blacklist_statement));
				return true;
			case MENU_ID_MOVE_FROM_BLACKLIST:
				String easemob_name = userinfo.geteasemob_username();
				moveOutOfBlackList(easemob_name);
				return true;
			case MENU_ID_DELETE:
				launchDialogActivityNeedConfirmForDelete(getString(R.string.delete_contact),getString(R.string.delete_contact_statement));
				return true;

		}
		
		return super.onOptionsItemSelected(item);
		
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		this.menu = menu;
		setIconEnable(menu, true); 
		
		if(userStatus == UserStatus.MYSELF.ordinal()){
			SamLog.i(TAG,"It is myself");
		}else{		
			 
			MenuItem item2 = menu.add(0,MENU_ID_MOVE_TO_BLACKLIST,1,R.string.move_to_blacklist);
			item2.setIcon(R.drawable.ease_search_clear_normal);

			MenuItem item3 = menu.add(0,MENU_ID_MOVE_FROM_BLACKLIST,2,R.string.move_from_blacklist);
			item3.setIcon(R.drawable.ease_search_clear_normal);
			
			MenuItem item4 = menu.add(0,MENU_ID_DELETE,3,R.string.delete);
			item4.setIcon(R.drawable.ease_mm_title_remove);
		}

		return super.onCreateOptionsMenu(menu);  
		
	}

	@Override
	public boolean onPrepareOptionsMenu(android.view.Menu menu){
		if(userStatus == UserStatus.MYSELF.ordinal()){
			return true;
		}else if(userStatus == UserStatus.NOT_FRIEND.ordinal()){
			menu.findItem(MENU_ID_MOVE_TO_BLACKLIST).setVisible(false);
			menu.findItem(MENU_ID_MOVE_FROM_BLACKLIST).setVisible(false);
			menu.findItem(MENU_ID_DELETE).setVisible(false);
		}else if(userStatus == UserStatus.FRIEND_NOT_IN_BLACKLIST.ordinal()){
			menu.findItem(MENU_ID_MOVE_TO_BLACKLIST).setVisible(true);
			menu.findItem(MENU_ID_MOVE_FROM_BLACKLIST).setVisible(false);
			menu.findItem(MENU_ID_DELETE).setVisible(true);
		}else if(userStatus == UserStatus.FRIEND_IN_BLACKLIST.ordinal()){
			menu.findItem(MENU_ID_MOVE_TO_BLACKLIST).setVisible(false);
			menu.findItem(MENU_ID_MOVE_FROM_BLACKLIST).setVisible(true);
			menu.findItem(MENU_ID_DELETE).setVisible(true);
		}

		return true;

	}
	
		
	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);

		setContentView(R.layout.activity_namecard);

		mContext = getBaseContext();

		mBack =  (ImageView) findViewById(R.id.back);
		mBack.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});

		mUserimg =  (ImageView) findViewById(R.id.userimg);

		mUsername = (TextView) findViewById(R.id.username);
		mResidence = (TextView) findViewById(R.id.residence); 
		mVisitence = (TextView) findViewById(R.id.visitence); 
		mCareer_info = (TextView) findViewById(R.id.career_info); 
		mSocial_info = (TextView) findViewById(R.id.social_info); 
		mAdd_friend_layout = (LinearLayout) findViewById(R.id.add_friend_layout); 
		mSend_msg_layout = (LinearLayout) findViewById(R.id.send_msg_layout); 

		mAdd_friend_layout.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				String easemob = userinfo.geteasemob_username();
				launchSendVerifyMsgActivity(easemob);
			}
		});

		mSend_msg_layout.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				String easemob = userinfo.geteasemob_username();
				startActivity(new Intent(NameCardActivity.this, ChatActivity.class).putExtra(EaseConstant.EXTRA_USER_ID, easemob));
			}
		});

		initFromIntent(getIntent());

		registerBroadcastReceiver();

	}
		
	       
	

	private void initFromIntent(Intent intent) {
		userinfo = (ContactUser)intent.getSerializableExtra("ContactUser");

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
		mUsername.setText(userinfo.getusername());
		mResidence.setText("北京");
		mVisitence.setText("旧金山");
		mCareer_info.setText("房产中介");
		mSocial_info.setText("从事跨国房产中介 20年");

		update();


	 }

	@Override
	public void onBackPressed(){
		finish();
	}


	@Override
	public void onDestroy(){
		super.onDestroy();	
		unregisterBroadcastReceiver();
	}

	private void moveOutOfBlackList(final String easemob_name){
		final ProgressDialog pd = new ProgressDialog(this);
		pd.setMessage(getString(R.string.be_removing));
		pd.setCanceledOnTouchOutside(false);
		pd.show();
		new Thread(new Runnable() {
			public void run() {
				try {
					EMContactManager.getInstance().deleteUserFromBlackList(easemob_name);
					runOnUiThread(new Runnable() {
						public void run() {
							pd.dismiss();
							EaseMobHelper.getInstance().sendContactChangeBroadcast();
						}
					});
				} catch (EaseMobException e) {
					e.printStackTrace();
					runOnUiThread(new Runnable() {
						public void run() {
							pd.dismiss();
							Toast.makeText(getApplicationContext(), R.string.Removed_from_the_failure, 0).show();
						}
					});
				}
			}
		}).start();
	}

	private void moveIntoBlackList(final String easemob_name){
		final ProgressDialog pd = new ProgressDialog(this);
		final String st1 = getString(R.string.Is_moved_into_blacklist);
		final String st2 = getResources().getString(R.string.Move_into_blacklist_success);
		final String st3 = getResources().getString(R.string.Move_into_blacklist_failure);

		pd.setMessage(st1);
		pd.setCanceledOnTouchOutside(false);
		pd.show();

		new Thread(new Runnable() {
			public void run() {
				try {
					//加入到黑名单
					EMContactManager.getInstance().addUserToBlackList(easemob_name,false);
					runOnUiThread(new Runnable() {
						public void run() {
							pd.dismiss();
							Toast.makeText(mContext, st2, 0).show();
							EaseMobHelper.getInstance().sendContactChangeBroadcast();
					}
				});
				} catch (EaseMobException e) {
					e.printStackTrace();
					runOnUiThread(new Runnable() {
						public void run() {
							pd.dismiss();
							Toast.makeText(mContext, st3, 0).show();
						}
					});
				}
			}
		}).start();

	}

	

	

	private void deleteContact(final EaseUser tobeDeleteUser) {
		String st1 = getResources().getString(R.string.deleting);
		final String st2 = getResources().getString(R.string.delete_failed);
		final ProgressDialog pd = new ProgressDialog(this);
		pd.setMessage(st1);
		pd.setCanceledOnTouchOutside(false);
		pd.show();
		new Thread(new Runnable() {
			public void run() {
				try {
					EMContactManager.getInstance().deleteContact(tobeDeleteUser.getUsername());
					EaseMobHelper.getInstance().removeContact(tobeDeleteUser);
					InviteMessgeDao inviteMessgeDao =new InviteMessgeDao(skyworld.appContext);
					inviteMessgeDao.deleteMessage(tobeDeleteUser.getUsername());
					runOnUiThread(new Runnable() {
						public void run() {
							pd.dismiss();
							//broadcast Contact change event
							EaseMobHelper.getInstance().sendContactChangeBroadcast();
							NameCardActivity.this.finish();
						}
					});
				} catch (final Exception e) {
					runOnUiThread(new Runnable() {
						public void run() {
							pd.dismiss();
							Toast.makeText(mContext, st2 + e.getMessage(), 1).show();
						}
					});

				}

			}
		}).start();

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {  
		if(requestCode == 1){
			if(resultCode == 1){ //OK
				SamLog.e(TAG,"delete the friend...");
				String easemob = userinfo.geteasemob_username();
				deleteContact(new EaseUser(easemob));
			}else{
				SamLog.e(TAG,"cancel delete the friend...");
			}
		}else if(requestCode == 2){
			if(resultCode == 1){ //OK
				SamLog.e(TAG,"move into blacklist...");
				String easemob = userinfo.geteasemob_username();
				moveIntoBlackList(easemob);
			}else{
				SamLog.e(TAG,"cancel move into blacklist...");
			}

		}
	}


	private void launchDialogActivity(String title,String msg){
		Intent newIntent = new Intent(this,DialogActivity.class);
		int intentFlags = Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP;
		newIntent.setFlags(intentFlags);
		newIntent.putExtra("title", title);
		newIntent.putExtra("message", msg);
		startActivity(newIntent);
	}

	private void launchDialogActivityNeedConfirmForDelete(String title,String msg){
		Intent newIntent = new Intent(DELETE_USERFRIEND_CONFIRM);		
		//int intentFlags = Intent.FLAG_ACTIVITY_NEW_TASK;// | Intent.FLAG_ACTIVITY_CLEAR_TOP;
		//newIntent.setFlags(intentFlags);
		newIntent.putExtra("title", title);
		newIntent.putExtra("message", msg);
		this.startActivityForResult(newIntent, 1);
	}

	private void launchDialogActivityNeedConfirmForMoveIntoBlackList(String title,String msg){
		Intent newIntent = new Intent(this,DialogActivity.class);	
		//int intentFlags = Intent.FLAG_ACTIVITY_NEW_TASK;// | Intent.FLAG_ACTIVITY_CLEAR_TOP;
		//newIntent.setFlags(intentFlags);
		newIntent.putExtra("title", title);
		newIntent.putExtra("message", msg);
		this.startActivityForResult(newIntent, 2);
	}

	private void launchSendVerifyMsgActivity(String easemob_name){
		Intent newIntent = new Intent(this,SendVerifyMsgActivity.class);
		int intentFlags = Intent.FLAG_ACTIVITY_CLEAR_TOP;
		newIntent.setFlags(intentFlags);
		newIntent.putExtra("easemob_name", easemob_name);
		startActivity(newIntent);
	}

}


