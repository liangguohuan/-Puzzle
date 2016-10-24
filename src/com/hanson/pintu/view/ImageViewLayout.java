package com.hanson.pintu.view;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.hanson.pintu.R;
import com.hanson.pintu.util.CommFunc;
import com.hanson.pintu.util.SoundPlayer;
import com.hanson.pintu.view.pojo.ImageViewLayoutAttr;

public class ImageViewLayout extends LinearLayout {

	private ArrayList<Bitmap> listBitMap = new ArrayList<Bitmap>();
	private ArrayList<ImageView> listImageView = new ArrayList<ImageView>();
	private int blockWidth;
	private int blockHeight;
	private static int step = 1;
	private ImageView firstClickView;
	private ImageView secondClickView;
	private LinearLayout linear;
	private int totalCount;
	private boolean completeTag = false;
	private OnCompleteViewClick completeClick = null;
	private OnCompleteView completeView;
	private OnItemClick itemClick;
	
	private ImageViewLayoutAttr ivla;
	private Matrix matrix;
	
	public ImageViewLayoutAttr getImageViewLayoutAttr() {
		return ivla;
	}
	public void setImageViewLayoutAttr(ImageViewLayoutAttr ivla) {
		this.ivla = ivla;
		ivla.setResources(getResources());
	}

	public ImageViewLayout(Context context, ImageViewLayoutAttr ivla) {
		super(context);
		setImageViewLayoutAttr(ivla);
	}
	
	//每个单元格被点击时，触发接口中定义函数
	public interface OnItemClick {
		public void callBack(ImageViewLayout ivl);
	}
	
	public void setOnitemClick(OnItemClick itemClick) {
		this.itemClick = itemClick;
	}
	
	public void doItemClick() {
		itemClick.callBack(this);
	}
	
	// 内部接口，完成拼图时，触发接口中定义函数
	public interface OnCompleteView {
		public void callBack(ImageViewLayout ivl);
	}
	
	public void setCompleteView(OnCompleteView completeView) {
		this.completeView = completeView;
	}
	
	private void doCompleteView() {
		completeTag = false;
		completeView.callBack(this);
		//清除
		ivla.setShuffCellRecord(null);
	}
	
	// 内部接口，当点击完成拼图时，触发接口中定义函数
	public interface OnCompleteViewClick {
		public void callBack(ImageViewLayout ivl);
	}
	
	public void setCompleteViewClick(OnCompleteViewClick itemClick) {
		this.completeClick = itemClick;
	}
	
	public void doCompleteViewClick() {
		completeTag = false;
		//ivla.setStep(0);
		if (completeClick != null)
			completeClick.callBack(this);
	}

	private void init() {
		//CommFunc.Log("wh", "init .........................");
		linear = new LinearLayout(getContext());
		linear.setOrientation(LinearLayout.VERTICAL);
		linear.setBackgroundResource(R.drawable.white_corner);
		linear.setPadding(ivla.getPadding(), ivla.getPadding(), ivla.getPadding(), ivla.getPadding());
		
		releaseResource();
		
		float width = ivla.getWidth();
		float height = ivla.getHeight();
		
		if((width == 0 && height == 0) || ivla.isbRefresh()) {
			DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
			width = displayMetrics.widthPixels;
			height = displayMetrics.heightPixels;
			width -= ivla.getPadding()*4;
			height -= ivla.getPadding()*4;
		}
		
		if(completeTag) {
			width += 2*ivla.getNumColoum();
			height += 2*ivla.getNumRow();
		}
		else {
			width -= 2*ivla.getNumColoum();
			height -= 2*ivla.getNumRow();
		}
		
		if(ivla.isJustGetCompleteView()) {
			width -= 2*ivla.getNumColoum();
			height -= 2*ivla.getNumRow();
		}
		
		ivla.setWidth(width);
		ivla.setHeight(height);
		
		matrix = new Matrix();
		matrix.postScale(ivla.getScaleWidth(), ivla.getScaleHeight());
		//matrix.setRotate(90);
		
		blockWidth = ivla.getBitmapWidth() / ivla.getNumColoum();
		blockHeight = ivla.getBitmapHeight() / ivla.getNumRow();
		totalCount = ivla.getNumColoum() * ivla.getNumRow();
		step = 1;
		//CommFunc.Log("wh", "totalCount: " + totalCount);
	}
	
