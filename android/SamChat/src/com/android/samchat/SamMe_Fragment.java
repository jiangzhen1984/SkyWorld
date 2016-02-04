package com.android.samchat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.android.samchat.R;
import com.android.samchat.easemobdemo.EaseMobHelper;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.samservice.*;
import com.android.samservice.info.AvatarRecord;
import com.android.samservice.info.LoginUser;
import com.easemob.EMCallBack;
import com.easemob.EMConnectionListener;
import com.easemob.EMError;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMContactManager;
import com.easemob.easeui.utils.EaseUserUtils;

import me.nereo.multi_image_selector.*;

public class SamMe_Fragment extends Fragment{
	static final String TAG = "SamMe_Fragment";

	static final String EXIT_APP_CONFIRM = "com.android.samchat.exitapp";
	static final String LOGOUT_CONFIRM = "com.android.samchat.logout";
	static final String UPGRADE_CONFIRM = "com.android.samchat.upgrade";

	public static final int MSG_LOGOUT_CALLBACK = 1;
	public static final int MSG_UPGRADE_CALLBACK = 2;

	private View rootView;
	private TextView mMyname;
	private LinearLayout mSettingLayout;
	private LinearLayout mLogoutLayout;
	private LinearLayout mExitappLayout;
	private LinearLayout mUpgradeLayout;
	private LinearLayout mAvatar_layout;

	private TextView mUpgrade_spec;

	private SamProcessDialog mDialog;

	private Uri cropImageUri;
	private ImageView mAvatar;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		SamLog.i(TAG, "onCreateView");
		
