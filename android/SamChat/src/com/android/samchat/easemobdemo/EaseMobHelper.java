package com.android.samchat.easemobdemo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.android.samchat.MainActivity;
import com.android.samservice.Constants;
import com.android.samservice.SMCallBack;
import com.android.samservice.SamLog;
import com.android.samservice.SamService;
import com.android.samservice.info.AvatarRecord;
import com.android.samservice.info.ContactUser;
import com.android.samservice.info.InviteMessageRecord;
import com.android.samservice.info.InviteMessageRecord.InviteMessageStatus;
import com.android.samservice.info.LoginUser;
import com.android.samservice.info.SendQuestion;
import com.hyphenate.EMCallBack;
import com.hyphenate.EMConnectionListener;
import com.hyphenate.EMContactListener;
import com.hyphenate.EMError;
import com.hyphenate.EMMessageListener;
import com.hyphenate.EMValueCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMContactManager;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMConversation.EMConversationType;
import com.hyphenate.chat.EMGroupManager;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.controller.EaseUI;
import com.hyphenate.easeui.controller.EaseUI.EaseSettingsProvider;
import com.hyphenate.easeui.controller.EaseUI.EaseUserProfileProvider;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.utils.EaseCommonUtils;
import com.hyphenate.exceptions.HyphenateException;



public class EaseMobHelper {

	static public interface DataSyncListener {
		public void onSyncComplete(boolean success);
	}

	protected static final String TAG = "EaseMobHelper";

    /**
     * EMEventListener
     */
    //protected EMEventListener eventListener = null;

	private Map<String, EaseUser> contactList;

	private static EaseMobHelper instance = null;
	
	private EaseMobModel demoModel = null;

	private EaseUI easeUI;
	
	/**
     * HuanXin sync groups status listener
     */
    private List<DataSyncListener> syncGroupsListeners;
    /**
     * HuanXin sync contacts status listener
     */
    private List<DataSyncListener> syncContactsListeners;
    /**
     * HuanXin sync blacklist status listener
     */
    private List<DataSyncListener> syncBlackListListeners;

    private boolean isSyncingGroupsWithServer = false;
    private boolean isSyncingContactsWithServer = false;
    private boolean isSyncingBlackListWithServer = false;
    private boolean isGroupsSyncedWithServer = false;
    private boolean isContactsSyncedWithServer = false;
    private boolean isBlackListSyncedWithServer = false;
    
    private boolean alreadyNotified = false;
	
	public boolean isVoiceCalling;
    public boolean isVideoCalling;

	private String username;

    private Context appContext;


    private EMConnectionListener connectionListener;

    private InviteMessgeDao inviteMessgeDao;
    private UserFriendDao userfDao;

    private LocalBroadcastManager broadcastManager;

    private boolean isContactGroupListenerRegisted;
    EMMessageListener messageListener;
	
    private Object lock;

	private EaseMobHelper() {
		lock = new Object();
	}

	public synchronized static  EaseMobHelper getInstance() {
		if (instance == null) {
			instance = new EaseMobHelper();
		}
		return instance;
	}

	/**
	 * init helper
	 * 
	 * @param context
	 *            application context
	 */
	public void init(Context context) {
		appContext = context;
		EMClient.getInstance().setDebugMode(false);
		easeUI = EaseUI.getInstance();
		setEaseUIProviders();
		demoModel = new EaseMobModel(context);
		EaseMobPreference.init(context);
		easeUI.setSettingsProvider(new SamSettingsProvider());
		
		setGlobalListeners();
		broadcastManager = LocalBroadcastManager.getInstance(appContext);
		
		initDbDao();
	}

