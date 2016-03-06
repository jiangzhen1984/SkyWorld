package com.android.samchat;

import java.util.Map;

import com.android.samchat.easemobdemo.EaseMobHelper;
import com.android.samservice.Constants;
import com.android.samservice.SamLog;
import com.easemob.easeui.EaseConstant;
import com.easemob.easeui.domain.EaseUser;
import com.easemob.easeui.ui.EaseContactListFragment.EaseContactListItemClickListener;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
  
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;  
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
 
  
public class MeActivity extends FragmentActivity  
{  
	private final static String TAG="MeActivity";
	
	private SamMe_Fragment mMeFragment;

	@Override  
	protected void onCreate(Bundle savedInstanceState)  
	{  
		super.onCreate(savedInstanceState);  
		setContentView(R.layout.activity_me); 

		mMeFragment = new SamMe_Fragment();

		FragmentManager fm = getSupportFragmentManager();  
		FragmentTransaction tx = fm.beginTransaction();  
		tx.add(R.id.id_content, (Fragment)mMeFragment,"ME");
		tx.commit();
		
	} 


	@Override
	protected void onDestroy(){
		super.onDestroy();
		
	}
	
	@Override
	public void onBackPressed(){
		finish();
	}

	
	
  
}  

