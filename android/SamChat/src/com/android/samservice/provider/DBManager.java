package com.android.samservice.provider;

import java.util.ArrayList;
import java.util.List;

import com.android.samservice.SamLog;
import com.android.samservice.info.*;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

//²Î¿¼£ºhttp://blog.csdn.net/liuhe688/article/details/6715983
public class DBManager
{
	static final String TAG = "DBManager";
	private DatabaseHelper helper;
	private SQLiteDatabase db;

	public DBManager(Context context)
	{
		helper = new DatabaseHelper(context);
		db = helper.getWritableDatabase();
	}

	public long addLogInUser(LoginUser user)
	{
		//DatabaseHelper:TABLE_NAME_LOGIN_USER
		String table = DatabaseHelper.TABLE_NAME_LOGIN_USER;
		/*
		id(primary) | status | username | countrycode |phonenumber |password |usertype | image file |description |area | location |login time|logout time | unique_id | easemob_username |easemob_status |lastupdate
		*/
		ContentValues cv = new ContentValues();
		cv.put("status",user.status);
		cv.put("username",user.username);
		cv.put("countrycode",user.countrycode);
		cv.put("phonenumber",user.phonenumber);
		cv.put("password",user.password);
		cv.put("usertype",user.usertype);
		cv.put("imagefile",user.imagefile);
		cv.put("description",user.description);
		cv.put("area",user.description);
		cv.put("location",user.description);
		cv.put("logintime",user.logintime);
		cv.put("logouttime",user.logouttime);
		cv.put("unique_id",user.unique_id);
		cv.put("easemob_username",user.easemob_username);
		cv.put("easemob_status",user.easemob_status);
		cv.put("lastupdate",user.lastupdate);
		cv.put("conversation_existed",user.conversation_existed);

		return db.insert(table,null,cv);
		
	}

	public long updateLogInUser(long id, LoginUser user)
	{
		String table = DatabaseHelper.TABLE_NAME_LOGIN_USER;
		
		ContentValues cv = new ContentValues();
		cv.put("status",user.status);
		cv.put("username",user.username);
		cv.put("countrycode",user.countrycode);
		cv.put("phonenumber",user.phonenumber);
		cv.put("password",user.password);
		cv.put("usertype",user.usertype);
		cv.put("imagefile",user.imagefile);
		cv.put("description",user.description);
		cv.put("area",user.description);
		cv.put("location",user.description);
		cv.put("logintime",user.logintime);
		cv.put("logouttime",user.logouttime);
		cv.put("unique_id",user.unique_id);
		cv.put("easemob_username",user.easemob_username);
		cv.put("easemob_status",user.easemob_status);
		cv.put("lastupdate",user.lastupdate);
		cv.put("conversation_existed",user.conversation_existed);

		String whereClause = "id=?";
		String [] whereArgs = {""+id+""};

		return db.update(table,cv,whereClause,whereArgs);
		
	}

	public long updateLoginUserConversationExisted(String username,int existed){
		//DatabaseHelper:TABLE_NAME_LOGIN_USER
		String table = DatabaseHelper.TABLE_NAME_LOGIN_USER;
		
		ContentValues cv = new ContentValues();
		cv.put("conversation_existed",existed);
		String whereClause = "username=?";
		String [] whereArgs = {username};

		return db.update(table,cv,whereClause,whereArgs);
	}

	public long updateLoginUserEasemobStatus(String username,int status){
		//DatabaseHelper:TABLE_NAME_LOGIN_USER
		String table = DatabaseHelper.TABLE_NAME_LOGIN_USER;
		
		ContentValues cv = new ContentValues();
		cv.put("easemob_status",status);
		String whereClause = "username=?";
		String [] whereArgs = {username};

		return db.update(table,cv,whereClause,whereArgs);
	}

	public long updateLoginUserAllStatus(String username,int status){
		//DatabaseHelper:TABLE_NAME_LOGIN_USER
		String table = DatabaseHelper.TABLE_NAME_LOGIN_USER;
		
		ContentValues cv = new ContentValues();
		cv.put("status",status);
		cv.put("easemob_status",status);
		String whereClause = "username=?";
		String [] whereArgs = {username};

		return db.update(table,cv,whereClause,whereArgs);
	}

	public long updateLoginUserLogoutStatus(String username,int status,long logouttime){
		//DatabaseHelper:TABLE_NAME_LOGIN_USER
		String table = DatabaseHelper.TABLE_NAME_LOGIN_USER;
		
		ContentValues cv = new ContentValues();
		cv.put("status",status);
		cv.put("logouttime",logouttime);
		String whereClause = "username=?";
		String [] whereArgs = {username};

		return db.update(table,cv,whereClause,whereArgs);
	}

	public LoginUser queryLogInUser(String phonenumber){
		String table = DatabaseHelper.TABLE_NAME_LOGIN_USER;
		LoginUser user = null;
		Cursor c = db.query(table,null,"phonenumber=?",new String[]{phonenumber},null,null,null);

		if(c.getCount()>1){
			SamLog.e(TAG, "Fatal Error for query login user 1");
			throw new RuntimeException("Fatal Error for query login user 1!");
		}
		
		while(c.moveToNext()){
			user = new LoginUser();
			user.id = c.getLong(c.getColumnIndex("id"));
			user.status = c.getInt(c.getColumnIndex("status"));
			user.username = c.getString(c.getColumnIndex("username"));
			user.countrycode = c.getString(c.getColumnIndex("countrycode"));
			user.phonenumber = c.getString(c.getColumnIndex("phonenumber"));
			user.password = c.getString(c.getColumnIndex("password"));
 			user.usertype = c.getInt(c.getColumnIndex("usertype"));
			user.imagefile = c.getString(c.getColumnIndex("imagefile"));
			user.description = c.getString(c.getColumnIndex("description"));
			user.area = c.getString(c.getColumnIndex("area"));
			user.location = c.getString(c.getColumnIndex("location"));
 			user.logintime = c.getLong(c.getColumnIndex("logintime"));
			user.logouttime = c.getLong(c.getColumnIndex("logouttime"));
			user.unique_id = c.getLong(c.getColumnIndex("unique_id"));
			user.easemob_username = c.getString(c.getColumnIndex("easemob_username"));
			user.easemob_status = c.getInt(c.getColumnIndex("easemob_status"));
			user.lastupdate = c.getLong(c.getColumnIndex("lastupdate"));
			user.conversation_existed = c.getInt(c.getColumnIndex("conversation_existed"));
		}

		c.close();

		return user;
	}


	public LoginUser queryLogInUserByUsername(String username){
		String table = DatabaseHelper.TABLE_NAME_LOGIN_USER;
		LoginUser user = null;
		Cursor c = db.query(table,null,"username=?",new String[]{username},null,null,null);

		if(c.getCount()>1){
			SamLog.e(TAG, "Fatal Error for query login user by username");
			throw new RuntimeException("Fatal Error for query login user by username!");
		}
		
		while(c.moveToNext()){
			user = new LoginUser();
			user.id = c.getLong(c.getColumnIndex("id"));
			user.status = c.getInt(c.getColumnIndex("status"));
			user.username = c.getString(c.getColumnIndex("username"));
			user.countrycode = c.getString(c.getColumnIndex("countrycode"));
			user.phonenumber = c.getString(c.getColumnIndex("phonenumber"));
			user.password = c.getString(c.getColumnIndex("password"));
 			user.usertype = c.getInt(c.getColumnIndex("usertype"));
			user.imagefile = c.getString(c.getColumnIndex("imagefile"));
			user.description = c.getString(c.getColumnIndex("description"));
			user.area = c.getString(c.getColumnIndex("area"));
			user.location = c.getString(c.getColumnIndex("location"));
 			user.logintime = c.getLong(c.getColumnIndex("logintime"));
			user.logouttime = c.getLong(c.getColumnIndex("logouttime"));
			user.unique_id = c.getLong(c.getColumnIndex("unique_id"));
			user.easemob_username = c.getString(c.getColumnIndex("easemob_username"));
			user.easemob_status = c.getInt(c.getColumnIndex("easemob_status"));
			user.lastupdate = c.getLong(c.getColumnIndex("lastupdate"));
			user.conversation_existed = c.getInt(c.getColumnIndex("conversation_existed"));
		}

		c.close();

		return user;
	}

