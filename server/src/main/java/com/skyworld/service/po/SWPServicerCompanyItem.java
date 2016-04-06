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
@Table( name = "SW_SERVICER_COMPANY_ITEM" )
public class SWPServicerCompanyItem {
	
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	protected long id;
	
	@Column(name="CMP_ITEM_TITLE", columnDefinition="VARCHAR(200)")
	private String title;
	
	@Column(name="CMP_ITEM_PIC", columnDefinition="VARCHAR(1000)")
	private String pic;
	
	@Column(name="CMP_ITEM_CONTENT", columnDefinition="TEXT")
	private String content;
	
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "SER_ID", insertable = true, updatable = true, nullable = false)
	private SWPUser servicer;
	
	
	
	public SWPServicerCompanyItem() {
		
	}
	
	public SWPServicerCompanyItem(SWPServicerCompanyItem u) {
		this.setId(u.getId());
		this.setTitle(u.getTitle());
		this.setPic(u.getPic());
		this.setContent(u.getContent());
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

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getPic() {
		return pic;
	}

	public void setPic(String pic) {
		this.pic = pic;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	
	
	

}
