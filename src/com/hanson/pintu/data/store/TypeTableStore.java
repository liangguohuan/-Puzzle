package com.hanson.pintu.data.store;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;

import com.hanson.pintu.data.helper.TypeTableHelper;
import com.hanson.pintu.data.pojo.TypeTable;

public class TypeTableStore {

	TypeTableHelper tth;
	/**
	 * 
	 */
	public TypeTableStore(Context context) {
		tth = new TypeTableHelper(context);
		// TODO Auto-generated constructor stub
	}
	
	public final TypeTable getInfo(long id) {
		ArrayList<TypeTable> list;
		Cursor cursor = tth.getList("id=?", new String[]{id + ""}, null, null);
		list = cursorToList(cursor);
		return list.get(0);
	}
	
	public final ArrayList<TypeTable> getList() {
		ArrayList<TypeTable> list;
		Cursor cursor;
		cursor = tth.getList(null, null, "orders ASC", null);
		list = cursorToList(cursor);
		return list;
	}
	
	private final ArrayList<TypeTable> cursorToList(Cursor cursor){
		ArrayList<TypeTable> list = new ArrayList<TypeTable>();
		while(cursor.moveToNext()){
			TypeTable typeTable = new TypeTable();
			String id = cursor.getString(cursor.getColumnIndex("id"));
			String name = cursor.getString(cursor.getColumnIndex("name"));
			String order = cursor.getString(cursor.getColumnIndex("orders"));
			typeTable.setId(Long.parseLong(id));
			typeTable.setName(name);
			typeTable.setOrder(Integer.valueOf(order));
			list.add(typeTable);
		}
		return list;
	}
}
