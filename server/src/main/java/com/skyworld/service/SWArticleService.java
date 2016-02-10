package com.skyworld.service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.skyworld.service.dsf.Article;
import com.skyworld.service.dsf.User;
import com.skyworld.service.po.SWPArticle;
import com.skyworld.service.po.SWPArticleComment;
import com.skyworld.service.po.SWPArticlePicture;
import com.skyworld.service.po.SWPArticleRecommendation;

public class SWArticleService extends BaseService {
	
	
	public void addArticle(Article article) {
		if (article == null) {
			throw new NullPointerException(" article is null");
		}
		if (article.getPublisher() == null) {
			throw new NullPointerException(" article is null");
		}
		
		Session session = openSession();
		Transaction t = session.beginTransaction();
		SWPArticle swpa = new SWPArticle();
		swpa.setComment(article.getComment());
		swpa.setCommentsCount(article.getCommentCount());
		swpa.setRecommendsCount(article.getRecommendationCount());
		swpa.setLat(article.getLat());
		swpa.setLng(article.getLng());
		swpa.setLocation(article.getLocation());
		swpa.setTimeStamp(article.getTime().getTime());
		swpa.setUserId(article.getPublisher().getId());
		session.save(swpa);
		session.flush();
		
		
		int picCount = article.getPicsCount();
		for(int i = 0; i < picCount; i ++) {
			SWPArticlePicture pic = new SWPArticlePicture();
			pic.setArticleId(swpa.getId());
			pic.setOriginPath(article.getPicPath(i));
			session.save(pic);
		}
		
		//do not need to add comment or recommendation at first time
		t.commit();
		session.close();
		article.setId(swpa.getId());
	}

	
	
	
	public SWPArticleComment addComment(Article article, User user, User toUser, String comment) {
		if (article == null || user == null) {
			throw new NullPointerException("article or user is null ");
		}
		return addComment(article.getId(), user.getId(), toUser == null ? 0 : toUser.getId(), comment);
	}
	
	
	public SWPArticleComment addComment(long arId, long userId, long toUserId, String comment) {
		Session session = openSession();
		Transaction t = session.beginTransaction();
		SWPArticleComment co = new SWPArticleComment();
		co.setArticleId(arId);
		co.setComment(comment);
		co.setTimeStamp(new Date(System.currentTimeMillis()));
		co.setUserId(userId);
		co.setToUserId(toUserId);
		session.save(co);
		
		SQLQuery query = session.createSQLQuery(" update SW_ARTICLE set AR_COMMENTS_COUNT = AR_COMMENTS_COUNT + 1 where id = ? ");
		query.setLong(0, arId);
		query.executeUpdate();
		
		t.commit();
		session.close();
		return co;
	}
	
	
	
	public SWPArticleRecommendation addRecommendation(Article article, User user) {
		if (article == null || user == null) {
			throw new NullPointerException("article or user is null ");
		}
		return addRecommendation(article.getId(), user.getId());
	}
	
	
	public SWPArticleRecommendation addRecommendation(long arId, long userId) {
		Session session = openSession();
		Transaction t = session.beginTransaction();
		SWPArticleRecommendation co = new SWPArticleRecommendation();
		co.setArticleId(arId);
		co.setTimeStamp(new Date(System.currentTimeMillis()));
		co.setUserId(userId);
		session.save(co);
		
		SQLQuery query = session.createSQLQuery(" update SW_ARTICLE set AR_RECOMMENDS_COUNT = AR_RECOMMENDS_COUNT + 1 where id = ? ");
		query.setLong(0, arId);
		query.executeUpdate();
		
		t.commit();
		session.close();
		return co;
	}
	
	
	public void cancelRecommedation(Article article, User user) {
		if (article == null || user == null) {
			throw new NullPointerException("article or user is null ");
		}
		cancelRecommedation(article.getId(), user.getId());
	}
	
	
	public void cancelRecommedation(long arId, long userId) {
		Session session = openSession();
		Transaction t = session.beginTransaction();
		SQLQuery query = session.createSQLQuery(" delete  from SW_ARTICLE_RECOMMENDATION where ARTICLE_ID = ? and USER_ID =? ");
		query.setLong(0, arId);
		query.setLong(1, userId);
		query.executeUpdate();
		
		query = session.createSQLQuery(" update SW_ARTICLE set AR_RECOMMENDS_COUNT = AR_RECOMMENDS_COUNT - 1 where id = ? ");
		query.setLong(0, arId);
		query.executeUpdate();
		
		t.commit();
		session.close();
	}
	
	
	public List<Article> queryArticle(List<Long> userIds) {
		Calendar c = Calendar.getInstance();
		java.util.Date start = c.getTime();
		
		c.set(Calendar.HOUR_OF_DAY, c.get(Calendar.HOUR_OF_DAY) - 2 );
		java.util.Date end = c.getTime();
		return queryArticle(userIds, start, end, 15);
	}
	
	
	
