package com.android.samservice;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;

import org.apache.http.HttpStatus;

import com.android.samservice.info.AvatarRecord;
import com.android.samservice.info.ContactUser;
import com.android.samservice.info.LoginUser;


import android.util.Log;
import android.app.Activity;
import android.content.Intent;

import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;



/* Author: KevinDong
 * SignService is responsible for all things related to sign in and sign up
 * 
 * */
public class SignService{
	public static String TAG="SamChat_SignService";
    //public static final String TOKEN_DIR="tmp";

    public static final int SIGN_IN_TIMEOUT = 5000;
    public static final int SIGN_UP_TIMEOUT = 15000;
    public static final int SIGN_TIMEOUT=20000;
    
    /*handle message id*/
    public static final int MSG_AUTO_SIGN_IN=0;
    public static final int MSG_AUTO_SIGN_IN_TIMEOUT=1;
    public static final int MSG_SIGN_UP=10;
    public static final int MSG_SIGN_UP_TIMEOUT=11;
    
    public static final int MSG_SIGN_IN=20;
    public static final int MSG_SIGN_IN_TIMEOUT=21;

    public static final int MSG_SIGN_OUT=30;

    public static final int MSG_SIGN_TIMEOUT=40;
    
    
    /*Auto Sign in*/
    public static final int R_AUTO_SIGN_IN_OK=0;
    public static final int R_AUTO_SIGN_IN_TIMEOUT=1;
    public static final int R_AUTO_SIGN_IN_NO_HISTORY=2;
    public static final int R_AUTO_SIGN_IN_FAILED=3;
    
    
    
    /*Sign up*/
    public static final int R_SIGN_UP_OK=0;
    public static final int R_SIGN_UP_TIMEOUT=1;
    public static final int R_SIGN_UP_FAILED=2;
    public static final int R_SIGN_UP_ERROR=3;

    public static final int RET_SU_FROM_SERVER_OK = 0;
    public static final int RET_SU_FROM_SERVER_HTTP_FAILED = -1;//parse http failed
    public static final int RET_SU_FROM_SERVER_ACTION_NOT_SUPPORT = -2;//action param not support
    public static final int RET_SU_FROM_SERVER_PARAM_NOT_SUPPORT = -3;//parse param failed
    public static final int RET_SU_FROM_SERVER_CELL_UN_EXISTED = -101;//username/cellphone has been existed
    public static final int RET_SU_FROM_SERVER_PWD_CPWD_NOT_SAME = -102;//pass and confirm pass not same
    public static final int RET_SU_FROM_SERVER_INTERNAL_ERROR = -103;
    

    /*Sign in*/
    public static final int R_SIGN_IN_OK=0;
    public static final int R_SIGN_IN_TIMEOUT=1;
    public static final int R_SIGN_IN_FAILED=2;
    public static final int R_SIGN_IN_ERROR=3;

    public static final int RET_SI_FROM_SERVER_OK = 0;
    public static final int RET_SI_FROM_SERVER_HTTP_FAILED = -1;//parse http failed
    public static final int RET_SI_FROM_SERVER_ACTION_NOT_SUPPORT = -2;//action param not support
    public static final int RET_SI_FROM_SERVER_PARAM_NOT_SUPPORT = -3;//parse param failed
    public static final int RET_SI_FROM_CLIENT_CONNECT_FAILED=-4;
    public static final int RET_SI_FROM_SERVER_UP_ERROR = -201;//username/password is error

    /*Sign out*/
    public static final int R_SIGN_OUT_OK=0;
    public static final int R_SIGN_OUT_TIMEOUT=1;
    public static final int R_SIGN_OUT_FAILED=2;
    public static final int R_SIGN_OUT_ERROR=3;
    public static final int RET_SOUT_FROM_SERVER_OK = 0;
    public static final int RET_SOUT_FROM_SERVER_HTTP_FAILED = -1;//parse http failed
    public static final int RET_SOUT_FROM_SERVER_ACTION_NOT_SUPPORT = -2;//action param not support
    public static final int RET_SOUT_FROM_SERVER_TOKEN_ERROR = -4;//parse param failed
    public static final int RET_SOUT_FROM_SERVER_TOKEN_INVALUD = -401;//token invalid
	
