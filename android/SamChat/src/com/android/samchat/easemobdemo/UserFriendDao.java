package com.android.samchat.easemobdemo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;

import com.android.samservice.Constants;
import com.android.samservice.SamLog;
import com.android.samservice.SamService;
import com.android.samservice.info.AvatarRecord;
import com.android.samservice.info.ContactUser;
import com.android.samservice.info.UserFriendRecord;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.utils.EaseCommonUtils;


public class UserFriendDao{
	
	public UserFriendDao(Context context) {
	}

	/**
	 * 保存好友list
	 * 
	 * @param contactList
	 */

	public void saveContactList(List<EaseUser> contactList) {
		List<UserFriendRecord> list = new ArrayList<UserFriendRecord>();
		for(EaseUser user : contactList){
			list.add(new UserFriendRecord(user.getUsername()));
		}

		SamService.getInstance().getDao().save_UserFriendList_db(list);
	}
	
	/**
	 * 获取好友list
	 * 
	 * @return
	 */

	public Map<String, EaseUser> getContactList() {
		Map<String, EaseUser> users = new HashMap<String, EaseUser>();
		
		List<UserFriendRecord> list = SamService.getInstance().getDao().query_UserFriendRecord_db();
		for(UserFriendRecord rd: list){
			EaseUser user = new EaseUser(rd.getfriend());
			ContactUser cuser = null;
			if(!Constants.USERNAME_EQUAL_EASEMOB_ID){
			 	cuser = SamService.getInstance().getDao().query_ContactUser_db(rd.getfriend());
			}else{
				cuser = SamService.getInstance().getDao().query_ContactUser_db_by_username(rd.getfriend());
			}
			
			if(cuser!=null && cuser.getusername()!=null){
				user.setNick(cuser.getusername());
			}

			if(cuser!=null && cuser.getusername()!=null){
				AvatarRecord ard = SamService.getInstance().getDao().query_AvatarRecord_db_by_username(cuser.getusername());
				if(ard!=null && ard.getavatarname()!=null){
					user.setAvatar(SamService.sam_cache_path+SamService.AVATAR_FOLDER+"/"+ard.getavatarname());
					//SamLog.e("TEST","getAvatar:"+user.getAvatar());
				}
			}
			EaseCommonUtils.setUserInitialLetter(user);
			users.put(rd.getfriend(), user);
		}
		return users;
	    
	}

	public EaseUser getContact(String easemob_name) {
		
		UserFriendRecord rd = SamService.getInstance().getDao().query_UserFriendRecord_db(easemob_name);
		if(rd!=null){
			EaseUser user = new EaseUser(rd.getfriend());
			ContactUser cuser = null;
			if(!Constants.USERNAME_EQUAL_EASEMOB_ID){
			 	cuser = SamService.getInstance().getDao().query_ContactUser_db(rd.getfriend());
			}else{
				cuser = SamService.getInstance().getDao().query_ContactUser_db_by_username(rd.getfriend());
			}

			if(cuser!=null && cuser.getusername()!=null){
				user.setNick(cuser.getusername());
			}

			if(cuser!=null && cuser.getusername()!=null){
				AvatarRecord ard = SamService.getInstance().getDao().query_AvatarRecord_db_by_username(cuser.getusername());
				if(ard!=null && ard.getavatarname()!=null){
					user.setAvatar(SamService.sam_cache_path+SamService.AVATAR_FOLDER+"/"+ard.getavatarname());
				}
			}
			EaseCommonUtils.setUserInitialLetter(user);

			return user;
		}else{
			return null;
		}	    
	}

	/**
	 * 保存一个联系人
	 * @param user
	 */
	public void saveContact(EaseUser user){
		 SamService.getInstance().getDao().add_update_UserFriendRecord_db(new UserFriendRecord(user.getUsername()));
	}

	
	/**
	 * 删除一个联系人
	 * @param username
	 */
	public void deleteContact(String username){
	    SamService.getInstance().getDao().delete_UserFriendRecord_db(username);
	}
	
}