		if(rootView == null){
			mDialog = new SamProcessDialog();
			rootView = inflater.inflate(R.layout.fragment_me, container,false);
			mMyname = (TextView)rootView.findViewById(R.id.myname);
			mMyname.setText(SamService.getInstance().get_current_user().getusername());
			mSettingLayout = (LinearLayout)rootView.findViewById(R.id.setting_layout);
			mUpgradeLayout = (LinearLayout)rootView.findViewById(R.id.upgrade_layout);
			mLogoutLayout = (LinearLayout)rootView.findViewById(R.id.logout_layout);
			mExitappLayout = (LinearLayout)rootView.findViewById(R.id.exitapp_layout);

			mUpgrade_spec = (TextView)rootView.findViewById(R.id.upgrade_spec);
			mAvatar_layout = (LinearLayout)rootView.findViewById(R.id.avatar_layout);
			mAvatar = (ImageView)rootView.findViewById(R.id.avatar);
			
			mSettingLayout.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View arg0) {
					launchSettingActivity();
				}
			});
			
			mUpgradeLayout.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View arg0) {
					if(SamService.getInstance().get_current_user().usertype > LoginUser.USER){
						//launchDialogActivity(getString(R.string.reminder), getString(R.string.upgrade_already));
						return;
					}else{
						SamLog.e(TAG,"upgrade to servicer");
						launchDialogActivityNeedConfirmForUpgrade(getString(R.string.reminder),getString(R.string.upgrade_reminder));
					}
				}
			});
			
			mLogoutLayout.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View arg0) {
					launchDialogActivityNeedConfirmForLogout(getString(R.string.reminder),getString(R.string.logout_reminder));
				}
			});
			
			mExitappLayout.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View arg0) {
					launchDialogActivityNeedConfirmForExitApp(getString(R.string.reminder),getString(R.string.exitapp_reminder));
				}
			});

			mAvatar_layout.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View arg0) {
					launchAvatarActivity();
				}
			});
			
		}
		return rootView;
	}

	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		SamLog.i(TAG, "onCreated");
		EMChatManager.getInstance().addConnectionListener(connectionListener);
	}
	
	@Override
	public void onAttach(Activity activity){
		super.onAttach(activity);
		SamLog.i(TAG, "onAttach");
	}
	
	@Override
	public void onStart(){
		super.onStart();
		SamLog.i(TAG, "onStart");
	}
	
	@Override
	public void onResume(){
		super.onResume();
		SamLog.i(TAG, "onResume");
	}
	
	
	@Override
	public void onDetach(){
		super.onDetach();
		SamLog.i(TAG, "onDetach");
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState){
		super.onActivityCreated(savedInstanceState);
		SamLog.i(TAG, "onActivityCreated");
		LoginUser cuser = SamService.getInstance().get_current_user();
		if(cuser.getUserType()> LoginUser.USER){
			mUpgrade_spec.setText(getString(R.string.sam_gbdy));
		}

		String phonenumber = cuser.getphonenumber();
		AvatarRecord rd = SamService.getInstance().getDao().query_AvatarRecord_db(phonenumber);
		if(rd!=null && rd.getavatarname()!=null){
			Bitmap bp = null;
			bp = EaseUserUtils.decodeFile(SamService.sam_cache_path+SamService.AVATAR_FOLDER+"/"+rd.getavatarname(),60,60);

			if(bp!=null){
				mAvatar.setImageBitmap(bp);
			}
		}
	}
	
	@Override
	public void onPause(){
		super.onPause();
		SamLog.i(TAG, "onPause");
	}
	
	@Override
	public void onStop(){
		super.onStop();
		SamLog.i(TAG, "onStop");
	}
	
	@Override
	public void onDestroyView(){
		super.onDestroyView();
		if(rootView != null){
			SamLog.i(TAG, "onDestroyView");
			((ViewGroup)(rootView.getParent())).removeView(rootView);
		}
	}

	@Override
	public void onDestroy() {
		EMChatManager.getInstance().removeConnectionListener(connectionListener);
		super.onDestroy();
	}
	

	private void launchDialogActivity(String title,String msg){
		Intent newIntent = new Intent(getActivity(),DialogActivity.class);
		int intentFlags = Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP;
		newIntent.setFlags(intentFlags);
		newIntent.putExtra("title", title);
		newIntent.putExtra("message", msg);
		startActivity(newIntent);
	}	

	private void launchDialogActivityNeedConfirmForExitApp(String title,String msg){
		Intent newIntent = new Intent(EXIT_APP_CONFIRM);		
		//int intentFlags = Intent.FLAG_ACTIVITY_NEW_TASK;// | Intent.FLAG_ACTIVITY_CLEAR_TOP;
		//newIntent.setFlags(intentFlags);
		newIntent.putExtra("title", title);
		newIntent.putExtra("message", msg);
		this.startActivityForResult(newIntent, 1);
	}

	private void launchDialogActivityNeedConfirmForLogout(String title,String msg){
		Intent newIntent = new Intent(LOGOUT_CONFIRM);		
		//int intentFlags = Intent.FLAG_ACTIVITY_NEW_TASK;// | Intent.FLAG_ACTIVITY_CLEAR_TOP;
		//newIntent.setFlags(intentFlags);
		newIntent.putExtra("title", title);
		newIntent.putExtra("message", msg);
		this.startActivityForResult(newIntent, 2);
	}

	private void launchDialogActivityNeedConfirmForUpgrade(String title,String msg){
		Intent newIntent = new Intent(UPGRADE_CONFIRM);		
		//int intentFlags = Intent.FLAG_ACTIVITY_NEW_TASK;// | Intent.FLAG_ACTIVITY_CLEAR_TOP;
		//newIntent.setFlags(intentFlags);
		newIntent.putExtra("title", title);
		newIntent.putExtra("message", msg);
		this.startActivityForResult(newIntent, 3);
	}

	private void launchAvatarActivity(){
		Intent intent = new Intent(getActivity(), MultiImageSelectorActivity.class);
		// whether show camera
		intent.putExtra(MultiImageSelectorActivity.EXTRA_SHOW_CAMERA, true);
		// max select image amount
		intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_COUNT, 1);
		// select mode (MultiImageSelectorActivity.MODE_SINGLE OR MultiImageSelectorActivity.MODE_MULTI)
		intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_MODE, MultiImageSelectorActivity.MODE_MULTI);
		// default select images (support array list)
		//intent.putStringArrayListExtra(MultiImageSelectorActivity.EXTRA_DEFAULT_SELECTED_LIST, defaultDataArray);
		startActivityForResult(intent, 4);
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
		startActivityForResult(intent, 5);
	}



    private Bitmap decodeUriAsBitmap(Uri uri) {
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(uri));
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


	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {  
		if(requestCode == 1){
			if(resultCode == 1){ //OK
				SamLog.e(TAG,"exit app...");
				exitProgram();
			}else{
				SamLog.e(TAG,"cancel exit app...");
			}
		}else if(requestCode == 2){
			if(resultCode == 1){ //OK
				SamLog.e(TAG,"logout...");
				logoutAccount();
			}else{
				SamLog.e(TAG,"cancel logout...");
			}
		}else if(requestCode == 3){
			if(resultCode == 1){//OK
				SamLog.e(TAG,"upgrade...");
				upgradeToServicer();
			}else{
				SamLog.e(TAG,"cancel upgrade...");
			}
		}else if(requestCode == 4){
			if(resultCode == Activity.RESULT_OK){
				if( data.hasExtra(MultiImageSelectorActivity.EXTRA_RESULT)) {
					List<String> resultList = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
					if(resultList.get(0)!=null){
						try{
							startCropIntent(resultList.get(0));
						}catch(IOException e){
							e.printStackTrace();
							Toast.makeText(getActivity(), R.string.start_crop_window_failed, Toast.LENGTH_SHORT).show();
							cropImageUri = null;
						}
					}
				}
			}
		}else if(requestCode == 5){
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
		}
	}

	private void uploadAvatar(String filePath){
		if(mDialog!=null){
    			mDialog.launchProcessDialog(getActivity(),getString(R.string.uploading_avatar));
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
    			mDialog.launchProcessDialog(getActivity(),getString(R.string.question_publish_now));
    		}

		EaseMobHelper.getInstance().logout(true,new EMCallBack() {
                    
                    @Override
                    public void onSuccess() {
                        getActivity().runOnUiThread(new Runnable() {
                            public void run() {
					SignService.getInstance().SignOut( mHandler, MSG_LOGOUT_CALLBACK);
					LoginUser user = SamService.getInstance().get_current_user();
					user.seteasemob_status(LoginUser.INACTIVE);
					SamService.getInstance().getDao().updateLoginUserEaseStatus(user.getphonenumber(),LoginUser.INACTIVE);			
                            }
                        });
                    }
                    
                    @Override
                    public void onProgress(int progress, String status) {}
                    
                    @Override
                    public void onError(int code, String message) {
                        getActivity().runOnUiThread(new Runnable() {
                            public void run() {
					SignService.getInstance().SignOut( mHandler, MSG_LOGOUT_CALLBACK);
					LoginUser user = SamService.getInstance().get_current_user();
					user.seteasemob_status(LoginUser.INACTIVE);
					SamService.getInstance().getDao().updateLoginUserEaseStatus(user.getphonenumber(),LoginUser.INACTIVE);			
                            }
                        });
                    }
                });
		
		
	}
	
	private void upgradeToServicer(){
		if(mDialog!=null){
    			mDialog.launchProcessDialog(getActivity(),getString(R.string.process));
    		}

		SamService.getInstance().upgrade( mHandler, MSG_UPGRADE_CALLBACK);

		
	}
	

	private void launchSettingActivity(){
		Intent newIntent = new Intent(getActivity(),SettingActivity.class);
		int intentFlags = Intent.FLAG_ACTIVITY_CLEAR_TOP;
		newIntent.setFlags(intentFlags);
		startActivity(newIntent);

	}
	
	private void launchSignInActivity()
	{
		Intent newIntent = new Intent(getActivity(),SignInActivity.class);
		int intentFlags = Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP;
		newIntent.setFlags(intentFlags);
		startActivity(newIntent);
	}

	private void exitProgram(){
		((MainActivity)getActivity()).exitProgrames();
	}


	protected EMConnectionListener connectionListener = new EMConnectionListener() {
		@Override
		public void onDisconnected(final int error) {
			getActivity().runOnUiThread(new Runnable() {
				public void run() {
					if (error == EMError.USER_REMOVED || error == EMError.CONNECTION_CONFLICT) {
						if(mDialog!=null){
    							mDialog.launchProcessDialog(getActivity(),getString(R.string.question_publish_now));
    						}

						EaseMobHelper.getInstance().reset();

						SignService.getInstance().SignOut( mHandler, MSG_LOGOUT_CALLBACK);
						LoginUser user = SamService.getInstance().get_current_user();
						user.seteasemob_status(LoginUser.INACTIVE);
						SamService.getInstance().getDao().updateLoginUserEaseStatus(user.getphonenumber(),LoginUser.INACTIVE);	
					} 
				}
			});
			
		}
        
		@Override
		public void onConnected() {
			getActivity().runOnUiThread(new Runnable() {
				public void run() {
					
				}
			});
		}
	};

	public Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if(getActivity() == null){
				SamLog.e(TAG,"MainActivity is killed, drop msg...");
				return;
			}

			switch(msg.what){
			case MSG_LOGOUT_CALLBACK:
				if(msg.arg1 == SignService.R_SIGN_OUT_OK){
					if(mDialog!=null){
    						mDialog.dismissPrgoressDiglog();
    					}
					((MainActivity)getActivity()).exitActivity();
					launchSignInActivity();
				}else if(msg.arg1 == SignService.R_SIGN_OUT_FAILED){
					if(mDialog!=null){
    						mDialog.dismissPrgoressDiglog();
    					}
					((MainActivity)getActivity()).exitActivity();
					launchSignInActivity();
				}
				break;
	
			case MSG_UPGRADE_CALLBACK:
				if(mDialog!=null){
    					mDialog.dismissPrgoressDiglog();
    				}
				
				if(msg.arg1 == SamService.R_UPGRADE_OK){
					mUpgrade_spec.setText(getString(R.string.sam_gbdy));
					launchDialogActivity(getString(R.string.upgrade_succeed_title),getString(R.string.upgrade_succeed_statement));
				}else if(msg.arg1 == SamService.R_UPGRADE_FAILED){
					launchDialogActivity(getString(R.string.upgrade_failed_title),getString(R.string.upgrade_failed_statement));
				}else if (msg.arg1 == SamService.R_UPGRADE_ERROR){
					launchDialogActivity(getString(R.string.upgrade_failed_title),getString(R.string.upgrade_failed_statement));
				}
				break;
			}
		}
	};
	
}
