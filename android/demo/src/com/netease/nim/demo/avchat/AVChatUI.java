package com.netease.nim.demo.avchat;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.StatFs;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.netease.nim.demo.DemoCache;
import com.netease.nim.demo.R;
import com.netease.nim.demo.avchat.activity.AVChatExitCode;
import com.netease.nim.demo.avchat.constant.CallStateEnum;
import com.netease.nim.uikit.common.ui.dialog.DialogMaker;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.netease.nimlib.sdk.ResponseCode;
import com.netease.nimlib.sdk.avchat.AVChatCallback;
import com.netease.nimlib.sdk.avchat.AVChatManager;
import com.netease.nimlib.sdk.avchat.constant.AVChatType;
import com.netease.nimlib.sdk.avchat.model.AVChatData;
import com.netease.nimlib.sdk.avchat.model.AVChatNotifyOption;
import com.netease.nimlib.sdk.avchat.model.VideoChatParam;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * ����Ƶ������, ����Ƶ��ع��ܹ���
 * Created by hzxuwen on 2015/4/23.
 */
public class AVChatUI implements AVChatUIListener {
    // constant
    private static final String TAG = "AVChatUI";

    // data
    private Context context;
    private AVChatData avChatData;
    private final AVChatListener aVChatListener;
    private String receiverId;
    private AVChatAudio avChatAudio;
    private AVChatVideo avChatVideo;
    private AVChatSurface avChatSurface;
    private VideoChatParam videoParam; // ��Ƶ�ɼ�����
    private String videoAccount; // ������Ƶ����onUserJoin�ص���user account

    private CallStateEnum callingState = CallStateEnum.INVALID;

    private long timeBase = 0;

    // view
    private View root;

    // state
    public boolean canSwitchCamera = false;
    private boolean isClosedCamera = false;
    public AtomicBoolean isCallEstablish = new AtomicBoolean(false);


    // ���洢
    private Handler uiHandler = new Handler(Looper.getMainLooper());
    private boolean recordWarning = false;
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            File dir = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(dir.getPath());
            long blockSize;
            if( Build.VERSION.SDK_INT >= 18) {
                blockSize = stat.getBlockSizeLong();
            } else {
                blockSize = stat.getBlockSize();
            }
            long availableBlocks;
            if(Build.VERSION.SDK_INT >= 18) {
                availableBlocks = stat.getAvailableBlocksLong();
            } else {
                availableBlocks = stat.getAvailableBlocks();
            }

            long size = availableBlocks * blockSize;

