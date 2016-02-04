package com.easemob.easeui.widget;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.easemob.easeui.R;
import com.easemob.easeui.domain.EaseEmojicon;
import com.easemob.easeui.domain.EaseEmojiconGroupEntity;
import com.easemob.easeui.model.EaseDefaultEmojiconDatas;
import com.easemob.easeui.utils.EaseSmileUtils;
import com.easemob.easeui.widget.EaseChatExtendMenu.EaseChatExtendMenuItemClickListener;
import com.easemob.easeui.widget.EaseChatPrimaryMenuBase.EaseChatPrimaryMenuListener;
import com.easemob.easeui.widget.emojicon.EaseEmojiconMenu;
import com.easemob.easeui.widget.emojicon.EaseEmojiconMenuBase;
import com.easemob.easeui.widget.emojicon.EaseEmojiconMenuBase.EaseEmojiconMenuListener;

/**
 * ����ҳ��ײ�����������˵��� <br/>
 * ��Ҫ����3���ؼ�:EaseChatPrimaryMenu(���˵����������������롢���͵ȹ���), <br/>
 * EaseChatExtendMenu(��չ��������ӺŰ�ť������С����Ĳ˵���), <br/>
 * �Լ�EaseEmojiconMenu(������)
 */
public class EaseChatInputMenu extends LinearLayout {
    FrameLayout primaryMenuContainer, emojiconMenuContainer;
    protected EaseChatPrimaryMenuBase chatPrimaryMenu;
    protected EaseEmojiconMenuBase emojiconMenu;
    protected EaseChatExtendMenu chatExtendMenu;
    protected FrameLayout chatExtendMenuContainer;
    protected LayoutInflater layoutInflater;

    private Handler handler = new Handler();
    private ChatInputMenuListener listener;
    private Context context;
    private boolean inited;

    public EaseChatInputMenu(Context context, AttributeSet attrs, int defStyle) {
        this(context, attrs);
    }

    public EaseChatInputMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public EaseChatInputMenu(Context context) {
        super(context);
        init(context, null);
    }

    private void init(Context context, AttributeSet attrs) {
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
        layoutInflater.inflate(R.layout.ease_widget_chat_input_menu, this);
        primaryMenuContainer = (FrameLayout) findViewById(R.id.primary_menu_container);
        emojiconMenuContainer = (FrameLayout) findViewById(R.id.emojicon_menu_container);
        chatExtendMenuContainer = (FrameLayout) findViewById(R.id.extend_menu_container);

         // ��չ��ť��
         chatExtendMenu = (EaseChatExtendMenu) findViewById(R.id.extend_menu);
        

    }

    /**
     * init view �˷��������registerExtendMenuItem���漰setCustomEmojiconMenu��
     * setCustomPrimaryMenu(�����Ҫ�Զ���������menu)����
     * @param emojiconGroupList ��������𣬴�nullʹ��easeuiĬ�ϵı���
     */
    public void init(List<EaseEmojiconGroupEntity> emojiconGroupList) {
        if(inited){
            return;
        }
        // ����ť�˵���,û���Զ������Ĭ�ϵ�
        if(chatPrimaryMenu == null){
            chatPrimaryMenu = (EaseChatPrimaryMenu) layoutInflater.inflate(R.layout.ease_layout_chat_primary_menu, null);
        }
        primaryMenuContainer.addView(chatPrimaryMenu);

        // ��������û���Զ������Ĭ�ϵ�
        if(emojiconMenu == null){
            emojiconMenu = (EaseEmojiconMenu) layoutInflater.inflate(R.layout.ease_layout_emojicon_menu, null);
            if(emojiconGroupList == null){
                emojiconGroupList = new ArrayList<EaseEmojiconGroupEntity>();
                emojiconGroupList.add(new EaseEmojiconGroupEntity(R.drawable.ee_1,  Arrays.asList(EaseDefaultEmojiconDatas.getData())));
            }
            ((EaseEmojiconMenu)emojiconMenu).init(emojiconGroupList);
        }
        emojiconMenuContainer.addView(emojiconMenu);

        processChatMenu();
        // ��ʼ��extendmenu
        chatExtendMenu.init();
        
        inited = true;
    }
    
    public void init(){
        init(null);
    }
    
    /**
     * �����Զ���ı��������ÿؼ���Ҫ�̳���EaseEmojiconMenuBase��
     * �Լ��ص�����Ҫ�ص���ȥ���¼������õ�EaseEmojiconMenuListener
     * @param customEmojiconMenu
     */
    public void setCustomEmojiconMenu(EaseEmojiconMenuBase customEmojiconMenu){
        this.emojiconMenu = customEmojiconMenu;
    }
    
    /**
     * �����Զ�������˵������ÿؼ���Ҫ�̳���EaseChatPrimaryMenuBase��
     * �Լ��ص�����Ҫ�ص���ȥ���¼������õ�EaseEmojiconMenuListener
     * @param customEmojiconMenu
     */
    public void setCustomPrimaryMenu(EaseChatPrimaryMenuBase customPrimaryMenu){
        this.chatPrimaryMenu = customPrimaryMenu;
    }
    
    public EaseChatPrimaryMenuBase getPrimaryMenu(){
        return chatPrimaryMenu;
    }
    
    public EaseChatExtendMenu getExtendMenu(){
        return chatExtendMenu;
    }
    
    public EaseEmojiconMenuBase getEmojiconMenu(){
        return emojiconMenu;
    }
    

