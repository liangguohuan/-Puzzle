package com.hanson.pintu.view.pojo;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

import com.hanson.pintu.data.pojo.Setting;

public class ImageViewLayoutAttr {
	private Resources resources;
	private float scaleWidth;
	private float scaleHeight;
	private boolean justGetCompleteView = false;
	private int numColoum = 4;
	private int numRow = 4;
	private int resId;
	private String fileName;
	private InputStream inputStream;
	private float width = 0;
	private float height = 0;
	private Bitmap bitmap;
	private Bitmap bitmapCollate;
	private int bitmapWidth;
	private int bitmapHeight;
	private int padding = 3;
	private boolean bSound = true;
	private String endDesc;
	private ArrayList<int[]> shuffCellRecord;
	private boolean bSwapRound = false;
	private boolean bScreenSetting = false; // 必须在 setInputStream 之前调用才有效
	private boolean bRefresh = false;
	private int step = 0;
	private String completePercent;
	private String time;
	private Setting setting;

	public ImageViewLayoutAttr() {
	}

	public Resources getResources() {
		return resources;
	}

	public void setResources(Resources resources) {
		this.resources = resources;
	}

	public float getScaleWidth() {
		this.scaleWidth = getWidth() / getBitmapWidth();
		return scaleWidth;
	}

	public void setScaleWidth(float scaleWidth) {
		this.scaleWidth = scaleWidth;
	}

	public float getScaleHeight() {
		this.scaleHeight = getHeight() * 7 / 8 / getBitmapHeight();
		return scaleHeight;
	}

	public void setScaleHeight(float scaleHeight) {
		this.scaleHeight = scaleHeight;
	}

	public boolean isJustGetCompleteView() {
		return justGetCompleteView;
	}

	public void setJustGetCompleteView(boolean justGetCompleteView) {
		this.justGetCompleteView = justGetCompleteView;
	}

	public int getNumColoum() {
		return numColoum;
	}

	public void setNumColoum(int numColoum) {
		this.numColoum = numColoum;
	}

	public int getNumRow() {
		return numRow;
	}

	public void setNumRow(int numRow) {
		this.numRow = numRow;
	}

	public int getResId() {
		return resId;
	}

	public void setResId(int resId) {
		this.resId = resId;
		setBitmap(BitmapFactory.decodeResource(getResources(), resId));

		this.fileName = "";
		this.inputStream = null;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
		setBitmap(BitmapFactory.decodeFile(fileName));

		this.resId = 0;
		this.inputStream = null;
	}

	public InputStream getInputStream() {
		return inputStream;
	}

	public void setInputStream(InputStream inputStream) {
		this.inputStream = inputStream;
		Bitmap bm = BitmapFactory.decodeStream(inputStream);
		if (this.bScreenSetting) {
			Matrix matrix = new Matrix();
			matrix.setRotate(90);
			Bitmap resizeBitmap = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(),
					bm.getHeight(), matrix, true);
			bm.recycle();
			setBitmap(resizeBitmap);
		} else {
			setBitmap(bm);
		}

		try {
			inputStream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.resId = 0;
		this.fileName = "";
	}

	public float getWidth() {
		return width;
	}

	public void setWidth(float width) {
		this.width = width;
	}

	public float getHeight() {
		return height;
	}

	public void setHeight(float height) {
		this.height = height;
	}

	public Bitmap getBitmap() {
		return bitmap;
	}

	public void setBitmap(Bitmap bitmap) {
		if (this.bitmap != null)
			this.bitmap.recycle();
		this.bitmap = bitmap;
		this.bitmapWidth = bitmap.getWidth();
		this.bitmapHeight = bitmap.getHeight();
	}

	public Bitmap getBitmapCollate() {
		return bitmapCollate;
	}

	public void setBitmapCollate(Bitmap bitmapCollate) {
		if (this.bitmapCollate != null) {
			this.bitmapCollate.recycle();
			getBitmap().recycle();
		}
		this.bitmapCollate = bitmapCollate;
	}

	public int getBitmapWidth() {
		return bitmapWidth;
	}

	public void setBitmapWidth(int bitmapWidth) {
		this.bitmapWidth = bitmapWidth;
	}

	public int getBitmapHeight() {
		return bitmapHeight;
	}

	public void setBitmapHeight(int bitmapHeight) {
		this.bitmapHeight = bitmapHeight;
	}

	public int getPadding() {
		return padding;
	}

	public void setPadding(int containerPadding) {
		this.padding = containerPadding;
	}

	public boolean isbSound() {
		return bSound;
	}

	public void setbSound(boolean bSound) {
		this.bSound = bSound;
	}

	public String getEndDesc() {
		return endDesc;
	}

	public void setEndDesc(String endDesc) {
		this.endDesc = endDesc;
	}

	public ArrayList<int[]> getShuffCellRecord() {
		return shuffCellRecord;
	}

	public void setShuffCellRecord(ArrayList<int[]> shuffCellRecord) {
		this.shuffCellRecord = shuffCellRecord;
	}

	public boolean isbSwapRound() {
		return bSwapRound;
	}

	public void setbSwapRound(boolean bSwapRound) {
		this.bSwapRound = bSwapRound;
	}

	public boolean isbScreenSetting() {
		return bScreenSetting;
	}

	public void setbScreenSetting(boolean bScreenSetting) {
		this.bScreenSetting = bScreenSetting;
	}

	public boolean isbRefresh() {
		return bRefresh;
	}

	public void setbRefresh(boolean bRefresh) {
		this.bRefresh = bRefresh;
	}

	public int getStep() {
		return step;
	}

	public void setStep(int step) {
		this.step = step;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public Setting getSetting() {
		return setting;
	}

	public void setSetting(Setting setting) {
		this.setting = setting;
		setbSwapRound(setting.getMode().equals("easy") ? false : true);
		setbScreenSetting(setting.getScreen().equals("vertical") ? false : true);
		setNumColoum(setting.getNumColoum());
		setNumRow(setting.getNumRow());
		setbSound(setting.isbSound());
	}

	public String getCompletePercent() {
		return completePercent;
	}

	public void setCompletePercent(String completePercent) {
		this.completePercent = completePercent;
	}
	
}
