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

import com.android.samchat.SamVendorInfo;
import com.android.samservice.info.ContactUser;
import com.android.samservice.info.LoginUser;
import com.android.samservice.info.SendAnswer;

public class HttpCommClient {
	public static final String TAG="HttpCommClient";
	public static final String URL = "http://121.42.207.185/SkyWorld/api/1.0/UserAPI";
	public static final String URL_QUESTION = "http://121.42.207.185/SkyWorld/api/1.0/QuestionAPI";
	public static final String PUSH_URL = "http://121.42.207.185/SkyWorld/push";
	public static final String URL_AVATAR = "http://121.42.207.185/SkyWorld/api/1.0/UserAvatarAPI";
	public static final String URL_ATICLE = "http://121.42.207.185/SkyWorld/api/1.0/ArticleApi";
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

	public ArticleInfo ainfo;

	public List<ArticleInfo> ainfoList;

	
	
	
	HttpCommClient(){
		statusCode = 0;
		ret = 0;
		token_id = null;
		question_id=null;
		userinfo = new LoginUser();
		hpinfo = new HttpPushInfo();
		uiArray = new ArrayList<ContactUser>();
		ainfo = new ArticleInfo();

		ainfoList = new ArrayList<ArticleInfo>();
		

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
		SamLog.i(TAG,"In HttpPushWait for question ...");
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
			
			httppost = new HttpPost(PUSH_URL);
 
			httppost.addHeader("Authorization", token); 
			httppost.addHeader("Accept", "application/json"); 

			SamLog.i(TAG,"before execute");
			HttpResponse response;  
			response = httpclient.execute(httppost); 
			SamLog.i(TAG,"after execute");
			
 
			statusCode = response.getStatusLine().getStatusCode();  
			if (statusCode == HttpStatus.SC_OK) {  
				String rev = EntityUtils.toString(response.getEntity());
				SamLog.e(TAG,rev);
				
				JSONObject obj = new JSONObject(rev);

				JSONObject header;   
				header = obj.getJSONObject("header");
				JSONObject body;
				body = obj.getJSONObject("body");
				
				String category = header.getString("category");
				if(category.equals("question")){
					SamLog.i(TAG,"received question");
					hpinfo.category = HttpPushInfo.QUESTION;
					hpinfo.datetime = body.getLong("datetime");
					hpinfo.quest_id = body.getString("quest_id");
					hpinfo.quest = body.getString("quest");
					hpinfo.opt = body.getInt("opt");

					JSONObject asker = body.getJSONObject("asker");
					hpinfo.unique_id = asker.getLong("id");
					hpinfo.username = asker.getString("username");
					hpinfo.cellphone = asker.getString("cellphone");
					hpinfo.lastupdate = asker.getLong("lastupdate");
					
					JSONObject easemob = asker.getJSONObject("easemob");
					hpinfo.easemob_username = easemob.getString("username");

					hpinfo.avatar = getImageFilename(asker);
					
				}else if(category.equals("answer")){
					SamLog.i(TAG,"received answer");
					hpinfo.category = HttpPushInfo.ANSWER;
					JSONObject ans = body.getJSONObject("ans");
					hpinfo.answer = ans.getString("answer");

					JSONObject syservicer = body.getJSONObject("syservicer");
					hpinfo.unique_id = syservicer.getLong("id");
					hpinfo.cellphone = syservicer.getString("cellphone");
					hpinfo.username = syservicer.getString("username");
					hpinfo.area = syservicer.getString("area");
					hpinfo.location = syservicer.getString("location");
					hpinfo.desc = syservicer.getString("desc");
					hpinfo.lastupdate = syservicer.getLong("lastupdate");
					
					JSONObject easemobA = syservicer.getJSONObject("easemob");
					hpinfo.easemob_username = easemobA.getString("username");
					hpinfo.avatar = getImageFilename(syservicer);

					JSONObject quest = body.getJSONObject("quest");
					hpinfo.quest_id = quest.getString("quest_id");
					hpinfo.quest = quest.getString("quest");
					
				}else if(category.equals("easemob")){
					SamLog.i(TAG,"received easemob");
					hpinfo.category = HttpPushInfo.EASEMOBINFO;
					hpinfo.unique_id = body.getLong("id");
					hpinfo.cellphone = body.getString("cellphone");
					JSONObject easemobA = body.getJSONObject("easemob");
					hpinfo.easemob_username = easemobA.getString("username");
				}else{
					SamLog.e(TAG,"Warning: not support this push cmd");
					throw (new Exception());
				}
				
			} else{
				SamLog.e(TAG,"HttpPushWait status code:"+statusCode);
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
	
	public boolean signin(String country_code,String username,String password){
		if(username==null || password==null){
			SamLog.e(TAG,"login param is null");
			return false;
		}
		
		String rev=null;
			
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
			SamLog.i(TAG,url);

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
				SamLog.i(TAG,"rev:"+rev);
				JSONObject obj = new JSONObject(rev); 
				ret = obj.getInt("ret");
				if(ret == SignService.RET_SI_FROM_SERVER_OK){
					token_id = obj.getString("token");
					JSONObject user = obj.getJSONObject("user");
					userinfo.unique_id = user.getLong("id");
					userinfo.username = user.getString("username");
					userinfo.countrycode = user.getString("country_code");
					userinfo.phonenumber = user.getString("cellphone");
					userinfo.password = password;
					userinfo.usertype = user.getInt("type");
					if(userinfo.usertype == LoginUser.MIDSERVER){
						userinfo.area = user.getString("area");
						userinfo.location = user.getString("location");
						userinfo.description= user.getString("desc");
					}
					userinfo.lastupdate = user.getLong("lastupdate");
					userinfo.imagefile = getImageFilename(user);
					
				}else{
					SamLog.i(TAG,"ret:"+ret);
				}
				return true;
			}else{
				SamLog.e(TAG,"sign in statusCode:"+statusCode);
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

	public boolean signout(String token){
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
			SamLog.i(TAG,url);

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
				SamLog.i(TAG,"ret:"+ret);
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
	
	public boolean signup(String username,String password,String confirm_pwd,String cellphone,String country_code){
		if(username==null || password==null || confirm_pwd == null||cellphone==null||country_code==null){
			SamLog.e(TAG,"sign up param is null");
			return false;
		}
			
		/*Construct sign up json data*/
		try{
			JSONObject signup_header = new JSONObject();
			signup_header.putOpt("action", "register");
			
			JSONObject signup_body = new JSONObject();
			signup_body.putOpt("cellphone",cellphone);
			signup_body.putOpt("country_code",country_code);			
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
			SamLog.i(TAG,url);

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

				SamLog.i(TAG,"rev:" + rev);
				if(ret == SignService.RET_SU_FROM_SERVER_OK){
					token_id = obj.getString("token");
					JSONObject user = obj.getJSONObject("user");
					userinfo.unique_id = user.getLong("id");
					userinfo.username = user.getString("username");
					userinfo.phonenumber = user.getString("cellphone");
					userinfo.password = password;
					userinfo.usertype = user.getInt("type");
					userinfo.lastupdate = user.getLong("lastupdate");
				}
				return true;
			}else{
				SamLog.i(TAG,"sign up statuc code:"+statusCode);
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
			
			SamLog.i(TAG,url);

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
				SamLog.i(TAG,"rev:"+rev);
				JSONObject obj = new JSONObject(rev); 
				ret = obj.getInt("ret");
				if(ret == SamService.RET_SEND_QUESTION_FROM_SERVER_OK){
					question_id = obj.getString("question_id");
				}else{
					SamLog.i(TAG,"ret:"+ret);
				}
				return true;
			}else{
				SamLog.e(TAG,"status code:"+statusCode);
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
			
			SamLog.i(TAG,url);

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
				SamLog.i(TAG,"rev:"+rev);
				JSONObject obj = new JSONObject(rev); 
				ret = obj.getInt("ret");
				if(ret == SamService.RET_CANCEL_QUESTION_FROM_SERVER_OK){
					//this.question_id = obj.getString("question_id");
					this.question_id = question_id;
				}else{
					SamLog.i(TAG,"ret:"+ret);
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



	public boolean upgrade(String token,SamVendorInfo vInfo){
		/*Construct sign up json data*/
		try{
			JSONObject upgrade_header = new JSONObject();
			upgrade_header.putOpt("action", "upgrade");
			upgrade_header.putOpt("token",token);
			
			
			JSONObject upgrade_body = new JSONObject();
			upgrade_body.putOpt("area", vInfo.getArea());
			upgrade_body.putOpt("location", vInfo.getLocation());
			upgrade_body.putOpt("desc", vInfo.getDesc());

			
			JSONObject upgrade_data = new JSONObject();
			upgrade_data.put("header", upgrade_header);
			upgrade_data.put("body", upgrade_body);
			

			List<BasicNameValuePair> params = new LinkedList<BasicNameValuePair>();
			params.add(new BasicNameValuePair("data",upgrade_data.toString()));
			String param = URLEncodedUtils.format(params, "UTF-8"); 
			
			String url = URL + "?" + param;
			
			SamLog.i(TAG,url);

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
				SamLog.i(TAG,"rev:"+rev);
				JSONObject obj = new JSONObject(rev); 
				ret = obj.getInt("ret");
				if(ret == SamService.RET_UPGRADE_FROM_SERVER_OK){
					token_id = obj.getString("token");
				}else{
					SamLog.i(TAG,"ret:"+ret);
				}
				
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
			
			SamLog.i(TAG,url);

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
				SamLog.i(TAG,"rev:"+rev);
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
			SamLog.i(TAG,"getImageFilename:"+imgFileName);
			return imgFileName;
		}catch(JSONException e){
			SamLog.i(TAG,"no avatar for this user");	
			return null;
		}
	}

	private void setAreaLocationDesc(ContactUser user,JSONObject jo ){
		try{
			String area = jo.getString("area");
			String location = jo.getString("location");
			String desc = jo.getString("desc");
			user.setarea(area);
			user.setlocation(location);
			user.setdescription(desc);
		}catch(JSONException e){
			SamLog.i(TAG,"this user not sam vendor ");	
			return;
		}
	}

	public boolean queryui_withoutToken(String phonenumber){
		try{
			JSONObject queryui_header = new JSONObject();
			queryui_header.putOpt("action", "query");

			JSONObject jsonparam = new JSONObject();
			jsonparam.putOpt("username", phonenumber);
			
			JSONObject queryui_body = new JSONObject();
			queryui_body.putOpt("opt", 3);
			queryui_body.put("param",jsonparam);

			
			JSONObject queryui_data = new JSONObject();
			queryui_data.put("header", queryui_header);
			queryui_data.put("body", queryui_body);
			

			List<BasicNameValuePair> params = new LinkedList<BasicNameValuePair>();
			params.add(new BasicNameValuePair("data",queryui_data.toString()));
			String param = URLEncodedUtils.format(params, "UTF-8"); 
			
			String url = URL + "?" + param;
			
			SamLog.i(TAG,url);

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
				SamLog.i(TAG,"rev:"+rev);
				JSONObject obj = new JSONObject(rev); 
				ret = obj.getInt("ret");
				if(ret == SamService.RET_QUERY_USERINFO_SERVER_OK){
					JSONArray jsonArrayX = obj.getJSONArray("users");
					for (int i = 0; i < jsonArrayX.length(); i++) {
						JSONObject jo = (JSONObject) jsonArrayX.get(i);
						ContactUser ui = new ContactUser();
						//{"id": id, "mail":"138","username":"138","cellphone":"1381196123","type":0,"avatar":origin_,last_update}
						ui.setunique_id(jo.getLong("id"));
						ui.setusername(jo.getString("username"));
						ui.setphonenumber(jo.getString("cellphone"));
						if(!Constants.USERNAME_EQUAL_EASEMOB_ID){
							ui.seteasemob_username(jo.getString("cellphone"));
						}else{
							ui.seteasemob_username(jo.getString("username"));
						}
						ui.setusertype(jo.getInt("type"));
						if(ui.getusertype() == LoginUser.MIDSERVER){
							setAreaLocationDesc(ui,jo);
						}
						ui.setlastupdate(jo.getLong("lastupdate"));
						ui.setimagefile(getImageFilename(jo));
						uiArray.add(ui);
					}
					
				}else{
					SamLog.i(TAG,"ret:"+ret);
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

	
	public boolean queryui(String queryname,String token){
		try{
			JSONObject queryui_header = new JSONObject();
			queryui_header.putOpt("action", "query");
			queryui_header.putOpt("token",token);

			JSONObject jsonparam = new JSONObject();
			jsonparam.putOpt("username", queryname);
			
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
			
			SamLog.i(TAG,url);

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
				SamLog.i(TAG,"rev:"+rev);
				JSONObject obj = new JSONObject(rev); 
				ret = obj.getInt("ret");
				if(ret == SamService.RET_QUERY_USERINFO_SERVER_OK){
					JSONArray jsonArrayX = obj.getJSONArray("users");
					for (int i = 0; i < jsonArrayX.length(); i++) {
						JSONObject jo = (JSONObject) jsonArrayX.get(i);
						ContactUser ui = new ContactUser();
						//{"id": id, "mail":"138","username":"138","cellphone":"1381196123","type":0,"avatar":origin_,last_update}
						ui.setunique_id(jo.getLong("id"));
						ui.setusername(jo.getString("username"));
						ui.setphonenumber(jo.getString("cellphone"));
						if(!Constants.USERNAME_EQUAL_EASEMOB_ID){
							ui.seteasemob_username(jo.getString("cellphone"));
						}else{
							ui.seteasemob_username(jo.getString("username"));
						}
						ui.setusertype(jo.getInt("type"));
						if(ui.getusertype() == LoginUser.MIDSERVER){
							setAreaLocationDesc(ui,jo);
						}
						ui.setlastupdate(jo.getLong("lastupdate"));
						ui.setimagefile(getImageFilename(jo));
						uiArray.add(ui);
					}
					
				}else{
					SamLog.i(TAG,"ret:"+ret);
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
			
			SamLog.i(TAG,url);

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
			SamLog.e(TAG,"statusCode:"+statusCode);
			if(statusCode == HttpStatus.SC_OK ){
				String rev = EntityUtils.toString(response.getEntity());
				SamLog.i(TAG,"rev:"+rev);
				JSONObject obj = new JSONObject(rev); 
				ret = obj.getInt("ret");
				if(ret == SamService.RET_QUERY_USERINFO_SERVER_OK){
					JSONArray jsonArrayX = obj.getJSONArray("users");
					for (int i = 0; i < jsonArrayX.length(); i++) {
						JSONObject jo = (JSONObject) jsonArrayX.get(i);
						ContactUser ui = new ContactUser();
						//{"id": id, "mail":"138","username":"138","cellphone":"1381196123","type":0,"avatar":origin_,last_update}
						ui.setunique_id(jo.getLong("id"));
						ui.setusername(jo.getString("username"));
						ui.setphonenumber(jo.getString("cellphone"));
						if(!Constants.USERNAME_EQUAL_EASEMOB_ID){
							ui.seteasemob_username(jo.getString("cellphone"));
						}else{
							ui.seteasemob_username(jo.getString("username"));
						}
						ui.setusertype(jo.getInt("type"));
						if(ui.getusertype() == LoginUser.MIDSERVER){
							setAreaLocationDesc(ui,jo);
						}
						ui.setlastupdate(jo.getLong("lastupdate"));
						ui.setimagefile(getImageFilename(jo));
						uiArray.add(ui);
					}
					
				}else{
					SamLog.i(TAG,"ret:"+ret);
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
			SamLog.i(TAG,url_chat);

			
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

			if(conn.getResponseCode() != HttpURLConnection.HTTP_OK){   
				return false; 
			}

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
			SamLog.i(TAG,"rev:"+rev);

			JSONObject obj = new JSONObject(rev);
			ret = obj.getInt("ret");

			return true;
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



	public boolean sendcomments(String comments, String token){
		/*Construct sign up json data*/
		try{
			JSONObject sendc_header = new JSONObject();
			sendc_header.putOpt("action", "feedback");
			sendc_header.putOpt("token", token);
			
			
			JSONObject sendc_body = new JSONObject();
			sendc_body.putOpt("comment", comments);

			
			JSONObject sendc_data = new JSONObject();
			sendc_data.put("header", sendc_header);
			sendc_data.put("body", sendc_body);
			

			List<BasicNameValuePair> params = new LinkedList<BasicNameValuePair>();
			params.add(new BasicNameValuePair("data",sendc_data.toString()));
			String param = URLEncodedUtils.format(params, "UTF-8"); 
			
			String url = URL + "?" + param;
			
			SamLog.i(TAG,url);

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
				SamLog.i(TAG,"rev:"+rev);
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

	public boolean uploadFG(List<String>photoes,String comment,String token){
		String CrLf = "\r\n";

		HttpURLConnection conn = null;
		OutputStream os = null;
		InputStream is = null;

		try {
			JSONObject uf_header = new JSONObject();
			uf_header.putOpt("action", "article-publish");
			uf_header.putOpt("token", token);

			JSONObject uf_body = new JSONObject();
			if(comment!=null && comment.length()>0){
				uf_body.putOpt("comment", comment);
			}else{
				uf_body.putOpt("comment", "");
			}
			
			JSONObject uf_data = new JSONObject();
			uf_data.put("header", uf_header);
			uf_data.put("body", uf_body);
			
			List<BasicNameValuePair> params = new LinkedList<BasicNameValuePair>();
			params.add(new BasicNameValuePair("data",uf_data.toString()));
			String param = URLEncodedUtils.format(params, "UTF-8"); 
			
			String url_aticle = URL_ATICLE + "?" + param;
			SamLog.i(TAG,url_aticle);
			
			java.net.URL url = new java.net.URL(url_aticle);
			conn = (HttpURLConnection)url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");

			int i=0;
			InputStream imgIs=null;
			ArrayList<byte[]> imgDatalist = new ArrayList<byte[]>();
			ArrayList<String> msg_head_list = new ArrayList<String>();
			byte[] imgData=null;
			String msg_header ="";
			
			if(photoes!=null && photoes.size()>0){
				for(i=0;i<photoes.size();i++){
					imgIs = new FileInputStream(new File(photoes.get(i))); 
					imgData = new byte[imgIs.available()];
					imgIs.read(imgData);
					imgIs.close();
					imgDatalist.add(imgData);

					msg_header="";
					msg_header +=CrLf +"-----------------------------4664151417711" + CrLf;
						           
					msg_header += "Content-Disposition: form-data; name=\"file"+i+"\"; filename=\""+photoes.get(i).trim()+"\""
									+ CrLf;
					msg_header += "Content-Type: image/jpeg" + CrLf;
					
					msg_header += CrLf;
					msg_head_list.add(msg_header);
					//SamLog.e(TAG,msg_header);
				}
			}

			SamLog.i(TAG,"photo num:"+photoes.size()+"i:"+i);

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

			
				
			//message1 += "-----------------------------4664151417711" + CrLf;
			//message1 += "Content-Disposition: form-data; name=\"photo\"; filename=\""+filePath.trim()+"\""
			//		+ CrLf;
			//message1 += "Content-Type: image/jpeg" + CrLf;
			//message1 += CrLf;
				
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

			int imgLength=0;
			for(int j=0;j<i;j++){
				imgLength = imgLength + msg_head_list.get(j).length() + imgDatalist.get(j).length;
			}

			if(i==0){
				imgLength = message1.length() + message2.length();
			}else{
				imgLength = message1.length() + imgLength + message2.length();
			}
			
			conn.setRequestProperty(
					"Content-Length",
					String.valueOf(imgLength));

			//SamLog.e(TAG,"m1:"+message1.length() +" m2:"+message2.length() + " imgLength:"+imgLength );

			os = conn.getOutputStream();
			

			os.write(message1.getBytes());

			SamLog.i(TAG,"photo num1:"+photoes.size()+"i:"+i);

			if(i==0){
				os.write(message2.getBytes());
			}else{
				int index = 0;
				int size = 1024;
				for(int j=0;j<i;j++){
					// FIXME
					index = 0;
					size = 1024;
					os.write(msg_head_list.get(j).getBytes());
					do {
						if ((index + size) > imgDatalist.get(j).length) {
							size = imgDatalist.get(j).length - index;
						}
						os.write(imgDatalist.get(j), index, size);
						index += size;
					} while (index < imgDatalist.get(j).length);
					
				}
				os.write(message2.getBytes());
				
			}
			
			os.flush();

			SamLog.i(TAG,"uploadFG status code:"+conn.getResponseCode());

			if(conn.getResponseCode() != HttpURLConnection.HTTP_OK){ 
				SamLog.e(TAG,"HttpStatus:"+conn.getResponseCode());
				return false; 
			}

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
			SamLog.i(TAG,"rev:"+rev);

			JSONObject obj = new JSONObject(rev);
			ret = obj.getInt("ret");

			if(ret != SamService.RET_UPLOAD_FG_SERVER_OK){
				return true;
			}
				
			JSONObject article = obj.getJSONObject("article");
			ainfo.timestamp = article.getLong("timestamp");
			ainfo.article_id = article.getLong("id");

			JSONArray jsonArrayX = article.getJSONArray("recommends");
			for (i = 0; i < jsonArrayX.length(); i++) {
				JSONObject jo = (JSONObject) jsonArrayX.get(i);
				ContactUser ui = new ContactUser();
				ui.setunique_id(jo.getLong("id"));
				ui.setphonenumber(jo.getString("cellphone"));
				ainfo.recommander.add(ui);
			}

			ainfo.status = article.getInt("status");
			ainfo.comment = article.getString("comment");

			jsonArrayX = article.getJSONArray("comments");
			for (i = 0; i < jsonArrayX.length(); i++) {
				CommentInfo cinfo = new CommentInfo();
				JSONObject jo = (JSONObject) jsonArrayX.get(i);
				String content = jo.getString("content");
				cinfo.content = content;
				
				long comm_timestamp = jo.getLong("timestamp");
				cinfo.comments_timestamp = comm_timestamp;
				
				ContactUser ui = new ContactUser();
				JSONObject user = jo.getJSONObject("user");
				ui.setunique_id(user.getLong("id"));
				ui.setphonenumber(user.getString("cellphone"));
				cinfo.commenter = ui;

				ainfo.comments.add(cinfo);
			}
			
			JSONObject pub = article.getJSONObject("publisher");
			ainfo.publisher.setphonenumber(pub.getString("cellphone"));
			ainfo.publisher.setunique_id(pub.getLong("id"));

			jsonArrayX = article.getJSONArray("pics");
			for (i = 0; i < jsonArrayX.length(); i++) {
				JSONObject jo = (JSONObject) jsonArrayX.get(i);
				String pic_url = jo.getString("url");
				ainfo.pics.add(pic_url);
			}

			return true;
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

	public boolean queryFG(String token,long start_timestamp, int fetch_count){
		String CrLf = "\r\n";

		HttpURLConnection conn = null;
		OutputStream os = null;
		InputStream is = null;
		try{
			JSONObject querya_header = new JSONObject();
			querya_header.putOpt("action", "article-query");
			querya_header.putOpt("token", token);
			
			
			JSONObject querya_body = new JSONObject();

			if(start_timestamp == 0){
				querya_body.putOpt("timestamp_start",""+(System.currentTimeMillis()));
			}else{
				querya_body.putOpt("timestamp_start",""+start_timestamp);
			}
			querya_body.putOpt("fetch_count",""+fetch_count);
			SamLog.e(TAG,"featch_count:"+fetch_count);
			//querya_body.putOpt("timestamp_end", ""+(System.currentTimeMillis()-30*24*60*60*1000L));

			querya_body.putOpt("qt",0);
			
			JSONObject querya_data = new JSONObject();
			querya_data.put("header", querya_header);
			querya_data.put("body", querya_body);
			

			List<BasicNameValuePair> params = new LinkedList<BasicNameValuePair>();
			params.add(new BasicNameValuePair("data",querya_data.toString()));
			String param = URLEncodedUtils.format(params, "UTF-8"); 
			
			String url_aticle = URL_ATICLE + "?" + param;
			SamLog.i(TAG,url_aticle);
			
			java.net.URL url = new java.net.URL(url_aticle);
			conn = (HttpURLConnection)url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			
			byte[] imgData = new byte[10];

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
			message1 += "Content-Disposition: form-data; name=\"photo\"; filename=\""+"test.txt"+"\""
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

			//SamLog.e(TAG,"m1:"+message1.length() +" m2:"+message2.length() + " imgLength:"+imgData.length );

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

			SamLog.i(TAG,"queryFG status code:"+conn.getResponseCode());

			if(conn.getResponseCode() != HttpURLConnection.HTTP_OK){  
				
				return false; 
			}

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
			SamLog.i(TAG,"rev:"+rev);

			JSONObject obj = new JSONObject(rev);
			ret = obj.getInt("ret");
			if(ret!=SamService.RET_QUERY_FG_SERVER_OK){
				return true;
			}

			int article_counts = obj.getInt("articles_count");
			JSONArray jsonArrayX = obj.getJSONArray("articles");
			SamLog.e(TAG,"jsonArrayX.length():"+jsonArrayX.length());
			for (int x = 0; x < jsonArrayX.length();x++) {
				ArticleInfo ainfo = new ArticleInfo();
				JSONObject article = (JSONObject) jsonArrayX.get(x);
				ainfo.timestamp = article.getLong("timestamp");
				ainfo.article_id = article.getLong("id");

				JSONArray jsonArrayR = article.getJSONArray("recommends");
				for (int i = 0; i < jsonArrayR.length(); i++) {
					JSONObject jo = (JSONObject) jsonArrayR.get(i);
					ContactUser ui = new ContactUser();
					ui.setunique_id(jo.getLong("id"));
					ui.setphonenumber(jo.getString("cellphone"));
					ainfo.recommander.add(ui);
				}

				ainfo.status = article.getInt("status");
				ainfo.comment = article.getString("comment");

				JSONArray jsonArrayC = article.getJSONArray("comments");
				for (int i = 0; i < jsonArrayC.length(); i++) {
					CommentInfo cinfo = new CommentInfo();
					JSONObject jo = (JSONObject) jsonArrayC.get(i);
					String content = jo.getString("content");
					cinfo.content = content;

					long comm_timestamp = jo.getLong("timestamp");
					cinfo.comments_timestamp = comm_timestamp;
				
					ContactUser ui = new ContactUser();
					JSONObject user = jo.getJSONObject("user");
					ui.setunique_id(user.getLong("id"));
					ui.setphonenumber(user.getString("cellphone"));
					cinfo.commenter = ui;

					ainfo.comments.add(cinfo);
				}

				JSONObject pub = article.getJSONObject("publisher");
				ainfo.publisher.setunique_id(pub.getLong("id"));
				ainfo.publisher.setphonenumber(pub.getString("cellphone"));

				JSONArray jsonArrayP = article.getJSONArray("pics");
				for (int i = 0; i < jsonArrayP.length(); i++) {
					JSONObject jo = (JSONObject) jsonArrayP.get(i);
					String pic_url = jo.getString("url");
					ainfo.pics.add(pic_url);
				}

				ainfoList.add(ainfo);

			}

			return true;
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

	public boolean commentFG(long article_id,String comment, String token){
		String CrLf = "\r\n";

		HttpURLConnection conn = null;
		OutputStream os = null;
		InputStream is = null;
		try{
			JSONObject commetfg_header = new JSONObject();
			commetfg_header.putOpt("action", "article-comment");
			commetfg_header.putOpt("token", token);
			
			
			JSONObject commetfg_body = new JSONObject();
			
			commetfg_body.putOpt("article_id",""+article_id);
			commetfg_body.putOpt("comment", comment);
			
			JSONObject commetfg_data = new JSONObject();
			commetfg_data.put("header", commetfg_header);
			commetfg_data.put("body", commetfg_body);
			

			List<BasicNameValuePair> params = new LinkedList<BasicNameValuePair>();
			params.add(new BasicNameValuePair("data",commetfg_data.toString()));
			String param = URLEncodedUtils.format(params, "UTF-8"); 
			
			String url_aticle = URL_ATICLE + "?" + param;
			SamLog.e(TAG,"url_aticle:"+url_aticle);

			
			java.net.URL url = new java.net.URL(url_aticle);
			conn = (HttpURLConnection)url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			
			byte[] imgData = new byte[10];

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
			message1 += "Content-Disposition: form-data; name=\"photo\"; filename=\""+"test.txt"+"\""
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

			SamLog.i(TAG,"commentFG status code:"+conn.getResponseCode());
			if(conn.getResponseCode() != HttpURLConnection.HTTP_OK){   
				return false; 
			}

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
			SamLog.i(TAG,"rev:"+rev);

			JSONObject obj = new JSONObject(rev);
			ret = obj.getInt("ret");
			if(ret!=SamService.RET_COMMENT_FG_SERVER_OK){
				return true;
			}

			JSONObject article = obj.getJSONObject("article");
			ainfo.timestamp = article.getLong("timestamp");
			ainfo.article_id = article.getLong("id");

			JSONArray jsonArrayX = article.getJSONArray("recommends");
			for (int i = 0; i < jsonArrayX.length(); i++) {
				JSONObject jo = (JSONObject) jsonArrayX.get(i);
				ContactUser ui = new ContactUser();
				ui.setunique_id(jo.getLong("id"));
				ui.setphonenumber(jo.getString("cellphone"));
				ainfo.recommander.add(ui);
			}

			ainfo.status = article.getInt("status");
			ainfo.comment = article.getString("comment");

			jsonArrayX = article.getJSONArray("comments");
			for (int i = 0; i < jsonArrayX.length(); i++) {
				CommentInfo cinfo = new CommentInfo();
				JSONObject jo = (JSONObject) jsonArrayX.get(i);
				String content = jo.getString("content");
				cinfo.content = content;

				long comm_timestamp = jo.getLong("timestamp");
				cinfo.comments_timestamp = comm_timestamp;
				
				ContactUser ui = new ContactUser();
				JSONObject user = jo.getJSONObject("user");
				ui.setunique_id(user.getLong("id"));
				ui.setphonenumber(user.getString("cellphone"));
				cinfo.commenter = ui;

				ainfo.comments.add(cinfo);
			}

			JSONObject pub = article.getJSONObject("publisher");
			ainfo.publisher.setphonenumber(pub.getString("cellphone"));
			ainfo.publisher.setunique_id(pub.getLong("id"));

			jsonArrayX = article.getJSONArray("pics");
			for (int i = 0; i < jsonArrayX.length(); i++) {
				JSONObject jo = (JSONObject) jsonArrayX.get(i);
				String pic_url = jo.getString("url");
				ainfo.pics.add(pic_url);
			}

			return true;
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


	public boolean follow(long user_id,int cmd, String token){
		/*Construct sign up json data*/
		try{
			JSONObject follow_header = new JSONObject();
			follow_header.putOpt("action", "follow");
			follow_header.putOpt("token",token);
			
			
			JSONObject follow_body = new JSONObject();
			follow_body.putOpt("user_id", ""+user_id);
			if(cmd == FollowCoreObj.FOLLOW){
				follow_body.putOpt("flag",""+1);
			}else{
				follow_body.putOpt("flag",""+2);
			}
			follow_body.putOpt("both", false);
			
			JSONObject follow_data = new JSONObject();
			follow_data.put("header", follow_header);
			follow_data.put("body", follow_body);
			

			List<BasicNameValuePair> params = new LinkedList<BasicNameValuePair>();
			params.add(new BasicNameValuePair("data",follow_data.toString()));
			String param = URLEncodedUtils.format(params, "UTF-8"); 
			
			String url = URL + "?" + param;
			
			SamLog.i(TAG,url);

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
				SamLog.i(TAG,"rev:"+rev);
				JSONObject obj = new JSONObject(rev); 
				ret = obj.getInt("ret");
				SamLog.i(TAG,"ret:"+ret);
				
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


	public boolean queryFollower(String token){
		/*Construct sign up json data*/
		try{
			JSONObject header = new JSONObject();
			header.putOpt("action", "relation");
			header.putOpt("token",token);
			
			
			JSONObject body = new JSONObject();
			body.putOpt("type", "2");
			
			JSONObject data = new JSONObject();
			data.put("header", header);
			data.put("body", body);
			

			List<BasicNameValuePair> params = new LinkedList<BasicNameValuePair>();
			params.add(new BasicNameValuePair("data",data.toString()));
			String param = URLEncodedUtils.format(params, "UTF-8"); 
			
			String url = URL + "?" + param;
			
			SamLog.i(TAG,url);

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
				SamLog.i(TAG,"rev:"+rev);
				JSONObject obj = new JSONObject(rev); 
				ret = obj.getInt("ret");
				SamLog.i(TAG,"ret:"+ret);
				if(ret == SamService.RET_QUERY_FOLLOWER_SERVER_OK){
					JSONArray jsonArrayX = obj.getJSONArray("users");
					for (int i = 0; i < jsonArrayX.length(); i++) {
						JSONObject jo = (JSONObject) jsonArrayX.get(i);
						ContactUser ui = new ContactUser();
						
						ui.setunique_id(jo.getLong("id"));
						ui.setphonenumber(jo.getString("cellphone"));
						ui.setusertype(jo.getInt("type"));
						ui.setusername(jo.getString("username"));
						ui.setimagefile(getImageFilename(jo));
						JSONObject easemobA = jo.getJSONObject("easemob");
						ui.seteasemob_username(easemobA.getString("username"));
						ui.setlastupdate(jo.getLong("lastupdate"));
												
						if(ui.getusertype() == LoginUser.MIDSERVER){
							setAreaLocationDesc(ui,jo);
						}						
						
						uiArray.add(ui);
					}
					
				}
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
	
}
