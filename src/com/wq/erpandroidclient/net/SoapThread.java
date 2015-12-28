package com.wq.erpandroidclient.net;

import java.util.Iterator;
import java.util.Map.Entry;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import com.wq.erpandroidclient.util.S;

import android.content.ContentValues;
import android.os.Handler;

/**
 * SOAP 请求线程
 * 
 * @author W.Q
 * @date 2014年4月14日
 */
public class SoapThread extends Thread {
	private int requestCode = 0;
	private Handler handler = null;
	// 调用的方法名
	private String methodName = null;// "getUserLogin";
	private ContentValues value = null;
	private String soapAction = null;// "http://SuperLogis.org/getUserLogin";
	private String WSDL_URL = null;// "http://221.215.123.230:81/SuperLogis_WebServices/SuperLogis_WebService.asmx";
	private String nameSpace = null;// "http://SuperLogis.org/";

	public SoapThread(int requestCode, Handler handler, String methodName, ContentValues value, String soapAction,
			String mWEDL_URL, String nameSpace) {
		this.requestCode = requestCode;
		this.handler = handler;
		this.methodName = methodName;
		this.value = value;
		this.soapAction = soapAction;
		this.WSDL_URL = mWEDL_URL;
		this.nameSpace = nameSpace;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		SoapObject so = new SoapObject(nameSpace, methodName);
		if (value != null) {
			Iterator<Entry<String, Object>> its = value.valueSet().iterator();
			while (its.hasNext()) {
				Entry<String, Object> en = its.next();
				S.i(en.toString());
				so.addProperty(en.getKey(), en.getValue());
			}
		}
		SoapSerializationEnvelope se = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		se.dotNet = true;// 是否是.net开发的服务端
		se.bodyOut = so;
		HttpTransportSE ht = new HttpTransportSE(WSDL_URL);
		try {
			ht.call(soapAction, se);
			S.i(se.toString());
			SoapObject response = (SoapObject) se.bodyIn;
			String resultMessage = response.getProperty(0).toString();
			handler.obtainMessage(requestCode, resultMessage).sendToTarget();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			handler.obtainMessage(requestCode).sendToTarget();
		}
	}
}
