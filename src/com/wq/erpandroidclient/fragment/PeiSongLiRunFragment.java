package com.wq.erpandroidclient.fragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.wq.erpandroidclient.BaoBiaoDetialAct;
import com.wq.erpandroidclient.R;
import com.wq.erpandroidclient.adapter.SuperAdapter;
import com.wq.erpandroidclient.net.ServerRequest;
import com.wq.erpandroidclient.util.G;
import com.wq.erpandroidclient.util.JSONParser;
import com.wq.erpandroidclient.util.ParamsHelper;
import com.wq.erpandroidclient.util.S;

/**
 * 
 * @author 王强 日期：2014年9月3日 简介：
 */
public class PeiSongLiRunFragment extends Fragment implements OnItemClickListener, OnClickListener {
	private final int MEN_DIAN_MSG = 0;
	List<ContentValues> lvData = new ArrayList<ContentValues>();
	List<ContentValues> oMDData = new ArrayList<ContentValues>();
	String account = null;
	ListView lv;
	SuperAdapter adapter, startAdapter, endAdapter;
	Spinner sp_md_start, sp_md_end;
	EditText et_start, et_end;
	ProgressDialog pd;
	Button btn_search;
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			pd.dismiss();
			String obj = (String) msg.obj;
			switch (msg.what) {
			case MEN_DIAN_MSG:
				try {
					List<ContentValues> res = new JSONParser().convertJSONArrayToList(new JSONArray(obj));
					for (ContentValues value : res) {
						oMDData.add(value);
					}
					startAdapter.notifyDataSetChanged();
					endAdapter.notifyDataSetChanged();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			}// end switch
		}// end handleMessage
	};// end handler

	public void onAttach(Activity activity) {
		super.onAttach(activity);
		pd = new ProgressDialog(activity);
		pd.setMessage(getString(R.string.pd_text));
		account = getActivity().getSharedPreferences(G.config, Context.MODE_PRIVATE).getString("user_name", "");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View rootView = inflater.inflate(R.layout.pei_song_li_run_fragment_layout, container, false);
		startAdapter = new SuperAdapter(getActivity(), android.R.layout.simple_dropdown_item_1line, oMDData) {

			@Override
			public void dataView(View convertView, ContentValues value, int position) {
				// TODO Auto-generated method stub
				TextView tv = (TextView) convertView.findViewById(android.R.id.text1);
				tv.setText(value.getAsString("MallName"));
				convertView.setTag(value);
			}
		};
		endAdapter = new SuperAdapter(getActivity(), android.R.layout.simple_dropdown_item_1line, oMDData) {

			@Override
			public void dataView(View convertView, ContentValues value, int position) {
				// TODO Auto-generated method stub
				TextView tv = (TextView) convertView.findViewById(android.R.id.text1);
				tv.setText(value.getAsString("MallName"));
				convertView.setTag(value);
			}
		};

		sp_md_start = (Spinner) rootView.findViewById(R.id.sp_md_start);
		sp_md_end = (Spinner) rootView.findViewById(R.id.sp_md_end);
		sp_md_end.setAdapter(endAdapter);
		
		sp_md_start.setAdapter(startAdapter);

		et_start = (EditText) rootView.findViewById(R.id.et_start);
		et_start.setText(getCurrentDateStart());
		et_end = (EditText) rootView.findViewById(R.id.et_end);
		et_end.setText(getCurrentDate());
		lv = (ListView) rootView.findViewById(R.id.lv);
		adapter = new SuperAdapter(getActivity(), R.layout.li_run_lv_item, lvData) {
//			"业务伙伴编号":"C001",
//			"业务伙伴名称":"四方利群",
//			"年度":"2014",
//			"月份":"10",
//			"开票数":0,
//			"含税数":0,
//			"销售":0,
//			"净销售":0,
//			"采购成本":0,
//			"费用":0,
//			"本月工资":0,
//			"净利润金额":0,
//			"其他":0,
//			"备注":""
			@Override
			public void dataView(View convertView, ContentValues value, int position) {
				// TODO Auto-generated method stub
				TextView tv1 = (TextView) convertView.findViewById(R.id.tv1);
				TextView tv2 = (TextView) convertView.findViewById(R.id.tv2);
				TextView tv3 = (TextView) convertView.findViewById(R.id.tv3);
				TextView tv4 = (TextView) convertView.findViewById(R.id.tv4);
				TextView tv5 = (TextView) convertView.findViewById(R.id.tv5);
				tv1.setText(String.valueOf(lvData.indexOf(value) + 1));
				tv2.setText(value.getAsString("门店名称"));
				tv3.setText(value.getAsString("实分量"));
				tv4.setText(value.getAsString("分配单价"));
				tv5.setText(value.getAsString("分配金额"));
				convertView.setTag(value);
			}
		};
		lv.setAdapter(adapter);
		lv.setOnItemClickListener(this);
		btn_search = (Button) rootView.findViewById(R.id.btn_search);
		btn_search.setOnClickListener(this);
		getMenDianInfo();
		return rootView;
	}
	
	private String getCurrentDateStart() {
		Calendar localCalendar = Calendar.getInstance();
	    StringBuffer localStringBuffer = new StringBuffer();
	    localStringBuffer.append(localCalendar.get(1) + "-");
	    localStringBuffer.append(1 + localCalendar.get(2) + "-");
	    localStringBuffer.append("1");
	    return localStringBuffer.toString();
	}

	/**
	 * 获取当前系统日期
	 * 
	 * @return
	 */
	private String getCurrentDate() {
//		Calendar c = Calendar.getInstance();
//		StringBuffer sb = new StringBuffer();
//		sb.append(c.get(Calendar.YEAR) + "-");
//		sb.append(c.get(Calendar.MONTH) + 1 + "-");
//		sb.append(c.get(Calendar.DATE) + 1);
//		return sb.toString();
		Calendar localCalendar = Calendar.getInstance();
	    localCalendar.setTime(new Date());
	    localCalendar.add(5, 1);
	    Date localDate = localCalendar.getTime();
	    return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(localDate);
	}

	/**
	 * 获取门店信息 MallName=麦岛超市 MallCode=C027 EmpCode=004002 EmpName=周洪进
	 */
	private void getMenDianInfo() {
		List<ContentValues> params = ParamsHelper.convert("_MallCode", "", "_EmpCode", account);
		ServerRequest.sendRequestWithOrderParams(MEN_DIAN_MSG, handler, "getMallInfo", params,
				"http://SuperLogis.org/getMallInfo");
	}

	/**
	 * 获取列表数据
	 */
	private void getData() {
		ContentValues sv = (ContentValues) sp_md_start.getSelectedItem();
		ContentValues ev = (ContentValues) sp_md_end.getSelectedItem();
		S.i(sv.toString());
		S.i(ev.toString());
		String sTime = et_start.getText().toString();
		String eTime = et_end.getText().toString();
		if (sTime.length() < 8) {
			Toast.makeText(getActivity(), "请按照格式输入正确的开始时间", Toast.LENGTH_LONG).show();
			return;
		}
		if (eTime.length() < 8) {
			Toast.makeText(getActivity(), "请按照格式输入正确的结束时间", Toast.LENGTH_LONG).show();
			return;
		}
		List<ContentValues> params = ParamsHelper.convert("_bMallCode", sv.getAsString("MallCode"), "_eMallCode",
				ev.getAsString("MallCode"), "_bDate", sTime, "_eDate", eTime);
		ServerRequest.sendRequestWithOrderParams(0, new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				S.i(msg.toString());
				String obj = (String) msg.obj;
				try {
					JSONArray jarray = new JSONArray(obj);
					List<ContentValues> res = new JSONParser().convertJSONArrayToList(jarray);
					if (res != null && res.size() > 0) {
						lvData.clear();
						lvData.addAll(res);
						adapter.notifyDataSetChanged();
					} else {
						Toast.makeText(getActivity(), "暂无数据！", Toast.LENGTH_LONG).show();
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}, "p_Report_QryLogs", params, "http://SuperLogis.org/p_Report_QryLogs");

	}// end getData

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		// TODO Auto-generated method stub
		Intent intent = new Intent(getActivity(),BaoBiaoDetialAct.class);
		intent.putExtra("data", (ContentValues)view.getTag());
		startActivity(intent);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		getData();
	}

}// end onCreateView
