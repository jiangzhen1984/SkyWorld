package com.android.samchat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import me.nereo.multi_image_selector.MultiImageSelectorActivity;

import com.android.samchat.AutoSwipeRefreshLayout.OnLoadListener;
import com.android.samchat.SoftInputMonitorLinearLayout.InputWindowListener;
import com.android.samservice.ArticleInfo;
import com.android.samservice.SMCallBack;
import com.android.samservice.SamDBDao;
import com.android.samservice.SamLog;
import com.android.samservice.SamService;
import com.android.samservice.info.ContactUser;
import com.android.samservice.info.FGRecord;
import com.android.samservice.info.PictureRecord;
import com.android.samservice.info.ReceivedQuestion;

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
import android.widget.AbsListView;
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
import android.view.inputmethod.InputMethodManager;
import android.content.BroadcastReceiver;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.content.IntentFilter;
import android.util.Log;

/*FG refresh algorithm
API1. query with start timestamp + end time stamp + count
API2. query with start timestamp + count


1. pull down from head:
    refresh from now timestamp with 10 count
    if < 10 count, end of fg and delete local db which is before the last fg
    else show 10 count
2. pull up from bottom if it is in pull down refresh procedure, wait until pull down refresh finished
3. pull up from bottom:
    if end of fg, do nothing
    else if refresh from oldest shown record with 10 count



*/

public class SamFriendGroupActivity extends Activity {
	private final String TAG = "SamFriendGroup";
	static public final String ACTIVITY_NAME="com.android.samchat.SamFriendGroupActivity";
	static public final int MAXIUM_EACH_REFRESH = 1;
	private final int MSG_PULL_DOWN_QUERY_SUCCEED = 0;
	private final int MSG_PULL_DOWN_QUERY_FAILED = 1;
	private final int MSG_PULL_UP_QUERY_SUCCEED = 3;
	private final int MSG_PULL_UP_QUERY_FAILED = 4;
	

	private boolean isDestroyed=false;

	private SoftInputMonitorLinearLayout mMainLayout;
	private ImageView mBack;
	private ImageView mTakePic;
	private ListView mFriend_share_list;
	private AutoSwipeRefreshLayout swipeRefreshLayout;

	private FriendGroupAdapter mAdapter;
	private Context mContext;

	private LinearLayout mComment_layout;
	private EditText mComment_input;
	private LinearLayout mComment_action_layout;
	private TextView mComment_action;

	private boolean comment_action_enable;
	private String cur_comment;
	private int cur_comment_item;


	List<ArticleInfo> shownList; 

	Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			if(SamFriendGroupActivity.this == null ||SamFriendGroupActivity.this.isFinishing() ){
				return;
			}
			
