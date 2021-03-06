package com.android.samservice;

public class SamCoreObj{
	static final int  STATUS_INIT=0;
	static final int  STATUS_DONE=1;
	static final int  STATUS_TIMEOUT=2;

	public CBObj refCBObj;
	public int request_status;
	public int retry_count=0;

	public boolean isSignin(){
		return (this instanceof SignInCoreObj);
	}
	
	public boolean isSignup(){
		return (this instanceof SignUpCoreObj);
	}

	public boolean isSendq(){
		return (this instanceof SendqCoreObj);
	}

	public boolean isCancelq(){
		return (this instanceof CancelqCoreObj);
	}

	public boolean isUpgrade(){
		return (this instanceof UpgradeCoreObj);
	}

	public boolean isSenda(){
		return (this instanceof SendaCoreObj);
	}

	public boolean isQueryui(){
		return (this instanceof QueryuiCoreObj);
	}

	public boolean isUploadAvatar(){
		return (this instanceof UploadAvatarCoreObj);
	}

	public boolean isSendComments(){
		return (this instanceof SendCommentsCoreObj);
	}

	public boolean isUploadFG(){
		return (this instanceof UploadFGCoreObj);
	}

	public boolean isQueryFG(){
		return (this instanceof QueryFGCoreObj);
	}

	public boolean isCommentFG(){
		return (this instanceof CommentFGCoreObj);
	}

	public boolean isFollow(){
		return (this instanceof FollowCoreObj);
	}

	public boolean isQueryFollower(){
		return (this instanceof QueryFollowerCoreObj);
	}

	public boolean isQueryPublicInfo(){
		return (this instanceof QueryPublicInfoCoreObj);
	}

	public boolean isQueryHotTopic(){
		return (this instanceof QueryHotTopicCoreObj);
	}
}






