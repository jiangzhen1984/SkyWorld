package com.skyworld.service.system;

import org.json.JSONObject;

import com.skyworld.service.APIBasicJsonPartApiService;
import com.skyworld.service.APICode;
import com.skyworld.service.PartsWrapper;
import com.skyworld.service.ServiceFactory;
import com.skyworld.service.resp.BasicResponse;
import com.skyworld.service.resp.RTCodeResponse;
import com.skyworld.service.resp.VersionResponse;

public class APIAppService extends APIBasicJsonPartApiService {

	public static final int OPT_ALL_VER_QUERY = 0;
	public static final int OPT_ANDROID_VER_QUERY = 1;
	public static final int OPT_IOS_VER_QUERY = 2;

	@Override
	protected BasicResponse service(JSONObject json, PartsWrapper partwrapper) {
		JSONObject body = json.getJSONObject("body");
		if (!body.has("opt")) {
			return new RTCodeResponse(APICode.REQUEST_PARAMETER_NOT_STISFIED);
		}
		int opt = body.getInt("opt");
		switch (opt) {
		case OPT_ANDROID_VER_QUERY:
			return new VersionResponse(ServiceFactory.getSystemBasicService().getAndroidAppVersion());
		case OPT_IOS_VER_QUERY:
			return new VersionResponse(ServiceFactory.getSystemBasicService().getIOSAppVersion());
		default:
			return new RTCodeResponse(APICode.VVERSION_QUERY_ERROR_NOT_SUPPORT_OPT);
		}
	}

}
