package com.netease.nim.uikit.cache;

import android.text.TextUtils;
import android.util.Log;

import com.netease.nim.uikit.NimUIKit;
import com.netease.nim.uikit.UIKitLogTag;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.RequestCallbackWrapper;
import com.netease.nimlib.sdk.ResponseCode;
import com.netease.nimlib.sdk.friend.model.Friend;
import com.netease.nimlib.sdk.uinfo.UserService;
import com.netease.nimlib.sdk.uinfo.UserServiceObserve;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * �û��������ݻ��棬�������û���ϵʹ�����������û������й�
 * ע�Ỻ����֪ͨ����ʹ��UserInfoHelper��registerObserver����
 * Created by huangjun on 2015/8/20.
 */
public class NimUserInfoCache {

    public static NimUserInfoCache getInstance() {
        return InstanceHolder.instance;
    }

    private Map<String, NimUserInfo> account2UserMap = new ConcurrentHashMap<>();

    private Map<String, List<RequestCallback<NimUserInfo>>> requestUserInfoMap = new ConcurrentHashMap<>(); // �ظ�������

    /**
     * ��������������
     */
    public void buildCache() {
        List<NimUserInfo> users = NIMClient.getService(UserService.class).getAllUserInfo();
        addOrUpdateUsers(users, false);
        LogUtil.i(UIKitLogTag.USER_CACHE, "build NimUserInfoCache completed, users count = " + account2UserMap.size());
    }

    public void clear() {
        clearUserCache();
    }

    /**
     * �����ŷ�������ȡ�û���Ϣ���ظ�������[�첽]
     */
    public void getUserInfoFromRemote(final String account, final RequestCallback<NimUserInfo> callback) {
        if (TextUtils.isEmpty(account)) {
            return;
        }

        if (requestUserInfoMap.containsKey(account)) {
            if (callback != null) {
                requestUserInfoMap.get(account).add(callback);
            }
            return; // �Ѿ��������У���Ҫ�ظ�����
        } else {
            List<RequestCallback<NimUserInfo>> cbs = new ArrayList<>();
            if (callback != null) {
                cbs.add(callback);
            }
            requestUserInfoMap.put(account, cbs);
        }

        List<String> accounts = new ArrayList<>(1);
        accounts.add(account);

        NIMClient.getService(UserService.class).fetchUserInfo(accounts).setCallback(new RequestCallbackWrapper<List<NimUserInfo>>() {

            @Override
            public void onResult(int code, List<NimUserInfo> users, Throwable exception) {
                NimUserInfo user = null;
                boolean hasCallback = requestUserInfoMap.get(account).size() > 0;
                if (code == ResponseCode.RES_SUCCESS && users != null && !users.isEmpty()) {
                    user = users.get(0);
                    // ���ﲻ��Ҫ���»��棬�ɼ����û����ϱ������ӣ������»���
                }

                // ����ص�
                if (hasCallback) {
                    List<RequestCallback<NimUserInfo>> cbs = requestUserInfoMap.get(account);
                    for (RequestCallback<NimUserInfo> cb : cbs) {
                        if (code == ResponseCode.RES_SUCCESS) {
                            cb.onSuccess(user);
                        } else {
                            cb.onFailed(code);
                        }
                    }
                }

                requestUserInfoMap.remove(account);
            }
        });
    }

    /**
     * �����ŷ�������ȡ�����û���Ϣ[�첽]
     */
    public void getUserInfoFromRemote(List<String> accounts, final RequestCallback<List<NimUserInfo>> callback) {
        NIMClient.getService(UserService.class).fetchUserInfo(accounts).setCallback(new RequestCallback<List<NimUserInfo>>() {
            @Override
            public void onSuccess(List<NimUserInfo> users) {
                Log.i(UIKitLogTag.USER_CACHE, "fetch userInfo completed, add users size =" + users.size());
                // ���ﲻ��Ҫ���»��棬�ɼ����û����ϱ������ӣ������»���
                if (callback != null) {
                    callback.onSuccess(users);
                }
            }

            @Override
            public void onFailed(int code) {
                if (callback != null) {
                    callback.onFailed(code);
                }
            }

            @Override
            public void onException(Throwable exception) {
                if (callback != null) {
                    callback.onException(exception);
                }
            }
        });
    }

    /**
     * ******************************* ҵ��ӿڣ���ȡ������û���Ϣ�� *********************************
     */

