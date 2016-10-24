package com.hanson.pintu.data.store;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import com.hanson.pintu.data.pojo.Setting;
import com.hanson.pintu.util.CommFunc;
import com.hanson.pintu.util.GlobalVariable;

public class SettingStore {
	/**
	 * 
	 */
	public SettingStore() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public final static void write(Setting setting) throws Exception {
		String filename = GlobalVariable.PINTU_ROOT_DIR + "setting.ser";
		FileOutputStream fos = new FileOutputStream(filename);
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		oos.writeObject(setting);
		oos.close();
		fos.close();
	}
	
	public final static Setting read() {
		Setting setting = null;
		try {
			String filename = GlobalVariable.PINTU_ROOT_DIR + "setting.ser";
			FileInputStream fis = new FileInputStream(filename);
			ObjectInputStream ois = new ObjectInputStream(fis);
			setting = (Setting)ois.readObject();
			ois.close();
			fis.close();
		} catch (Exception e) {
			// TODO: handle exception
			setting = new Setting();
			CommFunc.Log("XYZ", "Read is exception ...");
		}
		
		return setting;
	}
}
