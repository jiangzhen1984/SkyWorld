package com.skyworld.utils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ImageUtil {
	
	protected static Log log = LogFactory.getLog(ImageUtil.class);

	public static boolean copyImage(InputStream in, String destImage) {
		OutputStream out = null;
		byte[] buf = new byte[2048];
		int n = -1;
		try {
			out = new FileOutputStream(destImage);
			while((n = in.read(buf, 0, 2048)) != -1) {
				out.write(buf, 0, n);
			}
		} catch (IOException e) {
			log.error(" copy image failed ",e);
			return false;
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return true;
		
	}
}
