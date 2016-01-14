package com.android.samservice;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.util.LinkedList;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
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
import org.json.JSONException;
import org.json.JSONObject;
import org.apache.http.message.BasicNameValuePair;

import com.android.samservice.info.LoginUser;
import com.android.samservice.info.SendAnswer;

import android.util.Log;

public class HttpCommClient {
	public static final String TAG="HttpCommClient";
	public static final String URL = "http://121.42.207.185/SkyWorld/api/1.0/UserAPI";
	public static final String URL_QUESTION = "http://121.42.207.185/SkyWorld/api/1.0/QuestionAPI";
	public static final String PUSH_URL = "http://121.42.207.185/SkyWorld/push";
	//public static final String URL = "http://222.222.222.111";
	public static final int CONNECTION_TIMEOUT = 20000;
	public static final int HTTP_TIMEOUT = 10000;

	
	
	public int statusCode;
	public int ret;
	public String token_id;
	public String question_id;
	public LoginUser userinfo;
	public HttpPushInfo hpinfo;
	
	HttpCommClient(){
		statusCode = 0;
		ret = 0;
		token_id = null;
		question_id=null;
		userinfo = new LoginUser();
		hpinfo = new HttpPushInfo();
	}

	public boolean HttpPushWait(String token){
		SamLog.e(TAG,"In HttpPushWait for question ...");
		try {  
			HttpClient httpclient = new DefaultHttpClient();  
			HttpPost httppost = new HttpPost(PUSH_URL);   
			JSONObject obj = new JSONObject();  
			
			httppost.addHeader("Authorization", token); 
			httppost.addHeader("Accept", "application/json"); 

			HttpResponse response;  
			response = httpclient.execute(httppost);  
 
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
		try{
			//Log.e(TAG,"start HttpGetData...");
			HttpGet requestGet = new HttpGet(uri);
			DefaultHttpClient client = new DefaultHttpClient();
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
				}else{
					SamLog.e(TAG,"ret:"+ret);
				}
				return true;
			}else{
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
	
}
