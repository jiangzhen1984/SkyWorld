package com.netease.nim.uikit.cache;

import android.text.TextUtils;

import com.netease.nim.uikit.NimUIKit;
import com.netease.nim.uikit.UIKitLogTag;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.friend.FriendService;
import com.netease.nimlib.sdk.friend.FriendServiceObserve;
import com.netease.nimlib.sdk.friend.model.BlackListChangedNotify;
import com.netease.nimlib.sdk.friend.model.Friend;
import com.netease.nimlib.sdk.friend.model.FriendChangedNotify;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;


/**
 * ���ѹ�ϵ����
 * ע�⣺��ȡͨѶ¼�б��Ǹ���Friend�б��ʺţ�ȥȡ��Ӧ��UserInfo
 * <p/>
 * Created by huangjun on 2015/9/14.
 */
public class FriendDataCache {

    public static FriendDataCache getInstance() {
        return InstanceHolder.instance;
    }

    /**
     * ����
     */
    private Set<String> friendAccountSet = new CopyOnWriteArraySet<>();

    private Map<String, Friend> friendMap = new ConcurrentHashMap<>();

    private List<FriendDataChangedObserver> friendObservers = new ArrayList<>();

    /**
     * ��ʼ��&����
     */

    public void clear() {
        clearFriendCache();
    }

    public void buildCache() {
        // ��ȡ�����еĺ��ѹ�ϵ
        List<Friend> friends = NIMClient.getService(FriendService.class).getFriends();
        for (Friend f : friends) {
            friendMap.put(f.getAccount(), f);
        }

        // ��ȡ�����к��ѵ��ʺ�
        List<String> accounts = NIMClient.getService(FriendService.class).getFriendAccounts();
        if (accounts == null || accounts.isEmpty()) {
            return;
        }

        // �ų�������
        List<String> blacks = NIMClient.getService(FriendService.class).getBlackList();
        accounts.removeAll(blacks);

        // �ų����Լ�
        accounts.remove(NimUIKit.getAccount());

        // ȷ������
        friendAccountSet.addAll(accounts);

        LogUtil.i(UIKitLogTag.FRIEND_CACHE, "build FriendDataCache completed, friends count = " + friendAccountSet.size());
    }

    private void clearFriendCache() {
        friendAccountSet.clear();
        friendMap.clear();
    }

    /**
     * ****************************** ���Ѳ�ѯ�ӿ� ******************************
     */

    public List<String> getMyFriendAccounts() {
        List<String> accounts = new ArrayList<>(friendAccountSet.size());
        accounts.addAll(friendAccountSet);

        return accounts;
    }

    public int getMyFriendCounts() {
        return friendAccountSet.size();
    }

    public Friend getFriendByAccount(String account) {
        if (TextUtils.isEmpty(account)) {
            return null;
        }

        return friendMap.get(account);
    }

    public boolean isMyFriend(String account) {
        return friendAccountSet.contains(account);
    }

    /**
     * ****************************** ������ѹ�ϵ�������&֪ͨ ******************************
     */

    /**
     * �������SDK
     */
    public void registerObservers(boolean register) {
        NIMClient.getService(FriendServiceObserve.class).observeFriendChangedNotify(friendChangedNotifyObserver, register);
        NIMClient.getService(FriendServiceObserve.class).observeBlackListChangedNotify(blackListChangedNotifyObserver, register);
    }

    /**
     * APP��������
     */
    public void registerFriendDataChangedObserver(FriendDataChangedObserver o, boolean register) {
        if (o == null) {
            return;
        }

        if (register) {
            if (!friendObservers.contains(o)) {
                friendObservers.add(o);
            }
        } else {
            friendObservers.remove(o);
        }
    }

    public interface FriendDataChangedObserver {
        void onAddedOrUpdatedFriends(List<String> accounts);

        void onDeletedFriends(List<String> accounts);

        void onAddUserToBlackList(List<String> account);

        void onRemoveUserFromBlackList(List<String> account);
    }

