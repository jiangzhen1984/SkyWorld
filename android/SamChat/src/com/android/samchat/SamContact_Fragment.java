/**
 * 
 */
package com.android.samchat;

import com.android.samchat.R;
import com.android.samchat.easemobdemo.EaseMobHelper;
import com.android.samchat.easemobdemo.EaseMobHelper.DataSyncListener;

import com.android.samservice.*;
import com.easemob.chat.EMContactManager;
import com.easemob.easeui.ui.EaseContactListFragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class SamContact_Fragment extends EaseContactListFragment{

	static final String TAG = "SamContact_Fragment";
	public static final int MSG_UPDATE_BAGE_NEW_FRIEND = 1;

	private static boolean need_show = true;
	private static int contact_invite_msg_num=0;

	private View rootView;
	private BadgeView unread_new_friend_bage;
	private LinearLayout mNewFriend_layout;
	private RelativeLayout mNewFriend_relativelayout;
	//private TextView mNewFriendtxt;

	private LinearLayout mGroupchat_layout;

	ContactSyncListener contactSyncListener;
	BlackListSyncListener blackListSyncListener;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if(rootView == null){
			rootView = inflater.inflate(R.layout.fragment_contact, container,false);
			mNewFriend_layout = (LinearLayout)rootView.findViewById(R.id.newFriend_layout);
			mNewFriend_relativelayout = (RelativeLayout)rootView.findViewById(R.id.newFriend_relativelayout);
			//mNewFriendtxt = (TextView)rootView.findViewById(R.id.newFriendtxt);

			mGroupchat_layout = (LinearLayout)rootView.findViewById(R.id.groupchat_layout);
			
			unread_new_friend_bage = new BadgeView(getActivity().getBaseContext(), mNewFriend_relativelayout);
			unread_new_friend_bage.setBadgePosition(BadgeView.POSITION_TOP_RIGHT);
			rootView.setTag(unread_new_friend_bage);

			mNewFriend_layout.setOnClickListener(new OnClickListener(){
		    		@Override
		    		public void onClick(View arg0) {
		    			hideBage();
		    			launchNewFriendActivity();
				}
		    });

			mGroupchat_layout.setOnClickListener(new OnClickListener(){
		    		@Override
		    		public void onClick(View arg0) {
		    			launchGroupsActivity();
				}
		    });
			
		}
		return rootView;
	}

	private void hideBage(){
		
		if(rootView!=null){
			BadgeView badge = (BadgeView)rootView.getTag();
			if(badge.isShown()){
				SamLog.e(TAG,"hideBage!");
				badge.hide();
			}
		}

	}

	private void showBage(){
		if(rootView!=null && need_show){
			BadgeView badge = (BadgeView)rootView.getTag();
			SamLog.e(TAG,"showBage!");
			badge.setText(""+contact_invite_msg_num);
			badge.show();
		}

	}

	public void addInviteMsgNum(){
		contact_invite_msg_num++;
	}


	private void launchNewFriendActivity()
	{
		Intent newIntent = new Intent(getActivity(),NewFriendActivity.class);
		int intentFlags = Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP;
		newIntent.setFlags(intentFlags);
		SamLog.e(TAG,"launchNewFriendActivity!");
		need_show = false;
		startActivityForResult(newIntent,1);
	}

	private void launchGroupsActivity()
	{
		startActivity(new Intent(getActivity(), GroupsActivity.class));
		SamLog.e(TAG,"launchGroupsActivity!");
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {  
		if(requestCode == 1){
			if(resultCode == 1){ //OK
				SamLog.e(TAG,"nees show true");
				need_show = true;
				contact_invite_msg_num = 0;
			}else{
				SamLog.e(TAG,"nees show false");
			}
		}
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
		if(blackListSyncListener == null){
			blackListSyncListener = new BlackListSyncListener();
        		EaseMobHelper.getInstance().addSyncBlackListListener(blackListSyncListener);
			blackList = EMContactManager.getInstance().getBlackListUsernames();
		}
		if(contactSyncListener == null){
			contactSyncListener = new ContactSyncListener();
			EaseMobHelper.getInstance().addSyncContactListener(contactSyncListener);
			setContactsMap(EaseMobHelper.getInstance().getContactList());
			SamLog.i(TAG, "connectionListener is null, so get contactlist:"+EaseMobHelper.getInstance().getContactList().size());
		}
		super.onActivityCreated(savedInstanceState);
		SamLog.i(TAG, "onActivityCreated");
		
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
		super.onDestroy();

		if (contactSyncListener != null) {
			EaseMobHelper.getInstance().removeSyncContactListener(contactSyncListener);
			contactSyncListener = null;
		}

		if(blackListSyncListener != null){
			EaseMobHelper.getInstance().removeSyncBlackListListener(blackListSyncListener);
			blackListSyncListener = null;
		}

		
    }

	
	public Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch(msg.what){
			case MSG_UPDATE_BAGE_NEW_FRIEND:
				SamLog.e(TAG,"MSG_UPDATE_BAGE_NEW_FRIEND!");
				showBage();
				break;
			}
		}
	};


	class ContactSyncListener implements DataSyncListener{
        @Override
        public void onSyncComplete(final boolean success) {
            SamLog.e(TAG, "on contact list sync success:" + success);
            getActivity().runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            if(success){
                                setContactsMap(EaseMobHelper.getInstance().getContactList());
                                refresh();
                            }else{
                                String s1 = getResources().getString(R.string.get_failed_please_check);
                                Toast.makeText(getActivity(), s1, Toast.LENGTH_SHORT).show();
                                //loadingView.setVisibility(View.GONE);
                            }
                        }
                        
              });
        }
    }

	class BlackListSyncListener implements DataSyncListener{

        @Override
        public void onSyncComplete(boolean success) {
            getActivity().runOnUiThread(new Runnable(){

                @Override
                public void run() {
                    blackList = EMContactManager.getInstance().getBlackListUsernames();
                    refresh();
                }
                
            });
        }
        
    };
	

}
