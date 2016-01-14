package com.android.samchat;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.Inflater;

import com.android.samchat.R;
import com.android.samservice.*;
import com.android.samservice.info.LoginUser;
import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.easeui.EaseConstant;
import com.easemob.easeui.domain.EaseUser;
import com.easemob.easeui.ui.EaseContactListFragment;
import com.easemob.easeui.ui.EaseContactListFragment.EaseContactListItemClickListener;
import com.easemob.easeui.ui.EaseConversationListFragment;
import com.easemob.easeui.ui.EaseConversationListFragment.EaseConversationListItemClickListener;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.R.integer;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.Toast;
import android.view.Menu;
import android.view.ViewConfiguration;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;

public class MainActivity extends FragmentActivity implements
		OnPageChangeListener, OnTabChangeListener {

	static private final String TAG="SamChat_Main";
	static public final String ACTIVITY_NAME = "com.android.samchat.MainActivity";
	private boolean isExit = false; 
	
	
	private final int ACTIVITY_TIMEOUT=2000;
	private final int MSG_EXIT_ACTIVITY_TIMEOUT = 1;
	
	
	public static final int TAB_ID_SAMSERVICES=0;
	public static final int TAB_ID_SAMCHATS=1;
	public static final int TAB_ID_SAMME=2;
	public static final int TAB_ID_SAMCONTACT=3;
	
	private FragmentTabHost mTabHost;
	private LayoutInflater layoutInflater;
	private Class fragmentArray[] = { SamService_Fragment.class, SamChats_Fragment.class,
			SamMe_Fragment.class, SamContact_Fragment.class };
		
	private int imageViewArray[] = { R.drawable.sam_services, R.drawable.sam_chats,
			R.drawable.sam_me, R.drawable.sam_public };
	
	private int textViewArray[] = {R.string.sam_services,
			                   R.string.sam_chats,
		                       R.string.sam_me,
	                           R.string.sam_contact};
	
	private List<Fragment> list = new ArrayList<Fragment>();
	private ViewPager vp;
	private static int currentTabPostition = 0;
	
	public static int getCurrentTab(){
		return currentTabPostition;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		currentTabPostition = 0;
		
		Intent intent = new Intent();
		intent.setAction(SamService.FINISH_ALL_SIGN_ACTVITY);
		sendBroadcast(intent);

		IntentFilter hide_show_filter = new IntentFilter();
		hide_show_filter.addAction(SamChats_Fragment.HIDE_SAMCHATS_REDPOINT);
		hide_show_filter.addAction(SamChats_Fragment.SHOW_SAMCHATS_REDPOINT);
		registerReceiver(HideShowSamChatRDPReceiver, hide_show_filter);
		
		setContentView(R.layout.main_tab_layout);

		initView();
		initPage();

		//setOverflowShowingAlways();

	}

	/**
	 * 控件初始化
	 */
	private void initView() {
		vp = (ViewPager) findViewById(R.id.pager);
		vp.setOnPageChangeListener(this);
		layoutInflater = LayoutInflater.from(this);
		mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
		mTabHost.setup(this, getSupportFragmentManager(), R.id.pager);
		mTabHost.setOnTabChangedListener(this);

		int count = textViewArray.length;

		for (int i = 0; i < count; i++) {
			TabSpec tabSpec = mTabHost.newTabSpec(getString(textViewArray[i]))
					.setIndicator(getTabItemView(i));
			mTabHost.addTab(tabSpec, fragmentArray[i], null);
			mTabHost.setTag(i);
		}
		
		mTabHost.getTabWidget().setDividerDrawable(null);
	}

	/**
	 * 初始化Fragment
	 */
	private void initPage() {
		SamService_Fragment fragment1 = new SamService_Fragment();
		SamChats_Fragment fragment2 = new SamChats_Fragment();
		fragment2.setConversationListItemClickListener(new EaseConversationListItemClickListener() {
			@Override
			public void onListItemClicked(EMConversation conversation) {
				startActivity(new Intent(MainActivity.this, ChatActivity.class).putExtra(EaseConstant.EXTRA_USER_ID, conversation.getUserName()));
			}
		});
		
		SamMe_Fragment fragment3 = new SamMe_Fragment();
		
		SamContact_Fragment fragment4 = new SamContact_Fragment();
		fragment4.setContactsMap(getContacts());
		fragment4.setContactListItemClickListener(new EaseContactListItemClickListener() {
			@Override
			public void onListItemClicked(EaseUser user) {
               		 startActivity(new Intent(MainActivity.this, ChatActivity.class).putExtra(EaseConstant.EXTRA_USER_ID, user.getUsername()));
			}
        	});
		
		list.add(fragment1);
		list.add(fragment2);
		list.add(fragment3);
		list.add(fragment4);
		vp.setAdapter(new MyFragmentAdapter(getSupportFragmentManager(), list));
		vp.setOffscreenPageLimit(3);

		SamService.getInstance().onActivityLaunched(fragment1,fragment2);
	}

	private Map<String, EaseUser> getContacts(){
		Map<String, EaseUser> contacts = new HashMap<String, EaseUser>();
		EaseUser user = new EaseUser("13911791236" );
		contacts.put("13911791236", user);

		user = new EaseUser("13810137846" );
		contacts.put("13810137846", user);
		
		return contacts;
	}


	private View getTabItemView(int i) {
		TextView tab_msgNum = null;
		ImageView tab_redSmallPoint = null;
		View view = layoutInflater.inflate(R.layout.tab_content, null);
		ImageView mImageView = (ImageView) view.findViewById(R.id.tab_imageview);
		TextView mTextView = (TextView) view.findViewById(R.id.tab_textview);
		mImageView.setImageResource(imageViewArray[i]);
		mTextView.setText(textViewArray[i]);
		
		switch(i){
		    case TAB_ID_SAMSERVICES:
		    	mTextView.setTextColor(android.graphics.Color.GREEN);
		    	break;
		    case TAB_ID_SAMCHATS:
		    	//tab_redSmallPoint = (ImageView) view.findViewById(R.id.tab_redSmallPoint);
		    	//tab_redSmallPoint.setVisibility(View.VISIBLE);
		    	break;
		    case TAB_ID_SAMME:
		    	//tab_msgNum = (TextView)view.findViewById(R.id.tab_msgNum);
		    	//tab_msgNum.setText("14");
		    	//tab_msgNum.setVisibility(View.VISIBLE);
		    	break;
		    case TAB_ID_SAMCONTACT:
		    	break;
		}
		
		
		return view;
	}



	@Override
	public void onPageScrollStateChanged(int arg0) {

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		if(getCurrentFocus()!=null){
			imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}

	private void updateTextColor(int arg0){
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
	}

	
	
	@Override
	public void onPageSelected(int arg0) {
		currentTabPostition  = arg0;
		SamLog.e(TAG,"currentTabPostition:"+currentTabPostition);
		TabWidget widget = mTabHost.getTabWidget();
		int oldFocusability = widget.getDescendantFocusability();
		widget.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
		mTabHost.setCurrentTab(arg0);
		widget.setDescendantFocusability(oldFocusability);
		updateTextColor(arg0);
		//updateRedPoint(arg0,false);
		//.setBackgroundResource(R.drawable.selector_tab_background);

		
	}

	@Override
	public void onTabChanged(String tabId) {
		int position = mTabHost.getCurrentTab();
		vp.setCurrentItem(position);
	}

	private BroadcastReceiver HideShowSamChatRDPReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if(intent.getAction().equals(SamChats_Fragment.HIDE_SAMCHATS_REDPOINT)){
				updateRedPoint(TAB_ID_SAMCHATS, false);
			}else if(intent.getAction().equals(SamChats_Fragment.SHOW_SAMCHATS_REDPOINT)){
				updateRedPoint(TAB_ID_SAMCHATS, true);
			}
	    }
	};
	
	
	/*@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onMenuOpened(int featureId, Menu menu) {
		if (featureId == Window.FEATURE_ACTION_BAR && menu != null) {
			if (menu.getClass().getSimpleName().equals("MenuBuilder")) {
				try {
					Method m = menu.getClass().getDeclaredMethod(
							"setOptionalIconsVisible", Boolean.TYPE);
					m.setAccessible(true);
					m.invoke(menu, true);
				} catch (Exception e) {
				}
			}
		}
		return super.onMenuOpened(featureId, menu);
	}
	
	private void setOverflowShowingAlways() {
		try {
			ViewConfiguration config = ViewConfiguration.get(this);
			Field menuKeyField = ViewConfiguration.class
					.getDeclaredField("sHasPermanentMenuKey");
			menuKeyField.setAccessible(true);
			menuKeyField.setBoolean(config, false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}*/
	
	
	
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
		SamService.getInstance().stopSamService();
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

	
	/*@Override 
    public boolean onKeyDown(int keyCode, KeyEvent event) {
		
		
        if (keyCode == KeyEvent.KEYCODE_BACK) { 
            exit(); 
            return true; 
        } 
        return super.onKeyDown(keyCode, event);
    } */
       
    private void exit() { 
        if (!isExit) { 
            isExit = true; 
            Toast.makeText(getApplicationContext(), getString(R.string.exit_app_confirmation), 
                    Toast.LENGTH_SHORT).show(); 
            mHandler.sendEmptyMessageDelayed(MSG_EXIT_ACTIVITY_TIMEOUT,ACTIVITY_TIMEOUT); 
        } else { 
             
            SamLog.e(TAG, "exit application"); 
               
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
	    	}
	    	
	    }
    };


	public void exitActivity(){
		SamLog.e(TAG, "exit main activity"); 
		this.finish();
	}
}
