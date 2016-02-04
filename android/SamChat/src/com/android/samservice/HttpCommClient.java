package com.android.samservice;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.apache.http.message.BasicNameValuePair;

import com.android.samservice.info.ContactUser;
import com.android.samservice.info.LoginUser;
import com.android.samservice.info.SendAnswer;

public class HttpCommClient {
	public static final String TAG="HttpCommClient";
	public static final String URL = "http://121.42.207.185/SkyWorld/api/1.0/UserAPI";
	public static final String URL_QUESTION = "http://121.42.207.185/SkyWorld/api/1.0/QuestionAPI";
	public static final String PUSH_URL = "http://121.42.207.185/SkyWorld/push";
	public static final String URL_AVATAR = "http://121.42.207.185/SkyWorld/api/1.0/UserAvatarAPI";
	public static final int CONNECTION_TIMEOUT = 20000;
	public static final int HTTP_TIMEOUT = 10000;

	private HttpClient pushhttpclient;
	private Object pushLock;
	private boolean isInterrupt;
	
	public int statusCode;
	public int ret;
	public String token_id;
	public String question_id;
	public LoginUser userinfo;
	public HttpPushInfo hpinfo;
	public List<ContactUser> uiArray;
	
	
	HttpCommClient(){
		statusCode = 0;
		ret = 0;
		token_id = null;
		question_id=null;
		userinfo = new LoginUser();
		hpinfo = new HttpPushInfo();
		uiArray = new ArrayList<ContactUser>();

		pushhttpclient = null;
		pushLock = new Object();
		isInterrupt = false;
	}

	public void InterruptHttpPushWait(){
		synchronized(pushLock){
			isInterrupt = true;
			if(pushhttpclient!=null){
				pushhttpclient.getConnectionManager().shutdown();
				pushhttpclient = null;
			}
		}
	}

	public boolean HttpPushWait(String token){
		SamLog.e(TAG,"In HttpPushWait for question ...");
		HttpPost httppost = null;
		HttpClient httpclient = null;
		try {
			
			synchronized(pushLock){
				if(isInterrupt){
					isInterrupt = false;
					return false;
				}
				
				pushhttpclient = new DefaultHttpClient();

				httpclient = pushhttpclient;  
			}
			
			httppost = new HttpPost(PUSH_URL);;   
			JSONObject obj = new JSONObject();  
			
			httppost.addHeader("Authorization", token); 
			httppost.addHeader("Accept", "application/json"); 

			SamLog.e(TAG,"before execute");
			HttpResponse response;  
			response = httpclient.execute(httppost); 
			SamLog.e(TAG,"after execute");
			
 
			statusCode = response.getStatusLine().getStatusCode();  
			if (statusCode == HttpStatus.SC_OK) {  
				String rev = EntityUtils.toString(response.getEntity());
				
				SamLog.e(TAG,"rev:"+rev);
				obj = new JSONObject(rev);

				JSONObject header;   
				header = obj.getJSONObject("header");
				JSONObject body;
				body = obj.getJSONObject("body");
				
				String category = header.getString("category");
				if(category.equals("question")){
					SamLog.e(TAG,"it is question");
					hpinfo.category = HttpPushInfo.QUESTION;
					hpinfo.datetime = body.getLong("datetime");
					hpinfo.quest_id = body.getString("quest_id");
					hpinfo.quest = body.getString("quest");
					hpinfo.opt = body.getInt("opt");

					JSONObject asker = body.getJSONObject("asker");
					hpinfo.unique_id = asker.getLong("id");
					hpinfo.username = asker.getString("username");
					hpinfo.cellphone = asker.getString("cellphone");
					
					JSONObject easemob = asker.getJSONObject("easemob");
					hpinfo.easemob_username = easemob.getString("username");

					hpinfo.avatar = getImageFilename(asker);
					
				}else if(category.equals("answer")){
					SamLog.e(TAG,"it is answer");
					hpinfo.category = HttpPushInfo.ANSWER;
					JSONObject ans = body.getJSONObject("ans");
					hpinfo.answer = ans.getString("answer");

					JSONObject syservicer = body.getJSONObject("syservicer");
					hpinfo.unique_id = syservicer.getLong("id");
					hpinfo.cellphone = syservicer.getString("cellphone");
					hpinfo.username = syservicer.getString("username");
					JSONObject easemobA = syservicer.getJSONObject("easemob");
					hpinfo.easemob_username = easemobA.getString("username");
					hpinfo.avatar = getImageFilename(syservicer);

					JSONObject quest = body.getJSONObject("quest");
					hpinfo.quest_id = quest.getString("quest_id");
					hpinfo.quest = quest.getString("quest");
					
				}else if(category.equals("easemob")){
					SamLog.e(TAG,"it is easemob");
					hpinfo.category = HttpPushInfo.EASEMOBINFO;
					hpinfo.unique_id = body.getLong("id");
					hpinfo.cellphone = body.getString("cellphone");
					hpinfo.easemob_username = body.getString("easemob_username");
				}else{
					SamLog.e(TAG,"Fatal Error, not support this push cmd");
					throw (new Exception());
				}
				
			} 

			

			return true;
		} catch (ClientProtocolException e) {
			SamLog.e(TAG,"ClientProtocolException");
			//e.printStackTrace(); 
			return false;
		} catch (IOException e) { 
			SamLog.e(TAG,"IOException");
			//e.printStackTrace(); 
			synchronized(pushLock){
				if(isInterrupt){
					isInterrupt = false;
					
				}
			}
			return false;
		} catch (Exception e) { 
			SamLog.e(TAG,"Exception");
			//e.printStackTrace(); 
			return false;
		} finally{
			if(httppost!=null) httppost.abort();
			synchronized(pushLock){
				if(pushhttpclient!=null){
					pushhttpclient.getConnectionManager().shutdown();
					pushhttpclient = null;
				}
			}  
		}
	}
	
