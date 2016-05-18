package com.netease.nim.uikit;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.netease.nim.uikit.cache.DataCacheManager;
import com.netease.nim.uikit.cache.TeamDataCache;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.netease.nim.uikit.common.util.storage.StorageType;
import com.netease.nim.uikit.common.util.storage.StorageUtil;
import com.netease.nim.uikit.common.util.sys.ScreenUtil;
import com.netease.nim.uikit.contact.ContactEventListener;
import com.netease.nim.uikit.contact.ContactProvider;
import com.netease.nim.uikit.contact_selector.activity.ContactSelectActivity;
import com.netease.nim.uikit.samwraper.SendQuestionWraper;
import com.netease.nim.uikit.session.SessionCustomization;
import com.netease.nim.uikit.session.SessionEventListener;
import com.netease.nim.uikit.session.activity.P2PMessageActivity;
import com.netease.nim.uikit.session.activity.TeamMessageActivity;
import com.netease.nim.uikit.session.emoji.StickerManager;
import com.netease.nim.uikit.session.viewholder.MsgViewHolderBase;
import com.netease.nim.uikit.session.viewholder.MsgViewHolderFactory;
import com.netease.nim.uikit.team.activity.AdvancedTeamInfoActivity;
import com.netease.nim.uikit.team.activity.NormalTeamInfoActivity;
import com.netease.nim.uikit.uinfo.UserInfoHelper;
import com.netease.nimlib.sdk.msg.attachment.MsgAttachment;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.team.constant.TeamTypeEnum;
import com.netease.nimlib.sdk.team.model.Team;
import com.netease.nimlib.sdk.uinfo.UserInfoProvider;

import java.util.List;

/**
 * UIKit��������ࡣ
 */
public final class NimUIKit {

    // context
    private static Context context;

    // �Լ����û��ʺ�
    private static String account;

    // �û���Ϣ�ṩ��
    private static UserInfoProvider userInfoProvider;

    // ͨѶ¼��Ϣ�ṩ��
    private static ContactProvider contactProvider;

    // ����λ����Ϣ�ṩ��
    private static LocationProvider locationProvider;

    // ͼƬ���ء�������������
    private static ImageLoaderKit imageLoaderKit;

    // �Ự������Ϣ�б�һЩ����¼�����Ӧ������
    private static SessionEventListener sessionListener;

    // ͨѶ¼�б�һЩ����¼�����Ӧ������
    private static ContactEventListener contactEventListener;

    /**
     * ��ʼ��UIKit���봫��context�Լ��û���Ϣ�ṩ��
     *
     * @param context          ������
     * @param userInfoProvider �û���Ϣ�ṩ��
     * @param contactProvider  ͨѶ¼��Ϣ�ṩ��
     */
    public static void init(Context context, UserInfoProvider userInfoProvider, ContactProvider contactProvider) {
        NimUIKit.context = context.getApplicationContext();
        NimUIKit.userInfoProvider = userInfoProvider;
        NimUIKit.contactProvider = contactProvider;
        NimUIKit.imageLoaderKit = new ImageLoaderKit(context, null);

        // sync listener register
        LoginSyncDataStatusObserver.getInstance().registerLoginSyncDataStatus(true);  // ������¼ͬ���������֪ͨ
        //observer for friend data, user info and team data
        DataCacheManager.observeSDKDataChanged(true);
        //build first version of data cache even sync data not finished
        if (!TextUtils.isEmpty(getAccount())) {
            DataCacheManager.buildDataCache(); // build data cache on auto login
        }

        // init tools
        StorageUtil.init(context, null);
        ScreenUtil.init(context);
        StickerManager.getInstance().init();

        // init log
        String path = StorageUtil.getDirectoryByDirType(StorageType.TYPE_LOG);
        LogUtil.init(path, Log.DEBUG);
    }


/* how many observer register
1. login sync observer
2. Friend data observer
3. Team data observer
4. User info data observer
*/

    /**
     * �ͷŻ��棬һ����ע��ʱ����
     */
    public static void clearCache() {
        DataCacheManager.clearDataCache();
    }

    /**
     * ��һ�����촰�ڣ���ʼ����
     *
     * @param context       ������
     * @param id            �������ID���û��ʺ�account����Ⱥ��ID��
     * @param sessionType   �Ự����
     * @param customization ���ƻ���Ϣ����Բ�ͬ��������󣬿��ṩ��ͬ�Ķ��ƻ���
     */
    public static void startChatting(Context context, String id, SessionTypeEnum sessionType, SessionCustomization customization) {
        if (sessionType == SessionTypeEnum.P2P) {
            P2PMessageActivity.start(context, id, customization);
        } else if (sessionType == SessionTypeEnum.Team) {
            TeamMessageActivity.start(context, id, customization, null);
        }
    }

    /*SAMC_BEGIN()*/
    public static void startChatting(Context context, String id, SessionTypeEnum sessionType, int from_which_window,SessionCustomization customization) {
        if (sessionType == SessionTypeEnum.P2P) {
            P2PMessageActivity.start(context, id, from_which_window,customization);
        } else if (sessionType == SessionTypeEnum.Team) {
            TeamMessageActivity.start(context, id, customization, null);
        }
    }
    /*SAMC_END()*/

