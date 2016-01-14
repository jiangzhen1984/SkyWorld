package com.skyworld.service.resp;

public abstract class BasicResponse {

	protected int retCode;
	
	
	
	
	public abstract String getResponse();




	public int getRetCode() {
		return retCode;
	}




	public void setRetCode(int retCode) {
		this.retCode = retCode;
	}
	
	
	
	
}
