package com.android.samchat;

import android.app.Activity;
import android.app.ProgressDialog;

public class SamProcessDialog {
	private ProgressDialog mProgressDialog;
	
	public void launchProcessDialog (Activity activity_owner,String msg)
	{
	
		mProgressDialog = new ProgressDialog(activity_owner);
		// 设置mProgressDialog风格
		mProgressDialog.setProgress(ProgressDialog.STYLE_SPINNER);//圆形
		mProgressDialog.setProgress(ProgressDialog.STYLE_HORIZONTAL);//水平
		// 设置mProgressDialog标题
		//mProgressDialog.setTitle("title");
		// 设置mProgressDialog提示
		mProgressDialog.setMessage(msg);
		// 设置mProgressDialog进度条的图标
		//mProgressDialog.setIcon(R.drawable.dialog_warning); 
		// 设置mProgressDialog的进度条是否不明确
		//不滚动时，当前值在最小和最大值之间移动，一般在进行一些无法确定操作时间的任务时作为提示，明确时就是根据你的进度可以设置现在的进度值
		mProgressDialog.setIndeterminate(false);
		//mProgressDialog.setProgress(m_count++);
		// 是否可以按回退键取消
		mProgressDialog.setCancelable(false);
		// 设置mProgressDialog的一个Button
		mProgressDialog.show();
	}
	
	public void dismissPrgoressDiglog()
	{
		if(mProgressDialog!=null){
			mProgressDialog.dismiss();
		}
	}
}
