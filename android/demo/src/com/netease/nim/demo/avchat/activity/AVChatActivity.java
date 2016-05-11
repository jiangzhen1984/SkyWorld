package com.netease.nim.demo.avchat.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.netease.nim.demo.R;
import com.netease.nim.demo.avchat.AVChatNotification;
import com.netease.nim.demo.avchat.AVChatProfile;
import com.netease.nim.demo.avchat.AVChatUI;
import com.netease.nim.demo.avchat.constant.CallStateEnum;
import com.netease.nim.uikit.common.activity.TActivity;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.netease.nim.uikit.common.util.sys.NetworkUtil;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.auth.ClientType;
import com.netease.nimlib.sdk.avchat.AVChatManager;
import com.netease.nimlib.sdk.avchat.AVChatStateObserver;
import com.netease.nimlib.sdk.avchat.constant.AVChatEventType;
import com.netease.nimlib.sdk.avchat.constant.AVChatTimeOutEvent;
import com.netease.nimlib.sdk.avchat.constant.AVChatType;
import com.netease.nimlib.sdk.avchat.model.AVChatCalleeAckEvent;
import com.netease.nimlib.sdk.avchat.model.AVChatCommonEvent;
import com.netease.nimlib.sdk.avchat.model.AVChatControlEvent;
import com.netease.nimlib.sdk.avchat.model.AVChatData;
import com.netease.nimlib.sdk.avchat.model.AVChatOnlineAckEvent;

import java.io.File;

/**
 * ����Ƶ����
 * Created by hzxuwen on 2015/4/21.
 */
public class AVChatActivity extends TActivity implements AVChatUI.AVChatListener, AVChatStateObserver {
    // constant
    private static final String TAG = "AVChatActivity";
    private static final String KEY_IN_CALLING = "KEY_IN_CALLING";
    private static final String KEY_ACCOUNT = "KEY_ACCOUNT";
    private static final String KEY_CALL_TYPE = "KEY_CALL_TYPE";
    private static final String KEY_SOURCE = "source";
    private static final String KEY_CALL_CONFIG = "KEY_CALL_CONFIG";
    public static final String INTENT_ACTION_AVCHAT = "INTENT_ACTION_AVCHAT";

    /**
     * ���Թ㲥
     */
    public static final int FROM_BROADCASTRECEIVER = 0;
    /**
     * ���Է���
     */
    public static final int FROM_INTERNAL = 1;
    /**
     * ����֪ͨ��
     */
    public static final int FROM_NOTIFICATION = 2;
    /**
     * δ֪�����
     */
    public static final int FROM_UNKNOWN = -1;

    // data
    private AVChatUI avChatUI; // ����Ƶ�ܹ�����
    private AVChatData avChatData; // config for connect video server
    private int state; // calltype ��Ƶ����Ƶ
    private String receiverId; // �Է���account

    // state
    private boolean isUserFinish = false;
    private boolean mIsInComingCall = false;// is incoming call or outgoing call
    private boolean isCallEstablished = false; // �绰�Ƿ��ͨ
    private static boolean needFinish = true; // �������ȥ��δ��ͨʱ�����home������һ���Ҷ�ͨ��������������б�ָ�����finish
    private boolean hasOnpause = false; // �Ƿ���ͣ����Ƶ

    // notification
    private AVChatNotification notifier;

    public static void start(Context context, String account, int callType, int source) {
        needFinish = false;
        Intent intent = new Intent();
        intent.setClass(context, AVChatActivity.class);
        intent.putExtra(KEY_ACCOUNT, account);
        intent.putExtra(KEY_IN_CALLING, false);
        intent.putExtra(KEY_CALL_TYPE, callType);
        intent.putExtra(KEY_SOURCE, source);
        context.startActivity(intent);
    }

