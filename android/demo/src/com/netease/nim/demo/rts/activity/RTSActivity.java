package com.netease.nim.demo.rts.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.netease.nim.uikit.cache.NimUserInfoCache;
import com.netease.nim.demo.R;
import com.netease.nim.demo.rts.ActionTypeEnum;
import com.netease.nim.demo.rts.doodle.DoodleView;
import com.netease.nim.demo.rts.doodle.SupportActionType;
import com.netease.nim.demo.rts.doodle.TransactionCenter;
import com.netease.nim.demo.rts.doodle.action.MyPath;
import com.netease.nim.demo.session.extension.RTSAttachment;
import com.netease.nim.uikit.common.activity.TActionBarActivity;
import com.netease.nim.uikit.common.ui.dialog.EasyAlertDialog;
import com.netease.nim.uikit.common.ui.dialog.EasyAlertDialogHelper;
import com.netease.nim.uikit.common.ui.imageview.HeadImageView;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.netease.nim.uikit.common.util.sys.ActionBarUtil;
import com.netease.nim.uikit.common.util.sys.ScreenUtil;
import com.netease.nim.uikit.session.helper.MessageListPanelHelper;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.auth.ClientType;
import com.netease.nimlib.sdk.msg.MessageBuilder;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.constant.MsgDirectionEnum;
import com.netease.nimlib.sdk.msg.constant.MsgStatusEnum;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.netease.nimlib.sdk.rts.RTSCallback;
import com.netease.nimlib.sdk.rts.RTSChannelStateObserver;
import com.netease.nimlib.sdk.rts.RTSManager;
import com.netease.nimlib.sdk.rts.constant.RTSEventType;
import com.netease.nimlib.sdk.rts.constant.RTSTimeOutEvent;
import com.netease.nimlib.sdk.rts.constant.RTSTunType;
import com.netease.nimlib.sdk.rts.model.RTSCalleeAckEvent;
import com.netease.nimlib.sdk.rts.model.RTSCommonEvent;
import com.netease.nimlib.sdk.rts.model.RTSControlEvent;
import com.netease.nimlib.sdk.rts.model.RTSData;
import com.netease.nimlib.sdk.rts.model.RTSNotifyOption;
import com.netease.nimlib.sdk.rts.model.RTSOnlineAckEvent;
import com.netease.nimlib.sdk.rts.model.RTSOptions;
import com.netease.nimlib.sdk.rts.model.RTSTunData;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * ����/���ܻỰ����
 * <p/>
 * Created by huangjun on 2015/7/27.
 */
public class RTSActivity extends TActionBarActivity implements View.OnClickListener {

    public static final int FROM_BROADCAST_RECEIVER = 0; // ���Թ㲥
    public static final int FROM_INTERNAL = 1; // ���Է���
    private static final String KEY_RTS_DATA = "KEY_RTS_DATA";
    private static final String KEY_INCOMING = "KEY_INCOMING";
    private static final String KEY_SOURCE = "KEY_SOURCE";
    private static final String KEY_UID = "KEY_UID";

    // data
    private boolean isIncoming = false;
    private String account;      // �Է��ʺ�
    private String sessionId;    // �Ự��Ψһ��ʶ
    private RTSData sessionInfo; // ���λỰ����Ϣ
    private boolean audioOpen = false; // ����Ĭ��
    private boolean finishFlag = false; // ������ǣ������λص�onFinish
    private static boolean needFinish = true; // Activity���ٺ󣬴���������б�ָ�����finish

    private static boolean isBusy = false;

    // ����Ự����
    private View startSessionLayout;
    private TextView sessionStepText;
    private HeadImageView headImage;
    private TextView nameText;
    private View calleeAckLayout;
    private Button acceptBtn;
    private Button rejectBtn;
    private Button endSessionBtn;
    private Button audioSwitchBtn;

    // �װ岼��
    private View sessionLayout;
    private DoodleView doodleView;
    private Button backBtn;
    private Button clearBtn;

