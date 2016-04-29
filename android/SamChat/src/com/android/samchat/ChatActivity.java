package com.android.samchat;

import com.android.samservice.Constants;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.ui.EaseBaseActivity;
import com.hyphenate.easeui.ui.EaseChatFragment;

import android.content.Intent;
import android.os.Bundle;


public class ChatActivity extends EaseBaseActivity{
    public static ChatActivity activityInstance;
    private EaseChatFragment chatFragment;
    String toChatUsername;
    String chatActivityType;
    int chatType;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_chat);
        activityInstance = this;
        //�����˻�Ⱥid
        toChatUsername = getIntent().getExtras().getString(EaseConstant.EXTRA_USER_ID);
	 chatType = getIntent().getExtras().getInt(EaseConstant.EXTRA_CHAT_TYPE, EaseConstant.CHATTYPE_SINGLE);
	 if(chatType == EaseConstant.CHATTYPE_SINGLE){
	 	chatActivityType = getIntent().getExtras().getString(Constants.CHAT_ACTIVITY_TYPE);
	 }else{
		chatActivityType = null;
	 }
        chatFragment = new SamChatFragment();
        //�������
        chatFragment.setArguments(getIntent().getExtras());
        getSupportFragmentManager().beginTransaction().add(R.id.container, chatFragment).commit();
        
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        activityInstance = null;
    }
    
    @Override
    protected void onNewIntent(Intent intent) {
        // ���notification bar��������ҳ�棬��ֻ֤��һ������ҳ��
        String username = intent.getStringExtra("userId");
        if (toChatUsername.equals(username))
            super.onNewIntent(intent);
        else {
            finish();
            startActivity(intent);
        }

    }
    @Override
    public void onBackPressed() {
        chatFragment.onBackPressed();
    }
    
    public String getToChatUsername(){
        return toChatUsername;
    }
}
