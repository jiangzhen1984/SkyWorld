package com.netease.nim.demo.avchat;

import android.content.Context;
import android.graphics.Rect;
import android.view.View;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.netease.nim.uikit.cache.NimUserInfoCache;
import com.netease.nim.demo.R;
import com.netease.nim.demo.avchat.constant.CallStateEnum;
import com.netease.nim.demo.avchat.widgets.ToggleListener;
import com.netease.nim.demo.avchat.widgets.ToggleState;
import com.netease.nim.demo.avchat.widgets.ToggleView;
import com.netease.nim.uikit.common.ui.imageview.HeadImageView;
import com.netease.nimlib.sdk.avchat.AVChatManager;

/**
 * ��Ƶ�������� ��Ƶ�����ʼ������ع���
 * Created by hzxuwen on 2015/5/5.
 */
public class AVChatVideo implements View.OnClickListener, ToggleListener{

    // data
    private Context context;
    private View root;
    private AVChatUI manager;
    //�������ư�ť
    private View topRoot;
    private View switchAudio;
    private Chronometer time;
    private TextView netUnstableTV;
    //�м���ư�ť
    private View middleRoot;
    private HeadImageView headImg;
    private  TextView nickNameTV;
    private  TextView notifyTV;
    private View refuse_receive;
    private TextView refuseTV;
    private TextView receiveTV;
    //�ײ����ư�ť
    private View bottomRoot;
    ToggleView switchCameraToggle;
    ToggleView closeCameraToggle;
    ToggleView muteToggle;
    ToggleView recordToggle;
    ImageView hangUpImg;

    //record
    private View recordView;
    private View recordTip;
    private View recordWarning;

    private int topRootHeight = 0;
    private int bottomRootHeight = 0;

    private AVChatUIListener listener;

    // state
    private boolean init = false;
    private boolean shouldEnableToggle = false;
    private boolean isInSwitch = false;

    public AVChatVideo(Context context, View root, AVChatUIListener listener, AVChatUI manager) {
        this.context = context;
        this.root = root;
        this.listener = listener;
        this.manager = manager;
    }

    private void findViews() {
        if(init || root == null )
            return;
        topRoot = root.findViewById(R.id.avchat_video_top_control);
        switchAudio = topRoot.findViewById(R.id.avchat_video_switch_audio);
        switchAudio.setOnClickListener(this);
        time = (Chronometer) topRoot.findViewById(R.id.avchat_video_time);
        netUnstableTV = (TextView) topRoot.findViewById(R.id.avchat_video_netunstable);

        middleRoot = root.findViewById(R.id.avchat_video_middle_control);
        headImg = (HeadImageView) middleRoot.findViewById(R.id.avchat_video_head);
        nickNameTV = (TextView) middleRoot.findViewById(R.id.avchat_video_nickname);
        notifyTV = (TextView) middleRoot.findViewById(R.id.avchat_video_notify);

        refuse_receive = middleRoot.findViewById(R.id.avchat_video_refuse_receive);
        refuseTV = (TextView) refuse_receive.findViewById(R.id.refuse);
        receiveTV = (TextView) refuse_receive.findViewById(R.id.receive);
        refuseTV.setOnClickListener(this);
        receiveTV.setOnClickListener(this);

        recordView = root.findViewById(R.id.avchat_record_layout);
        recordTip = recordView.findViewById(R.id.avchat_record_tip);
        recordWarning = recordView.findViewById(R.id.avchat_record_warning);

        bottomRoot = root.findViewById(R.id.avchat_video_bottom_control);
        switchCameraToggle = new ToggleView(bottomRoot.findViewById(R.id.avchat_switch_camera), ToggleState.DISABLE, this);
        closeCameraToggle = new ToggleView(bottomRoot.findViewById(R.id.avchat_close_camera), ToggleState.DISABLE, this);
        muteToggle = new ToggleView(bottomRoot.findViewById(R.id.avchat_video_mute), ToggleState.DISABLE, this);
        recordToggle = new ToggleView(bottomRoot.findViewById(R.id.avchat_video_record), ToggleState.DISABLE, this);
        hangUpImg = (ImageView) bottomRoot.findViewById(R.id.avchat_video_logout);
        hangUpImg.setOnClickListener(this);
        init = true;
    }

    /**
     * ����Ƶ״̬�仯������ˢ��
     * @param state
     */
    public void onCallStateChange(CallStateEnum state) {
        if(CallStateEnum.isVideoMode(state))
            findViews();
        switch (state){
            case OUTGOING_VIDEO_CALLING:
                showProfile();//�Է�����ϸ��Ϣ
                showNotify(R.string.avchat_wait_recieve);
                setRefuseReceive(false);
                shouldEnableToggle = true;
                setTopRoot(false);
                setMiddleRoot(true);
                setBottomRoot(true);
                break;
            case INCOMING_VIDEO_CALLING:
                showProfile();//�Է�����ϸ��Ϣ
                showNotify(R.string.avchat_video_call_request);
                setRefuseReceive(true);
                receiveTV.setText(R.string.avchat_pickup);
                setTopRoot(false);
                setMiddleRoot(true);
                setBottomRoot(false);
                break;
            case VIDEO:
                isInSwitch = false;
                enableToggle();
                setTime(true);
                setTopRoot(true);
                setMiddleRoot(false);
                setBottomRoot(true);
                break;
            case VIDEO_CONNECTING:
                showNotify(R.string.avchat_connecting);
                shouldEnableToggle = true;
                break;
            case OUTGOING_AUDIO_TO_VIDEO:
                isInSwitch = true;
                setTime(true);
                setTopRoot(true);
                setMiddleRoot(false);
                setBottomRoot(true);
                break;
            default:
                break;
        }
        setRoot(CallStateEnum.isVideoMode(state));
    }

