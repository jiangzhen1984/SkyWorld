package com.android.samchat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.samservice.*;
import com.android.samservice.info.AvatarRecord;
import com.android.samservice.info.ContactUser;
import com.android.samservice.info.FollowerRecord;
import com.android.samservice.info.LoginUser;
import com.easemob.EMCallBack;
import com.easemob.EMConnectionListener;
import com.easemob.EMError;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMContactManager;
import com.easemob.easeui.utils.EaseUserUtils;

public class SamPublic_Fragment extends Fragment{
	static final String TAG = "SamPublic_Fragment";

	private View rootView;
	private Context mContext;
	private ListView mSampublic_list;
	private PublicListAdapter mAdapter;
	private boolean isBroadcastRegistered=false;
	private LocalBroadcastManager broadcastManager;
	private BroadcastReceiver broadcastReceiver; 


	public void refresh(List<ContactUser> follwerList) {
		mAdapter.setPublicArray(follwerList);
		mAdapter.setCount(follwerList.size());
		mAdapter.notifyDataSetChanged();
	}

	public void refresh(){
		LoginUser currentUser = SamService.getInstance().get_current_user();
		SamDBDao dao = SamService.getInstance().getDao();
		
		List<FollowerRecord> rdList = dao.query_FollowerRecord_db(currentUser.getunique_id());
		List<ContactUser> userArray = new ArrayList<ContactUser>();
		
		for(FollowerRecord rd:rdList){
			ContactUser user = dao.query_ContactUser_db_by_username(rd.getusername());
			if(user!=null){
				userArray.add(user);
			}
		}

		mAdapter.setPublicArray(userArray);
		mAdapter.setCount(userArray.size());
		mAdapter.notifyDataSetChanged();		
	}



	public void queryPublicSamInformation(long uid){
		SamService.getInstance().queryPublicInfo(uid,new SMCallBack(){
			@Override
			public void onSuccess(final Object obj){
				getActivity().runOnUiThread(new Runnable() {
					public void run() {}
				});
			} 

			@Override
			public void onFailed(int code) {
				getActivity().runOnUiThread(new Runnable() {
					public void run() {}
				});
			}

			@Override
			public void onError(int code) {
				getActivity().runOnUiThread(new Runnable() {
					public void run() {}
				});
			}

		});

	}
			
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		SamLog.i(TAG, "onCreateView");
		
		if(rootView == null){
			rootView = inflater.inflate(R.layout.fragment_public, container,false);
			mSampublic_list = (ListView)rootView.findViewById(R.id.sampublic_list);
			mContext = getActivity().getBaseContext();
			mAdapter = new PublicListAdapter(mContext);
			mSampublic_list.setAdapter(mAdapter);

			mSampublic_list.setOnItemClickListener(new OnItemClickListener(){   
			@Override   
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				/*arg2 is postion*/
				List<ContactUser> pubList = mAdapter.getPublicArray();
				ContactUser cuser = pubList.get(arg2);
				queryPublicSamInformation(cuser.getunique_id());
				
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
	}
	
	
	@Override
	public void onDetach(){
		super.onDetach();
		SamLog.i(TAG, "onDetach");
	}

	private void registerBroadcastReceiver() {
		broadcastManager = LocalBroadcastManager.getInstance(getActivity());
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(Constants.ACTION_FOLLOWER_CHANAGED);

		broadcastReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				refresh();
			}
		};
		
		broadcastManager.registerReceiver(broadcastReceiver, intentFilter);
	}
		

	
	private void unregisterBroadcastReceiver(){
	    broadcastManager.unregisterReceiver(broadcastReceiver);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState){
		super.onActivityCreated(savedInstanceState);

		refresh();
		
		if(!isBroadcastRegistered){
			registerBroadcastReceiver();
			isBroadcastRegistered = true;
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
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if(isBroadcastRegistered){
			unregisterBroadcastReceiver();
			isBroadcastRegistered = false;
		}
	}
	
}

