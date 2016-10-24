package com.hanson.pintu.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.hanson.pintu.R;
import com.hanson.pintu.data.helper.ListTableHelper;
import com.hanson.pintu.data.helper.TypeTableHelper;
import com.hanson.pintu.data.pojo.TypeTable;
import com.hanson.pintu.data.store.TypeTableStore;
import com.hanson.pintu.view.DraggableListView;

public class TypeListOrderActivity extends ListActivity {
	private TypeTableStore tts;
	private TypeTableHelper tth;
	private ListTableHelper lth;
	private ArrayList<TypeTable> listType;
	private List<String> arrTypeName = new ArrayList<String>();
	private List<Long> arrTypeValue = new ArrayList<Long>();
	
	private ArrayAdapter<String> adapter;
	private TypeTable currTypeTable;
	private int currPosition;
	
	private final static int DIALOG_LONG_CLICK = 1;
	private final static int DIALOG_UPDATE = 2;
	private final static int DIALOG_ALERT = 3;
	private final static int DIALOG_ADD = 4;
	
	private boolean isAdmin = false;

	@Override
	public void onCreate(Bundle icicle) {
		requestWindowFeature(Window.FEATURE_LEFT_ICON);
		super.onCreate(icicle);
		setContentView(R.layout.type_list_order);
		
		setTitle(R.string.typeManager);
		getWindow().setFeatureDrawableResource(Window.FEATURE_LEFT_ICON, 
				android.R.drawable.ic_dialog_info);
		
		tts = new TypeTableStore(this);
		tth = new TypeTableHelper(this);
		
		loadData();
		
		adapter = new TypeAdapter();
		setListAdapter(adapter);

		DraggableListView tlv = (DraggableListView) getListView();

		tlv.setSelected(true);
		tlv.setDropListener(onDrop);
		tlv.getAdapter();
		
		tlv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				currPosition = arg2;
				currTypeTable = listType.get(arg2);
				//adapter.add("fuckyou");
				//Toast.makeText(TypeListActivity.this, adapter.getPosition("其它") + "", Toast.LENGTH_SHORT).show();
			}
		});
		tlv.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				// TODO Auto-generated method stub
				currPosition = arg2;
				currTypeTable = listType.get(arg2);
				showDialog(DIALOG_LONG_CLICK);
				//Toast.makeText(TypeListActivity.this, arrTypeName[arg2], Toast.LENGTH_SHORT).show();
				return false;
			}
		});
	}

	private DraggableListView.DropListener onDrop = new DraggableListView.DropListener() {
		@Override
		public void drop(int from, int to) {
			String item = adapter.getItem(from);
			adapter.remove(item);
			adapter.insert(item, to);
			
			//互换排序号
			tth.update(listType.get(from).getId(), listType.get(from).getName(), listType.get(to).getOrder(), false);
			tth.update(listType.get(to).getId(), listType.get(to).getName(), listType.get(from).getOrder(), false);
			loadData();
			/*Toast.makeText(DragDemo.this, adapter.getList().toString(),
					Toast.LENGTH_SHORT).show();*/
			//CommFunc.Log("wh", "drop listenering ...............");
		}
	};
	
	@Override
	protected Dialog onCreateDialog(int id) {
		// TODO Auto-generated method stub
		switch (id) {
		case DIALOG_LONG_CLICK:
			String[] itemsLongClick = new String[]{getString(R.string.update), getString(R.string.add), getString(R.string.delete)};
			return new AlertDialog.Builder(this)
			.setTitle(R.string.operationSelect)
			.setItems(itemsLongClick, new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					switch (which) {
					case 0:
						if(currTypeTable.getId() > 5 || !isAdmin) {
							removeDialog(DIALOG_UPDATE);
							showDialog(DIALOG_UPDATE);
						}
						else {
							Toast.makeText(TypeListOrderActivity.this, getString(R.string.errorOperaterSystem), Toast.LENGTH_SHORT).show();
						}
						break;
					case 1:
						showDialog(DIALOG_ADD);
						break;
					case 2:
						showDialog(DIALOG_ALERT);
						break;
					default:
						break;
					}
				}
			})
			.create();
		case DIALOG_UPDATE:
			LayoutInflater factory = LayoutInflater.from(this);
	        final View textEntryView = factory.inflate(R.layout.dialog_only_input, null);
	        final EditText input = (EditText)textEntryView.findViewById(R.id.input);
	        input.setText(currTypeTable.getName());
	        
	        return new AlertDialog.Builder(this)
	        .setTitle(R.string.update)
	        .setView(textEntryView)
	        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                	String string = input.getText().toString();
                	tth.update(currTypeTable.getId(), string, true);
                	adapter.remove(currTypeTable.getName());
                	adapter.insert(string, currPosition);
                	loadData();
                	Toast.makeText(TypeListOrderActivity.this, getString(R.string.successed), Toast.LENGTH_SHORT).show();
                    /* User clicked OK so do some stuff */
                }
            })
            .setNegativeButton(R.string.cancel, new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					
				}
			})
	        .create();
		case DIALOG_ADD:
			LayoutInflater factoryAdd = LayoutInflater.from(this);
	        final View viewAdd = factoryAdd.inflate(R.layout.dialog_only_input, null);
	        final EditText inputAdd = (EditText)viewAdd.findViewById(R.id.input);
	        
	        return new AlertDialog.Builder(this)
	        .setTitle(R.string.update)
	        .setView(viewAdd)
	        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                	String string = inputAdd.getText().toString();
                	tth.insert(string, true);
                	adapter.add(string);
                	loadData();
                	Toast.makeText(TypeListOrderActivity.this, getString(R.string.successed), Toast.LENGTH_SHORT).show();
                    /* User clicked OK so do some stuff */
                }
            })
            .setNegativeButton(R.string.cancel, new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					
				}
			})
	        .create();
		case DIALOG_ALERT:
			return new AlertDialog.Builder(this)
			.setTitle(R.string.warmTip)
			.setMessage(R.string.alertDeleteType)
			.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                	tth.delete(currTypeTable.getId());
                	//把删除后的相关分类图片归类为“其它”
                	HashMap<String, Object> map = new HashMap<String, Object>();
                	map.put("type", 5);
                	lth.update(map, "type=?", new String[]{currTypeTable.getId() + ""});
                	
                	loadData();
                	adapter.remove(currTypeTable.getName());
                	Toast.makeText(TypeListOrderActivity.this, getString(R.string.successed), Toast.LENGTH_SHORT).show();
                    // User clicked OK so do some stuff 
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
	
	private void loadData() {
		arrTypeName.clear();
		arrTypeValue.clear();
		listType = tts.getList();
		for (int i = 0,len=listType.size(); i < len; i++) {
			arrTypeName.add(listType.get(i).getName());
			arrTypeValue.add(listType.get(i).getId());
		}
	}

	class TypeAdapter extends ArrayAdapter<String> {
		TypeAdapter() {
			super(TypeListOrderActivity.this, R.layout.row, arrTypeName);
		}

		public List<String> getList() {
			return arrTypeName;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			View row = convertView;
			if (row == null) {
				LayoutInflater inflater = getLayoutInflater();
				row = inflater.inflate(R.layout.row, parent, false);
			}
			TextView label = (TextView) row.findViewById(R.id.label);
			label.setText(arrTypeName.get(position));
			return (row);
		}
	}
}