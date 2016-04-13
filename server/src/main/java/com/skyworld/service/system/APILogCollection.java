package com.skyworld.service.system;

import java.io.IOException;
import java.util.Collection;

import javax.servlet.http.Part;

import org.json.JSONObject;

import com.skyworld.service.APIBasicJsonPartApiService;
import com.skyworld.service.APICode;
import com.skyworld.service.PartsWrapper;
import com.skyworld.service.resp.BasicResponse;
import com.skyworld.service.resp.RTCodeResponse;
import com.skyworld.utils.GlobalPath;

public class APILogCollection extends APIBasicJsonPartApiService {

	@Override
	protected BasicResponse service(JSONObject json, PartsWrapper partwrapper) {
		
		
		try {
			Collection<Part> cl = partwrapper.getParts();
			if (cl != null && cl.size() > 0) {
				for (Part p : cl) {
					p.write(GlobalPath.getLogCollectionPath()+"/" + p.getName());
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			return new RTCodeResponse(APICode.HANDLER_STREAM_FAILED);
		}
		
		return new RTCodeResponse(APICode.SUCCESS);
	}
	
	

}
