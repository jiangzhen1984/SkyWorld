package com.skyworld.api;

import junit.framework.TestCase;

import org.json.JSONObject;
import org.json.JSONTokener;
import org.junit.Before;
import org.junit.Test;

import com.skyworld.MockHttpServletRequest;
import com.skyworld.MockHttpServletResponse;
import com.skyworld.service.APIChainService;
import com.skyworld.service.APICode;
import com.skyworld.service.APIService;
import com.skyworld.service.article.APIFollowService;
import com.skyworld.service.question.APIAnswerService;
import com.skyworld.service.question.APIInquireService;
import com.skyworld.service.skservicer.APICmpQueryService;
import com.skyworld.service.skservicer.APIUpgradeService;
import com.skyworld.service.system.APIFeedbackService;
import com.skyworld.service.user.APILoginService;
import com.skyworld.service.user.APIRegisterService;
import com.skyworld.service.user.APIUserRelationQueryService;

public class APIServiceTestCase extends TestCase {

	
	MockHttpServletRequest request;
	MockHttpServletResponse response;
	
	APIService service;
	
	
	@Before
	protected void setUp() throws Exception {
		super.setUp();
		request = new MockHttpServletRequest();
		response = new MockHttpServletResponse();
		service = new APIChainService();
		
		((APIChainService)service).addActionMapping("login", new APILoginService());
		((APIChainService)service).addActionMapping("register", new APIRegisterService());
		((APIChainService)service).addActionMapping("upgrade", new APIUpgradeService());
		((APIChainService)service).addActionMapping("question", new APIInquireService());
		((APIChainService)service).addActionMapping("answer", new APIAnswerService());
		((APIChainService)service).addActionMapping("feedback", new APIFeedbackService());
		((APIChainService)service).addActionMapping("follow", new APIFollowService());
		((APIChainService)service).addActionMapping("relation", new APIUserRelationQueryService());
		((APIChainService)service).addActionMapping("skservicer-cmp-query", new APICmpQueryService());
	}
	
	
	@Test
	public void testAPIFollowService() {
		//check for not staticist parameters
		response.resetBuffer();
		request.setParam("data", buildNotEnoughParamForFollowService());
		service.service(request, response);
		String str = response.getFlusedBuffer();
		JSONObject respJson = parse(str);
		assertEquals(APICode.REQUEST_PARAMETER_NOT_STISFIED, respJson.getInt("ret"));
		
		
		
		//register two users
		response.resetBuffer();
		request.setParam("data", buildNormalRegister("11", "11"));
		service.service(request, response);
		str = response.getFlusedBuffer();
		respJson = parse(str);
		assertEquals(APICode.SUCCESS, respJson.getInt("ret"));
		assertTrue(respJson.has("user"));
		assertTrue(respJson.has("token"));
		String token1 = respJson.getString("token");
		long uid1 = respJson.getJSONObject("user").getLong("id");
		
		
		//register two users
		response.resetBuffer();
		request.setParam("data", buildNormalRegister("12", "12"));
		service.service(request, response);
		str = response.getFlusedBuffer();
		respJson = parse(str);
		assertEquals(APICode.SUCCESS, respJson.getInt("ret"));
		assertTrue(respJson.has("user"));
		assertTrue(respJson.has("token"));
		String token2 = respJson.getString("token");
		long uid2 = respJson.getJSONObject("user").getLong("id");
				
		
		System.out.println(buildMakeRelationParamForFollowService(token1, uid2));
		//check for make relation
		response.resetBuffer();
		request.setParam("data", buildMakeRelationParamForFollowService(token1, uid2));
		service.service(request, response);
		str = response.getFlusedBuffer();
		respJson = parse(str);
		assertEquals(APICode.SUCCESS, respJson.getInt("ret"));
		
		
		
		//query relation
		response.resetBuffer();
		request.setParam("data", buildNormalQueryRelationWith3(token1, uid2));
		service.service(request, response);
		str = response.getFlusedBuffer();
		respJson = parse(str);
		assertEquals(APICode.SUCCESS, respJson.getInt("ret"));
		
		
		System.out.println(buildMakeRelationParamForFollowService(token1, uid2));
		//check for make relation
		response.resetBuffer();
		request.setParam("data", buildMakeRemoveRelationParamForFollowService(token1, uid2));
		service.service(request, response);
		str = response.getFlusedBuffer();
		respJson = parse(str);
		assertEquals(APICode.SUCCESS, respJson.getInt("ret"));
		

				
		
	}
	
	
	String buildNotEnoughParamForFollowService() {
		JSONObject root = new JSONObject();
		JSONObject header = new JSONObject();
		JSONObject body = new JSONObject();
		
		root.put("header", header);
		root.put("body", body);
		
		header.put("action", "follow");
		header.put("token", "aaa");
		
		body.put("user_id", "1");
		body.put("flag", 1);
		return root.toString();
	}
	