	private void HttpPostData() {  
		try {  
		    HttpClient httpclient = new DefaultHttpClient();  
		    String uri = "http://www.yourweb.com";  
		    HttpPost httppost = new HttpPost(uri);   
		    //添加http头信息   
		    httppost.addHeader("Authorization", "your token"); //认证token   
		    httppost.addHeader("Content-Type", "application/json");  
		    httppost.addHeader("User-Agent", "imgfornote");  
		    //http post的json数据格式：  {"name": "your name","parentId": "id_of_parent"}   
		    JSONObject obj = new JSONObject();  
		    obj.put("name", "your name");  
		    obj.put("parentId", "your parentid");  
		    httppost.setEntity(new StringEntity(obj.toString()));     
		    HttpResponse response;  
		    response = httpclient.execute(httppost);  
		    //检验状态码，如果成功接收数据   
		    int code = response.getStatusLine().getStatusCode();  
		    if (code == 200) {   
		        String rev = EntityUtils.toString(response.getEntity());//返回json格式： {"id": "27JpL~j4vsL0LX00E00005","version": "abc"}          
		        obj = new JSONObject(rev);  
		        String id = obj.getString("id");  
		        String version = obj.getString("version");  
		    }  
		    } catch (ClientProtocolException e) {     
		    } catch (IOException e) {     
		    } catch (Exception e) {   
		    }  
		} 
	
	private boolean HttpGetData(String uri){
		DefaultHttpClient client = null;
		HttpGet requestGet = null;
		try{
			//Log.e(TAG,"start HttpGetData...");
			requestGet = new HttpGet(uri);
			client = new DefaultHttpClient();
			HttpParams params = client.getParams();
			if(params==null){
				params = new BasicHttpParams();
			}
			HttpConnectionParams.setConnectionTimeout(params, CONNECTION_TIMEOUT);
			HttpConnectionParams.setSoTimeout(params, HTTP_TIMEOUT);
			HttpResponse response = client.execute(requestGet);
			statusCode = response.getStatusLine().getStatusCode();
			if(statusCode == HttpStatus.SC_OK ){
				String rev = EntityUtils.toString(response.getEntity());          
				JSONObject obj = new JSONObject(rev); 
				ret = obj.getInt("ret");
				if(ret == SignService.RET_SU_FROM_SERVER_OK){
					token_id = obj.getString("token");
				}else{
					SamLog.e(TAG,"ret:"+ret);
				}
				return true;
			}else{
				return false;
			}
		} catch (ClientProtocolException e) {
			SamLog.e(TAG,"ClientProtocolException");
			e.printStackTrace(); 
			return false;
	    } catch (IOException e) { 
	    	SamLog.e(TAG,"IOException");
	    	e.printStackTrace(); 
	    	return false;
	    } catch (Exception e) { 
	    	SamLog.e(TAG,"Exception");
	    	e.printStackTrace(); 
	    	return false;
	    } finally{
			if(requestGet!=null) requestGet.abort();
			if(client!=null){
				client.getConnectionManager().shutdown();
				client = null;
			}
		}
	}
	
