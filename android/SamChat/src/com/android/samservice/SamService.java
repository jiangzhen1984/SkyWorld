package com.android.samservice;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpStatus;

import com.android.samchat.SamChats_Fragment;
import com.android.samchat.SamMe_Fragment;
import com.android.samchat.SamContact_Fragment;
import com.android.samchat.SamQADetailActivity;
import com.android.samchat.SamService_Fragment;
import com.android.samservice.info.*;
import com.android.samservice.provider.*;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.HandlerThread;
import android.util.Log;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;



public class SamService{
	public static boolean DEBUG=true;
	public static final String TAG="SamService";
	
    public static final int MIN_MPHONE_NUMBER_LENGTH = 6;
    public static final int MIN_USERNAME_LENGTH = 3;
    public static final int MAX_USERNAME_LENGTH = 15;
    public static final int MIN_PASSWORD_LENGTH = 6;
    public static final int MAX_PASSWORD_LENGTH = 32;
    public static final int SAMSERVICE_HANDLE_TIMEOUT=10000;
	
    public static final String FINISH_ALL_SIGN_ACTVITY = "com.android.sam.finishAllSign";
    public static final String EASEMOBNAMEGOT = "com.android.sam.easemobnamegot";
    
    public static String sam_cache_path;
    public static String sam_download_path;
    public static final String TOKEN_FILE="token";
    public static final String UP_FILE="up";

	

    /*handle message id*/
    public static final int MSG_AUTOLOGIN_CALLBACK = 0;
    public static final int MSG_HANDLE_TIMEOUT = 1;
    public static final int MSG_SEND_QUESTION=2;
    	//send question result
    	public static final int R_SEND_QUESTION_OK=0;
    	public static final int R_SEND_QUESTION_ERROR=1;
    	public static final int R_SEND_QUESTION_FAILED=2;
	//send question error type
	public static final int R_SEND_QUESTION_ERROR_TOKEN_FILE_EXCEPTION = 0;
	public static final int R_SEND_QUESTION_ERROR_TOKEN_FILE_NULL = 1;
	public static final int R_SEND_QUESTION_ERROR_HTTP_EXCEPTION = 2;
	public static final int R_SEND_QUESTION_ERROR_TIMEOUT = 3;
	//HTTP RET value from server
	public static final int RET_SEND_QUESTION_FROM_SERVER_OK = 0;
	public static final int RET_SEND_QUESTION_FROM_SERVER_HTTP_FAILED = -1;//parse http failed
	public static final int RET_SEND_QUESTION_FROM_SERVER_ACTION_NOT_SUPPORT = -2;
	public static final int RET_SEND_QUESTION_FROM_SERVER_PARAM_NOT_SUPPORT = -3;
	public static final int RET_SEND_QUESTION_FROM_SERVER_TOKEN_FORMAT_ERROR = -4;
	public static final int RET_SEND_QUESTION_FROM_SERVER_TOKEN_INVALID = -5;
	public static final int RET_SEND_QUESTION_FROM_SERVER_OPT_NOT_SUPPORT = -301;
	public static final int RET_SEND_QUESTION_FROM_SERVER_INTERNAL_ERROR = -302;

    public static final int MSG_CANCEL_QUESTION=3;
	//cancel question result
    	public static final int R_CANCEL_QUESTION_OK=0;
    	public static final int R_CANCEL_QUESTION_ERROR=1;
    	public static final int R_CANCEL_QUESTION_FAILED=2;

	public static final int R_CANCEL_QUESTION_ERROR_TOKEN_FILE_EXCEPTION = 0;
	public static final int R_CANCEL_QUESTION_ERROR_TOKEN_FILE_NULL = 1;
	public static final int R_CANCEL_QUESTION_ERROR_HTTP_EXCEPTION = 2;
	public static final int R_CANCEL_QUESTION_ERROR_TIMEOUT = 3;

	//HTTP RET value from server
	public static final int RET_CANCEL_QUESTION_FROM_SERVER_OK = 0;
	public static final int RET_CANCEL_QUESTION_FROM_SERVER_HTTP_FAILED = -1;//parse http failed
	public static final int RET_CANCEL_QUESTION_FROM_SERVER_ACTION_NOT_SUPPORT = -2;
	public static final int RET_CANCEL_QUESTION_FROM_SERVER_PARAM_NOT_SUPPORT = -3;
	public static final int RET_CANCEL_QUESTION_FROM_SERVER_TOKEN_FORMAT_ERROR = -4;
	public static final int RET_CANCEL_QUESTION_FROM_SERVER_TOKEN_INVALID = -5;
	public static final int RET_CANCEL_QUESTION_FROM_SERVER_OPT_NOT_SUPPORT = -301;
	public static final int RET_CANCEL_QUESTION_FROM_SERVER_INTERNAL_ERROR = -302;
	public static final int RET_CANCEL_QUESTION_FROM_SERVER_NO_THIS_QUESTION = -302;

   public static final int MSG_UPGRADE_TO_SERVICER=4;

	public static final int R_UPGRADE_OK=0;
    	public static final int R_UPGRADE_ERROR=1;
    	public static final int R_UPGRADE_FAILED=2;

	public static final int R_UPGRADE_ERROR_TOKEN_FILE_EXCEPTION = 0;
	public static final int R_UPGRADE_ERROR_TOKEN_FILE_NULL = 1;
	public static final int R_UPGRADE_ERROR_HTTP_EXCEPTION = 2;
	public static final int R_UPGRADE_ERROR_TIMEOUT = 3;
		
   	public static final int RET_UPGRADE_FROM_SERVER_OK = 0;
	public static final int RET_UPGRADE_FROM_SERVER_HTTP_FAILED = -1;//parse http failed
	public static final int RET_UPGRADE_FROM_SERVER_ACTION_NOT_SUPPORT = -2;
	public static final int RET_UPGRADE_FROM_SERVER_PARAM_NOT_SUPPORT = -3;
	public static final int RET_UPGRADE_FROM_SERVER_TOKEN_FORMAT_ERROR = -4;
	public static final int RET_UPGRADE_FROM_SERVER_TOKEN_INVALID = -5;
	public static final int RET_UPGRADE_FROM_SERVER_INTERNAL_ERROR = -501;
	public static final int RET_UPGRADE_FROM_SERVER_ILLEAGLE_ERROR = -502;
	public static final int RET_UPGRADE_FROM_SERVER_UPDATED_ALREADY = -503;

