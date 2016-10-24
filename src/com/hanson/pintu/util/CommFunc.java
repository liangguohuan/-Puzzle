package com.hanson.pintu.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DecimalFormat;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import com.hanson.pintu.data.pojo.ListTable;

public class CommFunc {
	//
	public final static String FloatToPercent(float val) {
		val = getTwoPointFloat(val);
		return (int)(val*100) + "%";
	}
	//将浮点数转换为保留两位小数
	public final static float getTwoPointFloat(float val){
    	DecimalFormat df = new DecimalFormat("#00");
    	df.setMaximumFractionDigits(2);
    	df.setMinimumFractionDigits(2);
    	return Float.parseFloat(df.format(val));
	}
	//日志调试
	public final static void Log(String tag, String string) {
		Log.d(tag, string);
	}
	//取得Sdcard目录
	public final static String getSdcardPath() {
		if(Environment.getExternalStorageState().equals("shared"))
			return "";
		return Environment.getExternalStorageDirectory() + "/";
	}
	
	public final static String[] getArrFileName(String fileNameList) {
		String[] arrFileName = fileNameList.split(",");
		return arrFileName;
	}
	
	public final static String getFileName(ListTable lt) {
		String[] arrFileName = getArrFileName(lt.getFilename());
		return arrFileName[0];
	}
	
	public final static String getFileNameList(String[] arrFileName) {
		String reStr = "";
		for (String string : arrFileName) {
			reStr += string + ",";
		}
		reStr = reStr.substring(0, reStr.length() - 1);
		return reStr;
	}
	
	public final static InputStream getInputStream(Context context, String fileName) throws Exception {
		InputStream is = null;
		try {
			if(fileName.contains("sdcard")) {
				is = FileUtil.getTempFileFromPackage(context, "play.jpg", fileName);
			}
			else {
				if(FileUtil.isFileExist(GlobalVariable.PINTU_IMAGE_DIR + fileName)) {
					is = FileUtil.getTempFileFromPackage(context, "play.jpg", GlobalVariable.PINTU_IMAGE_DIR + fileName);
				}
				else {
					is = context.getAssets().open(fileName);
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			is = context.getAssets().open("default.jpg");
		}
		return is;
	}
	
	public final static String getThumbImagePath(ListTable lt) {
		String fileName = getFileName(lt);
		if(fileName.contains("sdcard")) {
			try {
				fileName = URLEncoder.encode(fileName, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		fileName  = GlobalVariable.PINTU_IMAGE_THUMB_DIR + fileName;
		return fileName;
	}
	
	public final static void createThumbImage(final Bitmap bitmap, ListTable lt) throws Exception {
		if(!CommFunc.getSdcardPath().equals("")) {
			FileUtil.createDir(GlobalVariable.PINTU_IMAGE_THUMB_DIR);
			final String absoluteFilePath  = getThumbImagePath(lt);
			final File file = new File(absoluteFilePath);
			if(!FileUtil.isFileExist(absoluteFilePath)) {
				new Thread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						OutputStream output = null;
						try {
							output = new FileOutputStream(file);
						} catch (FileNotFoundException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						bitmap.compress(Bitmap.CompressFormat.JPEG, 50, output);
						try {
							output.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						CommFunc.Log("wh", "thread: -------------> " + absoluteFilePath);
					}
				}).start();
			}
		}
	}
	
	public final static Bitmap createThumbImage(Context context, ListTable lt) throws Exception {
		String fileName = CommFunc.getFileName(lt);
		String absoluteFilePath  = CommFunc.getThumbImagePath(lt);
		Bitmap newBitmap = null;
		int width = getThumbWidth(context);
		if(!FileUtil.isFileExist(absoluteFilePath)) {
			InputStream is = null;
			is = CommFunc.getInputStream(context, fileName);
			Bitmap bm = BitmapFactory.decodeStream(is);
			newBitmap = Bitmap.createScaledBitmap(bm, width, width, true);
			CommFunc.createThumbImage(newBitmap, lt);
			bm.recycle();
			is.close();
		}
		return newBitmap;
	}
	
	public final static void deleteThumbImage(ListTable lt) {
		String fileNameList = lt.getFilename();
		String[] arrFileName = CommFunc.getArrFileName(fileNameList);
		String fileName = arrFileName[0];
		String absoluteFilePath  = GlobalVariable.PINTU_IMAGE_THUMB_DIR + fileName;
		try {
			FileUtil.delFile(absoluteFilePath);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public final static int getThumbWidth(Context context) {
		int width = getScreenWidth(context)/2;
		return width - 10*2;
	}
	
	//获取屏幕宽度
	public final static int getScreenWidth(Context context){
    	int screenWidth = ((Activity) context).getWindowManager().getDefaultDisplay().getWidth();
    	return screenWidth;
    }
    
    //获取屏幕高度
	public final static int getScreenHeight(Context context){
    	int screenHeight = ((Activity) context).getWindowManager().getDefaultDisplay().getHeight();
    	return screenHeight;
    }
}
