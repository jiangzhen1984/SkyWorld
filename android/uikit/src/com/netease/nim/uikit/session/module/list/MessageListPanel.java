package com.netease.nim.uikit.session.module.list;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Pair;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.netease.nim.uikit.NimConstants;
import com.netease.nim.uikit.NimUIKit;
import com.netease.nim.uikit.R;
import com.netease.nim.uikit.UserPreferences;
import com.netease.nim.uikit.common.adapter.TAdapterDelegate;
import com.netease.nim.uikit.common.adapter.TViewHolder;
import com.netease.nim.uikit.common.ui.dialog.CustomAlertDialog;
import com.netease.nim.uikit.common.ui.dialog.EasyAlertDialog;
import com.netease.nim.uikit.common.ui.dialog.EasyAlertDialogHelper;
import com.netease.nim.uikit.common.ui.listview.AutoRefreshListView;
import com.netease.nim.uikit.common.ui.listview.ListViewUtil;
import com.netease.nim.uikit.common.ui.listview.MessageListView;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.netease.nim.uikit.common.util.media.BitmapDecoder;
import com.netease.nim.uikit.common.util.sys.ClipboardUtil;
import com.netease.nim.uikit.common.util.sys.ScreenUtil;
import com.netease.nim.uikit.samwraper.SendQuestionWraper;
import com.netease.nim.uikit.session.activity.VoiceTrans;
import com.netease.nim.uikit.session.audio.MessageAudioControl;
import com.netease.nim.uikit.session.helper.MessageListPanelHelper;
import com.netease.nim.uikit.session.module.Container;
import com.netease.nim.uikit.session.viewholder.MsgViewHolderBase;
import com.netease.nim.uikit.session.viewholder.MsgViewHolderFactory;
import com.netease.nim.uikit.uinfo.UserInfoHelper;
import com.netease.nim.uikit.uinfo.UserInfoObservable;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.RequestCallbackWrapper;
import com.netease.nimlib.sdk.avchat.model.AVChatAttachment;
import com.netease.nimlib.sdk.msg.MessageBuilder;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.MsgServiceObserve;
import com.netease.nimlib.sdk.msg.attachment.AudioAttachment;
import com.netease.nimlib.sdk.msg.attachment.FileAttachment;
import com.netease.nimlib.sdk.msg.constant.AttachStatusEnum;
import com.netease.nimlib.sdk.msg.constant.MsgDirectionEnum;
import com.netease.nimlib.sdk.msg.constant.MsgStatusEnum;
import com.netease.nimlib.sdk.msg.constant.MsgTypeEnum;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.AttachmentProgress;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.netease.nimlib.sdk.msg.model.QueryDirectionEnum;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ��Ϣ�շ�ģ��
 * Created by hzxuwen on 2015/6/10.
 */
public class MessageListPanel implements TAdapterDelegate {
    // container
    private Container container;
    private View rootView;

    // message list view
    private MessageListView messageListView;
    private List<IMMessage> items;
    private MsgAdapter adapter;
    private ImageView listviewBk;

    // ����Ϣ��������
    private IncomingMsgPrompt incomingMsgPrompt;
    private Handler uiHandler;

    // ����ʾ��Ϣ��¼�������պͷ�����Ϣ
    private boolean recordOnly;
    // �ӷ�������ȡ��Ϣ��¼
    private boolean remote;

    // ����ת����
    private VoiceTrans voiceTrans;

    // ����ͼƬ����
    private static Pair<String, Bitmap> background;

    /*SAMC_BEING()*/
    private Object queryLock;
    private boolean all_loaded;