    private static SignService mSignService;
    private HandlerThread mHandlerThread=null;
    private ServiceHandler mServiceHandler=null;

    private HandlerThread mTimeOutHandlerThread=null;
    private ServiceTimeOutHandler mTimeOutServiceHandler=null;
    
    public static synchronized SignService getInstance(){
    	if(mSignService == null){
    		mSignService = new SignService();
    	}
    	return mSignService;
    }
    
    private SignService(){
    	InitHandlerThread();
    }
    
    private void InitHandlerThread(){
    	mHandlerThread = new HandlerThread("SignService");
    	mHandlerThread.start();
    	
    	mServiceHandler = new ServiceHandler(mHandlerThread.getLooper());

	mTimeOutHandlerThread = new HandlerThread("SignTimeOutService");
    	mTimeOutHandlerThread.start();
    	
    	mTimeOutServiceHandler = new ServiceTimeOutHandler(mTimeOutHandlerThread.getLooper());
    }
    
    public void stopSignService(){
    	mHandlerThread.getLooper().quit();
	mTimeOutHandlerThread.getLooper().quit();
    	mSignService = null;
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

			file = new File(path  + "/" + fileName);

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

	private void deleteOldAvatar(String oldAvatar){
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

	private void downloadAvatar(HttpCommClient hcc){
		byte[] data=null;
		String shortImg=null;
		boolean downSucceed=false;
		StringBuffer oldAvatar=new StringBuffer();
		if(hcc.userinfo.imagefile!=null && (shortImg = getShortImgName(hcc.userinfo.imagefile))!=null){
			data = hcc.getImage(hcc.userinfo.imagefile);
			if(data!=null){
				downSucceed = saveAvatar(SamService.sam_cache_path+SamService.AVATAR_FOLDER, shortImg, data);
			}
		}

		if(downSucceed){
			SamService.getInstance().getDao().add_update_AvatarRecord_db(
				hcc.userinfo.phonenumber,
				hcc.userinfo.username,
				shortImg,
				oldAvatar
			);

			if(oldAvatar.length()!=0){
				deleteOldAvatar(oldAvatar.toString());
			}
		}
	}
    
    private boolean auto_sign_with_token(){
    	return false;
    }
    
    private boolean auto_sign_with_un_pa(HttpCommClient hcc){
	String username=null;
	String passwd=null;
	String cellphone = null;
	SignInfo sInfo = new SignInfo();
	if(!getSignInfo(sInfo)){
		hcc.ret = RET_SI_FROM_SERVER_UP_ERROR;
		return false;
	}
        
        /*sign in with username and password*/
    	username = sInfo.username;
	cellphone = sInfo.cellphone;
    	passwd = sInfo.password;
    	
    	if(!hcc.signin(null,username, passwd)){
    		return false;
    	}
    	
    	if(hcc.ret == RET_SI_FROM_SERVER_OK) 
    		return true;
    	else 
    		return false;
    }
    /*sign in rule:
     * 1. log in with token first (valid duration:30min)
     * 2. log in with username and passwd
     * 3. auto sign in failed will trigger user sign in  
     * */
	private void do_auto_sign_in(CBObj cbobj){
		if(cbobj==null || cbobj.cbHandler==null){
			SamLog.e(TAG, "bad msg in auto sign,drop it...");
			return;
		}
		
		Handler hndl = cbobj.cbHandler.get();
		if(hndl == null){
			SamLog.e(TAG, "cbhandler has been destroy in auto sign, drop cbMsg ...");
			return;
		}
		/*check token first*/
		
		/*Auto sign with token*/
		if(auto_sign_with_token()){
			if(cbobj.samobj == null){
				Message msg = hndl.obtainMessage(cbobj.cbMsg, R_AUTO_SIGN_IN_OK, 0);
				hndl.sendMessage(msg);
			}else{
				Message msg = hndl.obtainMessage(cbobj.cbMsg, R_AUTO_SIGN_IN_OK, 0,cbobj.samobj);
				hndl.sendMessage(msg);
			}
			return;
		}
    	
		/*Auto sign with password and username*/
		HttpCommClient hcc = new HttpCommClient();
		if(auto_sign_with_un_pa(hcc)){
			boolean need_update = false;
			/*store login user into db*/
			LoginUser user = hcc.userinfo;
			LoginUser userdb = SamService.getInstance().getDao().query_activie_LoginUser_db();
			if(userdb == null){
				throw new RuntimeException("fatal error: auto signin but no active user!");
			}else if(userdb!=null && userdb.getlastupdate()!=user.getlastupdate()){
				need_update = true;
				userdb.unique_id = user.unique_id;
				userdb.username = user.username;
				userdb.countrycode = user.countrycode;
				userdb.phonenumber = user.phonenumber;
				userdb.password = user.password;
				userdb.usertype = user.usertype;
				if(user.usertype == LoginUser.MIDSERVER){
					userdb.area = user.area;
					userdb.location = user.location;
					userdb.description =user.description;
				}
				userdb.lastupdate = user.lastupdate;
				userdb.imagefile = user.imagefile;

				user = userdb;
			}else if(user.imagefile!=null){
				String shortImg = getShortImgName(user.imagefile);
				if(shortImg != null){
					AvatarRecord rd = SamService.getInstance().getDao().query_AvatarRecord_db_by_username(user.getusername());
					if(rd == null || rd.getavatarname()==null){
						need_update = true;
					}else if(!rd.getavatarname().equals(shortImg)){
						need_update = true;
					}else{
						File file = new File(SamService.sam_cache_path+SamService.AVATAR_FOLDER+"/"+shortImg);
						if(!file.exists()){
							need_update = true;
						}
					}
				}
				userdb.unique_id = user.unique_id;
				userdb.username = user.username;
				userdb.countrycode = user.countrycode;
				userdb.phonenumber = user.phonenumber;
				userdb.password = user.password;
				userdb.usertype = user.usertype;
				if(user.usertype == LoginUser.MIDSERVER){
					userdb.area = user.area;
					userdb.location = user.location;
					userdb.description =user.description;
				}
				userdb.lastupdate = user.lastupdate;
				userdb.imagefile = user.imagefile;
				user = userdb;
			}else{
				userdb.unique_id = user.unique_id;
				userdb.username = user.username;
				userdb.countrycode = user.countrycode;
				userdb.phonenumber = user.phonenumber;
				userdb.password = user.password;
				userdb.usertype = user.usertype;
				if(user.usertype == LoginUser.MIDSERVER){
					userdb.area = user.area;
					userdb.location = user.location;
					userdb.description =user.description;
				}
				userdb.lastupdate = user.lastupdate;
				userdb.imagefile = user.imagefile;
				user = userdb;
			}

			user.id = SamService.getInstance().getDao().add_update_LoginUser_db(user);
			SamService.getInstance().set_current_user(user);

			/*********************/
					
			cbobj.sinfo.token = hcc.token_id;
			SamLog.e(TAG,"hcc token id:"+hcc.token_id);
			SamService.getInstance().store_current_token(hcc.token_id);

			if(need_update){
				downloadAvatar(hcc);
			}
			
			if(cbobj.samobj == null){
    				Message msg = hndl.obtainMessage(cbobj.cbMsg, R_AUTO_SIGN_IN_OK, hcc.ret,cbobj.sinfo);
    				hndl.sendMessage(msg);
			}else{
				Message msg = hndl.obtainMessage(cbobj.cbMsg, R_AUTO_SIGN_IN_OK, hcc.ret,cbobj.samobj);
    				hndl.sendMessage(msg);
			}
    			return;
    	}

	if(cbobj.samobj == null){
    		Message msg = hndl.obtainMessage(cbobj.cbMsg, R_AUTO_SIGN_IN_FAILED, hcc.ret);
		hndl.sendMessage(msg);
	}else{
		Message msg = hndl.obtainMessage(cbobj.cbMsg, R_AUTO_SIGN_IN_FAILED, hcc.ret,cbobj.samobj);
		hndl.sendMessage(msg);
	}
    	return;
    }


/*
	returen value
	true: Server return http status 200 (OK)
		but not means sign up successfully
	false: Server return http status not 200 (OK)
*/	
    private boolean signin_with_un_pa( SignInfo sInfo,HttpCommClient hcc){
    	String username=null;
       String passwd=null;
	String countrycode =null;

        if(sInfo == null ){
        	return false;
        }
        
        /*sign in with username and password*/
	countrycode = sInfo.country_code;
	if(countrycode == null){
		username = sInfo.username;
	}else{
		username = sInfo.cellphone;
	}
    	
    	passwd = sInfo.password;
	
    	
	return hcc.signin(countrycode,username, passwd);
	
    }

	private String getShortImgName(String photoPath) {
		int index  = photoPath.lastIndexOf("origin_");
		if(index != -1) {
			return photoPath.substring(index);
		} else {
			return null;
		}
	}
	
    private void do_sign_in(SamCoreObj samobj){
	CBObj cbobj = samobj.refCBObj;
	SignInCoreObj siobj = (SignInCoreObj)samobj;

	HttpCommClient hcc = new HttpCommClient();

	SignInfo sinfo = siobj.countrycode == null ?new SignInfo(siobj.username,siobj.password):new SignInfo(siobj.countrycode,siobj.username,siobj.password);

	if(signin_with_un_pa(sinfo,hcc)){
                if(hcc.ret == RET_SI_FROM_SERVER_OK){
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

			//need_update: if need to update avatar 
			boolean need_update = false;
			/*store login user into db*/
			LoginUser user = hcc.userinfo;
			LoginUser userdb = SamService.getInstance().getDao().query_LoginUser_db_by_username(user.getusername());
			
			if(userdb == null){
				need_update = true;
			}else if(userdb!=null && userdb.getlastupdate()!=user.getlastupdate()){
				need_update = true;
				userdb.unique_id = user.unique_id;
				userdb.username = user.username;
				userdb.countrycode = user.countrycode;
				userdb.phonenumber = user.phonenumber;
				userdb.password = user.password;
				userdb.usertype = user.usertype;
				if(user.usertype == LoginUser.MIDSERVER){
					userdb.area = user.area;
					userdb.location = user.location;
					userdb.description =user.description;
				}
				userdb.lastupdate = user.lastupdate;
				userdb.imagefile = user.imagefile;
				user = userdb;
			}else if(user.imagefile!=null){
				String shortImg = getShortImgName(user.imagefile);
				if(shortImg != null){
					AvatarRecord rd = SamService.getInstance().getDao().query_AvatarRecord_db_by_username(user.getusername());
					if(rd == null || rd.getavatarname()==null){
						need_update = true;
					}else if(!rd.getavatarname().equals(shortImg)){
						need_update = true;
					}else{
						File file = new File(SamService.sam_cache_path+SamService.AVATAR_FOLDER+"/"+shortImg);
						if(!file.exists()){
							need_update = true;
						}
					}
				}
				userdb.unique_id = user.unique_id;
				userdb.username = user.username;
				userdb.countrycode = user.countrycode;
				userdb.phonenumber = user.phonenumber;
				userdb.password = user.password;
				userdb.usertype = user.usertype;
				if(user.usertype == LoginUser.MIDSERVER){
					userdb.area = user.area;
					userdb.location = user.location;
					userdb.description =user.description;
				}
				userdb.lastupdate = user.lastupdate;
				userdb.imagefile = user.imagefile;
				user = userdb;
			}else{
				userdb.unique_id = user.unique_id;
				userdb.username = user.username;
				userdb.countrycode = user.countrycode;
				userdb.phonenumber = user.phonenumber;
				userdb.password = user.password;
				userdb.usertype = user.usertype;
				if(user.usertype == LoginUser.MIDSERVER){
					userdb.area = user.area;
					userdb.location = user.location;
					userdb.description =user.description;
				}
				userdb.lastupdate = user.lastupdate;
				userdb.imagefile = user.imagefile;
				user = userdb;
			}
						
			user.logintime = System.currentTimeMillis();
			user.status = LoginUser.ACTIVE;

			SamService.getInstance().getDao().clear_LoginUser_db();
			user.id = SamService.getInstance().getDao().add_update_LoginUser_db(user);
			SamService.getInstance().set_current_user(user);

			sinfo.token = hcc.token_id;
			SamLog.i(TAG,"hcc token id:"+hcc.token_id);
			SamService.getInstance().store_current_token(hcc.token_id);

			if(need_update){
				downloadAvatar(hcc);
			}

			//add login user into contact user db also
			ContactUser me = new ContactUser();
			me.setusername(user.getusername());
			me.setusertype(user.getUserType());
			if(user.getUserType() == LoginUser.MIDSERVER){
				me.setarea(user.getarea());
				me.setlocation(user.getlocation());
				me.setdescription(user.getdescription());
			}
			me.seteasemob_username(user.getusername());
			me.setimagefile(user.getimagefile());
			me.setlastupdate(user.getlastupdate());
			me.setphonenumber(user.getphonenumber());
			me.setunique_id(user.getunique_id());
			SamService.getInstance().getDao().add_update_ContactUser_db(me);

			cbobj.smcb.onSuccess(sinfo);
			
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
		
		
		cbobj.smcb.onError(R_SIGN_IN_ERROR);
  	}
  }

/*
	returen value
	true: Server return http status 200 (OK)
		but not means sign up successfully
	false: Server return http status not 200 (OK)
*/	
    private boolean signup_with_un_pa( SignInfo sInfo,HttpCommClient hcc){
	String username=null;
	String passwd=null;
	String cellphone=null;
	String country_code = null;

        if(sInfo == null ){
        	return false;
        }
        
        /*sign in with username and password*/
    	username = sInfo.username;
    	passwd = sInfo.password;
	cellphone = sInfo.cellphone;
	country_code = sInfo.country_code;

	return hcc.signup(username, passwd,passwd,cellphone,country_code);
	
    }
	
	private void do_sign_up(SamCoreObj samobj){
		CBObj cbobj = samobj.refCBObj;
		SignUpCoreObj suobj = (SignUpCoreObj)samobj;

		SignInfo sinfo = new SignInfo(suobj.username,suobj.password,suobj.cellphone,suobj.country_code);

		HttpCommClient hcc = new HttpCommClient();

		if(signup_with_un_pa(sinfo,hcc)){

			if(hcc.ret == RET_SU_FROM_SERVER_OK){
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
				
				
				/*store login user into db*/
				LoginUser user = hcc.userinfo;
				user.countrycode = suobj.country_code;
				user.logintime = System.currentTimeMillis();
				user.status = LoginUser.ACTIVE;

				SamService.getInstance().getDao().clear_LoginUser_db();
				user.id = SamService.getInstance().getDao().add_update_LoginUser_db(user);
				
				SamService.getInstance().set_current_user(user);

				/*********************/
				sinfo.token = hcc.token_id;
				SamService.getInstance().store_current_token(hcc.token_id);
				cbobj.smcb.onSuccess(sinfo);
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
			
		
			cbobj.smcb.onError(R_SIGN_UP_ERROR);
    		}
    }


	/*reset all data after sign out*/
	private void reset(){
		//delete avatar file
		File filePath = new File(SamService.sam_cache_path);

		if(filePath.exists()){
			File file = new File(SamService.sam_cache_path + SamService.AVATAR);
			if(file.exists()){
				file.delete();
			}
		}
	}
	
	private void do_sign_out(CBObj cbobj){
		if(cbobj==null || cbobj.cbHandler==null ){
			SamLog.e(TAG, "bad msg in sign out,drop it...");
			return;
		}
		
		Handler hndl = cbobj.cbHandler.get();
		if(hndl == null){
			SamLog.e(TAG, "cbhandler has been destroy in sign out, drop cbMsg ...");
			return;
		}

		HttpCommClient hcc = new HttpCommClient();
		LoginUser user = SamService.getInstance().get_current_user();
		String token = SamService.getInstance().get_current_token();
		if(user!=null && hcc.signout(token)){
			if(hcc.ret == RET_SOUT_FROM_SERVER_OK || hcc.ret == RET_SOUT_FROM_SERVER_TOKEN_INVALUD){
				SamService.getInstance().getDao().update_LogoutUser_db(user.getusername());
				Message msg = hndl.obtainMessage(cbobj.cbMsg, R_SIGN_OUT_OK, -1);
				hndl.sendMessage(msg);
			}else{
				SamLog.e(TAG,"Fatal Error, need fix this error");
				SamService.getInstance().getDao().update_LogoutUser_db(user.getusername());
				Message msg = hndl.obtainMessage(cbobj.cbMsg, R_SIGN_OUT_FAILED, -1);
				hndl.sendMessage(msg);
			}
			
		}else{
			SamService.getInstance().getDao().update_LogoutUser_db(user.getusername());
			Message msg = hndl.obtainMessage(cbobj.cbMsg, R_SIGN_OUT_FAILED, -1);
			hndl.sendMessage(msg);
		}

		reset();

	} 

	private void cancelTimeOut(SamCoreObj samobj) {
		mTimeOutServiceHandler.removeMessages(MSG_SIGN_TIMEOUT,samobj);
	}

	private void startTimeOut(SamCoreObj samobj) {
		Message msg = mTimeOutServiceHandler.obtainMessage(MSG_SIGN_TIMEOUT,samobj);
		mTimeOutServiceHandler.sendMessageDelayed(msg, SIGN_TIMEOUT);
	}

	 private final class ServiceTimeOutHandler extends Handler{
		public ServiceTimeOutHandler(Looper looper)
		{
		   super(looper);
		}

		@Override
		public void handleMessage(Message msg){
			SamCoreObj samobj = (SamCoreObj)msg.obj;
			CBObj cbobj = samobj.refCBObj;
			boolean continue_run = true;
			Handler hndl = null;

			switch(msg.what){
				case MSG_SIGN_TIMEOUT:
					synchronized(samobj){
						if(samobj.request_status == SamCoreObj.STATUS_INIT){
							samobj.request_status = SamCoreObj.STATUS_TIMEOUT;
						}else if(samobj.request_status == SamCoreObj.STATUS_DONE){
							continue_run = false;
						}
					}

					if(continue_run){
						if(samobj.isSignin()){
							cbobj.smcb.onError(R_SIGN_IN_TIMEOUT);
							SamLog.e(TAG, "SignServiceTimeOut Happened for msg isSignin");
						}else if(samobj.isSignup()){
							cbobj.smcb.onError(R_SIGN_UP_TIMEOUT);
							SamLog.e(TAG, "SignServiceTimeOut Happened for msg isSignup");
						}
					}

					
					break;
			}
		}
	}
    
    private final class ServiceHandler extends Handler{
    	public ServiceHandler(Looper looper)
		{
		   super(looper);
		}
    	
    	@Override
	public void handleMessage(Message msg){
    		switch(msg.what){
    			case MSG_AUTO_SIGN_IN:
    			/*start auto sign in job*/
    				do_auto_sign_in((CBObj)msg.obj);
    				break;
    			case MSG_SIGN_UP:
    			/*start sign up*/
				do_sign_up((SamCoreObj)msg.obj);
    				break;
    			case MSG_SIGN_IN:
    			/*start sign in job*/
    				do_sign_in((SamCoreObj)msg.obj);
    				break;
			case MSG_SIGN_OUT:
				do_sign_out((CBObj)msg.obj);
				break;
    		}
    		
    	}
    	
    	
    }
	
    public void attemptAutoSignIn(Handler callback,int cbMsg){
    	/*request auto sign in */
    	CBObj obj = new CBObj(callback,cbMsg);
    	Message msg = mServiceHandler.obtainMessage(MSG_AUTO_SIGN_IN, obj);
    	mServiceHandler.sendMessage(msg);

    }

   public void attemptAutoSignIn(Handler callback,int cbMsg,SamCoreObj samobj){
    	/*request auto sign in */
    	CBObj obj = new CBObj(callback,cbMsg,samobj);
    	Message msg = mServiceHandler.obtainMessage(MSG_AUTO_SIGN_IN, obj);
    	mServiceHandler.sendMessage(msg);

    }
	
	private boolean getSignInfo(SignInfo u_p){
		LoginUser user = SamService.getInstance().getDao().query_activie_LoginUser_db();
		if(user != null){
			if(user.username!=null && user.username.length()>=SamService.MIN_USERNAME_LENGTH && user.username.length()<=SamService.MAX_USERNAME_LENGTH){
				u_p.username = user.username;
			}else{
				SamLog.e(TAG,"get sign info failed in username");
				return false;
			}

			if(user.phonenumber!=null && user.phonenumber.length()>=SamService.MIN_MPHONE_NUMBER_LENGTH && user.phonenumber.length()<=SamService.MAX_MPHONE_NUMBER_LENGTH){
				u_p.cellphone = user.phonenumber;
			}else{
				SamLog.e(TAG,"get sign info failed in phonenumber");
				return false;
			}

			if(user.password!=null && user.password.length()>=SamService.MIN_PASSWORD_LENGTH && user.password.length() <= SamService.MAX_PASSWORD_LENGTH ){
				u_p.password = user.password;
			}else{
				SamLog.e(TAG,"get sign info failed in password");
				return false;
			}
		}else{
			SamLog.e(TAG,"get sign info failed in no active user");
			return false;
		}
		SamLog.e(TAG,"get sign info succesfully");
		return true;
	}
	
	public void SignUp(String uname, String pwd,String cellphone,String country_code,SMCallBack SMcb)
	{
		CBObj obj = new CBObj(SMcb);
		SamCoreObj  samobj = new SignUpCoreObj(obj,uname,pwd,cellphone,country_code);
		Message msg = mServiceHandler.obtainMessage(MSG_SIGN_UP, samobj);
		mServiceHandler.sendMessage(msg);
		startTimeOut(samobj);
	}
	
	public void SignIn(String un, String pwd, SMCallBack SMcb){
		CBObj obj = new CBObj(SMcb);
		SamCoreObj  samobj = new SignInCoreObj(obj,un,pwd);
		Message msg = mServiceHandler.obtainMessage(MSG_SIGN_IN, samobj);
		mServiceHandler.sendMessage(msg);
		startTimeOut(samobj);
	}

	public void SignIn(String cc,String un, String pwd, SMCallBack SMcb){
		CBObj obj = new CBObj(SMcb);
		SamCoreObj  samobj = new SignInCoreObj(obj,cc,un,pwd);
		Message msg = mServiceHandler.obtainMessage(MSG_SIGN_IN, samobj);
		mServiceHandler.sendMessage(msg);
		startTimeOut(samobj);
	}

	public void SignOut(Handler cbHandler, int cbMsg){
		CBObj obj = new CBObj(cbHandler,cbMsg);
		Message msg = mServiceHandler.obtainMessage(MSG_SIGN_OUT, obj);
		mServiceHandler.sendMessage(msg);
	}
	
	
	/*
	private static String[] getExternalStorageDirectory(Activity activity) {
		// 使用2.2 ，2.3的版本 用于获取内置存储卡路径
		String[] outStoragePath=new String[1];
		try {
		// 使用4.0以后的版本 ，  反射获取多个存储卡的路径，返回外资存储卡的路径
		StorageManager storageManager = (StorageManager) activity.getSystemService(Context.STORAGE_SERVICE);  
		    Class<?>[] paramClasses = {};  
		    Method getVolumePathsMethod = StorageManager.class.getMethod("getVolumePaths", paramClasses);  
		    getVolumePathsMethod.setAccessible(true);  
		    Object[] params = {};  
		    Object invoke = getVolumePathsMethod.invoke(storageManager, params);  
		    if(((String[])invoke).length > 1){
		    	outStoragePath=new String[((String[])invoke).length];
		    	for (int i = 0; i < ((String[])invoke).length; i++) {  
		        outStoragePath[i] = ((String[])invoke)[i];
		    }
		    } else {
		    	outStoragePath[0] = Environment.getExternalStorageDirectory().getAbsolutePath();
		    }
		} catch (Exception e) {
		outStoragePath[0] = Environment.getExternalStorageDirectory().getAbsolutePath();
		}
		return outStoragePath;
		}
	*/
	
	
	
	
}