	public LoginUser queryLogInUser(){
		String table = DatabaseHelper.TABLE_NAME_LOGIN_USER;
		LoginUser user = null;
		Cursor c = db.query(table,null,"status=?",new String[]{""+LoginUser.ACTIVE+""},null,null,null);

		if(c.getCount()>1){
			SamLog.e(TAG, "Fatal Error for query login user 2");
			throw new RuntimeException("Fatal Error for query login user 2!");
		}
		
		while(c.moveToNext()){
			user = new LoginUser();
			user.id = c.getLong(c.getColumnIndex("id"));
			user.status = c.getInt(c.getColumnIndex("status"));
			user.username = c.getString(c.getColumnIndex("username"));
			user.countrycode = c.getString(c.getColumnIndex("countrycode"));
			user.phonenumber = c.getString(c.getColumnIndex("phonenumber"));
			user.password = c.getString(c.getColumnIndex("password"));
 			user.usertype = c.getInt(c.getColumnIndex("usertype"));
			user.imagefile = c.getString(c.getColumnIndex("imagefile"));
			user.description = c.getString(c.getColumnIndex("description"));
			user.area = c.getString(c.getColumnIndex("area"));
			user.location = c.getString(c.getColumnIndex("location"));
 			user.logintime = c.getLong(c.getColumnIndex("logintime"));
			user.logouttime = c.getLong(c.getColumnIndex("logouttime"));
			user.unique_id = c.getLong(c.getColumnIndex("unique_id"));
			user.easemob_username = c.getString(c.getColumnIndex("easemob_username"));
			user.easemob_status = c.getInt(c.getColumnIndex("easemob_status"));
			user.lastupdate = c.getLong(c.getColumnIndex("lastupdate"));
			user.conversation_existed = c.getInt(c.getColumnIndex("conversation_existed"));
		}

		c.close();

		return user;
	}


	public List<LoginUser> queryAllLoginUser(){
		String table = DatabaseHelper.TABLE_NAME_LOGIN_USER ;

		List<LoginUser> array = new ArrayList<LoginUser>();
		LoginUser user = null;

		Cursor c = db.query(table,null,"status=? or easemob_status=?",new String[]{""+LoginUser.ACTIVE,""+LoginUser.ACTIVE},null,null,null);
		while(c.moveToNext()){
			user = new LoginUser();
			user.id = c.getLong(c.getColumnIndex("id"));
			user.status = c.getInt(c.getColumnIndex("status"));
			user.username = c.getString(c.getColumnIndex("username"));
			user.countrycode = c.getString(c.getColumnIndex("countrycode"));
			user.phonenumber = c.getString(c.getColumnIndex("phonenumber"));
			user.password = c.getString(c.getColumnIndex("password"));
 			user.usertype = c.getInt(c.getColumnIndex("usertype"));
			user.imagefile = c.getString(c.getColumnIndex("imagefile"));
			user.description = c.getString(c.getColumnIndex("description"));
			user.area = c.getString(c.getColumnIndex("area"));
			user.location = c.getString(c.getColumnIndex("location"));
 			user.logintime = c.getLong(c.getColumnIndex("logintime"));
			user.logouttime = c.getLong(c.getColumnIndex("logouttime"));
			user.unique_id = c.getLong(c.getColumnIndex("unique_id"));
			user.easemob_username = c.getString(c.getColumnIndex("easemob_username"));
			user.easemob_status = c.getInt(c.getColumnIndex("easemob_status"));
			user.lastupdate = c.getLong(c.getColumnIndex("lastupdate"));
			user.conversation_existed = c.getInt(c.getColumnIndex("conversation_existed"));

			array.add(user);
		}

		c.close();

		return array;
	}

	public long addContactUser(ContactUser user)
	{
		//DatabaseHelper:TABLE_NAME_CONTACT_USER
		String table = DatabaseHelper.TABLE_NAME_CONTACT_USER;

		ContentValues cv = new ContentValues();
		cv.put("username",user.username);
		cv.put("phonenumber",user.phonenumber);
		cv.put("usertype",user.usertype);
		cv.put("imagefile",user.imagefile);
		cv.put("description",user.description);
		cv.put("area",user.area);
		cv.put("location",user.location);
		cv.put("unique_id",user.unique_id);
		cv.put("easemob_username",user.easemob_username);

		return db.insert(table,null,cv);
		
	}

	public long updateContactUser(long id, ContactUser user)
	{
		//DatabaseHelper:TABLE_NAME_LOGIN_USER
		String table = DatabaseHelper.TABLE_NAME_CONTACT_USER;

		ContentValues cv = new ContentValues();
		cv.put("username",user.username);
		cv.put("phonenumber",user.phonenumber);
		cv.put("usertype",user.usertype);
		cv.put("imagefile",user.imagefile);
		cv.put("description",user.description);
		cv.put("area",user.area);
		cv.put("location",user.location);
		cv.put("unique_id",user.unique_id);
		cv.put("easemob_username",user.easemob_username);

		String whereClause = "id=?";
		String [] whereArgs = {""+id+""};

		return db.update(table,cv,whereClause,whereArgs);
		
	}

	public ContactUser queryContactUser(String phonenumber){
		String table = DatabaseHelper.TABLE_NAME_CONTACT_USER;
		ContactUser user = null;
		Cursor c = db.query(table,null,"phonenumber=?",new String[]{phonenumber},null,null,null);

		if(c.getCount()>1){
			SamLog.e(TAG, "Fatal Error for query contact user 1");
			throw new RuntimeException("Fatal Error for query contact user 1!");
			
		}
		
		while(c.moveToNext()){
			user = new ContactUser();
			user.id = c.getLong(c.getColumnIndex("id"));
			user.username = c.getString(c.getColumnIndex("username"));
			user.phonenumber = c.getString(c.getColumnIndex("phonenumber"));
 			user.usertype = c.getInt(c.getColumnIndex("usertype"));
			user.imagefile = c.getString(c.getColumnIndex("imagefile"));
			user.description = c.getString(c.getColumnIndex("description"));
			user.area = c.getString(c.getColumnIndex("area"));
			user.location = c.getString(c.getColumnIndex("location"));			
			user.unique_id = c.getLong(c.getColumnIndex("unique_id"));
			user.easemob_username = c.getString(c.getColumnIndex("easemob_username"));
		}

		c.close();

		return user;
	}

	public ContactUser queryContactUserByUsername(String username){
		String table = DatabaseHelper.TABLE_NAME_CONTACT_USER;
		ContactUser user = null;
		Cursor c = db.query(table,null,"username=?",new String[]{username},null,null,null);

		if(c.getCount()>1){
			SamLog.e(TAG, "Fatal Error for query contact user by username");
			throw new RuntimeException("Fatal Error for query contact user by username!");
			
		}
		
		while(c.moveToNext()){
			user = new ContactUser();
			user.id = c.getLong(c.getColumnIndex("id"));
			user.username = c.getString(c.getColumnIndex("username"));
			user.phonenumber = c.getString(c.getColumnIndex("phonenumber"));
 			user.usertype = c.getInt(c.getColumnIndex("usertype"));
			user.imagefile = c.getString(c.getColumnIndex("imagefile"));
			user.description = c.getString(c.getColumnIndex("description"));
			user.area = c.getString(c.getColumnIndex("area"));
			user.location = c.getString(c.getColumnIndex("location"));			
			user.unique_id = c.getLong(c.getColumnIndex("unique_id"));
			user.easemob_username = c.getString(c.getColumnIndex("easemob_username"));
		}

		c.close();

		return user;
	}