            if(size <= 10 * 1024 * 1024) {
                recordWarning = true;
                updateRecordTip();
            } else {
                uiHandler.postDelayed(this, 1000);
            }
        }
    };

    public interface AVChatListener {
        void uiExit();
    }

    public AVChatUI(Context context, View root, AVChatListener listener) {
        this.context = context;
        this.root = root;
        this.aVChatListener = listener;
    }

    /**
     * ******************************��ʼ��******************************
     */

    /**
     * ��ʼ����������ʼ����Ƶ�������� ��Ƶ����������Ƶ������ƹ�������
     *
     * @return boolean
     */
    public boolean initiation() {
        AVChatProfile.getInstance().setAVChatting(true);
        avChatAudio = new AVChatAudio(root.findViewById(R.id.avchat_audio_layout), this, this);
        avChatVideo = new AVChatVideo(context, root.findViewById(R.id.avchat_video_layout), this, this);
        avChatSurface = new AVChatSurface(context, this, root.findViewById(R.id.avchat_surface_layout));

        return true;
    }

    /**
     * ******************************����ͽ���***************************
     */

    /**
     * ����
     */
    public void inComingCalling(AVChatData avChatData) {
        this.avChatData = avChatData;
        receiverId = avChatData.getAccount();
        if (avChatData.getChatType() == AVChatType.AUDIO) {
            onCallStateChange(CallStateEnum.INCOMING_AUDIO_CALLING);
        } else {
            onCallStateChange(CallStateEnum.INCOMING_VIDEO_CALLING);
        }
    }


    //Only for test
    private int getVideoDimens() {

        File file = new File("sdcard/nim.properties");

        if(file.exists()) {
            InputStream is = null;

            try {
                is = new BufferedInputStream(new FileInputStream(file));
                Properties properties = new Properties();
                properties.load(is);
                int dimens = Integer.parseInt(properties.getProperty("avchat.video.dimens", "0"));
                LogUtil.i(TAG, "dimes:" + dimens);
                return dimens;
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if(is != null) {
                    try {
                        is.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

        }

        return 0;
    }

    /**
     * ��������Ƶ
     */
    public void outGoingCalling(String account, AVChatType callTypeEnum) {
        DialogMaker.showProgressDialog(context, null);
        this.receiverId = account;
        VideoChatParam videoParam = null;
        if (callTypeEnum == AVChatType.AUDIO) {
            onCallStateChange(CallStateEnum.OUTGOING_AUDIO_CALLING);
        } else {
            onCallStateChange(CallStateEnum.OUTGOING_VIDEO_CALLING);
            if (videoParam == null) {
                videoParam = new VideoChatParam(avChatSurface.mCapturePreview, 0, getVideoDimens());
            }
        }

        AVChatNotifyOption notifyOption = new AVChatNotifyOption();
        notifyOption.extendMessage = "extra_data";

        /**
         * ����ͨ��
         * account �Է��ʺ�
         * callTypeEnum ͨ�����ͣ���������Ƶ
         * videoParam ������Ƶͨ��ʱ���룬������Ƶͨ����null
         * AVChatCallback �ص�����������AVChatInfo
         */
        AVChatManager.getInstance().call(account, callTypeEnum, videoParam, notifyOption, new AVChatCallback<AVChatData>() {
            @Override
            public void onSuccess(AVChatData data) {
                avChatData = data;
                DialogMaker.dismissProgressDialog();
            }

            @Override
            public void onFailed(int code) {
                LogUtil.d(TAG, "avChat call failed code->" + code);
                DialogMaker.dismissProgressDialog();
                if (code == ResponseCode.RES_FORBIDDEN) {
                    Toast.makeText(context, R.string.avchat_no_permission, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, R.string.avchat_call_failed, Toast.LENGTH_SHORT).show();
                }
                closeSessions(-1);
            }

            @Override
            public void onException(Throwable exception) {
                LogUtil.d(TAG, "avChat call onException->" + exception);
                DialogMaker.dismissProgressDialog();
            }
        });
    }

    /**
     * ״̬�ı�
     *
     * @param stateEnum
     */
    public void onCallStateChange(CallStateEnum stateEnum) {
        callingState = stateEnum;
        avChatSurface.onCallStateChange(stateEnum);
        avChatAudio.onCallStateChange(stateEnum);
        avChatVideo.onCallStateChange(stateEnum);
    }

    /**
     * �Ҷ�
     *
     * @param type ����Ƶ����
     */
    private void hangUp(final int type) {
        if (type == AVChatExitCode.HANGUP || type == AVChatExitCode.PEER_NO_RESPONSE || type == AVChatExitCode.CANCEL) {
            AVChatManager.getInstance().hangUp(new AVChatCallback<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                }

                @Override
                public void onFailed(int code) {
                    LogUtil.d(TAG, "hangup onFailed->" + code);
                }

                @Override
                public void onException(Throwable exception) {
                    LogUtil.d(TAG, "hangup onException->" + exception);
                }
            });
        }
        closeSessions(type);
    }

    /**
     * �رձ�������Ƶ�����
     *
     * @param exitCode ����Ƶ����
     */
    public void closeSessions(int exitCode) {
        //not  user  hang up active  and warning tone is playing,so wait its end
        Log.i(TAG, "close session -> " + AVChatExitCode.getExitString(exitCode));
        if (avChatAudio != null)
            avChatAudio.closeSession(exitCode);
        if (avChatVideo != null)
            avChatVideo.closeSession(exitCode);
        uiHandler.removeCallbacks(runnable);
        showQuitToast(exitCode);
        isCallEstablish.set(false);
        canSwitchCamera = false;
        isClosedCamera = false;
        aVChatListener.uiExit();
    }

    /**
     * ��������������
     *
     * @param code
     */
    public void showQuitToast(int code) {
        switch (code) {
            case AVChatExitCode.NET_CHANGE: // �����л�
            case AVChatExitCode.NET_ERROR: // �����쳣
            case AVChatExitCode.CONFIG_ERROR: // �������������ݴ���
                Toast.makeText(context, R.string.avchat_net_error_then_quit, Toast.LENGTH_SHORT).show();
                break;
            case AVChatExitCode.PEER_HANGUP:
            case AVChatExitCode.HANGUP:
                if (isCallEstablish.get()) {
                    Toast.makeText(context, R.string.avchat_call_finish, Toast.LENGTH_SHORT).show();
                }
                break;
            case AVChatExitCode.PEER_BUSY:
                Toast.makeText(context, R.string.avchat_peer_busy, Toast.LENGTH_SHORT).show();
                break;
            case AVChatExitCode.PROTOCOL_INCOMPATIBLE_PEER_LOWER:
                Toast.makeText(context, R.string.avchat_peer_protocol_low_version, Toast.LENGTH_SHORT).show();
                break;
            case AVChatExitCode.PROTOCOL_INCOMPATIBLE_SELF_LOWER:
                Toast.makeText(context, R.string.avchat_local_protocol_low_version, Toast.LENGTH_SHORT).show();
                break;
            case AVChatExitCode.INVALIDE_CHANNELID:
                Toast.makeText(context, R.string.avchat_invalid_channel_id, Toast.LENGTH_SHORT).show();
                break;
            case AVChatExitCode.LOCAL_CALL_BUSY:
                Toast.makeText(context, R.string.avchat_local_call_busy, Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
    }

    /**
     * ******************************* ������ܾ����� *********************************
     */

    /**
     * �ܾ�����
     */
    private void rejectInComingCall() {
        /**
         * ���շ��ܾ�ͨ��
         * AVChatCallback �ص�����
         */
        AVChatManager.getInstance().hangUp(new AVChatCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
            }

            @Override
            public void onFailed(int code) {
                LogUtil.d(TAG, "reject sucess->" + code);
            }

            @Override
            public void onException(Throwable exception) {
                LogUtil.d(TAG, "reject sucess");
            }
        });
        closeSessions(AVChatExitCode.REJECT);
    }

    /**
     * �ܾ�����Ƶ�л�
     */
    private void rejectAudioToVideo() {
        onCallStateChange(CallStateEnum.AUDIO);
        AVChatManager.getInstance().ackSwitchToVideo(false, videoParam, null); // ��Ƶ�л�����Ƶ����Ļ�Ӧ. trueΪͬ�⣬falseΪ�ܾ�
        updateRecordTip();
    }

    /**
     * ��������
     */
    private void receiveInComingCall() {
        //��������֪���������Ա�֪ͨ������
        VideoChatParam videoParam = null;

        if (callingState == CallStateEnum.INCOMING_AUDIO_CALLING) {
            onCallStateChange(CallStateEnum.AUDIO_CONNECTING);
        } else {
            onCallStateChange(CallStateEnum.VIDEO_CONNECTING);
            videoParam = new VideoChatParam(avChatSurface.mCapturePreview, 0, getVideoDimens());
        }

        /**
         * ���շ������绰
         * videoParam ������Ƶͨ��ʱ���룬������Ƶͨ����null
         * AVChatCallback �ص��������ɹ������ӽ��������ɹ���ر�activity��
         */
        AVChatManager.getInstance().accept(videoParam, new AVChatCallback<Void>() {
            @Override
            public void onSuccess(Void v) {
                LogUtil.i(TAG, "accept success");

                isCallEstablish.set(true);
                canSwitchCamera = true;
            }

            @Override
            public void onFailed(int code) {
                if (code == -1) {
                    Toast.makeText(context, "��������Ƶ����ʧ��", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "��������ʧ��", Toast.LENGTH_SHORT).show();
                }
                LogUtil.e(TAG, "accept onFailed->" + code);
                closeSessions(AVChatExitCode.CANCEL);
            }

            @Override
            public void onException(Throwable exception) {
                LogUtil.d(TAG, "accept exception->" + exception);
            }
        });
    }

    /*************************** AVChatUIListener ******************************/

    /**
     * ����Ҷϻ�ȡ��
     */
    @Override
    public void onHangUp() {
        if (isCallEstablish.get()) {
            hangUp(AVChatExitCode.HANGUP);
        } else {
            hangUp(AVChatExitCode.CANCEL);
        }
    }

    /**
     * �ܾ����������ݵ�ǰ״̬��ѡ����ʵĲ���
     */
    @Override
    public void onRefuse() {
        switch (callingState) {
            case INCOMING_AUDIO_CALLING:
            case AUDIO_CONNECTING:
                rejectInComingCall();
                break;
            case INCOMING_AUDIO_TO_VIDEO:
                rejectAudioToVideo();
                break;
            case INCOMING_VIDEO_CALLING:
            case VIDEO_CONNECTING: // �����е���ܾ�
                rejectInComingCall();
                break;
            default:
                break;
        }
    }

    /**
     * �������������ݵ�ǰ״̬��ѡ����ʵĲ���
     */
    @Override
    public void onReceive() {
        switch (callingState) {
            case INCOMING_AUDIO_CALLING:
                receiveInComingCall();
                onCallStateChange(CallStateEnum.AUDIO_CONNECTING);
                break;
            case AUDIO_CONNECTING: // �����У������������ �޷�Ӧ
                break;
            case INCOMING_VIDEO_CALLING:
                receiveInComingCall();
                onCallStateChange(CallStateEnum.VIDEO_CONNECTING);
                break;
            case VIDEO_CONNECTING: // �����У������������ �޷�Ӧ
                break;
            case INCOMING_AUDIO_TO_VIDEO:
                receiveAudioToVideo();
            default:
                break;
        }
    }

    @Override
    public void toggleMute() {
        if (!isCallEstablish.get()) { // ����δ�������������¼����״̬
            return;
        } else { // �����Ѿ�����
            if (!AVChatManager.getInstance().isMute()) { // isMute�Ƿ��ھ���״̬
                // �ر���Ƶ
                AVChatManager.getInstance().setMute(true);
            } else {
                // ����Ƶ
                AVChatManager.getInstance().setMute(false);
            }
        }
    }

    @Override
    public void toggleSpeaker() {
        AVChatManager.getInstance().setSpeaker(!AVChatManager.getInstance().speakerEnabled()); // �����������Ƿ���
    }

    @Override
    public void toggleRecord() {
        if(AVChatManager.getInstance().isRecording()) {
            AVChatManager.getInstance().stopRecord(new AVChatCallback<Void>() {
                @Override
                public void onSuccess(Void aVoid) {

                }

                @Override
                public void onFailed(int code) {

                }

                @Override
                public void onException(Throwable exception) {

                }
            });

            uiHandler.removeCallbacks(runnable);
            recordWarning = false;

        } else {
            recordWarning = false;

            if(AVChatManager.getInstance().startRecord(new AVChatCallback<Void>() {
                @Override
                public void onSuccess(Void aVoid) {

                }

                @Override
                public void onFailed(int code) {

                }

                @Override
                public void onException(Throwable exception) {

                }
            })) {

                if(CallStateEnum.isAudioMode(callingState)) {
                    Toast.makeText(context, "��¼����˵��������", Toast.LENGTH_SHORT).show();
                }

                if(CallStateEnum.isVideoMode(callingState)) {
                    Toast.makeText(context, "��¼�����������ͼ��", Toast.LENGTH_SHORT).show();
                }

                uiHandler.post(runnable);

            } else {

                Toast.makeText(context, "¼��ʧ��", Toast.LENGTH_SHORT).show();
            }
        }

        updateRecordTip();
    }

    private void updateRecordTip() {

        if(CallStateEnum.isAudioMode(callingState)) {
            avChatAudio.showRecordView(AVChatManager.getInstance().isRecording(), recordWarning);
        }
        if(CallStateEnum.isVideoMode(callingState)) {
            avChatVideo.showRecordView(AVChatManager.getInstance().isRecording(), recordWarning);
        }

    }

    public void resetRecordTip() {
        uiHandler.removeCallbacks(runnable);
        recordWarning = false;
        if(CallStateEnum.isAudioMode(callingState)) {
            avChatAudio.showRecordView(AVChatManager.getInstance().isRecording(), recordWarning);
        }
        if(CallStateEnum.isVideoMode(callingState)) {
            avChatVideo.showRecordView(AVChatManager.getInstance().isRecording(), recordWarning);
        }
    }

    @Override
    public void videoSwitchAudio() {
        /**
         * ������Ƶ�л�����Ƶ
         */
        AVChatManager.getInstance().requestSwitchToAudio(new AVChatCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // ���沼���л���
                onCallStateChange(CallStateEnum.AUDIO);
                onVideoToAudio();
            }

            @Override
            public void onFailed(int code) {

            }

            @Override
            public void onException(Throwable exception) {

            }
        });
    }

    @Override
    public void audioSwitchVideo() {
        onCallStateChange(CallStateEnum.OUTGOING_AUDIO_TO_VIDEO);
        videoParam = new VideoChatParam(avChatSurface.mCapturePreview, 0, getVideoDimens());
        /**
         * ������Ƶ�л�����Ƶ
         */
        AVChatManager.getInstance().requestSwitchToVideo(videoParam, new AVChatCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                LogUtil.d(TAG, "requestSwitchToVideo onSuccess");
            }

            @Override
            public void onFailed(int code) {
                LogUtil.d(TAG, "requestSwitchToVideo onFailed" + code);
            }

            @Override
            public void onException(Throwable exception) {
                LogUtil.d(TAG, "requestSwitchToVideo onException" + exception);
            }
        });
    }

    @Override
    public void switchCamera() {
        AVChatManager.getInstance().toggleCamera(); // �л�����ͷ����Ҫ����ǰ�úͺ�������ͷ�л���
    }

    @Override
    public void closeCamera() {
        if (!isClosedCamera) {
            // �ر�����ͷ
            AVChatManager.getInstance().toggleLocalVideo(false, null);
            isClosedCamera = true;
            avChatSurface.localVideoOff();
        } else {
            // ������ͷ
            AVChatManager.getInstance().toggleLocalVideo(true, null);
            isClosedCamera = false;
            avChatSurface.localVideoOn();
        }

    }

    /**
     * ��Ƶ�л�Ϊ��Ƶ������
     */
    public void incomingAudioToVideo() {
        onCallStateChange(CallStateEnum.INCOMING_AUDIO_TO_VIDEO);
        if (videoParam == null) {
            videoParam = new VideoChatParam(avChatSurface.mCapturePreview, 0, getVideoDimens());
        }
    }

    /**
     * ͬ����Ƶ�л�Ϊ��Ƶ
     */
    private void receiveAudioToVideo() {
        AVChatManager.getInstance().ackSwitchToVideo(true, videoParam, new AVChatCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                onAudioToVideo();
                initAllSurfaceView(videoAccount);
            }

            @Override
            public void onFailed(int code) {

            }

            @Override
            public void onException(Throwable exception) {

            }
        }); // ��Ƶ�л�����Ƶ����Ļ�Ӧ. trueΪͬ�⣬falseΪ�ܾ�

    }

    /**
     * ��ʼ����Сͼ��
     *
     * @param largeAccount �Է����ʺ�
     */
    public void initAllSurfaceView(String largeAccount) {
        avChatSurface.initLargeSurfaceView(largeAccount);
        avChatSurface.initSmallSurfaceView(DemoCache.getAccount());
    }

    public void initRemoteSurfaceView(String account) {
        avChatSurface.initLargeSurfaceView(account);
    }

    public void initLocalSurfaceView() {
        avChatSurface.initSmallSurfaceView(DemoCache.getAccount());
    }

    /**
     * ��Ƶ�л�Ϊ��Ƶ
     */
    public void onAudioToVideo() {
        onCallStateChange(CallStateEnum.VIDEO);
        avChatVideo.onAudioToVideo(AVChatManager.getInstance().isMute(),
                AVChatManager.getInstance().isRecording(), recordWarning); // isMute�Ƿ��ھ���״̬
        if (!AVChatManager.getInstance().isVideoSend()) { // �Ƿ��ڷ�����Ƶ ������ͷ�Ƿ���
            AVChatManager.getInstance().toggleLocalVideo(true, null);
            avChatSurface.localVideoOn();
            isClosedCamera = false;
        }
    }

    /**
     * ��Ƶ�л�Ϊ��Ƶ
     */
    public void onVideoToAudio() {
        // �ж��Ƿ������������Ƿ������Խ�����Ӧ�ؼ�������������
        avChatAudio.onVideoToAudio(AVChatManager.getInstance().isMute(),
                AVChatManager.getInstance().speakerEnabled(),
                AVChatManager.getInstance().isRecording(), recordWarning);
    }

    public void peerVideoOff() {
        avChatSurface.peerVideoOff();
    }

    public void peerVideoOn() {
        avChatSurface.peerVideoOn();
    }

    /**
     * // �ָ���Ƶ���죨������Ƶ�����˵���̨�󣬴Ӻ�̨�ָ�ʱ���ã�
     */
    public void resumeVideo() {
        AVChatManager.getInstance().resumeVideo(avChatSurface.isLocalPreviewInSmallSize());
    }

    public boolean canSwitchCamera() {
        return canSwitchCamera;
    }

    public CallStateEnum getCallingState() {
        return callingState;
    }

    public String getVideoAccount() {
        return videoAccount;
    }

    public void setVideoAccount(String videoAccount) {
        this.videoAccount = videoAccount;
    }

    public String getAccount() {
        if (receiverId != null)
            return receiverId;
        return null;
    }

    public long getTimeBase() {
        return timeBase;
    }

    public void setTimeBase(long timeBase) {
        this.timeBase = timeBase;
    }
}
