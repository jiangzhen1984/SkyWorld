/**
 * 
 */
package com.android.samchat;

import com.android.samchat.R;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.samservice.*;
import com.android.samservice.info.LoginUser;
import com.easemob.easeui.ui.EaseConversationListFragment;

public class SamChats_Fragment extends EaseConversationListFragment {

	static final String TAG = "SamChats_Fragment";
	public static final String HIDE_SAMCHATS_REDPOINT = "com.android.sam.hidesrp";
	public static final String SHOW_SAMCHATS_REDPOINT = "com.android.sam.showsrp";
	public static final int MSG_QUESTION_RECEIVED = 1;
	public static final int MSG_QUESTION_CANCEL = 2;
	public static final int MSG_ANSWER_SEND_CALLBACK = 3;

	private static boolean need_show = true;

	private BadgeView unread_question_bage;
	static int i = 0;
	private View rootView;
	private SamProcessDialog mDialog;
	private LinearLayout mSamQA_layout;
	private RelativeLayout mSamQA_relativelayout;
	private TextView mSamQAtxt;

	private LinearLayout mSamFriendGroup_layout;
	private RelativeLayout mSamFriendGroup_relativelayout;

	private QuestionInfo question;

	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if(rootView == null){
			SamLog.e(TAG,"run again");
			mDialog = new SamProcessDialog();
			rootView = inflater.inflate(R.layout.fragment_chats, container,false);
			mSamQA_layout = (LinearLayout)rootView.findViewById(R.id.samQA_layout);
			mSamQA_relativelayout = (RelativeLayout)rootView.findViewById(R.id.samQA_relativelayout);
			mSamQAtxt = (TextView)rootView.findViewById(R.id.samQAtxt);

			mSamFriendGroup_layout =  (LinearLayout)rootView.findViewById(R.id.samFriendGroup_layout);
			mSamFriendGroup_relativelayout= (RelativeLayout)rootView.findViewById(R.id.samFriendGroup_relativelayout);
			
			unread_question_bage = new BadgeView(getActivity().getBaseContext(), mSamQA_relativelayout);
			unread_question_bage.setBadgePosition(BadgeView.POSITION_TOP_RIGHT);
			rootView.setTag(unread_question_bage);

			mSamQA_layout.setOnClickListener(new OnClickListener(){
		    		@Override
		    		public void onClick(View arg0) {
		    			hideBage();
		    			launchQAActivity();
				}
		    });

			mSamFriendGroup_layout.setOnClickListener(new OnClickListener(){
		    		@Override
		    		public void onClick(View arg0) {
		    			launchFGActivity();
				}
		    });


			
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
		if(rootView!=null){
			mSamQA_layout = (LinearLayout)rootView.findViewById(R.id.samQA_layout);
			if(SamService.getInstance().get_current_user().getUserType() == LoginUser.USER){
				mSamQA_layout.setVisibility(View.GONE);
			}else{
				mSamQA_layout.setVisibility(View.VISIBLE);
		
			}
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


	private void update_questions_newq( ){
		if(!need_show){
			launchQAActivity();
		}else{
			showBage();
		}
	
	}

	private void update_questions_cancelq( ){
		if(!need_show){
			launchQAActivity();
		}
	
	}
	
	public void showBage(){
		if(rootView!=null){
			BadgeView badge = (BadgeView)rootView.getTag();
			if(!badge.isShown()){
				badge.setText("...");
				badge.show();
			}
		}

		Intent intent = new Intent();
		intent.setAction(SamChats_Fragment.SHOW_SAMCHATS_REDPOINT);
		getActivity().sendBroadcast(intent);
	}
	
	public void hideBage(){
		
		if(rootView!=null){
			BadgeView badge = (BadgeView)rootView.getTag();
			if(badge.isShown()){
				SamLog.e(TAG,"hideBage!");
				badge.hide();
			}
		}

		Intent intent = new Intent();
		intent.setAction(SamChats_Fragment.HIDE_SAMCHATS_REDPOINT);
		getActivity().sendBroadcast(intent);
	}


	private void launchQAActivity()
	{
		Intent newIntent = new Intent(getActivity(),SamQAActivity.class);
		int intentFlags = Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP;
		newIntent.setFlags(intentFlags);
		SamLog.e(TAG,"launchQAActivity!");
		need_show = false;
		startActivityForResult(newIntent,1);
	}

	private void launchFGActivity()
	{
		Intent newIntent = new Intent(getActivity(),SamFriendGroupActivity.class);
		int intentFlags = Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP;
		newIntent.setFlags(intentFlags);
		SamLog.e(TAG,"launchFGActivity!");
		startActivity(newIntent);
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
	

	public Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch(msg.what){
			case MSG_QUESTION_RECEIVED:
				SamLog.e(TAG,"MSG_QUESTION_RECEIVED!");
				update_questions_newq();
				break;
			case MSG_QUESTION_CANCEL:
				SamLog.e(TAG,"MSG_QUESTION_CANCEL!");
				update_questions_cancelq();	
				break;
			}
		}

	};
	
}
