package com.skyworld.easemob;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.skyworld.easemob.EaseMobDeamon.Configuration;
import com.skyworld.utils.HttpClientSSLBuilder;

public class EasemobContactsQuery implements Runnable {

	Log log = LogFactory.getLog(EasemobContactsQuery.class);
	
	EaseMobDeamon.Configuration config;
	private Callback callback;
	private EasemobUser user;
	
	
	
	public EasemobContactsQuery(Configuration config, EasemobUser user, Callback callback) {
		super();
		this.config = config;
		this.callback = callback;
		this.user = user;
	}


	public interface Callback {
		
		public void onError(int code);
		
		public void onCompleted(List<EasemobUser> contacts);
	};
	
	
	@Override
	public void run() {
		log.info("====== EasemobContactsQuery start["+user+"] =====");
		if (config == null) {
			log.info("====== EasemobContactsQuery end with config null["+config+"]  =====");
			return;
		}
		if (user == null) {
			log.info("====== EasemobContactsQuery end with user null["+user+"]  =====");
			return;
		}
		
		if (!config.isAuthed()) {
			if (callback != null) {
				callback.onError(401);
				log.info("====== EasemobContactsQuery end with 401 ["+user+"]  =====");
			}
			return;
		}
		
		
		
		HttpGet get = new HttpGet(config.url + config.org + "/" + config.app
				+ "/users/"+user.userName+"/contacts/users");

		get.addHeader("content-type", "application/json");
		get.addHeader("Authorization", " Bearer " + config.token.getValue());

		HttpClientContext context = HttpClientContext.create();
		context.setCookieStore(new BasicCookieStore());
		context.setCredentialsProvider(new BasicCredentialsProvider());

		CloseableHttpResponse response = null;
		List<EasemobUser> contacts= null;
		try {
			HttpClient httpclient = HttpClientSSLBuilder.buildHttpClient();
			response = (CloseableHttpResponse) httpclient
					.execute(get, context);
			int httpCode = response.getStatusLine().getStatusCode();
			switch (httpCode) {
			case 200:
				log.info("handle EasemobContactsQuery 200");
				contacts = new ArrayList<EasemobUser>();
				handle200(response, contacts);
				break;
			case 401:
				log.info("handle EasemobContactsQuery 401");
				if (callback != null) {
					callback.onError(401);
					log.info("====== EasemobContactsQuery end with 401 ["+user+"]  =====");
				}
				return;
			default:
				log.info("handle EasemobContactsQuery "+httpCode);
				if (callback != null) {
					callback.onError(-1);
					log.info("====== EasemobContactsQuery end with 401 ["+user+"]  =====");
				}
				break;

			}

		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (response != null) {
				try {
					response.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		if (callback != null) {
			callback.onCompleted(contacts);
		}
		
		log.info("====== EasemobContactsQuery end["+user+"]  =====");
	}
	
	
	private void handle200(CloseableHttpResponse response, List<EasemobUser> contacts) {
		InputStream in = null;
		ByteArrayOutputStream out = null;

		HttpEntity responseEntity = response.getEntity();
		try {
			in = responseEntity.getContent();

			JSONTokener tokenr = new JSONTokener(in);
////			out = new ByteArrayOutputStream();
////			byte[] buf = new byte[200];
////			int n = -1;
////			while ((n = in.read(buf)) != -1) {
////				out.write(buf, 0, n);
////			}
//			//TODO save to database
//			log.info(new String(out.toByteArray()));
			
			JSONObject request = (JSONObject) tokenr.nextValue();
			if (request.has("data")) {
				JSONArray array = request.getJSONArray("data");
				int len = array.length();
				for (int i =0; i < len ; i ++) {
					EasemobUser u = new EasemobUser();
					u.userName = array.getString(i);
					contacts.add(u);
				}
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}


}
