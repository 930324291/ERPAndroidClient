package com.wq.erpandroidclient;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.wq.erpandroidclient.adapter.SuperAdapter;

/**
 * 
 * @author 王强
 * 日期：2014年9月3日
 * 简介：
 */
public class BaoBiaoDetialAct extends Activity implements OnClickListener {
	ListView lv;
	List<ContentValues> lvData = new ArrayList<ContentValues>();
	SuperAdapter adapter;
	ProgressDialog pd;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.kao_qin_detial_act_layout);
		pd = new ProgressDialog(this);
		pd.setMessage(getString(R.string.pd_text));
		ContentValues value = getIntent().getParcelableExtra("data");
		Iterator<Entry<String,Object>> it = value.valueSet().iterator();
		while (it.hasNext()){
			Entry<String,Object> key = it.next();
			ContentValues tem = new ContentValues();
			tem.put(key.getKey(), (String)key.getValue());
			lvData.add(tem);
		}
		lv = (ListView) this.findViewById(R.id.lv);
		adapter = new SuperAdapter(this, R.layout.kao_qin_detial_lv_item, lvData) {

			@Override
			public void dataView(View convertView, ContentValues value, int position) {
				// getCHIN_DetailsInfo(ByVal _ID As Integer)
				TextView tv_key = (TextView) convertView.findViewById(R.id.tv_key);
				TextView tv_value = (TextView) convertView.findViewById(R.id.tv_value);
				Iterator<Entry<String,Object>> it = value.valueSet().iterator();
				Entry<String,Object> en = it.next();
				tv_value.setText((String)en.getValue());
				tv_key.setText(en.getKey());
			}
		};
		lv.setAdapter(adapter);
	}//end onCreate
	
	private String getCurrentDateStart() {
		Calendar c = Calendar.getInstance();
		StringBuffer sb = new StringBuffer();
		sb.append(c.get(Calendar.YEAR) + "-");
		sb.append(c.get(Calendar.MONTH) + 1 + "-");
		sb.append("1");
		return sb.toString();
	}
	

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		this.finish();
	}
}
