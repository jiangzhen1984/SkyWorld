package com.android.samchat.easemobdemo;

import java.util.List;

import com.android.samservice.SamService;
import com.android.samservice.info.InviteMessageRecord;
import com.android.samservice.info.LoginUser;

import android.content.ContentValues;
import android.content.Context;


public class InviteMessgeDao {
		
	public InviteMessgeDao(Context context){
	}
	
	/**
	 * 保存message
	 * @param message
	 * @return  返回这条messaged在db中的id
	 */
	public long add_update_Message(InviteMessageRecord message){
		return SamService.getInstance().getDao().add_update_InviteMsgRecord_db(message);
	}
	
	/**
	 * 获取messges
	 * @return
	 */
	public List<InviteMessageRecord> getMessagesList(){
		LoginUser user = SamService.getInstance().get_current_user();
		
		return SamService.getInstance().getDao().query_InviteMsgRecordBasedReceiver_db(user.geteasemob_username());
	}

	public List<InviteMessageRecord> getMessagesList(String receiver,String sender){
		return SamService.getInstance().getDao().query_InviteMsgRecordBasedReceiverSender_db(receiver,sender);
	}

	public void deleteMessage(String sender){
	    LoginUser user = SamService.getInstance().get_current_user();
	    SamService.getInstance().getDao().delete_InviteMsgRecord_db(user.geteasemob_username(),sender);
	}

	public long updateMessage(long id, ContentValues values){
		return SamService.getInstance().getDao().updateInviteMsg(id, values);
	}
	
}

