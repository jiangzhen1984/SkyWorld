package com.netease.nim.demo;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.android.samservice.SamService;
import com.android.samservice.info.ContactUser;
import com.android.samservice.info.LoginUser;
import com.android.samservice.info.RespQuest;
import com.netease.nim.demo.avchat.AVChatProfile;
import com.netease.nim.demo.avchat.activity.AVChatActivity;
import com.netease.nim.demo.common.util.crash.AppCrashHandler;
import com.netease.nim.demo.common.util.sys.SystemUtil;
import com.netease.nim.demo.config.ExtraOptions;
import com.netease.nim.demo.config.preference.Preferences;
import com.netease.nim.demo.config.preference.UserPreferences;
import com.netease.nim.demo.contact.ContactHelper;
import com.netease.nim.demo.main.activity.WelcomeActivity;
import com.netease.nim.demo.rts.activity.RTSActivity;
import com.netease.nim.demo.session.NimDemoLocationProvider;
import com.netease.nim.demo.session.SessionHelper;
import com.netease.nim.uikit.ImageLoaderKit;
import com.netease.nim.uikit.NimMobHelper;
import com.netease.nim.uikit.NimUIKit;
import com.netease.nim.uikit.NimUIKit.SamServiceListener;
import com.netease.nim.uikit.cache.FriendDataCache;
import com.netease.nim.uikit.cache.NimUserInfoCache;
import com.netease.nim.uikit.cache.TeamDataCache;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.netease.nim.uikit.contact.ContactProvider;
import com.netease.nim.uikit.contact.core.query.PinYin;
import com.netease.nim.uikit.session.viewholder.MsgViewHolderThumbBase;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.NimStrings;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.SDKOptions;
import com.netease.nimlib.sdk.StatusBarNotificationConfig;
import com.netease.nimlib.sdk.auth.LoginInfo;
import com.netease.nimlib.sdk.avchat.AVChatManager;
import com.netease.nimlib.sdk.avchat.model.AVChatData;
import com.netease.nimlib.sdk.avchat.model.AVChatRingerConfig;
import com.netease.nimlib.sdk.msg.MessageNotifierCustomization;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.netease.nimlib.sdk.rts.RTSManager;
import com.netease.nimlib.sdk.rts.model.RTSData;
import com.netease.nimlib.sdk.uinfo.UserInfoProvider;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;
import com.netease.nim.uikit.samwraper.SendQuestionWraper;

import java.util.ArrayList;
import java.util.List;

public class NimApplication extends Application {

    public void onCreate() {
        super.onCreate();

        DemoCache.setContext(this);

        /*SAMC_BEGIN()*/
        DemoCache.setApp(this);
	 //not auto login Nim
	 //if(!inMainProcess()){
	 	NIMClient.init(this, getLoginInfo(), getOptions()); 
	 //}
        /*SAMC_END()*/

        ExtraOptions.provide();

        // crash handler
        AppCrashHandler.getInstance(this);

    }

    /*SAMC_BEGIN()*/
    public void NimInit(){
            NIMClient.init(this, getLoginInfo(), getOptions());
            // init pinyin
            PinYin.init(this);
            PinYin.validate();

            // ��ʼ��UIKitģ��
            initUIKit();

            NimMobHelper.getInstance().init(DemoCache.getApp());

            // ��ʼ����Ϣ����
            NIMClient.toggleNotification(UserPreferences.getNotificationToggle());

            // ע������ͨ������
            enableAVChat();

            // ע��װ�Ự
            enableRTS();

            // ע�����Ա仯����
            registerLocaleReceiver(true);
        
    }    
    /*SAMC_END()*/

    private LoginInfo getLoginInfo() {
        String account = Preferences.getUserAccount();
        String token = Preferences.getUserToken();

        if (!TextUtils.isEmpty(account) && !TextUtils.isEmpty(token)) {
            DemoCache.setAccount(account.toLowerCase());
            return new LoginInfo(account, token);
        } else {
            return null;
        }
    }

