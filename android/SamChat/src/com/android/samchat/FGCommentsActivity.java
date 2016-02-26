package com.android.samchat;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import me.nereo.multi_image_selector.MultiImageSelectorActivity;

import com.android.samservice.SMCallBack;
import com.android.samservice.SamLog;
import com.android.samservice.SamService;
import com.android.samservice.info.ReceivedQuestion;

import android.app.Activity;
import android.content.Intent;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.content.BroadcastReceiver;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.content.IntentFilter;
import android.util.Log;


public class FGCommentsActivity extends Activity {
	private final String TAG = "FGCommentsActivity";
	public static final String PICTURES = "PictureLists";

	private ImageView mBack;
	private LinearLayout mSend_layout;
	private TextView mSend;
	private Context mContext;
	private EditText mComments;
	private ImageView mFirstPicture;

	private ArrayList<String> photoes;
	private String comments;

	private boolean comments_available;

	private SamProcessDialog mDialog;

	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.activity_fg_comments);

		mDialog = new SamProcessDialog();

		mContext = getBaseContext();

		mSend_layout = (LinearLayout) findViewById(R.id.send_layout);
		mSend_layout.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				uploadFG();
			}
		});
		
		mSend = (TextView) findViewById(R.id.send);

		mComments = (EditText) findViewById(R.id.comments);
		mComments.addTextChangedListener(CM_TextWatcher);

		mFirstPicture = (ImageView) findViewById(R.id.firstPicture);

		mBack =  (ImageView) findViewById(R.id.back);
		mBack.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				launchDialogActivityNeedConfirm(getString(R.string.reminder),getString(R.string.cancel_fg_reminder));
			}
		});

		initFromIntent(getIntent());

		if(photoes!=null && photoes.size()>0){
			Bitmap bp = decodeFile(photoes.get(0), 80, 80);
			if(bp!=null){
				mFirstPicture.setImageBitmap(bp);
			}
		}else{
			mFirstPicture.setVisibility(View.GONE);
		}
		
		updateSendButton();

		
	}

	@Override
	public void onResume(){
		super.onResume();

	 }

	@Override
	public void onDestroy(){
		super.onDestroy();
		
	}

	@Override
	public void onBackPressed() {
       	launchDialogActivityNeedConfirm(getString(R.string.reminder),getString(R.string.cancel_fg_reminder));
	}

	private void launchDialogActivityNeedConfirm(String title,String msg){
		Intent newIntent = new Intent(this, DialogActivity.class);
		newIntent.putExtra("title", title);
		newIntent.putExtra("message", msg);
		this.startActivityForResult(newIntent, 1);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == 1){
			if(resultCode == 1){
				setResult(RESULT_CANCELED);
				finish();
			}else{

			}
		}

	}

	private void initFromIntent(Intent intent) {
		if (intent != null) {
			photoes = intent.getStringArrayListExtra(PICTURES);
			if(photoes!=null && photoes.size()>0){
				SamLog.e(TAG,"send info in FG with photoes");
			}else{
				SamLog.e(TAG,"send info in FG without photoes");
			}
			
		} else {
			setResult(RESULT_CANCELED);
			finish();
		}
	}

	private void updateSendButton(){
		boolean clickable = false;
		if(photoes!=null && photoes.size()>0 || comments_available){
			clickable = true;
		}else{
			clickable = false;
		}

		
		if(clickable){
			mSend_layout.setBackgroundColor(Color.rgb(0, 0x5F, 0xBF));
			mSend.setBackgroundColor(Color.rgb(0, 0x5F, 0xBF));
			mSend.setTextColor(Color.rgb(0xFF, 0xFF, 0xFF));
			
		}else{
			mSend_layout.setBackgroundColor(Color.rgb(0xBF, 0xBF, 0xBF));
			mSend.setBackgroundColor(Color.rgb(0xBF, 0xBF, 0xBF));
			mSend.setTextColor(Color.rgb(0x99, 0x99, 0x99));
		}
		
		mSend_layout.setEnabled(clickable);
		mSend_layout.setClickable(clickable);

	}
	

	private TextWatcher CM_TextWatcher = new TextWatcher(){
		@Override 
		public void onTextChanged(CharSequence s, int start, int before, int count) { 
		        
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,int after) { 
		       
		} 

		@Override
		public void afterTextChanged(Editable s) { 
			comments = mComments.getText().toString();

			if(comments!=null & !comments.equals("")){
				comments_available = true;
			}else{
				comments_available = false;
			}
		    	
			updateSendButton();
		}     
	};


	private Bitmap decodeFile(String filename,int req_Height,int req_Width){
		File file = null;
		FileInputStream fos = null;
		
		try {
			file = new File(filename);

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
			return BitmapFactory.decodeFile(filename,o2);
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	
	}


	private void uploadFG(){
		if(mDialog!=null){
    			mDialog.launchProcessDialog(this,getString(R.string.process));
    		}

		SamService.getInstance().uploadFG(photoes,comments,new SMCallBack(){
			@Override
			public void onSuccess(Object obj) {
				runOnUiThread(new Runnable() {
					public void run() {
						if(mDialog!=null){
    							mDialog.dismissPrgoressDiglog();
    						}
				
						setResult(RESULT_OK);
						FGCommentsActivity.this.finish();
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
				
						setResult(RESULT_CANCELED);
						FGCommentsActivity.this.finish();
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
				
						setResult(RESULT_CANCELED);
						FGCommentsActivity.this.finish();
					}
				});
			}

		});

	}
	
}


