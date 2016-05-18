package com.netease.nim.demo.main.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.android.samchat.activity.SignInActivity;
import com.android.samservice.SamService;
import com.android.samservice.SignService;
import com.android.samservice.info.LoginUser;
import com.netease.nim.demo.DemoCache;
import com.netease.nim.demo.R;
import com.netease.nim.demo.avchat.activity.AVChatActivity;
import com.netease.nim.demo.common.util.sys.SysInfoUtil;
import com.netease.nim.demo.config.preference.Preferences;
import com.netease.nim.demo.main.model.Extras;
import com.netease.nim.uikit.cache.DataCacheManager;
import com.netease.nim.uikit.common.activity.TActivity;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.netease.nim.uikit.common.util.string.MD5;
import com.netease.nimlib.sdk.AbortableFuture;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.NimIntent;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.auth.AuthService;
import com.netease.nimlib.sdk.auth.LoginInfo;
import com.netease.nimlib.sdk.msg.model.IMMessage;

import java.util.ArrayList;
import java.util.List;

/**
 * 欢迎/导航页（app启动Activity）
 */
public class WelcomeActivity extends TActivity {

    private static final String TAG = "WelcomeActivity";

    private boolean customSplash = false;

    private static boolean firstEnter = true; // 是否首次进入

    /*SAMC_BEGIN()*/
    public static int SAM_LAUNCHER_TIMEOUT=20000;
    public static final int MSG_AUTOLOGIN_CALLBACK = 1;
    private AbortableFuture<LoginInfo> loginRequest;
    /*SAMC_END()*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_welcome);

        if (savedInstanceState != null) {
            setIntent(new Intent()); 
        }
        if (!firstEnter) {
            onIntent();
        } else {
            showSplashView();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (firstEnter) {
            firstEnter = false;
	     /*SAMC_BEGIN()*/
	     SamService.getInstance(this).initSamService();
	     /*SAMC_END()*/
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    if (canAutoLogin()) {
                        //onIntent();
                    } else {
                        launchSignInActivity();
                        finish();
                    }
                }
            };
            if (customSplash) {
                new Handler().postDelayed(runnable, 1000);
            } else {
                runnable.run();
            }
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        /**
         * 如果Activity在，不会走到onCreate，而是onNewIntent，这时候需要setIntent
         * 场景：点击通知栏跳转到此，会收到Intent
         */
        setIntent(intent);
        onIntent();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, 0);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.clear();
    }

    // 处理收到的Intent
    private void onIntent() {
        LogUtil.i(TAG, "onIntent...");

        if (TextUtils.isEmpty(DemoCache.getAccount())) {
	      LogUtil.i("test", "onIntent isEmpty");
            // 判断当前app是否正在运行
            if (!SysInfoUtil.stackResumed(this)) {
		   /*SAMC_BEGIN()*/
                //LoginActivity.start(this);
                /*SAMC_END()*/
            }
	      /*SAMC_BEGIN()*/
            //finish();
            /*SAMC_END()*/
        } else {
            // 已经登录过了，处理过来的请求
            Intent intent = getIntent();
            if (intent != null) {
                if (intent.hasExtra(NimIntent.EXTRA_NOTIFY_CONTENT)) {
                    parseNotifyIntent(intent);
                    return;
                } else if (intent.hasExtra(Extras.EXTRA_JUMP_P2P) || intent.hasExtra(AVChatActivity.INTENT_ACTION_AVCHAT)) {
                    parseNormalIntent(intent);
                }
            }

            if (!firstEnter && intent == null) {
                finish();
            } else {
                showMainActivity();
            }
        }
    }

    	

    /**
     * 已经登陆过，自动登陆
     */
    /*SAMC_BEGIN()*/

	private void onLoginDone() {
		loginRequest = null;
	}

	private String tokenFromPassword(String password) {
		String appKey = readAppKey(this);
		boolean isDemo = "45c6af3c98409b18a84451215d0bdd6e".equals(appKey)
                || "fe416640c8e8a72734219e1847ad2547".equals(appKey);

		return isDemo ? MD5.getStringMD5(password) : password;
	}

	private static String readAppKey(Context context) {
		try {
			ApplicationInfo appInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
			if (appInfo != null) {
				return appInfo.metaData.getString("com.netease.nim.appKey");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private void login_nim(){

		SamService.getInstance().startWaitThread();

		final String userName = SamService.getInstance().get_current_user().geteasemob_username();
		final String password = SamService.getInstance().get_current_user().getpassword();

		Preferences.saveUserAccount(userName);
		Preferences.saveUserToken(tokenFromPassword(password));
		

		DemoCache.getApp().NimInit();

		onIntent();
	}

	private void launchSignInActivity()
	{
		Intent newIntent = new Intent(this,SignInActivity.class);
		int intentFlags = Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP;
		newIntent.setFlags(intentFlags);
		startActivity(newIntent);
		finish();
	}

	private void invalideAllLoginRecord(){
		SamService.getInstance().getDao().clear_LoginUser_db();
	}

    	Handler mHandler = new Handler() {
	    @Override
	    public void handleMessage(Message msg) {
	    	 switch(msg.what) {
	            case MSG_AUTOLOGIN_CALLBACK:
	            	if(msg.arg1==SignService.R_AUTO_SIGN_IN_OK){
				login_nim();
	            	}else if(msg.arg1==SignService.R_AUTO_SIGN_IN_FAILED){
				if(msg.arg2 == SignService.RET_SI_FROM_SERVER_UP_ERROR){
					//maybe username or password is incorrect
					invalideAllLoginRecord();
					clearPreferences();
	            			launchSignInActivity();
				}else if(msg.arg2 == SignService.RET_SI_FROM_CLIENT_CONNECT_FAILED){
					//network is not available, just run into main
					invalideAllLoginRecord();
					clearPreferences();
	            			launchSignInActivity();
				}else{
					invalideAllLoginRecord();
					clearPreferences();
	            			launchSignInActivity();
				}
				
	            	}
			break;
	        }
	    }
	 };
	

    private boolean canAutoLogin() {
        List<LoginUser> userList = SamService.getInstance().getDao().query_AllLoginUser_db();
        if(userList.size() == 0){
	     clearPreferences();
            return false;
        }else{
            SignService.getInstance().attemptAutoSignIn(mHandler, MSG_AUTOLOGIN_CALLBACK);
	     return true;
        }
    }

    private void clearPreferences(){
	     Preferences.saveUserToken("");
	     Preferences.saveUserAccount("");
    }
    /*SAMC_END()*/

    private void parseNotifyIntent(Intent intent) {
        ArrayList<IMMessage> messages = (ArrayList<IMMessage>) intent.getSerializableExtra(NimIntent.EXTRA_NOTIFY_CONTENT);
        if (messages == null || messages.size() > 1) {
            showMainActivity(null);
        } else {
            /*SAMC_BEGIN()*/
            //showMainActivity(new Intent().putExtra(NimIntent.EXTRA_NOTIFY_CONTENT, messages.get(0)));
            showMainActivity(null);
            /*SAMC_END()*/
        }
    }

    private void parseNormalIntent(Intent intent) {
        showMainActivity(intent);
    }

    /**
     * 首次进入，打开欢迎界面
     */
    private void showSplashView() {
        getWindow().setBackgroundDrawableResource(R.drawable.splash_bg);
        customSplash = true;
    }

    private void showMainActivity() {
        showMainActivity(null);
    }

    private void showMainActivity(Intent intent) {
        MainActivity.start(WelcomeActivity.this, intent);
        finish();
    }
}