    /**
     * �������ѹ�ϵ�仯
     */
    private Observer<FriendChangedNotify> friendChangedNotifyObserver = new Observer<FriendChangedNotify>() {
        @Override
        public void onEvent(FriendChangedNotify friendChangedNotify) {
            List<Friend> addedOrUpdatedFriends = friendChangedNotify.getAddedOrUpdatedFriends();
            List<String> myFriendAccounts = new ArrayList<>(addedOrUpdatedFriends.size());
            List<String> friendAccounts = new ArrayList<>(addedOrUpdatedFriends.size());
            List<String> deletedFriendAccounts = friendChangedNotify.getDeletedFriends();

            // ����ں������У���ô���ӵ������б���
            String account;
            for (Friend f : addedOrUpdatedFriends) {
                account = f.getAccount();
                friendMap.put(account, f);
                friendAccounts.add(account);

                if (NIMClient.getService(FriendService.class).isInBlackList(account)) {
                    continue;
                }

                myFriendAccounts.add(account);
            }

            // �����ҵĺ��ѹ�ϵ
            if (!myFriendAccounts.isEmpty()) {
                // update cache
                friendAccountSet.addAll(myFriendAccounts);

                // log
                DataCacheManager.Log(myFriendAccounts, "on add friends", UIKitLogTag.FRIEND_CACHE);
            }

            // ֪ͨ���ѹ�ϵ����
            if (!friendAccounts.isEmpty()) {
                for (FriendDataChangedObserver o : friendObservers) {
                    o.onAddedOrUpdatedFriends(friendAccounts);
                }
            }

            // ����ɾ���ĺ��ѹ�ϵ
            if (!deletedFriendAccounts.isEmpty()) {
                // update cache
                friendAccountSet.removeAll(deletedFriendAccounts);

                for (String a : deletedFriendAccounts) {
                    friendMap.remove(a);
                }

                // log
                DataCacheManager.Log(deletedFriendAccounts, "on delete friends", UIKitLogTag.FRIEND_CACHE);

                // notify
                for (FriendDataChangedObserver o : friendObservers) {
                    o.onDeletedFriends(deletedFriendAccounts);
                }
            }
        }
    };

    /**
     * �����������仯(�����Ƿ��������Ƴ������б�)
     */
    private Observer<BlackListChangedNotify> blackListChangedNotifyObserver = new Observer<BlackListChangedNotify>() {
        @Override
        public void onEvent(BlackListChangedNotify blackListChangedNotify) {
            List<String> addedAccounts = blackListChangedNotify.getAddedAccounts();
            List<String> removedAccounts = blackListChangedNotify.getRemovedAccounts();

            if (!addedAccounts.isEmpty()) {
                // ���ڣ����Ӻ����������Ƴ�
                friendAccountSet.removeAll(addedAccounts);

                // log
                DataCacheManager.Log(addedAccounts, "on add users to black list", UIKitLogTag.FRIEND_CACHE);

                // notify
                for (FriendDataChangedObserver o : friendObservers) {
                    o.onAddUserToBlackList(addedAccounts);
                }

                // ���ڣ�Ҫ�������ϵ���б���ɾ���ú���
                for (String account : addedAccounts) {
                    NIMClient.getService(MsgService.class).deleteRecentContact2(account, SessionTypeEnum.P2P);
                }
            }

            if (!removedAccounts.isEmpty()) {
                // �Ƴ����������ж��Ƿ�����������
                for (String account : removedAccounts) {
                    if (NIMClient.getService(FriendService.class).isMyFriend(account)) {
                        friendAccountSet.add(account);
                    }
                }

                // log
                DataCacheManager.Log(removedAccounts, "on remove users from black list", UIKitLogTag.FRIEND_CACHE);

                // ֪ͨ�۲���
                for (FriendDataChangedObserver o : friendObservers) {
                    o.onRemoveUserFromBlackList(removedAccounts);
                }
            }
        }
    };

    /**
     * ************************************ ���� **********************************************
     */

    static class InstanceHolder {
        final static FriendDataCache instance = new FriendDataCache();
    }
}
