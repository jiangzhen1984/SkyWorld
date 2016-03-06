package com.android.samchat;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.android.samchat.R;
import com.android.samchat.easemobdemo.EaseMobHelper;
import com.android.samchat.slidemenu.SlidingMenu;
import com.android.samservice.*;
import com.android.samservice.info.LoginUser;
import com.easemob.EMCallBack;
import com.easemob.EMConnectionListener;
import com.easemob.EMError;
import com.easemob.EMEventListener;
import com.easemob.EMNotifierEvent;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMGroupManager;
import com.easemob.chat.EMMessage;
import com.easemob.easeui.EaseConstant;
import com.easemob.easeui.controller.EaseUI;
import com.easemob.easeui.domain.EaseUser;
import com.easemob.easeui.ui.EaseContactListFragment.EaseContactListItemClickListener;
import com.easemob.easeui.ui.EaseConversationListFragment.EaseConversationListItemClickListener;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.Toast;
import android.view.inputmethod.InputMethodManager;

public class MainActivity extends IndicatorFragmentActivity implements
		OnPageChangeListener, EMEventListener{

	static public final String TAG="SamChat_Main";
	static public final String ACTIVITY_NAME = "com.android.samchat.MainActivity";
	
	static final String EXIT_APP_CONFIRM = "com.android.samchat.exitapp";
	static final String LOGOUT_CONFIRM = "com.android.samchat.logout";
	
	private boolean isExit = false; 
	
	
	private final int ACTIVITY_TIMEOUT=2000;
	private final int MSG_EXIT_ACTIVITY_TIMEOUT = 1;
	public static final int MSG_LOGOUT_CALLBACK = 2;
	
	
	public static final int TAB_ID_SAMSERVICES=0;
	public static final int TAB_ID_SAMCHATS=1;
	public static final int TAB_ID_SAMPUBLIC=2;
	public static final int TAB_ID_VENDOR=3;
	
	
	//private FragmentTabHost mTabHost;
	private LayoutInflater layoutInflater;
	private Class fragmentArray[] = {	
			SamService_Fragment.class, 
			SamChats_Fragment.class,
			SamPublic_Fragment.class, 
			SamVendor_Fragment.class };
		
	private int imageViewArray[] = {	
			R.drawable.sam_services, 
			R.drawable.sam_chats,
			R.drawable.sam_public,
			R.drawable.sam_me};

	private int imageViewArraySelected[] = {	
			R.drawable.sam_services_selected, 
			R.drawable.sam_chats_selected,
			R.drawable.sam_public_selected,
			R.drawable.sam_me_selected};
	
	private int textViewArray[] = {
			R.string.sam_services,
			R.string.sam_chats,
			R.string.sam_public,
			R.string.sam_vendor};
	
	private SamService_Fragment fragment_samservice;
	private SamChats_Fragment fragment_samchats;
	private SamPublic_Fragment fragment_sampublic;
	private SamVendor_Fragment fragment_vendor;
	

	private ConnectivityManager mConnectivityManager; 
   	private NetworkInfo netInfo; 


	private TextView mSlideContact;
	private TextView mSetting;
	private TextView mLogout;
	private TextView mExitApp;
	private TextView mMe;

	private ImageView mOption_button;

	private SamProcessDialog mDialog;
	

	@Override
	protected int supplyTabs(List<TabInfo> tabs) {
		TabInfo tabinfo = new TabInfo(TAB_ID_SAMSERVICES, getString(textViewArray[0]),
			imageViewArray[0],imageViewArraySelected[0],fragmentArray[0]);
		tabinfo.fragment = new SamService_Fragment();
		tabs.add(tabinfo);

		tabinfo = new TabInfo(TAB_ID_SAMCHATS, getString(textViewArray[1]),
					imageViewArray[1],imageViewArraySelected[1],fragmentArray[1]);
		tabinfo.fragment = new SamChats_Fragment();
		tabs.add(tabinfo);

		tabinfo = new TabInfo(TAB_ID_SAMPUBLIC, getString(textViewArray[2]),
					imageViewArray[2],imageViewArraySelected[2],fragmentArray[2]);
		tabinfo.fragment = new SamPublic_Fragment();
		tabs.add(tabinfo);
		
		tabinfo = new TabInfo(TAB_ID_VENDOR, getString(textViewArray[3]),
					imageViewArray[3],imageViewArraySelected[3],fragmentArray[3]);
		tabinfo.fragment = new SamVendor_Fragment();
		tabs.add(tabinfo);

		return TAB_ID_SAMSERVICES;
    }

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Intent intent = new Intent();
		intent.setAction(SamService.FINISH_ALL_SIGN_ACTVITY);
		sendBroadcast(intent);

		IntentFilter hide_show_filter = new IntentFilter();
		hide_show_filter.addAction(SamChats_Fragment.HIDE_SAMCHATS_REDPOINT);
		hide_show_filter.addAction(SamChats_Fragment.SHOW_SAMCHATS_REDPOINT);
		registerReceiver(HideShowSamChatRDPReceiver, hide_show_filter);
		
		initPage();

		mDialog = new SamProcessDialog();

		// configure the SlidingMenu
        	final SlidingMenu menu = new SlidingMenu(this);
        	menu.setMode(SlidingMenu.RIGHT);
        	//menu.setShadowWidthRes(R.dimen.shadow_width);
        	//menu.setShadowDrawable(R.drawable.shadow);
        	menu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
        	menu.setFadeDegree(0.35f);
        	menu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
        	menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
        	menu.setMenu(R.layout.slidemenu);
		mSlideContact = (TextView)menu.findViewById(R.id.contact);
		mSlideContact.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				menu.toggle();
				launchContactActivity();
			}
		});

		mSetting = (TextView)menu.findViewById(R.id.settings);
		mSetting.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				menu.toggle();
				launchSettingActivity();
			}
		});

		mMe = (TextView)menu.findViewById(R.id.me);
		mMe.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				menu.toggle();
				launchMeActivity();
			}
		});

		mLogout = (TextView)menu.findViewById(R.id.logout);
		mLogout.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View arg0) {
					launchDialogActivityNeedConfirmForLogout(getString(R.string.reminder),getString(R.string.logout_reminder));
				}
			});

		mExitApp = (TextView)menu.findViewById(R.id.exitapp);
		mExitApp.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View arg0) {
					launchDialogActivityNeedConfirmForExitApp(getString(R.string.reminder),getString(R.string.exitapp_reminder));
				}
			});

		mOption_button = (ImageView)findViewById(R.id.option_button);
		mOption_button.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				menu.toggle();
			}
		});
		
		EMGroupManager.getInstance().loadAllGroups();
		EMChatManager.getInstance().loadAllConversations();

		EMChatManager.getInstance().addConnectionListener(connectionListener);

	}


	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {  
		if(requestCode == 1){
			if(resultCode == 1){ //OK
				SamLog.e(TAG,"exit app...");
				exitProgram();
			}else{
				SamLog.e(TAG,"cancel exit app...");
			}
		}else if(requestCode == 2){
			if(resultCode == 1){ //OK
				SamLog.e(TAG,"logout...");
				logoutAccount();
			}else{
				SamLog.e(TAG,"cancel logout...");
			}
		}
	}

	private void logoutAccount(){
		if(mDialog!=null){
    			mDialog.launchProcessDialog(this,getString(R.string.question_publish_now));
    		}

		EaseMobHelper.getInstance().logout(true,new EMCallBack() {
                    
                    @Override
                    public void onSuccess() {
                    	MainActivity.this.runOnUiThread(new Runnable() {
                            public void run() {
					SignService.getInstance().SignOut( mHandler, MSG_LOGOUT_CALLBACK);
					LoginUser user = SamService.getInstance().get_current_user();
					user.seteasemob_status(LoginUser.INACTIVE);
					SamService.getInstance().getDao().updateLoginUserEaseStatus(user.getphonenumber(),LoginUser.INACTIVE);			
                            }
                        });
                    }
                    
                    @Override
                    public void onProgress(int progress, String status) {}
                    
                    @Override
                    public void onError(int code, String message) {
                    	MainActivity.this.runOnUiThread(new Runnable() {
                            public void run() {
					SignService.getInstance().SignOut( mHandler, MSG_LOGOUT_CALLBACK);
					LoginUser user = SamService.getInstance().get_current_user();
					user.seteasemob_status(LoginUser.INACTIVE);
					SamService.getInstance().getDao().updateLoginUserEaseStatus(user.getphonenumber(),LoginUser.INACTIVE);			
                            }
                        });
                    }
                });
		
		
	}

	private void launchSignInActivity()
	{
		Intent newIntent = new Intent(this,SignInActivity.class);
		int intentFlags = Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP;
		newIntent.setFlags(intentFlags);
		startActivity(newIntent);
	}

	private void exitProgram(){
		exitProgrames();
	}

	private void launchContactActivity(){
		Intent newIntent = new Intent(this,ContactActivity.class);
		int intentFlags = Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP;
		newIntent.setFlags(intentFlags);
		startActivity(newIntent);
	}

	private void launchSettingActivity(){
		Intent newIntent = new Intent(this,SettingActivity.class);
		int intentFlags = Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP;;
		newIntent.setFlags(intentFlags);
		startActivity(newIntent);

	}

	private void launchMeActivity(){
		Intent newIntent = new Intent(this,MeActivity.class);
		int intentFlags = Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP;
		newIntent.setFlags(intentFlags);
		startActivity(newIntent);
	}

	private void launchDialogActivityNeedConfirmForExitApp(String title,String msg){
		Intent newIntent = new Intent(EXIT_APP_CONFIRM);		
		//int intentFlags = Intent.FLAG_ACTIVITY_NEW_TASK;// | Intent.FLAG_ACTIVITY_CLEAR_TOP;
		//newIntent.setFlags(intentFlags);
		newIntent.putExtra("title", title);
		newIntent.putExtra("message", msg);
		this.startActivityForResult(newIntent, 1);
	}

	private void launchDialogActivityNeedConfirmForLogout(String title,String msg){
		Intent newIntent = new Intent(LOGOUT_CONFIRM);		
		//int intentFlags = Intent.FLAG_ACTIVITY_NEW_TASK;// | Intent.FLAG_ACTIVITY_CLEAR_TOP;
		//newIntent.setFlags(intentFlags);
		newIntent.putExtra("title", title);
		newIntent.putExtra("message", msg);
		this.startActivityForResult(newIntent, 2);
	}

	

	

	/**
	 * ≥ı ºªØFragment
	 */
	private void initPage() {
		fragment_samservice = (SamService_Fragment)getFragment(TAB_ID_SAMSERVICES);//new SamService_Fragment();
		fragment_samchats = (SamChats_Fragment)getFragment(TAB_ID_SAMCHATS);//new SamChats_Fragment();
		fragment_samchats.setConversationListItemClickListener(new EaseConversationListItemClickListener() {
			@Override
			public void onListItemClicked(EMConversation conversation) {
				Intent newIntent = new Intent(MainActivity.this, ChatActivity.class);
				if(conversation.isGroup()){
					newIntent.putExtra(Constants.EXTRA_CHAT_TYPE, Constants.CHATTYPE_GROUP);
				}

				newIntent.putExtra(Constants.EXTRA_USER_ID, conversation.getUserName());
				startActivity(newIntent);
			}
		});
		
		//fragment_samme = (SamMe_Fragment)getFragment(TAB_ID_SAMME);//new SamMe_Fragment();
		
		//fragment_samcontact= (SamContact_Fragment)getFragment(TAB_ID_SAMPUBLIC);//new SamContact_Fragment();
		

		//registerBroadcastReceiver();
		registerNetworkStatusReceiver();
		EMChatManager.getInstance().registerEventListener(this,
				new EMNotifierEvent.Event[] { EMNotifierEvent.Event.EventNewMessage ,EMNotifierEvent.Event.EventOfflineMessage, EMNotifierEvent.Event.EventConversationListChanged});
		
		SamService.getInstance().onActivityLaunched(fragment_samservice,fragment_samchats);
	}


	

	@Override
	public void onEvent(EMNotifierEvent event) {
	SamLog.e(TAG,"onEvent!!!");
		switch (event.getEvent()) {
		case EventNewMessage: 
		{
			EMMessage message = (EMMessage) event.getData();

			EaseUI.getInstance().getNotifier().onNewMsg(message);

			refreshUIWithMessage();
			break;
		}

		case EventOfflineMessage: {
		    refreshUIWithMessage();
			break;
		}

		case EventConversationListChanged: {
		    refreshUIWithMessage();
		    break;
		}
		
		default:
			break;
		}
	}

	private void refreshUIWithMessage() {
		SamLog.e(TAG,"refreshUIWithMessage!!!");
		runOnUiThread(new Runnable() {
			public void run() {
				if (fragment_samchats != null) {
					fragment_samchats.refresh();
				}
			}
		});
	}
   
	private BroadcastReceiver myNetReceiver = new BroadcastReceiver() { 
   		@Override 
		public void onReceive(Context context, Intent intent) { 
     			String action = intent.getAction(); 
			if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) { 
				mConnectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE); 
				netInfo = mConnectivityManager.getActiveNetworkInfo();   
				if(netInfo != null && netInfo.isAvailable()) { 
					SamLog.e(TAG,"network connected!!!!!!!!!!!!");
   					SamService.getInstance().onNetworkConnect();
				} else { 
					SamLog.e(TAG,"network disconnected!!!!!!!!!!!!");
   					SamService.getInstance().onNetworkDisconnect();
				} 
			} 
   
		}  
	}; 

	private void registerNetworkStatusReceiver(){
		IntentFilter mFilter = new IntentFilter(); 
		mFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION); 
		registerReceiver(myNetReceiver, mFilter); 
	}

	private void unregisterNetworkStatusReceiver(){
		if(myNetReceiver!=null){ 
			unregisterReceiver(myNetReceiver); 
		}
	}

	




	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		super.onPageScrolled(arg0,arg1,arg2);
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		if(getCurrentFocus()!=null){
			imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}


	/*(private void updateTextColor(int arg0){
		int count = textViewArray.length;
		
		for(int i=0;i<count;i++){
			if(i!=arg0){
				TextView txtView = (TextView) mTabHost.getTabWidget().getChildAt(i).findViewById(R.id.tab_textview); 
				txtView.setTextColor(android.graphics.Color.BLACK);
			}else{
				TextView txtView = (TextView) mTabHost.getTabWidget().getChildAt(i).findViewById(R.id.tab_textview); 
				txtView.setTextColor(android.graphics.Color.GREEN);
			}
		}
	
	}

	private void updateRedPoint(int arg0 , boolean enable){
		ImageView redv = (ImageView)mTabHost.getTabWidget().getChildAt(arg0).findViewById(R.id.tab_redSmallPoint);
		if(enable){
			redv.setVisibility(View.VISIBLE);
		}else{
			redv.setVisibility(View.INVISIBLE);
		}
	}*/

	
	
	/*@Override
	public void onPageSelected(int arg0) {
		currentTabPostition  = arg0;
		SamLog.i(TAG,"currentTabPostition:"+currentTabPostition);
		TabWidget widget = mTabHost.getTabWidget();
		int oldFocusability = widget.getDescendantFocusability();
		widget.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
		mTabHost.setCurrentTab(arg0);
		widget.setDescendantFocusability(oldFocusability);
		//widget.setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);
		updateTextColor(arg0);
		//updateRedPoint(arg0,false);
		//.setBackgroundResource(R.drawable.selector_tab_background);

		
	}

	@Override
	public void onTabChanged(String tabId) {
		int position = mTabHost.getCurrentTab();
		vp.setCurrentItem(position);
	}*/

	private BroadcastReceiver HideShowSamChatRDPReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if(intent.getAction().equals(SamChats_Fragment.HIDE_SAMCHATS_REDPOINT)){
				//updateRedPoint(TAB_ID_SAMCHATS, false);
			}else if(intent.getAction().equals(SamChats_Fragment.SHOW_SAMCHATS_REDPOINT)){
				//updateRedPoint(TAB_ID_SAMCHATS, true);
			}
	    }
	};
	
	
	@Override
	protected void onPause() {
	    super.onPause();
	    //finish();
	}
	 
	@Override
	protected void onResume(){
	    super.onResume();
	}
	 
	@Override
	protected void onDestroy(){
		super.onDestroy();
		SamLog.i(TAG,"MainActivity onDestroy!");
		unregisterReceiver(HideShowSamChatRDPReceiver);
		unregisterNetworkStatusReceiver();
		EMChatManager.getInstance().removeConnectionListener(connectionListener);
		
	}
	
	@Override
	public void onBackPressed(){
		Intent i= new Intent(Intent.ACTION_MAIN);
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		i.addCategory(Intent.CATEGORY_HOME);
		startActivity(i);
	}

	public void exitProgrames(){
    		/*Intent startMain = new Intent(Intent.ACTION_MAIN);
		startMain.addCategory(Intent.CATEGORY_HOME);
		startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(startMain);
		android.os.Process.killProcess(android.os.Process.myPid());*/

		 this.finish(); 
	}

       
    private void exit() { 
        if (!isExit) { 
            isExit = true; 
            Toast.makeText(getApplicationContext(), getString(R.string.exit_app_confirmation), 
                    Toast.LENGTH_SHORT).show(); 
            mHandler.sendEmptyMessageDelayed(MSG_EXIT_ACTIVITY_TIMEOUT,ACTIVITY_TIMEOUT); 
        } else { 
             
            SamLog.e(TAG, "exit application"); 

	     SamService.getInstance().stopSamService();
               
            this.finish(); 
        } 
    } 
    
	Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			
	    		switch(msg.what){
	    			case MSG_EXIT_ACTIVITY_TIMEOUT:
	    				isExit = false;
		    		break;
					
				case MSG_LOGOUT_CALLBACK:
				if(msg.arg1 == SignService.R_SIGN_OUT_OK){
					if(mDialog!=null){
    						mDialog.dismissPrgoressDiglog();
    					}
					exitActivity();
					launchSignInActivity();
				}else if(msg.arg1 == SignService.R_SIGN_OUT_FAILED){
					if(mDialog!=null){
    						mDialog.dismissPrgoressDiglog();
    					}
					exitActivity();
					launchSignInActivity();
				}
				break;
	    		}
		}
	};

	protected EMConnectionListener connectionListener = new EMConnectionListener() {
		@Override
		public void onDisconnected(final int error) {
			MainActivity.this.runOnUiThread(new Runnable() {
				public void run() {
					if (error == EMError.USER_REMOVED || error == EMError.CONNECTION_CONFLICT) {
						if(mDialog!=null){
    							mDialog.launchProcessDialog(MainActivity.this,getString(R.string.question_publish_now));
    						}

						EaseMobHelper.getInstance().reset();

						SignService.getInstance().SignOut( mHandler, MSG_LOGOUT_CALLBACK);
						LoginUser user = SamService.getInstance().get_current_user();
						user.seteasemob_status(LoginUser.INACTIVE);
						SamService.getInstance().getDao().updateLoginUserEaseStatus(user.getphonenumber(),LoginUser.INACTIVE);	
					} 
				}
			});
			
		}
        
		@Override
		public void onConnected() {
			MainActivity.this.runOnUiThread(new Runnable() {
				public void run() {
					
				}
			});
		}
	};


	public void exitActivity(){
		SamLog.e(TAG, "exit main activity"); 
		SamService.getInstance().stopSamService();
		this.finish();
	}


	
}
