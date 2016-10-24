package com.hanson.pintu;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.view.Window;
import android.view.WindowManager;

import com.hanson.pintu.data.BasicDBHelper;
import com.hanson.pintu.util.GlobalVariable;
import com.hanson.pintu.util.MyUncaughtExceptionHandler;

public class basicActivity extends Activity {
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        humanHanddle();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);  
        requestWindowFeature(Window.FEATURE_NO_TITLE);  
        createDB();
        registerReceiver(handdleExitAppReceiver, new IntentFilter(GlobalVariable.ACTIVITY_EXIT_RECEIVER));
		// TODO Auto-generated constructor stub
    }
    
    @Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unregisterReceiver(handdleExitAppReceiver);
	}



	HandlerThread localHandlerThread;
	Handler handler;

	private void humanHanddle() {
		localHandlerThread = new HandlerThread("humanHanddle");
		localHandlerThread.start();
		handler = new Handler(localHandlerThread.getLooper());
		Thread.setDefaultUncaughtExceptionHandler(new MyUncaughtExceptionHandler(this));
	}
	
	private void createDB() {
		new BasicDBHelper(this).getReadableDatabase().close();
	}
    
    //获取屏幕宽度
	protected final int getScreenWidth(){
    	int screenWidth = this.getWindowManager().getDefaultDisplay().getWidth();
    	return screenWidth;
    }
    
    //获取屏幕高度
	protected final int getScreenHeight(){
    	int screenHeight = this.getWindowManager().getDefaultDisplay().getHeight();
    	return screenHeight;
    }
    
    protected final void exitApp() {
		//android.os.Process.killProcess(android.os.Process.myPid());
		// 获取当前系统版本
		/*int SDK_VERSION = android.os.Build.VERSION.SDK_INT;
		final android.app.ActivityManager manager = (android.app.ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		manager.restartPackage(getPackageName());
		finish();
		if (SDK_VERSION == 8) {
			//制造异常退出
			setTitle(0);
		} else {
			android.os.Process.killProcess(android.os.Process.myPid());
			System.exit(0);
		}*/
    	Intent intentSend = new Intent(GlobalVariable.ACTIVITY_EXIT_RECEIVER);
		sendBroadcast(intentSend);
	}
    
    protected BroadcastReceiver handdleExitAppReceiver = new BroadcastReceiver(){
		@Override
		public void onReceive(Context context, Intent intent) {
			if(intent.getAction().equals(GlobalVariable.ACTIVITY_EXIT_RECEIVER)){
				finish();
				//android.os.Process.killProcess(android.os.Process.myPid());
				//System.exit(0);
			}
		}
	};
}
