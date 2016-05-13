package com.netease.nim.uikit.sam;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.netease.nim.uikit.NimConstants;
import com.netease.nim.uikit.R;
import com.netease.nim.uikit.cache.FriendDataCache;
import com.netease.nim.uikit.cache.TeamDataCache;
import com.netease.nim.uikit.common.adapter.TAdapterDelegate;
import com.netease.nim.uikit.common.adapter.TViewHolder;
import com.netease.nim.uikit.common.fragment.TFragment;
import com.netease.nim.uikit.common.ui.dialog.CustomAlertDialog;
import com.netease.nim.uikit.common.ui.listview.ListViewUtil;
import com.netease.nim.uikit.recent.RecentContactsCallback;
import com.netease.nim.uikit.recent.viewholder.CommonRecentViewHolder;
import com.netease.nim.uikit.recent.viewholder.RecentContactAdapter;
import com.netease.nim.uikit.recent.viewholder.RecentViewHolder;
import com.netease.nim.uikit.recent.viewholder.TeamRecentViewHolder;
import com.netease.nim.uikit.uinfo.UserInfoHelper;
import com.netease.nim.uikit.uinfo.UserInfoObservable;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.RequestCallbackWrapper;
import com.netease.nimlib.sdk.ResponseCode;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.MsgServiceObserve;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.netease.nimlib.sdk.msg.model.RecentContact;
import com.netease.nimlib.sdk.team.model.Team;
import com.netease.nimlib.sdk.team.model.TeamMember;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.netease.nim.uikit.common.ui.dialog.CustomAlertDialog.onSeparateItemClickListener;

/**
 * �����ϵ���б�(�Ự�б�)
 * <p/>
 * Created by huangjun on 2015/2/1.
 */
public class ProsFragment extends TFragment implements TAdapterDelegate {

    // �ö����ܿ�ֱ��ʹ�ã�Ҳ����Ϊ˼·���������߳������RecentContact��tag�ֶ�
    public static final long RECENT_TAG_STICKY = 1; // ��ϵ���ö�tag\
    
    // view
    private ListView listView;

    private View emptyBg;

    private TextView emptyHint;

    // data
    private List<RecentContact> items;

    private RecentContactAdapter adapter;

    private boolean msgLoaded = false;

    private RecentContactsCallback callback;

    private UserInfoObservable.UserInfoObserver userInfoObserver;

	private boolean isBroadcastRegistered = false;
	private BroadcastReceiver broadcastReceiver;
	private LocalBroadcastManager broadcastManager;