    public static void incomingSession(Context context, RTSData data, int source) {

        if(isBusy) {
            RTSManager.getInstance().close(data.getSessionId(), null);
            Toast.makeText(context, "close session", Toast.LENGTH_SHORT).show();
            return;
        }

        needFinish = false;
        Intent intent = new Intent();
        intent.setClass(context, RTSActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(KEY_RTS_DATA, data);
        intent.putExtra(KEY_INCOMING, true);
        intent.putExtra(KEY_SOURCE, source);
        context.startActivity(intent);
    }

    public static void startSession(Context context, String account, int source) {
        needFinish = false;
        Intent intent = new Intent();
        intent.setClass(context, RTSActivity.class);
        intent.putExtra(KEY_UID, account);
        intent.putExtra(KEY_INCOMING, false);
        intent.putExtra(KEY_SOURCE, source);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (needFinish) {
            finish();
            return;
        }

        isBusy = true;

        setContentView(R.layout.rts_activity);
        isIncoming = getIntent().getBooleanExtra(KEY_INCOMING, false);
        findViews();
        initActionBarButton();

        if (isIncoming) {
            incoming();
            registerInComingObserver(true);
        } else {
            outgoing();
            registerOutgoingObserver(true);
        }

        initAudioSwitch();
        registerCommonObserver(true);
    }

    private void initActionBarButton() {
        TextView closeSessionBtn = ActionBarUtil.addRightClickableTextViewOnActionBar(this, getString(R.string.close));
        closeSessionBtn.setBackgroundResource(R.drawable.nim_message_button_bottom_send_selector);
        closeSessionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EasyAlertDialogHelper.OnDialogActionListener listener = new EasyAlertDialogHelper.OnDialogActionListener() {

                    @Override
                    public void doCancelAction() {
                    }

                    @Override
                    public void doOkAction() {
                        endSession(); // �Ҷ�
                    }
                };
                final EasyAlertDialog dialog = EasyAlertDialogHelper.createOkCancelDiolag(RTSActivity.this,
                        getString(R.string.end_session_tip_head),
                        getString(R.string.end_session_tip_content),
                        getString(R.string.ok), getString(R.string.cancel), true, listener);
                dialog.show();
            }
        });
    }

    @Override
    public void onBackPressed() {
    }

    @Override
    protected boolean displayHomeAsUpEnabled() {
        return false;
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();

        // ������Ҫ�ػ�
        doodleView.onResume();
    }

    @Override
    protected void onDestroy() {
        if (doodleView != null) {
            doodleView.end();
        }

        super.onDestroy();
        registerInComingObserver(false);
        registerOutgoingObserver(false);
        registerCommonObserver(false);

        needFinish = true;

        isBusy = false;
    }

    private void findViews() {
        startSessionLayout = findViewById(R.id.start_session_layout);
        sessionLayout = findViewById(R.id.session_layout);

        headImage = (HeadImageView) findViewById(R.id.head_image);
        sessionStepText = (TextView) findViewById(R.id.session_step_text);
        nameText = (TextView) findViewById(R.id.name);
        calleeAckLayout = findViewById(R.id.callee_ack_layout);
        acceptBtn = (Button) findViewById(R.id.accept);
        rejectBtn = (Button) findViewById(R.id.reject);
        endSessionBtn = (Button) findViewById(R.id.end_session);
        doodleView = (DoodleView) findViewById(R.id.doodle_view);
        backBtn = (Button) findViewById(R.id.doodle_back);
        clearBtn = (Button) findViewById(R.id.doodle_clear);
        audioSwitchBtn = (Button) findViewById(R.id.audio_switch);

        acceptBtn.setOnClickListener(this);
        rejectBtn.setOnClickListener(this);
        endSessionBtn.setOnClickListener(this);
        audioSwitchBtn.setOnClickListener(this);
        backBtn.setOnClickListener(this);
        clearBtn.setOnClickListener(this);

        float screenWidth = ScreenUtil.screenWidth * 1.0f;
        ViewGroup.LayoutParams params = doodleView.getLayoutParams();
        params.width = ((int) (screenWidth / 100)) * 100; // ����ĻС��100������
        params.height = params.width; // ��֤��߱�Ϊ1:1
        doodleView.setLayoutParams(params);
    }

    private void incoming() {
        sessionInfo = (RTSData) getIntent().getSerializableExtra(KEY_RTS_DATA);
        account = sessionInfo.getAccount();
        sessionId = sessionInfo.getSessionId();

        Toast.makeText(RTSActivity.this, "incoming session, extra=" + sessionInfo.getExtra(),
                Toast.LENGTH_SHORT)
                .show();
        initIncomingSessionViews();
    }

    private void outgoing() {
        account = getIntent().getStringExtra(KEY_UID);

        initStartSessionViews();
        startSession();
    }

    private void initStartSessionViews() {
        initAccountInfoView();
        sessionStepText.setText(R.string.start_session);
        calleeAckLayout.setVisibility(View.GONE);
        endSessionBtn.setVisibility(View.VISIBLE);
        startSessionLayout.setVisibility(View.VISIBLE);
    }

    private void initIncomingSessionViews() {
        initAccountInfoView();
        sessionStepText.setText(R.string.receive_session);
        calleeAckLayout.setVisibility(View.VISIBLE);
        endSessionBtn.setVisibility(View.GONE);
        startSessionLayout.setVisibility(View.VISIBLE);
    }

    private void initAccountInfoView() {
        nameText.setText(NimUserInfoCache.getInstance().getUserDisplayName(account));
        headImage.loadBuddyAvatar(account);
    }

    private void registerOutgoingObserver(boolean register) {
        RTSManager.getInstance().observeCalleeAckNotification(sessionId, calleeAckEventObserver, register);
    }

    private void registerInComingObserver(boolean register) {
        RTSManager.getInstance().observeOnlineAckNotification(sessionId, onlineAckObserver, register);
    }

    private void registerCommonObserver(boolean register) {
        RTSManager.getInstance().observeChannelState(sessionId, channelStateObserver, register);
        RTSManager.getInstance().observeHangUpNotification(sessionId, endSessionObserver, register);
        RTSManager.getInstance().observeReceiveData(sessionId, receiveDataObserver, register);
        RTSManager.getInstance().observeTimeoutNotification(sessionId, timeoutObserver, register);
        RTSManager.getInstance().observeControlNotification(sessionId, controlObserver, register);
    }

    /**
     * ���з��������з��Ľ���or�ܾ��Ự����Ӧ
     */
    private Observer<RTSCalleeAckEvent> calleeAckEventObserver = new Observer<RTSCalleeAckEvent>() {
        @Override
        public void onEvent(RTSCalleeAckEvent rtsCalleeAckEvent) {
            if (rtsCalleeAckEvent.getEvent() == RTSEventType.CALLEE_ACK_AGREE) {
                // �ж�SDK�Զ�����ͨ���Ƿ�ɹ�
                if (!rtsCalleeAckEvent.isTunReady()) {
                    Toast.makeText(RTSActivity.this, "ͨ������ʧ��!��鿴LOG", Toast.LENGTH_SHORT).show();
                    return;
                }
                acceptView(); // ����Ự����
            } else if (rtsCalleeAckEvent.getEvent() == RTSEventType.CALLEE_ACK_REJECT) {
                Toast.makeText(RTSActivity.this, R.string.callee_reject, Toast.LENGTH_SHORT).show();
                onFinish(false);
            }
        }
    };

    /**
     * �����Է��Ҷ�
     */
    private Observer<RTSCommonEvent> endSessionObserver = new Observer<RTSCommonEvent>() {
        @Override
        public void onEvent(RTSCommonEvent rtsCommonEvent) {
            Toast.makeText(RTSActivity.this, R.string.target_has_end_session, Toast.LENGTH_SHORT).show();
            onFinish(false);
        }
    };

    /**
     * �����յ��Է����͵�ͨ������
     */
    private Observer<RTSTunData> receiveDataObserver = new Observer<RTSTunData>() {
        @Override
        public void onEvent(RTSTunData rtsTunData) {
            String data = "[parse bytes error]";
            try {
                data = new String(rtsTunData.getData(), 0, rtsTunData.getLength(), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            TransactionCenter.getInstance().onReceive(sessionId, data);
        }
    };

    /**
     * ���з��������������˵Ľ�����Ӧ
     */
    private Observer<RTSOnlineAckEvent> onlineAckObserver = new Observer<RTSOnlineAckEvent>() {
        @Override
        public void onEvent(RTSOnlineAckEvent rtsOnlineAckEvent) {
            if (rtsOnlineAckEvent.getClientType() != ClientType.Android) {
                String client = null;
                switch (rtsOnlineAckEvent.getClientType()) {
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
                    String option = rtsOnlineAckEvent.getEvent() == RTSEventType.CALLEE_ONLINE_CLIENT_ACK_AGREE ?
                            "����" : "�ܾ�";
                    Toast.makeText(RTSActivity.this, "�װ���ʾ����" + client + "�˱�" + option, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(RTSActivity.this, "�װ���ʾ���������˴���", Toast.LENGTH_SHORT).show();
                }
                onFinish();
            }
        }
    };

    /**
     * ����������Ϣ
     */
    private Observer<RTSControlEvent> controlObserver = new Observer<RTSControlEvent>() {
        @Override
        public void onEvent(RTSControlEvent rtsControlEvent) {
            Toast.makeText(RTSActivity.this, rtsControlEvent.getCommandInfo(), Toast.LENGTH_SHORT).show();
        }
    };

    /**
     * ������ǰ�Ự��״̬
     */
    private RTSChannelStateObserver channelStateObserver = new RTSChannelStateObserver() {

        @Override
        public void onConnectResult(RTSTunType tunType, int code) {
            Toast.makeText(RTSActivity.this, "onConnectResult, tunType=" + tunType.toString() + ", code=" +
                    code, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onRecordInfo(RTSTunType tunType, String url, String name) {
            String tip = "onRecordInfo, tunType=" + tunType.toString() + ", url=" + url + ", file name=" + name;
            Toast.makeText(RTSActivity.this, tip, Toast.LENGTH_SHORT).show();
            LogUtil.i("RTS", tip);
        }

        @Override
        public void onChannelEstablished(RTSTunType tunType) {
            Toast.makeText(RTSActivity.this, "onCallEstablished,tunType=" + tunType.toString(), Toast
                    .LENGTH_SHORT).show();

            if (tunType == RTSTunType.AUDIO) {
                RTSManager.getInstance().setSpeaker(sessionId, true); // Ĭ�Ͽ���������
            }
        }

        @Override
        public void onDisconnectServer(RTSTunType tunType) {
            Toast.makeText(RTSActivity.this, "onDisconnectServer, tunType=" + tunType.toString(), Toast
                    .LENGTH_SHORT).show();
            if (tunType == RTSTunType.TCP) {
                // �������ͨ�����ˣ���ô�رջỰ
                Toast.makeText(RTSActivity.this, "TCPͨ���Ͽ����Զ������Ự", Toast.LENGTH_SHORT).show();
                endSession();
            } else if (tunType == RTSTunType.AUDIO) {
                // �����Ƶͨ�����ˣ���ôUI�任
                if (audioOpen) {
                    audioSwitch();
                }
            }
        }

        @Override
        public void onError(RTSTunType tunType, int code) {
            Toast.makeText(RTSActivity.this, "onError, tunType=" + tunType.toString() + ", error=" + code,
                    Toast.LENGTH_LONG).show();
            endSession();
        }

        @Override
        public void onNetworkStatusChange(RTSTunType tunType, int value) {
            // �����ź�ǿ��
        }
    };

    private Observer<RTSTimeOutEvent> timeoutObserver = new Observer<RTSTimeOutEvent>() {
        @Override
        public void onEvent(RTSTimeOutEvent rtsTimeOutEvent) {
            Toast.makeText(RTSActivity.this,
                    (rtsTimeOutEvent == RTSTimeOutEvent.OUTGOING_TIMEOUT) ? getString(R.string.callee_ack_timeout) :
                            "��ʱδ�����Զ�����", Toast.LENGTH_SHORT).show();
            onFinish();
        }
    };

    private void startSession() {
        List<RTSTunType> types = new ArrayList<>(1);
        types.add(RTSTunType.AUDIO);
        types.add(RTSTunType.TCP);

        String pushContent = account + "����һ���Ự";
        String extra = "extra_data";
        RTSOptions options = new RTSOptions().setRecordAudioTun(true)
                .setRecordTCPTun(true);
        RTSNotifyOption notifyOption = new RTSNotifyOption();
        notifyOption.apnsContent = pushContent;
        notifyOption.extendMessage = extra;

        sessionId = RTSManager.getInstance().start(account, types, options, notifyOption, new RTSCallback<RTSData>() {
            @Override
            public void onSuccess(RTSData rtsData) {
                RTSAttachment attachment = new RTSAttachment((byte) 0);
                IMMessage msg = MessageBuilder.createCustomMessage(account, SessionTypeEnum.P2P, attachment.getContent(), attachment);
                MessageListPanelHelper.getInstance().notifyAddMessage(msg); // ������addһ��
                NIMClient.getService(MsgService.class).sendMessage(msg, false); // ���͸��Է�
            }

            @Override
            public void onFailed(int code) {
                if (code == 11001) {
                    Toast.makeText(RTSActivity.this, "�޿��ʹ�ı��з�", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(RTSActivity.this, "����Ựʧ��,code=" + code, Toast.LENGTH_SHORT).show();
                }
                onFinish();
            }

            @Override
            public void onException(Throwable exception) {
                Toast.makeText(RTSActivity.this, "����Ự�쳣,e=" + exception.toString(), Toast.LENGTH_SHORT).show();
                onFinish();
            }
        });

        if (sessionId == null) {
            Toast.makeText(RTSActivity.this, "����Ựʧ��,��Ƶͨ��ͬʱֻ����һ���Ự����", Toast.LENGTH_SHORT).show();
            onFinish();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.accept:
                acceptSession();
                break;
            case R.id.reject:
                endSession();
                break;
            case R.id.end_session:
                endSession();
                break;
            case R.id.doodle_back:
                doodleBack();
                break;
            case R.id.doodle_clear:
                clear();
                break;
            case R.id.audio_switch:
                audioSwitch();
                break;
            default:
                break;
        }
    }

    private void acceptSession() {
        RTSOptions options = new RTSOptions().setRecordAudioTun(true).setRecordTCPTun(true);
        RTSManager.getInstance().accept(sessionId, options, new RTSCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean success) {
                // �жϿ���ͨ���Ƿ�ɹ�
                if (success) {
                    acceptView();
                } else {
                    Toast.makeText(RTSActivity.this, "ͨ������ʧ��!��鿴LOG", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailed(int code) {
                if (code == -1) {
                    Toast.makeText(RTSActivity.this, "���ܻỰʧ��,��Ƶͨ��ͬʱֻ����һ���Ự����", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(RTSActivity.this, "���ܻỰʧ��, code=" + code, Toast.LENGTH_SHORT).show();
                }
                onFinish();
            }

            @Override
            public void onException(Throwable exception) {
                Toast.makeText(RTSActivity.this, "���ܻỰ�쳣, e=" + exception.toString(), Toast.LENGTH_SHORT).show();
                onFinish();
            }
        });
    }

    private void acceptView() {
        startSessionLayout.setVisibility(View.GONE);
        sessionLayout.setVisibility(View.VISIBLE);
        initDoodleView();


    }

    private void endSession() {
        RTSManager.getInstance().close(sessionId, new RTSCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

            }

            @Override
            public void onFailed(int code) {
                Toast.makeText(RTSActivity.this, "�Ҷ��������code��" + code, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onException(Throwable exception) {

            }
        });

        onFinish();
    }

    private void onFinish() {
        onFinish(true);
    }

    private void onFinish(boolean selfFinish) {
        if (finishFlag) {
            return;
        }
        finishFlag = true;

        RTSAttachment attachment = new RTSAttachment((byte) 1);

        IMMessage msg = MessageBuilder.createCustomMessage(account, SessionTypeEnum.P2P, attachment.getContent(), attachment);
        if (!selfFinish) {
            // �������Ự��������ģ��һ�����յ���Ϣ
            msg.setFromAccount(account);
            msg.setDirect(MsgDirectionEnum.In);
        }

        msg.setStatus(MsgStatusEnum.success);

        NIMClient.getService(MsgService.class).saveMessageToLocal(msg, true);

        finish();
    }

    /**
     * ***************************** ���� ***********************************
     */
    private void initDoodleView() {
        // add support ActionType
        SupportActionType.getInstance().addSupportActionType(ActionTypeEnum.Path.getValue(), MyPath.class);

        doodleView.init(sessionId, account, DoodleView.Mode.BOTH, Color.WHITE, this);

        doodleView.setPaintSize(10);
        doodleView.setPaintType(ActionTypeEnum.Path.getValue());

        // adjust paint offset
        new Handler(getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                TypedArray actionbarSizeTypedArray = obtainStyledAttributes(new int[]{android.R.attr.actionBarSize});
                float actionBarHeight = actionbarSizeTypedArray.getDimension(0, 0);
                Log.i("Doodle", "actionBarHeight =" + actionBarHeight);

                Rect frame = new Rect();
                getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
                int statusBarHeight = frame.top;
                Log.i("Doodle", "statusBarHeight =" + statusBarHeight);

                int marginTop = doodleView.getTop();
                Log.i("Doodle", "doodleView marginTop =" + marginTop);

                int marginLeft = doodleView.getLeft();
                Log.i("Doodle", "doodleView marginLeft =" + marginLeft);

                float offsetX = marginLeft;
                float offsetY = actionBarHeight + statusBarHeight + marginTop;

                doodleView.setPaintOffset(offsetX, offsetY);
                Log.i("Doodle", "client1 offsetX = " + offsetX + ", offsetY = " + offsetY);
            }
        }, 50);
    }

    /**
     * ����һ��
     */
    private void doodleBack() {
        doodleView.paintBack();
    }

    /**
     * ����
     */
    private void clear() {
        doodleView.clear();
    }

    /**
     * ��������
     */
    private void audioSwitch() {
        audioOpen = !audioOpen;
        RTSManager.getInstance().setMute(sessionId, !audioOpen);
        audioSwitchBtn.setBackgroundResource(audioOpen ? R.drawable.icon_audio_open : R.drawable.icon_audio_close);

        // ͨ������Э��֪ͨ�Է�
        String content = "�Է�����" + (audioOpen ? "�ر�" : "����");
        RTSManager.getInstance().sendControlCommand(sessionId, content, new RTSCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                String tip = "����" + (audioOpen ? "�ر�" : "����");
                Toast.makeText(RTSActivity.this, tip, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailed(int code) {
                Toast.makeText(RTSActivity.this, "����Э�鷢��ʧ��, code =" + code, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onException(Throwable exception) {
                Toast.makeText(RTSActivity.this, "����Э�鷢���쳣, e=" + exception.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * ��ʼ����������(Ĭ�Ϲر�)
     */
    private void initAudioSwitch() {
        RTSManager.getInstance().setMute(sessionId, true);
        audioSwitchBtn.setBackgroundResource(R.drawable.icon_audio_close);
    }
}
