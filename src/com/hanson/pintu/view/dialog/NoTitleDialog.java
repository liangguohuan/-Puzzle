package com.hanson.pintu.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hanson.pintu.R;

public class NoTitleDialog {

	LinearLayout layout;
	private TextView view;
	private Dialog dialog;
	private float fontSize = 14;
	private OnClickCallBack clickCallBack;
	
	public NoTitleDialog(Context context) {
		// TODO Auto-generated constructor stub
		dialog = new Dialog(context, R.style.noTitleDialog);
		layout = new LinearLayout(context);
		layout.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		layout.setGravity(Gravity.CENTER_VERTICAL);
		view = new TextView(context);
		view.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.FILL_PARENT));
		view.setGravity(Gravity.CENTER_VERTICAL);
		view.setTextSize(fontSize);
		view.setPadding(10, 10, 10, 10);
		view.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				clickCallBack.callBack();
			}
		});
	}
	
	public NoTitleDialog setMessage(String msg) {
		view.setText(msg);
		layout.addView(view);
		dialog.setContentView(layout);
		return this;
	}
	
	public NoTitleDialog setOnclickCallBack(OnClickCallBack callBackClick) {
		this.clickCallBack = callBackClick;
		return this;
	}
	
	public interface OnClickCallBack {
		public void callBack();
	}
	
	public Dialog create() {
		return dialog;
	}
	
	public void show() {
		dialog.show();
	}
	
}
