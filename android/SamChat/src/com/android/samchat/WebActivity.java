package com.android.samchat;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.samservice.HttpCommClient;
import com.android.samservice.SamService;
import com.easemob.chat.EMContactManager;
import com.easemob.easeui.utils.EaseUserUtils;
import com.easemob.exceptions.EaseMobException;

public class WebActivity extends Activity {

	private ValueCallback<Uri> mUploadMessage;
	public final static int FILECHOOSER_RESULTCODE = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		WebView webview = new WebView(this);
 		setContentView(webview);
          
           	WebSettings settings = webview.getSettings();
		settings.setAllowFileAccess(true);
		settings.setBuiltInZoomControls(true);
		settings.setBuiltInZoomControls(true);
		settings.setJavaScriptEnabled(true);

		webview.setWebChromeClient(new WebChromeClient()  
    		{  
           //The undocumented magic method override  
           //Eclipse will swear at you if you try to put @Override here  
        // For Android 3.0+
        public void openFileChooser(ValueCallback<Uri> uploadMsg) {  

            mUploadMessage = uploadMsg;  
            Intent i = new Intent(Intent.ACTION_GET_CONTENT);  
            i.addCategory(Intent.CATEGORY_OPENABLE);  
            i.setType("image/*");  
            WebActivity.this.startActivityForResult(Intent.createChooser(i,"File Chooser"), FILECHOOSER_RESULTCODE);  

           }

        // For Android 3.0+
           public void openFileChooser( ValueCallback uploadMsg, String acceptType ) {
           mUploadMessage = uploadMsg;
           Intent i = new Intent(Intent.ACTION_GET_CONTENT);
           i.addCategory(Intent.CATEGORY_OPENABLE);
           i.setType("*/*");
           WebActivity.this.startActivityForResult(
           Intent.createChooser(i, "File Browser"),
           FILECHOOSER_RESULTCODE);
           }

        //For Android 4.1
           public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture){
               mUploadMessage = uploadMsg;  
               Intent i = new Intent(Intent.ACTION_GET_CONTENT);  
               i.addCategory(Intent.CATEGORY_OPENABLE);  
               i.setType("image/*");  
               WebActivity.this.startActivityForResult( Intent.createChooser( i, "File Chooser" ), FILECHOOSER_RESULTCODE );

           }

    });

		
		Map<String,String> extraHeaders = new HashMap<String, String>();
		extraHeaders.put("Authorization", SamService.getInstance().get_current_token());
		webview.loadUrl(HttpCommClient.URL_VENDOR_WEB, extraHeaders);
		

	}


	@Override
      protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
          if (requestCode == FILECHOOSER_RESULTCODE) {
              if (null == mUploadMessage)
                  return;
              Uri result = intent == null || resultCode != RESULT_OK ? null : intent.getData();
              mUploadMessage.onReceiveValue(result);
              mUploadMessage = null;
          }
      }
 


}