    private SDKOptions getOptions() {
        SDKOptions options = new SDKOptions();

        // ���������Ϣ֪ͨ�����йܸ�SDK��ɣ���Ҫ����������á�
        StatusBarNotificationConfig config = UserPreferences.getStatusConfig();
        if (config == null) {
            config = new StatusBarNotificationConfig();
        }
        // ���֪ͨ��Ҫ��ת���Ľ���
        config.notificationEntrance = WelcomeActivity.class;
        config.notificationSmallIconId = R.drawable.ic_stat_notify_msg;

        // ֪ͨ������uri�ַ���
        config.notificationSound = "android.resource://com.netease.nim.demo/raw/msg";
        options.statusBarNotificationConfig = config;
        DemoCache.setNotificationConfig(config);
        UserPreferences.setStatusConfig(config);

        // ���ñ���ͼƬ���ļ���log�����ݵ�Ŀ¼
        String sdkPath = Environment.getExternalStorageDirectory() + "/" + getPackageName() + "/nim";
        options.sdkStorageRootPath = sdkPath;

        // �������ݿ������Կ
        options.databaseEncryptKey = "NETEASE";

        // �����Ƿ���ҪԤ���ظ�������ͼ
        options.preloadAttach = true;

        // ���ø�������ͼ�ĳߴ��С��
        options.thumbnailSize = MsgViewHolderThumbBase.getImageMaxEdge();

        // �û���Ϣ�ṩ��
        options.userInfoProvider = infoProvider;

        // ����֪ͨ�������İ�����ѡ����������ƽ�����SDKĬ���İ���
        options.messageNotifierCustomization = messageNotifierCustomization;

        return options;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    public boolean inMainProcess() {
        String packageName = getPackageName();
        String processName = SystemUtil.getProcessName(this);
        return packageName.equals(processName);
    }

    /**
     * ����Ƶͨ�����������
     */
    private void enableAVChat() {
        setupAVChat();
        registerAVChatIncomingCallObserver(true);
    }

    private void setupAVChat() {
        AVChatRingerConfig config = new AVChatRingerConfig();
        config.res_connecting = R.raw.avchat_connecting;
        config.res_no_response = R.raw.avchat_no_response;
        config.res_peer_busy = R.raw.avchat_peer_busy;
        config.res_peer_reject = R.raw.avchat_peer_reject;
        config.res_ring = R.raw.avchat_ring;
        AVChatManager.getInstance().setRingerConfig(config); // ������������
    }

    private void registerAVChatIncomingCallObserver(boolean register) {
        AVChatManager.getInstance().observeIncomingCall(new Observer<AVChatData>() {
            @Override
            public void onEvent(AVChatData data) {
                String extra = data.getExtra();
                Log.e("Extra", "Extra Message->" + extra);
                // �����������AVChatActivity
                AVChatProfile.getInstance().setAVChatting(true);
                AVChatActivity.launch(DemoCache.getContext(), data, AVChatActivity.FROM_BROADCASTRECEIVER);
            }
        }, register);
    }

    /**
     * �װ�ʵʱʱ�Ự���������
     */
    private void enableRTS() {
        //setupRTS();
        registerRTSIncomingObserver(true);
    }

//    private void setupRTS() {
//        RTSRingerConfig config = new RTSRingerConfig();
//        config.res_connecting = R.raw.avchat_connecting;
//        config.res_no_response = R.raw.avchat_no_response;
//        config.res_peer_busy = R.raw.avchat_peer_busy;
//        config.res_peer_reject = R.raw.avchat_peer_reject;
//        config.res_ring = R.raw.avchat_ring;
//        RTSManager.getInstance().setRingerConfig(config); // ������������
//    }

    private void registerRTSIncomingObserver(boolean register) {
        RTSManager.getInstance().observeIncomingSession(new Observer<RTSData>() {
            @Override
            public void onEvent(RTSData rtsData) {
                RTSActivity.incomingSession(DemoCache.getContext(), rtsData, RTSActivity.FROM_BROADCAST_RECEIVER);
            }
        }, register);
    }

    private void registerLocaleReceiver(boolean register) {
        if (register) {
            updateLocale();
            IntentFilter filter = new IntentFilter(Intent.ACTION_LOCALE_CHANGED);
            registerReceiver(localeReceiver, filter);
        } else {
            unregisterReceiver(localeReceiver);
        }
    }

    private BroadcastReceiver localeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_LOCALE_CHANGED)) {
                updateLocale();
            }
        }
    };

    private void updateLocale() {
        NimStrings strings = new NimStrings();
        strings.status_bar_multi_messages_incoming = getString(R.string.nim_status_bar_multi_messages_incoming);
        strings.status_bar_image_message = getString(R.string.nim_status_bar_image_message);
        strings.status_bar_audio_message = getString(R.string.nim_status_bar_audio_message);
        strings.status_bar_custom_message = getString(R.string.nim_status_bar_custom_message);
        strings.status_bar_file_message = getString(R.string.nim_status_bar_file_message);
        strings.status_bar_location_message = getString(R.string.nim_status_bar_location_message);
        strings.status_bar_notification_message = getString(R.string.nim_status_bar_notification_message);
        strings.status_bar_ticker_text = getString(R.string.nim_status_bar_ticker_text);
        strings.status_bar_unsupported_message = getString(R.string.nim_status_bar_unsupported_message);
        strings.status_bar_video_message = getString(R.string.nim_status_bar_video_message);
        strings.status_bar_hidden_message_content = getString(R.string.nim_status_bar_hidden_msg_content);
        NIMClient.updateStrings(strings);
    }

    private void initUIKit() {
        NimUIKit.registerSamServiceListener(new SamServiceListener(){
            public List<SendQuestionWraper> getSendQuestionWraper(String responser){
                 List<SendQuestionWraper> sq_list = new ArrayList<SendQuestionWraper>();
                 LoginUser cuser = SamService.getInstance().get_current_user();
                 List<RespQuest> rq_list = SamService.getInstance().getDao().query_RespQuest_db(cuser.getusername(), responser);
                 for(RespQuest rq:rq_list){
                     SendQuestionWraper sq = SamService.getInstance().getDao().query_send_question_db_wraper(rq.question_id);
                     if(sq!=null){
                           sq_list.add(sq);
                     }
                 }

                  return sq_list;
            }

            public List<String> getNotResponsedQuestion(String sender){
                 LoginUser me = SamService.getInstance().get_current_user();
                 ContactUser cuser = SamService.getInstance().getDao().query_ContactUser_db_by_username(sender);
                 if(cuser == null){
                     return null;
                 }
	
                 List<String> questions = SamService.getInstance().getDao().get_ReceivedQuestion_Not_Response_db(cuser.getid(),me.getusername());

                 return questions;
             }
        });
		
        // ��ʼ������Ҫ�����û���Ϣ�ṩ��
        NimUIKit.init(this, infoProvider, contactProvider);

        // ���õ���λ���ṩ�ߡ������Ҫ���͵���λ����Ϣ���ò��������ṩ���������Ҫ�����Ժ��ԡ�
        NimUIKit.setLocationProvider(new NimDemoLocationProvider());

        // �Ự���ڵĶ��Ƴ�ʼ����
        SessionHelper.init();

        // ͨѶ¼�б��Ƴ�ʼ��
        ContactHelper.init();
    }

    private UserInfoProvider infoProvider = new UserInfoProvider() {
        @Override
        public UserInfo getUserInfo(String account) {
            UserInfo user = NimUserInfoCache.getInstance().getUserInfo(account);
            if (user == null) {
                NimUserInfoCache.getInstance().getUserInfoFromRemote(account, null);
            }

            return user;
        }

        @Override
        public int getDefaultIconResId() {
            return R.drawable.avatar_def;
        }

        @Override
        public Bitmap getTeamIcon(String teamId) {
            Drawable drawable = getResources().getDrawable(R.drawable.nim_avatar_group);
            if (drawable instanceof BitmapDrawable) {
                return ((BitmapDrawable) drawable).getBitmap();
            }

            return null;
        }

        @Override
        public Bitmap getAvatarForMessageNotifier(String account) {
            /**
             * ע�⣺������ôӻ������ã������ȡ����ͷ����ܵ���UI��������������֪ͨ��������ʱ������
             */
            UserInfo user = getUserInfo(account);
            return (user != null) ? ImageLoaderKit.getNotificationBitmapFromCache(user) : null;
        }

        @Override
        public String getDisplayNameForMessageNotifier(String account, String sessionId, SessionTypeEnum sessionType) {
            String nick = null;
            if (sessionType == SessionTypeEnum.P2P) {
                nick = NimUserInfoCache.getInstance().getAlias(account);
            } else if (sessionType == SessionTypeEnum.Team) {
                nick = TeamDataCache.getInstance().getTeamNick(sessionId, account);
                if (TextUtils.isEmpty(nick)) {
                    nick = NimUserInfoCache.getInstance().getAlias(account);
                }
            }
            // ����null������sdk��������Է�������nick��sdk����ʾnick
            if (TextUtils.isEmpty(nick)) {
                return null;
            }

            return nick;
        }
    };

    private ContactProvider contactProvider = new ContactProvider() {
        @Override
        public List<UserInfoProvider.UserInfo> getUserInfoOfMyFriends() {
            List<NimUserInfo> nimUsers = NimUserInfoCache.getInstance().getAllUsersOfMyFriend();
            List<UserInfoProvider.UserInfo> users = new ArrayList<>(nimUsers.size());
            if (!nimUsers.isEmpty()) {
                users.addAll(nimUsers);
            }

            return users;
        }

        @Override
        public int getMyFriendsCount() {
            return FriendDataCache.getInstance().getMyFriendCounts();
        }

        @Override
        public String getUserDisplayName(String account) {
            return NimUserInfoCache.getInstance().getUserDisplayName(account);
        }
    };

    private MessageNotifierCustomization messageNotifierCustomization = new MessageNotifierCustomization() {
        @Override
        public String makeNotifyContent(String nick, IMMessage message) {
            return null; // ����SDKĬ���İ�
        }

        @Override
        public String makeTicker(String nick, IMMessage message) {
            return null; // ����SDKĬ���İ�
        }
    };
}
