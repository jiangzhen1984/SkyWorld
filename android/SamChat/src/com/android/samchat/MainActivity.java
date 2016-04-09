package com.android.samchat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import me.nereo.multi_image_selector.MultiImageSelectorActivity;

import com.android.samchat.R;
import com.android.samchat.easemobdemo.EaseMobHelper;
import com.android.samchat.slidemenu.SlidingMenu;
import com.android.samchat.slidemenu.SlidingMenu.OnOpenedListener;
import com.android.samservice.*;
import com.android.samservice.info.AvatarRecord;
import com.android.samservice.info.ContactUser;
import com.android.samservice.info.FollowerRecord;
import com.android.samservice.info.LoginUser;
import com.easemob.EMCallBack;
import com.easemob.EMConnectionListener;
import com.easemob.EMError;
import com.easemob.EMEventListener;
import com.easemob.EMNotifierEvent;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMGroup;
import com.easemob.chat.EMGroupManager;
import com.easemob.chat.EMMessage;
import com.easemob.chat.EMMessage.ChatType;
import com.easemob.easeui.EaseConstant;
import com.easemob.easeui.controller.EaseUI;
import com.easemob.easeui.domain.EaseUser;
import com.easemob.easeui.ui.EaseContactListFragment.EaseContactListItemClickListener;
import com.easemob.easeui.ui.EaseConversationListFragment.EaseConversationListItemClickListener;
import com.easemob.easeui.ui.EaseGroupRemoveListener;
import com.easemob.easeui.utils.EaseUserUtils;
import com.zijunlin.Zxing.Demo.CaptureActivity;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.Toast;
import android.view.inputmethod.InputMethodManager;

