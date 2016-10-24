package com.hanson.pintu;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.hanson.pintu.activity.AboutActivity;
import com.hanson.pintu.activity.CollateActivity;
import com.hanson.pintu.activity.ListActivity;
import com.hanson.pintu.activity.MainActivity;
import com.hanson.pintu.activity.TypeListActivity;
import com.hanson.pintu.activity.TypeListOrderActivity;
import com.hanson.pintu.data.pojo.ListTable;
import com.hanson.pintu.util.GlobalVariable;

public class ActivityManager {
	private static Intent intent;
	
	public static void toCollateActivity(Context context, String fileName) {
		Intent intentSend = new Intent(GlobalVariable.COLLATE_ACTIVITY_PLAY_RECEIVER);
		intentSend.putExtra("fileName", fileName);
		context.sendBroadcast(intentSend);
		
		intent = new Intent(context, CollateActivity.class);
		intent.putExtra("fileName", fileName);
		intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_SINGLE_TOP);
		context.startActivity(intent);
	}
	
	public static void toListActivity(Context context, ListTable listTable) {
		intent = new Intent(context, ListActivity.class);
		intent.putExtra("listTable", listTable);
		intent.putExtra("param", "abc");
		intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_SINGLE_TOP);
		context.startActivity(intent);
		/*if(!context.getClass().toString().contains("LoadActivity"))
			((Activity) context).finish();*/
	}
	
	public static void toMainActivity(Context context, Bundle bundle) {
		intent = new Intent(context, MainActivity.class);
		intent.putExtras(bundle);
		//intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_SINGLE_TOP);
		context.startActivity(intent);
		
		Intent intentSend = new Intent(GlobalVariable.MAIN_ACTIVITY_PLAY_RECEIVER);
		intentSend.putExtras(bundle);
		context.sendBroadcast(intentSend);
	}
	
	public static void toAboutActivity(Context context) {
		intent = new Intent(context, AboutActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_SINGLE_TOP);
		context.startActivity(intent);
	}
	
	public static void toTypeListActivity(Context context, Bundle bundle) {
		intent = new Intent(context, TypeListActivity.class);
		intent.putExtras(bundle);
		intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_SINGLE_TOP);
		context.startActivity(intent);
	}
	
	public static void toTypeListOrderActivity(Context context, Bundle bundle) {
		intent = new Intent(context, TypeListOrderActivity.class);
		intent.putExtras(bundle);
		intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_SINGLE_TOP);
		context.startActivity(intent);
	}
}
