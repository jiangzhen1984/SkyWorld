package com.skyworld.service;

import java.util.Collection;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
	
	public int saveQuestion(User user, Question question) {
		if (question == null) {
			throw new NullPointerException(" question is null");
		}
		SWPQuestion ques = new SWPQuestion();
		ques.setMail(user.getMail());
		ques.setName(user.getName());
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

}