    private void waitQueryLock(){
		synchronized (queryLock){
			try {
				all_loaded = false;
				queryLock.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
    		}
	}

	private void notifyQueryLock(boolean all){
		synchronized (queryLock){
			try {
				all_loaded = all;
				queryLock.notify();
			} catch (Exception e) {
				e.printStackTrace();
			}
    		}
	}

    private RequestCallback<List<IMMessage>> allload_callback = new RequestCallbackWrapper<List<IMMessage>>() {
            @Override
            public void onResult(int code, List<IMMessage> messages, Throwable exception) {
                if (messages != null) {
			LogUtil.i("test","allload_callback thread");
                    //notifyQueryLock(false);
                }else{
                    LogUtil.i("test","allload_callback thread");
                    //notifyQueryLock(true);
		   }
            }
        };


    private void allLocalMsgLoaded(IMMessage local_anchor){
	 LogUtil.i("test","load thread");
        NIMClient.getService(MsgService.class).queryMessageListEx(local_anchor, QueryDirectionEnum.QUERY_OLD, 1, true)
                    .setCallback(allload_callback);
        //waitQueryLock();

    }

    private IMMessage createMsgFromQuestion(SendQuestionWraper sq){
        IMMessage local_msg = MessageBuilder.createTextMessage(container.account,SessionTypeEnum.P2P,sq.question);
        Map<String, Object> ext = new HashMap<>();
        ext.put(NimConstants.MSG_LOCAL_KEY,Long.valueOf(sq.sendtime));
        local_msg.setLocalExtension(ext);
        local_msg.setDirect(MsgDirectionEnum.Out);
        local_msg.setStatus(MsgStatusEnum.success);
        return local_msg;
    }

    private static boolean isLocalQuestionMsg(IMMessage msg){
        return msg.getLocalExtension() == null?false:true;
    }

    private List<IMMessage> buildMsgList(List<SendQuestionWraper> sqList){
        List<IMMessage> build_msg_list = new ArrayList<IMMessage>();
        for(SendQuestionWraper sq: sqList){
            build_msg_list.add(createMsgFromQuestion(sq));
        }
        return build_msg_list;
    }

    private static Comparator<IMMessage> comp = new Comparator<IMMessage>() {

        @Override
        public int compare(IMMessage o1, IMMessage o2) {
             long time1,time2;
             if(isLocalQuestionMsg(o1)){
                  time1 = (Long)o1.getLocalExtension().get(NimConstants.MSG_LOCAL_KEY);
             }else{
                  time1 = o1.getTime();
             }

             if(isLocalQuestionMsg(o2)){
                  time2 = (Long)o2.getLocalExtension().get(NimConstants.MSG_LOCAL_KEY);
             }else{
                  time2 = o2.getTime();
             }

             if (time1 == time2) {
                    return 0;
             } else if (time1>time2) {
                    return 1;
             } else {
                    return -1;
             }

	  }
    };

    private boolean questionShown(SendQuestionWraper sq){
        for(IMMessage msg: items){
            if(isLocalQuestionMsg(msg) 
		&& (Long)msg.getLocalExtension().get(NimConstants.MSG_LOCAL_KEY) == sq.sendtime){
                 return true;
	     }
        }

	  return false;
    }

    private void mergeQuestion(List<SendQuestionWraper> sqList,boolean all_merge){
        if(all_merge){
            List<SendQuestionWraper> add_sqList = new ArrayList<SendQuestionWraper>();
            for(SendQuestionWraper sq: sqList){
                if(!questionShown(sq)) {
                    add_sqList.add(sq);
		  }
	     }
            if(add_sqList.size()>0){
                List<IMMessage> qMsgList = buildMsgList(add_sqList);
                items.addAll(qMsgList);
                Collections.sort(items,comp);
           }
            
        }else{
            List<SendQuestionWraper> add_sqList = new ArrayList<SendQuestionWraper>();
            IMMessage oldest_msg = items.get(0);
            long oldest_time = 0;
            if(isLocalQuestionMsg(oldest_msg)){
                   oldest_time = (Long)oldest_msg.getLocalExtension().get(NimConstants.MSG_LOCAL_KEY);
            }else{
                   oldest_time = oldest_msg.getTime();
            }
            for(SendQuestionWraper sq: sqList){
                if(sq.sendtime>=oldest_time && !questionShown(sq)) {
                    add_sqList.add(sq);
		  }
	     }

	     if(add_sqList.size()>0){
                List<IMMessage> qMsgList = buildMsgList(add_sqList);
                items.addAll(qMsgList);
                Collections.sort(items,comp);
           }

			
        }
    }
	
    private void insertSendQuestionToItems(){
       if(container.sessionType!=SessionTypeEnum.P2P){
           return;
       }

       if(items == null || items.size() == 0){
           return;
	}
	   
       List<SendQuestionWraper> sqList = NimUIKit.samListner.getSendQuestionWraper(container.account);
       if(sqList.size() == 0){
           return;
	}

	LogUtil.i("test","sqList size:"+sqList.size());

	if(items.size()<MessageLoader.LOAD_MESSAGE_COUNT){
            mergeQuestion(sqList,true);
	}else{
            mergeQuestion(sqList,all_loaded);
	}
	
    }

    /*SAMC_END()*/ 

    public MessageListPanel(Container container, View rootView) {
        this(container, rootView, null, false, false);
    }

    public MessageListPanel(Container container, View rootView, boolean recordOnly, boolean remote) {
        this(container, rootView, null, recordOnly, remote);
    }

    public MessageListPanel(Container container, View rootView, IMMessage anchor, boolean recordOnly, boolean remote) {
        /*SAMC_BEGN()*/
        queryLock = new Object();
        /*SAMC_END()*/ 
	
        this.container = container;
        this.rootView = rootView;
        this.recordOnly = recordOnly;
        this.remote = remote;

        init(anchor);
    }

    public void onResume() {
        setEarPhoneMode(UserPreferences.isEarPhoneModeEnable());
    }

    public void onPause() {
        MessageAudioControl.getInstance(container.activity).stopAudio();
    }

    public void onDestroy() {
        registerObservers(false);
    }

    public boolean onBackPressed() {
        uiHandler.removeCallbacks(null);
        MessageAudioControl.getInstance(container.activity).stopAudio(); // ���淵�أ�ֹͣ��������
        if (voiceTrans != null && voiceTrans.isShow()) {
            voiceTrans.hide();
            return true;
        }
        return false;
    }

    public void reload(Container container, IMMessage anchor) {
        this.container = container;
        items.clear();
        // ����load
        messageListView.setOnRefreshListener(new MessageLoader(anchor, remote));
    }

    private void init(IMMessage anchor) {
        initListView(anchor);

        this.uiHandler = new Handler();
        if (!recordOnly) {
            incomingMsgPrompt = new IncomingMsgPrompt(container.activity, rootView, messageListView, uiHandler);
        }

        registerObservers(true);
    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
	private void initListView(IMMessage anchor) {
        items = new ArrayList<>();
        adapter = new MsgAdapter(container.activity, items, this);
        adapter.setEventListener(new MsgItemEventListener());

        listviewBk = (ImageView) rootView.findViewById(R.id.message_activity_background);

        messageListView = (MessageListView) rootView.findViewById(R.id.messageListView);
        messageListView.requestDisallowInterceptTouchEvent(true);

        if (recordOnly && !remote) {
            messageListView.setMode(AutoRefreshListView.Mode.BOTH);
        } else {
            messageListView.setMode(AutoRefreshListView.Mode.START);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            messageListView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        }
        // adapter
        messageListView.setAdapter(adapter);

        messageListView.setListViewEventListener(new MessageListView.OnListViewEventListener() {
            @Override
            public void onListViewStartScroll() {
                container.proxy.shouldCollapseInputPanel();
            }
        });
        messageListView.setOnRefreshListener(new MessageLoader(anchor, remote));
    }

    // ˢ����Ϣ�б�
    public void refreshMessageList() {
        container.activity.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                adapter.notifyDataSetChanged();
            }
        });
    }

    public void scrollToBottom() {
        uiHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ListViewUtil.scrollToBottom(messageListView);
            }
        }, 200);
    }

    public void scrollToItem(final int position) {
        uiHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ListViewUtil.scrollToPosition(messageListView, position, 0);
            }
        }, 200);
    }

    public void onIncomingMessage(List<IMMessage> messages) {
        boolean needScrollToBottom = ListViewUtil.isLastMessageVisible(messageListView);
        boolean needRefresh = false;
        List<IMMessage> addedListItems = new ArrayList<>(messages.size());
        for (IMMessage message : messages) {
            if (isMyMessage(message)) {
                items.add(message);
                addedListItems.add(message);
                needRefresh = true;
            }
        }
        if (needRefresh) {
            adapter.notifyDataSetChanged();
        }

        adapter.updateShowTimeItem(addedListItems, false, true);

        // incoming messages tip
        IMMessage lastMsg = messages.get(messages.size() - 1);
        if (isMyMessage(lastMsg)) {
            if (needScrollToBottom) {
                ListViewUtil.scrollToBottom(messageListView);
            } else if (incomingMsgPrompt != null && lastMsg.getSessionType() != SessionTypeEnum.ChatRoom) {
                incomingMsgPrompt.show(lastMsg);
            }
        }
    }

    // ������Ϣ�󣬸��±�����Ϣ�б�
    public void onMsgSend(IMMessage message) {
        // add to listView and refresh
        items.add(message);
        List<IMMessage> addedListItems = new ArrayList<>(1);
        addedListItems.add(message);
        adapter.updateShowTimeItem(addedListItems, false, true);

        adapter.notifyDataSetChanged();
        ListViewUtil.scrollToBottom(messageListView);
    }

    /**
     * *************** implements TAdapterDelegate ***************
     */
    @Override
    public int getViewTypeCount() {
        return MsgViewHolderFactory.getViewTypeCount();
    }

    @Override
    public Class<? extends TViewHolder> viewHolderAtPosition(int position) {
        return MsgViewHolderFactory.getViewHolderByType(items.get(position));
    }

    @Override
    public boolean enabled(int position) {
        return false;
    }

    /**
     * ************************* �۲��� ********************************
     */

    private void registerObservers(boolean register) {
        MsgServiceObserve service = NIMClient.getService(MsgServiceObserve.class);
        service.observeMsgStatus(messageStatusObserver, register);
        service.observeAttachmentProgress(attachmentProgressObserver, register);
        if (register) {
            registerUserInfoObserver();
        } else {
            unregisterUserInfoObserver();
        }

        MessageListPanelHelper.getInstance().registerObserver(incomingLocalMessageObserver, register);
    }

    /**
     * ��Ϣ״̬�仯�۲���
     */
    Observer<IMMessage> messageStatusObserver = new Observer<IMMessage>() {
        @Override
        public void onEvent(IMMessage message) {
            if (isMyMessage(message)) {
                onMessageStatusChange(message);
            }
        }
    };

    /**
     * ��Ϣ�����ϴ�/���ؽ�ȹ۲���
     */
    Observer<AttachmentProgress> attachmentProgressObserver = new Observer<AttachmentProgress>() {
        @Override
        public void onEvent(AttachmentProgress progress) {
            onAttachmentProgressChange(progress);
        }
    };

    /**
     * ������Ϣ���չ۲���
     */
    MessageListPanelHelper.LocalMessageObserver incomingLocalMessageObserver = new MessageListPanelHelper.LocalMessageObserver() {
        @Override
        public void onAddMessage(IMMessage message) {
            if (message == null || !container.account.equals(message.getSessionId())) {
                return;
            }

            onMsgSend(message);
        }

        @Override
        public void onClearMessages(String account) {
            items.clear();
            refreshMessageList();
        }
    };

    private void onMessageStatusChange(IMMessage message) {
        int index = getItemIndex(message.getUuid());
        if (index >= 0 && index < items.size()) {
            IMMessage item = items.get(index);
            item.setStatus(message.getStatus());
            item.setAttachStatus(message.getAttachStatus());
            if (item.getAttachment() instanceof AVChatAttachment
                    || item.getAttachment() instanceof AudioAttachment) {
                item.setAttachment(message.getAttachment());
            }
            refreshViewHolderByIndex(index);
        }
    }

    private void onAttachmentProgressChange(AttachmentProgress progress) {
        int index = getItemIndex(progress.getUuid());
        if (index >= 0 && index < items.size()) {
            IMMessage item = items.get(index);
            float value = (float) progress.getTransferred() / (float) progress.getTotal();
            adapter.putProgress(item, value);
            refreshViewHolderByIndex(index);
        }
    }

    public boolean isMyMessage(IMMessage message) {
        return message.getSessionType() == container.sessionType
                && message.getSessionId() != null
                && message.getSessionId().equals(container.account);
    }

    /**
     * ˢ�µ�����Ϣ
     *
     * @param index
     */
    private void refreshViewHolderByIndex(final int index) {
        container.activity.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                if (index < 0) {
                    return;
                }

                Object tag = ListViewUtil.getViewHolderByIndex(messageListView, index);
                if (tag instanceof MsgViewHolderBase) {
                    MsgViewHolderBase viewHolder = (MsgViewHolderBase) tag;
                    viewHolder.refreshCurrentItem();
                }
            }
        });
    }

    private int getItemIndex(String uuid) {
        for (int i = 0; i < items.size(); i++) {
            IMMessage message = items.get(i);
            if (TextUtils.equals(message.getUuid(), uuid)) {
                return i;
            }
        }

        return -1;
    }

    public void setChattingBackground(String uriString, int color) {
        if (uriString != null) {
            Uri uri = Uri.parse(uriString);
            if (uri.getScheme().equalsIgnoreCase("file") && uri.getPath() != null) {
                listviewBk.setImageBitmap(getBackground(uri.getPath()));
            } else if (uri.getScheme().equalsIgnoreCase("android.resource")) {
                List<String> paths = uri.getPathSegments();
                if (paths == null || paths.size() != 2) {
                    return;
                }
                String type = paths.get(0);
                String name = paths.get(1);
                String pkg = uri.getHost();
                int resId = container.activity.getResources().getIdentifier(name, type, pkg);
                if (resId != 0) {
                    listviewBk.setBackgroundResource(resId);
                }
            }
        } else if (color != 0) {
            listviewBk.setBackgroundColor(color);
        }
    }

    private class MessageLoader implements AutoRefreshListView.OnRefreshListener {

        private static final int LOAD_MESSAGE_COUNT = 20;

        private QueryDirectionEnum direction = null;

        private IMMessage anchor;
        private boolean remote;

        private boolean firstLoad = true;

        public MessageLoader(IMMessage anchor, boolean remote) {
            this.anchor = anchor;
            this.remote = remote;
            if (remote) {
                loadFromRemote();
            } else {
                loadFromLocal(anchor == null ? QueryDirectionEnum.QUERY_OLD : QueryDirectionEnum.QUERY_NEW);
            }
        }

        private RequestCallback<List<IMMessage>> callback = new RequestCallbackWrapper<List<IMMessage>>() {
            @Override
            public void onResult(int code, List<IMMessage> messages, Throwable exception) {
                if (messages != null) {
                    onMessageLoaded(messages);
                }
            }
        };

        private void loadFromLocal(QueryDirectionEnum direction) {
            this.direction = direction;
            messageListView.onRefreshStart(direction == QueryDirectionEnum.QUERY_NEW ? AutoRefreshListView.Mode.END : AutoRefreshListView.Mode.START);
            NIMClient.getService(MsgService.class).queryMessageListEx(anchor(), direction, LOAD_MESSAGE_COUNT, true)
                    .setCallback(callback);
        }

        private void loadFromRemote() {
            this.direction = QueryDirectionEnum.QUERY_OLD;
            NIMClient.getService(MsgService.class).pullMessageHistory(anchor(), LOAD_MESSAGE_COUNT, true)
                    .setCallback(callback);
        }

        private IMMessage anchor() {
            if (items.size() == 0) {
                return anchor == null ? MessageBuilder.createEmptyMessage(container.account, container.sessionType, 0) : anchor;
            } else {
                int index = (direction == QueryDirectionEnum.QUERY_NEW ? items.size() - 1 : 0);
                return items.get(index);
            }
        }

        /**
         * ��ʷ��Ϣ���ش���
         *
         * @param messages
         */
        private void onMessageLoaded(List<IMMessage> messages) {
            int count = messages.size();

            if(!remote && count<LOAD_MESSAGE_COUNT){
                   all_loaded = true;
            }

            if (remote) {
                Collections.reverse(messages);
            }

            if (firstLoad && items.size() > 0) {
                // �ڵ�һ�μ��صĹ�������յ�������Ϣ����һ��ȥ��
                for (IMMessage message : messages) {
                    for (IMMessage item : items) {
                        if (item.isTheSame(message)) {
                            items.remove(item);
                            break;
                        }
                    }
                }
            }

            if (firstLoad && anchor != null) {
                items.add(anchor);
            }

            List<IMMessage> result = new ArrayList<>();
            for (IMMessage message : messages) {
                result.add(message);
            }
            if (direction == QueryDirectionEnum.QUERY_NEW) {
                items.addAll(result);
            } else {
                items.addAll(0, result);
            }

            // ����ǵ�һ�μ��أ�updateShowTimeItem���صľ���lastShowTimeItem
            if (firstLoad) {
                ListViewUtil.scrollToBottom(messageListView);
                sendReceipt(); // �����Ѷ���ִ
            }

            adapter.updateShowTimeItem(items, true, firstLoad);
            updateReceipt(items); // �����Ѷ���ִ��ǩ

            /*SAMC_BEGIN()*/
            insertSendQuestionToItems();          
            /*SAMC_END()*/
            refreshMessageList();
            messageListView.onRefreshComplete(count, LOAD_MESSAGE_COUNT, true);

            firstLoad = false;
        }

        /**
         * *************** OnRefreshListener ***************
         */
        @Override
        public void onRefreshFromStart() {
            if (remote) {
                loadFromRemote();
            } else {
                loadFromLocal(QueryDirectionEnum.QUERY_OLD);
            }
        }

        @Override
        public void onRefreshFromEnd() {
            if (!remote) {
                loadFromLocal(QueryDirectionEnum.QUERY_NEW);
            }
        }
    }

    private class MsgItemEventListener implements MsgAdapter.ViewHolderEventListener {

        @Override
        public void onFailedBtnClick(IMMessage message) {
            if (message.getDirect() == MsgDirectionEnum.Out) {
                // ��������Ϣ������Ƿ���ʧ�ܣ�ֱ���ط��������п��������ε��Ķ�ý����Ϣ�����ļ�����
                if (message.getStatus() == MsgStatusEnum.fail) {
                    resendMessage(message); // �ط�
                } else {
                    if (message.getAttachment() instanceof FileAttachment) {
                        FileAttachment attachment = (FileAttachment) message.getAttachment();
                        if (TextUtils.isEmpty(attachment.getPath())
                                && TextUtils.isEmpty(attachment.getThumbPath())) {
                            showReDownloadConfirmDlg(message);
                        }
                    } else {
                        resendMessage(message);
                    }
                }
            } else {
                showReDownloadConfirmDlg(message);
            }
        }

        @Override
        public boolean onViewHolderLongClick(View clickView, View viewHolderView, IMMessage item) {
            if (container.proxy.isLongClickEnabled()) {
                showLongClickAction(item);
            }
            return true;
        }

        // ��������(�Ի�����ʾ)
        private void showReDownloadConfirmDlg(final IMMessage message) {
            EasyAlertDialogHelper.OnDialogActionListener listener = new EasyAlertDialogHelper.OnDialogActionListener() {

                @Override
                public void doCancelAction() {
                }

                @Override
                public void doOkAction() {
                    // ������յ���Ϣ�󸽼����Զ����ء��������ʧ�ܣ��ɵ��øýӿ���������
                    if (message.getAttachment() != null && message.getAttachment() instanceof FileAttachment)
                        NIMClient.getService(MsgService.class).downloadAttachment(message, true);
                }
            };

            final EasyAlertDialog dialog = EasyAlertDialogHelper.createOkCancelDiolag(container.activity, null,
                    container.activity.getString(R.string.repeat_download_message), true, listener);
            dialog.show();
        }

        // �ط���Ϣ��������
        private void resendMessage(IMMessage message) {
            // ����״̬Ϊunsent
            int index = getItemIndex(message.getUuid());
            if (index >= 0 && index < items.size()) {
                IMMessage item = items.get(index);
                item.setStatus(MsgStatusEnum.sending);
                refreshViewHolderByIndex(index);
            }

            NIMClient.getService(MsgService.class).sendMessage(message, true);
        }

        /**
         * ****************************** �����˵� ********************************
         */

        // ������ϢItem�󵯳��˵�����
        private void showLongClickAction(IMMessage selectedItem) {
            onNormalLongClick(selectedItem);
        }

        /**
         * �����˵�����
         *
         * @param item
         */
        private void onNormalLongClick(IMMessage item) {
            CustomAlertDialog alertDialog = new CustomAlertDialog(container.activity);
            alertDialog.setCancelable(true);
            alertDialog.setCanceledOnTouchOutside(true);

            prepareDialogItems(item, alertDialog);
            alertDialog.show();
        }

        // ������Ϣitem�Ĳ˵���׼���������Ϣitem��MsgViewHolder���?���¼�(MsgViewHolderBase#onItemLongClick),�ҷ���Ϊtrue��
        // ���Ӧ��ĳ����¼�������õ��˴�
        private void prepareDialogItems(final IMMessage selectedItem, CustomAlertDialog alertDialog) {
            MsgTypeEnum msgType = selectedItem.getMsgType();

            MessageAudioControl.getInstance(container.activity).stopAudio();

            // 0 EarPhoneMode
            longClickItemEarPhoneMode(alertDialog, msgType);
            // 1 resend
            longClickItemResend(selectedItem, alertDialog);
            // 2 copy
            longClickItemCopy(selectedItem, alertDialog, msgType);
            // 3 delete
            longClickItemDelete(selectedItem, alertDialog);
            // 4 trans
            longClickItemVoidToText(selectedItem, alertDialog, msgType);
        }

        // �����˵���--�ط�
        private void longClickItemResend(final IMMessage item, CustomAlertDialog alertDialog) {
            if (item.getStatus() != MsgStatusEnum.fail) {
                return;
            }
            alertDialog.addItem(container.activity.getString(R.string.repeat_send_has_blank), new CustomAlertDialog.onSeparateItemClickListener() {

                @Override
                public void onClick() {
                    onResendMessageItem(item);
                }
            });
        }

        private void onResendMessageItem(IMMessage message) {
            int index = getItemIndex(message.getUuid());
            if (index >= 0) {
                showResendConfirm(message, index); // �ط�ȷ��
            }
        }

        private void showResendConfirm(final IMMessage message, final int index) {
            EasyAlertDialogHelper.OnDialogActionListener listener = new EasyAlertDialogHelper.OnDialogActionListener() {

                @Override
                public void doCancelAction() {
                }

                @Override
                public void doOkAction() {
                    resendMessage(message);
                }
            };
            final EasyAlertDialog dialog = EasyAlertDialogHelper.createOkCancelDiolag(container.activity, null,
                    container.activity.getString(R.string.repeat_send_message), true, listener);
            dialog.show();
        }

        // �����˵���--����
        private void longClickItemCopy(final IMMessage item, CustomAlertDialog alertDialog, MsgTypeEnum msgType) {
            if (msgType != MsgTypeEnum.text) {
                return;
            }
            alertDialog.addItem(container.activity.getString(R.string.copy_has_blank), new CustomAlertDialog.onSeparateItemClickListener() {

                @Override
                public void onClick() {
                    onCopyMessageItem(item);
                }
            });
        }

        private void onCopyMessageItem(IMMessage item) {
            ClipboardUtil.clipboardCopyText(container.activity, item.getContent());
        }

        // �����˵���--ɾ��
        private void longClickItemDelete(final IMMessage selectedItem, CustomAlertDialog alertDialog) {
            if (recordOnly) {
                return;
            }
            alertDialog.addItem(container.activity.getString(R.string.delete_has_blank), new CustomAlertDialog.onSeparateItemClickListener() {

                @Override
                public void onClick() {
                    deleteItem(selectedItem);
                }
            });
        }

        public void deleteItem(IMMessage messageItem) {
            NIMClient.getService(MsgService.class).deleteChattingHistory(messageItem);
            List<IMMessage> messages = new ArrayList<>();
            for (IMMessage message : items) {
                if (message.getUuid().equals(messageItem.getUuid())) {
                    continue;
                }
                messages.add(message);
            }
            updateReceipt(messages);
            adapter.deleteItem(messageItem);
        }


        // �����˵��� -- ��Ƶת����
        private void longClickItemVoidToText(final IMMessage item, CustomAlertDialog alertDialog, MsgTypeEnum msgType) {
            if (msgType != MsgTypeEnum.audio) return;

            if (item.getDirect() == MsgDirectionEnum.In
                    && item.getAttachStatus() != AttachStatusEnum.transferred)
                return;
            if (item.getDirect() == MsgDirectionEnum.Out
                    && item.getAttachStatus() != AttachStatusEnum.transferred)
                return;

            alertDialog.addItem(container.activity.getString(R.string.voice_to_text), new CustomAlertDialog.onSeparateItemClickListener() {

                @Override
                public void onClick() {
                    onVoiceToText(item);
                }
            });
        }

        // ����ת����
        private void onVoiceToText(IMMessage item) {
            if (voiceTrans == null)
                voiceTrans = new VoiceTrans(container.activity);
            voiceTrans.voiceToText(item);
            if (item.getDirect() == MsgDirectionEnum.In && item.getStatus() != MsgStatusEnum.read) {
                item.setStatus(MsgStatusEnum.read);
                NIMClient.getService(MsgService.class).updateIMMessageStatus(item);
                adapter.notifyDataSetChanged();
            }
        }

        // �����˵��� -- ��Ͳ�������л�
        private void longClickItemEarPhoneMode(CustomAlertDialog alertDialog, MsgTypeEnum msgType) {
            if (msgType != MsgTypeEnum.audio) return;

            String content = null;
            if (UserPreferences.isEarPhoneModeEnable()) {
                content = "�л�������������";
            } else {
                content = "�л�����Ͳ����";
            }
            final String finalContent = content;
            alertDialog.addItem(content, new CustomAlertDialog.onSeparateItemClickListener() {

                @Override
                public void onClick() {
                    Toast.makeText(container.activity, finalContent, Toast.LENGTH_SHORT).show();
                    setEarPhoneMode(!UserPreferences.isEarPhoneModeEnable());
                }
            });
        }
    }

    private void setEarPhoneMode(boolean earPhoneMode) {
        UserPreferences.setEarPhoneModeEnable(earPhoneMode);
        MessageAudioControl.getInstance(container.activity).setEarPhoneModeEnable(earPhoneMode);
    }

    private Bitmap getBackground(String path) {
        if (background != null && path.equals(background.first) && background.second != null) {
            return background.second;
        }

        if (background != null && background.second != null) {
            background.second.recycle();
        }

        Bitmap bitmap = null;
        if (path.startsWith("/android_asset")) {
            String asset = path.substring(path.indexOf("/", 1) + 1);
            try {
                InputStream ais = container.activity.getAssets().open(asset);
                bitmap = BitmapDecoder.decodeSampled(ais, ScreenUtil.screenWidth, ScreenUtil.screenHeight);
                ais.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            bitmap = BitmapDecoder.decodeSampled(path, ScreenUtil.screenWidth, ScreenUtil.screenHeight);
        }
        background = new Pair<>(path, bitmap);
        return bitmap;
    }

    private UserInfoObservable.UserInfoObserver uinfoObserver;

    private void registerUserInfoObserver() {
        if (uinfoObserver == null) {
            uinfoObserver = new UserInfoObservable.UserInfoObserver() {
                @Override
                public void onUserInfoChanged(List<String> accounts) {
                    if (container.sessionType == SessionTypeEnum.P2P) {
                        if (accounts.contains(container.account) || accounts.contains(NimUIKit.getAccount())) {
                            adapter.notifyDataSetChanged();
                        }
                    } else { // Ⱥ�ģ��򵥵�ȫ����ˢ
                        adapter.notifyDataSetChanged();
                    }
                }
            };
        }

        UserInfoHelper.registerObserver(uinfoObserver);
    }

    private void unregisterUserInfoObserver() {
        if (uinfoObserver != null) {
            UserInfoHelper.unregisterObserver(uinfoObserver);
        }
    }

    /**
     * �յ��Ѷ���ִ������VH���Ѷ�label��
     */

    public void receiveReceipt() {
        updateReceipt(items);
        refreshMessageList();
    }

    public void updateReceipt(final List<IMMessage> messages) {
        for (int i = messages.size() - 1; i >= 0; i--) {
            if (receiveReceiptCheck(messages.get(i))) {
                adapter.setUuid(messages.get(i).getUuid());
                break;
            }
        }
    }

    private boolean receiveReceiptCheck(final IMMessage msg) {
        if(msg != null && msg.getSessionType() == SessionTypeEnum.P2P
                && msg.getDirect() == MsgDirectionEnum.Out
                && msg.getMsgType() != MsgTypeEnum.tip
                && msg.getMsgType() != MsgTypeEnum.notification
                && msg.isRemoteRead()) {
            return true;
        }

        return false;
    }

    /**
     * �����Ѷ���ִ����Ҫ���ˣ�
     */

    public void sendReceipt() {
        if (container.account == null || container.sessionType != SessionTypeEnum.P2P) {
            return;
        }

        IMMessage message = getLastReceivedMessage();
        if(!sendReceiptCheck(message)) {
            return;
        }

        NIMClient.getService(MsgService.class).sendMessageReceipt(container.account, message);
    }

    private IMMessage getLastReceivedMessage() {
        IMMessage lastMessage = null;
        for (int i = items.size() - 1; i >= 0; i--) {
            if (sendReceiptCheck(items.get(i))) {
                lastMessage = items.get(i);
                break;
            }
        }

        return lastMessage;
    }

    private boolean sendReceiptCheck(final IMMessage msg) {
        if(msg == null || msg.getDirect() != MsgDirectionEnum.In ||
                msg.getMsgType() == MsgTypeEnum.tip || msg.getMsgType() == MsgTypeEnum.notification) {
            return false; // ���յ�����Ϣ��Tip��Ϣ��֪ͨ����Ϣ����Ҫ���Ѷ���ִ
        }

        return true;
    }
}