    public List<NimUserInfo> getAllUsersOfMyFriend() {
        List<String> accounts = FriendDataCache.getInstance().getMyFriendAccounts();
        List<NimUserInfo> users = new ArrayList<>();
        List<String> unknownAccounts = new ArrayList<>();
        for (String account : accounts) {
            if (hasUser(account)) {
                users.add(getUserInfo(account));
            } else {
                unknownAccounts.add(account);
            }
        }

        // fetch unknown userInfo���������ᷢ�����ٴν�������У�飬����ɾȥ
        if (!unknownAccounts.isEmpty()) {
            DataCacheManager.Log(unknownAccounts, "lack friend userInfo", UIKitLogTag.USER_CACHE);
            getUserInfoFromRemote(unknownAccounts, null);
        }

        return users;
    }

    public NimUserInfo getUserInfo(String account) {
        if (TextUtils.isEmpty(account) || account2UserMap == null) {
            LogUtil.e(UIKitLogTag.USER_CACHE, "getUserInfo null, account=" + account + ", account2UserMap=" + account2UserMap);
            return null;
        }

        return account2UserMap.get(account);
    }

    public boolean hasUser(String account) {
        if (TextUtils.isEmpty(account) || account2UserMap == null) {
            LogUtil.e(UIKitLogTag.USER_CACHE, "hasUser null, account=" + account + ", account2UserMap=" + account2UserMap);
            return false;
        }

        return account2UserMap.containsKey(account);
    }

    /**
     * ��ȡ�û���ʾ���ơ�
     * �������˱�ע��������ʾ��ע����
     * ��û�����ñ�ע�����û����ǳ�����ʾ�ǳƣ��û�û���ǳ�����ʾ�ʺš�
     *
     * @param account �û��ʺ�
     * @return
     */
    public String getUserDisplayName(String account) {
        String alias = getAlias(account);
        if (!TextUtils.isEmpty(alias)) {
            return alias;
        }

        return getUserName(account);
    }

    public String getAlias(String account) {
        Friend friend = FriendDataCache.getInstance().getFriendByAccount(account);
        if (friend != null && !TextUtils.isEmpty(friend.getAlias())) {
            return friend.getAlias();
        }
        return null;
    }

    // ��ȡ�û�ԭ�����ǳ�
    public String getUserName(String account) {
        NimUserInfo userInfo = getUserInfo(account);
        if (userInfo != null && !TextUtils.isEmpty(userInfo.getName())) {
            return userInfo.getName();
        } else {
            return account;
        }
    }

    public String getUserDisplayNameEx(String account) {
        if (account.equals(NimUIKit.getAccount())) {
            return "��";
        }

        return getUserDisplayName(account);
    }

    public String getUserDisplayNameYou(String account) {
        if (account.equals(NimUIKit.getAccount())) {
            return "��";  // ��Ϊ�û��Լ�����ʾ���㡱
        }

        return getUserDisplayName(account);
    }

    private void clearUserCache() {
        account2UserMap.clear();
    }

    /**
     * ************************************ �û����ϱ������(����SDK) *****************************************
     */

    /**
     * ��Application��onCreate����SDKע���û����ϱ���۲���
     */
    public void registerObservers(boolean register) {
        NIMClient.getService(UserServiceObserve.class).observeUserInfoUpdate(userInfoUpdateObserver, register);
    }

    private Observer<List<NimUserInfo>> userInfoUpdateObserver = new Observer<List<NimUserInfo>>() {
        @Override
        public void onEvent(List<NimUserInfo> users) {
            if (users == null || users.isEmpty()) {
                return;
            }

            addOrUpdateUsers(users, true);
        }
    };

    /**
     * *************************************** User�����������֪ͨ ********************************************
     */

    private void addOrUpdateUsers(final List<NimUserInfo> users, boolean notify) {
        if (users == null || users.isEmpty()) {
            return;
        }

        // update cache
        for (NimUserInfo u : users) {
            account2UserMap.put(u.getAccount(), u);
        }

        // log
        List<String> accounts = getAccounts(users);
        DataCacheManager.Log(accounts, "on userInfo changed", UIKitLogTag.USER_CACHE);

        // ֪ͨ���
        if (notify && accounts != null && !accounts.isEmpty()) {
            NimUIKit.notifyUserInfoChanged(accounts); // ֪ͨ��UI���
        }
    }

    private List<String> getAccounts(List<NimUserInfo> users) {
        if (users == null || users.isEmpty()) {
            return null;
        }

        List<String> accounts = new ArrayList<>(users.size());
        for (NimUserInfo user : users) {
            accounts.add(user.getAccount());
        }

        return accounts;
    }

    /**
     * ************************************ ���� **********************************************
     */

    static class InstanceHolder {
        final static NimUserInfoCache instance = new NimUserInfoCache();
    }
}
