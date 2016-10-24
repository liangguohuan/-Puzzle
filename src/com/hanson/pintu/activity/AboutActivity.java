package com.hanson.pintu.activity;

import android.os.Bundle;
import android.webkit.WebView;

import com.hanson.pintu.R;
import com.hanson.pintu.util.GlobalVariable;

public class AboutActivity extends basicInActivity {
	private WebView webView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about);
		webView = (WebView) findViewById(R.id.webview);
		webView.setBackgroundColor(0);
		try {
			// 本地文件处理(如果文件名中有空格需要用+来替代)
			webView.loadUrl(GlobalVariable.HELP_HTML_PATH);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
