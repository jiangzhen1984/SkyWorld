package com.skyworld.service;

import com.skyworld.easemob.EaseMobDeamon;

public class ServiceFactory {
	
	
	public static final int API_CODE_FIRST = 1;
	public static final int API_CODE_USER = API_CODE_FIRST;
	public static final int API_CODE_USER_AVATAR = 2;
	public static final int API_CODE_USER_QUESTION = 3;
	public static final int API_MAX = API_CODE_USER_QUESTION + 1;
	
	private static SWUserService eUserService;
	
	private static SWQuestionService eQuestionService;
	
	private static SystemBasicService eSystemBasicService;
	
	private static EaseMobDeamon  mEaseMobDeamon;
	
	private static APIService[] mApiService;

	public ServiceFactory() {
	}
	
	
	public static EaseMobDeamon getEaseMobService() {
		if (mEaseMobDeamon == null) {
			mEaseMobDeamon = new EaseMobDeamon();
		}
		
		return mEaseMobDeamon;
	}
	
	public static SWUserService getESUserService() {
		if (eUserService == null) {
			eUserService = new SWUserService();
		}
		
		return eUserService;
	}
	
	
	
	public static SWQuestionService getQuestionService() {
		if (eQuestionService == null) {
			eQuestionService = new SWQuestionService();
		}
		
		return eQuestionService;
	}
	
	
	public static SystemBasicService getSystemBasicService() {
		if (eSystemBasicService == null) {
			eSystemBasicService = new SystemBasicService();
		}
		
		return eSystemBasicService;
	}
	
	
	
	public static APIService getAPIService() {
		if (mApiService == null) {
			initService();
		}
		
		return mApiService[API_CODE_USER];
	}
	
	
	
	public  static APIService getAPIService(int code) {
		if (code < API_CODE_USER  || code >= API_MAX) {
			throw new IndexOutOfBoundsException("code is incorrect ");
		}
		
		if (mApiService == null) {
			initService();
		}
		return mApiService[code];
	}
	
	
	private static void initService() {
		mApiService = new APIService[API_MAX];
		mApiService[API_CODE_USER] = new APIChainService();
		((APIChainService)mApiService[API_CODE_USER]).addActionMapping("login", new APILoginService());
		((APIChainService)mApiService[API_CODE_USER]).addActionMapping("register", new APIRegisterService());
		((APIChainService)mApiService[API_CODE_USER]).addActionMapping("upgrade", new APIUpgradeService());
		((APIChainService)mApiService[API_CODE_USER]).addActionMapping("question", new APIInquireService());
		((APIChainService)mApiService[API_CODE_USER]).addActionMapping("answer", new APIAnswerService());
		((APIChainService)mApiService[API_CODE_USER]).addActionMapping("logout", new APILogoutService());
		((APIChainService)mApiService[API_CODE_USER]).addActionMapping("query", new APIQueryService());
		((APIChainService)mApiService[API_CODE_USER]).addActionMapping("feedback", new APIFeedbackService());
		
		
		mApiService[API_CODE_USER_QUESTION] = mApiService[API_CODE_USER];
		mApiService[API_CODE_USER_AVATAR] =  new APIUpdateAvatarService();
	}
	
	
	

}
