package com.netease.nim.uikit;

import android.os.Handler;

import com.netease.nim.uikit.common.util.log.LogUtil;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.auth.AuthServiceObserver;
import com.netease.nimlib.sdk.auth.constant.LoginSyncStatus;

import java.util.ArrayList;
import java.util.List;

/**
 * ��¼
 * Created by huangjun on 2015/10/9.
 */
public class LoginSyncDataStatusObserver {

    private static final String TAG = LoginSyncDataStatusObserver.class.getSimpleName();

    private static final int TIME_OUT_SECONDS = 10;

    private Handler uiHandler;

    private Runnable timeoutRunnable;

    /**
     * ״̬
     */
    private LoginSyncStatus syncStatus = LoginSyncStatus.NO_BEGIN;

    /**
     * ����
     */
    private List<Observer<Void>> observers = new ArrayList<>();

    /**
     * ע��ʱ���״̬&����
     */
    public void reset() {
        syncStatus = LoginSyncStatus.NO_BEGIN;
        observers.clear();
    }

    /**
     * ��App����ʱ��SDKע���¼��ͬ�����ݹ���״̬��֪ͨ
     * ����ʱ����������Application onCreate��
     */
    public void registerLoginSyncDataStatus(boolean register) {
        LogUtil.i(TAG, "observe login sync data completed event on Application create");
        NIMClient.getService(AuthServiceObserver.class).observeLoginSyncDataStatus(new Observer<LoginSyncStatus>() {
            @Override
            public void onEvent(LoginSyncStatus status) {
                syncStatus = status;
                if (status == LoginSyncStatus.BEGIN_SYNC) {
                    LogUtil.i(TAG, "login sync data begin");
                } else if (status == LoginSyncStatus.SYNC_COMPLETED) {
                    LogUtil.i(TAG, "login sync data completed");
                    onLoginSyncDataCompleted(false);
                }
            }
        }, register);
    }

    /**
     * ������¼��ͬ����������¼������湹����ɺ��Զ�ȡ������
     * ����ʱ������¼�ɹ���
     *
     * @param observer �۲���
     * @return ����true��ʾ����ͬ���Ѿ���ɻ��߲�����ͬ��������false��ʾ����ͬ������
     */
    public boolean observeSyncDataCompletedEvent(Observer<Void> observer) {
        if (syncStatus == LoginSyncStatus.NO_BEGIN || syncStatus == LoginSyncStatus.SYNC_COMPLETED) {
            /*
            * NO_BEGIN �����¼��δ��ʼͬ�����ݣ���ô�������Զ���¼�����:
            * PUSH�����Ѿ���¼ͬ����������ˣ���ʱUI���������󲢲�֪��������ֱ����Ϊͬ�����
            */
            return true;
        }

        // ����ͬ��
        if (!observers.contains(observer)) {
            observers.add(observer);
        }

        // ��ʱ��ʱ��
        if (uiHandler == null) {
            uiHandler = new Handler(NimUIKit.getContext().getMainLooper());
        }

        if (timeoutRunnable == null) {
            timeoutRunnable = new Runnable() {
                @Override
                public void run() {
                    // �����ʱ�����ڿ�ʼͬ����״̬��ģ�����
                    if (syncStatus == LoginSyncStatus.BEGIN_SYNC) {
                        onLoginSyncDataCompleted(true);
                    }
                }
            };
        }

        uiHandler.removeCallbacks(timeoutRunnable);
        uiHandler.postDelayed(timeoutRunnable, TIME_OUT_SECONDS * 1000);

        return false;
    }

    /**
     * ��¼ͬ��������ɴ���
     */
    private void onLoginSyncDataCompleted(boolean timeout) {
        LogUtil.i(TAG, "onLoginSyncDataCompleted, timeout=" + timeout);

        // �Ƴ���ʱ�����п�����ɰ�������ʱ�򣬳�ʱ���񶼻�û������
        if (timeoutRunnable != null) {
            uiHandler.removeCallbacks(timeoutRunnable);
        }

        // ֪ͨ�ϲ�
        for (Observer<Void> o : observers) {
            o.onEvent(null);
        }

        // ����״̬
        reset();
    }


    /**
     * ����
     */
    public static LoginSyncDataStatusObserver getInstance() {
        return InstanceHolder.instance;
    }

    static class InstanceHolder {
        final static LoginSyncDataStatusObserver instance = new LoginSyncDataStatusObserver();
    }
}
