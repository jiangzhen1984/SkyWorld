package com.android.samchat;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Message;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.GeolocationPermissions;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebStorage;
import android.webkit.WebView;
abstract class SamChatWebChromeClient extends WebChromeClient {
  private WebChromeClient mWrappedClient;
  private ValueCallback<Uri> mUploadMessage;
  private Context context;
     protected SamChatWebChromeClient(WebChromeClient wrappedClient) {
         mWrappedClient = wrappedClient;
     }
     /** } */
     @Override
     public void onProgressChanged(WebView view, int newProgress) {
         mWrappedClient.onProgressChanged(view, newProgress);
     }
     /** } */
     @Override
     public void onReceivedTitle(WebView view, String title) {
         mWrappedClient.onReceivedTitle(view, title);
     }
     /** } */
     @Override
     public void onReceivedIcon(WebView view, Bitmap icon) {
         mWrappedClient.onReceivedIcon(view, icon);
     }
     /** } */
     @Override
     public void onReceivedTouchIconUrl(WebView view, String url,
             boolean precomposed) {
         mWrappedClient.onReceivedTouchIconUrl(view, url, precomposed);
     }
     /** } */
     @Override
     public void onShowCustomView(View view, CustomViewCallback callback) {
         mWrappedClient.onShowCustomView(view, callback);
     }
     /** } */
     @Override
     public void onHideCustomView() {
         mWrappedClient.onHideCustomView();
     }
     /** } */
     @Override
     public boolean onCreateWindow(WebView view, boolean dialog,
             boolean userGesture, Message resultMsg) {
         return mWrappedClient.onCreateWindow(view, dialog, userGesture, resultMsg);
     }
     /** } */
     @Override
     public void onRequestFocus(WebView view) {
         mWrappedClient.onRequestFocus(view);
     }
     /** } */
     @Override
     public void onCloseWindow(WebView window) {
         mWrappedClient.onCloseWindow(window);
     }
     /** } */
     @Override
     public boolean onJsAlert(WebView view, String url, String message,
             JsResult result) {
         return mWrappedClient.onJsAlert(view, url, message, result);
     }
     /** } */
     @Override
     public boolean onJsConfirm(WebView view, String url, String message,
             JsResult result) {
         return mWrappedClient.onJsConfirm(view, url, message, result);
     }
     /** } */
     @Override
     public boolean onJsPrompt(WebView view, String url, String message,
             String defaultValue, JsPromptResult result) {
         return mWrappedClient.onJsPrompt(view, url, message, defaultValue, result);
     }
     /** } */
     @Override
     public boolean onJsBeforeUnload(WebView view, String url, String message,
             JsResult result) {
         return mWrappedClient.onJsBeforeUnload(view, url, message, result);
     }
     /** } */
     @Override
     public void onExceededDatabaseQuota(String url, String databaseIdentifier,
             long currentQuota, long estimatedSize, long totalUsedQuota,
             WebStorage.QuotaUpdater quotaUpdater) {
         mWrappedClient.onExceededDatabaseQuota(url, databaseIdentifier, currentQuota,
                 estimatedSize, totalUsedQuota, quotaUpdater);
     }
     /** } */
     @Override
     public void onReachedMaxAppCacheSize(long spaceNeeded, long totalUsedQuota,
             WebStorage.QuotaUpdater quotaUpdater) {
         mWrappedClient.onReachedMaxAppCacheSize(spaceNeeded, totalUsedQuota, quotaUpdater);
     }
     /** } */
     @Override
     public void onGeolocationPermissionsShowPrompt(String origin,
             GeolocationPermissions.Callback callback) {
         mWrappedClient.onGeolocationPermissionsShowPrompt(origin, callback);
     }
     /** } */
     @Override
     public void onGeolocationPermissionsHidePrompt() {
         mWrappedClient.onGeolocationPermissionsHidePrompt();
     }
     /** } */
     @Override
     public boolean onJsTimeout() {
         return mWrappedClient.onJsTimeout();
     }
     /** } */
     @Override
     @Deprecated
     public void onConsoleMessage(String message, int lineNumber, String sourceID) {
         mWrappedClient.onConsoleMessage(message, lineNumber, sourceID);
     }
     /** } */
     @Override
     public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
         return mWrappedClient.onConsoleMessage(consoleMessage);
     }
     /** } */
     @Override
     public Bitmap getDefaultVideoPoster() {
         return mWrappedClient.getDefaultVideoPoster();
     }
     /** } */
     @Override
     public View getVideoLoadingProgressView() {
         return mWrappedClient.getVideoLoadingProgressView();
     }
     /** } */
     @Override
     public void getVisitedHistory(ValueCallback<String[]> callback) {
         mWrappedClient.getVisitedHistory(callback);
     }
     /** } */
     
    /*public void openFileChooser(ValueCallback<Uri> uploadMsg,  
                    String acceptType, String capture) {  
                mUploadMessage = uploadMsg;  
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);  
                intent.addCategory(Intent.CATEGORY_OPENABLE);  
                intent.setType("image/*");  
                context.startActivityForResult(  
                        Intent.createChooser(intent, "完成操作需要使用"),  
                        WebActivity.FILECHOOSER_RESULTCODE);  
      
            }  
      
            // 3.0 + 调用这个方法  
            public void openFileChooser(ValueCallback<Uri> uploadMsg,  
                    String acceptType) {  
                mUploadMessage = uploadMsg;  
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);  
                intent.addCategory(Intent.CATEGORY_OPENABLE);  
                intent.setType("image/*");  
                context.startActivityForResult(  
                        Intent.createChooser(intent, "完成操作需要使用"),  
                        WebActivity.FILECHOOSER_RESULTCODE);  
            }  
      
            // Android < 3.0 调用这个方法  
            public void openFileChooser(ValueCallback<Uri> uploadMsg) {  
                mUploadMessage = uploadMsg;  
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);  
                intent.addCategory(Intent.CATEGORY_OPENABLE);  
                intent.setType("image/*");  
                context.startActivityForResult(  
                        Intent.createChooser(intent, "完成操作需要使用"),  
                        WebActivity.FILECHOOSER_RESULTCODE);  
      
            }  */
            /************** end ***************/  
}
