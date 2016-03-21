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
@Table( name = "SW_SERVICER_DESC" )
public class SWPServicerDesc {
	
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	protected long id;
	
	@Column(name="LOCATION", columnDefinition="VARCHAR(100)")
	private String location;
	
	@Column(name="AREA", columnDefinition="VARCHAR(100)")
	private String area;
	
	@Column(name="SER_DESC", columnDefinition="VARCHAR(2000)")
	private String desc;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "SER_ID", insertable = true, updatable = true, nullable = false)
	private SWPUser servicer;
	
	
	
	public SWPServicerDesc() {
		
	}
	
	public SWPServicerDesc(SWPServicerDesc u) {
		this.setId(u.getId());
		this.area = u.getArea();
		this.location = u.getLocation();
		this.desc = u.getDesc();
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

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	

}
