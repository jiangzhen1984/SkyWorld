package com.skyworld.service;

import com.skyworld.easemob.EaseMobDeamon;
import com.skyworld.easemob.EaseMobService;
import com.skyworld.service.article.APIArticleCommentService;
import com.skyworld.service.article.APIArticlePushlihService;
import com.skyworld.service.article.APIArticleQuery;
import com.skyworld.service.article.APIArticleRecommendationService;
import com.skyworld.service.article.APIFollowService;
import com.skyworld.service.question.APIAnswerService;
import com.skyworld.service.question.APIInquireService;
import com.skyworld.service.skservicer.APICmpQueryService;
import com.skyworld.service.skservicer.APIServicerCompanyUpdateService;
import com.skyworld.service.skservicer.APIUpgradeService;
import com.skyworld.service.system.APIFeedbackService;
import com.skyworld.service.user.APILoginService;
import com.skyworld.service.user.APILogoutService;
import com.skyworld.service.user.APIQueryService;
import com.skyworld.service.user.APIRegisterService;
import com.skyworld.service.user.APIUpdateAvatarService;
import com.skyworld.service.user.APIUserRelationQueryService;

public class ServiceFactory {
	
	
	public static final int API_CODE_FIRST = 1;
	public static final int API_CODE_USER = API_CODE_FIRST;
	public static final int API_CODE_USER_AVATAR = 2;
	public static final int API_CODE_USER_QUESTION = 3;
	public static final int API_CODE_ARTICLE = 4;
	public static final int API_CODE_SERVICER = 5;
	public static final int API_MAX = API_CODE_SERVICER + 1;
	
	private static SWUserService eUserService;
	
	private static SWQuestionService eQuestionService;
	
	private static SystemBasicService eSystemBasicService;
	
	private static SWArticleService eArticleService;
	
	private static EaseMobDeamon  mEaseMobDeamon;
	
	private static SWSKServicerService eSKServicerService;
	
	private static APIService[] mApiService;

	public ServiceFactory() {
	}
	
	
	public static EaseMobService getEaseMobService() {
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
	
	
	
	public static SWArticleService getEArticleService() {
		if (eArticleService == null) {
			eArticleService = new SWArticleService();
		}
		
		return eArticleService;
	}
	
	
	public static SWQuestionService getQuestionService() {
		if (eQuestionService == null) {
			eQuestionService = new SWQuestionService();
			eQuestionService.setUserService(getESUserService());
		}
		
		return eQuestionService;
	}
	
	
	public static SystemBasicService getSystemBasicService() {
		if (eSystemBasicService == null) {
			eSystemBasicService = new SystemBasicService();
		}
		
		return eSystemBasicService;
	}
	
	
	public static SWSKServicerService getESKServicerService() {
		if (eSKServicerService == null) {
			eSKServicerService = new SWSKServicerService();
		}
		
		return eSKServicerService;
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
		((APIChainService)mApiService[API_CODE_USER]).addActionMapping("follow", new APIFollowService());
		((APIChainService)mApiService[API_CODE_USER]).addActionMapping("relation", new APIUserRelationQueryService());
		((APIChainService)mApiService[API_CODE_USER]).addActionMapping("skservicer-cmp-query", new APICmpQueryService());
		
		mApiService[API_CODE_USER_QUESTION] = mApiService[API_CODE_USER];
		mApiService[API_CODE_USER_AVATAR] =  new APIUpdateAvatarService();
		
		mApiService[API_CODE_ARTICLE] =  new APIJsonPartDispatchService();
		((APIJsonPartDispatchService)mApiService[API_CODE_ARTICLE]).addActionMapping("article-publish", new APIArticlePushlihService());
		((APIJsonPartDispatchService)mApiService[API_CODE_ARTICLE]).addActionMapping("article-recommend", new APIArticleRecommendationService());
		((APIJsonPartDispatchService)mApiService[API_CODE_ARTICLE]).addActionMapping("article-comment", new APIArticleCommentService()); 
		((APIJsonPartDispatchService)mApiService[API_CODE_ARTICLE]).addActionMapping("article-query", new APIArticleQuery());
		
		mApiService[API_CODE_SERVICER] =  new APIJsonPartDispatchService();
		((APIJsonPartDispatchService)mApiService[API_CODE_SERVICER]).addActionMapping("servicer-company-update", new APIServicerCompanyUpdateService());
	}
	
	
	
	
	

}