	String buildMakeRelationParamForFollowService(String token, long uid2) {
		JSONObject root = new JSONObject();
		JSONObject header = new JSONObject();
		JSONObject body = new JSONObject();
		
		root.put("header", header);
		root.put("body", body);
		
		header.put("action", "follow");
		header.put("token", token);
		
		body.put("user_id", uid2);
		body.put("flag", 1);
		body.put("both", true);
		return root.toString();
	}
	
	
	String buildMakeRemoveRelationParamForFollowService(String token, long uid2) {
		JSONObject root = new JSONObject();
		JSONObject header = new JSONObject();
		JSONObject body = new JSONObject();
		
		root.put("header", header);
		root.put("body", body);
		
		header.put("action", "follow");
		header.put("token", token);
		
		body.put("user_id", uid2);
		body.put("flag", 2);
		body.put("both", true);
		return root.toString();
	}
	
	

	@Test
	public void testServiceHttpServletRequestHttpServletResponse() {
		service.service(request, response);
		String str = response.getFlusedBuffer();
		JSONObject respJson = parse(str);
		assertEquals(APICode.REQUEST_PARAMETER_INVALID, respJson.getInt("ret"));
		
		//check for invalid
		response.resetBuffer();
		request.setParam("data", buildInvalidData());
		service.service(request, response);
		str = response.getFlusedBuffer();
		respJson = parse(str);
		assertEquals(APICode.REQUEST_PARAMETER_NOT_STISFIED, respJson.getInt("ret"));
		
		
		//check for action not support
		response.resetBuffer();
		request.setParam("data", buildNoActionData());
		service.service(request, response);
		str = response.getFlusedBuffer();
		respJson = parse(str);
		assertEquals(APICode.ACTION_NOT_SUPPORT, respJson.getInt("ret"));
		
		
		
		//check for action login
		response.resetBuffer();
		request.setParam("data", buildNotEnoughLoginData());
		service.service(request, response);
		str = response.getFlusedBuffer();
		respJson = parse(str);
		assertEquals(APICode.REQUEST_PARAMETER_NOT_STISFIED, respJson.getInt("ret"));
		
		
		//check for action login with incorrect user
		response.resetBuffer();
		request.setParam("data", buildNormalLoginDataWithIncorrectUser());
		service.service(request, response);
		str = response.getFlusedBuffer();
		respJson = parse(str);
		assertEquals(APICode.LOGIN_ERROR_INCORRECT_USER_NAME_OR_PWD, respJson.getInt("ret"));
		
		
		//check register with not enough param
		response.resetBuffer();
		request.setParam("data", buildRegisterWithNotEnoughParam());
		service.service(request, response);
		str = response.getFlusedBuffer();
		respJson = parse(str);
		assertEquals(APICode.REQUEST_PARAMETER_NOT_STISFIED, respJson.getInt("ret"));
		
		
		//check register
		response.resetBuffer();
		request.setParam("data", buildNormalRegister("1234", "1234"));
		service.service(request, response);
		str = response.getFlusedBuffer();
		respJson = parse(str);
		assertEquals(APICode.SUCCESS, respJson.getInt("ret"));
		assertTrue(respJson.has("user"));
		assertTrue(respJson.has("token"));
		
		
		//check login
		response.resetBuffer();
		request.setParam("data", buildNormalLogin());
		service.service(request, response);
		str = response.getFlusedBuffer();
		respJson = parse(str);
		assertEquals(APICode.SUCCESS, respJson.getInt("ret"));
		assertTrue(respJson.has("user"));
		assertTrue(respJson.has("token"));
		assertEquals(respJson.getJSONObject("user").getString("cellphone"), "1234");
		String token =respJson.getString("token");
		
		
		//check upgrade with invalid token
		response.resetBuffer();
		request.setParam("data", buildUpgradeWithInvaidToken());
		service.service(request, response);
		str = response.getFlusedBuffer();
		respJson = parse(str);
		assertEquals(APICode.REQUEST_PARAMETER_NOT_STISFIED, respJson.getInt("ret"));
		
		
		//check upgrade
		response.resetBuffer();
		request.setParam("data", buildNormalUpgrade(token));
		service.service(request, response);
		str = response.getFlusedBuffer();
		respJson = parse(str);
		assertEquals(APICode.SUCCESS, respJson.getInt("ret"));
		assertEquals(respJson.getJSONObject("user").getInt("type"), 1);
		//re-get token
		token =respJson.getString("token");
		long uid = respJson.getJSONObject("user").getLong("id");
		
		//check upgrade again
		response.resetBuffer();
		request.setParam("data", buildNormalUpgrade(token));
		service.service(request, response);
		str = response.getFlusedBuffer();
		respJson = parse(str);
		assertEquals(APICode.USER_UPGRADE_ERROR_ALREADY, respJson.getInt("ret"));
		
		
		//check inquire without param
		response.resetBuffer();
		request.setParam("data", buildInquireWithoutEnoughParam(token));
		service.service(request, response);
		str = response.getFlusedBuffer();
		respJson = parse(str);
		assertEquals(APICode.REQUEST_PARAMETER_NOT_STISFIED, respJson.getInt("ret"));
		
		
		//check inquire with invalid token
		response.resetBuffer();
		request.setParam("data", buildInquireWithInvalidToken());
		service.service(request, response);
		str = response.getFlusedBuffer();
		respJson = parse(str);
		assertEquals(APICode.TOKEN_INVALID, respJson.getInt("ret"));
		
		
		//check inquire
		response.resetBuffer();
		request.setParam("data", buildNormalInquire(token));
		service.service(request, response);
		str = response.getFlusedBuffer();
		respJson = parse(str);
		assertEquals(APICode.SUCCESS, respJson.getInt("ret"));
		long qid = respJson.getLong("question_id");
		
		
		
		//check cancel inquire with no question id
		response.resetBuffer();
		request.setParam("data", buildNormalCancelInquire(token, -1));
		service.service(request, response);
		str = response.getFlusedBuffer();
		respJson = parse(str);
		assertEquals(APICode.INQUIRE_ERROR_SUCH_QUESTION, respJson.getInt("ret"));
		
		//check cancel inquire
		response.resetBuffer();
		request.setParam("data", buildNormalCancelInquire(token, qid));
		service.service(request, response);
		str = response.getFlusedBuffer();
		respJson = parse(str);
		assertEquals(APICode.SUCCESS, respJson.getInt("ret"));
		
		
		
		//prepare for finish question
		response.resetBuffer();
		request.setParam("data", buildNormalInquire(token));
		service.service(request, response);
		str = response.getFlusedBuffer();
		respJson = parse(str);
		assertEquals(APICode.SUCCESS, respJson.getInt("ret"));
		qid = respJson.getLong("question_id");
		
		//check cancel inquire
		response.resetBuffer();
		request.setParam("data", buildNormalFinishInquire(token, qid));
		service.service(request, response);
		str = response.getFlusedBuffer();
		respJson = parse(str);
		assertEquals(APICode.SUCCESS, respJson.getInt("ret"));
		
		
		
		//answer by customer without question id
		response.resetBuffer();
		request.setParam("data", buildNormalAnswer(token, -1));
		service.service(request, response);
		str = response.getFlusedBuffer();
		respJson = parse(str);
		assertEquals(APICode.ANSWER_ERROR_NO_SUCH_QUESTION, respJson.getInt("ret"));
		
		
		
		//prepare register for answer test case
		response.resetBuffer();
		request.setParam("data", buildNormalRegister("12345", "12345"));
		service.service(request, response);
		str = response.getFlusedBuffer();
		respJson = parse(str);
		assertEquals(APICode.SUCCESS, respJson.getInt("ret"));
		assertTrue(respJson.has("user"));
		assertTrue(respJson.has("token"));
		String cusToken = respJson.getString("token");
		
		//prepare for answer test case
		response.resetBuffer();
		request.setParam("data", buildNormalInquire(token));
		service.service(request, response);
		str = response.getFlusedBuffer();
		respJson = parse(str);
		assertEquals(APICode.SUCCESS, respJson.getInt("ret"));
		qid = respJson.getLong("question_id");
		
		//answer by customer
		response.resetBuffer();
		request.setParam("data", buildNormalAnswer(cusToken, qid));
		service.service(request, response);
		str = response.getFlusedBuffer();
		respJson = parse(str);
		assertEquals(APICode.ANSWER_ERROR_NOT_SERVICER, respJson.getInt("ret"));
		
		
		
		//feedback
		response.resetBuffer();
		request.setParam("data", buildNormalFeedback(cusToken));
		service.service(request, response);
		str = response.getFlusedBuffer();
		respJson = parse(str);
		assertEquals(APICode.SUCCESS, respJson.getInt("ret"));
		
		
		//normal servicer cmp query
		response.resetBuffer();
		request.setParam("data", buildNormalServicerCmpQuery(token, uid));
		service.service(request, response);
		str = response.getFlusedBuffer();
		respJson = parse(str);
		assertEquals(APICode.SUCCESS, respJson.getInt("ret"));

	}
	
	
	
	
	JSONObject parse(String str) {
		JSONTokener jsonParser = new JSONTokener(str);
		JSONObject resp = (JSONObject) jsonParser.nextValue();
		return resp;
	}
	
	
	String buildInvalidData() {
		JSONObject root = new JSONObject();
		JSONObject header = new JSONObject();
		JSONObject body = new JSONObject();
		
		root.put("header", header);
		root.put("body", body);
		
		return root.toString();
	}
	
