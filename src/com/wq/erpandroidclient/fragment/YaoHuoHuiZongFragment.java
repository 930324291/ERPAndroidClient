package com.wq.erpandroidclient.fragment;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.wq.erpandroidclient.R;
import com.wq.erpandroidclient.YaoHuoJiHuaDetialActivity;
import com.wq.erpandroidclient.adapter.SuperAdapter;
import com.wq.erpandroidclient.net.ServerRequest;
import com.wq.erpandroidclient.util.JSONParser;
import com.wq.erpandroidclient.util.S;
import com.wq.erpandroidclient.util.SPHelper;

/**
 * 要货汇总页面
 * 
 * @author W.Q
 * @date 2014年4月2日
 */
public class YaoHuoHuiZongFragment extends Fragment implements OnItemClickListener {
	ListView lv;
	List<ContentValues> lvData = new ArrayList<ContentValues>();
	SuperAdapter adapter;
	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				String obj = (String) msg.obj;
				S.i(obj);
				try {
					JSONArray jarray = new JSONArray(obj);
					List<ContentValues> res = new JSONParser().convertJSONArrayToList(jarray);
					lvData.clear();
					lvData.addAll(res);
					adapter.notifyDataSetChanged();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			}
		};
	};

	BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			getData();
		}
	};

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		IntentFilter filter = new IntentFilter();
		filter.addAction("commit.yaohuo.successed");
		activity.registerReceiver(receiver, filter);
	}

	@Override
	public void onDetach() {
		// TODO Auto-generated method stub
		super.onDetach();
		getActivity().unregisterReceiver(receiver);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View rootView = inflater.inflate(R.layout.yao_huo_hui_zong_fragment_layout, container, false);
		lv = (ListView) rootView.findViewById(R.id.lv);
		adapter = new SuperAdapter(getActivity(), R.layout.yao_huo_hui_zong_lv_item, lvData) {

			@Override
			public void dataView(View convertView, ContentValues value, int position) {
				// TODO Auto-generated method stub
				TextView tv1 = (TextView) convertView.findViewById(R.id.tv1);
				TextView tv2 = (TextView) convertView.findViewById(R.id.tv2);
				TextView tv3 = (TextView) convertView.findViewById(R.id.tv3);
				TextView tv4 = (TextView) convertView.findViewById(R.id.tv4);
				TextView tv5 = (TextView) convertView.findViewById(R.id.tv5);

				tv1.setText(String.valueOf(position + 1));
				tv2.setText(value.getAsString("DocEntry"));
				tv3.setText(value.getAsString("DocStatus"));
				tv4.setText(value.getAsString("MallName"));
				tv5.setText(value.getAsString("IsAudit"));

				convertView.setTag(value);
			}
		};
		lv.setAdapter(adapter);
		lv.setOnItemClickListener(this);
		return rootView;
	}// end onCreateView
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		getData();
	}

	private void getData() {
		ContentValues value = new ContentValues();
		String userName = SPHelper.getSpValue(getActivity(), "user_name", null);
		value.put("_EmpCode_Sub", userName);
		ServerRequest.sendRequest(0, handler, "getPLAN_M_InfoBySubUserCode", value,
				"http://SuperLogis.org/getPLAN_M_InfoBySubUserCode");
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		// TODO Auto-generated method stub
		ContentValues value = (ContentValues) view.getTag();
		S.i(value.toString());
		if ("未审".equals(value.getAsString("IsAudit"))) {
			Intent intent = new Intent(getActivity(), YaoHuoJiHuaDetialActivity.class);
			intent.putExtra("data", value);
			startActivity(intent);
		}
	}

}
