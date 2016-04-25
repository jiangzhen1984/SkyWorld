package com.android.samchat;

import android.content.Intent;
import android.os.Bundle;

import com.android.samservice.Constants;
import com.android.samservice.SamService;
import com.android.samservice.info.ContactUser;
import com.android.samservice.info.LoginUser;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.ui.EaseChatFragment;
import com.hyphenate.easeui.ui.EaseChatFragment.EaseChatFragmentListener;
import com.hyphenate.easeui.ui.EaseChatFragment.ClientDBInterface;
import com.hyphenate.easeui.widget.chatrow.EaseCustomChatRowProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Toast;

public class SamChatFragment extends EaseChatFragment implements EaseChatFragmentListener,ClientDBInterface{
    public static final int CONFIRM_ID_BACK_FROM_GROUP_DETAILS = 10;

   
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    protected void setUpView() {
        setChatFragmentListener(this);
	 setClientDBCallBack(this);
        super.setUpView();
    }

    @Override
    public void onSetMessageAttributes(EMMessage message) {
        return;
    }
    
    @Override
    public EaseCustomChatRowProvider onSetCustomChatRowProvider() {
        return null;
    }
    
    @Override
    public void onEnterToChatDetails() {
        if (chatType == Constants.CHATTYPE_GROUP) {
            EMGroup group = EMClient.getInstance().groupManager().getGroup(toChatUsername);
            if (group == null) {
                Toast.makeText(getActivity(), R.string.gorup_not_found, Toast.LENGTH_SHORT).show();
                return;
            }
            startActivityForResult(
                    (new Intent(getActivity(), GroupDetailsActivity.class).putExtra("groupId", toChatUsername)),
                    CONFIRM_ID_BACK_FROM_GROUP_DETAILS);
        }
    }

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == CONFIRM_ID_BACK_FROM_GROUP_DETAILS){
			updateGroupChatName();
		}else{
			super.onActivityResult(requestCode, resultCode, data);
		}
	}

    @Override
    public void onAvatarClick(String username) {
        //头像点击事件

    }
    
    @Override
    public boolean onMessageBubbleClick(EMMessage message) {
        //消息框点击事件，demo这里不做覆盖，如需覆盖，return true
        return false;
    }

    @Override
    public void onMessageBubbleLongClick(EMMessage message) {
        //消息框长按

    }

    @Override
    public boolean onExtendMenuItemClick(int itemId, View view) {
      
        return false;
    }


    @Override
    public List<String> getNotResponsedQuestion(String sender){
	LoginUser me = SamService.getInstance().get_current_user();
	ContactUser cuser = SamService.getInstance().getDao().query_ContactUser_db_by_username(sender);
	if(cuser == null){
		return null;
	}
	
       List<String> questions = SamService.getInstance().getDao().get_ReceivedQuestion_Not_Response_db(cuser.getid(),me.getusername());

	return questions;
    }
   
}


