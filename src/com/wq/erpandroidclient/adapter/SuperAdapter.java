package com.wq.erpandroidclient.adapter;

import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * 
 * @author W.Q
 * @date 2014年3月6日
 */
public abstract class SuperAdapter extends BaseAdapter {
	Context context;
	int layoutID;
	List<ContentValues> data;
	public SuperAdapter(Context context, int layoutID, List<ContentValues> data) {
		this.context = context;
		this.layoutID = layoutID;
		this.data = data;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if(convertView == null){
			convertView = LayoutInflater.from(context).inflate(layoutID, parent, false);
		}
		ContentValues value = data.get(position);
		dataView(convertView, value,position);
		return convertView;
	}
	
	public abstract void dataView(View convertView,ContentValues value,int position);

}
