package com.wq.erpandroidclient.util;

import android.util.Log;

/**
 * @author 王强
 * @date 2013年11月28日
 * @简介 自定义log输出类
 */
public class S {
	public static final String tag = "System.out.wq";
	public static final void i(String s) {
		StackTraceElement stack = Thread.currentThread().getStackTrace()[3];
		if (G.logSwitch) {
			Log.i(tag, stack.getFileName()+"-"+stack.getMethodName()+"-"+stack.getLineNumber()+"-->"+s);
		}
	}//end i
	
	public static final void i(float s){
		i(String.valueOf(s));
	}
}
