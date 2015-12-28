package com.wq.erpandroidclient;

import java.util.ArrayList;
import java.util.List;

import com.wq.erpandroidclient.adapter.SuperAdapter;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

/**
 * 员工选择界面
 * @author Administrator
 *
 */
public class EmployeePickerAct extends Activity implements OnItemClickListener ,OnClickListener {
	ListView lv;
	List<ContentValues> oData = null;
	List<ContentValues> lvData = new ArrayList<ContentValues>();
	SuperAdapter adapter;
	Button btn_search;
	EditText et_search;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.choose_goods_act_layout);
		lv = (ListView) this.findViewById(R.id.lv);
		btn_search = (Button) this.findViewById(R.id.btn_search);
		btn_search.setOnClickListener(this);
		et_search = (EditText) this.findViewById(R.id.et_search);
		oData = getIntent().getParcelableArrayListExtra("data");
		lvData.addAll(oData);
		adapter = new SuperAdapter(this,android.R.layout.simple_list_item_1,lvData) {
			
			@Override
			public void dataView(View convertView, ContentValues value, int position) {
				// TODO Auto-generated method stub
				TextView tv = (TextView) convertView.findViewById(android.R.id.text1);
				tv.setText(value.getAsString("EmpName"));
				convertView.setTag(value);
			}
		};
		et_search.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				String key = s.toString();
				if(key.length()>0){
					search(key);
				} else {
					lvData.clear();
					lvData.addAll(oData);
					adapter.notifyDataSetChanged();
				}
			}
		});
		lv.setAdapter(adapter);
		lv.setOnItemClickListener(this);
	}
	
	private void search(String key){
		key = key.trim();
		if(key == null || key.length()<1){
			return;
		}
		lvData.clear();
		for(ContentValues value : oData){
			if(value.getAsString("EmpName").contains(key)){
				lvData.add(value);
			}
		}
		adapter.notifyDataSetChanged();
	}//end search
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		// TODO Auto-generated method stub
		ContentValues value = (ContentValues)view.getTag();
		Intent resIntent = new Intent();
		resIntent.putExtra("data", value);
		this.setResult(Activity.RESULT_OK, resIntent);
		this.finish();
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		search(et_search.getText().toString());
	}
}