	public ContactUser queryContactUser(long id){
		String table = DatabaseHelper.TABLE_NAME_CONTACT_USER;
		ContactUser user = null;
		Cursor c = db.query(table,null,"id=?",new String[]{""+id},null,null,null);

		if(c.getCount()>1){
			SamLog.e(TAG, "Fatal Error for query contact user 2");
			throw new RuntimeException("Fatal Error for query contact user 2!");
		}
		
		while(c.moveToNext()){
			user = new ContactUser();
			user.id = c.getLong(c.getColumnIndex("id"));
			user.username = c.getString(c.getColumnIndex("username"));
			user.phonenumber = c.getString(c.getColumnIndex("phonenumber"));
 			user.usertype = c.getInt(c.getColumnIndex("usertype"));
			user.imagefile = c.getString(c.getColumnIndex("imagefile"));
			user.description = c.getString(c.getColumnIndex("description"));
			user.area = c.getString(c.getColumnIndex("area"));
			user.location = c.getString(c.getColumnIndex("location"));
			user.unique_id = c.getLong(c.getColumnIndex("unique_id"));
			user.easemob_username = c.getString(c.getColumnIndex("easemob_username"));
		}

		c.close();

		return user;
	}



  	public long addSendQuestion(SendQuestion question)
	{
		String table = DatabaseHelper.TABLE_NAME_SEND_QUESTION;
		
		ContentValues cv = new ContentValues();
		cv.put("question_id",question.question_id);
		cv.put("senduserid",question.senduserid);
		cv.put("question",question.question);
		cv.put("status",question.status);
		cv.put("sendtime",question.sendtime);
		cv.put("canceltime",question.canceltime);
		cv.put("sendercellphone",question.sendercellphone);
		cv.put("senderusername",question.senderusername);

		return db.insert(table,null,cv);
		
	}

	public long updateSendQuestion(long id, SendQuestion question)
	{
		String table = DatabaseHelper.TABLE_NAME_SEND_QUESTION;

		ContentValues cv = new ContentValues();
		cv.put("question_id",question.question_id);
		cv.put("senduserid",question.senduserid);
		cv.put("question",question.question);
		cv.put("status",question.status);
		cv.put("sendtime",question.sendtime);
		cv.put("canceltime",question.canceltime);
		cv.put("sendercellphone",question.sendercellphone);
		cv.put("senderusername",question.senderusername);

		String whereClause = "id=?";
		String [] whereArgs = {""+id+""};

		return db.update(table,cv,whereClause,whereArgs);
		
	}

	public SendQuestion querySendQuestion(String question_id){
		String table = DatabaseHelper.TABLE_NAME_SEND_QUESTION;
		SendQuestion question = null;
		Cursor c = db.query(table,null,"question_id=?",new String[]{question_id},null,null,null);

		if(c.getCount()>1){
			SamLog.e(TAG, "Fatal Error for query send question");
			throw new RuntimeException("Fatal Error for query send question!");
		}
		
		while(c.moveToNext()){
			question = new SendQuestion();
			question.id = c.getLong(c.getColumnIndex("id"));
			question.question_id = c.getString(c.getColumnIndex("question_id"));
			question.senduserid= c.getLong(c.getColumnIndex("senduserid"));
 			question.question = c.getString(c.getColumnIndex("question"));
			question.status = c.getInt(c.getColumnIndex("status"));
			
			question.sendtime = c.getLong(c.getColumnIndex("sendtime"));
			question.canceltime = c.getLong(c.getColumnIndex("canceltime"));

			question.sendercellphone = c.getString(c.getColumnIndex("sendercellphone"));
			question.senderusername = c.getString(c.getColumnIndex("senderusername"));
			
		}

		c.close();

		return question;
	}

	public long addReceivedAnswer(ReceivedAnswer answer)
	{
		String table = DatabaseHelper.TABLE_NAME_RECEIVED_ANSWER ;
		/*
		id(primary) |question_id | answer |contact user id | received time 
		*/
		ContentValues cv = new ContentValues();
		cv.put("question_id",answer.question_id);
		cv.put("answer",answer.answer);
		cv.put("contactuserid",answer.contactuserid);
		cv.put("receivedtime",answer.receivedtime );
		
		return db.insert(table,null,cv);
		
	}

	public List<ReceivedAnswer> queryReceivedAnswer(String question_id){
		String table = DatabaseHelper.TABLE_NAME_RECEIVED_ANSWER ;
		/*
		id(primary) |question_id | answer |contact user id | received time 
		*/

		List<ReceivedAnswer> answerArray = new ArrayList<ReceivedAnswer>();

		Cursor c = db.query(table,null,"question_id=?",new String[]{question_id},null,null,null);
		while(c.moveToNext()){
			ReceivedAnswer answer = new ReceivedAnswer();
			answer.id = c.getLong(c.getColumnIndex("id"));
			answer.question_id = c.getString(c.getColumnIndex("question_id"));
 			answer.answer = c.getString(c.getColumnIndex("answer"));
			answer.contactuserid= c.getLong(c.getColumnIndex("contactuserid"));
			answer.receivedtime  = c.getLong(c.getColumnIndex("receivedtime"));

			answerArray.add(answer);
		}

		c.close();

		return answerArray;
	}

	public long addReceivedQuestion(ReceivedQuestion question)
	{
		String table = DatabaseHelper.TABLE_NAME_RECEIVED_QUESTION;
		
		ContentValues cv = new ContentValues();
		cv.put("question_id",question.question_id);
		cv.put("question",question.question);
		cv.put("contactuserid",question.contactuserid);
		cv.put("status",question.status );
		cv.put("response",question.response );
		cv.put("receivedtime",question.receivedtime );
		cv.put("canceledtime",question.canceledtime );
		cv.put("receivercellphone",question.receivercellphone );
		cv.put("receiverusername",question.receiverusername );
		
		return db.insert(table,null,cv);
		
	}

	public long updateReceivedQuestion(long id, ReceivedQuestion question)
	{
		String table = DatabaseHelper.TABLE_NAME_RECEIVED_QUESTION;

		ContentValues cv = new ContentValues();
		cv.put("question_id",question.question_id);
		cv.put("question",question.question);
		cv.put("contactuserid",question.contactuserid);
		cv.put("status",question.status );
		cv.put("response",question.response );
		cv.put("receivedtime",question.receivedtime );
		cv.put("canceledtime",question.canceledtime );
		cv.put("receivercellphone",question.receivercellphone );
		cv.put("receiverusername",question.receiverusername );
		
		String whereClause = "id=?";
		String [] whereArgs = {""+id+""};

		return db.update(table,cv,whereClause,whereArgs);
		
	}

	public ReceivedQuestion queryReceivedQuestion(String question_id){
		String table = DatabaseHelper.TABLE_NAME_RECEIVED_QUESTION ;
		/*
		id(primary) |question_id | question |contact user id | status | response |received time | canceled time |receivercellphone
		*/

		ReceivedQuestion question = null;

		Cursor c = db.query(table,null,"question_id=?",new String[]{question_id},null,null,null);
		while(c.moveToNext()){
			question = new ReceivedQuestion();
			question.id = c.getLong(c.getColumnIndex("id"));
			question.question_id = c.getString(c.getColumnIndex("question_id"));
 			question.question = c.getString(c.getColumnIndex("question"));
			question.contactuserid= c.getLong(c.getColumnIndex("contactuserid"));
			question.status = c.getInt(c.getColumnIndex("status"));
			question.response = c.getInt(c.getColumnIndex("response"));
			question.receivedtime  = c.getLong(c.getColumnIndex("receivedtime"));
			question.canceledtime =  c.getLong(c.getColumnIndex("canceledtime"));
			question.receivercellphone = c.getString(c.getColumnIndex("receivercellphone"));
			question.receiverusername= c.getString(c.getColumnIndex("receiverusername"));
		}

		c.close();

		return question;
	}

