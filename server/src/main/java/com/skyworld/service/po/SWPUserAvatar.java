package com.skyworld.service.po;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "SW_USER_AVATAR")
public class SWPUserAvatar {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	@Transient
	private SWPUser user;

	@Column(name = "origin_path", columnDefinition = "VARCHAR(500)")
	private String originPath;
	
	
	
	public SWPUserAvatar(){
		
	}
	
	
	public SWPUserAvatar(SWPUserAvatar av) {
		this.setOriginPath(av.getOriginPath());
		this.setId(av.getId());
		this.setUser(av.getUser());
	}

	public String getOriginPath() {
		return originPath;
	}

	public void setOriginPath(String originPath) {
		this.originPath = originPath;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public SWPUser getUser() {
		return user;
	}

	public void setUser(SWPUser user) {
		this.user = user;
	}
	
	

}