	public boolean signin(String username,String password){
		if(username==null || password==null){
			SamLog.e(TAG,"login param is null");
			return false;
		}
		
		String rev="1";
			
		/*Construct login json data*/
		try{
			JSONObject login_header = new JSONObject();
			login_header.putOpt("action", "login");
			
			JSONObject login_body = new JSONObject();
			login_body.putOpt("username", username);
			login_body.putOpt("pwd", password);
			
			JSONObject login_data = new JSONObject();
			login_data.put("header", login_header);
			login_data.put("body", login_body);
			
			List<BasicNameValuePair> params = new LinkedList<BasicNameValuePair>();
			params.add(new BasicNameValuePair("data",login_data.toString()));
			String param = URLEncodedUtils.format(params, "UTF-8"); 
			
			String url = URL + "?" + param;

			HttpGet requestGet = new HttpGet(url);
			DefaultHttpClient client = new DefaultHttpClient();
			HttpParams http_params = client.getParams();
			if(http_params==null){
				http_params = new BasicHttpParams();
			}
			HttpConnectionParams.setConnectionTimeout(http_params, CONNECTION_TIMEOUT);
			HttpConnectionParams.setSoTimeout(http_params, HTTP_TIMEOUT);
			HttpResponse response = client.execute(requestGet);
			statusCode = response.getStatusLine().getStatusCode();
			if(statusCode == HttpStatus.SC_OK ){
				rev = EntityUtils.toString(response.getEntity()); 
				SamLog.e(TAG,"rev:"+rev);
				JSONObject obj = new JSONObject(rev); 
				ret = obj.getInt("ret");
				if(ret == SignService.RET_SU_FROM_SERVER_OK){
					token_id = obj.getString("token");
					JSONObject user = obj.getJSONObject("user");
					userinfo.username = user.getString("username");
					userinfo.phonenumber = user.getString("cellphone");
					userinfo.password = password;
					userinfo.usertype = user.getInt("type");
					userinfo.lastupdate = user.getLong("lastupdate");
					userinfo.imagefile = getImageFilename(user);
					
				}else{
					SamLog.e(TAG,"ret:"+ret);
				}
				return true;
			}else{
				SamLog.e(TAG,"sign in statusCode:"+statusCode);
				return false;
			}
		
		}catch (JSONException e) {  
			e.printStackTrace(); 
			/*int index = rev.indexOf("token");
			token_id = rev.substring(index+7, index+24);
			SamLog.e(TAG,"token:"+token_id);
			ret = 0;
			userinfo.username = username;
			userinfo.phonenumber = username;
			userinfo.password = password;
			userinfo.usertype = 0;
			return true;*/
			return false;
		} catch (ClientProtocolException e) {
			SamLog.e(TAG,"ClientProtocolException");
			e.printStackTrace(); 
			return false;
		} catch (IOException e) { 
			SamLog.e(TAG,"IOException");
			e.printStackTrace(); 
			return false;
		} catch (Exception e) { 
			SamLog.e(TAG,"Exception");
			e.printStackTrace(); 
			return false;
		}
	}

	public boolean signout(String username, String cellphone,String token){
		try{
			JSONObject signout_header = new JSONObject();
			signout_header.putOpt("action", "logout");
			signout_header.putOpt("token",token);

			JSONObject signout_data = new JSONObject();
			signout_data.put("header", signout_header);
			signout_data.put("body",signout_header);
			
			List<BasicNameValuePair> params = new LinkedList<BasicNameValuePair>();
			params.add(new BasicNameValuePair("data",signout_data.toString()));
			String param = URLEncodedUtils.format(params, "UTF-8"); 
			
			String url = URL + "?" + param;
			SamLog.e(TAG,url);

			HttpGet requestGet = new HttpGet(URL);
			DefaultHttpClient client = new DefaultHttpClient();
			HttpParams http_params = client.getParams();
			if(http_params==null){
				http_params = new BasicHttpParams();
			}
			HttpConnectionParams.setConnectionTimeout(http_params, CONNECTION_TIMEOUT);
			HttpConnectionParams.setSoTimeout(http_params, HTTP_TIMEOUT);
			HttpResponse response = client.execute(requestGet);
			statusCode = response.getStatusLine().getStatusCode();
			if(statusCode == HttpStatus.SC_OK ){
				String rev = EntityUtils.toString(response.getEntity());          
				JSONObject obj = new JSONObject(rev); 
				ret = obj.getInt("ret");
				SamLog.e(TAG,"ret:"+ret);
				return true;
			}else{
				SamLog.e(TAG,"status code:"+statusCode);
				return false;
			}
		}catch (JSONException e) {  
			e.printStackTrace();  
            		return false;
		}catch (ClientProtocolException e) {
			SamLog.e(TAG,"ClientProtocolException");
			e.printStackTrace(); 
			return false;
		}catch (IOException e) { 
			SamLog.e(TAG,"IOException");
			e.printStackTrace(); 
			return false;
		}catch (Exception e) { 
			SamLog.e(TAG,"Exception");
			e.printStackTrace(); 
			return false;
		} 
		
	}
	
