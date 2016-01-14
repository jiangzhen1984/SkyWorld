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
		id(primary) | status | username | phonenumber |password |usertype | image file |description |login time|logout time | unique_id | easemob_username |easemob_status
		*/
		ContentValues cv = new ContentValues();
		cv.put("status",user.status);
		cv.put("username",user.username);
		cv.put("phonenumber",user.phonenumber);
		cv.put("password",user.password);
		cv.put("usertype",user.usertype);
		cv.put("imagefile",user.imagefile);
		cv.put("description",user.description);
		cv.put("logintime",user.logintime);
		cv.put("logouttime",user.logouttime);
		cv.put("unique_id",user.unique_id);
		cv.put("easemob_username",user.easemob_username);
		cv.put("easemob_status",user.easemob_status);

		return db.insert(table,null,cv);
		
	}

	public long updateLogInUser(long id, LoginUser user)
	{
		//DatabaseHelper:TABLE_NAME_LOGIN_USER
		String table = DatabaseHelper.TABLE_NAME_LOGIN_USER;
		/*
		id(primary) | status | username | phone number |password |user type | image file |description |login time|logout time
		*/
		ContentValues cv = new ContentValues();
		cv.put("status",user.status);
		cv.put("username",user.username);
		cv.put("phonenumber",user.phonenumber);
		cv.put("password",user.password);
		cv.put("usertype",user.usertype);
		cv.put("imagefile",user.imagefile);
		cv.put("description",user.description);
		cv.put("logintime",user.logintime);
		cv.put("logouttime",user.logouttime);
		cv.put("unique_id",user.unique_id);
		cv.put("easemob_username",user.easemob_username);
		cv.put("easemob_status",user.easemob_status);

		String whereClause = "id=?";
		String [] whereArgs = {""+id+""};

		return db.update(table,cv,whereClause,whereArgs);
		
	}

	public LoginUser queryLogInUser(String phonenumber){
		String table = DatabaseHelper.TABLE_NAME_LOGIN_USER;
		LoginUser user = null;
		Cursor c = db.query(table,null,"phonenumber=?",new String[]{phonenumber},null,null,null);

		if(c.getCount()>1){
			SamLog.e(TAG, "Fatal Error for query login user");
		}
		
		while(c.moveToNext()){
			user = new LoginUser();
			user.id = c.getLong(c.getColumnIndex("id"));
			user.status = c.getInt(c.getColumnIndex("status"));
			user.username = c.getString(c.getColumnIndex("username"));
			user.phonenumber = c.getString(c.getColumnIndex("phonenumber"));
			user.password = c.getString(c.getColumnIndex("password"));
 			user.usertype = c.getInt(c.getColumnIndex("usertype"));
			user.imagefile = c.getString(c.getColumnIndex("imagefile"));
			user.description = c.getString(c.getColumnIndex("description"));
 			user.logintime = c.getLong(c.getColumnIndex("logintime"));
			user.logouttime = c.getLong(c.getColumnIndex("logouttime"));
			user.unique_id = c.getLong(c.getColumnIndex("unique_id"));
			user.easemob_username = c.getString(c.getColumnIndex("easemob_username"));
			user.easemob_status = c.getInt(c.getColumnIndex("easemob_status"));
		}

		c.close();

		return user;
	}

	public LoginUser queryLogInUser(){
		String table = DatabaseHelper.TABLE_NAME_LOGIN_USER;
		LoginUser user = null;
		Cursor c = db.query(table,null,"status=?",new String[]{""+LoginUser.ACTIVE+""},null,null,null);

		if(c.getCount()>1){
			SamLog.e(TAG, "Fatal Error for query login user");
		}
		
		while(c.moveToNext()){
			user = new LoginUser();
			user.id = c.getLong(c.getColumnIndex("id"));
			user.status = c.getInt(c.getColumnIndex("status"));
			user.username = c.getString(c.getColumnIndex("username"));
			user.phonenumber = c.getString(c.getColumnIndex("phonenumber"));
			user.password = c.getString(c.getColumnIndex("password"));
 			user.usertype = c.getInt(c.getColumnIndex("usertype"));
			user.imagefile = c.getString(c.getColumnIndex("imagefile"));
			user.description = c.getString(c.getColumnIndex("description"));
 			user.logintime = c.getLong(c.getColumnIndex("logintime"));
			user.logouttime = c.getLong(c.getColumnIndex("logouttime"));
			user.unique_id = c.getLong(c.getColumnIndex("unique_id"));
			user.easemob_username = c.getString(c.getColumnIndex("easemob_username"));
			user.easemob_status = c.getInt(c.getColumnIndex("easemob_status"));
		}

		c.close();

		return user;
	}

	public long addContactUser(ContactUser user)
	{
		//DatabaseHelper:TABLE_NAME_CONTACT_USER
		String table = DatabaseHelper.TABLE_NAME_CONTACT_USER;
		/*
		id(primary) | username | phonenumber | usertype | imagefile |description | unique_id | easemob_username
		*/
		ContentValues cv = new ContentValues();
		cv.put("username",user.username);
		cv.put("phonenumber",user.phonenumber);
		cv.put("usertype",user.usertype);
		cv.put("imagefile",user.imagefile);
		cv.put("description",user.description);
		cv.put("unique_id",user.unique_id);
		cv.put("easemob_username",user.easemob_username);

		return db.insert(table,null,cv);
		
	}

	public long updateContactUser(long id, ContactUser user)
	{
		//DatabaseHelper:TABLE_NAME_LOGIN_USER
		String table = DatabaseHelper.TABLE_NAME_CONTACT_USER;
		/*
		id(primary) | username | phonenumber | usertype | imagefile |description | unique_id | easemob_username
		*/
		ContentValues cv = new ContentValues();
		cv.put("username",user.username);
		cv.put("phonenumber",user.phonenumber);
		cv.put("usertype",user.usertype);
		cv.put("imagefile",user.imagefile);
		cv.put("description",user.description);
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
		}
		
		while(c.moveToNext()){
			user = new ContactUser();
			user.id = c.getLong(c.getColumnIndex("id"));
			user.username = c.getString(c.getColumnIndex("username"));
			user.phonenumber = c.getString(c.getColumnIndex("phonenumber"));
 			user.usertype = c.getInt(c.getColumnIndex("usertype"));
			user.imagefile = c.getString(c.getColumnIndex("imagefile"));
			user.description = c.getString(c.getColumnIndex("description"));
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
		}
		
		while(c.moveToNext()){
			user = new ContactUser();
			user.id = c.getLong(c.getColumnIndex("id"));
			user.username = c.getString(c.getColumnIndex("username"));
			user.phonenumber = c.getString(c.getColumnIndex("phonenumber"));
 			user.usertype = c.getInt(c.getColumnIndex("usertype"));
			user.imagefile = c.getString(c.getColumnIndex("imagefile"));
			user.description = c.getString(c.getColumnIndex("description"));
			user.unique_id = c.getLong(c.getColumnIndex("unique_id"));
			user.easemob_username = c.getString(c.getColumnIndex("easemob_username"));
		}

		c.close();

		return user;
	}



  	public long addSendQuestion(SendQuestion question)
	{
		String table = DatabaseHelper.TABLE_NAME_SEND_QUESTION;
		/*
		id(primary) |question_id | send user id | question | status | send time | cancel time | 
		*/
		ContentValues cv = new ContentValues();
		cv.put("question_id",question.question_id);
		cv.put("senduserid",question.senduserid);
		cv.put("question",question.question);
		cv.put("status",question.status);
		cv.put("sendtime",question.sendtime);
		cv.put("canceltime",question.canceltime);

		return db.insert(table,null,cv);
		
	}

	public long updateSendQuestion(long id, SendQuestion question)
	{
		String table = DatabaseHelper.TABLE_NAME_SEND_QUESTION;
		/*
		id(primary) |question_id | send user id | question | status | send time | cancel time | 
		*/
		ContentValues cv = new ContentValues();
		cv.put("question_id",question.question_id);
		cv.put("senduserid",question.senduserid);
		cv.put("question",question.question);
		cv.put("status",question.status);
		cv.put("sendtime",question.sendtime);
		cv.put("canceltime",question.canceltime);

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

	public ArrayList<ReceivedAnswer> queryReceivedAnswer(String question_id){
		String table = DatabaseHelper.TABLE_NAME_RECEIVED_ANSWER ;
		/*
		id(primary) |question_id | answer |contact user id | received time 
		*/

		ArrayList<ReceivedAnswer> answerArray = new ArrayList<ReceivedAnswer>();

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
		/*
		id(primary) |question_id | question |contact user id | status | shown | received time | canceled time
		*/
		ContentValues cv = new ContentValues();
		cv.put("question_id",question.question_id);
		cv.put("question",question.question);
		cv.put("contactuserid",question.contactuserid);
		cv.put("status",question.status );
		cv.put("shown",question.shown );
		cv.put("receivedtime",question.receivedtime );
		cv.put("canceledtime",question.canceledtime );
		
		return db.insert(table,null,cv);
		
	}

	public long updateReceivedQuestion(long id, ReceivedQuestion question)
	{
		String table = DatabaseHelper.TABLE_NAME_RECEIVED_QUESTION;
		/*
		id(primary) |question_id | question |contact user id | status | received time | canceled time
		*/
		ContentValues cv = new ContentValues();
		cv.put("question_id",question.question_id);
		cv.put("question",question.question);
		cv.put("contactuserid",question.contactuserid);
		cv.put("status",question.status );
		cv.put("shown",question.shown );
		cv.put("receivedtime",question.receivedtime );
		cv.put("canceledtime",question.canceledtime );

		String whereClause = "id=?";
		String [] whereArgs = {""+id+""};

		return db.update(table,cv,whereClause,whereArgs);
		
	}

	public ReceivedQuestion queryReceivedQuestion(String question_id){
		String table = DatabaseHelper.TABLE_NAME_RECEIVED_QUESTION ;
		/*
		id(primary) |question_id | question |contact user id | status | shown |received time | canceled time
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
			question.shown = c.getInt(c.getColumnIndex("shown"));
			question.receivedtime  = c.getLong(c.getColumnIndex("receivedtime"));
			question.canceledtime =  c.getLong(c.getColumnIndex("canceledtime"));
		}

		c.close();

		return question;
	}


	private long fetchPlacesCount(String table) {
		String sql = "SELECT COUNT(*) FROM " + table;
		SQLiteStatement statement = db.compileStatement(sql);
		long count = statement.simpleQueryForLong();
		return count;
	}

	public ArrayList<ReceivedQuestion> queryRecentReceivedQuestion(long num){
		String table = DatabaseHelper.TABLE_NAME_RECEIVED_QUESTION ;
		long start_id = 1;
		long count = fetchPlacesCount(table);
		if(count <= num){
			start_id = 1;
		}else{
			start_id = count - num + 1;
		}
		/*
		id(primary) |question_id | question |contact user id | status | shown |received time | canceled time
		*/

		ArrayList<ReceivedQuestion> ReceivedQuestionArray = new ArrayList<ReceivedQuestion>();
		ReceivedQuestion question = null;

		Cursor c = db.query(table,null,"id>=?",new String[]{""+start_id+""},null,null,null);
		while(c.moveToNext()){
			question = new ReceivedQuestion();
			question.id = c.getLong(c.getColumnIndex("id"));
			question.question_id = c.getString(c.getColumnIndex("question_id"));
 			question.question = c.getString(c.getColumnIndex("question"));
			question.contactuserid= c.getLong(c.getColumnIndex("contactuserid"));
			question.status = c.getInt(c.getColumnIndex("status"));
			question.shown = c.getInt(c.getColumnIndex("shown"));
			question.receivedtime  = c.getLong(c.getColumnIndex("receivedtime"));
			question.canceledtime =  c.getLong(c.getColumnIndex("canceledtime"));

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

	public ArrayList<SendAnswer> querySendAnswer(String question_id){
		String table = DatabaseHelper.TABLE_NAME_SEND_ANSWER ;

		/*
			id(primary) |question_id | answer |status | loginuserid | sendtime 
		*/

		ArrayList<SendAnswer> sendAnswerArray = new ArrayList<SendAnswer>();
		SendAnswer answer=null;

		Cursor c = db.query(table,null,"question_id=?",new String[]{question_id},null,null,null);
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

    /**
     * close database
     */
    public void closeDB()
    {
        db.close();
    }

}