	String buildNoActionData() {
		JSONObject root = new JSONObject();
		JSONObject header = new JSONObject();
		JSONObject body = new JSONObject();
		
		root.put("header", header);
		root.put("body", body);
		
		header.put("action", "aaa");
		return root.toString();
	}
	
	
	String buildNotEnoughLoginData() {
		JSONObject root = new JSONObject();
		JSONObject header = new JSONObject();
		JSONObject body = new JSONObject();
		
		root.put("header", header);
		root.put("body", body);
		
		header.put("action", "login");
		return root.toString();
	}

	
	
	String buildNormalLoginDataWithIncorrectUser() {
		JSONObject root = new JSONObject();
		JSONObject header = new JSONObject();
		JSONObject body = new JSONObject();
		
		root.put("header", header);
		root.put("body", body);
		
		header.put("action", "login");
		body.put("username", "1234");
		body.put("pwd", "1234");
		return root.toString();
	}
	
	
	String buildRegisterWithNotEnoughParam() {
		JSONObject root = new JSONObject();
		JSONObject header = new JSONObject();
		JSONObject body = new JSONObject();
		
		root.put("header", header);
		root.put("body", body);
		
		header.put("action", "register");
		body.put("username", "1234");
		body.put("pwd", "1234");
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
		body.put("country_code", 86);
		return root.toString();
	}
	
	
	String buildNormalLogin() {
		JSONObject root = new JSONObject();
		JSONObject header = new JSONObject();
		JSONObject body = new JSONObject();
		
		root.put("header", header);
		root.put("body", body);
		
		header.put("action", "login");
		body.put("username", "1234");
		body.put("pwd", "1234");
		return root.toString();
	}
	
	
	
