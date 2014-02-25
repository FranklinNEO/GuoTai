package com.redinfo.red4s.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class CodeDBHelper extends SQLiteOpenHelper {
	public static CodeDBHelper mInstance = null;

	/** 数据库名称 **/
	public static final String DATABASE_NAME = "code.db";

	/** 数据库版本号 **/
	private static final int DATABASE_VERSION = 1;

	/** DB对象 **/
	SQLiteDatabase mDb = null;

	Context mContext = null;

	public final static String CODE_TABLE_NAME = "code_date";

	/** 数据库SQL语句 创建表 **/

	public static final String CODE_TABLE_CREATE = "create table code_date("
			+ "_id INTEGER PRIMARY KEY AUTOINCREMENT,"
			+ "username TEXT NOT NULL," + "code20 TEXT NOT NULL,"
			+ "content TEXT NOT NULL," + "createTime TEXT NOT NULL,"
			+ "picPath TEXT," + "flag INTEGER NOT NULL);";

	public CodeDBHelper(Context context) {
		// TODO Auto-generated constructor stub
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		// 得到数据库对象
		mDb = getReadableDatabase();
		mContext = context;
	}

	/** 单例模式 **/
	public static synchronized CodeDBHelper getInstance(Context context) {
		if (mInstance == null) {
			mInstance = new CodeDBHelper(context);
		}
		return mInstance;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL(CODE_TABLE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}

	public void insert_code(String tablename, String username, String code20,
			String content, String picPath, String createTime, int flag) {
		ContentValues values = new ContentValues();
		values.put("username", username);
		values.put("code20", code20);
		values.put("content", content);
		values.put("picPath", picPath);
		values.put("createTime", createTime);
		values.put("flag", flag);
		mDb.insert(tablename, null, values);
		values.clear();
	}

	public void delete_code(String tablename, String code20, String createTime) {
		mDb.delete(tablename, "code20='" + code20 + "' and createTime='"
				+ createTime + "';", null);
	}

	public void delete_user(String tablename, String username) {
		mDb.delete(tablename, "username='" + username + "';", null);
	}

}
