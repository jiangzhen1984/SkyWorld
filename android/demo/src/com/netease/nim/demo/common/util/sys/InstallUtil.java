package com.netease.nim.demo.common.util.sys;

import java.io.File;

import com.netease.nim.uikit.NimUIKit;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.text.TextUtils;

public class InstallUtil {
	private static final String TAG = "InstallUtil";

	private static int versionCode;

	private static String versionName;

	/**
	 * �Ƿ��Ѱ�װapp
	 * 
	 * @param context
	 * @param packageName
	 * @return
	 */
	public static boolean isAppInstalled(Context context, String packageName) {
		try {
			if (TextUtils.isEmpty(packageName))
				return false;
			return context.getPackageManager().getPackageInfo(packageName, PackageManager.GET_ACTIVITIES) != null;
		} catch (NameNotFoundException localNameNotFoundException) {
			return false;
		}
	}

	/**
	 * ��app
	 * 
	 * @param packageName
	 * @param context
	 */
	public static void openApp(Context context, String packageName) {
		PackageManager packageManager = context.getPackageManager();
		Intent intent = packageManager.getLaunchIntentForPackage(packageName);
		if (intent != null)
			context.startActivity(intent);
	}

	/**
	 * ĳ��app�İ汾�ţ�δ��װʱ����null
	 */
	public static final String getVersionName(Context context, String packageName) {
		try {
			PackageInfo pi = context.getPackageManager().getPackageInfo(packageName, 0);
			if (pi != null) {
				return pi.versionName;
			} else {
				return null;
			}
		} catch (NameNotFoundException e) {
			return null;
		}
	}

	public static final int getVersionCode(Context context) {
		if (versionCode == 0) {
			loadVersionInfo(context);
		}

		return versionCode;
	}

	/**
	 * ���Ű汾��
	 */
	public static final String getVersionName(Context context) {
		if (TextUtils.isEmpty(versionName)) {
			loadVersionInfo(context);
		}

		return versionName;
	}

	private static final void loadVersionInfo(Context context) {
		try {
			PackageInfo pi = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
			if (pi != null) {
				versionCode = pi.versionCode;
				versionName = pi.versionName;
			}
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * ��װapk�ļ�
	 */
	public static void installApk(String filepath) {
        NimUIKit.getContext().startActivity(getInstallApkIntent(filepath));
	}

	/**
	 * ��װapk�ļ�
	 */
	public static Intent getInstallApkIntent(String filepath) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		File file = new File(filepath);
		intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
		return intent;
	}
}
