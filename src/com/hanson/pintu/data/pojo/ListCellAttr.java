package com.hanson.pintu.data.pojo;

import android.graphics.Bitmap;


public class ListCellAttr extends ListTable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5L;
	private String typeName;
	private Bitmap bitmap;
	private boolean bSelected;
	public String getTypeName() {
		return typeName;
	}
	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}
	public Bitmap getBitmap() {
		return bitmap;
	}
	public void setBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
	}
	public boolean isbSelected() {
		return bSelected;
	}
	public void setbSelected(boolean bSelected) {
		this.bSelected = bSelected;
	}
	public final static ListCellAttr listTableToListCellAttr(ListTable lt) {
		ListCellAttr lca = new ListCellAttr();
		lca.setId(lt.getId());
		lca.setFilename(lt.getFilename());
		lca.setType(lt.getType());
		lca.setCount(lt.getCount());
		lca.setSource(lt.getSource());
		return lca;
	}
	public final static ListTable listCellAttrToListTable(ListCellAttr lca) {
		ListTable lt = new ListTable();
		lt.setId(lca.getId());
		lt.setFilename(lca.getFilename());
		lt.setType(lca.getType());
		lt.setCount(lca.getCount());
		lt.setSource(lca.getSource());
		return lt;
	}
	@Override
	public String toString() {
		return "ListCellAttr [typeName=" + typeName + ", bitmap=" + bitmap
				+ ", bSelected=" + bSelected + "]" + super.toString();
	}
}
