package com.android.samchat.easemobdemo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;

import com.easemob.easeui.domain.EaseUser;

public class EaseMobModel {
    protected Context context = null;
    
    public EaseMobModel(Context ctx){
        context = ctx;
    }

    /*store contactlist into local db*/
    public boolean saveContactList(List<EaseUser> contactList) {
	 UserFriendDao dao = new UserFriendDao(context);
        dao.saveContactList(contactList);
        return true;
    }

    /*get contactlist from local db*/
    public Map<String, EaseUser> getContactList() {
	 UserFriendDao dao = new UserFriendDao(context);
        return dao.getContactList();
    }


    /*store single user into local db    */
    public void saveContact(EaseUser user){
        UserFriendDao dao = new UserFriendDao(context);
        dao.saveContact(user);
    }

    public void deleteContact(EaseUser user){
	 UserFriendDao dao = new UserFriendDao(context);
	 dao.deleteContact(user.getUsername());
    }

    public void setContactSynced(boolean synced){
        EaseMobPreference.getInstance().setContactSynced(synced);
    }
    
    public boolean isContactSynced(){
        return EaseMobPreference.getInstance().isContactSynced();
    }

     public void setGroupSynced(boolean synced){
        EaseMobPreference.getInstance().setGroupsSynced(synced);
    }
    
    public boolean isGroupSynced(){
        return EaseMobPreference.getInstance().isGroupsSynced();
    }
    
    public void setBlacklistSynced(boolean synced){
        EaseMobPreference.getInstance().setBlacklistSynced(synced);
    }
    
      
    public boolean isBacklistSynced(){
        return EaseMobPreference.getInstance().isBacklistSynced();
    }
    
}
