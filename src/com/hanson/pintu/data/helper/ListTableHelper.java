package com.hanson.pintu.data.helper;

import java.util.HashMap;

import android.content.Context;
import android.database.Cursor;

import com.hanson.pintu.data.BasicDBHelper;

public class ListTableHelper extends BasicDBHelper {
	private String TABLE_NAME = "list_table";
	public ListTableHelper(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		setTableName(TABLE_NAME);
	}
	
	public Cursor getList(String where, String[] whereParam, String order, String limit) {
		Cursor cursor = query(null, where, whereParam, order, limit);
		return cursor;
	}
	
	public long insert(String filename, long type, int count, int source, boolean bCheck) {
		if(bCheck && checkRowExit("filename=?", new String[]{filename})) {
			return -1;
		}
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("filename", filename);
		map.put("type", type);
		map.put("count", count);
		map.put("source", source);
		return insert(map);
	}
	
	public int update(long id, String filename, long type, int count, int source, boolean bCheck) {
		if(bCheck && checkRowExit("filename=?", new String[]{filename})) {
			return -1;
		}
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("filename", filename);
		map.put("type", type);
		map.put("count", count);
		map.put("source", source);
		return update(map, "id=?", new String[]{id + ""});
	}
	
	public int delete(long id) {
		return delete("id=?", new String[]{id + ""});
	}
}
