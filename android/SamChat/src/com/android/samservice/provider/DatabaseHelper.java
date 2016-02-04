package com.android.samservice.provider;

import com.android.samservice.SamLog;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;



public class DatabaseHelper extends SQLiteOpenHelper
{
	public static final String TAG = "DatabaseHelper";
	private static final int DATABASE_VERSION = 1;

	private static final String DATABASE_NAME = "SamDB.db";
	public static final String TABLE_NAME_LOGIN_USER = "LoginUserTable";
	/*
	id(primary) | status | username | phonenumber |password |usertype | image file |description |login time|logout time | unique_id | easemob_username |easemob_status |lastupdate
	*/
	
	public static final String TABLE_NAME_CONTACT_USER = "ContactUserTable";
	/*
	id(primary) | username | phonenumber | imagefile |description | unique_id | easemob_username | lastupdate
	*/
	
	public static final String TABLE_NAME_SEND_QUESTION = "SendQuestionTable";
	/*
	id(primary) |question_id | send user id | question | status | send time | cancel time | 
	*/
	public static final String TABLE_NAME_RECEIVED_ANSWER = "ReceivedAnswerTable";
	/*
	id(primary) |question_id | answer |contact user id | received time 
	*/

	public static final String TABLE_NAME_RECEIVED_QUESTION = "ReceivedQuestionTable";

	
	/*
	id(primary) |question_id | answer |status | loginuserid | sendtime 
	*/
	public static final String TABLE_NAME_SEND_ANSWER = "SendAnswerTable";

	/*
	id(primary) |sender | receiver | status |reason
	*/
	public static final String TABLE_CONTACT_INVITE_RECORD = "InviteMsgTable";

	/*
	id(primary) |user | friend
	*/
	public static final String TABLE_NAME_USER_FRIEND = "UserFriendTable";

	/*
	id(primary) |phonenumber | avatarname
	*/
	public static final String TABLE_NAME_AVATAR = "AvatarTable";

    // ���캯�������ø���SQLiteOpenHelper�Ĺ��캯��
    public DatabaseHelper(Context context, String name, CursorFactory factory,
            int version, DatabaseErrorHandler errorHandler)
    {
        super(context, name, factory, version, errorHandler);

    }

    public DatabaseHelper(Context context, String name, CursorFactory factory,
            int version)
    {
        super(context, name, factory, version);
        // SQLiteOpenHelper�Ĺ��캯��������
        // context�������Ļ���
        // name�����ݿ�����
        // factory���α깤������ѡ��
        // version�����ݿ�ģ�Ͱ汾��
    }

