package com.skyworld.service.resp;

import org.json.JSONObject;

import com.skyworld.service.APICode;
import com.skyworld.service.dsf.AndroidAppVersion;
import com.skyworld.service.dsf.IOSAppVersion;

public class VersionResponse extends JSONBasicResponse {
	
	AndroidAppVersion av;
	
	IOSAppVersion iv;
	
	

	public VersionResponse(AndroidAppVersion av) {
		super();
		this.av = av;
	}



	public VersionResponse(IOSAppVersion iv) {
		super();
		this.iv = iv;
	}



	@Override
	public JSONObject getResponseJSON() {
		JSONObject root = new JSONObject();
		root.put("ret", APICode.SUCCESS);
		if (av != null) {
			JSONObject android = new JSONObject();
			android.put("number", av.getNumber());
			android.put("url", av.getFilename());
			android.put("force", av.isForce());
			android.put("type", av.getType());
			
			root.put("android", android);
		}
		
		if (iv != null) {
			JSONObject ios = new JSONObject();
			ios.put("number", iv.getNumber());
			ios.put("url", iv.getFilename());
			ios.put("force", iv.isForce());
			ios.put("type", iv.getType());
			
			root.put("ios", ios);
		}
		return root;
	}
	
	
	


}
