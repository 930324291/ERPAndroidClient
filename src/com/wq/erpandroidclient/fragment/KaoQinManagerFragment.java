package com.wq.erpandroidclient.fragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.wq.erpandroidclient.EmployeePickerAct;
import com.wq.erpandroidclient.R;
import com.wq.erpandroidclient.adapter.SuperAdapter;
import com.wq.erpandroidclient.net.ServerRequest;
import com.wq.erpandroidclient.util.G;
import com.wq.erpandroidclient.util.JSONParser;
import com.wq.erpandroidclient.util.ParamsHelper;
import com.wq.erpandroidclient.util.S;

/**
 * 考勤管理页面
 * 
 * @author 王强
 *
 */
public class KaoQinManagerFragment extends Fragment implements OnClickListener{
	private final int MEN_DIAN_MSG = 0;
	private final int EM_MSG = 1;
	String account = null;// 当前登录用户名
	Spinner sp_mendian;
	SuperAdapter mdAdapter;
	List<ContentValues> mdData = new ArrayList<ContentValues>();
	EditText et_date;
	ListView lv;
	List<ContentValues> lvData = new ArrayList<ContentValues>();
	SuperAdapter lvAdapter;
	ContentValues userInfo = null;
	ContentValues currentMenDianInfo = null;// 当前选中的门店信息
	ProgressDialog pd;
	int msg_result_count = 0;
	//当前门店下所有的员工信息
	ArrayList<ContentValues> emList = new ArrayList<ContentValues>();
	Button btn_commit,btn_clear,btn_cancel;

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			msg_result_count++;
			if (msg_result_count >= 2) {
				pd.dismiss();
			}
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
					getEmList();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			case EM_MSG:// 获取门店员工列表信息返回
				S.i(obj.toString());
				try {
					List<ContentValues> res = new JSONParser().convertJSONArrayToList(new JSONArray(obj));
					emList.clear();
					emList.addAll(res);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			}//end switch
		}// end handleMessage
	};// end handler

	public void onAttach(Activity activity) {
		super.onAttach(activity);
		pd = new ProgressDialog(activity);
		pd.setMessage(getString(R.string.pd_text));
		account = getActivity().getSharedPreferences(G.config, Context.MODE_PRIVATE).getString("user_name", "");
	}

	/**
	 * 获取门店下员工列表 
	 * 每次切换门店的时候都要调用此方法
	 * 
	 */
	private void getEmList() {
		// get_ChinOHEM_Info(ByVal _MallCode As String, ByVal _EmpCode As
		// String) As String
		// 注释：获取指定门店及员工编号的所有考勤的员工信息：
		// 参数： _MallCode 代表　门店编码,不可为空．
		// _EmpCode 代表　当前登录用户的账号，可为空
		List<ContentValues> params = ParamsHelper.convert("_MallCode", currentMenDianInfo.getAsString("MallCode"),
				"_EmpCode", "");
		ServerRequest.sendRequestWithOrderParams(EM_MSG, handler, "get_ChinOHEM_Info", params,
				"http://SuperLogis.org/get_ChinOHEM_Info");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View rootView = inflater.inflate(R.layout.kao_qin_manager_fragment, container, false);
		btn_commit = (Button) rootView.findViewById(R.id.btn_commit);
		btn_clear = (Button) rootView.findViewById(R.id.btn_clear);
		btn_cancel = (Button) rootView.findViewById(R.id.btn_cancel);
		btn_commit.setOnClickListener(this);
		btn_clear.setOnClickListener(this);
		btn_cancel.setOnClickListener(this);
		
		sp_mendian = (Spinner) rootView.findViewById(R.id.spinner);
		et_date = (EditText) rootView.findViewById(R.id.et_date);
		et_date.setText(getCurrentDate());

		pd.show();
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
					userInfo = null;
					foot_tv_name.setText("");
					currentMenDianInfo = value;
					getEmList();
				}

			}// end onItemSelected

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub

			}
		});

		getMenDianInfo();

		lv = (ListView) rootView.findViewById(R.id.lv);
		lvAdapter = new SuperAdapter(getActivity(), R.layout.kao_qin_manager_lv_item, lvData) {

			@Override
			public void dataView(View convertView, ContentValues value, int position) {
				// TODO Auto-generated method stub
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

		initFootView(inflater);
		lv.setAdapter(lvAdapter);
		return rootView;
	}// end onCreateView

	Button foot_btn;
	TextView foot_tv_name;
	EditText foot_tv_in_time, foot_tv_out_time;

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if (requestCode == 0 && resultCode == Activity.RESULT_OK) {
			// 成功
			 userInfo = data.getParcelableExtra("data");
			 foot_tv_name.setText(userInfo.getAsString("EmpName"));
			 //[{"EmpCode":"001001","EmpName":"刘红丽"}]
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private void initFootView(LayoutInflater inflater) {
		View footView = inflater.inflate(R.layout.kao_qin_manager_lv_footer, null);
		foot_btn = (Button) footView.findViewById(R.id.btn_add);
		foot_tv_name = (TextView) footView.findViewById(R.id.tv2);// 员工名
		foot_tv_in_time = (EditText) footView.findViewById(R.id.tv3);// 上班时间
		foot_tv_out_time = (EditText) footView.findViewById(R.id.tv4);// 下班时间
		foot_tv_name.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getActivity(), EmployeePickerAct.class);
				intent.putExtra("data", emList);
				startActivityForResult(intent, 0);
			}
		});
		foot_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String name = foot_tv_name.getText().toString();
				String inTime = foot_tv_in_time.getText().toString();
				String outTime = foot_tv_out_time.getText().toString();
				if (name.length() < 1) {
					Toast.makeText(getActivity(), "请输入用户名称", Toast.LENGTH_LONG).show();
					return;
				}

				if (inTime.length() < 1) {
					Toast.makeText(getActivity(), "请输入上班时间", Toast.LENGTH_LONG).show();
					return;
				}

				if (outTime.length() < 1) {
					Toast.makeText(getActivity(), "请输入下班时间", Toast.LENGTH_LONG).show();
					return;
				}
