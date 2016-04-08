package com.skyworld.init;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.servlet.GenericServlet;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.skyworld.push.PushServerDeamon;
import com.skyworld.pushimpl.DefaultPushConnectionNotifier;
import com.skyworld.pushimpl.JSONTransformer;
import com.skyworld.service.ServiceFactory;

public class StartupServlet extends GenericServlet {

	
	private Log log = LogFactory.getLog(this.getClass());
	
	@Override
	public void destroy() {
		super.destroy(); 
		log.info("============Start to close Session factory=======");
		ServiceFactory.getESUserService().getSessionFactory().close();
		log.info("============Close Session factory successfully=======");
		log.info("============server destroyed=======");
	}

	@Override
	public void init() throws ServletException {
		super.init();
		log.info("============server starting up=======");
		ServiceFactory.getEaseMobService().start();
		Class<ServiceFactory> cls = ServiceFactory.class;
		Method[] ms = cls.getMethods();
		for (Method m : ms) {
			int i = m.getName().lastIndexOf("Service");
			if (i != -1) {
				try {
					if (m.getGenericParameterTypes().length <= 0) {
						log.info("============server initialized  ==> " + m.getName());
						m.invoke(null, null);
					}
				}catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		ServiceFactory.getEaseMobService().authorize("SkyWorld", "SkyWorld", "YXA6UW3TIKTGEeW8okGnOCdMYw", "YXA60GJ7UHua7FFXKEf_P3brVRdUusM");
		log.info("============server start request easemod token <<<=====");
		
		PushServerDeamon.getInstance().setHttpPushMessageTransformer(new JSONTransformer());
		PushServerDeamon.getInstance().setNotifiier(new DefaultPushConnectionNotifier());
	}

	@Override
	public void service(ServletRequest req, ServletResponse res)
			throws ServletException, IOException {
		
	}

	
	
	

}
