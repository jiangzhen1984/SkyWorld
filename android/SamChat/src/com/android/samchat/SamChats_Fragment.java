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

import android.app.Activity;
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
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.samservice.*;
import com.android.samservice.info.LoginUser;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.ui.EaseConversationListFragment;
import com.hyphenate.easeui.ui.EaseConversationListFragment.EaseConversationListItemClickListener;
import com.hyphenate.easeui.widget.EaseConversationList;


public class SamChats_Fragment extends Fragment {

	static final String TAG = "SamChats_Fragment";
	
	private View rootView;

	private EaseConversationList mConversationList;
	protected List<EMConversation> conversationList = new ArrayList<EMConversation>();
	private EaseConversationListItemClickListener listItemClickListener;

	protected boolean hidden;

	private void deleteConversation(EMConversation conversation){
		if(conversation.isGroup()){
			EMClient.getInstance().chatManager().deleteConversation(conversation.getUserName(), true);
			conversationList.clear();
			conversationList.addAll(loadConversationList());
			mConversationList.refresh();
			return;
		}
		
		String attr = conversation.getExtField();
		if(attr == null){
			return;
		}else if(!attr.contains(EaseConstant.CONVERSATION_ATTR_VIEW_CHAT)){
			return;
		}else if(attr.equals(EaseConstant.CONVERSATION_ATTR_VIEW_CHAT)){
			/*delete conversation from db*/
			EMClient.getInstance().chatManager().deleteConversation(conversation.getUserName(), true);
			conversationList.clear();
			conversationList.addAll(loadConversationList());
			mConversationList.refresh();
		}else{
			attr = attr.replaceAll(EaseConstant.CONVERSATION_ATTR_VIEW_CHAT,"");
			conversation.setExtField(attr);
			conversationList.clear();
			conversationList.addAll(loadConversationList());
			mConversationList.refresh();
		}
		
		
	}

	@Override  
	public boolean onContextItemSelected(MenuItem item) {  
		if(!getUserVisibleHint()){
			return false;
		}
		
		AdapterContextMenuInfo menuInfo = (AdapterContextMenuInfo)item.getMenuInfo(); 
		EMConversation conversation = conversationList.get(menuInfo.position);
		deleteConversation(conversation);
		return true;
	}  

	@Override  
	public void onCreateContextMenu(ContextMenu menu, View v,  ContextMenuInfo menuInfo) {  
		menu.add(0, v.getId(), 0, getString(R.string.delete_conversation));        
		super.onCreateContextMenu(menu, v, menuInfo);  
	} 
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		SamLog.e(TAG,"onCreateView");
		if(rootView == null){
			rootView = inflater.inflate(R.layout.fragment_chats, container,false);
			mConversationList = (EaseConversationList)rootView.findViewById(R.id.list); 
			registerForContextMenu(mConversationList);

			listItemClickListener = new EaseConversationListItemClickListener() {
				@Override
				public void onListItemClicked(EMConversation conversation) {
					Intent newIntent = new Intent(SamChats_Fragment.this.getActivity(), ChatActivity.class);

					if(conversation.isGroup()){
						newIntent.putExtra(Constants.EXTRA_CHAT_TYPE, Constants.CHATTYPE_GROUP);
						newIntent.putExtra(Constants.EXTRA_USER_ID, conversation.getUserName());
						startActivity(newIntent);
					}else{
						newIntent.putExtra(Constants.EXTRA_CHAT_TYPE, Constants.CHATTYPE_SINGLE);
						newIntent.putExtra(Constants.EXTRA_USER_ID, conversation.getUserName());
						newIntent.putExtra(Constants.CHAT_ACTIVITY_TYPE,Constants.CHAT_ACTIVITY_TYPE_VIEW_CHAT);					
						startActivity(newIntent);
					}
				}
			};
		}
		return rootView;
	}

	

	protected List<EMConversation> loadConversationList(){
        // 获取所有会话，包括陌生人
        Map<String, EMConversation> conversations = EMClient.getInstance().chatManager().getAllConversations();
	 Map<String, EMConversation> chatConversations = new HashMap<String, EMConversation>();
	 synchronized (conversations) {
            for (EMConversation conversation : conversations.values()) {
		  if(conversation.isGroup()){
			chatConversations.put(conversation.getUserName(), conversation);
			continue;
		  }else{
			String attr = conversation.getExtField();
			 if(attr!=null && attr.contains(Constants.CONVERSATION_ATTR_VIEW_CHAT)){
				chatConversations.put(conversation.getUserName(), conversation);
		  	}
		  }
            }
        }

	List<Pair<Long, EMConversation>> sortList = new ArrayList<Pair<Long, EMConversation>>();
	for (EMConversation conversation : chatConversations.values()) {
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
		intentFilter.addAction(Constants.ACTION_NEW_MSG_FROM_CHAT);
		intentFilter.addAction(Constants.ACTION_NEW_MSG_FROM_GROUP);

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

	private void setUpView(){
		conversationList.clear();
		conversationList.addAll(loadConversationList());
		mConversationList.init(conversationList);
		mConversationList.setConversationListIn(EaseConstant.CONVERSATION_LIST_IN_CHAT);
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
		SamLog.i(TAG, "onActivityCreated");

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



	



	private boolean isTopFragment(){
		boolean isTop = false;
		if(((MainActivity)getActivity()).getCurrentTab() == MainActivity.TAB_ID_SAMCHATS){
			isTop = true;
		}
		return isTop; 
	}

	private boolean isTopMainActivity()  
	{  
		boolean isTop = false;  
		ActivityManager am = (ActivityManager)getActivity().getSystemService(Context.ACTIVITY_SERVICE);  
		ComponentName cn = am.getRunningTasks(1).get(0).topActivity;  
		SamLog.e(TAG, "isTopActivity = " + cn.getClassName());  
		if (cn.getClassName().contains(MainActivity.ACTIVITY_NAME))  
		{  
			 isTop = true;  
		}  
		return isTop;  
	}

	private boolean isTopSamQAActivity()  
	{  
		boolean isTop = false;  
		ActivityManager am = (ActivityManager)getActivity().getSystemService(Context.ACTIVITY_SERVICE);  
		ComponentName cn = am.getRunningTasks(1).get(0).topActivity;  
		SamLog.e(TAG, "isTopActivity = " + cn.getClassName());  
		if (cn.getClassName().contains(MainActivity.ACTIVITY_NAME))  
		{  
			 isTop = true;  
		}  
		return isTop;  
	}
	

	
	
}
