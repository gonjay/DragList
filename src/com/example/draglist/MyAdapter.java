package com.example.draglist;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class MyAdapter extends BaseAdapter {

	private LayoutInflater mInflater;    
	public List<String> mData;    
	public Map<Integer, Boolean> isSelected;
	private String[] data = new String[]{
			"≤‚ ‘1","≤‚ ‘2","≤‚ ‘3",
			"≤‚ ‘4","≤‚ ‘5","≤‚ ‘6",
			"≤‚ ‘7","≤‚ ‘8","≤‚ ‘9",
			"≤‚ ‘10","≤‚ ‘11","≤‚ ‘12",
	};
	public MyAdapter(Context context) {    
		mInflater = LayoutInflater.from(context);    
		init();    
	}    
	private void init() {
		mData=new ArrayList<String>();    
		for (int i = 0; i < data.length; i++) {    
			mData.add(data[i]);    
		} 
		isSelected = new HashMap<Integer, Boolean>();    
		for (int i = 0; i < mData.size(); i++) {    
			isSelected.put(i, false);    
		}    
		
	}
	public void delItem(Collection<String> arg){
		mData.removeAll(arg);
	}
	@Override
	public int getCount() {
		return mData.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		ViewHolder holder = null;    
		//convertViewŒ™nullµƒ ±∫Ú≥ı ºªØconvertView°£    
		if (convertView == null) {    
			holder = new ViewHolder();    
			convertView = mInflater.inflate(R.layout.listdetail, null);
			holder.item = (TextView)convertView.findViewById(R.id.item);
			convertView.setTag(holder);    
		} else {    
			holder = (ViewHolder) convertView.getTag();    
		}    
		holder.item.setText(mData.get(position)); 
		if (isSelected.get(position)) {
			convertView.setBackgroundResource(R.drawable.list_yellow_single);
		}
		else {
			convertView.setBackgroundResource(R.drawable.select);
		}
		return convertView;   
	}
	public final class ViewHolder {    
		public TextView item;    
	} 

}
