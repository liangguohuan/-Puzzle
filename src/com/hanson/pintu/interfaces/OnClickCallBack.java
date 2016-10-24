package com.hanson.pintu.interfaces;

import android.content.DialogInterface;

import com.hanson.pintu.activity.ListActivity;

public interface OnClickCallBack {
	public void callBack(ListActivity.DialogChangeType dct, DialogInterface dialog, int whichButton);
	
}
