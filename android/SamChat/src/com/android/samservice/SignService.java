package com.android.samservice;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;

import org.apache.http.HttpStatus;

import com.android.samchat.*;
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
    
    /*handle message id*/
    public static final int MSG_AUTO_SIGN_IN=0;
    public static final int MSG_AUTO_SIGN_IN_TIMEOUT=1;
    public static final int MSG_SIGN_UP=10;
    public static final int MSG_SIGN_UP_TIMEOUT=11;
    
    public static final int MSG_SIGN_IN=20;
    public static final int MSG_SIGN_IN_TIMEOUT=21;

    public static final int MSG_SIGN_OUT=30;
    
    
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
    }
    
    public void stopSignService(){
    	mHandlerThread.getLooper().quit();
    	mSignService = null;
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
		return false;
	}
        
        /*sign in with username and password*/
    	username = sInfo.username;
	cellphone = sInfo.cellphone;
    	passwd = sInfo.password;
    	
    	if(!hcc.signin(cellphone, passwd)){
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
		if(!isSignTokenExisted() && SamService.getInstance().get_current_token() == null){
			if(cbobj.samobj == null){
				Message msg = hndl.obtainMessage(cbobj.cbMsg, R_AUTO_SIGN_IN_NO_HISTORY, -1);
				hndl.sendMessage(msg);
			}else{
				Message msg = hndl.obtainMessage(cbobj.cbMsg, R_AUTO_SIGN_IN_NO_HISTORY, -1,cbobj.samobj);
    				hndl.sendMessage(msg);
			}
    			return;
    		}
    	
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
			cbobj.sinfo.token = hcc.token_id;
			SamService.getInstance().store_current_token(hcc.token_id);
			SamService.getInstance().set_current_user(SamService.getInstance().query_activie_LoginUser_db());
			try{
				SamFile sfd = new SamFile();
				sfd.writeSamFile(SamService.sam_cache_path , SamService.TOKEN_FILE,hcc.token_id);
			}catch(IOException e){
				e.printStackTrace();
				if(cbobj.samobj == null){
					Message msg = hndl.obtainMessage(cbobj.cbMsg, R_AUTO_SIGN_IN_OK, -1,cbobj.sinfo);
    					hndl.sendMessage(msg);
				}else{
					Message msg = hndl.obtainMessage(cbobj.cbMsg, R_AUTO_SIGN_IN_OK, -1,cbobj.samobj);
    					hndl.sendMessage(msg);
				}
				return;
			}
			if(cbobj.samobj == null){
    				Message msg = hndl.obtainMessage(cbobj.cbMsg, R_AUTO_SIGN_IN_OK, 0,cbobj.sinfo);
    				hndl.sendMessage(msg);
			}else{
				Message msg = hndl.obtainMessage(cbobj.cbMsg, R_AUTO_SIGN_IN_OK, 0,cbobj.samobj);
    				hndl.sendMessage(msg);
			}
    			return;
    	}

	if(cbobj.samobj == null){
    		Message msg = hndl.obtainMessage(cbobj.cbMsg, R_AUTO_SIGN_IN_FAILED, -1);
		hndl.sendMessage(msg);
	}else{
		Message msg = hndl.obtainMessage(cbobj.cbMsg, R_AUTO_SIGN_IN_FAILED, -1,cbobj.samobj);
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

        if(sInfo == null ){
        	return false;
        }
        
        /*sign in with username and password*/
    	username = sInfo.username;
    	passwd = sInfo.password;
    	
	return hcc.signin(username, passwd);
	
    }
	
    private void do_sign_in(CBObj cbobj){
	if(cbobj==null || cbobj.cbHandler==null ){
		SamLog.e(TAG, "bad msg in sign in,drop it...");
		return;
	}
	
	Handler hndl = cbobj.cbHandler.get();
	if(hndl == null){
		SamLog.e(TAG, "cbhandler has been destroy in sign in, drop cbMsg ...");
		return;
	}
	HttpCommClient hcc = new HttpCommClient();

	if(signin_with_un_pa(cbobj.sinfo,hcc)){
                if(hcc.ret == RET_SI_FROM_SERVER_OK){
			/*store login user into db*/
			LoginUser user = hcc.userinfo;
			user.logintime = System.currentTimeMillis();
			user.status = LoginUser.ACTIVE;
			
			SamService.getInstance().add_update_LoginUser_db(user);
			SamService.getInstance().set_current_user(SamService.getInstance().query_activie_LoginUser_db());

			/*********************/
					
			cbobj.sinfo.token = hcc.token_id;
			SamLog.e(TAG,"hcc token id:"+hcc.token_id);
			SamService.getInstance().store_current_token(hcc.token_id);
			Message msg = hndl.obtainMessage(cbobj.cbMsg, R_SIGN_IN_OK, hcc.ret,cbobj.sinfo);
			hndl.sendMessage(msg);
                }else{
			Message msg = hndl.obtainMessage(cbobj.cbMsg, R_SIGN_IN_FAILED, hcc.ret);
			hndl.sendMessage(msg);
                }
	}else{
		Message msg = hndl.obtainMessage(cbobj.cbMsg, R_SIGN_IN_ERROR, -1);
		hndl.sendMessage(msg);
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

        if(sInfo == null ){
        	return false;
        }
        
        /*sign in with username and password*/
    	username = sInfo.username;
    	passwd = sInfo.password;
	cellphone = sInfo.cellphone;

	return hcc.signup(username, passwd,passwd,cellphone);
	
    }
	
	private void do_sign_up(CBObj cbobj){
		if(cbobj==null || cbobj.cbHandler==null ){
			SamLog.e(TAG, "bad msg in sign in,drop it...");
			return;
		}
		
		Handler hndl = cbobj.cbHandler.get();
		if(hndl == null){
			SamLog.e(TAG, "cbhandler has been destroy in sign in, drop cbMsg ...");
			return;
		}
		
		HttpCommClient hcc = new HttpCommClient();

		if(signup_with_un_pa(cbobj.sinfo,hcc)){

			if(hcc.ret == RET_SU_FROM_SERVER_OK){
				/*store login user into db*/
				LoginUser user = hcc.userinfo;
				user.logintime = System.currentTimeMillis();
				user.status = LoginUser.ACTIVE;
				SamService.getInstance().add_update_LoginUser_db(user);
				SamService.getInstance().set_current_user(SamService.getInstance().query_activie_LoginUser_db());

				/*********************/
				cbobj.sinfo.token = hcc.token_id;
				SamService.getInstance().store_current_token(hcc.token_id);
				Message msg = hndl.obtainMessage(cbobj.cbMsg, R_SIGN_UP_OK, hcc.ret,cbobj.sinfo);
				//hndl.obtainMessage(what, arg1, arg2, obj)
				hndl.sendMessage(msg);
			}else{
				Message msg = hndl.obtainMessage(cbobj.cbMsg, R_SIGN_UP_FAILED, hcc.ret);
				hndl.sendMessage(msg);
			}
		}else{
			Message msg = hndl.obtainMessage(cbobj.cbMsg, R_SIGN_UP_ERROR, -1);
			hndl.sendMessage(msg);
			return;
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
		if(user!=null && hcc.signout(user.username,user.phonenumber,token)){
			if(hcc.ret == RET_SOUT_FROM_SERVER_OK || hcc.ret == RET_SOUT_FROM_SERVER_TOKEN_INVALUD){
				SamService.getInstance().update_LogoutUser_db(user);
				Message msg = hndl.obtainMessage(cbobj.cbMsg, R_SIGN_OUT_OK, -1);
				hndl.sendMessage(msg);
			}else{
				SamLog.e(TAG,"Fatal Error, need fix this error");
				SamService.getInstance().update_LogoutUser_db(user);
				Message msg = hndl.obtainMessage(cbobj.cbMsg, R_SIGN_OUT_FAILED, -1);
				hndl.sendMessage(msg);
			}
			
		}else{
			SamService.getInstance().update_LogoutUser_db(user);
			Message msg = hndl.obtainMessage(cbobj.cbMsg, R_SIGN_OUT_FAILED, -1);
			hndl.sendMessage(msg);
		}

	} 
    
    
    private final class ServiceHandler extends Handler{
    	public ServiceHandler(Looper looper)
		{
		   super(looper);
		}
    	
    	@Override
		public void handleMessage(Message msg){
    		Message callbackMessage;
    		CBObj cbobj = (CBObj)msg.obj;
    		
    		if(cbobj==null || cbobj.cbHandler==null){
    			SamLog.e(TAG, "bad msg,drop it...");
    			return;
    		}
    		
    		Handler hndl = cbobj.cbHandler.get();
    		if(hndl == null){
    			SamLog.e(TAG, "cbhandler has been destroy, drop cbMsg ...");
    			return;
    		}
    		
    		switch(msg.what){
    			case MSG_AUTO_SIGN_IN:
    			/*start auto sign in job*/
    				do_auto_sign_in(cbobj);
    				break;
    			case MSG_SIGN_UP:
    			/*start sign up*/
				do_sign_up(cbobj);
    				break;
    			case MSG_SIGN_IN:
    			/*start sign in job*/
    				do_sign_in(cbobj);
    				break;
			case MSG_SIGN_OUT:
				do_sign_out(cbobj);
				break;
    		}
    		
    	}
    	
    	
    }
	
    public void attemptAutoSignIn(Handler callback,int cbMsg){
    	if(!isSignTokenExisted() && SamService.getInstance().get_current_token() == null){
    		Message msg = callback.obtainMessage(cbMsg, R_AUTO_SIGN_IN_NO_HISTORY, -1);
    		callback.sendMessage(msg);
    		return;
    	}
    	
    	/*request auto sign in */
    	CBObj obj = new CBObj(callback,cbMsg);
    	Message msg = mServiceHandler.obtainMessage(MSG_AUTO_SIGN_IN, obj);
    	mServiceHandler.sendMessage(msg);

    }

   public void attemptAutoSignIn(Handler callback,int cbMsg,SamCoreObj samobj){
    	if(!isSignTokenExisted() && SamService.getInstance().get_current_token() == null){
    		Message msg = callback.obtainMessage(cbMsg, R_AUTO_SIGN_IN_NO_HISTORY, -1,samobj);
    		callback.sendMessage(msg);
    		return;
    	}
    	
    	/*request auto sign in */
    	CBObj obj = new CBObj(callback,cbMsg,samobj);
    	Message msg = mServiceHandler.obtainMessage(MSG_AUTO_SIGN_IN, obj);
    	mServiceHandler.sendMessage(msg);

    }
	
	
	
	
		
	public boolean isSignTokenExisted(){
		String token_path = SamService.sam_cache_path +"/"+SamService.TOKEN_FILE;
		File filex = new File(token_path);
		
		//if(SamService.DEBUG) return true;
		
		if (!filex.exists()) {
			return false;
		}else{
			try{
				SamFile samfile = new SamFile();
				if(samfile.isSamFileEmpty(filex)){
					return false;
				}else{
					return true;
				}
			}catch(IOException e){
				e.printStackTrace();
				return false;
			}
		}
		
    }
	
	private boolean getSignInfo(SignInfo u_p){
		/*
		String up_path = SamService.sam_cache_path +"/" + SamService.UP_FILE;
		File filex = new File(up_path);
		
		if(!filex.exists()){
			return false;
		}else{
			try{
				SamFile samfile = new SamFile();
				if(samfile.isSamFileEmpty(filex)){
					return false;
				}
				
				String up = samfile.readSamFile(SamService.sam_cache_path,SamService.UP_FILE);
				if(up == null)
					return false;
				String spStr[] = up.split(",");
				if(spStr.length!=2){
					return false;
				}else{
					int length = spStr[0].length();
					if(length>=SamService.MIN_USERNAME_LENGTH){
						u_p.username = spStr[0];
					}else{
						return false;
					}
					
					length = spStr[1].length();
					if(length>=SamService.MIN_PASSWORD_LENGTH && length<=SamService.MAX_PASSWORD_LENGTH){
						u_p.password = spStr[1];
					}else{
						return false;
					}
					
					return true;
				}
				
			}catch(IOException e){
				e.printStackTrace();
				return false;
			}
			
			
		}
		*/

		LoginUser user = SamService.getInstance().query_activie_LoginUser_db();
		if(user != null){
			if(user.username!=null && user.username.length()>=SamService.MIN_USERNAME_LENGTH){
				u_p.username = user.username;
			}else{
				SamLog.e(TAG,"get sign info failed in username");
				return false;
			}

			if(user.phonenumber!=null && user.phonenumber.length()>=SamService.MIN_MPHONE_NUMBER_LENGTH){
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
	
	
	
	public void SignUp(Handler cbHandler,int cbMsg,String uname, String pwd,String cellphone)
	{
		/*request sign up*/
		CBObj obj = new CBObj(cbHandler,cbMsg,uname,pwd,cellphone);
		Message msg = mServiceHandler.obtainMessage(MSG_SIGN_UP, obj);
		mServiceHandler.sendMessage(msg);
	}
	
	public void SignIn(Handler cbHandler,Activity cbActivity,int cbMsg,String un, String pwd)
	{
		/*request sign up*/
		CBObj obj = new CBObj(cbHandler,cbMsg,un,pwd);
		Message msg = mServiceHandler.obtainMessage(MSG_SIGN_IN, obj);
		mServiceHandler.sendMessage(msg);
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
