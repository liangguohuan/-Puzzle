package com.hanson.pintu.data.pojo;

import java.io.Serializable;

public class ListTable implements Serializable {
	private static final long serialVersionUID = 2L;
	private long id;
	private String filename;
	private long type;
	private String typeName;
	private int count;
	private int source;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	public long getType() {
		return type;
	}
	public void setType(long type) {
		this.type = type;
	}
	public String getTypeName() {
		return typeName;
	}
	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public int getSource() {
		return source;
	}
	public void setSource(int source) {
		this.source = source;
	}
	public void setData(long id, String filename, long type, int count , int source) {
		setId(id);
		setFilename(filename);
		setType(type);
		setCount(count);
		setSource(source);
	}
	@Override
	public String toString() {
		return "ListTable [id=" + id + ", filename=" + filename + ", type="
				+ type + ", count=" + count + ", source=" + source
				+ ", typeName=" + typeName + "]";
	}
	
}