	String buildUpgradeWithInvaidToken() {
		JSONObject root = new JSONObject();
		JSONObject header = new JSONObject();
		JSONObject body = new JSONObject();
		
		root.put("header", header);
		root.put("body", body);
		
		header.put("action", "upgrade");
		header.put("token", "1234");
		return root.toString();
	}
	
	
	String buildNormalUpgrade(String token) {
		JSONObject root = new JSONObject();
		JSONObject header = new JSONObject();
		JSONObject body = new JSONObject();
		
		root.put("header", header);
		root.put("body", body);
		
		header.put("action", "upgrade");
		header.put("token", token);
		body.put("area", "area");
		body.put("location", "locaiton");
		body.put("desc", "desc");
		
		return root.toString();
	}
	
	
	
	String buildInquireWithoutEnoughParam(String token) {
		JSONObject root = new JSONObject();
		JSONObject header = new JSONObject();
		JSONObject body = new JSONObject();
		
		root.put("header", header);
		root.put("body", body);
		
		header.put("action", "question");
		header.put("token", token);
		return root.toString();
	}
	
	
	String buildInquireWithInvalidToken() {
		JSONObject root = new JSONObject();
		JSONObject header = new JSONObject();
		JSONObject body = new JSONObject();
		
		root.put("header", header);
		root.put("body", body);
		
		header.put("action", "question");
		header.put("token", "aaaa");
		body.put("opt", 1);
		body.put("question", "who am i");
		return root.toString();
	}
	
	
	String buildNormalInquire(String token) {
		JSONObject root = new JSONObject();
		JSONObject header = new JSONObject();
		JSONObject body = new JSONObject();
		
		root.put("header", header);
		root.put("body", body);
		
		header.put("action", "question");
		header.put("token", token);
		body.put("opt", 1);
		body.put("question", "who am i");
		return root.toString();
	}
	
	
	
