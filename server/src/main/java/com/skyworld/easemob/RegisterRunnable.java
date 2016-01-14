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

import com.skyworld.easemob.EaseMobDeamon.Configuration;
import com.skyworld.utils.HttpClientSSLBuilder;

public class RegisterRunnable implements Runnable {
	
	Log log = LogFactory.getLog(RegisterRunnable.class);

	EaseMobDeamon.Configuration config;
	String username;
	String password;
	
	
	EasemobRegisterCallback registerCallback;

	public RegisterRunnable(Configuration config, String username, String pwd, EasemobRegisterCallback registerCallback) {
		super();
		this.config = config;
		this.username = username;
		this.password = pwd;
		this.registerCallback = registerCallback;
	}

	@Override
	public void run() {
		log.info("====== RegisterRunnable start =====");
		if (config == null || username == null || username.isEmpty()
				|| password == null || password.isEmpty()) {
			return;
		}
		log.info("====== RegisterRunnable check passed =====");
		HttpPost post = new HttpPost(config.url + config.org + "/" + config.app
				+ "/users");

		JSONObject data = new JSONObject();
		data.put("username", username);
		data.put("password", password);
		HttpEntity entity = new StringEntity(data.toString(), "utf8");
		post.setEntity(entity);
		post.addHeader("content-type", "application/json");
		post.addHeader("Authorization", " Bearer " + config.token.getValue());

		HttpClientContext context = HttpClientContext.create();
		context.setCookieStore(new BasicCookieStore());
		context.setCredentialsProvider(new BasicCredentialsProvider());

		CloseableHttpResponse response = null;
		int ret = 0;
		try {
			HttpClient httpclient = HttpClientSSLBuilder.buildHttpClient();
			response = (CloseableHttpResponse) httpclient
					.execute(post, context);
			int httpCode = response.getStatusLine().getStatusCode();
			switch (httpCode) {
			case 200:
				log.info("handle 200");
				handle200(response);
				ret = 0;
				break;
			case 400:
				log.info("handle 400");
				handle200(response);
				ret = 1;
				break;
			case 401:
				log.info("handle 401");
				handle401(response);
				break;
			default:
				log.info("handle "+httpCode);
				handleOthers(response);
				ret = -1;
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
		
		if (registerCallback != null) {
			registerCallback.onRegistered();
		}

	}

	private void handle200(CloseableHttpResponse response) {
		InputStream in = null;
		ByteArrayOutputStream out = null;

		HttpEntity responseEntity = response.getEntity();
		try {
			in = responseEntity.getContent();

			out = new ByteArrayOutputStream();
			byte[] buf = new byte[200];
			int n = -1;
			while ((n = in.read(buf)) != -1) {
				out.write(buf, 0, n);
			}
			//TODO save to database
			log.info(new String(out.toByteArray()));
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

	private void handle401(CloseableHttpResponse response) {

	}

	private void handle400(CloseableHttpResponse response) {
	}

	private void handleOthers(CloseableHttpResponse response) {

	}

}
