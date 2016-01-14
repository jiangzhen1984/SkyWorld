package com.skyworld.easemob;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.skyworld.utils.HttpClientSSLBuilder;


public class AuthorizationRunnable implements Runnable {
	

	Log log = LogFactory.getLog(this.getClass());
	
	EaseMobDeamon.Configuration config;
	

	public AuthorizationRunnable(EaseMobDeamon.Configuration config) {
		this.config = config;
	}

	@Override
	public void run() {
		if (config == null) {
			log.error(" Can not start authroization process, config is null ");
			return;
		}
		
    
		
		HttpPost post = new HttpPost(config.url+ config.org+"/"+config.app+"/token");
		
		JSONObject data = new JSONObject();
		data.put("grant_type", "client_credentials");
		data.put("client_id", config.clientId);
		data.put("client_secret", config.clientSecret);
		HttpEntity entity = new StringEntity(data.toString(), "utf8");
		post.setEntity(entity);
		post.addHeader("content-type", "application/json");
		log.info(post.getRequestLine());
		
		
		 HttpClientContext context = HttpClientContext.create();
         // Contextual attributes set the local context level will take
         // precedence over those set at the client level.
         context.setCookieStore( new BasicCookieStore());
         context.setCredentialsProvider( new BasicCredentialsProvider());
         
         
		
		CloseableHttpResponse response = null;
		InputStream in = null;
		ByteArrayOutputStream out = null;
		 try {
			 HttpClient httpclient = HttpClientSSLBuilder.buildHttpClient();
			 response = (CloseableHttpResponse)httpclient.execute(post, context);
			 int httpCode = response.getStatusLine().getStatusCode();
			 if (httpCode == 200) {
				 HttpEntity responseEntity = response.getEntity();
				 in = responseEntity.getContent();
				 
				 out = new ByteArrayOutputStream();
				 byte[] buf = new byte[200];
				 int n = -1;
				 while ((n = in.read(buf)) != -1) {
					 out.write(buf, 0, n);
				 }
				 parseResult(new String(out.toByteArray()));
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
	
	
	
	
	

	
	private void parseResult(String content) {
		//{"access_token":"YWMtux9sqKZQEeWpEUd2vtSVEAAAAVLvUvROUzWgubsm5pwrM0ZGONZu-Qdhf1Q","expires_in":5184000,"application":"516dd320-a4c6-11e5-bca2-41a738274c63"}
		
		JSONTokener jsonParser = new JSONTokener(content);
		JSONObject request = (JSONObject) jsonParser.nextValue();
		
		String token = request.getString("access_token");
		int expires = request.getInt("expires_in");
		String uuid = request.getString("application");
		config.updateToken(token, expires, uuid);
		log.info("=====> get token : " + token);
	}

}
