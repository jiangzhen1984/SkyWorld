package com.netease.nim.uikit.cache;

import android.content.Context;
import android.os.Handler;

import com.netease.nim.uikit.NimUIKit;
import com.netease.nim.uikit.common.framework.NimSingleThreadExecutor;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.netease.nimlib.sdk.Observer;

import java.util.ArrayList;
import java.util.List;

/**
 * UIKit�������ݹ�����
 * <p/>
 * Created by huangjun on 2015/10/19.
 */
public class DataCacheManager {

    private static final String TAG = DataCacheManager.class.getSimpleName();

    /**
     * App��ʼ��ʱ��SDKע�����ݱ���۲���
     */
    public static void observeSDKDataChanged(boolean register) {
        FriendDataCache.getInstance().registerObservers(register);
        NimUserInfoCache.getInstance().registerObservers(register);
        TeamDataCache.getInstance().registerObservers(register);
    }

    /**
     * ���ػ��湹��(�첽)
     */
    public static void buildDataCacheAsync() {
        buildDataCacheAsync(null, null);
    }

    /**
     * ���ػ��湹��(�첽)
     */
    public static void buildDataCacheAsync(final Context context, final Observer<Void> buildCompletedObserver) {
        NimSingleThreadExecutor.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                buildDataCache();

                // callback
                if (context != null && buildCompletedObserver != null) {
                    new Handler(context.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            buildCompletedObserver.onEvent(null);
                        }
                    });
                }

                LogUtil.i(TAG, "build data cache completed");
            }
        });
    }

    /**
     * ���ػ��湹����ͬ����
     */
    //build the cache based on current db data,not including server data if sync not finished
    public static void buildDataCache() {
        // clear
        clearDataCache();

        // build user/friend/team data cache
        // db data could be new or old, sync observe will update the new information
        FriendDataCache.getInstance().buildCache();

        NimUserInfoCache.getInstance().buildCache();
        //team db data could be new or old, need team observe to update new info
        TeamDataCache.getInstance().buildCache();

        // build self avatar cache
        List<String> accounts = new ArrayList<>(1);
        accounts.add(NimUIKit.getAccount());
        NimUIKit.getImageLoaderKit().buildAvatarCache(accounts);
    }

    /**
     * ��ջ��棨ͬ����
     */
    public static void clearDataCache() {
        // clear user/friend/team data cache
        FriendDataCache.getInstance().clear();
        NimUserInfoCache.getInstance().clear();
        TeamDataCache.getInstance().clear();

        // clear avatar cache
        NimUIKit.getImageLoaderKit().clear();
    }

    /**
     * ����������ݱ����־
     */
    public static void Log(List<String> accounts, String event, String logTag) {
        StringBuilder sb = new StringBuilder();
        sb.append(event);
        sb.append(" : ");
        for (String account : accounts) {
            sb.append(account);
            sb.append(" ");
        }
        sb.append(", total size=" + accounts.size());

        LogUtil.i(logTag, sb.toString());
    }
}
