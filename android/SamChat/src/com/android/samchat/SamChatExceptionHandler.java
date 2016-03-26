package com.android.samchat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.android.samservice.SamLog;
 
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

public class SamChatExceptionHandler implements UncaughtExceptionHandler {  
    public static final String TAG = "SamChatExceptionHandler";  
  
    // ϵͳĬ�ϵ�UncaughtException������  
    private Thread.UncaughtExceptionHandler mDefaultHandler;  
    // CrashHandlerʵ��  
    private static SamChatExceptionHandler INSTANCE = new SamChatExceptionHandler();  
    // �����Context����  
    private Context mContext;  
    // �����洢�豸��Ϣ���쳣��Ϣ  
    private Map<String, String> infos = new HashMap<String, String>();  
  
    // ���ڸ�ʽ������,��Ϊ��־�ļ�����һ����  
    private DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");  
    private String nameString;  
  
    /** ��ֻ֤��һ��CrashHandlerʵ�� */  
    private SamChatExceptionHandler() {  
    }  
  
    /** ��ȡCrashHandlerʵ�� ,����ģʽ */  
    public static SamChatExceptionHandler getInstance() {  
        return INSTANCE;  
    }  
  
    /** 
     * ��ʼ�� 
     *  
     * @param context 
     */  
    public void init(Context context) {  
        mContext = context;  
        // ��ȡϵͳĬ�ϵ�UncaughtException������  
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();  
        // ���ø�CrashHandlerΪ�����Ĭ�ϴ�����  
        Thread.setDefaultUncaughtExceptionHandler(this);  
        nameString = "samchat" ;
    }  
  
    /** 
     * ��UncaughtException����ʱ��ת��ú��������� 
     */  
    @Override  
    public void uncaughtException(Thread thread, Throwable ex) {  
        if (!handleException(ex) && mDefaultHandler != null) {  
            // ����û�û�д�������ϵͳĬ�ϵ��쳣������������  
            mDefaultHandler.uncaughtException(thread, ex);  
        } else {  
            try {  
                Thread.sleep(3000);  
            } catch (InterruptedException e) {  
                Log.e(TAG, "error : ", e);  
            }  
            // �˳�����  
            android.os.Process.killProcess(android.os.Process.myPid());  
            System.exit(1);  
        }  
    }  
  
    /** 
     * �Զ��������,�ռ�������Ϣ ���ʹ��󱨸�Ȳ������ڴ����. 
     *  
     * @param ex 
     * @return true:��������˸��쳣��Ϣ;���򷵻�false. 
     */  
    private boolean handleException(Throwable ex) {  
        if (ex == null) {  
            return false;  
        }  

	 /*new Thread() {  
            @Override  
            public void run() {  
                Looper.prepare();  
                Toast.makeText(mContext, "�ܱ�Ǹ,��������쳣,�����ռ���־�������˳�", Toast.LENGTH_LONG)  
                        .show();  
                Looper.loop();  
            }  
        }.start();*/  
        // �ռ��豸������Ϣ  
        collectDeviceInfo(mContext);  
        // ������־�ļ�  
        StringBuffer sb = getCrashInformation(ex); 

        uploadCrashInfo(sb);

        return false;  

    } 


     /** 
     * upload crash info to server
     */
    public void uploadCrashInfo(StringBuffer sb){
        SamLog.e(TAG,"upload crash info now ...");
        SamLog.e(TAG,sb.toString());
    }
	
  
    /** 
     * �ռ��豸������Ϣ 
     *  
     * @param ctx 
     */  
    public void collectDeviceInfo(Context ctx) {  
        try {  
            PackageManager pm = ctx.getPackageManager();  
            PackageInfo pi = pm.getPackageInfo(ctx.getPackageName(),  
                    PackageManager.GET_ACTIVITIES);  
            if (pi != null) {  
                String versionName = pi.versionName == null ? "null"  
                        : pi.versionName;  
                String versionCode = pi.versionCode + "";  
                infos.put("versionName", versionName);  
                infos.put("versionCode", versionCode);  
            }  
        } catch (NameNotFoundException e) {  
            
        }  

        String phoneInfo = "Product: " + android.os.Build.PRODUCT;
        phoneInfo += ", CPU_ABI: " + android.os.Build.CPU_ABI;
        phoneInfo += ", TAGS: " + android.os.Build.TAGS;
        phoneInfo += ", VERSION_CODES.BASE: " + android.os.Build.VERSION_CODES.BASE;
        phoneInfo += ", MODEL: " + android.os.Build.MODEL;
        phoneInfo += ", SDK: " + android.os.Build.VERSION.SDK;
        phoneInfo += ", VERSION.RELEASE: " + android.os.Build.VERSION.RELEASE;
        phoneInfo += ", DEVICE: " + android.os.Build.DEVICE;
        phoneInfo += ", DISPLAY: " + android.os.Build.DISPLAY;
        phoneInfo += ", BRAND: " + android.os.Build.BRAND;
        phoneInfo += ", BOARD: " + android.os.Build.BOARD;
        phoneInfo += ", FINGERPRINT: " + android.os.Build.FINGERPRINT;
        phoneInfo += ", ID: " + android.os.Build.ID;
        phoneInfo += ", MANUFACTURER: " + android.os.Build.MANUFACTURER;
        phoneInfo += ", USER: " + android.os.Build.USER;
        infos.put("phoneInfo",phoneInfo);
		
    }  
  
    /** 
     * ���������Ϣ���ļ��� 
     *  
     * @param ex 
     * @return �����ļ�����,���ڽ��ļ����͵������� 
     */  
    private StringBuffer getCrashInformation(Throwable ex) {  
        long timestamp = System.currentTimeMillis();  
        String time = formatter.format(new Date());  
        String date = time + "-" + timestamp;
        StringBuffer sb = new StringBuffer();  

        sb.append(date + "\n");                      
          
        for (Map.Entry<String, String> entry : infos.entrySet()) {  
            String key = entry.getKey();  
            String value = entry.getValue();  
            sb.append(key + "=" + value + "\n");  
        }  
  
        Writer writer = new StringWriter();  
        PrintWriter printWriter = new PrintWriter(writer);  
        ex.printStackTrace(printWriter);  
        Throwable cause = ex.getCause();  
        while (cause != null) {  
            cause.printStackTrace(printWriter);  
            cause = cause.getCause();  
        }  
        printWriter.close();  
        String result = writer.toString();  
        sb.append(result);
  
        return sb;  
    }  
  
}  