    /**
     * ע����չ�˵���item
     * 
     * @param name
     *            item����
     * @param drawableRes
     *            item����
     * @param itemId
     *            id
     * @param listener
     *            item����¼�
     */
    public void registerExtendMenuItem(String name, int drawableRes, int itemId,
            EaseChatExtendMenuItemClickListener listener) {
        chatExtendMenu.registerMenuItem(name, drawableRes, itemId, listener);
    }

    /**
     * ע����չ�˵���item
     * 
     * @param name
     *            item����
     * @param drawableRes
     *            item����
     * @param itemId
     *            id
     * @param listener
     *            item����¼�
     */
    public void registerExtendMenuItem(int nameRes, int drawableRes, int itemId,
            EaseChatExtendMenuItemClickListener listener) {
        chatExtendMenu.registerMenuItem(nameRes, drawableRes, itemId, listener);
    }


    protected void processChatMenu() {
        // ������Ϣ��
        chatPrimaryMenu.setChatPrimaryMenuListener(new EaseChatPrimaryMenuListener() {

            @Override
            public void onSendBtnClicked(String content) {
                if (listener != null)
                    listener.onSendMessage(content);
            }

            @Override
            public void onToggleVoiceBtnClicked() {
                hideExtendMenuContainer();
            }

            @Override
            public void onToggleExtendClicked() {
                toggleMore();
            }

            @Override
            public void onToggleEmojiconClicked() {
                toggleEmojicon();
            }

            @Override
            public void onEditTextClicked() {
                hideExtendMenuContainer();
            }


            @Override
            public boolean onPressToSpeakBtnTouch(View v, MotionEvent event) {
                if(listener != null){
                    return listener.onPressToSpeakBtnTouch(v, event);
                }
                return false;
            }
        });

        // emojicon menu
        emojiconMenu.setEmojiconMenuListener(new EaseEmojiconMenuListener() {

            @Override
            public void onExpressionClicked(EaseEmojicon emojicon) {
                if(emojicon.getType() != EaseEmojicon.Type.BIG_EXPRESSION){
                    if(emojicon.getEmojiText() != null){
                        chatPrimaryMenu.onEmojiconInputEvent(EaseSmileUtils.getSmiledText(context,emojicon.getEmojiText()));
                    }
                }else{
                    if(listener != null){
                        listener.onBigExpressionClicked(emojicon);
                    }
                }
            }

            @Override
            public void onDeleteImageClicked() {
                chatPrimaryMenu.onEmojiconDeleteEvent();
            }
        });

    }

    /**
     * ��ʾ������ͼ�갴ťҳ
     * 
     */
    protected void toggleMore() {
        if (chatExtendMenuContainer.getVisibility() == View.GONE) {
            hideKeyboard();
            handler.postDelayed(new Runnable() {
                public void run() {
                    chatExtendMenuContainer.setVisibility(View.VISIBLE);
                    chatExtendMenu.setVisibility(View.VISIBLE);
                    emojiconMenu.setVisibility(View.GONE);
                }
            }, 50);
        } else {
            if (emojiconMenu.getVisibility() == View.VISIBLE) {
                emojiconMenu.setVisibility(View.GONE);
                chatExtendMenu.setVisibility(View.VISIBLE);
            } else {
                chatExtendMenuContainer.setVisibility(View.GONE);
            }

        }

    }

    /**
     * ��ʾ�����ر���ҳ
     */
    protected void toggleEmojicon() {
        if (chatExtendMenuContainer.getVisibility() == View.GONE) {
            hideKeyboard();
            handler.postDelayed(new Runnable() {
                public void run() {
                    chatExtendMenuContainer.setVisibility(View.VISIBLE);
                    chatExtendMenu.setVisibility(View.GONE);
                    emojiconMenu.setVisibility(View.VISIBLE);
                }
            }, 50);
        } else {
            if (emojiconMenu.getVisibility() == View.VISIBLE) {
                chatExtendMenuContainer.setVisibility(View.GONE);
                emojiconMenu.setVisibility(View.GONE);
            } else {
                chatExtendMenu.setVisibility(View.GONE);
                emojiconMenu.setVisibility(View.VISIBLE);
            }

        }
    }

    /**
     * ���������
     */
    private void hideKeyboard() {
        chatPrimaryMenu.hideKeyboard();
    }

    /**
     * ����������չ��ť��(����������)
     */
    public void hideExtendMenuContainer() {
        chatExtendMenu.setVisibility(View.GONE);
        emojiconMenu.setVisibility(View.GONE);
        chatExtendMenuContainer.setVisibility(View.GONE);
        chatPrimaryMenu.onExtendMenuContainerHide();
    }

    /**
     * ϵͳ���ؼ�����ʱ���ô˷���
     * 
     * @return ����false��ʾ���ؼ�ʱ��չ�˵���ʱ��״̬��true���ʾ�����ؼ�ʱ��չ���ǹر�״̬<br/>
     *         �������ʱ��״̬״̬�����ȹر���չ���ٷ���ֵ
     */
    public boolean onBackPressed() {
        if (chatExtendMenuContainer.getVisibility() == View.VISIBLE) {
            hideExtendMenuContainer();
            return false;
        } else {
            return true;
        }

    }
    

    public void setChatInputMenuListener(ChatInputMenuListener listener) {
        this.listener = listener;
    }

    public interface ChatInputMenuListener {
        /**
         * ������Ϣ��ť���
         * 
         * @param content
         *            �ı�����
         */
        void onSendMessage(String content);
        
        /**
         * ����鱻���
         * @param emojicon
         */
        void onBigExpressionClicked(EaseEmojicon emojicon);

        /**
         * ����˵����ťtouch�¼�
         * @param v
         * @param event
         * @return
         */
        boolean onPressToSpeakBtnTouch(View v, MotionEvent event);
    }
    
}
