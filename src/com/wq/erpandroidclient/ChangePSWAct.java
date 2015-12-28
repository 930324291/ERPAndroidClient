package com.wq.erpandroidclient;

import com.wq.erpandroidclient.net.ServerRequest;
import com.wq.erpandroidclient.util.G;
import com.wq.erpandroidclient.util.S;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;

/**
 * 修改密码页面
 * 
 * @author 王强
 *
 */
public class ChangePSWAct extends Activity implements OnClickListener {
	EditText et_old, et_new, et_sure;
	ProgressDialog pd = null;
	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			pd.dismiss();
			S.i(msg.toString());
			String obj = (String) msg.obj;
			if (obj != null && obj.contains("false")) {
				Toast.makeText(ChangePSWAct.this, "修改密码失败！", Toast.LENGTH_LONG).show();
			} else {
				Toast.makeText(ChangePSWAct.this, "修改密码成功！", Toast.LENGTH_LONG).show();
				ChangePSWAct.this.finish();
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.change_psw_layout);
		pd = new ProgressDialog(this);
		pd.setMessage(getString(R.string.pd_text));

		et_old = (EditText) this.findViewById(R.id.et_psw_old);
		et_new = (EditText) this.findViewById(R.id.et_psw_new);
		et_sure = (EditText) this.findViewById(R.id.et_psw_sure);

	}

	/**
	 * 修改密码方法
	 */
	private void change() {
		String oldPsw = et_old.getText().toString();
		String newPsw = et_new.getText().toString();
		String surePsw = et_sure.getText().toString();
		if (oldPsw.length() < 1) {
			Toast.makeText(this, "旧密码不能为空！", Toast.LENGTH_LONG).show();
			return;
		}

		if (newPsw.length() < 1) {
			Toast.makeText(this, "新密码不能为空！", Toast.LENGTH_LONG).show();
			return;
		}

		if (surePsw.length() < 1) {
			Toast.makeText(this, "请再次输入新密码！", Toast.LENGTH_LONG).show();
			return;
		}

		if (!newPsw.equals(surePsw)) {
			Toast.makeText(this, "两次输入的新密码不相同！", Toast.LENGTH_LONG).show();
			return;
		}
		// updateLoginUserPass(ByVal _userCode As String, ByVal _OPassWord As
		// String, ByVal _NPassword As String) As String
		// 注释：根据当前登录用户的编号及密码，对密码进行修改。
		// 参数： _UserCode当前登录员工的编号
		// 　　　　_OPassWord　原密码
		// _NPassword　新密码
		String soapAction = "http://SuperLogis.org/updateLoginUserPass";
		ContentValues value = new ContentValues();
		String user_name = getSharedPreferences(G.config, Context.MODE_PRIVATE).getString("user_name", "");
		value.put("_userCode", user_name);
		value.put("_OPassWord", oldPsw);
		value.put("_NPassword", newPsw);
		S.i(value.toString());
		ServerRequest.sendRequest(0, handler, "updateLoginUserPass", value, soapAction);
		pd.show();
	}// end change

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		change();
	}
}
