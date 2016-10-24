package com.hanson.pintu.activity;

import java.io.InputStream;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;

import com.hanson.pintu.ActivityManager;
import com.hanson.pintu.R;
import com.hanson.pintu.basicActivity;
import com.hanson.pintu.data.helper.ListTableHelper;
import com.hanson.pintu.data.pojo.ListTable;
import com.hanson.pintu.data.store.ListTableStore;
import com.hanson.pintu.util.CommFunc;
import com.hanson.pintu.util.GlobalVariable;

public class basicInActivity extends basicActivity {
	protected ListTable listTable = null;
	private String fileNameList;
	protected final static int DIALOG_EXIT = 1000;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		menu.add(0, GlobalVariable.MENU_MEDIA_IMAGES, 0, getString(R.string.media_images)).setIcon(GlobalVariable.MENU_MEDIA_IMAGES_ICON);
		menu.add(0, GlobalVariable.MENU_LIST, 0, getString(R.string.list)).setIcon(GlobalVariable.MENU_LIST_ICON);
		menu.add(0, GlobalVariable.MENU_HELP, 0, getString(R.string.help)).setIcon(GlobalVariable.MENU_HELP_ICON);
		menu.add(0, GlobalVariable.MENU_EXIT, 0, getString(R.string.exit)).setIcon(GlobalVariable.MENU_EXIT_ICON);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
			case GlobalVariable.MENU_MEDIA_IMAGES:
				requestGalleryImage();
				break;
			case GlobalVariable.MENU_LIST:
				ActivityManager.toListActivity(this, listTable);
				break;
			case GlobalVariable.MENU_HELP:
				//测试用来倒数据，正式版本时，删除下面代码，把 BasicDBHelper 中 test_sql 改为 init_sql, 最好还要把 initDB, getDataBase 函数设置为 private
				/*BasicDBHelper dbHelper = new BasicDBHelper(this);
				SQLiteDatabase db = dbHelper.getDataBase();
				dbHelper.initDB(db);*/
				ActivityManager.toAboutActivity(this);
				break;
			case GlobalVariable.MENU_EXIT:
				showDialog(DIALOG_EXIT);
				break;
			case GlobalVariable.MENU_LOAD_TYPE_MANAGER:
				Bundle bundle = new Bundle();
				ActivityManager.toTypeListOrderActivity(this, bundle);
				break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		// TODO Auto-generated method stub
		switch (id) {
		case DIALOG_EXIT:
			 return new AlertDialog.Builder(this)
			 .setTitle(R.string.warmTip)
			 .setMessage(R.string.exitTip)
			 .setPositiveButton(getString(R.string.ok), new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					exitApp();
				}
			})
			 .setNegativeButton(getString(R.string.cancel), new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					
				}
			})
			.create();
		default:
			break;
		}
		return super.onCreateDialog(id);
	}
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	// TODO Auto-generated method stub
    	if(requestCode == 100 && data != null){
    		String localUri = data.getData().getPath();
    		if(!localUri.equals("")) {
	    		String id = localUri.substring(localUri.lastIndexOf("/") + 1);
	    		String [] proj={MediaStore.Images.Media.DATA}; 
	            Cursor mCursor = managedQuery( MediaStore.Images.Media.EXTERNAL_CONTENT_URI,proj, "_ID=?", new String[]{id}, null);  
	            int column_index = mCursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA); 
	            mCursor.moveToPosition(0); 
	            fileNameList = mCursor.getString(column_index); 
	            mCursor.close();
	            try {
					listTable = setRecord(fileNameList);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Bundle bundle = new Bundle();
				bundle.putBoolean("bMediaImage", true);
				bundle.putSerializable("listTable", listTable);
	            ActivityManager.toMainActivity(this, bundle);
	            //发送广播通知ListActivity
	            Intent intent = new Intent(GlobalVariable.GET_MEDIA_IMAGES_PLAY);
	            intent.putExtra("listTable", listTable);
	            sendBroadcast(intent);
    		}
    	}    
    	super.onActivityResult(requestCode, resultCode, data);
    }
    
    private ListTable setRecord(String filename) throws Exception {
    	ListTableHelper lth = new ListTableHelper(this);
    	long id = lth.insert(filename, 5, 1, 1, true);
    	ListTableStore lts = new ListTableStore(this);
    	if(id == -1) {
    		id = lts.getInfo(filename).getId();
    	}
    	ListTable lt = lts.getInfo(id);
    	//创建缩略图
    	CommFunc.createThumbImage(this, lt);
    	return lt;
    }
    
    private void requestGalleryImage()
    {
      Intent localIntent = new Intent("android.intent.action.GET_CONTENT");
      localIntent.addCategory("android.intent.category.OPENABLE");
      localIntent.setType("image/*");
      startActivityForResult(localIntent, 100);
    }
    
    protected InputStream getInputStream(String fileName) {
    	InputStream is = null;
		try {
			is = CommFunc.getInputStream(basicInActivity.this, fileName);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return is;
    }
}
