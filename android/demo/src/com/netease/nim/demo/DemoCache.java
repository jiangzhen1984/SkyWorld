package com.netease.nim.demo;

import android.content.Context;

import com.netease.nim.uikit.NimUIKit;
import com.netease.nimlib.sdk.StatusBarNotificationConfig;

/**
 * Created by jezhee on 2/20/15.
 */
public class DemoCache {

    private static Context context;

    private static String account;

    private static StatusBarNotificationConfig notificationConfig;

    /*SAMC_BEGIN()*/
    private static NimApplication app;
    private static boolean firstEntry=true;
    /*SAMC_END()*/

    public static void clear() {
        account = null;
    }

    public static String getAccount() {
        return account;
    }

    public static void setAccount(String account) {
        DemoCache.account = account;
        NimUIKit.setAccount(account);
    }

    public static void setNotificationConfig(StatusBarNotificationConfig notificationConfig) {
        DemoCache.notificationConfig = notificationConfig;
    }

    public static StatusBarNotificationConfig getNotificationConfig() {
        return notificationConfig;
    }

    public static Context getContext() {
        return context;
    }

    public static void setContext(Context context) {
        DemoCache.context = context.getApplicationContext();
    }

    /*SAMC_BEGIN()*/
    public static void setApp(NimApplication app){
         DemoCache.app = app;
    }

    public static NimApplication getApp(){
         return DemoCache.app;
    }

    public static void setFirstEntry(boolean first){
         DemoCache.firstEntry = first;
    }

    public static boolean getFirstEntry(){
         return DemoCache.firstEntry;
    }
    /*SAMC_END()*/
}
