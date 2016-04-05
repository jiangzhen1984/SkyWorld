package com.skyworld.service.user;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.skyworld.cache.CacheManager;
import com.skyworld.cache.TokenFactory;
import com.skyworld.init.GlobalConstants;
import com.skyworld.service.APICode;
import com.skyworld.service.APIService;
import com.skyworld.service.ServiceFactory;
import com.skyworld.service.dsf.User;
import com.skyworld.service.resp.BasicResponse;
import com.skyworld.service.resp.RTCodeResponse;
import com.skyworld.service.resp.UpdateAvatarResponse;
import com.skyworld.utils.GlobalPath;
import com.skyworld.utils.ImageUtil;

public class APIUpdateAvatarService implements APIService {

	protected Log log = LogFactory.getLog(this.getClass());
	
	private static final int TYPE_ORIGIN = 1;
	
	
	
	
	@Override
	public void service(HttpServletRequest req, HttpServletResponse resp) {
		
		Collection<Part> parts = null;
		try {
			parts = (Collection<Part>)req.getParts();
		} catch (Exception e) {
			e.printStackTrace();
			writeResponse(new RTCodeResponse(APICode.UPDATE_AVATAR_SIZE_EXCEED_MAX_LIMITION), req, resp);
			return;
		} 
		if (parts == null || parts.size() <=0) {
			writeResponse(new RTCodeResponse(APICode.REQUEST_PARAMETER_NOT_STISFIED), req, resp);
			return;
		}
		
		String data = req.getParameter("data");
		log.info(" request ==> data =  " + data);
		JSONObject root = null;
		if (data == null || (root = parse(data)) == null) {
			writeResponse(new RTCodeResponse(APICode.REQUEST_PARAMETER_INVALID), req, resp);
			return;
		}
		
		JSONObject header = root.getJSONObject("header");
		if (!header.has("action")) {
			writeResponse(new RTCodeResponse(APICode.REQUEST_PARAMETER_NOT_STISFIED), req, resp);
			return;
		}
		
		writeResponse(service(root, parts), req, resp);
	}


	protected BasicResponse service(JSONObject json, Collection<Part> parts) {
		JSONObject header = json.getJSONObject("header");
		JSONObject body = json.getJSONObject("body");

		if (!body.has("type")) {
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
		if (user == null) {
			return new RTCodeResponse(APICode.TOKEN_INVALID);
		}
		int opt = body.getInt("type");
		switch (opt) {
		case TYPE_ORIGIN:
			InputStream in = null;
			try {
				Part p =  parts.iterator().next();
				if (p.getSize() > GlobalConstants.MAX_AVATAR_SIZE) {
					return new RTCodeResponse(APICode.UPDATE_AVATAR_SIZE_EXCEED_MAX_LIMITION);
				}
				in = p.getInputStream();
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
		
		String filename = "origin_"+System.currentTimeMillis()+".png";
		String imageDir = GlobalPath.getAvatarHome();
		String contextPath = GlobalPath.getAvatarContext();
		log.info("write avatar to :"  +  (imageDir+"/" + filename));
		boolean ret = ImageUtil.copyImage(in, imageDir+"/" + filename);
		if (!ret) {
			return new RTCodeResponse(APICode.HANDLER_STREAM_FAILED);
		}
		user.setAvatarPath(contextPath+filename);
		ServiceFactory.getESUserService().updateUserAvatar(user);
		return new UpdateAvatarResponse(user);
	}
	
	

	private void writeResponse(BasicResponse response, HttpServletRequest req, HttpServletResponse resp) {
		resp.setCharacterEncoding("utf8");
		String strResp = response.getResponse();
		resp.setContentType("application/json");
		resp.setContentLength(strResp.length());
		try {
			resp.getWriter().write(strResp);
			resp.flushBuffer();
		} catch (IOException e) {
			log.error(e);
		}
		return;
	}
	
	
	protected JSONObject parse(String json) {
		JSONObject root = new JSONObject();
		JSONTokener jsonParser = new JSONTokener(json);
		JSONObject request = (JSONObject) jsonParser.nextValue();
		JSONObject header = request.getJSONObject("header");
		JSONObject body = request.getJSONObject("body");
		if (header == null || body == null) {
			return null;
		}
		
		root.put("header", header);
		root.put("body", body);
		return root;
	}

}