	public List<String> get_ReceivedQuestion_Not_Response(long contactuserid,String receiverusername){
		String table = DatabaseHelper.TABLE_NAME_RECEIVED_QUESTION ;
		/*
		id(primary) |question_id | question |contact user id | status | response |received time | canceled time |receivercellphone
		*/

		List<String> questions = new ArrayList<String>();
		List<Long> ids=new ArrayList<Long>();

		Cursor c = db.query(table,null,"contactuserid=? and receiverusername=? and response=?",new String[]{""+contactuserid,receiverusername,""+ReceivedQuestion.NOT_RESPONSED},null,null,null);
		while(c.moveToNext()){
			String question_id = c.getString(c.getColumnIndex("question_id"));
			questions.add(question_id);

			long id = c.getLong(c.getColumnIndex("id"));
			ids.add(id);
		}

		c.close();

		for(Long index:ids){
			ContentValues cv = new ContentValues();
			cv.put("response",ReceivedQuestion.RESPONSED);
		
			String whereClause = "id=?";
			String [] whereArgs = {""+Long.valueOf(index)};

			db.update(table,cv,whereClause,whereArgs);
		}

		return questions;
	}

	public void clearReceivedQuestion(String receiverusername,long datetime){
		String table = DatabaseHelper.TABLE_NAME_RECEIVED_QUESTION ;

		db.delete(table, "receiverusername=? and receivedtime<?", new String[]{receiverusername,""+datetime});		

	}


	private long fetchPlacesCount(String table) {
		String sql = "SELECT COUNT(*) FROM " + table;
		SQLiteStatement statement = db.compileStatement(sql);
		long count = statement.simpleQueryForLong();
		return count;
	}

	public List<ReceivedQuestion> queryRecentReceivedQuestion(String receiverusername){
		String table = DatabaseHelper.TABLE_NAME_RECEIVED_QUESTION ;

		List<ReceivedQuestion> ReceivedQuestionArray = new ArrayList<ReceivedQuestion>();
		ReceivedQuestion question = null;

		Cursor c = db.query(table,null,"receiverusername=?",new String[]{receiverusername},null,null,null);
		while(c.moveToNext()){
			question = new ReceivedQuestion();
			question.id = c.getLong(c.getColumnIndex("id"));
			question.question_id = c.getString(c.getColumnIndex("question_id"));
 			question.question = c.getString(c.getColumnIndex("question"));
			question.contactuserid= c.getLong(c.getColumnIndex("contactuserid"));
			question.status = c.getInt(c.getColumnIndex("status"));
			question.response = c.getInt(c.getColumnIndex("response"));
			question.receivedtime  = c.getLong(c.getColumnIndex("receivedtime"));
			question.canceledtime =  c.getLong(c.getColumnIndex("canceledtime"));
			question.receivercellphone = c.getString(c.getColumnIndex("receivercellphone"));
			question.receiverusername = c.getString(c.getColumnIndex("receiverusername"));
			
			
			ReceivedQuestionArray.add(question);
		}

		c.close();

		return ReceivedQuestionArray;
	}

	public long addSendAnswer(SendAnswer answer)
	{
		String table = DatabaseHelper.TABLE_NAME_SEND_ANSWER;
		/*
			id(primary) |question_id | answer |status | loginuserid | sendtime 
		*/
		ContentValues cv = new ContentValues();
		cv.put("question_id",answer.question_id);
		cv.put("answer",answer.answer);
		cv.put("status",answer.status);
		cv.put("loginuserid",answer.loginuserid );
		cv.put("sendtime",answer.sendtime );
		
		return db.insert(table,null,cv);
	}

	public long updateSendAnswer(long id, SendAnswer answer)
	{
		String table = DatabaseHelper.TABLE_NAME_SEND_ANSWER;
		/*
			id(primary) |question_id | answer |status | loginuserid | sendtime 
		*/
		ContentValues cv = new ContentValues();
		cv.put("question_id",answer.question_id);
		cv.put("answer",answer.answer);
		cv.put("status",answer.status);
		cv.put("loginuserid",answer.loginuserid );
		cv.put("sendtime",answer.sendtime );

		String whereClause = "id=?";
		String [] whereArgs = {""+id+""};

		return db.update(table,cv,whereClause,whereArgs);
		
	}

	public List<SendAnswer> querySendAnswer(String question_id,long sender_id){
		String table = DatabaseHelper.TABLE_NAME_SEND_ANSWER ;

		/*
			id(primary) |question_id | answer |status | loginuserid | sendtime 
		*/

		List<SendAnswer> sendAnswerArray = new ArrayList<SendAnswer>();
		SendAnswer answer=null;

		Cursor c = db.query(table,null,"question_id=? and loginuserid=?",new String[]{question_id,""+sender_id},null,null,null);
		while(c.moveToNext()){
			answer = new SendAnswer();
			answer.id = c.getLong(c.getColumnIndex("id"));
			answer.question_id = c.getString(c.getColumnIndex("question_id"));
 			answer.answer = c.getString(c.getColumnIndex("answer"));
			answer.status = c.getInt(c.getColumnIndex("status"));
			answer.loginuserid = c.getLong(c.getColumnIndex("loginuserid"));
			answer.sendtime  = c.getLong(c.getColumnIndex("sendtime"));

			sendAnswerArray.add(answer);
		}

		c.close();

		return sendAnswerArray;
	}


	
	public long addInviteRecord(InviteMessageRecord record)
	{
		String table = DatabaseHelper.TABLE_CONTACT_INVITE_RECORD;
		/*
			id(primary) |sender | receiver | status | reason | time
		*/
		ContentValues cv = new ContentValues();
		cv.put("sender",record.sender);
		cv.put("receiver",record.receiver);
		cv.put("status",record.status);
		cv.put("reason",record.reason);
		cv.put("time",record.time);
		
		return db.insert(table,null,cv);
	}

	public long updateInviteRecord(long id, InviteMessageRecord record)
	{
		String table = DatabaseHelper.TABLE_CONTACT_INVITE_RECORD;
		/*
			id(primary) |sender | receiver | status | reason | time
		*/
		ContentValues cv = new ContentValues();
		cv.put("sender",record.sender);
		cv.put("receiver",record.receiver);
		cv.put("status",record.status);
		cv.put("reason",record.reason);
		cv.put("time",record.time);

		String whereClause = "id=?";
		String [] whereArgs = {""+id+""};

		return db.update(table,cv,whereClause,whereArgs);
		
	}

	public long updateInviteRecord(long id, ContentValues values){
		String table = DatabaseHelper.TABLE_CONTACT_INVITE_RECORD;
		/*
			id(primary) |sender | receiver | status | reason | time
		*/
		String whereClause = "id=?";
		String [] whereArgs = {""+id+""};

		return db.update(table,values,whereClause,whereArgs);
	}

	public void deleteInviteRecord(String receiver,String sender){
		String table = DatabaseHelper.TABLE_CONTACT_INVITE_RECORD;
		/*
			id(primary) |sender | receiver | status | reason | time
		*/
		db.delete(table, "receiver=? and sender = ?", new String[]{receiver,sender});
	}

	public List<InviteMessageRecord> queryInviteRecordBasedReceiver(String receiver){
		String table = DatabaseHelper.TABLE_CONTACT_INVITE_RECORD ;

		/*
			id(primary) |sender | receiver | status | reason | time
		*/

		List<InviteMessageRecord> ContactInviteRecordArray = new ArrayList<InviteMessageRecord>();
		InviteMessageRecord record = null;

		Cursor c = db.query(table,null,"receiver=?",new String[]{receiver},null,null,null);
		while(c.moveToNext()){
			record = new InviteMessageRecord();
			record.id = c.getLong(c.getColumnIndex("id"));
			record.sender = c.getString(c.getColumnIndex("sender"));
			record.receiver= c.getString(c.getColumnIndex("receiver"));
			record.status = c.getInt(c.getColumnIndex("status"));
			record.reason= c.getString(c.getColumnIndex("reason"));
			record.time = c.getLong(c.getColumnIndex("time"));

			ContactInviteRecordArray.add(record);
		}

		c.close();

		return ContactInviteRecordArray;
	}

