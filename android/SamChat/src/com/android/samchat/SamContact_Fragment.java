/**
 * 
 */
package com.android.samchat;

import com.android.samchat.R;
import com.android.samchat.easemobdemo.EaseMobHelper;
import com.android.samchat.easemobdemo.EaseMobHelper.DataSyncListener;

import com.android.samservice.*;
import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.ui.EaseContactListFragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
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

	private View rootView;

	ContactSyncListener contactSyncListener;
	BlackListSyncListener blackListSyncListener;

	private boolean isBroadcastRegistered = false;
	private BroadcastReceiver broadcastReceiver;
	private LocalBroadcastManager broadcastManager;

	private void registerBroadcastReceiver() {
		broadcastManager = LocalBroadcastManager.getInstance(getActivity());
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(Constants.ACTION_CONTACT_CHANAGED);

		broadcastReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				boolean isInvite = intent.getBooleanExtra("isInvite",false);
				if(!isInvite){
					SamLog.e(TAG,"update contacts");
					setContactsMap(EaseMobHelper.getInstance().getContactList());
					refresh();
				}
			}
		};
		
		broadcastManager.registerReceiver(broadcastReceiver, intentFilter);
	}
		

	
	private void unregisterBroadcastReceiver(){
	    broadcastManager.unregisterReceiver(broadcastReceiver);
	}

	@Override
	protected void setUpView() {
		super.setUpView();
		// 设置标题栏点击事件
        	titleBar.setLeftLayoutClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				getActivity().finish();
			}
		});
	}

	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if(rootView == null){
			rootView = inflater.inflate(R.layout.fragment_contact, container,false);
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
			//blackList = EMClient.getInstance().contactManager().getBlackListUsernames();
		}
		if(contactSyncListener == null){
			contactSyncListener = new ContactSyncListener();
			EaseMobHelper.getInstance().addSyncContactListener(contactSyncListener);
			setContactsMap(EaseMobHelper.getInstance().getContactList());
			SamLog.i(TAG, "connectionListener is null, so get contactlist:"+EaseMobHelper.getInstance().getContactList().size());
		}

		if(!isBroadcastRegistered){
			registerBroadcastReceiver();
			isBroadcastRegistered = true;
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

		if(isBroadcastRegistered){
			unregisterBroadcastReceiver();
			isBroadcastRegistered = false;
		}

		
    }

	
	class ContactSyncListener implements DataSyncListener{
		@Override
		public void onSyncComplete(final boolean success) {
			SamLog.i(TAG, "on contact list sync success:" + success);

			Activity av = getActivity();
			if(av == null ||av.isFinishing() ){
				return;
			}
		
			av.runOnUiThread(new Runnable() {
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
                    //blackList = EMClient.getInstance().contactManager().getBlackListUsernames();
                    refresh();
                }
                
            });
        }
        
    };
	

}
