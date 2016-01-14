package com.easemob.easeui.widget.chatrow;

import android.widget.BaseAdapter;

import com.easemob.chat.EMMessage;

/**
 * �Զ���chat row�ṩ��
 *
 */
public interface EaseCustomChatRowProvider {
    /**
     * ��ȡ���������͵��Զ���chatrow<br/>
     * ע�⣬ÿһ��chatrow����������type������type�ͽ���type
     * @return
     */
    int getCustomChatRowTypeCount(); 
    
    /**
     * ��ȡchatrow type���������0, ��1��ʼ��������
     * @return
     */
    int getCustomChatRowType(EMMessage message);
    
    /**
     * ���ݸ���message����chat row
     * @return
     */
    EaseChatRow getCustomChatRow(EMMessage message, int position, BaseAdapter adapter);
    
}