	public boolean signup(String username,String password,String confirm_pwd,String cellphone){
		if(username==null || password==null || confirm_pwd == null||cellphone==null){
			SamLog.e(TAG,"sign up param is null");
			return false;
		}
			
		/*Construct sign up json data*/
		try{
			JSONObject signup_header = new JSONObject();
			signup_header.putOpt("action", "register");
			
			JSONObject signup_body = new JSONObject();
			signup_body.putOpt("cellphone",cellphone);
			signup_body.putOpt("username", username);
			signup_body.putOpt("pwd", password);
			signup_body.putOpt("confirm_pwd", confirm_pwd);
			
			JSONObject signup_data = new JSONObject();
			signup_data.put("header", signup_header);
			signup_data.put("body", signup_body);

			List<BasicNameValuePair> params = new LinkedList<BasicNameValuePair>();
			params.add(new BasicNameValuePair("data",signup_data.toString()));
			String param = URLEncodedUtils.format(params, "UTF-8"); 
			
			String url = URL + "?" + param;
			SamLog.e(TAG,url);

			HttpGet requestGet = new HttpGet(url);
			DefaultHttpClient client = new DefaultHttpClient();
			HttpParams http_params = client.getParams();
			if(http_params==null){
				http_params = new BasicHttpParams();
			}
			HttpConnectionParams.setConnectionTimeout(http_params, CONNECTION_TIMEOUT);
			HttpConnectionParams.setSoTimeout(http_params, HTTP_TIMEOUT);
			HttpResponse response = client.execute(requestGet);
			statusCode = response.getStatusLine().getStatusCode();
			if(statusCode == HttpStatus.SC_OK ){
				String rev = EntityUtils.toString(response.getEntity());          
				JSONObject obj = new JSONObject(rev); 
				ret = obj.getInt("ret");

				SamLog.e(TAG,"ret:" + ret);
				SamLog.e(TAG,"rev:" + rev);
				if(ret == SignService.RET_SU_FROM_SERVER_OK){
					token_id = obj.getString("token");
					JSONObject user = obj.getJSONObject("user");
					userinfo.username = user.getString("username");
					userinfo.phonenumber = user.getString("cellphone");
					userinfo.password = password;
					userinfo.usertype = user.getInt("type");
					userinfo.lastupdate = user.getLong("lastupdate");
				}else{
					SamLog.e(TAG,"ret:"+ret);
				}
				return true;
			}else{
				SamLog.e(TAG,"sign up statuc code:"+statusCode);
				return false;
			}
		
		}catch (JSONException e) {  
			e.printStackTrace();  
			return false;
		} catch (ClientProtocolException e) {
			SamLog.e(TAG,"ClientProtocolException");
			e.printStackTrace(); 
			return false;
		} catch (IOException e) { 
			SamLog.e(TAG,"IOException");
			e.printStackTrace(); 
			return false;
		} catch (Exception e) { 
			SamLog.e(TAG,"Exception");
			e.printStackTrace(); 
			return false;
		}
		
	}
	
	
	public boolean sendquestion(String question,String token){
		/*Construct sign up json data*/
		try{
			JSONObject sendq_header = new JSONObject();
			sendq_header.putOpt("action", "question");
			sendq_header.putOpt("token", token);
			
			
			JSONObject sendq_body = new JSONObject();
			sendq_body.putOpt("opt",1);
			sendq_body.putOpt("question", question);

			
			JSONObject sendq_data = new JSONObject();
			sendq_data.put("header", sendq_header);
			sendq_data.put("body", sendq_body);
			

			List<BasicNameValuePair> params = new LinkedList<BasicNameValuePair>();
			params.add(new BasicNameValuePair("data",sendq_data.toString()));
			String param = URLEncodedUtils.format(params, "UTF-8"); 
			
			String url = URL_QUESTION + "?" + param;
			
			SamLog.e(TAG,url+"test:"+sendq_data.toString());

			HttpGet requestGet = new HttpGet(url);
			DefaultHttpClient client = new DefaultHttpClient();
			HttpParams http_params = client.getParams();
			if(http_params==null){
				http_params = new BasicHttpParams();
			}
			HttpConnectionParams.setConnectionTimeout(http_params, CONNECTION_TIMEOUT);
			HttpConnectionParams.setSoTimeout(http_params, HTTP_TIMEOUT);
			HttpResponse response = client.execute(requestGet);
			statusCode = response.getStatusLine().getStatusCode();
			if(statusCode == HttpStatus.SC_OK ){
				String rev = EntityUtils.toString(response.getEntity());          
				JSONObject obj = new JSONObject(rev); 
				ret = obj.getInt("ret");
				if(ret == SamService.RET_SEND_QUESTION_FROM_SERVER_OK){
					question_id = obj.getString("question_id");
				}else{
					SamLog.e(TAG,"ret:"+ret);
				}
				return true;
			}else{
				return false;
			}
		}catch (JSONException e) {  
			// TODO Auto-generated catch block  
			e.printStackTrace();  
			return false;
        	}catch (ClientProtocolException e) {
			SamLog.e(TAG,"ClientProtocolException");
			e.printStackTrace(); 
			return false;
	    	} catch (IOException e) { 
	    		SamLog.e(TAG,"IOException");
	    		e.printStackTrace(); 
	    		return false;
	    	} catch (Exception e) { 
	    		SamLog.e(TAG,"Exception");
	    		e.printStackTrace(); 
	    		return false;
	   	 } 

		
	}



