package com.netease.nim.uikit.session.module.list;

import android.content.Context;
import android.view.View;

import com.netease.nim.uikit.common.adapter.TAdapter;
import com.netease.nim.uikit.common.adapter.TAdapterDelegate;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MsgAdapter extends TAdapter<IMMessage> {

    private ViewHolderEventListener eventListener;
    private Map<String, Float> progresses; // ���ļ����䣬��Ҫ��ʾ����������ϢID map
    private String messageId;

    public MsgAdapter(Context context, List<IMMessage> items, TAdapterDelegate delegate) {
        super(context, items, delegate);

        timedItems = new HashSet<>();
        progresses = new HashMap<>();
    }

    public void setEventListener(ViewHolderEventListener eventListener) {
        this.eventListener = eventListener;
    }

    public ViewHolderEventListener getEventListener() {
        return eventListener;
    }

    public void deleteItem(IMMessage message) {
        if (message == null) {
            return;
        }

        int index = 0;
        for (IMMessage item : getItems()) {
            if (item.isTheSame(message)) {
                break;
            }
            ++index;
        }

        if (index < getCount()) {
            getItems().remove(index);
            relocateShowTimeItemAfterDelete(message, index);
            notifyDataSetChanged();
        }
    }

    public float getProgress(IMMessage message) {
        Float progress = progresses.get(message.getUuid());
        return progress == null ? 0 : progress;
    }

    public void putProgress(IMMessage message, float progress) {
        progresses.put(message.getUuid(), progress);
    }

    /*********************** ʱ����ʾ���� ****************************/

    private Set<String> timedItems; // ��Ҫ��ʾ��Ϣʱ�����ϢID
    private IMMessage lastShowTimeItem; // ������Ϣʱ����ʾ,�жϺ�������Ϣ���ʱ����

    public boolean needShowTime(IMMessage message) {
        return timedItems.contains(message.getUuid());
    }

    /**
     * �б��������Ϣʱ������ʱ����ʾ
     */
    public void updateShowTimeItem(List<IMMessage> items, boolean fromStart, boolean update) {
        IMMessage anchor = fromStart ? null : lastShowTimeItem;
        for (IMMessage message : items) {
            if (setShowTimeFlag(message, anchor)) {
                anchor = message;
            }
        }

        if (update) {
            lastShowTimeItem = anchor;
        }
    }

    /**
     * �Ƿ���ʾʱ��item
     */
    private boolean setShowTimeFlag(IMMessage message, IMMessage anchor) {
        boolean update = false;

        if (hideTimeAlways(message)) {
            setShowTime(message, false);
        } else {
            if (anchor == null) {
                setShowTime(message, true);
                update = true;
            } else {
                long time = anchor.getTime();
                long now = message.getTime();

                if (now - time < (long) (5 * 60 * 1000)) {
                    setShowTime(message, false);
                } else {
                    setShowTime(message, true);
                    update = true;
                }
            }
        }

        return update;
    }

    private void setShowTime(IMMessage message, boolean show) {
        if (show) {
            timedItems.add(message.getUuid());
        } else {
            timedItems.remove(message.getUuid());
        }
    }

    private void relocateShowTimeItemAfterDelete(IMMessage messageItem, int index) {
        // �����ɾ������ʾ��ʱ�䣬��Ҫ�̳�
        if (needShowTime(messageItem)) {
            setShowTime(messageItem, false);
            if (getCount() > 0) {
                IMMessage nextItem;
                if (index == getCount()) {
                    //ɾ���������һ��
                    nextItem = getItem(index - 1);
                } else {
                    //ɾ���Ĳ������һ��
                    nextItem = getItem(index);
                }

                // ������������Ҫ��ʾʱ�����Ϣ�����ж�
                if (hideTimeAlways(nextItem)) {
                    setShowTime(nextItem, false);
                    if (lastShowTimeItem != null && lastShowTimeItem != null
                            && lastShowTimeItem.isTheSame(messageItem)) {
                        lastShowTimeItem = null;
                        for (int i = getCount() - 1; i >= 0; i--) {
                            IMMessage item = getItem(i);
                            if (needShowTime(item)) {
                                lastShowTimeItem = item;
                                break;
                            }
                        }
                    }
                } else {
                    setShowTime(nextItem, true);
                    if (lastShowTimeItem == null
                            || (lastShowTimeItem != null && lastShowTimeItem.isTheSame(messageItem))) {
                        lastShowTimeItem = nextItem;
                    }
                }
            } else {
                lastShowTimeItem = null;
            }
        }
    }

    /*SAMC_BEGIN()*/
    private boolean isLocalQuestionMsg(IMMessage msg){
        return msg.getLocalExtension() == null?false:true;
    }
    /*SAMC_END()*/	

    private boolean hideTimeAlways(IMMessage message) {
        if (message.getSessionType() == SessionTypeEnum.ChatRoom) {
            return true;
        }

        /*SAMC_BEGIN()*/
        if(message.getSessionType() == SessionTypeEnum.P2P
	     && isLocalQuestionMsg(message)){
            return true;
	 }
        /*SAMC_END()*/	

        switch (message.getMsgType()) {
        case notification:
            return true;
        default:
            return false;
        }
    }

    public interface ViewHolderEventListener {
        // �����¼���Ӧ����
        boolean onViewHolderLongClick(View clickView, View viewHolderView, IMMessage item);

        // ����ʧ�ܻ��߶�ý���ļ�����ʧ��ָʾ��ť�����Ӧ����
        void onFailedBtnClick(IMMessage resendMessage);
    }

    public void setUuid(String messageId) {
        this.messageId = messageId;
    }

    public String getUuid() {
        return messageId;
    }
}
