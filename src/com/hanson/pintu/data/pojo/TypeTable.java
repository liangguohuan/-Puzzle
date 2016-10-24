package com.hanson.pintu.data.pojo;

import java.io.Serializable;

public class TypeTable implements Serializable {
	private static final long serialVersionUID = 1L;
	private long id;
	private String name;
	private int order;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getOrder() {
		return order;
	}
	public void setOrder(int order) {
		this.order = order;
	}
	@Override
	public String toString() {
		return "TypeTable [id=" + id + ", name=" + name + "]";
	}
	
}
