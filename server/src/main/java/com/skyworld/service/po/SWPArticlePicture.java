package com.skyworld.service.po;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="SW_ARTICLE_MEDIA")
public class SWPArticlePicture {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	protected long id;
	
	@Column(name="ARTICLE_ID", columnDefinition="BIGINT")
	protected long articleId;
	
	@Column(name="ORIGIN_PATH", columnDefinition="VARCHAR(300)")
	protected String originPath;
	
	@Column(name="ORIGIN_PIC_1", columnDefinition="VARCHAR(300)")
	protected String pic1;
	
	@Column(name="ORIGIN_PIC_2", columnDefinition="VARCHAR(300)")
	protected String pic2;
	
	@Column(name="M_1", columnDefinition="VARCHAR(300)")
	protected String media1;
	
	@Column(name="M_2", columnDefinition="VARCHAR(300)")
	protected String media2;
	
	@Column(name="M_3", columnDefinition="VARCHAR(300)")
	protected String media3;
	

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getArticleId() {
		return articleId;
	}

	public void setArticleId(long articleId) {
		this.articleId = articleId;
	}

	public String getOriginPath() {
		return originPath;
	}

	public void setOriginPath(String originPath) {
		this.originPath = originPath;
	}

	public String getPic1() {
		return pic1;
	}

	public void setPic1(String pic1) {
		this.pic1 = pic1;
	}

	public String getPic2() {
		return pic2;
	}

	public void setPic2(String pic2) {
		this.pic2 = pic2;
	}

	public String getMedia1() {
		return media1;
	}

	public void setMedia1(String media1) {
		this.media1 = media1;
	}

	public String getMedia2() {
		return media2;
	}

	public void setMedia2(String media2) {
		this.media2 = media2;
	}

	public String getMedia3() {
		return media3;
	}

	public void setMedia3(String media3) {
		this.media3 = media3;
	}

	
	
	
	
}

