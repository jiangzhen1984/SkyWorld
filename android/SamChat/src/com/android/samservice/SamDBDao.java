package com.android.samservice;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;

import com.android.samservice.info.*;
import com.android.samservice.provider.DBManager;

public class SamDBDao{
	public static final String TAG="SamDBDao";
	private Object dbLock;
	private DBManager dbHandle;

	public void close(){
		synchronized(dbLock){
			if(dbHandle!=null)
				dbHandle.closeDB();
		}
	}

	public SamDBDao(){
		dbLock = null;
		dbHandle = null;
	}

	public SamDBDao(Context context){
		dbLock = new Object();
		dbHandle = new DBManager(context);
	}

	public long updateLoginUserEaseStatus(String phonenumber,int status){
		long ret = 0;
		synchronized(dbLock){
			ret = dbHandle.updateLoginUserEasemobStatus(phonenumber,status);
		}

		if(ret == -1){
			throw new RuntimeException("db error: login user table");
		}

		return ret;
	}

	public long updateLoginUserAllStatus(String phonenumber,int status){
		long ret = 0;
		synchronized(dbLock){
			ret = dbHandle.updateLoginUserAllStatus(phonenumber,status);
		}

		if(ret == -1){
			throw new RuntimeException("db error: login user table");
		}

		return ret;
	}

	

	public long add_update_LoginUser_db(LoginUser user){
		long ret = 0;
		synchronized(dbLock){
			LoginUser tuser = dbHandle.queryLogInUser(user.phonenumber);
			if(tuser !=null){
				ret = dbHandle.updateLogInUser(tuser.id, user);
			}else{
				ret = dbHandle.addLogInUser(user);
			}	
		}

		if(ret == -1){
			throw new RuntimeException("db error: login user table");
		}

		return ret;
		
	}

	public long update_LogoutUser_db(LoginUser user){
		synchronized(dbLock){
			user.status = LoginUser.INACTIVE;
			user.logouttime = System.currentTimeMillis();
			SamLog.e(TAG,"easemob:"+user.geteasemob_username());
			return dbHandle.updateLogInUser(user.id, user);
		}
	}

	public long update_LogoutUser_db(String phonenumber){
		long ret = 0;
		int status = LoginUser.INACTIVE;
		long logouttime = System.currentTimeMillis();
		synchronized(dbLock){
			ret = dbHandle.updateLoginUserLogoutStatus(phonenumber,status,logouttime);
		}

		if(ret == -1){
			throw new RuntimeException("db error: login user table");
		}

		return ret;
	}

	public LoginUser query_activie_LoginUser_db(){
		synchronized(dbLock){
			return dbHandle.queryLogInUser();
		}
	}

	public List<LoginUser> query_AllLoginUser_db(){
		synchronized(dbLock){
			return dbHandle.queryAllLoginUser();
		}
	}

	public LoginUser query_LoginUser_db(String cellphone){
		synchronized(dbLock){
			return dbHandle.queryLogInUser(cellphone);
		}
	}

	public long add_update_SendQuestion_db(SendQuestion question){
		synchronized(dbLock){
			SendQuestion tq = dbHandle.querySendQuestion(question.question_id);
			if(tq !=null){
				return dbHandle.updateSendQuestion(tq.id, question);
			}else{
				return dbHandle.addSendQuestion(question);
			}	
		}

	}

	public SendQuestion query_send_question_db(String question_id){
		synchronized(dbLock){
			return dbHandle.querySendQuestion(question_id);
		}

	}

	public long add_update_ContactUser_db(ContactUser user){
		long ret = -1;
		synchronized(dbLock){
			ContactUser tuser = dbHandle.queryContactUser(user.getphonenumber());
			if(tuser !=null){
				ret = dbHandle.updateContactUser(tuser.getid(), user);
			}else{
				ret = dbHandle.addContactUser(user);
			}	
		}

		if(ret == -1){
			throw new RuntimeException("db error: contact user table");
		}

		return ret;
	}

	public  ContactUser query_ContactUser_db(String phonenumber){
		synchronized(dbLock){
			return dbHandle.queryContactUser(phonenumber);
		}

	}

	public  ContactUser query_ContactUser_db(long id){
		synchronized(dbLock){
			return dbHandle.queryContactUser(id);
		}

	}

	
	public long add_update_ReceivedQuestion_db(ReceivedQuestion quest){
		synchronized(dbLock){
			ReceivedQuestion tq = dbHandle.queryReceivedQuestion(quest.getquestion_id());
			if(tq !=null){
				return dbHandle.updateReceivedQuestion(tq.getid(), quest);
			}else{
				return dbHandle.addReceivedQuestion(quest);
			}	
		}
	}

	public ReceivedQuestion query_ReceivedQuestion_db(String question_id){
		synchronized(dbLock){
			return dbHandle.queryReceivedQuestion(question_id);
		}
	}

	public List<ReceivedQuestion> query_RecentReceivedQuestion_db(long num){
		synchronized(dbLock){
			return dbHandle.queryRecentReceivedQuestion(num,SamService.getInstance().get_current_user().getphonenumber());
		}
	}

	public long add_SendAnswer_db(SendAnswer answer){
		synchronized(dbLock){
				return dbHandle.addSendAnswer(answer);
		}
	}

	public long update_SendAnswer_db(SendAnswer answer){
		synchronized(dbLock){
				return dbHandle.updateSendAnswer(answer.getid(),answer);
		}
	}

	public List<SendAnswer> query_SendAnswer_db(String question_id){
		synchronized(dbLock){
				return dbHandle.querySendAnswer(question_id);
		}
	}

