package com.netease.nim.demo.chatroom.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.netease.nim.demo.DemoCache;
import com.netease.nim.demo.R;
import com.netease.nim.demo.chatroom.activity.ChatRoomActivity;
import com.netease.nim.demo.chatroom.helper.ChatRoomMemberCache;
import com.netease.nim.demo.chatroom.viewholder.OnlinePeopleViewHolder;
import com.netease.nim.uikit.cache.SimpleCallback;
import com.netease.nim.uikit.common.adapter.TAdapter;
import com.netease.nim.uikit.common.adapter.TAdapterDelegate;
import com.netease.nim.uikit.common.adapter.TViewHolder;
import com.netease.nim.uikit.common.fragment.TFragment;
import com.netease.nim.uikit.common.ui.dialog.CustomAlertDialog;
import com.netease.nim.uikit.common.ui.ptr.PullToRefreshBase;
import com.netease.nim.uikit.common.ui.ptr.PullToRefreshListView;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.chatroom.ChatRoomService;
import com.netease.nimlib.sdk.chatroom.constant.MemberQueryType;
import com.netease.nimlib.sdk.chatroom.constant.MemberType;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomMember;
import com.netease.nimlib.sdk.chatroom.model.MemberOption;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ��������������fragment
 * Created by hzxuwen on 2015/12/17.
 */
public class OnlinePeopleFragment extends TFragment implements TAdapterDelegate {
    private static final String TAG = OnlinePeopleFragment.class.getSimpleName();
    private static final int LIMIT = 100;

    private PullToRefreshListView listView;
    private TextView onlineText;
    private TAdapter<ChatRoomMember> adapter;
    private List<ChatRoomMember> items = new ArrayList<>();
    private String roomId;
    private Map<String, ChatRoomMember> memberCache = new ConcurrentHashMap<>();
    private long updateTime = 0; // ���ο͵�updateTime
    private long enterTime = 0; // �ο͵�enterTime

    private boolean isNormalEmpty = false; // �̶���Ա�Ƿ���ȡ��

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.online_people_fragment, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initAdapter();
        findViews();
        registerObservers(true);
    }

    public void onCurrent() {
        clearCache();
        roomId = ((ChatRoomActivity) getActivity()).getRoomInfo().getRoomId();
        fetchData();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        registerObservers(false);
    }

    private void clearCache() {
        updateTime = 0;
        enterTime = 0;
        items.clear();
        memberCache.clear();
        isNormalEmpty = false;
    }

    private void initAdapter() {
        adapter = new TAdapter<>(getActivity(), items, this);
    }

    private void findViews() {
        onlineText = findView(R.id.no_online_people);
        listView = findView(R.id.chat_room_online_list);
        listView.setAdapter(adapter);
        listView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {

            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                fetchData();
            }
        });

