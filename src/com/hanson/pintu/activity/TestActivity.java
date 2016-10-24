package com.hanson.pintu.activity;

import java.util.ArrayList;

import android.os.Bundle;

import com.hanson.pintu.basicActivity;
import com.hanson.pintu.data.pojo.ListTable;
import com.hanson.pintu.data.store.ListCellAttrStore;
import com.hanson.pintu.data.store.ListTableStore;
import com.hanson.pintu.util.CommFunc;

public class TestActivity extends basicActivity {
	ListCellAttrStore lcas;
	ListTableStore lts;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		lcas = new ListCellAttrStore(this);
		lts = new ListTableStore(this);
		//ArrayList<ListTable> list = lts.getList(-1, -1, null, null);
		ArrayList<ListTable> list = lcas.getList(-1, -1, null, null);
		CommFunc.Log("wh", "!list: " + list.get(0).toString());
	}
}
