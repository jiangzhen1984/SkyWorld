package com.android.samchat;

import java.util.List;

import org.apache.harmony.javax.security.auth.Refreshable;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.samservice.Constants;
import com.easemob.EMValueCallBack;
import com.easemob.chat.EMGroup;
import com.easemob.chat.EMGroupManager;
import com.easemob.easeui.ui.EaseGroupRemoveListener;
//import com.easemob.chatuidemo.Constant;
//import com.easemob.chatuidemo.adapter.GroupAdapter;
import com.easemob.util.EMLog;

public class GroupsActivity extends Activity {
	public static final String TAG = "GroupsActivity";
	private ListView groupListView;
	protected List<EMGroup> grouplist;
	private GroupAdapter groupAdapter;
	private InputMethodManager inputMethodManager;
	public static GroupsActivity instance;
	private View progressBar;
	private AutoNormalSwipeRefreshLayout swipeRefreshLayout;
	private ImageView mBack;
	private GroupRemoveListener groupRemoveListener;
	
	
	Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			if(GroupsActivity.this == null ||GroupsActivity.this.isFinishing() ){
				return;
			}

			swipeRefreshLayout.setRefreshing(false);

			switch (msg.what) {
				case 0:
					refresh();
				break;
				case 1:
					Toast.makeText(GroupsActivity.this, R.string.Failed_to_get_group_chat_information, Toast.LENGTH_LONG).show();
				break;

				default:
				break;
			}
		};
	};

		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_groups);

		instance = this;
		inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		grouplist = EMGroupManager.getInstance().getAllGroups();
		groupListView = (ListView) findViewById(R.id.list);
		//show group list
		//groupAdapter = new GroupAdapter(this, 1, grouplist);
		//groupListView.setAdapter(groupAdapter);
		
		swipeRefreshLayout = (AutoNormalSwipeRefreshLayout) findViewById(R.id.swipe_layout);
		swipeRefreshLayout.setColorSchemeResources(R.color.holo_blue_bright, R.color.holo_green_light,
		                R.color.holo_orange_light, R.color.holo_red_light);
		//下拉刷新
		swipeRefreshLayout.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
			    EMGroupManager.getInstance().asyncGetGroupsFromServer(new EMValueCallBack<List<EMGroup>>() {
                    
                    @Override
                    public void onSuccess(List<EMGroup> value) {
                        handler.sendEmptyMessage(0);
                    }
                    
                    @Override
                    public void onError(int error, String errorMsg) {
                        handler.sendEmptyMessage(1);
                    }
                });
			}
		});
		
		groupListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (position == 0) {
					// 新建群聊
					startActivityForResult(new Intent(GroupsActivity.this, NewGroupActivity.class), 0);
				} else {
					// 进入群聊
					Intent intent = new Intent(GroupsActivity.this, ChatActivity.class);
					// it is group chat
					intent.putExtra("chatType", Constants.CHATTYPE_GROUP);
					intent.putExtra("userId", groupAdapter.getItem(position - 1).getGroupId());
					startActivityForResult(intent, 0);
				}
			}

		});
		groupListView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
					if (getCurrentFocus() != null)
						inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
								InputMethodManager.HIDE_NOT_ALWAYS);
				}
				return false;
			}
		});


		mBack =  (ImageView) findViewById(R.id.back);
		mBack.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});

		groupRemoveListener = new GroupRemoveListener();
		EMGroupManager.getInstance().addGroupChangeListener(groupRemoveListener);

		swipeRefreshLayout.autoRefresh(); 
		
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		swipeRefreshLayout.autoRefresh();
	}

	@Override
	public void onResume() {
		super.onResume();
	}
	
	private void refresh(){
		grouplist = EMGroupManager.getInstance().getAllGroups();
		groupAdapter = new GroupAdapter(this, 1, grouplist);
		groupListView.setAdapter(groupAdapter);
		groupAdapter.notifyDataSetChanged();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		instance = null;
	}


	private class GroupRemoveListener extends EaseGroupRemoveListener {

	@Override
	public void onInvitationReceived(String groupId, String groupName, String inviter, String reason) {
		//handler.sendEmptyMessage(0);
		runOnUiThread(new Runnable() {
			public void run() {
				swipeRefreshLayout.autoRefresh(); 
			}
		});
	}

	@Override
	public void onApplicationReceived(String groupId, String groupName, String applyer, String reason) {
		//handler.sendEmptyMessage(0);
		runOnUiThread(new Runnable() {
			public void run() {
				swipeRefreshLayout.autoRefresh(); 
			}
		});
	}

	@Override
	public void onApplicationAccept(String groupId, String groupName, String accepter) {
		//handler.sendEmptyMessage(0);
		runOnUiThread(new Runnable() {
			public void run() {
				swipeRefreshLayout.autoRefresh(); 
			}
		});
        
	}

	@Override
	public void onApplicationDeclined(String groupId, String groupName, String decliner, String reason) {
		//handler.sendEmptyMessage(0);
		runOnUiThread(new Runnable() {
			public void run() {
				swipeRefreshLayout.autoRefresh(); 
			}
		});
	}

	@Override
	public void onInvitationAccpted(String groupId, String inviter, String reason) {
		//handler.sendEmptyMessage(0);
		runOnUiThread(new Runnable() {
			public void run() {
				swipeRefreshLayout.autoRefresh(); 
			}
		});
	}

	@Override
	public void onInvitationDeclined(String groupId, String invitee, String reason) {
		//handler.sendEmptyMessage(0);
		runOnUiThread(new Runnable() {
			public void run() {
				swipeRefreshLayout.autoRefresh(); 
			}
		});
	}

	@Override
	public void onUserRemoved(final String groupId, String groupName) {
             //handler.sendEmptyMessage(0);
		runOnUiThread(new Runnable() {
			public void run() {
				swipeRefreshLayout.autoRefresh(); 
			}
		});
	}

	@Override
	public void onGroupDestroy(final String groupId, String groupName) {
		//handler.sendEmptyMessage(0);
		runOnUiThread(new Runnable() {
			public void run() {
				swipeRefreshLayout.autoRefresh(); 
			}
		});
	}

    }

	

}