//				LineNum ：　行号,例：1,2,3,4….,不可为空；
//		        EmpCode  ：　考勤的员工编号，不可为空；通过接口：get_ChinOHEM_Info
//		        EmpName：　考勤的员工名称，不可为空；通过接口：get_ChinOHEM_Info
//		        MallCode：　门店编号，不可为空；通过接口：getMallInfo
//		        MallName：　门店名称，不可为空；通过接口：getMallInfo
//		        BTime：　上班日期，例：08:20不可为空；
//		        ETime：　下班日期，例：17:35不可为空；
//				ChkDate：　考勤日期，不可为空，默认为当前日期；
//				cMemo：　备注，可为空；
//				EmpCode_Sub：　考勤提交人（当前登录用户的账号），不可为空；

				ContentValues value = new ContentValues();
				value.put("LineNum", String.valueOf(lvData.size()+1));
				value.put("EmpCode", userInfo.getAsString("EmpCode"));
				value.put("EmpName", userInfo.getAsString("EmpName"));
				value.put("MallCode", currentMenDianInfo.getAsString("MallCode"));
				value.put("MallName", currentMenDianInfo.getAsString("MallName"));
				value.put("BTime", inTime);
				value.put("ETime", outTime);
				value.put("ChkDate", getCurrentDate());
				value.put("cMemo", "-");
				value.put("EmpCode_Sub", account);
				lvData.add(value);
				S.i(value.toString());
				lvAdapter.notifyDataSetChanged();
				foot_tv_name.setText("");
				userInfo = null;

			}// end setOnClickListener
		});

		lv.addFooterView(footView, null, false);
	}// end initFootView

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
	    localCalendar.add(5, 0);
	    Date localDate = localCalendar.getTime();
	    return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(localDate);
	}

	/**
	 * 获取门店信息
	 * MallName=麦岛超市 MallCode=C027 EmpCode=004002 EmpName=周洪进
	 */
	private void getMenDianInfo() {
		List<ContentValues> params = ParamsHelper.convert("_MallCode", "", "_EmpCode", account);
		ServerRequest.sendRequestWithOrderParams(MEN_DIAN_MSG, handler, "getMallInfo", params,
				"http://SuperLogis.org/getMallInfo");
	}

	/**
	 * 想服务端提交数据
	 */
	private void commitDataToServer(){
		JSONArray jarray = new JSONArray();
		for(ContentValues value:lvData){
			Iterator<Entry<String,Object>> it = value.valueSet().iterator();
			JSONObject json = new JSONObject();
			while(it.hasNext()){
				Entry<String,Object> key = it.next();
				try {
					json.put(key.getKey(), (String)key.getValue());
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}//end while
			jarray.put(json);
		}//end for
//		InsertCHIN
		ContentValues value = new ContentValues();
		value.put("_CHIN", jarray.toString());
		ServerRequest.sendRequest(0, new Handler(){
			@Override
			public void handleMessage(Message msg) {
				String obj = (String)msg.obj;
				if(obj.contains("true")){
					Toast.makeText(getActivity(), "提交成功！", Toast.LENGTH_LONG).show();
					lvData.clear();
					userInfo=null;
					foot_tv_name.setText("");
					lvAdapter.notifyDataSetChanged();
				}else {
					Toast.makeText(getActivity(), "提交失败！", Toast.LENGTH_LONG).show();
				}
			}
		}, "InsertCHIN", value, "http://SuperLogis.org/InsertCHIN");
	}//end commitDataToServer
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.btn_commit:
			if(lvData.size()>0){
				commitDataToServer();
			} else {
				Toast.makeText(getActivity(), "请填写考勤数据！", Toast.LENGTH_LONG).show();
			}
			break;
		case R.id.btn_clear:
			lvData.clear();
			lvAdapter.notifyDataSetChanged();
			userInfo = null;
			foot_tv_name.setText("");
			break;
		case R.id.btn_cancel:
			getActivity().finish();
			break;
		}
	}//end onClick

}