	 protected void setEaseUIProviders() {
        easeUI.setUserProfileProvider(new EaseUserProfileProvider() {
            
            @Override
            public EaseUser getUser(String username) {
                return getUserInfo(username);
            }
        });
	}


    
    protected void setGlobalListeners(){
	syncGroupsListeners = new ArrayList<DataSyncListener>();
	syncContactsListeners = new ArrayList<DataSyncListener>();
	syncBlackListListeners = new ArrayList<DataSyncListener>();
        
	isGroupsSyncedWithServer = demoModel.isGroupSynced();
	isContactsSyncedWithServer = demoModel.isContactSynced();
	isBlackListSyncedWithServer = demoModel.isBacklistSynced();
        
        // create the global connection listener
        connectionListener = new EMConnectionListener() {
            @Override
            public void onDisconnected(int error) {
                if (error == EMError.USER_REMOVED) {
                    //onCurrentAccountRemoved();
                }else if (error == EMError.USER_LOGIN_ANOTHER_DEVICE) {
                    //onConnectionConflict();
                }
            }

            @Override
            public void onConnected() {
			SamLog.e(TAG,"Easemob connected...");
                
                // in case group and contact were already synced, we supposed to notify sdk we are ready to receive the events
                if(isContactsSyncedWithServer && isGroupsSyncedWithServer){
                    new Thread(){
                        @Override
                        public void run(){
                            EaseMobHelper.getInstance().notifyForRecevingEvents();
                        }
                    }.start();
                }else{

		      if(!isGroupsSyncedWithServer){
                        asyncFetchGroupsFromServer(null);
                    }
                    
                    if(!isContactsSyncedWithServer){
                        asyncFetchContactsFromServer(null);
                    }
                    
                    if(!isBlackListSyncedWithServer){
                        asyncFetchBlackListFromServer(null);
                    }
                }
            }
        };
        
        EMClient.getInstance().addConnectionListener(connectionListener);       

        registerContactListener();

        registerEventListener();
        
    }


