package com.skyworld.service.po;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;


@Entity
@Table( name = "SW_USER" )
public class SWPUser {
	
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	protected long id;
	
	@Column(name="CELL_PHONE", columnDefinition="VARCHAR(40)")
	private String cellPhone;
	
	@Column(name="USER_PWD", columnDefinition="VARCHAR(40)")
	private String password;
	
	@Column(name="NAME", columnDefinition="VARCHAR(40)")
	private String name;
	
	@Column(name="ADDRESS", columnDefinition="VARCHAR(200)")
	private String address;
	
	@Column(name="MAIL", columnDefinition="VARCHAR(100)")
	private String mail;
	
	@Column(name="U_TYPE", columnDefinition="NUMERIC(1)")
	private int uType;

	@Column(name="avatar_id", columnDefinition="NUMERIC(20)")
	private long avatarId;
	
	
	@Column(name="LAST_UPDATE", columnDefinition="NUMERIC(30)")
	private long lastUpdate;
	
	@Transient
	private SWPUserAvatar avatar;
	
	
	public SWPUser() {
		
	}
	
	public SWPUser(SWPUser u) {
		this.setAddress(u.getAddress());
		this.setCellPhone(u.getCellPhone());
		this.setName(u.getName());
		this.setId(u.getId());
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getCellPhone() {
		return cellPhone;
	}

	public void setCellPhone(String cellPhone) {
		this.cellPhone = cellPhone;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getMail() {
		return mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}

	public int getuType() {
		return uType;
	}

	public void setuType(int uType) {
		this.uType = uType;
	}

	public SWPUserAvatar getAvatar() {
		return avatar;
	}

	public void setAvatar(SWPUserAvatar avatar) {
		this.avatar = avatar;
		if (this.avatar != null) {
			this.avatarId = this.avatar.getId();
		}
	}

	public long getAvatarId() {
		return avatarId;
	}

	public void setAvatarId(long avatarId) {
		this.avatarId = avatarId;
	}

	public long getLastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate(long lastUpdate) {
		this.lastUpdate = lastUpdate;
	}
	
	
	

}
