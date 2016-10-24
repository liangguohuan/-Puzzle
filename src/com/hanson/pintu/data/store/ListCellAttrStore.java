package com.hanson.pintu.data.store;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.hanson.pintu.data.pojo.ListCellAttr;
import com.hanson.pintu.data.pojo.ListTable;
import com.hanson.pintu.data.pojo.TypeTable;
import com.hanson.pintu.util.CommFunc;
import com.hanson.pintu.util.FileUtil;

public class ListCellAttrStore extends ListTableStore {

	private Context context;
	private HashMap<String,Bitmap> listBitmap = new HashMap<String, Bitmap>(); 
	
	public ListCellAttrStore(Context context) {
		super(context);
		this.context = context;
		// TODO Auto-generated constructor stub
	}
	
	public final ArrayList<ListCellAttr> getCellList(long type, int source,String order, String limit) {
		ArrayList<ListCellAttr> list;
		ArrayList<ListTable> listTemp = super.getList(type, source, order, limit);
		list = listTableToListCellAttr(listTemp);
		return list;
	}
	
	private ArrayList<ListCellAttr> listTableToListCellAttr(ArrayList<ListTable> list) {
		HashMap<Long, String> hmListType = getListTypeHashMap();
		ArrayList<ListCellAttr> listTemp = new ArrayList<ListCellAttr>(); 
		for (ListTable listTable : list) {
			ListCellAttr listCellAttr = listTableToListCellAttr(listTable, hmListType);
			listTemp.add(listCellAttr);
		}
		return listTemp;
	}
	
	//提供外部调用
	public ListCellAttr listTableToListCellAttr(ListTable listTable) {
		HashMap<Long, String> hmListType = getListTypeHashMap();
		ListCellAttr listCellAttr = listTableToListCellAttr(listTable, hmListType);
		return listCellAttr;
	}
	
	private ListCellAttr listTableToListCellAttr(ListTable listTable, HashMap<Long, String> hmListType) {
		ListCellAttr listCellAttr = new ListCellAttr();
		listCellAttr = ListCellAttr.listTableToListCellAttr(listTable);
		listCellAttr.setTypeName(getTypeNameById(listTable.getType(), hmListType));
		Bitmap bitmap = getBitmapFromListTable(listTable);
		listBitmap.put(listTable.toString(), bitmap);
		listCellAttr.setBitmap(bitmap);
		return listCellAttr;
	}
	
	private HashMap<Long, String> getListTypeHashMap() {
		TypeTableStore tts = new TypeTableStore(context);
		ArrayList<TypeTable> listType;
		listType = tts.getList();
		HashMap<Long, String> hmListType = new HashMap<Long, String>();
		for (TypeTable tt : listType) {
			hmListType.put(tt.getId(), tt.getName());
		}
		return hmListType;
	}
	
	private String getTypeNameById(long typeId, HashMap<Long, String> hmListType) {
		return hmListType.get(typeId);
	}
	
	private Bitmap getBitmapFromListTable(ListTable lt) {
		Bitmap newBitmap = null;
		String absoluteFilePath  = CommFunc.getThumbImagePath(lt);
		if(!FileUtil.isFileExist(absoluteFilePath)) {
			try {
				newBitmap = CommFunc.createThumbImage(context, lt);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else {
			newBitmap = BitmapFactory.decodeFile(absoluteFilePath);
		}
		return newBitmap;
	}
	
	public void recycle(ArrayList<ListCellAttr> list) {
		if(list.size() > 0) {
			for (ListCellAttr lca : list) {
				ListTable lt = ListCellAttr.listCellAttrToListTable(lca);
				String key = getKey(lt);
				listBitmap.get(key).recycle();
			}
			list.clear();
		}
	}
	
	public ArrayList<ListCellAttr> remove(ArrayList<ListCellAttr> list, int index) {
		ListTable lt = ListCellAttr.listCellAttrToListTable(list.get(index));
		listBitmap.get(lt.toString()).recycle();
		list.remove(index);
		return list;
	}
	
	private String getKey(ListTable lt) {
		return lt.toString();
	}
}
