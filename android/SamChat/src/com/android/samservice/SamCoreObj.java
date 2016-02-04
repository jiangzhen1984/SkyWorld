package com.android.samservice;

public class SamCoreObj{
	static final int  STATUS_INIT=0;
	static final int  STATUS_DONE=1;
	static final int  STATUS_TIMEOUT=2;

	public CBObj refCBObj;
	public int request_status;

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
}






