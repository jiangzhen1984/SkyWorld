/**
 * 
 */
package com.android.samchat;

import java.util.ArrayList;
import java.util.List;

import com.android.samchat.R;
import com.android.samservice.*;
import com.android.samservice.info.ReceivedAnswer;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.text.InputType;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;


public class SamService_Fragment extends Fragment {

	static final String TAG = "SamService_Fragment";

	public static final int MSG_SEND_QUESTION_CALLBACK = 1;
	public static final int MSG_ANSWER_BACK = 2;	
	public static final int MSG_AUTOLOGIN_CALLBACK=3;
	public static final int MSG_CANCEL_QUESTION_CALLBACK = 4;

	
	public static final String CANCEL_QUESTION_CONFIRM = "com.android.samchat.cancel_question";

	/*SamServiceFragement confirm*/
	public static final int REQUST_CODE_CONFIRM_CANCEL_QUESTION = 11;
	public static final int REQUST_CODE_BACK_FROM_SAMANSWERDETAILACTIVITY = 12;

	public static final int HOT_TOPIC_MAX=20;
	
	private ListView mTopSearchList;
	private Context mContext;
	private SearchListAdapter mAdpater;
	private View rootView;
	private EditText mSearch;
	private ImageView mClear;
	private TextView mCancel_search;
	private TextView mHot_topic;
	private AutoNormalSwipeRefreshLayout mSwipe_layout;

	private EditText mSamservice_search_input_show;
	private LinearLayout mSamservice_search_layout_show;
	private RelativeLayout mBanner_layout;

	private String current_question;
	private SamServiceType current_type;

	private boolean isSendingQuestion=false;
	private boolean isCancelingQuestion=false;

	private SamProcessDialog mDialog;


	private List<String> hotTopicArray = new ArrayList<String>();
	private String testString[]={
		"硅谷比较好的学区在哪里",
		"女儿去美国 读高中,怎么样才能找到合适的寄宿家庭",
		"如何得到医院的医疗补助",
		"美国的一栋房产,每年要多少花费",
		"想去cosco,没有会员卡怎么办",
		"硅谷比较好的学区在哪里",
		"女儿去美国 读高中,怎么样才能找到合适的寄宿家庭",
		"如何得到医院的医疗补助",
		"美国的一栋房产,每年要多少花费",
		"想去cosco,没有会员卡怎么办",
		"硅谷比较好的学区在哪里",
		"女儿去美国 读高中,怎么样才能找到合适的寄宿家庭",
		"如何得到医院的医疗补助",
		"美国的一栋房产,每年要多少花费",
		"想去cosco,没有会员卡怎么办",
		"硅谷比较好的学区在哪里",
		"女儿去美国 读高中,怎么样才能找到合适的寄宿家庭",
		"如何得到医院的医疗补助",
		"美国的一栋房产,每年要多少花费",
		"想去cosco,没有会员卡怎么办",
	};

	private List<String> dyanamicHotTopicArray = new ArrayList<String>();
	private long query_time=0;

	private List<String> showHotTopicArray = new ArrayList<String>();