	public long add_ReceivedAnswer_db(ReceivedAnswer answer){
		synchronized(dbLock){
				return dbHandle.addReceivedAnswer(answer);
		}
	}

	public List<ReceivedAnswer> query_ReceivedAnswer_db(String question_id){
		synchronized(dbLock){
				return dbHandle.queryReceivedAnswer(question_id);
		}
	}

	public long add_update_InviteMsgRecord_db(InviteMessageRecord record){
		synchronized(dbLock){
			if(record.id == 0){
				return dbHandle.addInviteRecord(record);
			}else{
				return dbHandle.updateInviteRecord(record.id, record);
			}
		}
	}

	public long updateInviteMsg(long id, ContentValues values){
		synchronized(dbLock){
			return dbHandle.updateInviteRecord( id,  values);
		}
	}

	public void delete_InviteMsgRecord_db(String receiver,String sender){
		
		synchronized(dbLock){
			dbHandle.deleteInviteRecord(receiver,sender);
		}
	}

	public List<InviteMessageRecord> query_InviteMsgRecordBasedReceiver_db(String receiver){
		synchronized(dbLock){
				return dbHandle.queryInviteRecordBasedReceiver(receiver);
		}
	}

	public List<InviteMessageRecord> query_InviteMsgRecordBasedReceiver_db(String receiver,int status){
		synchronized(dbLock){
				return dbHandle.queryInviteRecordBasedReceiver(receiver,status);
		}
	}

	public List<InviteMessageRecord> query_InviteMsgRecordBasedReceiverSender_db(String receiver,String sender){
		synchronized(dbLock){
				return dbHandle.queryInviteRecordBasedReceiverSender(receiver,sender);
		}
	}

	public long add_update_UserFriendRecord_db(UserFriendRecord record){
		synchronized(dbLock){
			if(record.id == 0){
				return dbHandle.addUserFriendRecord(record);
			}else{
				return dbHandle.updateUserFriendRecord(record.id, record);
			}
		}
	}

	public List<UserFriendRecord> query_UserFriendRecord_db(){
		synchronized(dbLock){
				return dbHandle.queryUserFriendRecord();
		}
	}

	public UserFriendRecord query_UserFriendRecord_db(String easemob_name){
		synchronized(dbLock){
				return dbHandle.queryUserFriendRecord(easemob_name);
		}
	}

	public void sava_UserFriendList_db(List<UserFriendRecord> list){
		synchronized(dbLock){
				dbHandle.clearUserFriendTable();
				for (UserFriendRecord record : list) {
					dbHandle.addUserFriendRecord(record);
				}
				SamLog.e(TAG,"save friend list:"+list.size());
		}
		
		
	}

	public void delete_UserFriendRecord_db(String easemob_name){
		synchronized(dbLock){
				dbHandle.deleteUserFriend(easemob_name);
		}
	}

	public long add_update_AvatarRecord_db(AvatarRecord record){
		synchronized(dbLock){
			if(record.id == 0){
				return dbHandle.addAvatarRecord(record);
			}else{
				return dbHandle.updateAvatarRecord(record.id, record);
			}
		}
	}

	public long add_update_AvatarRecord_db(String phonenumber,String nickname, String filename,StringBuffer oldAvatar){
		long ret = -1;
		synchronized(dbLock){
			AvatarRecord rd = dbHandle.queryAvatarRecord(phonenumber);
			if(rd!=null){
				oldAvatar.append(rd.getavatarname());
				if(oldAvatar.length()!=0 && oldAvatar.toString().equals(filename)){
					SamLog.e(TAG,"Waring: oldAvatar is same to new Avatar!");
					oldAvatar.delete(0, oldAvatar.length());
				}else{
					rd.setavatarname(filename);
				}
				rd.setnickname(nickname);
				ret = dbHandle.updateAvatarRecord(rd.id, rd);
			}else{
				rd = new AvatarRecord(phonenumber,filename,nickname);
				ret = dbHandle.addAvatarRecord(rd);
			}
		}

		if(ret == -1){
			throw new RuntimeException("db error: avatar table");
		}

		return ret;
	}

	public long add_update_AvatarRecord_db(String phonenumber,String nickname, String filename){
		long ret = -1;
		synchronized(dbLock){
			AvatarRecord rd = dbHandle.queryAvatarRecord(phonenumber);
			if(rd!=null){
				rd.setavatarname(filename);
				rd.setnickname(nickname);
				ret = dbHandle.updateAvatarRecord(rd.id, rd);
			}else{
				rd = new AvatarRecord(phonenumber,filename,nickname);
				ret = dbHandle.addAvatarRecord(rd);
			}
		}

		if(ret == -1){
			throw new RuntimeException("db error: avatar table");
		}

		return ret;
	}


	public AvatarRecord query_AvatarRecord_db(String phonenumber){
		synchronized(dbLock){
				return dbHandle.queryAvatarRecord(phonenumber);
		}
	}

	public boolean isAvatarExistedInDB(String phonenumber,String shortImg){
		AvatarRecord rd = query_AvatarRecord_db(phonenumber);
		if(rd!=null && rd.getavatarname()!=null && rd.getavatarname().equals(shortImg)){
			return true;
		}else{
			return false;
		}
		
	}

	public boolean isAvatarExistedInFS(String shortImg){
		//delete avatar file
		File filePath = new File(SamService.sam_cache_path+SamService.AVATAR_FOLDER);

		if(filePath.exists()){
			File file = new File(SamService.sam_cache_path+SamService.AVATAR_FOLDER+"/"+shortImg);
			if(file.exists()){
				return true;
			}
		}

		return false;
	}


}
