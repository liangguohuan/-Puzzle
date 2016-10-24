package com.hanson.pintu.view;

import android.content.Context;
import android.view.MotionEvent;
import android.widget.LinearLayout;

import com.hanson.pintu.R;
import com.hanson.pintu.util.CommFunc;

public class SlideLayout extends LinearLayout {

	public SlideLayout(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		this.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		this.setBackgroundResource(R.drawable.row_bg);
	}
	float x_temp01 = 0.0f;
	float y_temp01 = 0.0f;
	float x_temp02 = 0.0f;
	float y_temp02 = 0.0f;

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		// 获得当前坐标
		float x = event.getX();
		float y = event.getY();
		CommFunc.Log("wh", "on touch event ..................");
		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN: {
				CommFunc.Log("wh", "on down event ..................");
				x_temp01 = x;
				y_temp01 = y;
			}
				break;
			case MotionEvent.ACTION_UP: {
				CommFunc.Log("wh", "on up event ..................");
				x_temp02 = x;
				y_temp02 = y;
	
				if (x_temp01 != 0 && y_temp01 != 0)
				{
	
					if (x_temp01 - x_temp02 > 100)// 向左
					{
						//ActivityManager.toCollateActivity(this, fileName);
					}
					if (x_temp02 - x_temp01 > 100) // 向右
					{
						//ActivityManager.toCollateActivity(this, fileName);
					}
					CommFunc.Log("wh", "I am moving .....................");
				}
			}
				break;
	
		}
		return super.onTouchEvent(event);
	}
}
