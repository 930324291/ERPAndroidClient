package com.wq.erpandroidclient.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * 
 * @author W.Q
 * @date 2014年3月3日
 */
public class SPHelper {

	private static final String SP_NAME = G.config;
	
	public static final String getSpValue(Context context, String key,String defaultValue) {
		return getSP(context, SP_NAME).getString(key, defaultValue);
	}
	
	public static final int getSpValue(Context context, String key,int defaultValue){
		return getSP(context, SP_NAME).getInt(key, defaultValue);
	}
	
	public static final boolean getSpValue(Context context,String key,boolean defaultValue){
		return getSP(context, SP_NAME).getBoolean(key, defaultValue);
	}
	
	public static final void putSpValue(Context context,String key,String value){
		getSP(context, SP_NAME).edit().putString(key, value).commit();
	}
	
	public static final void putSpValue(Context context,String key,int value){
		getSP(context, SP_NAME).edit().putInt(key, value).commit();
	}
	
	public static final void putSpValue(Context context,String key,boolean value){
		getSP(context, SP_NAME).edit().putBoolean(key, value).commit();
	}

	private static final SharedPreferences getSP(Context context, String sp_name) {
		return context.getApplicationContext().getSharedPreferences(sp_name, Context.MODE_PRIVATE);
	}

}
