package com.android.samservice;

import java.lang.ref.WeakReference;

import android.app.Activity;
import android.os.Handler;

public class CBObj{
	public WeakReference <Handler> cbHandler;
	public 	int cbMsg;
	public 	Activity cbActivity;
	public 	SignInfo sinfo;
	public 	QuestionInfo qinfo;
	public 	SamCoreObj samobj;
	public	boolean isBroadcast;
	public	SMCallBack smcb;

	CBObj(){
		this.cbHandler = null;
		this.cbMsg = 0;
		this.sinfo = new SignInfo();
		this.qinfo = new QuestionInfo();
		this.samobj = null;
		this.isBroadcast = true;
		this.smcb = null;
	}

	CBObj(SMCallBack SMCB){
		this.cbHandler = null;
		this.cbMsg = 0;
		this.sinfo = new SignInfo();
		this.qinfo = new QuestionInfo();
		this.samobj = null;
		this.isBroadcast = false;
		this.smcb = SMCB;
	}

	
	CBObj(Handler cbHandler, int cbMsg){
		this.cbHandler = new WeakReference <Handler> (cbHandler);
		this.cbMsg = cbMsg;
		this.sinfo = new SignInfo();
		this.qinfo = new QuestionInfo();
		this.samobj = null;
		this.isBroadcast = false;
		this.smcb = null;
	}

		
	CBObj(Handler cbHandler, int cbMsg,SamCoreObj samobj){
		this.cbHandler = new WeakReference <Handler> (cbHandler);
		this.cbMsg = cbMsg;
		this.sinfo = new SignInfo();
		this.qinfo = new QuestionInfo();
		this.samobj = samobj;
		this.isBroadcast = false;
		this.smcb = null;
	}

	CBObj(Handler cbHandler, int cbMsg, String un,String pwd){
		this.cbHandler = new WeakReference <Handler> (cbHandler);
		this.cbMsg = cbMsg;
		this.sinfo = new SignInfo(un,pwd);
		this.samobj = null;
		this.isBroadcast = false;
		this.smcb = null;
	}	
	
	CBObj(Handler cbHandler, int cbMsg, String un,String pwd,String cellphone){
		this.cbHandler = new WeakReference <Handler> (cbHandler);
		this.cbMsg = cbMsg;
		this.sinfo = new SignInfo(un,pwd,cellphone);
		this.isBroadcast = false;
		this.smcb = null;
	}

	CBObj(Handler cbHandler, int cbMsg, String question){
		this.cbHandler = new WeakReference <Handler> (cbHandler);
		this.cbMsg = cbMsg;
		this.qinfo = new QuestionInfo(question);
		this.samobj = null;
		this.isBroadcast = false;
		this.smcb = null;
	}
	

}
