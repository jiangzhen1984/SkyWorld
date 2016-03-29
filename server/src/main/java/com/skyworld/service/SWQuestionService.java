package com.skyworld.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.skyworld.cache.CacheManager;
import com.skyworld.push.event.MessageEvent;
import com.skyworld.pushimpl.QuestionMessage;
import com.skyworld.service.dsf.Question;
import com.skyworld.service.dsf.SKServicer;
import com.skyworld.service.dsf.User;
import com.skyworld.service.po.SWPQuestion;


public class SWQuestionService extends BaseService {

	private Log log = LogFactory.getLog(this.getClass());
	
	
	private SWUserService userService;
	
	
	
	
	public SWUserService getUserService() {
		return userService;
	}




	public void setUserService(SWUserService userService) {
		this.userService = userService;
	}




	public int saveQuestion(User user, Question question) {
		if (question == null) {
			throw new NullPointerException(" question is null");
		}
		SWPQuestion ques = new SWPQuestion();
		ques.setUserId(user.getId());
		ques.setQuestion(question.getQuestion());
		ques.setTimestamp(question.getTimestamp());
		ques.setState(question.getState().ordinal());
		
		Session session = openSession();
		Transaction t = session.beginTransaction();
		session.save(ques);
		question.setId(ques.getId());
		t.commit();
		session.close();
		question.setAsker(user);
		return 0;
	}
	


	
	public void cancelQuestion(Question question) {
		if (question == null) {
			throw new NullPointerException(" question is null");
		}
		Session session = openSession();
		SWPQuestion q = (SWPQuestion)session.get(SWPQuestion.class , question.getId());
		if (q != null) {
			q.setState(Question.State.CANCEL.ordinal());
			Transaction t = session.beginTransaction();
			session.update(q);
			t.commit();
		}
		session.close();
		question.setState(Question.State.CANCEL);
	}
	
	
	public void finishQuestion(Question question) {
		if (question == null) {
			throw new NullPointerException(" question is null");
		}
		
		Session session = openSession();
		SWPQuestion q = (SWPQuestion)session.get(SWPQuestion.class , question.getId());
		if (q != null) {
			q.setState(Question.State.RESOVLED.ordinal());
			Transaction t = session.beginTransaction();
			session.update(q);
			t.commit();
		}
		session.close();
		question.setState(Question.State.RESOVLED);
	}
	
	
	
	public void broadcastQuestion(Question question) {
		Collection<User> servicerList = CacheManager.getIntance().getAllServicer();
		Iterator<User> it = servicerList.iterator();
		while(it.hasNext()) {
			User u = it.next();
			if (u.getPushTerminal() != null) {
				log.info("Push question to user["+u+"] terminal: " +  u.getPushTerminal());
				u.getPushTerminal().postEvent(new MessageEvent(new QuestionMessage(question)));
				question.addSKServicer((SKServicer)u);
			} else {
				log.warn("User["+u+"] terminal is null");
			}
		}
	}
	
	
	
	public List<Question> queryQuestion(long quersionId) {
		if (userService == null) {
			throw new RuntimeException("User Service doesn't inject yet! please use setUserService to set ");
		}
		List<Question> list = null;
		Session session = openSession();
		SWPQuestion swq = (SWPQuestion)session.get(SWPQuestion.class, quersionId);
		if (swq != null) {
			list  = new ArrayList<Question>(1);
			Question que = new Question();
			que.setQuestion(swq.getQuestion());
			que.setTimestamp(swq.getTimestamp());
			que.setState(Question.State.fromInt(swq.getState()));
			que.setAsker(userService.getUser(swq.getUserId(), session));
		}
		session.close();
		return list;
	}
	
	
	public List<Question> queryQuestion(User publisher) {
		if (publisher == null) {
			return null;
		}
		List<Question> list = null;
		Session session = openSession();
		Query query = session.createQuery(" from SWPQuestion where userId = ? ");
		query.setLong(0, publisher.getId());
		List<SWPQuestion> swqList = query.list();
		if (swqList.size() <= 0) {
			session.close();
			return null;
		}
		
		list = new ArrayList<Question>(swqList.size());
		for (SWPQuestion swq : swqList) {
			Question que = new Question();
			que.setQuestion(swq.getQuestion());
			que.setTimestamp(swq.getTimestamp());
			que.setState(Question.State.fromInt(swq.getState()));
			que.setAsker(publisher);
			list.add(que);
		}
		session.close();
		return list;
	}

}
