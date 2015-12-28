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
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.wq.erpandroidclient.KaoQinDetialAct;
import com.wq.erpandroidclient.R;
import com.wq.erpandroidclient.adapter.SuperAdapter;
import com.wq.erpandroidclient.net.ServerRequest;
import com.wq.erpandroidclient.util.G;
import com.wq.erpandroidclient.util.JSONParser;
import com.wq.erpandroidclient.util.ParamsHelper;
import com.wq.erpandroidclient.util.S;

/**
 * 考勤明细查询页面
 * 
 * @author 王强
 *
 */
public class KaoQinSearchFragment extends Fragment implements OnItemClickListener {
	private final int MEN_DIAN_MSG = 0;
	private final int LV_DATA_MSG = 1;

	String account = null;// 当前登录用户名
	Spinner sp_mendian;
	SuperAdapter mdAdapter;
	List<ContentValues> mdData = new ArrayList<ContentValues>();
	EditText et_date;
	ListView lv;
	List<ContentValues> lvData = new ArrayList<ContentValues>();
	SuperAdapter lvAdapter;
	ContentValues currentMenDianInfo = null;// 当前选中的门店信息
	ProgressDialog pd;
	Button btn_search;
	// LineNum : 行号
	// LineStatus : 行状态
	// MallCode : 门店编号
	// MallName　：门店名称
	// EmpCode : 被考勤员工编号
	// EmpName : 被考勤员工名称
	// ChkDate　：考勤日期
	// cMemo : 备注
	// IsAudit　：是否审核
	// EmpCode_Sub : 提交考勤人员编号
	// EmpName_Sub : 提交考试人员名称
	// BTime : 上班时间
	// ETime　：　下班时间

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
						mdData.add(value);
					}
					if (mdData.size() > 0)
						currentMenDianInfo = mdData.get(0);
					mdAdapter.notifyDataSetChanged();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			case LV_DATA_MSG:
				S.i(msg.toString());
				try {
					JSONArray array = new JSONArray(obj);
					List<ContentValues> res = new JSONParser().convertJSONArrayToList(array);
					if (res != null) {
						lvData.addAll(res);
						lvAdapter.notifyDataSetChanged();
					}
				} catch (Exception e) {
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
		View rootView = inflater.inflate(R.layout.kao_qin_search_fragment, container, false);
		sp_mendian = (Spinner) rootView.findViewById(R.id.spinner);
		mdAdapter = new SuperAdapter(getActivity(), android.R.layout.simple_dropdown_item_1line, mdData) {

			@Override
			public void dataView(View convertView, ContentValues value, int position) {
				// TODO Auto-generated method stub
				TextView tv = (TextView) convertView.findViewById(android.R.id.text1);
				tv.setText(value.getAsString("MallName"));
				convertView.setTag(value);
			}
		};
		sp_mendian.setAdapter(mdAdapter);
		sp_mendian.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				ContentValues value = (ContentValues) view.getTag();
				if (value != null && !value.equals(currentMenDianInfo)) {
					lvData.clear();
					lvAdapter.notifyDataSetChanged();
					currentMenDianInfo = value;
				}

			}// end onItemSelected

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub

			}
		});
		et_date = (EditText) rootView.findViewById(R.id.et_date);
		et_date.setText(getCurrentDate());
		lv = (ListView) rootView.findViewById(R.id.lv);
		lvAdapter = new SuperAdapter(getActivity(), R.layout.kao_qin_manager_lv_item, lvData) {

			@Override
			public void dataView(View convertView, ContentValues value, int position) {
				// {
				// "ID":1,
				// "LineNum":1,
				// "LineStatus":"未清",
				// "MallCode":"C001",
				// "MallName":"四方利群",
				// "EmpCode":"001001",
				// "EmpName":"刘红丽",
				// "ChkDate":"2014-09-03",
				// "cMemo":"-",
				// "IsAudit":"未审核",
				// "EmpCode_Sub":"001001",
				// "EmpName_Sub":"刘红丽",
				// "BTime":"08:30",
				// "ETime":"17:30"
				// },
				TextView tv1 = (TextView) convertView.findViewById(R.id.tv1);// 序号
				TextView tv2 = (TextView) convertView.findViewById(R.id.tv2);// 员工名
				TextView tv3 = (TextView) convertView.findViewById(R.id.tv3);// 上班时间
				TextView tv4 = (TextView) convertView.findViewById(R.id.tv4);// 下班时间
				tv1.setText(String.valueOf(1 + lvData.indexOf(value)));
				tv2.setText(value.getAsString("EmpName"));
				tv3.setText(value.getAsString("BTime"));
				tv4.setText(value.getAsString("ETime"));
				convertView.setTag(value);
			}
		};

		lv.setAdapter(lvAdapter);
		lv.setOnItemClickListener(this);
		btn_search = (Button) rootView.findViewById(R.id.btn_search);
		btn_search.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String mallCode = currentMenDianInfo == null ? "" : currentMenDianInfo.getAsString("MallCode");
				if (mallCode.length() == 0) {
					// getCHIN_InfoBySubUserCode(ByVal _EmpCode_Sub As String,
					// ByVal _MallCode As String, ByVal _ChkDate As String
					// ) As String

					Toast.makeText(getActivity(), "请选择门店信息！", Toast.LENGTH_LONG).show();
					return;
				}
				String date = et_date.getText().toString().trim();
				if (date.length() < 8) {
					Toast.makeText(getActivity(), "请选择门店信息！", Toast.LENGTH_LONG).show();
					return;
				}
				// getCHIN_InfoBySubUserCode(ByVal _EmpCode_Sub As String, ByVal
				// _MallCode As String, ByVal _ChkDate As String
				List<ContentValues> params = ParamsHelper.convert("_EmpCode_Sub", account, "_MallCode", mallCode,
						"_ChkDate", date);
				for (ContentValues vs : params) {
					S.i(vs.toString());
				}
				ServerRequest.sendRequestWithOrderParams(LV_DATA_MSG, handler, "getCHIN_InfoBySubUserCode", params,
						"http://SuperLogis.org/getCHIN_InfoBySubUserCode");
				pd.show();
			}// end onClick
		});
		getMenDianInfo();
		return rootView;
	}

	/**
	 * 获取门店信息 MallName=麦岛超市 MallCode=C027 EmpCode=004002 EmpName=周洪进
	 */
	private void getMenDianInfo() {
		List<ContentValues> params = ParamsHelper.convert("_MallCode", "", "_EmpCode", account);
		ServerRequest.sendRequestWithOrderParams(MEN_DIAN_MSG, handler, "getMallInfo", params,
				"http://SuperLogis.org/getMallInfo");
		pd.show();
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

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		// TODO Auto-generated method stub
		ContentValues value = (ContentValues) view.getTag();
		Intent intent = new Intent(getActivity(), KaoQinDetialAct.class);
		intent.putExtra("data", value);
		startActivity(intent);
	}

}