public class MainActivity extends IndicatorFragmentActivity implements
		OnPageChangeListener, EMEventListener{

	static public final String TAG="SamChat_Main";
	static public final String ACTIVITY_NAME = "com.android.samchat.MainActivity";
	
	static final String EXIT_APP_CONFIRM = "com.android.samchat.exitapp";
	static final String LOGOUT_CONFIRM = "com.android.samchat.logout";

	private static final int NOTIFICATION_FLAG = 1; 


	/*Slide menu confirm*/
	public static final int CONFIRM_ID_EXITAPP = 301;
	public static final int CONFIRM_ID_LOGOUT = 302;

	public static final int CONFIRM_ID_CONTACT_ACTIVITY_EXITED=303;

	public static final int CONFIRM_ID_AVATAR_SELECTED=304;
	public static final int CONFIRM_ID_CROP_FINISHED=305;

	public static final int CONFIRM_ID_INVITE_FRIEND_ACTIVITY_EXITED=306;

	public static final int CONFIRM_ID_CAPTURE_ACTIVITY_EXITED=307;
	

	private int sInviteNum = 0;
	
	private boolean isExit = false; 
	
	
	private final int ACTIVITY_TIMEOUT=2000;
	private final int MSG_EXIT_ACTIVITY_TIMEOUT = 1;
	public static final int MSG_LOGOUT_CALLBACK = 2;
	public static final int MSG_LOADING_INIT_DATA = 3;
	public static final int MSG_LOADING_INIT_DATA_FINISH = 4;
	
	
	public static final int TAB_ID_SAMSERVICES=0;
	public static final int TAB_ID_SAMCHATS=1;
	public static final int TAB_ID_SAMPUBLIC=2;
	public static final int TAB_ID_VENDOR=3;
	
	
	//private FragmentTabHost mTabHost;
	private LayoutInflater layoutInflater;
	private Class fragmentArray[] = {	
			SamService_Fragment.class, 
			SamChats_Fragment.class,
			SamPublic_Fragment.class, 
			SamVendor_Fragment.class };
		
	private int imageViewArray[] = {	
			R.drawable.sam_services, 
			R.drawable.sam_chats,
			R.drawable.sam_public,
			R.drawable.sam_vendor};

	private int imageViewArraySelected[] = {	
			R.drawable.sam_services_selected, 
			R.drawable.sam_chats_selected,
			R.drawable.sam_public_selected,
			R.drawable.sam_vendor_selected};
	
	private int textViewArray[] = {
			R.string.sam_services,
			R.string.sam_chats,
			R.string.sam_public,
			R.string.sam_vendor};
	
	private SamService_Fragment fragment_samservice;
	private SamChats_Fragment fragment_samchats;
	private SamPublic_Fragment fragment_sampublic;
	private SamVendor_Fragment fragment_vendor;
	

	private ConnectivityManager mConnectivityManager; 
   	private NetworkInfo netInfo; 


	private TextView mContact_reminder;
	private RelativeLayout mUn_read_new_friend_num_layout;
	private TextView mUn_read_new_friend_num;

	private LinearLayout mContact_layout;
	private LinearLayout mInvite_friend_layout;
	private LinearLayout mSweep_layout;
	private LinearLayout mStart_group_layout;
	private LinearLayout mSettings_layout;	
	private LinearLayout mLogout_layout;
	private LinearLayout mExitapp_layout;
	private TextView mVersion;

	private RelativeLayout mMine_layout;
	private ImageView mAvatar;
	private TextView mUsername;
	
	//private TextView mMe;

	//private ImageView mOption_button;
	private RelativeLayout mOption_button_layout;
	private ImageView mOption_button_reminder;
	

	private SamProcessDialog mDialog;

	private SlidingMenu menu;

	private LocalBroadcastManager broadcastManager;

	private BroadcastReceiver	broadcastReceiver;

	private boolean isContactActivityLaunched = false;
	private boolean isInviteFriendActivityLaunched = false;


	private Uri cropImageUri;

	private boolean isSyncedFollowList = false;
	private boolean isSyncingFollowList = false;

	private String versionName;
	private Context mContext;


	public void sendNotification(){
		NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);  
		Intent newIntent = new Intent(this,SamQAActivity.class);
		int intentFlags = Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP;
		newIntent.setFlags(intentFlags);
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, newIntent, 0);
		Notification notify = new Notification.Builder(this)  
                    .setSmallIcon(R.drawable.notification)
                    .setTicker(getString(R.string.notification)) 
                    .setContentTitle(getString(R.string.notification_title))
                    .setContentText(getString(R.string.notification_content))
                    .setContentIntent(pendingIntent) 
                    .getNotification();
		
		notify.flags |= Notification.FLAG_AUTO_CANCEL;  
		manager.notify(NOTIFICATION_FLAG, notify);  
	}

	public void cancelNotification(){
		NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);  
		manager.cancel(NOTIFICATION_FLAG);
	}

	public void updateReminderIcon( final int id , final boolean show){
		if(show)	{
			if(getCurrentTab() != id){
				updateIndicatorReminderIcon(id,show);
			}
		}else{
			updateIndicatorReminderIcon(id,show);
		}
	}
	

	@Override
	protected int supplyTabs(List<TabInfo> tabs) {
		TabInfo tabinfo = new TabInfo(TAB_ID_SAMSERVICES, getString(textViewArray[0]),
			imageViewArray[0],imageViewArraySelected[0],fragmentArray[0]);
		tabinfo.fragment = new SamService_Fragment();
		tabs.add(tabinfo);

		tabinfo = new TabInfo(TAB_ID_SAMCHATS, getString(textViewArray[1]),
					imageViewArray[1],imageViewArraySelected[1],fragmentArray[1]);
		tabinfo.fragment = new SamChats_Fragment();
		tabs.add(tabinfo);

		tabinfo = new TabInfo(TAB_ID_SAMPUBLIC, getString(textViewArray[2]),
					imageViewArray[2],imageViewArraySelected[2],fragmentArray[2]);
		tabinfo.fragment = new SamPublic_Fragment();
		tabs.add(tabinfo);
		
		tabinfo = new TabInfo(TAB_ID_VENDOR, getString(textViewArray[3]),
					imageViewArray[3],imageViewArraySelected[3],fragmentArray[3]);
		tabinfo.fragment = new SamVendor_Fragment();
		tabs.add(tabinfo);

		return TAB_ID_SAMSERVICES;
	}

	private void launchMineActivity(){
		Intent newIntent = new Intent(this,MineActivity.class);
		int intentFlags = Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP;
		newIntent.setFlags(intentFlags);
		startActivity(newIntent);
	}

	private void launchAvatarActivity(){
		Intent intent = new Intent(MainActivity.this, MultiImageSelectorActivity.class);
		// whether show camera
		intent.putExtra(MultiImageSelectorActivity.EXTRA_SHOW_CAMERA, true);
		// max select image amount
		intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_COUNT, 1);
		// select mode (MultiImageSelectorActivity.MODE_SINGLE OR MultiImageSelectorActivity.MODE_MULTI)
		intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_MODE, MultiImageSelectorActivity.MODE_MULTI);
		// default select images (support array list)
		//intent.putStringArrayListExtra(MultiImageSelectorActivity.EXTRA_DEFAULT_SELECTED_LIST, defaultDataArray);
		startActivityForResult(intent, CONFIRM_ID_AVATAR_SELECTED);
	}

	private void getSWVersion(Context ctx){
		try {  
			PackageManager pm = ctx.getPackageManager();  
			PackageInfo pi = pm.getPackageInfo(ctx.getPackageName(),PackageManager.GET_ACTIVITIES);  
			if (pi != null) {  
				versionName = pi.versionName == null ? "null" : pi.versionName;  
			}  
		} catch (NameNotFoundException e) {  
            
		}  

	}

	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Intent intent = new Intent();
		intent.setAction(SamService.FINISH_ALL_SIGN_ACTVITY);
		sendBroadcast(intent);

		mContext = getBaseContext();
		getSWVersion(mContext);

		initPage();

		mDialog = new SamProcessDialog(this);

		// configure the SlidingMenu
        	menu = new SlidingMenu(this);
        	menu.setMode(SlidingMenu.RIGHT);
        	//menu.setShadowWidthRes(R.dimen.shadow_width);
        	//menu.setShadowDrawable(R.drawable.shadow);
        	menu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
        	menu.setFadeDegree(0.35f);
        	menu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
        	menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
        	menu.setMenu(R.layout.slidemenu);

		menu.setOnOpenedListener(new OnOpenedListener(){
			@Override
			public void onOpened() {
				updateOptionButtonReminder(false);
			}
		});

		mMine_layout = (RelativeLayout)menu.findViewById(R.id.mine_layout);
		mAvatar = (ImageView)menu.findViewById(R.id.avatar);
		mUsername = (TextView)menu.findViewById(R.id.username);

		LoginUser cuser = SamService.getInstance().get_current_user();

		String username = cuser.getusername();
		AvatarRecord rd = SamService.getInstance().getDao().query_AvatarRecord_db_by_username(username);
		if(rd!=null && rd.getavatarname()!=null){
			Bitmap bp = null;
			bp = EaseUserUtils.decodeFile(SamService.sam_cache_path+SamService.AVATAR_FOLDER+"/"+rd.getavatarname(),60,60);

			if(bp!=null){
				mAvatar.setImageBitmap(bp);
			}
		}

		mUsername.setText(cuser.getusername());

		mMine_layout.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				//launchAvatarActivity();
				launchMineActivity();
			}
		});

		mContact_reminder = (TextView)menu.findViewById(R.id.contact_reminder);
		mContact_layout = (LinearLayout)menu.findViewById(R.id.contact_layout);
		mContact_layout.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				menu.toggle();
				isContactActivityLaunched = true;
				//updateContactReminder(false);
				launchContactActivity();
			}
		});

		mUn_read_new_friend_num =  (TextView)menu.findViewById(R.id.un_read_new_friend_num);
		mUn_read_new_friend_num_layout = (RelativeLayout)menu.findViewById(R.id.un_read_new_friend_num_layout); 
		mInvite_friend_layout = (LinearLayout)menu.findViewById(R.id.invite_friend_layout); 
		mInvite_friend_layout.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				menu.toggle();
				isInviteFriendActivityLaunched = true;
				updateInviteFriendReminder(false);
				launchInviteFriendActivity();
			}
		});

		mSweep_layout = (LinearLayout)menu.findViewById(R.id.sweep_layout); 
		mSweep_layout.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				launchCaptureActivity();
			}
		});

		mStart_group_layout = (LinearLayout)menu.findViewById(R.id.start_group_layout);
		mStart_group_layout.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				menu.toggle();
				launchGroupsActivity();
			}
		});

		mSettings_layout = (LinearLayout)menu.findViewById(R.id.settings_layout);
		mSettings_layout.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				menu.toggle();
				launchSettingActivity();
			}
		});

		mLogout_layout = (LinearLayout)menu.findViewById(R.id.logout_layout);
		mLogout_layout.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View arg0) {
					launchDialogActivityNeedConfirmForLogout(getString(R.string.reminder),getString(R.string.logout_reminder));
				}
			});

		
		mExitapp_layout = (LinearLayout)menu.findViewById(R.id.exitapp_layout);
		mExitapp_layout.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View arg0) {
					launchDialogActivityNeedConfirmForExitApp(getString(R.string.reminder),getString(R.string.exitapp_reminder));
				}
			});

		mVersion = (TextView)menu.findViewById(R.id.version);
		mVersion.setText(versionName);
		

		//mOption_button = (ImageView)findViewById(R.id.option_button);
		mOption_button_reminder = (ImageView)findViewById(R.id.option_button_reminder);
		mOption_button_layout = (RelativeLayout) findViewById(R.id.option_button_layout);
		mOption_button_layout.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				menu.toggle();
			}
		});



		//async loading follow list
		
		
		EMGroupManager.getInstance().loadAllGroups();
		EMChatManager.getInstance().loadAllConversations();

		EMChatManager.getInstance().addConnectionListener(connectionListener);
		registerBroadcastReceiver();
		GroupContactInfoDownLoadListener listener = new GroupContactInfoDownLoadListener();
		EMGroupManager.getInstance().addGroupChangeListener(listener);


		Message msg = mHandler.obtainMessage(MSG_LOADING_INIT_DATA);
		mHandler.sendMessage(msg); 

		asyncFollowListFromServer();
		
		asyncDismissLoadInitDataDialog();
		
	}



	synchronized private void  asyncFollowListFromServer(){
		if(isSyncingFollowList){
			return;
		}

		isSyncingFollowList = true;

		new Thread(){
			@Override
			public void run(){
				queryFollowList();
			}
       	}.start();
	}

	private boolean isLoadedFinished(){
		return EaseMobHelper.getInstance().isContactsSyncedWithServer() 
			& EaseMobHelper.getInstance().isGroupsSyncedWithServer()
			& isSyncedFollowList;
	}

	private void asyncDismissLoadInitDataDialog(){
		new Thread(){
			@Override
			public void run(){
				while(!isLoadedFinished()){
					SystemClock.sleep(2000);
				}

				Message msg = mHandler.obtainMessage(MSG_LOADING_INIT_DATA_FINISH);
				mHandler.sendMessage(msg); 
			}
       	}.start();
	}


	

	private void queryFollowList(){
		
		SamService smSrvc = SamService.getInstance(); 
		
		
		smSrvc.queryFollowList(new SMCallBack(){
			@Override
			public void onSuccess(final Object obj) {
				runOnUiThread(new Runnable() {
					public void run() {
						isSyncedFollowList = true;
						isSyncingFollowList = false;

						EaseMobHelper.getInstance().sendFollowerChangeBroadcast();
					}
				});
			}

			@Override
			public void onFailed(int code) {
				runOnUiThread(new Runnable() {
					public void run() {
						isSyncedFollowList = false;
						isSyncingFollowList = false;
						SamLog.e(TAG,"follower onFailed");
						asyncFollowListFromServer();
					}
				});
			}

			@Override
			public void onError(int code) {
				runOnUiThread(new Runnable() {
					public void run() {
						isSyncedFollowList = false;
						isSyncingFollowList = false;
						SamLog.e(TAG,"follower onError");
						asyncFollowListFromServer();
					}
				});
			}

		});

	}

	private void startCropIntent(String path) throws IOException {
		String cropImage = SamService.sam_cache_path + SamService.AVATAR_FOLDER 
			+ "/origin_" + SamService.getInstance().get_current_user().geteasemob_username();
		SamLog.e(TAG,"crop image path:"+cropImage);

		File cropFilePath = new File(SamService.sam_cache_path+SamService.AVATAR_FOLDER);

       	if(!cropFilePath.exists()){
           		cropFilePath.mkdirs();
       	}

        	File cropFile = new File(cropImage);

	 	if(!cropFile.exists()){
 			cropFile.createNewFile();
		}

		cropImageUri = Uri.fromFile(cropFile);

		File file = new File(path);
		Intent intent = new Intent("com.android.camera.action.CROP");
		Uri uri = Uri.fromFile(file);// parse(pathUri);13         
		intent.setDataAndType(uri, "image/*");
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", 600);
		intent.putExtra("outputY", 600);
		// 设置为true直接返回bitmap
		intent.putExtra("return-data", false);
		// 上面设为false的时候将MediaStore.EXTRA_OUTPUT关联一个Uri
		intent.putExtra(MediaStore.EXTRA_OUTPUT, cropImageUri);
		intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
		startActivityForResult(intent, CONFIRM_ID_CROP_FINISHED);
	}



    private Bitmap decodeUriAsBitmap(Uri uri) {
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        return bitmap;
    }

	private byte[] compress(Bitmap image,String outPath, float pixelW, float pixelH){
		ImageFactory zip = new ImageFactory();
		try{
			return zip.compressAndGenImage(image, outPath, 20);
			//return zip.ratioAndGenThumb(image,outPath,pixelW,pixelH);
		}catch(Exception e){
			return null;
			
		}
	}

	private void launchSendVerifyMsgActivity(String easemob_name){
		Intent newIntent = new Intent(this,SendVerifyMsgActivity.class);
		int intentFlags = Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP;;
		newIntent.setFlags(intentFlags);
		newIntent.putExtra("easemob_name", easemob_name);
		startActivity(newIntent);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {  
		SamLog.e(TAG,"main onActivityResult:"+requestCode);
		if(requestCode == CONFIRM_ID_EXITAPP){
			if(resultCode == 1){ //OK
				SamLog.e(TAG,"exit app...");
				exitProgram();
			}else{
				SamLog.e(TAG,"cancel exit app...");
			}
		}else if(requestCode == CONFIRM_ID_LOGOUT){
			if(resultCode == 1){ //OK
				SamLog.e(TAG,"logout...");
				logoutAccount();
			}else{
				SamLog.e(TAG,"cancel logout...");
			}
		}else if(requestCode == CONFIRM_ID_CONTACT_ACTIVITY_EXITED){
			isContactActivityLaunched = false;
		}else if(requestCode ==  CONFIRM_ID_INVITE_FRIEND_ACTIVITY_EXITED){
			isInviteFriendActivityLaunched = false;
			sInviteNum = 0;
		}else if(requestCode == CONFIRM_ID_CAPTURE_ACTIVITY_EXITED){
			if(resultCode == RESULT_OK){
			String easemob_name = data.getStringExtra("easemob_name");
				if(easemob_name!=null){
					launchSendVerifyMsgActivity(easemob_name);
				}
			}

		}else if(requestCode == CONFIRM_ID_AVATAR_SELECTED){
			if(resultCode == Activity.RESULT_OK){
				if( data.hasExtra(MultiImageSelectorActivity.EXTRA_RESULT)) {
					List<String> resultList = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
					if(resultList.get(0)!=null){
						try{
							startCropIntent(resultList.get(0));
						}catch(IOException e){
							e.printStackTrace();
							Toast.makeText(this, R.string.start_crop_window_failed, Toast.LENGTH_SHORT).show();
							cropImageUri = null;
						}
					}
				}
			}
		}else if(requestCode == CONFIRM_ID_CROP_FINISHED){
			if(resultCode == Activity.RESULT_OK){
				SamLog.e(TAG,"crop image successfully");
				Bitmap bp = decodeUriAsBitmap(cropImageUri);
				byte [] bytes;
				if(bp!=null){
					if((bytes = compress(bp, SamService.sam_cache_path + SamService.AVATAR_FOLDER 
								+ "/origin_" + SamService.getInstance().get_current_user().geteasemob_username(),
							60, 60))!=null){
						if (bytes.length != 0) {
							bp =  BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
						}

						mAvatar.setImageBitmap(bp);
						uploadAvatar(SamService.sam_cache_path + SamService.AVATAR_FOLDER 
									+ "/origin_" + SamService.getInstance().get_current_user().geteasemob_username());
					}

					/*bp = compress(bp, SamService.sam_cache_path + SamService.AVATAR_FOLDER 
								+ "/origin_" + SamService.getInstance().get_current_user().geteasemob_username(),
							50, 50);
					if(bp!=null){
						mAvatar.setImageBitmap(bp);
						uploadAvatar(SamService.sam_cache_path + SamService.AVATAR_FOLDER 
									+ "/origin_" + SamService.getInstance().get_current_user().geteasemob_username());
					}*/
				}
				
			}
		}else{
			super.onActivityResult(requestCode, resultCode, data);
		}
	}


	private void uploadAvatar(String filePath){
		if(mDialog!=null){
    			mDialog.launchProcessDialog(this,getString(R.string.uploading_avatar));
    		}
		
		SamService.getInstance().upload_avatar(filePath, new SMCallBack(){
			@Override
			public void onSuccess(Object obj) {
				StringBuffer oldAvatar = new StringBuffer();
				SamService.getInstance().getDao().add_update_AvatarRecord_db(
					SamService.getInstance().get_current_user().getphonenumber(),
					SamService.getInstance().get_current_user().getusername(),
					"origin_" + SamService.getInstance().get_current_user().geteasemob_username(),
					oldAvatar
				);

				SamLog.e(TAG,"oldAvatar:"+oldAvatar);

				if(oldAvatar.length()!=0 && !oldAvatar.equals("origin_" + SamService.getInstance().get_current_user().geteasemob_username())){
					SamService.getInstance().deleteOldAvatar(oldAvatar.toString());
				}
			
				if(mDialog!=null){
    					mDialog.dismissPrgoressDiglog();
    				}
				SamLog.e(TAG,"upload avatar succeed");

				
			}

			@Override
			public void onFailed(int code) {
				if(mDialog!=null){
    					mDialog.dismissPrgoressDiglog();
    				}
				SamLog.e(TAG,"upload avatar failed");
			}

			@Override
			public void onError(int code) {
				if(mDialog!=null){
    					mDialog.dismissPrgoressDiglog();
    				}
				SamLog.e(TAG,"upload avatar error");
			}

		});

	}

	private void logoutAccount(){
		if(mDialog!=null){
    			mDialog.launchProcessDialog(this,getString(R.string.logout));
    		}

		EaseMobHelper.getInstance().logout(true,new EMCallBack() {
                    
                    @Override
                    public void onSuccess() {
                    	MainActivity.this.runOnUiThread(new Runnable() {
                            public void run() {
					LoginUser user = SamService.getInstance().get_current_user();
					user.seteasemob_status(LoginUser.INACTIVE);
					SamService.getInstance().getDao().updateLoginUserEaseStatus(user.getusername(),LoginUser.INACTIVE);			
                            
					SignService.getInstance().SignOut( mHandler, MSG_LOGOUT_CALLBACK);
				  }
                        });
                    }
                    
                    @Override
                    public void onProgress(int progress, String status) {}
                    
                    @Override
                    public void onError(int code, String message) {
                    	MainActivity.this.runOnUiThread(new Runnable() {
                            public void run() {
					LoginUser user = SamService.getInstance().get_current_user();
					user.seteasemob_status(LoginUser.INACTIVE);
					SamService.getInstance().getDao().updateLoginUserEaseStatus(user.getusername(),LoginUser.INACTIVE);

					SignService.getInstance().SignOut( mHandler, MSG_LOGOUT_CALLBACK);
								
                            }
                        });
                    }
                });
		
		
	}

	private void launchSignInActivity()
	{
		Intent newIntent = new Intent(this,SignInActivity.class);
		int intentFlags = Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP;
		newIntent.setFlags(intentFlags);
		startActivity(newIntent);
	}

	private void exitProgram(){
		exitProgrames();
	}

	private void launchCaptureActivity(){
		Intent newIntent = new Intent(this,CaptureActivity.class);
		int intentFlags = Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP;
		newIntent.setFlags(intentFlags);
		startActivityForResult(newIntent, CONFIRM_ID_CAPTURE_ACTIVITY_EXITED);
	}

	private void launchContactActivity(){
		Intent newIntent = new Intent(this,ContactActivity.class);
		int intentFlags = Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP;
		newIntent.setFlags(intentFlags);
		startActivityForResult(newIntent, CONFIRM_ID_CONTACT_ACTIVITY_EXITED);
	}

	private void launchInviteFriendActivity()
	{
		Intent newIntent = new Intent(this,NewFriendActivity.class);
		int intentFlags = Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP;
		newIntent.setFlags(intentFlags);
		SamLog.e(TAG,"launchNewFriendActivity!");
		startActivityForResult(newIntent,CONFIRM_ID_INVITE_FRIEND_ACTIVITY_EXITED);
	}

	private void launchGroupsActivity()
	{
		startActivity(new Intent(this, GroupsActivity.class));
		SamLog.e(TAG,"launchGroupsActivity!");
	}

	private void launchSettingActivity(){
		Intent newIntent = new Intent(this,SettingActivity.class);
		int intentFlags = Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP;;
		newIntent.setFlags(intentFlags);
		startActivity(newIntent);

	}

	private void launchDialogActivityNeedConfirmForExitApp(String title,String msg){
		Intent newIntent = new Intent(EXIT_APP_CONFIRM);		
		//int intentFlags = Intent.FLAG_ACTIVITY_NEW_TASK;// | Intent.FLAG_ACTIVITY_CLEAR_TOP;
		//newIntent.setFlags(intentFlags);
		newIntent.putExtra("title", title);
		newIntent.putExtra("message", msg);
		this.startActivityForResult(newIntent, CONFIRM_ID_EXITAPP);
	}

	private void launchDialogActivityNeedConfirmForLogout(String title,String msg){
		Intent newIntent = new Intent(LOGOUT_CONFIRM);		
		//int intentFlags = Intent.FLAG_ACTIVITY_NEW_TASK;// | Intent.FLAG_ACTIVITY_CLEAR_TOP;
		//newIntent.setFlags(intentFlags);
		newIntent.putExtra("title", title);
		newIntent.putExtra("message", msg);
		this.startActivityForResult(newIntent, CONFIRM_ID_LOGOUT);
	}

	

	

	/**
	 * 初始化Fragment
	 */
	private void initPage() {
		fragment_samservice = (SamService_Fragment)getFragment(TAB_ID_SAMSERVICES);//new SamService_Fragment();
		fragment_samchats = (SamChats_Fragment)getFragment(TAB_ID_SAMCHATS);//new SamChats_Fragment();
		fragment_sampublic = (SamPublic_Fragment)getFragment(TAB_ID_SAMPUBLIC);
		fragment_samchats.setConversationListItemClickListener(new EaseConversationListItemClickListener() {
			@Override
			public void onListItemClicked(EMConversation conversation) {
				Intent newIntent = new Intent(MainActivity.this, ChatActivity.class);
				if(conversation.isGroup()){
					newIntent.putExtra(Constants.EXTRA_CHAT_TYPE, Constants.CHATTYPE_GROUP);
				}

				newIntent.putExtra(Constants.EXTRA_USER_ID, conversation.getUserName());
				startActivity(newIntent);
			}
		});
		
		//fragment_samme = (SamMe_Fragment)getFragment(TAB_ID_SAMME);//new SamMe_Fragment();
		
		//fragment_samcontact= (SamContact_Fragment)getFragment(TAB_ID_SAMPUBLIC);//new SamContact_Fragment();
		

		//registerBroadcastReceiver();
		registerNetworkStatusReceiver();
		EMChatManager.getInstance().registerEventListener(this,
				new EMNotifierEvent.Event[] { EMNotifierEvent.Event.EventNewMessage ,EMNotifierEvent.Event.EventOfflineMessage, EMNotifierEvent.Event.EventConversationListChanged});
		
		SamService.getInstance().onActivityLaunched(fragment_samservice,fragment_samchats);
	}


	private void sendGroupMemberInfoUpdateBroadcast(final String groupId){
		Intent newIntent = new Intent(Constants.GROUP_MEMBER_INFO_UPDATE);
		newIntent.putExtra("groupId",groupId);
		LocalBroadcastManager.getInstance(this).sendBroadcast(newIntent);
	}
	
	private void updateGroupInfo(final String groupId){
		new Thread(new Runnable() {
			public void run() {
				try {
					final EMGroup returnGroup = EMGroupManager.getInstance().getGroupFromServer(groupId);
					// 更新本地数据
					EMGroupManager.getInstance().createOrUpdateLocalGroup(returnGroup);
					//update member info db
					List<String> members = returnGroup.getMembers();
					List<String> needMembers = new ArrayList<String>();
					
					for(String member: members){
						if(SamService.getInstance().getDao().query_ContactUser_db_by_username(member) == null){
							needMembers.add(member);
						}
					}

					if(needMembers.size()>0){
						SamService.getInstance().query_user_info_from_server(needMembers, new SMCallBack(){
						@Override
						public void onSuccess(final Object obj){
							sendGroupMemberInfoUpdateBroadcast(groupId);
						} 

						@Override
						public void onFailed(int code) {

						}

						@Override
						public void onError(int code) {

						}

						});
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	@Override
	public void onEvent(EMNotifierEvent event) {
	SamLog.e(TAG,"onEvent!!!");
		switch (event.getEvent()) {
		case EventNewMessage: 
		{
			EMMessage message = (EMMessage) event.getData();

			//EaseUI.getInstance().getNotifier().onNewMsg(message);

			refreshUIWithMessage();

			String groupId = null;
            		// 群组消息
			if (message.getChatType() == ChatType.GroupChat) {
				groupId = message.getTo();
				updateGroupInfo(groupId);
			}
			
			break;
		}

		case EventOfflineMessage: {
		    refreshUIWithMessage();
			break;
		}

		case EventConversationListChanged: {
		    refreshUIWithMessage();
		    break;
		}
		
		default:
			break;
		}
	}

	private void refreshUIWithMessage() {
		SamLog.e(TAG,"refreshUIWithMessage!!!");
		runOnUiThread(new Runnable() {
			public void run() {
				if (fragment_samchats != null) {
					fragment_samchats.refresh();
				}
			}
		});
	}

	private void refreshPublicUI(final List<ContactUser> follwerList) {
		SamLog.e(TAG,"refreshPublicUI!!!");
		runOnUiThread(new Runnable() {
			public void run() {
				if (fragment_sampublic!= null) {
					fragment_sampublic.refresh(follwerList);
				}
			}
		});
	}
   
   
	private BroadcastReceiver myNetReceiver = new BroadcastReceiver() { 
   		@Override 
		public void onReceive(Context context, Intent intent) { 
     			String action = intent.getAction(); 
			if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) { 
				mConnectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE); 
				netInfo = mConnectivityManager.getActiveNetworkInfo();   
				if(netInfo != null && netInfo.isAvailable()) { 
					SamLog.e(TAG,"network connected!!!!!!!!!!!!");
   					SamService.getInstance().onNetworkConnect();
				} else { 
					SamLog.e(TAG,"network disconnected!!!!!!!!!!!!");
   					SamService.getInstance().onNetworkDisconnect();
				} 
			} 
   
		}  
	}; 

	private void registerNetworkStatusReceiver(){
		IntentFilter mFilter = new IntentFilter(); 
		mFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION); 
		registerReceiver(myNetReceiver, mFilter); 
	}

	private void unregisterNetworkStatusReceiver(){
		if(myNetReceiver!=null){ 
			unregisterReceiver(myNetReceiver); 
		}
	}

	




	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		super.onPageScrolled(arg0,arg1,arg2);
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		if(getCurrentFocus()!=null){
			imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}

	@Override
	public void onPageSelected(int position) {
		super.onPageSelected(position);
		updateReminderIcon(position,false);
	}


	/*(private void updateTextColor(int arg0){
		int count = textViewArray.length;
		
		for(int i=0;i<count;i++){
			if(i!=arg0){
				TextView txtView = (TextView) mTabHost.getTabWidget().getChildAt(i).findViewById(R.id.tab_textview); 
				txtView.setTextColor(android.graphics.Color.BLACK);
			}else{
				TextView txtView = (TextView) mTabHost.getTabWidget().getChildAt(i).findViewById(R.id.tab_textview); 
				txtView.setTextColor(android.graphics.Color.GREEN);
			}
		}
	
	}

	private void updateRedPoint(int arg0 , boolean enable){
		ImageView redv = (ImageView)mTabHost.getTabWidget().getChildAt(arg0).findViewById(R.id.tab_redSmallPoint);
		if(enable){
			redv.setVisibility(View.VISIBLE);
		}else{
			redv.setVisibility(View.INVISIBLE);
		}
	}*/

	
	
	/*@Override
	public void onPageSelected(int arg0) {
		currentTabPostition  = arg0;
		SamLog.i(TAG,"currentTabPostition:"+currentTabPostition);
		TabWidget widget = mTabHost.getTabWidget();
		int oldFocusability = widget.getDescendantFocusability();
		widget.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
		mTabHost.setCurrentTab(arg0);
		widget.setDescendantFocusability(oldFocusability);
		//widget.setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);
		updateTextColor(arg0);
		//updateRedPoint(arg0,false);
		//.setBackgroundResource(R.drawable.selector_tab_background);

		
	}

	@Override
	public void onTabChanged(String tabId) {
		int position = mTabHost.getCurrentTab();
		vp.setCurrentItem(position);
	}*/


	
	
	@Override
	protected void onPause() {
	    super.onPause();
	}
	 
	@Override
	protected void onResume(){
	    super.onResume();
	}
	 
	@Override
	protected void onDestroy(){
		super.onDestroy();
		SamLog.i(TAG,"MainActivity onDestroy!");
		cancelNotification();
		unregisterNetworkStatusReceiver();
		unregisterBroadcastReceiver();
		EMChatManager.getInstance().removeConnectionListener(connectionListener);
		EaseMobHelper.getInstance().reset();

	}
	
	@Override
	public void onBackPressed(){
		if(menu.isMenuShowing()){
			menu.toggle();
			return;
		}
		
		Intent i= new Intent(Intent.ACTION_MAIN);
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		i.addCategory(Intent.CATEGORY_HOME);
		startActivity(i);
	}

	public void exitProgrames(){
		if(mDialog!=null){
			mDialog.launchProcessDialog(this,getString(R.string.exiting));
		}
		
		SamService.getInstance().stopSamService();
		if(mDialog!=null){
			mDialog.dismissPrgoressDiglog();
		}
		
		this.finish(); 
	}

       
    /*private void exit() { 
        if (!isExit) { 
            isExit = true; 
            Toast.makeText(getApplicationContext(), getString(R.string.exit_app_confirmation), 
                    Toast.LENGTH_SHORT).show(); 
            mHandler.sendEmptyMessageDelayed(MSG_EXIT_ACTIVITY_TIMEOUT,ACTIVITY_TIMEOUT); 
        } else { 
             
            SamLog.e(TAG, "exit application"); 

	     SamService.getInstance().stopSamService();
               
            this.finish(); 
        } 
    } */
    
	Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			
	    		switch(msg.what){
	    			case MSG_EXIT_ACTIVITY_TIMEOUT:
	    				isExit = false;
		    		break;
					
				case MSG_LOGOUT_CALLBACK:
				if(msg.arg1 == SignService.R_SIGN_OUT_OK){
					if(mDialog!=null){
    						mDialog.dismissPrgoressDiglog();
    					}
					exitActivity();
					launchSignInActivity();
				}else if(msg.arg1 == SignService.R_SIGN_OUT_FAILED){
					if(mDialog!=null){
    						mDialog.dismissPrgoressDiglog();
    					}
					exitActivity();
					launchSignInActivity();
				}
				break;

				case MSG_LOADING_INIT_DATA:
					if(mDialog!=null){
    						mDialog.launchProcessDialog(MainActivity.this,getString(R.string.load_data));
    					}
					break;

				case MSG_LOADING_INIT_DATA_FINISH:
					if(mDialog!=null){
    						mDialog.dismissPrgoressDiglog();
    					}
					break;

				
	    		}
		}
	};

	protected EMConnectionListener connectionListener = new EMConnectionListener() {
		@Override
		public void onDisconnected(final int error) {
			MainActivity.this.runOnUiThread(new Runnable() {
				public void run() {
					if (error == EMError.USER_REMOVED || error == EMError.CONNECTION_CONFLICT) {
						if(mDialog!=null){
    							mDialog.launchProcessDialog(MainActivity.this,getString(R.string.exiting_user_conflict));
    						}

						EaseMobHelper.getInstance().reset();

						SignService.getInstance().SignOut( mHandler, MSG_LOGOUT_CALLBACK);
						LoginUser user = SamService.getInstance().get_current_user();
						user.seteasemob_status(LoginUser.INACTIVE);
						SamService.getInstance().getDao().updateLoginUserEaseStatus(user.getusername(),LoginUser.INACTIVE);	
					} 
				}
			});
			
		}
        
		@Override
		public void onConnected() {
			MainActivity.this.runOnUiThread(new Runnable() {
				public void run() {
					
				}
			});
		}
	};


	public void exitActivity(){
		SamLog.e(TAG, "exit main activity"); 
		SamService.getInstance().stopSamService();
		this.finish();
	}


	private void updateOptionButtonReminder(boolean show){
		if(show){
			mOption_button_reminder.setVisibility(View.VISIBLE);
		}else{
			mOption_button_reminder.setVisibility(View.INVISIBLE);
		}
	}

	private void updateContactReminder(boolean show){
		if(show){
			mContact_reminder.setVisibility(View.VISIBLE);
		}else{
			mContact_reminder.setVisibility(View.INVISIBLE);
		}
	}

	private void updateInviteFriendReminder(boolean show){
		if(show){
			mUn_read_new_friend_num.setText(""+(sInviteNum<=99?sInviteNum:99));
			mUn_read_new_friend_num_layout.setVisibility(View.VISIBLE);
		}else{
			mUn_read_new_friend_num_layout.setVisibility(View.INVISIBLE);
		}
	}

	private void registerBroadcastReceiver() {
		broadcastManager = LocalBroadcastManager.getInstance(this);
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(Constants.ACTION_CONTACT_CHANAGED);
		intentFilter.addAction(Constants.ACTION_AVATAR_UPDATE);
		intentFilter.addAction(Constants.ACTION_QAACTIVITY_DESTROYED);
		

		broadcastReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				if(intent.getAction().equals(Constants.ACTION_CONTACT_CHANAGED)){
					boolean isInvite = intent.getBooleanExtra("isInvite",false);
					if(isInvite){
						if(!menu.isMenuShowing() && !isInviteFriendActivityLaunched){
							updateOptionButtonReminder(true);
						}

						if(!isInviteFriendActivityLaunched){
							sInviteNum ++;
							updateInviteFriendReminder(true);
						}
					}
					
				}else if(intent.getAction().equals(Constants.ACTION_AVATAR_UPDATE)){
					LoginUser cuser = SamService.getInstance().get_current_user();

					String username = cuser.getusername();
					AvatarRecord rd = SamService.getInstance().getDao().query_AvatarRecord_db_by_username(username);
					if(rd!=null && rd.getavatarname()!=null){
						Bitmap bp = null;
						bp = EaseUserUtils.decodeFile(SamService.sam_cache_path+SamService.AVATAR_FOLDER+"/"+rd.getavatarname(),60,60);

						if(bp!=null){
							mAvatar.setImageBitmap(bp);
						}
					}
				}else if(intent.getAction().equals(Constants.ACTION_QAACTIVITY_DESTROYED)){
					fragment_samchats.dismissBage();
					updateReminderIcon(MainActivity.TAB_ID_SAMCHATS,false);
				}
				

			}
		};
		
		broadcastManager.registerReceiver(broadcastReceiver, intentFilter);
	}
		

	
	private void unregisterBroadcastReceiver(){
	    broadcastManager.unregisterReceiver(broadcastReceiver);
	}

	private EMGroup downloadGroupInfo(String groupId){
		EMGroup group = null;
		try{
			group= EMGroupManager.getInstance().getGroupFromServer(groupId);
			group =EMGroupManager.getInstance().createOrUpdateLocalGroup(group);
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}

		return group;
	}

	private class GroupContactInfoDownLoadListener extends EaseGroupRemoveListener {
		@Override
		public void onInvitationReceived(String groupId, String groupName, String inviter, String reason) {
			SamLog.i(TAG,"onInvitationReceived groundId:"+groupId
						+" groupName:"+groupName+" inviter:"+inviter+" reason:"+reason);

			EMGroup group = downloadGroupInfo(groupId);

			if(group == null){
				return;
			}
			
			List<String> members = group.getMembers();
			SamLog.i(TAG,"members size:"+members.size());
			List<String> needMembers = new ArrayList<String>();
			for(String member: members){
				if(SamService.getInstance().getDao().query_ContactUser_db_by_username(member) == null){
					needMembers.add(member);
				}
			}

			if(needMembers.size()>0){
				SamService.getInstance().query_user_info_from_server(needMembers, new SMCallBack(){
					@Override
					public void onSuccess(final Object obj){

					} 

					@Override
					public void onFailed(int code) {

					}

					@Override
					public void onError(int code) {

					}

				});
			}
			
			
			
		}

		@Override
		public void onApplicationReceived(String groupId, String groupName, String applyer, String reason) {
			SamLog.i(TAG,"onApplicationReceived");
		}

		@Override
		public void onApplicationAccept(String groupId, String groupName, String accepter) {
        		SamLog.i(TAG,"onApplicationAccept");
		}

		@Override
		public void onApplicationDeclined(String groupId, String groupName, String decliner, String reason) {
			SamLog.i(TAG,"onApplicationDeclined");
		}

		@Override
		public void onInvitationAccpted(String groupId, String inviter, String reason) {
        		SamLog.i(TAG,"onInvitationAccpted");
		}

		@Override
		public void onInvitationDeclined(String groupId, String invitee, String reason) {
			SamLog.i(TAG,"onInvitationDeclined");
        	}

		@Override
		public void onUserRemoved(final String groupId, String groupName) {
			SamLog.i(TAG,"onUserRemoved");
		}

		@Override
		public void onGroupDestroy(final String groupId, String groupName) {
			SamLog.i(TAG,"onGroupDestroy");
		}

	}

	



	
}