    /**
     * ��һ�����촰�ڣ����ڴ�������Ϣ�д���Ⱥ��ʱ����Ⱥ�ģ�
     *
     * @param context       ������
     * @param id            �������ID���û��ʺ�account����Ⱥ��ID��
     * @param sessionType   �Ự����
     * @param customization ���ƻ���Ϣ����Բ�ͬ��������󣬿��ṩ��ͬ�Ķ��ƻ���
     * @param backToClass   ���ص�ָ��ҳ��
     */
    public static void startChatting(Context context, String id, SessionTypeEnum sessionType, SessionCustomization customization,
                                     Class<? extends Activity> backToClass) {
        if (sessionType == SessionTypeEnum.Team) {
            TeamMessageActivity.start(context, id, customization, backToClass);
        }
    }

    /**
     * ����ϵ��ѡ����
     *
     * @param context     �����ģ�Activity��
     * @param option      ��ϵ��ѡ������ѡ������
     * @param requestCode startActivityForResultʹ�õ�������
     */
    public static void startContactSelect(Context context, ContactSelectActivity.Option option, int requestCode) {
        ContactSelectActivity.startActivityForResult(context, option, requestCode);
    }

    /**
     * ���������߼�Ⱥ����ҳ
     *
     * @param context ������
     * @param teamId  Ⱥid
     */
    public static void startTeamInfo(Context context, String teamId) {
        Team team = TeamDataCache.getInstance().getTeamById(teamId);
        if (team == null) {
            return;
        }
        if (team.getType() == TeamTypeEnum.Advanced) {
            AdvancedTeamInfoActivity.start(context, teamId); // �����̶�Ⱥ����ҳ
        } else if (team.getType() == TeamTypeEnum.Normal) {
            NormalTeamInfoActivity.start(context, teamId); // ��������������ҳ
        }

    }

    public static Context getContext() {
        return context;
    }

    public static String getAccount() {
        return account;
    }

    public static UserInfoProvider getUserInfoProvider() {
        return userInfoProvider;
    }

    public static ContactProvider getContactProvider() {
        return contactProvider;
    }

    public static LocationProvider getLocationProvider() {
        return locationProvider;
    }

    public static ImageLoaderKit getImageLoaderKit() {
        return imageLoaderKit;
    }

    public static void setLocationProvider(LocationProvider locationProvider) {
        NimUIKit.locationProvider = locationProvider;
    }

    /**
     * ������Ϣ��������ע���Ӧ����Ϣ��չʾViewHolder
     *
     * @param attach     ��������
     * @param viewHolder ��ϢViewHolder
     */
    public static void registerMsgItemViewHolder(Class<? extends MsgAttachment> attach, Class<? extends MsgViewHolderBase> viewHolder) {
        MsgViewHolderFactory.register(attach, viewHolder);
    }

    /**
     * ע��Tip������Ϣ��չʾViewHolder
     * @param viewHolder Tip��ϢViewHolder
     */
    public static void registerTipMsgViewHolder(Class<? extends MsgViewHolderBase> viewHolder) {
        MsgViewHolderFactory.registerTipMsgViewHolder(viewHolder);
    }

    /**
     * ���õ�ǰ��¼�û����ʺ�
     *
     * @param account �ʺ�
     */
    public static void setAccount(String account) {
        NimUIKit.account = account;
    }

    /**
     * ��ȡ��������¼�������
     *
     * @return
     */
    public static SessionEventListener getSessionListener() {
        return sessionListener;
    }

    /**
     * �������������¼�������
     *
     * @param sessionListener
     */
    public static void setSessionListener(SessionEventListener sessionListener) {
        NimUIKit.sessionListener = sessionListener;
    }

    /**
     * ��ȡͨѶ¼�б���¼�������
     *
     * @return
     */
    public static ContactEventListener getContactEventListener() {
        return contactEventListener;
    }

    /**
     * ����ͨѶ¼�б���¼�������
     *
     * @param contactEventListener
     */
    public static void setContactEventListener(ContactEventListener contactEventListener) {
        NimUIKit.contactEventListener = contactEventListener;
    }

    /**
     * ���û����Ϸ����Ķ�ʱ������ô˽ӿڣ�֪ͨ����UI
     *
     * @param accounts ���û���Ϣ�Ķ����ʺ��б�
     */
    public static void notifyUserInfoChanged(List<String> accounts) {
        UserInfoHelper.notifyChanged(accounts);
    }


    /*SAMC_BEGIN()*/
    public static SamServiceListener samListner;
    public static void registerSamServiceListener(SamServiceListener listner){
        samListner = listner;
    }

    public static interface SamServiceListener{
        List<SendQuestionWraper> getSendQuestionWraper(String responser);
        List<String> getNotResponsedQuestion(String sender);
        String getAvatar(String account);
    }
	
    /*SAMC_END*/
}
