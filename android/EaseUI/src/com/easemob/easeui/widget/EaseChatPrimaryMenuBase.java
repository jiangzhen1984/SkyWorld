package com.easemob.easeui.widget;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.RelativeLayout;

public abstract class EaseChatPrimaryMenuBase extends RelativeLayout{
    protected EaseChatPrimaryMenuListener listener;
    protected Activity activity;
    protected InputMethodManager inputManager;

    public EaseChatPrimaryMenuBase(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public EaseChatPrimaryMenuBase(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public EaseChatPrimaryMenuBase(Context context) {
        super(context);
        init(context);
    }
    
    private void init(Context context){
        this.activity = (Activity) context;
        inputManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
    }
    
    /**
     * ��������ť�����listener
     * @param listener
     */
    public void setChatPrimaryMenuListener(EaseChatPrimaryMenuListener listener){
        this.listener = listener;
    }
    
    /**
     * ��������
     * @param emojiContent
     */
    public abstract void onEmojiconInputEvent(CharSequence emojiContent);

    /**
     * ����ɾ��
     */
    public abstract void onEmojiconDeleteEvent();
    
    /**
     * ������չ��ť��(����������)����
     */
    public abstract void onExtendMenuContainerHide();
    
    /**
     * ���������
     */
    public void hideKeyboard() {
        if (activity.getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (activity.getCurrentFocus() != null)
                inputManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
    
    public interface EaseChatPrimaryMenuListener{
        /**
         * ���Ͱ�ť����¼�
         * @param content ��������
         */
        void onSendBtnClicked(String content);
        
        /**
         * ����˵����ťontouch�¼�
         * @return
         */
        boolean onPressToSpeakBtnTouch(View v, MotionEvent event);
        
        /**
         * ����˵����ť���ػ���ʾ�¼�
         */
        void onToggleVoiceBtnClicked();
        
        /**
         * ���ػ���ʾ��չmenu��ť�������¼�
         */
        void onToggleExtendClicked();
        
        /**
         * ���ػ���ʾ��������ť����¼�
         */
        void onToggleEmojiconClicked();
        
        /**
         * ������������¼�
         */
        void onEditTextClicked();
        
    }

}