	protected void registerEventListener() {
		messageListener = new EMMessageListener() {
			@Override
			public void onMessageReceived(List<EMMessage> messages) {
				boolean isMsgFromService=false;
				boolean isMsgFromChat=false;
				boolean isMsgFromVendor=false;
				boolean isGroupMsg=false;

				Map<String, EMConversation> conversations = EMClient.getInstance().chatManager().getAllConversations();
				
				for (EMMessage message : messages) {
					if(message.getChatType()==EMMessage.ChatType.Chat){
						String chat_type = message.getStringAttribute (EaseConstant.CHAT_ACTIVITY_TYPE, EaseConstant.CHAT_ACTIVITY_TYPE_VIEW_CHAT);
				
						if(chat_type.equals(EaseConstant.CHAT_ACTIVITY_TYPE_VIEW_SERVICE)){
							isMsgFromService = true;
							String from = message.getFrom();
							EMConversation conv = EMClient.getInstance().chatManager().getConversation(from,EMConversationType.Chat);
							synchronized(conv){
								String attr = conv.getExtField();
		  						if(attr!=null && attr.contains(EaseConstant.CONVERSATION_ATTR_VIEW_VENDOR)){
									continue;
		  						}else if(attr == null){
		  							conv.setExtField(EaseConstant.CONVERSATION_ATTR_VIEW_VENDOR);
								}else{
									conv.setExtField(attr+EaseConstant.CONVERSATION_ATTR_VIEW_VENDOR);
								}
							}
						}else if(chat_type.equals(EaseConstant.CHAT_ACTIVITY_TYPE_VIEW_CHAT)){
							isMsgFromChat = true;
							String from = message.getFrom();
							EMConversation conv = EMClient.getInstance().chatManager().getConversation(from,EMConversationType.Chat);
							synchronized(conv){
								String attr = conv.getExtField();
		  						if(attr!=null && attr.contains(EaseConstant.CONVERSATION_ATTR_VIEW_CHAT)){
									continue;
		  						}else if(attr == null){
		  							conv.setExtField(EaseConstant.CONVERSATION_ATTR_VIEW_CHAT);
								}else{
									conv.setExtField(attr+EaseConstant.CONVERSATION_ATTR_VIEW_CHAT);
								}
							}
						}else if((chat_type.equals(EaseConstant.CHAT_ACTIVITY_TYPE_VIEW_VENDOR))){
							isMsgFromVendor = true;
							String from = message.getFrom();
							EMConversation conv = EMClient.getInstance().chatManager().getConversation(from,EMConversationType.Chat);
							String link_questions = message.getStringAttribute (EaseConstant.CHAT_QUESTIONS_BACK, null);
							String[] quest_ids=null; 
							if(link_questions!=null){
								quest_ids = link_questions.split(" ");
							}
							if(quest_ids!=null){
								for(int i=0; i< quest_ids.length;i++){
									SendQuestion sq = SamService.getInstance().getDao().query_send_question_db(quest_ids[i]);
									if(sq!=null){
										EMMessage qMsg = EMMessage.createSendMessage(EMMessage.Type.TXT);
										qMsg.setMsgTime(sq.sendtime);
										qMsg.setFrom(SamService.getInstance().get_current_user().geteasemob_username());
										qMsg.setTo(from);
										qMsg.addBody(new EMTextMessageBody(sq.question));
										qMsg.setAttribute(Constants.CHAT_ACTIVITY_TYPE,Constants.CHAT_ACTIVITY_TYPE_VIEW_SERVICE);
										//qMsg.setDelivered(true);
										qMsg.setStatus(EMMessage.Status.SUCCESS);
										synchronized(conv){
											conv.insertMessage(qMsg);
										}
									}
								}
							}
							

							
							synchronized(conv){
								String attr = conv.getExtField();
		  						if(attr!=null && attr.contains(EaseConstant.CONVERSATION_ATTR_VIEW_SERVICE)){
									continue;
		  						}else if(attr == null){
		  							conv.setExtField(EaseConstant.CONVERSATION_ATTR_VIEW_SERVICE);
								}else{
									conv.setExtField(attr+EaseConstant.CONVERSATION_ATTR_VIEW_SERVICE);
								}
							}
						}
					}else{
						isGroupMsg = true;
					}
				}

				if(isMsgFromService){
					sendNewMsgFromServiceBroadcast();
				}

				if(isMsgFromChat){
					sendNewMsgFromChatBroadcast();
				}

				if(isMsgFromVendor){
					sendNewMsgFromVendorBroadcast();
				}

				if (isGroupMsg){
					/*receive group msg*/
					sendNewMsgFromGroup();
				}

				
			}
			
			@Override
			public void onCmdMessageReceived(List<EMMessage> messages) {
			}

			@Override
			public void onMessageReadAckReceived(List<EMMessage> messages) {
			}
			
			@Override
			public void onMessageDeliveryAckReceived(List<EMMessage> message) {
			}
			
			@Override
			public void onMessageChanged(EMMessage message, Object change) {
				
			}
		};
		
		EMClient.getInstance().chatManager().addMessageListener(messageListener);
	}

    
    private void initDbDao() {
        inviteMessgeDao = new InviteMessgeDao(appContext);
        userfDao = new UserFriendDao(appContext);
    }
    
    public void registerContactListener(){
       // if(!isContactGroupListenerRegisted){
	     SamLog.e(TAG,"registerContactListener");
	     EMClient.getInstance().contactManager().setContactListener(new MyContactListener());
            isContactGroupListenerRegisted = true;
       // }
        
    }

 	public void updateInviteMsgStatus(String username,int status){
		String receiver = SamService.getInstance().get_current_user().geteasemob_username();
		List<InviteMessageRecord> msgs = inviteMessgeDao.getMessagesList(receiver,username);
		for(InviteMessageRecord msg: msgs){
			ContentValues values = new ContentValues();
			values.put("status",status);//InviteMessageStatus.AGREED.ordinal());
			inviteMessgeDao.updateMessage(msg.getid(),values);
		}
	}

	public void sendContactChangeBroadcast(){
		broadcastManager.sendBroadcast(new Intent(Constants.ACTION_CONTACT_CHANAGED));
	}


	public void sendFollowerChangeBroadcast(){
		broadcastManager.sendBroadcast(new Intent(Constants.ACTION_FOLLOWER_CHANAGED));
	}

