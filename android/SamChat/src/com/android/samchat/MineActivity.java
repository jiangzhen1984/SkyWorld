package com.android.samchat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.android.samchat.easemobdemo.EaseMobHelper;
import com.android.samservice.Constants;
import com.android.samservice.SMCallBack;
import com.android.samservice.SamLog;
import com.android.samservice.SamService;
import com.android.samservice.info.AvatarRecord;
import com.android.samservice.info.LoginUser;
import com.easemob.easeui.utils.EaseUserUtils;
import com.zijunlin.Zxing.Demo.CreateQRImageTest;

import me.nereo.multi_image_selector.MultiImageSelectorActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


public class MineActivity extends Activity {
	private final String TAG = "MineActivity";

	public static final int CONFIRM_ID_AVATAR_SELECTED=200;
	public static final int CONFIRM_ID_CROP_FINISHED=201;

	public static final int CONFIRM_ID_SEX_SELECTED=202;
	public static final int CONFIRM_ID_ADDRESS_UPDATE=203;
	public static final int CONFIRM_ID_HOBBY_UPDATE=204;

	private LinearLayout mBack_layout;
	private ImageView mWall_photo;
	private ImageView mAvatar;
	private LinearLayout mTwo_dimension_layout;
	private RelativeLayout mAddress_layout;
	private LinearLayout mSex_layout;
	private RelativeLayout mHobby_layout;

	private ImageView mTwo_dimension_pic;

	private TextView mUsername;
	private TextView mSex;
	private TextView mAddress;
	private TextView mHobby;

	private Context mContext;

	private Uri cropImageUri;
	private SamProcessDialog mDialog;

	private int sex = SexSelectActivity.SEX_NONE;
	private String address=null;
	private String hobby=null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mine);

		mContext = getBaseContext();
		mDialog = new SamProcessDialog();

		mBack_layout = (LinearLayout)findViewById(R.id.back_layout);
		mWall_photo =  (ImageView)findViewById(R.id.wall_photo);
		mAvatar =  (ImageView)findViewById(R.id.avatar);
		mTwo_dimension_layout = (LinearLayout)findViewById(R.id.two_dimension_layout);
		mAddress_layout = (RelativeLayout)findViewById(R.id.address_layout);
		mSex_layout = (LinearLayout)findViewById(R.id.sex_layout);
		mHobby_layout =  (RelativeLayout)findViewById(R.id.hobby_layout);

		mUsername = (TextView)findViewById(R.id.username);
		mSex =  (TextView)findViewById(R.id.sex);
		mAddress = (TextView)findViewById(R.id.address);
		mHobby = (TextView)findViewById(R.id.hobby);

		mTwo_dimension_pic = (ImageView)findViewById(R.id.two_dimension_pic);

		CreateQRImageTest qrI = new CreateQRImageTest(mTwo_dimension_pic,300,300);

		LoginUser cuser = SamService.getInstance().get_current_user();

		String username = cuser.getusername();
		qrI.createQRImage(username);

		
		AvatarRecord rd = SamService.getInstance().getDao().query_AvatarRecord_db_by_username(username);
		if(rd!=null && rd.getavatarname()!=null){
			Bitmap bp = null;
			bp = EaseUserUtils.decodeFile(SamService.sam_cache_path+SamService.AVATAR_FOLDER+"/"+rd.getavatarname(),60,60);

			if(bp!=null){
				mAvatar.setImageBitmap(bp);
			}
		}

		mUsername.setText(username);

		mBack_layout.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});

		mWall_photo.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				
			}
		});

		mAvatar.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				launchAvatarActivity();
			}
		});

		mTwo_dimension_layout.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				
			}
		});

		mAddress_layout.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				launchAddressActivity();
			}
		});

		mSex_layout.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				launchSexActivity();
			}
		});

		mHobby_layout.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				launchHobbyActivity();
			}
		});


		

	}


	@Override
	public void onBackPressed() {
		finish();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		 if(requestCode == CONFIRM_ID_AVATAR_SELECTED){
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
							50, 50))!=null){
						if (bytes.length != 0) {
							bp =  BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
						}

						mAvatar.setImageBitmap(bp);
						uploadAvatar(SamService.sam_cache_path + SamService.AVATAR_FOLDER 
									+ "/origin_" + SamService.getInstance().get_current_user().geteasemob_username());
					}
				}
				
			}
		}else if(requestCode == CONFIRM_ID_SEX_SELECTED){
			if(resultCode ==  Activity.RESULT_OK){
				sex = data.getIntExtra(SexSelectActivity.SELECT_RESULE,SexSelectActivity.SEX_NONE);
				if(sex == SexSelectActivity.SEX_MALE){
					mSex.setText(getString(R.string.male));
				}else if(sex == SexSelectActivity.SEX_FEMALE){
					mSex.setText(getString(R.string.female));
				}else{
					mSex.setText(getString(R.string.nofill));
				}
			}	
		}else if(requestCode == CONFIRM_ID_ADDRESS_UPDATE){
			if(resultCode ==  Activity.RESULT_OK){
				address = data.getStringExtra(BasicInfoUpdateActivity.NEW_INFO);
				if(address!=null){
					mAddress.setText(address);
				}
				
			}
		}else if(requestCode == CONFIRM_ID_HOBBY_UPDATE){
			if(resultCode ==  Activity.RESULT_OK){
				hobby = data.getStringExtra(BasicInfoUpdateActivity.NEW_INFO);
				if(hobby!=null){
					mHobby.setText(hobby);
				}
				
			}
		}
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

				EaseMobHelper.getInstance().sendAvatarUpdateBroadcast();

				
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

	private void launchAvatarActivity(){
		Intent intent = new Intent(this, MultiImageSelectorActivity.class);
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


	private void launchSexActivity(){
		Intent newIntent = new Intent(this,SexSelectActivity.class);
		int intentFlags = Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP;
		newIntent.setFlags(intentFlags);
		newIntent.putExtra(Constants.SEX_SELECT,sex);

		startActivityForResult(newIntent, CONFIRM_ID_SEX_SELECTED);
	}

	private void launchAddressActivity(){
		Intent newIntent = new Intent(this,BasicInfoUpdateActivity.class);
		int intentFlags = Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP;
		newIntent.setFlags(intentFlags);
		newIntent.putExtra(Constants.BASIC_INFO_TYPE,BasicInfoUpdateActivity.BASIC_INFO_ADDRESS);
		if(address!=null){
			newIntent.putExtra(Constants.BASIC_INFO_DEFAULT_VALUE,address);
		}
		
		startActivityForResult(newIntent, CONFIRM_ID_ADDRESS_UPDATE);
	}

	private void launchHobbyActivity(){
		Intent newIntent = new Intent(this,BasicInfoUpdateActivity.class);
		int intentFlags = Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP;
		newIntent.setFlags(intentFlags);
		newIntent.putExtra(Constants.BASIC_INFO_TYPE,BasicInfoUpdateActivity.BASIC_INFO_HOBBY);
		if(hobby!=null){
			newIntent.putExtra(Constants.BASIC_INFO_DEFAULT_VALUE,hobby);
		}
		startActivityForResult(newIntent, CONFIRM_ID_HOBBY_UPDATE);
	}

}
