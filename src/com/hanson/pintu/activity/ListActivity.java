package com.hanson.pintu.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Spinner;
import android.widget.Toast;

import com.hanson.pintu.ActivityManager;
import com.hanson.pintu.R;
import com.hanson.pintu.adapter.ListGridAdapter;
import com.hanson.pintu.data.helper.ListTableHelper;
import com.hanson.pintu.data.pojo.ListCellAttr;
import com.hanson.pintu.data.pojo.ListTable;
import com.hanson.pintu.data.pojo.TypeTable;
import com.hanson.pintu.data.store.ListCellAttrStore;
import com.hanson.pintu.data.store.TypeTableStore;
import com.hanson.pintu.interfaces.OnClickCallBack;
import com.hanson.pintu.util.CommFunc;
import com.hanson.pintu.util.GlobalVariable;

public class ListActivity extends basicInActivity {
	GridView mGrid;
	ListGridAdapter lgad;
	ArrayList<ListCellAttr> list = new ArrayList<ListCellAttr>();
	ArrayList<TypeTable> listType;
	private ListTableHelper lth;
	private TypeTableStore tts;
	private ListCellAttrStore lcas;
	
	private int lastItem = 0;
	private int showItemCount = 0;
	private int currPosition;
	private long type = -1;
	private int source = -1;
	private int total;
	private int totalPage;
	private long offset = 0;
	private final int limit = 8;
	private int page = 0;
	
	private boolean bOrder = false;
	private ListCellAttr currLt;
	private long[] arrTypeItemValue;
	private String[] arrTypeItemName;
	private boolean bImageSelect = false;
	private TreeMap<Integer, ListCellAttr> listSelectRecord;
	private final static int DIALOG_LONG_CLICK = 1;
	private final static int DIALOG_MENU_CLICK = 2;
	private final static int DIALOG_START_WITH_END_DES = 3;
	private final static int DIALOG_CHANGE_TYPE = 4;
	private final static int DIALOG_TYPE_FILTER = 5;
	private final static int DIALOG_CHANGE_TYPE_SPLIT = 6;
	private final static int DIALOG_CHANGE_TYPE_BATCH = 7;
	private final static int DIALOG_INIT_LOADING = 8;
	
	private boolean isAdmin = false;   //是否做分类来源判断
	private HashMap<Integer, String> nextLoadPageRecoard = new HashMap<Integer, String>();
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		showDialog(DIALOG_INIT_LOADING);
		
		setContentView(R.layout.list);
		
		// 注册广播接收器
		registerReceiver(handdleUpdateDateByDateCell, new IntentFilter(GlobalVariable.GET_MEDIA_IMAGES_PLAY));
		
		//listTable = (ListCellAttr) getIntent().getSerializableExtra("listTable");
		/*if(listTable != null) {
			type = listTable.getType();
			source = listTable.getSource();
		}*/
		lth = new ListTableHelper(ListActivity.this);
		tts = new TypeTableStore(ListActivity.this);
		lcas = new ListCellAttrStore(ListActivity.this);
		listSelectRecord = new TreeMap<Integer, ListCellAttr>();
		
		setDefaultData();
		
		mGrid = (GridView) findViewById(R.id.myGrid);
		lgad = new ListGridAdapter(ListActivity.this);
		