	public void sendAvatarUpdateBroadcast(){
		broadcastManager.sendBroadcast(new Intent(Constants.ACTION_AVATAR_UPDATE));
	}

	public void sendQAActivityDestroyedBroadcast(){
		broadcastManager.sendBroadcast(new Intent(Constants.ACTION_QAACTIVITY_DESTROYED));
	}

	public void sendNewMsgFromServiceBroadcast(){
		broadcastManager.sendBroadcast(new Intent(Constants.ACTION_NEW_MSG_FROM_SERVICE));
	}

	public void sendNewMsgFromChatBroadcast(){
		broadcastManager.sendBroadcast(new Intent(Constants.ACTION_NEW_MSG_FROM_CHAT));
	}

	public void sendNewMsgFromVendorBroadcast(){
		broadcastManager.sendBroadcast(new Intent(Constants.ACTION_NEW_MSG_FROM_VENDOR));
	}

	public void sendNewMsgFromGroup(){
		broadcastManager.sendBroadcast(new Intent(Constants.ACTION_NEW_MSG_FROM_GROUP));
	}

	public void sendNewQuestReceived(String from){
		broadcastManager.sendBroadcast(new Intent(EaseConstant.ACTION_NEW_QUEST_FOR_VENDOR_CHAT_FRAG).putExtra(EaseConstant.QUESTION_OWNER, from));
	}


	public class SamSettingsProvider implements EaseSettingsProvider{

        @Override
        public boolean isMsgNotifyAllowed(EMMessage message) {
            // TODO Auto-generated method stub
            return demoModel.isMsgNotificationEnable();
        }

        @Override
        public boolean isMsgSoundAllowed(EMMessage message) {
            return demoModel.isMsgNotificationSoundEnable();
        }

        @Override
        public boolean isMsgVibrateAllowed(EMMessage message) {
            return demoModel.isMsgNotificationVibrateEnable();
        }

        @Override
        public boolean isSpeakerOpened() {
            return true;
        }

        
    }
	
    
     public class MyContactListener implements EMContactListener {

        @Override
        public void onContactAdded(String username) {  
           SamLog.e(TAG,"onContactAdded");
            Map<String, EaseUser> localUsers = getContactList();
            Map<String, EaseUser> toAddUsers = new HashMap<String, EaseUser>();
            EaseUser user = new EaseUser(username);
            if (!localUsers.containsKey(username)) {
                userfDao.saveContact(user);
            }

		    updateInviteMsgStatus(username,InviteMessageStatus.AGREED.ordinal());
            toAddUsers.put(username, user);
            
            localUsers.putAll(toAddUsers);
            broadcastManager.sendBroadcast(new Intent(Constants.ACTION_CONTACT_CHANAGED));
        }

        @Override
        public void onContactDeleted(String username) {
            SamLog.e(TAG,"onContactDeleted");
            Map<String, EaseUser> localUsers = EaseMobHelper.getInstance().getContactList();
            localUsers.remove(username);
            userfDao.deleteContact(username);
            inviteMessgeDao.deleteMessage(username);

            broadcastManager.sendBroadcast(new Intent(Constants.ACTION_CONTACT_CHANAGED));
        }

