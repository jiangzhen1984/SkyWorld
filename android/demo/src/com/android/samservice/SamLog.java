package com.android.samservice;

import android.util.Log;

public class SamLog {
	public static void e(String Tag,String data){
		if(SamService.DEBUG) Log.e(Tag,data);
	}
	
	public static void i(String Tag,String data){
		//return;
		if(SamService.DEBUG) Log.i(Tag,data);
	}
	
	public static void w(String Tag,String data){
		if(SamService.DEBUG) Log.w(Tag,data);
	}

	public static void ship(String Tag,String data){
		Log.e(Tag,data);
	}
}
