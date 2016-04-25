package com.android.samchat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.android.samchat.R;
import com.android.samchat.easemobdemo.EaseMobHelper;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.samservice.*;
import com.android.samservice.info.AvatarRecord;
import com.android.samservice.info.ContactUser;
import com.android.samservice.info.LoginUser;
import com.android.samservice.info.ReceivedQuestion;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMConversation.EMConversationType;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.ui.EaseConversationListFragment.EaseConversationListItemClickListener;
import com.hyphenate.easeui.widget.EaseConversationList;

public class SamVendor_Fragment extends Fragment{
	static final String TAG = "SamVendor_Fragment";

	public static final int MSG_QUESTION_RECEIVED=1;
	public static final int MSG_QUESTION_CANCEL=2;
 
	private View rootView; 
	private EaseConversationList mConversationList;
	protected List<EMConversation> conversationList = new ArrayList<EMConversation>();
	private EaseConversationListItemClickListener listItemClickListener;

	protected boolean hidden;


	public Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if(SamVendor_Fragment.this.getActivity() == null){
				return;
			}
		
			switch(msg.what){
			case MSG_QUESTION_RECEIVED:
				SamLog.e(TAG,"MSG_QUESTION_RECEIVED!");
				ReceivedQuestion rq = (ReceivedQuestion)msg.obj;
				long id = rq.getcontactuserid();
				ContactUser cuser = SamService.getInstance().getDao().query_ContactUser_db(id);
				LoginUser me = SamService.getInstance().get_current_user();
				String question = rq.getquestion();
				String from = cuser.geteasemob_username();
				String to = me.geteasemob_username();

				EMMessage qMsg =EMMessage.createReceiveMessage(EMMessage.Type.TXT);
				qMsg.setMsgTime(rq.getreceivedtime());
				qMsg.setFrom(from);
				qMsg.setTo(to);
				qMsg.addBody(new EMTextMessageBody(question));
				qMsg.setAttribute(Constants.CHAT_ACTIVITY_TYPE,Constants.CHAT_ACTIVITY_TYPE_VIEW_SERVICE);

				//qMsg.setUnread(true);

				EMConversation qConversation = EMClient.getInstance().chatManager().getConversation(from,EMConversationType.Chat,true);
				
				synchronized(qConversation){
					String attr = qConversation.getExtField();
		  			if(attr!=null && attr.contains(Constants.CONVERSATION_ATTR_VIEW_VENDOR)){
						qConversation.insertMessage(qMsg);
		  			}else if(attr == null){
						qConversation.setExtField(Constants.CONVERSATION_ATTR_VIEW_VENDOR);
						qConversation.insertMessage(qMsg);
					}else{
						qConversation.setExtField(attr+Constants.CONVERSATION_ATTR_VIEW_VENDOR);
						qConversation.insertMessage(qMsg);
					}
				}

				conversationList.clear();
				conversationList.addAll(loadConversationList());
				mConversationList.refresh();		

				EaseMobHelper.getInstance().sendNewQuestReceived(from);
				
				break;
			}
		}

	};

	protected List<EMConversation> loadConversationList(){
        // 获取所有会话，包括陌生人
	 List< EMConversation > conversations = EMClient.getInstance().chatManager().getConversationsByType(EMConversationType.Chat);
	 Map<String, EMConversation> vendorConversations = new HashMap<String, EMConversation>();
	 synchronized (conversations) {
            for (EMConversation conversation : conversations) {
                String attr = conversation.getExtField();
		  if(attr!=null && attr.contains(Constants.CONVERSATION_ATTR_VIEW_VENDOR)){
			vendorConversations.put(conversation.getUserName(), conversation);
		  }
            }
        }

	List<Pair<Long, EMConversation>> sortList = new ArrayList<Pair<Long, EMConversation>>();
	for (EMConversation conversation : vendorConversations.values()) {
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
		intentFilter.addAction(Constants.ACTION_NEW_MSG_FROM_SERVICE);

		broadcastReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				SamLog.e(TAG,"receive broadcast ACTION_NEW_MSG_FROM_SERVICE");
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

	private void setUpView(){
		conversationList.clear();
		conversationList.addAll(loadConversationList());
		mConversationList.init(conversationList);
		mConversationList.setConversationListIn(EaseConstant.CONVERSATION_LIST_IN_VENDOR);
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
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		SamLog.i(TAG, "onCreateView");
		
		if(rootView == null){
			rootView = inflater.inflate(R.layout.fragment_vendor, container,false); 
			mConversationList = (EaseConversationList)rootView.findViewById(R.id.list); 

			LoginUser cuser = SamService.getInstance().get_current_user();
			if(cuser.getUserType() == LoginUser.USER){
				mConversationList.setVisibility(View.GONE); 
			}else{
				mConversationList.setVisibility(View.VISIBLE);
 			}

			listItemClickListener = new EaseConversationListItemClickListener() {
				@Override
				public void onListItemClicked(EMConversation conversation) {
					if(SamVendor_Fragment.this.getActivity()==null){
						return;
					}
					
					Intent newIntent = new Intent(SamVendor_Fragment.this.getActivity(), ChatActivity.class);
					
					newIntent.putExtra(Constants.EXTRA_USER_ID, conversation.getUserName());
					newIntent.putExtra(Constants.EXTRA_CHAT_TYPE, Constants.CHATTYPE_SINGLE);
					newIntent.putExtra(Constants.CHAT_ACTIVITY_TYPE,Constants.CHAT_ACTIVITY_TYPE_VIEW_VENDOR);					
					
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
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState){
		super.onActivityCreated(savedInstanceState);
		setUpView();
		if(!isBroadcastRegistered){
			registerBroadcastReceiver();
			isBroadcastRegistered=true;
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

		if(isBroadcastRegistered){
			unregisterBroadcastReceiver();
			isBroadcastRegistered=false;
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {  
		 
	} 
	
}