        @Override
        public void onContactInvited(String username, String reason) {
        	SamLog.e(TAG,"onContactInvited");
		List<InviteMessageRecord> msgs = inviteMessgeDao.getMessagesList();

		for (InviteMessageRecord inviteMessage : msgs) {
			if (inviteMessage.getsender().equals(username)){
				if(inviteMessage.getstatus()==InviteMessageStatus.BEINVITEED.ordinal()){
					if(reason.equals(inviteMessage.getreason())) {
						return;
					}else{
						inviteMessgeDao.deleteMessage(username);
					}
				}else if(inviteMessage.getstatus()==InviteMessageStatus.AGREED.ordinal()){
					inviteMessgeDao.deleteMessage(username);
				}else if(inviteMessage.getstatus()==InviteMessageStatus.REFUSED.ordinal()){
					inviteMessgeDao.deleteMessage(username);
				}
			}
		}
		
		final String uname = username;
		final String why = reason;
		
		//download inviter infor
		SamService.getInstance().query_user_info_from_server(username,new SMCallBack(){
			@Override
			public void onSuccess(Object obj) {
				InviteMessageRecord msg = new InviteMessageRecord();
				msg.setreceiver(SamService.getInstance().get_current_user().geteasemob_username());
				msg.setsender(uname);
				msg.settime(System.currentTimeMillis());
				msg.setreason(why);
				msg.setstatus(InviteMessageStatus.BEINVITEED.ordinal());
				notifyNewIviteMessage(msg);
				
				Intent newIntent = new Intent(Constants.ACTION_CONTACT_CHANAGED);
				newIntent.putExtra("isInvite", true);
				broadcastManager.sendBroadcast(newIntent);		
			}

			@Override
			public void onFailed(int code) {
				//Intent newIntent = new Intent(Constants.ACTION_CONTACT_CHANAGED);
				//newIntent.putExtra("isInvite", true);
				//broadcastManager.sendBroadcast(newIntent);		
			}

			@Override
			public void onError(int code) {
				//Intent newIntent = new Intent(Constants.ACTION_CONTACT_CHANAGED);
				//newIntent.putExtra("isInvite", true);
				//broadcastManager.sendBroadcast(newIntent);		
			}

		});
		
		
	}

        @Override
        public void onContactAgreed(String username) {
        	SamLog.e(TAG,"onContactAgreed");
            /*List<InviteMesageRecord> msgs = inviteMessgeDao.getMessagesList();
            for (InviteMesageRecord inviteMessage : msgs) {
                if (inviteMessage.getSender().equals(username)) {
                    //return;
                    //this guy send invite to me before and we should delete it if we have been firend
                }
            }

            InviteMessage msg = new InviteMessage();
            msg.setSender(username);
            msg.setTime(System.currentTimeMillis());
            msg.setStatus(InviteMesageStatus.BEAGREED);
            notifyNewIviteMessage(msg);
            broadcastManager.sendBroadcast(new Intent(Constant.ACTION_CONTACT_CHANAGED));*/
        }

        @Override
        public void onContactRefused(String username) {
           SamLog.e(TAG,"onContactRefused");
        }

    }

    private void notifyNewIviteMessage(InviteMessageRecord msg){
        if(inviteMessgeDao == null){
            inviteMessgeDao = new InviteMessgeDao(appContext);
        }
        inviteMessgeDao.add_update_Message(msg);

        //getNotifier().viberateAndPlayTone(null);
    }

    public InviteMessgeDao getInviteMsgDao(){
		return inviteMessgeDao;
    }
    
