package com.wq.erpandroidclient.fragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;

import com.wq.erpandroidclient.BaoBiaoDetialAct;
import com.wq.erpandroidclient.R;
import com.wq.erpandroidclient.adapter.SuperAdapter;
import com.wq.erpandroidclient.net.ServerRequest;
import com.wq.erpandroidclient.util.G;
import com.wq.erpandroidclient.util.JSONParser;
import com.wq.erpandroidclient.util.ParamsHelper;
import com.wq.erpandroidclient.util.S;

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
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class WuLiuLiRunFragment extends Fragment implements OnItemClickListener, OnClickListener {
	List<ContentValues> lvData = new ArrayList<ContentValues>();
	SuperAdapter adapter;
	EditText et_start, et_end;
	ProgressDialog pd;
	Button btn_search;
	ListView lv;
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			pd.dismiss();
			String obj = (String) msg.obj;
			switch (msg.what) {
			case 0:
				try {
					List<ContentValues> res = new JSONParser().convertJSONArrayToList(new JSONArray(obj));
					for (ContentValues value : res) {
						lvData.add(value);
					}
					adapter.notifyDataSetChanged();
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
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View rootView = inflater.inflate(R.layout.wu_liu_li_run_fragment_layout, container, false);
		et_start = (EditText) rootView.findViewById(R.id.et_start);
		et_end = (EditText) rootView.findViewById(R.id.et_end);
		et_end.setText(getCurrentDate());
		et_start.setText(getCurrentDateStart());
		lv = (ListView) rootView.findViewById(R.id.lv);
		adapter = new SuperAdapter(getActivity(), R.layout.li_run_lv_item, lvData) {
			@Override
			public void dataView(View convertView, ContentValues value, int position) {
				// TODO Auto-generated method stub
				TextView tv1 = (TextView) convertView.findViewById(R.id.tv1);
				TextView tv2 = (TextView) convertView.findViewById(R.id.tv2);
				TextView tv3 = (TextView) convertView.findViewById(R.id.tv3);
				TextView tv4 = (TextView) convertView.findViewById(R.id.tv4);
				TextView tv5 = (TextView) convertView.findViewById(R.id.tv5);
				tv1.setText(String.valueOf(lvData.indexOf(value) + 1));
				tv2.setText(value.getAsString("年度"));
				tv3.setText(value.getAsString("月份"));
				tv4.setText(value.getAsString("总毛利润"));
				tv5.setText(value.getAsString("净利润金额"));
				convertView.setTag(value);
			}
		};
		lv.setAdapter(adapter);
		lv.setOnItemClickListener(this);
		btn_search = (Button) rootView.findViewById(R.id.btn_search);
		btn_search.setOnClickListener(this);
		return rootView;
	}

	/**
	 * 获取当前系统日期
	 * 
	 * @return
	 */
	private String getCurrentDate() {
		Calendar localCalendar = Calendar.getInstance();
	    localCalendar.setTime(new Date());
	    localCalendar.add(5, 1);
	    Date localDate = localCalendar.getTime();
	    return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(localDate);
	}

	private String getCurrentDateStart() {
		Calendar localCalendar = Calendar.getInstance();
	    StringBuffer localStringBuffer = new StringBuffer();
	    localStringBuffer.append(localCalendar.get(1) + "-");
	    localStringBuffer.append(1 + localCalendar.get(2) + "-");
	    localStringBuffer.append("1");
	    return localStringBuffer.toString();
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		// TODO Auto-generated method stub
		Intent intent = new Intent(getActivity(), BaoBiaoDetialAct.class);
		intent.putExtra("data", (ContentValues) view.getTag());
		startActivity(intent);
	}
	
	private void getData(){
		// p_Report_QryLogsProfit(ByVal _bDate As String, ByVal _eDate As String)
		// 参数： _bDate　代表　查询的开始日期
		// _eDate 代表　查询的结束日期
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
		List<ContentValues> params = ParamsHelper.convert("_bDate", sTime, "_eDate", eTime);
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
		}, "p_Report_QryLogsProfit", params, "http://SuperLogis.org/p_Report_QryLogsProfit");
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		getData();
	}
}
