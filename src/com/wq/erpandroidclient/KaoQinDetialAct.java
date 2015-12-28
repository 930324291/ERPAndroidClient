package com.wq.erpandroidclient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.wq.erpandroidclient.adapter.SuperAdapter;
import com.wq.erpandroidclient.net.ServerRequest;
import com.wq.erpandroidclient.util.JSONParser;

/**
 * 考勤明细页面
 * 
 * @author Administrator
 *
 */
public class KaoQinDetialAct extends Activity implements OnClickListener {
	ListView lv;
	List<ContentValues> lvData = new ArrayList<ContentValues>();
	SuperAdapter adapter;
	ProgressDialog pd;
	HashMap<String,String> keys = new HashMap<String,String>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.kao_qin_detial_act_layout);
		pd = new ProgressDialog(this);
		pd.setMessage(getString(R.string.pd_text));
		initKeys();
		ContentValues value = getIntent().getParcelableExtra("data");
		lv = (ListView) this.findViewById(R.id.lv);
		adapter = new SuperAdapter(this, R.layout.kao_qin_detial_lv_item, lvData) {

			@Override
			public void dataView(View convertView, ContentValues value, int position) {
				// getCHIN_DetailsInfo(ByVal _ID As Integer)
				TextView tv_key = (TextView) convertView.findViewById(R.id.tv_key);
				TextView tv_value = (TextView) convertView.findViewById(R.id.tv_value);
				Iterator<String> it = value.keySet().iterator();
				String key = it.next();
				tv_value.setText(value.getAsString(key));
				tv_key.setText(keys.get(key));
			}
		};
		lv.setAdapter(adapter);
		getDataById(value.getAsString("ID"));
	}//end onCreate
	
	private void initKeys(){
		keys.put("ID", "序号");
		keys.put("LineNum", "行号");
		keys.put("LineStatus", "行状态");
		keys.put("MallCode", "门店编号");
		keys.put("MallName", "门店名称");
		keys.put("EmpCode", "被考勤员工编号");
		keys.put("EmpName", "被考勤员工名称");
		keys.put("ChkDate", "考勤日期");
		keys.put("cMemo", "备注");
		keys.put("IsAudit", "是否审核");
		keys.put("EmpCode_Sub", "提交考勤人员编号");
		keys.put("EmpName_Sub", "提交考试人员名称");
		keys.put("BTime", "上班时间");
		keys.put("ETime", "下班时间");
	}

	private void getDataById(String id) {
		ContentValues value = new ContentValues();
		value.put("_ID ", Integer.parseInt(id));
		ServerRequest.sendRequest(0, new Handler() {
			@Override
			public void handleMessage(Message msg) {
				pd.dismiss();
				String obj = (String) msg.obj;
				try {
					JSONArray jarray = new JSONArray(obj);
					List<ContentValues> res = new JSONParser().convertJSONArrayToList(jarray);
					if (res != null && res.size() > 0) {
						ContentValues value = res.get(0);
						Iterator<String> it = value.keySet().iterator();
						while (it.hasNext()) {
							String key = it.next();
							ContentValues temV = new ContentValues();
							temV.put(key, value.getAsString(key));
							lvData.add(temV);
						}
						adapter.notifyDataSetChanged();
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}, "getCHIN_DetailsInfo", value, "http://SuperLogis.org/getCHIN_DetailsInfo");
		pd.show();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		this.finish();
	}
}
