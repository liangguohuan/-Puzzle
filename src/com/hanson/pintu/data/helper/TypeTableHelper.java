package com.hanson.pintu.data.helper;

import java.util.HashMap;

import android.content.Context;
import android.database.Cursor;

import com.hanson.pintu.data.BasicDBHelper;
import com.hanson.pintu.util.CommFunc;

public class TypeTableHelper extends BasicDBHelper {
	private String TABLE_NAME = "type_table";
	public TypeTableHelper(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		setTableName(TABLE_NAME);
	}
	
	public Cursor getList(String where, String[] whereParam, String order, String limit) {
		Cursor cursor = query(null, where, whereParam, order, limit);
		return cursor;
	}
	
	public long insert(String name, boolean bCheck) {
		if(bCheck && checkRowExit("name=?", new String[]{name})) {
			return -1;
		}
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("name", name);
		int order = Integer.parseInt(getMaxValue("orders", null, null));
		order++;
		CommFunc.Log("wh", "order: " + order);
		map.put("orders", order);
		return insert(map);
	}
	
	public int update(long id, String name, boolean bCheck) {
		if(bCheck && checkRowExit("name=?", new String[]{name})) {
			return -1;
		}
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("name", name);
		return update(map, "id=?", new String[]{id + ""});
	}
	
	public int update(long id, String name, int order, boolean bCheck) {
		if(bCheck && checkRowExit("name=?", new String[]{name})) {
			return -1;
		}
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("name", name);
		map.put("orders", order);
		return update(map, "id=?", new String[]{id + ""});
	}
	
	public int delete(long id) {
		if(id == 1) {
			return 0;
		}
		return delete("id=?", new String[]{id + ""});
	}
}