	private void queryHotTopic(){
		SamService.getInstance().queryHotTopic(dyanamicHotTopicArray.size(), query_time, new SMCallBack(){
			@Override
			public void onSuccess(final Object obj) {
				getActivity().runOnUiThread(new Runnable() {
					public void run() {
						HotTopicResult result = (HotTopicResult)obj;
						dyanamicHotTopicArray.addAll(result.topics);
						query_time = result.query_time;	

						mSwipe_layout.setRefreshing(false);
						showHotTopicArray.clear();
						if(dyanamicHotTopicArray.size()<=HOT_TOPIC_MAX){
							showHotTopicArray.addAll(dyanamicHotTopicArray);
							for(int i=0;i<HOT_TOPIC_MAX-dyanamicHotTopicArray.size();i++){
								showHotTopicArray.add(hotTopicArray.get(i));
							}
						}else{
							for(int i=dyanamicHotTopicArray.size()-HOT_TOPIC_MAX;i<dyanamicHotTopicArray.size();i++){
								showHotTopicArray.add(dyanamicHotTopicArray.get(i));
							}
						}

						mAdpater.setHotTopicArray(showHotTopicArray);
						mAdpater.notifyDataSetChanged();
						
					}
				});
			}

			@Override
			public void onFailed(int code) {
				getActivity().runOnUiThread(new Runnable() {
					public void run() {
						mSwipe_layout.setRefreshing(false);
						
						Toast toast = Toast.makeText(getActivity(), R.string.cant_find_pictures, Toast.LENGTH_SHORT);
						toast.setGravity(Gravity.CENTER, 0, 0);
						toast.show();

						
					}
				});
			}

			@Override
			public void onError(int code) {
				getActivity().runOnUiThread(new Runnable() {
					public void run() {
						mSwipe_layout.setRefreshing(false);
						
						Toast toast = Toast.makeText(getActivity(), R.string.cant_find_pictures, Toast.LENGTH_SHORT);
						toast.setGravity(Gravity.CENTER, 0, 0);
						toast.show();

						
					}
				});
			}

		});
	}
	

	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if(rootView == null){
			
			SamLog.e(TAG, "onCreateView");
			
			rootView=inflater.inflate(R.layout.fragment_samservice, container,false);
			mTopSearchList = (ListView)rootView.findViewById(R.id.top_search_list);
			mContext = getActivity().getBaseContext();
			mAdpater = new SearchListAdapter(mContext);
			for(int i=0;i<testString.length;i++){
				hotTopicArray.add(testString[i]);
			}
			mAdpater.setHotTopicArray(hotTopicArray);	
			mTopSearchList.setAdapter(mAdpater);
			mSearch = (EditText) rootView.findViewById(R.id.samservice_search_input);
			mClear = (ImageView) rootView.findViewById(R.id.samservice_search_clear);
			mHot_topic = (TextView) rootView.findViewById(R.id.hot_topic);
			mSwipe_layout = (AutoNormalSwipeRefreshLayout)rootView.findViewById(R.id.swipe_layout);
			mSwipe_layout.setColorSchemeResources(R.color.holo_blue_bright, R.color.holo_green_light,
		                R.color.holo_orange_light, R.color.holo_red_light);
			//下拉刷新
			mSwipe_layout.setOnRefreshListener(new OnRefreshListener() {

				@Override
				public void onRefresh() {
					queryHotTopic();					
				}
			});

			mBanner_layout = (RelativeLayout)rootView.findViewById(R.id.banner_layout);

			mSamservice_search_input_show = (EditText)rootView.findViewById(R.id.samservice_search_input_show);
			mSamservice_search_layout_show = (LinearLayout)rootView.findViewById(R.id.samservice_search_layout_show);

			mSamservice_search_input_show.setEnabled(false);
			mSamservice_search_input_show.setFocusable(false);
			mSamservice_search_layout_show.setVisibility(View.GONE);

					

			mCancel_search = (TextView)  rootView.findViewById(R.id.cancel_search);
			mCancel_search.setVisibility(View.GONE);
			mCancel_search.setOnClickListener(new OnClickListener(){
		    		@Override
		    		public void onClick(View arg0) {
		    			if(!isCancelingQuestion){
		    				launchDialogActivityNeedConfirm(getString(R.string.reminder),getString(R.string.cancel_reminder));
						isCancelingQuestion = true;
					}
		    		}
		    	
			});

			
			mSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {  
				public boolean onEditorAction(TextView v, int actionId,KeyEvent event) {    
					if (actionId==EditorInfo.IME_ACTION_SEARCH ||(event!=null&&event.getKeyCode()== KeyEvent.KEYCODE_ENTER)) { 
						closeInputMethod();

						if(!isSendingQuestion){
							String question = mSearch.getText().toString().trim();
							if(!question.equals("")){
								/*send questions to server*/
								current_question = question;
								if(mDialog!=null){
    									mDialog.launchProcessDialog(getActivity(),getString(R.string.question_publish_now));
    								}
								isSendingQuestion = true;
								mSwipe_layout.setEnabled(false);
								publish_question(question);
							}
						}
					
						return true;  
					}    
					return false;    
				}    
			}); 
			
			mClear.setOnClickListener(new OnClickListener(){
		    		@Override
		    		public void onClick(View arg0) {
		    			mSearch.setText("");
		    		}
		    	
			});