	public boolean cancelquestion(String question_id,String token){
		/*Construct sign up json data*/
		try{
			JSONObject cancelq_header = new JSONObject();
			cancelq_header.putOpt("action", "question");
			cancelq_header.putOpt("token",token);
			
			
			JSONObject cancelq_body = new JSONObject();
			cancelq_body.putOpt("opt",2);
			cancelq_body.putOpt("question_id", question_id);

			
			JSONObject cancelq_data = new JSONObject();
			cancelq_data.put("header", cancelq_header);
			cancelq_data.put("body", cancelq_body);
			

			List<BasicNameValuePair> params = new LinkedList<BasicNameValuePair>();
			params.add(new BasicNameValuePair("data",cancelq_data.toString()));
			String param = URLEncodedUtils.format(params, "UTF-8"); 
			
			String url = URL_QUESTION + "?" + param;
			
			SamLog.e(TAG,url+"test:"+cancelq_data.toString());

			HttpGet requestGet = new HttpGet(url);
			DefaultHttpClient client = new DefaultHttpClient();
			HttpParams http_params = client.getParams();
			if(http_params==null){
				http_params = new BasicHttpParams();
			}
			HttpConnectionParams.setConnectionTimeout(http_params, CONNECTION_TIMEOUT);
			HttpConnectionParams.setSoTimeout(http_params, HTTP_TIMEOUT);
			HttpResponse response = client.execute(requestGet);
			statusCode = response.getStatusLine().getStatusCode();
			if(statusCode == HttpStatus.SC_OK ){
				String rev = EntityUtils.toString(response.getEntity());
				SamLog.e(TAG,"rev:"+rev);
				JSONObject obj = new JSONObject(rev); 
				ret = obj.getInt("ret");
				if(ret == SamService.RET_CANCEL_QUESTION_FROM_SERVER_OK){
					//this.question_id = obj.getString("question_id");
					this.question_id = question_id;
				}else{
					SamLog.e(TAG,"ret:"+ret);
				}
				return true;
			}else{
				return false;
			}
		}catch (JSONException e) {  
			// TODO Auto-generated catch block  
			e.printStackTrace();  
			return false;
        	}catch (ClientProtocolException e) {
			SamLog.e(TAG,"ClientProtocolException");
			e.printStackTrace(); 
			return false;
	    	} catch (IOException e) { 
	    		SamLog.e(TAG,"IOException");
	    		e.printStackTrace(); 
	    		return false;
	    	} catch (Exception e) { 
	    		SamLog.e(TAG,"Exception");
	    		e.printStackTrace(); 
	    		return false;
	   	 } 

		
	}



	public boolean upgrade(String token){
		/*Construct sign up json data*/
		try{
			JSONObject upgrade_header = new JSONObject();
			upgrade_header.putOpt("action", "upgrade");
			upgrade_header.putOpt("token",token);
			
			
			JSONObject upgrade_body = new JSONObject();
			upgrade_body.putOpt("action", "upgrade");
			upgrade_body.putOpt("token",token);

			
			JSONObject upgrade_data = new JSONObject();
			upgrade_data.put("header", upgrade_header);
			upgrade_data.put("body", upgrade_body);
			

			List<BasicNameValuePair> params = new LinkedList<BasicNameValuePair>();
			params.add(new BasicNameValuePair("data",upgrade_data.toString()));
			String param = URLEncodedUtils.format(params, "UTF-8"); 
			
			String url = URL + "?" + param;
			
			SamLog.e(TAG,url+"test:"+upgrade_data.toString());

			HttpGet requestGet = new HttpGet(url);
			DefaultHttpClient client = new DefaultHttpClient();
			HttpParams http_params = client.getParams();
			if(http_params==null){
				http_params = new BasicHttpParams();
			}
			HttpConnectionParams.setConnectionTimeout(http_params, CONNECTION_TIMEOUT);
			HttpConnectionParams.setSoTimeout(http_params, HTTP_TIMEOUT);
			HttpResponse response = client.execute(requestGet);
			statusCode = response.getStatusLine().getStatusCode();
			if(statusCode == HttpStatus.SC_OK ){
				String rev = EntityUtils.toString(response.getEntity());
				SamLog.e(TAG,"rev:"+rev);
				JSONObject obj = new JSONObject(rev); 
				ret = obj.getInt("ret");
				token_id = obj.getString("token");
				return true;
			}else{
				SamLog.e(TAG,"statusCode:" + statusCode);
				return false;
			}
		}catch (JSONException e) {  
			// TODO Auto-generated catch block  
			e.printStackTrace();  
			return false;
        	}catch (ClientProtocolException e) {
			SamLog.e(TAG,"ClientProtocolException");
			e.printStackTrace(); 
			return false;
	    	} catch (IOException e) { 
	    		SamLog.e(TAG,"IOException");
	    		e.printStackTrace(); 
	    		return false;
	    	} catch (Exception e) { 
	    		SamLog.e(TAG,"Exception");
	    		e.printStackTrace(); 
	    		return false;
	   	 } 

		
	}


