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
	public static final int MSG_QUESTION_RECEIVED = 1;
	public static final int MSG_QUESTION_CANCEL = 2;
	public static final int MSG_ANSWER_SEND_CALLBACK = 3;

	private boolean isQAActivityLaunched = false;

	private BadgeView unread_question_bage;
	static int i = 0;
	private View rootView;
	private LinearLayout mSamQA_layout;
	private RelativeLayout mSamQA_relativelayout;
	private RelativeLayout mUn_read_question_num_layout;
	private TextView mUn_read_question_num;
	private TextView mSamQAtxt;
	

	private LinearLayout mSamFriendGroup_layout;
	private RelativeLayout mSamFriendGroup_relativelayout;

	private QuestionInfo question;

	private int un_read_question_num = 0;

	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		SamLog.e(TAG,"onCreateView");
		if(rootView == null){
			SamLog.e(TAG,"run again");
			
		
			rootView = inflater.inflate(R.layout.fragment_chats, container,false);
			mSamQA_layout = (LinearLayout)rootView.findViewById(R.id.samQA_layout);
			mSamQA_relativelayout = (RelativeLayout)rootView.findViewById(R.id.samQA_relativelayout);
			mSamQAtxt = (TextView)rootView.findViewById(R.id.samQAtxt);
			mUn_read_question_num_layout = (RelativeLayout)rootView.findViewById(R.id.un_read_question_num_layout);
			mUn_read_question_num = (TextView)rootView.findViewById(R.id.un_read_question_num);
			
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



	public void dismissBage(){
		un_read_question_num = 0;
		isQAActivityLaunched = false;
		hideBage();
	}


	private void update_questions_newq( ){
		if(isQAActivityLaunched){
			launchQAActivity();
		}else{
			un_read_question_num++;
			showBage();
		}

		((MainActivity)getActivity()).updateReminderIcon(MainActivity.TAB_ID_SAMCHATS,true);

		((MainActivity)getActivity()).sendNotification();
	
	}

	private void update_questions_cancelq( ){
		if(isQAActivityLaunched){
			launchQAActivity();
		}
	
	}
	
	public void showBage(){
		if(rootView!=null && mUn_read_question_num_layout!=null){
			mUn_read_question_num.setText(""+un_read_question_num);
			mUn_read_question_num_layout.setVisibility(View.VISIBLE);
		}

	}
	
	public void hideBage(){
		if(rootView!=null && mUn_read_question_num_layout!=null){
			mUn_read_question_num_layout.setVisibility(View.INVISIBLE);
		}

	}


	private void launchQAActivity()
	{
		Intent newIntent = new Intent(getActivity(),SamQAActivity.class);
		int intentFlags = Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP;
		newIntent.setFlags(intentFlags);
		SamLog.i(TAG,"launchQAActivity!");
		isQAActivityLaunched = true;
		un_read_question_num = 0;
		hideBage();
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
			isQAActivityLaunched = false;
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