	public List<Article> queryArticle(List<Long> userIds, java.util.Date start, java.util.Date end, int pageSize) {
		if (userIds == null || userIds.size() <= 0 ) {
			return null;
		}
		int num = userIds.size();
		StringBuffer queryBuffer = new StringBuffer();
		queryBuffer.append(" select a.id as aid, a.AR_COMMENT, a.USER_ID , ap.id as apid, ap.ORIGIN_PATH as opp ");
		queryBuffer.append(" from SW_Article a join SW_ARTICLE_PICTURE ap ");
		queryBuffer.append("  on ap.ARTICLE_ID = a.id ");
		
		queryBuffer.append(" WHERE  ");
		queryBuffer.append(" ( a.USER_ID = ? ");
		for (int i = 1; i < num; i++) {
			queryBuffer.append(" OR a.USER_ID = ? ");
		}
		queryBuffer.append(" ) ");
		queryBuffer.append(" and (a.AR_TIME_STAMP <= ? and a.AR_TIME_STAMP >= ?) ");
		queryBuffer.append(" order by a.AR_TIME_STAMP desc ");
		
		Session session = openSession();
		SQLQuery query = session.createSQLQuery(queryBuffer.toString());

		for (int i = 0; i < num; i++) {
			query.setLong(i, userIds.get(i));
		}
		
		query.setLong(num, start.getTime());
		query.setLong(num + 1, end.getTime());
		
		
		List<Object[]> cache = (List<Object[]>)query.list();
		List<Article> list = new ArrayList<Article>(cache.size());
		
		Iterator<Object[]> it = cache.iterator();
		Article last = null;
		while (it.hasNext()) {
			Object[] obj = it.next();
			Article ar = null;
			if (last == null || last.getId() != ((BigInteger)obj[0]).longValue()) {
				ar = new Article();
				ar.setId(((BigInteger)obj[0]).longValue());
				ar.setComment((String)obj[1]);
				ar.setPublisher(ServiceFactory.getESUserService()
						.getUser(((BigDecimal)obj[2]).longValue()));
				
				queryArticleData(session, ar, 0);
			} else {
				ar = last;
			}
			
			ar.addPic(((BigInteger)obj[3]).longValue(), (String)obj[4]);
			
			if (ar != last) {
				list.add(ar);
			}
			last = ar;
		}
		
		session.close();
		
		return list;
	}
	
	
	
	public void queryArticleRelatedData(Article article, int type) {
		Session session = openSession();
		queryArticleData(session, article, type);
		session.close();
	}
	
	
	private void queryArticleData(Session session, Article article, int type) {
		Query query = session.createQuery(" from SWPArticleComment where articleId =?" );
	    query.setLong(0, article.getId());
		List<SWPArticleComment> list = query.list();
		Iterator<SWPArticleComment> it = list.iterator();
		while (it.hasNext()) {
			SWPArticleComment c = it.next();
			article.addComment(c.getId(), ServiceFactory.getESUserService()
					.getUser(c.getUserId()), c.getComment(), c.getToUserId() <= 0 ? null :ServiceFactory
					.getESUserService().getUser(c.getToUserId()));
		}
		
		
		
		query = session.createQuery(" from SWPArticleRecommendation where articleId =?" );
		query.setLong(0, article.getId());
		
		List<SWPArticleRecommendation> listRecomend = query.list();
		Iterator<SWPArticleRecommendation> itRecomend = listRecomend.iterator();
		while (itRecomend.hasNext()) {
			SWPArticleRecommendation c = itRecomend.next();
			article.addRecommend(c.getId(), ServiceFactory.getESUserService()
					.getUser(c.getUserId()));
		}
	}
}