	public void generateView() {
		if(!ivla.isJustGetCompleteView()) {
			init();
		}
		if(ivla.isJustGetCompleteView()){
			View child = generateCollateImageView();
			this.addView(child);
		}else{
			generateBlockView();
		}
	}
	
	private void generateBlockView() {
		this.removeAllViews();
		generateImageViewCells(getContext());
		shuffleImageView();
	}
	
	//取得对照图片
	private View generateCollateImageView() {
		completeTag = true;
		init();
    	ImageView iv = new ImageView(getContext());
    	Bitmap bitmapCollate = Bitmap.createBitmap(ivla.getBitmap(), 0, 0, ivla.getBitmapWidth(),	ivla.getBitmapHeight(), matrix, true);
		ivla.setBitmapCollate(bitmapCollate);
		iv.setImageBitmap(ivla.getBitmapCollate());
		linear.removeAllViews();
		linear.addView(iv);
		completeTag = false;
		return linear;
	}
	
	float x_temp01 = 0.0f;
	float x_temp02 = 0.0f;
	private void generateImageViewCells(Context context) {
		int index = 0;
		for (int i = 0; i < ivla.getNumRow(); i++) {
			LinearLayout linearIn = new LinearLayout(context);
			linearIn.setOrientation(LinearLayout.HORIZONTAL);
			for (int j = 0; j < ivla.getNumColoum(); j++) {
				int x = j * blockWidth;
				int y = i * blockHeight;
				Bitmap mapi = Bitmap.createBitmap(ivla.getBitmap(), x, y, blockWidth,	blockHeight, matrix, true);
				ImageView iv = new ImageView(context);
				iv.setImageBitmap(mapi);
				iv.setTag(new int[]{index, x, y});
				iv.setOnTouchListener(new OnTouchListener() {
					@Override
					public boolean onTouch(View v, MotionEvent event) {
						// TODO Auto-generated method stub
						// 获得当前坐标
						float x = event.getX();
						boolean bHandled = false;
						switch (event.getAction()) {
						case MotionEvent.ACTION_DOWN:
							bHandled = true;
							x_temp01 = x;
							break;
						case MotionEvent.ACTION_CANCEL:
							bHandled = true;
							break;
						case MotionEvent.ACTION_UP:
							bHandled = true;
							x_temp02 = x;
							if (x_temp01 - x_temp02 > 30 || x_temp02 - x_temp01 > 30){
								bHandled = false;
							}
							else{
								click(v);
							}
							x_temp01 = 0.0f;
							x_temp02 = 0.0f;
							break;
						default:
							break;
						}
						return bHandled;
					}
				});
				iv.setPadding(1, 1, 1, 1);
				listBitMap.add(mapi);
				listImageView.add(iv);
				linearIn.addView(iv);
				index++;
			}
			linear.addView(linearIn);
		}
		this.addView(linear);
	}
	