    /**
     * incoming call
     *
     * @param context
     */
    public static void launch(Context context, AVChatData config, int source) {
        needFinish = false;
        Intent intent = new Intent();
        intent.setClass(context, AVChatActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(KEY_CALL_CONFIG, config);
        intent.putExtra(KEY_IN_CALLING, true);
        intent.putExtra(KEY_SOURCE, source);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (needFinish || !checkSource()) {
            finish();
            return;
        }
        View root = LayoutInflater.from(this).inflate(R.layout.avchat_activity, null);
        setContentView(root);
        mIsInComingCall = getIntent().getBooleanExtra(KEY_IN_CALLING, false);
        avChatUI = new AVChatUI(this, root, this);
        if (!avChatUI.initiation()) {
            this.finish();
            return;
        }

        if (mIsInComingCall) {
            inComingCalling();
        } else {
            outgoingCalling();
        }
        registerNetCallObserver(true);

        notifier = new AVChatNotification(this);
        notifier.init(receiverId != null ? receiverId : avChatData.getAccount());
        isCallEstablished = false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        AVChatManager.getInstance().pauseVideo(); // ��ͣ��Ƶ���죨��������Ƶ��������У�APP�˵���̨ʱ������ã�
        hasOnpause = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        activeCallingNotifier();
    }

    @Override
    protected void onResume() {
        super.onResume();
        cancelCallingNotifier();
        if (hasOnpause) {
            avChatUI.resumeVideo();
            hasOnpause = false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AVChatProfile.getInstance().setAVChatting(false);
        registerNetCallObserver(false);
        cancelCallingNotifier();
        needFinish = true;
    }

    @Override
    public void onBackPressed() {
    }

    /**
     * �ж����绹��ȥ��
     *
     * @return
     */
    private boolean checkSource() {
        switch (getIntent().getIntExtra(KEY_SOURCE, FROM_UNKNOWN)) {
            case FROM_BROADCASTRECEIVER: // incoming call
                parseIncomingIntent();
                return true;
            case FROM_INTERNAL: // outgoing call
                parseOutgoingIntent();
                if (state == AVChatType.VIDEO.getValue() || state == AVChatType.AUDIO.getValue()) {
                    return true;
                }
                return false;
            default:
                return false;
        }
    }

    /**
     * �����������
     */
    private void parseIncomingIntent() {
        avChatData = (AVChatData) getIntent().getSerializableExtra(KEY_CALL_CONFIG);
        state = avChatData.getChatType().getValue();
    }

    /**
     * ȥ���������
     */
    private void parseOutgoingIntent() {
        receiverId = getIntent().getStringExtra(KEY_ACCOUNT);
        state = getIntent().getIntExtra(KEY_CALL_TYPE, -1);
    }

    /**
     * ע�����
     *
     * @param register
     */
    private void registerNetCallObserver(boolean register) {
        AVChatManager.getInstance().observeAVChatState(this, register);
        AVChatManager.getInstance().observeCalleeAckNotification(callAckObserver, register);
        AVChatManager.getInstance().observeControlNotification(callControlObserver, register);
        AVChatManager.getInstance().observeHangUpNotification(callHangupObserver, register);
        AVChatManager.getInstance().observeOnlineAckNotification(onlineAckObserver, register);
        AVChatManager.getInstance().observeTimeoutNotification(timeoutObserver, register);
        AVChatManager.getInstance().observeAutoHangUpForLocalPhone(autoHangUpForLocalPhoneObserver, register);
    }

    /**
     * ע��/ע������ͨ�����з�����Ӧ���������ܾ���æ��
     */
    Observer<AVChatCalleeAckEvent> callAckObserver = new Observer<AVChatCalleeAckEvent>() {
        @Override
        public void onEvent(AVChatCalleeAckEvent ackInfo) {
            if (ackInfo.getEvent() == AVChatEventType.CALLEE_ACK_BUSY) {
                avChatUI.closeSessions(AVChatExitCode.PEER_BUSY);
            } else if (ackInfo.getEvent() == AVChatEventType.CALLEE_ACK_REJECT) {
                avChatUI.closeSessions(AVChatExitCode.REJECT);
            } else if (ackInfo.getEvent() == AVChatEventType.CALLEE_ACK_AGREE) {
                if (ackInfo.isDeviceReady()) {
                    avChatUI.isCallEstablish.set(true);
                    avChatUI.canSwitchCamera = true;
                } else {
                    // �豸��ʼ��ʧ��
                    Toast.makeText(AVChatActivity.this, R.string.avchat_device_no_ready, Toast.LENGTH_SHORT).show();
                    avChatUI.closeSessions(AVChatExitCode.OPEN_DEVICE_ERROR);
                }
            }
        }
    };

    Observer<AVChatTimeOutEvent> timeoutObserver = new Observer<AVChatTimeOutEvent>() {
        @Override
        public void onEvent(AVChatTimeOutEvent event) {
            if (event == AVChatTimeOutEvent.NET_BROKEN_TIMEOUT) {
                avChatUI.closeSessions(AVChatExitCode.NET_ERROR);
            } else {
                avChatUI.closeSessions(AVChatExitCode.PEER_NO_RESPONSE);
            }

            // ���糬ʱ���Լ�δ����
            if (event == AVChatTimeOutEvent.INCOMING_TIMEOUT) {
                activeMissCallNotifier();
            }
        }
    };

    Observer<Integer> autoHangUpForLocalPhoneObserver = new Observer<Integer>() {
        @Override
        public void onEvent(Integer integer) {
            avChatUI.closeSessions(AVChatExitCode.PEER_BUSY);
        }
    };

    /**
     * ע��/ע������ͨ��������Ϣ������Ƶģʽ�л�֪ͨ��
     */
    Observer<AVChatControlEvent> callControlObserver = new Observer<AVChatControlEvent>() {
        @Override
        public void onEvent(AVChatControlEvent netCallControlNotification) {
            handleCallControl(netCallControlNotification);
        }
    };

    /**
     * ע��/ע������ͨ���Է��Ҷϵ�֪ͨ
     */
    Observer<AVChatCommonEvent> callHangupObserver = new Observer<AVChatCommonEvent>() {
        @Override
        public void onEvent(AVChatCommonEvent avChatHangUpInfo) {
            avChatUI.closeSessions(AVChatExitCode.HANGUP);
            cancelCallingNotifier();
            // �����incoming call���з��Ҷϣ���ô֪ͨ����֪ͨ
            if (mIsInComingCall && !isCallEstablished) {
                activeMissCallNotifier();
            }
        }
    };

    /**
     * ע��/ע��ͬʱ���ߵ������˶����з�����Ӧ
     */
    Observer<AVChatOnlineAckEvent> onlineAckObserver = new Observer<AVChatOnlineAckEvent>() {
        @Override
        public void onEvent(AVChatOnlineAckEvent ackInfo) {
            if (ackInfo.getClientType() != ClientType.Android) {
                String client = null;
                switch (ackInfo.getClientType()) {
                    case ClientType.Web:
                        client = "Web";
                        break;
                    case ClientType.Windows:
                        client = "Windows";
                        break;
                    default:
                        break;
                }
                if (client != null) {
                    String option = ackInfo.getEvent() == AVChatEventType.CALLEE_ONLINE_CLIENT_ACK_AGREE ? "������" : "�ܾ���";
                    Toast.makeText(AVChatActivity.this, "ͨ������" + client + "�˱�" + option, Toast.LENGTH_SHORT).show();
                }
                avChatUI.closeSessions(-1);
            }
        }
    };


    /**
     * ����
     */
    private void inComingCalling() {
        avChatUI.inComingCalling(avChatData);
    }

    /**
     * ����
     */
    private void outgoingCalling() {
        if (!NetworkUtil.isNetAvailable(AVChatActivity.this)) { // ���粻����
            Toast.makeText(this, R.string.network_is_not_available, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        avChatUI.outGoingCalling(receiverId, AVChatType.typeOfValue(state));
    }

    /**
     * *************************** AVChatListener *********************************
     */

    @Override
    public void uiExit() {
        finish();
    }


    /**
     * ************************ AVChatStateObserver ****************************
     */

    @Override
    public void onConnectedServer(int res) {
        handleWithConnectServerResult(res);
    }

    @Override
    public void onUserJoin(String account) {
        Log.d(TAG, "onUserJoin");
        avChatUI.setVideoAccount(account);

        avChatUI.initRemoteSurfaceView(avChatUI.getVideoAccount());
    }

    @Override
    public void onUserLeave(String account, int event) {

    }

    @Override
    public void onProtocolIncompatible(int status) {

    }

    @Override
    public void onDisconnectServer() {

    }

    @Override
    public void onNetworkStatusChange(int value) {

    }

    @Override
    public void onCallEstablished() {
        Log.d(TAG, "onCallEstablished");
        if (avChatUI.getTimeBase() == 0)
            avChatUI.setTimeBase(SystemClock.elapsedRealtime());

        if (state == AVChatType.AUDIO.getValue()) {
            avChatUI.onCallStateChange(CallStateEnum.AUDIO);
        } else {
            avChatUI.initLocalSurfaceView();
            avChatUI.onCallStateChange(CallStateEnum.VIDEO);
        }
        isCallEstablished = true;
    }

    @Override
    public void onOpenDeviceError(int code) {

    }

    @Override
    public void onRecordEnd(String[] files, int event) {
        if(files != null && files.length > 0) {
            String file = files[0];
            String parent = new File(file).getParent();
            String msg;
            if(event == 0) {
                msg = "¼���ѽ���";
            } else {
                msg = "����ֻ��ڴ治��, ¼���ѽ���";
            }

            if(!TextUtils.isEmpty(parent)) {
                msg += ", ¼���ļ��ѱ�������" + parent;
            }

            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        } else {
            if(event == 1) {
                Toast.makeText(this, "����ֻ��ڴ治��, ¼���ѽ���.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "¼���ѽ���.", Toast.LENGTH_SHORT).show();
            }
        }

        if(event == 1) {
            if(avChatUI != null) {
                avChatUI.resetRecordTip();
            }
        }
    }

    /****************************** ���ӽ������� ********************/

    /**
     * �������ӷ������ķ���ֵ
     *
     * @param auth_result
     */
    protected void handleWithConnectServerResult(int auth_result) {
        LogUtil.i(TAG, "result code->" + auth_result);
        if (auth_result == 200) {
            Log.d(TAG, "onConnectServer success");
        } else if (auth_result == 101) { // ���ӳ�ʱ
            avChatUI.closeSessions(AVChatExitCode.PEER_NO_RESPONSE);
        } else if (auth_result == 401) { // ��֤ʧ��
            avChatUI.closeSessions(AVChatExitCode.CONFIG_ERROR);
        } else if (auth_result == 417) { // ��Ч��channelId
            avChatUI.closeSessions(AVChatExitCode.INVALIDE_CHANNELID);
        } else { // ���ӷ���������ֱ���˳�
            avChatUI.closeSessions(AVChatExitCode.CONFIG_ERROR);
        }
    }

    /**************************** ��������Ƶ�л� *********************************/

    /**
     * ��������Ƶ�л�����
     *
     * @param notification
     */
    private void handleCallControl(AVChatControlEvent notification) {
        switch (notification.getControlCommand()) {
            case SWITCH_AUDIO_TO_VIDEO:
                avChatUI.incomingAudioToVideo();
                break;
            case SWITCH_AUDIO_TO_VIDEO_AGREE:
                onAudioToVideo();
                break;
            case SWITCH_AUDIO_TO_VIDEO_REJECT:
                avChatUI.onCallStateChange(CallStateEnum.AUDIO);
                Toast.makeText(AVChatActivity.this, R.string.avchat_switch_video_reject, Toast.LENGTH_SHORT).show();
                break;
            case SWITCH_VIDEO_TO_AUDIO:
                onVideoToAudio();
                break;
            case NOTIFY_VIDEO_OFF:
                avChatUI.peerVideoOff();
                break;
            case NOTIFY_VIDEO_ON:
                avChatUI.peerVideoOn();
                break;
            case NOTIFY_RECORD_START:
                Toast.makeText(this, "�Է���ʼ��ͨ��¼��", Toast.LENGTH_SHORT).show();
                break;
            case NOTIFY_RECORD_STOP:
                Toast.makeText(this, "�Է�������ͨ��¼��", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
    }

    /**
     * ��Ƶ�л�Ϊ��Ƶ
     */
    private void onAudioToVideo() {
        avChatUI.onAudioToVideo();
        avChatUI.initAllSurfaceView(avChatUI.getVideoAccount());
    }

    /**
     * ��Ƶ�л�Ϊ��Ƶ
     */
    private void onVideoToAudio() {
        avChatUI.onCallStateChange(CallStateEnum.AUDIO);
        avChatUI.onVideoToAudio();
    }

    /**
     * ֪ͨ��
     */
    private void activeCallingNotifier() {
        if (notifier != null && !isUserFinish) {
            notifier.activeCallingNotification(true);
        }
    }

    private void cancelCallingNotifier() {
        if (notifier != null) {
            notifier.activeCallingNotification(false);
        }
    }

    private void activeMissCallNotifier() {
        if (notifier != null) {
            notifier.activeMissCallNotification(true);
        }
    }

    @Override
    public void finish() {
        isUserFinish = true;
        super.finish();
    }
}

