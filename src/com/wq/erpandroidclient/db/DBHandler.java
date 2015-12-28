package com.wq.erpandroidclient.db;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * 数据库操作类
 * 
 * @author 王强 2012-12-7
 */
public class DBHandler {
	// 降序
	public final static String DESC = "DESC";
	// 升序
	public final static String ASC = "ASC";
	// 数据库操作实例
	private SQLiteDatabase db = null;
	private DBHelper dbHelper = null;
	// 数据库操作实例对象
	private static volatile DBHandler dbHandler = null;

	/**
	 * 得到数据库操作实例
	 * 
	 * @param context
	 * @return
	 */
	public static DBHandler getInstance(Context context) {

		if (dbHandler == null) {
			synchronized (DBHandler.class) {
				if (dbHandler == null) {
					dbHandler = new DBHandler(context);
				}
			}
		}
		return dbHandler;
	}

	/**
	 * 单例模式
	 * 
	 * @param context
	 */
	private DBHandler(Context context) {
		dbHelper = new DBHelper(context);
	}

	/**
	 * 关闭数据库
	 */
	private void closeDB() {
		if (db != null && db.isOpen()) {
			db.close();
			db = null;
		} else {
			db = null;
		}
	}

	/**
	 * 打开数据库
	 * 
	 * @return true 打开成功 ，false 打开失败
	 */
	private boolean openDB() {
		if (db == null) {
			db = dbHelper.getWritableDatabase();
		}

		if (db != null && db.isOpen()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 根据表的字段名称和字段值，查询数据,结果无序
	 * 
	 * @param tableName
	 * @param columnName
	 * @param columnValue
	 * @return
	 */
	public List<ContentValues> selectTable(String tableName, String columnName, String columnValue) {
		return selectTableOrdered(tableName, columnName, columnValue, null, null);
	}

	/**
	 * 分页查询
	 * 
	 * @param tableName
	 *            表名
	 * @param pageNum
	 *            返回第几页数据
	 * @param pageSize
	 *            数据分页大小
	 * @param orderColumnName
	 *            排序所依赖的列明
	 * @param order
	 *            排序方式
	 * @return
	 */
	public List<ContentValues> selectTableByPage(String tableName, int pageNum, int pageSize, String orderColumnName,
			String order) {
		// select * from finery where id > 7 order by id asc limit 10 offset 3 ;
		int offset = pageNum > 0 ? (pageNum - 1) * pageSize : 0;
		String sql = "select * from " + tableName + " order by " + orderColumnName + " " + order + " limit " + pageSize
				+ " offset " + offset + " ;";
		List<ContentValues> res = new ArrayList<ContentValues>();
		this.openDB();
		Cursor c = null;
		c = db.rawQuery(sql, null);
		ContentValues values = null;
		while (c.moveToNext()) {
			values = new ContentValues();
			for (int i = 0; i < c.getColumnCount(); i++) {
				values.put(c.getColumnName(i), c.getString(i));
			}
			res.add(values);
		}
		c.close();
		this.closeDB();
		return res;
	}

	/**
	 * 根据表的字段名称和字段值，查询数据,结果有序
	 * 
	 * @param tableName
	 *            表名
	 * @param columnName
	 *            查询条件 字段名
	 * @param columnValue
	 *            查询条件字段值
	 * @param orderColumnName
	 *            排序字段名称
	 * @param order
	 *            排序方式
	 * @return
	 */
	public List<ContentValues> selectTableOrdered(String tableName, String columnName, String columnValue,
			String orderColumnName, String order) {
		List<ContentValues> res = new ArrayList<ContentValues>();
		this.openDB();
		String sql = null;
		Cursor c = null;
		if (columnName == null || columnValue == null) {
			if (orderColumnName != null && order != null) {
				sql = "select * from " + tableName + " ORDER BY " + orderColumnName + " " + order;
			} else {
				sql = "select * from " + tableName;
			}
			c = db.rawQuery(sql, null);
		} else {
			if (orderColumnName != null && order != null) {
				sql = "select * from " + tableName + " where " + columnName + "=?" + " ORDER BY " + orderColumnName
						+ " " + order;
			} else {
				sql = "select * from " + tableName + " where " + columnName + "=?";
			}
			c = db.rawQuery(sql, new String[] { columnValue });
		}
		ContentValues values = null;
		while (c.moveToNext()) {
			values = new ContentValues();
			for (int i = 0; i < c.getColumnCount(); i++) {
				values.put(c.getColumnName(i), c.getString(i));
			}
			res.add(values);
		}
		c.close();
		this.closeDB();
		return res;
	}

	/**
	 * 查询数据
	 * 
	 * @param sql
	 *            查询语句
	 * @return
	 */
	public List<ContentValues> selectTable(String sql,String[] selectionArgs) {
		List<ContentValues> res = new ArrayList<ContentValues>();
		this.openDB();
		Cursor c = null;
		c = db.rawQuery(sql, selectionArgs);
		ContentValues values = null;
		while (c.moveToNext()) {
			values = new ContentValues();
			for (int i = 0; i < c.getColumnCount(); i++) {
				values.put(c.getColumnName(i), c.getString(i));
			}
			res.add(values);
		}
		c.close();
		this.closeDB();
		return res;
	}

	/**
	 * 删除表记录
	 * 
	 * @param tableName
	 *            表名
	 * @param columnName
	 *            条件字段名
	 * @param columnValues
	 *            条件字段值集合
	 */
	public void deleteTableByNames(String tableName, String columnName, List<String> columnValues) {
		// 获得数据库的写入锁
		this.openDB();
		db.beginTransaction();
		String sql = null;
		if (columnName == null || columnValues == null) {
			sql = "delete from " + tableName;
			db.execSQL(sql);
		} else {
			sql = "delete from " + tableName + " where " + columnName + "=";
			for (String value : columnValues) {
				db.execSQL(sql + value);
			}
		}
		db.setTransactionSuccessful();
		db.endTransaction();
		this.closeDB();
	}

	/**
	 * 删除表记录
	 * 
	 * @param tableName
	 *            表名
	 * @param columnName
	 *            条件字段名
	 * @param columnValues
	 *            条件字段值
	 */
	public void deleteTableByName(String tableName, String columnName, String columnValues) {
		// 获得数据库的写入锁
		this.openDB();
		db.beginTransaction();
		String sql = null;
		if (columnName == null || columnValues == null) {
			sql = "delete from " + tableName;
			db.execSQL(sql);
		} else {
			// db.delete(tableName, columnName+"=?", new
			// String[]{columnValues});
			sql = "delete from " + tableName + " where " + columnName + " = '" + columnValues + "'";
			db.execSQL(sql);
		}
		db.setTransactionSuccessful();
		db.endTransaction();
		this.closeDB();
	}

	/**
	 * 向指定的数据表插入数据 注意：该方法没有进行数据重复检查，可能会造成数据重复
	 * 
	 * @param tableName
	 *            表名
	 * @param data
	 *            要插入的数据
	 * @return 返回插入的条数
	 */
	public int insertTableByName(String tableName, List<ContentValues> data) {
		int res = 0;
		this.openDB();
		db.beginTransaction();
		for (ContentValues values : data) {
			float i = db.insert(tableName, null, values);
			if (i != -1) {
				res++;
			}
		}
		db.setTransactionSuccessful();
		db.endTransaction();
		this.closeDB();
		return res;
	}

	/**
	 * 向指定的数据表插入数据 注意：该方法没有进行数据重复检查，可能会造成数据重复
	 * 
	 * @param tableName
	 *            表名
	 * @param values
	 *            要插入的数据集合
	 * @return
	 */
	public int insertTableByName(String tableName, ContentValues values) {
		this.openDB();
		int res = (int) db.insert(tableName, null, values);
		this.closeDB();
		return res;
	}

	/**
	 * 表记录修改方法
	 * 
	 * @param tableName
	 *            表名
	 * @param data
	 *            要修改内容的数据集合
	 * @param columnName
	 *            字段名称
	 * @param columnValue
	 *            字段值
	 * @return 修改条数
	 */
	public int updateTableByName(String tableName, List<ContentValues> data, String columnName, String columnValue) {
		int res = 0;
		this.openDB();
		db.beginTransaction();
		for (ContentValues values : data) {
			int i = db.update(tableName, values, columnName + "=?", new String[] { columnValue });
			if (i > 0) {
				res++;
			}
		}
		db.setTransactionSuccessful();
		db.endTransaction();
		this.closeDB();
		return res;
	}

	public int updateTableByName(String tableName, List<ContentValues> data, String columnName) {
		int res = 0;
		int c = 0;
		this.openDB();
		db.beginTransaction();
		for (ContentValues values : data) {
			int i = db.update(tableName, values, columnName + "=?", new String[] { values.getAsString(columnName) });
			if (i > 0) {
				res++;
			}
			c++;
		}
		db.setTransactionSuccessful();
		db.endTransaction();
		this.closeDB();
		return res;
	}

	/**
	 * 表记录修改方法
	 * 
	 * @param tableName
	 *            表名
	 * @param value
	 *            要修改内容的数据集合
	 * @param columnName
	 *            字段名称
	 * @param columnValue
	 *            字段值
	 * @return 修改条数
	 */
	public int updateTableByName(String tableName, ContentValues value, String columnName, String columnValue) {
		int res = 0;
		this.openDB();
		res = db.update(tableName, value, columnName + "=?", new String[] { columnValue });
		this.closeDB();
		return res;
	}

}
