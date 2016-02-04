package com.easemob.easeui.controller;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

import com.easemob.EMEventListener;
import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMChatOptions;
import com.easemob.chat.EMMessage;
import com.easemob.easeui.domain.EaseEmojicon;
import com.easemob.easeui.domain.EaseUser;
import com.easemob.easeui.model.EaseNotifier;

public final class EaseUI {
    private static final String TAG = EaseUI.class.getSimpleName();

    /**
     * the global EaseUI instance
     */
    private static EaseUI instance = null;
    
    /**
     * EMEventListener
     */
    private EMEventListener eventListener = null;
    
    /**
     * �û������ṩ��
     */
    private EaseUserProfileProvider userProvider;
    
    private EaseSettingsProvider settingsProvider;
    
    /**
     * application context
     */
    private Context appContext = null;
    
    /**
     * init flag: test if the sdk has been inited before, we don't need to init again
     */
    private boolean sdkInited = false;
    
    /**
     * the notifier
     */
    private EaseNotifier notifier = null;
    
    /**
     * ������¼ע����eventlistener��foreground Activity
     */
    private List<Activity> activityList = new ArrayList<Activity>();
    
    public void pushActivity(Activity activity){
        if(!activityList.contains(activity)){
            activityList.add(0,activity); 
        }
    }
    
    public void popActivity(Activity activity){
        activityList.remove(activity);
    }
    
    
    private EaseUI(){}
    
    /**
     * ��ȡEaseUI��ʵ������
     * @return
     */
    public synchronized static EaseUI getInstance(){
        if(instance == null){
            instance = new EaseUI();
        }
        return instance;
    }
    
    /**
     *this function will initialize the HuanXin SDK
     * 
     * @return boolean true if caller can continue to call HuanXin related APIs after calling onInit, otherwise false.
     * 
     * ��ʼ������sdk��easeui��
     * ����true�����ȷ��ʼ��������false���������Ϊfalse�����ں����ĵ����в�Ҫ�����κκͻ�����صĴ���
     * @param context
     * @return
     */
    public synchronized boolean init(Context context){
        if(sdkInited){
            return true;
        }
        appContext = context;
        
        int pid = android.os.Process.myPid();
        String processAppName = getAppName(pid);
        
        Log.d(TAG, "process app name : " + processAppName);
        
        // ���app������Զ�̵�service����application:onCreate�ᱻ����2��
        // Ϊ�˷�ֹ����SDK����ʼ��2�Σ��Ӵ��жϻᱣ֤SDK����ʼ��1��
        // Ĭ�ϵ�app�����԰���ΪĬ�ϵ�process name�����У�����鵽��process name����app��process name����������
        if (processAppName == null || !processAppName.equalsIgnoreCase(appContext.getPackageName())) {
            Log.e(TAG, "enter the service process!");
            
            // ���application::onCreate �Ǳ�service ���õģ�ֱ�ӷ���
            return false;
        }
        // ��ʼ������SDK,һ��Ҫ�ȵ���init()
        EMChat.getInstance().init(context);
        
        initChatOptions();
        if(settingsProvider == null){
            settingsProvider = new DefaultSettingsProvider();
        }
        
        sdkInited = true;
        return true;
    }
    
    protected void initChatOptions(){
        Log.d(TAG, "init HuanXin Options");
        
        // ��ȡ��EMChatOptions����
        EMChatOptions options = EMChatManager.getInstance().getChatOptions();
        // Ĭ����Ӻ���ʱ���ǲ���Ҫ��֤�ģ��ĳ���Ҫ��֤
        options.setAcceptInvitationAlways(false);
        // Ĭ�ϻ����ǲ�ά�����ѹ�ϵ�б�ģ����app�������ŵĺ��ѹ�ϵ���������������Ϊtrue
        options.setUseRoster(true);
        // �����Ƿ���Ҫ�Ѷ���ִ
        options.setRequireAck(true);
        // �����Ƿ���Ҫ���ʹ��ִ
        options.setRequireDeliveryAck(false);
        // ���ô�db��ʼ������ʱ, ÿ��conversation��Ҫ����msg�ĸ���
        options.setNumberOfMessagesLoaded(1);
        
        notifier = createNotifier();
        notifier.init(appContext);
        
//        notifier.setNotificationInfoProvider(getNotificationListener());
    }
    
    
    protected EaseNotifier createNotifier(){
        return new EaseNotifier();
    }
    
