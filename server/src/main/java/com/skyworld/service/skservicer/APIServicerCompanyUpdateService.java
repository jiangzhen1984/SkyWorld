package com.skyworld.service.skservicer;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Iterator;

import javax.servlet.http.Part;

import org.json.JSONObject;

import com.skyworld.cache.CacheManager;
import com.skyworld.cache.Token;
import com.skyworld.cache.TokenFactory;
import com.skyworld.service.APIBasicJsonPartApiService;
import com.skyworld.service.APICode;
import com.skyworld.service.PartsWrapper;
import com.skyworld.service.ServiceFactory;
import com.skyworld.service.dsf.SKServicer;
import com.skyworld.service.resp.BasicResponse;
import com.skyworld.service.resp.RTCodeResponse;
import com.skyworld.service.resp.RegisterResponse;
import com.skyworld.utils.GlobalPath;
import com.skyworld.utils.ImageUtil;

public class APIServicerCompanyUpdateService extends APIBasicJsonPartApiService {

	@Override
	protected BasicResponse service(JSONObject json,  PartsWrapper partwrapper) {
		JSONObject header = json.getJSONObject("header");
		JSONObject body = json.getJSONObject("body");
		
		if (!header.has("token")) {
			return new RTCodeResponse(APICode.REQUEST_PARAMETER_NOT_STISFIED);
		}
		String tokenId = header.getString("token");
		if (tokenId == null || tokenId.trim().isEmpty()) {
			return new RTCodeResponse(APICode.REQUEST_PARAMETER_NOT_STISFIED);
		}
		
		Token token = TokenFactory.valueOf(tokenId);
		SKServicer servicer = CacheManager.getIntance().getSKServicer(token);
		if (servicer == null) {
			return new RTCodeResponse(APICode.TOKEN_INVALID);
		}
		
		
		boolean ret;
		try {
			ret = handlePart(servicer,  partwrapper.getParts());
			if (!ret) {
				return new RTCodeResponse(APICode.HANDLER_STREAM_FAILED);
			}
		} catch (IOException e) {
			e.printStackTrace();
			return new RTCodeResponse(APICode.HANDLER_STREAM_FAILED);
		}
		
		populateCmpData(servicer, body);
		
		ServiceFactory.getESKServicerService().updateCompanyInfo(servicer);
		
		
		return new RegisterResponse(servicer, token);
	}

	
	
	private void populateCmpData(SKServicer servicer, JSONObject body) {
		if (body.has("cmp_name")) {
			servicer.setCmpName(body.getString("cmp_name"));
		}
		
		if (body.has("cmp_desc")) {
			servicer.setCmpDesc(body.getString("cmp_desc"));
		}
		
		if (body.has("cmp_website")) {
			servicer.setWebsite(body.getString("cmp_website"));
		}
		
		if (body.has("cmp_phone")) {
			servicer.setCmpPhone(body.getString("cmp_phone"));
		}
	}
	

	private boolean handlePart(SKServicer servicer, Collection<Part> parts) {
		if (parts == null || parts.size() <= 0) {
			return true;
		}
		
		Iterator<Part> its = parts.iterator();
		int index = 1;
		while (its.hasNext()) {
			Part p = its.next();
			
			if (p == null || p.getContentType() == null) {
				continue;
			}
			//TODO handle video
			
			if (!p.getContentType().toLowerCase().startsWith("image/")) {
				log.error("Found non-image type part: " + p.getContentType());
				continue;
			}
			String filename = "servicer_cmp_"+servicer.getId()+"_"+System.currentTimeMillis()+"_" + index +".png";
			String imageDir = GlobalPath.getSKServicerHome();
			String contextPath = GlobalPath.getSKServicerContext();
			log.info("write article image to :"  +  (imageDir+"/" + filename));
			InputStream in = null;
			try {
				in = p.getInputStream();
				boolean ret = ImageUtil.copyImage(in, imageDir+"/" + filename);
				if (!ret) {
					return false;
				}
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			} finally {
				if (in != null) {
					try {
						in.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				} 
					
			}
			servicer.setLogoPath(contextPath+filename);
			break;
		}
		
		return true;
	}
}
