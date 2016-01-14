/**
 * 
 */
package com.android.samchat;

import com.android.samchat.R;

import com.android.samservice.*;
import com.easemob.easeui.ui.EaseContactListFragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SamContact_Fragment extends EaseContactListFragment{

	static final String TAG = "SamContact_Fragment";

	private static boolean need_show = true;

	private View rootView;
	private BadgeView unread_new_friend_bage;
	private LinearLayout mNewFriend_layout;
	private RelativeLayout mNewFriend_relativelayout;
	private TextView mNewFriendtxt;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if(rootView == null){
			rootView = inflater.inflate(R.layout.fragment_contact, container,false);
			mNewFriend_layout = (LinearLayout)rootView.findViewById(R.id.newFriend_layout);
			mNewFriend_relativelayout = (RelativeLayout)rootView.findViewById(R.id.newFriend_relativelayout);
			mNewFriendtxt = (TextView)rootView.findViewById(R.id.newFriendtxt);
			
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


			
		}
		return rootView;
	}

	public void hideBage(){
		
		if(rootView!=null){
			BadgeView badge = (BadgeView)rootView.getTag();
			if(badge.isShown()){
				SamLog.e(TAG,"hideBage!");
				badge.hide();
			}
		}

		//Intent intent = new Intent();
		//intent.setAction(SamChats_Fragment.HIDE_SAMCHATS_REDPOINT);
		//getActivity().sendBroadcast(intent);
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

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {  
		if(requestCode == 1){
			if(resultCode == 1){ //OK
				SamLog.e(TAG,"nees show true");
				need_show = true;
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

}
