package com.skyworld.utils;

import java.io.File;
import java.util.Calendar;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.skyworld.init.GlobalConstants;

public class GlobalPath {
	
	protected static Log log = LogFactory.getLog(GlobalPath.class);
	
	public static String getServerHome() {
		String home = System.getProperty("catalina.home", null);
		if (home == null) {
			throw new NullPointerException("Didn't find  catalina home");
		} else {
			return home;
		}
	}
	
	
	public static String getArticlePicHome() {
		return getSubHome(GlobalConstants.ARTICLE_CONTEXT);
	}
	
	
	public static String getArticlePicContext() {
		Calendar c = Calendar.getInstance();
		return c.get(Calendar.YEAR)+"/"+ (c.get(Calendar.MONTH) + 1)+"/"+c.get(Calendar.DAY_OF_MONTH)+"/";
	}
	
	
	public static String getAvatarHome() {
		return getSubHome(GlobalConstants.AVATAR_CONTEXT);
	}
	
	
	public static String getAvatarContext() {
		Calendar c = Calendar.getInstance();
		return c.get(Calendar.YEAR)+"/"+ (c.get(Calendar.MONTH) + 1)+"/"+c.get(Calendar.DAY_OF_MONTH)+"/";
	}
	
	
	
	
	public static String getSKServicerHome() {
		return getSubHome(GlobalConstants.SKSERVICER_CONTEXT);
	}
	
	
	public static String getSKServicerContext() {
		Calendar c = Calendar.getInstance();
		return c.get(Calendar.YEAR)+"/"+ (c.get(Calendar.MONTH) + 1)+"/"+c.get(Calendar.DAY_OF_MONTH)+"/";
	}

	
	
	
	public static String getSubHome(String context) {
		String home = getServerHome();
		Calendar c = Calendar.getInstance();
		String contextPath =  c.get(Calendar.YEAR)+"/"+ (c.get(Calendar.MONTH) + 1)+"/"+c.get(Calendar.DAY_OF_MONTH)+"/";
		File imageDir = new File(home + "/webapps/"+context+"/" + contextPath);
		if (!imageDir.exists()) {
			boolean ret = imageDir.mkdirs();
			log.info("Create dir ret:" + ret+"   ===>" + imageDir.getAbsolutePath());
		}
		
		return imageDir.getAbsolutePath();
	}
}