    public EaseNotifier getNotifier(){
        return notifier;
    }
    
    public boolean hasForegroundActivies(){
        return activityList.size() != 0;
    }
    
    /**
     * �����û������ṩ��
     * @param provider
     */
    public void setUserProfileProvider(EaseUserProfileProvider userProvider){
        this.userProvider = userProvider;
    }
    
    /**
     * ��ȡ�û������ṩ��
     * @return
     */
    public EaseUserProfileProvider getUserProfileProvider(){
        return userProvider;
    }
    
    public void setSettingsProvider(EaseSettingsProvider settingsProvider){
        this.settingsProvider = settingsProvider;
    }
    
    public EaseSettingsProvider getSettingsProvider(){
        return settingsProvider;
    }
    
    
    /**
     * check the application process name if process name is not qualified, then we think it is a service process and we will not init SDK
     * @param pID
     * @return
     */
    private String getAppName(int pID) {
        String processName = null;
        ActivityManager am = (ActivityManager) appContext.getSystemService(Context.ACTIVITY_SERVICE);
        List l = am.getRunningAppProcesses();
        Iterator i = l.iterator();
        PackageManager pm = appContext.getPackageManager();
        while (i.hasNext()) {
            ActivityManager.RunningAppProcessInfo info = (ActivityManager.RunningAppProcessInfo) (i.next());
            try {
                if (info.pid == pID) {
                    CharSequence c = pm.getApplicationLabel(pm.getApplicationInfo(info.processName, PackageManager.GET_META_DATA));
                    // Log.d("Process", "Id: "+ info.pid +" ProcessName: "+
                    // info.processName +"  Label: "+c.toString());
                    // processName = c.toString();
                    processName = info.processName;
                    return processName;
                }
            } catch (Exception e) {
                // Log.d("Process", "Error>> :"+ e.toString());
            }
        }
        return processName;
    }
    
    /**
     * �û������ṩ��
     * @author wei
     *
     */
    public interface EaseUserProfileProvider {
        /**
         * ���ش�username��Ӧ��user
         * @param username ����id
         * @return
         */
        EaseUser getUser(String username);
    }
    
    /**
     * ������Ϣ�ṩ��
     *
     */
    public interface EaseEmojiconInfoProvider {
        /**
         * ����Ψһʶ��ŷ��ش˱�������
         * @param emojiconIdentityCode
         * @return
         */
        EaseEmojicon getEmojiconInfo(String emojiconIdentityCode);
        
        /**
         * ��ȡ���ֱ����ӳ��Map,map��keyΪ�����emoji�ı����ݣ�valueΪ��Ӧ��ͼƬ��Դid���߱���·��(����Ϊ�����ַ)
         * @return
         */
        Map<String, Object> getTextEmojiconMapping();
    }
    
    private EaseEmojiconInfoProvider emojiconInfoProvider;
    
    /**
     * ��ȡ�����ṩ��
     * @return
     */
    public EaseEmojiconInfoProvider getEmojiconInfoProvider(){
        return emojiconInfoProvider;
    }
    
    /**
     * ���ñ�����Ϣ�ṩ��
     * @param emojiconInfoProvider
     */
    public void setEmojiconInfoProvider(EaseEmojiconInfoProvider emojiconInfoProvider){
        this.emojiconInfoProvider = emojiconInfoProvider;
    }
    
    /**
     * ����Ϣ��ʾ���õ��ṩ��
     *
     */
    public interface EaseSettingsProvider {
        boolean isMsgNotifyAllowed(EMMessage message);
        boolean isMsgSoundAllowed(EMMessage message);
        boolean isMsgVibrateAllowed(EMMessage message);
        boolean isSpeakerOpened();
    }
    
    /**
     * default settings provider
     *
     */
    protected class DefaultSettingsProvider implements EaseSettingsProvider{

        @Override
        public boolean isMsgNotifyAllowed(EMMessage message) {
            // TODO Auto-generated method stub
            return true;
        }

        @Override
        public boolean isMsgSoundAllowed(EMMessage message) {
            return true;
        }

        @Override
        public boolean isMsgVibrateAllowed(EMMessage message) {
            return true;
        }

        @Override
        public boolean isSpeakerOpened() {
            return true;
        }

        
    }
}
