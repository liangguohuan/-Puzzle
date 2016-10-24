package com.hanson.pintu.data;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

import com.hanson.pintu.R;
import com.hanson.pintu.util.CommFunc;

public class BasicDBHelper extends SQLiteOpenHelper {
	
	private final static int DATABASE_VERSION = 1;
	private final static String DATABASE_NAME = "pintuDB";
	private String TABLE_NAME = "list_table";
	private Context context;
	
	public String getTableName() {
		return TABLE_NAME;
	}
	
	public void setTableName(String tableName) {
		TABLE_NAME = tableName;
	}
	
	public BasicDBHelper(Context context, String name, CursorFactory factory, int version) {
		super(context, name, factory, DATABASE_VERSION);
		// TODO Auto-generated constructor stub
	}
	public BasicDBHelper(Context context,String name){
		this(context,name,DATABASE_VERSION);
	}
	public BasicDBHelper(Context context,String name,int version){
		this(context, name,null,DATABASE_VERSION);
	}
	
	public BasicDBHelper(Context context) {
		this(context, DATABASE_NAME, DATABASE_VERSION);
		this.context = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL("DROP TABLE IF EXISTS type_table");
		db.execSQL("DROP TABLE IF EXISTS list_table");
		db.execSQL("CREATE TABLE 'type_table'(id INTEGER PRIMARY KEY AUTOINCREMENT, name VARCHAR(30), orders INTEGER DEFAULT 2)");
		db.execSQL("CREATE TABLE 'list_table'(id INTEGER PRIMARY KEY AUTOINCREMENT, filename TEXT, type INTEGER, count TINYINT, source TINYINT)");
		
		initDB(db);
		//closeDataBase(db);
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldDATABASE_VERSION, int newDATABASE_VERSION) {
		// TODO Auto-generated method stub
		onCreate(db);
	}
	
	private void initDB(SQLiteDatabase db) {
		try{
			InputStream is = context.getResources().openRawResource(R.raw.init_sql);
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			String sql_insert = "";
			while((sql_insert = br.readLine()) != null){
				db.execSQL(sql_insert);
			}
			is.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private SQLiteDatabase getDataBase() {
		return this.getReadableDatabase();
	}
	
	private void closeDataBase(SQLiteDatabase db) {
		//db.close();
	}
	
	public ContentValues hashMapToContentValues(HashMap<String, Object> map) {
		Set<String> set = map.keySet();
		Iterator<String> i = set.iterator();
		ContentValues cv = new ContentValues();
		while(i.hasNext()){
			String key = i.next();
			String val = map.get(key).toString();
			cv.put(key, val);
		}
		return cv;
	}
	
	/**
	 * 取得随机列表
	 * @param count
	 * @param where
	 * @param whereParam
	 * @return
	 */
	public Cursor getRandomList(int count, String where, String[] whereParam) {
		Cursor cursor = query(null, where, whereParam, "random()", count + "");
		return cursor;
	}
	
	/**
	 * 通用函数 getTotal
	 * @param where
	 * @param whereParam
	 * @return
	 */
	public int getTotal(String where, String[] whereParam){
		Cursor cursor = query(new String[]{"count(*) as c"}, where, whereParam, null, null);
		cursor.moveToNext();
		int total = Integer.valueOf(cursor.getString(cursor.getColumnIndex("c")));
		CommFunc.Log("wh", "total: " + total);
		return total;
	}
	
	/**
	 * 通用函数 checkRowExit
	 * @param where
	 * @param whereParam
	 * @return
	 */
	public boolean checkRowExit(String where, String[] whereParam){
		return getTotal(where, whereParam) > 0 ? true : false;
	}
	
	/**
	 * 取最大值
	 * @param feild
	 * @param where
	 * @param whereParam
	 * @return
	 */
	public String getMaxValue(String feild, String where, String[] whereParam){
		Cursor cursor = query(new String[]{"MAX(" + feild + ") AS max"}, where, whereParam, null, null);
		cursor.moveToNext();
		String maxVal = cursor.getString(cursor.getColumnIndex("max"));
		return maxVal;
	}
	
	/**
	 * 取最小值
	 * @param feild
	 * @param where
	 * @param whereParam
	 * @return
	 */
	public String getMinValue(String feild, String where, String[] whereParam){
		Cursor cursor = query(new String[]{"MIN(" + feild + ") AS min"}, where, whereParam, null, null);
		cursor.moveToNext();
		String minVal = cursor.getString(cursor.getColumnIndex("min"));
		return minVal;
	}
	
	/**
	 * 通用函数 getMaxId
	 * @param where
	 * @param whereParam
	 * @return
	 */
	public long getMaxId(String where, String[] whereParam){
		Cursor cursor = query(new String[]{"MAX(id) AS id"}, where, whereParam, null, null);
		cursor.moveToNext();
		if(cursor.getString(cursor.getColumnIndex("id")) == null)
			return 0l;
		int id = Integer.valueOf(cursor.getString(cursor.getColumnIndex("id")));
		return id;
	}
	
	/**
	 * 通用函数 getMinId
	 * @param where
	 * @param whereParam
	 * @return
	 */
	public long getMinId(String where, String[] whereParam){
		Cursor cursor = query(new String[]{"MIN(id) AS id"}, where, whereParam, null, null);
		cursor.moveToNext();
		if(cursor.getString(cursor.getColumnIndex("id")) == null)
			return 0l;
		int id = Integer.valueOf(cursor.getString(cursor.getColumnIndex("id")));
		return id;
	}
	
	/**
	 * 通用函数 insert
	 * @param db
	 * @param map
	 * @return
	 */
	public long insert(HashMap<String, Object> map) {
		SQLiteDatabase db = getDataBase();
		ContentValues cv = hashMapToContentValues(map);
		long row = db.insert(TABLE_NAME, null, cv);
		closeDataBase(db);
		return row;
	}
	
	/**
	 * 通用函数 update
	 * @param db
	 * @param map
	 * @param where
	 * @param whereValue
	 * @return
	 */
	public int update(HashMap<String, Object> map, String where, String[] whereValue) {
		SQLiteDatabase db = getDataBase();
		ContentValues cv = hashMapToContentValues(map);
		int reInt = db.update(TABLE_NAME, cv, where, whereValue);
		closeDataBase(db);
		return reInt;
	}
	
	/**
	 * 通用函数 delete
	 * @param db
	 * @param where
	 * @param whereValue
	 */
	public int delete(String where, String[] whereValue) {
		SQLiteDatabase db = getDataBase();
		int reInt = db.delete(TABLE_NAME, where, whereValue);
		closeDataBase(db);
		return reInt;
	}
	
	/**
	 * 通用函数 query
	 * @param db
	 * @param columns
	 * @param where
	 * @param whereParam
	 * @param order
	 * @param limit
	 * @return
	 */
	public Cursor query(String[] columns, String where, String[] whereParam, String order, String limit) {
		SQLiteDatabase db = getDataBase();
		Cursor cursor = db.query(TABLE_NAME, columns, where, whereParam, null, null, order, limit);
		closeDataBase(db);
		return cursor;
	}
}
