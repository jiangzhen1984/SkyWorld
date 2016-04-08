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
	
	@Column(name = "scale_1_path", columnDefinition = "VARCHAR(500)")
	private String scale1Path;
	
	@Column(name = "scale_2_path", columnDefinition = "VARCHAR(500)")
	private String scale2Path;
	
	
	
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


	public String getScale1Path() {
		return scale1Path;
	}


	public void setScale1Path(String scale1Path) {
		this.scale1Path = scale1Path;
	}


	public String getScale2Path() {
		return scale2Path;
	}


	public void setScale2Path(String scale2Path) {
		this.scale2Path = scale2Path;
	}
	
	

}
