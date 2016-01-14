package com.skyworld.service.dsf;



public class SKServicer extends User {


	public SKServicer() {
		super();
		super.setUserType(UserType.SERVICER);
	}

	public SKServicer(User u) {
		super(u);
		super.setUserType(UserType.SERVICER);
	}
	

}
