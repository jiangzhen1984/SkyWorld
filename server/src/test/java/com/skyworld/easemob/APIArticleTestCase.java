package com.skyworld.easemob;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

import javax.servlet.http.Part;

import junit.framework.TestCase;

import org.json.JSONObject;
import org.json.JSONTokener;
import org.junit.Test;

import com.skyworld.service.APIArticleCommentService;
import com.skyworld.service.APIArticlePushlihService;
import com.skyworld.service.APIArticleQuery;
import com.skyworld.service.APIArticleRecommendationService;
import com.skyworld.service.APIChainService;
import com.skyworld.service.APICode;
import com.skyworld.service.APIJsonPartDispatchService;
import com.skyworld.service.APILoginService;
import com.skyworld.service.APIRegisterService;
import com.skyworld.service.APIService;

public class APIArticleTestCase extends TestCase {
	
	MockHttpServletRequest request;
	MockHttpServletResponse response;
	
	APIService userService;
	APIJsonPartDispatchService service;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		
		request = new MockHttpServletRequest();
		response = new MockHttpServletResponse();
		
		userService = new APIChainService();
		
		((APIChainService)userService).addActionMapping("login", new APILoginService());
		((APIChainService)userService).addActionMapping("register", new APIRegisterService());
		
		service =  new APIJsonPartDispatchService();
		service.addActionMapping("article-publish", new APIArticlePushlihService());
		service.addActionMapping("article-recommend", new APIArticleRecommendationService());
		service.addActionMapping("article-comment", new APIArticleCommentService());
		service.addActionMapping("article-query", new APIArticleQuery());
		

	}
	
	@Test
	public void testArticlePublish() {
		String result;
		JSONObject respJson;
		
		
		// for no token and no comment no image
		response.resetBuffer();
		request.setParam("data", buildInvalidData("article-publish"));
		service.service(request, response);
		result = response.getFlusedBuffer();
		respJson = parse(result);
		assertEquals(APICode.REQUEST_PARAMETER_NOT_STISFIED, respJson.getInt("ret"));
		
		
		
		//register
		response.resetBuffer();
		request.setParam("data", buildNormalRegister("12346", "12346"));
		userService.service(request, response);
		result = response.getFlusedBuffer();
		respJson = parse(result);
		assertEquals(APICode.SUCCESS, respJson.getInt("ret"));
		assertTrue(respJson.has("user"));
		assertTrue(respJson.has("token"));
		
		String token = respJson.getString("token");
		
		// for with comment and no image
		response.resetBuffer();
		request.setParam("data", buildNormalDataWithoutPic("article-publish", "eeeee", token));
		service.service(request, response);
		result = response.getFlusedBuffer();
		respJson = parse(result);
		assertEquals(APICode.SUCCESS, respJson.getInt("ret"));
		
		
		
		// for with comment and no image
		response.resetBuffer();
		request.setParam("data", buildNormalDataWithoutPic("article-publish", "eeeee", token));
		request.addPart(new LocalPart("/home/jiangzhen/Pictures/b.png"));
		request.addPart(new LocalPart("/home/jiangzhen/Pictures/b1.png"));
		request.addPart(new LocalPart("/home/jiangzhen/Pictures/c.png"));
		
		service.service(request, response);
		result = response.getFlusedBuffer();
		respJson = parse(result);
		System.out.println(result);
		assertEquals(APICode.SUCCESS, respJson.getInt("ret"));
		
	}
	
	
	@Test
	public void testArticleRecomment() {
		String result;
		JSONObject respJson;
		
		
		
		//register
		response.resetBuffer();
		request.setParam("data", buildNormalRegister("1234", "1234"));
		userService.service(request, response);
		result = response.getFlusedBuffer();
		respJson = parse(result);
		assertEquals(APICode.SUCCESS, respJson.getInt("ret"));
		assertTrue(respJson.has("user"));
		assertTrue(respJson.has("token"));
		
		String token = respJson.getString("token");
		
		
		
		
		//register
		response.resetBuffer();
		request.setParam("data", buildNormalRegister("1", "2"));
		userService.service(request, response);
		result = response.getFlusedBuffer();
		respJson = parse(result);
		assertEquals(APICode.SUCCESS, respJson.getInt("ret"));
		assertTrue(respJson.has("user"));
		assertTrue(respJson.has("token"));
		
		String token1 = respJson.getString("token");
		
		// for with comment and no image
		response.resetBuffer();
		request.setParam("data", buildNormalDataWithoutPic("article-publish", "eeeee", token));
		service.service(request, response);
		result = response.getFlusedBuffer();
		respJson = parse(result);
		assertEquals(APICode.SUCCESS, respJson.getInt("ret"));
		long arid = respJson.getJSONObject("article").getLong("id");
		
		
		response.resetBuffer();
		request.setParam("data", buildDataForRecommendWithInvalidToken());
		service.service(request, response);
		result = response.getFlusedBuffer();
		respJson = parse(result);
		assertEquals(APICode.TOKEN_INVALID, respJson.getInt("ret"));
		
		
		response.resetBuffer();
		System.out.println(buildNormalDataForRecommend(token1, arid, true));
		request.setParam("data", buildNormalDataForRecommend(token1, arid, true));
		service.service(request, response);
		result = response.getFlusedBuffer();
		respJson = parse(result);
		System.out.println(result);
		assertEquals(APICode.SUCCESS, respJson.getInt("ret"));
		assertEquals(1, respJson.getJSONObject("article").getJSONArray("recommends").length());
		
		
		
		response.resetBuffer();
		request.setParam("data", buildNormalDataForRecommend(token1, arid, false));
		service.service(request, response);
		result = response.getFlusedBuffer();
		respJson = parse(result);
		System.out.println(result);
		assertEquals(APICode.SUCCESS, respJson.getInt("ret"));
		assertEquals(0, respJson.getJSONObject("article").getJSONArray("recommends").length());
		
		
	}
	
	
	
	
	
	@Test
	public void testArticleComment() {
		String result;
		JSONObject respJson;
		
		
		
		//register
		response.resetBuffer();
		request.setParam("data", buildNormalRegister("12345", "12345"));
		userService.service(request, response);
		result = response.getFlusedBuffer();
		respJson = parse(result);
		assertEquals(APICode.SUCCESS, respJson.getInt("ret"));
		assertTrue(respJson.has("user"));
		assertTrue(respJson.has("token"));
		
		String token = respJson.getString("token");
		
		
		
		
		//register
		response.resetBuffer();
		request.setParam("data", buildNormalRegister("17", "27"));
		userService.service(request, response);
		result = response.getFlusedBuffer();
		respJson = parse(result);
		assertEquals(APICode.SUCCESS, respJson.getInt("ret"));
		assertTrue(respJson.has("user"));
		assertTrue(respJson.has("token"));
		
		String token1 = respJson.getString("token");
		
		
		
		//register
		response.resetBuffer();
		request.setParam("data", buildNormalRegister("178", "278"));
		userService.service(request, response);
		result = response.getFlusedBuffer();
		respJson = parse(result);
		assertEquals(APICode.SUCCESS, respJson.getInt("ret"));
		assertTrue(respJson.has("user"));
		assertTrue(respJson.has("token"));
		
		String token2 = respJson.getString("token");
		
		// for with comment and no image
		response.resetBuffer();
		System.out.println(buildNormalDataWithoutPic("article-publish", "eeeee", token));
		request.setParam("data", buildNormalDataWithoutPic("article-publish", "eeeee", token));
		service.service(request, response);
		result = response.getFlusedBuffer();
		respJson = parse(result);
		assertEquals(APICode.SUCCESS, respJson.getInt("ret"));
		long arid = respJson.getJSONObject("article").getLong("id");
		
		
		
		
		response.resetBuffer();
		System.out.println(buildNormalDataForComment(token1, arid, "eee"));
		request.setParam("data", buildNormalDataForComment(token1, arid, "eee"));
		service.service(request, response);
		result = response.getFlusedBuffer();
		respJson = parse(result);
		System.out.println(result);
		assertEquals(APICode.SUCCESS, respJson.getInt("ret"));
		assertEquals(1, respJson.getJSONObject("article").getJSONArray("comments").length());
		
		
		response.resetBuffer();
		request.setParam("data", buildNormalDataForComment(token2, arid, "eee"));
		service.service(request, response);
		result = response.getFlusedBuffer();
		respJson = parse(result);
		System.out.println(result);
		assertEquals(APICode.SUCCESS, respJson.getInt("ret"));
		assertEquals(2, respJson.getJSONObject("article").getJSONArray("comments").length());
		
	}
	
	
	
	@Test
	public void testArticleQuery() {
		String result;
		JSONObject respJson;
		
		
		// for no token and no comment no image
		response.resetBuffer();
		request.setParam("data", buildInvalidData("article-publish"));
		service.service(request, response);
		result = response.getFlusedBuffer();
		respJson = parse(result);
		assertEquals(APICode.REQUEST_PARAMETER_NOT_STISFIED, respJson.getInt("ret"));
		
		
		
		//register
		response.resetBuffer();
		request.setParam("data", buildNormalRegister("aaaaa", "aa"));
		userService.service(request, response);
		result = response.getFlusedBuffer();
		respJson = parse(result);
		assertEquals(APICode.SUCCESS, respJson.getInt("ret"));
		assertTrue(respJson.has("user"));
		assertTrue(respJson.has("token"));
		
		String token = respJson.getString("token");
		
		// for with comment and no image
		response.resetBuffer();
		request.setParam("data", buildNormalDataWithoutPic("article-publish", "eeeee", token));
		service.service(request, response);
		result = response.getFlusedBuffer();
		respJson = parse(result);
		assertEquals(APICode.SUCCESS, respJson.getInt("ret"));
		
		
		
		// for with comment and no image
		response.resetBuffer();
		request.setParam("data", buildNormalDataWithoutPic("article-publish", "eeeee", token));
		request.addPart(new LocalPart("/home/jiangzhen/Pictures/b.png"));
		request.addPart(new LocalPart("/home/jiangzhen/Pictures/b1.png"));
		request.addPart(new LocalPart("/home/jiangzhen/Pictures/c.png"));
		
		service.service(request, response);
		result = response.getFlusedBuffer();
		respJson = parse(result);
		System.out.println(result);
		assertEquals(APICode.SUCCESS, respJson.getInt("ret"));
		long arid = respJson.getJSONObject("article").getLong("id");
		
		
		response.resetBuffer();
		request.setParam("data", buildNormalDataForComment(token, arid, "eee"));
		service.service(request, response);
		result = response.getFlusedBuffer();
		respJson = parse(result);
		System.out.println(result);
		assertEquals(APICode.SUCCESS, respJson.getInt("ret"));
		assertEquals(1, respJson.getJSONObject("article").getJSONArray("comments").length());
		
		
		response.resetBuffer();
		request.setParam("data", buildNormalDataForComment(token, arid, "eee"));
		service.service(request, response);
		result = response.getFlusedBuffer();
		respJson = parse(result);
		System.out.println(result);
		assertEquals(APICode.SUCCESS, respJson.getInt("ret"));
		assertEquals(2, respJson.getJSONObject("article").getJSONArray("comments").length());
		
		
		
		response.resetBuffer();
		request.setParam("data", buildNormalDataForRecommend(token, arid, true));
		service.service(request, response);
		result = response.getFlusedBuffer();
		respJson = parse(result);
		System.out.println(result);
		assertEquals(APICode.SUCCESS, respJson.getInt("ret"));
		assertEquals(1, respJson.getJSONObject("article").getJSONArray("recommends").length());
		
		
		// for  query
		response.resetBuffer();
		request.setParam("data", buildNormalArticleQuery(token));
		
		service.service(request, response);
		result = response.getFlusedBuffer();
		respJson = parse(result);
		System.out.println(result);
		assertEquals(APICode.SUCCESS, respJson.getInt("ret"));
		assertTrue(respJson.getInt("articles_count") >= 2);
		assertTrue(respJson.getJSONArray("articles").length() >= 2);
	}
	
	
	
	
	String buildNormalArticleQuery(String token) {
		JSONObject root = new JSONObject();
		JSONObject header = new JSONObject();
		JSONObject body = new JSONObject();
		
		root.put("header", header);
		root.put("body", body);
		
		header.put("token", token);
		header.put("action", "article-query");
		
		body.put("timestamp_start", System.currentTimeMillis());
		body.put("timestamp_end", System.currentTimeMillis() - 3600 * 1000);
		body.put("qt", 0);
		return root.toString();
	}
	
	
	String buildNormalRegister(String cellphone, String pwd) {
		JSONObject root = new JSONObject();
		JSONObject header = new JSONObject();
		JSONObject body = new JSONObject();

		root.put("header", header);
		root.put("body", body);

		header.put("action", "register");
		body.put("username", cellphone);
		body.put("cellphone", cellphone);
		body.put("pwd", pwd);
		body.put("confirm_pwd", pwd);
		return root.toString();
	}

	JSONObject parse(String str) {
		JSONTokener jsonParser = new JSONTokener(str);
		JSONObject resp = (JSONObject) jsonParser.nextValue();
		return resp;
	}
	
	
	String buildInvalidData(String action) {
		JSONObject root = new JSONObject();
		JSONObject header = new JSONObject();
		JSONObject body = new JSONObject();
		
		root.put("header", header);
		root.put("body", body);
		
		header.put("action", action);
		
		return root.toString();
	}
	
	
	String buildNormalDataWithoutPic(String action, String comment, String token) {
		JSONObject root = new JSONObject();
		JSONObject header = new JSONObject();
		JSONObject body = new JSONObject();
		
		root.put("header", header);
		root.put("body", body);
		
		header.put("token", token);
		header.put("action", action);
		
		body.put("comment", comment);
		
		return root.toString();
	}
	
	
	
	String buildNormalData(String action, String comment, String token) {
		JSONObject root = new JSONObject();
		JSONObject header = new JSONObject();
		JSONObject body = new JSONObject();
		
		root.put("header", header);
		root.put("body", body);
		
		header.put("token", token);
		header.put("action", action);
		
		body.put("comment", comment);
		
		return root.toString();
	}
	
	
	
	
	String buildDataForRecommendWithInvalidToken() {
		JSONObject root = new JSONObject();
		JSONObject header = new JSONObject();
		JSONObject body = new JSONObject();
		
		root.put("header", header);
		root.put("body", body);
		
		header.put("token", "sssssssss");
		header.put("action", "article-recommend");
		
		return root.toString();
	}
	
	
	String buildNormalDataForRecommend(String token, long id, boolean flag) {
		JSONObject root = new JSONObject();
		JSONObject header = new JSONObject();
		JSONObject body = new JSONObject();
		
		root.put("header", header);
		root.put("body", body);
		
		header.put("token", token);
		header.put("action", "article-recommend");
		
		body.put("article_id", id);
		body.put("flag", flag);
		
		return root.toString();
	}
	
	
	
	String buildNormalDataForComment(String token, long id,String comment) {
		JSONObject root = new JSONObject();
		JSONObject header = new JSONObject();
		JSONObject body = new JSONObject();
		
		root.put("header", header);
		root.put("body", body);
		
		header.put("token", token);
		header.put("action", "article-comment");
		
		body.put("article_id", id);
		body.put("comment", comment);
		
		return root.toString();
	}
	
	
	class LocalPart implements Part {
		
		String filepath;
		
		

		public LocalPart(String filepath) {
			super();
			this.filepath = filepath;
		}

		@Override
		public InputStream getInputStream() throws IOException {
			return new FileInputStream(new File(filepath));
		}

		@Override
		public String getContentType() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getName() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getSize() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public void write(String fileName) throws IOException {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void delete() throws IOException {
			// TODO Auto-generated method stub
			
		}

		@Override
		public String getHeader(String name) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Collection<String> getHeaders(String name) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Collection<String> getHeaderNames() {
			// TODO Auto-generated method stub
			return null;
		}
		
	}
	

}
