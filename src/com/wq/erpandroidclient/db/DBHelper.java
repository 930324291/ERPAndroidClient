package com.wq.erpandroidclient.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * @author 王强 2012-12-7 数据库创建类
 */
public class DBHelper extends SQLiteOpenHelper {

	// 数据库名称
	private final static String DB_NAME = "QING_DAO_TONG.db";
	// 数据库版本号
	private final static int DB_VERSON = 9;

	/** 运动类型表 */
	public static final String PRACTICE = "practis";

	/** 步行运动数据 */
	public static final String PRACTICE_DATA_FOOT = "foot_data";
	
	/** 运动类型表 */
	private final String PRACTIS_TABLE = "CREATE TABLE IF NOT EXISTS " + PRACTICE + " (" 
			+ "type_name text,"// 运动种类名称
			+ "isShow text,"// 是否加载 1显示，0不显示
			+ "icon text," 
			+ "data text) ;";//

	/** 运动数据记录表 */
	private final String PRACTICE_DATA_TABLE = "CREATE TABLE IF NOT EXISTS " + PRACTICE_DATA_FOOT + " (" 
			+ "id integer PRIMARY KEY AUTOINCREMENT,"
			+ "type_name text,"// 运动种类名称
			+ "time_year text,"// 年
			+ "time_month text,"// 月
			+ "time_day text,"// 日
			+ "time_h text,"// 时
			+ "time_m text,"// 分
			+ "data INTEGER) ;";// 最后一次记录的数据
	
	private final String DROP_PRACTICE_TABLE = "DROP TABLE IF EXISTS " + PRACTICE;
	private final String DROP_PRACTICE_DATA_TABLE = "DROP TABLE IF EXISTS " + PRACTICE_DATA_FOOT;

	/**
	 * @param context
	 */
	public DBHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSON);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL(PRACTIS_TABLE);

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		db.execSQL(DROP_PRACTICE_TABLE);
		db.execSQL(DROP_PRACTICE_DATA_TABLE);
		onCreate(db);
	}

}