    /********************** ������ʾ **********************************/

    /**
     * ��ʾ������Ϣ
     */
    private void showProfile(){
        String account = manager.getAccount();
        headImg.loadBuddyAvatar(account);
        nickNameTV.setText(NimUserInfoCache.getInstance().getUserDisplayName(account));
    }

    /**
     * ��ʾ֪ͨ
     * @param resId
     */
    private void showNotify(int resId){
        notifyTV.setText(resId);
        notifyTV.setVisibility(View.VISIBLE);
    }

    /************************ ������������ ****************************/

    private void setRoot(boolean visible) {
        root.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    private void setRefuseReceive(boolean visible){
        refuse_receive.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    private void setTopRoot(boolean visible){
        topRoot.setVisibility(visible ? View.VISIBLE : View.GONE);
        if(topRootHeight == 0){
            Rect rect = new Rect();
            topRoot.getGlobalVisibleRect(rect);
            topRootHeight = rect.bottom;
        }
    }

    private void setMiddleRoot(boolean visible){
        middleRoot.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    private void setBottomRoot(boolean visible){
        bottomRoot.setVisibility(visible ? View.VISIBLE : View.GONE);
        if(bottomRootHeight == 0){
            bottomRootHeight = bottomRoot.getHeight();
        }
    }

    private void setTime(boolean visible){
        time.setVisibility(visible ? View.VISIBLE : View.GONE);
        if(visible){
            time.setBase(manager.getTimeBase());
            time.start();
        }
    }

    /**
     * �ײ����ƿ��ؿ���
     */
    private void enableToggle() {
        if (shouldEnableToggle) {
            if (manager.canSwitchCamera())
                switchCameraToggle.enable();
            closeCameraToggle.enable();
            muteToggle.enable();
            recordToggle.enable();
            shouldEnableToggle = false;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.avchat_video_logout:
                listener.onHangUp();
                break;
            case R.id.refuse:
                listener.onRefuse();
                break;
            case R.id.receive:
                listener.onReceive();
                break;
            case R.id.avchat_video_mute:
                listener.toggleMute();
                break;
            case R.id.avchat_video_switch_audio:
                if(isInSwitch) {
                    Toast.makeText(context, R.string.avchat_in_switch, Toast.LENGTH_SHORT).show();
                } else {
                    listener.videoSwitchAudio();
                }
                break;
            case R.id.avchat_switch_camera:
                listener.switchCamera();
                break;
            case R.id.avchat_close_camera:
                listener.closeCamera();
                break;
            case R.id.avchat_video_record:
                listener.toggleRecord();
                break;
            default:
                break;
        }

    }

    public void showRecordView(boolean show, boolean warning) {
        if(show) {
            recordView.setVisibility(View.VISIBLE);
            recordTip.setVisibility(View.VISIBLE);
            if(warning) {
                recordWarning.setVisibility(View.VISIBLE);
            } else {
                recordWarning.setVisibility(View.GONE);
            }
        } else {
            recordView.setVisibility(View.INVISIBLE);
            recordTip.setVisibility(View.INVISIBLE);
            recordWarning.setVisibility(View.GONE);
        }
    }

    /**
     * ��Ƶ�л�Ϊ��Ƶ, ����ؼ��Ƿ�����ʾ
     * @param muteOn
     */
    public void onAudioToVideo(boolean muteOn, boolean recordOn, boolean recordWarning){
        muteToggle.toggle(muteOn ? ToggleState.ON : ToggleState.OFF);
        closeCameraToggle.toggle(ToggleState.OFF);
        if(manager.canSwitchCamera()){
            if(AVChatManager.getInstance().isFrontCamera())
                switchCameraToggle.off(false);
            else
                switchCameraToggle.on(false);
        }
        recordToggle.toggle(recordOn ? ToggleState.ON : ToggleState.OFF);

        showRecordView(recordOn, recordWarning);

    }

    /******************************* toggle listener *************************/
    @Override
    public void toggleOn(View v) {
        onClick(v);
    }

    @Override
    public void toggleOff(View v) {
        onClick(v);
    }

    @Override
    public void toggleDisable(View v) {

    }

    public void closeSession(int exitCode){
        if(init){
            time.stop();
            switchCameraToggle.disable(false);
            muteToggle.disable(false);
            recordToggle.disable(false);
            closeCameraToggle.disable(false);
            receiveTV.setEnabled(false);
            refuseTV.setEnabled(false);
            hangUpImg.setEnabled(false);
        }
    }
}
