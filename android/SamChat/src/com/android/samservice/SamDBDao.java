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

	public long updateLoginUserEaseStatus(String username,int status){
		long ret = 0;
		synchronized(dbLock){
			ret = dbHandle.updateLoginUserEasemobStatus(username,status);
		}

		if(ret == -1){
			throw new RuntimeException("db error: login user table");
		}

		return ret;
	}

	public long updateLoginUserAllStatus(String username,int status){
		long ret = 0;
		synchronized(dbLock){
			ret = dbHandle.updateLoginUserAllStatus(username,status);
		}

		if(ret == -1){
			throw new RuntimeException("db error: login user table");
		}

		return ret;
	}

	

	public long add_update_LoginUser_db(LoginUser user){
		long ret = 0;
		synchronized(dbLock){
			LoginUser tuser = dbHandle.queryLogInUserByUsername(user.username);
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

	public long update_LogoutUser_db(String username){
		long ret = 0;
		int status = LoginUser.INACTIVE;
		long logouttime = System.currentTimeMillis();
		synchronized(dbLock){
			ret = dbHandle.updateLoginUserLogoutStatus(username,status,logouttime);
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

	public LoginUser query_LoginUser_db_by_username(String username){
		synchronized(dbLock){
			return dbHandle.queryLogInUserByUsername(username);
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
			ContactUser tuser = dbHandle.queryContactUserByUsername(user.getusername());
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

	public  ContactUser query_ContactUser_db_by_username(String usrname){
		synchronized(dbLock){
			return dbHandle.queryContactUserByUsername(usrname);
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

	public List<ReceivedQuestion> query_RecentReceivedQuestion_db(){
		synchronized(dbLock){
			return dbHandle.queryRecentReceivedQuestion(SamService.getInstance().get_current_user().getusername());
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

	public List<SendAnswer> query_SendAnswer_db(String question_id,long sender_id){
		synchronized(dbLock){
				return dbHandle.querySendAnswer(question_id,sender_id);
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

	public void save_UserFriendList_db(List<UserFriendRecord> list){
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
			AvatarRecord rd = dbHandle.queryAvatarRecordByUsername(nickname);
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
				rd.setavatarname(filename);
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

	public AvatarRecord query_AvatarRecord_db_by_username(String useranme){
		synchronized(dbLock){
				return dbHandle.queryAvatarRecordByUsername(useranme);
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

	public boolean isAvatarExistedInDBByUsername(String username,String shortImg){
		AvatarRecord rd = query_AvatarRecord_db_by_username(username);
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

	public long add_FGRecord_db(FGRecord rd){
		synchronized(dbLock){
				return dbHandle.addFGRecord(rd);
		}
	}

	public List<FGRecord> query_FGRecord_db(String owner_phonenumber){
		synchronized(dbLock){
				return dbHandle.queryFGRecord(owner_phonenumber);
		}
	}

	public FGRecord query_FGRecord_db(long fg_id,String owner_phonenumber){
		synchronized(dbLock){
				return dbHandle.queryFGRecord(fg_id,owner_phonenumber);
		}
	}

	public List<FGRecord> query_FGRecord_db_by_username(String owner_username){
		synchronized(dbLock){
				return dbHandle.queryFGRecordByUsername(owner_username);
		}
	}

	public FGRecord query_FGRecord_db_by_username(long fg_id,String owner_username){
		synchronized(dbLock){
				return dbHandle.queryFGRecordByUsername(fg_id,owner_username);
		}
	}

	public RecommanderRecord query_RecommanderRecord_db(String recommander_phonenumber,long fg_id,long timestamp){
		synchronized(dbLock){
				return dbHandle.queryRecommanderRecord(recommander_phonenumber, fg_id, 0);
		}
	}

	public long add_RecommanderRecord_db(RecommanderRecord record){
		synchronized(dbLock){
				return dbHandle.addRecommanderRecord(record);
		}
	}

	public CommenterRecord query_CommenterRecord_db(String commenter_phonenumber,long fg_id,long timestamp){
		synchronized(dbLock){
				return dbHandle.queryCommenterRecord(commenter_phonenumber,fg_id,timestamp);
		}
	}

	public List<CommenterRecord> query_CommenterRecord_db(long fg_id){
		synchronized(dbLock){
				return dbHandle.queryCommenterRecord(fg_id);
		}
	}

	public long add_CommenterRecord_db(CommenterRecord record){
		synchronized(dbLock){
				return dbHandle.addCommenterRecord(record);
		}
	}

	public PictureRecord query_PictureRecord_db(long fg_id,String url_thumbnail){
		synchronized(dbLock){
				return dbHandle.queryPictureRecord(fg_id,url_thumbnail);
		}
	}

	public List<PictureRecord> query_PictureRecord_db(long fg_id){
		synchronized(dbLock){
				return dbHandle.queryPictureRecord(fg_id);
		}
	}

	

	public PictureRecord query_PictureRecord_db_thumbnail_pic(long fg_id, String thumbnail_pic){
		synchronized(dbLock){
				return dbHandle.queryPictureRecord_thumbnail_pic(fg_id,thumbnail_pic);
		}
	}

	public long add_PictureRecord_db(PictureRecord record){
		synchronized(dbLock){
				return dbHandle.addPictureRecord(record);
		}
	}


	public long update_PictureRecord_db_thumbnail(long fg_id, String thumbnail_pic,int sequence){
		synchronized(dbLock){
			return dbHandle.updatePictureRecord_thumbnail(fg_id, thumbnail_pic,sequence);
		}
	}

	public boolean isThumbPicExistedInDB(long fg_id,String shortImg){
		PictureRecord rd = query_PictureRecord_db_thumbnail_pic(fg_id,shortImg);
		if(rd!=null && rd.getthumbnail_pic()!=null && rd.getthumbnail_pic().equals(shortImg)){
			return true;
		}else{
			return false;
		}
		
	}

	public boolean isThumbPicExistedInFS(String shortImg){
		//delete avatar file
		File filePath = new File(SamService.sam_cache_path+SamService.FG_PIC_FOLDER);

		if(filePath.exists()){
			File file = new File(SamService.sam_cache_path+SamService.FG_PIC_FOLDER+"/"+shortImg);
			if(file.exists()){
				return true;
			}
		}

		return false;
	}

	public void clear_FollowerRecord_db(){
		synchronized(dbLock){
				dbHandle.clearFollowerTable();
		}
	}

	public void save_FollowerRecordList_db(List<FollowerRecord> list){
		synchronized(dbLock){
				dbHandle.clearFollowerTable();
				for (FollowerRecord record : list) {
					dbHandle.addFollowerRecord(record);
				}
		}
	}

	public long add_FollowerRecord_db(FollowerRecord record){
		synchronized(dbLock){
				return dbHandle.addFollowerRecord(record);
		}
	}

	public long update_FollowerRecord_db(long id, FollowerRecord record){
		synchronized(dbLock){
				return dbHandle.updateFollowerRecord(id, record);
		}
	}

	public void delete_FollowerRecord_db(long unique_id){
		synchronized(dbLock){
				dbHandle.deleteFollower(unique_id);
		}
	}

	public FollowerRecord query_FollowerRecord_db(long unique_id,long owner_unique_id){
		synchronized(dbLock){
				return dbHandle.queryFollowerRecord(unique_id,owner_unique_id);
		}
	}

	public List<FollowerRecord> query_FollowerRecord_db(long owner_unique_id){
		synchronized(dbLock){
				return dbHandle.queryFollowerRecord(owner_unique_id);
		}
	}



}
