package com.skyworld.service.po;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="SW_APP_VERSION")
public class SWAppVersion {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	protected long id;
	
	@Column(name="AV_TYPE", columnDefinition="SMALLINT")
	protected int type;
	
	@Column(name="AV_NUMBER", columnDefinition="NUMERIC(10)")
	protected int number;
	
	@Column(name="AV_FORCE", columnDefinition="TINyint(1)")
	protected boolean force;
	
	@Column(name="AR_LOCATION", columnDefinition="VARCHAR(200)")
	protected String filename;
	
	@Column(name="AV_APP_TYPE", columnDefinition="SMALLINT")
	protected int appType;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public boolean isForce() {
		return force;
	}

	public void setForce(boolean force) {
		this.force = force;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public int getAppType() {
		return appType;
	}

	public void setAppType(int appType) {
		this.appType = appType;
	}
	
	
	
}