	public List<InviteMessageRecord> queryInviteRecordBasedReceiver(String receiver,int status){
		String table = DatabaseHelper.TABLE_CONTACT_INVITE_RECORD ;

		/*
			id(primary) |sender | receiver | status | reason | time
		*/

		List<InviteMessageRecord> ContactInviteRecordArray = new ArrayList<InviteMessageRecord>();
		InviteMessageRecord record = null;

		Cursor c = db.query(table,null,"receiver=? and status=?",new String[]{receiver,""+status},null,null,null);
		while(c.moveToNext()){
			record = new InviteMessageRecord();
			record.id = c.getLong(c.getColumnIndex("id"));
			record.sender = c.getString(c.getColumnIndex("sender"));
			record.receiver= c.getString(c.getColumnIndex("receiver"));
			record.status = c.getInt(c.getColumnIndex("status"));
			record.reason= c.getString(c.getColumnIndex("reason"));
			record.time = c.getLong(c.getColumnIndex("time"));

			ContactInviteRecordArray.add(record);
		}

		c.close();

		return ContactInviteRecordArray;
	}

	public List<InviteMessageRecord> queryInviteRecordBasedReceiverSender(String receiver,String sender){
		String table = DatabaseHelper.TABLE_CONTACT_INVITE_RECORD ;

		/*
			id(primary) |sender | receiver | status | reason | time
		*/

		List<InviteMessageRecord> ContactInviteRecordArray = new ArrayList<InviteMessageRecord>();
		InviteMessageRecord record = null;

		Cursor c = db.query(table,null,"receiver=? and sender=?",new String[]{receiver,sender},null,null,null);
		while(c.moveToNext()){
			record = new InviteMessageRecord();
			record.id = c.getLong(c.getColumnIndex("id"));
			record.sender = c.getString(c.getColumnIndex("sender"));
			record.receiver= c.getString(c.getColumnIndex("receiver"));
			record.status = c.getInt(c.getColumnIndex("status"));
			record.reason= c.getString(c.getColumnIndex("reason"));
			record.time = c.getLong(c.getColumnIndex("time"));
			ContactInviteRecordArray.add(record);
		}

		c.close();

		return ContactInviteRecordArray;
	}

	public long addUserFriendRecord(UserFriendRecord record)
	{
		String table = DatabaseHelper.TABLE_NAME_USER_FRIEND;
		/*
			id(primary) | friend
		*/
		ContentValues cv = new ContentValues();
		cv.put("friend",record.friend);
		
		return db.insert(table,null,cv);
	}

	public long updateUserFriendRecord(long id, UserFriendRecord record)
	{
		String table = DatabaseHelper.TABLE_NAME_USER_FRIEND;
		/*
			id(primary) | friend
		*/
		ContentValues cv = new ContentValues();
		cv.put("friend",record.friend);

		String whereClause = "id=?";
		String [] whereArgs = {""+id+""};

		return db.update(table,cv,whereClause,whereArgs);
	}

	public List<UserFriendRecord> queryUserFriendRecord(){
		String table = DatabaseHelper.TABLE_NAME_USER_FRIEND ;

		/*
			id(primary) | friend
		*/

		List<UserFriendRecord> recordArray = new ArrayList<UserFriendRecord>();
		UserFriendRecord record = null;

		Cursor c = db.rawQuery("select * from " + table, null);

		while(c.moveToNext()){
			record = new UserFriendRecord();
			record.id = c.getLong(c.getColumnIndex("id"));
			record.friend= c.getString(c.getColumnIndex("friend"));
			recordArray.add(record);
		}

		c.close();

		return recordArray;
	}

	public UserFriendRecord queryUserFriendRecord(String easemob_name){
		String table = DatabaseHelper.TABLE_NAME_USER_FRIEND ;

		/*
			id(primary) | user | friend
		*/

		UserFriendRecord record = null;

		Cursor c = db.query(table,null,"friend=?",new String[]{easemob_name},null,null,null);

		while(c.moveToNext()){
			record = new UserFriendRecord();
			record.id = c.getLong(c.getColumnIndex("id"));
			record.friend= c.getString(c.getColumnIndex("friend"));
		}

		c.close();

		return record;
	}

	public void clearUserFriendTable(){
		String table = DatabaseHelper.TABLE_NAME_USER_FRIEND ;
		db.delete(table, null, null);
	}

	public void deleteUserFriend(String easemob_name){
		String table = DatabaseHelper.TABLE_NAME_USER_FRIEND ;

		/*
			id(primary) | friend
		*/
		db.delete(table, "friend=?", new String[]{easemob_name});
	}

	public long addAvatarRecord(AvatarRecord record)
	{
		String table = DatabaseHelper.TABLE_NAME_AVATAR;
		/*
		id(primary) |phonenumber | avatarname |nickname
		*/
		ContentValues cv = new ContentValues();
		cv.put("phonenumber",record.phonenumber);
		cv.put("avatarname",record.avatarname);
		cv.put("nickname",record.nickname);
		
		return db.insert(table,null,cv);
	}

	public long updateAvatarRecord(long id, AvatarRecord record)
	{
		String table = DatabaseHelper.TABLE_NAME_AVATAR;
		/*
		id(primary) |phonenumber | avatarname |nickname
		*/
		ContentValues cv = new ContentValues();
		cv.put("phonenumber",record.phonenumber);
		cv.put("avatarname",record.avatarname);
		cv.put("nickname",record.nickname);

		String whereClause = "id=?";
		String [] whereArgs = {""+id+""};

		return db.update(table,cv,whereClause,whereArgs);
	}

	public AvatarRecord queryAvatarRecord(String phonenumber){
		String table = DatabaseHelper.TABLE_NAME_AVATAR ;

		/*
		id(primary) |phonenumber | avatarname |nickname
		*/

		AvatarRecord record = null;

		Cursor c = db.query(table,null,"phonenumber=?",new String[]{phonenumber},null,null,null);

		while(c.moveToNext()){
			record = new AvatarRecord();
			record.id = c.getLong(c.getColumnIndex("id"));
			record.phonenumber= c.getString(c.getColumnIndex("phonenumber"));
			record.avatarname= c.getString(c.getColumnIndex("avatarname"));
			record.nickname= c.getString(c.getColumnIndex("nickname"));
		}

		c.close();

		return record;
	}

	public AvatarRecord queryAvatarRecordByUsername(String nickname){
		String table = DatabaseHelper.TABLE_NAME_AVATAR ;

		/*
		id(primary) |phonenumber | avatarname |nickname
		*/

		AvatarRecord record = null;

		Cursor c = db.query(table,null,"nickname=?",new String[]{nickname},null,null,null);

		if(c.getCount()>1){
			SamLog.e(TAG, "Fatal Error for query avatar by username");
			throw new RuntimeException("Fatal Error for query avatar by username!");
		}

		while(c.moveToNext()){
			record = new AvatarRecord();
			record.id = c.getLong(c.getColumnIndex("id"));
			record.phonenumber= c.getString(c.getColumnIndex("phonenumber"));
			record.avatarname= c.getString(c.getColumnIndex("avatarname"));
			record.nickname= c.getString(c.getColumnIndex("nickname"));
		}

		c.close();

		return record;
	}

	public long addFGRecord(FGRecord record)
	{
		String table = DatabaseHelper.TABLE_NAME_FG;

		ContentValues cv = new ContentValues();
		cv.put("timestamp",record.timestamp);
		cv.put("fg_id",record.fg_id);
		cv.put("status",record.status);
		cv.put("comment",record.comment);
		cv.put("publisher_phonenumber",record.publisher_phonenumber);
		cv.put("owner_phonenumber",record.owner_phonenumber);
		cv.put("publisher_username",record.publisher_username);
		cv.put("owner_username",record.owner_username);
		
		return db.insert(table,null,cv);
	}

	public List<FGRecord> queryFGRecordByUsername(String publisher_username,String owner_username){
		String table = DatabaseHelper.TABLE_NAME_FG ;
		FGRecord record = null;

		Cursor c = db.query(table,null,"publisher_username=? and owner_username=?",new String[]{publisher_username,owner_username},null,null,null);
		List<FGRecord> RecordArray = new ArrayList<FGRecord>();
		while(c.moveToNext()){
			record = new FGRecord();
			record.id = c.getLong(c.getColumnIndex("id"));
			record.timestamp= c.getLong(c.getColumnIndex("timestamp"));
			record.fg_id= c.getLong(c.getColumnIndex("fg_id"));
			record.status= c.getInt(c.getColumnIndex("status"));
			record.comment= c.getString(c.getColumnIndex("comment"));
			record.publisher_phonenumber= c.getString(c.getColumnIndex("publisher_phonenumber"));
			record.owner_phonenumber = c.getString(c.getColumnIndex("owner_phonenumber"));
			record.publisher_username = c.getString(c.getColumnIndex("publisher_username"));
			record.owner_username = c.getString(c.getColumnIndex("owner_username"));
			
			RecordArray.add(record);
		}

		c.close();

		return RecordArray;
	}

