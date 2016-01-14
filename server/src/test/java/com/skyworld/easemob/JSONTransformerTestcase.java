package com.skyworld.easemob;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

import com.skyworld.pushimpl.AnswerMessage;
import com.skyworld.pushimpl.EasemobMessage;
import com.skyworld.pushimpl.JSONTransformer;
import com.skyworld.pushimpl.QuestionMessage;
import com.skyworld.service.dsf.Answer;
import com.skyworld.service.dsf.Question;
import com.skyworld.service.dsf.SKServicer;
import com.skyworld.service.dsf.User;

public class JSONTransformerTestcase extends TestCase {

	
	JSONTransformer transformer;
	
	@Before
	protected void setUp() throws Exception {
		super.setUp();
		transformer = new JSONTransformer();
	}

	@Test
	public void testSerialize() {
		User asker = new User();
		asker.setId(2);
		asker.setName("akser_username");
		asker.setMail("akser_mail");
		asker.setCellPhone("138");
		QuestionMessage qm = new QuestionMessage(new Question(asker, "Who am I?"));
		String str = transformer.serialize(qm);
		System.out.println(str);
		
		
		SKServicer skservicer = new SKServicer(asker);
		
		AnswerMessage am = new AnswerMessage(new Question(asker, "Who am I?"), new Answer("U r test case"), skservicer);
		str = transformer.serialize(am);
		System.out.println(str);
		
		
		EasemobMessage em = new EasemobMessage(asker);
		str = transformer.serialize(em);
		System.out.println(str);
	}

}