//        listView.setOnItemLongClickListener(longClickListener); // ����������γ�Ա����
    }

    private void stopRefreshing() {
        postDelayed(new Runnable() {
            @Override
            public void run() {
                listView.onRefreshComplete();
            }
        }, 50);
    }

    /*************************** TAdapterDelegate **************************/
    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public Class<? extends TViewHolder> viewHolderAtPosition(int position) {
        return OnlinePeopleViewHolder.class;
    }

    @Override
    public boolean enabled(int position) {
        return true;
    }

    private void fetchData() {
        if (!isNormalEmpty) {
            // ��ȡ�̶����߳�Ա
            getMembers(MemberQueryType.ONLINE_NORMAL, updateTime, 0);
        } else {
            // ��ȡ�ǹ̶���Ա
            getMembers(MemberQueryType.GUEST, enterTime, 0);
        }
    }

    /**
     * ��ȡ��Ա�б�
     */
    private void getMembers(final MemberQueryType memberQueryType, final long time, int limit) {
        ChatRoomMemberCache.getInstance().fetchRoomMembers(roomId, memberQueryType, time, (LIMIT - limit), new SimpleCallback<List<ChatRoomMember>>() {
            @Override
            public void onResult(boolean success, List<ChatRoomMember> result) {
                if (success) {
                    if (onlineText.getVisibility() == View.VISIBLE || result == null || result.isEmpty()) {
                        onlineText.setVisibility(View.GONE);
                    }

                    addMembers(result);

                    if (memberQueryType == MemberQueryType.ONLINE_NORMAL && result.size() < LIMIT) {
                        isNormalEmpty = true; // �̶���Ա�Ѿ�����
                        getMembers(MemberQueryType.GUEST, enterTime, result.size());
                    }
                }

                stopRefreshing();
            }
        });
    }

    private void addMembers(List<ChatRoomMember> members) {
        for (ChatRoomMember member : members) {
            if (!isNormalEmpty) {
                updateTime = member.getUpdateTime();
            } else {
                enterTime = member.getEnterTime();
            }

            if (memberCache.containsKey(member.getAccount())) {
                items.remove(memberCache.get(member.getAccount()));
            }
            memberCache.put(member.getAccount(), member);

            items.add(member);
        }
        Collections.sort(items, comp);
        adapter.notifyDataSetChanged();
    }

    /**
     * *************************** ��Ա�������� ****************************
     */
    private void registerObservers(boolean register) {
        ChatRoomMemberCache.getInstance().registerRoomMemberChangedObserver(roomMemberChangedObserver, register);
    }

    ChatRoomMemberCache.RoomMemberChangedObserver roomMemberChangedObserver = new ChatRoomMemberCache.RoomMemberChangedObserver() {
        @Override
        public void onRoomMemberIn(ChatRoomMember member) {
        }

        @Override
        public void onRoomMemberExit(ChatRoomMember member) {
        }
    };


    /**************************** �����˵� *********************************/
    AdapterView.OnItemLongClickListener longClickListener = new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            fetchMemberInfo((ChatRoomMember) parent.getAdapter().getItem(position));
            return true;
        }
    };

    // �����˵�ǰ����ȡ��Ա��������
    private void fetchMemberInfo(final ChatRoomMember member) {
        ChatRoomMemberCache.getInstance().fetchMember(roomId, member.getAccount(), new SimpleCallback<ChatRoomMember>() {
            @Override
            public void onResult(boolean success, ChatRoomMember result) {
                if (success) {
                    showLongClickMenu(result);
                } else {
                    Toast.makeText(getActivity(), R.string.chatroom_fetch_member_failed, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // ��ʾ�����˵�
    private void showLongClickMenu(final ChatRoomMember currentMember) {
        // ���ܲ���������
        // 1���������û�������
        // 2�������������Լ�
        // 3���û��Լ�����ͨ��Ա
        // 4���û��Լ��������û�
        // 5���û��Լ����ο�
        if (currentMember.getMemberType() == MemberType.CREATOR
                || currentMember.getAccount().equals(DemoCache.getAccount())
                || ChatRoomMemberCache.getInstance().getChatRoomMember(roomId, DemoCache.getAccount()).getMemberType() == MemberType.NORMAL
                || ChatRoomMemberCache.getInstance().getChatRoomMember(roomId, DemoCache.getAccount()).getMemberType() == MemberType.LIMITED
                || ChatRoomMemberCache.getInstance().getChatRoomMember(roomId, DemoCache.getAccount()).getMemberType() == MemberType.GUEST) {
            return;
        }
        CustomAlertDialog alertDialog = new CustomAlertDialog(getActivity());
        alertDialog.addItem(R.string.chatroom_kick_member, new CustomAlertDialog.onSeparateItemClickListener() {
            @Override
            public void onClick() {
                kickMember(currentMember);
            }
        });

        // ����/ȡ������
        addMutedItem(currentMember, alertDialog);

        // ����/�Ƴ�������
        addBlackListItem(currentMember, alertDialog);

        // ����/ȡ������Ա
        addAdminItem(currentMember, alertDialog);

        // ��Ϊ/ȡ����ͨ��Ա
        addNormalItem(currentMember, alertDialog);

        alertDialog.show();
    }

    // ��ӽ��Բ˵�
    private void addMutedItem(final ChatRoomMember chatRoomMember, CustomAlertDialog alertDialog) {
        int titleId = chatRoomMember.isMuted() ? R.string.cancel_muted : R.string.chatroom_muted;
        alertDialog.addItem(titleId, new CustomAlertDialog.onSeparateItemClickListener() {
            @Override
            public void onClick() {
                setMuted(chatRoomMember);
            }
        });
    }

    // ��Ӻ������˵�
    private void addBlackListItem(final ChatRoomMember chatRoomMember, CustomAlertDialog alertDialog) {
        int titleId = chatRoomMember.isInBlackList() ? R.string.move_out_blacklist : R.string.chatroom_blacklist;
        alertDialog.addItem(titleId, new CustomAlertDialog.onSeparateItemClickListener() {
            @Override
            public void onClick() {
                setBlackList(chatRoomMember);
            }
        });
    }

    // ��ӹ���Ա�˵�
    private void addAdminItem(final ChatRoomMember chatRoomMember, CustomAlertDialog alertDialog) {
        // �������߱Ȳ�����Ȩ�޴�, �򷵻�
        if (chatRoomMember.getMemberType() == MemberType.ADMIN
                && ChatRoomMemberCache.getInstance().getChatRoomMember(roomId, DemoCache.getAccount()).getMemberType() != MemberType.CREATOR) {
            return;
        }
        final boolean isAdmin = chatRoomMember.getMemberType() == MemberType.ADMIN;
        int titleId = isAdmin ? R.string.cancel_admin : R.string.chatroom_set_admin;
        alertDialog.addItem(titleId, new CustomAlertDialog.onSeparateItemClickListener() {
            @Override
            public void onClick() {
                setAdmin(chatRoomMember, isAdmin);
            }
        });
    }

    private void addNormalItem(final ChatRoomMember chatRoomMember, CustomAlertDialog alertDialog) {
        final boolean isNormal = chatRoomMember.getMemberType() == MemberType.NORMAL;
        int titleId = isNormal ? R.string.cancel_normal_member : R.string.set_normal_member;
        alertDialog.addItem(titleId, new CustomAlertDialog.onSeparateItemClickListener() {
            @Override
            public void onClick() {
                setNormalMember(chatRoomMember, isNormal);
            }
        });
    }

    // ����
    private void kickMember(final ChatRoomMember chatRoomMember) {
        Map<String, Object> reason = new HashMap<>();
        reason.put("reason", "���ǲ�ˬ��");
        NIMClient.getService(ChatRoomService.class).kickMember(roomId, chatRoomMember.getAccount(), reason).setCallback(new RequestCallback<Void>() {
            @Override
            public void onSuccess(Void param) {
                Toast.makeText(getActivity(), R.string.chatroom_kick_member, Toast.LENGTH_SHORT).show();
                ChatRoomMember del = null;
                for (ChatRoomMember member : items) {
                    if (member.getAccount().equals(chatRoomMember.getAccount())) {
                        del = member;
                        break;
                    }
                }

                if (del != null) {
                    items.remove(del);
                    memberCache.remove(del.getAccount());
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailed(int code) {
                Log.d(TAG, "kick member failed:" + code);
            }

            @Override
            public void onException(Throwable exception) {
                Log.d(TAG, "kick member exception:" + exception);
            }
        });
    }

    // ����/ȡ������
    private void setMuted(final ChatRoomMember chatRoomMember) {
        MemberOption option = new MemberOption(roomId, chatRoomMember.getAccount());
        NIMClient.getService(ChatRoomService.class).markChatRoomMutedList(!chatRoomMember.isMuted(), option)
                .setCallback(new RequestCallback<ChatRoomMember>() {
                    @Override
                    public void onSuccess(ChatRoomMember param) {
                        Toast.makeText(getActivity(), R.string.set_success, Toast.LENGTH_SHORT).show();
                        refreshList(param, chatRoomMember);
                    }

                    @Override
                    public void onFailed(int code) {
                        Log.d(TAG, "set muted failed:" + code);
                    }

                    @Override
                    public void onException(Throwable exception) {

                    }
                });
    }

    // ����/�Ƴ�������
    private void setBlackList(ChatRoomMember chatRoomMember) {
        MemberOption option = new MemberOption(roomId, chatRoomMember.getAccount());
        NIMClient.getService(ChatRoomService.class).markChatRoomBlackList(!chatRoomMember.isInBlackList(), option)
                .setCallback(new RequestCallback<ChatRoomMember>() {
                    @Override
                    public void onSuccess(ChatRoomMember param) {
                        Toast.makeText(getActivity(), R.string.set_success, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailed(int code) {
                        Log.d(TAG, "set black list failed:" + code);
                    }

                    @Override
                    public void onException(Throwable exception) {

                    }
                });
    }

    // ����/ȡ������Ա
    private void setAdmin(final ChatRoomMember member, boolean isAdmin) {
        NIMClient.getService(ChatRoomService.class)
                .markChatRoomManager(!isAdmin, new MemberOption(roomId, member.getAccount()))
                .setCallback(new RequestCallback<ChatRoomMember>() {
                    @Override
                    public void onSuccess(ChatRoomMember param) {
                        Toast.makeText(getActivity(), R.string.set_success, Toast.LENGTH_SHORT).show();
                        refreshList(param, member);
                    }

                    @Override
                    public void onFailed(int code) {
                        Log.d(TAG, "set admin failed:" + code);
                    }

                    @Override
                    public void onException(Throwable exception) {

                    }
                });
    }

    // ����/ȡ����ͨ��Ա
    private void setNormalMember(final ChatRoomMember member, boolean isNormal) {
        NIMClient.getService(ChatRoomService.class).markNormalMember(!isNormal, new MemberOption(roomId, member.getAccount()))
                .setCallback(new RequestCallback<ChatRoomMember>() {
                    @Override
                    public void onSuccess(ChatRoomMember param) {
                        Toast.makeText(getActivity(), R.string.set_success, Toast.LENGTH_SHORT).show();
                        refreshList(param, member);
                    }

                    @Override
                    public void onFailed(int code) {

                    }

                    @Override
                    public void onException(Throwable exception) {

                    }
                });
    }

    private void refreshList(ChatRoomMember param, ChatRoomMember member) {
        ChatRoomMember temp = null;
        for (ChatRoomMember m : items) {
            if (m.getAccount().equals(param.getAccount())) {
                temp = m;
            }
        }
        member.setMemberType(param.getMemberType());
        items.remove(temp);
        items.add(member);
        Collections.sort(items, comp);
        adapter.notifyDataSetChanged();
    }

    private  static Map<MemberType, Integer> compMap = new HashMap<>();

    static {
        compMap.put(MemberType.CREATOR, 0);
        compMap.put(MemberType.ADMIN, 1);
        compMap.put(MemberType.NORMAL, 2);
        compMap.put(MemberType.LIMITED, 3);
        compMap.put(MemberType.GUEST, 4);
    }

    private static Comparator<ChatRoomMember> comp = new Comparator<ChatRoomMember>() {
        @Override
        public int compare(ChatRoomMember lhs, ChatRoomMember rhs) {
            if (lhs == null) {
                return 1;
            }

            if (rhs == null) {
                return -1;
            }

            return compMap.get(lhs.getMemberType()) - compMap.get(rhs.getMemberType());
        }
    };
}
