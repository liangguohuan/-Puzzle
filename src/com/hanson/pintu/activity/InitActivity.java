package com.hanson.pintu.activity;

import android.content.Intent;
import android.os.Bundle;

import com.hanson.pintu.R;
import com.hanson.pintu.util.CommFunc;
import com.hanson.pintu.util.DesUtil;
import com.hanson.pintu.util.FileUtil;
import com.hanson.pintu.util.GlobalVariable;

public class InitActivity extends basicInActivity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.test);
		//encryptFile();
		Intent intent = new Intent(this, ListActivity.class);
		startActivity(intent);
	}
	
	private void encryptFile() {
		DesUtil du = null;
		try {
			du = new DesUtil();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			String[] list = getAssets().list("");
			for (String fileName : list) {
				//CommFunc.Log("wh", string + "");
				if(fileName.endsWith(".jpg")) {
					byte[] buffer = FileUtil.readFileByAllByteFromInputStream(getAssets().open(fileName));
					buffer = du.encrypt(buffer);
					FileUtil.write2SDFromByte(GlobalVariable.PINTU_IMAGE_DIR, fileName, buffer);
				}
				Thread.sleep(1000);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			CommFunc.Log("wh", "error read: " + e.getMessage());
		}
	}
}
