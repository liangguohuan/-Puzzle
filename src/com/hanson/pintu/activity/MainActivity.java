package com.hanson.pintu.activity;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.CheckBox;
import android.widget.Chronometer;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.hanson.pintu.ActivityManager;
import com.hanson.pintu.R;
import com.hanson.pintu.data.pojo.ListTable;
import com.hanson.pintu.data.pojo.Setting;
import com.hanson.pintu.data.store.ListTableStore;
import com.hanson.pintu.data.store.SettingStore;
import com.hanson.pintu.util.CommFunc;
import com.hanson.pintu.util.GlobalVariable;
import com.hanson.pintu.view.ImageViewLayout;
import com.hanson.pintu.view.ImageViewLayout.OnCompleteViewClick;
import com.hanson.pintu.view.dialog.NoTitleDialog;
import com.hanson.pintu.view.pojo.ImageViewLayoutAttr;

public class MainActivity extends basicInActivity {
	private int slideWidth; 
	private Chronometer mChronometer;
	private TextView stepView;
	private TextView percentView;
	private ListTableStore lts = null;
	private ImageViewLayout ivl;
	private int autoIndex = 0;
	private String fileName = null;
	private String fileNameList = null;
	private String[] arrFileName = null;
	private InputStream is = null;
	private Setting setting;
	private String endDesc;
	private boolean bGame;
	private boolean bHistory;
	private boolean bRandom;
	private int playTag = 0;
	private boolean bShowDialogGame = false;
	private boolean bShowDialogEndDesc = false;
	private HashMap<Integer, ArrayList<int[]>> shuffCellRecord = new HashMap<Integer, ArrayList<int[]>>();
	private ArrayList<String> timerRecord = new ArrayList<String>();
	private ArrayList<String> stepRecord = new ArrayList<String>();
	private final static int DIALOG_END_DESC = 1;
	private final static int DIALOG_GAME_SHOW = 2;
	private final static int DIALOG_SETTINGS = 3;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		// TODO Auto-generated constructor stub
        setContentView(R.layout.main);
        
        stepView = (TextView) findViewById(R.id.step);
		percentView = (TextView) findViewById(R.id.percent);
        mChronometer = (Chronometer) findViewById(R.id.chronometer);
        
		lts = new ListTableStore(MainActivity.this);
        Bundle bundle = getIntent().getExtras();
        
    	setting = SettingStore.read();
        
        endDesc = bundle.getString("endDesc");
        bGame = bundle.getBoolean("game");
        bHistory = bundle.getBoolean("history");
        bRandom = bundle.getBoolean("random");
        CommFunc.Log("wh", "history: " + bHistory + ", bRandom: " + bRandom + ", bGame: " + bGame);
        if(bHistory) {
        	long id = -1;
        	if(setting.getHistoryRecord() != null)
        		id = setting.getHistoryRecord().getId();
        	if(id > 0) {
        		listTable = lts.getInfo(id);
        	}
        	else {
        		listTable = lts.getRandomOne();
        	}
        }
        else if(bRandom) {
        	listTable = lts.getRandomOne();
        }
        else {
        	listTable = (ListTable) getIntent().getSerializableExtra("listTable");
        }
        
        if(listTable == null){
        	/*ArrayList<ListTable> listTemp = lts.getList(-1, -1, null, "0,1");
        	listTable = listTemp.get(0);*/
        	listTable = lts.getRandomOne();
        }

        initData();
        
        ImageViewLayoutAttr ivla = new ImageViewLayoutAttr();
        if(bHistory 
        && setting != null 
        && setting.getHistoryRecord() != null
        && setting.getHistoryRecord().getRecord() != null) {
        	ivla.setShuffCellRecord(setting.getHistoryRecord().getRecord());
        }
        ivla.setSetting(setting);
        ivl = new ImageViewLayout(this, ivla);
        ivl.getImageViewLayoutAttr().setInputStream(is);
        ivl.generateView();
        //shuff cell record
		shuffCellRecord.put(autoIndex, ivl.getImageViewLayoutAttr().getShuffCellRecord());
        ivl.setGravity(Gravity.CENTER_HORIZONTAL);
        ivl.setOnitemClick(new ImageViewLayout.OnItemClick() {
			
			@Override
			public void callBack(ImageViewLayout ivl) {
				// TODO Auto-generated method stub
				initStepAndPercent();
			}
		});
        ivl.setCompleteView(new ImageViewLayout.OnCompleteView() {
			
			@Override
			public void callBack(ImageViewLayout ivl) {
				// TODO Auto-generated method stub
				//CommFunc.Log("wh", "autoIndex: " + autoIndex);		
				if(autoIndex == arrFileName.length - 1) {
					mChronometer.stop();
					timerRecord.add(mChronometer.getText().toString());
					stepRecord.add(ivl.getImageViewLayoutAttr().getStep() + "");
					//Toast.makeText(MainActivity.this, mChronometer.getText().toString(), Toast.LENGTH_SHORT).show();
				}
				
				if(bShowDialogEndDesc) {
					showDialog(DIALOG_END_DESC);
					bShowDialogEndDesc = false;
					endDesc = null;
					playTag = 0;
				}
				if(bShowDialogGame) {
					removeDialog(DIALOG_GAME_SHOW);
					showDialog(DIALOG_GAME_SHOW);
					shuffCellRecord.clear();
					timerRecord.clear();
					stepRecord.clear();
					//ivl.getImageViewLayoutAttr().setStep(0);
					bShowDialogGame = false;
					bGame = false;
					playTag = 0;
				}
			}
		});
		ivl.setCompleteViewClick(new OnCompleteViewClick() {
			
			@Override
			public void callBack(ImageViewLayout ivl) {
				// TODO Auto-generated method stub
				nextPlay();
			}
		});
		
