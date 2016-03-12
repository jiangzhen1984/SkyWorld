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
 
  
public class ContactActivity extends FragmentActivity  
{  
	private final static String TAG="ContactActivity";
	
	private SamContact_Fragment mContactFragment;
	
	@Override  
	protected void onCreate(Bundle savedInstanceState)  
	{  
		super.onCreate(savedInstanceState);  
		setContentView(R.layout.activity_contacts); 

		mContactFragment = new SamContact_Fragment();

		mContactFragment.setContactsMap(getContacts());
		mContactFragment.setContactListItemClickListener(new EaseContactListItemClickListener() {
			@Override
			public void onListItemClicked(EaseUser user) {
               		 startActivity(new Intent(ContactActivity.this, ChatActivity.class).putExtra(EaseConstant.EXTRA_USER_ID, user.getUsername()));
			}
        	});
  
		FragmentManager fm = getSupportFragmentManager();  
		FragmentTransaction tx = fm.beginTransaction();  
		tx.add(R.id.id_content, (Fragment)mContactFragment,"CONTACTS");
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

	

	

	private Map<String, EaseUser> getContacts(){
		Map<String, EaseUser> contacts = EaseMobHelper.getInstance().getContactList();
		
		return contacts;
	}

	
  
}  
