package com.skyworld.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.Collection;

import javax.servlet.http.Part;

import org.json.JSONObject;

import com.skyworld.cache.CacheManager;
import com.skyworld.cache.TokenFactory;
import com.skyworld.service.dsf.User;
import com.skyworld.service.resp.BasicResponse;
import com.skyworld.service.resp.RTCodeResponse;
import com.skyworld.service.resp.UpdateAvatarResponse;

public class APIUpdateAvatarService extends APIBasicJsonApiService {

	
	private static final int TYPE_ORIGIN = 1;
	
	@Override
	protected BasicResponse service(JSONObject json) {
		JSONObject header = json.getJSONObject("header");
		JSONObject body = json.getJSONObject("body");

		if (!body.has("type")) {
			return new RTCodeResponse(APICode.REQUEST_PARAMETER_NOT_STISFIED);
		}
		

		Collection<Part> parts = (Collection<Part>)json.get("parts");
		if (parts.size() <=0) {
			return new RTCodeResponse(APICode.REQUEST_PARAMETER_NOT_STISFIED);
		}
		

		if (!header.has("token")) {
			return new RTCodeResponse(APICode.REQUEST_PARAMETER_NOT_STISFIED);
		}

		String tokenId = header.getString("token");
		if (tokenId == null || tokenId.trim().isEmpty()) {
			return new RTCodeResponse(APICode.REQUEST_PARAMETER_NOT_STISFIED);
		}
		
		User user = CacheManager.getIntance().getUser(TokenFactory.valueOf(tokenId));
		
		int opt = body.getInt("type");
		switch (opt) {
		case TYPE_ORIGIN:
			InputStream in = null;
			try {
				in =  parts.iterator().next().getInputStream();
				return handleUpdateAvatarOrigin(user,  in);
			} catch (IOException e) {
				e.printStackTrace();
				return new RTCodeResponse(APICode.UPDATE_AVATAR_ERROR);
			} finally {
				if (in != null) {
					try {
						in.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		default:
			return new RTCodeResponse(APICode.UPDATE_AVATAR_TYPE_NOT_SUPPORT);
		}
	}
	
	
	private BasicResponse handleUpdateAvatarOrigin(User user, InputStream in) throws IOException {
		String home = System.getProperty("catalina.home", null);
		if (home == null) {
			throw new NullPointerException("Didn't find  catalina home");
		}
		Calendar c = Calendar.getInstance();
		String contextPath =  c.get(Calendar.YEAR)+"/"+ c.get(Calendar.MONTH)+"/"+c.get(Calendar.DAY_OF_MONTH)+"/";
		File imageDir = new File(home + "/webapps/avatar/" + contextPath);
		if (!imageDir.exists()) {
			boolean ret = imageDir.mkdirs();
			log.info("Create dir ret:" + ret+"   ===>" + imageDir.getAbsolutePath());
		}
		
		String filename = "origin_"+System.currentTimeMillis()+".png";
		OutputStream out = null;
		File image = new File(imageDir.getAbsoluteFile()+filename);
		byte[] buf = new byte[2048];
		int n = -1;
		try {
			out = new FileOutputStream(image);
			while((n = in.read(buf, 0, 2048)) != -1) {
				out.write(buf, 0, n);
			}
		} catch (IOException e) {
			throw e;
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		user.setAvatarPath(contextPath+filename);
		ServiceFactory.getESUserService().updateUserAvatar(user);
		return new UpdateAvatarResponse(user);
	}

}
