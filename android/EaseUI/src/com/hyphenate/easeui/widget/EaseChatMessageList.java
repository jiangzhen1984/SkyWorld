package com.hyphenate.easeui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.hyphenate.chat.EMChatManager;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.R;
import com.hyphenate.easeui.adapter.EaseMessageAdapter;
import com.hyphenate.easeui.utils.EaseCommonUtils;
import com.hyphenate.easeui.widget.chatrow.EaseCustomChatRowProvider;

public class EaseChatMessageList extends RelativeLayout{
    
    protected static final String TAG = "EaseChatMessageList";
    protected ListView listView;
    protected SwipeRefreshLayout swipeRefreshLayout;
    protected Context context;
    protected EMConversation conversation;
    protected int chatType;
    protected String toChatUsername;
    protected EaseMessageAdapter messageAdapter;
    protected boolean showUserNick;
    protected boolean showAvatar;
    protected Drawable myBubbleBg;
    protected Drawable otherBuddleBg;

	public EaseChatMessageList(Context context, AttributeSet attrs, int defStyle) {
        this(context, attrs);
    }

    public EaseChatMessageList(Context context, AttributeSet attrs) {
    	super(context, attrs);
    	parseStyle(context, attrs);
    	init(context);
    }

    public EaseChatMessageList(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context){
        this.context = context;
        LayoutInflater.from(context).inflate(R.layout.ease_chat_message_list, this);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.chat_swipe_layout);
        listView = (ListView) findViewById(R.id.list);
    }
    
    /**
     * init widget
     * @param toChatUsername
     * @param chatType
     * @param customChatRowProvider
     */
    public void init(String toChatUsername, int chatType, EaseCustomChatRowProvider customChatRowProvider) {
        this.chatType = chatType;
        this.toChatUsername = toChatUsername;
        
        conversation = EMClient.getInstance().chatManager().getConversation(toChatUsername, EaseCommonUtils.getConversationType(chatType), true);
        messageAdapter = new EaseMessageAdapter(context, toChatUsername, chatType, listView);
        messageAdapter.setShowAvatar(showAvatar);
        messageAdapter.setShowUserNick(showUserNick);
        messageAdapter.setMyBubbleBg(myBubbleBg);
        messageAdapter.setOtherBuddleBg(otherBuddleBg);
        messageAdapter.setCustomChatRowProvider(customChatRowProvider);
        // ����adapter��ʾ��Ϣ
        listView.setAdapter(messageAdapter);
        
        refreshSelectLast();
    }
    
    protected void parseStyle(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.EaseChatMessageList);
        showAvatar = ta.getBoolean(R.styleable.EaseChatMessageList_msgListShowUserAvatar, true);
        myBubbleBg = ta.getDrawable(R.styleable.EaseChatMessageList_msgListMyBubbleBackground);
        otherBuddleBg = ta.getDrawable(R.styleable.EaseChatMessageList_msgListMyBubbleBackground);
        showUserNick = ta.getBoolean(R.styleable.EaseChatMessageList_msgListShowUserNick, false);
        ta.recycle();
    }
    
    
    /**
     * ˢ���б�
     */
    public void refresh(){
        if (messageAdapter != null) {
            messageAdapter.refresh();
        }
    }
    
    /**
     * ˢ���б������������һ��item
     */
    public void refreshSelectLast(){
        if (messageAdapter != null) {
            messageAdapter.refreshSelectLast();
        }
    }
    
    /**
     * ˢ��ҳ��,����������position
     * @param position
     */
    public void refreshSeekTo(int position){
        if (messageAdapter != null) {
            messageAdapter.refreshSeekTo(position);
        }
    }
    
    

    /**
     * ��ȡlistview
     * @return
     */
	public ListView getListView() {
		return listView;
	} 
	
	/**
	 * ��ȡSwipeRefreshLayout
	 * @return
	 */
	public SwipeRefreshLayout getSwipeRefreshLayout(){
	    return swipeRefreshLayout;
	}
	
	public EMMessage getItem(int position){
	    return messageAdapter.getItem(position);
	}
	
	/**
	 * �����Ƿ���ʾ�û��ǳ�
	 * @param showUserNick
	 */
	public void setShowUserNick(boolean showUserNick){
	    this.showUserNick = showUserNick;
	}
	
	public boolean isShowUserNick(){
	    return showUserNick;
	}
	
	public interface MessageListItemClickListener{
	    void onResendClick(EMMessage message);
	    /**
	     * �ؼ��ж�����������¼�Ĭ��ʵ�֣������Ҫ�Լ�ʵ�֣�return true��
	     * ��ȻҲ��������Ӧ��chatrow��onBubbleClick()������ʵ�ֵ���¼�
	     * @param message
	     * @return
	     */
	    boolean onBubbleClick(EMMessage message);
	    void onBubbleLongClick(EMMessage message);
	    void onUserAvatarClick(String username);
	}
	
	/**
	 * ����list item��ؼ��ĵ���¼�
	 * @param listener
	 */
	public void setItemClickListener(MessageListItemClickListener listener){
        if (messageAdapter != null) {
            messageAdapter.setItemClickListener(listener);
        }
	}
	
	/**
	 * �����Զ���chatrow�ṩ��
	 * @param rowProvider
	 */
	public void setCustomChatRowProvider(EaseCustomChatRowProvider rowProvider){
        if (messageAdapter != null) {
            messageAdapter.setCustomChatRowProvider(rowProvider);
        }
    }
}
