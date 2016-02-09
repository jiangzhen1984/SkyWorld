package com.skyworld.service.po;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="SW_ARTICLE_PICTURE")
public class SWPArticlePicture {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	protected long id;
	
	@Column(name="ARTICLE_ID", columnDefinition="NUMERIC(20)")
	protected long articleId;
	
	@Column(name="ORIGIN_PATH", columnDefinition="VARCHAR(300)")
	protected String originPath;
	
	@Column(name="ORIGIN_PIC_1", columnDefinition="VARCHAR(300)")
	protected String pic1;
	
	@Column(name="ORIGIN_PIC_2", columnDefinition="VARCHAR(300)")
	protected String pic2;
	

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

	
	
}

