package com.hanson.pintu.activity;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

import com.hanson.pintu.ActivityManager;
import com.hanson.pintu.R;
import com.hanson.pintu.data.pojo.Setting;
import com.hanson.pintu.data.store.SettingStore;
import com.hanson.pintu.util.GlobalVariable;

public class LoadActivity extends basicInActivity {
	private Setting setting;
	private Button startBtn;
	private CheckBox soundCheckBox;
	private CheckBox historyCheckBox;
	private CheckBox randomCheckBox;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.load);
		
		startBtn = (Button) findViewById(R.id.start);
		soundCheckBox = (CheckBox) findViewById(R.id.sound);
		historyCheckBox = (CheckBox) findViewById(R.id.history);
		randomCheckBox = (CheckBox) findViewById(R.id.random);
		
		soundCheckBox.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				setting.setbSound(soundCheckBox.isChecked());
				try {
					SettingStore.write(setting);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		
		
		startBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				boolean bHistory = historyCheckBox.isChecked();
				boolean bRandom = randomCheckBox.isChecked();
				Bundle bundle = new Bundle();
				bundle.putBoolean("random", bRandom);
				bundle.putBoolean("history", bHistory);
				ActivityManager.toMainActivity(LoadActivity.this, bundle);
			}
		});
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		initSound();
		super.onResume();
	}
	
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if(keyCode == KeyEvent.KEYCODE_BACK){
			showDialog(DIALOG_EXIT);
		}
		return false;
		//return super.onKeyUp(keyCode, event);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		menu.add(0, GlobalVariable.MENU_LOAD_TYPE_MANAGER, 0, getString(R.string.typeManager)).setIcon(GlobalVariable.MENU_LOAD_TYPE_MANAGER_ICON);
		return super.onCreateOptionsMenu(menu);
	}

	private void initSound() {
		//取配置数据
		setting = SettingStore.read();
		boolean bSound = setting.isbSound();
		soundCheckBox.setChecked(bSound);
	}
}
