package com.skyworld.service.dsf;

import com.skyworld.service.po.SWPUser;



public class SKServicer extends User {

	
	private String location;
	
	private String area;
	
	private String serviceDesc;
	

	public SKServicer() {
		super();
		super.setUserType(UserType.SERVICER);
	}

	public SKServicer(User u) {
		super(u);
		super.setUserType(UserType.SERVICER);
	}
	
	

	public SKServicer(SWPUser u) {
		super(u);
		super.setUserType(UserType.SERVICER);
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public String getServiceDesc() {
		return serviceDesc;
	}

	public void setServiceDesc(String serviceDesc) {
		this.serviceDesc = serviceDesc;
	}
	
	
	
	

}
