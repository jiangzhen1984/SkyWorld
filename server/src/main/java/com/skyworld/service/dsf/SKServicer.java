package com.skyworld.service.dsf;

import com.skyworld.service.po.SWPUser;



public class SKServicer extends User {

	
	private String location;
	
	private String area;
	
	private String serviceDesc;
	
	
	///////company
	private String cmpName;
	
	private String website;
	
	private String cmpDesc;
	
	private String logoPath;
	
	

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

	public String getCmpName() {
		return cmpName;
	}

	public void setCmpName(String cmpName) {
		this.cmpName = cmpName;
	}

	public String getWebsite() {
		return website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}

	public String getCmpDesc() {
		return cmpDesc;
	}

	public void setCmpDesc(String cmpDesc) {
		this.cmpDesc = cmpDesc;
	}

	public String getLogoPath() {
		return logoPath;
	}
	
	public String getLogoURL() {
		return logoPath;
	}

	public void setLogoPath(String logoPath) {
		this.logoPath = logoPath;
	}
	
	
	
	

}