			switch (msg.what) {
				case MSG_PULL_DOWN_QUERY_SUCCEED:
					swipeRefreshLayout.setRefreshing(false);
					refresh((List<ArticleInfo>)msg.obj);
				break;
				case MSG_PULL_DOWN_QUERY_FAILED:
					swipeRefreshLayout.setRefreshing(false);
					Toast.makeText(SamFriendGroupActivity.this, R.string.failed_to_friend_group_information, Toast.LENGTH_SHORT).show();
					swipeRefreshLayout.disable_pullup_load(false);
				break;
				case MSG_PULL_UP_QUERY_SUCCEED:
					swipeRefreshLayout.setLoading(false);
					refresh((List<ArticleInfo>)msg.obj);
				break;
				case MSG_PULL_UP_QUERY_FAILED:
					swipeRefreshLayout.setLoading(false);
					Toast.makeText(SamFriendGroupActivity.this, R.string.failed_to_friend_group_information, Toast.LENGTH_SHORT).show();
				break;

				default:
				break;
			}
		};
	};

	Handler inputhandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			if(SamFriendGroupActivity.this == null ||SamFriendGroupActivity.this.isFinishing() ){
				return;
			}
			
			switch (msg.what) {
				case 0:
					cur_comment_item = msg.arg1;
					mComment_layout.setVisibility(View.VISIBLE);
					mComment_input.requestFocus();
					InputMethodManager inputManager = (InputMethodManager) mComment_input.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);  
					inputManager.toggleSoftInput(0, InputMethodManager.SHOW_FORCED); 
				break;

				case 1:
					//SamLog.e(TAG,"indication:"+msg.arg1+" is clicked");
					launchPageScrollActivity(msg.arg1,(ArrayList<String>)msg.obj);
				break;

				case 2:
					mAdapter.notifyDataSetChanged();
				break;
				default:

				break;
			}
	    	};
	};

	private void launchPageScrollActivity(int page_indication,ArrayList<String> pathList){
		Intent intent = new Intent(this,ViewPagerScrollActivity.class);
		intent.putExtra(ViewPagerScrollActivity.EXTRA_PAGE_INDICATION, page_indication);
		intent.putStringArrayListExtra(ViewPagerScrollActivity.EXTRA_PAGE_LIST, pathList);
		startActivity(intent);
	} 


	private List<FGRecord>  validate(List<FGRecord> rdList){
		List<FGRecord> validList = new ArrayList<FGRecord>();
		SamDBDao dao = SamService.getInstance().getDao();
		for(int i=0;i<rdList.size();i++){
			FGRecord rd = rdList.get(i);
			//SamLog.e(TAG,"order by article:"+rd.getfg_id()+" timestamp:"+rd.gettimestamp());
			String publisher_phonenumber = rd.getpublisher_phonenumber();
			ContactUser user = dao.query_ContactUser_db(publisher_phonenumber);
			if(!publisher_phonenumber.equals(SamService.getInstance().get_current_user().getphonenumber()) && user == null ){
				SamLog.e(TAG,"FGRecord validate: no contactuser info");
				continue;
			}

			List<PictureRecord> picList = dao.query_PictureRecord_db(rd.getfg_id());
			boolean skip = false;
			for(int j=0;j<picList.size();j++){
				String shortImg = picList.get(j).getthumbnail_pic();
				if(shortImg == null || shortImg.equals("") || !dao.isThumbPicExistedInFS(shortImg)){
					SamLog.e(TAG,"FGRecord validate: ThumbPic no ExistedInFS");
					skip = true;
					break;
				}
			}

			if(skip){
				continue;
			}

			validList.add(rd);
			
		}

		return validList;
	}

	private void mergeNewList(List<ArticleInfo> newList){
		if(newList.size() == 0){
			return;
		}

		if(shownList.size() == 0){
			shownList = newList;
			return;
		}

		

		boolean needSort = false;

		for(int i=0;i<newList.size();i++){
			boolean needAdded=true;
			for(int j=0;j<shownList.size();j++){
				if(newList.get(i).article_id == shownList.get(j).article_id){
					needAdded = false;
					break;
				}
			}
			
			if(needAdded){
				shownList.add(newList.get(i));
				needSort = true;
			}
		}

		if(needSort){
			Collections.sort(shownList, new Comparator<ArticleInfo>() {
				@Override
				public int compare(ArticleInfo lhs, ArticleInfo rhs) {
					if(lhs.timestamp < rhs.timestamp){
						return 1;
					}else if(lhs.timestamp == rhs.timestamp){
						return 0;
					}else{
						return -1;
					}
				}
			});
		}

		//SamLog.e(TAG,"shownlist size:" + shownList.size());

	}

	private List<FGRecord> getFGRecordList(List<ArticleInfo> ainfoList){
		String owner_phonenumber = SamService.getInstance().get_current_user().getphonenumber();
		List<FGRecord> rdList = new ArrayList<FGRecord>();
		for(ArticleInfo ainfo :ainfoList){
			FGRecord rd = SamService.getInstance().getDao().query_FGRecord_db(ainfo.article_id, owner_phonenumber);
			if(rd!=null){
				rdList.add(rd);
			}
		}

		return rdList;
		
	}
	
	private void refresh(List<ArticleInfo> ainfoList){
		mergeNewList(ainfoList);

		List<FGRecord> rdList = getFGRecordList(shownList);

		//SamLog.e(TAG,"rdList size:"+rdList.size());

		mAdapter.setFGRecordList(rdList);
		mAdapter.setCount(rdList.size());
		mAdapter.notifyDataSetChanged();

		//ainfoList: query article info list this time
		if(ainfoList.size()<MAXIUM_EACH_REFRESH){
			//last article has been got by this refresh
			swipeRefreshLayout.disable_pullup_load(true);
		}else{
			swipeRefreshLayout.disable_pullup_load(false);
		}
		
	}

	private void updateCommentAction(){
		boolean clickable = comment_action_enable;
		if(clickable){
			mComment_action.setTextColor(Color.rgb(255, 255, 255));
			mComment_action.setBackgroundColor(Color.rgb(0xFF, 0x66, 0x00));
			mComment_action_layout.setBackgroundColor(Color.rgb(0xFF, 0x66, 0x00));
		}else{
			mComment_action.setTextColor(Color.rgb(0xFF, 0x66, 0x00));
			mComment_action.setBackgroundColor(Color.rgb(0xBF, 0xBF, 0xBF));
			mComment_action_layout.setBackgroundColor(Color.rgb(0xBF, 0xBF, 0xBF));
		}
		
		mComment_action_layout.setEnabled(clickable);
		mComment_action_layout.setClickable(clickable);
		
		
	}

	private TextWatcher CI_TextWatcher = new TextWatcher(){
		@Override 
		public void onTextChanged(CharSequence s, int start, int before, int count) { 
		        
		}
		
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,int after) { 
		       
		} 
		
		@Override
		public void afterTextChanged(Editable s) { 
		  	cur_comment = mComment_input.getText().toString();
		   	if(cur_comment!=null & !cur_comment.equals("") ){
		   		comment_action_enable = true;
		   	}else{
		   		comment_action_enable = false;
		   	}
		    	
		   	updateCommentAction();
		}     
	};

	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.activity_friend_group);

		shownList = new ArrayList<ArticleInfo>();

		mMainLayout = (SoftInputMonitorLinearLayout)findViewById(R.id.mainLayout);
		mMainLayout.setListener(new InputWindowListener() {
			@Override
			public void show() {
                
			}

			@Override
			public void hidden() {
               		mComment_layout.setVisibility(View.GONE);
			}
		});
		

		mComment_layout =  (LinearLayout)findViewById(R.id.comment_input_layout);
		mComment_input = (EditText)findViewById(R.id.comment_input);
		mComment_action_layout = (LinearLayout)findViewById(R.id.comment_action_layout);
		mComment_action = (TextView)findViewById(R.id.comment_action);
		
		mComment_input.addTextChangedListener(CI_TextWatcher);
		
		mComment_action.setTextColor(Color.rgb(0xFF, 0x66, 0x00));
		mComment_action_layout.setBackgroundColor(Color.rgb(0xBF, 0xBF, 0xBF));
		
		mComment_action.setEnabled(false);
		mComment_action.setClickable(false);
		mComment_action_layout.setEnabled(false);
		mComment_action_layout.setClickable(false);

		mComment_layout.setVisibility(View.GONE);

		mComment_action_layout.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				long article_id = mAdapter.getFGRecordList().get(cur_comment_item).getfg_id();
				//SamLog.e(TAG,"article_id:"+article_id+" item:"+cur_comment_item+" commentFG:"+cur_comment);
				commentFG(article_id, cur_comment);

				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE); 
				imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS); 

				mComment_input.setText("");
				comment_action_enable = false;
				updateCommentAction();

				mComment_layout.setVisibility(View.GONE);

	   	 	}

		});


		mContext = getBaseContext();

		mFriend_share_list = (ListView) findViewById(R.id.friend_share_list);
		mAdapter = new FriendGroupAdapter(mContext,mFriend_share_list,inputhandler);
		mFriend_share_list.setAdapter(mAdapter);
		mAdapter.InitHandlerThread(this);
		//mAdapter.setScrollListener();
		
		
		

		mBack =  (ImageView) findViewById(R.id.back);
		mBack.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});

		mTakePic = (ImageView) findViewById(R.id.take_picture);
		mTakePic.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				launchPictureSelectActivity();
			}
		});


		swipeRefreshLayout = (AutoSwipeRefreshLayout) findViewById(R.id.swipe_layout);
		swipeRefreshLayout.setColorSchemeResources(R.color.holo_blue_bright, R.color.holo_green_light,
		                R.color.holo_orange_light, R.color.holo_red_light);
		//下拉刷新
		swipeRefreshLayout.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				swipeRefreshLayout.disable_pullup_load(true);
				swipeRefreshLayout.setRefreshing(true);
				queryFG(0,MAXIUM_EACH_REFRESH);
			}
			
                    
		});



		// 上拉加载监听器
		swipeRefreshLayout.setOnLoadListener(new OnLoadListener() {
			@Override
			public void onLoad() {
				if(shownList.size() == 0){
					swipeRefreshLayout.setLoading(false);
				}else{
					ArticleInfo ainfo = shownList.get(shownList.size()-1);
					long start_timestamp = ainfo.timestamp-1;
					queryFG(start_timestamp,MAXIUM_EACH_REFRESH);
					
				}
                        
                    }
        	});

		swipeRefreshLayout.setOnScrollSubListener(mAdapter.getScrollListener());

		swipeRefreshLayout.autoRefresh();
	       
	}

	@Override
	public void onResume(){
		super.onResume();

	 }

	@Override
	public void onDestroy(){
		super.onDestroy();
		mAdapter.stopLoadDecoderThread();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == 1){//pictures to share are selected
			if(resultCode == Activity.RESULT_OK){
				if( data.hasExtra(MultiImageSelectorActivity.EXTRA_RESULT)) {
					ArrayList<String> resultList = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
					if(resultList!=null && resultList.size()>0){
						launchCommentsActivity(resultList);
					}
				}
			}
		}else if(requestCode == 2){
			if(resultCode == Activity.RESULT_OK){
				//query and refresh FG list
				swipeRefreshLayout.autoRefresh();
			}
		}

	}


	private void launchPictureSelectActivity(){
		Intent intent = new Intent(this, MultiImageSelectorActivity.class);
		// whether show camera
		intent.putExtra(MultiImageSelectorActivity.EXTRA_SHOW_CAMERA, true);
		// max select image amount
		intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_COUNT, 9);
		// select mode (MultiImageSelectorActivity.MODE_SINGLE OR MultiImageSelectorActivity.MODE_MULTI)
		intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_MODE, MultiImageSelectorActivity.MODE_MULTI);
		// default select images (support array list)
		//intent.putStringArrayListExtra(MultiImageSelectorActivity.EXTRA_DEFAULT_SELECTED_LIST, defaultDataArray);
		startActivityForResult(intent, 1);
	}

	private void launchCommentsActivity(ArrayList<String> resultList ){
		Intent intent = new Intent(this, FGCommentsActivity.class);
		intent.putStringArrayListExtra(FGCommentsActivity.PICTURES,resultList);
		startActivityForResult(intent,2);

	}

	private void queryFG(final long start_timestamp, int max){
		SamService.getInstance().queryFG(start_timestamp,max,new SMCallBack(){
			@Override
			public void onSuccess(Object obj) {
				if(SamFriendGroupActivity.this == null ||SamFriendGroupActivity.this.isFinishing() ){
					return;
				}

				List<ArticleInfo> ainfoList = (List<ArticleInfo>)obj;
				
				SamLog.e(TAG,"queryFG succedd");
				if(start_timestamp !=0){
					Message msg = handler.obtainMessage(MSG_PULL_UP_QUERY_SUCCEED,ainfoList);
					handler.sendMessage(msg);
				}else{
					Message msg = handler.obtainMessage(MSG_PULL_DOWN_QUERY_SUCCEED,ainfoList);
					handler.sendMessage(msg);
				}
				
					
			}

			

			@Override
			public void onFailed(int code) {
				if(SamFriendGroupActivity.this == null ||SamFriendGroupActivity.this.isFinishing() ){
					return;
				}
				
				runOnUiThread(new Runnable() {
					public void run() {
						SamLog.e(TAG,"queryFG failed");
						if(start_timestamp !=0){
							handler.sendEmptyMessage(MSG_PULL_UP_QUERY_FAILED);
						}else{
							handler.sendEmptyMessage(MSG_PULL_DOWN_QUERY_FAILED);
						}
					}
				});
			}

			@Override
			public void onError(int code) {
				if(SamFriendGroupActivity.this == null ||SamFriendGroupActivity.this.isFinishing() ){
					return;
				}
				
				runOnUiThread(new Runnable() {
					public void run() {
						SamLog.e(TAG,"queryFG error");
						if(start_timestamp !=0){
							handler.sendEmptyMessage(MSG_PULL_UP_QUERY_FAILED);
						}else{
							handler.sendEmptyMessage(MSG_PULL_DOWN_QUERY_FAILED);
						}
					}
				});
			}

		});
	}


	

	private void commentFG(long article_id,String comment){
		SamService.getInstance().commentFG(article_id,comment,new SMCallBack(){
			@Override
			public void onSuccess(Object obj) {
				if(SamFriendGroupActivity.this == null ||SamFriendGroupActivity.this.isFinishing() ){
					return;
				}
				
				runOnUiThread(new Runnable() {
					public void run() {
						SamLog.e(TAG,"commentFG succedd");
						swipeRefreshLayout.autoRefresh();
					}
				});
			}

			

			@Override
			public void onFailed(int code) {
				if(SamFriendGroupActivity.this == null ||SamFriendGroupActivity.this.isFinishing() ){
					return;
				}
				
				runOnUiThread(new Runnable() {
					public void run() {
						SamLog.e(TAG,"commentFG failed");
						handler.sendEmptyMessage(1);
					}
				});
			}

			@Override
			public void onError(int code) {
				if(SamFriendGroupActivity.this == null ||SamFriendGroupActivity.this.isFinishing() ){
					return;
				}
				
				runOnUiThread(new Runnable() {
					public void run() {
						SamLog.e(TAG,"commentFG error");
						handler.sendEmptyMessage(1);
					}
				});
			}

		});
	}
	


	
}

