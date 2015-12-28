package com.wq.erpandroidclient;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.wq.erpandroidclient.adapter.SuperAdapter;
import com.wq.erpandroidclient.util.ViewHolder;

/**
 * 程序的主界面
 * 
 * @author W.Q
 * @date 2014年3月30日
 */
public class MainActivity extends Activity implements OnItemClickListener {
	ListView lv;
	List<ContentValues> lvData = new ArrayList<ContentValues>();
	SuperAdapter adapter = null;

	String itemTexts[] = { "要货", "报表", "手机考勤","修改密码" };
	private int[] resource = { R.drawable.goods, R.drawable.forms, R.drawable.work, R.drawable.pwd };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.main_layout);
		lv = (ListView) this.findViewById(R.id.lv);

		ContentValues value = null;
		for (int i = 0; i < itemTexts.length; i++) {
			value = new ContentValues();
			value.put("title", itemTexts[i]);
			value.put("icon",resource[i]);
			lvData.add(value);
		}

		adapter = new SuperAdapter(this, R.layout.main_lv_item, lvData) {

			@Override
			public void dataView(View convertView, ContentValues value,int position) {
				// TODO Auto-generated method stub
				ImageView iv = ViewHolder.get(convertView, R.id.iv);
				iv.setImageResource(value.getAsInteger("icon"));
				TextView tv = ViewHolder.get(convertView, R.id.tv);
				tv.setText(value.getAsString("title"));
			}
		};

		lv.setAdapter(adapter);
		lv.setOnItemClickListener(this);

	}// end onCreate

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		switch (arg2) {
		case 0://要货界面
			this.startActivity(new Intent(this, YaoHuoActivity.class));
			break;
		case 1://报表
			this.startActivity(new Intent(this, BaoBiaoActivity.class));
			break;
		case 2://考勤
			this.startActivity(new Intent(this, KaoQinActivity.class));
			break;
		case 3://
			this.startActivity(new Intent(this, ChangePSWAct.class));
			break;
		}
	}//end onIt
}// end MainActivity