	public boolean answer(SendAnswer sda,String token){
		try{
			JSONObject upgrade_header = new JSONObject();
			upgrade_header.putOpt("action", "answer");
			upgrade_header.putOpt("token",token);
			
			
			JSONObject upgrade_body = new JSONObject();
			upgrade_body.putOpt("answer", sda.getanswer());
			upgrade_body.putOpt("question_id",sda.getquestion_id());

			
			JSONObject upgrade_data = new JSONObject();
			upgrade_data.put("header", upgrade_header);
			upgrade_data.put("body", upgrade_body);
			

			List<BasicNameValuePair> params = new LinkedList<BasicNameValuePair>();
			params.add(new BasicNameValuePair("data",upgrade_data.toString()));
			String param = URLEncodedUtils.format(params, "UTF-8"); 
			
			String url = URL_QUESTION + "?" + param;
			
			SamLog.e(TAG,url+"test:"+upgrade_data.toString());

			HttpGet requestGet = new HttpGet(url);
			DefaultHttpClient client = new DefaultHttpClient();
			HttpParams http_params = client.getParams();
			if(http_params==null){
				http_params = new BasicHttpParams();
			}
			HttpConnectionParams.setConnectionTimeout(http_params, CONNECTION_TIMEOUT);
			HttpConnectionParams.setSoTimeout(http_params, HTTP_TIMEOUT);
			HttpResponse response = client.execute(requestGet);
			statusCode = response.getStatusLine().getStatusCode();
			if(statusCode == HttpStatus.SC_OK ){
				String rev = EntityUtils.toString(response.getEntity());
				SamLog.e(TAG,"rev:"+rev);
				JSONObject obj = new JSONObject(rev); 
				ret = obj.getInt("ret");
				return true;
			}else{
				return false;
			}
		}catch (JSONException e) {  
			// TODO Auto-generated catch block  
			e.printStackTrace();  
			return false;
        	}catch (ClientProtocolException e) {
			SamLog.e(TAG,"ClientProtocolException");
			e.printStackTrace(); 
			return false;
	    	} catch (IOException e) { 
	    		SamLog.e(TAG,"IOException");
	    		e.printStackTrace(); 
	    		return false;
	    	} catch (Exception e) { 
	    		SamLog.e(TAG,"Exception");
	    		e.printStackTrace(); 
	    		return false;
	   	 } 

		
	}


	private String getImageFilename(JSONObject jo){
		String imgFileName = null;
		
		try{
			JSONObject avatar = jo.getJSONObject("avatar");
			imgFileName = avatar.getString("origin");
			SamLog.e(TAG,"getImageFilename:"+imgFileName);
			return imgFileName;
		}catch(JSONException e){
			SamLog.e(TAG,"no avatar for this user");	
			return null;
		}
	}

	
	public boolean queryui(String phonenumber,String token){
		try{
			JSONObject queryui_header = new JSONObject();
			queryui_header.putOpt("action", "query");
			queryui_header.putOpt("token",token);

			JSONObject jsonparam = new JSONObject();
			jsonparam.putOpt("username", phonenumber);
			
			JSONObject queryui_body = new JSONObject();
			queryui_body.putOpt("opt", 1);
			queryui_body.put("param",jsonparam);

			
			JSONObject queryui_data = new JSONObject();
			queryui_data.put("header", queryui_header);
			queryui_data.put("body", queryui_body);
			

			List<BasicNameValuePair> params = new LinkedList<BasicNameValuePair>();
			params.add(new BasicNameValuePair("data",queryui_data.toString()));
			String param = URLEncodedUtils.format(params, "UTF-8"); 
			
			String url = URL + "?" + param;
			
			SamLog.e(TAG,url+"test:"+queryui_data.toString());

			HttpGet requestGet = new HttpGet(url);
			DefaultHttpClient client = new DefaultHttpClient();
			HttpParams http_params = client.getParams();
			if(http_params==null){
				http_params = new BasicHttpParams();
			}
			HttpConnectionParams.setConnectionTimeout(http_params, CONNECTION_TIMEOUT);
			HttpConnectionParams.setSoTimeout(http_params, HTTP_TIMEOUT);
			HttpResponse response = client.execute(requestGet);
			statusCode = response.getStatusLine().getStatusCode();
			if(statusCode == HttpStatus.SC_OK ){
				String rev = EntityUtils.toString(response.getEntity());
				SamLog.e(TAG,"rev:"+rev);
				JSONObject obj = new JSONObject(rev); 
				ret = obj.getInt("ret");
				if(ret == SamService.RET_QUERY_USERINFO_SERVER_OK){
					JSONArray jsonArrayX = obj.getJSONArray("users");
					for (int i = 0; i < jsonArrayX.length(); i++) {
						JSONObject jo = (JSONObject) jsonArrayX.get(i);
						ContactUser ui = new ContactUser();
						//{"mail":"138","username":"138","cellphone":"1381196123","type":0}
						ui.setusername(jo.getString("username"));
						ui.setphonenumber(jo.getString("cellphone"));
						ui.seteasemob_username(jo.getString("cellphone"));
						ui.setusertype(jo.getInt("type"));
						ui.setlastupdate(jo.getLong("lastupdate"));
						ui.setimagefile(getImageFilename(jo));
						uiArray.add(ui);
					}
					
				}else{
					SamLog.e(TAG,"ret:"+ret);
				}
				return true;
			}else{
				return false;
			}
		}catch (JSONException e) {  
			// TODO Auto-generated catch block  
			e.printStackTrace();  
			return false;
        	}catch (ClientProtocolException e) {
			SamLog.e(TAG,"ClientProtocolException");
			e.printStackTrace(); 
			return false;
	    	} catch (IOException e) { 
	    		SamLog.e(TAG,"IOException");
	    		e.printStackTrace(); 
	    		return false;
	    	} catch (Exception e) { 
	    		SamLog.e(TAG,"Exception");
	    		e.printStackTrace(); 
	    		return false;
	   	 } 

		
	}