		mGrid.setAdapter(lgad);
        mGrid.setOnScrollListener(new OnScrollListener() {
		    //添加滚动条滚到最底部，加载余下的元素
		    @Override
		    public void onScrollStateChanged(AbsListView view, int scrollState) {
		        //if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
		        	//lgad.releaseResource();
		            loadDataList();
		        //}
		    }
		    @Override
		    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		    	showItemCount = visibleItemCount;
		    	lastItem = firstVisibleItem + visibleItemCount - 1;
		    }
	    });
        mGrid.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				currLt = list.get(arg2);
				currPosition = arg2;
				if(bImageSelect) {
					
					if(currLt.getSource() > 0 || bOrder || !isAdmin) {
						list.get(arg2).setbSelected(!list.get(arg2).isbSelected());
						lgad.setDataList(list);
						notifyDataSetChanged();
						if(list.get(arg2).isbSelected()) {
							listSelectRecord.put(arg2, list.get(arg2));
						}
						else {
							listSelectRecord.remove(arg2);
						}
					}
					
					//判断是否是排序
					if(bOrder) {
						if(listSelectRecord.size() > 1){
							int position1 = listSelectRecord.firstKey();
							int position2 = listSelectRecord.lastKey();
							ListCellAttr lt1 = listSelectRecord.get(position1);
							ListCellAttr lt2 = listSelectRecord.get(position2);
							
							lth.update(lt1.getId(), lt2.getFilename(), lt2.getType(), lt2.getCount(), lt2.getSource(), false);
							lth.update(lt2.getId(), lt1.getFilename(), lt1.getType(), lt1.getCount(), lt1.getSource(), false);
							
							//开始改变两者ID
							long id1 = lt1.getId();
							long id2 = lt2.getId();
							lt1.setId(id2);   //lt1变为lt2
							lt2.setId(id1);   //lt2变为lt1
							
							list.set(position1, lt2);
							list.set(position2, lt1);
							
							currLt = lt1;
							
							setbSelected(position1, false);
							setbSelected(position2, false);
							
							lgad.setDataList(list);
							notifyDataSetChanged();
							Toast.makeText(ListActivity.this, getString(R.string.orderSuccessed), Toast.LENGTH_SHORT).show();
							listSelectRecord.clear();
						}
					}
				}
				else {
					ListTable lt = ListCellAttr.listCellAttrToListTable(list.get(arg2));
					Bundle bundle = new Bundle();
					bundle.putSerializable("listTable", lt);
					ActivityManager.toMainActivity(ListActivity.this, bundle);
				}
				//Toast.makeText(ListActivity.this, lt.getId() + "", Toast.LENGTH_SHORT).show();
				//CommFunc.Log("wh", "index: " + arg2);
			}
		});
        mGrid.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				currLt = list.get(arg2);
				currPosition = arg2;
				removeDialog(DIALOG_LONG_CLICK);  //权限判断受限，每次必须重新创建
				showDialog(DIALOG_LONG_CLICK);
				return false;
			}
        	
		});
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				loadDataList();
				lgad.setDataList(list);
				removeDialog(DIALOG_INIT_LOADING);
			}
		}).start();
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		//lcas.recycle(list);
		// 释放广播接收器
		unregisterReceiver(handdleUpdateDateByDateCell);
		super.onDestroy();
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		MenuItem item = menu.findItem(GlobalVariable.MENU_LIST_IMAGE_SELECT);
		MenuItem itemOrder = menu.findItem(GlobalVariable.MENU_LIST_ORDER);
		
		if(bOrder) {
			bImageSelect = true; //保证，当做排序操作时，选择操作永远有效
			itemOrder.setTitle(getString(R.string.orderCancel)).setIcon(GlobalVariable.MENU_LIST_ORDER_CANCEL_ICON);
			menu.findItem(GlobalVariable.MENU_LIST_IMAGE_SELECT).setEnabled(false);
		}
		else {
			itemOrder.setTitle(getString(R.string.order)).setIcon(GlobalVariable.MENU_LIST_ORDER_ICON);
			menu.findItem(GlobalVariable.MENU_LIST_IMAGE_SELECT).setEnabled(true);
		}
		
		if(bImageSelect) {
			item.setTitle(getString(R.string.cancelSelect)).setIcon(GlobalVariable.MENU_LIST_IMAGE_SELECT_ICON_CANCEL);
		}
		else {
			item.setTitle(getString(R.string.imageSelect)).setIcon(GlobalVariable.MENU_LIST_IMAGE_SELECT_ICON);
		}
		
		if(bOrder) {
			menu.findItem(GlobalVariable.MENU_LIST_OPERATION_SELECT).setEnabled(false);
		} 
		else {
			menu.findItem(GlobalVariable.MENU_LIST_OPERATION_SELECT).setEnabled(bImageSelect);
		}
		return super.onPrepareOptionsMenu(menu);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		menu.add(0, GlobalVariable.MENU_LIST_SELECT_FILTER, 0, getString(R.string.typeFilter)).setIcon(GlobalVariable.MENU_LIST_SELECT_FILTER_ICON);
		menu.add(0, GlobalVariable.MENU_LIST_IMAGE_SELECT, 0, getString(R.string.imageSelect)).setIcon(GlobalVariable.MENU_LIST_IMAGE_SELECT_ICON);
		menu.add(0, GlobalVariable.MENU_LIST_OPERATION_SELECT, 0, getString(R.string.operationSelect)).setIcon(GlobalVariable.MENU_LIST_OPERATION_SELECT_ICON);
		menu.add(0, GlobalVariable.MENU_LIST_ORDER, 0, getString(R.string.order)).setIcon(GlobalVariable.MENU_LIST_ORDER_ICON);
		super.onCreateOptionsMenu(menu);
		menu.removeItem(GlobalVariable.MENU_LIST);
		menu.findItem(GlobalVariable.MENU_LIST_OPERATION_SELECT).setEnabled(false);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
			case GlobalVariable.MENU_LIST_SELECT_FILTER:
				showDialog(DIALOG_TYPE_FILTER);
				break;
			case GlobalVariable.MENU_LIST_IMAGE_SELECT:
				recordListClear();  //清除选择记录
				if(item.getTitle() ==  getString(R.string.imageSelect) || bOrder) {
					bImageSelect = true;
				}
				else {
					bImageSelect = false;
				}
				break;
			case GlobalVariable.MENU_LIST_OPERATION_SELECT:
				if(listSelectRecord.size() > 0) {
					boolean isShow = true;
					for (ListCellAttr lt : listSelectRecord.values()) {
						if(lt.getSource() == 0) {
							isShow = false;
							break;
						}
					}
					if(isShow || !isAdmin) {
						showDialog(DIALOG_MENU_CLICK);
					} 
					else {
						Toast.makeText(ListActivity.this, getString(R.string.errorOperaterSource), Toast.LENGTH_SHORT).show();
					}
				}
				else {
					Toast.makeText(ListActivity.this, getString(R.string.errorCombinCount), Toast.LENGTH_SHORT).show();
				}
				break;
			case GlobalVariable.MENU_LIST_ORDER:
				recordListClear(); //清除选择记录
				bOrder = !bOrder;
				bImageSelect = bOrder ? true : false;
				break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		// TODO Auto-generated method stub
		switch (id) {
		case DIALOG_LONG_CLICK:
			
			break;
		}
		super.onPrepareDialog(id, dialog);
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		// TODO Auto-generated method stub
		switch (id) {
		case DIALOG_LONG_CLICK:
			return new AlertDialog.Builder(ListActivity.this)
				.setTitle(R.string.operation)
				.setItems(getItemsForDialogLong(), new OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						switch (which) {
						case 0:
							showDialog(DIALOG_START_WITH_END_DES);
							break;
						case 1:
							/*PlayParam pp = new PlayParam();
							pp.setbGame(true);
							ActivityManager.toMainActivity(ListActivity.this, currLt, pp);*/
							Bundle bundle = new Bundle();
							bundle.putSerializable("listTable", ListCellAttr.listCellAttrToListTable(currLt));
							bundle.putBoolean("game", true);
							ActivityManager.toMainActivity(ListActivity.this, bundle);
							break;
						case 2:
							removeDialog(DIALOG_CHANGE_TYPE);
							showDialog(DIALOG_CHANGE_TYPE);
							break;
						case 3:
							removeData(currLt, currPosition);
							Toast.makeText(ListActivity.this, getString(R.string.completeDel), Toast.LENGTH_SHORT).show();
							break;
						case 4:
							removeDialog(DIALOG_CHANGE_TYPE_SPLIT);
							showDialog(DIALOG_CHANGE_TYPE_SPLIT);
							break;
						default:
							break;
						}
					}
				})
				.create();
		case DIALOG_MENU_CLICK:
			return new AlertDialog.Builder(ListActivity.this)
			.setTitle(R.string.operation)
			.setItems(getItemsForDialogMenu(), new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					//Toast.makeText(ListActivity.this, listSelectRecord.values().toString(), Toast.LENGTH_SHORT).show();
					switch (which) {
					case 0:
						removeDialog(DIALOG_CHANGE_TYPE_BATCH);
						showDialog(DIALOG_CHANGE_TYPE_BATCH);
						break;
					case 1:
						if(listSelectRecord.size() > 2) {
							boolean bOk = true;
							String[] arrFileName = new String[listSelectRecord.size()];
							int[] arrPosition = new int[listSelectRecord.size()];
							ListCellAttr[] arrListCellAttr = new ListCellAttr[listSelectRecord.size()];
							int i = 0;
							for (Integer position : listSelectRecord.keySet()) {
								ListCellAttr lt = listSelectRecord.get(position);
								if(lt.getType() == 1) {
									bOk = false;
									break;
								}
								arrFileName[i] = lt.getFilename();
								arrPosition[i] = position;
								arrListCellAttr[i] = lt;
								i++;
							}
							if(bOk) {
								String fileNameList = CommFunc.getFileNameList(arrFileName);
								long id = lth.insert(fileNameList, 1, arrFileName.length, 1, true);
								for (int j = 0; j < arrPosition.length; j++) {
									int position = arrPosition[j];
									position -= j;  //注意删除时，必须第次减少之前去掉的个数
									ListCellAttr lt = arrListCellAttr[j];
									setbSelected(position, false);
									removeData(lt, position);
									CommFunc.Log("wh", "remove data .... : " + position);
									//保存第一张图片
									/*if(j > 0) {
										removeData(lt, position);
									}
									else {
										changeType(lt, position, 1l);
									}*/
								}
								ListTable ltNew = new ListTable();
								ltNew.setData(id, fileNameList, 1, arrFileName.length, 1);
								
								addData(ltNew, currPosition - arrPosition.length + 1);
								
								//如果已经加载的数据小于limit,则再加载一次，防止出现空白
								if(list.size() < limit)
									loadDataList();
								
								Toast.makeText(ListActivity.this, getString(R.string.completeCombin), Toast.LENGTH_SHORT).show();
								listSelectRecord.clear();
							}
							else {
								Toast.makeText(ListActivity.this, getString(R.string.errorCombinImage), Toast.LENGTH_SHORT).show();
							}
						} else {
							Toast.makeText(ListActivity.this, getString(R.string.errorCombinCount), Toast.LENGTH_SHORT).show();
						}
						break;
					case 2:
						int i = 0;
						for (Integer position : listSelectRecord.keySet()) {
							ListCellAttr lt = listSelectRecord.get(position);
							setbSelected(position, false);
							position -= i;  //注意删除时，必须第次减少之前去掉的个数
							removeData(lt, position);
							i++;
						}
						//如果已经加载的数据小于limit,则再加载一次，防止出现空白
						if(list.size() < limit)
							loadDataList();
						
						Toast.makeText(ListActivity.this, getString(R.string.completeDel), Toast.LENGTH_SHORT).show();
						listSelectRecord.clear();
						break;
					default:
						break;
					}
				}
			})
			.create();
		case DIALOG_START_WITH_END_DES:
			LayoutInflater factory = LayoutInflater.from(ListActivity.this);
	        final View textEntryView = factory.inflate(R.layout.dialog_end_des, null);
	        final EditText input = (EditText)textEntryView.findViewById(R.id.text);
	        
	        return new AlertDialog.Builder(this)
	        .setTitle(R.string.setEndDes)
	        .setView(textEntryView)
	        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                	String string = input.getText().toString();
                	Bundle bundle = new Bundle();
					bundle.putSerializable("listTable", ListCellAttr.listCellAttrToListTable(currLt));
					bundle.putString("endDesc", string);
					ActivityManager.toMainActivity(ListActivity.this, bundle);
                    /* User clicked OK so do some stuff */
                }
            })
	        .create();
		case DIALOG_CHANGE_TYPE:
			DialogChangeType dct = new DialogChangeType();
			AlertDialog ad = dct.generateDialog();
			dct.setOnclickCallBack(new OnClickCallBack() {
				
				@Override
				public void callBack(DialogChangeType dct, DialogInterface dialog, int whichButton) {
					// TODO Auto-generated method stub
	            	long type = dct.getValue();
	            	changeType(currLt, currPosition, type);
	            	Toast.makeText(ListActivity.this, getString(R.string.successed), Toast.LENGTH_SHORT).show();
				}
			});
			return ad;
		case DIALOG_TYPE_FILTER:
			getListTypeFromDb(false);
			String[] newArrTypeItemName = new String[arrTypeItemName.length + 1];
			final long[] newArrTypeItemValue = new long[arrTypeItemName.length + 1];
			newArrTypeItemName[0] = getString(R.string.allType);
			newArrTypeItemValue[0] = -1;
			for (int i = 0, len=arrTypeItemName.length; i < len; i++) {
				newArrTypeItemName[i+1] = arrTypeItemName[i];
				newArrTypeItemValue[i+1] = arrTypeItemValue[i];
			}
			
			return new AlertDialog.Builder(ListActivity.this)
			.setTitle(R.string.typeFilter)
			.setItems(newArrTypeItemName, new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					type = newArrTypeItemValue[which];
					if(lcas.getTotal(type, source) > 0) {
						setDefaultData();
						type = newArrTypeItemValue[which];  //必要
						lcas.recycle(list); //释放资源
						loadDataList();
					}
					else {
						//Toast.makeText(ListActivity.this, getString(R.string.errorNoImage), Toast.LENGTH_SHORT).show();
						//showDialog(DIALOG_TYPE_FILTER);
						
						//调用同一 Dialog 不能在 onclick 事件中实现，只能通过其它方法，如果 handler、广播
						Message msg = new Message();
						msg.what = 2;
						handler.sendMessage(msg);
					}
				}
			})
			.create();
		 case DIALOG_CHANGE_TYPE_SPLIT:
		    DialogChangeType dct2 = new DialogChangeType(true);
			AlertDialog ad2 = dct2.generateDialog();
			dct2.setOnclickCallBack(new OnClickCallBack() {
				
				@Override
				public void callBack(DialogChangeType dct, DialogInterface dialog, int whichButton) {
					// TODO Auto-generated method stub
					long type = dct.getValue();
					
					setbSelected(currPosition, false);
					removeData(currLt, currPosition);
					
					String[] arrFilenName = CommFunc.getArrFileName(currLt.getFilename());
					for (int i = 0; i < arrFilenName.length; i++) {
						long id = lth.insert(arrFilenName[i], type, 1, currLt.getSource(), true);
						
						//把拆分的图片放到被拆分的图片后面
						ListTable tempInfo = lcas.getInfo(id);
						addData(tempInfo, currPosition + i);
					}
					Toast.makeText(ListActivity.this, getString(R.string.successed), Toast.LENGTH_SHORT).show();
					listSelectRecord.clear();
				}
			});
			return ad2;
		 case DIALOG_CHANGE_TYPE_BATCH:
			 getListTypeFromDb(false);
			 DialogChangeType dct3 = new DialogChangeType(true);
				AlertDialog ad3 = dct3.generateDialog();
				dct3.setOnclickCallBack(new OnClickCallBack() {
					
					@Override
					public void callBack(DialogChangeType dct, DialogInterface dialog, int whichButton) {
						// TODO Auto-generated method stub
						long type = dct.getValue();
						boolean bOk = true;
						for (Integer position : listSelectRecord.keySet()) {
							ListCellAttr lt = listSelectRecord.get(position);
							if(lt.getType() == 1)
								bOk = false;
						}
						if(bOk) {
							for (Integer position : listSelectRecord.keySet()) {
								ListCellAttr lt = listSelectRecord.get(position);
								changeType(lt, position, type);
								setbSelected(position, false);
							}
							Toast.makeText(ListActivity.this, getString(R.string.successed), Toast.LENGTH_SHORT).show();
							listSelectRecord.clear();
						}
						else {
							Toast.makeText(ListActivity.this, getString(R.string.errorChangeType), Toast.LENGTH_SHORT).show();
						}
					}
				});
			 return ad3;
		  case DIALOG_INIT_LOADING:
			 ProgressDialog dialog = new ProgressDialog(this);
	         //dialog.setTitle("Indeterminate");
	         dialog.setMessage(getString(R.string.loading));
	         dialog.setIndeterminate(true);
	         dialog.setCancelable(true);
	         return dialog;
		}
		return super.onCreateDialog(id);
	}
	
	//从数据库中取分类
	private void getListTypeFromDb(boolean bReload) {
		if(tts == null)
			tts = new TypeTableStore(ListActivity.this);
		if(bReload) {
			listType = tts.getList();
		}
		else if(listType == null) {
			listType = tts.getList();
		} 
		arrTypeItemValue = new long[listType.size()];
		arrTypeItemName = new String[listType.size()];
		for (int i = 0, len=listType.size(); i < len; i++) {
			arrTypeItemName[i] = listType.get(i).getName();
			arrTypeItemValue[i] = listType.get(i).getId();
		}
	}
	
	private String[] getItemsForDialogMenu() {
		int[] arrId = new int[]{R.string.changeType, R.string.combinType, R.string.delete};
		String[] temp = new String[arrId.length];
		for (int i = 0, len=arrId.length; i < len; i++) {
			temp[i] = getString(arrId[i]);
		}
		return temp;
	}
	
	private String[] getItemsForDialogLong() {
		int[] arrId;
		if(currLt.getSource() == 0) {
			arrId = new int[]{R.string.startWithEndDes, R.string.startWithGame};
		}
		else {
			if(currLt.getType() != 1) {
				arrId = new int[]{R.string.startWithEndDes, R.string.startWithGame, R.string.changeType, R.string.delete};
			}
			else {
				arrId = new int[]{R.string.startWithEndDes, R.string.startWithGame, R.string.changeType, R.string.delete, R.string.splitCombin};
			}
		}
		
		if(!isAdmin) {
			arrId = new int[]{R.string.startWithEndDes, R.string.startWithGame, R.string.changeType, R.string.delete, R.string.splitCombin};
		}
		String[] temp = new String[arrId.length];
		for (int i = 0, len=arrId.length; i < len; i++) {
			temp[i] = getString(arrId[i]);
		}
		return temp;
	}
	
	private Handler handler = new Handler()
	{
		public void handleMessage(Message msg){
			switch(msg.what){
				case 1:
					lgad.notifyDataSetChanged();
			        break;
				case 2:
					Toast.makeText(ListActivity.this, getString(R.string.errorNoImage), Toast.LENGTH_SHORT).show();
					showDialog(DIALOG_TYPE_FILTER);
					break;
			}
			super.handleMessage(msg);
		}
	};
	
	private void setDefaultData(){
		type = -1;
		source = -1;
		page = 0;
		offset = 0;
		total = lcas.getTotal(type, source);
		totalPage = (int)Math.ceil((double)total/(double)limit);
		lcas.recycle(list);
	}
	
	private void loadDataList() {
		if (page <= totalPage) {
			if(nextLoadPageRecoard.containsKey(page)) {
				loadDataLisyByThread();
			}
			else {
				list = lcas.getCellList(type, source, null, offset + "," + limit);
				list = appendList(lgad.getDataList(),list);
				lgad.setDataList(list);
				notifyDataSetChanged();
				
				loadDataLisyByThread();
			}
			CommFunc.Log("XYZ", "loading new data .............");
		}
	}
	
	private void loadDataLisyByThread() {
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				offset = limit*(page+1);
				list = lcas.getCellList(type, source, null, offset + "," + limit);
				list = appendList(lgad.getDataList(),list);
				lgad.setDataList(list);
				notifyDataSetChanged();
				
				nextLoadPageRecoard.put(page + 1, "yes");
				
				page++;
				offset = limit*page;
			}
		}).start();
	}
	
	private void recordListClear() {
		for (Integer i : listSelectRecord.keySet()) {
			list.get(i).setbSelected(false);
		}
		lgad.setDataList(list);
		notifyDataSetChanged();
		listSelectRecord.clear();
	}
	
	private void notifyDataSetChanged() {
		Message msg = new Message();
		msg.what = 1;
		handler.sendMessage(msg);
	}
	
	//添加记录
	private void addData(ListTable lt, int position) {
		list.add(position, lcas.listTableToListCellAttr(lt));
		notifyDataSetChanged();
	}
	
	private void setbSelected(int position, boolean bSelected) {
		list.get(position).setbSelected(bSelected);
		notifyDataSetChanged();
	}
	
	//删除某条记录
	private void removeData(ListCellAttr lt, int position) {
		lth.delete(lt.getId());
		
		//同时删除缩略图
		CommFunc.deleteThumbImage(lt);
		
		list = lcas.remove(list, position);
		lgad.setDataList(list);
		notifyDataSetChanged();
	}
	
	private void changeType(ListCellAttr lt, int position, long type) {
		lth.update(lt.getId(), lt.getFilename(), type, lt.getCount(), lt.getSource(), false);
		list.get(position).setType(type);
		list.get(position).setTypeName(tts.getInfo(type).getName());
		lgad.setDataList(list);
		notifyDataSetChanged();
		CommFunc.Log("XYZ", "change type: ...");
	}
	
	/**
	 * 将新的List加入到原来的List中
	 */
	private ArrayList<ListCellAttr> appendList(ArrayList<ListCellAttr> sourceList, ArrayList<ListCellAttr> appendList){
		for(ListCellAttr o : appendList){
			sourceList.add(o);
		}
		return sourceList;
	}
	
	/**
	 * 广播：DateCell触摸
	 */
	private BroadcastReceiver handdleUpdateDateByDateCell = new BroadcastReceiver(){
		@Override
		public void onReceive(Context context, Intent intent) {
			if(intent.getAction().equals(GlobalVariable.GET_MEDIA_IMAGES_PLAY)){
				//如果没选择分类或者分类为其它，则进行操作
				if(type == -1 || type == 5) {
					ListTable listTable = (ListTable) intent.getSerializableExtra("listTable");
					addData(listTable, 0);
				}
			}
		}
	};

	/**
	 * 分类选择对话框类
	 * @author Administrator
	 *
	 */
	public class DialogChangeType {
		private View view;
		private Spinner s1;
		private long[] arrItemTypeValue;
		private String[] arrItemTypeName;
		private OnClickCallBack callBackClick;
		private long value;
		
		public DialogChangeType(){
			this(false);
		}
		public DialogChangeType(boolean bSplitImage) {
			getListTypeFromDb(false);
			int defaultValue = 0;
			if(bSplitImage) {
				int j = 0;
				arrItemTypeValue = new long[listType.size() -1];
				arrItemTypeName = new String[listType.size() -1];
				for (int i = 0, len=listType.size(); i < len; i++) {
					if(listType.get(i).getId() != 1){
						arrItemTypeValue[j] = listType.get(i).getId();
						arrItemTypeName[j] = listType.get(i).getName();
						j++;
					}
				}
				CommFunc.Log("wh", "is split image -----------------> ");
			}
			else {
				//防止做批量处理时，没初始化 currLt
				if(currLt != null) {
					for (int i = 0, len=listType.size(); i < len; i++) {
						if(currLt.getType() == listType.get(i).getId())
							defaultValue = i;
					}
				}
				arrItemTypeValue = arrTypeItemValue;
				arrItemTypeName = arrTypeItemName;
			}
			LayoutInflater factoryChangeType = LayoutInflater.from(ListActivity.this);
	        view = factoryChangeType.inflate(R.layout.dialog_change_type, null);
	        view.setPadding(10, 10, 10, 10);
	        s1 = (Spinner) view.findViewById(R.id.mySpinner);
	        ArrayAdapter<String> adapter = new ArrayAdapter<String>(ListActivity.this,
	                android.R.layout.simple_spinner_item, arrItemTypeName);
	        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	        s1.setAdapter(adapter);
	        s1.setSelection(defaultValue, true);
		}
		
		public AlertDialog generateDialog() {
			return new AlertDialog.Builder(ListActivity.this)
	        .setTitle(R.string.changeType)
	        .setView(view)
	        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog, int whichButton) {
	            	value = arrItemTypeValue[s1.getSelectedItemPosition()];
	            	doCompleteViewClick(DialogChangeType.this, dialog, whichButton);
	            	/* User clicked OK so do some stuff */
	            }
	        })
	        .create();
		}
		
		public long getValue() {
			return value;
		}

		public void setOnclickCallBack(OnClickCallBack callBackClick) {
			this.callBackClick = callBackClick;
		}
		
		public void doCompleteViewClick(DialogChangeType dct, DialogInterface dialog, int whichButton) {
			callBackClick.callBack(dct, dialog, whichButton);
		}
        
	}
}