    public static final int MSG_ANSWER_QUESTION=5;
	public static final int R_ANSWER_QUESTION_OK=0;
    	public static final int R_ANSWER_QUESTION_ERROR=1;
    	public static final int R_ANSWER_QUESTION_FAILED=2;

	public static final int R_ANSWER_QUESTION_ERROR_TOKEN_FILE_EXCEPTION = 0;
	public static final int R_ANSWER_QUESTION_ERROR_TOKEN_FILE_NULL = 1;
	public static final int R_ANSWER_QUESTION_ERROR_HTTP_EXCEPTION = 2;
	public static final int R_ANSWER_QUESTION_ERROR_TIMEOUT = 3;

	public static final int RET_ANSWER_FROM_SERVER_OK = 0;
	public static final int RET_ANSWER_FROM_SERVER_HTTP_FAILED = -1;//parse http failed
	public static final int RET_ANSWER_FROM_SERVER_ACTION_NOT_SUPPORT = -2;
	public static final int RET_ANSWER_FROM_SERVER_PARAM_NOT_SUPPORT = -3;
	public static final int RET_ANSWER_FROM_SERVER_TOKEN_FORMAT_ERROR = -4;
	public static final int RET_ANSWER_FROM_SERVER_TOKEN_INVALID = -5;
	public static final int RET_ANSWER_FROM_SERVER_NO_SUCH_QUESTION = -601;
	public static final int RET_ANSWER_FROM_SERVER_NOT_SYSERVICER = -602;

	public static final int MSG_PUSH_MSG_GOTANSWER = 0;
	public static final int MSG_PUSH_MSG_PUSHSERVERSHUTDOWN = 1;	
	public static final int MSG_PUSH_MSG_GOTQUESTION = 2;
	public static final int MSG_PUSH_MSG_EASEMOB_INFO = 3;

	private static SamService mSamService;
	private static Context mContext;
	private static Object pushLock = new Object();

	private HandlerThread mHandlerTimeOutThread;
	private SamServiceTimeOutHandler mHandlerTimeOutHandler;
	
	private HandlerThread mHandlerThread;
	private SamServiceHandler mSamServiceHandler;
	private HandlerThread mPushThread;
	private SamPushServiceHandler mSamPushServiceHandler;
	private WeakReference <Handler>  answer_hndl;
	private WeakReference <Handler>  servicer_question_hndl;
	private WaitThread mWaitThread;
	
	private String current_question_id;
	private String current_token;
	private Object dbLock;
	private DBManager dbHandle;

	

	private LoginUser current_user;

	private ArrayList<ActiveQuestion> activeQuestionArray = new ArrayList<ActiveQuestion>();

	public ArrayList<ActiveQuestion> getActiveQuestionArray(){
		return activeQuestionArray;
	}

	synchronized public void set_current_user(LoginUser user){
		current_user = user;
	}

	public LoginUser get_current_user(){
		return current_user;
	}

	public long add_update_LoginUser_db(LoginUser user){
		synchronized(dbLock){
			LoginUser tuser = dbHandle.queryLogInUser(user.phonenumber);
			if(tuser !=null){
				return dbHandle.updateLogInUser(tuser.id, user);
			}else{
				return dbHandle.addLogInUser(user);
			}	
		}
		
	}

	public long update_LogoutUser_db(LoginUser user){
		synchronized(dbLock){
			user.status = LoginUser.INACTIVE;
			user.logouttime = System.currentTimeMillis();
			return dbHandle.updateLogInUser(user.id, user);
		}
	}

