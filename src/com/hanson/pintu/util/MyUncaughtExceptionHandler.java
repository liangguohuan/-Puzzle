package com.hanson.pintu.util;

import android.content.Context;
import android.os.Looper;

/**
 * 全局异常处理类
 * @author Administrator
 *
 */
public class MyUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {
	
	private Thread.UncaughtExceptionHandler mDefaultHandler;
	//private Context mContext;
	public MyUncaughtExceptionHandler(Context context) {
		this.mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
		//this.mContext = context;
	}

	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		if (!handleException(ex) && mDefaultHandler != null) {
			//如果用户没有处理则让系统默认的异常处理器来处理
			mDefaultHandler.uncaughtException(thread, ex);
		} else {
			//Sleep一会后结束程序
			try {
				//Thread.sleep(3000);
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			android.os.Process.killProcess(android.os.Process.myPid());
			System.exit(0);
		}
	}
	
	private boolean handleException(Throwable ex) {
		if (ex == null) {
			return true;
		}
		//final String msg = ex.getLocalizedMessage();
		//使用Toast来显示异常信息
		new Thread() {
			@Override
			public void run() {
				Looper.prepare();
				//Toast.makeText(mContext, "程序出错啦:" + msg, Toast.LENGTH_LONG).show();
				Looper.loop();
			}

		}.start();
		return true;
	}
}