			mTopSearchList.setOnItemClickListener(new OnItemClickListener(){   
			@Override   
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {   
				/*arg2 is postion*/
				if(mAdpater.getListType() == SearchListAdapter.LIST_TYPE_ANSWER 
					&& arg2+1!=mAdpater.getCount()){
				//click answer to view:
					if(current_question == null){
						return;
					}
					
					ReceivedAnswer answer = mAdpater.getAnswerInfo(arg2);
					mAdpater.setRead(arg2);
					Intent intent = new Intent();
					intent.setClass(getActivity(),SamAnswerDetailActivity.class);
					Bundle bundle = new Bundle();
					bundle.putSerializable("ReceivedAnswer",answer);
					bundle.putSerializable("CurrentQuestion",current_question);
					intent.putExtras(bundle);
					startActivityForResult(intent,REQUST_CODE_BACK_FROM_SAMANSWERDETAILACTIVITY);
									
				}else if(mAdpater.getListType() == SearchListAdapter.LIST_TYPE_TOP_SEARCH){
				//click top search to view:
					List<String> topicList = mAdpater.getHotTopicArray();
					String question = topicList.get(arg2);
					mSearch.setText(question);	
					mSearch.requestFocus();

					InputMethodManager inputManager =(InputMethodManager)mContext.getSystemService(Context.INPUT_METHOD_SERVICE); 
					inputManager.showSoftInput(mSearch, 0);
				}
				
			}   
               
       	 }); 

			
			
		}
		
		return rootView;
	}


	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		SamLog.i(TAG, "onCreated");
		current_type = SamServiceType.TOP_SEARCH; 

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
		mDialog = new SamProcessDialog(getActivity());
		SamLog.i(TAG, "onActivityCreated");

		//queryHotTopic();

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
	public void onPause(){
		super.onPause();
		SamLog.i(TAG, "onPause");
		if(mSearch!=null) 
			mSearch.clearFocus();
	}
	
	@Override
	public void onStop(){
		super.onStop();
		SamLog.i(TAG, "onStop");
	}


	private void cancel_question(){
		/*send cancel to server*/
		SamService.getInstance().cancel_question( mHandler, MSG_CANCEL_QUESTION_CALLBACK);

	}
	
	private void closeInputMethod() {
		InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
		boolean isOpen = imm.isActive();
		if (isOpen) {
			imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    		}
	}

	private void publish_question(String question){
		closeInputMethod(); 
	
		/*send question to server*/
		SamService.getInstance().send_question(question, mHandler, MSG_SEND_QUESTION_CALLBACK);
	}

	private void update_answers(ReceivedAnswer answer){
		mAdpater.addAnswerInfo(answer);
		//mAdpater.setCount(mAdpater.getCountOfAnswerInfo()+1);
		mAdpater.setListType_answer();
		mAdpater.notifyDataSetChanged();
	}

	private void back_to_topsearch(){
		mAdpater.clearAnswerInfo();
		mAdpater.setListType_topSearch();
		//mAdpater.setCount(15);	
		mAdpater.notifyDataSetChanged();
	}
	
	public Handler mHandler = new Handler() {
	    @Override
	    public void handleMessage(Message msg) {
	    	if(getActivity() == null){
	    		SamLog.e(TAG,"MainActivity is killed, drop msg...");
	    		return;
	    	}
	    	
	    	switch(msg.what){
	    	case MSG_SEND_QUESTION_CALLBACK:
	    		isTopActivity();
	    		isTopFragment();
	    		if(msg.arg1 == SamService.R_SEND_QUESTION_OK){
	    			SamLog.i(TAG,"question_id = " + ((SamCoreObj)msg.obj).refCBObj.qinfo.question_id);
				if(mDialog!=null){
	    				mDialog.dismissPrgoressDiglog();
	    			}
				mBanner_layout.setVisibility(View.GONE);
				mSamservice_search_input_show.setText(current_question);
				mSamservice_search_layout_show.setVisibility(View.VISIBLE);

				mCancel_search.setVisibility(View.VISIBLE);
				mHot_topic.setVisibility(View.GONE);
				current_type = SamServiceType.ANSWER;
				mSearch.setInputType(InputType.TYPE_NULL);
				mAdpater.setListType_answer();
				//mAdpater.setCount(1);
				mAdpater.notifyDataSetChanged();
				/*store samobj*/
				isSendingQuestion = false;
	    		}else if(msg.arg1 == SamService.R_SEND_QUESTION_ERROR){
	    			SamLog.i(TAG,"question send error ...");
				if(mDialog!=null){
	    				mDialog.dismissPrgoressDiglog();
	    			}
				current_question = null;
				isSendingQuestion = false;
				mSwipe_layout.setEnabled(true);

				launchDialogActivity(getString(R.string.question_publish_failed),getString(R.string.question_action_error_statement));
	    		}else if(msg.arg1 == SamService.R_SEND_QUESTION_FAILED){
	    			SamLog.i(TAG,"question send failed ...");
				/*send question failed due to server error*/
				if(mDialog!=null){
	    				mDialog.dismissPrgoressDiglog();
	    			}
				current_question = null;
				isSendingQuestion = false;
				mSwipe_layout.setEnabled(true);
				launchDialogActivity(getString(R.string.question_publish_failed),getString(R.string.question_action_server_error_statement));				
	    		}else{
	    			SamLog.i(TAG,"impossible here ...");
				if(mDialog!=null){
	    				mDialog.dismissPrgoressDiglog();
	    			}
				current_question = null;
				isSendingQuestion = false;
				mSwipe_layout.setEnabled(true);
				launchDialogActivity("Fatal Error","Close samchat!");
	    		}
	    		break;
	    	case MSG_ANSWER_BACK:
			ReceivedAnswer ra = (ReceivedAnswer)msg.obj;
			update_answers(ra);
			break;

		case MSG_CANCEL_QUESTION_CALLBACK:
		  	if(msg.arg1 == SamService.R_CANCEL_QUESTION_OK){
				SamLog.i(TAG,"cancel question succeed ...");
				if(mDialog!=null){
	    				mDialog.dismissPrgoressDiglog();
	    			}

				mBanner_layout.setVisibility(View.VISIBLE);
				mSamservice_search_input_show.setText("");
				mSamservice_search_layout_show.setVisibility(View.GONE);
				
				current_type = SamServiceType.TOP_SEARCH;
				current_question = null;
				mSearch.setInputType(InputType.TYPE_CLASS_TEXT);
				mSearch.setText("");
				mCancel_search.setVisibility(View.GONE);
				mHot_topic.setVisibility(View.VISIBLE);
				back_to_topsearch();

				isCancelingQuestion = false;
				mSwipe_layout.setEnabled(true);
				
			}else if(msg.arg1 == SamService.R_CANCEL_QUESTION_FAILED){
				SamLog.i(TAG,"cancel question failed ...");
				/*cancel question failed due to server error*/
				if(mDialog!=null){
	    				mDialog.dismissPrgoressDiglog();
	    			}
				launchDialogActivity(getString(R.string.question_cancel_failed),getString(R.string.question_action_server_error_statement));	
				isCancelingQuestion = false;
			}else if(msg.arg1 == SamService.R_CANCEL_QUESTION_ERROR){
				SamLog.i(TAG,"question send error ...");
				if(mDialog!=null){
	    				mDialog.dismissPrgoressDiglog();
	    			}
				launchDialogActivity(getString(R.string.question_cancel_failed),getString(R.string.question_action_error_statement));
				isCancelingQuestion = false;
			}else{
				SamLog.i(TAG,"impossible here ...");
				if(mDialog!=null){
	    				mDialog.dismissPrgoressDiglog();
	    			}
				launchDialogActivity("Fatal Error","Close samchat!");
				isCancelingQuestion = false;
			}
	      }
	    }
	};


	private void launchDialogActivity(String title,String msg){
		Intent newIntent = new Intent(getActivity(),DialogActivity.class);
		int intentFlags = Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP;
		newIntent.setFlags(intentFlags);
		newIntent.putExtra("title", title);
		newIntent.putExtra("message", msg);
		startActivity(newIntent);
	}

	private void launchDialogActivityNeedConfirm(String title,String msg){
		Intent newIntent = new Intent(CANCEL_QUESTION_CONFIRM);		
		//newIntent.setFlags(intentFlags);
		newIntent.putExtra("title", title);
		newIntent.putExtra("message", msg);
		startActivityForResult(newIntent, REQUST_CODE_CONFIRM_CANCEL_QUESTION);
	}


	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {  
		if(requestCode == REQUST_CODE_CONFIRM_CANCEL_QUESTION){
			SamLog.e(TAG,"result code:"+ resultCode);
			if(resultCode == 1){ //OK
				SamLog.e(TAG,"onActivityResult = 0");
				cancel_question_confirm_ok();
			}else{
				SamLog.e(TAG,"onActivityResult != 0");
				isCancelingQuestion = false;
				cancel_question_confirm_not_ok();
			}
    	   
		}else if(requestCode == REQUST_CODE_BACK_FROM_SAMANSWERDETAILACTIVITY){
			SamLog.e(TAG,"onActivityResult,requestCode:2");
			mAdpater.notifyDataSetChanged();
		}
	} 
	
	private void cancel_question_confirm_ok(){
		if(mDialog!=null){
    			mDialog.launchProcessDialog(getActivity(),getString(R.string.question_cancel_now));
    		}
		
		cancel_question();
		
		return;
	}

	private void cancel_question_confirm_not_ok(){
		return;
	}


	private boolean isTopActivity()  
	{  
		boolean isTop = false;  
		ActivityManager am = (ActivityManager)getActivity().getSystemService(Context.ACTIVITY_SERVICE);  
		ComponentName cn = am.getRunningTasks(1).get(0).topActivity;  
		SamLog.e(TAG, "isTopActivity = " + cn.getClassName());  
		if (cn.getClassName().contains(MainActivity.ACTIVITY_NAME))  
		{  
			 isTop = true;  
		}  
		SamLog.e(TAG, "isTopActivity = " + isTop);  
		return isTop;  
    }
	
	private boolean isTopFragment(){
		boolean isTop = false;
		if(((MainActivity)getActivity()).getCurrentTab() == 0){
			isTop = true;
		}
		SamLog.e(TAG, "isTopFragment = " + isTop);  
		return isTop;  
		
	}

}

