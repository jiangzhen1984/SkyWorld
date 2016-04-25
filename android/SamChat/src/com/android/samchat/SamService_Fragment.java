/**
 * 
 */
package com.android.samchat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.android.samchat.R;
import com.android.samservice.*;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMConversation.EMConversationType;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.ui.EaseConversationListFragment.EaseConversationListItemClickListener;
import com.hyphenate.easeui.widget.EaseConversationList;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.util.Pair;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;

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
import android.widget.TextView;
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

	public static final int HOT_TOPIC_MAX=20;
	
	private ListView mTopSearchList;
	private Context mContext;
	private SearchListAdapter mAdpater;
	private View rootView;
	private EditText mSearch;
	private ImageView mClear;
	private AutoNormalSwipeRefreshLayout mSwipe_layout;
	private LinearLayout mHot_topic_layout;
	private EaseConversationList mConversationList;
	protected List<EMConversation> conversationList = new ArrayList<EMConversation>();
	private EaseConversationListItemClickListener listItemClickListener;

	private boolean isSendingQuestion=false;

	private SamProcessDialog mDialog;

	protected boolean hidden;


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



	

	protected List<EMConversation> loadConversationList(){
        // 获取所有会话，包括陌生人
		List< EMConversation > conversations = EMClient.getInstance().chatManager().getConversationsByType(EMConversationType.Chat);
	 Map<String, EMConversation> serviceConversations = new HashMap<String,EMConversation>();
	 synchronized (conversations) {
            for (EMConversation conversation : conversations) {
                String attr = conversation.getExtField();
		  if(attr!=null && attr.contains(Constants.CONVERSATION_ATTR_VIEW_SERVICE)){
			serviceConversations.put(conversation.getUserName(), conversation);
		  }
            }
        }

	List<Pair<Long, EMConversation>> sortList = new ArrayList<Pair<Long, EMConversation>>();
	for (EMConversation conversation : serviceConversations.values()) {
                if (conversation.getAllMessages().size() != 0) {
                        sortList.add(new Pair<Long, EMConversation>(conversation.getLastMessage().getMsgTime(), conversation));
                }
            }

        try {
            // Internal is TimSort algorithm, has bug
            sortConversationByLastChatTime(sortList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<EMConversation> list = new ArrayList<EMConversation>();
        for (Pair<Long, EMConversation> sortItem : sortList) {
            list.add(sortItem.second);
        }
        return list;
    }

    /**
     * 根据最后一条消息的时间排序
     * 
     * @param usernames
     */
    private void sortConversationByLastChatTime(List<Pair<Long, EMConversation>> conversationList) {
        Collections.sort(conversationList, new Comparator<Pair<Long, EMConversation>>() {
            @Override
            public int compare(final Pair<Long, EMConversation> con1, final Pair<Long, EMConversation> con2) {

                if (con1.first == con2.first) {
                    return 0;
                } else if (con2.first > con1.first) {
                    return 1;
                } else {
                    return -1;
                }
            }

        });
    }


	private boolean isBroadcastRegistered=false;
	private LocalBroadcastManager broadcastManager;
	private BroadcastReceiver broadcastReceiver; 

	private void registerBroadcastReceiver() {
		broadcastManager = LocalBroadcastManager.getInstance(getActivity());
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(Constants.ACTION_NEW_MSG_FROM_VENDOR);

		broadcastReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				conversationList.clear();
				conversationList.addAll(loadConversationList());
				mConversationList.refresh();
			}
		};
		
		broadcastManager.registerReceiver(broadcastReceiver, intentFilter);
	}
		

	
	private void unregisterBroadcastReceiver(){
	    broadcastManager.unregisterReceiver(broadcastReceiver);
	}

	private boolean isAnswerConversationExisted(){
		return false;
	}


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
			mContext = skyworld.appContext;//getActivity().getBaseContext();
			mConversationList = (EaseConversationList)rootView.findViewById(R.id.list);
			mTopSearchList = (ListView)rootView.findViewById(R.id.top_search_list);
			mHot_topic_layout = (LinearLayout)rootView.findViewById(R.id.hot_topic_layout);			
			mAdpater = new SearchListAdapter(mContext);
			for(int i=0;i<testString.length;i++){
				hotTopicArray.add(testString[i]);
			}
			mAdpater.setHotTopicArray(hotTopicArray);	
			mTopSearchList.setAdapter(mAdpater);
			mSearch = (EditText) rootView.findViewById(R.id.samservice_search_input);
			mClear = (ImageView) rootView.findViewById(R.id.samservice_search_clear);
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
			
			mSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {  
				public boolean onEditorAction(TextView v, int actionId,KeyEvent event) {    
					if (actionId==EditorInfo.IME_ACTION_SEARCH ||(event!=null&&event.getKeyCode()== KeyEvent.KEYCODE_ENTER)) { 
						closeInputMethod();

						if(!isSendingQuestion){
							String question = mSearch.getText().toString().trim();
							if(!question.equals("")){
								/*send questions to server*/
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
					if(mAdpater.getListType() == SearchListAdapter.LIST_TYPE_TOP_SEARCH){
					//click top search to view:
						if(!isSendingQuestion){
							List<String> topicList = mAdpater.getHotTopicArray();
							String question = topicList.get(arg2);
							mSearch.setText(question);	
							mSearch.requestFocus();
	
							InputMethodManager inputManager =(InputMethodManager)mContext.getSystemService(Context.INPUT_METHOD_SERVICE); 
							inputManager.showSoftInput(mSearch, 0);
						}
					}
				}   
               
       	 	}); 


			if(!isAnswerConversationExisted()){
				mSwipe_layout.setEnabled(true);
				mHot_topic_layout.setVisibility(View.VISIBLE);
				mConversationList.setVisibility(View.GONE);
			}else{
				mSwipe_layout.setEnabled(false);
				mHot_topic_layout.setVisibility(View.GONE);
				mConversationList.setVisibility(View.VISIBLE);
			}


			listItemClickListener = new EaseConversationListItemClickListener() {
				@Override
				public void onListItemClicked(EMConversation conversation) {
					Intent newIntent = new Intent(SamService_Fragment.this.getActivity(), ChatActivity.class);
					newIntent.putExtra(Constants.EXTRA_USER_ID, conversation.getUserName());
					newIntent.putExtra(Constants.EXTRA_CHAT_TYPE, Constants.CHATTYPE_SINGLE);
					newIntent.putExtra(Constants.CHAT_ACTIVITY_TYPE,Constants.CHAT_ACTIVITY_TYPE_VIEW_SERVICE);					
					startActivity(newIntent);
					
				}
			};
			
		}
		
		return rootView;
	}


	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		SamLog.i(TAG, "onCreated");
	}
	
	@Override
	public void onStart(){
		super.onStart();
		SamLog.i(TAG, "onStart");
	}
	
	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		this.hidden = hidden;
		if (!hidden) {
			mConversationList.refresh();
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		if (!hidden) {
			mConversationList.refresh();
		}
	}
	
	
	@Override
	public void onDetach(){
		super.onDetach();
		SamLog.i(TAG, "onDetach");
	}


	private void setUpView(){
		conversationList.clear();
		conversationList.addAll(loadConversationList());
		mConversationList.init(conversationList);
		mConversationList.setConversationListIn(EaseConstant.CONVERSATION_LIST_IN_SERIVCE);
		if(listItemClickListener != null){
			mConversationList.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					EMConversation conversation = mConversationList.getItem(position);
					listItemClickListener.onListItemClicked(conversation);
				}
			});
		}
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState){
		super.onActivityCreated(savedInstanceState);
		mDialog = new SamProcessDialog(getActivity());
		SamLog.i(TAG, "onActivityCreated");

		setUpView();
		
		if(!isBroadcastRegistered){
			registerBroadcastReceiver();
			isBroadcastRegistered=true;
		}

	}
	
	@Override
	public void onDestroyView(){
		super.onDestroyView();
		if(rootView != null){
			SamLog.i(TAG, "onDestroyView");
			((ViewGroup)(rootView.getParent())).removeView(rootView);
		}

		if(isBroadcastRegistered){
			unregisterBroadcastReceiver();
			isBroadcastRegistered=false;
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

	public Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
	    		if(getActivity() == null){
	    			SamLog.e(TAG,"MainActivity is killed, drop msg...");
	    			return;
	  	  	}
	    	
	  	  	switch(msg.what){
	 	   	case MSG_SEND_QUESTION_CALLBACK:
	 	   		if(msg.arg1 == SamService.R_SEND_QUESTION_OK){
	 	   			SamLog.i(TAG,"question_id = " + ((SamCoreObj)msg.obj).refCBObj.qinfo.question_id);
					if(mDialog!=null){
	 	   				mDialog.dismissPrgoressDiglog();
		    			}
				
					mHot_topic_layout.setVisibility(View.GONE);
					mConversationList.setVisibility(View.VISIBLE);
					isSendingQuestion = false;
		    		}else if(msg.arg1 == SamService.R_SEND_QUESTION_ERROR){
	  	  			SamLog.i(TAG,"question send error ...");
					if(mDialog!=null){
	  	  				mDialog.dismissPrgoressDiglog();
	 	   			}
					isSendingQuestion = false;
					if(!isAnswerConversationExisted()){
						mSwipe_layout.setEnabled(true);
						mHot_topic_layout.setVisibility(View.VISIBLE);
						mConversationList.setVisibility(View.GONE);
					}

					launchDialogActivity(getString(R.string.question_publish_failed),getString(R.string.question_action_error_statement));
	 	   		}else if(msg.arg1 == SamService.R_SEND_QUESTION_FAILED){
	 	   			SamLog.i(TAG,"question send failed ...");
					/*send question failed due to server error*/
					if(mDialog!=null){
	    					mDialog.dismissPrgoressDiglog();
	    				}
					isSendingQuestion = false;
					if(!isAnswerConversationExisted()){
						mSwipe_layout.setEnabled(true);
						mHot_topic_layout.setVisibility(View.VISIBLE);
						mConversationList.setVisibility(View.GONE);
					}

					launchDialogActivity(getString(R.string.question_publish_failed),getString(R.string.question_action_server_error_statement));				
	    			}else{
	    				SamLog.i(TAG,"impossible here ...");
					if(mDialog!=null){
	    					mDialog.dismissPrgoressDiglog();
	    				}
					isSendingQuestion = false;
					if(!isAnswerConversationExisted()){
						mSwipe_layout.setEnabled(true);
						mHot_topic_layout.setVisibility(View.VISIBLE);
						mConversationList.setVisibility(View.GONE);
					}

					launchDialogActivity("Fatal Error","Close samchat!");
	    			}
	    			break;		
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

