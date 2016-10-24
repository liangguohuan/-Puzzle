package com.hanson.pintu.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.hanson.pintu.R;
import com.hanson.pintu.data.pojo.ListCellAttr;
import com.hanson.pintu.data.pojo.ListTable;
import com.hanson.pintu.view.MyImageView;

public class ListGridAdapter extends BaseAdapter {

	private Context context;
	private ArrayList<ListCellAttr> list = new ArrayList<ListCellAttr>();
	/**
	 * @param context
	 */
	public ListGridAdapter(Context context) {
		this.context = context;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public ListTable getItem(int position) {
		// TODO Auto-generated method stub
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		MyImageView iv = null;
		ListCellAttr lt = (ListCellAttr)getItem(position);
		if (convertView == null) {
			iv = new MyImageView(context);
			iv.setAdjustViewBounds(true);
            iv.setScaleType(ImageView.ScaleType.FIT_XY);
            iv.setBackgroundResource(R.drawable.white_corner);
		}
		else {
        	iv = (MyImageView) convertView;
        }
		
		//严重提示：一定要把 Bitmap 赋值放在最后，如果放在判断 if (convertView == null) 里，会造成图片乱闪，图片显示不正常
		iv.setImageBitmap(lt.getBitmap());
		iv.setNumSign(position + 1);
		iv.setbSelected(lt.isbSelected());
		iv.setText(lt.getTypeName());
		return iv;
	}

	public ArrayList<ListCellAttr> getDataList() {
		return list;
	}

	public void setDataList(ArrayList<ListCellAttr> list) {
		this.list = list;
	}
}
