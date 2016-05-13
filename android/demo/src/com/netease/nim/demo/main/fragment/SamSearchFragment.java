package com.netease.nim.demo.main.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.samchat.activity.DialogActivity;
import com.android.samchat.adapter.SearchListAdapter;
import com.android.samchat.dialog.SamProcessDialog;
import com.android.samchat.swipe.AutoNormalSwipeRefreshLayout;
import com.android.samservice.HotTopicResult;
import com.android.samservice.SMCallBack;
import com.android.samservice.SamLog;
import com.android.samservice.SamService;
import com.android.samservice.info.LoginUser;
import com.netease.nim.demo.DemoCache;
import com.netease.nim.demo.R;
import com.netease.nim.demo.config.preference.Preferences;
import com.netease.nim.demo.login.LoginActivity;
import com.netease.nim.demo.login.LogoutHelper;
import com.netease.nim.demo.main.activity.MultiportActivity;
import com.netease.nim.demo.main.model.MainTab;
import com.netease.nim.demo.main.reminder.ReminderManager;
import com.netease.nim.demo.session.SessionHelper;
import com.netease.nim.demo.session.extension.GuessAttachment;
import com.netease.nim.demo.session.extension.RTSAttachment;
import com.netease.nim.demo.session.extension.SnapChatAttachment;
import com.netease.nim.demo.session.extension.StickerAttachment;
import com.netease.nim.uikit.NimConstants;
import com.netease.nim.uikit.common.activity.TActionBarActivity;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.netease.nim.uikit.recent.RecentContactsCallback;
import com.netease.nim.uikit.sam.SearchFragment;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.StatusCode;
import com.netease.nimlib.sdk.auth.AuthServiceObserver;
import com.netease.nimlib.sdk.auth.ClientType;
import com.netease.nimlib.sdk.auth.OnlineClient;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.attachment.MsgAttachment;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.netease.nimlib.sdk.msg.model.RecentContact;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SamSearchFragment extends MainTabFragment {
	private static final String TAG="SamSearchFragment";
    public static final int MSG_SEND_QUESTION_CALLBACK = 1;

    public static final int HOT_TOPIC_MAX=20;

    private View notifyBar;

    private TextView notifyBarText;

    // 同时在线的其他端的信息
    private List<OnlineClient> onlineClients;

    private View multiportBar;

    private SearchFragment fragment;

    private Context mContext;

    private ListView mTopSearchList;
    private LinearLayout mHot_topic_layout;
    private SearchListAdapter mAdpater;
    private EditText mSearch;
    private ImageView mClear;
    private AutoNormalSwipeRefreshLayout mSwipe_layout;
    private LinearLayout mSearch_fragment;

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

    private SamProcessDialog mDialog;
    private boolean isSendingQuestion=false;

    public SamSearchFragment() {
         this.setContainerId(MainTab.SAM_SEARCH.fragmentId);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        onCurrent();
    }

    @Override
    public void onDestroy() {
        registerObservers(false);
        super.onDestroy();
    }

    @Override
    protected void onInit() {
        mContext = DemoCache.getContext();
        mDialog = new SamProcessDialog(getActivity());
        findViews();
        registerObservers(true);

        addSearchFragment();
    }

    private void registerObservers(boolean register) {
        NIMClient.getService(AuthServiceObserver.class).observeOtherClients(clientsObserver, register);
        NIMClient.getService(AuthServiceObserver.class).observeOnlineStatus(userStatusObserver, register);
    }

    private void setAnswerConversationExisted(String username,int existed){
        SamService.getInstance().getDao().updateLoginUserConversationExisted(username,existed);
    }

    private void launchDialogActivity(String title,String msg){
		Intent newIntent = new Intent(getActivity(),DialogActivity.class);
		int intentFlags = Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP;
		newIntent.setFlags(intentFlags);
		newIntent.putExtra("title", title);
		newIntent.putExtra("message", msg);
		startActivity(newIntent);
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
					if(mDialog!=null){
	 	   				mDialog.dismissPrgoressDiglog();
		    			}
				
					mHot_topic_layout.setVisibility(View.GONE);
					mSearch_fragment.setVisibility(View.VISIBLE);
					LoginUser cuser = SamService.getInstance().get_current_user();
					setAnswerConversationExisted(cuser.getusername(), LoginUser.EXISTED);
					cuser.setconversation_existed(LoginUser.EXISTED);
					isSendingQuestion = false;
		    		}else if(msg.arg1 == SamService.R_SEND_QUESTION_ERROR){
					if(mDialog!=null){
	  	  				mDialog.dismissPrgoressDiglog();
	 	   			}
					isSendingQuestion = false;
					if(SamService.getInstance().get_current_user().getconversation_existed()!=LoginUser.EXISTED){
						mSwipe_layout.setEnabled(true);
						mHot_topic_layout.setVisibility(View.VISIBLE);
						mSearch_fragment.setVisibility(View.GONE);
					}

					launchDialogActivity(getString(R.string.question_publish_failed),getString(R.string.question_action_error_statement));
	 	   		}else if(msg.arg1 == SamService.R_SEND_QUESTION_FAILED){
					/*send question failed due to server error*/
					if(mDialog!=null){
	    					mDialog.dismissPrgoressDiglog();
	    				}
					isSendingQuestion = false;
					if(SamService.getInstance().get_current_user().getconversation_existed()!=LoginUser.EXISTED){
						mSwipe_layout.setEnabled(true);
						mHot_topic_layout.setVisibility(View.VISIBLE);
						mSearch_fragment.setVisibility(View.GONE);
					}

					launchDialogActivity(getString(R.string.question_publish_failed),getString(R.string.question_action_server_error_statement));				
	    			}else{
					if(mDialog!=null){
	    					mDialog.dismissPrgoressDiglog();
	    				}
					isSendingQuestion = false;
					if(SamService.getInstance().get_current_user().getconversation_existed()!=LoginUser.EXISTED){
						mSwipe_layout.setEnabled(true);
						mHot_topic_layout.setVisibility(View.VISIBLE);
						mSearch_fragment.setVisibility(View.GONE);
					}

					launchDialogActivity("Fatal Error","Close samchat!");
	    			}
	    			break;		
			}
		}
	};


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
						
						Toast toast = Toast.makeText(getActivity(), R.string.update_hot_topic_failed, Toast.LENGTH_SHORT);
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
						
						Toast toast = Toast.makeText(getActivity(), R.string.update_hot_topic_failed, Toast.LENGTH_SHORT);
						toast.setGravity(Gravity.CENTER, 0, 0);
						toast.show();

						
					}
				});
			}

		});
	}

    private void findViews() {
        notifyBar = getView().findViewById(R.id.status_notify_bar);
        notifyBarText = (TextView) getView().findViewById(R.id.status_desc_label);
        notifyBar.setVisibility(View.GONE);

        multiportBar = getView().findViewById(R.id.multiport_notify_bar);
        multiportBar.setVisibility(View.GONE);
        multiportBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MultiportActivity.startActivity(getActivity(), onlineClients);
            }
        });

        mTopSearchList = (ListView)getView().findViewById(R.id.top_search_list);
        mHot_topic_layout = (LinearLayout)getView().findViewById(R.id.hot_topic_layout);	
        mAdpater = new SearchListAdapter(mContext);
        for(int i=0;i<testString.length;i++){
            hotTopicArray.add(testString[i]);
        }
        mAdpater.setHotTopicArray(hotTopicArray);	
        mTopSearchList.setAdapter(mAdpater);

        mSearch = (EditText) getView().findViewById(R.id.samservice_search_input);
        mClear = (ImageView) getView().findViewById(R.id.samservice_search_clear);
        mSwipe_layout = (AutoNormalSwipeRefreshLayout)getView().findViewById(R.id.swipe_layout);
        (mSwipe_layout).setColorScheme(R.color.holo_blue_bright, R.color.holo_green_light,
		                R.color.holo_orange_light, R.color.holo_red_light);
        mSearch_fragment = (LinearLayout) getView().findViewById(R.id.search_fragment);
		
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
               
           }); 

           if(SamService.getInstance().get_current_user().getconversation_existed()!=LoginUser.EXISTED){
                 mSwipe_layout.setEnabled(true);
                 mHot_topic_layout.setVisibility(View.VISIBLE);
                 mSearch_fragment.setVisibility(View.GONE);
           }else{
                 mSwipe_layout.setEnabled(false);
                 mHot_topic_layout.setVisibility(View.GONE);
                 mSearch_fragment.setVisibility(View.VISIBLE);
           }

    }

    /**
     * 用户状态变化
     */
    Observer<StatusCode> userStatusObserver = new Observer<StatusCode>() {

        @Override
        public void onEvent(StatusCode code) {
            if (code.wontAutoLogin()) {
                kickOut(code);
            } else {
                if (code == StatusCode.NET_BROKEN) {
                    notifyBar.setVisibility(View.VISIBLE);
                    notifyBarText.setText(R.string.net_broken);
                } else if (code == StatusCode.UNLOGIN) {
                    notifyBar.setVisibility(View.VISIBLE);
                    notifyBarText.setText(R.string.nim_status_unlogin);
                } else {
                    notifyBar.setVisibility(View.GONE);
                }
            }
        }
    };

    Observer<List<OnlineClient>> clientsObserver = new Observer<List<OnlineClient>>() {
        @Override
        public void onEvent(List<OnlineClient> onlineClients) {
            SamSearchFragment.this.onlineClients = onlineClients;
            if (onlineClients == null || onlineClients.size() == 0) {
                multiportBar.setVisibility(View.GONE);
            } else {
                multiportBar.setVisibility(View.VISIBLE);
                TextView status = (TextView) multiportBar.findViewById(R.id.multiport_desc_label);
                OnlineClient client = onlineClients.get(0);
                switch (client.getClientType()) {
                    case ClientType.Windows:
                        status.setText(getString(R.string.multiport_logging) + getString(R.string.computer_version));
                        break;
                    case ClientType.Web:
                        status.setText(getString(R.string.multiport_logging) + getString(R.string.web_version));
                        break;
                    case ClientType.iOS:
                    case ClientType.Android:
                        status.setText(getString(R.string.multiport_logging) + getString(R.string.mobile_version));
                        break;
                    default:
                        multiportBar.setVisibility(View.GONE);
                        break;
                }
            }
        }
    };

    private void kickOut(StatusCode code) {
        Preferences.saveUserToken("");

        if (code == StatusCode.PWD_ERROR) {
            LogUtil.e("Auth", "user password error");
            Toast.makeText(getActivity(), R.string.login_failed, Toast.LENGTH_SHORT).show();
        } else {
            LogUtil.i("Auth", "Kicked!");
        }
        onLogout();
    }

    // 注销
    private void onLogout() {
        // 清理缓存&注销监听&清除状态
        LogoutHelper.logout();

        LoginActivity.start(getActivity(), true);
        getActivity().finish();
    }

    // 将最近联系人列表fragment动态集成进来。 开发者也可以使用在xml中配置的方式静态集成。
    private void addSearchFragment() {
        fragment = new SearchFragment();
        fragment.setContainerId(R.id.search_fragment);

        final TActionBarActivity activity = (TActionBarActivity) getActivity();

        // 如果是activity从堆栈恢复，FM中已经存在恢复而来的fragment，此时会使用恢复来的，而new出来这个会被丢弃掉
        fragment = (SearchFragment) activity.addFragment(fragment);

        fragment.setCallback(new RecentContactsCallback() {
            @Override
            public void onRecentContactsLoaded() {
                // 最近联系人列表加载完毕
            }

            @Override
            public void onUnreadCountChange(int unreadCount) {
                ReminderManager.getInstance().updateSearchUnreadNum(unreadCount);
            }

            @Override
            public void onItemClick(RecentContact recent) {
                // 回调函数，以供打开会话窗口时传入定制化参数，或者做其他动作
                switch (recent.getSessionType()) {
                    case P2P:
                        SessionHelper.startP2PSession(getActivity(), recent.getContactId(),NimConstants.MSG_FROM_SEARCH);
                        break;
                    case Team:
                        SessionHelper.startTeamSession(getActivity(), recent.getContactId());
                        break;
                    default:
                        break;
                }
            }

            @Override
            public String getDigestOfAttachment(MsgAttachment attachment) {
                // 设置自定义消息的摘要消息，展示在最近联系人列表的消息缩略栏上
                // 当然，你也可以自定义一些内建消息的缩略语，例如图片，语音，音视频会话等，自定义的缩略语会被优先使用。
                if (attachment instanceof GuessAttachment) {
                    GuessAttachment guess = (GuessAttachment) attachment;
                    return guess.getValue().getDesc();
                } else if (attachment instanceof RTSAttachment) {
                    return "[白板]";
                } else if (attachment instanceof StickerAttachment) {
                    return "[贴图]";
                } else if (attachment instanceof SnapChatAttachment) {
                    return "[阅后即焚]";
                }

                return null;
            }

            @Override
            public String getDigestOfTipMsg(RecentContact recent) {
                String msgId = recent.getRecentMessageId();
                List<String> uuids = new ArrayList<>(1);
                uuids.add(msgId);
                List<IMMessage> msgs = NIMClient.getService(MsgService.class).queryMessageListByUuidBlock(uuids);
                if (msgs != null && !msgs.isEmpty()) {
                    IMMessage msg = msgs.get(0);
                    Map<String, Object> content = msg.getRemoteExtension();
                    if (content != null && !content.isEmpty()) {
                        return (String) content.get("content");
                    }
                }

                return null;
            }
        });
    }
}
