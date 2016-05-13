package com.netease.nim.uikit;

import com.netease.nimlib.sdk.msg.model.RecentContact;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

public class NimMobHelper{

	private static NimMobHelper instance = null;
	private Context appContext;
	private LocalBroadcastManager broadcastManager;
	
	private NimMobHelper() {

	}

	public synchronized static  NimMobHelper getInstance() {
		if (instance == null) {
			instance = new NimMobHelper();
		}
		return instance;
	}

	/**
	 * init helper
	 * 
	 * @param context
	 *            application context
	 */
	public void init(Context context) {
		appContext = context;
		
		broadcastManager = LocalBroadcastManager.getInstance(appContext);
		
	}

	public void sendSearchUpdateBroadcast(RecentContact conversation){
		Bundle bundle = new Bundle();
		bundle.putSerializable("new_conversation",conversation);
		Intent intent = new Intent(NimConstants.ACTION_SEARCH_UPDATE);
		intent.putExtras(bundle);
	
		broadcastManager.sendBroadcast(intent);
	}

	public void sendChatUpdateBroadcast(RecentContact conversation){
		Bundle bundle = new Bundle();
		bundle.putSerializable("new_conversation",conversation);
		Intent intent = new Intent(NimConstants.ACTION_CHAT_UPDATE);
		intent.putExtras(bundle);
		broadcastManager.sendBroadcast(intent);
	}

	public void sendProsUpdateBroadcast(RecentContact conversation){
		Bundle bundle = new Bundle();
		bundle.putSerializable("new_conversation",conversation);
		Intent intent = new Intent(NimConstants.ACTION_PROS_UPDATE);
		intent.putExtras(bundle);
		broadcastManager.sendBroadcast(intent);
	}

} 