    public DatabaseHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }

	private void createLoginUserTable(SQLiteDatabase db){
	/*
		id(primary) | status | username | phonenumber |password |usertype | image file |description |login time|logout time | unique_id | easemob_username |easemob_status |lastupdate
	*/
		StringBuffer sBuffer = new StringBuffer();
		sBuffer.append("CREATE TABLE [" + TABLE_NAME_LOGIN_USER + "] (");
        	sBuffer.append("[id] INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, ");
		sBuffer.append("[status] INTEGER, ");	//0: no login  1:login
        	sBuffer.append("[username] TEXT,");
       	sBuffer.append("[phonenumber] TEXT,");
        	sBuffer.append("[password] TEXT,");
		sBuffer.append("[usertype] INTEGER,");//0:noraml user 1:sam  2:sam team
		sBuffer.append("[imagefile] TEXT,");
		sBuffer.append("[description] TEXT,");
		sBuffer.append("[logintime] INTEGER,"); //System.getCurrentMilliseconds
		sBuffer.append("[logouttime] INTEGER,"); //System.getCurrentMilliseconds
		sBuffer.append("[unique_id] INTEGER,"); 
		sBuffer.append("[easemob_username] TEXT,"); //null: easemob history 
		sBuffer.append("[easemob_status] INTEGER,"); //0:no login 1:login
		sBuffer.append("[lastupdate] INTEGER )"); //last user info udate time recroded in server side
		// ִ�д������SQL���
        	db.execSQL(sBuffer.toString());
	}

	private void createContactUserTable(SQLiteDatabase db){
	/*
		id(primary) | username | phonenumber | usertype | imagefile |description | unique_id | easemob_username |lastupdate
	*/
		StringBuffer sBuffer = new StringBuffer();
		sBuffer.append("CREATE TABLE [" + TABLE_NAME_CONTACT_USER + "] (");
        	sBuffer.append("[id] INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, ");
        	sBuffer.append("[username] TEXT,");
       	sBuffer.append("[phonenumber] TEXT,");
		sBuffer.append("[usertype] INTEGER,");//0:noraml user 1:sam  2:sam team
		sBuffer.append("[imagefile] TEXT,");
		sBuffer.append("[description] TEXT,");
		sBuffer.append("[unique_id] INTEGER,");
		sBuffer.append("[easemob_username] TEXT,");
		sBuffer.append("[lastupdate] TEXT )");
		
		// ִ�д������SQL���
        	db.execSQL(sBuffer.toString());
	}

	private void createSendQuestionTable(SQLiteDatabase db){
	/*
		id(primary) |question_id | send user id | question | status | send time | cancel time | 
	*/
		StringBuffer sBuffer = new StringBuffer();
		sBuffer.append("CREATE TABLE [" + TABLE_NAME_SEND_QUESTION + "] (");
		sBuffer.append("[id] INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, ");
        	sBuffer.append("[question_id] TEXT, ");
        	sBuffer.append("[senduserid] INTEGER,");
       	sBuffer.append("[question] TEXT,");
		sBuffer.append("[status] INTEGER,");//0:cancel 1:active
		sBuffer.append("[sendtime] INTEGER,");
		sBuffer.append("[canceltime] INTEGER )");
		// ִ�д������SQL���
        	db.execSQL(sBuffer.toString());
	}

	private void createReceviedAnswerTable(SQLiteDatabase db){
	/*
		id(primary) |question_id | contact user id |answer | received time 
	*/
		StringBuffer sBuffer = new StringBuffer();
		sBuffer.append("CREATE TABLE [" + TABLE_NAME_RECEIVED_ANSWER + "] (");
		sBuffer.append("[id] INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, ");
        	sBuffer.append("[question_id] TEXT, ");
        	sBuffer.append("[contactuserid] INTEGER,");
       	sBuffer.append("[answer] TEXT,");	
		sBuffer.append("[receivedtime] INTEGER )");
		// ִ�д������SQL���
        	db.execSQL(sBuffer.toString());
	}

	private void createReceivedQuestionTableForServicer(SQLiteDatabase db){
	/*
		id(primary) |question_id | question |contact user id | status | shown |received time | canceled time |loginuserid
	*/
		StringBuffer sBuffer = new StringBuffer();
		sBuffer.append("CREATE TABLE [" + TABLE_NAME_RECEIVED_QUESTION + "] (");
		sBuffer.append("[id] INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, ");
        	sBuffer.append("[question_id] TEXT, ");
		sBuffer.append("[question] TEXT,");
        	sBuffer.append("[contactuserid] INTEGER,");
		sBuffer.append("[status] INTEGER, ");
		sBuffer.append("[shown] INTEGER,");
		sBuffer.append("[receivedtime] INTEGER,");
		sBuffer.append("[canceledtime] INTEGER,");
		sBuffer.append("[receivercellphone] TEXT)");
		// ִ�д������SQL���
        	db.execSQL(sBuffer.toString());
		
	}

	private void createSendAnswerTableForServicer(SQLiteDatabase db){
	/*
	id(primary) |question_id | answer |status | loginuserid | sendtime 
*/
		StringBuffer sBuffer = new StringBuffer();
		sBuffer.append("CREATE TABLE [" + TABLE_NAME_SEND_ANSWER + "] (");
		sBuffer.append("[id] INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, ");
        	sBuffer.append("[question_id] TEXT, ");
		sBuffer.append("[answer] TEXT,");
		sBuffer.append("[status] INTEGER,");
        	sBuffer.append("[loginuserid] INTEGER,");
		sBuffer.append("[sendtime] INTEGER )");
		// ִ�д������SQL���
        	db.execSQL(sBuffer.toString());
	}

	
	private void createInviteMsgTable(SQLiteDatabase db){
	/*
	id(primary) |sender | receiver | status |reason | time
	*/
		StringBuffer sBuffer = new StringBuffer();
		sBuffer.append("CREATE TABLE [" + TABLE_CONTACT_INVITE_RECORD + "] (");
		sBuffer.append("[id] INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, ");
        	sBuffer.append("[sender] TEXT, ");
		sBuffer.append("[receiver] TEXT,");
		sBuffer.append("[status] INTEGER,");
		sBuffer.append("[reason] TEXT,");
		sBuffer.append("[time] INTEGER )");
		// ִ�д������SQL���
        	db.execSQL(sBuffer.toString());
	}

	private void createUserFriendTable(SQLiteDatabase db){
	/*
	id(primary) |user | friend
	*/
		StringBuffer sBuffer = new StringBuffer();
		sBuffer.append("CREATE TABLE [" + TABLE_NAME_USER_FRIEND+ "] (");
		sBuffer.append("[id] INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, ");
        	sBuffer.append("[user] TEXT, ");
		sBuffer.append("[friend] TEXT )");
		// ִ�д������SQL���
        	db.execSQL(sBuffer.toString());
	}

	private void createAvatarTable(SQLiteDatabase db){
	/*
	id(primary) |phonenumber | avatarname |nickname
	*/
		StringBuffer sBuffer = new StringBuffer();
		sBuffer.append("CREATE TABLE [" + TABLE_NAME_AVATAR+ "] (");
		sBuffer.append("[id] INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, ");
        	sBuffer.append("[phonenumber] TEXT, ");
		sBuffer.append("[avatarname] TEXT,");
		sBuffer.append("[nickname] TEXT )");
		// ִ�д������SQL���
        	db.execSQL(sBuffer.toString());
	}


    // �̳�SQLiteOpenHelper��,����Ҫ��д������������onCreate(),onUpgrade(),onOpen()
    @Override
    public void onCreate(SQLiteDatabase db)
    {
    	SamLog.e(TAG,"onCreate");
    	createLoginUserTable(db);
    	createContactUserTable(db);
    	createSendQuestionTable(db);
    	createReceviedAnswerTable(db);
    	createReceivedQuestionTableForServicer(db);
    	createSendAnswerTableForServicer(db);
    	createInviteMsgTable(db);
    	createUserFriendTable(db);
    	createAvatarTable(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
	db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_LOGIN_USER);
	db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_CONTACT_USER);
	db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_SEND_QUESTION);
	db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_RECEIVED_ANSWER);
	db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_RECEIVED_QUESTION);
	db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_SEND_ANSWER);
	db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACT_INVITE_RECORD);
	db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_USER_FRIEND);
	db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_AVATAR);
	onCreate(db);
    }

    @Override
    public void onOpen(SQLiteDatabase db)
    {
        super.onOpen(db);
    }

}
