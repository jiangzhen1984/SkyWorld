package com.netease.nim.demo.main.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.netease.nim.demo.R;
import com.netease.nim.demo.main.adapter.SystemMessageAdapter;
import com.netease.nim.demo.main.viewholder.SystemMessageViewHolder;
import com.netease.nim.uikit.cache.NimUserInfoCache;
import com.netease.nim.uikit.common.activity.TActionBarActivity;
import com.netease.nim.uikit.common.adapter.TAdapterDelegate;
import com.netease.nim.uikit.common.adapter.TViewHolder;
import com.netease.nim.uikit.common.ui.dialog.CustomAlertDialog;
import com.netease.nim.uikit.common.ui.listview.AutoRefreshListView;
import com.netease.nim.uikit.common.ui.listview.ListViewUtil;
import com.netease.nim.uikit.common.ui.listview.MessageListView;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.RequestCallbackWrapper;
import com.netease.nimlib.sdk.ResponseCode;
import com.netease.nimlib.sdk.friend.FriendService;
import com.netease.nimlib.sdk.friend.model.AddFriendNotify;
import com.netease.nimlib.sdk.msg.SystemMessageObserver;
import com.netease.nimlib.sdk.msg.SystemMessageService;
import com.netease.nimlib.sdk.msg.constant.SystemMessageStatus;
import com.netease.nimlib.sdk.msg.constant.SystemMessageType;
import com.netease.nimlib.sdk.msg.model.SystemMessage;
import com.netease.nimlib.sdk.team.TeamService;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * ϵͳ��Ϣ���Ľ���
 * <p/>
 * Created by huangjun on 2015/3/18.
 */