	public LoginUser query_activie_LoginUser_db(){
		synchronized(dbLock){
			return dbHandle.queryLogInUser();
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
		synchronized(dbLock){
			ContactUser tuser = dbHandle.queryContactUser(user.get_phonenumber());
			if(tuser !=null){
				return dbHandle.updateContactUser(tuser.get_id(), user);
			}else{
				return dbHandle.addContactUser(user);
			}	
		}
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

	public ArrayList<ReceivedQuestion> query_RecentReceivedQuestion_db(long num){
		synchronized(dbLock){
			return dbHandle.queryRecentReceivedQuestion(num);
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

	public ArrayList<SendAnswer> query_SendAnswer_db(String question_id){
		synchronized(dbLock){
				return dbHandle.querySendAnswer(question_id);
		}
	}

	public long add_ReceivedAnswer_db(ReceivedAnswer answer){
		synchronized(dbLock){
				return dbHandle.addReceivedAnswer(answer);
		}
	}

	public ArrayList<ReceivedAnswer> query_ReceivedAnswer_db(String question_id){
		synchronized(dbLock){
				return dbHandle.queryReceivedAnswer(question_id);
		}
	}


	

	synchronized public void store_current_token(String token){
			current_token = token;
	}

	synchronized public String get_current_token(){
		return current_token;
	}
	
    static public boolean isNumeric(String str){    
        Pattern pattern = Pattern.compile("[0-9]*");    
        Matcher isNum = pattern.matcher(str);
  
    	if( !isNum.matches() ){   
    		return false;    
    	}else{
    		return true;  
    	}
     }
    
    public void initSamService(){
	current_token = null;
	current_question_id = null;
		
    	sam_cache_path = mContext.getExternalCacheDir().getAbsolutePath();
    	
    	sam_download_path = mContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
    	
    	SamLog.e(TAG,"sam_cache_path:"+sam_cache_path);
    	SamLog.e(TAG,"sam_download_path:"+sam_download_path);

    	File file1 = new File(sam_cache_path);

    	if(!file1.exists()){
    		file1.mkdirs();
    	}

    	File file2 = new File(sam_download_path);

    	if(!file2.exists()){
    		file2.mkdirs();
    	}

	
	 
    }

	public static synchronized SamService getInstance(){
		if(mSamService == null){
			mSamService	= new SamService();
		}
		return mSamService;
	}

	public static synchronized SamService getInstance(Activity activity){
		mContext = activity.getApplicationContext();
		if(mSamService == null){
			mSamService	 = new SamService();
		}
		return mSamService;
	}
	
    private SamService(){
	dbHandle = new DBManager(mContext);
	dbLock = new Object();
	
    	InitHandlerThread();
    }
    
    private void InitHandlerThread(){
    	mHandlerThread = new HandlerThread("SamService");
    	mHandlerThread.start();
    	mSamServiceHandler = new SamServiceHandler(mHandlerThread.getLooper());
    	
    	mPushThread = new HandlerThread("SamPushService");
    	mPushThread.start();
    	mSamPushServiceHandler = new SamPushServiceHandler(mPushThread.getLooper());

	mHandlerTimeOutThread = new HandlerThread("SamServiceTimeOut");
	mHandlerTimeOutThread.start();
	mHandlerTimeOutHandler = new SamServiceTimeOutHandler(mHandlerTimeOutThread.getLooper());

	mWaitThread = null;
    	
    }
    
	public void stopSamService(){
		
		if(mWaitThread!=null)
			mWaitThread.StopThread();

		/*remove all msg in mSamServiceHandler*/
		mSamServiceHandler.removeMessages(MSG_AUTOLOGIN_CALLBACK);
		mSamServiceHandler.removeMessages(MSG_SEND_QUESTION);
		mSamServiceHandler.removeMessages(MSG_CANCEL_QUESTION);
		mHandlerThread.getLooper().quit();

		mSamPushServiceHandler.removeMessages(MSG_PUSH_MSG_GOTANSWER);
		mSamPushServiceHandler.removeMessages(MSG_PUSH_MSG_PUSHSERVERSHUTDOWN);
		mPushThread.getLooper().quit();

		synchronized(pushLock){
			mHandlerTimeOutHandler.removeMessages(MSG_HANDLE_TIMEOUT);
			mHandlerTimeOutThread.getLooper().quit();
			mSamPushServiceHandler = null;
		}

		mSamService = null;
		answer_hndl = null;
		servicer_question_hndl = null;
		
		if(dbHandle!=null) 
			dbHandle.closeDB();


	}

	public void onActivityLaunched(SamService_Fragment fragment_samservice, SamChats_Fragment fragment_samchat){
		
		answer_hndl = new WeakReference <Handler> (fragment_samservice.mHandler); 
		servicer_question_hndl = new WeakReference <Handler>(fragment_samchat.mHandler);
		
	}

	public void startWaitThread(){
		if(mWaitThread == null){
			mWaitThread = new WaitThread("SamWait");
			mWaitThread.start();
		}
	}


	private void cancelTimeOut(SamCoreObj samobj) {
		mHandlerTimeOutHandler.removeMessages(MSG_HANDLE_TIMEOUT,samobj);
	}

	private void startTimeOut(SamCoreObj samobj) {
		Message msg = mHandlerTimeOutHandler.obtainMessage(MSG_HANDLE_TIMEOUT,samobj);
		mHandlerTimeOutHandler.sendMessageDelayed(msg, SAMSERVICE_HANDLE_TIMEOUT);
	}

	private final class SamServiceTimeOutHandler extends Handler{
		public SamServiceTimeOutHandler(Looper looper)
		{
		   super(looper);
		}

		@Override
		public void handleMessage(Message msg){
			SamCoreObj samobj = (SamCoreObj)msg.obj;
			CBObj cbobj = samobj.refCBObj;
			boolean continue_run = true;
			Handler hndl = null;

			if(cbobj==null){
				SamLog.e(TAG, "SamServiceTimeOutHandler:bad msg,drop it...");
    				return;
			}
			
			if(!cbobj.isBroadcast){
    				if(cbobj.cbHandler==null || (hndl = cbobj.cbHandler.get() )== null){
					SamLog.e(TAG, "SamServiceTimeOutHandler:cbhandler has been destroy, drop cbMsg ...");
    					return;
				}
    			}
			
			switch(msg.what){
				case MSG_HANDLE_TIMEOUT:
					synchronized(samobj){
						if(samobj.request_status == SamCoreObj.STATUS_INIT){
							samobj.request_status = SamCoreObj.STATUS_TIMEOUT;
						}else if(samobj.request_status == SamCoreObj.STATUS_DONE){
							continue_run = false;
						}
					}

					if(continue_run){
						if(samobj.isSendq()){
							Message msg1 = hndl.obtainMessage(cbobj.cbMsg, R_SEND_QUESTION_ERROR, R_SEND_QUESTION_ERROR_TIMEOUT,samobj);
							hndl.sendMessage(msg1);
						}else if(samobj.isCancelq()){
							Message msg2 = hndl.obtainMessage(cbobj.cbMsg, R_CANCEL_QUESTION_ERROR, R_CANCEL_QUESTION_ERROR_TIMEOUT,samobj);
							hndl.sendMessage(msg2);
						}else if(samobj.isUpgrade()){
							Message msg3 = hndl.obtainMessage(cbobj.cbMsg, R_UPGRADE_ERROR, R_UPGRADE_ERROR_TIMEOUT,null);
							hndl.sendMessage(msg3);
						}else if(samobj.isSenda()){
							SendAnswer sda = ((SendaCoreObj)samobj).sda;
							sda.setstatus(SendAnswer.SEND_FAILED);
							update_SendAnswer_db(sda);
							Intent intent = new Intent();
							intent.setAction(SamQADetailActivity.SEND_ANSWER_STATUS_BROADCAST);
							Bundle bundle = new Bundle();
							bundle.putSerializable("SendAnswer",sda);
							intent.putExtras(bundle);
							intent.putExtra("NoSuchQuestion", true);
							SamLog.e(TAG,"Send Answer FAILED:timeout!");
						}
					}

					
					break;
			}
		}
	}
	
    private final class SamServiceHandler extends Handler{
    	public SamServiceHandler(Looper looper)
		{
		   super(looper);
		}
    	
    	@Override
		public void handleMessage(Message msg){
    		Message callbackMessage;
    		CBObj cbobj = ((SamCoreObj)msg.obj).refCBObj;
		Handler hndl = null;
    		
    		if(cbobj==null){
			SamLog.e(TAG, "SamServiceHandler:bad msg,drop it...");
    			return;
		}
			
		if(!cbobj.isBroadcast){
    			if(cbobj.cbHandler==null || (hndl = cbobj.cbHandler.get() )== null){
				SamLog.e(TAG, "SamServiceHandler:cbhandler has been destroy, drop cbMsg ...");
    				return;
			}
    		}
    		
    		switch(msg.what){
    		case MSG_SEND_QUESTION:
    			do_send_question((SamCoreObj)msg.obj);
    			
    			break;
    		case MSG_CANCEL_QUESTION:
			do_cancel_queston((SamCoreObj)msg.obj);
			break;

		case MSG_UPGRADE_TO_SERVICER:
			do_upgrade((SamCoreObj)msg.obj);
			break;

		case MSG_ANSWER_QUESTION:
			do_answer_question((SamCoreObj)msg.obj);
			break;

		case MSG_AUTOLOGIN_CALLBACK:
		{
			if(msg.arg1 == SignService.R_AUTO_SIGN_IN_OK){
				SamCoreObj samobj = (SamCoreObj)msg.obj;
				cancelTimeOut(samobj);
				boolean continue_run = true;
				synchronized(samobj){
					if(samobj.request_status == SamCoreObj.STATUS_INIT){
						samobj.request_status = SamCoreObj.STATUS_DONE;
					}else if(samobj.request_status == SamCoreObj.STATUS_TIMEOUT){
						continue_run = false;
					}
				}

				if(!continue_run) return;
				
				if(samobj.isSendq()){
					send_question(cbobj.qinfo.question,cbobj.cbHandler.get(),cbobj.cbMsg);
				}else if(samobj.isCancelq()){
					cancel_question(cbobj.cbHandler.get(),cbobj.cbMsg);
				}else if(samobj.isUpgrade()){
					upgrade(cbobj.cbHandler.get(),cbobj.cbMsg);
				}else if(samobj.isSenda()){
					reanswer_question(((SendaCoreObj)samobj).sda);
				}
			}else{
				SamCoreObj samobj = (SamCoreObj)msg.obj;
				cancelTimeOut( samobj);
				boolean continue_run = true;
				synchronized(samobj){
					if(samobj.request_status == SamCoreObj.STATUS_INIT){
						samobj.request_status = SamCoreObj.STATUS_DONE;
					}else if(samobj.request_status == SamCoreObj.STATUS_TIMEOUT){
						continue_run = false;
					}
				}

				if(!continue_run) return;
				
				if(samobj.isSendq()){
					Message msg1 = hndl.obtainMessage(cbobj.cbMsg, R_SEND_QUESTION_ERROR, R_SEND_QUESTION_ERROR_TOKEN_FILE_NULL,samobj);
					hndl.sendMessage(msg1);
				}else if(samobj.isCancelq()){
					Message msg2 = hndl.obtainMessage(cbobj.cbMsg, R_CANCEL_QUESTION_ERROR, R_CANCEL_QUESTION_ERROR_TOKEN_FILE_NULL,samobj);
					hndl.sendMessage(msg2);
				}else if(samobj.isUpgrade()){
					Message msg3 = hndl.obtainMessage(cbobj.cbMsg, R_UPGRADE_ERROR, R_UPGRADE_ERROR_TOKEN_FILE_NULL,null);
					hndl.sendMessage(msg3);
				}else if(samobj.isSenda()){
					SendAnswer sda = ((SendaCoreObj)samobj).sda;
					sda.setstatus(SendAnswer.SEND_FAILED);
					update_SendAnswer_db(sda);
					Intent intent = new Intent();
					intent.setAction(SamQADetailActivity.SEND_ANSWER_STATUS_BROADCAST);
					Bundle bundle = new Bundle();
					bundle.putSerializable("SendAnswer",sda);
					intent.putExtras(bundle);
					intent.putExtra("FatalError", false);
					SamLog.e(TAG,"Send Answer FAILED: Auto Sign in!");
				}
				
			}

			break;
		}
			
			
    		}
    		
    	}
    	
    	
    }
    
    
    
    private final class SamPushServiceHandler extends Handler{
		public SamPushServiceHandler(Looper looper)
		{
		   super(looper);
		}

		private boolean needPushToUI(int msg){
			if(msg == MSG_PUSH_MSG_EASEMOB_INFO ){
				return false;
			}else{
				return true;
			}
		}
    	
		@Override
		public void handleMessage(Message msg){
		Handler hndl;
		Handler sq_hndl;

		if(needPushToUI(msg.what) ){
			if(answer_hndl == null || servicer_question_hndl == null){
				SamLog.e(TAG, "answer_hndl has been destroy, drop cbMsg ...");
				return;
			}
		
			hndl = answer_hndl.get();
			if(hndl == null){
				SamLog.e(TAG, "answer_hndl has been destroy, drop cbMsg ...");
				return;
			}

			sq_hndl = servicer_question_hndl.get();
			if(sq_hndl == null){
				SamLog.e(TAG, "servicer_question_hndl has been destroy, drop cbMsg ...");
				return;
			}
		}else{
			hndl = null;
			sq_hndl = null;
		}
		

		switch(msg.what){
			case MSG_PUSH_MSG_GOTANSWER:
				SamLog.i(TAG,"answer is got!");
				HttpPushInfo phinfoa = (HttpPushInfo)msg.obj;
				ContactUser user_answer = new ContactUser();
				user_answer.set_username(phinfoa.username);
				user_answer.set_phonenumber(phinfoa.cellphone);
				user_answer.set_unique_id(phinfoa.unique_id);
				user_answer.set_easemob_username(phinfoa.easemob_username);
				add_update_ContactUser_db(user_answer);
				user_answer = query_ContactUser_db(phinfoa.cellphone);

				ReceivedAnswer ra = new ReceivedAnswer();
				ra.answer = phinfoa.answer;
				ra.contactuserid = user_answer.get_id();
				ra.question_id = phinfoa.quest_id;

				if(isAnswerValid(phinfoa.quest_id)){
					add_ReceivedAnswer_db(ra);
					Message msg1 = hndl.obtainMessage(SamService_Fragment.MSG_ANSWER_BACK, ra);
					hndl.sendMessage(msg1);
				}
				
				break;

			case MSG_PUSH_MSG_PUSHSERVERSHUTDOWN:
				SamLog.e(TAG,"push server is shutdown , question bussiness could not be enable");
				break;

			case MSG_PUSH_MSG_GOTQUESTION:
				SamLog.i(TAG,"question is got!");
				if(current_user.usertype == LoginUser.USER) {
					SamLog.i(TAG,"question is got for normal user, drop it!");
					break;
				}else{
					HttpPushInfo phinfoq = (HttpPushInfo)msg.obj;
					
					ContactUser user = new ContactUser();
					user.set_username(phinfoq.username);
					user.set_phonenumber(phinfoq.cellphone);
					user.set_unique_id(phinfoq.unique_id);
					user.set_easemob_username(phinfoq.easemob_username);
					add_update_ContactUser_db(user);
					user = query_ContactUser_db(phinfoq.cellphone);

					ReceivedQuestion rq = new ReceivedQuestion();
					rq.setquestion_id(phinfoq.quest_id);
					rq.setquestion(phinfoq.quest);
					rq.setcontactuserid(user.get_id());
					rq.setstatus(ReceivedQuestion.ACTIVE);
					rq.setshown(ReceivedQuestion.NOT_SHOWN);
					rq.setreceivedtime(phinfoq.datetime);	
					SamLog.e(TAG,"Store in time:"+phinfoq.datetime);
					add_update_ReceivedQuestion_db(rq);

					Message msg1 = sq_hndl.obtainMessage(SamChats_Fragment.MSG_QUESTION_RECEIVED, null);
					sq_hndl.sendMessage(msg1);
					break;
				}
			case MSG_PUSH_MSG_EASEMOB_INFO:
				SamLog.i(TAG,"EASEMOB_INFO is got");
				HttpPushInfo phinfoq = (HttpPushInfo)msg.obj;
				LoginUser user = query_LoginUser_db(phinfoq.cellphone);
				if(user != null){
					if(user.easemob_username == null){
						SamLog.i(TAG,"EASEMOB_INFO not exited, store into db:easemob_username = "+phinfoq.easemob_username);
						user.unique_id = phinfoq.unique_id;
						user.easemob_username = phinfoq.easemob_username;
						add_update_LoginUser_db(user);
						
						if(user.status == LoginUser.ACTIVE){
							set_current_user(user);
							Intent intent = new Intent();
							intent.setAction(SamService.EASEMOBNAMEGOT);
							mContext.sendBroadcast(intent);
						}
						
						SamLog.i(TAG,"update current user:" + user.easemob_username);
						
					}else{
						SamLog.i(TAG,"EASEMOB_INFO existed, drop it");
					}
				}else{
					SamLog.i(TAG,"Invalid cellphone number ,  drop this EASEMOB_INFO");
				}
				break;
			
		}
    		
    	}
    	
    	
    }
    

	private boolean isAnswerValid(String quest_id){
		boolean ret = false;
		synchronized(activeQuestionArray){
			for(int i = 0;i < activeQuestionArray.size(); i ++){
				if(quest_id.equals(activeQuestionArray.get(i).question_id)){
					ret  = true;
					return ret;
				}
			}

			return ret;
		}
	}
	
	public void cancel_question(Handler callback,int cbMsg){
		CBObj obj = new CBObj(callback,cbMsg);
		SamCoreObj samobj= new CancelqCoreObj(obj,current_question_id);
    		Message msg = mSamServiceHandler.obtainMessage(MSG_CANCEL_QUESTION, samobj);
    		mSamServiceHandler.sendMessage(msg);
		startTimeOut(samobj);
	}

	public void cancel_question(Handler callback,int cbMsg,String question_id){
		CBObj obj = new CBObj(callback,cbMsg);
		SamCoreObj samobj= new CancelqCoreObj(obj,question_id);
    		Message msg = mSamServiceHandler.obtainMessage(MSG_CANCEL_QUESTION, samobj);
    		mSamServiceHandler.sendMessage(msg);
		startTimeOut(samobj);
	}

	public void send_question(String question,Handler callback,int cbMsg){
		CBObj obj = new CBObj(callback,cbMsg,question);
		SamCoreObj samobj= new SendqCoreObj(obj);
		Message msg = mSamServiceHandler.obtainMessage(MSG_SEND_QUESTION, samobj);
		mSamServiceHandler.sendMessage(msg);
		
		startTimeOut(samobj);
	}

	public void upgrade(Handler callback,int cbMsg){
		CBObj obj = new CBObj(callback,cbMsg);
		SamCoreObj  samobj =  new UpgradeCoreObj(obj);

		Message msg = mSamServiceHandler.obtainMessage(MSG_UPGRADE_TO_SERVICER, samobj);
    		mSamServiceHandler.sendMessage(msg);

		startTimeOut(samobj);
		
	}

	public void answer_question(String question_id, String answer){
		CBObj obj = new CBObj();
		
		SendAnswer sda = new SendAnswer();
		sda.setquestion_id(question_id);
		sda.setanswer(answer);
		sda.setloginuserid(get_current_user().getid());
		long id = add_SendAnswer_db(sda);
		sda.setid(id);
		SamLog.e(TAG,"store answer into db:"+id);
		
		SamCoreObj samobj = new SendaCoreObj(obj,sda);

		Message msg = mSamServiceHandler.obtainMessage(MSG_ANSWER_QUESTION, samobj);
    		mSamServiceHandler.sendMessage(msg);

		startTimeOut(samobj);

		SamLog.e(TAG,"answer_question");
	}

	public void reanswer_question(SendAnswer sda){
		CBObj obj = new CBObj();
		
		SamCoreObj samobj = new SendaCoreObj(obj,sda);

		Message msg = mSamServiceHandler.obtainMessage(MSG_ANSWER_QUESTION, samobj);
    		mSamServiceHandler.sendMessage(msg);

		startTimeOut(samobj);

		SamLog.e(TAG,"answer_question");
	}

	private void do_answer_question(SamCoreObj samobj){
		CBObj cbobj = samobj.refCBObj;

		/*store new sendanswer into db*/
		SendaCoreObj sdaobj = (SendaCoreObj)(samobj);
		SendAnswer sda = sdaobj.sda;

		Intent intent = new Intent();
		intent.setAction(SamQADetailActivity.SEND_ANSWER_STATUS_BROADCAST);
		
		String token = get_current_token();

		HttpCommClient hcc = new HttpCommClient();
		if(hcc.answer(sda,token)){
			if(hcc.ret == RET_ANSWER_FROM_SERVER_OK){
				cancelTimeOut(samobj);
				boolean continue_run = true;
				synchronized(samobj){
					if(samobj.request_status == SamCoreObj.STATUS_INIT){
						samobj.request_status = SamCoreObj.STATUS_DONE;
					}else if(samobj.request_status == SamCoreObj.STATUS_TIMEOUT){
						continue_run = false;
					}
				}

				if(!continue_run) return;
				
				sda.setstatus(SendAnswer.SEND_SUCCEED);
				update_SendAnswer_db(sda);
				Bundle bundle = new Bundle();
				bundle.putSerializable("SendAnswer",sda);
				intent.putExtras(bundle);
				intent.putExtra("NoSuchQuestion", false);
				SamLog.e(TAG,"Send Answer OK!");
			}else if(hcc.ret == RET_ANSWER_FROM_SERVER_TOKEN_INVALID){
				SamLog.e(TAG,"Send Answer TOKEN INVALIDE!");
				/*auto sign in*/
				SignService.getInstance().attemptAutoSignIn(mSamServiceHandler, MSG_AUTOLOGIN_CALLBACK,samobj);	
			}else if(hcc.ret == RET_ANSWER_FROM_SERVER_NO_SUCH_QUESTION){
				cancelTimeOut(samobj);
				boolean continue_run = true;
				synchronized(samobj){
					if(samobj.request_status == SamCoreObj.STATUS_INIT){
						samobj.request_status = SamCoreObj.STATUS_DONE;
					}else if(samobj.request_status == SamCoreObj.STATUS_TIMEOUT){
						continue_run = false;
					}
				}

				if(!continue_run) return;

				sda.setstatus(SendAnswer.SEND_FAILED);
				update_SendAnswer_db(sda);
				Bundle bundle = new Bundle();
				bundle.putSerializable("SendAnswer",sda);
				intent.putExtras(bundle);
				intent.putExtra("NoSuchQuestion", true);
				SamLog.e(TAG,"Send Answer FAILED:NoSuchQuestion!");
			}else{
				cancelTimeOut(samobj);
				boolean continue_run = true;
				synchronized(samobj){
					if(samobj.request_status == SamCoreObj.STATUS_INIT){
						samobj.request_status = SamCoreObj.STATUS_DONE;
					}else if(samobj.request_status == SamCoreObj.STATUS_TIMEOUT){
						continue_run = false;
					}
				}

				if(!continue_run) return;
				
				sda.setstatus(SendAnswer.SEND_FAILED);
				update_SendAnswer_db(sda);
				Bundle bundle = new Bundle();
				bundle.putSerializable("SendAnswer",sda);
				intent.putExtras(bundle);
				intent.putExtra("NoSuchQuestion", true);
				SamLog.e(TAG,"Send Answer FAILED:Send Failed!");
			}
		}else{
			sda.setstatus(SendAnswer.SEND_FAILED);
			update_SendAnswer_db(sda);
			Bundle bundle = new Bundle();
			bundle.putSerializable("SendAnswer",sda);
			intent.putExtras(bundle);
			intent.putExtra("NoSuchQuestion", false);
			SamLog.e(TAG,"Send Answer ERROR!");
		}

		SamLog.e(TAG,"Send Answer Broadcast!");

		mContext.sendBroadcast(intent);

	}

	private void do_upgrade(SamCoreObj samobj){
		CBObj cbobj = samobj.refCBObj;
		
		if(cbobj==null || cbobj.cbHandler==null ){
			SamLog.e(TAG, "bad msg in upgrade,drop it...");
			return;
		}
		
		Handler hndl = cbobj.cbHandler.get();
		if(hndl == null){
			SamLog.e(TAG, "cbhandler has been destroy in upgrade, drop cbMsg ...");
			return;
		}

		String token = get_current_token();

		if(token == null){
			SamLog.e(TAG, "token is null, should never run to here");
			Message msg = hndl.obtainMessage(cbobj.cbMsg, R_CANCEL_QUESTION_ERROR, R_CANCEL_QUESTION_ERROR_TOKEN_FILE_NULL);
			hndl.sendMessage(msg);
			return;
		}

		HttpCommClient hcc = new HttpCommClient();
		if(hcc.upgrade(token)){
			if(hcc.ret == RET_UPGRADE_FROM_SERVER_OK){
				boolean continue_run = true;
				cancelTimeOut(samobj);
				synchronized(samobj){
					if(samobj.request_status == SamCoreObj.STATUS_INIT){
						samobj.request_status = SamCoreObj.STATUS_DONE;
					}else if(samobj.request_status == SamCoreObj.STATUS_TIMEOUT){
						continue_run = false;
					}
				}

				if(!continue_run) return;

				//update new token
				store_current_token(hcc.token_id);
				
				//update question info into db
				LoginUser user = query_activie_LoginUser_db();
				user.usertype = LoginUser.MIDSERVER;
				add_update_LoginUser_db(user);
				set_current_user(user);
				

				Message msg = hndl.obtainMessage(cbobj.cbMsg, R_UPGRADE_OK, 0,null);
				hndl.sendMessage(msg);
			}else{

				if(hcc.ret == RET_UPGRADE_FROM_SERVER_TOKEN_INVALID){
				/*auto sign in*/
					SignService.getInstance().attemptAutoSignIn(mSamServiceHandler, MSG_AUTOLOGIN_CALLBACK,samobj);		

				}else{
					cancelTimeOut(samobj);
					boolean continue_run = true;
					synchronized(samobj){
						if(samobj.request_status == SamCoreObj.STATUS_INIT){
							samobj.request_status = SamCoreObj.STATUS_DONE;
						}else if(samobj.request_status == SamCoreObj.STATUS_TIMEOUT){
							continue_run = false;
						}
					}

					if(!continue_run) return;
					
					Message msg = hndl.obtainMessage(cbobj.cbMsg, R_UPGRADE_FAILED, 0,null);
					hndl.sendMessage(msg);
				}
			}

			
		}else{
				boolean continue_run = true;
				cancelTimeOut(samobj);
				synchronized(samobj){
					if(samobj.request_status == SamCoreObj.STATUS_INIT){
						samobj.request_status = SamCoreObj.STATUS_DONE;
					}else if(samobj.request_status == SamCoreObj.STATUS_TIMEOUT){
						continue_run = false;
					}
				}

				if(!continue_run) return;
				Message msg = hndl.obtainMessage(cbobj.cbMsg, R_UPGRADE_ERROR, R_UPGRADE_ERROR_HTTP_EXCEPTION,null);
				hndl.sendMessage(msg);
		}
		
	}


	private void do_cancel_queston(SamCoreObj samobj){
		CBObj cbobj = samobj.refCBObj;
		
		if(cbobj==null || cbobj.cbHandler==null ){
			SamLog.e(TAG, "bad msg in cancel queston,drop it...");
			return;
		}
		
		Handler hndl = cbobj.cbHandler.get();
		if(hndl == null){
			SamLog.e(TAG, "cbhandler has been destroy in cancel question, drop cbMsg ...");
			return;
		}

		String token = get_current_token();

		if(token == null){
			SamLog.e(TAG, "token is null, should never run to here");
			Message msg = hndl.obtainMessage(cbobj.cbMsg, R_CANCEL_QUESTION_ERROR, R_CANCEL_QUESTION_ERROR_TOKEN_FILE_NULL);
			hndl.sendMessage(msg);
			return;
		}

		HttpCommClient hcc = new HttpCommClient();
		if(hcc.cancelquestion(current_question_id,token)){
			
			/*cbobj.qinfo.id = question id assigned by server*/
			if(hcc.ret == RET_CANCEL_QUESTION_FROM_SERVER_OK){
				boolean continue_run = true;
				cancelTimeOut(samobj);
				synchronized(samobj){
					if(samobj.request_status == SamCoreObj.STATUS_INIT){
						samobj.request_status = SamCoreObj.STATUS_DONE;
					}else if(samobj.request_status == SamCoreObj.STATUS_TIMEOUT){
						continue_run = false;
					}
				}

				if(!continue_run) return;

				//update activeQuestionArray
				synchronized(activeQuestionArray){
					for(int i = 0;i < activeQuestionArray.size(); i ++){
						if(((CancelqCoreObj)samobj).question_id.equals(activeQuestionArray.get(i).question_id)){
							activeQuestionArray.remove(i);
							break;
						}
					}
				}

				
				//update question info into db
				SendQuestion question = query_send_question_db(current_question_id);
				if(question !=null){
					question.canceltime = System.currentTimeMillis();
					question.status = SendQuestion.CANCEL;
					add_update_SendQuestion_db(question);
				}
				
				cbobj.qinfo.ret = hcc.ret;
				synchronized(this){
					current_question_id = null;
				}
				Message msg = hndl.obtainMessage(cbobj.cbMsg, R_CANCEL_QUESTION_OK, 0,samobj);
				hndl.sendMessage(msg);
			}else{
				cbobj.qinfo.ret = hcc.ret;

				if(hcc.ret == RET_CANCEL_QUESTION_FROM_SERVER_TOKEN_INVALID){
				/*auto sign in*/
					SignService.getInstance().attemptAutoSignIn(mSamServiceHandler, MSG_AUTOLOGIN_CALLBACK,samobj);		

				}else{
					cancelTimeOut(samobj);
					boolean continue_run = true;
					synchronized(samobj){
						if(samobj.request_status == SamCoreObj.STATUS_INIT){
							samobj.request_status = SamCoreObj.STATUS_DONE;
						}else if(samobj.request_status == SamCoreObj.STATUS_TIMEOUT){
							continue_run = false;
						}
					}

					if(!continue_run) return;
					
					Message msg = hndl.obtainMessage(cbobj.cbMsg, R_CANCEL_QUESTION_FAILED, 0,samobj);
					hndl.sendMessage(msg);
				}
			}

			//Message msg1 = hndl.obtainMessage(SamService_Fragment.MSG_ANSWER_BACK, 0, -1,cbobj.qinfo);
			//hndl.sendMessage(msg1);
		}else{
				boolean continue_run = true;
				cancelTimeOut(samobj);
				synchronized(samobj){
					if(samobj.request_status == SamCoreObj.STATUS_INIT){
						samobj.request_status = SamCoreObj.STATUS_DONE;
					}else if(samobj.request_status == SamCoreObj.STATUS_TIMEOUT){
						continue_run = false;
					}
				}

				if(!continue_run) return;
				Message msg = hndl.obtainMessage(cbobj.cbMsg, R_CANCEL_QUESTION_ERROR, R_CANCEL_QUESTION_ERROR_HTTP_EXCEPTION,samobj);
				hndl.sendMessage(msg);
		}
    	
		

	}
   
	private void do_send_question(SamCoreObj samobj){
		CBObj cbobj = samobj.refCBObj;
		if(cbobj==null || cbobj.cbHandler==null ){
			SamLog.e(TAG, "bad msg in send question,drop it...");
			return;
		}
		
		Handler hndl = cbobj.cbHandler.get();
		if(hndl == null){
			SamLog.e(TAG, "cbhandler has been destroy in send question, drop cbMsg ...");
			return;
		}

		String token = get_current_token();

		if(token == null){
			SamLog.e(TAG, "token is null, should never run to here");
			Message msg = hndl.obtainMessage(cbobj.cbMsg, R_SEND_QUESTION_ERROR, R_SEND_QUESTION_ERROR_TOKEN_FILE_NULL,samobj);
			hndl.sendMessage(msg);
			return;
		}
		
		
		HttpCommClient hcc = new HttpCommClient();
		if(hcc.sendquestion(cbobj.qinfo.question,token)){
			/*cbobj.qinfo.id = question id assigned by server*/
			if(hcc.ret == RET_SEND_QUESTION_FROM_SERVER_OK){
				cancelTimeOut(samobj);
				boolean continue_run = true;
				synchronized(samobj){
					if(samobj.request_status == SamCoreObj.STATUS_INIT){
						samobj.request_status = SamCoreObj.STATUS_DONE;
					}else if(samobj.request_status == SamCoreObj.STATUS_TIMEOUT){
						continue_run = false;
					}
				}

				if(!continue_run) return;
				
				cbobj.qinfo.question_id = hcc.question_id;
				cbobj.qinfo.ret = hcc.ret;
				synchronized(this){
					current_question_id = hcc.question_id;
				}

				((SendqCoreObj)samobj).question_id = hcc.question_id;
				ActiveQuestion aq = new ActiveQuestion(hcc.question_id,cbobj.qinfo.question);
				synchronized(activeQuestionArray){
					activeQuestionArray.add(aq);
				}

				/*store new question into db*/
				SendQuestion question = new SendQuestion(current_user.id,hcc.question_id,cbobj.qinfo.question);
				if(add_update_SendQuestion_db(question)!=-1){
					SamLog.e(TAG,"insert question success");
				}
				
				Message msg = hndl.obtainMessage(cbobj.cbMsg, R_SEND_QUESTION_OK, 0,samobj);
				hndl.sendMessage(msg);
			}else{
				cbobj.qinfo.ret = hcc.ret;
				//Message msg = hndl.obtainMessage(cbobj.cbMsg, R_SEND_QUESTION_FAILED, 0,cbobj.qinfo);
				//hndl.sendMessage(msg);

				if(hcc.ret == RET_SEND_QUESTION_FROM_SERVER_TOKEN_INVALID){
				/*auto sign in*/
					SignService.getInstance().attemptAutoSignIn(mSamServiceHandler, MSG_AUTOLOGIN_CALLBACK,samobj);		

				}else{
					cancelTimeOut(samobj);
					boolean continue_run = true;
					synchronized(samobj){
						if(samobj.request_status == SamCoreObj.STATUS_INIT){
							samobj.request_status = SamCoreObj.STATUS_DONE;
						}else if(samobj.request_status == SamCoreObj.STATUS_TIMEOUT){
							continue_run = false;
						}
					}

					if(!continue_run) return;
					
					Message msg = hndl.obtainMessage(cbobj.cbMsg, R_SEND_QUESTION_FAILED, 0,samobj);
					hndl.sendMessage(msg);
				}

				


				
			}

			//Message msg1 = hndl.obtainMessage(SamService_Fragment.MSG_ANSWER_BACK, 0, -1,cbobj.qinfo);
			//hndl.sendMessage(msg1);
		}else{
			boolean continue_run = true;
			synchronized(samobj){
				if(samobj.request_status == SamCoreObj.STATUS_INIT){
					samobj.request_status = SamCoreObj.STATUS_DONE;
				}else if(samobj.request_status == SamCoreObj.STATUS_TIMEOUT){
					continue_run = false;
				}
			}

			if(!continue_run) return;
		
			Message msg = hndl.obtainMessage(cbobj.cbMsg, R_SEND_QUESTION_ERROR, R_SEND_QUESTION_ERROR_HTTP_EXCEPTION,samobj);
			hndl.sendMessage(msg);
		}
    	
    }



	public class WaitThread extends Thread {  
		private boolean run=true;
		private HttpCommClient hcc;
		private WeakReference <SamPushServiceHandler>  push_hndl;
  
		public WaitThread (){}  
		public WaitThread (String name){  
			super(name);
			push_hndl = new WeakReference <SamPushServiceHandler>(mSamPushServiceHandler);
			
		}  
      
		@Override  
		public void run() {  
			hcc = new HttpCommClient();
			while(run){   
				/*connect to server*/
				if(current_token == null){
					SamLog.e(TAG,"WaitThread: current token is null");
					SystemClock.sleep(5000);
					continue;
				}

				if(!hcc.HttpPushWait(get_current_token())){
					SystemClock.sleep(5000);
					SamLog.e(TAG,"WaitThread: HttpPushWait false");
					continue;
				}else{
					if(hcc.statusCode == HttpStatus.SC_OK){
						int msg_id;
						if(hcc.hpinfo.category == HttpPushInfo.QUESTION){
							msg_id = MSG_PUSH_MSG_GOTQUESTION;
						}else if(hcc.hpinfo.category == HttpPushInfo.ANSWER){
							msg_id = MSG_PUSH_MSG_GOTANSWER;
						}else if(hcc.hpinfo.category == HttpPushInfo.EASEMOBINFO){
							msg_id = MSG_PUSH_MSG_EASEMOB_INFO;
						}else{
							SamLog.e(TAG,"WaitThread:Fatal Eror, category is not support");
							continue;
						}
						synchronized(pushLock){
							SamPushServiceHandler ph = push_hndl.get();
							if(ph!=null){
								Message msg = ph.obtainMessage(msg_id, 0, 0,hcc.hpinfo);
								ph.sendMessage(msg);
							}
						}
					}else if(hcc.statusCode == 401){ //token invalide
						SamLog.e(TAG,"WaitThread: token invalid");
						SystemClock.sleep(1000);
						continue;
					}else if(hcc.statusCode == 408){ //server cancel connection , need resend wait cmd
						SamLog.e(TAG,"WaitThread: server cancel connection , need resend wait cmd");
						continue;
					}else if(hcc.statusCode == 503){
						SamLog.e(TAG,"WaitThread:Push servier is shutdown");
						Message msg = mSamPushServiceHandler.obtainMessage(MSG_PUSH_MSG_PUSHSERVERSHUTDOWN, 0, 0,null);
						mSamPushServiceHandler.sendMessage(msg);
						SystemClock.sleep(5000);
						continue;
					}else{
						SamLog.e(TAG,"WaitThread:impossible here. StatusCode:"+hcc.statusCode);
						SystemClock.sleep(10000);
						continue;
					}


				}

				
				
			}
    		} 

		public void StopThread(){
			run = false;
			
		}

	}   
    
}
