package com.hanson.pintu.activity;

import java.io.InputStream;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;

import com.hanson.pintu.util.GlobalVariable;
import com.hanson.pintu.view.ImageViewLayout;
import com.hanson.pintu.view.pojo.ImageViewLayoutAttr;

public class CollateActivity extends basicInActivity {
	private String fileName;
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		// TODO Auto-generated constructor stub
        fileName = getIntent().getStringExtra("fileName");
        newCollateView();
        
        registerReceiver(handdleCollateReceiver, new IntentFilter(GlobalVariable.COLLATE_ACTIVITY_PLAY_RECEIVER));
    }
    
    @Override
    protected void onResume() {
    	// TODO Auto-generated method stub
    	super.onResume();
    }
    
    @Override
    protected void onDestroy() {
    	// TODO Auto-generated method stub
    	unregisterReceiver(handdleCollateReceiver);
    	super.onDestroy();
    }
    
    private void goBack() {
    	finish();
    }
    
    private void newCollateView() {
        InputStream is = getInputStream(fileName);
        ImageViewLayoutAttr ivla = new ImageViewLayoutAttr();
        ImageViewLayout ivl = new ImageViewLayout(this, ivla);
        ivl.getImageViewLayoutAttr().setJustGetCompleteView(true);
        ivl.getImageViewLayoutAttr().setInputStream(is);
        ivl.generateView();
        ivl.setGravity(Gravity.CENTER_HORIZONTAL);
		setContentView(ivl);
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

		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				x_temp01 = x;
				y_temp01 = y;
				break;
			case MotionEvent.ACTION_UP:
				x_temp02 = x;
				y_temp02 = y;
	
				if (x_temp01 != 0 && y_temp01 != 0)
				{
	
					if (x_temp01 - x_temp02 > 50)// 向左
					{
						goBack();
					}
					if (x_temp02 - x_temp01 > 50) // 向右
					{
						goBack();
					}
				}
				break;
	
		}
		return super.onTouchEvent(event);
	}
	
	private BroadcastReceiver handdleCollateReceiver = new BroadcastReceiver(){
		@Override
		public void onReceive(Context context, Intent intent) {
			if(intent.getAction().equals(GlobalVariable.COLLATE_ACTIVITY_PLAY_RECEIVER)){
				String fileNameRe = intent.getStringExtra("fileName");
				if(!fileNameRe.equals(fileName)) {
					fileName = fileNameRe;
					newCollateView();
				}
			}
		}
	};
}