	public List<FGRecord> queryFGRecord(String publisher_phonenumber,String owner_phonenumber){
		String table = DatabaseHelper.TABLE_NAME_FG ;
		FGRecord record = null;

		Cursor c = db.query(table,null,"publisher_phonenumber=? and owner_phonenumber=?",new String[]{publisher_phonenumber,owner_phonenumber},null,null,null);
		List<FGRecord> RecordArray = new ArrayList<FGRecord>();
		while(c.moveToNext()){
			record = new FGRecord();
			record.id = c.getLong(c.getColumnIndex("id"));
			record.timestamp= c.getLong(c.getColumnIndex("timestamp"));
			record.fg_id= c.getLong(c.getColumnIndex("fg_id"));
			record.status= c.getInt(c.getColumnIndex("status"));
			record.comment= c.getString(c.getColumnIndex("comment"));
			record.publisher_phonenumber= c.getString(c.getColumnIndex("publisher_phonenumber"));
			record.owner_phonenumber = c.getString(c.getColumnIndex("owner_phonenumber"));
			record.publisher_username = c.getString(c.getColumnIndex("publisher_username"));
			record.owner_username = c.getString(c.getColumnIndex("owner_username"));
			
			RecordArray.add(record);
		}

		c.close();

		return RecordArray;
	}


	public List<FGRecord> queryFGRecordByUsername(String owner_username){
		String table = DatabaseHelper.TABLE_NAME_FG ;

		FGRecord record = null;

		Cursor c = db.query(table,null,"owner_username=?",new String[]{owner_username},null,null,"timestamp desc");
		List<FGRecord> RecordArray = new ArrayList<FGRecord>();
		while(c.moveToNext()){
			record = new FGRecord();
			record.id = c.getLong(c.getColumnIndex("id"));
			record.timestamp= c.getLong(c.getColumnIndex("timestamp"));
			record.fg_id= c.getLong(c.getColumnIndex("fg_id"));
			record.status= c.getInt(c.getColumnIndex("status"));
			record.comment= c.getString(c.getColumnIndex("comment"));
			record.publisher_phonenumber= c.getString(c.getColumnIndex("publisher_phonenumber"));
			record.owner_phonenumber = c.getString(c.getColumnIndex("owner_phonenumber"));
			record.publisher_username = c.getString(c.getColumnIndex("publisher_username"));
			record.owner_username = c.getString(c.getColumnIndex("owner_username"));
			
			RecordArray.add(record);
		}

		c.close();

		return RecordArray;
	}

	public List<FGRecord> queryFGRecord(String owner_phonenumber){
		String table = DatabaseHelper.TABLE_NAME_FG ;

		FGRecord record = null;

		Cursor c = db.query(table,null,"owner_phonenumber=?",new String[]{owner_phonenumber},null,null,"timestamp desc");
		List<FGRecord> RecordArray = new ArrayList<FGRecord>();
		while(c.moveToNext()){
			record = new FGRecord();
			record.id = c.getLong(c.getColumnIndex("id"));
			record.timestamp= c.getLong(c.getColumnIndex("timestamp"));
			record.fg_id= c.getLong(c.getColumnIndex("fg_id"));
			record.status= c.getInt(c.getColumnIndex("status"));
			record.comment= c.getString(c.getColumnIndex("comment"));
			record.publisher_phonenumber= c.getString(c.getColumnIndex("publisher_phonenumber"));
			record.owner_phonenumber = c.getString(c.getColumnIndex("owner_phonenumber"));
			record.publisher_username = c.getString(c.getColumnIndex("publisher_username"));
			record.owner_username = c.getString(c.getColumnIndex("owner_username"));
			
			RecordArray.add(record);
		}

		c.close();

		return RecordArray;
	}


	public FGRecord queryFGRecordByUsername(long fg_id,String owner_username){
		String table = DatabaseHelper.TABLE_NAME_FG ;

		FGRecord record = null;

		Cursor c = db.query(table,null,"fg_id=? and owner_username=?",new String[]{""+fg_id , owner_username},null,null,null);

		if(c.getCount()>1){
			SamLog.e(TAG, "Fatal Error for query FG");
			throw new RuntimeException("Fatal Error for query FG!");
		}

		while(c.moveToNext()){
			record = new FGRecord();
			record.id = c.getLong(c.getColumnIndex("id"));
			record.timestamp= c.getLong(c.getColumnIndex("timestamp"));
			record.fg_id= c.getLong(c.getColumnIndex("fg_id"));
			record.status= c.getInt(c.getColumnIndex("status"));
			record.comment= c.getString(c.getColumnIndex("comment"));
			record.publisher_phonenumber= c.getString(c.getColumnIndex("publisher_phonenumber"));
			record.owner_phonenumber = c.getString(c.getColumnIndex("owner_phonenumber"));
			record.publisher_username = c.getString(c.getColumnIndex("publisher_username"));
			record.owner_username = c.getString(c.getColumnIndex("owner_username"));
			
		}

		c.close();

		return record;
	}

	public FGRecord queryFGRecord(long fg_id,String owner_phonenumber){
		String table = DatabaseHelper.TABLE_NAME_FG ;

		FGRecord record = null;

		Cursor c = db.query(table,null,"fg_id=? and owner_phonenumber=?",new String[]{""+fg_id , owner_phonenumber},null,null,null);

		if(c.getCount()>1){
			SamLog.e(TAG, "Fatal Error for query FG");
			throw new RuntimeException("Fatal Error for query FG!");
		}

		while(c.moveToNext()){
			record = new FGRecord();
			record.id = c.getLong(c.getColumnIndex("id"));
			record.timestamp= c.getLong(c.getColumnIndex("timestamp"));
			record.fg_id= c.getLong(c.getColumnIndex("fg_id"));
			record.status= c.getInt(c.getColumnIndex("status"));
			record.comment= c.getString(c.getColumnIndex("comment"));
			record.publisher_phonenumber= c.getString(c.getColumnIndex("publisher_phonenumber"));
			record.owner_phonenumber = c.getString(c.getColumnIndex("owner_phonenumber"));
			record.publisher_username = c.getString(c.getColumnIndex("publisher_username"));
			record.owner_username = c.getString(c.getColumnIndex("owner_username"));
			
		}

		c.close();

		return record;
	}

	
	public long addRecommanderRecord(RecommanderRecord record)
	{
		String table = DatabaseHelper.TABLE_NAME_RECOMMANDER;
		/*
		id(primary) |recommander_phonenumber | fg_id |timestamp
		*/
		ContentValues cv = new ContentValues();
		cv.put("recommander_phonenumber",record.recommander_phonenumber);
		cv.put("fg_id",record.fg_id);
		cv.put("timestamp",record.timestamp);
		
		return db.insert(table,null,cv);
	}

	public RecommanderRecord queryRecommanderRecord(String recommander_phonenumber,long fg_id,long timestamp){
		String table = DatabaseHelper.TABLE_NAME_RECOMMANDER ;

		/*
		id(primary) |recommander_phonenumber | fg_id |timestamp
		*/

		RecommanderRecord record = null;

		Cursor c = db.query(table,null,"recommander_phonenumber=? and fg_id=? and timestamp=?",new String[]{recommander_phonenumber,""+fg_id,""+timestamp},null,null,null);

		if(c.getCount()>1){
			SamLog.e(TAG, "Fatal Error for query RecommanderRecord");
			throw new RuntimeException("Fatal Error for query RecommanderRecord!");
		}

		while(c.moveToNext()){
			record = new RecommanderRecord();
			record.id = c.getLong(c.getColumnIndex("id"));
			record.recommander_phonenumber= c.getString(c.getColumnIndex("recommander_phonenumber"));
			record.fg_id = c.getLong(c.getColumnIndex("fg_id"));
			record.timestamp = c.getLong(c.getColumnIndex("timestamp"));
		}

		c.close();

		return record;
	}