		this.addContentView(ivl, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
		closeInputStream();
		
		initBottom();
		
		CommFunc.Log("wh", "on create ...............");
		
		registerReceiver(handdlePlayReceiver, new IntentFilter(GlobalVariable.MAIN_ACTIVITY_PLAY_RECEIVER));
    } 
    
    public boolean onKeyUp(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		/*if(keyCode == KeyEvent.KEYCODE_BACK){
			finish();
		}*/
		return super.onKeyUp(keyCode, event);
	}
    
    @Override
    protected void onResume() {
    	// TODO Auto-generated method stub
    	super.onResume();
    	/*CommFunc.Log("wh", "mainActivity onResume .............");
    	ListTable listTable2 = (ListTable) getIntent().getSerializableExtra("listTable");
    	if(listTable2 != null)
    		CommFunc.Log("wh", "good .............");*/
    }
    
    @Override
    protected void onDestroy() {
    	// TODO Auto-generated method stub
    	//存储历史记录
		saveHistory();
    	ivl.recycle();
    	CommFunc.Log("wh", "mainActivity destroy .............");
    	unregisterReceiver(handdlePlayReceiver);
    	super.onDestroy();
    }
    
    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		menu.add(0, GlobalVariable.MENU_MAIN_SETTING, 0, getString(R.string.setting)).setIcon(GlobalVariable.MENU_MAIN_SETTING_ICON);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
			case GlobalVariable.MENU_MAIN_SETTING:
				removeDialog(DIALOG_SETTINGS);
				showDialog(DIALOG_SETTINGS);
				break;
		}
		return super.onOptionsItemSelected(item);
	}
    
    @Override
    protected Dialog onCreateDialog(int id) {
    	// TODO Auto-generated method stub
    	switch (id) {
		case DIALOG_END_DESC:
			return new NoTitleDialog(this)
			.setMessage(endDesc)
			.setOnclickCallBack(new NoTitleDialog.OnClickCallBack() {
				@Override
				public void callBack() {
					// TODO Auto-generated method stub
					removeDialog(DIALOG_END_DESC);
					nextPlay();
				}
			})
			.create();
		case DIALOG_GAME_SHOW:
			String result = "第一位玩家" + timerRecord.get(0) + "\n第二位玩家" + timerRecord.get(1);
				   result+= "\n第一位玩家总步数：" + stepRecord.get(0) + "\n第二位玩家总步数：" + stepRecord.get(1);
			return new NoTitleDialog(this)
			.setMessage(result)
			.setOnclickCallBack(new NoTitleDialog.OnClickCallBack() {
				@Override
				public void callBack() {
					// TODO Auto-generated method stub
					removeDialog(DIALOG_GAME_SHOW);
					nextPlay();
				}
			})
			.create();
		case DIALOG_SETTINGS:
			LayoutInflater factory = LayoutInflater.from(this);
	        final View view = factory.inflate(R.layout.main_setting, null);
	        final RadioButton easyMode = (RadioButton) view.findViewById(R.id.easyMode);
	        final RadioButton hardMode = (RadioButton) view.findViewById(R.id.hardMode);
	        final RadioButton screenHorizontal = (RadioButton) view.findViewById(R.id.screenHorizontal);
	        final RadioButton screenVertical = (RadioButton) view.findViewById(R.id.screenVertical);
	        final SeekBar seekNumColoum = (SeekBar) view.findViewById(R.id.seekNumColoum);
	        final SeekBar seekNumRow = (SeekBar) view.findViewById(R.id.seekNumRow);
	        final TextView progressNumRow = (TextView) view.findViewById(R.id.progressNumRow);
	        final TextView progressNumColoum = (TextView) view.findViewById(R.id.progressNumColoum);
	        final CheckBox sound = (CheckBox) view.findViewById(R.id.sound);
	        
	        //操作模式
	        String operaterMode = setting.getMode();
	        if(operaterMode.equals("easy")) {
	        	easyMode.setChecked(true);
	        	hardMode.setChecked(false);
	        }
	        else {
	        	easyMode.setChecked(false);
	        	hardMode.setChecked(true);
	        }
	        //屏幕设置
	        String screenSetting = setting.getScreen();
	        if(screenSetting.equals("vertical")) {
	        	screenHorizontal.setChecked(false);
	        	screenVertical.setChecked(true);
	        }
	        else {
	        	screenHorizontal.setChecked(true);
	        	screenVertical.setChecked(false);
	        }
	        //单元格设置
	        int progressNumRowVal = setting.getNumRow();
	        int progressNumColoumVal = setting.getNumColoum();
	        seekNumColoum.setProgress(progressNumColoumVal);
	        seekNumRow.setProgress(progressNumRowVal);
	        progressNumRow.setText(progressNumRowVal + "");
	        progressNumColoum.setText(progressNumColoumVal + "");
	        //声音设置
	        if(setting.isbSound()) {
	        	sound.setChecked(true);
	        } 
	        else {
	        	sound.setChecked(false);
	        }
	        
	        seekNumColoum.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
				
				@Override
				public void onStopTrackingTouch(SeekBar seekBar) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void onStartTrackingTouch(SeekBar seekBar) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void onProgressChanged(SeekBar seekBar, int progress,
						boolean fromUser) {
					// TODO Auto-generated method stub
					if(progress < 3) {
			    		seekBar.setProgress(3);
			    	} 
			    	else {
			    		progressNumColoum.setText(progress + "");
			    	}
				}
			});
	        
	        seekNumRow.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
				
				@Override
				public void onStopTrackingTouch(SeekBar seekBar) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void onStartTrackingTouch(SeekBar seekBar) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void onProgressChanged(SeekBar seekBar, int progress,
						boolean fromUser) {
					// TODO Auto-generated method stub
					if(progress < 3) {
			    		seekBar.setProgress(3);
			    	} 
			    	else {
			    		progressNumRow.setText(progress + "");
			    	}
				}
			});
	        
			return new AlertDialog.Builder(this)
			.setTitle(R.string.setting)
			.setView(view)
			.setPositiveButton(R.string.ok, new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					boolean bSwapRound = easyMode.isChecked() ? false : true;
					boolean bScreenSetting = screenHorizontal.isChecked() ? true : false;
					int progressNumRowVal = seekNumRow.getProgress();
					int progressNumColoumVal = seekNumColoum.getProgress();
					boolean bSound = sound.isChecked() ? true : false;
					ivl.getImageViewLayoutAttr().setbSwapRound(bSwapRound);
					ivl.getImageViewLayoutAttr().setbSound(bSound);
					
					//CommFunc.Log("wh", "progressNumRowVal: " + progressNumRowVal + ", progressNumColoumVal: " + progressNumColoumVal);
					
					boolean bRefresh = false;
					if(ivl.getImageViewLayoutAttr().getNumColoum() == progressNumColoumVal 
					&& ivl.getImageViewLayoutAttr().getNumRow() == progressNumRowVal) {
						bRefresh = false;
					} 
					else {
						bRefresh = true;
					}
					
					if(ivl.getImageViewLayoutAttr().isbScreenSetting() != bScreenSetting) {
						bRefresh = true;
					}
					ivl.getImageViewLayoutAttr().setbScreenSetting(bScreenSetting);
					
					if(bRefresh) {
						ivl.getImageViewLayoutAttr().setbRefresh(true);
						ivl.getImageViewLayoutAttr().setNumColoum(progressNumColoumVal);
						ivl.getImageViewLayoutAttr().setNumRow(progressNumRowVal);
						ivl.getImageViewLayoutAttr().setShuffCellRecord(null);
						autoIndex--;
						nextPlay();
						ivl.getImageViewLayoutAttr().setbRefresh(false);
					}
					
					//存储配置
					setting = SettingStore.read();
					setting.setMode(easyMode.isChecked() ? "easy" : "hard");
					setting.setScreen(screenHorizontal.isChecked() ? "horizontal" : "vertical");
					setting.setNumColoum(progressNumColoumVal);
					setting.setNumRow(progressNumRowVal);
					setting.setbSound(sound.isChecked());
					
					//保存设置必须另起一个线程处理，因为进行文件写操作会受主线程影响不正常
					/*new Thread(new Runnable() {
						
						@Override
						public void run() {
							// TODO Auto-generated method stub
*/							try {
								SettingStore.write(setting);
								CommFunc.Log("wh", "saving ............" + setting.getNumColoum());
							} catch (Exception e) {
								// TODO Auto-generated catch block
								CommFunc.Log("XYZ", "write is exception ...");
							}
					/*	}
					}).start();*/
				}
			})
			.setNegativeButton(R.string.cancel, new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					
				}
			})
			.create();
		default:
			break;
		}
    	return super.onCreateDialog(id);
    }
    
    private void closeInputStream() {
    	try {
			is.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    private void initData() {
    	autoIndex = 0;
    	slideWidth = (int)getScreenWidth()/3;
    	if(bHistory)
    		autoIndex = setting.getHistoryRecord().getAutoIndex();
    	fileNameList = listTable.getFilename();
        arrFileName = CommFunc.getArrFileName(fileNameList);
        fileName = arrFileName[autoIndex];
        is = getInputStream(fileName);
        if(arrFileName.length == 1 && endDesc != null) {
        	bShowDialogEndDesc = true;
        }
    }
    
    private void initBottom() {
    	bHistory = false;
	    mChronometer.setBase(SystemClock.elapsedRealtime());
		mChronometer.start();
    	initStepAndPercent();
    }
    
    private void initStepAndPercent() {
    	int step = ivl.getImageViewLayoutAttr().getStep();
		String percent = ivl.getImageViewLayoutAttr().getCompletePercent();
		stepView.setText(step + "");
		percentView.setText(percent);
    }
    
    private void nextPlay() {
    	autoIndex++;
    	int stepOld = ivl.getImageViewLayoutAttr().getStep();
    	if(bGame) {
    		playTag++;
    		if(playTag == arrFileName.length*2 -1) {
    			bShowDialogGame = true;
    		}
    		
    	}
    	if(endDesc != null) {
    		playTag++;
    		if(playTag == arrFileName.length - 1)
    			bShowDialogEndDesc = true;
    		
    	}
		if(autoIndex == arrFileName.length){
			if(!bGame) {
				listTable = lts.getNextInfo(listTable);
				//CommFunc.Log("wh", "new data .........");
			}
			initData();
		}
		fileName = arrFileName[autoIndex];
		is = getInputStream(fileName);
		ivl.getImageViewLayoutAttr().setInputStream(is);
		CommFunc.Log("XYZ", "autoIndex：" + autoIndex + ", count: " + shuffCellRecord.size());
		if(bGame)
			ivl.getImageViewLayoutAttr().setShuffCellRecord(shuffCellRecord.get(autoIndex));
		ivl.generateView();  //生成玩图
		//shuff cell record
		shuffCellRecord.put(autoIndex, ivl.getImageViewLayoutAttr().getShuffCellRecord());
		closeInputStream();

		if(bGame) {
			ivl.getImageViewLayoutAttr().setStep(stepOld);
			if(playTag == arrFileName.length) {
				ivl.getImageViewLayoutAttr().setStep(0);
				initBottom();
			}
		}
		else {
			ivl.getImageViewLayoutAttr().setStep(0);
			initBottom();
		}
		//存储历史记录 ，放到onDestroy()函数中处理
		//saveHistory();
    }
    
    //保存历史记录
    private void saveHistory() {
    	new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				Setting setting = SettingStore.read();
				if(setting != null) {
					Setting.HistoryRecord shr = setting.getHistoryRecord();
					shr.setAutoIndex(autoIndex);
					shr.setId(listTable.getId());
					shr.setRecord(shuffCellRecord.get(autoIndex));
					setting.setHistoryRecord(shr);
			    	try {
						SettingStore.write(setting);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}).start();
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
				// 向左
				if (Math.abs(x_temp01 - x_temp02) > slideWidth) {
					ActivityManager.toCollateActivity(this, fileName);
				}
				x_temp01 = 0.0f;
				y_temp01 = 0.0f;
				x_temp02 = 0.0f;
				y_temp02 = 0.0f;
				break;
	
		}
		return super.onTouchEvent(event);
	}
	
	private BroadcastReceiver handdlePlayReceiver = new BroadcastReceiver(){
		@Override
		public void onReceive(Context context, Intent intent) {
			if(intent.getAction().equals(GlobalVariable.MAIN_ACTIVITY_PLAY_RECEIVER)){
				finish();
				/*Bundle bundle = intent.getExtras();
				ActivityManager.toMainActivity(context, bundle);*/
				/*if(bundle.getString("endDesc") != null)
					endDesc = bundle.getString("endDesc");
				bGame = bundle.getBoolean("game");
		        bHistory = bundle.getBoolean("history");
		        bRandom = bundle.getBoolean("random");
				ListTable lt = (ListTable) bundle.getSerializable("listTable");
				
				if(!lt.equals(listTable)) {
					CommFunc.Log("wh", "new play ........................");
					listTable = lt;
					initData();
					autoIndex = -1;
					ivl.getImageViewLayoutAttr().setbRefresh(true);
					nextPlay();
					ivl.getImageViewLayoutAttr().setbRefresh(false);
				}*/
			}
		}
	};
}