    /**
     * 璐﹀彿鍦ㄥ埆鐨勮澶囩櫥褰�
     */
    protected void onConnectionConflict(){
        Intent intent = new Intent(appContext, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(Constants.ACCOUNT_CONFLICT, true);
        appContext.startActivity(intent);
    }
    
    /**
     * 璐﹀彿琚Щ闄�
     */
    protected void onCurrentAccountRemoved(){
        Intent intent = new Intent(appContext, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(Constants.ACCOUNT_REMOVED, true);
        appContext.startActivity(intent);
    }
	
	private EaseUser getUserInfo(String username){
		EaseUser user = null;
		if(username.equals(SamService.getInstance().get_current_user().geteasemob_username()))
		{
			LoginUser currentUser = SamService.getInstance().get_current_user();
			user = new EaseUser(currentUser.geteasemob_username());
			user.setNick(currentUser.getusername());
			AvatarRecord rd = SamService.getInstance().getDao().query_AvatarRecord_db_by_username(currentUser.getusername());
			if(rd!=null && rd.getavatarname()!=null){
				user.setAvatar(SamService.sam_cache_path+SamService.AVATAR_FOLDER+"/"+rd.getavatarname());
			}
			return user;
		}

		user = getContactList().get(username);
		if(user!=null){
			return user;
		}else{
			//this user is not in my friend list
			ContactUser cuser = null;
			if(!Constants.USERNAME_EQUAL_EASEMOB_ID){
			 	cuser = SamService.getInstance().getDao().query_ContactUser_db(username);
			}else{
				cuser = SamService.getInstance().getDao().query_ContactUser_db_by_username(username);
			}

			if(cuser == null){
				return null;
			}else{
				user = new EaseUser(cuser.geteasemob_username());
				user.setNick(cuser.getusername());
				AvatarRecord rd = SamService.getInstance().getDao().query_AvatarRecord_db_by_username(cuser.getusername());
				if(rd!=null && rd.getavatarname()!=null){
					user.setAvatar(SamService.sam_cache_path+SamService.AVATAR_FOLDER+"/"+rd.getavatarname());
				}
				return user;
			}
		}
	}
	
    

	public boolean isLoggedIn() {
		return EMClient.getInstance().isLoggedInBefore();
	}

	
	public void logout(boolean unbindDeviceToken, final EMCallBack callback) {
		EMClient.getInstance().logout(unbindDeviceToken, new EMCallBack() {
			
			@Override
			public void onSuccess() {
			    	reset();
				if (callback != null) {
					callback.onSuccess();
				}

			}

			@Override
			public void onProgress(int progress, String status) {
				if (callback != null) {
					callback.onProgress(progress, status);
				}
			}

			@Override
			public void onError(int code, String error) {
				reset();
				if (callback != null) {
					callback.onError(code, error);
				}
			}
		});
	}
	

	public void setContactList(Map<String, EaseUser> contactList) {
		this.contactList = contactList;
	}
	
	synchronized public void removeContact(EaseUser user){
		contactList.remove(user.getUsername());
    		demoModel.deleteContact(user);
	}

    synchronized public void saveContact(EaseUser user){
    	contactList.put(user.getUsername(), user);
    	demoModel.saveContact(user);
    }
    

    synchronized public Map<String, EaseUser> getContactList() {
        if (isLoggedIn()) {
            contactList = demoModel.getContactList();
        }
        
       // SamLog.e(TAG,"contactList:" + contactList.size());
        
        return contactList;
    }
    


    

	    public void addSyncContactListener(DataSyncListener listener) {
	        if (listener == null) {
	            return;
	        }
	        if (!syncContactsListeners.contains(listener)) {
	            syncContactsListeners.add(listener);
	        }
	    }

	    public void removeSyncContactListener(DataSyncListener listener) {
	        if (listener == null) {
	            return;
	        }
	        if (syncContactsListeners.contains(listener)) {
	            syncContactsListeners.remove(listener);
	        }
	    }

	    public void addSyncBlackListListener(DataSyncListener listener) {
	        if (listener == null) {
	            return;
	        }
	        if (!syncBlackListListeners.contains(listener)) {
	            syncBlackListListeners.add(listener);
	        }
	    }

	    public void removeSyncBlackListListener(DataSyncListener listener) {
	        if (listener == null) {
	            return;
	        }
	        if (syncBlackListListeners.contains(listener)) {
	            syncBlackListListeners.remove(listener);
	        }
	    }
	

	private void SetUserInfo(EaseUser user,String cellphone){
		AvatarRecord rd = SamService.getInstance().getDao().query_AvatarRecord_db(cellphone);
		if(rd!=null){
			
		}
	}


	private void query_user_info_from_server(List<String> usernames){
		SamService.getInstance().query_user_info_from_server(usernames,new SMCallBack(){
			@Override
			public void onSuccess(Object obj) {
				synchronized (lock){
					try {
						lock.notify();
					} catch (Exception e) {
						e.printStackTrace();
					}
    				}				
			}

			@Override
			public void onFailed(int code) {
				SamLog.i(TAG,"query user info failed ...");
				synchronized (lock){
					try {
						lock.notify();
					} catch (Exception e) {
						e.printStackTrace();
					}
    				}
			}

			@Override
			public void onError(int code) {
				SamLog.i(TAG,"query user info errorr ...");
				synchronized (lock){
					try {
						lock.notify();
					} catch (Exception e) {
						e.printStackTrace();
					}
    				}
				
			}

		});
	}

	private void syncFetchContactInfoFromServer(List<String> usernames){
		if(usernames == null || usernames.size()==0)
			return;

		SamLog.e(TAG,"syncFetchContactInfoFromServer");
		
		query_user_info_from_server(usernames);

		synchronized (lock){
			try {
				lock.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
    		}
	}	

   public void asyncFetchContactsFromServer(final EMValueCallBack<List<String>> callback){
       if(isSyncingContactsWithServer){
           return;
       }
       
       isSyncingContactsWithServer = true;
       
       new Thread(){
           @Override
           public void run(){
               List<String> usernames = null;
               try {
            	   usernames = EMClient.getInstance().contactManager().getAllContactsFromServer();
                   // in case that logout already before server returns, we should return immediately
                   if(!isLoggedIn()){
                       isContactsSyncedWithServer = false;
                       isSyncingContactsWithServer = false;
                       notifyContactsSyncListener(false);
                       return;
                   }

		      SamLog.e(TAG,"before syncFetchContactInfoFromServer");
		      syncFetchContactInfoFromServer(usernames);
			Map<String, EaseUser> userlist = new HashMap<String, EaseUser>();
			for (String username : usernames) {
				EaseUser user = new EaseUser(username);
				ContactUser cuser = null;
				if(Constants.USERNAME_EQUAL_EASEMOB_ID)
					cuser = SamService.getInstance().getDao().query_ContactUser_db_by_username(username);
				else
					cuser = SamService.getInstance().getDao().query_ContactUser_db(username); 

				if(cuser!=null && cuser.getusername()!=null){
					user.setNick(cuser.getusername());
				}

				if(cuser!=null && cuser.getusername()!=null){
					AvatarRecord rd = SamService.getInstance().getDao().query_AvatarRecord_db_by_username(cuser.getusername());
					if(rd!=null && rd.getavatarname()!=null){
						user.setAvatar(SamService.sam_cache_path+SamService.AVATAR_FOLDER+"/"+rd.getavatarname());
					}
				}

				EaseCommonUtils.setUserInitialLetter(user);
				userlist.put(username, user);
                   }
                  
                    //store into db
                    SamLog.e(TAG,"save contact here!!!!!!!!!!!!!");
                   UserFriendDao dao = new UserFriendDao(appContext);
                   List<EaseUser> users = new ArrayList<EaseUser>(userlist.values());
                   dao.saveContactList(users);

                   demoModel.setContactSynced(true);
                   SamLog.e(TAG, "set contact syn status to true");
                   
                   isContactsSyncedWithServer = true;
                   isSyncingContactsWithServer = false;

                   notifyContactsSyncListener(true);

		     if(isGroupsSyncedWithServer){
                   	  notifyForRecevingEvents();
		     }
                  
                   if(callback != null){
                       callback.onSuccess(usernames);
                   }
               } catch (HyphenateException e) {
                   demoModel.setContactSynced(false);
                   isContactsSyncedWithServer = false;
                   isSyncingContactsWithServer = false;

                   e.printStackTrace();
                   if(callback != null){
                       callback.onError(e.getErrorCode(), e.toString());
                   }
               }
               
           }
       }.start();
   }

   public void notifyContactsSyncListener(boolean success){
   	SamLog.e(TAG,"syncContactsListeners:"+syncContactsListeners.size());
       for (DataSyncListener listener : syncContactsListeners) {
           listener.onSyncComplete(success);
       }
   }
   



    public synchronized void asyncFetchGroupsFromServer(final EMCallBack callback){
       if(isSyncingGroupsWithServer){
           return;
       }
       
       isSyncingGroupsWithServer = true;
       
       new Thread(){
           @Override
           public void run(){
               try {
            	   EMClient.getInstance().groupManager().getJoinedGroupsFromServer();
                   
                   // in case that logout already before server returns, we should return immediately
                   if((!isLoggedIn())){
                       isGroupsSyncedWithServer = false;
                       isSyncingGroupsWithServer = false;
                       noitifyGroupSyncListeners(false);
                       return;
                   }
                   
                   demoModel.setGroupSynced(true);
                   
                   isGroupsSyncedWithServer = true;
                   isSyncingGroupsWithServer = false;
                   
                   noitifyGroupSyncListeners(true);
                   if(isContactsSyncedWithServer()){
                       notifyForRecevingEvents();
                   }
                   if(callback != null){
                       callback.onSuccess();
                   }
               } catch (HyphenateException e) {
                   demoModel.setGroupSynced(false);
                   isGroupsSyncedWithServer = false;
                   isSyncingGroupsWithServer = false;
                   noitifyGroupSyncListeners(false);
                   if(callback != null){
                       callback.onError(e.getErrorCode(), e.toString());
                   }
               }
           
           }
       }.start();
   }

   public void noitifyGroupSyncListeners(boolean success){
       for (DataSyncListener listener : syncGroupsListeners) {
           listener.onSyncComplete(success);
       }
   }
   
   public void asyncFetchBlackListFromServer(final EMValueCallBack<List<String>> callback){
       
       if(isSyncingBlackListWithServer){
           return;
       }
       
       isSyncingBlackListWithServer = true;
       
       new Thread(){
           @Override
           public void run(){
               try {
            	   List<String> usernames = EMClient.getInstance().contactManager().getBlackListFromServer();
                   
                   // in case that logout already before server returns, we should return immediately
                   if((!isLoggedIn())){
                	  isBlackListSyncedWithServer = false;
                       isSyncingBlackListWithServer = false;
                       notifyBlackListSyncListener(false);
		         return;
                   }
                   
                   demoModel.setBlacklistSynced(true);
                   
                   isBlackListSyncedWithServer = true;
                   isSyncingBlackListWithServer = false;
                   
                   EMClient.getInstance().contactManager().saveBlackList(usernames);
                   notifyBlackListSyncListener(true);
                   if(callback != null){
                       callback.onSuccess(usernames);
                   }
               } catch (HyphenateException e) {
                   demoModel.setBlacklistSynced(false);
                   
                   isBlackListSyncedWithServer = false;
                   isSyncingBlackListWithServer = true;
                   e.printStackTrace();
                   
                   if(callback != null){
                       callback.onError(e.getErrorCode(), e.toString());
                   }
               }
               
           }
       }.start();
   }
	
	public void notifyBlackListSyncListener(boolean success){
        for (DataSyncListener listener : syncBlackListListeners) {
            listener.onSyncComplete(success);
        }
    }
	
    public boolean isSyncingGroupsWithServer() {
        return isSyncingGroupsWithServer;
    }

    public boolean isSyncingContactsWithServer() {
        return isSyncingContactsWithServer;
    }

    public boolean isSyncingBlackListWithServer() {
        return isSyncingBlackListWithServer;
    }
    
    public boolean isGroupsSyncedWithServer() {
        return isGroupsSyncedWithServer;
    }

    public boolean isContactsSyncedWithServer() {
        return isContactsSyncedWithServer;
    }

    public boolean isBlackListSyncedWithServer() {
        return isBlackListSyncedWithServer;
    }
	
    public synchronized void notifyForRecevingEvents(){
        if(alreadyNotified){
            return;
        }

        SamLog.e(TAG,"notify swich on from now on ...");
        //EMClient.getInstance().setAppInited();
        alreadyNotified = true;
    }
	
    public synchronized void reset(){
        isSyncingGroupsWithServer = false;
        isSyncingContactsWithServer = false;
        isSyncingBlackListWithServer = false;

        demoModel.setGroupSynced(false);
        demoModel.setContactSynced(false);
        demoModel.setBlacklistSynced(false);
		 
        
        isGroupsSyncedWithServer = false;
        isContactsSyncedWithServer = false;
        isBlackListSyncedWithServer = false;
        
        alreadyNotified = false;
        isContactGroupListenerRegisted = false;
        
        setContactList(null);

	 EMClient.getInstance().chatManager().removeMessageListener(messageListener);
        
    }

    
}