	private void completeHanddle() {
		doCompleteView();
		this.removeAllViews();
		View child = generateCollateImageView();
		child.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(ivla.isbSound())
					SoundPlayer.sound_next(getContext());
				doCompleteViewClick();
			}
		});
		this.addView(child);
	}
	
	private void shuffleImageView() {
		//CommFunc.Log("wh", "totalCount: " + totalCount + "listImageView.size: " + listImageView.size());
		ArrayList<int[]> shuffCellRecord = new ArrayList<int[]>();
		
		int randomCount = ivla.getShuffCellRecord() != null ? totalCount : totalCount*2;
		//randomCount = 2;
		for (int j = 0; j < randomCount; j++) {
			int index1 = 0;
			int index2 = 0;
			
			if(ivla.getShuffCellRecord() != null) {
				int[] arrTemp = ivla.getShuffCellRecord().get(j);
				index1 = arrTemp[0];
				index2 = arrTemp[1];
			}
			else {
				index1 = (int) Math.floor(Math.random() * (totalCount - 1));
				index2 = (int) Math.floor(Math.random() * (totalCount - 1));
			}
			
			shuffCellRecord.add(new int[]{index1, index2});
			if(index1 != index2)
				swapImageView(listImageView.get(index1), listImageView.get(index2));
		}
		ivla.setShuffCellRecord(shuffCellRecord);
		ivla.setCompletePercent(getCompleteSatus());
	}
	
	private boolean checkSwapImageView(ImageView iv1, ImageView iv2) {
		int[] tag1 = (int[])iv1.getTag();
		int[] tag2 = (int[])iv2.getTag();
		int x1 = tag1[1];
		int y1 = tag1[2]; 
		int x2 = tag2[1];
		int y2 = tag2[2];
		int distanceLeft = Math.abs(x1 - x2); 
		int distanceTop = Math.abs(y1 - y2); 
		if((distanceLeft < (blockWidth + 1) && y1 == y2) || (distanceTop < (blockHeight + 1) && x1 == x2))
			return true;
		return false;
	}
	
	private void click(View v) {
		if(step % 2 == 0) {
			secondClickView = (ImageView)v;
			if(ivla.isbSwapRound()) {
				if(checkSwapImageView(firstClickView, secondClickView)) {
					swapImageView(firstClickView, secondClickView);
					firstClickView.setBackgroundColor(0);
				}else{
					step--;
				}
			}else {
				swapImageView(firstClickView, secondClickView);
				firstClickView.setBackgroundColor(0);
			}
			if(ivla.isbSound())
				SoundPlayer.sound_step2(getContext());
		}else {
			firstClickView = (ImageView)v;
			firstClickView.setBackgroundColor(0xffff0000);
			if(ivla.isbSound())
				SoundPlayer.sound_step2(getContext());
		}
		
		if(firstClickView != secondClickView && firstClickView != null && secondClickView != null && step % 2 == 0) {
			ivla.setStep(ivla.getStep() + 1);
			ivla.setCompletePercent(getCompleteSatus());
		}
		
		//监测顺序变化
		if(getImageViewCellsOrder() && step > 1) {
			completeHanddle();
			if(ivla.isbSound())
				SoundPlayer.sound_complete_level_1(getContext());
		}
		
		doItemClick();
		step++;
	}

	private boolean getImageViewCellsOrder() {
		for (int j = 0; j < totalCount; j++) {
			int[] tag = (int[])listImageView.get(j).getTag();
			if(tag[0] != j)
				return false;
		}
		return true;
	}
	
	private String getCompleteSatus() {
		int i = 0;
		for (int j = 0; j < totalCount; j++) {
			int[] tag = (int[])listImageView.get(j).getTag();
			if(tag[0] != j)
				i++;
		}
		float val = (float)(totalCount - i)/totalCount;
		return CommFunc.FloatToPercent(val);
	}
	
	private void swapImageView(ImageView iv1, ImageView iv2) {
		int[] tag1 = (int[])iv1.getTag();
		int[] tag2 = (int[])iv2.getTag();
		int index1 = tag1[0];
		int index2 = tag2[0];
		tag1[0] = index2;
		tag2[0] = index1;
		iv1.setImageBitmap(listBitMap.get(index2));
		iv1.setTag(tag1);
		iv2.setImageBitmap(listBitMap.get(index1));
		iv2.setTag(tag2);
	}
	
	//释放资源(创建时)
	private void releaseResource() {
		for (Bitmap bitmap : listBitMap) {
			bitmap.recycle();
		}
		listBitMap.clear();
		listImageView.clear();
	}
	
	//释放所有内存(销毁时)
	public void recycle() {
		if(ivla.getBitmap() != null)
			ivla.getBitmap().recycle();
		if(ivla.getBitmapCollate() != null)
			ivla.getBitmapCollate().recycle();
		releaseResource();
	}
}
