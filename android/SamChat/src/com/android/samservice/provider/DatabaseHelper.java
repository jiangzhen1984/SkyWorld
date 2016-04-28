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
	/*
	id(primary) | status | username | countrycode|phonenumber |password |usertype | image file |description |area | location |login time|logout time | unique_id | easemob_username |easemob_status |lastupdate|conversation_existed
	*/
	public static final String TABLE_NAME_LOGIN_USER = "LoginUserTable";
	
	/*
	id(primary) | username | phonenumber | imagefile |description |area | location | unique_id | easemob_username | lastupdate
	*/
	public static final String TABLE_NAME_CONTACT_USER = "ContactUserTable";
	
	/*
	id(primary) |question_id | send user id | question | status | send time | cancel time | sendercellphone | senderusername
	*/
	public static final String TABLE_NAME_SEND_QUESTION = "SendQuestionTable";

	/*
		id(primary) |question_id | contact user id |answer | received time 
	*/
	public static final String TABLE_NAME_RECEIVED_ANSWER = "ReceivedAnswerTable";
	
	/*
	id(primary) |question_id | question |contact user id | status | response |received time | canceled time |receivercellphone |receiverusername
	*/
	public static final String TABLE_NAME_RECEIVED_QUESTION = "ReceivedQuestionTable";

	
	/*
	id(primary) |question_id | answer |status | loginuserid | sendtime 
	*/
	public static final String TABLE_NAME_SEND_ANSWER = "SendAnswerTable";

	/*
	id(primary) |sender | receiver | status |reason |time
	*/
	public static final String TABLE_CONTACT_INVITE_RECORD = "InviteMsgTable";

	/*
	id(primary) |user | friend
	*/
	public static final String TABLE_NAME_USER_FRIEND = "UserFriendTable";

	/*
	id(primary) |phonenumber | avatarname |nickname
	*/
	public static final String TABLE_NAME_AVATAR = "AvatarTable";

	/*
	id(primary) |timestamp | fg_id |status |comment | publisher_phonenumber | publisher_username | owner_phonenumber | owner_username
	*/
	public static final String TABLE_NAME_FG = "FGTable";

	/*
	id(primary) |recommander_phonenumber | fg_id |timestamp
	*/
	public static final String TABLE_NAME_RECOMMANDER = "RecommanderTable";

	/*
	id(primary) |commenter_phonenumber | content | fg_id |timestamp
	*/
	public static final String TABLE_NAME_COMMENTER = "CommenterTable";

	/*
	id(primary) |thumbnail_pic | original_pic
	*/
	public static final String TABLE_NAME_PICTURE = "PicTable";

	/*
	id(primary) |unique_id | username | owner_unique_id
	*/
	public static final String TABLE_NAME_FOLLOWER = "FollowerTable";

	/*
	id(primary) | owner_unique_id | cmplogo | cmpwebsite | cmpname | cmpdesc | cmpphone
	*/
	public static final String TABLE_NAME_PUBLIC_INFO = "PublicInfoTable";

    // 构造函数，调用父类SQLiteOpenHelper的构造函数
    public DatabaseHelper(Context context, String name, CursorFactory factory,
            int version, DatabaseErrorHandler errorHandler)
    {
        super(context, name, factory, version, errorHandler);

    }

    public DatabaseHelper(Context context, String name, CursorFactory factory,
            int version)
    {
        super(context, name, factory, version);
        // SQLiteOpenHelper的构造函数参数：
        // context：上下文环境
        // name：数据库名字
        // factory：游标工厂（可选）
        // version：数据库模型版本号
    }

    public DatabaseHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }

	private void createLoginUserTable(SQLiteDatabase db){
	/*
		id(primary) | status | username | phonenumber |password |usertype | image file |description |area | location |login time|logout time | unique_id | easemob_username |easemob_status |lastupdate
	*/
		StringBuffer sBuffer = new StringBuffer();
		sBuffer.append("CREATE TABLE [" + TABLE_NAME_LOGIN_USER + "] (");
        	sBuffer.append("[id] INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, ");
		sBuffer.append("[status] INTEGER, ");	//0: no login  1:login
        	sBuffer.append("[username] TEXT,");
		sBuffer.append("[countrycode] TEXT,");
       	sBuffer.append("[phonenumber] TEXT,");
        	sBuffer.append("[password] TEXT,");
		sBuffer.append("[usertype] INTEGER,");//0:noraml user 1:sam  2:sam team
		sBuffer.append("[imagefile] TEXT,");
		sBuffer.append("[description] TEXT,");
		sBuffer.append("[area] TEXT,");
		sBuffer.append("[location] TEXT,");
		sBuffer.append("[logintime] INTEGER,"); //System.getCurrentMilliseconds
		sBuffer.append("[logouttime] INTEGER,"); //System.getCurrentMilliseconds
		sBuffer.append("[unique_id] INTEGER,"); 
		sBuffer.append("[easemob_username] TEXT,"); //null: easemob history 
		sBuffer.append("[easemob_status] INTEGER,"); //0:no login 1:login
		sBuffer.append("[lastupdate] INTEGER,"); //last user info udate time recroded in server side
		sBuffer.append("[conversation_existed]  INTEGER)"); 
		
		// 执行创建表的SQL语句
        	db.execSQL(sBuffer.toString());
	}

	private void createContactUserTable(SQLiteDatabase db){
	/*
		id(primary) | username | phonenumber | imagefile |description |area | location | unique_id | easemob_username | lastupdate
	*/
		StringBuffer sBuffer = new StringBuffer();
		sBuffer.append("CREATE TABLE [" + TABLE_NAME_CONTACT_USER + "] (");
        	sBuffer.append("[id] INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, ");
        	sBuffer.append("[username] TEXT,");
       	sBuffer.append("[phonenumber] TEXT,");
		sBuffer.append("[usertype] INTEGER,");//0:noraml user 1:sam  2:sam team
		sBuffer.append("[imagefile] TEXT,");
		sBuffer.append("[description] TEXT,");
		sBuffer.append("[area] TEXT,");
		sBuffer.append("[location] TEXT,");
		sBuffer.append("[unique_id] INTEGER,");
		sBuffer.append("[easemob_username] TEXT,");
		sBuffer.append("[lastupdate] TEXT )");
		
		// 执行创建表的SQL语句
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
		sBuffer.append("[canceltime] INTEGER ,");
		sBuffer.append("[sendercellphone] TEXT,");
		sBuffer.append("[senderusername] TEXT )");
		// 执行创建表的SQL语句
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
		// 执行创建表的SQL语句
        	db.execSQL(sBuffer.toString());
	}

	private void createReceivedQuestionTableForServicer(SQLiteDatabase db){
		StringBuffer sBuffer = new StringBuffer();
		sBuffer.append("CREATE TABLE [" + TABLE_NAME_RECEIVED_QUESTION + "] (");
		sBuffer.append("[id] INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, ");
        	sBuffer.append("[question_id] TEXT, ");
		sBuffer.append("[question] TEXT,");
        	sBuffer.append("[contactuserid] INTEGER,");
		sBuffer.append("[status] INTEGER, ");
		sBuffer.append("[response] INTEGER,");
		sBuffer.append("[receivedtime] INTEGER,");
		sBuffer.append("[canceledtime] INTEGER,");
		sBuffer.append("[receivercellphone] TEXT,");
		sBuffer.append("[receiverusername] TEXT)");
		// 执行创建表的SQL语句
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
		// 执行创建表的SQL语句
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
		// 执行创建表的SQL语句
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
		// 执行创建表的SQL语句
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
		// 执行创建表的SQL语句
        	db.execSQL(sBuffer.toString());
	}

	
	private void createFGTable(SQLiteDatabase db){
		StringBuffer sBuffer = new StringBuffer();
		sBuffer.append("CREATE TABLE [" + TABLE_NAME_FG+ "] (");
		sBuffer.append("[id] INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, ");
        	sBuffer.append("[timestamp] INTEGER, ");
		sBuffer.append("[fg_id] INTEGER,");
		sBuffer.append("[status] INTEGER,");
		sBuffer.append("[comment] TEXT,");
		sBuffer.append("[publisher_phonenumber] TEXT ,");
		sBuffer.append("[publisher_username] TEXT ,");
		sBuffer.append("[owner_phonenumber] TEXT,");
		sBuffer.append("[owner_username] TEXT )");
		// 执行创建表的SQL语句
        	db.execSQL(sBuffer.toString());
	}

	private void createRecommanderTable(SQLiteDatabase db){
	/*
	id(primary) |recommander_phonenumber | fg_id |timestamp
	*/
		StringBuffer sBuffer = new StringBuffer();
		sBuffer.append("CREATE TABLE [" + TABLE_NAME_RECOMMANDER+ "] (");
		sBuffer.append("[id] INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, ");
        	sBuffer.append("[recommander_phonenumber] TEXT ,");
		sBuffer.append("[fg_id] INTEGER,");
		sBuffer.append("[timestamp] INTEGER )");
		// 执行创建表的SQL语句
        	db.execSQL(sBuffer.toString());
	}

	
	private void createCommenterTable(SQLiteDatabase db){
	/*
	id(primary) |commenter_phonenumber | content | fg_id |timestamp
	*/
		StringBuffer sBuffer = new StringBuffer();
		sBuffer.append("CREATE TABLE [" + TABLE_NAME_COMMENTER+ "] (");
		sBuffer.append("[id] INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, ");
        	sBuffer.append("[commenter_phonenumber] TEXT ,");
		sBuffer.append("[content] TEXT,");
		sBuffer.append("[fg_id] INTEGER,");
		sBuffer.append("[timestamp] INTEGER )");
		// 执行创建表的SQL语句
        	db.execSQL(sBuffer.toString());
	}

	
	private void createPictureTable(SQLiteDatabase db){
	/*
	id(primary) |fg_id | thumbnail_pic | original_pic | url_thumbnail | url_original |sequence
	*/
		StringBuffer sBuffer = new StringBuffer();
		sBuffer.append("CREATE TABLE [" + TABLE_NAME_PICTURE+ "] (");
		sBuffer.append("[id] INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, ");
		sBuffer.append("[fg_id] INTEGER ,");
        	sBuffer.append("[thumbnail_pic] TEXT ,");
		sBuffer.append("[original_pic] TEXT ,");
		sBuffer.append("[url_thumbnail] TEXT ,");
		sBuffer.append("[url_original] TEXT ,");
		sBuffer.append("[sequence] INTEGER )");
		// 执行创建表的SQL语句
        	db.execSQL(sBuffer.toString());
	}


	private void createFollowerTable(SQLiteDatabase db){
	/*
	id(primary) |unique_id | username | owner_unique_id
	*/
		StringBuffer sBuffer = new StringBuffer();
		sBuffer.append("CREATE TABLE [" + TABLE_NAME_FOLLOWER+ "] (");
		sBuffer.append("[id] INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, ");
		sBuffer.append("[unique_id] INTEGER ,");
        	sBuffer.append("[username] TEXT ,");
		sBuffer.append("[owner_unique_id] INTEGER )");
		// 执行创建表的SQL语句
        	db.execSQL(sBuffer.toString());

	}

	private void createPublicInfoTable(SQLiteDatabase db){
	/*
	id(primary) | owner_unique_id | cmplogo | cmpwebsite | cmpname | cmpdesc | cmpphone
	*/
		StringBuffer sBuffer = new StringBuffer();
		sBuffer.append("CREATE TABLE [" + TABLE_NAME_PUBLIC_INFO+ "] (");
		sBuffer.append("[id] INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, ");
		sBuffer.append("[owner_unique_id] INTEGER ,");
        	sBuffer.append("[cmplogo] TEXT ,");
		sBuffer.append("[cmpwebsite] TEXT ,");
		sBuffer.append("[cmpname] TEXT ,");
		sBuffer.append("[cmpdesc] TEXT ,");
		sBuffer.append("[cmpphone] TEXT )");
		// 执行创建表的SQL语句
        db.execSQL(sBuffer.toString());
	}

    // 继承SQLiteOpenHelper类,必须要覆写的三个方法：onCreate(),onUpgrade(),onOpen()
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
		
	createFGTable(db);
	createRecommanderTable(db);
	createCommenterTable(db);
	createPictureTable(db);
	createFollowerTable(db);
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

	db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_FG);
	db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_RECOMMANDER);
	db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_COMMENTER);
	db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_PICTURE);
	db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_FOLLOWER);
	onCreate(db);
    }

    @Override
    public void onOpen(SQLiteDatabase db)
    {
        super.onOpen(db);
    }

}