	private void registerBroadcastReceiver() {
		broadcastManager = LocalBroadcastManager.getInstance(getActivity());
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(NimConstants.ACTION_PROS_UPDATE);

		broadcastReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				RecentContact conversation = (RecentContact)intent.getSerializableExtra("new_conversation");
				int index = -1;
                		for (int i = 0; i < items.size(); i++) {
                    		if (conversation.getContactId().equals(items.get(i).getContactId())
                            		&& conversation.getSessionType() == (items.get(i).getSessionType())) {
                        			index = i;
                        			break;
                    		}
                		}

               	 	if (index >= 0) {
                    		items.remove(index);
                		}

                		items.add(conversation);
				refreshMessages(true);
            		}
		};
		
		broadcastManager.registerReceiver(broadcastReceiver, intentFilter);
	}
		

	
	private void unregisterBroadcastReceiver(){
	    broadcastManager.unregisterReceiver(broadcastReceiver);
	}

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        findViews();
        initMessageList();
        requestMessages(true);
        registerObservers(true);
        if(!isBroadcastRegistered){
            registerBroadcastReceiver();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.nim_recent_contacts, container, false);
    }

    @Override
    public Class<? extends TViewHolder> viewHolderAtPosition(int position) {
        SessionTypeEnum type = items.get(position).getSessionType();
        if (type == SessionTypeEnum.Team) {
            return TeamRecentViewHolder.class;
        } else {
            return CommonRecentViewHolder.class;
        }
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public boolean enabled(int position) {
        return true;
    }

    private void notifyDataSetChanged() {
        adapter.notifyDataSetChanged();
	 /*SAMC_BEGIN()*/
        //boolean empty = items.isEmpty() && msgLoaded;
        //emptyBg.setVisibility(empty ? View.VISIBLE : View.GONE);
        //emptyHint.setHint("��û�лỰ����ͨѶ¼���Ҹ������İɣ�");
	 /*SAMC_END()*/
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        registerObservers(false);
        if(isBroadcastRegistered){
            unregisterBroadcastReceiver();
        }
    }

    /**
     * ����ҳ��ؼ�
     */
    private void findViews() {
        listView = findView(R.id.lvMessages);
        emptyBg = findView(R.id.emptyBg);
        emptyHint = findView(R.id.message_list_empty_hint);
    }

    /**
     * ��ʼ����Ϣ�б�
     */
    private void initMessageList() {
        items = new ArrayList<>();

        adapter = new RecentContactAdapter(getActivity(), items, this);
        adapter.setCallback(callback);

        listView.setAdapter(adapter);
        listView.setItemsCanFocus(true);
        listView.setOnItemClickListener(new OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (callback != null) {
                    RecentContact recent = (RecentContact) parent.getAdapter().getItem(position);
                    callback.onItemClick(recent);
                }
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (position < listView.getHeaderViewsCount()) {
                    return false;
                }
                showLongClickMenu((RecentContact) parent.getAdapter().getItem(position));

                return true;
            }
        });
        listView.setOnScrollListener(new OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                adapter.onMutable(scrollState == SCROLL_STATE_FLING);
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
    }

    private void showLongClickMenu(final RecentContact recent) {
        CustomAlertDialog alertDialog = new CustomAlertDialog(getActivity());
        alertDialog.setTitle(UserInfoHelper.getUserTitleName(recent.getContactId(), recent.getSessionType()));
        String title = getString(R.string.main_msg_list_delete_chatting);
        alertDialog.addItem(title, new onSeparateItemClickListener() {
            @Override
            public void onClick() {
                // ɾ��Ự��ɾ�����Ϣ��ʷ��һ��ɾ��
                NIMClient.getService(MsgService.class).deleteRecentContact(recent);
                NIMClient.getService(MsgService.class).clearChattingHistory(recent.getContactId(), recent.getSessionType());
                items.remove(recent);

                if (recent.getUnreadCount() > 0) {
                    refreshMessages(true);
                } else {
                    notifyDataSetChanged();
                }
            }
        });

        title = (isTagSet(recent, RECENT_TAG_STICKY) ? getString(R.string.main_msg_list_clear_sticky_on_top) : getString(R.string.main_msg_list_sticky_on_top));
        alertDialog.addItem(title, new onSeparateItemClickListener() {
            @Override
            public void onClick() {
                if (isTagSet(recent, RECENT_TAG_STICKY)) {
                    removeTag(recent, RECENT_TAG_STICKY);
                } else {
                    addTag(recent, RECENT_TAG_STICKY);
                }
                NIMClient.getService(MsgService.class).updateRecent(recent);

                refreshMessages(false);
            }
        });
        alertDialog.show();
    }

    private void addTag(RecentContact recent, long tag) {
        tag = recent.getTag() | tag;
        recent.setTag(tag);
    }

    private void removeTag(RecentContact recent, long tag) {
        tag = recent.getTag() & ~tag;
        recent.setTag(tag);
    }

    private boolean isTagSet(RecentContact recent, long tag) {
        return (recent.getTag() & tag) == tag;
    }

    private List<RecentContact> loadedRecents;

    private void requestMessages(boolean delay) {
        if (msgLoaded) {
            return;
        }
        getHandler().postDelayed(new Runnable() {

            @Override
            public void run() {
                if (msgLoaded) {
                    return;
                }
                // ��ѯ�����ϵ���б����
                NIMClient.getService(MsgService.class).queryRecentContacts().setCallback(new RequestCallbackWrapper<List<RecentContact>>() {

                    @Override
                    public void onResult(int code, List<RecentContact> recents, Throwable exception) {
                        if (code != ResponseCode.RES_SUCCESS || recents == null) {
                            return;
                        }

                        List<RecentContact> pros_recents = new ArrayList<RecentContact>();
			    for(RecentContact result:recents){
                            if((result.getTag() & NimConstants.RECENT_TAG_SHOW_IN_PROS)!=0){
                                pros_recents.add(result);
				 }
			    }

			    loadedRecents = pros_recents;

                        // �˴�����ǽ���ճ�ʼ����Ϊ�˷�ֹ���濨�٣������ں�̨����Ҫ��ʾ���û����Ϻ�Ⱥ�������ں�̨���غã�Ȼ����ˢ�½���
                        //
                        msgLoaded = true;
                        if (isAdded()) {
                            onRecentContactsLoaded();
                        }
                    }
                });
            }
        }, delay ? 250 : 0);
    }

    private void onRecentContactsLoaded() {
        items.clear();
        if (loadedRecents != null) {
            items.addAll(loadedRecents);
            loadedRecents = null;
        }
        refreshMessages(true);

        if (callback != null) {
            callback.onRecentContactsLoaded();
        }
    }

    private void refreshMessages(boolean unreadChanged) {
        sortRecentContacts(items);
        notifyDataSetChanged();

        if (unreadChanged) {

            // ��ʽһ���ۼ�ÿ�������ϵ�˵�δ�����죩
            
            int unreadNum = 0;
            for (RecentContact r : items) {
                unreadNum += r.getUnreadCount();
            }
            

            // ��ʽ����ֱ�Ӵ�SDK��ȡ�������
            /*int unreadNum = NIMClient.getService(MsgService.class).getTotalUnreadCount();*/

            if (callback != null) {
                callback.onUnreadCountChange(unreadNum);
            }
        }
    }

    /**
     * **************************** ���� ***********************************
     */
    private void sortRecentContacts(List<RecentContact> list) {
        if (list.size() == 0) {
            return;
        }
        Collections.sort(list, comp);
    }

    private static Comparator<RecentContact> comp = new Comparator<RecentContact>() {

        @Override
        public int compare(RecentContact o1, RecentContact o2) {
            // �ȱȽ��ö�tag
            long sticky = (o1.getTag() & RECENT_TAG_STICKY) - (o2.getTag() & RECENT_TAG_STICKY);
            if (sticky != 0) {
                return sticky > 0 ? -1 : 1;
            } else {
                long time = o1.getTime() - o2.getTime();
                return time == 0 ? 0 : (time > 0 ? -1 : 1);
            }
        }
    };

    /**
     * ********************** ����Ϣ������״̬�仯 ************************
     */

   
    private void registerObservers(boolean register) {
        MsgServiceObserve service = NIMClient.getService(MsgServiceObserve.class);
	 /*SAMC_BEGIN()*/
        service.observeRecentContact(messageObserver, register);
        service.observeMsgStatus(statusObserver, register);
        service.observeRecentContactDeleted(deleteObserver, register);
	 /*SAMC_END()*/
    }

    Observer<List<RecentContact>> messageObserver = new Observer<List<RecentContact>>() {
        @Override
        public void onEvent(List<RecentContact> messages) {
            int index;
            for (RecentContact msg : messages) {
                index = -1;
                for (int i = 0; i < items.size(); i++) {
                    if (msg.getContactId().equals(items.get(i).getContactId())
                            && msg.getSessionType() == (items.get(i).getSessionType())) {
                        index = i;
                        break;
                    }
                }

                if (index >= 0) {
                    items.remove(index);
			items.add(msg);
                }

            }

            refreshMessages(true);
        }
    };

    Observer<IMMessage> statusObserver = new Observer<IMMessage>() {
        @Override
        public void onEvent(IMMessage message) {
            int index = getItemIndex(message.getUuid());
            if (index >= 0 && index < items.size()) {
                RecentContact item = items.get(index);
                item.setMsgStatus(message.getStatus());
                refreshViewHolderByIndex(index);
            }
        }
    };

    Observer<RecentContact> deleteObserver = new Observer<RecentContact>() {
        @Override
        public void onEvent(RecentContact recentContact) {
            if (recentContact != null) {
                for (RecentContact item : items) {
                    if (TextUtils.equals(item.getContactId(), recentContact.getContactId())
                            && item.getSessionType() == recentContact.getSessionType()) {
                        items.remove(item);
                        refreshMessages(true);
                        break;
                    }
                }
            } else {
                items.clear();
                refreshMessages(true);
            }
        }
    };

   private int getItemIndex(String uuid) {
        for (int i = 0; i < items.size(); i++) {
            RecentContact item = items.get(i);
            if (TextUtils.equals(item.getRecentMessageId(), uuid)) {
                return i;
            }
        }

        return -1;
    }

    protected void refreshViewHolderByIndex(final int index) {
        getActivity().runOnUiThread(new Runnable() {

            @Override
            public void run() {
                Object tag = ListViewUtil.getViewHolderByIndex(listView, index);
                if (tag instanceof RecentViewHolder) {
                    RecentViewHolder viewHolder = (RecentViewHolder) tag;
                    viewHolder.refreshCurrentItem();
                }
            }
        });
    }

    public void setCallback(RecentContactsCallback callback) {
        this.callback = callback;
    }

}

