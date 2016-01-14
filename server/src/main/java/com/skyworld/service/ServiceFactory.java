package com.skyworld.service;

import com.skyworld.easemob.EaseMobDeamon;

public class ServiceFactory {
	
	private static SWUserService eUserService;
	
	private static SWQuestionService eQuestionService;
	
	private static EaseMobDeamon  mEaseMobDeamon;
	
	private static APIService mApiService;

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
	
	
	
	public static APIService getAPIService() {
		if (mApiService == null) {
			mApiService = new APIChainService();
			((APIChainService)mApiService).addActionMapping("login", new APILoginService());
			((APIChainService)mApiService).addActionMapping("register", new APIRegisterService());
			((APIChainService)mApiService).addActionMapping("upgrade", new APIUpgradeService());
			((APIChainService)mApiService).addActionMapping("question", new APIInquireService());
			((APIChainService)mApiService).addActionMapping("answer", new APIAnswerService());
			((APIChainService)mApiService).addActionMapping("logout", new APILogoutService());
		}
		
		return mApiService;
	}
	
	
	
	

}
