package com.skyworld.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.skyworld.cache.Token;
import com.skyworld.cache.TokenFactory;
import com.skyworld.service.resp.BasicResponse;
import com.skyworld.service.resp.RTCodeResponse;

/**
 * FXIME add property check structure for optimize
 * @author 28851274
 *
 */
public abstract class APIBasicJsonApiService implements APIService {

	protected Log log = LogFactory.getLog(this.getClass());
	
	private Map<String, APIBasicJsonApiService> mapping = new HashMap<String, APIBasicJsonApiService>();
	
	@Override
	public void service(HttpServletRequest req, HttpServletResponse resp) {
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
		// xcl 2016-4-7  调用具体实现类的service方法
		BasicResponse response = null;
		if((response = service(root)) != null){
			writeResponse(response, req, resp);
			return;
		}
		//
		String action = header.getString("action");
		APIBasicJsonApiService hanldeService = mapping.get(action);
		log.info(" action ==>   " + action+"    servicer===>" + hanldeService);
		if (hanldeService == null) {
			writeResponse(new RTCodeResponse(APICode.ACTION_NOT_SUPPORT), req, resp);
			return;
		}
	
		writeResponse(hanldeService.service(root), req, resp);
	}
	
	
	public void addActionMapping(String key, APIBasicJsonApiService service) {
		mapping.put(key, service);
	}
	
	
	
	protected Token checkAuth(JSONObject header) {
		if (!header.has("token")) {
			return null;
		}

		String tokenId = header.getString("token");
		if (tokenId == null || tokenId.trim().isEmpty()) {
			return null;
		}
		return TokenFactory.valueOf(tokenId);
	}
	
	
	protected abstract BasicResponse service(JSONObject json);

	
	
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
	
	
	
	private void writeResponse(BasicResponse response, HttpServletRequest req, HttpServletResponse resp) {
		resp.setCharacterEncoding("utf8");
		String strResp = response.getResponse();
		log.info("===> ["+Integer.toOctalString(strResp.length())+"]" + strResp);
		resp.setContentType("application/json");
		try {
			resp.getWriter().write(strResp);
			resp.flushBuffer();
		} catch (IOException e) {
			log.error(e);
		}
		return;
	}
}
