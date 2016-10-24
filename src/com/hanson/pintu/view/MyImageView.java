package com.hanson.pintu.view;

import com.hanson.pintu.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.widget.ImageView;

public class MyImageView extends ImageView {

	// fields
	private Paint pt = new Paint();
	private RectF rect = new RectF();
	private String text;
	private boolean bSelected = false;
	private boolean bStart = false;
	private int numSign = 0;
	
	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
		invalidate();
	}

	public boolean isbSelected() {
		return bSelected;
	}

	public void setbSelected(boolean bSelected) {
		this.bSelected = bSelected;
		invalidate();
	}

	public boolean isbStart() {
		return bStart;
	}

	public void setbStart(boolean bStart) {
		this.bStart = bStart;
		invalidate();
	}

	public int getNumSign() {
		return numSign;
	}

	public void setNumSign(int numSign) {
		this.numSign = numSign;
	}

	public MyImageView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void setImageBitmap(Bitmap bm) {
		// TODO Auto-generated method stub
		super.setImageBitmap(bm);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		// init rectangles
		rect.set(0, 0, this.getWidth(), this.getHeight());
		rect.inset(1, 1);

		// drawing
		if(text != null)
			drawTypeText(canvas);
	}
	
	private void drawTypeText(Canvas canvas) {
		// background
		//pt.setColor(0xff000000);
		//canvas.drawRect(rect, pt);

		// text
		pt.setTypeface(null);
		pt.setTextSize(18);
		pt.setAntiAlias(true);
		//pt.setFakeBoldText(true);
		pt.setColor(0xffff0000);

		final String sDayName = text;

		int iTextPosX = (int) rect.right - (int) pt.measureText(sDayName);
		int iTextPosY = (int) rect.bottom + (int) (-pt.ascent()) - getTextHeight();

		iTextPosX -= ((int) rect.width() >> 1) - ((int) pt.measureText(sDayName) >> 1);
		iTextPosY -= ((int) rect.height() >> 1) - (getTextHeight() >> 1);
		
		canvas.drawText(sDayName, iTextPosX, iTextPosY, pt);
		
		if(bSelected) {
			Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.select);
			canvas.drawBitmap(bitmap, 5, 5, pt);
		}
		if(bStart) {
			Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.start);
			canvas.drawBitmap(bitmap, 5, 5, pt);
		}
		if(numSign > 0) {
			final String numStr = numSign + "";
			iTextPosX = 5; 
			iTextPosY = 20;
			canvas.drawText(numStr, iTextPosX, iTextPosY, pt);
		}
	}
	
	private int getTextHeight() {
		return (int) (-pt.ascent() + pt.descent());
	}
}
