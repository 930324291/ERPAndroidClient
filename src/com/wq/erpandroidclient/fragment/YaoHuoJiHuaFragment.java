package com.wq.erpandroidclient.fragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
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
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.wq.erpandroidclient.GoodsChoseAct;
import com.wq.erpandroidclient.R;
import com.wq.erpandroidclient.adapter.SuperAdapter;
import com.wq.erpandroidclient.net.ServerRequest;
import com.wq.erpandroidclient.util.G;
import com.wq.erpandroidclient.util.JSONParser;
import com.wq.erpandroidclient.util.ParamsHelper;
import com.wq.erpandroidclient.util.S;
import com.wq.erpandroidclient.util.SPHelper;
import com.wq.erpandroidclient.util.ViewHolder;

/**
 * 要货计划页面
 * 
 * @author W.Q
 * @date 2014年4月2日
 */
public class YaoHuoJiHuaFragment extends Fragment implements OnItemClickListener, OnClickListener {
	final int MEN_DIAN_MSG = 0;
	final int WU_LIAO_ZU_MSG = 1;
	final int GET_ORDER_NO = 2;
	final int COMMIT_TO_SERVER = 3;

	ListView lv;
	List<ContentValues> lvData = new ArrayList<ContentValues>();
	SuperAdapter adapter;
	View lvFooter;
	Button btn_add;// 增加数据按钮
	EditText et4;// 单品号，数量，单位
	TextView tv_danpinhao, tv_danwei;
	Button btnPingMing;// 品名
	ArrayList<ContentValues> spinnerPMData = new ArrayList<ContentValues>();

	Button btn_commit, btn_clear, btn_cancel;
	EditText et_order_no;// 订单编号
	EditText et_daohuoriqi;// 到货日期
	ContentValues menDianInfo = null;// 记录当前选中门店信息
	ContentValues pinMingInfo = null;// 记录用户选择的品名信息

	// 门店列表
	Spinner spinnerMD;
	List<ContentValues> spinnerMDData = new ArrayList<ContentValues>();
	ProgressDialog pd;
	int backMsgCount = 0;
	// 处理消息返回
	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			String obj = (String) msg.obj;
			switch (msg.what) {
			case MEN_DIAN_MSG:
				S.i("-" + obj);
				backMsgCount++;
				utilMenDianMsg(obj);
				break;
			case WU_LIAO_ZU_MSG:
				S.i("-" + obj);
				backMsgCount++;
				utilPinMingMsg(obj);
				break;
			case GET_ORDER_NO:
				backMsgCount++;
				S.i(obj);
				try {
					JSONArray array = new JSONArray(obj);
					String orderNo = array.getJSONObject(0).getString("NewDocEntry");
					et_order_no.setText(orderNo);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				break;
			case COMMIT_TO_SERVER:
				S.i(obj);
				if ("true".equals(obj)) {
					// 成功
					lvData.clear();
					adapter.notifyDataSetChanged();
					getActivity().sendBroadcast(new Intent("commit.yaohuo.successed"));
					Toast.makeText(getActivity(), "提交成功！", Toast.LENGTH_LONG).show();
				} else {
					Toast.makeText(getActivity(), "提交失败请重新提交！", Toast.LENGTH_LONG).show();
				}

				break;
			}// end switch
			if (backMsgCount > 2) {
				if (pd.isShowing()) {
					pd.cancel();
				}
			}
		};
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View rootView = inflater.inflate(R.layout.yao_huo_ji_hua_fragment_layout, container, false);
		pd = new ProgressDialog(getActivity());
		pd.setMessage(getString(R.string.pd_text));
		pd.show();
		lv = (ListView) rootView.findViewById(R.id.lv);
		spinnerMD = (Spinner) rootView.findViewById(R.id.spinner_mendian);
		adapter = new SuperAdapter(getActivity(), R.layout.yao_huo_lv_item, lvData) {

			@Override
			public void dataView(View convertView, ContentValues value, int position) {
				// TODO Auto-generated method stub
				TextView tv1 = ViewHolder.get(convertView, R.id.tv1);
				tv1.setText(String.valueOf(position + 1));
				TextView tv2 = ViewHolder.get(convertView, R.id.tv2);
				tv2.setText(value.getAsString("ItemCode"));
				TextView tv3 = ViewHolder.get(convertView, R.id.tv3);
				tv3.setText(value.getAsString("ItemName"));
				TextView tv4 = ViewHolder.get(convertView, R.id.tv4);
				tv4.setText(value.getAsString("ShuLiang"));
				TextView tv5 = ViewHolder.get(convertView, R.id.tv5);
				tv5.setText(value.getAsString("DanWei"));
			}
		};

