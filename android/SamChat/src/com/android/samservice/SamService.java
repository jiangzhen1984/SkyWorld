package com.android.samservice;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpStatus;

import com.android.samchat.SamChats_Fragment;
import com.android.samchat.SamMe_Fragment;
import com.android.samchat.SamContact_Fragment;
import com.android.samchat.SamQADetailActivity;
import com.android.samchat.SamService_Fragment;
import com.android.samchat.SamVendorInfo;
import com.android.samchat.skyworld;
import com.android.samservice.info.*;
import com.android.samservice.provider.*;

import android.app.Activity;
import android.content.ContentValues;
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
    public static final int MAX_MPHONE_NUMBER_LENGTH = 15;	
    public static final int MIN_USERNAME_LENGTH = 3;
    public static final int MAX_USERNAME_LENGTH = 15;
    public static final int MIN_PASSWORD_LENGTH = 6;
    public static final int MAX_PASSWORD_LENGTH = 32;
    public static final int SAMSERVICE_HANDLE_TIMEOUT=10000;
    public static final int SAMSERVICE_PUSH_HANDLE_RECONNECT_TIMEOUT=60000;


    public static final int RETRY_MAX = 3;
	
    public static final String FINISH_ALL_SIGN_ACTVITY = "com.android.sam.finishAllSign";
    public static final String EASEMOBNAMEGOT = "com.android.sam.easemobnamegot";
    public static final String CONTACT_INVITE_NEW = "com.android.sam.contactinvitenew";
    public static final String CONTACT_INVITE_UPDATE = "com.android.sam.contactinviteupdate";
    
    public static String sam_cache_path;
    public static String sam_download_path;
    public static String AVATAR_FOLDER = "/avfolder";
    public static String AVATAR="/avatar";
    public static String FG_PIC_FOLDER = "/fgfolder";
    public static String FG_PIC="/fgpic";
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

    public static final int MSG_QUERY_USERINFO=6;
	//query user info
    	public static final int R_QUERY_USERINFO_OK=0;
    	public static final int R_QUERY_USERINFO_ERROR=1;
    	public static final int R_QUERY_USERINFO_FAILED=2;

	public static final int R_QUERY_USERINFO_ERROR_TOKEN_FILE_EXCEPTION = 0;
	public static final int R_QUERY_USERINFO_ERROR_TOKEN_FILE_NULL = 1;
	public static final int R_QUERY_USERINFO_ERROR_HTTP_EXCEPTION = 2;
	public static final int R_QUERY_USERINFO_ERROR_TIMEOUT = 3;

	public static final int RET_QUERY_USERINFO_SERVER_OK = 0;
	public static final int RET_QUERY_USERINFO_SERVER_HTTP_FAILED = -1;//parse http failed
	public static final int RET_QUERY_USERINFO_SERVER_ACTION_NOT_SUPPORT = -2;
	public static final int RET_QUERY_USERINFO_SERVER_PARAM_NOT_SUPPORT = -3;
	public static final int RET_QUERY_USERINFO_SERVER_TOKEN_FORMAT_ERROR = -4;
	public static final int RET_QUERY_USERINFO_SERVER_TOKEN_INVALID = -5;
	public static final int RET_QUERY_USERINFO_SERVER_NO_SUCH_USER = -702;

    public static final int MSG_UPLOAD_AVATAR = 7;
	//upload avatar
    	public static final int R_UPLOAD_AVATAR_OK=0;
    	public static final int R_UPLOAD_AVATAR_ERROR=1;
    	public static final int R_UPLOAD_AVATAR_FAILED=2;

	public static final int R_UPLOAD_AVATAR_ERROR_TOKEN_FILE_EXCEPTION = 0;
	public static final int R_UPLOAD_AVATAR_ERROR_TOKEN_FILE_NULL = 1;
	public static final int R_UPLOAD_AVATAR_ERROR_HTTP_EXCEPTION = 2;
	public static final int R_UPLOAD_AVATAR_ERROR_TIMEOUT = 3;

	public static final int RET_UPLOAD_AVATAR_SERVER_OK = 0;
	public static final int RET_UPLOAD_AVATAR_SERVER_HTTP_FAILED = -1;//parse http failed
	public static final int RET_UPLOAD_AVATAR_SERVER_ACTION_NOT_SUPPORT = -2;
	public static final int RET_UPLOAD_AVATAR_SERVER_PARAM_NOT_SUPPORT = -3;
	public static final int RET_UPLOAD_AVATAR_SERVER_TOKEN_FORMAT_ERROR = -4;
	public static final int RET_UPLOAD_AVATAR_SERVER_TOKEN_INVALID = -5;
	public static final int RET_UPLOAD_AVATAR_SERVER_UPLOAD_FAILED = -801;
	public static final int RET_UPLOAD_AVATAR_SERVER_TYPE_NOT_SUPPORT = -802;
	public static final int RET_UPLOAD_AVATAR_SERVER_OVER_SIZE = -803;

    public static final int MSG_SEND_COMMENTS = 8;
	//send feedback comments
    	public static final int R_SEND_COMMENTS_OK=0;
    	public static final int R_SEND_COMMENTS_ERROR=1;
    	public static final int R_SEND_COMMENTS_FAILED=2;

	public static final int R_SEND_COMMENTS_ERROR_TOKEN_FILE_EXCEPTION = 0;
	public static final int R_SEND_COMMENTS_ERROR_TOKEN_FILE_NULL = 1;
	public static final int R_SEND_COMMENTS_ERROR_HTTP_EXCEPTION = 2;
	public static final int R_SEND_COMMENTS_ERROR_TIMEOUT = 3;

	public static final int RET_SEND_COMMENTS_SERVER_OK = 0;
	public static final int RET_SEND_COMMENTS_SERVER_HTTP_FAILED = -1;//parse http failed
	public static final int RET_SEND_COMMENTS_SERVER_ACTION_NOT_SUPPORT = -2;
	public static final int RET_SEND_COMMENTS_SERVER_TOKEN_INVALID = -5;

    public static final int MSG_UPLOAD_FG = 9;
	//upload to friend group
    	public static final int R_UPLOAD_FG_OK=0;
    	public static final int R_UPLOAD_FG_ERROR=1;
    	public static final int R_UPLOAD_FG_FAILED=2;

	public static final int R_UPLOAD_FG_ERROR_TOKEN_FILE_EXCEPTION = 0;
	public static final int R_UPLOAD_FG_ERROR_TOKEN_FILE_NULL = 1;
	public static final int R_UPLOAD_FG_ERROR_HTTP_EXCEPTION = 2;
	public static final int R_UPLOAD_FG_ERROR_TIMEOUT = 3;

	public static final int RET_UPLOAD_FG_SERVER_OK = 0;
	public static final int RET_UPLOAD_FG_SERVER_HTTP_FAILED = -1;//parse http failed
	public static final int RET_UPLOAD_FG_SERVER_ACTION_NOT_SUPPORT = -2;
	
	public static final int RET_UPLOAD_FG_SERVER_PARAM_NOT_SUPPORT = -3;
	public static final int RET_UPLOAD_FG_SERVER_TOKEN_FORMAT_ERROR = -4;
	public static final int RET_UPLOAD_FG_SERVER_TOKEN_INVALID = -5;
	public static final int RET_UPLOAD_FG_SERVER_HANDLE_STREAM_FAILED = -6;

    public static final int MSG_QUERY_FG = 10;
	//query to friend group
    	public static final int R_QUERY_FG_OK=0;
    	public static final int R_QUERY_FG_ERROR=1;
    	public static final int R_QUERY_FG_FAILED=2;

	public static final int R_QUERY_FG_ERROR_TOKEN_FILE_EXCEPTION = 0;
	public static final int R_QUERY_FG_ERROR_TOKEN_FILE_NULL = 1;
	public static final int R_QUERY_FG_ERROR_HTTP_EXCEPTION = 2;
	public static final int R_QUERY_FG_ERROR_TIMEOUT = 3;

	public static final int RET_QUERY_FG_SERVER_OK = 0;
	public static final int RET_QUERY_FG_SERVER_HTTP_FAILED = -1;//parse http failed
	public static final int RET_QUERY_FG_SERVER_ACTION_NOT_SUPPORT = -2;
	public static final int RET_QUERY_FG_SERVER_PARAM_NOT_SUPPORT = -3;
	public static final int RET_QUERY_FG_SERVER_TOKEN_FORMAT_ERROR = -4;
	public static final int RET_QUERY_FG_SERVER_TOKEN_INVALID = -5;
	public static final int RET_QUERY_FG_SERVER_HANDLE_STREAM_FAILED = -6;


    public static final int MSG_COMMENT_FG = 11;
	//query to friend group
    	public static final int R_COMMENT_FG_OK=0;
    	public static final int R_COMMENT_FG_ERROR=1;
    	public static final int R_COMMENT_FG_FAILED=2;

	public static final int R_COMMENT_FG_ERROR_TOKEN_FILE_EXCEPTION = 0;
	public static final int R_COMMENT_FG_ERROR_TOKEN_FILE_NULL = 1;
	public static final int R_COMMENT_FG_ERROR_HTTP_EXCEPTION = 2;
	public static final int R_COMMENT_FG_ERROR_TIMEOUT = 3;

	public static final int RET_COMMENT_FG_SERVER_OK = 0;
	public static final int RET_COMMENT_FG_SERVER_HTTP_FAILED = -1;//parse http failed
	public static final int RET_COMMENT_FG_SERVER_ACTION_NOT_SUPPORT = -2;
	public static final int RET_COMMENT_FG_SERVER_PARAM_NOT_SUPPORT = -3;
	public static final int RET_COMMENT_FG_SERVER_TOKEN_FORMAT_ERROR = -4;
	public static final int RET_COMMENT_FG_SERVER_TOKEN_INVALID = -5;
	public static final int RET_COMMENT_FG_SERVER_HANDLE_STREAM_FAILED = -6;

    public static final int MSG_FOLLOW = 12;
	//query to friend group
    	public static final int R_FOLLOW_OK=0;
    	public static final int R_FOLLOW_ERROR=1;
    	public static final int R_FOLLOW_FAILED=2;

	public static final int R_FOLLOW_ERROR_TOKEN_FILE_EXCEPTION = 0;
	public static final int R_FOLLOW_ERROR_TOKEN_FILE_NULL = 1;
	public static final int R_FOLLOW_ERROR_HTTP_EXCEPTION = 2;
	public static final int R_FOLLOW_ERROR_TIMEOUT = 3;

	public static final int RET_FOLLOW_SERVER_OK = 0;
	public static final int RET_FOLLOW_SERVER_HTTP_FAILED = -1;//parse http failed
	public static final int RET_FOLLOW_SERVER_ACTION_NOT_SUPPORT = -2;
	public static final int RET_FOLLOW_SERVER_PARAM_NOT_SUPPORT = -3;
	public static final int RET_FOLLOW_SERVER_TOKEN_FORMAT_ERROR = -4;
	public static final int RET_FOLLOW_SERVER_TOKEN_INVALID = -5;
	public static final int RET_FOLLOW_SERVER_USER_NOT_EXISTED = -1001;
	public static final int RET_FOLLOW_SERVER_UNSUPPORT_FLAG = -1002;


    public static final int MSG_QUERY_FOLLOWER = 13;
	//query to friend group
    	public static final int R_QUERY_FOLLOWER_OK=0;
    	public static final int R_QUERY_FOLLOWER_ERROR=1;
    	public static final int R_QUERY_FOLLOWER_FAILED=2;

	public static final int R_QUERY_FOLLOWER_ERROR_TOKEN_FILE_EXCEPTION = 0;
	public static final int R_QUERY_FOLLOWER_ERROR_TOKEN_FILE_NULL = 1;
	public static final int R_QUERY_FOLLOWER_ERROR_HTTP_EXCEPTION = 2;
	public static final int R_QUERY_FOLLOWER_ERROR_TIMEOUT = 3;

	public static final int RET_QUERY_FOLLOWER_SERVER_OK = 0;
	public static final int RET_QUERY_FOLLOWER_SERVER_HTTP_FAILED = -1;//parse http failed
	public static final int RET_QUERY_FOLLOWER_SERVER_ACTION_NOT_SUPPORT = -2;
	public static final int RET_QUERY_FOLLOWER_SERVER_PARAM_NOT_SUPPORT = -3;
	public static final int RET_QUERY_FOLLOWER_SERVER_TOKEN_FORMAT_ERROR = -4;
	public static final int RET_QUERY_FOLLOWER_SERVER_TOKEN_INVALID = -5;
	public static final int RET_QUERY_FOLLOWER_SERVER_NO_ANY_DATA = -702;


    public static final int MSG_QUERY_PUBLIC_INFO = 14;
	//query public followed
    	public static final int R_QUERY_PUBLIC_INFO_OK=0;
    	public static final int R_QUERY_PUBLIC_INFO_ERROR=1;
    	public static final int R_QUERY_PUBLIC_INFO_FAILED=2;

	public static final int R_QUERY_PUBLIC_INFO_ERROR_TOKEN_FILE_EXCEPTION = 0;
	public static final int R_QUERY_PUBLIC_INFO_ERROR_TOKEN_FILE_NULL = 1;
	public static final int R_QUERY_PUBLIC_INFO_ERROR_HTTP_EXCEPTION = 2;
	public static final int R_QUERY_PUBLIC_INFO_ERROR_TIMEOUT = 3;

	public static final int RET_QUERY_PUBLIC_INFO_SERVER_OK = 0;
	public static final int RET_QUERY_PUBLIC_INFO_SERVER_HTTP_FAILED = -1;//parse http failed
	public static final int RET_QUERY_PUBLIC_INFO_SERVER_ACTION_NOT_SUPPORT = -2;
	public static final int RET_QUERY_PUBLIC_INFO_SERVER_PARAM_NOT_SUPPORT = -3;
	public static final int RET_QUERY_PUBLIC_INFO_SERVER_TOKEN_FORMAT_ERROR = -4;
	public static final int RET_QUERY_PUBLIC_INFO_SERVER_TOKEN_INVALID = -5;
	public static final int RET_QUERY_PUBLIC_INFO_SERVER_OPT_NOT_SUPPORT = -1201;
	public static final int RET_QUERY_PUBLIC_INFO_SERVER_USER_NOT_PUBLIC = -1202;


    public static final int MSG_QUERY_HOT_TOPIC = 15;
	//query hot topic
    	public static final int R_QUERY_HOT_TOPIC_OK=0;
    	public static final int R_QUERY_HOT_TOPIC_ERROR=1;
    	public static final int R_QUERY_HOT_TOPIC_FAILED=2;

	public static final int R_QUERY_HOT_TOPIC_ERROR_TOKEN_FILE_EXCEPTION = 0;
	public static final int R_QUERY_HOT_TOPIC_ERROR_TOKEN_FILE_NULL = 1;
	public static final int R_QUERY_HOT_TOPIC_ERROR_HTTP_EXCEPTION = 2;
	public static final int R_QUERY_HOT_TOPIC_ERROR_TIMEOUT = 3;

	public static final int RET_QUERY_HOT_TOPIC_SERVER_OK = 0;
	public static final int RET_QUERY_HOT_TOPIC_SERVER_HTTP_FAILED = -1;//parse http failed
	public static final int RET_QUERY_HOT_TOPIC_SERVER_ACTION_NOT_SUPPORT = -2;
	public static final int RET_QUERY_HOT_TOPIC_SERVER_PARAM_NOT_SUPPORT = -3;
	public static final int RET_QUERY_HOT_TOPIC_SERVER_TOKEN_FORMAT_ERROR = -4;
	public static final int RET_QUERY_HOT_TOPIC_SERVER_TOKEN_INVALID = -5;

	public static final int MSG_PUSH_MSG_GOTANSWER = 0;
	public static final int MSG_PUSH_MSG_PUSHSERVERSHUTDOWN = 1;	
	public static final int MSG_PUSH_MSG_GOTQUESTION = 2;
	public static final int MSG_PUSH_MSG_EASEMOB_INFO = 3;
	public static final int MSG_PUSH_MSG_RECONNECT_TIMOUT = 4;

	private static SamService mSamService;
	private static Context mContext;
	private static Object pushLock = new Object();
	private Object stopLock = new Object();

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

	private SamDBDao dao;	

	private LoginUser current_user;

	private List<ActiveQuestion> activeQuestionArray = new ArrayList<ActiveQuestion>();


	

	public List<ActiveQuestion> getActiveQuestionArray(){
		return activeQuestionArray;
	}

	synchronized public void set_current_user(LoginUser user){
		current_user = user;
	}

	public LoginUser get_current_user(){
		return current_user;
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
	if(mContext.getExternalCacheDir()!=null){
		sam_cache_path = mContext.getExternalCacheDir().getAbsolutePath();
	}else{
		sam_download_path = mContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
	}

	
    	
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
		mContext = skyworld.appContext;
		if(mSamService == null){
			mSamService	= new SamService();
		}
		return mSamService;
	}

	public static synchronized SamService getInstance(Activity activity){
		mContext = skyworld.appContext;//activity.getApplicationContext();
		if(mSamService == null){
			mSamService	 = new SamService();
		}
		return mSamService;
	}

	public SamDBDao getDao(){
		return dao;
	}

	private SamService(){
		dao = new SamDBDao(mContext);
	
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

	private void waitStopLock(){
		synchronized (stopLock){
			try {
				stopLock.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
    		}
	}

	private void notifyStopLock(){
		synchronized (stopLock){
			try {
				stopLock.notify();
			} catch (Exception e) {
				e.printStackTrace();
			}
    		}
	}
    
	public void stopSamService(){
		
		if(mWaitThread!=null){
			mWaitThread.StopThread();
			SamLog.e(TAG,"before waitStopLock");
			waitStopLock();
			SamLog.e(TAG,"after waitStopLock");
			
		}

		/*remove all msg in mSamServiceHandler*/
		mSamServiceHandler.removeMessages(MSG_AUTOLOGIN_CALLBACK);
		mSamServiceHandler.removeMessages(MSG_SEND_QUESTION);
		mSamServiceHandler.removeMessages(MSG_CANCEL_QUESTION);
		mSamServiceHandler.removeMessages(MSG_UPGRADE_TO_SERVICER);
		mSamServiceHandler.removeMessages(MSG_ANSWER_QUESTION);
		mSamServiceHandler.removeMessages(MSG_QUERY_USERINFO);
		mSamServiceHandler.removeMessages(MSG_UPLOAD_AVATAR);
		mSamServiceHandler.removeMessages(MSG_SEND_COMMENTS);
		mSamServiceHandler.removeMessages(MSG_UPLOAD_FG);
		mSamServiceHandler.removeMessages(MSG_QUERY_FG);
		mSamServiceHandler.removeMessages(MSG_COMMENT_FG);

		mHandlerThread.getLooper().quit();

		mSamPushServiceHandler.removeMessages(MSG_PUSH_MSG_GOTANSWER);
		mSamPushServiceHandler.removeMessages(MSG_PUSH_MSG_PUSHSERVERSHUTDOWN);
		mSamPushServiceHandler.removeMessages(MSG_PUSH_MSG_GOTQUESTION);
		mSamPushServiceHandler.removeMessages(MSG_PUSH_MSG_EASEMOB_INFO);
		mPushThread.getLooper().quit();

		synchronized(pushLock){
			mHandlerTimeOutHandler.removeMessages(MSG_HANDLE_TIMEOUT);
			mHandlerTimeOutThread.getLooper().quit();
			mSamPushServiceHandler = null;
		}

		mSamService = null;
		answer_hndl = null;
		servicer_question_hndl = null;
		
		if(dao!=null){ 
			dao.close();
			dao = null;
		}


	}

	public void onActivityLaunched(SamService_Fragment fragment_samservice, SamChats_Fragment fragment_samchat){
		
		answer_hndl = new WeakReference <Handler> (fragment_samservice.mHandler); 
		servicer_question_hndl = new WeakReference <Handler>(fragment_samchat.mHandler);
		
	}

	public void startWaitThread(){
		if(mWaitThread == null){
			SamLog.e(TAG,"start SamWait Thread");
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
			
			if(!cbobj.isBroadcast && cbobj.smcb==null){
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
							SamLog.e(TAG, "SamServiceTimeOut Happened for msg isSendq");
						}else if(samobj.isCancelq()){
							Message msg2 = hndl.obtainMessage(cbobj.cbMsg, R_CANCEL_QUESTION_ERROR, R_CANCEL_QUESTION_ERROR_TIMEOUT,samobj);
							hndl.sendMessage(msg2);
							SamLog.e(TAG, "SamServiceTimeOut Happened for msg isCancelq");
						}else if(samobj.isUpgrade()){
							//Message msg3 = hndl.obtainMessage(cbobj.cbMsg, R_UPGRADE_ERROR, R_UPGRADE_ERROR_TIMEOUT,null);
							//hndl.sendMessage(msg3);
							SamLog.e(TAG, "SamServiceTimeOut Happened for msg isUpgrade");
							cbobj.smcb.onError(R_UPGRADE_ERROR_TIMEOUT);
						}else if(samobj.isSenda()){
							SendAnswer sda = ((SendaCoreObj)samobj).sda;
							sda.setstatus(SendAnswer.SEND_FAILED);
							dao.update_SendAnswer_db(sda);
							Intent intent = new Intent();
							intent.setAction(SamQADetailActivity.SEND_ANSWER_STATUS_BROADCAST);
							Bundle bundle = new Bundle();
							bundle.putSerializable("SendAnswer",sda);
							intent.putExtras(bundle);
							intent.putExtra("NoSuchQuestion", true);
							mContext.sendBroadcast(intent);
							SamLog.e(TAG, "SamServiceTimeOut Happened for msg isSenda");
						}else if(samobj.isQueryui()){
							cbobj.smcb.onError(R_QUERY_USERINFO_ERROR_TIMEOUT);
							SamLog.e(TAG, "SamServiceTimeOut Happened for msg isQueryui");
						}else if(samobj.isUploadAvatar()){
							cbobj.smcb.onError(R_UPLOAD_AVATAR_ERROR_TIMEOUT);
							SamLog.e(TAG, "SamServiceTimeOut Happened for msg isUploadAvatar");
						}else if(samobj.isSendComments()){
							cbobj.smcb.onError(R_SEND_COMMENTS_ERROR_TIMEOUT);
							SamLog.e(TAG, "SamServiceTimeOut Happened for msg isSendComments");
						}else if(samobj.isUploadFG()){
							cbobj.smcb.onError(R_UPLOAD_FG_ERROR_TIMEOUT);
							SamLog.e(TAG, "SamServiceTimeOut Happened for msg isUploadFG");
						}else if(samobj.isQueryFG()){
							cbobj.smcb.onError(R_QUERY_FG_ERROR_TIMEOUT);
							SamLog.e(TAG, "SamServiceTimeOut Happened for msg isQueryFG");
						}else if(samobj.isCommentFG()){
							cbobj.smcb.onError(R_COMMENT_FG_ERROR_TIMEOUT);
							SamLog.e(TAG, "SamServiceTimeOut Happened for msg isCommentFG");
						}else if(samobj.isFollow()){
							cbobj.smcb.onError(R_FOLLOW_ERROR_TIMEOUT);
							SamLog.e(TAG, "SamServiceTimeOut Happened for msg isFollow");
						}else if(samobj.isQueryFollower()){
							cbobj.smcb.onError(R_QUERY_FOLLOWER_ERROR_TIMEOUT);
							SamLog.e(TAG, "SamServiceTimeOut Happened for msg isQueryFollower");
						}else if(samobj.isQueryPublicInfo()){
							cbobj.smcb.onError(R_QUERY_PUBLIC_INFO_ERROR_TIMEOUT);
							SamLog.e(TAG, "SamServiceTimeOut Happened for msg isQueryPublicInfo");
						}else if(samobj.isQueryHotTopic()){
							cbobj.smcb.onError(R_QUERY_HOT_TOPIC_ERROR_TIMEOUT);
							SamLog.e(TAG, "SamServiceTimeOut Happened for msg isQueryHotTopic");
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
    		CBObj cbobj = ((SamCoreObj)msg.obj).refCBObj;
		Handler hndl = null;
    		
    		if(cbobj==null){
			SamLog.e(TAG, "SamServiceHandler:bad msg,drop it...");
    			return;
		}
			
		if(!cbobj.isBroadcast && cbobj.smcb ==null){
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
		case MSG_QUERY_USERINFO:
			do_query_userinfo((SamCoreObj)msg.obj);
			break;

		case MSG_UPLOAD_AVATAR:
			do_upload_avatar((SamCoreObj)msg.obj);
			break;

		case MSG_SEND_COMMENTS:
			do_send_comments((SamCoreObj)msg.obj);
			break;

		case MSG_UPLOAD_FG:
			do_upload_fg((SamCoreObj)msg.obj);
			break;

		case MSG_QUERY_FG:
			do_query_fg((SamCoreObj)msg.obj);
			break;

		case MSG_COMMENT_FG:
			do_comment_fg((SamCoreObj)msg.obj);
			break;
		case MSG_FOLLOW:
			do_follow((SamCoreObj)msg.obj);
			break;
		case MSG_QUERY_FOLLOWER:
			do_query_follower((SamCoreObj)msg.obj);
			break;

		case MSG_QUERY_PUBLIC_INFO:
			do_query_public((SamCoreObj)msg.obj);
			break;

		case MSG_QUERY_HOT_TOPIC:
			do_query_hot_topic((SamCoreObj)msg.obj);
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
					//upgrade(cbobj.cbHandler.get(),cbobj.cbMsg);
					upgrade(((UpgradeCoreObj)samobj).vInfo,  cbobj.smcb);
				}else if(samobj.isSenda()){
					reanswer_question(((SendaCoreObj)samobj).sda);
				}else if(samobj.isQueryui()){
					if(((QueryuiCoreObj)samobj).withOutToken){
						query_user_existed_withOutToken_from_server(((QueryuiCoreObj)samobj).queryname,cbobj.smcb);
					}else if(((QueryuiCoreObj)samobj).queryname!=null){
						query_user_info_from_server(((QueryuiCoreObj)samobj).queryname,cbobj.smcb);
					}else if(((QueryuiCoreObj)samobj).easemob_names!=null){
						query_user_info_from_server(((QueryuiCoreObj)samobj).easemob_names,cbobj.smcb);
					}
				}else if(samobj.isUploadAvatar()){
					upload_avatar(((UploadAvatarCoreObj)samobj).filePath, cbobj.smcb);
				}else if(samobj.isSendComments()){
					send_comments(((SendCommentsCoreObj)samobj).comments, cbobj.smcb);
				}else if(samobj.isUploadFG()){
					uploadFG(((UploadFGCoreObj)samobj).photoes, ((UploadFGCoreObj)samobj).comments, cbobj.smcb);
				}else if(samobj.isQueryFG()){
					queryFG(((QueryFGCoreObj)samobj).start_timestamp,((QueryFGCoreObj)samobj).fetch_count, cbobj.smcb);
				}else if(samobj.isCommentFG()){
					commentFG(((CommentFGCoreObj)samobj).article_id,((CommentFGCoreObj)samobj).comment, cbobj.smcb);
				}else if(samobj.isFollow()){
					if(((FollowCoreObj)samobj).cmd == FollowCoreObj.FOLLOW){
						follow(((FollowCoreObj)samobj).unique_id,((FollowCoreObj)samobj).username, cbobj.smcb);
					}else{
						unfollow(((FollowCoreObj)samobj).unique_id,((FollowCoreObj)samobj).username, cbobj.smcb);
					}
				}else if(samobj.isQueryFollower()){
					queryFollowList(cbobj.smcb);
				}else if(samobj.isQueryPublicInfo()){
					queryPublicInfo(((QueryPublicInfoCoreObj)samobj).uid, cbobj.smcb);
				}else if(samobj.isQueryHotTopic()){
					queryHotTopic(((QueryHotTopicCoreObj)samobj).cur_count,((QueryHotTopicCoreObj)samobj).update_time_pre, cbobj.smcb);
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
					//Message msg3 = hndl.obtainMessage(cbobj.cbMsg, R_UPGRADE_ERROR, R_UPGRADE_ERROR_TOKEN_FILE_NULL,null);
					//hndl.sendMessage(msg3);
					cbobj.smcb.onError(R_UPGRADE_ERROR_TOKEN_FILE_NULL);
				}else if(samobj.isSenda()){
					SendAnswer sda = ((SendaCoreObj)samobj).sda;
					sda.setstatus(SendAnswer.SEND_FAILED);
					dao.update_SendAnswer_db(sda);
					Intent intent = new Intent();
					intent.setAction(SamQADetailActivity.SEND_ANSWER_STATUS_BROADCAST);
					Bundle bundle = new Bundle();
					bundle.putSerializable("SendAnswer",sda);
					intent.putExtras(bundle);
					intent.putExtra("FatalError", false);
					mContext.sendBroadcast(intent);
					SamLog.e(TAG,"Send Answer FAILED: Auto Sign in!");
				}else if(samobj.isQueryui()){
					cbobj.smcb.onError(R_QUERY_USERINFO_ERROR_TOKEN_FILE_NULL);
				}else if(samobj.isUploadAvatar()){
					cbobj.smcb.onError(R_UPLOAD_AVATAR_ERROR_TOKEN_FILE_NULL);
				}else if(samobj.isSendComments()){
					cbobj.smcb.onError(R_SEND_COMMENTS_ERROR_TOKEN_FILE_NULL);
				}else if(samobj.isUploadFG()){
					cbobj.smcb.onError(R_UPLOAD_FG_ERROR_TOKEN_FILE_NULL);
				}else if(samobj.isQueryFG()){
					cbobj.smcb.onError(R_QUERY_FG_ERROR_TOKEN_FILE_NULL);
				}else if(samobj.isCommentFG()){
					cbobj.smcb.onError(R_COMMENT_FG_ERROR_TOKEN_FILE_NULL);
				}else if(samobj.isFollow()){
					cbobj.smcb.onError(R_FOLLOW_ERROR_TOKEN_FILE_NULL);
				}else if(samobj.isQueryFollower()){
					cbobj.smcb.onError(R_QUERY_FOLLOWER_ERROR_TOKEN_FILE_NULL);
				}else if(samobj.isQueryPublicInfo()){
					cbobj.smcb.onError(R_QUERY_PUBLIC_INFO_ERROR_TOKEN_FILE_NULL);
				}else if(samobj.isQueryHotTopic()){
					cbobj.smcb.onError(R_QUERY_HOT_TOPIC_ERROR_TOKEN_FILE_NULL);
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
			if(msg == MSG_PUSH_MSG_EASEMOB_INFO || msg == MSG_PUSH_MSG_RECONNECT_TIMOUT){
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
				user_answer.setusername(phinfoa.username);
				user_answer.setphonenumber(phinfoa.cellphone);
				user_answer.setunique_id(phinfoa.unique_id);
				user_answer.seteasemob_username(phinfoa.easemob_username);
				user_answer.setarea(phinfoa.area);
				user_answer.setlocation(phinfoa.location);
				user_answer.setdescription(phinfoa.desc);
				user_answer.setimagefile(phinfoa.avatar);
				user_answer.setusertype(LoginUser.MIDSERVER);
				user_answer.setlastupdate(phinfoa.lastupdate);

				SamLog.e(TAG,"user_anser username:"+user_answer.getusername());
				user_answer.id = dao.add_update_ContactUser_db(user_answer);

				ReceivedAnswer ra = new ReceivedAnswer();
				ra.answer = phinfoa.answer;
				ra.contactuserid = user_answer.getid();
				ra.question_id = phinfoa.quest_id;

				if(isAnswerValid(phinfoa.quest_id)){
					if(phinfoa.avatar!=null){
						downloadAvatar(new HttpCommClient(),phinfoa.avatar,user_answer.getphonenumber(),user_answer.getusername());
					}
					dao.add_ReceivedAnswer_db(ra);
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
					ContactUser userdb = dao.query_ContactUser_db_by_username(phinfoq.username);
					
					if(userdb == null){
						ContactUser user = new ContactUser();
						user.setusername(phinfoq.username);
						user.setphonenumber(phinfoq.cellphone);
						user.setunique_id(phinfoq.unique_id);
						user.setlastupdate(phinfoq.lastupdate);
						user.seteasemob_username(phinfoq.easemob_username);
						user.setimagefile(phinfoq.avatar);
						userdb = user;
						userdb.id = dao.add_update_ContactUser_db(userdb);
					}else{
						if(userdb.getlastupdate()!= phinfoq.lastupdate){
							userdb.setusername(phinfoq.username);
							userdb.setphonenumber(phinfoq.cellphone);
							userdb.setunique_id(phinfoq.unique_id);
							userdb.setlastupdate(phinfoq.lastupdate);
							userdb.seteasemob_username(phinfoq.easemob_username);
							userdb.setimagefile(phinfoq.avatar);
							userdb.id = dao.add_update_ContactUser_db(userdb);
						}						
					}
					
										

					ReceivedQuestion rq=null;
					int msgid=0;
					if(phinfoq.opt == 0){
						rq = new ReceivedQuestion();
						rq.setquestion_id(phinfoq.quest_id);
						rq.setquestion(phinfoq.quest);
						rq.setcontactuserid(userdb.getid());
						rq.setstatus(ReceivedQuestion.ACTIVE);
						rq.setresponse(ReceivedQuestion.NOT_RESPONSED);
						rq.setreceivedtime(phinfoq.datetime);
						rq.setreceivercellphone(get_current_user().getphonenumber());
						rq.setreceiverusername(get_current_user().getusername());
						msgid = SamChats_Fragment.MSG_QUESTION_RECEIVED;
					}else if(phinfoq.opt == 1){
						rq = dao.query_ReceivedQuestion_db(phinfoq.quest_id);
						if(rq!=null){
							rq.setstatus(ReceivedQuestion.CANCEL);
							rq.setcanceledtime(phinfoq.datetime);
							msgid = SamChats_Fragment.MSG_QUESTION_CANCEL;
						}else{
							break;
						}
					}else{
						break;
					}

					if(phinfoq.avatar!=null){
						downloadAvatar(new HttpCommClient(),phinfoq.avatar,userdb.getphonenumber(),userdb.getusername());
					}

					dao.add_update_ReceivedQuestion_db(rq);
					if(phinfoq.opt == 0){
						dao.clearReceviedQuestion_db(phinfoq.datetime - 1*60*60*1000L);
					}
					Message msg1 = sq_hndl.obtainMessage(msgid, null);
					sq_hndl.sendMessage(msg1);
					
					break;
				}
			case MSG_PUSH_MSG_EASEMOB_INFO:
				SamLog.i(TAG,"EASEMOB_INFO is got");
				HttpPushInfo phinfoq = (HttpPushInfo)msg.obj;
				LoginUser user = dao.query_LoginUser_db(phinfoq.cellphone);
				if(user != null){
					if(user.easemob_username == null){
						SamLog.i(TAG,"EASEMOB_INFO not existed, store into db:easemob_username = "+phinfoq.easemob_username);
						user.unique_id = phinfoq.unique_id;
						user.easemob_username = phinfoq.easemob_username;
						dao.add_update_LoginUser_db(user);
												
						if(user.status == LoginUser.ACTIVE){
							set_current_user(user);
							Intent intent = new Intent();
							intent.setAction(SamService.EASEMOBNAMEGOT);
							mContext.sendBroadcast(intent);
							SamLog.i(TAG,"update current user:" + user.easemob_username);
						}
						
						
						
					}else{
						SamLog.i(TAG,"EASEMOB_INFO existed, drop it");
					}
				}else{
					SamLog.i(TAG,"Invalid cellphone number ,  drop this EASEMOB_INFO");
				}
				break;

			case MSG_PUSH_MSG_RECONNECT_TIMOUT:
				cancelHeartTimeOut();
				if(mWaitThread!=null){
					mWaitThread.InterruptWaitThread();
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

	/*public void upgrade(Handler callback,int cbMsg){
		CBObj obj = new CBObj(callback,cbMsg);
		SamCoreObj  samobj =  new UpgradeCoreObj(obj);

		Message msg = mSamServiceHandler.obtainMessage(MSG_UPGRADE_TO_SERVICER, samobj);
    		mSamServiceHandler.sendMessage(msg);

		startTimeOut(samobj);
		
	}*/

	public void upgrade(SamVendorInfo vInfo,SMCallBack SMcb ){

		CBObj obj = new CBObj(SMcb);
		SamCoreObj samobj = new UpgradeCoreObj(obj,vInfo);
		Message msg = mSamServiceHandler.obtainMessage(MSG_UPGRADE_TO_SERVICER,samobj);
		mSamServiceHandler.sendMessage(msg);
		startTimeOut(samobj);
		
	}

	

	public void answer_question(String question_id, String answer){
		CBObj obj = new CBObj();
		
		SendAnswer sda = new SendAnswer();
		sda.setquestion_id(question_id);
		sda.setanswer(answer);
		sda.setloginuserid(get_current_user().getid());
		long id = dao.add_SendAnswer_db(sda);
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

	
	/*public void query_user_info_from_server(Handler callback,int cbMsg,String phonenumber){
 		CBObj obj = new CBObj(callback,cbMsg);
		SamCoreObj samobj = new QueryuiCoreObj(obj,phonenumber);

		Message msg = mSamServiceHandler.obtainMessage(MSG_QUERY_USERINFO, samobj);
		mSamServiceHandler.sendMessage(msg);

		startTimeOut(samobj);

	}*/


	public void query_user_info_from_server(List<String>easemob_names,SMCallBack SMcb){
 		CBObj obj = new CBObj(SMcb);
		SamCoreObj samobj = new QueryuiCoreObj(obj,easemob_names);

		Message msg = mSamServiceHandler.obtainMessage(MSG_QUERY_USERINFO, samobj);
		mSamServiceHandler.sendMessage(msg);

		startTimeOut(samobj);

	}

	public void query_user_info_from_server(String queryname,SMCallBack SMcb){
 		CBObj obj = new CBObj(SMcb);
		SamCoreObj samobj = new QueryuiCoreObj(obj,queryname);

		Message msg = mSamServiceHandler.obtainMessage(MSG_QUERY_USERINFO, samobj);
		mSamServiceHandler.sendMessage(msg);

		startTimeOut(samobj);

	}

	public void query_user_existed_withOutToken_from_server(String queryname,SMCallBack SMcb){
 		CBObj obj = new CBObj(SMcb);
		SamCoreObj samobj = new QueryuiCoreObj(obj,queryname,true);

		Message msg = mSamServiceHandler.obtainMessage(MSG_QUERY_USERINFO, samobj);
		mSamServiceHandler.sendMessage(msg);

		startTimeOut(samobj);

	}

	

	public void upload_avatar(String filePath, SMCallBack SMcb){
		CBObj obj = new CBObj(SMcb);
		SamCoreObj  samobj = new UploadAvatarCoreObj(obj,filePath);
		Message msg = mSamServiceHandler.obtainMessage(MSG_UPLOAD_AVATAR,samobj);
		mSamServiceHandler.sendMessage(msg);
		startTimeOut(samobj);
	}

	public void send_comments(String comments, SMCallBack SMcb){
		CBObj obj = new CBObj(SMcb);
		SamCoreObj  samobj = new SendCommentsCoreObj(obj,comments);
		Message msg = mSamServiceHandler.obtainMessage(MSG_SEND_COMMENTS,samobj);
		mSamServiceHandler.sendMessage(msg);
		startTimeOut(samobj);
	}

	public void uploadFG(List<String>photoes,String comments, SMCallBack SMcb){
		CBObj obj = new CBObj(SMcb);
		SamCoreObj samobj = new UploadFGCoreObj(obj,photoes,comments);
		Message msg = mSamServiceHandler.obtainMessage(MSG_UPLOAD_FG,samobj);
		mSamServiceHandler.sendMessage(msg);
		startTimeOut(samobj);
	}

	public void queryFG(long start_timestamp, int fetch_count,SMCallBack SMcb){
		CBObj obj = new CBObj(SMcb);
		SamCoreObj samobj = new QueryFGCoreObj(obj,start_timestamp,fetch_count);
		Message msg = mSamServiceHandler.obtainMessage(MSG_QUERY_FG,samobj);
		mSamServiceHandler.sendMessage(msg);
		startTimeOut(samobj);
	}

	public void commentFG(long article_id,String comment,SMCallBack SMcb){
		CBObj obj = new CBObj(SMcb);
		SamCoreObj samobj = new CommentFGCoreObj(obj,article_id,comment);
		Message msg = mSamServiceHandler.obtainMessage(MSG_COMMENT_FG,samobj);
		mSamServiceHandler.sendMessage(msg);
		startTimeOut(samobj);
	}

	public void follow(long unique_id, String username,SMCallBack SMcb){
		CBObj obj = new CBObj(SMcb);
		SamCoreObj samobj = new FollowCoreObj(obj,unique_id,username,FollowCoreObj.FOLLOW);
		Message msg = mSamServiceHandler.obtainMessage(MSG_FOLLOW,samobj);
		mSamServiceHandler.sendMessage(msg);
		startTimeOut(samobj);
	}

	public void unfollow(long unique_id,String username, SMCallBack SMcb){
		CBObj obj = new CBObj(SMcb);
		SamCoreObj samobj = new FollowCoreObj(obj,unique_id,username,FollowCoreObj.UNFOLLOW);
		Message msg = mSamServiceHandler.obtainMessage(MSG_FOLLOW,samobj);
		mSamServiceHandler.sendMessage(msg);
		startTimeOut(samobj);
	}

	public void queryFollowList(SMCallBack SMcb){
		CBObj obj = new CBObj(SMcb);
		SamCoreObj samobj = new QueryFollowerCoreObj(obj);
		Message msg = mSamServiceHandler.obtainMessage(MSG_QUERY_FOLLOWER,samobj);
		mSamServiceHandler.sendMessage(msg);
		startTimeOut(samobj);
	}

	public void queryPublicInfo(long uid,SMCallBack SMcb){
		CBObj obj = new CBObj(SMcb);
		SamCoreObj samobj = new QueryPublicInfoCoreObj(uid,obj);
		Message msg = mSamServiceHandler.obtainMessage(MSG_QUERY_PUBLIC_INFO,samobj);
		mSamServiceHandler.sendMessage(msg);
		startTimeOut(samobj);

	}

	public void queryHotTopic(long cur_count,long update_time_pre,SMCallBack SMcb){
		CBObj obj = new CBObj(SMcb);
		SamCoreObj samobj = new QueryHotTopicCoreObj(cur_count,update_time_pre,obj);
		Message msg = mSamServiceHandler.obtainMessage(MSG_QUERY_HOT_TOPIC,samobj);
		mSamServiceHandler.sendMessage(msg);
		startTimeOut(samobj);

	}

	

	private String getShortPicName(String url_thumb) {
		int index  = url_thumb.lastIndexOf("article_");
		if(index != -1) {
			return url_thumb.substring(index);
		} else {
			return null;
		}
	}

	private boolean savePicture(String path, String fileName,byte[] data){
		File filePath = null; 
		File file = null;
		FileOutputStream fos = null;

		SamLog.e(TAG,"data size:" + data.length);

		try{
			filePath = new File(path);
			if(!filePath.exists()){
				filePath.mkdirs();
			}

			file = new File(path  +"/"+ fileName);

			if(!file.exists()){
				file.createNewFile();
			}
  
			fos = new FileOutputStream(file);    
  
			fos.write(data); 

			return true;
  
		}catch(Exception e){
			return false;
			
		}finally{
			try{
				if(fos!=null) fos.close();
			}catch(Exception e){

			}

		}

	}

	private void downloadthumbnail_pic(HttpCommClient hcc,String url_thumb,long fg_id,int sequence){
		byte[] data=null;
		String shortImg=null;
		boolean downSucceed=false;
		StringBuffer oldAvatar=new StringBuffer();
		if(url_thumb!=null && (shortImg = getShortPicName(url_thumb))!=null 
			&& (!dao.isThumbPicExistedInDB(fg_id,shortImg)||!dao.isThumbPicExistedInFS(shortImg))){
			data = hcc.getImage(url_thumb);
			if(data!=null){
				downSucceed = savePicture(SamService.sam_cache_path+SamService.FG_PIC_FOLDER, shortImg, data);
			}
		}

		if(downSucceed){
			SamLog.e(TAG,"download thumb pic succeed and update into db:"+shortImg);
			dao.update_PictureRecord_db_thumbnail(fg_id, shortImg,sequence);
		}

		/*if(dao.isThumbPicExistedInDB(fg_id,shortImg)){
			SamLog.e(TAG,shortImg+" is in db");
		}else{
			SamLog.e(TAG,shortImg+" is not in db");
		}*/

		/*if(dao.isThumbPicExistedInFS(shortImg)){
			SamLog.e(TAG,shortImg+" is in FS");
		}else{
			SamLog.e(TAG,shortImg+" is not in FS");
		}*/
	}


	private void do_query_hot_topic(SamCoreObj samobj){
		CBObj cbobj = samobj.refCBObj;
		QueryHotTopicCoreObj qhtobj = (QueryHotTopicCoreObj)samobj;

		String token = get_current_token();
		if(token == null){
			SamLog.e(TAG, "token is null in do_query_hot_topic, should never run to here");
			cbobj.smcb.onError(R_QUERY_HOT_TOPIC_ERROR_TOKEN_FILE_NULL);
			return;
		}

		HttpCommClient hcc = new HttpCommClient();
		if(hcc.queryHotTopic(qhtobj.cur_count,qhtobj.update_time_pre,token)){
			if(hcc.ret == RET_QUERY_HOT_TOPIC_SERVER_OK){
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

				HotTopicResult result = new HotTopicResult(hcc.hottopic_query_time,hcc.hottopicArray);
				
				cbobj.smcb.onSuccess((Object)result);
				
			}else if(hcc.ret == RET_QUERY_HOT_TOPIC_SERVER_TOKEN_INVALID){
				SamLog.e(TAG,"query hot topic TOKEN INVALIDE!");
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

				cbobj.smcb.onFailed(hcc.ret);

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

			cbobj.smcb.onError(R_QUERY_HOT_TOPIC_ERROR_HTTP_EXCEPTION);
		}

		
	}



	private void do_query_public(SamCoreObj samobj){
		CBObj cbobj = samobj.refCBObj;
		QueryPublicInfoCoreObj qpiobj = (QueryPublicInfoCoreObj)samobj;

		String token = get_current_token();
		if(token == null){
			SamLog.e(TAG, "token is null in do_query_public, should never run to here");
			cbobj.smcb.onError(R_QUERY_PUBLIC_INFO_ERROR_TOKEN_FILE_NULL);
			return;
		}

		HttpCommClient hcc = new HttpCommClient();
		if(hcc.queryPublicInfo(qpiobj.uid,token)){
			if(hcc.ret == RET_QUERY_PUBLIC_INFO_SERVER_OK){
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

				ContactUser userdb=null;
				boolean need_update = false;
				ContactUser cuser = hcc.uiArray.size()>1?hcc.uiArray.get(0):null;

				if(cuser!=null){
					need_update = false;
					userdb = dao.query_ContactUser_db_by_username(cuser.getusername());
					if(userdb == null){
						need_update = true;
					}else if(userdb!=null && userdb.getlastupdate()!=cuser.getlastupdate()){
						need_update = true;
					} 

					if(need_update){
						dao.add_update_ContactUser_db(cuser);
					}

					if(cuser.imagefile!=null){
						String shortImg = getShortImgName(cuser.imagefile);
						if(shortImg != null){
							AvatarRecord rd = dao.query_AvatarRecord_db_by_username(cuser.getusername());
							if(rd == null || rd.getavatarname()==null){
								downloadAvatar(hcc, cuser.imagefile,cuser.phonenumber,cuser.username);
							}else if(!rd.getavatarname().equals(shortImg)){
								downloadAvatar(hcc, cuser.imagefile,cuser.phonenumber,cuser.username);
							}else if(!dao.isAvatarExistedInFS(shortImg)){
								downloadAvatar(hcc, cuser.imagefile,cuser.phonenumber,cuser.username);
							}
						}
						
					}
				}

				PublicInfo pub = hcc.pubinfoArray.get(0);
				PublicInfo rd = null;
				if((rd = dao.query_PublicInfo_db(pub.getowner_unique_id())) == null){
					rd = new PublicInfo();
					rd.setcmpdesc(pub.getcmpdesc());
					rd.setcmplogo(pub.getcmplogo());
					rd.setcmpname(pub.getcmpname());
					rd.setcmpphone(pub.getcmpphone());
					rd.setcmpwebsite(pub.getcmpwebsite());
					rd.setowner_unique_id(pub.getowner_unique_id());
					dao.add_PublicInfo_db(rd);
				}else{
					rd.setcmpdesc(pub.getcmpdesc());
					rd.setcmplogo(pub.getcmplogo());
					rd.setcmpname(pub.getcmpname());
					rd.setcmpphone(pub.getcmpphone());
					rd.setcmpwebsite(pub.getcmpwebsite());
					rd.setowner_unique_id(pub.getowner_unique_id());
					dao.update_PublicInfo_db(rd.getid(), rd);

				}
				
				cbobj.smcb.onSuccess((Object)hcc.pubinfoArray);
				
			}else if(hcc.ret == RET_QUERY_PUBLIC_INFO_SERVER_TOKEN_INVALID){
				SamLog.e(TAG,"query public TOKEN INVALIDE!");
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

				cbobj.smcb.onFailed(hcc.ret);

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

			cbobj.smcb.onError(R_QUERY_PUBLIC_INFO_ERROR_HTTP_EXCEPTION);
		}

		
	}

	private void do_query_follower(SamCoreObj samobj){
		CBObj cbobj = samobj.refCBObj;
		QueryFollowerCoreObj qfcobj = (QueryFollowerCoreObj)samobj;

		String token = get_current_token();

		if(token == null){
			SamLog.e(TAG, "token is null in do_query_follower, should never run to here");
			cbobj.smcb.onError(R_QUERY_FOLLOWER_ERROR_TOKEN_FILE_NULL);
			return;
		}

		HttpCommClient hcc = new HttpCommClient();

		if(hcc.queryFollower(token)){
			if(hcc.ret == RET_QUERY_FOLLOWER_SERVER_OK ){
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

				ContactUser userdb=null;
				boolean need_update = false;

				for(ContactUser cuser : hcc.uiArray){
					need_update = false;
					userdb = dao.query_ContactUser_db_by_username(cuser.getusername());
					if(userdb == null){
						need_update = true;
					}else if(userdb!=null && userdb.getlastupdate()!=cuser.getlastupdate()){
						need_update = true;
					} 

					if(need_update){
						dao.add_update_ContactUser_db(cuser);
					}

					if(cuser.imagefile!=null){
						String shortImg = getShortImgName(cuser.imagefile);
						if(shortImg != null){
							AvatarRecord rd = dao.query_AvatarRecord_db_by_username(cuser.getusername());
							if(rd == null || rd.getavatarname()==null){
								downloadAvatar(hcc, cuser.imagefile,cuser.phonenumber,cuser.username);
							}else if(!rd.getavatarname().equals(shortImg)){
								downloadAvatar(hcc, cuser.imagefile,cuser.phonenumber,cuser.username);
							}else if(!dao.isAvatarExistedInFS(shortImg)){
								downloadAvatar(hcc, cuser.imagefile,cuser.phonenumber,cuser.username);
							}
						}
						
					}
				}

				
				List<FollowerRecord> list = new ArrayList<FollowerRecord>();
				for(ContactUser user: hcc.uiArray){
					FollowerRecord rd = new FollowerRecord(user.getunique_id(),user.getusername(),current_user.getunique_id());
					list.add(rd);
				}
				SamService.getInstance().getDao().save_FollowerRecordList_db(list);
				
				cbobj.smcb.onSuccess((Object)hcc.uiArray);
			}else if(hcc.ret == RET_QUERY_FOLLOWER_SERVER_NO_ANY_DATA){
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

				cbobj.smcb.onSuccess((Object)(new ArrayList<ContactUser>()));

			}else if(hcc.ret == RET_QUERY_FOLLOWER_SERVER_TOKEN_INVALID){
				SamLog.e(TAG,"query follow TOKEN INVALIDE!");
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

				cbobj.smcb.onFailed(hcc.ret);
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

			cbobj.smcb.onError(R_QUERY_FOLLOWER_ERROR_HTTP_EXCEPTION);
		}
	}

	

	private void do_follow(SamCoreObj samobj){
		CBObj cbobj = samobj.refCBObj;
		FollowCoreObj fwobj = (FollowCoreObj)samobj;

		String token = get_current_token();

		if(token == null){
			SamLog.e(TAG, "token is null, should never run to here");
			cbobj.smcb.onError(R_FOLLOW_ERROR_TOKEN_FILE_NULL);
			return;
		}

		HttpCommClient hcc = new HttpCommClient();

		if(hcc.follow(fwobj.unique_id,fwobj.cmd,token)){
			if(hcc.ret == RET_FOLLOW_SERVER_OK ){
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

				if(fwobj.cmd == FollowCoreObj.FOLLOW){
					SamLog.e(TAG,"follow guy "+"unique_id:"+fwobj.unique_id+" username:"+fwobj.username);
					if(dao.query_FollowerRecord_db(fwobj.unique_id, current_user.getunique_id()) == null){
						dao.add_FollowerRecord_db(new FollowerRecord(fwobj.unique_id,fwobj.username,current_user.getunique_id()));
					}
				}else{
					SamLog.e(TAG,"unfollow guy "+"unique_id:"+fwobj.unique_id+" username:"+fwobj.username);
					dao.delete_FollowerRecord_db(fwobj.unique_id,current_user.getunique_id());
				}

				cbobj.smcb.onSuccess(null);
			}else if(hcc.ret == RET_FOLLOW_SERVER_TOKEN_INVALID){
				SamLog.e(TAG,"follow TOKEN INVALIDE!");
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

				cbobj.smcb.onFailed(hcc.ret);
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

			cbobj.smcb.onError(R_FOLLOW_ERROR_HTTP_EXCEPTION);
		}
	}

	private void do_comment_fg(SamCoreObj samobj){
		CBObj cbobj = samobj.refCBObj;
		CommentFGCoreObj cfobj = (CommentFGCoreObj)samobj;

		String token = get_current_token();

		if(token == null){
			SamLog.e(TAG, "token is null, should never run to here");
			cbobj.smcb.onError(R_COMMENT_FG_ERROR_TOKEN_FILE_NULL);
			return;
		}

		HttpCommClient hcc = new HttpCommClient();
		if(hcc.commentFG(cfobj.article_id,cfobj.comment, token)){
			if(hcc.ret == RET_COMMENT_FG_SERVER_OK ){
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

				cbobj.smcb.onSuccess(null);
			}else if(hcc.ret == RET_COMMENT_FG_SERVER_TOKEN_INVALID){
				SamLog.e(TAG,"comment fg TOKEN INVALIDE!");
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

				cbobj.smcb.onFailed(hcc.ret);
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

			cbobj.smcb.onError(R_QUERY_FG_ERROR_HTTP_EXCEPTION);
		}
		

	}

	private void do_query_fg(SamCoreObj samobj){
		CBObj cbobj = samobj.refCBObj;
		QueryFGCoreObj qfobj = (QueryFGCoreObj)samobj;

		String token = get_current_token();

		if(token == null){
			SamLog.e(TAG, "token is null, should never run to here");
			cbobj.smcb.onError(R_QUERY_FG_ERROR_TOKEN_FILE_NULL);
			return;
		}

		HttpCommClient hcc = new HttpCommClient();
		if(hcc.queryFG(token,qfobj.start_timestamp,qfobj.fetch_count)){
			if(hcc.ret == RET_QUERY_FG_SERVER_OK ){
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

				String owner_phonenumber = get_current_user().getphonenumber();

				SamLog.e(TAG,"ainfoList size:"+hcc.ainfoList.size());
				for(int i=0;i<hcc.ainfoList.size();i++){
					ArticleInfo ainfo = hcc.ainfoList.get(i);
					FGRecord rd  = null;
					RecommanderRecord recommandrd = null;
					CommenterRecord commenterrd=null;
					PictureRecord picturerd=null;

					if((rd = dao.query_FGRecord_db(ainfo.article_id,owner_phonenumber))==null){
						rd = new FGRecord(ainfo.timestamp,ainfo.article_id,ainfo.status,ainfo.comment,ainfo.publisher.getphonenumber(),owner_phonenumber);
						rd.setid(dao.add_FGRecord_db(rd));
					}

					for(int j=0;j<ainfo.recommander.size();j++){
						if((recommandrd = dao.query_RecommanderRecord_db(ainfo.recommander.get(j).getphonenumber(),ainfo.article_id,0))==null){
							recommandrd = new RecommanderRecord(ainfo.recommander.get(j).getphonenumber(),ainfo.article_id,0);
							recommandrd.setid(dao.add_RecommanderRecord_db(recommandrd));
						}
					}

					for(int j=0;j<ainfo.comments.size();j++){
						if((commenterrd = dao.query_CommenterRecord_db(ainfo.comments.get(j).commenter.getphonenumber(),ainfo.article_id,ainfo.comments.get(j).comments_timestamp))==null){
							commenterrd = new CommenterRecord(ainfo.comments.get(j).commenter.getphonenumber(),ainfo.comments.get(j).content,ainfo.article_id,ainfo.comments.get(j).comments_timestamp);
							commenterrd.setid(dao.add_CommenterRecord_db(commenterrd));
						}
					}

					for(int j=0;j<ainfo.pics.size();j++){
						if((picturerd = dao.query_PictureRecord_db(ainfo.article_id,ainfo.pics.get(j)))==null){
							picturerd = new PictureRecord(ainfo.article_id,ainfo.pics.get(j),ainfo.pics.get(j));
							picturerd.setsequence(j);
							picturerd.setid(dao.add_PictureRecord_db(picturerd));
						}

						//SamLog.e(TAG,"url_thumbnail:"+picturerd.geturl_thumbnail());
						downloadthumbnail_pic(hcc,ainfo.pics.get(j),ainfo.article_id,picturerd.getsequence());
					}

						
				}

				cbobj.smcb.onSuccess(hcc.ainfoList);
				return;
				
			}else if(hcc.ret == RET_QUERY_FG_SERVER_TOKEN_INVALID){
				SamLog.e(TAG,"query fg TOKEN INVALIDE!");
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

				cbobj.smcb.onFailed(hcc.ret);
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

			cbobj.smcb.onError(R_QUERY_FG_ERROR_HTTP_EXCEPTION);
		}

	}

	private void do_upload_fg(SamCoreObj samobj){
		CBObj cbobj = samobj.refCBObj;
		UploadFGCoreObj ufobj = (UploadFGCoreObj)samobj;

		String token = get_current_token();

		if(token == null){
			SamLog.e(TAG, "token is null, should never run to here");
			cbobj.smcb.onError(R_UPLOAD_FG_ERROR_TOKEN_FILE_NULL);
			return;
		}

		HttpCommClient hcc = new HttpCommClient();

		if(hcc.uploadFG(ufobj.photoes,ufobj.comments,token)){
			if(hcc.ret == RET_UPLOAD_FG_SERVER_OK){
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

				cbobj.smcb.onSuccess(null);
			}else if(hcc.ret == RET_UPLOAD_FG_SERVER_TOKEN_INVALID){
				SamLog.e(TAG,"upload fg TOKEN INVALIDE!");
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

				cbobj.smcb.onFailed(hcc.ret);
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

			cbobj.smcb.onError(R_UPLOAD_FG_ERROR_HTTP_EXCEPTION);
		}

		
	}

	private void do_send_comments(SamCoreObj samobj){
		CBObj cbobj = samobj.refCBObj;
		SendCommentsCoreObj scobj = (SendCommentsCoreObj)samobj;

		String token = get_current_token();

		if(token == null){
			SamLog.e(TAG, "token is null, should never run to here");
			cbobj.smcb.onError(R_SEND_COMMENTS_ERROR_TOKEN_FILE_NULL);
			return;
		}

		HttpCommClient hcc = new HttpCommClient();
		if(hcc.sendcomments(scobj.comments,token)){
			if(hcc.ret == RET_SEND_COMMENTS_SERVER_OK){
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

				
				SamLog.e(TAG,"send comments ok");
				cbobj.smcb.onSuccess(null);
				
			}else if(hcc.ret == RET_SEND_COMMENTS_SERVER_TOKEN_INVALID){
				SamLog.e(TAG,"send comments TOKEN INVALIDE!");
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

				cbobj.smcb.onFailed(hcc.ret);
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

			cbobj.smcb.onError(R_SEND_COMMENTS_ERROR_HTTP_EXCEPTION);
		}

	}

	private void do_upload_avatar(SamCoreObj samobj){
		CBObj cbobj = samobj.refCBObj;
		UploadAvatarCoreObj avobj = (UploadAvatarCoreObj)samobj;
		
		String token = get_current_token();

		if(token == null){
			SamLog.e(TAG, "token is null, should never run to here");
			cbobj.smcb.onError(R_UPLOAD_AVATAR_ERROR_TOKEN_FILE_NULL);
			return;
		}

		HttpCommClient hcc = new HttpCommClient();
		if(hcc.uploadavatar(avobj.filePath, token)){
			if(hcc.ret == RET_UPLOAD_AVATAR_SERVER_OK){
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

				
				SamLog.e(TAG,"upload avatar ok");
				cbobj.smcb.onSuccess(null);
				
			}else if(hcc.ret == RET_UPLOAD_AVATAR_SERVER_TOKEN_INVALID){
				SamLog.e(TAG,"upload avatar TOKEN INVALIDE!");
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

				cbobj.smcb.onFailed(hcc.ret);
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

			cbobj.smcb.onError(R_UPLOAD_AVATAR_ERROR_HTTP_EXCEPTION);
		}
	}


	private String getShortImgName(String photoPath) {
		int index  = photoPath.lastIndexOf("origin_");
		if(index != -1) {
			return photoPath.substring(index);
		} else {
			return null;
		}
	}

	private boolean saveAvatar(String path, String fileName,byte[] data){
		File filePath = null; 
		File file = null;
		FileOutputStream fos = null;

		SamLog.e(TAG,"data size:" + data.length);

		try{
			filePath = new File(path);
			if(!filePath.exists()){
				filePath.mkdirs();
			}

			file = new File(path  +"/"+ fileName);

			if(!file.exists()){
				file.createNewFile();
			}
  
			fos = new FileOutputStream(file);    
  
			fos.write(data); 

			return true;
  
		}catch(Exception e){
			return false;
			
		}finally{
			try{
				if(fos!=null) fos.close();
			}catch(Exception e){

			}

		}

	}

	public void deleteOldAvatar(String oldAvatar){
		if(oldAvatar == null){
			return;
		}
		
		//delete avatar file
		File filePath = new File(SamService.sam_cache_path+SamService.AVATAR_FOLDER);

		if(filePath.exists()){
			File file = new File(SamService.sam_cache_path+SamService.AVATAR_FOLDER+"/"+oldAvatar);
			if(file.exists()){
				file.delete();
			}
		}
	}

	private void downloadAvatar(HttpCommClient hcc,String imagefile,String phonenumber,String username){
		byte[] data=null;
		String shortImg=null;
		boolean downSucceed=false;
		StringBuffer oldAvatar=new StringBuffer();
		if(imagefile!=null && (shortImg = getShortImgName(imagefile))!=null 
			&& (!dao.isAvatarExistedInDBByUsername(username,shortImg)||!dao.isAvatarExistedInFS(shortImg))){
			data = hcc.getImage(imagefile);
			if(data!=null){
				downSucceed = saveAvatar(SamService.sam_cache_path+SamService.AVATAR_FOLDER, shortImg, data);
			}
		}

		if(downSucceed){
			SamLog.e(TAG,"download avatar succeed and update into db");
			dao.add_update_AvatarRecord_db(
				phonenumber,
				username,
				shortImg,
				oldAvatar
			);

			if(oldAvatar.length()!=0){
				deleteOldAvatar(oldAvatar.toString());
			}
		}
	}


	private void do_query_userinfo(SamCoreObj samobj){
		boolean ret = false;
		CBObj cbobj = samobj.refCBObj;
		QueryuiCoreObj quiobj = (QueryuiCoreObj)samobj;
		String queryname = quiobj.queryname;
		List<String> easemob_usernames = quiobj.easemob_names;
		
		String token = get_current_token();

		if(!quiobj.withOutToken && token == null){
			SamLog.e(TAG, "token is null, should never run to here");
			cbobj.smcb.onError(R_QUERY_USERINFO_ERROR_TOKEN_FILE_NULL);
			return;
		}

		HttpCommClient hcc = new HttpCommClient();

		if(quiobj.withOutToken){
			ret = hcc.queryui_withoutToken(queryname);
		}else if(queryname!=null){
			SamLog.e(TAG,"query queryname:"+queryname);
			ret = hcc.queryui(queryname,token);
		}else if(easemob_usernames!=null){
			SamLog.e(TAG,"query username list");
			ret = hcc.queryui(easemob_usernames,token);
		}else{
			throw new RuntimeException("fatal error: query userinfo param error!");
		}
		
		if(ret){
			if(hcc.ret == RET_QUERY_USERINFO_SERVER_OK){
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

				if(quiobj.withOutToken){
					cbobj.smcb.onSuccess((Object)hcc.uiArray);
					return;
				}

				
				SamLog.e(TAG,"query phonenumber R_QUERY_USERINFO_OK");
				
				ContactUser userdb=null;
				boolean need_update = false;

				quiobj.uiArray = hcc.uiArray;
				for(ContactUser cuser : hcc.uiArray){
					need_update = false;
					userdb = dao.query_ContactUser_db_by_username(cuser.getusername());
					if(userdb == null){
						need_update = true;
					}else if(userdb!=null && userdb.getlastupdate()!=cuser.getlastupdate()){
						need_update = true;
					} 

					if(need_update){
						dao.add_update_ContactUser_db(cuser);
					}

					if(cuser.imagefile!=null){
						String shortImg = getShortImgName(cuser.imagefile);
						if(shortImg != null){
							AvatarRecord rd = dao.query_AvatarRecord_db_by_username(cuser.getusername());
							if(rd == null || rd.getavatarname()==null){
								downloadAvatar(hcc, cuser.imagefile,cuser.phonenumber,cuser.username);
							}else if(!rd.getavatarname().equals(shortImg)){
								downloadAvatar(hcc, cuser.imagefile,cuser.phonenumber,cuser.username);
							}else if(!dao.isAvatarExistedInFS(shortImg)){
								downloadAvatar(hcc, cuser.imagefile,cuser.phonenumber,cuser.username);
							}
						}
						
					}
				}
				
				cbobj.smcb.onSuccess((Object)hcc.uiArray);
				
			}else if(hcc.ret == RET_QUERY_USERINFO_SERVER_TOKEN_INVALID){
				SamLog.e(TAG,"Query user info  TOKEN INVALIDE!");
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

				cbobj.smcb.onFailed(hcc.ret);

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

				cbobj.smcb.onError(R_QUERY_USERINFO_ERROR_HTTP_EXCEPTION);
		}
		


		
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
				dao.update_SendAnswer_db(sda);
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
				dao.update_SendAnswer_db(sda);
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
				dao.update_SendAnswer_db(sda);
				Bundle bundle = new Bundle();
				bundle.putSerializable("SendAnswer",sda);
				intent.putExtras(bundle);
				intent.putExtra("NoSuchQuestion", false);
				SamLog.e(TAG,"Send Answer FAILED:Send Failed!");
			}
		}else{
			sda.setstatus(SendAnswer.SEND_FAILED);
			dao.update_SendAnswer_db(sda);
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
		UpgradeCoreObj ugobj = (UpgradeCoreObj)samobj;

		String token = get_current_token();

		if(token == null){
			SamLog.e(TAG, "token is null, should never run to here");
			cbobj.smcb.onError(R_UPGRADE_ERROR_TOKEN_FILE_NULL);
			return;
		}

		HttpCommClient hcc = new HttpCommClient();
		if(hcc.upgrade(token,ugobj.vInfo)){
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
				LoginUser user = dao.query_activie_LoginUser_db();
				user.usertype = LoginUser.MIDSERVER;
				user.location = ugobj.vInfo.getLocation();
				user.area = ugobj.vInfo.getArea();
				user.description = ugobj.vInfo.getDesc();
				dao.add_update_LoginUser_db(user);
				set_current_user(user);

				if(mWaitThread!=null){
					mWaitThread.InterruptWaitThread();
				}

				cbobj.smcb.onSuccess(ugobj.vInfo);
				
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
					
					cbobj.smcb.onFailed(hcc.ret);
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
				
				cbobj.smcb.onError(R_UPGRADE_ERROR_HTTP_EXCEPTION);
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
		String cancel_question_id = ((CancelqCoreObj)samobj).question_id;
		if(hcc.cancelquestion(cancel_question_id,token)){
			
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
				SendQuestion question = dao.query_send_question_db(cancel_question_id);
				if(question !=null){
					question.canceltime = System.currentTimeMillis();
					question.status = SendQuestion.CANCEL;
					dao.add_update_SendQuestion_db(question);
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
				question.setsendercellphone(current_user.getphonenumber());
				question.setsenderusername(current_user.getusername());
				if(dao.add_update_SendQuestion_db(question)!=-1){
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


	private void cancelHeartTimeOut() {
		if(mWaitThread!=null && mWaitThread.push_hndl!=null){
			synchronized(pushLock){
				SamPushServiceHandler ph = mWaitThread.push_hndl.get();
				if(ph!=null){
					ph.removeMessages(MSG_PUSH_MSG_RECONNECT_TIMOUT);
				}
			}
		}
	}

	private void startHeartTimeOut() {
		if(mWaitThread!=null && mWaitThread.push_hndl!=null){
			synchronized(pushLock){
				SamPushServiceHandler ph = mWaitThread.push_hndl.get();
				if(ph!=null){
					Message msg = ph.obtainMessage(MSG_PUSH_MSG_RECONNECT_TIMOUT, 0, 0);
					ph.sendMessageDelayed(msg, SAMSERVICE_PUSH_HANDLE_RECONNECT_TIMEOUT);
				}
			}
		}
	}


	public void onNetworkDisconnect(){
		if(mWaitThread!=null){
			mWaitThread.networkAvaliable = false;
		}
	}

	public void onNetworkConnect(){
		if(mWaitThread!=null){
			mWaitThread.networkAvaliable = true;
			mWaitThread.notifyNetwork();
		}
	}

	public class WaitThread extends Thread {  
		private boolean run=true;
		private HttpCommClient hcc;
		public WeakReference <SamPushServiceHandler>  push_hndl;
		public boolean networkAvaliable;
		public Object networklock;
  
		public WaitThread (){}  
		public WaitThread (String name){  
			super(name);
			push_hndl = new WeakReference <SamPushServiceHandler>(mSamPushServiceHandler);
			networkAvaliable = false;
			networklock = new Object();
			
		} 

		public void waitNetwork(){
			synchronized (networklock){
				try {
					networklock.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
    			}
		}

		public void notifyNetwork(){
			synchronized (networklock){
				try {
					networklock.notify();
				} catch (Exception e) {
					e.printStackTrace();
				}
    			}	
		}
      
		@Override  
		public void run() {  
			hcc = new HttpCommClient();
			
			while(run){
				networkAvaliable = NetworkMonitor.isNetworkAvailable();
				
				if(!networkAvaliable){
					/*pending wait thread to power save*/
					cancelHeartTimeOut();
					waitNetwork();
				}
				/*connect to server*/
				if(current_token == null){
					SamLog.e(TAG,"WaitThread: current token is null");
					run = false;
					continue;
				}

				
				cancelHeartTimeOut();
				startHeartTimeOut();

				//SamLog.w(TAG,"Connect Push URL");

				if(!hcc.HttpPushWait(get_current_token())){
					SystemClock.sleep(2000);
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
						cancelHeartTimeOut();
						continue;
					}else if(hcc.statusCode == 503){
						SamLog.e(TAG,"WaitThread:Push servier is shutdown");
						Message msg = mSamPushServiceHandler.obtainMessage(MSG_PUSH_MSG_PUSHSERVERSHUTDOWN, 0, 0,null);
						mSamPushServiceHandler.sendMessage(msg);
						SystemClock.sleep(3000);
						continue;
					}else{
						SamLog.e(TAG,"WaitThread:impossible here. StatusCode:"+hcc.statusCode);
						SystemClock.sleep(3000);
						continue;
					}


				}

				
				
			}

			SamLog.e(TAG,"WaitThread exit !!!!");

			notifyStopLock();
    		} 

		public void StopThread(){
			run = false;
			InterruptWaitThread();
			
		}

		public void InterruptWaitThread(){
			SamLog.e(TAG,"InterruptWaitThread");
			hcc.InterruptHttpPushWait();
		}

	}   
    
}
