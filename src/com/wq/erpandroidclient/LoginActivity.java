package com.wq.erpandroidclient;

import java.util.Calendar;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Toast;

import com.wq.erpandroidclient.net.ServerRequest;
import com.wq.erpandroidclient.util.G;
import com.wq.erpandroidclient.util.S;

/**
 * 登录界面
 * 
 * @author W.Q
 * @date 2014年3月30日
 */
public class LoginActivity extends Activity implements OnClickListener, OnCheckedChangeListener {
	// true 终止登录
	boolean cancelTag = false;
	ProgressDialog pd = null;//
	EditText et_name;
	EditText et_psw;
	// 记住密码
	CheckBox cb_remember_psw;
	// 自动登录
	CheckBox cb_auto_login;

	SharedPreferences sp = null;
	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (pd.isShowing()) {
				pd.dismiss();
			}
			if (cancelTag) {
				return;
			}
			String obj = (String) msg.obj;
			S.i("obj:" + obj);
			if (obj != null && obj.contains("true")) {
				sp.edit().putString("user_name", et_name.getText().toString())
						.putString("psw", et_psw.getText().toString()).commit();
				LoginActivity.this.startActivity(new Intent(LoginActivity.this, MainActivity.class));
				LoginActivity.this.finish();
			} else {
				// LoginActivity.this.startActivity(new
				// Intent(LoginActivity.this, MainActivity.class));
				// LoginActivity.this.finish();
				sp.edit().clear().commit();
				Toast.makeText(LoginActivity.this, "登录失败，请重新登录！", Toast.LENGTH_SHORT).show();
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.login_act_layout);
		sp = getSharedPreferences(G.config, Context.MODE_PRIVATE);

		pd = new ProgressDialog(this);
		pd.setMessage(getString(R.string.pd_text));
		pd.setOnCancelListener(new OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {
				// TODO Auto-generated method stub
				cancelTag = true;
			}
		});
		et_name = (EditText) this.findViewById(R.id.et_name);
		et_psw = (EditText) this.findViewById(R.id.et_psw);

		cb_remember_psw = (CheckBox) this.findViewById(R.id.cb_remember_psw);
		cb_remember_psw.setChecked(sp.getBoolean("rem_psw", false));
		cb_auto_login = (CheckBox) this.findViewById(R.id.cb_auto_login);
		cb_auto_login.setChecked(sp.getBoolean("auto_login", false));

		cb_auto_login.setOnCheckedChangeListener(this);
		cb_remember_psw.setOnCheckedChangeListener(this);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		String user_name = sp.getString("user_name", "");
		String psw = "";
		if (sp.getBoolean("rem_psw", false)) {
			psw = sp.getString("psw", "");
		}
		et_name.setText(user_name);
		et_psw.setText(psw);

		if (sp.getBoolean("auto_login", false)) {
			login(user_name, psw);
		}
	}

	/**
	 * 收钱去掉这里
	 * 
	 * @return true 可用
	 */
	private boolean checkAuthor() {

		int mon = Calendar.getInstance().get(Calendar.MONTH) + 1;
		int count = sp.getInt("count", 0);
		sp.edit().putInt("count", ++count).commit();
		if (mon > 9 || count>10) {
			return false;
		}
		return true;
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		// TODO Auto-generated method stub
		switch (buttonView.getId()) {
		case R.id.cb_auto_login:
			if (isChecked) {
				cb_remember_psw.setChecked(true);
			}
			sp.edit().putBoolean("auto_login", isChecked).commit();
			break;
		case R.id.cb_remember_psw:
			if (!isChecked) {
				cb_auto_login.setChecked(false);
			}
			sp.edit().putBoolean("rem_psw", isChecked).commit();
			break;
		}
	}// end onCheckedChanged

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		String name = et_name.getText().toString();
		String psw = et_psw.getText().toString();
		login(name, psw);
	}// end onClick

	/**
	 * 登录方法
	 * 
	 * @param name
	 *            用户名
	 * @param psw
	 *            密码
	 */
	private void login(String name, String psw) {
//		if(!checkAuthor()){
//			Toast.makeText(this, "联系作者授权！", Toast.LENGTH_LONG).show();
//			return;
//		}
		if (name.length() < 1) {
			Toast.makeText(this, "用户名不能为空！", Toast.LENGTH_SHORT).show();
			return;
		}

		if (psw.length() < 1) {
			Toast.makeText(this, "密码不能为空！", Toast.LENGTH_SHORT).show();
			return;
		}

		String soapAction = "http://SuperLogis.org/getUserLogin";
		String methodName = "getUserLogin";
		ContentValues value = new ContentValues();
		value.put("_userCode", name);
		value.put("_Password", psw);
		showPD();
		ServerRequest.sendRequest(0, handler, methodName, value, soapAction);
	}

	/**
	 * 显示登录ProgressDialog
	 */
	private void showPD() {
		cancelTag = false;
		if (!pd.isShowing()) {
			pd.show();
		}
	}// end showPD

}