	String buildNormalCancelInquire(String token, long id) {
		JSONObject root = new JSONObject();
		JSONObject header = new JSONObject();
		JSONObject body = new JSONObject();
		
		root.put("header", header);
		root.put("body", body);
		
		header.put("action", "question");
		header.put("token", token);
		body.put("opt", 2);
		body.put("question_id", id);
		return root.toString();
	}
	
	
	String buildNormalFinishInquire(String token, long id) {
		JSONObject root = new JSONObject();
		JSONObject header = new JSONObject();
		JSONObject body = new JSONObject();
		
		root.put("header", header);
		root.put("body", body);
		
		header.put("action", "question");
		header.put("token", token);
		body.put("opt", 3);
		body.put("question_id", id);
		return root.toString();
	}
	
	
	
	
	String buildNormalAnswer(String token, long id) {
		JSONObject root = new JSONObject();
		JSONObject header = new JSONObject();
		JSONObject body = new JSONObject();
		
		root.put("header", header);
		root.put("body", body);
		
		header.put("action", "answer");
		header.put("token", token);
		body.put("question_id", id);
		body.put("answer", "this is answer");
		
		return root.toString();
	}
	
	
	String buildNormalFeedback(String token) {
		JSONObject root = new JSONObject();
		JSONObject header = new JSONObject();
		JSONObject body = new JSONObject();
		
		root.put("header", header);
		root.put("body", body);
		
		header.put("action", "feedback");
		header.put("token", token);
		body.put("comment", "aaaa");
		
		return root.toString();
	}
	
	
	String buildNormalQueryRelationWith3(String token, long uid2) {
		JSONObject root = new JSONObject();
		JSONObject header = new JSONObject();
		JSONObject body = new JSONObject();
		
		root.put("header", header);
		root.put("body", body);
		
		header.put("action", "relation");
		header.put("token", token);
		body.put("userid2", uid2);
		body.put("type", 3);
		
		return root.toString();
	}
	
	
	
	String buildNormalServicerCmpQuery(String token, long uid2) {
		JSONObject root = new JSONObject();
		JSONObject header = new JSONObject();
		JSONObject body = new JSONObject();
		
		root.put("header", header);
		root.put("body", body);
		
		header.put("action", "skservicer-cmp-query");
		header.put("token", token);
		body.put("uid", uid2);
		body.put("opt", 1);
		
		return root.toString();
	}
	
}