public class SystemMessageActivity extends TActionBarActivity implements TAdapterDelegate,
        AutoRefreshListView.OnRefreshListener, SystemMessageViewHolder.SystemMessageListener {

    private static final boolean MERGE_ADD_FRIEND_VERIFY = false; // �Ƿ�Ҫ�ϲ��������룬ͬһ���û����������һ���������ݣ�Ĭ�ϲ��ϲ���

    private static final int LOAD_MESSAGE_COUNT = 10;

    // view
    private MessageListView listView;

    // adapter
    private SystemMessageAdapter adapter;
    private List<SystemMessage> items = new ArrayList<>();
    private Set<Long> itemIds = new HashSet<>();

    // db
    private boolean firstLoad = true;

    public static void start(Context context) {
        start(context, null, true);
    }

    public static void start(Context context, Intent extras, boolean clearTop) {
        Intent intent = new Intent();
        intent.setClass(context, SystemMessageActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        if (clearTop) {
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        }
        if (extras != null) {
            intent.putExtras(extras);
        }
        context.startActivity(intent);
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public Class<? extends TViewHolder> viewHolderAtPosition(int position) {
        return SystemMessageViewHolder.class;
    }

    @Override
    public boolean enabled(int position) {
        return false;
    }


    @Override
    public void onRefreshFromStart() {
    }

    @Override
    public void onRefreshFromEnd() {
        loadMessages(); // load old data
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.system_notification_message_activity);
        setTitle(R.string.verify_reminder);

        initAdapter();
        initListView();

        loadMessages(); // load old data
        registerSystemObserver(true);
    }

    @Override
    protected void onResume() {
        super.onResume();

        NIMClient.getService(SystemMessageService.class).resetSystemMessageUnreadCount();
    }

    @Override
    protected void onStop() {
        super.onStop();

        NIMClient.getService(SystemMessageService.class).resetSystemMessageUnreadCount();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        registerSystemObserver(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.notification_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.notification_menu_btn:
                deleteAllMessages();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initAdapter() {
        adapter = new SystemMessageAdapter(this, items, this, this);
    }

    private void initListView() {
        listView = (MessageListView) findViewById(R.id.messageListView);
        listView.setMode(AutoRefreshListView.Mode.END);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            listView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        }

        // adapter
        listView.setAdapter(adapter);

        // listener
        listView.setOnRefreshListener(this);
    }


    private int loadOffset = 0;
    private Set<String> addFriendVerifyRequestAccounts = new HashSet<>(); // ���͹�����������˺ţ���������ϲ��ã�

    /**
     * ������ʷ��Ϣ
     */
    public void loadMessages() {
        listView.onRefreshStart(AutoRefreshListView.Mode.END);
        boolean loadCompleted; // �Ƿ��Ѿ�������ɣ�����û��������or�Ѿ����㱾����������
        int validMessageCount = 0; // ʵ�ʼ��ص��������ų������˱��ϲ�����Ŀ��
        List<String> messageFromAccounts = new ArrayList<>(LOAD_MESSAGE_COUNT);

        while (true) {
            List<SystemMessage> temps = NIMClient.getService(SystemMessageService.class)
                    .querySystemMessagesBlock(loadOffset, LOAD_MESSAGE_COUNT);

            loadOffset += temps.size();
            loadCompleted = temps.size() < LOAD_MESSAGE_COUNT;

            int tempValidCount = 0;

            for (SystemMessage m : temps) {
                // ȥ��
                if (duplicateFilter(m)) {
                    continue;
                }

                // ͬһ���˺ŵĺ���������������һ��
                if (addFriendVerifyFilter(m)) {
                    continue;
                }

                // ������Ч��Ϣ
                items.add(m);
                tempValidCount++;
                if (!messageFromAccounts.contains(m.getFromAccount())) {
                    messageFromAccounts.add(m.getFromAccount());
                }

                // �ж��Ƿ�ﵽ������
                if (++validMessageCount >= LOAD_MESSAGE_COUNT) {
                    loadCompleted = true;
                    // �Ѿ�����Ҫ�󣬴�ʱ��Ҫ����loadOffset
                    loadOffset -= temps.size();
                    loadOffset += tempValidCount;

                    break;
                }
            }

            if (loadCompleted) {
                break;
            }
        }

        // ��������Դ��ˢ�½���
        refresh();

        boolean first = firstLoad;
        firstLoad = false;
        if (first) {
            ListViewUtil.scrollToPosition(listView, 0, 0); // ��һ�μ��غ���ʾ����
        }

        listView.onRefreshComplete(validMessageCount, LOAD_MESSAGE_COUNT, true);

        // �ռ�δ֪�û����ϵ��˺ż��ϲ���Զ�̻�ȡ
        collectAndRequestUnknownUserInfo(messageFromAccounts);
    }

    /**
     * ����Ϣ����
     */
    private void onIncomingMessage(final SystemMessage message) {
        // ͬһ���˺ŵĺ���������������һ��
        if (addFriendVerifyFilter(message)) {
            SystemMessage del = null;
            for (SystemMessage m : items) {
                if (m.getFromAccount().equals(message.getFromAccount()) && m.getType() == SystemMessageType.AddFriend) {
                    AddFriendNotify attachData = (AddFriendNotify) m.getAttachObject();
                    if (attachData != null && attachData.getEvent() == AddFriendNotify.Event.RECV_ADD_FRIEND_VERIFY_REQUEST) {
                        del = m;
                        break;
                    }
                }
            }

            if (del != null) {
                items.remove(del);
            }
        }

        loadOffset++;
        items.add(0, message);

        refresh();

        // �ռ�δ֪�û����ϵ��˺ż��ϲ���Զ�̻�ȡ
        List<String> messageFromAccounts = new ArrayList<>(1);
        messageFromAccounts.add(message.getFromAccount());
        collectAndRequestUnknownUserInfo(messageFromAccounts);
    }


    // ȥ��
    private boolean duplicateFilter(final SystemMessage msg) {
        if (itemIds.contains(msg.getMessageId())) {
            return true;
        }

        itemIds.add(msg.getMessageId());
        return false;
    }

    // ͬһ���˺ŵĺ���������������һ��
    private boolean addFriendVerifyFilter(final SystemMessage msg) {
        if (!MERGE_ADD_FRIEND_VERIFY) {
            return false; // ����ҪMERGE��������
        }

        if (msg.getType() != SystemMessageType.AddFriend) {
            return false; // ������
        }

        AddFriendNotify attachData = (AddFriendNotify) msg.getAttachObject();
        if (attachData == null) {
            return true; // ����
        }

        if (attachData.getEvent() != AddFriendNotify.Event.RECV_ADD_FRIEND_VERIFY_REQUEST) {
            return false; // ������
        }

        if (addFriendVerifyRequestAccounts.contains(msg.getFromAccount())) {
            return true; // ����
        }

        addFriendVerifyRequestAccounts.add(msg.getFromAccount());
        return false; // ������
    }

    // ����δ֪���û�����
    private void collectAndRequestUnknownUserInfo(List<String> messageFromAccounts) {
        List<String> unknownAccounts = new ArrayList<>();
        for (String account : messageFromAccounts) {
            if (!NimUserInfoCache.getInstance().hasUser(account)) {
                unknownAccounts.add(account);
            }
        }

        if (!unknownAccounts.isEmpty()) {
            requestUnknownUser(unknownAccounts);
        }
    }

    @Override
    public void onAgree(SystemMessage message) {
        onSystemNotificationDeal(message, true);
    }

    @Override
    public void onReject(SystemMessage message) {
        onSystemNotificationDeal(message, false);
    }

    @Override
    public void onLongPressed(SystemMessage message) {
        showLongClickMenu(message);
    }

    private void onSystemNotificationDeal(final SystemMessage message, final boolean pass) {
        RequestCallback callback = new RequestCallback<Void>() {
            @Override
            public void onSuccess(Void param) {
                onProcessSuccess(pass, message);
            }

            @Override
            public void onFailed(int code) {
                onProcessFailed(code, message);
            }

            @Override
            public void onException(Throwable exception) {

            }
        };
        if (message.getType() == SystemMessageType.TeamInvite) {
            if (pass) {
                NIMClient.getService(TeamService.class).acceptInvite(message.getTargetId(), message.getFromAccount()).setCallback(callback);
            } else {
                NIMClient.getService(TeamService.class).declineInvite(message.getTargetId(), message.getFromAccount(), "").setCallback(callback);
            }

        } else if (message.getType() == SystemMessageType.ApplyJoinTeam) {
            if (pass) {
                NIMClient.getService(TeamService.class).passApply(message.getTargetId(), message.getFromAccount()).setCallback(callback);
            } else {
                NIMClient.getService(TeamService.class).rejectApply(message.getTargetId(), message.getFromAccount(), "").setCallback(callback);
            }
        } else if (message.getType() == SystemMessageType.AddFriend) {
            NIMClient.getService(FriendService.class).ackAddFriendRequest(message.getFromAccount(), pass).setCallback(callback);
        }
    }

    private void onProcessSuccess(final boolean pass, SystemMessage message) {
        SystemMessageStatus status = pass ? SystemMessageStatus.passed : SystemMessageStatus.declined;
        NIMClient.getService(SystemMessageService.class).setSystemMessageStatus(message.getMessageId(),
                status);
        message.setStatus(status);
        refreshViewHolder(message);
    }

    private void onProcessFailed(final int code, SystemMessage message) {
        Toast.makeText(SystemMessageActivity.this, "failed, error code=" + code,
                Toast.LENGTH_LONG).show();
        if (code == 408) {
            return;
        }

        SystemMessageStatus status = SystemMessageStatus.expired;
        NIMClient.getService(SystemMessageService.class).setSystemMessageStatus(message.getMessageId(),
                status);
        message.setStatus(status);
        refreshViewHolder(message);
    }

    private void refresh() {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void refreshViewHolder(final SystemMessage message) {
        final long messageId = message.getMessageId();

        int index = -1;
        for (int i = 0; i < items.size(); i++) {
            SystemMessage item = items.get(i);
            if (messageId == item.getMessageId()) {
                index = i;
                break;
            }
        }

        if (index < 0) {
            return;
        }

        final int m = index;
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                if (m < 0) {
                    return;
                }

                Object tag = ListViewUtil.getViewHolderByIndex(listView, m);
                if (tag instanceof SystemMessageViewHolder) {
                    ((SystemMessageViewHolder) tag).refreshDirectly(message);
                }
            }
        });
    }

    private void registerSystemObserver(boolean register) {
        NIMClient.getService(SystemMessageObserver.class).observeReceiveSystemMsg(new Observer<SystemMessage>() {
            @Override
            public void onEvent(SystemMessage message) {
                onIncomingMessage(message);
            }
        }, register);
    }

    private void requestUnknownUser(List<String> accounts) {
        NimUserInfoCache.getInstance().getUserInfoFromRemote(accounts, new RequestCallbackWrapper<List<NimUserInfo>>() {
            @Override
            public void onResult(int code, List<NimUserInfo> users, Throwable exception) {
                if (code == ResponseCode.RES_SUCCESS) {
                    if (users != null && !users.isEmpty()) {
                        refresh();
                    }
                }
            }
        });
    }

    private void deleteAllMessages() {
        NIMClient.getService(SystemMessageService.class).clearSystemMessages();
        NIMClient.getService(SystemMessageService.class).resetSystemMessageUnreadCount();
        items.clear();
        refresh();
        Toast.makeText(SystemMessageActivity.this, R.string.clear_all_success, Toast.LENGTH_SHORT).show();
    }

    private void showLongClickMenu(final SystemMessage message) {
        CustomAlertDialog alertDialog = new CustomAlertDialog(this);
        alertDialog.setTitle(R.string.delete_tip);
        String title = getString(R.string.delete_system_message);
        alertDialog.addItem(title, new CustomAlertDialog.onSeparateItemClickListener() {
            @Override
            public void onClick() {
                deleteSystemMessage(message);
            }
        });
        alertDialog.show();
    }

    private void deleteSystemMessage(final SystemMessage message) {
        NIMClient.getService(SystemMessageService.class).deleteSystemMessage(message.getMessageId());
        items.remove(message);
        refresh();
        Toast.makeText(SystemMessageActivity.this, R.string.delete_success, Toast.LENGTH_SHORT).show();
    }
}