	public boolean queryui(List<String> usernames,String token){
		try{
			JSONObject queryui_header = new JSONObject();
			queryui_header.putOpt("action", "query");
			queryui_header.putOpt("token",token);

			JSONObject jsonparam = new JSONObject();
			JSONArray paramArray = new JSONArray();
			for(int i=0;i<usernames.size();i++){
				paramArray.put(usernames.get(i));
			}
			jsonparam.put("usernames", paramArray);
			
			JSONObject queryui_body = new JSONObject();
			queryui_body.putOpt("opt", 2);
			queryui_body.put("param",jsonparam);

			
			JSONObject queryui_data = new JSONObject();
			queryui_data.put("header", queryui_header);
			queryui_data.put("body", queryui_body);
			

			List<BasicNameValuePair> params = new LinkedList<BasicNameValuePair>();
			params.add(new BasicNameValuePair("data",queryui_data.toString()));
			String param = URLEncodedUtils.format(params, "UTF-8"); 
			
			String url = URL + "?" + param;
			
			SamLog.e(TAG,url+"test:"+queryui_data.toString());

			HttpGet requestGet = new HttpGet(url);
			DefaultHttpClient client = new DefaultHttpClient();
			HttpParams http_params = client.getParams();
			if(http_params==null){
				http_params = new BasicHttpParams();
			}
			HttpConnectionParams.setConnectionTimeout(http_params, CONNECTION_TIMEOUT);
			HttpConnectionParams.setSoTimeout(http_params, HTTP_TIMEOUT);
			HttpResponse response = client.execute(requestGet);
			statusCode = response.getStatusLine().getStatusCode();
			if(statusCode == HttpStatus.SC_OK ){
				String rev = EntityUtils.toString(response.getEntity());
				SamLog.e(TAG,"rev:"+rev);
				JSONObject obj = new JSONObject(rev); 
				ret = obj.getInt("ret");
				if(ret == SamService.RET_QUERY_USERINFO_SERVER_OK){
					JSONArray jsonArrayX = obj.getJSONArray("users");
					for (int i = 0; i < jsonArrayX.length(); i++) {
						JSONObject jo = (JSONObject) jsonArrayX.get(i);
						ContactUser ui = new ContactUser();
						//{"mail":"138","username":"138","cellphone":"1381196123","type":0}
						ui.setusername(jo.getString("username"));
						ui.setphonenumber(jo.getString("cellphone"));
						ui.seteasemob_username(jo.getString("cellphone"));
						ui.setusertype(jo.getInt("type"));
						ui.setlastupdate(jo.getLong("lastupdate"));
						ui.setimagefile(getImageFilename(jo));
						uiArray.add(ui);
					}
					
				}else{
					SamLog.e(TAG,"ret:"+ret);
				}
				return true;
			}else{
				return false;
			}
		}catch (JSONException e) {  
			// TODO Auto-generated catch block  
			e.printStackTrace();  
			return false;
        	}catch (ClientProtocolException e) {
			SamLog.e(TAG,"ClientProtocolException");
			e.printStackTrace(); 
			return false;
	    	} catch (IOException e) { 
	    		SamLog.e(TAG,"IOException");
	    		e.printStackTrace(); 
	    		return false;
	    	} catch (Exception e) { 
	    		SamLog.e(TAG,"Exception");
	    		e.printStackTrace(); 
	    		return false;
	   	 } 

		
	}

public boolean uploadavatar(String filePath, String token){
		String CrLf = "\r\n";

		HttpURLConnection conn = null;
		OutputStream os = null;
		InputStream is = null;
		String photoName = getPhotoName(filePath);

		if(photoName == null) {
			return false;
		}

		try {
			JSONObject av_header = new JSONObject();
			av_header.putOpt("action", "update-avatar");
			av_header.putOpt("token", token);
			
			JSONObject av_body = new JSONObject();
			av_body.putOpt("type", "1");
			
			JSONObject av_data = new JSONObject();
			av_data.put("header", av_header);
			av_data.put("body", av_body);
			
			List<BasicNameValuePair> params = new LinkedList<BasicNameValuePair>();
			params.add(new BasicNameValuePair("data",av_data.toString()));
			String param = URLEncodedUtils.format(params, "UTF-8"); 
			
			String url_chat = URL_AVATAR + "?" + param;

			
			java.net.URL url = new java.net.URL(url_chat);
			conn = (HttpURLConnection)url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");

			InputStream imgIs = new FileInputStream(new File(filePath));
			byte[] imgData = new byte[imgIs.available()];
			imgIs.read(imgData);
			imgIs.close();

			String message1 = "";
			message1 += "Accept:"
					+ " text/html,application/xml,"
					+ "application/xhtml+xml,text/html;q=0.9,text/plain;q=0.8,image/png,*/*;q=0.5"
					+ CrLf;
			message1 += "Connection:" + " Keep-Alive" + CrLf;
			message1 += "User-Agent:"
					+ " Mozilla/5.0 (X11; U; Linux "
					+ "i686; en-US; rv:1.8.1.6) Gecko/20061201 Firefox/2.0.0.6 (Ubuntu-feisty)"
					+ CrLf;
				
			message1 += "-----------------------------4664151417711" + CrLf;
			message1 += "Content-Disposition: form-data; name=\"photo\"; filename=\""+filePath.trim()+"\""
					+ CrLf;
			message1 += "Content-Type: image/jpeg" + CrLf;
			message1 += CrLf;
				
			// the image is sent between the messages in the multipart
			// message.

			String message2 = "";
			message2 += CrLf
					+ "-----------------------------4664151417711--" + CrLf;

			conn.setRequestProperty("Content-Type",
					"multipart/form-data; boundary=---------------------------4664151417711");

			
			// might not need to specify the content-length when sending
			// chunked
			// data.
			conn.setRequestProperty(
					"Content-Length",
					String.valueOf((message1.length() + message2.length() + imgData.length)));

			SamLog.e(TAG,"m1:"+message1.length() +" m2:"+message2.length() + " imgLength:"+imgData.length );

			os = conn.getOutputStream();

			os.write(message1.getBytes());

			// FIXME
			int index = 0;
			int size = 1024;
			do {
				if ((index + size) > imgData.length) {
					size = imgData.length - index;
				}
				os.write(imgData, index, size);
				index += size;
			} while (index < imgData.length);

			os.write(message2.getBytes());
			os.flush();

			is = conn.getInputStream();
			StringBuilder sb = new StringBuilder();
			char buff = 512;
			int len;
			byte[] data = new byte[buff];
			do {
				len = is.read(data);

				if (len > 0) {
					sb.append(new String(data, 0, len));
				}
			} while (len > 0);

			String rev = sb.toString();
			SamLog.e(TAG,"rev:"+rev);

			JSONObject obj = new JSONObject(rev);
			ret = obj.getInt("ret");

			if(ret == SamService.RET_UPLOAD_AVATAR_SERVER_OK){
				return true;
			}else{
				return false;
			}
		} catch (Exception e) {
				e.printStackTrace();
				return false;
		} finally {
			try {
				if(os!=null) os.close();
			} catch (Exception e) {
			}

			try {
				if(is!=null) is.close();
			} catch (Exception e) {
			}

			try {
				if(conn!=null) conn.disconnect();
			} catch (Exception e) {
			}

			
		}
	}

