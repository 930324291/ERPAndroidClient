package com.wq.erpandroidclient.net;

import java.util.List;

import android.content.ContentValues;
import android.os.Handler;

import com.wq.erpandroidclient.util.G;

/**
 * 网络请求操作封装
 * 
 * @author W.Q
 * @date 2014年4月14日
 */
public class ServerRequest {

	public static void sendRequest(int requestCode, Handler handler, String methodName, ContentValues value,
			String soapAction) {
		sendRequest(requestCode, handler, methodName, value, soapAction, G.WSDL_URL, G.NAME_SPACE);
	}

	public static void sendRequestWithOrderParams(int requestCode, Handler handler, String methodName, List<ContentValues> values,
			String soapAction) {
		new SoapThreadWithOrderParams(requestCode, handler, methodName, values, soapAction, G.WSDL_URL, G.NAME_SPACE)
				.start();
	}

	/**
	 * 发送网络请求，获取服务端数据
	 * 
	 * @param requestCode
	 *            请求编码
	 * @param handler
	 *            消息回调接口
	 * @param methodName
	 * @param value
	 * @param soapAction
	 * @param mWEDL_URL
	 * @param nameSpace
	 */
	public static void sendRequest(int requestCode, Handler handler, String methodName, ContentValues value,
			String soapAction, String mWEDL_URL, String nameSpace) {
		new SoapThread(requestCode, handler, methodName, value, soapAction, mWEDL_URL, nameSpace).start();
	}
}