	public long addCommenterRecord(CommenterRecord record)
	{
		String table = DatabaseHelper.TABLE_NAME_COMMENTER;
		/*
		id(primary) |commenter_phonenumber | content | fg_id |timestamp
		*/
		ContentValues cv = new ContentValues();
		cv.put("commenter_phonenumber",record.commenter_phonenumber);
		cv.put("content",record.content);
		cv.put("fg_id",record.fg_id);
		cv.put("timestamp",record.timestamp);
		
		return db.insert(table,null,cv);
	}

	public CommenterRecord queryCommenterRecord(String commenter_phonenumber,long fg_id,long timestamp){
		String table = DatabaseHelper.TABLE_NAME_COMMENTER;
		/*
		id(primary) |commenter_phonenumber | content | fg_id |timestamp
		*/
		CommenterRecord record = null;

		Cursor c = db.query(table,null,"commenter_phonenumber=? and fg_id=? and timestamp=?",new String[]{commenter_phonenumber,""+fg_id,""+timestamp},null,null,null);

		if(c.getCount()>1){
			SamLog.e(TAG, "Fatal Error for query CommenterRecord");
			throw new RuntimeException("Fatal Error for query CommenterRecord!");
		}

		while(c.moveToNext()){
			record = new CommenterRecord();
			record.id = c.getLong(c.getColumnIndex("id"));
			record.commenter_phonenumber= c.getString(c.getColumnIndex("commenter_phonenumber"));
			record.content = c.getString(c.getColumnIndex("content"));
			record.fg_id = c.getLong(c.getColumnIndex("fg_id"));
			record.timestamp = c.getLong(c.getColumnIndex("timestamp"));
		}

		c.close();

		return record;
	}

	public List<CommenterRecord> queryCommenterRecord(long fg_id){
		String table = DatabaseHelper.TABLE_NAME_COMMENTER;
		/*
		id(primary) |commenter_phonenumber | content | fg_id |timestamp
		*/

		CommenterRecord record = null;

		Cursor c = db.query(table,null,"fg_id=?",new String[]{""+fg_id},null,null,"timestamp desc");
		List<CommenterRecord> RecordArray = new ArrayList<CommenterRecord>();
		while(c.moveToNext()){
			record = new CommenterRecord();
			record.id = c.getLong(c.getColumnIndex("id"));
			record.commenter_phonenumber= c.getString(c.getColumnIndex("commenter_phonenumber"));
			record.content = c.getString(c.getColumnIndex("content"));
			record.fg_id = c.getLong(c.getColumnIndex("fg_id"));
			record.timestamp = c.getLong(c.getColumnIndex("timestamp"));
			RecordArray.add(record);
		}

		c.close();

		return RecordArray;
	}

	public long addPictureRecord(PictureRecord record)
	{
		String table = DatabaseHelper.TABLE_NAME_PICTURE;
		/*
		id(primary) |fg_id | thumbnail_pic | original_pic | url_thumbnail | url_original |sequence
		*/
		ContentValues cv = new ContentValues();
		cv.put("fg_id",record.fg_id);
		cv.put("thumbnail_pic",record.thumbnail_pic);
		cv.put("original_pic",record.original_pic);
		cv.put("url_thumbnail",record.url_thumbnail);
		cv.put("url_original",record.url_original);
		cv.put("sequence",record.sequence);
		
		return db.insert(table,null,cv);
	}

	
	public long updatePictureRecord(long id, PictureRecord record)
	{
		String table = DatabaseHelper.TABLE_NAME_PICTURE;
		/*
		id(primary) |fg_id | thumbnail_pic | original_pic | url_thumbnail | url_original |sequence
		*/
		ContentValues cv = new ContentValues();
		cv.put("fg_id",record.fg_id);
		cv.put("thumbnail_pic",record.thumbnail_pic);
		cv.put("original_pic",record.original_pic);
		cv.put("url_thumbnail",record.url_thumbnail);
		cv.put("url_original",record.url_original);
		cv.put("sequence",record.sequence);
		
		String whereClause = "id=?";
		String [] whereArgs = {""+id+""};

		return db.update(table,cv,whereClause,whereArgs);
	}

	

	public long updatePictureRecord_original(long fg_id, String original_pic,int sequence)
	{
		String table = DatabaseHelper.TABLE_NAME_PICTURE;
		/*
		id(primary) |fg_id | thumbnail_pic | original_pic | url_thumbnail | url_original |sequence
		*/
		ContentValues cv = new ContentValues();
		cv.put("original_pic",original_pic);
		
		String whereClause = "fg_id=? and sequence=? ";
		String [] whereArgs = {""+fg_id+"",""+sequence};

		return db.update(table,cv,whereClause,whereArgs);
	}

	public long updatePictureRecord_thumbnail(long fg_id, String thumbnail_pic,int sequence)
	{
		String table = DatabaseHelper.TABLE_NAME_PICTURE;
		/*
		id(primary) |fg_id | thumbnail_pic | original_pic | url_thumbnail | url_original |sequence
		*/
		ContentValues cv = new ContentValues();
		cv.put("thumbnail_pic",thumbnail_pic);
		
		String whereClause = "fg_id=? and sequence=? ";
		String [] whereArgs = {""+fg_id+"",""+sequence};

		return db.update(table,cv,whereClause,whereArgs);
	}

	public List<PictureRecord> queryPictureRecord(long fg_id){
		String table = DatabaseHelper.TABLE_NAME_PICTURE;
		/*
		id(primary) |fg_id | thumbnail_pic | original_pic | url_thumbnail | url_original |sequence
		*/
		PictureRecord record = null;

		Cursor c = db.query(table,null,"fg_id=? ",new String[]{""+fg_id},null,null,null);

		List<PictureRecord> RecordArray = new ArrayList<PictureRecord>();
		while(c.moveToNext()){
			record = new PictureRecord();
			record.id = c.getLong(c.getColumnIndex("id"));
			record.fg_id = c.getLong(c.getColumnIndex("fg_id"));
			record.thumbnail_pic = c.getString(c.getColumnIndex("thumbnail_pic"));
			record.original_pic = c.getString(c.getColumnIndex("original_pic"));
			record.url_thumbnail = c.getString(c.getColumnIndex("url_thumbnail"));
			record.url_original = c.getString(c.getColumnIndex("url_original"));
			record.sequence = c.getInt(c.getColumnIndex("sequence")); 
			RecordArray.add(record);
		}

		c.close();

		return RecordArray;
	}

	public PictureRecord queryPictureRecord(long fg_id,String url_thumbnail){
		String table = DatabaseHelper.TABLE_NAME_PICTURE;
		/*
		id(primary) |fg_id | thumbnail_pic | original_pic | url_thumbnail | url_original |sequence
		*/
		PictureRecord record = null;

		Cursor c = db.query(table,null,"fg_id=? and url_thumbnail=?",new String[]{""+fg_id,url_thumbnail},null,null,null);

		if(c.getCount()>1){
			SamLog.e(TAG, "Fatal Error for query PictureRecord");
			throw new RuntimeException("Fatal Error for query PictureRecord!");
		}

		while(c.moveToNext()){
			record = new PictureRecord();
			record.id = c.getLong(c.getColumnIndex("id"));
			record.fg_id = c.getLong(c.getColumnIndex("fg_id"));
			record.thumbnail_pic = c.getString(c.getColumnIndex("thumbnail_pic"));
			record.original_pic = c.getString(c.getColumnIndex("original_pic"));
			record.url_thumbnail = c.getString(c.getColumnIndex("url_thumbnail"));
			record.url_original = c.getString(c.getColumnIndex("url_original"));
			record.sequence = c.getInt(c.getColumnIndex("sequence")); 
		}

		c.close();

		return record;
	}

