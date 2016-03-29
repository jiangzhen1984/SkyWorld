package com.skyworld.service.po;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;


@Entity
@Table( name = "SW_SERVICER_COMPANY_DESC" )
public class SWPServicerCompanyDesc {
	
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	protected long id;
	
	@Column(name="CMP_NAME", columnDefinition="VARCHAR(200)")
	private String cmpName;
	
	@Column(name="WEBSITE", columnDefinition="VARCHAR(1000)")
	private String website;
	
	@Column(name="CMP_DESC", columnDefinition="VARCHAR(2000)")
	private String cmpDesc;
	
	@Column(name="CMP_LOGO", columnDefinition="VARCHAR(2000)")
	private String logoPath;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "SER_ID", insertable = true, updatable = true, nullable = false)
	private SWPUser servicer;
	
	
	
	public SWPServicerCompanyDesc() {
		
	}
	
	public SWPServicerCompanyDesc(SWPServicerCompanyDesc u) {
		this.setId(u.getId());
		this.setCmpDesc(u.cmpDesc);
		this.setLogoPath(u.logoPath);
		this.setWebsite(u.website);
		this.setCmpName(u.cmpName);
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
	
	
	

	

	public SWPUser getServicer() {
		return servicer;
	}

	public void setServicer(SWPUser servicer) {
		this.servicer = servicer;
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

	public void setLogoPath(String logoPath) {
		this.logoPath = logoPath;
	}
	
	

}
