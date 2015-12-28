package com.wq.erpandroidclient;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.wq.erpandroidclient.adapter.SuperAdapter;
import com.wq.erpandroidclient.net.ServerRequest;
import com.wq.erpandroidclient.util.JSONParser;
import com.wq.erpandroidclient.util.S;
import com.wq.erpandroidclient.util.SPHelper;
import com.wq.erpandroidclient.util.ViewHolder;

/**
 * 要货计划详情页面
 * 
 * @author W.Q
 * @Date 2014年6月10日
 */
public class YaoHuoJiHuaDetialActivity extends Activity implements OnItemClickListener, OnClickListener,
		OnItemLongClickListener {
	TextView tv_DocEntry, tv_DocStatus, tv_MallCode, tv_MallName, tv_IsAudit;
	EditText et_DocDate,et_recDate;
	ListView lv;
	List<ContentValues> lvData = new ArrayList<ContentValues>();
	SuperAdapter adapter;
	View lvHeader;
	PopupWindow popWindom;
	View popView;
	ProgressDialog pd;

	Button btn_add;// 增加数据按钮
	EditText et4;// 单品号，数量，单位
	TextView tv_danpinhao, tv_danwei;
	Button btnPingMing;// 品名

	ArrayList<ContentValues> spinnerPMData = new ArrayList<ContentValues>();

	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			String obj = (String) msg.obj;
			S.i(obj);
			switch (msg.what) {
			case 0:
				S.i(obj);
				try {
					JSONArray jarray = new JSONArray(obj);
					List<ContentValues> res = new JSONParser().convertJSONArrayToList(jarray);
					lvData.addAll(res);
					adapter.notifyDataSetChanged();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			case 1:
				if ("true".equals(obj)) {
					// 成功
					Toast.makeText(YaoHuoJiHuaDetialActivity.this, "提交成功！", Toast.LENGTH_LONG).show();
				} else {
					Toast.makeText(YaoHuoJiHuaDetialActivity.this, "提交失败请重新提交！", Toast.LENGTH_LONG).show();
				}
				break;
			case 2:
				utilPinMingMsg(obj);
				break;
			}// end switch
			pd.cancel();
		}// end
	};

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

	ContentValues intentValue = null;
	View lvFooter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.yao_huo_ji_hua_detial_act_layout);
		pd = new ProgressDialog(this);
		pd.setMessage(getString(R.string.pd_text));
		pd.show();

		lvHeader = getLayoutInflater().inflate(R.layout.yao_huo_ji_hua_detial_lv_header, null);
		tv_DocEntry = (TextView) lvHeader.findViewById(R.id.tv_DocEntry);
		tv_DocStatus = (TextView) lvHeader.findViewById(R.id.tv_DocStatus);
		tv_MallCode = (TextView) lvHeader.findViewById(R.id.tv_MallCode);
		tv_MallName = (TextView) lvHeader.findViewById(R.id.tv_MallName);
		tv_IsAudit = (TextView) lvHeader.findViewById(R.id.tv_IsAudit);
		et_DocDate = (EditText) lvHeader.findViewById(R.id.et_DocDate);
		et_recDate = (EditText) lvHeader.findViewById(R.id.et_recDate);
		intentValue = getIntent().getParcelableExtra("data");
		S.i(intentValue.toString());
		tv_DocEntry.setText(intentValue.getAsString("DocEntry"));
		tv_DocStatus.setText(intentValue.getAsString("DocStatus"));
		tv_MallCode.setText(intentValue.getAsString("MallCode"));
		tv_MallName.setText(intentValue.getAsString("MallName"));
		tv_IsAudit.setText(intentValue.getAsString("IsAudit"));
		et_DocDate.setText(intentValue.getAsString("DocDate"));
		et_recDate.setText(intentValue.getAsString("ReceDate"));
		lv = (ListView) this.findViewById(R.id.lv);
		lv.addHeaderView(lvHeader, null, false);
		lvFooter = getLayoutInflater().inflate(R.layout.yao_huo_lv_footer, null);
		btn_add = (Button) lvFooter.findViewById(R.id.btn_add);
		btn_add.setOnClickListener(this);
		tv_danpinhao = (TextView) lvFooter.findViewById(R.id.tv_danpinhao);
		btnPingMing = (Button) lvFooter.findViewById(R.id.btn3);
		btnPingMing.setOnClickListener(this);
		et4 = (EditText) lvFooter.findViewById(R.id.et4);
		tv_danwei = (TextView) lvFooter.findViewById(R.id.tv_danwei);

		lv.addFooterView(lvFooter);
		adapter = new SuperAdapter(this, R.layout.yao_huo_lv_item, lvData) {

			@Override
			public void dataView(View convertView, ContentValues value, int position) {
				// TODO Auto-generated method stub
				TextView tv1 = ViewHolder.get(convertView, R.id.tv1);
				tv1.setText(String.valueOf(position));
				TextView tv2 = ViewHolder.get(convertView, R.id.tv2);
				tv2.setText(value.getAsString("ItemCode"));
				TextView tv3 = ViewHolder.get(convertView, R.id.tv3);
				tv3.setText(value.getAsString("ItemName"));
				TextView tv4 = ViewHolder.get(convertView, R.id.tv4);
				tv4.setText(value.getAsString("Quantity"));
				TextView tv5 = ViewHolder.get(convertView, R.id.tv5);
				tv5.setText(value.getAsString("InvntryUom"));
			}
		};
		lv.setAdapter(adapter);
		lv.setOnItemClickListener(this);
		lv.setOnItemLongClickListener(this);
		ContentValues requestValues = new ContentValues();
		requestValues.put("_DocEntry", intentValue.getAsString("DocEntry"));
		ServerRequest.sendRequest(0, handler, "getPLAN_S_InfoBySubUserCode_DocEntry", requestValues,
				"http://SuperLogis.org/getPLAN_S_InfoBySubUserCode_DocEntry");
		intiSpPinMing();
	}// end onCreate

	private void showEditPopWindow(View item, ContentValues value) {
		popView = getLayoutInflater().inflate(R.layout.edit_item_layout, null);
		Button btn_edit = (Button) popView.findViewById(R.id.btn_edit);
		Button btn_delete = (Button) popView.findViewById(R.id.btn_delete);
		EditText et = (EditText) popView.findViewById(R.id.et);
		String s = value.getAsString("Quantity");
		et.setText(s);

		btn_delete.setTag(value);
		btn_edit.setTag(value);
		btn_delete.setOnClickListener(this);
		btn_edit.setOnClickListener(this);
		popWindom = new PopupWindow(popView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		popWindom.setOutsideTouchable(true);
		popWindom.setTouchable(true);
		popWindom.setFocusable(true);
		popWindom.setBackgroundDrawable(getResources().getDrawable(R.drawable.shape_pop_bg));
		popWindom.setTouchInterceptor(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (popWindom != null && event.getAction() == MotionEvent.ACTION_OUTSIDE) {
					popWindom.dismiss();
					return true;
				}
				return false;
			}
		});
		popWindom.showAsDropDown(item, 0, -(126 + item.getHeight()));
	}

	ContentValues pinMingInfo = null;// 记录用户选择的品名信息

	/**
	 * 初始化品名Spinner
	 */
	private void intiSpPinMing() {
		ServerRequest.sendRequest(2, handler, "getItemInfo", null, "http://SuperLogis.org/getItemInfo");
	}// end intiSpPinMing

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		ContentValues value = (ContentValues) v.getTag();
		switch (v.getId()) {
		case R.id.btn_delete:
			popWindom.dismiss();
			lvData.remove(value);
			adapter.notifyDataSetChanged();
			break;
		case R.id.btn_edit:
			S.i("ss" + popView.getHeight());
			popWindom.dismiss();
			EditText et = (EditText) popView.findViewById(R.id.et);
			String etStr = et.getText().toString();
			if (etStr.length() > 0) {
				int index = 0;
				for (; index < lvData.size(); index++) {
					if (lvData.get(index).getAsString("LineNum").equals(value.getAsString("LineNum"))) {
						break;
					}
				}
				ContentValues uv = lvData.get(index);
				uv.put("Quantity", etStr);
				lvData.set(index, uv);
				adapter.notifyDataSetChanged();
			} else {
				Toast.makeText(this, "请输入数量！", Toast.LENGTH_SHORT).show();
			}

			break;
		case R.id.btn_commit:
			commitDataToServer();
			break;
		case R.id.btn_add:
			addData();
			break;
			
		case R.id.btn3:
			if(spinnerPMData.size()>0){
				Intent intent = new Intent(this,GoodsChoseAct.class);
				intent.putParcelableArrayListExtra("data", spinnerPMData);
				startActivityForResult(intent, 0);
			} else{
				Toast.makeText(this, "物料组为空！", Toast.LENGTH_LONG).show();
			}
			
			break;
		}
	}// end onClick
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if(requestCode == 0 && resultCode == Activity.RESULT_OK){
			pinMingInfo = data.getParcelableExtra("data");
			tv_danpinhao.setText(pinMingInfo.getAsString("ItemCode"));
			tv_danwei.setText(pinMingInfo.getAsString("InvntryUom"));
			btnPingMing.setText(pinMingInfo.getAsString("ItemName"));
			return;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * 添加字段 tv1.setText(value.getAsString("LineNum"));
	 * tv2.setText(value.getAsString("ItemCode"));
	 * tv3.setText(value.getAsString("Dscription"));
	 * tv4.setText(value.getAsString("Quantity"));
	 * tv5.setText(value.getAsString("OUnitMsr"));
	 */
	private void addData() {
		String etStr4 = et4.getText().toString();
		if (etStr4.length() < 1) {
			Toast.makeText(this, "数量不能为空", Toast.LENGTH_SHORT).show();
			return;
		}
		ContentValues value = new ContentValues();
		value.put("ItemCode", tv_danpinhao.getText().toString());
		value.put("ItemName", pinMingInfo.getAsString("ItemName"));
		value.put("Quantity", etStr4);
		value.put("InvntryUom", tv_danwei.getText().toString());
		lvData.add(value);
		adapter.notifyDataSetChanged();
		et4.setText("");
	}// end addData

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
		try {
			JSONObject jsonHead = new JSONObject();
			jsonHead.put("docEntry", intentValue.getAsString("DocEntry"));
			jsonHead.put("MallCode", intentValue.getAsString("MallCode"));
			jsonHead.put("MallName", intentValue.getAsString("MallName"));
			String empCode = SPHelper.getSpValue(this, "user_name", "001");
			jsonHead.put("EMpCode", empCode);
			jsonHead.put("ReceveDate", et_recDate.getText().toString());
			jsonHead.put("cRichMemo", "cRichMemo");
			jsonHead.put("cPhone", "android");
			JSONArray bodyArray = new JSONArray();
			JSONObject jsonBody = null;
			if (lvData.size() == 0) {
				Toast.makeText(this, "数据为空！", Toast.LENGTH_LONG).show();
				return;
			}
			for (int i = 0; i < lvData.size(); i++) {
				ContentValues value = lvData.get(i);
				jsonBody = new JSONObject();
				jsonBody.put("docEntry", intentValue.getAsString("DocEntry"));
				jsonBody.put("LineNum", String.valueOf(i));
				jsonBody.put("ItemCode", value.getAsString("ItemCode"));
				jsonBody.put("ItemName", value.getAsString("ItemName"));
				jsonBody.put("InvntryUom", value.getAsString("InvntryUom"));
				jsonBody.put("Quantity", value.getAsString("Quantity"));
				jsonBody.put("ReceDate", et_recDate.getText().toString());
				jsonBody.put("cMemo", "cMemo");
				bodyArray.put(jsonBody);
			}

			S.i(jsonHead.toString());
			S.i(bodyArray.toString());
			ContentValues cv = new ContentValues();
			cv.put("_PM", jsonHead.toString());
			cv.put("_PS", bodyArray.toString());
			ServerRequest.sendRequest(1, handler, "InsertPlan", cv, "http://SuperLogis.org/InsertPlan");
			pd.show();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
		// TODO Auto-generated method stub
		showEditPopWindow(view, lvData.get(position - 1));
		return true;
	}
}