		btn_commit = (Button) rootView.findViewById(R.id.btn_commit);
		btn_commit.setOnClickListener(this);
		btn_clear = (Button) rootView.findViewById(R.id.btn_clear);
		btn_clear.setOnClickListener(this);
		btn_cancel = (Button) rootView.findViewById(R.id.btn_cancel);
		btn_cancel.setOnClickListener(this);
		et_order_no = (EditText) rootView.findViewById(R.id.et_order_no);
		et_daohuoriqi = (EditText) rootView.findViewById(R.id.et_daohuoriqi);
		et_daohuoriqi.setText(getDaoHuoRiQi());

		lvFooter = inflater.inflate(R.layout.yao_huo_lv_footer, null);
		btn_add = (Button) lvFooter.findViewById(R.id.btn_add);
		btn_add.setOnClickListener(this);
		tv_danpinhao = (TextView) lvFooter.findViewById(R.id.tv_danpinhao);
		btnPingMing = (Button) lvFooter.findViewById(R.id.btn3);

		btnPingMing.setOnClickListener(this);
		et4 = (EditText) lvFooter.findViewById(R.id.et4);
		tv_danwei = (TextView) lvFooter.findViewById(R.id.tv_danwei);

		lv.addFooterView(lvFooter);
		lv.setAdapter(adapter);
		initSpinnerMenDian();
		intiSpPinMing();
		getOrderNo();
		return rootView;
	}// end onCreateView

	/**
	 * 获取当前系统日期
	 * 
	 * @return
	 */
	private String getDaoHuoRiQi() {
		Calendar localCalendar = Calendar.getInstance();
	    localCalendar.setTime(new Date());
	    localCalendar.add(5, 1);
	    Date localDate = localCalendar.getTime();
	    return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(localDate);
	}

	/**
	 * 获取订单编号
	 */
	private void getOrderNo() {
		ServerRequest.sendRequest(GET_ORDER_NO, handler, "getDocEntry", null, "http://SuperLogis.org/getDocEntry");
	}

	/**
	 * 想服务端提交数据 表头：
	 * 
	 * 序号 参数 参数值 1 docEntry OPL800_2014090 2 MallCode 001 3 MallName 山东路家乐福店 4
	 * EMpCode 001 5 EMpName LongSences 6 ReceveDate 2014-05-20 注：不晚于当前日期 7
	 * cRichMemo 文字性的描述，可随便输入，可空 8 cPhone 提交当前单据的手机型号 天天向上 15:46:29 表体： 序号 参数
	 * 参数值 1 docEntry OPL800_2014090
	 * 
	 * 2 LineNum 1,为第一行的行号，第二行是:2 3 ItemCode 01001 4 ItemName 鲜鲅鱼 5 InvntryUom
	 * KG 6 Quantity 0.01 注：小数后两位 7 ReceDate 2014-05-20
	 * 注：默认同表头的ReceveDate一样，但如果手机端可以将行的日期调为其他日期。 8 cMemo 行的备注，可为空。
	 */
	private void commitDataToServer() {
		if (menDianInfo == null) {
			Toast.makeText(getActivity(), "请选择门店", Toast.LENGTH_LONG).show();
			return;
		}

		try {
			JSONObject jsonHead = new JSONObject();
			jsonHead.put("docEntry", et_order_no.getText().toString());
			jsonHead.put("MallCode", menDianInfo.getAsString("MallCode"));
			jsonHead.put("MallName", menDianInfo.getAsString("MallName"));
			String empCode = SPHelper.getSpValue(getActivity(), "user_name", "001");
			jsonHead.put("EMpCode", empCode);
			jsonHead.put("ReceveDate", et_daohuoriqi.getText().toString());
			jsonHead.put("cRichMemo", "cRichMemo");
			jsonHead.put("cPhone", "android");

			JSONArray bodyArray = new JSONArray();
			JSONObject jsonBody = null;
			if (lvData.size() == 0) {
				Toast.makeText(getActivity(), "数据为空！", Toast.LENGTH_LONG).show();
				return;
			}
			for (int i = 0; i < lvData.size(); i++) {
				ContentValues value = lvData.get(i);
				jsonBody = new JSONObject();
				jsonBody.put("docEntry", et_order_no.getText().toString());
				jsonBody.put("LineNum", String.valueOf(i));

				jsonBody.put("ItemCode", value.getAsString("ItemCode"));
				jsonBody.put("ItemName", value.getAsString("ItemName"));
				jsonBody.put("InvntryUom", value.getAsString("DanWei"));
				jsonBody.put("Quantity", value.getAsString("ShuLiang"));
				jsonBody.put("ReceDate", et_daohuoriqi.getText().toString());
				jsonBody.put("cMemo", "cMemo");
				bodyArray.put(jsonBody);
			}

			S.i(jsonHead.toString());
			S.i(bodyArray.toString());
			ContentValues cv = new ContentValues();
			cv.put("_PM", jsonHead.toString());
			cv.put("_PS", bodyArray.toString());
			ServerRequest.sendRequest(COMMIT_TO_SERVER, handler, "InsertPlan", cv, "http://SuperLogis.org/InsertPlan");
			pd.show();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Toast.makeText(getActivity(), "提交失败", Toast.LENGTH_LONG).show();
		}
	}

	/**
	 * 初始化品名Spinner
	 */
	private void intiSpPinMing() {
		// spPinMing.setOnItemSelectedListener(new OnItemSelectedListener() {
		//
		// @Override
		// public void onItemSelected(AdapterView<?> parent, View view, int
		// position, long id) {
		// // TODO Auto-generated method stub
		// pinMingInfo = (ContentValues) view.getTag();
		// tv_danpinhao.setText(pinMingInfo.getAsString("ItemCode"));
		// tv_danwei.setText(pinMingInfo.getAsString("InvntryUom"));
		// }
		//
		// @Override
		// public void onNothingSelected(AdapterView<?> parent) {
		// // TODO Auto-generated method stub
		//
		// }
		// });
		ServerRequest.sendRequest(WU_LIAO_ZU_MSG, handler, "getItemInfo", null, "http://SuperLogis.org/getItemInfo");
	}// end intiSpPinMing

	/**
	 * 获取门店信息
	 */
	private void initSpinnerMenDian() {
		spinnerMD.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				menDianInfo = (ContentValues) view.getTag();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub

			}
		});
		String account = getActivity().getSharedPreferences(G.config, Context.MODE_PRIVATE).getString("user_name", "");
		List<ContentValues> params = ParamsHelper.convert("_MallCode", "", "_EmpCode", account);
		ServerRequest.sendRequestWithOrderParams(MEN_DIAN_MSG, handler, "getMallInfo", params,
				"http://SuperLogis.org/getMallInfo");
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub

	}// end onItemClick

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_add:
			addData();
			break;
		case R.id.btn_commit:
			// 网络提交
			commitDataToServer();
			break;
		case R.id.btn_clear:
			lvData.clear();
			adapter.notifyDataSetChanged();
			break;
		case R.id.btn_cancel:
			getActivity().finish();
			break;
		case R.id.btn3:
			if (spinnerPMData.size() > 0) {
				Intent intent = new Intent(getActivity(), GoodsChoseAct.class);
				intent.putParcelableArrayListExtra("data", spinnerPMData);
				startActivityForResult(intent, 0);
			} else {
				Toast.makeText(getActivity(), "物料组为空！", Toast.LENGTH_LONG).show();
			}

			break;
		}
	}// end onClick

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if (requestCode == 0 && resultCode == Activity.RESULT_OK) {
			pinMingInfo = data.getParcelableExtra("data");
			tv_danpinhao.setText(pinMingInfo.getAsString("ItemCode"));
			tv_danwei.setText(pinMingInfo.getAsString("InvntryUom"));
			btnPingMing.setText(pinMingInfo.getAsString("ItemName"));
			return;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * 添加字段
	 */
	private void addData() {
		String etStr4 = et4.getText().toString();
		if (etStr4.length() < 1) {
			Toast.makeText(getActivity(), "数量不能为空", Toast.LENGTH_SHORT).show();
			return;
		}
		ContentValues value = new ContentValues();
		value.put("ItemCode", tv_danpinhao.getText().toString());
		value.put("ItemName", pinMingInfo.getAsString("ItemName"));
		value.put("ShuLiang", etStr4);
		value.put("DanWei", tv_danwei.getText().toString());
		lvData.add(value);
		adapter.notifyDataSetChanged();
		et4.setText("");
	}// end addData

	/**
	 * 解析门店信息
	 * 
	 * @param obj
	 */
	private void utilPinMingMsg(String obj) {
		try {
			List<ContentValues> res = new JSONParser().convertJSONArrayToList(new JSONArray(obj));
			spinnerPMData.addAll(res);
			if (spinnerPMData.size() > 0) {
				pinMingInfo = spinnerPMData.get(0);
				tv_danpinhao.setText(pinMingInfo.getAsString("ItemCode"));
				tv_danwei.setText(pinMingInfo.getAsString("InvntryUom"));
				btnPingMing.setText(pinMingInfo.getAsString("ItemName"));
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}// end utilMenDianMsg

	/**
	 * 解析门店信息
	 * 
	 * @param obj
	 */
	private void utilMenDianMsg(String obj) {
		try {
			List<ContentValues> res = new JSONParser().convertJSONArrayToList(new JSONArray(obj));
			for (ContentValues value : res) {
				spinnerMDData.add(value);
			}
			SuperAdapter spAdapter = new SuperAdapter(getActivity(), android.R.layout.simple_dropdown_item_1line,
					spinnerMDData) {

				@Override
				public void dataView(View convertView, ContentValues value, int position) {
					// TODO Auto-generated method stub
					TextView tv = (TextView) convertView.findViewById(android.R.id.text1);
					tv.setText(value.getAsString("MallName"));
					convertView.setTag(value);
				}
			};
			spinnerMD.setAdapter(spAdapter);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}// end utilMenDianMsg
}// end class