	public PictureRecord queryPictureRecord_thumbnail_pic(long fg_id,String thumbnail_pic){
		String table = DatabaseHelper.TABLE_NAME_PICTURE;
		/*
		id(primary) |fg_id | thumbnail_pic | original_pic | url_thumbnail | url_original |sequence
		*/
		PictureRecord record = null;

		Cursor c = db.query(table,null,"fg_id=? and thumbnail_pic=?",new String[]{""+fg_id,thumbnail_pic},null,null,null);

		if(c.getCount()>1){
			SamLog.e(TAG, "Fatal Error for query PictureRecord thumbnail_pic");
			throw new RuntimeException("Fatal Error for query PictureRecord thumbnail_pic!");
		}

		while(c.moveToNext()){
			record = new PictureRecord();
			record.id = c.getLong(c.getColumnIndex("id"));
			record.fg_id = c.getLong(c.getColumnIndex("fg_id"));
			record.thumbnail_pic = c.getString(c.getColumnIndex("thumbnail_pic"));
			record.original_pic = c.getString(c.getColumnIndex("original_pic"));
			record.url_thumbnail = c.getString(c.getColumnIndex("url_thumbnail"));
			record.url_original = c.getString(c.getColumnIndex("url_original"));
			record.sequence = c.getInt(c.getColumnIndex("sequence")); 
		}

		c.close();

		return record;
	}



	public long addFollowerRecord(FollowerRecord record)
	{
		String table = DatabaseHelper.TABLE_NAME_FOLLOWER;

		ContentValues cv = new ContentValues();
		cv.put("unique_id",record.unique_id);
		cv.put("username",record.username);
		cv.put("owner_unique_id",record.owner_unique_id);
		
		return db.insert(table,null,cv);
	}

	public long updateFollowerRecord(long id, FollowerRecord record)
	{
		String table = DatabaseHelper.TABLE_NAME_FOLLOWER;

		ContentValues cv = new ContentValues();
		cv.put("unique_id",record.unique_id);
		cv.put("username",record.username);
		cv.put("owner_unique_id",record.owner_unique_id);
		

		String whereClause = "id=?";
		String [] whereArgs = {""+id+""};

		return db.update(table,cv,whereClause,whereArgs);
	}

	public List<FollowerRecord> queryFollowerRecord(long owner_unique_id){
		String table = DatabaseHelper.TABLE_NAME_FOLLOWER ;

		List<FollowerRecord> recordArray = new ArrayList<FollowerRecord>();
		FollowerRecord record = null;

		Cursor c = db.query(table,null,"owner_unique_id=?",new String[]{""+owner_unique_id},null,null,null);

		while(c.moveToNext()){
			record = new FollowerRecord();
			record.id = c.getLong(c.getColumnIndex("id"));
			record.unique_id = c.getLong(c.getColumnIndex("unique_id"));
			record.username= c.getString(c.getColumnIndex("username"));
			record.owner_unique_id = c.getLong(c.getColumnIndex("owner_unique_id"));
			recordArray.add(record);
		}

		c.close();

		return recordArray;
	}

	public FollowerRecord queryFollowerRecord(long unique_id,long owner_unique_id){
		String table = DatabaseHelper.TABLE_NAME_FOLLOWER ;

		FollowerRecord record = null;

		Cursor c = db.query(table,null,"unique_id=? and owner_unique_id=?",new String[]{""+unique_id,""+owner_unique_id},null,null,null);

		if(c.getCount()>1){
			SamLog.e(TAG, "Fatal Error for query follower");
			throw new RuntimeException("Fatal Error for query follower!");
		}

		while(c.moveToNext()){
			record = new FollowerRecord();
			record.id = c.getLong(c.getColumnIndex("id"));
			record.unique_id = c.getLong(c.getColumnIndex("unique_id"));
			record.username= c.getString(c.getColumnIndex("username"));
			record.owner_unique_id = c.getLong(c.getColumnIndex("owner_unique_id"));
		}

		c.close();

		return record;
	}

	public FollowerRecord queryFollowerRecordByUsername(String username,long owner_unique_id){
		String table = DatabaseHelper.TABLE_NAME_FOLLOWER ;

		FollowerRecord record = null;

		Cursor c = db.query(table,null,"username=? and owner_unique_id=?",new String[]{username,""+owner_unique_id},null,null,null);

		if(c.getCount()>1){
			SamLog.e(TAG, "Fatal Error for query follower by name");
			throw new RuntimeException("Fatal Error for query follower by name!");
		}

		while(c.moveToNext()){
			record = new FollowerRecord();
			record.id = c.getLong(c.getColumnIndex("id"));
			record.unique_id = c.getLong(c.getColumnIndex("unique_id"));
			record.username= c.getString(c.getColumnIndex("username"));
			record.owner_unique_id = c.getLong(c.getColumnIndex("owner_unique_id"));
		}

		c.close();

		return record;
	}

	public void clearFollowerTable(){
		String table = DatabaseHelper.TABLE_NAME_FOLLOWER ;
		db.delete(table, null, null);
	}

	public void deleteFollower(long unique_id,long owner_unique_id){
		String table = DatabaseHelper.TABLE_NAME_FOLLOWER ;

		db.delete(table, "unique_id=? and owner_unique_id=?", new String[]{""+unique_id,""+owner_unique_id});
	}

	public void deleteFollowerByUsername(String username,long owner_unique_id){
		String table = DatabaseHelper.TABLE_NAME_FOLLOWER ;

		db.delete(table, "username=? and owner_unique_id=?", new String[]{username,""+owner_unique_id});
	}




	public long addPublicInfo(PublicInfo record)
	{
		String table = DatabaseHelper.TABLE_NAME_PUBLIC_INFO;
		//id(primary) | owner_unique_id | cmplogo | cmpwebsite | cmpname | cmpdesc | cmpphone
		ContentValues cv = new ContentValues();
		cv.put("owner_unique_id",record.owner_unique_id);
		cv.put("cmplogo",record.cmplogo);
		cv.put("cmpwebsite",record.cmpwebsite);
		cv.put("cmpname",record.cmpname);
		cv.put("cmpdesc",record.cmpdesc);
		cv.put("cmpphone",record.cmpphone);
		
		return db.insert(table,null,cv);
	}

	public long updatePublicInfo(long id, PublicInfo record)
	{
		String table = DatabaseHelper.TABLE_NAME_PUBLIC_INFO;

		ContentValues cv = new ContentValues();
		cv.put("owner_unique_id",record.owner_unique_id);
		cv.put("cmplogo",record.cmplogo);
		cv.put("cmpwebsite",record.cmpwebsite);
		cv.put("cmpname",record.cmpname);
		cv.put("cmpdesc",record.cmpdesc);
		cv.put("cmpphone",record.cmpphone);
		

		String whereClause = "id=?";
		String [] whereArgs = {""+id+""};

		return db.update(table,cv,whereClause,whereArgs);
	}

	public PublicInfo queryPublicInfo(long owner_unique_id){
		String table = DatabaseHelper.TABLE_NAME_PUBLIC_INFO ;

		PublicInfo record = null;

		Cursor c = db.query(table,null,"owner_unique_id=?",new String[]{""+owner_unique_id},null,null,null);

		if(c.getCount()>1){
			SamLog.e(TAG, "Fatal Error for query public by name");
			throw new RuntimeException("Fatal Error for query public by name!");
		}

		while(c.moveToNext()){
			record = new PublicInfo();
			record.id = c.getLong(c.getColumnIndex("id"));
			record.owner_unique_id = c.getLong(c.getColumnIndex("owner_unique_id"));
			record.cmplogo= c.getString(c.getColumnIndex("cmplogo"));
			record.cmpwebsite = c.getString(c.getColumnIndex("cmpwebsite"));
			record.cmpname = c.getString(c.getColumnIndex("cmpname"));
			record.cmpdesc = c.getString(c.getColumnIndex("cmpdesc"));
			record.cmpphone = c.getString(c.getColumnIndex("cmpphone"));
		}

		c.close();

		return record;
	}



    /**
     * close database
     */
    public void closeDB()
    {
        db.close();
    }

}