	private String getPhotoName(String photoPath) {
		int index  = photoPath.lastIndexOf("/");
		if(index != -1) {
			return photoPath.substring(index);
		} else {
			return null;
		}
	}
	
	private byte[] readStream(InputStream inStream) throws Exception{   
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();   
        byte[] buffer = new byte[1024];   
        int len = 0;   
        while( (len=inStream.read(buffer)) != -1){   
            outStream.write(buffer, 0, len);   
        }   
        outStream.close();   
        inStream.close();   
        return outStream.toByteArray();   
    } 

	public byte[] getImage(String path){   
		HttpURLConnection conn =null;

		try{
			java.net.URL url = new java.net.URL(path);   
			conn = (HttpURLConnection) url.openConnection();   
			conn.setRequestMethod("GET");   
			InputStream inStream = conn.getInputStream();   
			if(conn.getResponseCode() == HttpURLConnection.HTTP_OK){   
				return readStream(inStream);   
			}
			return null;
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}finally{
			try {
				if(conn!=null) conn.disconnect();
			} catch (Exception e) {
			}
		}
 
	}

	/*
	private String getQuery(List<NameValuePair> params) throws UnsupportedEncodingException
	{
    StringBuilder result = new StringBuilder();
    boolean first = true;

    for (NameValuePair pair : params)
    {
        if (first)
            first = false;
        else
            result.append("&");

        result.append(URLEncoder.encode(pair.getName(), "UTF-8"));
        result.append("=");
        result.append(URLEncoder.encode(pair.getValue(), "UTF-8"));
    }

    return result.toString();
	}*/
	
}
