package com.wq.erpandroidclient;

import android.app.Application;

import com.wq.erpandroidclient.util.CrashHandler;

public class ERPApplication extends Application {
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		CrashHandler.getInstance().init(getApplicationContext());
	}
}
