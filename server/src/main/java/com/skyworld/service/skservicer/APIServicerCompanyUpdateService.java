package com.skyworld.service.skservicer;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import com.skyworld.service.dsf.SKServicer.SKServicerCMPItem;
import com.skyworld.service.resp.BasicResponse;
import com.skyworld.service.resp.RTCodeResponse;
import com.skyworld.service.resp.RegisterResponse;
import com.skyworld.service.resp.SKServicerCmpQueryResponse;
import com.skyworld.utils.GlobalPath;
import com.skyworld.utils.ImageUtil;

public class APIServicerCompanyUpdateService extends APIBasicJsonPartApiService {

	private static final int OPT_UPDATE_CMP = 1;
	private static final int OPT_ADD_CMP_ITEM = 2;
	private static final int OPT_REMOVE_CMP_ITEM = 3;

	@Override
	protected BasicResponse service(JSONObject json, PartsWrapper partwrapper) {
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
		if (!body.has("opt")) {
			return new RTCodeResponse(APICode.REQUEST_PARAMETER_NOT_STISFIED);
		}
		int opt = body.getInt("opt");
		switch (opt) {
		case OPT_UPDATE_CMP:
			return updateCmpInfor(body, servicer, token, partwrapper);
		case OPT_ADD_CMP_ITEM:
			return addCmpItem(body, servicer, partwrapper);
		case OPT_REMOVE_CMP_ITEM:
			return removeCmpItem(body, servicer, partwrapper);
		default:
			return new RTCodeResponse(
					APICode.SKSERVICER_UPDATE_ERROR_NOT_SUPPORT_OPT);
		}

	}

	private BasicResponse updateCmpInfor(JSONObject json, SKServicer servicer,
			Token token, PartsWrapper partwrapper) {

		boolean ret;
		try {
			ret = handlePart(servicer, partwrapper.getParts());
			if (!ret) {
				return new RTCodeResponse(APICode.HANDLER_STREAM_FAILED);
			}
		} catch (IOException e) {
			e.printStackTrace();
			return new RTCodeResponse(APICode.HANDLER_STREAM_FAILED);
		}

		populateCmpData(servicer, json);

		ServiceFactory.getESKServicerService().updateCompanyInfo(servicer);
		return new RegisterResponse(servicer, token);
	}

	private BasicResponse addCmpItem(JSONObject json, SKServicer servicer,
			PartsWrapper partwrapper) {
		String title = null;
		String content = null;
		if (json.has("title")) {
			title = json.getString("title");
		}
		if (json.has("content")) {
			content = json.getString("content");
		}
		String picUrl = extraImg(content);
		log.info(picUrl);
		SKServicerCMPItem cmpItem = servicer.new SKServicerCMPItem(title, picUrl, content);
		ServiceFactory.getESKServicerService().addCmpItem(servicer, cmpItem);
		return new SKServicerCmpQueryResponse(servicer, cmpItem);
	}

	private BasicResponse removeCmpItem(JSONObject json, SKServicer servicer,
			PartsWrapper partwrapper) {
		return null;
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
			if (!p.getContentType().toLowerCase().startsWith("image/")) {
				log.error("Found non-image type part: " + p.getContentType());
				continue;
			}
			String filename = "servicer_cmp_" + servicer.getId() + "_"
					+ System.currentTimeMillis() + "_" + index + ".png";
			String imageDir = GlobalPath.getSKServicerHome();
			String contextPath = GlobalPath.getSKServicerContext();
			log.info("write article image to :" + (imageDir + "/" + filename));
			InputStream in = null;
			try {
				in = p.getInputStream();
				boolean ret = ImageUtil
						.copyImage(in, imageDir + "/" + filename);
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
			servicer.setLogoPath(contextPath + filename);
			break;
		}

		return true;
	}
	
	
	private String extraImg(String str) {
		Pattern p = Pattern.compile("(<img)(.)+(/>)");
		Matcher m =p.matcher(str);
		String url = null;
		if (m.find()) {
			String img = m.group();
			p = Pattern.compile("(src=\")([\\w\\.\\/]+)(\")");
			m = p.matcher(img);
			if (m.find()) {
				img = m.group();
				url = img.substring(5, img.length() -1);
			}
		}
		
		
		return url;
	}
}
