package com.hanson.pintu.data.store;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;

import com.hanson.pintu.data.helper.ListTableHelper;
import com.hanson.pintu.data.pojo.ListTable;
import com.hanson.pintu.util.CommFunc;

public class ListTableStore {

	ListTableHelper lth;
	/**
	 * 
	 */
	public ListTableStore(Context context) {
		lth = new ListTableHelper(context);
		// TODO Auto-generated constructor stub
	}
	
	public final ListTable getInfo(long id) {
		ArrayList<ListTable> list;
		Cursor cursor = lth.getList("id=?", new String[]{id + ""}, null, null);
		list = cursorToList(cursor);
		ListTable lt = list.size() > 0 ? list.get(0) : new ListTable();
		return lt;
	}
	
	public final ListTable getRandomOne(){
		ArrayList<ListTable> list;
		Cursor cursor = lth.getRandomList(1, null, null);
		list = cursorToList(cursor);
		return list.get(0);
	}
	
	public final ListTable getInfo(String filename) {
		ArrayList<ListTable> list;
		Cursor cursor = lth.getList("filename=?", new String[]{filename}, null, null);
		list = cursorToList(cursor);
		return list.get(0);
	}
	
	public final ListTable getNextInfo(ListTable listTable) {
		ArrayList<ListTable> list = null;
		long id = listTable.getId();
		long type = listTable.getType();
		int source = listTable.getSource();
		String where = "id>? AND type=? AND source=?";
		String[] whereParam = new String[]{id + "", type + "", source + ""};
		
		long maxId = lth.getMaxId("type=? AND source=?", new String[]{type + "", source + ""});
		if(id < maxId) {
			Cursor cursor = lth.getList(where, whereParam, null, "0,1");
			list = cursorToList(cursor);
			CommFunc.Log("wh", "getNextInfo cursor: " + cursor);
		}
		else if(id == maxId) {
			list = getList(type, source, "id ASC", "0,1");
		}
		return list.get(0);
	}
	
	public ArrayList<ListTable> getList(long type, int source,String order, String limit) {
		ArrayList<ListTable> list;
		Cursor cursor;
		String where = "";
		String[] whereParam = null;
		if(type > -1 && source > -1) {
			where += "type=? AND source=?";
			whereParam = new String[]{type + "", source + ""};
		}
		else if(type > -1) {
			where += "type=?";
			whereParam = new String[]{type + ""};
		}
		else if(source > -1) {
			where += "source=?";
			whereParam = new String[]{source + ""};
		}
		
		if(order == null)
			order = "id DESC";
		cursor = lth.getList(where, whereParam, order, limit);
		list = cursorToList(cursor);
		return list;
	}
	
	public final int getTotal(long type, int source) {
		String where = "";
		String[] whereParam = null;
		if(type > -1 && source > -1) {
			where += "type=? AND source=?";
			whereParam = new String[]{type + "", source + ""};
		}
		else if(type > -1) {
			where += "type=?";
			whereParam = new String[]{type + ""};
		}
		else if(source > -1) {
			where += "source=?";
			whereParam = new String[]{source + ""};
		}
		int total = lth.getTotal(where, whereParam);
		return total;
	}
	
	private final ArrayList<ListTable> cursorToList(Cursor cursor){
		ArrayList<ListTable> list = new ArrayList<ListTable>();
		while(cursor.moveToNext()){
			ListTable listTable = new ListTable();
			String id = cursor.getString(cursor.getColumnIndex("id"));
			String filename = cursor.getString(cursor.getColumnIndex("filename"));
			String type = cursor.getString(cursor.getColumnIndex("type"));
			String count = cursor.getString(cursor.getColumnIndex("count"));
			String source = cursor.getString(cursor.getColumnIndex("source"));
			listTable.setId(Long.parseLong(id));
			listTable.setFilename(filename);
			listTable.setType(Long.parseLong(type));
			listTable.setCount(Integer.parseInt(count));
			listTable.setSource(Integer.valueOf(source));
			list.add(listTable);
		}
		return list;
	}
}
